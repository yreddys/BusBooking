import React, { useState } from 'react';
import axios from 'axios';
import { useParams, useLocation, useNavigate } from 'react-router-dom';

const BookTicket = () => {
  const { busId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const [userName, setUserName] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');

  const selectedSeats = location.state?.selectedSeats || [];  // Get selected seats from the previous state

  const bookTicket = () => {
    const seatObjects = selectedSeats.map(seat => ({
      seatRow: seat.rowIndex,
      seat: seat.seatIndex
    }));

    // Step 1: Call backend to create booking and generate Razorpay order
    axios.post(`http://localhost:8080/api/booking/bookTicket`, {
      busId,
      userName,
      email,
      age,
      seats: seatObjects
    }).then(response => {
      const { orderId, amount, confirmationCode } = response.data;  // Get order ID, amount, and confirmation code

      // Step 2: Open Razorpay payment gateway
      const options = {
        key: 'rzp_test_YH1ns2YOwVGPtQ', // Replace with your Razorpay key
        amount: amount * 100,  // Razorpay accepts amount in paise
        currency: 'INR',
        name: 'Bus Ticket Booking',
        description: 'Complete your payment',
        order_id: orderId, // Order ID from Razorpay
        handler: function (response) {
          // Step 3: Once payment is successful, call backend to complete booking
          completeBooking(response.razorpay_payment_id, confirmationCode);  // Pass confirmationCode correctly here
        },
        prefill: {
          name: userName,
          email: email,
        },
        theme: {
          color: '#3399cc'
        }
      };
      const rzp = new window.Razorpay(options);
      rzp.open();
    }).catch(error => {
      console.error(error);
      alert('Error booking ticket');
    });
  };

  // Step 4: Complete the booking by confirming payment with backend
  const completeBooking = (paymentId, confirmationCode) => {
    axios.post(`http://localhost:8080/api/booking/completeBooking`, {
      paymentId,
      confirmationCode  // Ensure confirmationCode is passed here
    }).then(response => {
      alert('Booking successful! Your confirmation code is: ' + response.data);
      navigate("/");  // Redirect to home after successful booking
    }).catch(error => {
      console.error('Error completing booking: ', error);
      alert('Error completing booking');
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
