package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.entity.Item;
import com.konopleva.crudeapp.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping()
    public ResponseEntity<List<Item>> getAllItems() {
        var items = warehouseService.findAllItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
