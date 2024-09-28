import React, { useState } from 'react';
import axios from 'axios';

const GetTicket = () => {
  const [confirmationCode, setConfirmationCode] = useState(''); // Confirmation code input
  const [ticketDetails, setTicketDetails] = useState(null); // Store ticket details
  const [error, setError] = useState(null); // To handle errors

  // Fetch ticket details by confirmation code
  const getTicket = () => {
    // Clear the previous ticket details and errors before making a new request
    setTicketDetails(null);
    setError(null);

    console.log("Fetching ticket for confirmation code:", confirmationCode); // Log the confirmation code
    axios.get(`http://localhost:8080/api/booking/getTicket/${confirmationCode}`)
      .then(response => {
        console.log("Ticket details fetched:", response.data); // Log the ticket details
        setTicketDetails(response.data);
      })
      .catch(error => {
        console.log(error);
        setError('Ticket not found');
      });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Retrieve Your Ticket</h2>
      <input
        type="text"
        placeholder="Confirmation Code"
        value={confirmationCode}
        onChange={(e) => setConfirmationCode(e.target.value)}
      />
      <button onClick={getTicket}>Get Ticket</button>

      {/* Display error message if there's an error */}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {/* Check if ticket details exist and render the information */}
      {ticketDetails && (
        <div>
          <h3>Bus Number: {ticketDetails.bus?.busNumber || 'N/A'}</h3> {/* Display bus number */}
          
          {/* Loop through each seat and display the individual ticket and user details */}
          {ticketDetails.seats?.map((seat, index) => (
            <div key={index} style={{ marginBottom: '20px' }}>
              <h4>Ticket {index + 1}</h4> {/* Label each ticket separately */}
              <p>Name: {seat.userName || 'N/A'}</p> {/* Display name for each seat */}
              <p>Email: {seat.email || 'N/A'}</p> {/* Display email for each seat */}
              <p>Age: {seat.age || 'N/A'}</p> {/* Display age for each seat */}
              <p>Seat: Row {seat.seatRow + 1}, Seat {seat.seat + 1}</p> {/* Display seat row and number */}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GetTicket;
