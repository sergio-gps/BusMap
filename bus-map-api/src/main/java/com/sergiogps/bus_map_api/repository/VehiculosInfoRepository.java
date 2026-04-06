package com.sergiogps.bus_map_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.VehiculosInfo;

public interface VehiculosInfoRepository extends JpaRepository<VehiculosInfo, Integer> {

    Optional<VehiculosInfo> findByVehiculo_VehiculoId(Integer vehiculoId);

    List<VehiculosInfo> findByVehiculo_VehiculoIdIn(List<Integer> vehiculoIds);

    List<VehiculosInfo> findByMatriculaContainingIgnoreCase(String matricula);

    List<VehiculosInfo> findByMarcaModeloContainingIgnoreCase(String marcaModelo);
}
