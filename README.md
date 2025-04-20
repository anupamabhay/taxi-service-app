# Taxi Service App

## Description

This project consists of a Spring Boot backend and a React frontend application designed to load, serve, and display Taxi trip data along with taxi zone information. It provides RESTful API endpoints to query trip summaries, top zones, and detailed trip data with support for filtering, sorting, and pagination.

## Technologies Used

**Backend:**

* Java 17+
* Spring Boot 3.x
* Spring Data JPA (Hibernate)
* Spring Web
* MySQL (Database)
* Lombok
* OpenCSV (for data loading)
* Maven

**Frontend:**

* React.js
* Axios (for API calls)
* CSS Modules (for styling)
* Node.js / npm (or yarn) for development environment

## Core Features

* **Data Loading:** Loads initial zone and trip data from CSV files into the MySQL database on backend startup (`DataLoaderService`).
* **Backend API (`/api` base path):**
    * `GET /zones`: Retrieves a list of all taxi zones.
    * `GET /top-zones`: Retrieves the top 5 zones based on pickup or dropoff counts (`?orderBy=pickups` or `?orderBy=dropoffs`).
    * `GET /zone-trips`: Retrieves a daily summary (pickup/dropoff counts) for a specific zone (`?zoneId=...&date=YYYY-MM-DD`).
    * `GET /list-trips`: Retrieves a paginated, filterable, and sortable list of taxi trips. Supports filtering by pickup/dropoff zone IDs and dates, sorting by pickup/dropoff times, and standard pagination (`?page=`, `?size=`, `?sort=...`).
* **Frontend UI:**
    * Displays Top 5 zones dynamically based on selected criteria (pickups/dropoffs).
    * Provides a form to select a zone and date to view the daily trip summary.
    * Presents a table view of detailed trip records with controls for filtering (by zone/date), sorting (by time), and pagination (next/previous page, items per page).

## Setup & Running

**Prerequisites:**

* Java JDK (17 or later)
* Apache Maven
* MySQL Server
* Node.js and npm (or yarn)

**Backend:**

1.  **Database Setup:**
    * Ensure MySQL server is running.
    * Create the database: `CREATE DATABASE IF NOT EXISTS taxiservice_db;`
    * Manually create the `zone` and `trip` tables within `taxiservice_db` using appropriate SQL `CREATE TABLE` statements matching the `Zone` and `Trip` entities.
2.  **Configuration:** Update database URL, username, and password in `src/main/resources/application.properties` if they differ from the defaults provided. Ensure `spring.jpa.hibernate.ddl-auto` is set appropriately (e.g., `validate` or `none` after tables are created).
3.  **Data Files:** Place `taxi_zone_lookup.csv` and `yellow_tripdata.csv` (or your actual trip data file name) inside the `src/main/resources/data/` directory.
4.  **Run:** Navigate to the project's root directory in a terminal and run: `mvn spring-boot:run` (or run the `TaxiServiceApplication` main method from your IDE). The backend should start, typically on port 8080.

**Frontend:**

1.  **Navigate:** Open a terminal in the frontend project's root directory.
2.  **Install Dependencies:** Run `npm install` (or `yarn install`).
3.  **Configuration:** Verify the `baseURL` in `src/services/api.js` points to your running backend (e.g., `http://localhost:8080/api`).
4.  **Run:** Start the development server: `npm start` (or `yarn start`).
5.  **Access:** Open your browser to the address provided (usually `http://localhost:5173` for Vite or `http://localhost:3000` for CRA).


## Frontend UI

![image](https://github.com/user-attachments/assets/061feba6-27cd-4174-b797-26ada68ba041)

![image](https://github.com/user-attachments/assets/637526ab-e26c-4664-b92c-9b5b3c2e1bd0)

