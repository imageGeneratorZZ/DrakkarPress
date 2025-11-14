(function (window, document) {
    const NAMESPACE = "DrakkarPress";

    function getDefaults() {
        const root = window[NAMESPACE] || {};
        return (root.defaults && root.defaults.stats) || {};
    }

    function formatValue(value, format, locale) {
        const numericValue = Number(value || 0);
        const targetFormat = format || "number";
        const targetLocale = locale || document.documentElement.lang || "es-ES";

        switch (targetFormat) {
            case "compact":
                return new Intl.NumberFormat(targetLocale, {
                    notation: "compact",
                    maximumFractionDigits: 1
                }).format(numericValue);
            case "integer":
            case "number":
            default:
                return new Intl.NumberFormat(targetLocale, {
                    maximumFractionDigits: 0
                }).format(numericValue);
        }
    }

    function applyStats(stats) {
        const defaults = getDefaults();
        const currentStats = { ...defaults, ...(stats || {}) };
        const locale = document.documentElement.lang || "es-ES";

        document.querySelectorAll("[data-stat]").forEach((element) => {
            const key = element.getAttribute("data-stat");
            const format = element.getAttribute("data-format") || "number";
            const fallback = element.getAttribute("data-default");
            const prefix = element.getAttribute("data-prefix") || "";
            const suffix = element.getAttribute("data-suffix") || "";

            let value = currentStats[key];

            if (value === undefined || value === null) {
                value = fallback !== null && fallback !== undefined
                    ? fallback
                    : defaults[key];
            }

            element.textContent = `${prefix}${formatValue(value, format, locale)}${suffix}`;
        });
    }

    document.addEventListener("drakkarpress:stats", (event) => {
        applyStats(event.detail);
    });

    document.addEventListener("DOMContentLoaded", () => {
        applyStats();
    });
})(window, document);
