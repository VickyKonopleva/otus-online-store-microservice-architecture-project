package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<String> confirmDelivery(@RequestParam String orderId) {
        var result = deliveryService.confirmDelivery(orderId);
        String message = "Delivery confirmed for order with id '%s' for user %s at %s. Planned delivery time: %s";
        return new ResponseEntity<>(
                String.format(
                        message,
                        result.getOrderId(),
                        result.getAssociatedUserEmail(),
                        result.getActualDeliveryTime(),
                        result.getPlannedDeliveryTime()),
                HttpStatus.OK);
    }

}
