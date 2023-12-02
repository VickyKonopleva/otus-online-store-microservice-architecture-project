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
public class OrderState {
    private String id;
    private String associatedUserEmail;
    private String description;
    private Integer price;
    private String address;
    private LocalDateTime creationTime;
    private String status;
}
