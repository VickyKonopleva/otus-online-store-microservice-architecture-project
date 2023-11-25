package com.konopleva.crudeapp.repository;

import com.konopleva.crudeapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o WHERE o.associatedUserEmail = ?1")
    List<Order> findAllByUserEmail(@Param("email") String email);
}
