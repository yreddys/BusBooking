import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const BusLayout = () => {
  const { busId } = useParams();  // Get the busId from the route
  const [seatLayout, setSeatLayout] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const navigate = useNavigate();  // Navigation hook for routing to the next page

  useEffect(() => {
    // Fetch the seat layout for the selected bus from the backend
    axios.get(`http://localhost:8080/api/bus/${busId}`)
      .then(response => {
        setSeatLayout(response.data.seatLayout);
      })
      .catch(error => {
        console.log(error);
      });
  }, [busId]);

  // Handle seat selection
  const handleSeatClick = (rowIndex, seatIndex) => {
    const seatStatus = seatLayout[rowIndex][seatIndex];
    if (seatStatus === 0) {
      const isAlreadySelected = selectedSeats.some(seat => seat.rowIndex === rowIndex && seat.seatIndex === seatIndex);
      if (isAlreadySelected) {
        setSelectedSeats(selectedSeats.filter(seat => !(seat.rowIndex === rowIndex && seat.seatIndex === seatIndex)));
      } else {
        setSelectedSeats([...selectedSeats, { rowIndex, seatIndex }]);
      }
    } else {
      alert('Seat not available');
    }
  };

  // Proceed to the booking page
  const handleBooking = () => {
    navigate(`/book/${busId}`, { state: { selectedSeats } });
  };

  return (
    <div style={{ padding: '20px' }}>
      <h3>Select Your Seats</h3>
      <div style={{ display: 'grid', gridTemplateColumns: `repeat(${seatLayout[0]?.length || 0}, 1fr)` }}>
        {seatLayout.map((row, rowIndex) => (
          row.map((seat, seatIndex) => (
            <div
              key={`${rowIndex}-${seatIndex}`}
              style={{
                width: '40px',
                height: '40px',
                backgroundColor: seat === 0 ? 'green' : 'red',
                margin: '5px',
                cursor: seat === 0 ? 'pointer' : 'not-allowed'
              }}
              onClick={() => handleSeatClick(rowIndex, seatIndex)}
            >
              {seatIndex + 1}
            </div>
          ))
        ))}
      </div>
      <button onClick={handleBooking} disabled={selectedSeats.length === 0}>Book Selected Seats</button>
    </div>
  );
};

export default BusLayout;
