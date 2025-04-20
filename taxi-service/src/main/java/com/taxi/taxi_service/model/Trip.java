package com.taxi.taxi_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity 
@Data
public class Trip {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "pickup_date_time")
	private LocalDateTime pickupDateTime;
	@Column(name = "dropoff_date_time")
	private LocalDateTime dropoffDateTime;
	@Column(name = "pickup_location_id")
	private Integer pickupLocationID;
	@Column(name = "dropoff_location_id")
	private Integer dropoffLocationID;
}
