import { useState, useEffect, useCallback } from 'react';
import { LogOut } from 'lucide-react';
import api from './api/axiosInstance';
import logo from './assets/logo.png';
import './Dashboard.css';
import BalanceCard from './components/BalanceCard';
import TransactionHistory from './components/TransactionHistory';
import AddMoneyForm from './components/AddMoneyForm';
import SendMoneyForm from './components/SendMoneyForm';

export default function Dashboard({ onLogout }) {
  const [wallet, setWallet] = useState(null);
  const [history, setHistory] = useState([]);

  const fetchWallet = useCallback(async () => {
    try {
      const res = await api.get('/wallets');
      setWallet(res.data);
    } catch (e) {
      console.error('Error fetching wallet:', e);
    }
  }, []);

  const fetchHistory = useCallback(async () => {
    try {
      const res = await api.get('/payments/history');
      setHistory(res.data);
    } catch (e) {
      console.error('Error fetching history:', e);
    }
  }, []);

  const refreshData = useCallback(() => {
    fetchWallet();
    fetchHistory();
  }, [fetchWallet, fetchHistory]);

  useEffect(() => {
    let timeoutId;
    let isMounted = true;

    const pollData = async () => {
      if (!isMounted) return;
      await Promise.all([fetchWallet(), fetchHistory()]);
      
      if (isMounted) {
        timeoutId = setTimeout(pollData, 5000);
      }
    };

    pollData();

    return () => {
      isMounted = false;
      clearTimeout(timeoutId);
    };
  }, [fetchWallet, fetchHistory]);

  return (
    <div className="dashboard-page">
      <div className="dashboard-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <img src={logo} alt="Digipay Logo" style={{ width: '40px', height: '40px', borderRadius: '10px' }} />
          <h1 className="dashboard-title">Digipay</h1>
        </div>
        <button className="logout-button" onClick={onLogout} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}>
          <LogOut size={18} />
          Sign Out
        </button>
      </div>

      <div className="dashboard-widgets">
        <BalanceCard wallet={wallet} />
        <TransactionHistory history={history} wallet={wallet} />
      </div>

      <div className="dashboard-forms">
        <AddMoneyForm onRefresh={refreshData} />
        <SendMoneyForm onRefresh={refreshData} />
      </div>
    </div>
  );
}

