package com.sergiogps.bus_map_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.TiposVehiculo;

public interface TiposVehiculoRepository extends JpaRepository<TiposVehiculo, Integer> {

    Optional<TiposVehiculo> findByNombreIgnoreCase(String nombre);
}
