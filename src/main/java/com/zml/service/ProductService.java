package com.zml.service;

import com.zml.entity.Product;

import java.io.InputStream;
import java.util.List;

public interface ProductService {

    List<Product> findAll();

    void save(Product product, InputStream inputStream);

    Product findById(Integer id);

    void secKill(Integer id,String name);

}
