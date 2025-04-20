package com.taxi.taxi_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.taxi_service.dto.TopZoneDTO;
import com.taxi.taxi_service.dto.TripSummaryDTO;
import com.taxi.taxi_service.model.Trip;
import com.taxi.taxi_service.service.TripService;

@RestController
@RequestMapping("/api")
public class TripController {
	@Autowired private TripService tripService;
	
	@GetMapping("/top-zones")
	public ResponseEntity<List<TopZoneDTO>> getTopZones(@RequestParam(defaultValue = "pickup") String orderBy) {
		return ResponseEntity.ok(tripService.getTopZones(orderBy));
	}
	
	@GetMapping("/zone-trips")
	public ResponseEntity<TripSummaryDTO> getTripSummary(@RequestParam Integer zoneId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(tripService.getTripSummary(zoneId, date));
	}
	
	@GetMapping("/list-trips")
	public ResponseEntity<Page<Trip>> listTrips(
			@RequestParam(required = false) Integer pickupLocationId,
			@RequestParam(required = false) Integer dropoffLocationId,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate pickupDate,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE)  LocalDate dropoffDate,
			Pageable pageable	
	) {
		Page<Trip> tripPage = tripService.findTrips(pickupLocationId, dropoffLocationId, pickupDate, dropoffDate, pageable);
		
		return ResponseEntity.ok(tripPage);
	}
}
