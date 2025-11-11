package com.drakkarpress.platform.repository;

import com.drakkarpress.platform.model.Rune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuneRepository extends JpaRepository<Rune, UUID> {

    /**
     * Buscar runa por símbolo
     */
    Optional<Rune> findBySymbol(String symbol);

    /**
     * Buscar runa por nombre
     */
    Optional<Rune> findByName(String name);

    /**
     * Buscar runas activas
     */
    List<Rune> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Buscar runas por categoría
     */
    List<Rune> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(String category);

    /**
     * Buscar runas más populares
     */
    @Query("SELECT r FROM Rune r WHERE r.isActive = true ORDER BY r.timesSelected DESC")
    List<Rune> findMostPopular();

    /**
     * Buscar runas menos usadas
     */
    @Query("SELECT r FROM Rune r WHERE r.isActive = true ORDER BY r.timesSelected ASC")
    List<Rune> findLeastUsed();

    /**
     * Contar runas activas
     */
    long countByIsActiveTrue();
}
