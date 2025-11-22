/**
 * Authentication State Manager
 * Gestiona el estado de autenticación y actualiza UI según sesión
 */

window.AuthState = (function() {
    const API_BASE_URL = window.API_BASE_URL || 'http://localhost:8080';
    
    function getToken() {
        return localStorage.getItem('authToken');
    }
    
    function getUserData() {
        const userStr = localStorage.getItem('userData');
        return userStr ? JSON.parse(userStr) : null;
    }
    
    function isAuthenticated() {
        return !!getToken();
    }
    
    function logout() {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userData');
        window.location.href = '/login.html';
    }
    
    async function refreshUserData() {
        const token = getToken();
        if (!token) return null;
        
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (response.ok) {
                const userData = await response.json();
                localStorage.setItem('userData', JSON.stringify(userData));
                return userData;
            } else if (response.status === 401) {
                logout();
            }
        } catch (error) {
            console.error('Error fetching user data:', error);
        }
        
        return null;
    }
    
    function updateAuthButtons() {
        const authButtons = document.getElementById('authButtons');
        if (!authButtons) return;
        
        if (isAuthenticated()) {
            const user = getUserData();
            const username = user?.username || 'Usuario';
            const isPremium = user?.isPremium || false;
            
            authButtons.innerHTML = `
                <div class="flex items-center gap-sm">
                    ${isPremium ? '<span class="badge badge-premium">Premium</span>' : ''}
                    <div class="dropdown">
                        <button class="nav-link dropdown-toggle" aria-expanded="false" aria-haspopup="true">
                            <span aria-hidden="true">👤</span> ${username}
                        </button>
                        <div class="dropdown-content" role="menu">
                            <a href="my-books.html" role="menuitem">📚 Mi Biblioteca</a>
                            <a href="profile.html" role="menuitem">⚙️ Mi Perfil</a>
                            ${!isPremium ? '<a href="#" id="upgradePremium" role="menuitem">⭐ Mejorar a Premium</a>' : ''}
                            <a href="#" id="logoutBtn" role="menuitem">🚪 Cerrar Sesión</a>
                        </div>
                    </div>
                </div>
            `;
            
            // Event listeners
            const logoutBtn = document.getElementById('logoutBtn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    logout();
                });
            }
            
            const upgradePremium = document.getElementById('upgradePremium');
            if (upgradePremium) {
                upgradePremium.addEventListener('click', (e) => {
                    e.preventDefault();
                    if (window.PremiumCTA) {
                        window.PremiumCTA.showUpgradeModal();
                    }
                });
            }
        } else {
            authButtons.innerHTML = `
                <a href="register.html" class="btn btn-outline btn-sm text-white">Registrarse</a>
                <a href="login.html" class="btn btn-secondary btn-sm">Iniciar Sesión</a>
            `;
        }
    }
    
    function init() {
        updateAuthButtons();
        
        // Refrescar datos de usuario si está autenticado
        if (isAuthenticated()) {
            refreshUserData().then(updateAuthButtons);
        }
    }
    
    return {
        init,
        isAuthenticated,
        getToken,
        getUserData,
        logout,
        refreshUserData,
        updateAuthButtons
    };
})();
