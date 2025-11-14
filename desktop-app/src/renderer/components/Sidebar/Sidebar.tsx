import React from 'react';

interface SidebarProps {
  currentView: string;
  onNavigate: (view: any) => void;
  isBackendConnected: boolean;
}

const Sidebar: React.FC<SidebarProps> = ({ currentView, onNavigate, isBackendConnected }) => {
  const menuItems = [
    { id: 'dashboard', icon: '📊', label: 'Dashboard' },
    { id: 'generators', icon: '✨', label: 'Generadores' },
    { id: 'library', icon: '📚', label: 'Biblioteca' },
    { id: 'shop', icon: '🛍️', label: 'Mi Tienda' },
    { id: 'settings', icon: '⚙️', label: 'Configuración' },
  ];

  return (
    <aside style={{
      width: '250px',
      background: 'rgba(255, 255, 255, 0.95)',
      backdropFilter: 'blur(10px)',
      boxShadow: '2px 0 20px rgba(0, 0, 0, 0.1)',
      display: 'flex',
      flexDirection: 'column',
      padding: '20px',
    }}>
      {/* Logo */}
      <div style={{
        textAlign: 'center',
        marginBottom: '30px',
        paddingBottom: '20px',
        borderBottom: '2px solid rgba(102, 126, 234, 0.2)',
      }}>
        <h1 style={{
          fontSize: '24px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          marginBottom: '5px',
        }}>
          ⚔️ DrakkarPress
        </h1>
        <p style={{ fontSize: '12px', color: '#666' }}>Desktop v1.0 BETA</p>
      </div>

      {/* Status Badge */}
      <div style={{
        padding: '10px',
        borderRadius: '8px',
        background: isBackendConnected 
          ? 'rgba(76, 175, 80, 0.1)' 
          : 'rgba(255, 152, 0, 0.1)',
        marginBottom: '20px',
        textAlign: 'center',
      }}>
        <span style={{
          fontSize: '12px',
          color: isBackendConnected ? '#4caf50' : '#ff9800',
          fontWeight: 'bold',
        }}>
          {isBackendConnected ? '✓ Conectado' : '⏳ Offline'}
        </span>
      </div>

      {/* Menu Items */}
      <nav style={{ flex: 1 }}>
        {menuItems.map(item => (
          <button
            key={item.id}
            onClick={() => onNavigate(item.id)}
            style={{
              width: '100%',
              padding: '15px',
              marginBottom: '8px',
              border: 'none',
              borderRadius: '10px',
              background: currentView === item.id 
                ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                : 'transparent',
              color: currentView === item.id ? 'white' : '#333',
              fontSize: '16px',
              fontWeight: currentView === item.id ? 'bold' : 'normal',
              cursor: 'pointer',
              textAlign: 'left',
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              transition: 'all 0.3s ease',
            }}
            onMouseEnter={(e) => {
              if (currentView !== item.id) {
                e.currentTarget.style.background = 'rgba(102, 126, 234, 0.1)';
              }
            }}
            onMouseLeave={(e) => {
              if (currentView !== item.id) {
                e.currentTarget.style.background = 'transparent';
              }
            }}
          >
            <span style={{ fontSize: '20px' }}>{item.icon}</span>
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      {/* Footer */}
      <div style={{
        paddingTop: '20px',
        borderTop: '2px solid rgba(102, 126, 234, 0.2)',
        textAlign: 'center',
      }}>
        <p style={{ fontSize: '11px', color: '#999' }}>
          © 2025 DrakkarPress
        </p>
      </div>
    </aside>
  );
};

export default Sidebar;
