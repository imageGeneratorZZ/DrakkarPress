import React, { useEffect, useState } from 'react';

const Library: React.FC = () => {
  const [creations, setCreations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadCreations();
  }, []);

  const loadCreations = async () => {
    try {
      // @ts-ignore
      const data = await window.electronAPI.creations.getAll();
      setCreations(data);
    } catch (error) {
      console.error('Error loading creations:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadCreations();
      return;
    }
    try {
      // @ts-ignore
      const results = await window.electronAPI.creations.search(searchQuery);
      setCreations(results);
    } catch (error) {
      console.error('Error searching:', error);
    }
  };

  const handleExport = async (id: number) => {
    try {
      // @ts-ignore
      await window.electronAPI.creations.export(id, 'pdf');
      alert('Exportación completada!');
    } catch (error) {
      alert('Error al exportar');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px', color: 'white' }}>
        <h2>Cargando biblioteca...</h2>
      </div>
    );
  }

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
          marginBottom: '15px',
        }}>
          📚 Mi Biblioteca
        </h1>

        {/* Barra de búsqueda */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            placeholder="Buscar por título, contenido o tags..."
            style={{
              flex: 1,
              padding: '12px',
              borderRadius: '8px',
              border: '2px solid #e0e0e0',
              fontSize: '16px',
            }}
          />
          <button
            onClick={handleSearch}
            style={{
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
            🔍 Buscar
          </button>
        </div>
      </div>

      {/* Lista de creaciones */}
      {creations.length === 0 ? (
        <div style={{
          background: 'white',
          borderRadius: '15px',
          padding: '60px',
          textAlign: 'center',
          boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
        }}>
          <div style={{ fontSize: '64px', marginBottom: '20px' }}>📝</div>
          <h2>Tu biblioteca está vacía</h2>
          <p style={{ color: '#666', marginTop: '10px' }}>
            Comienza a crear contenido con los generadores de IA
          </p>
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: '20px',
        }}>
          {creations.map(creation => (
            <div
              key={creation.id}
              style={{
                background: 'white',
                borderRadius: '15px',
                padding: '20px',
                boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
                cursor: 'pointer',
                transition: 'transform 0.3s ease',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-5px)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
              }}
            >
              <h3 style={{ marginBottom: '10px', color: '#333' }}>
                {creation.title}
              </h3>
              <p style={{ fontSize: '14px', color: '#666', marginBottom: '10px' }}>
                {creation.type} • {creation.wordCount} palabras
              </p>
              <p style={{
                fontSize: '14px',
                color: '#999',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                display: '-webkit-box',
                WebkitLineClamp: 3,
                WebkitBoxOrient: 'vertical',
                marginBottom: '15px',
              }}>
                {creation.content?.substring(0, 150)}...
              </p>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button
                  onClick={() => handleExport(creation.id)}
                  style={{
                    flex: 1,
                    padding: '8px',
                    background: '#667eea',
                    color: 'white',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: 'pointer',
                  }}
                >
                  📄 Exportar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Library;
