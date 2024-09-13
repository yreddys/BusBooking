import React, { useState } from 'react';
import axios from 'axios';

const CancelBooking = () => {
  const [cancelConfirmationCode, setCancelConfirmationCode] = useState('');
  const [cancelStatus, setCancelStatus] = useState('');

  const cancelBooking = () => {
    axios.post(`http://localhost:8080/api/booking/cancel/${cancelConfirmationCode}`)
      .then(response => {
        setCancelStatus(response.data);
      })
      .catch(error => {
        console.log(error);
        setCancelStatus('Cancellation failed: ' + error.message);
      });
  };

  return (
    <div>
      <h2>Cancel Your Booking</h2>
      <input
        type="text"
        placeholder="Enter Confirmation Code"
        value={cancelConfirmationCode}
        onChange={(e) => setCancelConfirmationCode(e.target.value)}
      />
      <button onClick={cancelBooking}>Cancel Booking</button>

      {cancelStatus && (
        <div>
          <p>{cancelStatus}</p>
        </div>
      )}
    </div>
  );
};

export default CancelBooking;
