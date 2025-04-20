package com.taxi.taxi_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSummaryDTO {
	private Integer zoneId;
	private String date;
	
	private long pickupCount;
	private long dropoffCount;
}
