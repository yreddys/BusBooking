import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const BusLayout = () => {
  const { busId } = useParams();
  const [seatLayout, setSeatLayout] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const navigate = useNavigate(); // Hook to navigate programmatically

  useEffect(() => {
    // Fetch bus layout from the backend
    axios.get(`http://localhost:8080/api/bus/${busId}`)
      .then(response => {
        setSeatLayout(response.data.seatLayout);
      })
      .catch(error => {
        console.log(error);
      });
  }, [busId]);

  const handleSeatClick = (rowIndex, seatIndex) => {
    const seatStatus = seatLayout[rowIndex][seatIndex];
    if (seatStatus === 0) {
      // Send zero-based indices to the backend (rowIndex and seatIndex start from 0)
      setSelectedSeats([...selectedSeats, { row: rowIndex, seat: seatIndex }]);
    } else {
      alert('Seat not available');
    }
  };

  const proceedToBookTicket = () => {
    // Navigate to BookTicket with busId and selectedSeats as state
    if (selectedSeats.length > 0) {
      navigate(`/book/${busId}`, { state: { seats: selectedSeats } });
    } else {
      alert('Please select at least one seat before proceeding.');
    }
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
                backgroundColor: seat === 0 ? 'green' : seat === 1 ? 'red' : 'grey',
                margin: '5px',
                cursor: seat === 0 ? 'pointer' : 'not-allowed'
              }}
              onClick={() => handleSeatClick(rowIndex, seatIndex)}  // Zero-based rowIndex and seatIndex
            >
              {seatIndex + 1}  {/* Display one-based index for the user */}
            </div>
          ))
        ))}
      </div>
      <button onClick={proceedToBookTicket} style={{ marginTop: '20px' }}>
        Proceed to Book Ticket
      </button>
    </div>
  );
};

export default BusLayout;
