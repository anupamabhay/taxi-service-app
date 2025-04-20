// src/App.js
import React from 'react';

import TopZones from './components/TopZones';
import ZoneTrips from './components/ZoneTrips';
import ListTrips from './components/ListTrips';

function App() {
  return (
    <div>
      <h1 style={{ textAlign: 'center' }}>Taxi Service Data</h1>
      
      <TopZones />
      
      <ZoneTrips />
      
      <ListTrips />
      
    </div>
  );
}

export default App;