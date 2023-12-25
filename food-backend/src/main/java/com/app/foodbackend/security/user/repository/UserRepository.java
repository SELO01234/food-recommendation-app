package com.app.foodbackend.security.user.repository;

import com.app.foodbackend.security.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String username);

    Optional<User> findById(Integer id);

    boolean existsByEmail(String email);

    boolean existsByUserName(String username);

    boolean existsById(Integer id);

    @Query(value = "SELECT COUNT(role_id) FROM _user WHERE role_id= :role_id", nativeQuery = true)
    Integer numberOfRoles(@Param("role_id") Integer roleId);
}
