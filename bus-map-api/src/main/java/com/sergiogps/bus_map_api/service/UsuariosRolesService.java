package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.UsuariosRoles;
import com.sergiogps.bus_map_api.entity.key.UsuariosRolesId;
import com.sergiogps.bus_map_api.repository.UsuariosRolesRepository;

@Service
public class UsuariosRolesService implements CrudService<UsuariosRoles, UsuariosRolesId> {
    private final UsuariosRolesRepository repo;

    public UsuariosRolesService(UsuariosRolesRepository repo) { this.repo = repo; }

    @Override
    public List<UsuariosRoles> findAll() { return repo.findAll(); }

    @Override
    public Optional<UsuariosRoles> findById(UsuariosRolesId id) { return repo.findById(id); }

    @Override
    public UsuariosRoles create(UsuariosRoles entity) { return repo.save(entity); }
}
