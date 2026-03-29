package com.sergiogps.bus_map_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;

public interface LineasRepository extends JpaRepository<Lineas, Integer> {

    Optional<Lineas> findById(String lineasId);

    Optional<Lineas> findByNombreLinea(String nombreLinea);

    List<Lineas> findByNombreLineaContainingIgnoreCase(String nombreParcial);

    @Query("SELECT p FROM Lineas l JOIN l.paradas p WHERE l.id = :lineaId")
    List<Paradas> findParadasByLineaId(
            @Param("lineaId") Integer lineaId);
}
