package com.zml.service.mq;

import com.zml.entity.Product;
import com.zml.mapper.ProductMapper;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class ProductInventoryCache {
    private Logger logger = LoggerFactory.getLogger(ProductInventoryCache.class);

    @Autowired
    private ProductMapper productMapper;

    @JmsListener(destination = "product_inventory",containerFactory = "jmsListenerContainerFactory")
    public void job(Message message){

        TextMessage textMessage = (TextMessage) message;

        try {
            Integer id = Integer.valueOf(textMessage.getText());

            logger.info("开始减库存");
            Product product = productMapper.selectByPrimaryKey(id);
            product.setProductInventory(product.getProductInventory() - 1);
            productMapper.updateByPrimaryKey(product);
            logger.info("减库存成功");

            textMessage.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
