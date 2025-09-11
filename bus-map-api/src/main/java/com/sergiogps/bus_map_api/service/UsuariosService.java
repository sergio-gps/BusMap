package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;

@Service
public class UsuariosService implements CrudService<Usuarios, Integer> {
    private final UsuariosRepository repo;

    public UsuariosService(UsuariosRepository repo) { this.repo = repo; }

    @Override
    public List<Usuarios> findAll() { return repo.findAll(); }

    @Override
    public Optional<Usuarios> findById(Integer id) { return repo.findById(id); }

    @Override
    public Usuarios create(Usuarios entity) { return repo.save(entity); }
}
