// DrakkarPress API Client
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:12000/api'
    : '/api'; // En producción usa el proxy de Netlify

class DrakkarAPI {
    constructor() {
        // Instrumentación para diagnosticar problemas de login en producción
        this.token = localStorage.getItem('drakkar_token');
        this.user = JSON.parse(localStorage.getItem('drakkar_user') || 'null');
        if (!this.token) {
            console.warn('[DrakkarAPI] No se encontró token en localStorage al iniciar.');
        } else {
            console.log('[DrakkarAPI] Token cargado correctamente');
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
                this.user = { id: data.data.userId, email, username };
                localStorage.setItem('drakkar_token', this.token);
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
                // El backend ahora devuelve username; si falta, derivar del email
                const username = data.data.username || (email.split('@')[0]);
                this.user = { id: data.data.userId, email, username }; 
                localStorage.setItem('drakkar_token', this.token);
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
        this.user = null;
        localStorage.removeItem('drakkar_token');
        localStorage.removeItem('drakkar_user');
    }

    // Verificar si está autenticado
    isAuthenticated() {
        return !!this.token;
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
                this.user = { id: data.data.userId, email: data.data.provider + '@social', username: data.data.username };
                localStorage.setItem('drakkar_token', this.token);
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
}

// Instancia global
const api = new DrakkarAPI();

// Reintento pasivo por si el navegador tarda en persistir localStorage tras el redirect
setTimeout(() => {
    if (!api.token && localStorage.getItem('drakkar_token')) {
        api.token = localStorage.getItem('drakkar_token');
        api.user = JSON.parse(localStorage.getItem('drakkar_user') || 'null');
        console.log('[DrakkarAPI] Token recuperado en reintento tardío');
    }
}, 500);

// Exportar para uso en otros scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { DrakkarAPI, api };
}
