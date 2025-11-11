package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Buscar usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Buscar usuario por username
     */
    Optional<User> findByUsername(String username);

    /**
     * Buscar usuario por email o username
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);

    /**
     * Verificar si existe email
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe username
     */
    boolean existsByUsername(String username);

    /**
     * Buscar usuarios por fase
     */
    @Query("SELECT u FROM User u WHERE u.userNumber <= :maxUserNumber AND u.isActive = true")
    List<User> findUsersByPhase(@Param("maxUserNumber") Long maxUserNumber);

    /**
     * Contar usuarios activos
     */
    long countByIsActiveTrue();

    /**
     * Buscar usuarios creados en rango de fechas
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Buscar usuarios con membresía premium activa
     */
    @Query("SELECT u FROM User u JOIN u.membership m WHERE m.isActive = true AND m.plan != 'FREE'")
    List<User> findPremiumUsers();

    /**
     * Buscar usuarios sin verificar email
     */
    List<User> findByIsEmailVerifiedFalseAndIsActiveTrue();

    /**
     * Buscar por país
     */
    List<User> findByCountryAndIsActiveTrue(String country);

    /**
     * Último user_number registrado
     */
    @Query("SELECT MAX(u.userNumber) FROM User u")
    Optional<Long> findMaxUserNumber();

    /**
     * Buscar usuarios recientes
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(@Param("since") LocalDateTime since);
}
