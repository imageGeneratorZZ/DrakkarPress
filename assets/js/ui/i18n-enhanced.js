/**
 * i18n Enhanced - Dynamic Internationalization System
 * Supports multiple languages with JSON-based locales
 * Features: localStorage persistence, dynamic loading, data-i18n attributes
 */

window.i18nEnhanced = (function() {
    'use strict';

    // Configuration
    const CONFIG = {
        defaultLanguage: 'es',
        supportedLanguages: ['es', 'en'],
        storageKey: 'drakkarpress-lang',
        localesPath: '/assets/locales/'
    };

    // State
    let currentLanguage = CONFIG.defaultLanguage;
    let translations = {};
    let isInitialized = false;

    /**
     * Get language from localStorage or browser default
     */
    function detectLanguage() {
        // Check localStorage first
        const stored = localStorage.getItem(CONFIG.storageKey);
        if (stored && CONFIG.supportedLanguages.includes(stored)) {
            return stored;
        }

        // Check browser language
        const browserLang = navigator.language.split('-')[0];
        if (CONFIG.supportedLanguages.includes(browserLang)) {
            return browserLang;
        }

        return CONFIG.defaultLanguage;
    }

    /**
     * Load translations from JSON file
     */
    async function loadTranslations(lang) {
        try {
            const response = await fetch(`${CONFIG.localesPath}${lang}.json`);
            if (!response.ok) {
                throw new Error(`Failed to load translations for ${lang}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Error loading translations for ${lang}:`, error);
            
            // Fallback to default language if not already
            if (lang !== CONFIG.defaultLanguage) {
                console.warn(`Falling back to ${CONFIG.defaultLanguage}`);
                return loadTranslations(CONFIG.defaultLanguage);
            }
            
            return {};
        }
    }

    /**
     * Get nested translation value by dot notation key
     * Example: get('hero.title') returns translations.hero.title
     */
    function getTranslation(key, fallback = key) {
        const keys = key.split('.');
        let value = translations;

        for (const k of keys) {
            if (value && typeof value === 'object' && k in value) {
                value = value[k];
            } else {
                return fallback;
            }
        }

        return typeof value === 'string' ? value : fallback;
    }

    /**
     * Translate element with data-i18n attribute
     */
    function translateElement(element) {
        const key = element.getAttribute('data-i18n');
        if (!key) return;

        const translation = getTranslation(key);
        
        // Check if element should use innerHTML (for HTML content)
        if (element.hasAttribute('data-i18n-html')) {
            element.innerHTML = translation;
        } else {
            element.textContent = translation;
        }
    }

    /**
     * Scan and translate all elements with data-i18n
     */
    function translatePage() {
        const elements = document.querySelectorAll('[data-i18n]');
        elements.forEach(translateElement);

        // Update html lang attribute
        document.documentElement.setAttribute('lang', currentLanguage);
        document.documentElement.setAttribute('data-lang', currentLanguage);
    }

    /**
     * Change language and reload translations
     */
    async function changeLanguage(lang) {
        if (!CONFIG.supportedLanguages.includes(lang)) {
            console.warn(`Unsupported language: ${lang}`);
            return false;
        }

        currentLanguage = lang;
        localStorage.setItem(CONFIG.storageKey, lang);

        // Load translations
        translations = await loadTranslations(lang);

        // Translate page
        translatePage();

        // Dispatch custom event
        window.dispatchEvent(new CustomEvent('languageChanged', {
            detail: { language: lang }
        }));

        return true;
    }

    /**
     * Initialize i18n system
     */
    async function init() {
        if (isInitialized) {
            console.warn('i18n already initialized');
            return;
        }

        // Detect language
        currentLanguage = detectLanguage();

        // Load translations
        translations = await loadTranslations(currentLanguage);

        // Translate page on load
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', translatePage);
        } else {
            translatePage();
        }

        // Setup language switcher if exists
        setupLanguageSwitcher();

        isInitialized = true;
    }

    /**
     * Setup language switcher UI
     */
    function setupLanguageSwitcher() {
        const switcher = document.getElementById('language-switcher');
        if (!switcher) return;

        // Create language options
        const html = CONFIG.supportedLanguages.map(lang => {
            const active = lang === currentLanguage ? 'active' : '';
            const label = lang.toUpperCase();
            return `
                <button 
                    class="lang-btn ${active}" 
                    data-lang="${lang}"
                    aria-label="Switch to ${lang === 'es' ? 'Spanish' : 'English'}"
                >
                    ${label}
                </button>
            `;
        }).join('');

        switcher.innerHTML = html;

        // Add click handlers
        switcher.querySelectorAll('.lang-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const lang = btn.getAttribute('data-lang');
                const success = await changeLanguage(lang);
                
                if (success) {
                    // Update active state
                    switcher.querySelectorAll('.lang-btn').forEach(b => 
                        b.classList.remove('active')
                    );
                    btn.classList.add('active');
                }
            });
        });
    }

    /**
     * Add observer for dynamic content
     */
    function observeDynamicContent() {
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === 1) { // Element node
                        // Translate if it has data-i18n
                        if (node.hasAttribute && node.hasAttribute('data-i18n')) {
                            translateElement(node);
                        }
                        
                        // Translate children
                        if (node.querySelectorAll) {
                            node.querySelectorAll('[data-i18n]').forEach(translateElement);
                        }
                    }
                });
            });
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    }

    // Public API
    return {
        /**
         * Initialize the i18n system
         */
        init,

        /**
         * Change current language
         * @param {string} lang - Language code (es, en)
         */
        changeLanguage,

        /**
         * Get translation by key
         * @param {string} key - Translation key (dot notation)
         * @param {string} fallback - Fallback value if key not found
         */
        t: getTranslation,

        /**
         * Get current language
         */
        getCurrentLanguage() {
            return currentLanguage;
        },

        /**
         * Get all translations for current language
         */
        getTranslations() {
            return translations;
        },

        /**
         * Manually translate page (useful after dynamic content)
         */
        translatePage,

        /**
         * Enable automatic translation of dynamic content
         */
        enableDynamicTranslation() {
            observeDynamicContent();
        }
    };
})();

// Auto-initialize if not in module context
if (typeof module === 'undefined') {
    window.i18nEnhanced.init();
}
