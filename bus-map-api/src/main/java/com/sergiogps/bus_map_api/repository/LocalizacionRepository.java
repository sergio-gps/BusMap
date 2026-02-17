package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sergiogps.bus_map_api.entity.Localizacion;

public interface LocalizacionRepository extends JpaRepository<Localizacion, Integer> { }
