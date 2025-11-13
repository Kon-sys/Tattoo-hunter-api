import React, { useState } from 'react';

function App() {
  const [time, setTime] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleClick = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch('http://localhost:8080/api/time');
      if (!response.ok) {
        throw new Error('HTTP error ' + response.status);
      }
      const text = await response.text();
      setTime(text);
    } catch (e) {
      setError(e.message || 'Unknown error');
      setTime('');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div style={{ fontFamily: 'sans-serif', padding: '20px' }}>
        <h1>Current Time Demo</h1>
        <button onClick={handleClick} disabled={loading}>
          {loading ? 'Loading...' : 'Get current time'}
        </button>

        <div style={{ marginTop: '10px', fontWeight: 'bold' }}>
          {error && <span style={{ color: 'red' }}>Error: {error}</span>}
          {!error && time && <span>{time}</span>}
        </div>
      </div>
  );
}

export default App;
