package com.sergiogps.bus_map_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.Permisos;

public interface PermisosRepository extends JpaRepository<Permisos, Integer> {
    Optional<Permisos> findByPermisoName(String permisoName);
}
