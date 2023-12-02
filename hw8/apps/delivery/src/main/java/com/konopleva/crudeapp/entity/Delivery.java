package com.konopleva.crudeapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(generator = "delivery_uuid2")
    @GenericGenerator(name = "delivery_uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "associated_user")
    private String associatedUserEmail;

    private String address;

    @Column(name = "planned_delivery_time")
    private LocalDateTime plannedDeliveryTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime = null;

    private String status;
}
