/**
 * Premium CTA Handler
 * Gestiona el flujo de upgrade a Premium con checkout Shopify
 */

window.PremiumCTA = (function() {
    const API_BASE_URL = window.API_BASE_URL || 'http://localhost:8080';
    
    function showLoginModal() {
        // Modal simple para indicar que debe iniciar sesión
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content card" role="dialog" aria-labelledby="loginModalTitle">
                <h3 id="loginModalTitle" class="text-2xl font-bold mb-md text-primary">
                    🔐 Inicia Sesión para Continuar
                </h3>
                <p class="mb-lg text-medium">
                    Para actualizar a Premium necesitas crear una cuenta o iniciar sesión.
                </p>
                <div class="flex gap-md justify-center">
                    <a href="register.html" class="btn btn-secondary">Registrarse</a>
                    <a href="login.html" class="btn btn-primary">Iniciar Sesión</a>
                    <button id="closeModal" class="btn btn-outline">Cancelar</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        document.getElementById('closeModal').addEventListener('click', () => {
            modal.remove();
        });
        
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.remove();
        });
    }
    
    function showPlanSelectionModal() {
        const user = window.AuthState?.getUserData();
        const userNumber = user?.userNumber || 999999;
        
        // Determinar fase y precio
        let phase, monthlyPrice, annualPrice, badge;
        if (userNumber <= 1000) {
            phase = 'PREMIUM_PHASE_1';
            monthlyPrice = 5;
            annualPrice = 50;
            badge = 'Fundador';
        } else if (userNumber <= 10000) {
            phase = 'PREMIUM_PHASE_2';
            monthlyPrice = 10;
            annualPrice = 100;
            badge = 'Early Adopter';
        } else {
            phase = 'PREMIUM_PHASE_3';
            monthlyPrice = 19.99;
            annualPrice = 199;
            badge = 'Premium';
        }
        
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content card" role="dialog" aria-labelledby="planModalTitle" style="max-width: 600px;">
                <h3 id="planModalTitle" class="text-3xl font-bold mb-md text-primary text-center">
                    ⭐ Mejora a Premium
                </h3>
                <div class="badge badge-${badge.toLowerCase().replace(' ', '-')} mb-lg" style="display:block;text-align:center;">
                    ${badge} #${userNumber}
                </div>
                <p class="text-center text-lg mb-xl text-medium">
                    Tu precio está <strong>bloqueado de por vida</strong>. 
                    Nunca pagarás más que estos precios.
                </p>
                
                <div class="grid grid-2 gap-lg mb-xl">
                    <div class="plan-option card" data-frequency="MONTHLY">
                        <h4 class="text-xl font-semibold mb-sm">💳 Mensual</h4>
                        <p class="text-4xl font-bold text-primary mb-md">$${monthlyPrice}</p>
                        <p class="text-sm text-light">por mes</p>
                        <ul class="mt-md text-sm text-left" style="list-style:none;padding:0;">
                            <li>✓ Generadores IA ilimitados</li>
                            <li>✓ Biblioteca personal</li>
                            <li>✓ Soporte prioritario</li>
                            <li>✓ Badge exclusivo</li>
                        </ul>
                        <button class="btn btn-primary btn-lg mt-lg w-full" data-plan="${phase}" data-frequency="MONTHLY">
                            Elegir Mensual
                        </button>
                    </div>
                    
                    <div class="plan-option card" data-frequency="ANNUAL" style="border: 3px solid var(--secondary);">
                        <div class="badge badge-founder mb-sm">Ahorra ${Math.round((1 - annualPrice/(monthlyPrice*12)) * 100)}%</div>
                        <h4 class="text-xl font-semibold mb-sm">💰 Anual</h4>
                        <p class="text-4xl font-bold text-primary mb-md">$${annualPrice}</p>
                        <p class="text-sm text-light">por año</p>
                        <ul class="mt-md text-sm text-left" style="list-style:none;padding:0;">
                            <li>✓ Todo lo de Mensual</li>
                            <li>✓ 2 meses gratis</li>
                            <li>✓ Badge dorado</li>
                            <li>✓ Acceso beta features</li>
                        </ul>
                        <button class="btn btn-secondary btn-lg mt-lg w-full" data-plan="${phase}" data-frequency="ANNUAL">
                            Elegir Anual (Recomendado)
                        </button>
                    </div>
                </div>
                
                <p class="text-center text-sm text-light mb-md">
                    ✅ Cancela cuando quieras • 🔒 Pago seguro con Shopify
                </p>
                
                <button id="closePlanModal" class="btn btn-outline btn-sm">Cancelar</button>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Event listeners para botones de plan
        modal.querySelectorAll('[data-plan]').forEach(btn => {
            btn.addEventListener('click', async () => {
                const planType = btn.dataset.plan;
                const frequency = btn.dataset.frequency;
                await startCheckout(planType, frequency);
            });
        });
        
        document.getElementById('closePlanModal').addEventListener('click', () => {
            modal.remove();
        });
        
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.remove();
        });
    }
    
    async function startCheckout(planType, frequency) {
        const token = window.AuthState?.getToken();
        if (!token) {
            showLoginModal();
            return;
        }
        
        try {
            // Mostrar loading
            const loadingModal = showLoadingModal();
            
            const response = await fetch(`${API_BASE_URL}/api/payments/create-checkout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ planType, frequency })
            });
            
            if (!response.ok) {
                throw new Error('Error creating checkout session');
            }
            
            const data = await response.json();
            const checkoutUrl = data.data?.checkoutUrl;
            
            if (checkoutUrl) {
                // Redirigir a Shopify checkout
                window.location.href = checkoutUrl;
            } else {
                throw new Error('No checkout URL received');
            }
        } catch (error) {
            console.error('Checkout error:', error);
            alert('Error al iniciar el proceso de pago. Por favor intenta nuevamente.');
            document.querySelector('.modal-overlay')?.remove();
        }
    }
    
    function showLoadingModal() {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content card text-center">
                <div class="spinner mb-md"></div>
                <p class="text-lg">Preparando tu checkout seguro...</p>
            </div>
        `;
        document.body.appendChild(modal);
        return modal;
    }
    
    function showUpgradeModal() {
        if (!window.AuthState?.isAuthenticated()) {
            showLoginModal();
        } else {
            showPlanSelectionModal();
        }
    }
    
    function init() {
        // Vincular todos los botones CTA de Premium
        document.querySelectorAll('[data-premium-cta]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                showUpgradeModal();
            });
        });
    }
    
    return {
        init,
        showUpgradeModal,
        startCheckout
    };
})();

// Auto-inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => window.PremiumCTA.init());
} else {
    window.PremiumCTA.init();
}
