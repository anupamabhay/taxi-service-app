import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Function to get all zones
export const getZones = () => apiClient.get("/zones");

// Function to get top zones
export const getTopZones = (orderBy = "pickup") =>
  apiClient.get("/top-zones", { params: { orderBy } });

// Function to get trips summary for a specific zone/date
export const getZoneTrips = (zoneId, date) => {
    if(!zoneId || !date) {
        console.error("zoneId and date are required");
        return Promise.reject(new Error("zoneId and date are required"));
    }
    
    return apiClient.get("/zone-trips", {
        params: { zoneId, date },
    });
}

// Function to get paginated/filtered/sorted trips
export const listTrips = (params = {}) => {
    // Destructuring and setting default values for pagination and sorting
    const {
        page = 0,
        size = 10,
        sort = "pickupDateTime,asc",
        pickupLocationId,
        dropoffLocationId,
        pickupDate,
        dropoffDate,
    } = params;

    const queryParams = {page, size, sort};

    // Adding optional parameters to the query
    if (pickupLocationId) queryParams.pickupLocationId = pickupLocationId;
    if (dropoffLocationId) queryParams.dropoffLocationId = dropoffLocationId;
    if (pickupDate) queryParams.pickupDate = pickupDate;
    if (dropoffDate) queryParams.dropoffDate = dropoffDate;

    console.log('Requesting /list-trips with params: ', queryParams);

    return apiClient.get("/list-trips", {
        params: queryParams,
    });
}