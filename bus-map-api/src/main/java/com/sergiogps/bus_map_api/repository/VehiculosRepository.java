package com.sergiogps.bus_map_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sergiogps.bus_map_api.entity.Vehiculos;

public interface VehiculosRepository extends JpaRepository<Vehiculos, Integer> {

    @Query("SELECT v FROM Vehiculos v LEFT JOIN FETCH v.tipo")
    List<Vehiculos> findAllWithTipo();

    long countByTipo_TipoId(Integer tipoId);

    @Query("SELECT DISTINCT v.vehiculoId FROM Vehiculos v")
    List<Integer> findAllVehiculoIds();

    @Query("SELECT v FROM Vehiculos v LEFT JOIN FETCH v.tipo WHERE v.vehiculoId = :vehiculoId")
    Vehiculos findByIdWithTipo(@Param("vehiculoId") Integer vehiculoId);
}
