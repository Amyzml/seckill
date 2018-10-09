package com.zml.service.impl;

import com.zml.entity.Account;
import com.zml.entity.AccountExample;
import com.zml.exception.ServiceException;
import com.zml.mapper.AccountMapper;
import com.zml.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private JedisPool jedisPool;
    @Override
    public Account findByName(String name, String password) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andNameEqualTo(name);

        List<Account> accountList = accountMapper.selectByExample(accountExample);

        for (Account account : accountList) {
            if (account.getPassword().equals(password)) {

                try(Jedis jedis = jedisPool.getResource()){
                    jedis.lpush("Account:"+ account.getId() + ":" + name,name);
                }

                return account;
            } else {
                throw new ServiceException("账号或者密码错误");
            }
        }
        return null;
    }
}
