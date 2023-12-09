package com.app.foodbackend.security.user.repository;

import com.app.foodbackend.security.user.entity.Role;
import com.app.foodbackend.security.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByName(String name);

    Role findByName(String name);
}
