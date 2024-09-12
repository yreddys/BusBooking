import React, { useState } from 'react';
import axios from 'axios';
import { useParams, useLocation } from 'react-router-dom';

const BookTicket = () => {
  const { busId } = useParams();
  const location = useLocation();
  const [userName, setUserName] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');

  const selectedSeats = location.state?.selectedSeats || [];  // Get selected seats from the previous state

  const bookTicket = () => {
    const seatObjects = selectedSeats.map(seat => ({
      seatRow: seat.rowIndex,
      seat: seat.seatIndex
    }));

    axios.post(`http://localhost:8080/api/booking/bookTicket`, {
      busId,
      userName,
      email,
      age,
      seats: seatObjects
    }).then(response => {
      alert(response.data);  // Show booking confirmation
    }).catch(error => {
      console.error(error);
      alert('Error booking ticket');
    });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Book Your Ticket</h2>
      <input type="text" placeholder="Name" value={userName} onChange={(e) => setUserName(e.target.value)} />
      <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="number" placeholder="Age" value={age} onChange={(e) => setAge(e.target.value)} />
      
      <h4>Selected Seats:</h4>
      {selectedSeats.map((seat, index) => (
        <p key={index}>Row {seat.rowIndex + 1}, Seat {seat.seatIndex + 1}</p>
      ))}

      <button onClick={bookTicket} disabled={!userName || !email || !age || selectedSeats.length === 0}>
        Confirm Booking
      </button>
    </div>
  );
};

export default BookTicket;
