import React, { useState } from 'react';

interface GeneratorsProps {
  isBackendConnected: boolean;
}

const Generators: React.FC<GeneratorsProps> = ({ isBackendConnected }) => {
  const [activeGenerator, setActiveGenerator] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string>('');

  const generators = [
    { id: 'idea', icon: '💡', title: 'Generar Idea de Libro', category: 'Creación' },
    { id: 'titles', icon: '🏷️', title: 'Sugerir Títulos', category: 'Creación' },
    { id: 'character', icon: '👤', title: 'Generar Personaje', category: 'Creación' },
    { id: 'recipe', icon: '🍳', title: 'Generar Receta', category: 'Especializado' },
    { id: 'report', icon: '📊', title: 'Generar Informe', category: 'Especializado' },
    { id: 'chapter', icon: '📖', title: 'Extender Capítulo', category: 'Edición' },
    { id: 'synopsis', icon: '📝', title: 'Generar Sinopsis', category: 'Edición' },
    { id: 'dialogue', icon: '💬', title: 'Generar Diálogo', category: 'Edición' },
  ];

  const handleGenerate = async (generatorId: string) => {
    setLoading(true);
    setResult('');

    try {
      if (isBackendConnected) {
        // Llamar al backend real
        const response = await fetch(`http://localhost:8080/api/ai/${generatorId}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ /* datos del formulario */ }),
        });
        const data = await response.text();
        setResult(data);

        // Guardar en base de datos local
        // @ts-ignore
        await window.electronAPI.creations.create({
          title: `Generación ${generatorId}`,
          type: generatorId,
          content: data,
          metadata: {},
        });
      } else {
        // Modo demo offline
        setResult(`🎭 MODO DEMO (Offline)\n\nEste es un resultado de ejemplo para el generador "${generatorId}".\n\nEl backend no está disponible. Cuando se conecte, obtendrás resultados reales generados por IA.\n\n✨ Características de la versión completa:\n- Generación con IA avanzada\n- 25+ géneros combinables\n- Personalización completa\n- Guardado automático en biblioteca`);
      }
    } catch (error) {
      setResult(`❌ Error: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  if (activeGenerator) {
    return (
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        <button
          onClick={() => setActiveGenerator(null)}
          style={{
            marginBottom: '20px',
            padding: '10px 20px',
            background: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '16px',
          }}
        >
          ← Volver a Generadores
        </button>

        <div style={{
          background: 'white',
          borderRadius: '15px',
          padding: '30px',
          boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
        }}>
          <h2 style={{ marginBottom: '20px', color: '#667eea' }}>
            {generators.find(g => g.id === activeGenerator)?.title}
          </h2>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
              Prompt:
            </label>
            <textarea
              style={{
                width: '100%',
                padding: '15px',
                borderRadius: '8px',
                border: '2px solid #e0e0e0',
                fontSize: '16px',
                minHeight: '150px',
                fontFamily: 'inherit',
              }}
              placeholder="Describe lo que quieres generar..."
            />
          </div>

          <button
            onClick={() => handleGenerate(activeGenerator)}
            disabled={loading}
            style={{
              width: '100%',
              padding: '15px',
              background: loading 
                ? '#ccc' 
                : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: loading ? 'not-allowed' : 'pointer',
            }}
          >
            {loading ? '⏳ Generando...' : '✨ Generar'}
          </button>

          {result && (
            <div style={{
              marginTop: '20px',
              padding: '20px',
              background: '#f8f9fa',
              borderLeft: '4px solid #667eea',
              borderRadius: '8px',
              whiteSpace: 'pre-wrap',
            }}>
              {result}
            </div>
          )}
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
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
          ✨ Generadores de IA
        </h1>
        <p style={{ color: '#666' }}>
          Selecciona un generador para crear contenido con inteligencia artificial
        </p>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
        gap: '20px',
      }}>
        {generators.map(gen => (
          <button
            key={gen.id}
            onClick={() => setActiveGenerator(gen.id)}
            style={{
              background: 'white',
              border: 'none',
              borderRadius: '15px',
              padding: '25px',
              cursor: 'pointer',
              boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
              textAlign: 'left',
              transition: 'transform 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-5px)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
            }}
          >
            <div style={{ fontSize: '48px', marginBottom: '15px' }}>{gen.icon}</div>
            <h3 style={{ fontSize: '18px', color: '#333', marginBottom: '8px' }}>
              {gen.title}
            </h3>
            <span style={{
              display: 'inline-block',
              padding: '4px 12px',
              background: 'rgba(102, 126, 234, 0.1)',
              color: '#667eea',
              borderRadius: '20px',
              fontSize: '12px',
            }}>
              {gen.category}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default Generators;
