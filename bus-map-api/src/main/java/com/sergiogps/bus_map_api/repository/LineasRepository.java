package com.sergiogps.bus_map_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;

public interface LineasRepository extends JpaRepository<Lineas, Integer> {

    Optional<Lineas> findByLineaId(Integer lineasId);

    Optional<Lineas> findByNombre(String nombreLinea);

    List<Lineas> findByNombreContainingIgnoreCase(String nombreParcial);

    @Query("SELECT p FROM Lineas l JOIN l.paradas p WHERE l.lineaId = :lineaId")
    List<Paradas> findParadasByLineaId(
            @Param("lineaId") Integer lineaId);

    @Modifying
    @Query(value = "INSERT INTO lineas_paradas (linea_id, parada_id) VALUES (:lineaId, :paradaId)", nativeQuery = true)
    void insertParadaInLinea(@Param("lineaId") Integer lineaId, @Param("paradaId") Integer paradaId);

    @Modifying
    @Query(value = "DELETE FROM lineas_paradas WHERE linea_id = :lineaId AND parada_id = :paradaId", nativeQuery = true)
    void deleteParadaFromLinea(@Param("lineaId") Integer lineaId, @Param("paradaId") Integer paradaId);
}
