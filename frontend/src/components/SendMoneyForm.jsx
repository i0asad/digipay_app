import { useState } from 'react';
import { Send } from 'lucide-react';
import api from '../api/axiosInstance';
import './SendMoneyForm.css';

export default function SendMoneyForm({ onRefresh }) {
  const [targetWalletId, setTargetWalletId] = useState('');
  const [sendAmount, setSendAmount] = useState('');
  const [sendMsg, setSendMsg] = useState({ text: '', type: '' });
  const [isLoading, setIsLoading] = useState(false);

  const handleSendMoney = async (e) => {
    e.preventDefault();
    setSendMsg({ text: '', type: '' });
    setIsLoading(true);
    try {
      await api.post('/payments', {
        targetWalletId: parseInt(targetWalletId),
        amount: parseFloat(sendAmount)
      });
      
      setSendMsg({ text: 'Payment successful!', type: 'success' });
      setTargetWalletId('');
      setSendAmount('');
      onRefresh();
    } catch (err) {
      setSendMsg({ 
        text: err.response?.data?.message || 'Payment failed', 
        type: 'error' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <Send size={20} color="var(--accent-color)" />
        Send Money
      </h3>
      {sendMsg.text && <p className={sendMsg.type === 'error' ? 'error-message' : 'success-message'}>{sendMsg.text}</p>}
      <form onSubmit={handleSendMoney}>
        <div className="form-group">
          <label>Recipient Wallet ID</label>
          <input type="number" value={targetWalletId} onChange={e => setTargetWalletId(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Amount (₹)</label>
          <input type="number" step="0.01" value={sendAmount} onChange={e => setSendAmount(e.target.value)} required />
        </div>
        <button className="button submit-button" disabled={isLoading}>
          {isLoading ? 'Processing...' : 'Send Payment'}
        </button>
      </form>
    </div>
  );
}
