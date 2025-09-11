package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Localizacion;
import com.sergiogps.bus_map_api.repository.LocalizacionRepository;

@Service
public class LocalizacionService implements CrudService<Localizacion, Integer> {
    private final LocalizacionRepository repo;

    public LocalizacionService(LocalizacionRepository repo) { this.repo = repo; }

    @Override
    public List<Localizacion> findAll() { return repo.findAll(); }

    @Override
    public Optional<Localizacion> findById(Integer id) { return repo.findById(id); }

    @Override
    public Localizacion create(Localizacion entity) { return repo.save(entity); }
}
