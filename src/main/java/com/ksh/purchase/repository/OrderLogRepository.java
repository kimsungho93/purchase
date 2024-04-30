package com.ksh.purchase.repository;

import com.ksh.purchase.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
}
