// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import express from "express";
import crypto from "crypto";
import {authService} from "./services/authService.js";
import keycloakUtil from "./utils/KeycloakUtil.mjs";
import {
    clearUserSession,
    isRefreshTokenExpired,
    isTokenExpired,
    refreshUserSession,
    saveSession,
    saveUserSession,
} from "./utils/tokenManager.mjs";

const router = express.Router();

const isSecureReq = (req) => {
    const isProd = process.env.NODE_ENV === "production";
    return isProd ? (req.headers["x-forwarded-proto"] === "https" || req.secure) : false;
};

const saveServiceSession = async (req, accessToken) => {
    req.session.tokens = {accessToken, refreshToken: null};
    req.session.user = {username: null, email: null, relatedPartyId: null, relatedPartyRole: null};
    req.session.serviceSession = true;
    await saveSession(req);
};

const establishServiceSession = async (req) => {
    try {
        const accessToken = keycloakUtil.getToken() || await keycloakUtil.implicitLogin();
        if (accessToken) {
            await saveServiceSession(req, accessToken);
            return true;
        }
    } catch (error) {
        console.error("[Auth] Service session failed:", error.message);
    }
    return false;
};

router.get("/csrf", (req, res) => {
    const existing = req.cookies?.["XSRF-TOKEN"];
    const csrfToken = existing || crypto.randomBytes(32).toString("hex");
    res.cookie("XSRF-TOKEN", csrfToken, {
        httpOnly: false,
        secure: isSecureReq(req),
        sameSite: "lax",
        path: "/",
    });
    res.json({csrfToken});
});

const requireCsrf = (req, res, next) => {
    if (req.path === "/refresh") return next();

    const method = (req.method || "GET").toUpperCase();
    if (!["POST", "PUT", "PATCH", "DELETE"].includes(method)) return next();

    const csrfCookie = req.cookies?.["XSRF-TOKEN"];
    const csrfHeader = req.get("X-CSRF-Token");

    if (!csrfCookie || !csrfHeader || csrfCookie !== csrfHeader) {
        return res.status(403).json({error: "Invalid CSRF token"});
    }
    return next();
};

router.post("/login", requireCsrf, async (req, res) => {
    try {
        const {email, password} = req.body || {};
        if (!email || !password) {
            return res.status(400).json({error: "Email and password are required"});
        }

        const result = await authService.authenticateUser(email, password);
        await saveUserSession(req, {accessToken: result.token, refreshToken: result.refreshToken});

        return res.json({success: true, user: req.session.user, serviceSession: false});
    } catch (error) {
        console.error("[Auth] Login failed:", error.message);
        return res.status(400).json({error: error.message || "Login failed"});
    }
});

router.post("/register", requireCsrf, async (req, res) => {
    try {
        const result = await authService.registerUser(req.body);
        if (result.success) {
            return res.status(201).json({success: true, message: "Registered successfully. Please log in."});
        }
        return res.status(400).json({success: false, code: result.code, error: result.message});
    } catch (error) {
        console.error("[Auth] Registration failed:", error.message);
        return res.status(500).json({success: false, error: "Registration failed"});
    }
});

router.put("/related-party-role", requireCsrf, async (req, res) => {
    try {
        const {email, relatedPartyRole} = req.body;
        if (!email) return res.status(400).json({error: "Email is required"});
        if (!relatedPartyRole) return res.status(400).json({error: "Related party role is required"});

        const result = await authService.updateRelatedPartyRole(email, relatedPartyRole);

        const refreshToken = req.session.tokens?.refreshToken;
        if (refreshToken) {
            const refreshResult = await refreshUserSession(req);
            if (!refreshResult?.token) {
                console.warn("[Auth] Token refresh after role update failed");
            }
        }

        return res.json({
            success: result.success,
            message: result.message,
            ...(req.session.user ? {user: req.session.user, serviceSession: false} : {}),
        });
    } catch (error) {
        console.error("[Auth] Role update failed:", error.message);
        return res.status(400).json({error: error.message});
    }
});

router.post("/refresh", async (req, res) => {
    try {
        const refreshToken = req.session.tokens?.refreshToken;
        const isServiceSession = req.session.serviceSession === true;

        if (!refreshToken && isServiceSession) {
            const serviceToken = req.session.tokens?.accessToken;
            if (serviceToken && !isTokenExpired(serviceToken)) {
                return res.json({success: true, user: req.session.user, serviceSession: true});
            }
            const established = await establishServiceSession(req);
            if (established) {
                return res.json({success: true, user: req.session.user, serviceSession: true});
            }
            return res.status(401).json({error: "Session expired"});
        }

        if (!refreshToken) {
            if (req.session.tokens?.accessToken) {
                await clearUserSession(req);
            }
            return res.status(401).json({
                error: "Session expired",
                code: "FULL_SESSION_EXPIRED",
                message: "Both tokens expired. Please log in again."
            });
        }

        if (isRefreshTokenExpired(refreshToken)) {
            console.warn("[Auth] Refresh token is expired, clearing session");
            await clearUserSession(req);
            return res.status(401).json({
                error: "Session expired",
                code: "FULL_SESSION_EXPIRED",
                message: "Both tokens expired. Please log in again."
            });
        }

        const refreshResult = await refreshUserSession(req);

        if (!refreshResult?.token) {
            if (refreshResult?.expired) {
                return res.status(401).json({
                    error: "Session expired",
                    code: "FULL_SESSION_EXPIRED",
                    message: "Both tokens expired. Please log in again."
                });
            }

            return res.status(401).json({
                error: "Session expired",
                code: "REFRESH_FAILED",
                message: "Please log in again"
            });
        }

        return res.json({success: true, user: req.session.user, serviceSession: false});

    } catch (error) {
        console.error("[Auth] Refresh failed:", error.message);
        await clearUserSession(req);
        return res.status(401).json({
            error: "Session expired",
            code: "REFRESH_ERROR",
            message: "Please log in again"
        });
    }
});

router.get("/me", async (req, res) => {
    const accessToken = req.session.tokens?.accessToken;

    if (accessToken) {
        if (isTokenExpired(accessToken)) {
            return res.status(401).json({
                error: "Token expired",
                code: "TOKEN_EXPIRED",
            });
        }

        return res.json({
            user: req.session.user,
            serviceSession: req.session.serviceSession ?? false,
        });
    }

    const established = await establishServiceSession(req);
    if (established) {
        return res.json({user: req.session.user, serviceSession: true});
    }
    return res.status(401).json({error: "Not authenticated"});
});

router.get("/status", async (req, res) => {
    const accessToken = req.session.tokens?.accessToken;

    if (accessToken && !isTokenExpired(accessToken)) {
        return res.json({
            authenticated: true,
            username: req.session.user?.username,
            name: req.session.user?.username,
            email: req.session.user?.email,
            serviceSession: req.session.serviceSession ?? false,
        });
    }

    const established = await establishServiceSession(req);
    if (established) {
        return res.json({
            authenticated: true,
            username: "phase7-bridge",
            name: "Phase 7 Bridge",
            email: null,
            serviceSession: true,
        });
    }

    return res.status(401).json({authenticated: false});
});

router.post("/logout", requireCsrf, (req, res) => {
    req.session.destroy((err) => {
        if (err) {
            console.error("[Auth] Session destruction failed:", err);
        }
        res.clearCookie("selfcare.sid", {path: "/"});
        res.clearCookie("XSRF-TOKEN", {path: "/"});
        return res.json({success: true});
    });
});

export default router;
