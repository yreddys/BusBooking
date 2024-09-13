import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div style={{ textAlign: 'center', padding: '50px' }}>
      <h1>Welcome to the Bus Booking System</h1>
      <div>
        <Link to="/get-ticket" style={{ marginRight: '15px' }}>Get Ticket</Link>
        <Link to="/check-buses" style={{ marginRight: '15px' }}>Check Available Buses</Link>
        <Link to="/CancelBooking" style={{ marginRight: '15px' }}>CancelBooking</Link>
        <Link to="/admin/add-bus">Admin (Add Bus)</Link>
      </div>
    </div>
  );
};

export default Home;
