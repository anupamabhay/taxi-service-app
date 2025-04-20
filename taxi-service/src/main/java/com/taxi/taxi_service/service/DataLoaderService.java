package com.taxi.taxi_service.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.apache.bcel.util.ClassPath;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVReader;
import com.taxi.taxi_service.model.Trip;
import com.taxi.taxi_service.model.Zone;
import com.taxi.taxi_service.repository.TripRepository;
import com.taxi.taxi_service.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataLoaderService implements ApplicationRunner {	
	private final ZoneRepository zoneRepository;
	private final TripRepository tripRepository;
	
	private static final int BATCH_SIZE = 500;
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	// Dataset file paths
	private final String ZONES_PATH = "data/taxi_zone_lookup.csv";
	private final String YELLOW_TRIPDATA_PATH = "data/yellow_tripdata.csv";
	
	@Override
	@Transactional // Used to wrap the method within a DB transaction such that a transaction is started before the method execution. If method executes successfully, transaction is committed. If method throws an exception, transaction is rolled back.
	public void run(ApplicationArguments args) throws Exception {
		log.info("ApplicationRunner started, initiating data loading process...");
		loadDataOnStartup();
	}	
	
	// Method to load the files on startup
	public void loadDataOnStartup() {
		log.info("Data loading started.");
		
		// data loading logic
		try {
			if(zoneRepository.count() == 0) loadZonesFromFile(ZONES_PATH);
			else log.info("Zones already loaded!");
			
			if(tripRepository.count() == 0) loadTripsFromFile(YELLOW_TRIPDATA_PATH);
			else log.info("Trips already loaded!");
		} catch (Exception e) {
			log.error("Data loading failed.");
			e.printStackTrace();
		}

		log.info("Data loading successful.");
	}
	
	// Load zones from taxi_zone_lookup.csv (ZONES_PATH)
	private void loadZonesFromFile(String resourcePath) {
		log.info("Loading zones from: {}", resourcePath);
		
		/*
		 * ClassPathResource(resourcePath) locates the resource file in the classpath.
		 * getInputStream() opens the file on classpath and creates an InputStream with the ClassPathResource object
		 * InputStreamReader acts as a bridge b/w CSVReader and InputStream. It reads the bytes from InputStream, decodes them into char.
		 * CSVReader object is created with the InputStreamReader as its source. It internally parses the characters according to CSV rules. 
		 */
		try(CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource(resourcePath).getInputStream()))) {
			// Read the header from the CSV
			String[] headers = reader.readNext();
			if(headers == null) {
				log.error("Zone CSV is empty!");
				return;
			}
			
			String[] line;
			List<Zone> zoneList = new ArrayList<Zone>(BATCH_SIZE);
			
			// readNextSilently() reads the next line/record, parses it into a String array. Returns null in case of an exception.
			while((line = reader.readNextSilently()) != null) {
				try {
					Zone zone = new Zone();
					zone.setLocationID(Integer.parseInt(line[0]));
					zone.setBorough(line[1]);
					zone.setZoneName(line[2]);
					zone.setServiceZone(line[3]);
					
					zoneList.add(zone);
					
					// Save the first 100 records
					if(zoneList.size() >= BATCH_SIZE) {
						zoneRepository.saveAll(zoneList);
						zoneList.clear();
					}
				} catch(Exception e) {
					log.warn("Skipping bad zone record: {}", String.join(",", line));
				}
			}
			
			if(!zoneList.isEmpty()) zoneRepository.saveAll(zoneList);
			log.info("Zones have been loaded successfully.");
		
		} catch (Exception e) {
			log.error("Unexpected error loading zones CSV: {}", resourcePath);
			e.printStackTrace();
		}
	}
	
	// Load trips from yellow_tripdata.csv (YELLOW_TRIPDATA_PATH)
	private void loadTripsFromFile(String resourcePath) {
		log.info("Loading tripdata from: {}", resourcePath);
		
		try(CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource(resourcePath).getInputStream()))) {
			String[] headers = reader.readNext();
			if(headers == null) {
				log.error("Trip CSV is empty!"); 
				return; 
			}
			
			String[] line;
			List<Trip> tripList = new ArrayList<Trip>(BATCH_SIZE);
			
			while((line = reader.readNextSilently()) != null) {
				try {
					Trip trip = new Trip();
					trip.setPickupDateTime(LocalDateTime.parse(line[0], DTF));
					trip.setDropoffDateTime(LocalDateTime.parse(line[1], DTF));
					trip.setPickupLocationID(Integer.parseInt(line[2]));
					trip.setDropoffLocationID(Integer.parseInt(line[3]));
					
					tripList.add(trip);
					if(tripList.size() >= BATCH_SIZE) {
						tripRepository.saveAll(tripList);
						tripList.clear();
					}
					
				} catch(Exception e) {
					log.warn("Skipping bad tripdata record: {}", String.join(",", line));
					e.printStackTrace();
				}
			}
			
			if(!tripList.isEmpty()) tripRepository.saveAll(tripList);
			log.info("Tripdata has been loaded successfully.");
			
		} catch (Exception e) {
			log.error("Unexpected error loading tripdata CSV: {}", resourcePath);
			e.printStackTrace();
		}
	}
}
