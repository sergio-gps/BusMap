package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.RolesPermisos;
import com.sergiogps.bus_map_api.entity.key.RolesPermisosId;
import com.sergiogps.bus_map_api.repository.RolesPermisosRepository;

@Service
public class RolesPermisosService implements CrudService<RolesPermisos, RolesPermisosId> {
    private final RolesPermisosRepository repo;

    public RolesPermisosService(RolesPermisosRepository repo) { this.repo = repo; }

    @Override
    public List<RolesPermisos> findAll() { return repo.findAll(); }

    @Override
    public Optional<RolesPermisos> findById(RolesPermisosId id) { return repo.findById(id); }

    @Override
    public RolesPermisos create(RolesPermisos entity) { return repo.save(entity); }
}
