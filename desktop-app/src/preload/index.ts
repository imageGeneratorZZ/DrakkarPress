import { contextBridge, ipcRenderer } from 'electron';

// Exponer API segura al renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  // ==========================================
  // CREATIONS API
  // ==========================================
  creations: {
    getAll: (filters?: any) => ipcRenderer.invoke('creations:getAll', filters),
    getById: (id: number) => ipcRenderer.invoke('creations:getById', id),
    create: (data: any) => ipcRenderer.invoke('creations:create', data),
    update: (id: number, data: any) => ipcRenderer.invoke('creations:update', id, data),
    delete: (id: number) => ipcRenderer.invoke('creations:delete', id),
    search: (query: string) => ipcRenderer.invoke('creations:search', query),
    export: (id: number, format: string) => ipcRenderer.invoke('creations:export', id, format),
    getStats: () => ipcRenderer.invoke('creations:getStats'),
  },

  // ==========================================
  // HISTORY API
  // ==========================================
  history: {
    save: (data: any) => ipcRenderer.invoke('history:save', data),
    getByCreation: (creationId: number) => ipcRenderer.invoke('history:getByCreation', creationId),
  },

  // ==========================================
  // SHOP API
  // ==========================================
  shop: {
    getAllProducts: () => ipcRenderer.invoke('shop:getAllProducts'),
    createProduct: (data: any) => ipcRenderer.invoke('shop:createProduct', data),
    updateProduct: (id: number, data: any) => ipcRenderer.invoke('shop:updateProduct', id, data),
    deleteProduct: (id: number) => ipcRenderer.invoke('shop:deleteProduct', id),
    publishProduct: (id: number) => ipcRenderer.invoke('shop:publishProduct', id),
    getStats: () => ipcRenderer.invoke('shop:getStats'),
  },

  // ==========================================
  // SETTINGS API
  // ==========================================
  settings: {
    get: (key: string) => ipcRenderer.invoke('settings:get', key),
    set: (key: string, value: any, type: string) => ipcRenderer.invoke('settings:set', key, value, type),
    getAll: () => ipcRenderer.invoke('settings:getAll'),
    delete: (key: string) => ipcRenderer.invoke('settings:delete', key),
  },

  // ==========================================
  // FILE SYSTEM API
  // ==========================================
  fs: {
    selectFile: () => ipcRenderer.invoke('fs:selectFile'),
    selectFolder: () => ipcRenderer.invoke('fs:selectFolder'),
  },

  // ==========================================
  // AI GENERATION API
  // ==========================================
  ai: {
    // Status
    checkHealth: () => ipcRenderer.invoke('ai:checkHealth'),
    getStatus: () => ipcRenderer.invoke('ai:getStatus'),
    
    // Basic generators
    generateIdea: (prompt: string, genre?: string) => ipcRenderer.invoke('ai:generateIdea', prompt, genre),
    generateCharacter: (prompt: string) => ipcRenderer.invoke('ai:generateCharacter', prompt),
    generateSynopsis: (prompt: string, length?: 'short' | 'medium' | 'long') => 
      ipcRenderer.invoke('ai:generateSynopsis', prompt, length),
    generateDialogue: (prompt: string, characters?: string[], tone?: string) => 
      ipcRenderer.invoke('ai:generateDialogue', prompt, characters, tone),
    generateTitles: (prompt: string, count?: number) => ipcRenderer.invoke('ai:generateTitles', prompt, count),
    generateRecipe: (prompt: string) => ipcRenderer.invoke('ai:generateRecipe', prompt),
    generateReport: (prompt: string) => ipcRenderer.invoke('ai:generateReport', prompt),
    
    // Advanced generators
    expandChapter: (config: any) => ipcRenderer.invoke('ai:expandChapter', config),
    generateMaestro: (chapterNum: number, prevContext?: string) => 
      ipcRenderer.invoke('ai:generateMaestro', chapterNum, prevContext),
  },

  // ==========================================
  // UPDATER API
  // ==========================================
  updater: {
    onUpdateAvailable: (callback: () => void) => {
      ipcRenderer.on('update-available', callback);
    },
    onUpdateDownloaded: (callback: () => void) => {
      ipcRenderer.on('update-downloaded', callback);
    },
    installUpdate: () => {
      ipcRenderer.send('install-update');
    },
  },
});
