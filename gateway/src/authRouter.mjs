import express from "express";
import {issueCsrf, requireCsrf} from "./csrf.mjs";
import {keycloakService} from "./keycloakService.mjs";
import {isTokenExpired} from "./jwt.mjs";
import {clearUserSession, saveSession} from "./session.mjs";
import {refreshUserSession, saveUserSession} from "./tokenManager.mjs";

const router = express.Router();

const escapeHtml = (value = "") => String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");

const safeRedirectUri = (redirectUri) => {
    if (!redirectUri) return "/";
    try {
        const parsed = new URL(redirectUri);
        if (["http://localhost:3000", "http://localhost:3004", "http://localhost:3006"].includes(parsed.origin)) {
            return parsed.toString();
        }
    } catch (_error) {
        if (String(redirectUri).startsWith("/")) return redirectUri;
    }
    return "/";
};

const renderLoginPage = (redirectUri = "/") => `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>POC Gateway Login</title>
  <style>
    :root { color-scheme: light; font-family: Arial, Helvetica, sans-serif; }
    body { min-height: 100vh; margin: 0; display: grid; place-items: center; background: #f4f6f8; color: #1f2933; }
    main { width: min(420px, calc(100vw - 32px)); background: white; border: 1px solid #d8dee4; border-radius: 8px; padding: 28px; box-shadow: 0 12px 36px rgba(15, 23, 42, 0.08); }
    h1 { font-size: 22px; margin: 0 0 8px; }
    p { margin: 0 0 22px; color: #52606d; }
    label { display: block; font-weight: 600; margin: 14px 0 6px; }
    input { width: 100%; box-sizing: border-box; border: 1px solid #bcccdc; border-radius: 6px; padding: 11px 12px; font-size: 15px; }
    button { width: 100%; margin-top: 20px; border: 0; border-radius: 6px; padding: 12px; background: #f16e00; color: white; font-weight: 700; font-size: 15px; cursor: pointer; }
    button:disabled { opacity: 0.65; cursor: wait; }
    .error { min-height: 20px; margin-top: 14px; color: #ba2525; font-size: 14px; }
  </style>
</head>
<body>
  <main>
    <h1>Sign in</h1>
    <p>Use your local Order-to-Bill POC account.</p>
    <form id="login-form">
      <input type="hidden" id="redirect-uri" value="${escapeHtml(safeRedirectUri(redirectUri))}">
      <label for="email">Email</label>
      <input id="email" name="email" type="email" autocomplete="username" value="admin@otb.com" required autofocus>
      <label for="password">Password</label>
      <input id="password" name="password" type="password" autocomplete="current-password" required>
      <button type="submit">Sign in</button>
      <div class="error" id="error" role="alert"></div>
    </form>
  </main>
  <script>
    const form = document.getElementById("login-form");
    const errorBox = document.getElementById("error");
    const button = form.querySelector("button");
    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      errorBox.textContent = "";
      button.disabled = true;
      try {
        const csrfResponse = await fetch("/auth/csrf", { credentials: "include" });
        const { csrfToken } = await csrfResponse.json();
        const response = await fetch("/auth/login", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            "X-CSRF-Token": csrfToken,
          },
          body: JSON.stringify({
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
          }),
        });
        if (!response.ok) {
          const payload = await response.json().catch(() => ({}));
          throw new Error(payload.error || "Login failed");
        }
        window.location.href = document.getElementById("redirect-uri").value || "/";
      } catch (error) {
        errorBox.textContent = error.message || "Login failed";
        button.disabled = false;
      }
    });
  </script>
</body>
</html>`;

router.get("/csrf", issueCsrf);

router.get("/login", (req, res) => {
    res
        .set("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; base-uri 'self'; form-action 'self'; frame-ancestors 'self'")
        .type("html")
        .send(renderLoginPage(req.query.redirect_uri));
});

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

router.get("/logout", (req, res) => {
    const redirectUri = safeRedirectUri(req.query.redirect_uri);
    req.session.destroy((err) => {
        if (err) console.error("[GatewayAuth] Session destruction failed:", err);
        res.clearCookie("poc-gateway.sid", {path: "/"});
        res.clearCookie("XSRF-TOKEN", {path: "/"});
        res.redirect(redirectUri);
    });
});

router.use((_req, res) => res.status(404).json({error: "Auth route not found"}));

export default router;
