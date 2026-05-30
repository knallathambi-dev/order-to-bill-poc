// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import axios from "axios";

export const API_BASE_PATH = "/api";
const REQUEST_TIMEOUT = 30000;
const CSRF_METHODS = ["POST", "PUT", "PATCH", "DELETE"];

const getCookie = (name) => {
    const match = document.cookie.match(new RegExp("(^| )" + name + "=([^;]+)"));
    return match ? decodeURIComponent(match[2]) : null;
};

const apiClient = axios.create({
    baseURL: API_BASE_PATH,
    timeout: REQUEST_TIMEOUT,
    withCredentials: true,
    headers: {Accept: "application/json"},
});

const fetchCsrfCookie = async () => {
    try {
        await apiClient.get("/auth/csrf");
    } catch {
    }
};

const isCsrfError = (error) => {
    if (error?.response?.status !== 403) return false;
    const msg = String(error.response?.data?.error || "").toLowerCase();
    return msg.includes("csrf");
};

const isAuthEndpoint = (url) => {
    return url.includes("/auth/login") ||
        url.includes("/auth/register") ||
        url.includes("/auth/refresh") ||
        url.includes("/auth/csrf");
};

const notifySessionExpired = () => {
    if (typeof window !== "undefined") {
        window.dispatchEvent(new CustomEvent("auth:session-expired"));
    }
};

const notifyFullSessionExpired = () => {
    if (typeof window !== "undefined") {
        window.dispatchEvent(new CustomEvent("auth:full-session-expired"));
    }
};

const isFullSessionExpired = (error) => {
    const data = error?.response?.data;
    return data?.code === "FULL_SESSION_EXPIRED";
};

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error) => {
    failedQueue.forEach(({resolve, reject}) => {
        if (error) {
            reject(error);
        } else {
            resolve();
        }
    });
    failedQueue = [];
};

apiClient.interceptors.request.use((config) => {
    const method = (config.method || "get").toUpperCase();
    if (CSRF_METHODS.includes(method)) {
        const csrfToken = getCookie("XSRF-TOKEN");
        if (csrfToken) {
            config.headers["X-CSRF-Token"] = csrfToken;
        }
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config || {};
        const status = error.response?.status;
        const url = originalRequest.url || "";

        if (isAuthEndpoint(url)) {
            return Promise.reject(error);
        }

        if (isCsrfError(error) && !originalRequest._csrfRetried) {
            originalRequest._csrfRetried = true;
            await fetchCsrfCookie();
            return apiClient(originalRequest);
        }

        if (status === 401 && !originalRequest._authRetried) {
            originalRequest._authRetried = true;

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({resolve, reject});
                }).then(() => {
                    return apiClient(originalRequest);
                });
            }

            isRefreshing = true;

            try {
                const response = await apiClient.post("/auth/refresh");
                const data = response.data || {};

                if (data.success) {
                    if (data.serviceSession === true && !data.user?.email) {
                        processQueue(new Error("Session downgraded to service"));
                        if (typeof window !== "undefined") {
                            window.dispatchEvent(new CustomEvent("auth:service-session-active"));
                        }
                        return Promise.reject(error);
                    }

                    processQueue(null);
                    return apiClient(originalRequest);
                }

                processQueue(new Error("Refresh failed"));
                notifySessionExpired();
                return Promise.reject(error);

            } catch (refreshError) {
                if (isFullSessionExpired(refreshError)) {
                    processQueue(refreshError);
                    notifyFullSessionExpired();
                    return Promise.reject(error);
                }

                processQueue(refreshError);
                notifySessionExpired();
                return Promise.reject(error);
            } finally {
                isRefreshing = false;
            }
        }

        if (status === 401 && isFullSessionExpired(error)) {
            notifyFullSessionExpired();
            return Promise.reject(error);
        }

        return Promise.reject(error);
    },
);

export default apiClient;