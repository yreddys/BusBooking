import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './components/Home';
import BookTicket from './components/BookTicket';
import GetTicket from './components/GetTicket';
import CheckBuses from './components/CheckBuses';
import AddBusForm from './components/AddBusForm';
import BusLayout from './components/BusLayout';
import CancelBooking from './components/CancelBooking';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/get-ticket" element={<GetTicket />} />
        <Route path="/check-buses" element={<CheckBuses />} />
        <Route path="/admin/add-bus" element={<AddBusForm />} />
        <Route path="/book/:busId" element={<BookTicket />} />
        <Route path="/bus/:busId" element={<BusLayout />} />
        <Route path="/CancelBooking" element={<CancelBooking />} />
      </Routes>
    </Router>
  );
}

export default App;
