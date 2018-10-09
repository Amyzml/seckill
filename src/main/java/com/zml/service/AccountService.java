package com.zml.service;

import com.zml.entity.Account;

public interface AccountService {
    Account findByName(String name, String password);

}
