package com.energizeglobal.egsinterviewtest.service;


import com.energizeglobal.egsinterviewtest.domain.Product;
import com.energizeglobal.egsinterviewtest.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        log.debug("Retrieving All Products");
        return productRepository.findAll();
    }
}
