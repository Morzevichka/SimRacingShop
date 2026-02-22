package com.morzevichka.user_service.repository;

import com.morzevichka.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
