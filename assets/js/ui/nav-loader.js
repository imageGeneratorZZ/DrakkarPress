/**
 * Navigation Component Loader
 * Carga el componente de navegación reutilizable en todas las páginas
 */

(async function loadNavigation() {
    const navPlaceholder = document.getElementById('nav-placeholder');
    if (!navPlaceholder) return;

    try {
        const response = await fetch('/assets/components/nav.html');
        if (!response.ok) throw new Error('Failed to load navigation');
        
        const html = await response.text();
        navPlaceholder.innerHTML = html;
        
        // Inicializar funcionalidad de navegación
        initializeNavigation();
        
        // Cargar estado de autenticación
        if (window.AuthState) {
            window.AuthState.init();
        }
    } catch (error) {
        console.error('Error loading navigation:', error);
        navPlaceholder.innerHTML = '<p>Error cargando navegación</p>';
    }
})();

function initializeNavigation() {
    // Mobile menu toggle
    const mobileToggle = document.querySelector('.mobile-menu-toggle');
    const navLinks = document.querySelector('.nav-links');
    
    if (mobileToggle && navLinks) {
        mobileToggle.addEventListener('click', () => {
            const isOpen = navLinks.classList.toggle('mobile-open');
            mobileToggle.setAttribute('aria-expanded', isOpen);
            
            // Animar hamburguesa
            const spans = mobileToggle.querySelectorAll('span');
            if (isOpen) {
                spans[0].style.transform = 'rotate(45deg) translateY(7px)';
                spans[1].style.opacity = '0';
                spans[2].style.transform = 'rotate(-45deg) translateY(-7px)';
            } else {
                spans.forEach(span => {
                    span.style.transform = '';
                    span.style.opacity = '';
                });
            }
        });
    }
    
    // Dropdown keyboard navigation
    const dropdowns = document.querySelectorAll('.dropdown');
    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        const content = dropdown.querySelector('.dropdown-content');
        
        if (toggle && content) {
            toggle.addEventListener('click', (e) => {
                e.preventDefault();
                const isExpanded = toggle.getAttribute('aria-expanded') === 'true';
                toggle.setAttribute('aria-expanded', !isExpanded);
                content.style.display = isExpanded ? 'none' : 'block';
            });
            
            // Cerrar dropdown al hacer clic fuera
            document.addEventListener('click', (e) => {
                if (!dropdown.contains(e.target)) {
                    toggle.setAttribute('aria-expanded', 'false');
                    content.style.display = 'none';
                }
            });
            
            // Navegación con teclado
            toggle.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    toggle.click();
                }
            });
        }
    });
}
