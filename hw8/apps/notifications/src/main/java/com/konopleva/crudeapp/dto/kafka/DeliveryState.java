package com.konopleva.crudeapp.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryState {
    private String id;
    private String orderId;
    private String associatedUserEmail;
    private String address;
    private LocalDateTime plannedDeliveryTime;
    private String status;
    private LocalDateTime actualDeliveryTime;
}
