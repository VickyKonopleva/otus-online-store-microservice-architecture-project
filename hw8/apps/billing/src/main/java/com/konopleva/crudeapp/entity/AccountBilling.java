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
@Table(name = "accounts")
public class AccountBilling {
    @Id
    @GeneratedValue(generator = "billing_uuid2")
    @GenericGenerator(name = "billing_uuid2", strategy = "uuid2")
    private String id;

    private String email;

    private String username;

    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
