// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

const ENDPOINTS = {
    AUTH_STATUS: "api/auth/status",
    LOGIN: "auth/login",
    LOGOUT: "auth/logout",
    USER_ROLES: "userRolePermission/v1/userRole",
};

class BffAuthService {
    #gatewayUrl = "";
    #userInfo = null;
    #entitlements = [];
    #authenticated = false;
    #initialized = false;
    #entitlementsLoaded = false;
    #entitlementsFailed = false;
    #initPromise = null;
    #loginRedirecting = false;

    static get CSRF_HEADER_NAME() {
        return CSRF_HEADER_NAME;
    }

    get authenticated() {
        return this.#authenticated;
    }

    get initialized() {
        return this.#initialized;
    }

    get entitlementsLoaded() {
        return this.#entitlementsLoaded;
    }

    get entitlementsFailed() {
        return this.#entitlementsFailed;
    }

    get gatewayUrl() {
        return this.#gatewayUrl;
    }

    static getCsrfToken() {
        return document.cookie
            .split("; ")
            .find((row) => row.startsWith(`${CSRF_COOKIE_NAME}=`))
            ?.split("=")[1];
    }

    configure(gatewayUrl) {
        if (!this.#gatewayUrl) {
            this.#gatewayUrl = gatewayUrl.endsWith("/") ? gatewayUrl : `${gatewayUrl}/`;
        }
    }

    async init(gatewayUrl) {
        if (this.#initialized) return;
        if (this.#loginRedirecting) return;
        if (this.#initPromise) return this.#initPromise;

        this.#initPromise = this.#doInit(gatewayUrl);
        return this.#initPromise;
    }

    async #doInit(gatewayUrl) {
        this.configure(gatewayUrl);
        try {
            const response = await fetch(`${this.#gatewayUrl}${ENDPOINTS.AUTH_STATUS}`, {
                credentials: "include",
            });

            if (response.status === 401 || !response.ok) {
                this.login();
                return;
            }

            const authStatus = await response.json();

            if (!authStatus.authenticated) {
                this.login();
                return;
            }

            this.#authenticated = true;
            this.#userInfo = {
                preferred_username: authStatus.username,
                name: authStatus.name,
                email: authStatus.email,
            };
            this.#entitlements = await this.#fetchEntitlements();
            this.#initialized = true;
        } catch (error) {
            console.error("[Auth] Initialization failed:", error);
            this.login();
        }
    }

    login() {
        if (this.#loginRedirecting) return;
        this.#loginRedirecting = true;
        const returnUrl = encodeURIComponent(window.location.href);
        window.location.href = `${this.#gatewayUrl}${ENDPOINTS.LOGIN}?redirect_uri=${returnUrl}`;
    }

    logout = () => {
        const homeUrl = encodeURIComponent(window.location.origin);
        window.location.href = `${this.#gatewayUrl}${ENDPOINTS.LOGOUT}?redirect_uri=${homeUrl}`;
    };

    getUserInfo() {
        return this.#userInfo ?? {preferred_username: "", name: "", email: ""};
    }

    hasEntitlement(entitlementId) {
        return this.#entitlements.includes(entitlementId);
    }

    async #fetchEntitlements() {
        try {
            const response = await fetch(`${this.#gatewayUrl}${ENDPOINTS.USER_ROLES}`, {
                credentials: "include",
            });

            if (!response.ok) {
                console.error("[Auth] Failed to fetch user roles:", response.status);
                this.#entitlementsFailed = true;
                return [];
            }

            const userRoles = await response.json();
            this.#entitlementsLoaded = true;

            if (!Array.isArray(userRoles) || userRoles.length === 0) return [];
            return [...new Set(
                userRoles.flatMap((role) => (role.entitlement || []).map((e) => e.id))
            )];
        } catch (error) {
            console.error("[Auth] Failed to fetch user roles:", error?.message ?? error);
            this.#entitlementsFailed = true;
            return [];
        }
    }
}

export default new BffAuthService();