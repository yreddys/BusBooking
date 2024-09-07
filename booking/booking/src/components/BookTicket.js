import React, { useState, useEffect } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import axios from 'axios';

const BookTicket = () => {
  const { busId } = useParams(); // Get busId from URL params
  const location = useLocation(); // Get state passed from navigate
  const [userName, setUserName] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');
  
  // Access seats passed via navigation
  const { seats } = location.state || { seats: [] }; // Default to empty array if no seats are passed

  useEffect(() => {
    console.log('Seats received for booking (zero-based):', seats); // Debugging point
  }, [seats]);

  const bookTicket = () => {
    // Send booking request to the backend
    axios.post(`http://localhost:8080/api/booking/bookTicket`, {
      busId,
      userName,
      email,
      age,
      seats // Pass zero-based indices to the backend
    })
    .then(response => {
      alert(`Booking successful. Your confirmation code is: ${response.data}`);
    })
    .catch(error => {
      console.error('Error booking ticket:', error);
      alert('Error booking ticket');
    });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Book Your Ticket</h2>
      <input type="text" placeholder="Name" value={userName} onChange={(e) => setUserName(e.target.value)} />
      <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="number" placeholder="Age" value={age} onChange={(e) => setAge(e.target.value)} />

      <h4>Selected Seats</h4>
      {/* Render the selected seats (convert zero-based to one-based for display) */}
      {seats.length > 0 ? (
        seats.map((seat, index) => (
          <p key={index}>
            Row {seat.seatRow + 1}, Seat {seat.seat + 1} {/* Convert zero-based index to one-based for display */}
          </p>
        ))
      ) : (
        <p>No seats selected</p>
      )}

      <button onClick={bookTicket}>Confirm Booking</button>
    </div>
  );
};

export default BookTicket;
