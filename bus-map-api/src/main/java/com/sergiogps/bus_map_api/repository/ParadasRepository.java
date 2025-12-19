package com.sergiogps.bus_map_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergiogps.bus_map_api.entity.Paradas;

@Repository
public interface ParadasRepository extends JpaRepository<Paradas, Integer> {}