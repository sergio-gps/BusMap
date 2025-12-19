package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergiogps.bus_map_api.entity.Lineas;

@Repository
public interface LineasRepository extends JpaRepository<Lineas, Integer> { }
