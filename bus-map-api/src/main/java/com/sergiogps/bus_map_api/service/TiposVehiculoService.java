package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.entity.TiposVehiculo;
import com.sergiogps.bus_map_api.repository.TiposVehiculoRepository;
import com.sergiogps.bus_map_api.repository.VehiculosRepository;

@Service
public class TiposVehiculoService implements CrudService<TiposVehiculo, Integer> {
    private final TiposVehiculoRepository repo;
    private final VehiculosRepository vehiculosRepository;

    public TiposVehiculoService(TiposVehiculoRepository repo, VehiculosRepository vehiculosRepository) {
        this.repo = repo;
        this.vehiculosRepository = vehiculosRepository;
    }

    @Override
    public List<TiposVehiculo> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<TiposVehiculo> findById(Integer id) {
        Integer tipoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(tipoId);
    }

    @Override
    public TiposVehiculo create(TiposVehiculo entity) {
        TiposVehiculo tipo = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(tipo), "No se pudo crear el tipo de vehículo");
    }

    @Transactional
    public Optional<TiposVehiculo> update(Integer id, TiposVehiculo entity) {
        Integer tipoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(tipoId).map(existing -> {
            existing.setTipoId(tipoId);
            existing.setNombre(entity.getNombre());
            return Objects.requireNonNull(repo.save(existing), "No se pudo actualizar el tipo");
        });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        Integer tipoId = Objects.requireNonNull(id, "id no puede ser null");
        if (!repo.existsById(tipoId)) {
            return false;
        }
        if (vehiculosRepository.countByTipo_TipoId(tipoId) > 0) {
            return false;
        }
        repo.deleteById(tipoId);
        return true;
    }

    @Transactional
    public TiposVehiculo resolveOrCreate(Integer tipoId, String tipoNombre) {
        if (tipoId != null) {
            return repo.findById(tipoId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de vehículo no encontrado con id: " + tipoId));
        }

        if (tipoNombre == null || tipoNombre.isBlank()) {
            throw new IllegalArgumentException("Debe indicar tipoId o tipoNombre");
        }

        return repo.findByNombreIgnoreCase(tipoNombre.trim())
                .orElseGet(() -> {
                    TiposVehiculo nuevoTipo = new TiposVehiculo();
                    nuevoTipo.setNombre(tipoNombre.trim());
                    return repo.save(nuevoTipo);
                });
    }
}
