package com.drakkarpress.repository;

import com.drakkarpress.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") String role);
    
    @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    List<User> findVerifiedUsers();
    
    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findActiveUsers();
}
