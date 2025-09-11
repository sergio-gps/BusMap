package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.VehiculosInfo;
import com.sergiogps.bus_map_api.repository.VehiculosInfoRepository;

@Service
public class VehiculosInfoService implements CrudService<VehiculosInfo, Integer> {
    private final VehiculosInfoRepository repo;

    public VehiculosInfoService(VehiculosInfoRepository repo) { this.repo = repo; }

    @Override
    public List<VehiculosInfo> findAll() { return repo.findAll(); }

    @Override
    public Optional<VehiculosInfo> findById(Integer id) { return repo.findById(id); }

    @Override
    public VehiculosInfo create(VehiculosInfo entity) { return repo.save(entity); }
}
