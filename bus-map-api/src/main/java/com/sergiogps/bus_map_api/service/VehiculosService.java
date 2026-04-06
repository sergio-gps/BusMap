package com.sergiogps.bus_map_api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.dto.VehiculoAdminDTO;
import com.sergiogps.bus_map_api.dto.VehiculoAdminRequestDTO;
import com.sergiogps.bus_map_api.entity.TiposVehiculo;
import com.sergiogps.bus_map_api.entity.Vehiculos;
import com.sergiogps.bus_map_api.entity.VehiculosInfo;
import com.sergiogps.bus_map_api.repository.VehiculosInfoRepository;
import com.sergiogps.bus_map_api.repository.VehiculosRepository;

@Service
public class VehiculosService implements CrudService<Vehiculos, Integer> {
    private final VehiculosRepository repo;
    private final VehiculosInfoRepository vehiculosInfoRepository;
    private final TiposVehiculoService tiposVehiculoService;

    public VehiculosService(
            VehiculosRepository repo,
            VehiculosInfoRepository vehiculosInfoRepository,
            TiposVehiculoService tiposVehiculoService
    ) {
        this.repo = repo;
        this.vehiculosInfoRepository = vehiculosInfoRepository;
        this.tiposVehiculoService = tiposVehiculoService;
    }

    @Override
    public List<Vehiculos> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Vehiculos> findById(Integer id) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(vehiculoId);
    }

    @Override
    public Vehiculos create(Vehiculos entity) {
        Vehiculos vehiculo = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(vehiculo), "No se pudo crear el vehículo");
    }

    @Transactional(readOnly = true)
    public List<VehiculoAdminDTO> findAllAdmin() {
        List<Vehiculos> vehiculos = repo.findAllWithTipo();
        if (vehiculos.isEmpty()) {
            return List.of();
        }

        List<Integer> ids = vehiculos.stream()
                .map(Vehiculos::getVehiculoId)
                .filter(Objects::nonNull)
                .toList();

        Map<Integer, VehiculosInfo> infoByVehiculoId = new HashMap<>();
        for (VehiculosInfo info : vehiculosInfoRepository.findByVehiculo_VehiculoIdIn(ids)) {
            if (info != null && info.getVehiculoId() != null) {
                infoByVehiculoId.put(info.getVehiculoId(), info);
            }
        }

        List<VehiculoAdminDTO> resultado = new ArrayList<>();
        for (Vehiculos vehiculo : vehiculos) {
            resultado.add(toAdminDto(vehiculo, infoByVehiculoId.get(vehiculo.getVehiculoId())));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoAdminDTO> findAdminById(Integer id) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        Vehiculos vehiculo = repo.findByIdWithTipo(vehiculoId);
        if (vehiculo == null) {
            return Optional.empty();
        }
        VehiculosInfo info = vehiculosInfoRepository.findByVehiculo_VehiculoId(vehiculoId).orElse(null);
        return Optional.of(toAdminDto(vehiculo, info));
    }

    @Transactional(readOnly = true)
    public List<VehiculoAdminDTO> searchAdmin(String query) {
        if (query == null || query.isBlank()) {
            return findAllAdmin();
        }

        String normalized = query.trim().toLowerCase();
        return findAllAdmin().stream()
                .filter(item ->
                        containsIgnoreCase(item.matricula(), normalized)
                                || (item.vehiculoId() != null && item.vehiculoId().toString().contains(normalized))
                                || containsIgnoreCase(item.marcaModelo(), normalized)
                                || containsIgnoreCase(item.tipoNombre(), normalized)
                )
                .toList();
    }

    @Transactional
    public VehiculoAdminDTO createAdmin(VehiculoAdminRequestDTO body) {
        VehiculoAdminRequestDTO request = Objects.requireNonNull(body, "body no puede ser null");

        TiposVehiculo tipo = tiposVehiculoService.resolveOrCreate(request.tipoId(), request.tipoNombre());

        Vehiculos vehiculo = new Vehiculos();
        vehiculo.setTipo(tipo);
        Vehiculos savedVehiculo = Objects.requireNonNull(repo.save(vehiculo), "No se pudo crear el vehículo");

        VehiculosInfo info = new VehiculosInfo();
        info.setVehiculo(savedVehiculo);
        info.setMatricula(Objects.requireNonNull(request.matricula(), "matricula es obligatoria"));
        info.setMarcaModelo(request.marcaModelo());
        info.setCapacidad(request.capacidad());
        info.setActivo(request.activo() != null ? request.activo() : Boolean.TRUE);
        VehiculosInfo savedInfo = Objects.requireNonNull(vehiculosInfoRepository.save(info), "No se pudo crear info de vehículo");

        Vehiculos withTipo = repo.findByIdWithTipo(savedVehiculo.getVehiculoId());
        return toAdminDto(withTipo, savedInfo);
    }

    @Transactional
    public Optional<VehiculoAdminDTO> updateAdmin(Integer id, VehiculoAdminRequestDTO body) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        VehiculoAdminRequestDTO request = Objects.requireNonNull(body, "body no puede ser null");

        return repo.findById(vehiculoId).map(existing -> {
            Integer previousTipoId = existing.getTipo() != null ? existing.getTipo().getTipoId() : null;

            TiposVehiculo tipo = tiposVehiculoService.resolveOrCreate(request.tipoId(), request.tipoNombre());
            existing.setTipo(tipo);
            existing.setVehiculoId(vehiculoId);
            Vehiculos savedVehiculo = Objects.requireNonNull(repo.save(existing), "No se pudo actualizar el vehículo");

            VehiculosInfo info = vehiculosInfoRepository.findByVehiculo_VehiculoId(vehiculoId).orElseGet(() -> {
                VehiculosInfo nuevaInfo = new VehiculosInfo();
                nuevaInfo.setVehiculo(savedVehiculo);
                return nuevaInfo;
            });

            info.setVehiculo(savedVehiculo);
            info.setMatricula(Objects.requireNonNull(request.matricula(), "matricula es obligatoria"));
            info.setMarcaModelo(request.marcaModelo());
            info.setCapacidad(request.capacidad());
            info.setActivo(request.activo() != null ? request.activo() : Boolean.TRUE);
            VehiculosInfo savedInfo = Objects.requireNonNull(vehiculosInfoRepository.save(info), "No se pudo actualizar info de vehículo");

            if (previousTipoId != null && !Objects.equals(previousTipoId, tipo.getTipoId()) && repo.countByTipo_TipoId(previousTipoId) == 0) {
                tiposVehiculoService.deleteById(previousTipoId);
            }

            Vehiculos withTipo = repo.findByIdWithTipo(savedVehiculo.getVehiculoId());
            return toAdminDto(withTipo, savedInfo);
        });
    }

    @Transactional
    public boolean deleteAdminById(Integer id) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");

        Optional<Vehiculos> vehiculoOpt = repo.findById(vehiculoId);
        if (vehiculoOpt.isEmpty()) {
            return false;
        }

        Vehiculos vehiculo = vehiculoOpt.get();
        Integer tipoId = vehiculo.getTipo() != null ? vehiculo.getTipo().getTipoId() : null;

        vehiculo.getLineas().forEach(linea -> linea.getVehiculos().remove(vehiculo));

        vehiculosInfoRepository.findByVehiculo_VehiculoId(vehiculoId)
                .ifPresent(vehiculosInfoRepository::delete);

        repo.delete(vehiculo);

        if (tipoId != null && repo.countByTipo_TipoId(tipoId) == 0) {
            tiposVehiculoService.deleteById(tipoId);
        }

        return true;
    }

    @Transactional
    public Optional<Vehiculos> update(Integer id, Vehiculos entity) {
        Integer vehiculoId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(vehiculoId).map(existing -> {
            existing.setTipo(entity.getTipo());
            existing.setVehiculoId(vehiculoId);
            return Objects.requireNonNull(repo.save(existing), "No se pudo actualizar el vehículo");
        });
    }

    @Transactional
    public boolean deleteById(Integer id) {
        return deleteAdminById(id);
    }

    @Transactional
    public int importVehiculos(List<Vehiculos> vehiculos) {
        int procesadas = 0;
        if (vehiculos == null || vehiculos.isEmpty()) {
            return procesadas;
        }

        for (Vehiculos vehiculo : vehiculos) {
            if (vehiculo == null) {
                continue;
            }
            Objects.requireNonNull(repo.save(vehiculo), "No se pudo guardar el vehículo importado");
            procesadas++;
        }

        return procesadas;
    }

    private VehiculoAdminDTO toAdminDto(Vehiculos vehiculo, VehiculosInfo info) {
        String tipoNombre = null;
        Integer tipoId = null;
        if (vehiculo != null && vehiculo.getTipo() != null) {
            tipoId = vehiculo.getTipo().getTipoId();
            tipoNombre = vehiculo.getTipo().getNombre();
        }

        return new VehiculoAdminDTO(
                vehiculo != null ? vehiculo.getVehiculoId() : null,
                tipoId,
                tipoNombre,
                info != null ? info.getMatricula() : null,
                info != null ? info.getMarcaModelo() : null,
                info != null ? info.getCapacidad() : null,
                info != null ? info.getActivo() : null
        );
    }

    private boolean containsIgnoreCase(String value, String queryLower) {
        return value != null && value.toLowerCase().contains(queryLower);
    }
}
