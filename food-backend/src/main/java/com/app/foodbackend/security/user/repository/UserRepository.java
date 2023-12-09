package com.app.foodbackend.security.user.repository;

import com.app.foodbackend.security.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String username);

    Optional<User> findById(Integer id);

    boolean existsByEmail(String email);
}
