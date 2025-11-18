/**
 * Sistema de Autenticación y Gestión de Sesión
 * Carga perfil de usuario desde el backend
 */

// Importar configuración
import { API_BASE_URL } from './config.js';

/**
 * Obtener token del localStorage
 */
export function getToken() {
    return localStorage.getItem('drakkarpress_token');
}

/**
 * Guardar token en localStorage
 */
export function saveToken(token) {
    localStorage.setItem('drakkarpress_token', token);
}

/**
 * Eliminar token del localStorage
 */
export function removeToken() {
    localStorage.removeItem('drakkarpress_token');
    localStorage.removeItem('drakkarpress_user');
}

/**
 * Verificar si el usuario está autenticado
 */
export function isAuthenticated() {
    return !!getToken();
}

/**
 * Obtener datos del usuario actual desde el backend
 */
export async function getCurrentUser() {
    const token = getToken();
    
    if (!token) {
        return null;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                // Token inválido o expirado
                removeToken();
                return null;
            }
            throw new Error('Error al obtener datos del usuario');
        }

        const user = await response.json();
        
        // Guardar en localStorage para acceso rápido
        localStorage.setItem('drakkarpress_user', JSON.stringify(user));
        
        return user;
    } catch (error) {
        console.error('Error al cargar usuario:', error);
        return null;
    }
}

/**
 * Obtener usuario del localStorage (más rápido, pero puede estar desactualizado)
 */
export function getCachedUser() {
    const userStr = localStorage.getItem('drakkarpress_user');
    if (!userStr) return null;
    
    try {
        return JSON.parse(userStr);
    } catch (e) {
        return null;
    }
}

/**
 * Cerrar sesión
 */
export async function logout() {
    const token = getToken();
    
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
    
    removeToken();
    window.location.href = '/login.html';
}

/**
 * Redirigir a login si no está autenticado
 */
export function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
    }
}

/**
 * Actualizar UI con información del usuario
 */
export async function updateUserUI() {
    const user = await getCurrentUser();
    
    if (!user) {
        requireAuth();
        return;
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
        const initials = getInitials(user.fullName || user.username);
        avatarElement.textContent = initials;
    }

    // Actualizar rol
    const roleElement = document.querySelector('[data-user-role]');
    if (roleElement) {
        roleElement.textContent = getRoleDisplayName(user.role);
    }

    // Mostrar userNumber si existe
    const userNumberElement = document.querySelector('[data-user-number]');
    if (userNumberElement && user.userNumber) {
        userNumberElement.textContent = `Usuario #${user.userNumber}`;
    }

    // Actualizar badge de membresía
    const membershipBadge = document.querySelector('[data-membership-badge]');
    if (membershipBadge && user.membership) {
        membershipBadge.textContent = getMembershipBadge(user.userNumber);
    }

    return user;
}

/**
 * Obtener iniciales del nombre
 */
function getInitials(name) {
    if (!name) return '??';
    
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
}

/**
 * Obtener nombre legible del rol
 */
function getRoleDisplayName(role) {
    const roleNames = {
        'WRITER': 'Escritor',
        'PRINT_SHOP': 'Imprenta',
        'RESELLER': 'Revendedor',
        'ADMIN': 'Administrador'
    };
    return roleNames[role] || role;
}

/**
 * Obtener badge de membresía según userNumber
 */
function getMembershipBadge(userNumber) {
    if (!userNumber) return 'Free Member';
    
    if (userNumber <= 1000) {
        return '🏆 Fundador';
    } else if (userNumber <= 10000) {
        return '⭐ Early Adopter';
    } else if (userNumber <= 15000) {
        return '🚀 Launch Member';
    }
    return 'Free Member';
}

/**
 * Agregar botón de logout
 */
export function addLogoutButton() {
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
            logout();
        }
    });

    sidebar.appendChild(logoutItem);
}

/**
 * Inicializar autenticación en la página
 */
export async function initAuth() {
    // Verificar si está en página de login/register
    if (window.location.pathname.includes('login.html') || 
        window.location.pathname.includes('register.html')) {
        // Si ya está autenticado, redirigir al dashboard
        if (isAuthenticated()) {
            const user = getCachedUser();
            if (user) {
                redirectToDashboard(user.role);
            }
        }
        return;
    }

    // Requerir autenticación para páginas protegidas
    requireAuth();

    // Cargar y mostrar perfil del usuario
    await updateUserUI();

    // Agregar botón de logout
    addLogoutButton();
}

/**
 * Redirigir al dashboard según rol
 */
function redirectToDashboard(role) {
    const dashboards = {
        'WRITER': '/escritores.html',
        'PRINT_SHOP': '/imprentas.html',
        'RESELLER': '/revendedores.html',
        'ADMIN': '/escritores.html'
    };
    
    window.location.href = dashboards[role] || '/index.html';
}

// Inicializar automáticamente si el DOM está listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuth);
} else {
    initAuth();
}
