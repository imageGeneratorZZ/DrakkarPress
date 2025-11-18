/**
 * Sistema Global de Manejo de Errores - DrakkarPress
 * Intercepta errores de red, muestra mensajes amigables, y maneja relogin automático
 */
(function(window) {
    'use strict';

    const ErrorHandler = {
        /**
         * Mostrar notificación toast
         */
        showToast(message, type = 'info') {
            // Crear contenedor si no existe
            let container = document.getElementById('toastContainer');
            if (!container) {
                container = document.createElement('div');
                container.id = 'toastContainer';
                container.style.cssText = `
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 10000;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                `;
                document.body.appendChild(container);
            }

            // Crear toast
            const toast = document.createElement('div');
            const colors = {
                success: '#10b981',
                error: '#ef4444',
                warning: '#f59e0b',
                info: '#3b82f6'
            };
            
            toast.style.cssText = `
                background: ${colors[type] || colors.info};
                color: white;
                padding: 16px 20px;
                border-radius: 12px;
                box-shadow: 0 10px 25px rgba(0,0,0,0.2);
                min-width: 300px;
                max-width: 500px;
                font-weight: 600;
                animation: slideIn 0.3s ease-out;
                cursor: pointer;
            `;
            
            const icons = {
                success: '✅',
                error: '❌',
                warning: '⚠️',
                info: 'ℹ️'
            };
            
            toast.innerHTML = `${icons[type] || icons.info} ${message}`;
            
            // Auto-remover después de 5 segundos
            setTimeout(() => {
                toast.style.animation = 'slideOut 0.3s ease-in';
                setTimeout(() => toast.remove(), 300);
            }, 5000);
            
            // Remover al hacer click
            toast.onclick = () => {
                toast.style.animation = 'slideOut 0.3s ease-in';
                setTimeout(() => toast.remove(), 300);
            };
            
            container.appendChild(toast);

            // Agregar animaciones CSS si no existen
            if (!document.getElementById('toastAnimations')) {
                const style = document.createElement('style');
                style.id = 'toastAnimations';
                style.textContent = `
                    @keyframes slideIn {
                        from { transform: translateX(400px); opacity: 0; }
                        to { transform: translateX(0); opacity: 1; }
                    }
                    @keyframes slideOut {
                        from { transform: translateX(0); opacity: 1; }
                        to { transform: translateX(400px); opacity: 0; }
                    }
                `;
                document.head.appendChild(style);
            }
        },

        /**
         * Manejo de errores de fetch
         */
        async handleFetchError(response, defaultMessage = 'Error en la petición') {
            if (response.status === 401 || response.status === 403) {
                this.showToast('Sesión expirada. Por favor, inicia sesión nuevamente.', 'error');
                
                // Limpiar sesión y redirigir a login
                if (window.DrakkarPress?.auth) {
                    window.DrakkarPress.auth.logout();
                }
                return;
            }

            try {
                const json = await response.json();
                const message = json.message || json.error || defaultMessage;
                this.showToast(message, 'error');
                return json;
            } catch (e) {
                this.showToast(defaultMessage, 'error');
                return null;
            }
        },

        /**
         * Manejo de errores de red
         */
        handleNetworkError(error, context = '') {
            console.error('Network error:', error);
            
            if (error.message === 'Failed to fetch' || error.message === 'NetworkError') {
                this.showToast('Error de conexión. Verifica tu internet.', 'error');
            } else if (error.message === 'Session expired') {
                this.showToast('Sesión expirada', 'error');
            } else {
                const message = context 
                    ? `Error en ${context}: ${error.message}` 
                    : error.message;
                this.showToast(message, 'error');
            }
        },

        /**
         * Wrapper de fetch con manejo de errores automático
         */
        async safeFetch(url, options = {}, context = '') {
            try {
                const response = await fetch(url, options);
                
                if (!response.ok) {
                    await this.handleFetchError(response, `Error ${response.status}`);
                    throw new Error(`HTTP ${response.status}`);
                }
                
                return response;
            } catch (error) {
                this.handleNetworkError(error, context);
                throw error;
            }
        },

        /**
         * Mostrar indicador de carga
         */
        showLoading(element, text = 'Cargando...') {
            if (!element) return;
            
            element.dataset.originalContent = element.innerHTML;
            element.dataset.originalDisabled = element.disabled;
            element.disabled = true;
            element.innerHTML = `<span style="opacity: 0.7;">⏳ ${text}</span>`;
        },

        /**
         * Ocultar indicador de carga
         */
        hideLoading(element) {
            if (!element || !element.dataset.originalContent) return;
            
            element.innerHTML = element.dataset.originalContent;
            element.disabled = element.dataset.originalDisabled === 'true';
            delete element.dataset.originalContent;
            delete element.dataset.originalDisabled;
        },

        /**
         * Validar formulario antes de enviar
         */
        validateForm(form) {
            const inputs = form.querySelectorAll('[required]');
            let valid = true;
            
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    input.style.borderColor = '#ef4444';
                    valid = false;
                } else {
                    input.style.borderColor = '';
                }
            });
            
            if (!valid) {
                this.showToast('Por favor, completa todos los campos requeridos', 'warning');
            }
            
            return valid;
        },

        /**
         * Manejo global de errores no capturados
         */
        initGlobalHandlers() {
            // Errores de JavaScript
            window.addEventListener('error', (event) => {
                console.error('Global error:', event.error);
                this.showToast('Ha ocurrido un error inesperado', 'error');
            });

            // Promesas rechazadas sin catch
            window.addEventListener('unhandledrejection', (event) => {
                console.error('Unhandled rejection:', event.reason);
                if (event.reason?.message !== 'Session expired') {
                    this.showToast('Error en operación asíncrona', 'error');
                }
            });
        }
    };

    // Exponer globalmente
    window.DrakkarPress = window.DrakkarPress || {};
    window.DrakkarPress.errors = ErrorHandler;

    // Alias
    window.showToast = ErrorHandler.showToast.bind(ErrorHandler);
    window.safeFetch = ErrorHandler.safeFetch.bind(ErrorHandler);

    // Inicializar handlers globales
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => ErrorHandler.initGlobalHandlers());
    } else {
        ErrorHandler.initGlobalHandlers();
    }

})(window);
