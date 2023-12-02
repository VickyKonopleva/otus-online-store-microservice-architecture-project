package com.konopleva.crudeapp.repository;

import com.konopleva.crudeapp.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, String> {
    Optional<Item> findItemByName(String name);
}
