import React, { useState, useEffect } from 'react';

// Componentes principales
import Sidebar from './components/Sidebar/Sidebar';
import Dashboard from './components/Dashboard/Dashboard';
import Generators from './components/Generators/Generators';
import Library from './components/Library/Library';
import Shop from './components/Shop/Shop';
import Settings from './components/Settings/Settings';

type View = 'dashboard' | 'generators' | 'library' | 'shop' | 'settings';

const App: React.FC = () => {
  const [currentView, setCurrentView] = useState<View>('dashboard');
  const [isBackendConnected, setIsBackendConnected] = useState(false);

  useEffect(() => {
    // Verificar conexión con backend
    checkBackendConnection();
    
    // Verificar cada 30 segundos
    const interval = setInterval(checkBackendConnection, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const checkBackendConnection = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/health');
      setIsBackendConnected(response.ok);
    } catch (error) {
      setIsBackendConnected(false);
    }
  };

  const renderView = () => {
    switch (currentView) {
      case 'dashboard':
        return <Dashboard onNavigate={setCurrentView} isBackendConnected={isBackendConnected} />;
      case 'generators':
        return <Generators isBackendConnected={isBackendConnected} />;
      case 'library':
        return <Library />;
      case 'shop':
        return <Shop />;
      case 'settings':
        return <Settings />;
      default:
        return <Dashboard onNavigate={setCurrentView} isBackendConnected={isBackendConnected} />;
    }
  };

  return (
    <div style={{ 
      display: 'flex', 
      height: '100vh', 
      width: '100vw',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <Sidebar 
        currentView={currentView} 
        onNavigate={setCurrentView}
        isBackendConnected={isBackendConnected}
      />
      <main style={{ 
        flex: 1, 
        overflow: 'auto',
        padding: '20px'
      }}>
        {renderView()}
      </main>
    </div>
  );
};

export default App;
