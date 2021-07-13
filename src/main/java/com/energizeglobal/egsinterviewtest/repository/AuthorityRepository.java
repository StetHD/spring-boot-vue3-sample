package com.energizeglobal.egsinterviewtest.repository;

import com.energizeglobal.egsinterviewtest.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {}
