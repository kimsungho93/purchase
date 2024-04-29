package com.ksh.purchase.repository;

import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByStatus(Pageable pageable, ProductStatus status);
    List<Product> findByIdIn(List<Long> ids);
}
