package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.RolesPermisos;
import com.sergiogps.bus_map_api.entity.key.RolesPermisosId;

public interface RolesPermisosRepository extends JpaRepository<RolesPermisos, RolesPermisosId> { }
