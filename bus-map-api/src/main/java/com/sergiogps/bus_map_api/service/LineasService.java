package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.repository.LineasRepository;

@Service
public class LineasService implements CrudService<Lineas, Integer> {
    private final LineasRepository repo;

    public LineasService(LineasRepository repo) { this.repo = repo; }

    @Override
    public List<Lineas> findAll() { return repo.findAll(); }

    @Override
    public Optional<Lineas> findById(Integer id) { return repo.findById(id); }

    @Override
    public Lineas create(Lineas entity) { return repo.save(entity); }
}
