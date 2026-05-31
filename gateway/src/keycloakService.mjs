import {config} from "./config.mjs";
import {httpClient} from "./httpClient.mjs";
import {buildUserFromToken, isTokenExpired} from "./jwt.mjs";

const tokenEndpoint = () =>
    `${config.keycloak.url}/realms/${config.keycloak.realm}/protocol/openid-connect/token`;

const adminTokenEndpoint = () =>
    `${config.keycloak.url}/realms/master/protocol/openid-connect/token`;

const extractKeycloakError = (response, fallback = "Keycloak request failed") =>
    response?.data?.error_description || response?.data?.errorMessage || response?.data?.error || response?.data?.message || fallback;

class KeycloakService {
    constructor() {
        this.serviceToken = null;
        this.serviceLoginInFlight = null;
    }

    async authenticateUser(email, password) {
        const credentials = new URLSearchParams({
            grant_type: "password",
            client_id: config.keycloak.selfcareClientId,
            username: email,
            password,
            scope: "openid",
        });
        if (config.keycloak.selfcareClientSecret) {
            credentials.set("client_secret", config.keycloak.selfcareClientSecret);
        }

        const response = await httpClient.post(tokenEndpoint(), credentials.toString(), {
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
        });

        if (response.status !== 200 || !response.data?.access_token) {
            const error = new Error(response.status === 401 ? "Invalid credentials" : extractKeycloakError(response, "Authentication failed"));
            error.status = response.status || 400;
            throw error;
        }

        return {
            accessToken: response.data.access_token,
            refreshToken: response.data.refresh_token,
            user: buildUserFromToken(response.data.access_token),
        };
    }

    async refreshUserToken(refreshToken) {
        const credentials = new URLSearchParams({
            grant_type: "refresh_token",
            refresh_token: refreshToken,
            client_id: config.keycloak.selfcareClientId,
        });
        if (config.keycloak.selfcareClientSecret) {
            credentials.set("client_secret", config.keycloak.selfcareClientSecret);
        }

        const response = await httpClient.post(tokenEndpoint(), credentials.toString(), {
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
        });

        if (response.status !== 200 || !response.data?.access_token) {
            const error = new Error(extractKeycloakError(response, "Token refresh failed"));
            error.status = response.status || 401;
            error.invalidGrant = response.data?.error === "invalid_grant";
            throw error;
        }

        return {
            accessToken: response.data.access_token,
            refreshToken: response.data.refresh_token,
            user: buildUserFromToken(response.data.access_token),
        };
    }

    async getServiceToken() {
        if (this.serviceToken && !isTokenExpired(this.serviceToken, config.tokenExpiryBufferSeconds)) {
            return this.serviceToken;
        }

        if (this.serviceLoginInFlight) return this.serviceLoginInFlight;

        const credentials = new URLSearchParams({
            grant_type: "client_credentials",
            client_id: config.keycloak.gatewayClientId,
            client_secret: config.keycloak.gatewayClientSecret,
            scope: "openid",
        });

        this.serviceLoginInFlight = (async () => {
            const response = await httpClient.post(tokenEndpoint(), credentials.toString(), {
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
            });

            if (response.status !== 200 || !response.data?.access_token) {
                this.serviceToken = null;
                throw new Error(extractKeycloakError(response, "Gateway service-token login failed"));
            }

            this.serviceToken = response.data.access_token;
            return this.serviceToken;
        })();

        try {
            return await this.serviceLoginInFlight;
        } finally {
            this.serviceLoginInFlight = null;
        }
    }

    async registerUser(userData) {
        const {firstName, lastName, email, password} = userData || {};
        if (!firstName || !email || !password) {
            return {success: false, code: "VALIDATION_ERROR", message: "First name, email, and password are required"};
        }
        if (!config.keycloak.adminUsername || !config.keycloak.adminPassword) {
            return {success: false, code: "ADMIN_DISABLED", message: "Registration is not configured"};
        }

        const adminToken = await this.getAdminToken();
        const canonicalEmail = email.trim().toLowerCase();
        const relatedPartyId = canonicalEmail.replace(/[^A-Za-z0-9_-]/g, "-");

        const response = await httpClient.post(
            `${config.keycloak.url}/admin/realms/${config.keycloak.realm}/users`,
            {
                username: canonicalEmail,
                email: canonicalEmail,
                firstName,
                lastName: lastName || "",
                enabled: true,
                emailVerified: true,
                credentials: [{type: "password", value: password, temporary: false}],
                attributes: {relatedPartyId: [relatedPartyId], relatedPartyRole: ["prospect"]},
            },
            {headers: {Authorization: `Bearer ${adminToken}`, "Content-Type": "application/json"}},
        );

        if (response.status === 409) {
            return {success: false, code: "USER_EXISTS", message: "User already exists"};
        }
        if (response.status < 200 || response.status >= 300) {
            return {success: false, message: extractKeycloakError(response, "Registration failed")};
        }

        return {success: true};
    }

    async updateRelatedPartyRole(email, relatedPartyRole) {
        if (!email || !relatedPartyRole) {
            const error = new Error("Email and related party role are required");
            error.status = 400;
            throw error;
        }
        if (!config.keycloak.adminUsername || !config.keycloak.adminPassword) {
            const error = new Error("Related party updates are not configured");
            error.status = 500;
            throw error;
        }

        const adminToken = await this.getAdminToken();
        const searchResponse = await httpClient.get(`${config.keycloak.url}/admin/realms/${config.keycloak.realm}/users`, {
            headers: {Authorization: `Bearer ${adminToken}`},
            params: {email, exact: true},
        });
        const user = searchResponse.data?.[0];
        if (!user?.id) {
            const error = new Error("User not found");
            error.status = 404;
            throw error;
        }

        const userResponse = await httpClient.get(`${config.keycloak.url}/admin/realms/${config.keycloak.realm}/users/${user.id}`, {
            headers: {Authorization: `Bearer ${adminToken}`},
        });
        const payload = {
            ...userResponse.data,
            attributes: {
                ...(userResponse.data?.attributes || {}),
                relatedPartyRole: [relatedPartyRole],
            },
        };

        const updateResponse = await httpClient.put(
            `${config.keycloak.url}/admin/realms/${config.keycloak.realm}/users/${user.id}`,
            payload,
            {headers: {Authorization: `Bearer ${adminToken}`, "Content-Type": "application/json"}},
        );

        if (updateResponse.status < 200 || updateResponse.status >= 300) {
            const error = new Error(extractKeycloakError(updateResponse, "Failed to update related party role"));
            error.status = updateResponse.status;
            throw error;
        }

        return {success: true, message: "Related party role updated successfully"};
    }

    async getAdminToken() {
        const credentials = new URLSearchParams({
            grant_type: "password",
            client_id: "admin-cli",
            username: config.keycloak.adminUsername,
            password: config.keycloak.adminPassword,
        });
        const response = await httpClient.post(adminTokenEndpoint(), credentials.toString(), {
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
        });
        if (response.status !== 200 || !response.data?.access_token) {
            throw new Error(extractKeycloakError(response, "Failed to obtain admin token"));
        }
        return response.data.access_token;
    }
}

export const keycloakService = new KeycloakService();
