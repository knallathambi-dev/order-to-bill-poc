// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import axios from "../utils/axios.mjs";

const KEYCLOAK_REALM = "SpringBootKeycloak";
const KEYCLOAK_MASTER_REALM = "master";
const KEYCLOAK_CLIENT_ID = "gateway";
const KEYCLOAK_ADMIN_CLIENT_ID = "admin-cli";
const DEFAULT_CLIENT_ROLE = "SelfCareAdmin";

const generateRandomId = (length = 9) => {
    return Math.random().toString(36).slice(2, 2 + length);
};

const extractKeycloakError = (error, fallback = "Unknown error") => {
    const data = error?.response?.data;
    if (!data) return error?.message || fallback;
    return data.error_description || data.errorMessage || data.error || data.message || fallback;
};

const getKeycloakAdminToken = async () => {
    const tokenEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/realms/${KEYCLOAK_MASTER_REALM}/protocol/openid-connect/token`;

    const credentials = new URLSearchParams({
        grant_type: "password",
        client_id: KEYCLOAK_ADMIN_CLIENT_ID,
        username: process.env.EXPRESS_APP_ADMIN_USERNAME,
        password: process.env.EXPRESS_APP_ADMIN_PASSWORD,
    });

    try {
        const {data} = await axios.post(tokenEndpoint, credentials.toString(), {
            headers: {"Content-Type": "application/x-www-form-urlencoded"}
        });
        return data.access_token;
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to obtain admin token");
        console.error("[AuthService] Admin token error:", msg);
        throw new Error("Failed to obtain admin token");
    }
};

const createKeycloakUser = async (adminToken, userData) => {
    const createUserEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/users`;
    const relatedPartyId = generateRandomId(9);

    const keycloakUserPayload = {
        username: userData.email,
        email: userData.email,
        firstName: userData.firstName,
        lastName: userData.lastName || "",
        enabled: true,
        emailVerified: true,
        credentials: [{
            type: "password",
            value: userData.password,
            temporary: false,
        }],
        attributes: {
            relatedPartyId: [relatedPartyId],
            relatedPartyRole: ["prospect"]
        }
    };

    try {
        const response = await axios.post(createUserEndpoint, keycloakUserPayload, {
            headers: {
                Authorization: `Bearer ${adminToken}`,
                "Content-Type": "application/json",
            },
        });

        const locationHeader = response.headers.location;
        if (!locationHeader) {
            throw new Error("Location header missing from response");
        }

        const userId = locationHeader.split("/").pop();
        if (!userId) {
            throw new Error("Failed to extract user ID");
        }

        return userId;
    } catch (error) {
        const status = error?.response?.status;
        const msg = extractKeycloakError(error, "Failed to create user");

        if (status === 409) {
            console.error("[AuthService] User already exists:", msg);
            throw new Error("User already exists");
        }

        console.error("[AuthService] Create user error:", msg);
        throw new Error("Failed to create user");
    }
};

const getKeycloakClientRoles = async (adminToken) => {
    const rolesEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/clients/${process.env.EXPRESS_APP_SERVICE_CLIENT_ID}/roles`;

    try {
        const {data: roles} = await axios.get(rolesEndpoint, {
            headers: {Authorization: `Bearer ${adminToken}`},
        });
        return roles;
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to retrieve client roles");
        console.error("[AuthService] Get roles error:", msg);
        throw new Error("Failed to retrieve client roles");
    }
};

const assignClientRoleToUser = async (adminToken, userId, role) => {
    if (!role) return;

    const assignRolesEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/users/${userId}/role-mappings/clients/${process.env.EXPRESS_APP_SERVICE_CLIENT_ID}`;

    try {
        await axios.post(assignRolesEndpoint, [role], {
            headers: {
                Authorization: `Bearer ${adminToken}`,
                "Content-Type": "application/json",
            },
        });
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to assign role to user");
        console.error("[AuthService] Assign role error:", msg);
        throw new Error("Failed to assign role to user");
    }
};

const getUserTokensFromKeycloak = async (email, password) => {
    const tokenEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token`;

    const credentials = new URLSearchParams({
        username: email,
        password: password,
        grant_type: "password",
        client_id: KEYCLOAK_CLIENT_ID,
        client_secret: process.env.EXPRESS_APP_CLIENT_SECRET,
        scope: "openid",
    });

    try {
        const {data} = await axios.post(tokenEndpoint, credentials.toString(), {
            headers: {"Content-Type": "application/x-www-form-urlencoded"}
        });
        return data;
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to obtain user tokens");
        console.error("[AuthService] User token error:", msg);
        throw error;
    }
};

const getUserIdByEmail = async (adminToken, email) => {
    const searchEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/users`;

    try {
        const {data: users} = await axios.get(searchEndpoint, {
            headers: {Authorization: `Bearer ${adminToken}`},
            params: {email, exact: true}
        });

        if (!users || users.length === 0) {
            throw new Error("User not found");
        }

        return users[0].id;
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to find user");
        console.error("[AuthService] Find user error:", msg);
        throw new Error("Failed to find user");
    }
};

const updateUserAttributes = async (adminToken, userId, attributes) => {
    const endpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/users/${userId}`;

    try {
        const {data: user} = await axios.get(endpoint, {
            headers: {Authorization: `Bearer ${adminToken}`},
        });

        const payload = {...user, attributes};

        await axios.put(endpoint, payload, {
            headers: {
                Authorization: `Bearer ${adminToken}`,
                "Content-Type": "application/json",
            },
        });
    } catch (error) {
        const msg = extractKeycloakError(error, "Failed to update user attributes");
        console.error("[AuthService] Update attributes error:", msg);
        throw new Error("Failed to update user attributes");
    }
};

export const authService = {
    async authenticateUser(email, password) {
        if (!email || !password) {
            throw new Error("Email and password are required");
        }

        try {
            const tokenData = await getUserTokensFromKeycloak(email, password);
            return {
                token: tokenData.access_token,
                refreshToken: tokenData.refresh_token,
                expiresIn: tokenData.expires_in
            };
        } catch (error) {
            const message = error?.response?.data?.error_description ||
                error?.response?.data?.error ||
                "Authentication failed";

            console.error(`[AuthService] Authentication failed for ${email}:`, message);
            throw new Error(error?.response?.status === 401 ? "Invalid credentials" : message);
        }
    },

    async registerUser(userData) {
        const {firstName, lastName, email, password} = userData;
        if (!firstName || !email || !password) {
            return {success: false, code: "VALIDATION_ERROR", message: "First name, email, and password are required"};
        }

        const canonicalEmail = email.trim().toLowerCase();
        try {
            const adminToken = await getKeycloakAdminToken();

            const userId = await createKeycloakUser(adminToken, {
                firstName,
                lastName,
                email: canonicalEmail,
                password
            });

            const roles = await getKeycloakClientRoles(adminToken);
            const role = roles.find(r => r.name === DEFAULT_CLIENT_ROLE);
            if (role) await assignClientRoleToUser(adminToken, userId, role);

            return {success: true};
        } catch (error) {
            return {success: false, message: error?.message || "Registration failed"};
        }
    },

    async refreshToken(refreshToken) {
        if (!refreshToken) {
            throw new Error("Refresh token is required");
        }

        const tokenEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token`;

        const credentials = new URLSearchParams({
            grant_type: "refresh_token",
            refresh_token: refreshToken,
            client_id: KEYCLOAK_CLIENT_ID,
            client_secret: process.env.EXPRESS_APP_CLIENT_SECRET,
        });

        try {
            const {data} = await axios.post(tokenEndpoint, credentials.toString(), {
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
            });

            return {
                token: data.access_token,
                refreshToken: data.refresh_token,
                expiresIn: data.expires_in,
            };
        } catch (error) {
            const message = extractKeycloakError(error, "Token refresh failed");
            console.error("[AuthService] Token refresh failed:", message);
            throw new Error(message);
        }
    },

    async updateRelatedPartyRole(email, relatedPartyRole) {
        try {
            const adminToken = await getKeycloakAdminToken();
            const userId = await getUserIdByEmail(adminToken, email);

            const getUserEndpoint = `${process.env.EXPRESS_APP_KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/users/${userId}`;
            const {data: user} = await axios.get(getUserEndpoint, {
                headers: {Authorization: `Bearer ${adminToken}`},
            });

            const updatedAttributes = {
                ...(user.attributes || {}),
                relatedPartyRole: [relatedPartyRole],
            };

            await updateUserAttributes(adminToken, userId, updatedAttributes);

            return {success: true, message: "Related party role updated successfully"};
        } catch (error) {
            const message = error?.response?.data?.errorMessage ||
                error?.response?.data?.error ||
                error.message ||
                "Failed to update related party role";

            console.error(`[AuthService] Failed to update role for ${email}:`, message);
            throw new Error(message);
        }
    }
};