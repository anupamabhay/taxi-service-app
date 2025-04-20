package com.taxi.taxi_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Zone {
	//"LocationID","Borough","Zone","service_zone"
	@Id @Column(name = "location_id")
	private Integer locationID;
	private String borough;
	private String zoneName;
	private String serviceZone;
}
