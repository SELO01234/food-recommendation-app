package com.app.foodbackend.security.user.repository;

import com.app.foodbackend.security.user.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    boolean existsByName(String name);
}
