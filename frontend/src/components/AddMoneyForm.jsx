import { useState } from 'react';
import { PlusCircle } from 'lucide-react';
import api from '../api/axiosInstance';
import './AddMoneyForm.css';

export default function AddMoneyForm({ onRefresh }) {
  const [addAmount, setAddAmount] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [cvv, setCvv] = useState('');
  const [expiry, setExpiry] = useState('');
  const [addMsg, setAddMsg] = useState({ text: '', type: '' });
  const [isLoading, setIsLoading] = useState(false);

  const handleAddMoney = async (e) => {
    e.preventDefault();
    setAddMsg({ text: '', type: '' });
    setIsLoading(true);
    try {
      await api.post('/wallets/add-money', {
        amount: parseFloat(addAmount),
        cardNumber,
        cvv,
        expiry
      });
      
      setAddMsg({ text: 'Funds added successfully!', type: 'success' });
      setAddAmount('');
      setCardNumber('');
      setCvv('');
      setExpiry('');
      onRefresh();
    } catch (err) {
      setAddMsg({ 
        text: err.response?.data?.message || 'Error adding funds', 
        type: 'error' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <PlusCircle size={20} color="var(--accent-color)" />
        Add Money
      </h3>
      {addMsg.text && <p className={addMsg.type === 'error' ? 'error-message' : 'success-message'}>{addMsg.text}</p>}
      <form onSubmit={handleAddMoney}>
        <div className="form-group">
          <label>Amount (₹)</label>
          <input type="number" step="0.01" value={addAmount} onChange={e => setAddAmount(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Card Number</label>
          <input type="text" value={cardNumber} onChange={e => setCardNumber(e.target.value)} placeholder="16-digit card number" maxLength={16} required />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>CVV</label>
            <input type="text" value={cvv} onChange={e => setCvv(e.target.value)} maxLength={4} required />
          </div>
          <div className="form-group">
            <label>Expiry (MM/YY)</label>
            <input type="text" value={expiry} onChange={e => setExpiry(e.target.value)} required />
          </div>
        </div>
        <button className="button submit-button" disabled={isLoading}>
          {isLoading ? 'Processing...' : 'Top Up'}
        </button>
      </form>
    </div>
  );
}
