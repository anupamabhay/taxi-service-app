import React, { useCallback, useEffect, useState } from "react";
import { getZones, listTrips } from "../services/api";
import styles from "./ListTrips.module.css";

const PaginationControls = ({ pageData, goToPage }) => {
  if (!pageData || pageData.totalPages <= 1) return null;

  return (
    <div style={{ marginTop: "1em" }}>
      <button
        onClick={() => goToPage(pageData.number - 1)}
        disabled={pageData.first}
      >
        &laquo; Previous
      </button>
      <span style={{ margin: "0 1em" }}>
        Page {pageData.number + 1} of {pageData.totalPages} (Total:{" "}
        {pageData.totalElements})
      </span>
      <button
        onClick={() => goToPage(pageData.number + 1)}
        disabled={pageData.last}
      >
        Next &raquo;
      </button>
      <select
        value={pageData.size}
        onChange={(e) => goToPage(0, parseInt(e.target.value))}
        style={{ marginLeft: "1em" }}
      >
        <option value="10">10</option>
        <option value="20">20</option>
        <option value="50">50</option>
      </select>{" "}
      items per page
    </div>
  );
};

const ListTrips = () => {
  const [trips, setTrips] = useState([]);
  const [pageData, setPageData] = useState({
    number: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
    first: true,
    last: true,
  });
  const [filters, setFilters] = useState({
    pickupLocationId: "",
    dropoffLocationId: "",
    pickupDate: "",
    dropoffDate: "",
  });
  const [sort, setSort] = useState("pickupDateTime,asc");
  const [allZones, setAllZones] = useState([]);
  const [zonesLoading, setZonesLoading] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const loadAllZones = async () => {
      setZonesLoading(true);
      setErrorMsg("");
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
    };

    loadAllZones();
  }, []);

  // Fetch trip data (memoized with useCallback)
  const fetchTrips = useCallback(async () => {
    setIsLoading(true);
    setErrorMsg("");
    try {
      const params = {
        ...filters,
        page: pageData.number,
        size: pageData.size,
        sort,
      };

      //remove empty filters
      Object.keys(params).forEach((key) => {
        if (!params[key]) delete params[key];
      });

      const response = await listTrips(params);
      setTrips(response.data.content || []);
      setPageData(response.data); // Contains number, size, totalPages, totalElements, first, last
    } catch (error) {
      setErrorMsg("Failed to load trips");
      console.error("Error loading trips:", error);
      setTrips([]);
      setPageData((prev) => ({
        ...prev,
        content: [],
        number: 0,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
      }));
    } finally {
      setIsLoading(false);
    }
  }, [pageData.number, pageData.size, filters, sort]);

  useEffect(() => {
    fetchTrips();
  }, [fetchTrips]); // [fetchTrips] = all dependencies included via useCallback

  // Event Handlers
  const handleFilterChange = (event) => {
    const { name, value } = event.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
    setPageData((prev) => ({ ...prev, number: 0 })); // Reset to fpage 0 on filter change
  };

  const handleSortChange = (event) => {
    setSort(event.target.value);
    setPageData((prev) => ({ ...prev, number: 0 })); // Reset to page 0 on sort change
  };

  const goToPage = (pageNumber, newSize = pageData.size) => {
    const requestedPage = newSize !== pageData.size ? 0 : pageNumber; // Rest to 0 if size changes

    if (
      requestedPage >= 0 &&
      (pageData.totalPages === 0 || requestedPage < pageData.totalPages)
    ) {
      setPageData((prev) => ({
        ...prev,
        number: requestedPage,
        size: newSize,
      }));
    }
  };

  return (
    <div className={styles.container}>
      <h3 className={styles.title}>Taxi Trip List</h3>

      <div className={styles.controlsContainer}>
        <div className={styles.filterBox}>
          <h4>Filters</h4>
          <div className={styles.filterGroup}>
            <label className={styles.label} htmlFor="pickupLocationId">
              Pickup Zone:
            </label>
            <select
              className={styles.select}
              id="pickupLocationId"
              name="pickupLocationId"
              value={filters.pickupLocationId}
              onChange={handleFilterChange}
              disabled={zonesLoading}
            >
              <option value="">{zonesLoading ? "Loading..." : "Any"}</option>
              {allZones.map((zone) => (
                <option key={`pu-${zone.locationID}`} value={zone.locationID}>
                  {zone.zoneName}
                </option>
              ))}
            </select>
          </div>
          <div className={styles.filterGroup}>
            <label className={styles.label} htmlFor="dropoffLocationId">
              Dropoff Zone:
            </label>
            <select
              className={styles.select}
              id="dropoffLocationId"
              name="dropoffLocationId"
              value={filters.dropoffLocationId}
              onChange={handleFilterChange}
              disabled={zonesLoading}
            >
              <option value="">{zonesLoading ? "Loading..." : "Any"}</option>
              {allZones.map((zone) => (
                <option key={`do-${zone.locationID}`} value={zone.locationID}>
                  {zone.zoneName}
                </option>
              ))}
            </select>
          </div>
          <div className={styles.filterGroup}>
            <label className={styles.label} htmlFor="pickupDate">
              Pickup Date:
            </label>
            <input
              className={styles.input}
              id="pickupDate"
              type="date"
              name="pickupDate"
              value={filters.pickupDate}
              onChange={handleFilterChange}
            />
          </div>
          <div className={styles.filterGroup}>
            <label className={styles.label} htmlFor="dropoffDate">
              Dropoff Date:
            </label>
            <input
              className={styles.input}
              id="dropoffDate"
              type="date"
              name="dropoffDate"
              value={filters.dropoffDate}
              onChange={handleFilterChange}
            />
          </div>
        </div>

        <div className={styles.sortBox}>
          <h4>Sorting</h4>
          <label htmlFor="sort-select">Sort By: </label>
          <select
            id="sort-select"
            value={sort}
            onChange={handleSortChange}
            className={styles.select}
          >
            <option value="pickupDateTime,asc">Pickup Time (Asc)</option>
            <option value="pickupDateTime,desc">Pickup Time (Desc)</option>
            <option value="dropoffDateTime,asc">Dropoff Time (Asc)</option>
            <option value="dropoffDateTime,desc">Dropoff Time (Desc)</option>
          </select>
        </div>
      </div>

      {isLoading && <p className={styles.loadingMsg}>Loading trips...</p>}
      {errorMsg && <p className={styles.errorMsg}>{errorMsg}</p>}

      {/* Results Table */}
      {!isLoading && !errorMsg && (
        <>
          <table className={styles.dataTable}>
            <thead>
              <tr>
                <th>Trip ID</th>
                <th>Pickup Time</th>
                <th>Dropoff Time</th>
                <th>Pickup Zone ID</th>
                <th>Dropoff Zone ID</th>
              </tr>
            </thead>
            <tbody>
              {trips.length > 0 ? (
                trips.map((trip) => (
                  <tr key={trip.id}>
                    <td>{trip.id}</td>
                    <td>{trip.pickupDateTime}</td>
                    <td>{trip.dropoffDateTime}</td>
                    <td>{trip.pickupLocationID}</td>
                    <td>{trip.dropoffLocationID}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" className={styles.noData}>
                    No trips found matching criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          <div className={styles.pagination}>
            <PaginationControls pageData={pageData} goToPage={goToPage} />
          </div>
        </>
      )}
    </div>
  );
};

export default ListTrips;
