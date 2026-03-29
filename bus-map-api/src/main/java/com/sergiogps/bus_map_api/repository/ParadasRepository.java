package com.sergiogps.bus_map_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;

public interface ParadasRepository extends JpaRepository<Paradas, Integer> {

    Optional<Paradas> findByNombreParada(String nombreParada);

    List<Paradas> findByNombreParadaContainingIgnoreCase(String nombreParcial);

    @Query("""
                SELECT l
                FROM Paradas p
                JOIN p.lineas l
                WHERE p.paradaId = :paradaId
            """)
    List<Lineas> findLineasByParadaId(@Param("paradaId") Integer paradaId);

    void insertLineaParada(Integer lineaId, Integer paradaId);

    void deleteLineasByParadaId(Integer paradaId);
}
