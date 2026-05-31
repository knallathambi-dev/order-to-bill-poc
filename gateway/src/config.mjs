import dotenv from "dotenv";
import path from "path";
import {fileURLToPath} from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

dotenv.config({path: path.join(__dirname, "../env/.env")});

const truthy = (value) => ["1", "true", "yes", "on"].includes(String(value || "").toLowerCase());

export const config = {
    env: process.env.NODE_ENV || "development",
    port: Number(process.env.PORT || process.env.POC_GATEWAY_PORT || 8088),
    sessionSecret: process.env.SESSION_SECRET || "otb-poc-local-session-secret",
    internalSecret: process.env.INTERNAL_SECRET || "",
    allowedOrigins: (process.env.ALLOWED_ORIGINS || "http://localhost:3000,http://localhost:3004,http://localhost:3006")
        .split(",")
        .map((origin) => origin.trim())
        .filter(Boolean),
    requestTimeoutMs: Number(process.env.GATEWAY_REQUEST_TIMEOUT_MS || 60000),
    tokenExpiryBufferSeconds: Number(process.env.TOKEN_EXPIRY_BUFFER_SECONDS || 60),
    trustProxy: truthy(process.env.TRUST_PROXY) || process.env.NODE_ENV === "production",
    secureCookies: truthy(process.env.SECURE_COOKIES || process.env.COOKIE_SECURE),
    keycloak: {
        url: (process.env.KEYCLOAK_URL || process.env.EXPRESS_APP_KEYCLOAK_URL || "http://localhost:8080").replace(/\/+$/, ""),
        realm: process.env.KEYCLOAK_REALM || process.env.EXPRESS_APP_KEYCLOAK_REALM || "discobole",
        gatewayClientId: process.env.GATEWAY_CLIENT_ID || process.env.POC_GATEWAY_CLIENT_ID || "poc-gateway",
        gatewayClientSecret: process.env.GATEWAY_CLIENT_SECRET || process.env.POC_GATEWAY_CLIENT_SECRET || "change-me",
        selfcareClientId: process.env.SELFCARE_CLIENT_ID || process.env.EXPRESS_APP_USER_CLIENT_ID || "selfcare-ui",
        selfcareClientSecret: process.env.SELFCARE_CLIENT_SECRET || process.env.EXPRESS_APP_CLIENT_SECRET || "",
        adminUsername: process.env.KEYCLOAK_ADMIN_USERNAME || process.env.EXPRESS_APP_ADMIN_USERNAME || "",
        adminPassword: process.env.KEYCLOAK_ADMIN_PASSWORD || process.env.EXPRESS_APP_ADMIN_PASSWORD || "",
        serviceClientIdForRoles: process.env.KEYCLOAK_SERVICE_CLIENT_ID || process.env.EXPRESS_APP_SERVICE_CLIENT_ID || "",
        defaultClientRole: process.env.DEFAULT_CLIENT_ROLE || process.env.EXPRESS_APP_DEFAULT_CLIENT_ROLE || "OTB_CUSTOMER",
    },
};

export const routeTarget = (name, fallback) => (process.env[`ROUTE_${name}_URL`] || fallback).replace(/\/+$/, "");
