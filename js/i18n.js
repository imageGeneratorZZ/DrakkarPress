// i18n.js - Sistema de Internacionalización para DrakkarPress

const translations = {
    es: {
        // Navegación
        'nav.readers': 'Lectores',
        'nav.writers': 'Escritores',
        'nav.editorials': 'o Editoriales',
        'nav.resellers': 'Revendedores',
        'nav.printers': 'Red de Imprentas',
        'nav.printers.sub': '¿Quieres Imprimir?',
        'nav.marketing': 'Marketing',
        'nav.marketing.sub': 'Sinérgico',
        'nav.ai': 'IA',
        'nav.login': 'Iniciar Sesión',
        
        // Hero
        'hero.title': 'La Flota Editorial Digital',
        'hero.subtitle': 'Donde tus libros navegan al mundo',
        'hero.search.placeholder': '🔍 Buscar libros, autores, ISBN...',
        'hero.search.button': 'Buscar',
        'hero.filter.category': 'Categoría',
        'hero.filter.format': 'Formato',
        'hero.filter.price': 'Precio',
        'hero.filter.more': 'Más',
        'hero.stats': '📊 {books} libros | {authors} autores | {printers} imprentas | {countries} países',
        
        // Marketing Section
        'marketing.hero.title': 'Marketing Sinérgico',
        'marketing.hero.subtitle': 'Potenciamos tu éxito con <strong>campañas bidireccionales</strong>: desde DrakkarPress hacia tu negocio y desde tu sitio hacia nuestra red. <strong>Todos ganan más tráfico y ventas.</strong>',
        'marketing.hero.cta': 'Ver Estrategias Completas →',
        'marketing.title': 'Campañas de Marketing Sinérgicas',
        'marketing.subtitle': 'Potenciamos el éxito de todos: escritores, revendedores e imprentas',
        
        // Roles
        'roles.title': '¿Cómo quieres participar en DrakkarPress?',
        'roles.writer.title': 'Soy Escritor',
        'roles.writer.subtitle': 'Crea y publica tus libros',
        'roles.writer.publish': 'Publica gratis',
        'roles.writer.ai': 'IA te ayuda a crear',
        'roles.writer.royalty': '90% directo / 60% con revendedor',
        'roles.writer.inventory': 'Sin inventario',
        'roles.writer.automatic': 'Venta automática',
        'roles.writer.cta': 'Publicar mi libro →',
        
        'roles.reseller.title': 'Quiero Revender',
        'roles.reseller.subtitle': 'Vende libros y gana comisiones',
        'roles.reseller.investment': 'Sin inversión inicial',
        'roles.reseller.catalog': 'Elige tu catálogo',
        'roles.reseller.commission': '30% de comisión',
        'roles.reseller.tracking': 'Enlaces con tracking',
        'roles.reseller.ai': 'IA genera marketing',
        'roles.reseller.cta': 'Ser afiliado →',
        
        'roles.printer.title': 'Tengo Imprenta',
        'roles.printer.subtitle': 'Imprime libros bajo demanda',
        'roles.printer.orders': 'Pedidos automáticos',
        'roles.printer.flow': 'Flujo constante',
        'roles.printer.payments': 'Pagos puntuales',
        'roles.printer.panel': 'Panel de gestión',
        'roles.printer.risk': 'Sin riesgo',
        'roles.printer.cta': 'Unirme a la red →',
        
        'roles.reader.title': 'Quiero Comprar',
        'roles.reader.subtitle': 'Descubre tu próxima lectura',
        'roles.reader.titles': 'Miles de títulos',
        'roles.reader.format': 'Digital o físico',
        'roles.reader.shipping': 'Envío rápido local',
        'roles.reader.prices': 'Precios accesibles',
        'roles.reader.library': 'Biblioteca personal',
        'roles.reader.cta': 'Explorar catálogo →',
        
        // Books
        'books.title': 'Libros Destacados',
        'books.digital': 'Digital',
        'books.printed': 'Impreso',
        'books.printing': 'Impresión',
        'books.shipping': 'días',
        'books.buy': 'Comprar',
        'books.by': 'por',
        
        // AI Section
        'ai.title': 'IA de DrakkarPress - Tu Asistente Editorial',
        'ai.subtitle': 'Inteligencia Artificial al servicio de los escritores',
        'ai.ideas.title': 'Ideas de Libros',
        'ai.ideas.subtitle': 'Genera conceptos únicos',
        'ai.extend.title': 'Extensión de Texto',
        'ai.extend.subtitle': 'Amplía tus capítulos',
        'ai.synopsis.title': 'Sinopsis',
        'ai.synopsis.subtitle': 'Crea descripciones atractivas',
        'ai.marketing.title': 'Marketing Social',
        'ai.marketing.subtitle': 'Posts para redes sociales',
        'ai.children.title': 'Libros Infantiles',
        'ai.children.subtitle': 'Estructuras y plantillas',
        'ai.multilingual.title': 'Multiidioma',
        'ai.multilingual.subtitle': 'Disponible en varios idiomas',
        'ai.cta': 'Probar IA de DrakkarPress Gratis',
        
        // Map Section
        'map.title': 'Red de Imprentas DrakkarPress',
        'map.subtitle.main': '¿Quieres Imprimir?',
        'map.subtitle.desc': 'Impresión local, entrega rápida en 45 países',
        'map.interactive': 'Mapa Interactivo de la Red',
        'map.active': 'imprentas activas',
        'map.printers': 'Imprentas',
        'map.countries': 'Países',
        'map.delivery': 'Tiempo de Entrega',
        'map.books': 'Libros Impresos',
        
        // Footer
        'footer.about.title': 'Acerca de',
        'footer.about.us': 'Sobre DrakkarPress',
        'footer.about.mission': 'Nuestra misión',
        'footer.about.team': 'Equipo',
        'footer.about.careers': 'Carreras',
        
        'footer.for.title': 'Para',
        'footer.for.writers': 'Para escritores',
        'footer.for.resellers': 'Para revendedores',
        'footer.for.printers': 'Para imprentas',
        
        'footer.resources.title': 'Recursos',
        'footer.resources.blog': 'Blog',
        'footer.resources.help': 'Centro de ayuda',
        'footer.resources.api': 'API',
        'footer.resources.tutorials': 'Tutoriales',
        
        'footer.legal.title': 'Legal',
        'footer.legal.terms': 'Términos y Condiciones',
        'footer.legal.privacy': 'Política de Privacidad',
        'footer.legal.cookies': 'Política de Cookies',
        
        'footer.newsletter.title': 'Mantente al día',
        'footer.newsletter.description': 'Recibe novedades y ofertas',
        'footer.newsletter.placeholder': 'Tu email',
        'footer.newsletter.button': 'Suscribirse',
        
        'footer.copyright': 'DrakkarPress. Todos los derechos reservados.',
        
        // Misc
        'page.title': 'DrakkarPress - La Flota Editorial Digital',
    },
    
    en: {
        // Navigation
        'nav.readers': 'Readers',
        'nav.writers': 'Writers',
        'nav.editorials': 'or Publishers',
        'nav.resellers': 'Resellers',
        'nav.printers': 'Print Network',
        'nav.printers.sub': 'Want to Print?',
        'nav.marketing': 'Marketing',
        'nav.marketing.sub': 'Synergistic',
        'nav.ai': 'AI',
        'nav.login': 'Sign In',
        
        // Hero
        'hero.title': 'The Digital Editorial Fleet',
        'hero.subtitle': 'Where your books sail to the world',
        'hero.search.placeholder': '🔍 Search books, authors, ISBN...',
        'hero.search.button': 'Search',
        'hero.filter.category': 'Category',
        'hero.filter.format': 'Format',
        'hero.filter.price': 'Price',
        'hero.filter.more': 'More',
        'hero.stats': '📊 {books} books | {authors} authors | {printers} printers | {countries} countries',
        
        // Marketing Section
        'marketing.hero.title': 'Synergistic Marketing',
        'marketing.hero.subtitle': 'We boost your success with <strong>bidirectional campaigns</strong>: from DrakkarPress to your business and from your site to our network. <strong>Everyone gains more traffic and sales.</strong>',
        'marketing.hero.cta': 'See Full Strategies →',
        'marketing.title': 'Synergistic Marketing Campaigns',
        'marketing.subtitle': 'We empower everyone\'s success: writers, resellers and printers',
        
        // Roles
        'roles.title': 'How do you want to participate in DrakkarPress?',
        'roles.writer.title': 'I\'m a Writer',
        'roles.writer.subtitle': 'Create and publish your books',
        'roles.writer.publish': 'Publish for free',
        'roles.writer.ai': 'AI helps you create',
        'roles.writer.royalty': '90% direct / 60% with reseller',
        'roles.writer.inventory': 'No inventory',
        'roles.writer.automatic': 'Automatic sales',
        'roles.writer.cta': 'Publish my book →',
        
        'roles.reseller.title': 'I Want to Resell',
        'roles.reseller.subtitle': 'Sell books and earn commissions',
        'roles.reseller.investment': 'No initial investment',
        'roles.reseller.catalog': 'Choose your catalog',
        'roles.reseller.commission': '30% commission',
        'roles.reseller.tracking': 'Links with tracking',
        'roles.reseller.ai': 'AI generates marketing',
        'roles.reseller.cta': 'Become an affiliate →',
        
        'roles.printer.title': 'I Have a Print Shop',
        'roles.printer.subtitle': 'Print books on demand',
        'roles.printer.orders': 'Automatic orders',
        'roles.printer.flow': 'Constant flow',
        'roles.printer.payments': 'Timely payments',
        'roles.printer.panel': 'Management panel',
        'roles.printer.risk': 'No risk',
        'roles.printer.cta': 'Join the network →',
        
        'roles.reader.title': 'I Want to Buy',
        'roles.reader.subtitle': 'Discover your next read',
        'roles.reader.titles': 'Thousands of titles',
        'roles.reader.format': 'Digital or physical',
        'roles.reader.shipping': 'Fast local shipping',
        'roles.reader.prices': 'Affordable prices',
        'roles.reader.library': 'Personal library',
        'roles.reader.cta': 'Explore catalog →',
        
        // Books
        'books.title': 'Featured Books',
        'books.digital': 'Digital',
        'books.printed': 'Printed',
        'books.printing': 'Printing',
        'books.shipping': 'days',
        'books.buy': 'Buy',
        'books.by': 'by',
        
        // AI Section
        'ai.title': 'DrakkarPress AI - Your Editorial Assistant',
        'ai.subtitle': 'Artificial Intelligence at the service of writers',
        'ai.ideas.title': 'Book Ideas',
        'ai.ideas.subtitle': 'Generate unique concepts',
        'ai.extend.title': 'Text Extension',
        'ai.extend.subtitle': 'Expand your chapters',
        'ai.synopsis.title': 'Synopsis',
        'ai.synopsis.subtitle': 'Create attractive descriptions',
        'ai.marketing.title': 'Social Marketing',
        'ai.marketing.subtitle': 'Social media posts',
        'ai.children.title': 'Children\'s Books',
        'ai.children.subtitle': 'Structures and templates',
        'ai.multilingual.title': 'Multilingual',
        'ai.multilingual.subtitle': 'Available in multiple languages',
        'ai.cta': 'Try DrakkarPress AI Free',
        
        // Map Section
        'map.title': 'DrakkarPress Print Network',
        'map.subtitle.main': 'Want to Print?',
        'map.subtitle.desc': 'Local printing, fast delivery in 45 countries',
        'map.interactive': 'Interactive Network Map',
        'map.active': 'active printers',
        'map.printers': 'Printers',
        'map.countries': 'Countries',
        'map.delivery': 'Delivery Time',
        'map.books': 'Books Printed',
        
        // Footer
        'footer.about.title': 'About',
        'footer.about.us': 'About DrakkarPress',
        'footer.about.mission': 'Our mission',
        'footer.about.team': 'Team',
        'footer.about.careers': 'Careers',
        
        'footer.for.title': 'For',
        'footer.for.writers': 'For writers',
        'footer.for.resellers': 'For resellers',
        'footer.for.printers': 'For printers',
        
        'footer.resources.title': 'Resources',
        'footer.resources.blog': 'Blog',
        'footer.resources.help': 'Help Center',
        'footer.resources.api': 'API',
        'footer.resources.tutorials': 'Tutorials',
        
        'footer.legal.title': 'Legal',
        'footer.legal.terms': 'Terms and Conditions',
        'footer.legal.privacy': 'Privacy Policy',
        'footer.legal.cookies': 'Cookie Policy',
        
        'footer.newsletter.title': 'Stay updated',
        'footer.newsletter.description': 'Receive news and offers',
        'footer.newsletter.placeholder': 'Your email',
        'footer.newsletter.button': 'Subscribe',
        
        'footer.copyright': 'DrakkarPress. All rights reserved.',
        
        // Misc
        'page.title': 'DrakkarPress - The Digital Editorial Fleet',
    },
    
    pt: {
        // Navegação
        'nav.readers': 'Leitores',
        'nav.writers': 'Escritores',
        'nav.editorials': 'ou Editoras',
        'nav.resellers': 'Revendedores',
        'nav.printers': 'Rede de Gráficas',
        'nav.printers.sub': 'Quer Imprimir?',
        'nav.marketing': 'Marketing',
        'nav.marketing.sub': 'Sinérgico',
        'nav.ai': 'IA',
        'nav.login': 'Entrar',
        
        // Hero
        'hero.title': 'A Frota Editorial Digital',
        'hero.subtitle': 'Onde seus livros navegam para o mundo',
        'hero.search.placeholder': '🔍 Buscar livros, autores, ISBN...',
        'hero.search.button': 'Buscar',
        'hero.filter.category': 'Categoria',
        'hero.filter.format': 'Formato',
        'hero.filter.price': 'Preço',
        'hero.filter.more': 'Mais',
        'hero.stats': '📊 {books} livros | {authors} autores | {printers} gráficas | {countries} países',
        
        // Roles
        'roles.title': 'Como você quer participar do DrakkarPress?',
        'roles.writer.title': 'Sou Escritor',
        'roles.writer.subtitle': 'Crie e publique seus livros',
        'roles.writer.publish': 'Publique grátis',
        'roles.writer.ai': 'IA te ajuda a criar',
        'roles.writer.royalty': '90% direto / 60% com revendedor',
        'roles.writer.inventory': 'Sem estoque',
        'roles.writer.automatic': 'Venda automática',
        'roles.writer.cta': 'Publicar meu livro →',
        
        // ... (continuar con todas las traducciones)
        
        'page.title': 'DrakkarPress - A Frota Editorial Digital',
    },
    
    fr: {
        'nav.readers': 'Lecteurs',
        'nav.writers': 'Écrivains',
        'nav.editorials': 'ou Éditeurs',
        'nav.login': 'Se connecter',
        'hero.title': 'La Flotte Éditoriale Numérique',
        'hero.subtitle': 'Où vos livres naviguent vers le monde',
        'hero.search.button': 'Chercher',
        'page.title': 'DrakkarPress - La Flotte Éditoriale Numérique',
    },
    
    de: {
        'nav.readers': 'Leser',
        'nav.writers': 'Autoren',
        'nav.editorials': 'oder Verlage',
        'nav.login': 'Anmelden',
        'hero.title': 'Die Digitale Verlagsflotte',
        'hero.subtitle': 'Wo Ihre Bücher in die Welt segeln',
        'hero.search.button': 'Suchen',
        'page.title': 'DrakkarPress - Die Digitale Verlagsflotte',
    },
    
    it: {
        'nav.readers': 'Lettori',
        'nav.writers': 'Scrittori',
        'nav.editorials': 'o Editori',
        'nav.login': 'Accedi',
        'hero.title': 'La Flotta Editoriale Digitale',
        'hero.subtitle': 'Dove i tuoi libri navigano verso il mondo',
        'hero.search.button': 'Cerca',
        'page.title': 'DrakkarPress - La Flotta Editoriale Digitale',
    }
};

// Sistema i18n
class I18n {
    constructor() {
        this.currentLang = this.detectLanguage();
        this.translations = translations;
    }
    
    detectLanguage() {
        // 1. Buscar en localStorage
        const saved = localStorage.getItem('drakkarpress_lang');
        if (saved && translations[saved]) {
            return saved;
        }
        
        // 2. Buscar en URL query parameter
        const urlParams = new URLSearchParams(window.location.search);
        const urlLang = urlParams.get('lang');
        if (urlLang && translations[urlLang]) {
            return urlLang;
        }
        
        // 3. Detectar del navegador
        const browserLang = navigator.language || navigator.userLanguage;
        const shortLang = browserLang.split('-')[0]; // 'es-ES' -> 'es'
        
        if (translations[shortLang]) {
            return shortLang;
        }
        
        // 4. Default: español
        return 'es';
    }
    
    setLanguage(lang) {
        if (!translations[lang]) {
            console.warn(`Language '${lang}' not supported`);
            return;
        }
        
        this.currentLang = lang;
        localStorage.setItem('drakkarpress_lang', lang);
        document.documentElement.setAttribute('lang', lang);
        document.documentElement.setAttribute('data-lang', lang);
        this.translatePage();
    }
    
    t(key, params = {}) {
        const translation = translations[this.currentLang][key] || translations['es'][key] || key;
        
        // Reemplazar parámetros {variable}
        return translation.replace(/\{(\w+)\}/g, (match, param) => {
            return params[param] !== undefined ? params[param] : match;
        });
    }
    
    translatePage() {
        // Traducir elementos con atributo data-i18n
        document.querySelectorAll('[data-i18n]').forEach(element => {
            const key = element.getAttribute('data-i18n');
            const translation = this.t(key);
            
            if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
                element.placeholder = translation;
            } else {
                element.innerHTML = translation;
            }
        });
        
        // Traducir placeholders con data-i18n-placeholder
        document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
            const key = element.getAttribute('data-i18n-placeholder');
            element.placeholder = this.t(key);
        });
        
        // Traducir títulos con data-i18n-title
        document.querySelectorAll('[data-i18n-title]').forEach(element => {
            const key = element.getAttribute('data-i18n-title');
            element.title = this.t(key);
        });
        
        // Actualizar título de la página
        document.title = this.t('page.title');
    }
    
    getAvailableLanguages() {
        return Object.keys(translations).map(code => ({
            code,
            name: this.getLanguageName(code),
            flag: this.getLanguageFlag(code)
        }));
    }
    
    getLanguageName(code) {
        const names = {
            'es': 'Español',
            'en': 'English',
            'pt': 'Português',
            'fr': 'Français',
            'de': 'Deutsch',
            'it': 'Italiano'
        };
        return names[code] || code;
    }
    
    getLanguageFlag(code) {
        const flags = {
            'es': '🇪🇸',
            'en': '🇺🇸',
            'pt': '🇧🇷',
            'fr': '🇫🇷',
            'de': '🇩🇪',
            'it': '🇮🇹'
        };
        return flags[code] || '🌐';
    }
}

// Instancia global
const i18n = new I18n();

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    i18n.translatePage();
    
    // Crear selector de idioma si no existe
    createLanguageSelector();
});

// Función para crear el selector de idioma
function createLanguageSelector() {
    const nav = document.querySelector('nav .nav-links');
    if (!nav) return;
    
    const languages = i18n.getAvailableLanguages();
    const currentLang = i18n.currentLang;
    
    const langSelector = document.createElement('div');
    langSelector.className = 'language-selector';
    langSelector.innerHTML = `
        <button class="lang-btn" onclick="toggleLanguageMenu()">
            ${i18n.getLanguageFlag(currentLang)} ${i18n.getLanguageName(currentLang)} ▾
        </button>
        <div class="lang-menu" id="langMenu" style="display: none;">
            ${languages.map(lang => `
                <button class="lang-option ${lang.code === currentLang ? 'active' : ''}" 
                        onclick="changeLang('${lang.code}')">
                    ${lang.flag} ${lang.name}
                </button>
            `).join('')}
        </div>
    `;
    
    // Insertar antes del botón de login
    const loginBtn = nav.querySelector('.btn-primary');
    if (loginBtn) {
        nav.insertBefore(langSelector, loginBtn);
    } else {
        nav.appendChild(langSelector);
    }
}

function toggleLanguageMenu() {
    const menu = document.getElementById('langMenu');
    menu.style.display = menu.style.display === 'none' ? 'block' : 'none';
}

function changeLang(code) {
    i18n.setLanguage(code);
    toggleLanguageMenu();
    
    // Actualizar el botón del selector
    const langBtn = document.querySelector('.lang-btn');
    if (langBtn) {
        langBtn.innerHTML = `${i18n.getLanguageFlag(code)} ${i18n.getLanguageName(code)} ▾`;
    }
    
    // Actualizar opciones activas
    document.querySelectorAll('.lang-option').forEach(option => {
        option.classList.remove('active');
    });
    event.target.classList.add('active');
}

// Cerrar menú al hacer clic fuera
document.addEventListener('click', (event) => {
    const langSelector = document.querySelector('.language-selector');
    if (langSelector && !langSelector.contains(event.target)) {
        const menu = document.getElementById('langMenu');
        if (menu) menu.style.display = 'none';
    }
});

// Exportar para uso en otros módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { i18n, I18n, translations };
}
