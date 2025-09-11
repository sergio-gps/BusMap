package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.BusesLinea;
import com.sergiogps.bus_map_api.entity.key.BusesLineaId;

public interface BusesLineaRepository extends JpaRepository<BusesLinea, BusesLineaId> { }
