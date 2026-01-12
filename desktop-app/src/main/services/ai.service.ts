import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';
import * as fs from 'fs';
import axios from 'axios';

/**
 * Servicio para gestionar el backend Python y llamar a generadores de IA
 */

const BACKEND_PORT = 5000;
const BACKEND_URL = `http://127.0.0.1:${BACKEND_PORT}`;
const PYTHON_BACKEND_PATH = path.join(__dirname, '../../../backend_python/server.py');

let pythonProcess: ChildProcess | null = null;
let isBackendReady = false;

/**
 * Iniciar el servidor Python en segundo plano
 */
export async function startPythonBackend(): Promise<boolean> {
  return new Promise((resolve, reject) => {
    console.log('🐍 Iniciando backend Python...');
    console.log(`📁 Path: ${PYTHON_BACKEND_PATH}`);

    // Verificar que el archivo existe
    if (!fs.existsSync(PYTHON_BACKEND_PATH)) {
      console.error(`❌ No se encontró el backend Python en: ${PYTHON_BACKEND_PATH}`);
      resolve(false);
      return;
    }

    // Iniciar proceso Python
    pythonProcess = spawn('python', [PYTHON_BACKEND_PATH], {
      env: {
        ...process.env,
        BACKEND_PORT: String(BACKEND_PORT),
        PYTHONUNBUFFERED: '1'
      },
      cwd: path.dirname(PYTHON_BACKEND_PATH)
    });

    // Capturar stdout
    pythonProcess.stdout?.on('data', (data) => {
      const output = data.toString();
      console.log(`[Python Backend] ${output}`);
      
      // Detectar cuando el servidor está listo
      if (output.includes('Running on') || output.includes('iniciando')) {
        isBackendReady = true;
        console.log('✅ Backend Python listo');
      }
    });

    // Capturar stderr
    pythonProcess.stderr?.on('data', (data) => {
      console.error(`[Python Backend Error] ${data.toString()}`);
    });

    // Manejar cierre del proceso
    pythonProcess.on('close', (code) => {
      console.log(`Backend Python cerrado con código: ${code}`);
      isBackendReady = false;
      pythonProcess = null;
    });

    pythonProcess.on('error', (error) => {
      console.error('Error iniciando backend Python:', error);
      reject(error);
    });

    // Esperar a que el servidor esté listo (máximo 10 segundos)
    let attempts = 0;
    const maxAttempts = 20;
    const checkInterval = setInterval(async () => {
      attempts++;
      
      try {
        const response = await axios.get(`${BACKEND_URL}/health`, { timeout: 1000 });
        if (response.status === 200) {
          clearInterval(checkInterval);
          isBackendReady = true;
          console.log('✅ Backend Python verificado y funcionando');
          resolve(true);
        }
      } catch (error) {
        if (attempts >= maxAttempts) {
          clearInterval(checkInterval);
          console.warn('⚠️  Backend Python no responde después de 10s');
          resolve(false);
        }
      }
    }, 500);
  });
}

/**
 * Detener el servidor Python
 */
export function stopPythonBackend(): void {
  if (pythonProcess) {
    console.log('🛑 Deteniendo backend Python...');
    pythonProcess.kill();
    pythonProcess = null;
    isBackendReady = false;
  }
}

/**
 * Verificar si el backend está disponible
 */
export async function checkBackendHealth(): Promise<boolean> {
  try {
    const response = await axios.get(`${BACKEND_URL}/health`, { timeout: 2000 });
    return response.status === 200;
  } catch (error) {
    return false;
  }
}

/**
 * Obtener estado del backend
 */
export function getBackendStatus(): { isRunning: boolean; url: string } {
  return {
    isRunning: isBackendReady,
    url: BACKEND_URL
  };
}

// =============================================================================
// GENERADORES
// =============================================================================

/**
 * Generar idea de libro
 */
export async function generateIdea(prompt: string, genre: string = 'General'): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/idea`, {
      prompt,
      genre
    }, {
      timeout: 30000,
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error generando idea:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Generar personaje
 */
export async function generateCharacter(prompt: string): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/character`, {
      prompt
    }, {
      timeout: 30000,
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error generando personaje:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Generar sinopsis
 */
export async function generateSynopsis(
  prompt: string, 
  length: 'short' | 'medium' | 'long' = 'medium'
): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/synopsis`, {
      prompt,
      length
    }, {
      timeout: 30000,
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error generando sinopsis:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Generar diálogo
 */
export async function generateDialogue(
  prompt: string,
  characters: string[] = [],
  tone: string = 'natural'
): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/dialogue`, {
      prompt,
      characters,
      tone
    }, {
      timeout: 30000,
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error generando diálogo:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Sugerir títulos
 */
export async function generateTitles(prompt: string, count: number = 10): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/titles`, {
      prompt,
      count
    }, {
      timeout: 30000,
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error generando títulos:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Expandir capítulo usando plantilla_expansion.py
 */
export async function expandChapter(config: {
  chapter_num: number;
  tomo_num: number;
  outline?: string;
  pov?: string;
  tone?: string;
  target_words?: number;
  erotica_style?: string;
  include_references?: boolean;
}): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/chapter/expand`, config, {
      timeout: 120000, // 2 minutos para generación larga
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error expandiendo capítulo:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Generar capítulo con GENERADOR_MAESTRO_GROQ
 */
export async function generateMaestroChapter(
  chapter_num: number,
  prev_context?: string
): Promise<string> {
  try {
    const response = await axios.post(`${BACKEND_URL}/api/ai/book/generate-maestro`, {
      chapter_num,
      prev_context
    }, {
      timeout: 120000, // 2 minutos
      responseType: 'text'
    });
    
    return response.data;
  } catch (error: any) {
    console.error('Error con generador maestro:', error);
    throw new Error(error.response?.data?.error || error.message);
  }
}

/**
 * Generar receta (placeholder - agregar lógica específica)
 */
export async function generateRecipe(prompt: string): Promise<string> {
  // TODO: Implementar endpoint específico para recetas
  return generateIdea(prompt, 'Receta');
}

/**
 * Generar informe (placeholder - agregar lógica específica)
 */
export async function generateReport(prompt: string): Promise<string> {
  // TODO: Implementar endpoint específico para informes
  return generateIdea(prompt, 'Informe');
}
