package com.konopleva.crudeapp.repository;

import com.konopleva.crudeapp.entity.AccountBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountBilling, String> {
    @Query("SELECT ac FROM AccountBilling ac WHERE ac.email = ?1")
    Optional<AccountBilling> findByEmail(String email);
}
