package com.sergiogps.bus_map_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    @EntityGraph(attributePaths = {"roles", "seguridad"})
    Optional<Usuarios> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "seguridad"})
    Optional<Usuarios> findByEmail(String email);
}
