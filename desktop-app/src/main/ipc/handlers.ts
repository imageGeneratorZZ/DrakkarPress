import { ipcMain } from 'electron';
import creationsService from '../services/creations.service';
import shopService from '../services/shop.service';
import settingsService from '../services/settings.service';

export function registerIpcHandlers() {
  // ==========================================
  // CREATIONS HANDLERS
  // ==========================================
  
  ipcMain.handle('creations:getAll', async (_, filters?: any) => {
    return await creationsService.getAll(filters);
  });

  ipcMain.handle('creations:getById', async (_, id: string) => {
    return await creationsService.getById(id);
  });

  ipcMain.handle('creations:create', async (_, data: any) => {
    return await creationsService.create(data);
  });

  ipcMain.handle('creations:update', async (_, id: string, data: any) => {
    return await creationsService.update(id, data);
  });

  ipcMain.handle('creations:delete', async (_, id: string) => {
    return await creationsService.delete(id);
  });

  ipcMain.handle('creations:search', async (_, query: string) => {
    return await creationsService.search(query);
  });

  ipcMain.handle('creations:export', async (_, id: string, format: string) => {
    return await creationsService.exportCreation(id, format as any);
  });

  ipcMain.handle('creations:getStats', async () => {
    return await creationsService.getStats();
  });

  // ==========================================
  // GENERATION HISTORY HANDLERS
  // ==========================================
  
  ipcMain.handle('history:save', async (_, data: any) => {
    return await creationsService.saveHistory(data);
  });

  ipcMain.handle('history:getByCreation', async (_, creationId: string) => {
    return await creationsService.getHistory(creationId);
  });

  // ==========================================
  // SHOP HANDLERS
  // ==========================================
  
  ipcMain.handle('shop:getAllProducts', async () => {
    return await shopService.getAllProducts();
  });

  ipcMain.handle('shop:createProduct', async (_, data: any) => {
    return await shopService.createProduct(data);
  });

  ipcMain.handle('shop:updateProduct', async (_, id: string, data: any) => {
    return await shopService.updateProduct(id, data);
  });

  ipcMain.handle('shop:deleteProduct', async (_, id: string) => {
    return await shopService.deleteProduct(id);
  });

  ipcMain.handle('shop:publishProduct', async (_, id: string) => {
    return await shopService.publishProduct(id);
  });

  ipcMain.handle('shop:getStats', async () => {
    return await shopService.getStats();
  });

  // ==========================================
  // SETTINGS HANDLERS
  // ==========================================
  
  ipcMain.handle('settings:get', async (_, key: string) => {
    return await settingsService.get(key);
  });

  ipcMain.handle('settings:set', async (_, key: string, value: any, type: any) => {
    return await settingsService.set(key, value, type);
  });

  ipcMain.handle('settings:getAll', async () => {
    return await settingsService.getAll();
  });

  ipcMain.handle('settings:delete', async (_, key: string) => {
    return await settingsService.delete(key);
  });

  // ==========================================
  // FILE SYSTEM HANDLERS
  // ==========================================
  
  ipcMain.handle('fs:selectFile', async () => {
    const { dialog } = require('electron');
    const result = await dialog.showOpenDialog({
      properties: ['openFile'],
      filters: [
        { name: 'Text Files', extensions: ['txt', 'md', 'docx'] },
        { name: 'All Files', extensions: ['*'] }
      ]
    });
    return result.filePaths[0];
  });

  ipcMain.handle('fs:selectFolder', async () => {
    const { dialog } = require('electron');
    const result = await dialog.showOpenDialog({
      properties: ['openDirectory']
    });
    return result.filePaths[0];
  });

  console.log('✅ IPC handlers registered successfully');
}
