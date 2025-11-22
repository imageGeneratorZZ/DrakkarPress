// DrakkarPress API Client
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:12000/api'
    : '/api'; // En producción usa el proxy de Netlify

class DrakkarAPI {
    constructor() {
        this.token = localStorage.getItem('drakkar_token');
        this.user = JSON.parse(localStorage.getItem('drakkar_user') || 'null');
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
            
            if (data.success) {
                this.token = data.data.token;
                this.user = { id: data.data.userId, email };
                localStorage.setItem('drakkar_token', this.token);
                localStorage.setItem('drakkar_user', JSON.stringify(this.user));
                return { success: true, user: this.user };
            }
            
            return { success: false, message: data.message };
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
}

// Instancia global
const api = new DrakkarAPI();

// Exportar para uso en otros scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { DrakkarAPI, api };
}
