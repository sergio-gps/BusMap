package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Vehiculos;
import com.sergiogps.bus_map_api.repository.VehiculosRepository;

@Service
public class VehiculosService implements CrudService<Vehiculos, Integer> {
    private final VehiculosRepository repo;

    public VehiculosService(VehiculosRepository repo) { this.repo = repo; }

    @Override
    public List<Vehiculos> findAll() { return repo.findAll(); }

    @Override
    public Optional<Vehiculos> findById(Integer id) { return repo.findById(id); }

    @Override
    public Vehiculos create(Vehiculos entity) { return repo.save(entity); }
}
