// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import axios from "axios";
import BffAuthService from "./BffAuthService";

const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";
const CSRF_METHODS = new Set(["POST", "PUT", "DELETE", "PATCH"]);

const DEFAULT_HEADERS = {
    "Content-Type": "application/json",
};

let isRedirectingToLogin = false;

function getCsrfToken() {
    return document.cookie
        .split("; ")
        .find((row) => row.startsWith(`${CSRF_COOKIE_NAME}=`))
        ?.split("=")[1];
}

const httpClient = axios.create({
    headers: DEFAULT_HEADERS,
    withCredentials: true,
});

httpClient.interceptors.request.use(
    (config) => {
        if (isRedirectingToLogin) {
            const controller = new AbortController();
            controller.abort();
            config.signal = controller.signal;
            return config;
        }

        const gatewayUrl = BffAuthService.gatewayUrl;
        if (gatewayUrl && !config.baseURL) {
            config.baseURL = gatewayUrl;
        }

        const method = (config.method || "GET").toUpperCase();
        if (CSRF_METHODS.has(method)) {
            const csrfToken = getCsrfToken();
            if (csrfToken) {
                config.headers[CSRF_HEADER_NAME] = csrfToken;
            } else {
                console.warn("[httpClient] CSRF token missing for mutating request:", config.url);
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);

httpClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isCancel(error) || error.code === "ERR_CANCELED") {
            return new Promise(() => {});
        }

        const status = error.response?.status;

        if (status === 401) {
            if (!isRedirectingToLogin) {
                isRedirectingToLogin = true;
                console.warn("[httpClient] Unauthenticated — redirecting to login.");
                BffAuthService.login();
            }
            return new Promise(() => {});
        }

        if (status === 403) {
            console.error("[httpClient] Forbidden — insufficient permissions for:", error.config?.url);
        }

        if (status >= 500) {
            console.error(`[httpClient] Server error ${status}:`, error.config?.url);
        }

        return Promise.reject(error);
    }
);

export default httpClient;