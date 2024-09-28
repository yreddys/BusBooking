import React, { useState, useMemo } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import axios from 'axios';

const BookTicket = () => {
  const { busId } = useParams();
  const location = useLocation();

  // Use useMemo to get selected seats from location state
  const selectedSeats = useMemo(() => location.state?.selectedSeats || [], [location.state]);

  // Initialize state for booking details and confirmation codes
  const [bookingDetails, setBookingDetails] = useState(
    selectedSeats.map(() => ({ userName: '', email: '', age: '' }))
  );
  const [loading, setLoading] = useState(false); // Loading state for submitting form
  const [confirmationCodes, setConfirmationCodes] = useState([]); // Store confirmation codes
  const [bookingConfirmed, setBookingConfirmed] = useState(false); // Track booking confirmation

  // Handle input changes for user details
  const handleInputChange = (index, field, value) => {
    const updatedBookingDetails = [...bookingDetails];
    updatedBookingDetails[index][field] = value;
    setBookingDetails(updatedBookingDetails);
  };

  // Function to submit booking request
  const bookTicket = () => {
    setLoading(true); // Set loading state to true during the booking process

    // Prepare seat objects with user details
    const seatObjects = selectedSeats.map((seat, index) => ({
      seatRow: seat.rowIndex,
      seat: seat.seatIndex,
      userName: bookingDetails[index]?.userName || '',
      email: bookingDetails[index]?.email || '',
      age: bookingDetails[index]?.age || 0,
    }));

    // Axios POST request to initiate booking
    axios.post(`http://localhost:8080/api/booking/bookTicket`, {
      busId,
      seats: seatObjects,
    })
      .then(response => {
        console.log('Booking response:', response); // Log the response for debugging

        const { orderId, amount, confirmationCodes: generatedCodes } = response.data;

        // Ensure that the confirmation codes are valid
        if (generatedCodes && generatedCodes.length > 0) {
          // Store all confirmation codes in state to display after booking
          setConfirmationCodes(generatedCodes);

          // Initialize Razorpay payment
          const options = {
            key: 'rzp_test_YH1ns2YOwVGPtQ',  // Replace with your Razorpay API Key
            amount: amount * 100, // Convert to paisa
            currency: 'INR',
            name: 'Bus Ticket Booking',
            description: 'Complete your payment',
            order_id: orderId,
            handler: function (paymentResponse) {
              console.log('Razorpay Payment Success:', paymentResponse);
              completeBooking(paymentResponse.razorpay_payment_id, generatedCodes);
            },
            prefill: {
              name: bookingDetails[0]?.userName || '',
              email: bookingDetails[0]?.email || '',
            },
            theme: {
              color: '#3399cc',
            },
          };
          const rzp = new window.Razorpay(options);
          rzp.open();
        } else {
          alert('Error: Confirmation codes are missing from the backend response.');
        }
      })
      .catch(error => {
        console.error('Error in booking:', error);
        alert('Error booking ticket. Please try again.');
      })
      .finally(() => {
        setLoading(false); // Set loading state to false after booking attempt
      });
  };

  // Function to complete the booking process after successful payment
  const completeBooking = (paymentId, codes) => {
    if (!codes || codes.length === 0) {
      alert('Error: Confirmation codes are missing.');
      return;
    }

    // Send payment ID and confirmation code to complete the booking
    axios.post('http://localhost:8080/api/booking/completeBooking', {
      paymentId,
      confirmationCode: codes[0], // Assuming only one confirmation code for now
    })
      .then(response => {
        console.log('Booking completion response:', response);
        setBookingConfirmed(true); // Mark the booking as confirmed
        alert('Booking successful! Your ticket has been confirmed.');
      })
      .catch(error => {
        console.error('Error completing booking:', error);
        alert('Error completing booking. Please try again.');
      });
  };

  return (
    <div>
      <h2>Book Your Ticket</h2>
      <div>
        {selectedSeats.map((seat, index) => (
          <div key={index}>
            <h3>Seat {seat.rowIndex + 1}, {seat.seatIndex + 1}</h3>
            <label>
              Name:
              <input
                type="text"
                value={bookingDetails[index]?.userName || ''}
                onChange={(e) => handleInputChange(index, 'userName', e.target.value)}
              />
            </label>
            <label>
              Email:
              <input
                type="email"
                value={bookingDetails[index]?.email || ''}
                onChange={(e) => handleInputChange(index, 'email', e.target.value)}
              />
            </label>
            <label>
              Age:
              <input
                type="number"
                value={bookingDetails[index]?.age || ''}
                onChange={(e) => handleInputChange(index, 'age', e.target.value)}
              />
            </label>
          </div>
        ))}
      </div>

      {/* Display Confirmation Codes if Available */}
      {confirmationCodes.length > 0 && (
        <div>
          <h3>Booking Confirmed!</h3>
          <p>Your Confirmation Codes:</p>
          <ul>
            {confirmationCodes.map((code, index) => (
              <li key={index}>Confirmation Code: {code}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Conditionally hide the "Confirm Booking" button after confirmation */}
      {!bookingConfirmed && (
        <button onClick={bookTicket} disabled={loading}>
          {loading ? 'Booking...' : 'Confirm Booking'}
        </button>
      )}
    </div>
  );
};

export default BookTicket;
