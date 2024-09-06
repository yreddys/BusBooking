import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <header style={{ backgroundColor: '#333', color: '#fff', padding: '10px' }}>
      <nav>
        <Link to="/" style={{ color: 'white', marginRight: '15px' }}>Home</Link>
        <Link to="/get-ticket" style={{ color: 'white', marginRight: '15px' }}>Get Ticket</Link>
        <Link to="/check-buses" style={{ color: 'white', marginRight: '15px' }}>Check Buses</Link>
        <Link to="/admin/add-bus" style={{ color: 'white' }}>Admin</Link> {/* Admin Link */}
      </nav>
    </header>
  );
};

export default Header;
