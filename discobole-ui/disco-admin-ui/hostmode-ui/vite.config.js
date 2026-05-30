// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";
import federation from "@originjs/vite-plugin-federation";
import {HttpsProxyAgent} from "https-proxy-agent";
import {
    FALLOUT_MANAGEMENT_SERVICE_URL,
    ORCHESTRATION_SERVICE_URL,
    ORDER_CAPTURE_URL,
    ORDER_INVENTORY_URL,
    PRODUCT_INVENTORY_URL,
    USER_ROLES_URL
} from "./src/constants.js";

let ENV_CONFIG;

try {
    const envModule = await import("./env.local.js");
    ENV_CONFIG = envModule.ENV_CONFIG;
} catch {
    ENV_CONFIG = {
        GATEWAY_URL: process.env.GATEWAY_URL,
        CORPORATE_PROXY: process.env.CORPORATE_PROXY,
        FEATURE_ORDER_MANAGEMENT: process.env.FEATURE_ORDER_MANAGEMENT,
        FEATURE_PRODUCT_INVENTORY: process.env.FEATURE_PRODUCT_INVENTORY,
        FEATURE_ORDER_ORCHESTRATION: process.env.FEATURE_ORDER_ORCHESTRATION,
        ORDER_INVENTORY_UI_URL: process.env.ORDER_INVENTORY_UI_URL,
        ORDER_ORCHESTRATION_UI_URL: process.env.ORDER_ORCHESTRATION_UI_URL,
        PRODUCT_INVENTORY_UI_URL: process.env.PRODUCT_INVENTORY_UI_URL,
    };
}

const mainPaths = [
    USER_ROLES_URL,
    ORDER_INVENTORY_URL,
    ORDER_CAPTURE_URL,
    PRODUCT_INVENTORY_URL,
    ORCHESTRATION_SERVICE_URL,
    FALLOUT_MANAGEMENT_SERVICE_URL,
];

const authPaths = [
    "/api/auth",
    "/auth/login",
    "/auth/logout",
];

const createProxyConfig = (paths) => {
    const corporateProxy = ENV_CONFIG.CORPORATE_PROXY;
    const agent = corporateProxy ? new HttpsProxyAgent(corporateProxy) : undefined;
    console.log(
        `[PROXY CONFIG] Corporate proxy: ${corporateProxy || "none"}, agent created: ${!!agent}`
    );

    const proxy = {};
    paths.forEach((p) => {
        proxy[p] = {
            target: ENV_CONFIG.GATEWAY_URL,
            changeOrigin: true,
            secure: false,
            configure: (proxyServer, options) => {
                if (agent) {
                    proxyServer.options.agent = agent;
                }

                proxyServer.on("proxyReq", (proxyReq, req) => {
                    console.log(
                        `[PROXY] ${req.method} ${req.url} -> ${options.target}${req.url}`
                    );
                });

                proxyServer.on("error", (err, req) => {
                    console.error(`[PROXY ERROR] ${req.url}:`, err.message);
                });
            },
        };
    });
    return proxy;
};

export default defineConfig(() => {

    return {
        plugins: [
            react(),
            federation({
                name: "hostmode",
                remotes: {
                    order_inventory: {
                        external: `Promise.resolve(window.__ENV__?.ORDER_INVENTORY_UI_URL + '/assets/remoteEntry.js')`,
                        externalType: "promise",
                        from: "vite",
                    },
                    product_inventory: {
                        external: `Promise.resolve(window.__ENV__?.PRODUCT_INVENTORY_UI_URL + '/assets/remoteEntry.js')`,
                        externalType: "promise",
                        from: "vite",
                    },
                    order_orchestration: {
                        external: `Promise.resolve(window.__ENV__?.ORDER_ORCHESTRATION_UI_URL + '/assets/remoteEntry.js')`,
                        externalType: "promise",
                        from: "vite",
                    },
                },
                shared: {
                    "react": {singleton: true, requiredVersion: "19.1.2", eager: true},
                    "react-dom": {singleton: true, requiredVersion: "19.1.2", eager: true},
                    "react-router-dom": {singleton: true, requiredVersion: "7.13.0", eager: true},
                    "@discobole/common-ui": {singleton: true, requiredVersion: "1.1.0"},
                },
            }),
        ],
        test: {
            globals: true,
            environment: "jsdom",
            setupFiles: "./src/setupTests.js",
            coverage: {
                reporter: ["text", "json", "html", "cobertura"],
                reportsDirectory: "./coverage",
            },
        },
        build: {
            modulePreload: false,
            target: "esnext",
            minify: false,
            cssCodeSplit: false,
        },
        server: {
            port: 5173,
            proxy: createProxyConfig([...mainPaths, ...authPaths]),
        },
    };
});