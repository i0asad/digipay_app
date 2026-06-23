import { History, ArrowUpRight, ArrowDownLeft } from 'lucide-react';

export default function TransactionHistory({ history, wallet }) {
  return (
    <div className="card dashboard-history-card">
      <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <History size={20} color="var(--accent-color)" />
        Recent Transactions
      </h3>
      {history.length > 0 ? (
        <div className="dashboard-history-list">
          {history.map(tx => {
            const isDebit = wallet && tx.sourceWalletId === wallet.walletId;
            const sign = isDebit ? '-' : '+';
            const color = isDebit ? 'var(--text-secondary)' : 'var(--success-color)';
            const statusColor = tx.status === 'SUCCESS' ? 'var(--success-color)' : 'var(--danger-color)';

            return (
              <div key={tx.id} className="dashboard-history-item">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  <div style={{
                    width: '32px', height: '32px', borderRadius: '50%',
                    background: isDebit ? 'rgba(239, 68, 68, 0.1)' : 'rgba(34, 197, 94, 0.1)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    color
                  }}>
                    {isDebit ? <ArrowUpRight size={16} /> : <ArrowDownLeft size={16} />}
                  </div>
                  <div>
                    <div className="dashboard-history-title">{isDebit ? `To Wallet #${tx.targetWalletId}` : `From Wallet #${tx.sourceWalletId}`}</div>
                    <div className="dashboard-history-status" style={{ color: statusColor }}>{tx.status}</div>
                  </div>
                </div>
                <div className="dashboard-history-amount" style={{ color, display: 'flex', alignItems: 'center' }}>
                  {sign}₹{Number(tx.amount ?? 0).toFixed(2)}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <p>No transactions found.</p>
      )}
    </div>
  );
}
