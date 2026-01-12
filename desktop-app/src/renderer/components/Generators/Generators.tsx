import React, { useState, useEffect } from 'react';

interface GeneratorsProps {
  isBackendConnected: boolean;
}

const Generators: React.FC<GeneratorsProps> = ({ isBackendConnected: initialBackendStatus }) => {
  const [activeGenerator, setActiveGenerator] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string>('');
  const [prompt, setPrompt] = useState<string>('');
  const [isBackendConnected, setIsBackendConnected] = useState(initialBackendStatus);

  // Verificar estado del backend al montar
  useEffect(() => {
    checkBackendStatus();
  }, []);

  const checkBackendStatus = async () => {
    try {
      // @ts-ignore
      const status = await window.electronAPI.ai.getStatus();
      setIsBackendConnected(status.isRunning);
    } catch (error) {
      console.error('Error checking backend status:', error);
      setIsBackendConnected(false);
    }
  };

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
      let generatedContent = '';
      
      // @ts-ignore
      const api = window.electronAPI.ai;

      switch (generatorId) {
        case 'idea':
          generatedContent = await api.generateIdea(prompt, 'General');
          break;
        case 'titles':
          generatedContent = await api.generateTitles(prompt, 10);
          break;
        case 'character':
          generatedContent = await api.generateCharacter(prompt);
          break;
        case 'recipe':
          generatedContent = await api.generateRecipe(prompt);
          break;
        case 'report':
          generatedContent = await api.generateReport(prompt);
          break;
        case 'chapter':
          // Para expandir capítulo, usar config más complejo
          generatedContent = await api.expandChapter({
            chapter_num: 1,
            tomo_num: 1,
            outline: prompt,
            target_words: 2000
          });
          break;
        case 'synopsis':
          generatedContent = await api.generateSynopsis(prompt, 'medium');
          break;
        case 'dialogue':
          generatedContent = await api.generateDialogue(prompt, [], 'natural');
          break;
        default:
          generatedContent = '❌ Generador no implementado';
      }

      setResult(generatedContent);

      // Guardar en base de datos local
      // @ts-ignore
      await window.electronAPI.creations.create({
        title: `${generators.find(g => g.id === generatorId)?.title}`,
        type: generatorId,
        content: generatedContent,
        metadata: { prompt, timestamp: new Date().toISOString() },
      });

    } catch (error: any) {
      console.error('Error generando:', error);
      setResult(`❌ Error: ${error.message || error}\n\n⚠️  Verifica que el backend Python esté iniciado:\n\npython backend_python/server.py`);
    } finally {
      setLoading(false);
    }
  };

  if (activeGenerator) {
    return (
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        <button
          onClick={() => {
            setActiveGenerator(null);
            setPrompt('');
            setResult('');
          }}
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
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
            <h2 style={{ color: '#667eea', margin: 0 }}>
              {generators.find(g => g.id === activeGenerator)?.title}
            </h2>
            <div style={{ 
              display: 'inline-block',
              padding: '4px 12px',
              background: isBackendConnected ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)',
              color: isBackendConnected ? '#22c55e' : '#ef4444',
              borderRadius: '20px',
              fontSize: '12px',
              fontWeight: 'bold'
            }}>
              {isBackendConnected ? '🟢 Backend Conectado' : '🔴 Backend Desconectado'}
            </div>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
              Prompt:
            </label>
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
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
            disabled={loading || !prompt.trim()}
            style={{
              width: '100%',
              padding: '15px',
              background: (loading || !prompt.trim())
                ? '#ccc' 
                : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: (loading || !prompt.trim()) ? 'not-allowed' : 'pointer',
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
              maxHeight: '500px',
              overflowY: 'auto'
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
