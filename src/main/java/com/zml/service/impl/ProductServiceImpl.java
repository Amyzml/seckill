package com.zml.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zml.entity.Account;
import com.zml.entity.AccountExample;
import com.zml.entity.Product;
import com.zml.entity.ProductExample;
import com.zml.exception.ServiceException;
import com.zml.job.ProductInventoryJob;
import com.zml.mapper.AccountMapper;
import com.zml.mapper.ProductMapper;
import com.zml.service.ProductService;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

   @Autowired
    private ProductMapper productMapper;
   @Autowired
   private AccountMapper accountMapper;
   @Value("${qiniu.ak}")
    private String 	accessKey;
   @Value("${qiniu.sk}")
    private String secretKey;
   @Value("${qiniu.buket}")
   private String buket;
   @Autowired
   private JedisPool jedisPool;
   @Autowired
   private SchedulerFactoryBean schedulerFactoryBean;
    @Override
    public List<Product> findAll() {
        ProductExample productExample = new ProductExample();
        productExample.setOrderByClause("start_time asc");

        return productMapper.selectByExample(productExample);
    }

    @Override
    public void save(Product product, InputStream inputStream) {
        String kay = uploadQiNiu(inputStream);
        product.setProductImage(kay);
        productMapper.insertSelective(product);

        System.out.println("product -- >" + product);
        //在redis中添加商品的库存量
        try(Jedis jedis = jedisPool.getResource()){
                for (int  i = 0;i < product.getProductInventory();i++ ){
                    jedis.lpush("product:" + product.getId() + ":inventory",String.valueOf(i));
                }
        }
        //秒杀结束时的定时任务，秒杀结束后更新库存
        addTimedTask(product.getId(),product.getEndTime().getTime());
    }

    @Override
    public Product findById(Integer id) {
        Product product;
        try(Jedis jedis = jedisPool.getResource()){
            String json = jedis.get("product:" + id);

            if (json == null){
                product =  productMapper.selectByPrimaryKey(id);
                jedis.set("product:" + id,JSON.toJSONString(product));
            }else{
                product= JSON.parseObject(json,Product.class);
            }
        }
        return product;
    }

    /**
     * 添加定时任务
     */
    private void addTimedTask(Integer productId,Long endTime){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAsString("productId",productId);

        JobDetail jobDetail = JobBuilder
                .newJob(ProductInventoryJob.class)
                .setJobData(jobDataMap)
                .withIdentity(new JobKey("taskID:"+productId,"productInventoryGroup"))
                .build();

        DateTime dateTime = new DateTime(endTime);

        StringBuilder cron = new StringBuilder("0")
                .append(" ")
                .append(dateTime.getMinuteOfHour())
                .append(" ")
                .append(dateTime.getHourOfDay())
                .append(" ")
                .append(dateTime.getDayOfMonth())
                .append(" ")
                .append(dateTime.getMonthOfYear())
                .append(" ? ")
                .append(dateTime.getYear());

        logger.info("CRON EX: {}" ,cron.toString());

        ScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron.toString());
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder).build();

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (Exception ex) {
            throw new ServiceException(ex,"添加定时任务异常");
        }
    }

    /**
     * miaosha
     * @param id
     */
    @Override
    public void secKill(Integer id,String name) {

        try(Jedis jedis = jedisPool.getResource()) {
            Product product = JSON.parseObject(jedis.get("product:" + id), Product.class);

            if (!product.isStart()) {
                throw new ServiceException("太早了，还没开始");
            }

            String value = jedis.lpop("product:" + id + ":inventory");


            if (value == null) {
                logger.error("库存不足，抢购失败");
                throw new ServiceException("抢光了");
            } else {

                AccountExample accountExample = new AccountExample();
                accountExample.createCriteria().andNameEqualTo(name);

                List<Account> accountList = accountMapper.selectByExample(accountExample);
                for (Account account : accountList){
                    String accountValue = jedis.lpop("Account:"+ account.getId() + ":" + name);
                    if (accountValue != null){
                        logger.info("抢购成功");
                    }else {
                        throw new ServiceException("只能抢购一次");
                    }
                }

            }
            //修改redis 的缓存
            product.setProductInventory(product.getProductInventory() - 1);
            jedis.set("product:" + id,JSON.toJSONString(product));


             /*


             jmsTemplate.send("product_inventory", new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText(id.toString());
                        return textMessage;
                    }
                });*/
        }

        //方案二
     /*  try(Jedis jedis = jedisPool.getResource()){
           Product product = JSON.parseObject(jedis.get("product:" + id),Product.class);

           jedis.set("product:" + id + ":num",product.getProductInventory().toString());
            Long num = jedis.decr("product:" + id + ":num");


            if (num < 0L){
                logger.error("库存不足，抢购失败");
                throw new ServiceException("抢光了");
            }else {
                product.setProductInventory(product.getProductInventory() - 1);
                jedis.set("product:" + id,JSON.toJSONString(product));

                logger.info("抢购成功");
            }
        }*/

      /*
        方案一
        Product product = productMapper.selectByPrimaryKey(id);

        if (product.getProductInventory() > 0){
            product.setProductInventory(product.getProductInventory() - 1);
            productMapper.updateByPrimaryKey(product);

            logger.info("抢购成功");
        }else {

            logger.error("库存不足，抢购失败");
            throw new ServiceException("抢光了");
        }*/
    }

















    private String uploadQiNiu(InputStream inputStream) {
        Configuration configuration = new Configuration(Zone.zone1());
        UploadManager uploadManager = new UploadManager(configuration);

        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(buket);

        try {
            Response response = uploadManager.put(IOUtils.toByteArray(inputStream),null,uploadToken);
            DefaultPutRet defaultPutRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            return defaultPutRet.key;
        } catch (IOException e) {
            throw new RuntimeException("上传到七牛服务器失败");
        }
    }
}
