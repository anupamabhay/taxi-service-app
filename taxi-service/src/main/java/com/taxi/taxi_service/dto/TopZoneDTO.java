package com.taxi.taxi_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopZoneDTO {
	private String zoneName;
	private long count;
}
