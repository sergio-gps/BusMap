package com.sergiogps.bus_map_api.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.entity.Lineas;
import com.sergiogps.bus_map_api.entity.Paradas;
import com.sergiogps.bus_map_api.repository.LineasRepository;
import com.sergiogps.bus_map_api.repository.ParadasRepository;

@Service
public class LineasService implements CrudService<Lineas, Integer> {
    private final LineasRepository repo;
    private final ParadasRepository paradasRepository;

    public LineasService(LineasRepository repo, ParadasRepository paradasRepository) {
        this.repo = repo;
        this.paradasRepository = paradasRepository;
    }

    @Override
    public List<Lineas> findAll() { return repo.findAll(); }

    @Override
    public Optional<Lineas> findById(Integer id) {
        Integer lineaId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(lineaId);
    }

    @Override
    public Lineas create(Lineas entity) {
        Lineas linea = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(linea), "No se pudo crear la línea");
    }

    @Transactional
    public Optional<Lineas> update(Integer id, Lineas entity) {
        Integer lineaId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(lineaId).map(existing -> {
            // El ID de la URL prevalece para evitar cambios de clave primaria desde el body.
            existing.setLineaId(lineaId);
            existing.setNombre(entity.getNombre());
            existing.setDestino(entity.getDestino());
            existing.setOrigen(entity.getOrigen());
            existing.setColor(entity.getColor());

            if (entity.getParadas() != null) {
                syncParadas(lineaId, entity.getParadas());
                existing.setParadas(repo.findParadasByLineaId(lineaId));
            }
            return Objects.requireNonNull(repo.save(existing), "No se pudo actualizar la línea");
        });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        Integer lineaId = Objects.requireNonNull(id, "id no puede ser null");
        if (!repo.existsById(lineaId)) {
            return false;
        }
        repo.deleteById(lineaId);
        return true;
    }

    @Transactional
    public int importLineas(List<Lineas> lineas) {
        int procesadas = 0;
        if (lineas == null || lineas.isEmpty()) {
            return procesadas;
        }

        for (Lineas linea : lineas) {
            if (linea == null || linea.getLineaId() == null || linea.getLineaId() <= 0) {
                continue;
            }
            Objects.requireNonNull(repo.save(linea), "No se pudo guardar la línea importada");
            procesadas++;
        }

        return procesadas;
    }

    @Transactional(readOnly = true)
    public Optional<List<Paradas>> findParadasByLineaId(Integer lineaId) {
        Integer id = Objects.requireNonNull(lineaId, "lineaId no puede ser null");
        if (!repo.existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(repo.findParadasByLineaId(id));
    }

    @Transactional
    public Optional<Lineas> addParadaToLinea(Integer lineaId, Integer paradaId) {
        Integer lId = Objects.requireNonNull(lineaId, "lineaId no puede ser null");
        Integer pId = Objects.requireNonNull(paradaId, "paradaId no puede ser null");

        if (!repo.existsById(lId) || !paradasRepository.existsById(pId)) {
            return Optional.empty();
        }

        boolean alreadyLinked = repo.findParadasByLineaId(lId).stream()
                .anyMatch(parada -> Objects.equals(parada.getParadaId(), pId));

        if (!alreadyLinked) {
            repo.insertParadaInLinea(lId, pId);
        }

        return repo.findById(lId);
    }

    @Transactional
    public Optional<Lineas> removeParadaFromLinea(Integer lineaId, Integer paradaId) {
        Integer lId = Objects.requireNonNull(lineaId, "lineaId no puede ser null");
        Integer pId = Objects.requireNonNull(paradaId, "paradaId no puede ser null");

        if (!repo.existsById(lId) || !paradasRepository.existsById(pId)) {
            return Optional.empty();
        }

        repo.deleteParadaFromLinea(lId, pId);
        return repo.findById(lId);
    }

    private void syncParadas(Integer lineaId, List<Paradas> paradas) {
        Set<Integer> paradasUnicas = new LinkedHashSet<>();
        for (Paradas parada : paradas) {
            if (parada != null && parada.getParadaId() != null) {
                Integer paradaId = Objects.requireNonNull(parada.getParadaId(), "paradaId no puede ser null");
                if (paradasRepository.existsById(paradaId)) {
                    paradasUnicas.add(paradaId);
                }
            }
        }

        List<Paradas> actuales = repo.findParadasByLineaId(lineaId);
        for (Paradas paradaActual : actuales) {
            if (paradaActual != null && paradaActual.getParadaId() != null && !paradasUnicas.contains(paradaActual.getParadaId())) {
                repo.deleteParadaFromLinea(lineaId, paradaActual.getParadaId());
            }
        }

        for (Integer paradaId : paradasUnicas) {
            boolean exists = actuales.stream().anyMatch(p -> Objects.equals(p.getParadaId(), paradaId));
            if (!exists) {
                repo.insertParadaInLinea(lineaId, paradaId);
            }
        }
    }
}
