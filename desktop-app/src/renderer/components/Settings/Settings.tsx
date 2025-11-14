import React, { useEffect, useState } from 'react';

const Settings: React.FC = () => {
  const [settings, setSettings] = useState<any>({});
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    try {
      // @ts-ignore
      const data = await window.electronAPI.settings.getAll();
      setSettings(data);
    } catch (error) {
      console.error('Error loading settings:', error);
    }
  };

  const handleSave = async (key: string, value: any, type: string) => {
    try {
      // @ts-ignore
      await window.electronAPI.settings.set(key, value, type);
      setSaved(true);
      setTimeout(() => setSaved(false), 2000);
    } catch (error) {
      console.error('Error saving setting:', error);
    }
  };

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      <div style={{
        background: 'white',
        borderRadius: '15px',
        padding: '30px',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
      }}>
        <h1 style={{
          fontSize: '28px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          marginBottom: '30px',
        }}>
          ⚙️ Configuración
        </h1>

        {saved && (
          <div style={{
            padding: '15px',
            background: 'rgba(76, 175, 80, 0.1)',
            border: '2px solid #4caf50',
            borderRadius: '8px',
            marginBottom: '20px',
            color: '#4caf50',
            fontWeight: 'bold',
          }}>
            ✓ Configuración guardada
          </div>
        )}

        {/* Apariencia */}
        <Section title="🎨 Apariencia">
          <SettingRow label="Tema">
            <select
              value={settings.theme || 'dark'}
              onChange={(e) => handleSave('theme', e.target.value, 'string')}
              style={selectStyle}
            >
              <option value="light">Claro</option>
              <option value="dark">Oscuro</option>
              <option value="auto">Automático</option>
            </select>
          </SettingRow>
        </Section>

        {/* Idioma */}
        <Section title="🌐 Idioma">
          <SettingRow label="Idioma de la aplicación">
            <select
              value={settings.language || 'es'}
              onChange={(e) => handleSave('language', e.target.value, 'string')}
              style={selectStyle}
            >
              <option value="es">Español</option>
              <option value="en">English</option>
            </select>
          </SettingRow>
        </Section>

        {/* IA */}
        <Section title="🤖 Inteligencia Artificial">
          <SettingRow label="Modelo de IA">
            <select
              value={settings.aiModel || 'gpt-4'}
              onChange={(e) => handleSave('aiModel', e.target.value, 'string')}
              style={selectStyle}
            >
              <option value="gpt-4">GPT-4</option>
              <option value="gpt-3.5">GPT-3.5 Turbo</option>
              <option value="claude">Claude</option>
            </select>
          </SettingRow>
          <SettingRow label="Creatividad">
            <input
              type="range"
              min="0"
              max="1"
              step="0.1"
              value={settings.aiTemperature || 0.7}
              onChange={(e) => handleSave('aiTemperature', parseFloat(e.target.value), 'number')}
              style={{ width: '100%' }}
            />
            <span style={{ fontSize: '14px', color: '#666' }}>
              {settings.aiTemperature || 0.7}
            </span>
          </SettingRow>
        </Section>

        {/* Control por Voz */}
        <Section title="🎤 Control por Voz">
          <SettingRow label="Habilitar control por voz">
            <input
              type="checkbox"
              checked={settings.voiceEnabled || false}
              onChange={(e) => handleSave('voiceEnabled', e.target.checked, 'boolean')}
              style={{ width: '24px', height: '24px', cursor: 'pointer' }}
            />
          </SettingRow>
          <SettingRow label="Idioma de reconocimiento">
            <select
              value={settings.voiceLanguage || 'es-ES'}
              onChange={(e) => handleSave('voiceLanguage', e.target.value, 'string')}
              style={selectStyle}
              disabled={!settings.voiceEnabled}
            >
              <option value="es-ES">Español</option>
              <option value="en-US">English</option>
            </select>
          </SettingRow>
        </Section>

        {/* Sincronización */}
        <Section title="🔄 Sincronización">
          <SettingRow label="Sincronización automática">
            <input
              type="checkbox"
              checked={settings.autoSync || false}
              onChange={(e) => handleSave('autoSync', e.target.checked, 'boolean')}
              style={{ width: '24px', height: '24px', cursor: 'pointer' }}
            />
          </SettingRow>
          <SettingRow label="Frecuencia de backup">
            <select
              value={settings.backupFrequency || 'daily'}
              onChange={(e) => handleSave('backupFrequency', e.target.value, 'string')}
              style={selectStyle}
            >
              <option value="hourly">Cada hora</option>
              <option value="daily">Diario</option>
              <option value="weekly">Semanal</option>
              <option value="manual">Manual</option>
            </select>
          </SettingRow>
        </Section>

        {/* Información */}
        <Section title="ℹ️ Información">
          <div style={{ padding: '15px', background: '#f8f9fa', borderRadius: '8px' }}>
            <p style={{ marginBottom: '5px' }}><strong>Versión:</strong> 1.0.0 BETA</p>
            <p style={{ marginBottom: '5px' }}><strong>Electron:</strong> 28.2.0</p>
            <p><strong>Node:</strong> 20.x</p>
          </div>
        </Section>
      </div>
    </div>
  );
};

// Componentes auxiliares
const Section: React.FC<{title: string, children: React.ReactNode}> = ({ title, children }) => (
  <div style={{ marginBottom: '30px' }}>
    <h2 style={{ fontSize: '20px', marginBottom: '15px', color: '#333' }}>
      {title}
    </h2>
    {children}
  </div>
);

const SettingRow: React.FC<{label: string, children: React.ReactNode}> = ({ label, children }) => (
  <div style={{
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '15px 0',
    borderBottom: '1px solid #e0e0e0',
  }}>
    <span style={{ fontSize: '16px', color: '#666' }}>{label}</span>
    <div>{children}</div>
  </div>
);

const selectStyle = {
  padding: '8px 12px',
  borderRadius: '6px',
  border: '2px solid #e0e0e0',
  fontSize: '14px',
  minWidth: '150px',
};

export default Settings;
