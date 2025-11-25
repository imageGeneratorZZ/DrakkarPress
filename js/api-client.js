// DrakkarPress API Client
// Nueva lógica de selección de base URL:
// 1. Localhost -> backend local puerto 12000
// 2. Si existe subdominio api.drakkarpress.com configurado -> usarlo directamente
// 3. Si estamos en drakkarpress.com y NO existe api.* aún -> intentar /api (proxy)
// 4. Fallback final: relative /api
const host = window.location.hostname;
// Permitir override por query (?apiBase=http://...)
const urlParams = new URLSearchParams(window.location.search);
const queryOverride = urlParams.get('apiBase');
// Permitir override persistente por localStorage
const storedOverride = localStorage.getItem('drakkar_api_base');

let API_BASE_URL;
if (queryOverride) {
    API_BASE_URL = queryOverride;
    localStorage.setItem('drakkar_api_base', queryOverride);
    console.info('[DrakkarAPI] Usando apiBase override por query:', API_BASE_URL);
} else if (storedOverride) {
    API_BASE_URL = storedOverride;
    console.info('[DrakkarAPI] Usando apiBase override persistente:', API_BASE_URL);
} else if (host === 'localhost' || host === '127.0.0.1') {
    API_BASE_URL = 'http://localhost:12000/api';
} else if (host === 'drakkarpress.com' || host === 'www.drakkarpress.com') {
    // Usar Railway backend directamente hasta que api subdomain esté configurado
    API_BASE_URL = 'https://overflowing-consideration-production.up.railway.app/api';
} else if (host === 'api.drakkarpress.com') {
    API_BASE_URL = 'https://api.drakkarpress.com';
} else {
    API_BASE_URL = '/api';
}

class DrakkarAPI {
    constructor() {
        // Instrumentación para diagnosticar problemas de login en producción
        this.token = localStorage.getItem('drakkar_token');
        this.refreshToken = localStorage.getItem('drakkar_refresh');
        this.user = JSON.parse(localStorage.getItem('drakkar_user') || 'null');
        if (!this.token) {
            console.warn('[DrakkarAPI] No se encontró token en localStorage al iniciar.');
        } else {
            console.log('[DrakkarAPI] Token cargado correctamente');
        }
        if (!this.refreshToken) {
            console.warn('[DrakkarAPI] No se encontró refresh token en localStorage.');
        }
        if (!this.user) {
            console.warn('[DrakkarAPI] No hay objeto usuario en localStorage.');
        } else {
            console.log('[DrakkarAPI] Usuario cargado:', this.user);
        }
    }

    // Headers con autenticación
    getHeaders(includeAuth = true) {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (includeAuth && this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    // Registro de usuario
    async register(email, username, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify({ email, username, password })
            });

            const data = await response.json();
            
            if (data.success) {
                this.token = data.data.token;
                this.refreshToken = data.data.refreshToken;
                this.user = { id: data.data.userId, email, username };
                localStorage.setItem('drakkar_token', this.token);
                localStorage.setItem('drakkar_refresh', this.refreshToken);
                localStorage.setItem('drakkar_user', JSON.stringify(this.user));
                return { success: true, user: this.user };
            }
            
            return { success: false, message: data.message };
        } catch (error) {
            console.error('Error en registro:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Login
    async login(email, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta login:', data);

            if (data.success && data.data) {
                this.token = data.data.token;
                this.refreshToken = data.data.refreshToken;
                // El backend ahora devuelve username; si falta, derivar del email
                const username = data.data.username || (email.split('@')[0]);
                this.user = { id: data.data.userId, email, username }; 
                localStorage.setItem('drakkar_token', this.token);
                localStorage.setItem('drakkar_refresh', this.refreshToken);
                localStorage.setItem('drakkar_user', JSON.stringify(this.user));
                console.log('[DrakkarAPI] Login OK. Token y usuario almacenados');
                return { success: true, user: this.user };
            }

            console.warn('[DrakkarAPI] Login falló:', data.message);
            return { success: false, message: data.message || 'Error desconocido' };
        } catch (error) {
            console.error('Error en login:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Logout
    logout() {
        this.token = null;
        this.refreshToken = null;
        this.user = null;
        localStorage.removeItem('drakkar_token');
        localStorage.removeItem('drakkar_refresh');
        localStorage.removeItem('drakkar_user');
    }

    // Verificar si está autenticado
    isAuthenticated() {
        return !!this.token;
    }

    // Renovar access & refresh token
    async refresh() {
        if (!this.refreshToken) return { success: false, message: 'No hay refresh token' };
        try {
            const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify({ refreshToken: this.refreshToken })
            });
            const data = await response.json();
            if (data.success && data.data) {
                this.token = data.data.token;
                if (data.data.refreshToken) {
                    this.refreshToken = data.data.refreshToken;
                    localStorage.setItem('drakkar_refresh', this.refreshToken);
                }
                localStorage.setItem('drakkar_token', this.token);
                return { success: true, token: this.token };
            }
            return { success: false, message: data.message || 'Fallo refresh' };
        } catch (e) {
            return { success: false, message: e.message };
        }
    }

    // Obtener usuario actual
    getCurrentUser() {
        return this.user;
    }

    // Obtener libros (ejemplo)
    async getBooks(limit = 10) {
        try {
            const response = await fetch(`${API_BASE_URL}/books?limit=${limit}`, {
                headers: this.getHeaders()
            });
            return await response.json();
        } catch (error) {
            console.error('Error obteniendo libros:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Permite inspeccionar rápidamente qué base URL está usando el frontend
    getApiBaseUrl() {
        return API_BASE_URL;
    }

    // Health check adaptativo según base URL
    async getHealth() {
        const base = API_BASE_URL;
        const path = base.endsWith('/api') ? '/health' : '/api/health';
        try {
            const response = await fetch(`${base}${path}`, { headers: { 'Accept': 'application/json' } });
            const ct = response.headers.get('content-type') || '';
            if (ct.includes('application/json')) {
                return { ok: response.ok, json: await response.json(), status: response.status };
            }
            const text = await response.text();
            return { ok: false, htmlSnapshot: text.substring(0, 200), status: response.status };
        } catch (e) {
            return { ok: false, error: e.message };
        }
    }

    // Social login demo (Google/Facebook)
    async socialLogin(provider) {
        try {
            const externalToken = 'demo-' + Date.now();
            const response = await fetch(`${API_BASE_URL}/auth/social`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify({ provider, externalToken })
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta social login:', data);
            if (data.success && data.data) {
                this.token = data.data.token;
                this.refreshToken = data.data.refreshToken;
                this.user = { id: data.data.userId, email: data.data.provider + '@social', username: data.data.username };
                localStorage.setItem('drakkar_token', this.token);
                localStorage.setItem('drakkar_refresh', this.refreshToken);
                localStorage.setItem('drakkar_user', JSON.stringify(this.user));
                return { success: true, user: this.user };
            }
            return { success: false, message: data.message || 'Error social login' };
        } catch (e) {
            console.error('Error social login:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Obtener perfil propio
    async getMyProfile() {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/profile/me`, { headers: this.getHeaders(true) });
            const data = await response.json();
            return data;
        } catch (e) {
            console.error('Error obteniendo perfil:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Actualizar perfil
    async updateMyProfile(updates) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/profile/me`, {
                method: 'PUT',
                headers: this.getHeaders(true),
                body: JSON.stringify(updates)
            });
            const data = await response.json();
            if (data.success) {
                // Sincronizar username si cambió fullName (no se actualiza username todavía en backend)
                const stored = JSON.parse(localStorage.getItem('drakkar_user') || 'null');
                if (stored) {
                    stored.fullName = data.data.fullName;
                    stored.bio = data.data.bio;
                    localStorage.setItem('drakkar_user', JSON.stringify(stored));
                    this.user = stored;
                }
            }
            return data;
        } catch (e) {
            console.error('Error actualizando perfil:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Crear checkout (nuevo)
    async createCheckout(planType, frequency) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/payments/create-checkout`, {
                method: 'POST',
                headers: this.getHeaders(true),
                body: JSON.stringify({ planType, frequency })
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta crear checkout:', data);
            return data;
        } catch (e) {
            console.error('Error creando checkout:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Crear proyecto (nuevo)
    async createProject(title, genre, style, synopsis, chapters) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects`, {
                method: 'POST',
                headers: this.getHeaders(true),
                body: JSON.stringify({ title, genre, style, synopsis, chapters })
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta crear proyecto:', data);
            return data;
        } catch (e) {
            console.error('Error creando proyecto:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Generar esquema de proyecto (nuevo)
    async generateProjectOutline(projectId) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/outline`, {
                method: 'POST',
                headers: this.getHeaders(true)
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta generar esquema:', data);
            return data;
        } catch (e) {
            console.error('Error generando esquema de proyecto:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Generar capítulo (nuevo)
    async generateChapter(projectId, chapterId) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/chapters/${chapterId}/generate`, {
                method: 'POST',
                headers: this.getHeaders(true),
                body: JSON.stringify({}) // Body vacío como en el ejemplo de PowerShell
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta generar capítulo:', data);
            return data;
        } catch (e) {
            console.error('Error generando capítulo:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Regenerar capítulo manteniendo coherencia
    async regenerateChapter(projectId, chapterOrder) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/chapters/${chapterOrder}/regenerate`, {
                method: 'POST',
                headers: this.getHeaders(true)
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta regenerar capítulo:', data);
            return data;
        } catch (e) {
            console.error('Error regenerando capítulo:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Regenerar capítulos en cascada
    async regenerateCascade(projectId, fromChapterOrder) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/chapters/${fromChapterOrder}/regenerate-cascade`, {
                method: 'POST',
                headers: this.getHeaders(true)
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta regenerar cascada:', data);
            return data;
        } catch (e) {
            console.error('Error regenerando cascada:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Generar libro completo automáticamente
    async generateCompleteBook(projectId) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/generate-complete`, {
                method: 'POST',
                headers: this.getHeaders(true)
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta generar libro completo:', data);
            return data;
        } catch (e) {
            console.error('Error generando libro completo:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Continuar capítulo
    async continueChapter(projectId, chapterOrder, currentContent) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/chapters/${chapterOrder}/continue`, {
                method: 'POST',
                headers: this.getHeaders(true),
                body: JSON.stringify({ currentContent })
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta continuar capítulo:', data);
            return data;
        } catch (e) {
            console.error('Error continuando capítulo:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Actualizar/editar capítulo
    async updateChapter(projectId, chapterOrder, content) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/chapters/${chapterOrder}`, {
                method: 'PUT',
                headers: this.getHeaders(true),
                body: JSON.stringify({ content })
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta actualizar capítulo:', data);
            return data;
        } catch (e) {
            console.error('Error actualizando capítulo:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Exportar libro
    async exportBook(projectId) {
        if (!this.isAuthenticated()) return { success: false, message: 'No autenticado' };
        try {
            const response = await fetch(`${API_BASE_URL}/generator/projects/${projectId}/export`, {
                method: 'GET',
                headers: this.getHeaders(true)
            });
            const data = await response.json();
            console.log('[DrakkarAPI] Respuesta exportar libro:', data);
            return data;
        } catch (e) {
            console.error('Error exportando libro:', e);
            return { success: false, message: 'Error de conexión' };
        }
    }

    // Generar outline (alias para compatibilidad)
    async generateOutline(projectId) {
        return this.generateProjectOutline(projectId);
    }
}
// Instancia por defecto para conveniencia en frontend
const api = new DrakkarAPI();
if (typeof window !== 'undefined') {
    window.DrakkarAPI = DrakkarAPI;
    window.drakkarApi = api;
    // Exponer utilidades para cambiar base sin recargar toda la app
    window.setApiBase = (base) => {
        localStorage.setItem('drakkar_api_base', base);
        console.info('[DrakkarAPI] Nuevo apiBase guardado. Recarga para aplicar:', base);
    };
    setApiBase('http://localhost:12000/api'); location.reload();
}

// Export CommonJS (Node) si aplica
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { DrakkarAPI, api };
}
