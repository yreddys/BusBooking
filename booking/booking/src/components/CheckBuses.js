import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';  // Use useNavigate for programmatic navigation

const CheckBuses = () => {
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [availableBuses, setAvailableBuses] = useState([]);
  const navigate = useNavigate();  // Initialize useNavigate hook

  const checkAvailableBuses = () => {
    // Update the request URL to include the full base URL (localhost:8080)
    axios.get(`http://localhost:8080/api/bus/available`, { params: { source, destination } })
      .then(response => {
        setAvailableBuses(response.data);
      })
      .catch(error => {
        console.log(error);
        alert('No buses available');
      });
  };

  const handleBookNow = (busId) => {
    // Navigate to the BusLayout page for the selected bus
    navigate(`/bus/${busId}`);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Check Available Buses</h2>
      <input type="text" placeholder="From" value={source} onChange={(e) => setSource(e.target.value)} />
      <input type="text" placeholder="To" value={destination} onChange={(e) => setDestination(e.target.value)} />
      <button onClick={checkAvailableBuses}>Check Buses</button>

      {availableBuses.length > 0 && (
        <div>
          <h3>Available Buses</h3>
          {availableBuses.map(bus => (
            <div key={bus.id}>
              <p>{bus.source} to {bus.destination}</p>
              <p>Bus Number: {bus.busNumber}</p>
              <p>Available Seats: {bus.totalSeats}</p>
              <button onClick={() => handleBookNow(bus.id)}>Book Now</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CheckBuses;
