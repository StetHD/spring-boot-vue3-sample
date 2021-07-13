package com.energizeglobal.egsinterviewtest.web.rest;

import com.energizeglobal.egsinterviewtest.domain.Product;
import com.energizeglobal.egsinterviewtest.repository.ProductRepository;
import com.energizeglobal.egsinterviewtest.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts() {
        log.debug("Product Resource");
        return productService.getProducts();
    }
}
