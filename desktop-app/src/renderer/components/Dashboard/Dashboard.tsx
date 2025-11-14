import React, { useEffect, useState } from 'react';

interface DashboardProps {
  onNavigate: (view: string) => void;
  isBackendConnected: boolean;
}

const Dashboard: React.FC<DashboardProps> = ({ onNavigate, isBackendConnected }) => {
  const [stats, setStats] = useState({
    totalCreations: 0,
    totalWords: 0,
    favoriteCount: 0,
    byType: [],
  });

  const [shopStats, setShopStats] = useState({
    totalProducts: 0,
    activeProducts: 0,
    totalSales: 0,
    totalRevenue: 0,
  });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      // @ts-ignore - electronAPI se define en preload
      const creationStats = await window.electronAPI.creations.getStats();
      setStats(creationStats);

      // @ts-ignore
      const shopStatsData = await window.electronAPI.shop.getStats();
      setShopStats(shopStatsData);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

  const quickActions = [
    { id: 'generate', icon: '✨', label: 'Nuevo Generador', color: '#667eea', view: 'generators' },
    { id: 'library', icon: '📚', label: 'Ver Biblioteca', color: '#764ba2', view: 'library' },
    { id: 'shop', icon: '🛍️', label: 'Publicar Producto', color: '#4caf50', view: 'shop' },
    { id: 'voice', icon: '🎤', label: 'Control por Voz', color: '#ff9800', view: 'settings' },
  ];

  return (
    <div style={{ maxWidth: '1400px', margin: '0 auto' }}>
      {/* Header */}
      <div style={{
        background: 'white',
        borderRadius: '15px',
        padding: '30px',
        marginBottom: '20px',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
      }}>
        <h1 style={{
          fontSize: '32px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          marginBottom: '10px',
        }}>
          ¡Bienvenido a DrakkarPress! 👋
        </h1>
        <p style={{ color: '#666', fontSize: '16px' }}>
          Tu plataforma de escritura con IA - Versión Desktop BETA
        </p>

        {!isBackendConnected && (
          <div style={{
            marginTop: '15px',
            padding: '15px',
            background: 'rgba(255, 152, 0, 0.1)',
            borderLeft: '4px solid #ff9800',
            borderRadius: '8px',
          }}>
            <strong>⏳ Modo Offline</strong>
            <p style={{ fontSize: '14px', marginTop: '5px', color: '#666' }}>
              El backend no está disponible. Puedes trabajar localmente y sincronizar después.
            </p>
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '20px',
        marginBottom: '20px',
      }}>
        {quickActions.map(action => (
          <button
            key={action.id}
            onClick={() => onNavigate(action.view)}
            style={{
              background: 'white',
              border: 'none',
              borderRadius: '15px',
              padding: '25px',
              cursor: 'pointer',
              boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
              transition: 'transform 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-5px)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
            }}
          >
            <div style={{
              fontSize: '48px',
              marginBottom: '15px',
            }}>
              {action.icon}
            </div>
            <h3 style={{
              fontSize: '18px',
              color: action.color,
              marginBottom: '5px',
            }}>
              {action.label}
            </h3>
          </button>
        ))}
      </div>

      {/* Stats Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '20px',
        marginBottom: '20px',
      }}>
        <StatCard 
          icon="📝" 
          label="Creaciones" 
          value={stats.totalCreations} 
          color="#667eea"
        />
        <StatCard 
          icon="📊" 
          label="Palabras Totales" 
          value={stats.totalWords.toLocaleString()} 
          color="#764ba2"
        />
        <StatCard 
          icon="⭐" 
          label="Favoritos" 
          value={stats.favoriteCount} 
          color="#ffc107"
        />
        <StatCard 
          icon="🛍️" 
          label="Productos" 
          value={shopStats.totalProducts} 
          color="#4caf50"
        />
        <StatCard 
          icon="💰" 
          label="Ventas" 
          value={shopStats.totalSales} 
          color="#ff9800"
        />
        <StatCard 
          icon="💵" 
          label="Ingresos" 
          value={`$${shopStats.totalRevenue.toFixed(2)}`} 
          color="#e91e63"
        />
      </div>

      {/* Recent Activity */}
      <div style={{
        background: 'white',
        borderRadius: '15px',
        padding: '25px',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
      }}>
        <h2 style={{
          fontSize: '24px',
          marginBottom: '20px',
          color: '#333',
        }}>
          📈 Actividad Reciente
        </h2>
        <div style={{
          textAlign: 'center',
          padding: '40px',
          color: '#999',
        }}>
          <p>Comienza a crear contenido para ver tu actividad aquí</p>
          <button
            onClick={() => onNavigate('generators')}
            style={{
              marginTop: '15px',
              padding: '12px 24px',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: 'pointer',
            }}
          >
            🚀 Crear Primera Creación
          </button>
        </div>
      </div>
    </div>
  );
};

// Componente StatCard
const StatCard: React.FC<{icon: string, label: string, value: string | number, color: string}> = 
  ({ icon, label, value, color }) => (
  <div style={{
    background: 'white',
    borderRadius: '15px',
    padding: '20px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
    textAlign: 'center',
  }}>
    <div style={{ fontSize: '36px', marginBottom: '10px' }}>{icon}</div>
    <div style={{ fontSize: '24px', fontWeight: 'bold', color, marginBottom: '5px' }}>
      {value}
    </div>
    <div style={{ fontSize: '14px', color: '#666' }}>{label}</div>
  </div>
);

export default Dashboard;
