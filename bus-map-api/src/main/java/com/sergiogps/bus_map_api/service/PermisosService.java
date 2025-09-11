package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Permisos;
import com.sergiogps.bus_map_api.repository.PermisosRepository;

@Service
public class PermisosService implements CrudService<Permisos, Integer> {
    private final PermisosRepository repo;

    public PermisosService(PermisosRepository repo) { this.repo = repo; }

    @Override
    public List<Permisos> findAll() { return repo.findAll(); }

    @Override
    public Optional<Permisos> findById(Integer id) { return repo.findById(id); }

    @Override
    public Permisos create(Permisos entity) { return repo.save(entity); }
}
