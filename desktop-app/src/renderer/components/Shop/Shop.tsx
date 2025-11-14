import React, { useEffect, useState } from 'react';

const Shop: React.FC = () => {
  const [products, setProducts] = useState<any[]>([]);
  const [stats, setStats] = useState({
    totalProducts: 0,
    activeProducts: 0,
    totalSales: 0,
    totalRevenue: 0,
  });

  useEffect(() => {
    loadProducts();
    loadStats();
  }, []);

  const loadProducts = async () => {
    try {
      // @ts-ignore
      const data = await window.electronAPI.shop.getAllProducts();
      setProducts(data);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const loadStats = async () => {
    try {
      // @ts-ignore
      const data = await window.electronAPI.shop.getStats();
      setStats(data);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

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
          fontSize: '28px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          marginBottom: '10px',
        }}>
          🛍️ Mi Tienda Personal
        </h1>
        <p style={{ color: '#666' }}>
          Gestiona tus productos y ventas
        </p>
      </div>

      {/* Stats */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '20px',
        marginBottom: '20px',
      }}>
        <StatCard icon="📦" label="Productos" value={stats.totalProducts} />
        <StatCard icon="✅" label="Activos" value={stats.activeProducts} />
        <StatCard icon="💰" label="Ventas" value={stats.totalSales} />
        <StatCard icon="💵" label="Ingresos" value={`$${stats.totalRevenue.toFixed(2)}`} />
      </div>

      {/* Products List */}
      <div style={{
        background: 'white',
        borderRadius: '15px',
        padding: '30px',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
      }}>
        <h2 style={{ marginBottom: '20px' }}>Mis Productos</h2>
        
        {products.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
            <p>No tienes productos publicados</p>
            <button style={{
              marginTop: '15px',
              padding: '12px 24px',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: 'pointer',
            }}>
              🚀 Publicar Primer Producto
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {products.map(product => (
              <div
                key={product.id}
                style={{
                  padding: '20px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '10px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <div>
                  <h3>{product.title}</h3>
                  <p style={{ color: '#666', fontSize: '14px' }}>
                    {product.format} • ${product.price}
                  </p>
                  <span style={{
                    display: 'inline-block',
                    padding: '4px 12px',
                    background: product.status === 'active' ? '#4caf50' : '#ff9800',
                    color: 'white',
                    borderRadius: '20px',
                    fontSize: '12px',
                    marginTop: '8px',
                  }}>
                    {product.status}
                  </span>
                </div>
                <div>
                  <p style={{ fontSize: '14px', color: '#666' }}>
                    Ventas: {product.salesCount}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

const StatCard: React.FC<{icon: string, label: string, value: string | number}> = 
  ({ icon, label, value }) => (
  <div style={{
    background: 'white',
    borderRadius: '15px',
    padding: '20px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
    textAlign: 'center',
  }}>
    <div style={{ fontSize: '36px', marginBottom: '10px' }}>{icon}</div>
    <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#667eea', marginBottom: '5px' }}>
      {value}
    </div>
    <div style={{ fontSize: '14px', color: '#666' }}>{label}</div>
  </div>
);

export default Shop;
