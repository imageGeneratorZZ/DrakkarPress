package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

    /**
     * Buscar conexión específica
     */
    Optional<Connection> findByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    /**
     * Verificar si existe conexión
     */
    boolean existsByFollowerIdAndFollowedIdAndConnectionStatus(UUID followerId, UUID followedId, String status);

    /**
     * Buscar seguidores de un usuario
     */
    List<Connection> findByFollowedIdAndConnectionStatusOrderByCreatedAtDesc(UUID followedId, String status);

    /**
     * Buscar usuarios seguidos
     */
    List<Connection> findByFollowerIdAndConnectionStatusOrderByCreatedAtDesc(UUID followerId, String status);

    /**
     * Contar seguidores
     */
    long countByFollowedIdAndConnectionStatus(UUID followedId, String status);

    /**
     * Contar seguidos
     */
    long countByFollowerIdAndConnectionStatus(UUID followerId, String status);

    /**
     * Buscar conexiones mutuas
     */
    @Query("SELECT c1 FROM Connection c1 WHERE c1.follower.id = :userId AND c1.connectionStatus = 'ACCEPTED' AND EXISTS (SELECT c2 FROM Connection c2 WHERE c2.follower.id = c1.followed.id AND c2.followed.id = :userId AND c2.connectionStatus = 'ACCEPTED')")
    List<Connection> findMutualConnections(@Param("userId") UUID userId);

    /**
     * Buscar solicitudes pendientes
     */
    List<Connection> findByFollowedIdAndConnectionStatusOrderByCreatedAtAsc(UUID followedId, String status);

    /**
     * Sugerencias de conexión (amigos de amigos)
     */
    @Query("SELECT DISTINCT c2.followed FROM Connection c1 JOIN Connection c2 ON c1.followed.id = c2.follower.id WHERE c1.follower.id = :userId AND c1.connectionStatus = 'ACCEPTED' AND c2.connectionStatus = 'ACCEPTED' AND c2.followed.id != :userId AND NOT EXISTS (SELECT c3 FROM Connection c3 WHERE c3.follower.id = :userId AND c3.followed.id = c2.followed.id)")
    List<Object> findConnectionSuggestions(@Param("userId") UUID userId);
}
