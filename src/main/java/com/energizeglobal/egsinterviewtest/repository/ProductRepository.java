package com.energizeglobal.egsinterviewtest.repository;

import com.energizeglobal.egsinterviewtest.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
