package com.example.wiki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wiki.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginIgnoreCase(String login);
}
