import { app, BrowserWindow, ipcMain } from 'electron';
import path from 'path';
import { initDatabase } from './database/connection';
import { registerIpcHandlers } from './ipc/handlers';
import { autoUpdater } from 'electron-updater';
import * as aiService from './services/ai.service';

let mainWindow: BrowserWindow | null = null;
const isDev = process.env.NODE_ENV === 'development';

async function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 700,
    title: 'DrakkarPress',
    icon: path.join(__dirname, '../../public/icons/icon.png'),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, '../preload/index.js'),
    },
    frame: true,
    backgroundColor: '#667eea',
    show: false,
  });

  // Esperar a que la ventana esté lista antes de mostrarla
  mainWindow.once('ready-to-show', () => {
    mainWindow?.show();
  });

  // Cargar la aplicación
  if (isDev) {
    mainWindow.loadURL('http://localhost:3000');
    mainWindow.webContents.openDevTools();
  } else {
    mainWindow.loadFile(path.join(__dirname, '../renderer/index.html'));
  }

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  // Verificar actualizaciones (solo en producción)
  if (!isDev) {
    autoUpdater.checkForUpdatesAndNotify();
  }
}

// Inicializar la aplicación
app.whenReady().then(async () => {
  // Inicializar base de datos
  await initDatabase();
  
  // Iniciar backend Python
  console.log('🐍 Iniciando backend Python de generación IA...');
  const backendStarted = await aiService.startPythonBackend();
  if (backendStarted) {
    console.log('✅ Backend Python iniciado correctamente');
  } else {
    console.warn('⚠️  Backend Python no se pudo iniciar. Las funciones de IA no estarán disponibles.');
  }
  
  // Registrar handlers de IPC
  registerIpcHandlers();
  
  // Crear ventana principal
  await createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

// Salir cuando todas las ventanas estén cerradas (excepto en macOS)
app.on('window-all-closed', () => {
  // Detener backend Python
  console.log('🛑 Cerrando aplicación, deteniendo backend Python...');
  aiService.stopPythonBackend();
  
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// Limpiar recursos al salir
app.on('quit', () => {
  aiService.stopPythonBackend();
});

// Auto-updater events
autoUpdater.on('update-available', () => {
  mainWindow?.webContents.send('update-available');
});

autoUpdater.on('update-downloaded', () => {
  mainWindow?.webContents.send('update-downloaded');
});

// Instalar actualización
ipcMain.on('install-update', () => {
  autoUpdater.quitAndInstall();
});

export { mainWindow };
