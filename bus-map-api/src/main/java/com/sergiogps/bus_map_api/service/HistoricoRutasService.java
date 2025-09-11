package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.HistoricoRutas;
import com.sergiogps.bus_map_api.repository.HistoricoRutasRepository;

@Service
public class HistoricoRutasService implements CrudService<HistoricoRutas, Integer> {
    private final HistoricoRutasRepository repo;

    public HistoricoRutasService(HistoricoRutasRepository repo) { this.repo = repo; }

    @Override
    public List<HistoricoRutas> findAll() { return repo.findAll(); }

    @Override
    public Optional<HistoricoRutas> findById(Integer id) { return repo.findById(id); }

    @Override
    public HistoricoRutas create(HistoricoRutas entity) { return repo.save(entity); }
}
