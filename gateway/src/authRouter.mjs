import express from "express";
import {issueCsrf, requireCsrf} from "./csrf.mjs";
import {keycloakService} from "./keycloakService.mjs";
import {isTokenExpired} from "./jwt.mjs";
import {clearUserSession, saveSession} from "./session.mjs";
import {refreshUserSession, saveUserSession} from "./tokenManager.mjs";

const router = express.Router();

router.get("/csrf", issueCsrf);

router.post("/login", requireCsrf, async (req, res) => {
    try {
        const {email, password} = req.body || {};
        if (!email || !password) return res.status(400).json({error: "Email and password are required"});

        const result = await keycloakService.authenticateUser(email, password);
        await saveUserSession(req, {
            accessToken: result.accessToken,
            refreshToken: result.refreshToken,
        });

        res.json({success: true, user: req.session.user, serviceSession: false});
    } catch (error) {
        res.status(error.status === 401 ? 400 : (error.status || 400)).json({error: error.message || "Login failed"});
    }
});

router.post("/register", requireCsrf, async (req, res) => {
    try {
        const result = await keycloakService.registerUser(req.body);
        if (result.success) {
            return res.status(201).json({success: true, message: "Registered successfully. Please log in."});
        }
        res.status(400).json({success: false, code: result.code, error: result.message});
    } catch (error) {
        res.status(500).json({success: false, error: error.message || "Registration failed"});
    }
});

router.put("/related-party-role", requireCsrf, async (req, res) => {
    try {
        const {email, relatedPartyRole} = req.body || {};
        const result = await keycloakService.updateRelatedPartyRole(email, relatedPartyRole);
        if (req.session?.tokens?.refreshToken) {
            await refreshUserSession(req);
        }
        res.json({
            success: result.success,
            message: result.message,
            ...(req.session.user ? {user: req.session.user, serviceSession: false} : {}),
        });
    } catch (error) {
        res.status(error.status || 400).json({error: error.message || "Role update failed"});
    }
});

router.post("/refresh", async (req, res) => {
    const refreshToken = req.session?.tokens?.refreshToken;
    if (!refreshToken) {
        await clearUserSession(req);
        return res.status(401).json({
            error: "Session expired",
            code: "FULL_SESSION_EXPIRED",
            message: "Both tokens expired. Please log in again.",
        });
    }

    const result = await refreshUserSession(req);
    if (!result?.token) {
        return res.status(401).json({
            error: "Session expired",
            code: result?.expired ? "FULL_SESSION_EXPIRED" : "REFRESH_FAILED",
            message: result?.expired ? "Both tokens expired. Please log in again." : "Please log in again",
        });
    }

    res.json({success: true, user: req.session.user, serviceSession: false});
});

router.get("/me", async (req, res) => {
    const accessToken = req.session?.tokens?.accessToken;
    const refreshToken = req.session?.tokens?.refreshToken;
    if (!accessToken || !refreshToken) {
        return res.status(401).json({error: "Not authenticated"});
    }

    if (isTokenExpired(accessToken)) {
        return res.status(401).json({error: "Token expired", code: "TOKEN_EXPIRED"});
    }

    res.json({user: req.session.user, serviceSession: false});
});

router.get("/status", (req, res) => {
    const accessToken = req.session?.tokens?.accessToken;
    if (accessToken && !isTokenExpired(accessToken)) {
        return res.json({
            authenticated: true,
            username: req.session.user?.username,
            name: req.session.user?.username,
            email: req.session.user?.email,
            serviceSession: false,
        });
    }
    res.status(401).json({authenticated: false});
});

router.post("/logout", requireCsrf, (req, res) => {
    req.session.destroy((err) => {
        if (err) console.error("[GatewayAuth] Session destruction failed:", err);
        res.clearCookie("poc-gateway.sid", {path: "/"});
        res.clearCookie("XSRF-TOKEN", {path: "/"});
        res.json({success: true});
    });
});

router.use((_req, res) => res.status(404).json({error: "Auth route not found"}));

export default router;
