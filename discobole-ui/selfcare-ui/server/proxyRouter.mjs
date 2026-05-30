// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import express from "express";
import axios from "./utils/axios.mjs";
import keycloakUtil from "./utils/KeycloakUtil.mjs";
import {clearUserSession, isTokenExpired, refreshUserSession,} from "./utils/tokenManager.mjs";

const router = express.Router();

const REQUEST_TIMEOUT = 60000;
const BEARER_PREFIX = "Bearer ";
const BLOCKED_RESPONSE_HEADERS_PREFIX = "access-control-";
const TOKEN_EXPIRY_BUFFER_SECONDS = 60;

const requireInternalSecret = (req, res, next) => {
    const isProd = process.env.NODE_ENV === "production";
    if (!isProd) return next();

    const expected = process.env.INTERNAL_SECRET;
    const provided = req.get("x-internal-secret");

    if (!expected) return res.status(500).json({error: "Server misconfiguration"});
    if (provided !== expected) return res.status(403).json({error: "Forbidden"});
    next();
};

router.use(requireInternalSecret);

const getValidServiceToken = async () => {
    const existingToken = keycloakUtil.getToken();
    if (existingToken && !isTokenExpired(existingToken, TOKEN_EXPIRY_BUFFER_SECONDS)) {
        return existingToken;
    }
    return await keycloakUtil.implicitLogin();
};

const getAuthToken = async (req) => {
    const sessionToken = req.session?.tokens?.accessToken;
    const isUserSession = req.session?.serviceSession === false;

    if (sessionToken && isUserSession) {
        if (!isTokenExpired(sessionToken, TOKEN_EXPIRY_BUFFER_SECONDS)) {
            return {token: sessionToken, isUserSession: true, wasRefreshed: false};
        }

        const refreshResult = await refreshUserSession(req);
        if (refreshResult?.token) {
            return {token: refreshResult.token, isUserSession: true, wasRefreshed: true};
        }

        if (refreshResult?.expired) {
            const error = new Error("Full session expired");
            error.code = "FULL_SESSION_EXPIRED";
            throw error;
        }

        throw new Error("User session expired and refresh failed");
    }

    const serviceToken = await getValidServiceToken();
    return {token: serviceToken, isUserSession: false, wasRefreshed: false};
};

const buildUpstreamHeaders = (requestHeaders, authToken) => {
    const headers = {
        authorization: `${BEARER_PREFIX}${authToken}`,
        accept: requestHeaders.accept || "*/*",
    };
    if (requestHeaders["content-type"]) headers["content-type"] = requestHeaders["content-type"];
    if (requestHeaders["user-agent"]) headers["user-agent"] = requestHeaders["user-agent"];
    return headers;
};

const copyResponseHeaders = (upstreamHeaders, clientResponse) => {
    for (const [name, value] of Object.entries(upstreamHeaders)) {
        if (!name.toLowerCase().startsWith(BLOCKED_RESPONSE_HEADERS_PREFIX)) {
            clientResponse.setHeader(name, value);
        }
    }
};

router.use(async (req, res) => {
    const proxyBaseUrl = (process.env.EXPRESS_APP_PROXY_URL || "").replace(/\/+$/, "");
    const requestPath = req.originalUrl.replace(/^\/api/, "");
    const upstreamUrl = `${proxyBaseUrl}${requestPath}`;
    const requestBody = req.body;

    try {
        const {token, isUserSession, wasRefreshed} = await getAuthToken(req);
        const upstreamHeaders = buildUpstreamHeaders(req.headers, token);

        const upstreamResponse = await axios({
            method: req.method,
            url: upstreamUrl,
            headers: upstreamHeaders,
            data: requestBody,
            responseType: "stream",
            timeout: REQUEST_TIMEOUT,
            validateStatus: () => true,
        });

        if (upstreamResponse.status === 401 && isUserSession && !wasRefreshed) {
            upstreamResponse.data.resume();

            const refreshResult = await refreshUserSession(req);
            if (refreshResult?.token) {
                const retryHeaders = buildUpstreamHeaders(req.headers, refreshResult.token);
                const retryResponse = await axios({
                    method: req.method,
                    url: upstreamUrl,
                    headers: retryHeaders,
                    data: requestBody,
                    responseType: "stream",
                    timeout: REQUEST_TIMEOUT,
                    validateStatus: () => true,
                });

                copyResponseHeaders(retryResponse.headers, res);
                res.status(retryResponse.status);
                retryResponse.data.pipe(res);
                return;
            }

            if (refreshResult?.expired) {
                return res.status(401).json({
                    error: "Session expired",
                    message: "Both tokens expired. Please log in again.",
                    code: "FULL_SESSION_EXPIRED",
                });
            }

            await clearUserSession(req);
        }

        copyResponseHeaders(upstreamResponse.headers, res);
        res.status(upstreamResponse.status);
        upstreamResponse.data.pipe(res);

        req.on("aborted", () => {
            if (upstreamResponse.data?.destroy) upstreamResponse.data.destroy();
        });
    } catch (error) {
        if (error.code === "FULL_SESSION_EXPIRED") {
            return res.status(401).json({
                error: "Session expired",
                message: "Both tokens expired. Please log in again.",
                code: "FULL_SESSION_EXPIRED",
            });
        }

        if (error.message?.includes("session expired") || error.message?.includes("refresh failed")) {
            return res.status(401).json({
                error: "Session expired",
                message: "Please log in again",
                code: "SESSION_EXPIRED",
            });
        }

        const status = error?.response?.status || 500;
        const message = error?.response?.data?.message || error.message || "Proxy request failed";
        res.status(status).json({
            error: "Proxy request failed",
            message,
            code: error.code,
        });
    }
});

export default router;