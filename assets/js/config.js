(function (window) {
    const DEFAULT_API_URL = "http://localhost:8080";
    const PRODUCTION_API_URL = "https://overflowing-consideration-production.up.railway.app";
    const DEFAULT_STATS_REFRESH_INTERVAL_MS = 60_000;

    const hostname = (window.location && window.location.hostname || "").toLowerCase();
    const storedUrl = window.localStorage ? window.localStorage.getItem("drakkarpress_api_url") : null;

    let apiBaseUrl = DEFAULT_API_URL;
    if (storedUrl) {
        apiBaseUrl = storedUrl;
    } else if (hostname.includes("drakkarpress.com") || hostname.includes("netlify.app")) {
        apiBaseUrl = PRODUCTION_API_URL;
    }

    let statsRefreshIntervalMs = DEFAULT_STATS_REFRESH_INTERVAL_MS;

    window.DrakkarPress = window.DrakkarPress || {};
    window.DrakkarPress.config = {
        DEFAULT_API_URL,
        PRODUCTION_API_URL,
        DEFAULT_STATS_REFRESH_INTERVAL_MS,
        get apiBaseUrl() {
            return apiBaseUrl;
        },
        getApiBaseUrl() {
            return apiBaseUrl;
        },
        setApiBaseUrl(url) {
            if (typeof url === "string" && url.trim().length > 0) {
                apiBaseUrl = url.trim();
                if (window.localStorage) {
                    window.localStorage.setItem("drakkarpress_api_url", apiBaseUrl);
                }
            }
        },
        resetApiBaseUrl() {
            apiBaseUrl = DEFAULT_API_URL;
            if (window.localStorage) {
                window.localStorage.removeItem("drakkarpress_api_url");
            }
        },
        get statsRefreshIntervalMs() {
            return statsRefreshIntervalMs;
        },
        setStatsRefreshIntervalMs(value) {
            const numeric = Number(value);
            if (!Number.isNaN(numeric) && numeric >= 0) {
                statsRefreshIntervalMs = numeric;
                document.dispatchEvent(new CustomEvent("drakkarpress:stats:refresh"));
            }
        }
    };

    const defaultStats = {
        books: 124583,
        authors: 8421,
        printShops: 326,
        resellers: 5120,
        countries: 45,
        activeUsers: 12840,
        totalAiGenerations: 124583
    };

    if (!window.DrakkarPress.defaults || typeof window.DrakkarPress.defaults !== "object") {
        window.DrakkarPress.defaults = {};
    }

    if (!window.DrakkarPress.defaults.stats) {
        window.DrakkarPress.defaults.stats = defaultStats;
    } else {
        window.DrakkarPress.defaults.stats = {
            ...defaultStats,
            ...window.DrakkarPress.defaults.stats
        };
    }
})(window);
