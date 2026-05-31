// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import axios from "./axios.mjs";
import {jwtDecode} from "jwt-decode";

class KeycloakUtil {
    constructor() {
        this.keycloak = {tokenTimeoutHandle: null, token: null, tokenParsed: null, timeSkew: 0};
        this._loginInFlight = null;
    }

    async implicitLogin() {
        const keycloakConfig = {
            realm: process.env.EXPRESS_APP_KEYCLOAK_REALM || "discobole",
            url: process.env.EXPRESS_APP_KEYCLOAK_URL,
            clientId: process.env.EXPRESS_APP_CLIENT_ID || "poc-gateway",
        };

        const hasPasswordCredentials = process.env.EXPRESS_APP_USERNAME && process.env.EXPRESS_APP_PASSWORD;
        if (!keycloakConfig.url || (!hasPasswordCredentials && !process.env.EXPRESS_APP_CLIENT_SECRET)) {
            const error = new Error("Missing Keycloak configuration");
            console.error("[Keycloak] Configuration error");
            return Promise.reject(error);
        }

        if (this._loginInFlight) {
            return this._loginInFlight;
        }

        const uri = `/realms/${keycloakConfig.realm}/protocol/openid-connect/token`;
        const tokenEndpoint = `${keycloakConfig.url.replace(/\/+$/, "")}${uri}`;

        const grantType = hasPasswordCredentials ? "password" : "client_credentials";
        const urlencoded = new URLSearchParams({
            grant_type: grantType,
            client_id: keycloakConfig.clientId,
            scope: "openid",
        });
        if (process.env.EXPRESS_APP_CLIENT_SECRET) {
            urlencoded.set("client_secret", process.env.EXPRESS_APP_CLIENT_SECRET);
        }
        if (grantType === "password") {
            urlencoded.set("username", process.env.EXPRESS_APP_USERNAME);
            urlencoded.set("password", process.env.EXPRESS_APP_PASSWORD);
        }

        this._loginInFlight = (async () => {
            try {
                const response = await axios.post(tokenEndpoint, urlencoded.toString(), {
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    timeout: 15000,
                });

                if (response.status !== 200 || !response.data?.access_token) {
                    const errorMsg = response.data?.error_description || response.data?.error || `HTTP ${response.status}`;
                    console.error(`[Keycloak] Login failed: ${errorMsg}`);
                    this.clearTokenData();
                    return Promise.reject(new Error(`Keycloak login failed: ${errorMsg}`));
                }

                this.setToken(response.data.access_token);
                return response.data.access_token;
            } catch (error) {
                const msg = error?.response?.data?.error_description
                    || error?.response?.data?.error
                    || error.message;
                console.error(`[Keycloak] Login failed: ${msg}`);
                this.clearTokenData();
                return Promise.reject(error);
            } finally {
                this._loginInFlight = null;
            }
        })();

        return this._loginInFlight;
    }

    getToken() {
        return this.keycloak?.token || null;
    }

    setToken(token) {
        const now = Date.now();

        if (this.keycloak?.tokenTimeoutHandle) {
            clearTimeout(this.keycloak.tokenTimeoutHandle);
            this.keycloak.tokenTimeoutHandle = null;
        }

        if (token) {
            this.keycloak.token = token;

            try {
                this.keycloak.tokenParsed = jwtDecode(token);
            } catch (error) {
                this.clearTokenData();
                return;
            }

            this.keycloak.timeSkew = Math.floor(now / 1000) - (this.keycloak.tokenParsed?.iat || Math.floor(now / 1000));

            const expiresMs = Math.max(0, Math.floor((this.keycloak.tokenParsed.exp - now / 1000 + this.keycloak.timeSkew) * 1000));
            const earlyMs = Math.min(expiresMs, Math.max(30000, Math.floor(expiresMs * 0.05)));
            const refreshIn = Math.max(0, expiresMs - earlyMs);

            if (expiresMs <= 0) {
                this.implicitLogin().catch(() => {});
            } else {
                this.keycloak.tokenTimeoutHandle = setTimeout(() => {
                    this.implicitLogin().catch(() => {});
                }, refreshIn);
            }
        } else {
            this.clearTokenData();
        }
    }

    clearTokenData() {
        if (!this.keycloak) return;

        if (this.keycloak.tokenTimeoutHandle) {
            clearTimeout(this.keycloak.tokenTimeoutHandle);
            this.keycloak.tokenTimeoutHandle = null;
        }
        delete this.keycloak.token;
        delete this.keycloak.tokenParsed;
        this.keycloak.timeSkew = 0;
    }
}

export default new KeycloakUtil();
