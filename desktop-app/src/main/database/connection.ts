import Store from 'electron-store';

// Usar electron-store para almacenamiento local JSON
export const db = new Store({
  name: 'drakkarpress-data',
  defaults: {
    creations: [],
    generationHistory: [],
    userSettings: {},
    shopProducts: [],
    communityMessages: []
  }
});

export function initDatabase() {
  try {
    console.log('✅ Database initialized successfully with electron-store.');
    return true;
  } catch (error) {
    console.error('❌ Unable to initialize database:', error);
    throw error;
  }
}

export function closeDatabase() {
  console.log('✅ Database connection closed.');
}
