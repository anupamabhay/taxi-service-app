import React, { useEffect, useState } from 'react'
import { getTopZones } from '../services/api';
import styles from './TopZones.module.css';

const TopZones = () => {
    const [orderBy, setOrderBy] = useState("pickup");
    const [zones, setZones] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
  
    useEffect(() => {
        const loadTopZones = async () => {
            setIsLoading(true);
            setErrorMsg('');
            try {
                const response = await getTopZones(orderBy);
                setZones(response.data || []);
            } catch (error) {
                setErrorMsg("Failed to load top zones");
                console.error("Error loading top zones:", error);
                setZones([]);
            } finally {
                setIsLoading(false);
            }
        }

        loadTopZones();
    }, [orderBy]);


    return (
        <div className={styles.container}>
            <h3 className={styles.title}>Top 5 Zones</h3>

            <div className={styles.radioGroup}>
                <label>
                    <input
                        type="radio"
                        value="pickup"
                        checked={orderBy === 'pickup'}
                        onChange={(e) => setOrderBy(e.target.value)} /> Pickups
                </label>
                <label style={{ marginLeft: '1em' }}>
                    <input
                        type="radio"
                        value="dropoff"
                        checked={orderBy === 'dropoff'}
                        onChange={(e) => setOrderBy(e.target.value)} /> Dropoffs
                </label>
            </div>

            {isLoading && <p className={styles.loadingMsg}>Loading...</p>}
            {errorMsg && <p className={styles.errorMsg}>{errorMsg}</p>}

            {!isLoading && !errorMsg && (
                <table  className={styles.dataTable}>
                <thead>
                    <tr><th>Rank</th><th>Zone Name</th><th>Trip Count</th></tr>
                </thead>
                <tbody>
                    {zones.length > 0 ? (
                    zones.map((zone, index) => (
                        <tr key={zone.zoneName || index}> {/* Add index for safety if zoneName might not be unique */}
                        <td>{index + 1}</td>
                        <td>{zone.zoneName}</td>
                        <td>{zone.count}</td>
                        </tr>
                    ))
                    ) : (
                    <tr><td colSpan="3" className={styles.noData}>No data found.</td></tr>
                    )}
                </tbody>
                </table>
            )}
        </div>
    )
}

export default TopZones;