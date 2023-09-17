package com.konopleva.crudeapp.repository;

import com.konopleva.crudeapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
