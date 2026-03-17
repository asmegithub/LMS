package com.EGM.LMS.repository;

import com.EGM.LMS.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStudent_IdOrderByCreatedAtDesc(UUID studentId);
}
