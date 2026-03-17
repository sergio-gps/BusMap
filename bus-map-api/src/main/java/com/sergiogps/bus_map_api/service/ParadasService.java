package com.sergiogps.bus_map_api.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.dto.Parada;
import com.sergiogps.bus_map_api.entity.Paradas;
import com.sergiogps.bus_map_api.repository.LineasRepository;
import com.sergiogps.bus_map_api.repository.ParadasRepository;

@Service
public class ParadasService {

    private final ParadasRepository _paradasRepository;
    private final LineasRepository _lineasRepository;

    public ParadasService(ParadasRepository paradasRepository, LineasRepository lineasRepository) {
        this._paradasRepository = paradasRepository;
        this._lineasRepository = lineasRepository;
    }

    @Transactional(readOnly = true)
    public List<Parada> findAll() {
        List<Paradas> entidades = _paradasRepository.findAll();
        List<Parada> resultado = new ArrayList<>(entidades.size());

        for (Paradas entidad : entidades) {
            resultado.add(toDto(entidad));
        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public Optional<Parada> findById(Integer paradaId) {
        Integer id = Objects.requireNonNull(paradaId, "paradaId no puede ser null");
        return _paradasRepository.findById(id).map(this::toDto);
    }

    @Transactional
    @SuppressWarnings("null")
    public Parada create(Parada dto) {
        Paradas entidad = toEntity(dto);
        Paradas saved = Objects.requireNonNull(_paradasRepository.save(entidad), "No se pudo guardar la parada");
        syncLineas(saved.getParadaId(), dto.getLineas());
        return toDto(saved);
    }

    @Transactional
    public Optional<Parada> update(Integer paradaId, Parada dto) {
        Integer id = Objects.requireNonNull(paradaId, "paradaId no puede ser null");
        return _paradasRepository.findById(id).map(existing -> {
            existing.setNombre(dto.getNombre());
            existing.setLatitud(dto.getLatitud());
            existing.setLongitud(dto.getLongitud());

            // Si el body trae un número distinto, prevalece el de la URL.
            existing.setParadaId(id);

            Paradas saved = Objects.requireNonNull(_paradasRepository.save(existing), "No se pudo actualizar la parada");
            syncLineas(id, dto.getLineas());
            return toDto(saved);
        });
    }

    @Transactional
    public boolean deleteById(Integer paradaId) {
        Integer id = Objects.requireNonNull(paradaId, "paradaId no puede ser null");
        if (!_paradasRepository.existsById(id)) {
            return false;
        }

        _paradasRepository.deleteLineasByParadaId(id);
        _paradasRepository.deleteById(id);
        return true;
    }

    @Transactional
    @SuppressWarnings("null")
    public int importParadas(List<Parada> paradas) {
        int procesadas = 0;
        if (paradas == null || paradas.isEmpty()) {
            return procesadas;
        }

        for (Parada parada : paradas) {
            if (parada == null || parada.getId() <= 0 || parada.getNombre() == null || parada.getNombre().isBlank()) {
                continue;
            }

            Paradas entidad = toEntity(parada);
            Paradas saved = Objects.requireNonNull(_paradasRepository.save(entidad), "No se pudo guardar la parada importada");
            syncLineas(saved.getParadaId(), parada.getLineas());
            procesadas++;
        }

        return procesadas;
    }

    private Parada toDto(Paradas entidad) {
        Parada dto = new Parada();
        dto.setId(entidad.getParadaId());
        dto.setNombre(entidad.getNombre());
        dto.setLatitud(entidad.getLatitud() != null ? entidad.getLatitud() : 0.0d);
        dto.setLongitud(entidad.getLongitud() != null ? entidad.getLongitud() : 0.0d);

        List<Integer> lineas = _paradasRepository.findLineasByParadaId(entidad.getParadaId());
        dto.setLineasIntegers(new ArrayList<>(lineas));
        return dto;
    }

    private Paradas toEntity(Parada dto) {
        Paradas entidad = new Paradas();
        entidad.setParadaId(dto.getId());
        entidad.setNombre(dto.getNombre());
        entidad.setLatitud(dto.getLatitud());
        entidad.setLongitud(dto.getLongitud());
        return entidad;
    }

    private void syncLineas(Integer paradaId, List<Integer> lineas) {
        _paradasRepository.deleteLineasByParadaId(paradaId);

        Set<Integer> lineasUnicas = new LinkedHashSet<>();
        if (lineas != null) {
            for (Integer lineaId : lineas) {
                if (lineaId != null && lineaId > 0 && _lineasRepository.existsById(lineaId)) {
                    lineasUnicas.add(lineaId);
                }
            }
        }

        for (Integer lineaId : lineasUnicas) {
            _paradasRepository.insertLineaParada(lineaId, paradaId);
        }
    }
}
