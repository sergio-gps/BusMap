package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.entity.VehiculosInfo;
import com.sergiogps.bus_map_api.repository.VehiculosInfoRepository;

@Service
public class VehiculosInfoService implements CrudService<VehiculosInfo, Integer> {
    private final VehiculosInfoRepository repo;

    public VehiculosInfoService(VehiculosInfoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<VehiculosInfo> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<VehiculosInfo> findById(Integer id) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(vehiculoId);
    }

    @Override
    public VehiculosInfo create(VehiculosInfo entity) {
        VehiculosInfo info = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(info), "No se pudo crear la info de vehículo");
    }

    @Transactional
    public Optional<VehiculosInfo> update(Integer id, VehiculosInfo entity) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(vehiculoId).map(existing -> {
            existing.setVehiculoId(vehiculoId);
            existing.setMatricula(entity.getMatricula());
            existing.setMarcaModelo(entity.getMarcaModelo());
            existing.setCapacidad(entity.getCapacidad());
            existing.setActivo(entity.getActivo());
            return Objects.requireNonNull(repo.save(existing), "No se pudo actualizar la info de vehículo");
        });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        if (!repo.existsById(vehiculoId)) {
            return false;
        }
        repo.deleteById(vehiculoId);
        return true;
    }
}
