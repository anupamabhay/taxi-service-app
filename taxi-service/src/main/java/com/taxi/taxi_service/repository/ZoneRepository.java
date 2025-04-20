package com.taxi.taxi_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi.taxi_service.model.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {}
