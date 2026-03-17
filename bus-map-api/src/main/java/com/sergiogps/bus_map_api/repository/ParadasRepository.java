package com.sergiogps.bus_map_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sergiogps.bus_map_api.entity.Paradas;

public interface ParadasRepository extends JpaRepository<Paradas, Integer> {

    @Query(value = "SELECT linea_id FROM lineas_paradas WHERE parada_id = :paradaId ORDER BY linea_id", nativeQuery = true)
    List<Integer> findLineasByParadaId(@Param("paradaId") Integer paradaId);

    @Modifying
    @Query(value = "DELETE FROM lineas_paradas WHERE parada_id = :paradaId", nativeQuery = true)
    void deleteLineasByParadaId(@Param("paradaId") Integer paradaId);

    @Modifying
    @Query(value = "INSERT INTO lineas_paradas (linea_id, parada_id) VALUES (:lineaId, :paradaId)", nativeQuery = true)
    void insertLineaParada(@Param("lineaId") Integer lineaId, @Param("paradaId") Integer paradaId);
}
