
import { useNavigate } from 'react-router-dom';
import { Zap, ShieldCheck, Activity, ArrowRight } from 'lucide-react';
import logo from './assets/logo_full.png';
import './Home.css';

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="home-container">
      <div className="card home-card">
        <div className="home-title-container" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '1.5rem', marginBottom: '1.5rem' }}>
          <img src={logo} alt="Digipay Logo" className="app-logo" style={{ width: '256px', height: 'auto', borderRadius: '16px' }} />
          <h1 className="home-title" style={{ margin: 0 }}>
            Welcome to Digipay
          </h1>
        </div>
        <p className="home-subtitle">
          The fastest, most secure way to send and receive money online. Built with modern security and real-time risk scoring to keep your funds safe.
        </p>

        <div className="home-features">
          <div className="home-feature-item promo-feature">
            <h3 className="home-feature-title promo-text">
              🎉 Free ₹100 Bonus
            </h3>
            <p className="home-feature-desc promo-text-desc">Create a free account today and get ₹100 credited to your wallet instantly!</p>
          </div>
          <div className="home-feature-item">
            <h3 className="home-feature-title">
              <Zap size={24} style={{ marginRight: '8px', color: 'var(--accent-color)' }} />
              Instant Transfers
            </h3>
            <p className="home-feature-desc">Send money to anyone, anywhere in seconds.</p>
          </div>
          <div className="home-feature-item">
            <h3 className="home-feature-title">
              <ShieldCheck size={24} style={{ marginRight: '8px', color: 'var(--accent-color)' }} />
              Bank-Grade Security
            </h3>
            <p className="home-feature-desc">Full KYC compliance with PAN and Phone verification.</p>
          </div>
          <div className="home-feature-item">
            <h3 className="home-feature-title">
              <Activity size={24} style={{ marginRight: '8px', color: 'var(--accent-color)' }} />
              Smart Risk Scoring
            </h3>
            <p className="home-feature-desc">AI-driven transaction monitoring prevents fraud.</p>
          </div>
        </div>

        <div className="home-action-buttons">
          <button 
            className="button home-login-btn" 
            onClick={() => navigate('/auth', { state: { isLogin: true } })}
          >
            Login
          </button>
          <button 
            className="button home-cta-btn" 
            onClick={() => navigate('/auth', { state: { isLogin: false } })}
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}
          >
            Register and claim ₹100 Bonus
            <ArrowRight size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}

