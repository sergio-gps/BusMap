package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.UsuariosRoles;
import com.sergiogps.bus_map_api.entity.key.UsuariosRolesId;

public interface UsuariosRolesRepository extends JpaRepository<UsuariosRoles, UsuariosRolesId> { }
