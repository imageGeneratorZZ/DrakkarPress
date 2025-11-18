/**
 * Sistema de Autenticación y Gestión de Sesión - DrakkarPress
 * Compatible con config.js global (sin ES modules)
 */
(function(window) {
    'use strict';

    const TOKEN_KEY = 'drakkarpress_token';
    const REFRESH_TOKEN_KEY = 'drakkarpress_refresh_token';
    const USER_KEY = 'drakkarpress_user';

    function getApiBaseUrl() {
        return window.DrakkarPress?.config?.getApiBaseUrl() || 'https://overflowing-consideration-production.up.railway.app';
    }

    const DrakkarAuth = {
        /**
         * Obtener token del localStorage
         */
        getToken() {
            return localStorage.getItem(TOKEN_KEY);
        },

        /**
         * Guardar token en localStorage
         */
        saveToken(token, refreshToken) {
            localStorage.setItem(TOKEN_KEY, token);
            if (refreshToken) {
                localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
            }
        },

        /**
         * Eliminar token del localStorage
         */
        removeToken() {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(REFRESH_TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
        },

        /**
         * Verificar si el usuario está autenticado
         */
        isAuthenticated() {
            return !!this.getToken();
        },

        /**
         * Obtener datos del usuario actual desde el backend
         */
        async getCurrentUser() {
            const token = this.getToken();
            const API_BASE_URL = getApiBaseUrl();
            
            if (!token) {
                console.log('No hay token disponible');
                return null;
            }

            try {
                console.log('Cargando perfil de usuario desde:', `${API_BASE_URL}/api/auth/me`);
                
                const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                console.log('Respuesta del servidor:', response.status);

                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        console.warn('Token inválido o expirado');
                        this.removeToken();
                        return null;
                    }
                    const errorText = await response.text();
                    console.error('Error del servidor:', errorText);
                    throw new Error('Error al obtener datos del usuario');
                }

                const responseData = await response.json();
                console.log('Datos recibidos:', responseData);
                
                // La respuesta viene en formato ApiResponse<data>
                const user = responseData.data || responseData;
                
                // Guardar en localStorage para acceso rápido
                localStorage.setItem(USER_KEY, JSON.stringify(user));
                
                return user;
            } catch (error) {
                console.error('Error al cargar usuario:', error);
                return null;
            }
        },

        /**
         * Obtener usuario del localStorage (más rápido)
         */
        getCachedUser() {
            const userStr = localStorage.getItem(USER_KEY);
            if (!userStr) return null;
            
            try {
                return JSON.parse(userStr);
            } catch (e) {
                return null;
            }
        },

        /**
         * Cerrar sesión
         */
        async logout(redirectToLogin = true) {
            const token = this.getToken();
            const API_BASE_URL = getApiBaseUrl();
            
            if (token) {
                try {
                    await fetch(`${API_BASE_URL}/api/auth/logout`, {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                } catch (error) {
                    console.error('Error al cerrar sesión en el servidor:', error);
                }
            }
            
            this.removeToken();
            if (redirectToLogin) {
                window.location.href = 'login.html';
            }
        },

        /**
         * Redirigir a login si no está autenticado
         */
        requireAuth() {
            if (!this.isAuthenticated()) {
                const currentPath = window.location.pathname.split('/').pop() || 'index.html';
                // Evitar loops infinitos
                if (currentPath.includes('login.html')) {
                    return false;
                }
                window.location.href = 'login.html?redirect=' + encodeURIComponent(currentPath);
                return false;
            }
            return true;
        },

        /**
         * Fetch con autenticación automática
         */
        async authFetch(url, options = {}) {
            const token = this.getToken();
            if (!token) {
                throw new Error('No authentication token available');
            }

            const headers = {
                ...options.headers,
                'Authorization': `Bearer ${token}`
            };

            const response = await fetch(url, { ...options, headers });

            // Si es 401/403, limpiar sesión y redirigir
            if (response.status === 401 || response.status === 403) {
                console.warn('Auth token invalid or expired, logging out');
                this.logout();
                throw new Error('Session expired');
            }

            return response;
        },

        /**
         * Actualizar UI con información del usuario
         */
        async updateUserUI() {
            const user = await this.getCurrentUser();
            
            if (!user) {
                this.requireAuth();
                return null;
            }

            // Actualizar nombre en header
            const welcomeElement = document.querySelector('.welcome');
            if (welcomeElement) {
                const firstName = user.firstName || user.fullName?.split(' ')[0] || user.username;
                welcomeElement.textContent = `¡Hola, ${firstName}! 👋`;
            }

            // Actualizar user-info
            const userNameElements = document.querySelectorAll('[data-user-name]');
            userNameElements.forEach(el => {
                el.textContent = user.fullName || user.username;
            });

            const userEmailElements = document.querySelectorAll('[data-user-email]');
            userEmailElements.forEach(el => {
                el.textContent = user.email;
            });

            // Actualizar avatar
            const avatarElement = document.querySelector('.avatar');
            if (avatarElement) {
                const initials = this.getInitials(user.fullName || user.username);
                avatarElement.textContent = initials;
            }

            // Actualizar rol
            const roleElement = document.querySelector('[data-user-role]');
            if (roleElement) {
                roleElement.textContent = this.getRoleDisplayName(user.role);
            }

            // Mostrar userNumber si existe
            const userNumberElement = document.querySelector('[data-user-number]');
            if (userNumberElement && user.userNumber) {
                userNumberElement.textContent = `Usuario #${user.userNumber}`;
            }

            // Actualizar badge de membresía
            const membershipBadge = document.querySelector('[data-membership-badge]');
            if (membershipBadge && user.membership) {
                membershipBadge.textContent = this.getMembershipBadge(user.userNumber);
            }

            return user;
        },

        /**
         * Actualizar navbar dinámicamente según estado de sesión
         */
        updateNavbar() {
            const isAuth = this.isAuthenticated();
            const user = this.getCachedUser();

            // Buscar elementos del navbar
            const authLinks = document.querySelectorAll('[data-auth-required]');
            const guestLinks = document.querySelectorAll('[data-guest-only]');
            const userInfo = document.getElementById('navUserInfo');
            const logoutBtn = document.getElementById('navLogout');

            // Mostrar/ocultar según estado
            authLinks.forEach(el => {
                el.style.display = isAuth ? '' : 'none';
            });

            guestLinks.forEach(el => {
                el.style.display = isAuth ? 'none' : '';
            });

            // Mostrar info del usuario si existe elemento
            if (userInfo && user) {
                const displayName = user.displayName || user.username || user.email || 'Usuario';
                userInfo.textContent = displayName;
                userInfo.style.display = '';
            } else if (userInfo) {
                userInfo.style.display = 'none';
            }

            // Configurar botón de logout
            if (logoutBtn) {
                logoutBtn.style.display = isAuth ? '' : 'none';
                logoutBtn.onclick = (e) => {
                    e.preventDefault();
                    if (confirm('¿Cerrar sesión?')) {
                        this.logout();
                    }
                };
            }
        },

        /**
         * Agregar botón de logout
         */
        addLogoutButton() {
            const sidebar = document.querySelector('.sidebar .nav-menu');
            if (!sidebar) return;

            // Verificar si ya existe
            if (document.querySelector('[data-logout-button]')) return;

            const logoutItem = document.createElement('a');
            logoutItem.href = '#';
            logoutItem.className = 'nav-item';
            logoutItem.setAttribute('data-logout-button', 'true');
            logoutItem.style.marginTop = '20px';
            logoutItem.style.borderTop = '1px solid #E0E6ED';
            logoutItem.style.paddingTop = '20px';
            logoutItem.innerHTML = `
                <span class="icon" style="font-size:20px">🚪</span>
                <span>Cerrar Sesión</span>
            `;
            
            logoutItem.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
                    this.logout();
                }
            });

            sidebar.appendChild(logoutItem);
        },

        /**
         * Obtener iniciales del nombre
         */
        getInitials(name) {
            if (!name) return '??';
            
            const parts = name.trim().split(' ');
            if (parts.length >= 2) {
                return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
            }
            return name.substring(0, 2).toUpperCase();
        },

        /**
         * Obtener nombre legible del rol
         */
        getRoleDisplayName(role) {
            const roleNames = {
                'WRITER': 'Escritor',
                'PRINT_SHOP': 'Imprenta',
                'RESELLER': 'Revendedor',
                'ADMIN': 'Administrador'
            };
            return roleNames[role] || role;
        },

        /**
         * Obtener badge de membresía según userNumber
         */
        getMembershipBadge(userNumber) {
            if (!userNumber) return 'Free Member';
            
            if (userNumber <= 1000) {
                return '🏆 Fundador';
            } else if (userNumber <= 10000) {
                return '⭐ Early Adopter';
            } else if (userNumber <= 15000) {
                return '🚀 Launch Member';
            }
            return 'Free Member';
        },

        /**
         * Obtiene el plan de membresía del usuario
         */
        getMembershipPlan() {
            const user = this.getCachedUser();
            return user?.membershipPlan || 'FREE';
        },

        /**
         * Verifica si el usuario tiene plan premium
         */
        isPremium() {
            const plan = this.getMembershipPlan();
            return ['BASIC', 'PRO', 'ENTERPRISE'].includes(plan);
        },

        /**
         * Inicializar autenticación en la página
         */
        async initAuth() {
            // Verificar si está en página de login/register
            const pathname = window.location.pathname.toLowerCase();
            const isAuthPage = pathname.includes('login.html') || 
                               pathname.includes('register.html') ||
                               pathname.endsWith('login') ||
                               pathname.endsWith('register');
            
            if (isAuthPage) {
                // Si ya está autenticado, redirigir al dashboard
                if (this.isAuthenticated()) {
                    const user = this.getCachedUser();
                    if (user) {
                        window.location.href = 'escritores.html';
                    }
                }
                return;
            }

            // Actualizar navbar
            this.updateNavbar();

            // Si está en página protegida, requerir auth y cargar usuario
            const isProtectedPage = pathname.includes('escritores.html') || 
                                    pathname.includes('imprentas.html') ||
                                    pathname.includes('revendedores.html') ||
                                    pathname.includes('my-books.html');

            if (isProtectedPage) {
                if (!this.requireAuth()) return;
                await this.updateUserUI();
                this.addLogoutButton();
            }
        }
    };

    // Exponer globalmente
    window.DrakkarPress = window.DrakkarPress || {};
    window.DrakkarPress.auth = DrakkarAuth;

    // Alias para compatibilidad
    window.DrakkarAuth = DrakkarAuth;

    // Auto-inicializar al cargar
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => DrakkarAuth.initAuth());
    } else {
        DrakkarAuth.initAuth();
    }

})(window);
