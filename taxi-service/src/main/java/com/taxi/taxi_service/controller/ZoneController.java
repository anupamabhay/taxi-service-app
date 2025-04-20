package com.taxi.taxi_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.taxi_service.model.Zone;
import com.taxi.taxi_service.service.TripService;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {
	@Autowired private TripService tripService;
	
	@GetMapping
	public ResponseEntity<List<Zone>> getAllZones() {
		return ResponseEntity.ok(tripService.getAllZones());
	}
}
