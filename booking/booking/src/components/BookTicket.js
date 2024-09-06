import React, { useState } from 'react';
import axios from 'axios';
import { useParams, useLocation } from 'react-router-dom';

const BookTicket = () => {
  const { busId } = useParams();
  const location = useLocation();  // Hook to get passed state
  const [userName, setUserName] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');
  const [seats, setSeats] = useState(location.state?.seats || []);  // Seats passed from BusLayout

  const bookTicket = () => {
    axios.post(`http://localhost:8080/api/booking/bookTicket`, {
      busId,
      userName,
      email,
      age,
      seats
    }).then(response => {
      alert(response.data);
    }).catch(error => {
      console.log(error);
      alert('Error booking ticket');
    });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Book Your Ticket</h2>
      <p>Selected Seats: {seats.map(seat => `Row ${seat.row + 1}, Seat ${seat.seat + 1}`).join(', ')}</p>
      <input type="text" placeholder="Name" value={userName} onChange={(e) => setUserName(e.target.value)} />
      <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="number" placeholder="Age" value={age} onChange={(e) => setAge(e.target.value)} />
      <button onClick={bookTicket}>Confirm Booking</button>
    </div>
  );
};

export default BookTicket;
