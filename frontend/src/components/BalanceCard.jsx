import { Wallet } from 'lucide-react';

export default function BalanceCard({ wallet }) {
  return (
    <div className="card dashboard-card-balance">
      <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <Wallet size={20} color="var(--accent-color)" />
        Your Balance
      </h3>
      {wallet ? (
        <>
          <div className="dashboard-balance-amount">
            ₹{Number(wallet.balance ?? 0).toFixed(2)}
          </div>
          <p>Wallet ID: <strong>{wallet.walletId}</strong></p>
        </>
      ) : (
        <p>Loading balance...</p>
      )}
    </div>
  );
}
