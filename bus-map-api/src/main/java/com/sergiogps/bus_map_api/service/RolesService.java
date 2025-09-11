package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Roles;
import com.sergiogps.bus_map_api.repository.RolesRepository;

@Service
public class RolesService implements CrudService<Roles, Integer> {
    private final RolesRepository repo;

    public RolesService(RolesRepository repo) { this.repo = repo; }

    @Override
    public List<Roles> findAll() { return repo.findAll(); }

    @Override
    public Optional<Roles> findById(Integer id) { return repo.findById(id); }

    @Override
    public Roles create(Roles entity) { return repo.save(entity); }
}
