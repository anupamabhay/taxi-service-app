package com.taxi.taxi_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.taxi_service.dto.TopZoneDTO;
import com.taxi.taxi_service.dto.TripSummaryDTO;
import com.taxi.taxi_service.model.Trip;
import com.taxi.taxi_service.model.Zone;
import com.taxi.taxi_service.repository.TripRepository;
import com.taxi.taxi_service.repository.ZoneRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;



@Service
@Transactional(readOnly = true) // readOnly specifies that the code within this transaction will only perform READ operations.
@Slf4j
public class TripService {
	@Autowired private EntityManager entityManager;
	@Autowired private ZoneRepository zoneRepository;
	@Autowired private TripRepository tripRepository;
	
	// Method to GET top 5 zones based on pickup or dropoff
	public List<TopZoneDTO> getTopZones(String orderBy) {
		String locationField = "pickupLocationID";
		if(orderBy.equalsIgnoreCase("dropoff")) locationField = "dropoffLocationID";
		
		String jpql = """
	              SELECT NEW com.taxi.taxi_service.dto.TopZoneDTO(z.zoneName, COUNT(t))
	              FROM Trip t JOIN Zone z ON t.%s = z.locationID
	              GROUP BY z.zoneName
	              ORDER BY COUNT(t) DESC
	              """.formatted(locationField);
		
		TypedQuery<TopZoneDTO> query = entityManager.createQuery(jpql, TopZoneDTO.class).setMaxResults(5);
		List<TopZoneDTO> resultList = query.getResultList();
		return resultList;
	}
	
	// GET all zones 
	public List<Zone> getAllZones() {
		return zoneRepository.findAll();
	}
	
	// GET trip summary for a specific zone on a specific data
	public TripSummaryDTO getTripSummary(Integer zoneId, LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(LocalTime.MAX);
		
		long pickupCount = countTrips(zoneId, start, end, "pickupLocationID", "pickupDateTime");
		long dropoffCount = countTrips(zoneId, start, end, "dropoffLocationID", "dropoffDateTime");
		
		return new TripSummaryDTO(zoneId, date.toString(), pickupCount, dropoffCount);
	}
	
	// Helper method for counting trips
	public long countTrips(Integer zoneId, LocalDateTime start, LocalDateTime end, String locationField, String dateField) {
		String jpql = "SELECT COUNT(t) FROM Trip t WHERE t." + locationField + " = :zoneId AND t." + dateField + " BETWEEN :start AND :end";
		
		try {
			TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
					.setParameter("zoneId", zoneId)
					.setParameter("start", start)
					.setParameter("end", end);
			
			Long resultCount = query.getSingleResult();
			return resultCount != null ? resultCount : 0L;			
			
		} catch (NoResultException e) {
			log.info("No result found for the given data.");
			return 0L;
		}
	}
	
	// Method for paginating, filtering and sorting trips
	public Page<Trip> findTrips(Integer pickupLocationId, Integer dropoffLocationId, LocalDate pickupDate, LocalDate dropoffDate, Pageable pageable) {
		log.info("Finding trips with filters: {}, {}, {}, {}, {}", pickupLocationId, dropoffLocationId, pickupDate, dropoffDate, pageable);
		
		Specification<Trip> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			// Filter for pickupLocationID
			if(pickupLocationId != null) {
				predicates.add(criteriaBuilder.equal(root.get("pickupLocationID"), pickupLocationId));
			}
			
			// Filter for dropoffLocationID
			if(dropoffLocationId != null) {
				predicates.add(criteriaBuilder.equal(root.get("dropoffLocationID"), dropoffLocationId));
			}
			
			// Filter for pickupDate
			if(pickupDate != null) {
				LocalDateTime start = pickupDate.atStartOfDay();
				LocalDateTime end = pickupDate.atTime(LocalTime.MAX);
				predicates.add(criteriaBuilder.between(root.get("pickupDateTime"), start, end));
			}
			
			// Filter for dropoffDate
			if(dropoffDate != null) {
				LocalDateTime start = dropoffDate.atStartOfDay();
				LocalDateTime end = dropoffDate.atTime(LocalTime.MAX);
				predicates.add(criteriaBuilder.between(root.get("dropoffDateTime"), start, end));
			}
			
			// Combine all predicates using AND 
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
		
		// Execute the query using the Specification and Pageable
		return tripRepository.findAll(spec, pageable);
	}
}
