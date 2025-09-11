package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.BusesLinea;
import com.sergiogps.bus_map_api.entity.key.BusesLineaId;
import com.sergiogps.bus_map_api.repository.BusesLineaRepository;

@Service
public class BusesLineaService implements CrudService<BusesLinea, BusesLineaId> {
    private final BusesLineaRepository repo;

    public BusesLineaService(BusesLineaRepository repo) { this.repo = repo; }

    @Override
    public List<BusesLinea> findAll() { return repo.findAll(); }

    @Override
    public Optional<BusesLinea> findById(BusesLineaId id) { return repo.findById(id); }

    @Override
    public BusesLinea create(BusesLinea entity) { return repo.save(entity); }
}
