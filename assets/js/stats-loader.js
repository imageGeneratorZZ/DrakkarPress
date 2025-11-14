(function (window, document) {
    const STATUS_SELECTOR = '[data-role="stats-status"]';
    const MODE_SELECTOR = '[data-role="stats-mode"]';
    const REFRESH_EVENT = 'drakkarpress:stats:refresh';

    let refreshTimer = null;
    let lastState = null;

    function getRefreshInterval() {
        const config = window.DrakkarPress && window.DrakkarPress.config;
        if (!config) {
            return 60_000;
        }

        const interval = Number(config.statsRefreshIntervalMs ?? config.DEFAULT_STATS_REFRESH_INTERVAL_MS);
        if (Number.isFinite(interval) && interval >= 0) {
            return interval;
        }

        return config.DEFAULT_STATS_REFRESH_INTERVAL_MS || 60_000;
    }

    function clearRefreshTimer() {
        if (refreshTimer) {
            window.clearTimeout(refreshTimer);
            refreshTimer = null;
        }
    }

    function scheduleNextRefresh() {
        clearRefreshTimer();
        const interval = getRefreshInterval();
        if (interval > 0) {
            refreshTimer = window.setTimeout(() => refreshStats({ silent: true }), interval);
        }
    }

    function setStatus(state, message) {
        const statusEl = document.querySelector(STATUS_SELECTOR);
        if (!statusEl) {
            return;
        }

        statusEl.classList.remove("api-status--online", "api-status--offline", "api-status--loading");
        statusEl.classList.add(`api-status--${state}`);
        statusEl.textContent = message;
        lastState = state;
    }

    function setModeBadge(label) {
        const modeEl = document.querySelector(MODE_SELECTOR);
        if (modeEl) {
            modeEl.textContent = label;
        }
    }

    function setOfflineState(message) {
        if (lastState !== "offline") {
            setStatus("offline", message);
        }
        setModeBadge("Datos estimados");
    }

    async function refreshStats(options = {}) {
        const silent = Boolean(options.silent);
        const api = window.DrakkarPress && window.DrakkarPress.api;

        if (!api) {
            scheduleNextRefresh();
            return;
        }

        if (!silent) {
            setStatus("loading", "Conectando...");
            setModeBadge("Sincronizando datos...");
        }

        const { data, error } = await api.getStats();

        if (error || !data) {
            setOfflineState("API offline (modo demo)");
            scheduleNextRefresh();
            return;
        }

        document.dispatchEvent(new CustomEvent("drakkarpress:stats", { detail: data }));

        const source = (data.source || "").toLowerCase();

        switch (source) {
            case "baseline":
                setStatus("online", "API online (baseline manual)");
                setModeBadge("Modo baseline");
                break;
            case "hybrid":
                setStatus("online", "API online (datos mixtos)");
                setModeBadge("Datos en transición");
                break;
            case "live-with-fallback":
                setStatus("online", "API conectada (con fallback)");
                setModeBadge("Datos en vivo + respaldo");
                break;
            case "live":
            default:
                setStatus("online", "API conectada");
                setModeBadge("Datos en vivo");
        }

        scheduleNextRefresh();
    }

    document.addEventListener("DOMContentLoaded", () => {
        setStatus("loading", "Preparando...");
        setModeBadge("Datos históricos");
        refreshStats();
    });

    document.addEventListener(REFRESH_EVENT, () => {
        refreshStats();
    });

    window.addEventListener("offline", () => setOfflineState("Sin conexión (modo local)"));
    window.addEventListener("online", () => refreshStats());

    window.DrakkarPress = window.DrakkarPress || {};
    window.DrakkarPress.refreshStats = refreshStats;
})(window, document);
