/**
 * Interactive Tabs Component
 * Sistema de tabs reutilizable con accesibilidad
 */

window.Tabs = (function() {
    function init(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        
        const tabs = container.querySelectorAll('[data-tab]');
        const contents = container.querySelectorAll('[data-tab-content]');
        
        function switchTab(tabId) {
            // Ocultar todos los contenidos
            contents.forEach(content => {
                content.classList.remove('active');
                content.setAttribute('aria-hidden', 'true');
            });
            
            // Desactivar todos los tabs
            tabs.forEach(tab => {
                tab.classList.remove('active');
                tab.setAttribute('aria-selected', 'false');
                tab.setAttribute('tabindex', '-1');
            });
            
            // Activar tab seleccionado
            const activeTab = container.querySelector(`[data-tab="${tabId}"]`);
            const activeContent = container.querySelector(`[data-tab-content="${tabId}"]`);
            
            if (activeTab && activeContent) {
                activeTab.classList.add('active');
                activeTab.setAttribute('aria-selected', 'true');
                activeTab.setAttribute('tabindex', '0');
                
                activeContent.classList.add('active');
                activeContent.setAttribute('aria-hidden', 'false');
                
                // Focus en el tab activo
                activeTab.focus();
            }
        }
        
        // Click handlers
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const tabId = tab.dataset.tab;
                switchTab(tabId);
            });
            
            // Keyboard navigation
            tab.addEventListener('keydown', (e) => {
                const currentIndex = Array.from(tabs).indexOf(tab);
                let newIndex;
                
                switch(e.key) {
                    case 'ArrowRight':
                    case 'ArrowDown':
                        e.preventDefault();
                        newIndex = (currentIndex + 1) % tabs.length;
                        tabs[newIndex].click();
                        break;
                    case 'ArrowLeft':
                    case 'ArrowUp':
                        e.preventDefault();
                        newIndex = (currentIndex - 1 + tabs.length) % tabs.length;
                        tabs[newIndex].click();
                        break;
                    case 'Home':
                        e.preventDefault();
                        tabs[0].click();
                        break;
                    case 'End':
                        e.preventDefault();
                        tabs[tabs.length - 1].click();
                        break;
                }
            });
        });
        
        // Activar primer tab por defecto
        if (tabs.length > 0) {
            const firstTab = tabs[0].dataset.tab;
            switchTab(firstTab);
        }
    }
    
    return { init };
})();
