// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {authService} from "../services/authService.js";

export const decodeJwtPayload = (token) => {
    try {
        const [, payload] = token.split(".");
        if (!payload) return null;
        return JSON.parse(Buffer.from(payload, "base64url").toString("utf8"));
    } catch {
        return null;
    }
};

export const isTokenExpired = (token, bufferSeconds = 30) => {
    const payload = decodeJwtPayload(token);
    if (!payload?.exp) return true;
    const now = Math.floor(Date.now() / 1000);
    return payload.exp <= (now + bufferSeconds);
};

export const isRefreshTokenExpired = (refreshToken, bufferSeconds = 0) => {
    if (!refreshToken) return true;
    const payload = decodeJwtPayload(refreshToken);
    if (!payload?.exp) return true;
    const now = Math.floor(Date.now() / 1000);
    return payload.exp <= (now + bufferSeconds);
};

export const buildUserFromToken = (accessToken) => {
    const payload = decodeJwtPayload(accessToken) || {};
    return {
        username: payload.given_name || null,
        email: payload.email || null,
        relatedPartyId: payload.relatedPartyId || null,
        relatedPartyRole: payload.relatedPartyRole || null,
    };
};

export const saveSession = (req) => {
    return new Promise((resolve, reject) => {
        req.session.save((err) => {
            if (err) reject(err);
            else resolve();
        });
    });
};

export const saveUserSession = async (req, {accessToken, refreshToken}) => {
    req.session.tokens = {accessToken, refreshToken};
    req.session.user = buildUserFromToken(accessToken);
    req.session.serviceSession = false;
    await saveSession(req);
};

export const clearUserSession = async (req) => {
    delete req.session.tokens;
    delete req.session.user;
    delete req.session.serviceSession;
    await saveSession(req).catch(() => {
    });
};

const refreshLocks = new Map();

const LOCK_CLEANUP_TIMEOUT = 15000;

export const refreshUserSession = async (req) => {
    const sessionId = req.sessionID;

    if (refreshLocks.has(sessionId)) {
        return refreshLocks.get(sessionId);
    }

    const refreshPromise = (async () => {
        const refreshToken = req.session?.tokens?.refreshToken;
        if (!refreshToken) {
            console.warn("[TokenManager] No refresh token available for session", sessionId);
            return {token: null, expired: true};
        }

        if (isRefreshTokenExpired(refreshToken)) {
            console.warn("[TokenManager] Refresh token is expired for session", sessionId);
            await clearUserSession(req);
            return {token: null, expired: true};
        }

        try {
            const result = await authService.refreshToken(refreshToken);

            if (!result?.token) {
                console.error("[TokenManager] Refresh returned no token");
                await clearUserSession(req);
                return {token: null, expired: false};
            }

            await saveUserSession(req, {
                accessToken: result.token,
                refreshToken: result.refreshToken,
            });

            console.log("[TokenManager] Session refreshed successfully");
            return {token: result.token, expired: false};
        } catch (error) {
            console.error("[TokenManager] Refresh failed:", error.message);
            await clearUserSession(req);

            const isInvalidGrant = error.message?.includes("invalid_grant") ||
                error.message?.includes("Token is not active") ||
                error.message?.includes("Session not active");

            return {token: null, expired: isInvalidGrant};
        } finally {
            refreshLocks.delete(sessionId);
        }
    })();

    refreshLocks.set(sessionId, refreshPromise);

    setTimeout(() => {
        if (refreshLocks.get(sessionId) === refreshPromise) {
            refreshLocks.delete(sessionId);
        }
    }, LOCK_CLEANUP_TIMEOUT);

    return refreshPromise;
};