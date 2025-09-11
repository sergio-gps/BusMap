package com.sergiogps.bus_map_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRolName(String rolName);
}
