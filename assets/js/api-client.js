(function (window) {
    window.DrakkarPress = window.DrakkarPress || {};
    const config = window.DrakkarPress.config || { apiBaseUrl: "" };

    async function request(path, options) {
        const baseUrl = typeof config.getApiBaseUrl === "function" ? config.getApiBaseUrl() : config.apiBaseUrl;
        const url = `${baseUrl}${path}`;
        const defaultOptions = {
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include"
        };

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });
            const isJson = (response.headers.get("content-type") || "").includes("application/json");
            const payload = isJson ? await response.json() : await response.text();
            if (!response.ok) {
                return { data: null, error: payload || response.statusText };
            }
            return { data: payload, error: null };
        } catch (error) {
            return { data: null, error: error.message || "Network error" };
        }
    }

    async function getHealth() {
        return request("/health", { method: "GET" });
    }

    async function listCreations() {
        return request("/api/creations", { method: "GET" });
    }

    async function createCreation(payload) {
        return request("/api/creations", { method: "POST", body: JSON.stringify(payload) });
    }

    async function getStats() {
        const result = await request("/api/stats", { method: "GET" });

        if (result.error || !result.data) {
            return {
                data: null,
                error: result.error || "Stats unavailable"
            };
        }

        return {
            data: result.data,
            error: null
        };
    }

    async function getSummary() {
        const [health, creations] = await Promise.allSettled([getHealth(), listCreations()]);
        const summary = {
            healthStatus: "offline",
            creationsCount: 0,
            healthMessage: null
        };

        if (health.status === "fulfilled" && !health.value.error && health.value.data) {
            summary.healthStatus = "online";
            summary.healthMessage = typeof health.value.data === "string" ? health.value.data : health.value.data.message || null;
        }

        if (creations.status === "fulfilled" && !creations.value.error && Array.isArray(creations.value.data)) {
            summary.creationsCount = creations.value.data.length;
        }

        return summary;
    }

    window.DrakkarPress.api = {
        request,
        getHealth,
        listCreations,
        createCreation,
        getStats,
        getSummary
    };
})(window);
