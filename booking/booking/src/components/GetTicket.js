import React, { useState } from 'react';
import axios from 'axios';
import CancelBooking from './CancelBooking'; // Import the new component

const GetTicket = () => {
  const [confirmationCode, setConfirmationCode] = useState('');
  const [ticketDetails, setTicketDetails] = useState(null);

  const getTicket = () => {
    axios.get(`http://localhost:8080/api/booking/getTicket/${confirmationCode}`)
      .then(response => {
        setTicketDetails(response.data);
      })
      .catch(error => {
        console.log(error);
        alert('Ticket not found');
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

      {ticketDetails && (
        <div>
          <h3>Ticket Details</h3>
          <p>Name: {ticketDetails.userName}</p>
          <p>Email: {ticketDetails.email}</p>
          <p>Age: {ticketDetails.age}</p>
          <p>Bus Number: {ticketDetails.bus.busNumber}</p>
          <p>Seats: {ticketDetails.seats.map(seat => `Row ${seat.seatRow + 1}, Seat ${seat.seat + 1}`).join(', ')}</p>
        </div>
      )}

      {/* Use the CancelBooking component here */}
      <CancelBooking />
    </div>
  );
};

export default GetTicket;
