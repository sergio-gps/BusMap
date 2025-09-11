package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Seguridad;
import com.sergiogps.bus_map_api.repository.SeguridadRepository;

@Service
public class SeguridadService implements CrudService<Seguridad, Integer> {
    private final SeguridadRepository repo;

    public SeguridadService(SeguridadRepository repo) { this.repo = repo; }

    @Override
    public List<Seguridad> findAll() { return repo.findAll(); }

    @Override
    public Optional<Seguridad> findById(Integer id) { return repo.findById(id); }

    @Override
    public Seguridad create(Seguridad entity) { return repo.save(entity); }
}
