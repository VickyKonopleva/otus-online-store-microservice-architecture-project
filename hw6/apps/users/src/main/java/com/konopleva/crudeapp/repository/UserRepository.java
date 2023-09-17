package com.konopleva.crudeapp.repository;

import com.konopleva.crudeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
