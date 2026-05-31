import crypto from "crypto";
import {config} from "./config.mjs";

const isSecureReq = (req) => config.env === "production"
    ? (req.headers["x-forwarded-proto"] === "https" || req.secure)
    : false;

export const issueCsrf = (req, res) => {
    const existing = req.cookies?.["XSRF-TOKEN"];
    const csrfToken = existing || crypto.randomBytes(32).toString("hex");
    res.cookie("XSRF-TOKEN", csrfToken, {
        httpOnly: false,
        secure: config.secureCookies && isSecureReq(req),
        sameSite: "lax",
        path: "/",
    });
    res.json({csrfToken});
};

export const requireCsrf = (req, res, next) => {
    const method = (req.method || "GET").toUpperCase();
    if (!["POST", "PUT", "PATCH", "DELETE"].includes(method)) return next();
    if (req.path === "/refresh") return next();

    const csrfCookie = req.cookies?.["XSRF-TOKEN"];
    const csrfHeader = req.get("X-CSRF-Token");
    if (!csrfCookie || !csrfHeader || csrfCookie !== csrfHeader) {
        return res.status(403).json({error: "Invalid CSRF token"});
    }
    return next();
};
