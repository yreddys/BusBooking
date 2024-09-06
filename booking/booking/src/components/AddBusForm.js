import React, { useState } from 'react';
import axios from 'axios';

const AddBusForm = () => {
  // State variables to hold form input values
  const [busNumber, setBusNumber] = useState('');
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [totalSeats, setTotalSeats] = useState('');

  // Handler for form submission
  const handleAddBus = (e) => {
    e.preventDefault();

    // Create a bus object to send in the request
    const busData = {
      busNumber,
      source,
      destination,
      totalSeats: parseInt(totalSeats),  // Ensure totalSeats is an integer
    };

    // Send POST request to the backend
    axios.post('http://localhost:8080/api/bus/addBus', busData)
      .then(response => {
        alert('Bus added successfully!');
        // Reset the form after success
        setBusNumber('');
        setSource('');
        setDestination('');
        setTotalSeats('');
      })
      .catch(error => {
        console.error('Error adding bus:', error);
        alert('Failed to add the bus. Please try again.');
      });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h3>Add New Bus</h3>
      <form onSubmit={handleAddBus}>
        <div style={{ marginBottom: '10px' }}>
          <label>Bus Number:</label>
          <input
            type="text"
            value={busNumber}
            onChange={(e) => setBusNumber(e.target.value)}
            required
            placeholder="Bus Number"
          />
        </div>
        <div style={{ marginBottom: '10px' }}>
          <label>Source:</label>
          <input
            type="text"
            value={source}
            onChange={(e) => setSource(e.target.value)}
            required
            placeholder="Source (e.g., City A)"
          />
        </div>
        <div style={{ marginBottom: '10px' }}>
          <label>Destination:</label>
          <input
            type="text"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            required
            placeholder="Destination (e.g., City B)"
          />
        </div>
        <div style={{ marginBottom: '10px' }}>
          <label>Total Seats:</label>
          <input
            type="number"
            value={totalSeats}
            onChange={(e) => setTotalSeats(e.target.value)}
            required
            placeholder="Total Seats"
          />
        </div>
        <button type="submit">Add Bus</button>
      </form>
    </div>
  );
};

export default AddBusForm;
