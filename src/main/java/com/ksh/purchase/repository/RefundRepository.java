package com.ksh.purchase.repository;

import com.ksh.purchase.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefundRepository extends JpaRepository<Refund, Long>{
}
