import React, { useEffect, useState } from 'react'
import { getZones, getZoneTrips } from '../services/api';
import styles from './ZoneTrips.module.css';

const ZoneTrips = () => {
    const [allZones, setAllZones] = useState([]);
    const [zonesLoading, setZonesLoading] = useState(false);
    const [selectedZoneId, setSelectedZoneId] = useState('');
    const [selectedDate, setSelectedDate] = useState('');
    const [tripSummary, setTripSummary] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
        
    // Fetch all zones for the dropdown menu
    useEffect(() => {
        const loadAllZones = async () => {
            setZonesLoading(true);
            setErrorMsg('');
            try {
                const response = await getZones();
                setAllZones(response.data || []);
            } catch (error) {
                setErrorMsg("Failed to load zones");
                console.error("Error loading zones:", error);
                setAllZones([]);
            } finally {
                setZonesLoading(false);
            }
        }

        loadAllZones();
    }, []);
    
    // Handle form submission
    const handleSubmit = async (event) => {
        event.preventDefault();
        if(!selectedZoneId || !selectedDate) {
            setErrorMsg("Please select a zone and a date");
            return;
        }

        setIsLoading(true);
        setErrorMsg('');
        setTripSummary(null);
        try {
            const response = await getZoneTrips(selectedZoneId, selectedDate);
            setTripSummary(response.data);
        } catch (error) {
            setErrorMsg("Failed to load trip summary");
            console.error("Error loading trip summary:", error);
            setTripSummary(null);
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <div className={styles.container}>
            <h3 className={styles.title}>Zone Trip Summary</h3>
            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.formGroup}>
                    <label htmlFor="zone-select" className={styles.label}>Select Zone: </label>
                    <select
                        id="zone-select"
                        value={selectedZoneId}
                        onChange={(e) => setSelectedZoneId(e.target.value)}
                        required
                        disabled={zonesLoading}
                        className={styles.select}
                    >
                        <option value="" disabled>
                            {zonesLoading ? 'Loading Zones...' : '-- Select a Zone --'}
                        </option>

                        {allZones.map((zone) => (
                            <option key={zone.locationID} value={zone.locationID}>
                            {zone.zoneName} ({zone.borough})
                            </option>
                        ))}
                    </select>
                </div>

                <div className={styles.formGroup}>
                    <label htmlFor="date-select" className={styles.label}>Select Date:</label>
                    <input
                        type="date"
                        id="date-select"
                        value={selectedDate}
                        onChange={(e) => setSelectedDate(e.target.value)}
                        required
                        className={styles.input}
                    />
                </div>

                <button type="submit" disabled={isLoading || zonesLoading} className={styles.button}>
                    {isLoading ? 'Getting Summary...' : 'Get Summary'}
                </button>
            </form>

            {errorMsg && <p className={styles.errorMsg}>{errorMsg}</p>}
            {isLoading && <p className={styles.loadingMsg}>Loading Summary...</p>}

            {tripSummary && !isLoading && (
                <div className={styles.resultsBox}>
                    <h4>Results for Zone {tripSummary.zoneId} on {tripSummary.date}:</h4>
                    {/* Ensure field names match your TripSummaryDTO */}
                    <p>Total Pickups: {tripSummary.pickupCount}</p>
                    <p>Total Dropoffs: {tripSummary.dropoffCount}</p>
                </div>
            )}  
        </div>
    )
}

export default ZoneTrips