import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const BusLayout = () => {
  const { busId } = useParams(); // Get busId from URL params
  const [seatLayout, setSeatLayout] = useState([]); // Holds the seat layout fetched from the backend
  const [selectedSeats, setSelectedSeats] = useState([]); // Holds the selected seats
  const navigate = useNavigate(); // To navigate to the booking page

  // Fetch seat layout data from the backend when the component is mounted
  useEffect(() => {
    axios.get(`http://localhost:8080/api/bus/${busId}`)
      .then(response => {
        setSeatLayout(response.data.seatLayout); // Set seat layout from backend
      })
      .catch(error => {
        console.error('Error fetching bus layout:', error); // Debugging point
      });
  }, [busId]);

  // Handle the seat click
  const handleSeatClick = (rowIndex, seatIndex) => {
    const seatStatus = seatLayout[rowIndex][seatIndex]; // Get seat status
    if (seatStatus === 0) { // 0 means seat is available
      const alreadySelected = selectedSeats.some(seat => seat.seatRow === rowIndex && seat.seat === seatIndex);
      if (!alreadySelected) {
        const newSelectedSeats = [...selectedSeats, { seatRow: rowIndex, seat: seatIndex }]; // Zero-based indices
        setSelectedSeats(newSelectedSeats); // Add to selectedSeats state
      }
    } else {
      alert('Seat not available');
    }
  };

  const proceedToBookTicket = () => {
    if (selectedSeats.length > 0) {
      // Pass selectedSeats (zero-based indices) to the booking page
      navigate(`/book/${busId}`, { state: { seats: selectedSeats } });
    } else {
      alert('Please select at least one seat.');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h3>Select Your Seats</h3>
      {/* Render the seat layout */}
      <div style={{ display: 'grid', gridTemplateColumns: `repeat(${seatLayout[0]?.length || 0}, 1fr)` }}>
        {seatLayout.map((row, rowIndex) => (
          row.map((seat, seatIndex) => (
            <div
              key={`${rowIndex}-${seatIndex}`}
              style={{
                width: '40px',
                height: '40px',
                backgroundColor: seat === 0 ? 'green' : seat === 1 ? 'red' : 'grey',
                margin: '5px',
                cursor: seat === 0 ? 'pointer' : 'not-allowed'
              }}
              onClick={() => handleSeatClick(rowIndex, seatIndex)} // Handle seat selection
            >
              {/* Display seat index (one-based for user display) */}
              {seatIndex + 1} {/* One-based display */}
            </div>
          ))
        ))}
      </div>

      {/* Display selected seats (convert zero-based to one-based for display) */}
      <div>
        <h4>Selected Seats:</h4>
        {selectedSeats.length > 0 ? (
          selectedSeats.map((seat, index) => (
            <p key={index}>
              Row {seat.seatRow + 1}, Seat {seat.seat + 1}  {/* Convert zero-based index to one-based */}
            </p>
          ))
        ) : (
          <p>No seats selected</p>
        )}
      </div>

      {/* Proceed to booking button */}
      <button onClick={proceedToBookTicket} style={{ marginTop: '20px' }}>
        Proceed to Book Ticket
      </button>
    </div>
  );
};

export default BusLayout;
