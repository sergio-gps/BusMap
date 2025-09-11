package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.TiposVehiculo;
import com.sergiogps.bus_map_api.repository.TiposVehiculoRepository;

@Service
public class TiposVehiculoService implements CrudService<TiposVehiculo, Integer> {
    private final TiposVehiculoRepository repo;

    public TiposVehiculoService(TiposVehiculoRepository repo) { this.repo = repo; }

    @Override
    public List<TiposVehiculo> findAll() { return repo.findAll(); }

    @Override
    public Optional<TiposVehiculo> findById(Integer id) { return repo.findById(id); }

    @Override
    public TiposVehiculo create(TiposVehiculo entity) { return repo.save(entity); }
}
