import express from "express";
import {config} from "./config.mjs";
import {httpClient} from "./httpClient.mjs";
import {keycloakService} from "./keycloakService.mjs";
import {POLICY, isMethodPublicForRoute, resolveRoute} from "./routeRegistry.mjs";
import {clearUserSession} from "./session.mjs";
import {getFreshUserToken, refreshUserSession} from "./tokenManager.mjs";

const router = express.Router();
const BEARER_PREFIX = "Bearer ";
const BLOCKED_RESPONSE_HEADERS_PREFIX = "access-control-";

const hasInternalSecret = (req) => {
    if (!config.internalSecret) return false;
    return req.get("x-internal-secret") === config.internalSecret;
};

const buildUpstreamHeaders = (requestHeaders, authToken) => {
    const headers = {
        authorization: `${BEARER_PREFIX}${authToken}`,
        accept: requestHeaders.accept || "*/*",
    };
    if (requestHeaders["content-type"]) headers["content-type"] = requestHeaders["content-type"];
    if (requestHeaders["user-agent"]) headers["user-agent"] = requestHeaders["user-agent"];
    if (requestHeaders["x-request-id"]) headers["x-request-id"] = requestHeaders["x-request-id"];
    return headers;
};

const copyResponseHeaders = (upstreamHeaders, clientResponse) => {
    for (const [name, value] of Object.entries(upstreamHeaders || {})) {
        const lower = name.toLowerCase();
        if (!lower.startsWith(BLOCKED_RESPONSE_HEADERS_PREFIX) && lower !== "transfer-encoding") {
            clientResponse.setHeader(name, value);
        }
    }
};

export const selectAuthToken = async (req, route) => {
    if (hasInternalSecret(req)) {
        return {token: await keycloakService.getServiceToken(), source: "internal-service"};
    }

    const userResult = await getFreshUserToken(req);
    if (userResult.token) {
        return {token: userResult.token, source: "user", refreshed: userResult.refreshed};
    }

    if (route.policy === POLICY.INTERNAL_ONLY) {
        const error = new Error("Internal secret required");
        error.status = 403;
        throw error;
    }

    if (isMethodPublicForRoute(route, req.method)) {
        return {token: await keycloakService.getServiceToken(), source: "public-service"};
    }

    const error = new Error("Authentication required");
    error.status = 401;
    throw error;
};

const forwardOnce = async (req, route, token) => httpClient({
    method: req.method,
    url: route.upstreamUrl,
    headers: buildUpstreamHeaders(req.headers, token),
    data: req.body,
    responseType: "stream",
    timeout: config.requestTimeoutMs,
    validateStatus: () => true,
});

router.use(async (req, res) => {
    const route = resolveRoute(req.originalUrl);
    if (!route) return res.status(404).json({error: "No gateway route configured"});

    try {
        const auth = await selectAuthToken(req, route);
        let upstreamResponse = await forwardOnce(req, route, auth.token);

        if (upstreamResponse.status === 401 && auth.source === "user" && !auth.refreshed) {
            upstreamResponse.data?.resume?.();
            const refreshResult = await refreshUserSession(req);
            if (refreshResult?.token) {
                upstreamResponse = await forwardOnce(req, route, refreshResult.token);
            } else {
                await clearUserSession(req);
                return res.status(401).json({
                    error: "Session expired",
                    code: refreshResult?.expired ? "FULL_SESSION_EXPIRED" : "SESSION_EXPIRED",
                    message: refreshResult?.expired ? "Both tokens expired. Please log in again." : "Please log in again",
                });
            }
        }

        copyResponseHeaders(upstreamResponse.headers, res);
        res.status(upstreamResponse.status);
        upstreamResponse.data.pipe(res);

        req.on("aborted", () => {
            upstreamResponse.data?.destroy?.();
        });
    } catch (error) {
        const status = error.status || error.response?.status || 500;
        const message = error.response?.data?.message || error.message || "Proxy request failed";
        res.status(status).json({error: status === 401 ? "Authentication required" : "Proxy request failed", message});
    }
});

export default router;
