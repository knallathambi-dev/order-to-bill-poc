// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {defineConfig, loadEnv} from "vite";
import react from "@vitejs/plugin-react";
import federation from "@originjs/vite-plugin-federation";
import path from "path";
import {fileURLToPath} from "url";
import svgr from "@svgr/rollup";
import {HttpsProxyAgent} from "https-proxy-agent";
import {PRODUCT_INVENTORY_URL, USER_ROLES_URL} from "./src/constants.js";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), "");

    const mainPaths = [
        USER_ROLES_URL,
        PRODUCT_INVENTORY_URL,
    ];

    const createProxyConfig = (paths) => {
        const corporateProxy = env.VITE_CORPORATE_PROXY;
        const agent = corporateProxy ? new HttpsProxyAgent(corporateProxy) : undefined;
        console.log(`[PROXY CONFIG] Corporate proxy: ${corporateProxy || "none"}, agent created: ${!!agent}`);

        const proxy = {};
        paths.forEach((p) => {
            proxy[p] = {
                target: env.VITE_GATEWAY_URL,
                changeOrigin: true,
                secure: false,
                configure: (proxyServer, options) => {
                    if (agent) {
                        proxyServer.options.agent = agent;
                    }
                    proxyServer.on("proxyReq", (proxyReq, req) => {
                        console.log(`[PROXY] ${req.method} ${req.url} -> ${options.target}${req.url}`);
                    });
                    proxyServer.on("error", (err, req) => {
                        console.error(`[PROXY ERROR] ${req.url}:`, err.message);
                    });
                },
            };
        });
        return proxy;
    };

    return {
        plugins: [
            react(),
            federation({
                name: "product_inventory",
                filename: "remoteEntry.js",
                exposes: {
                    "./App": "./src/App.jsx"
                },
                shared: {
                    "react": {singleton: true, requiredVersion: "19.1.2"},
                    "react-dom": {singleton: true, requiredVersion: "19.1.2"},
                    "react-router-dom": {singleton: true, requiredVersion: "7.13.0"},
                    "@discobole/common-ui": {singleton: true, requiredVersion: "1.1.0"},
                }
            }),
            svgr()
        ],
        test: {
            globals: true,
            environment: 'jsdom',
            setupFiles: './src/setupTests.jsx',
            coverage: {
                reporter: ['text', 'json', 'html'],
            },
            include: ['**/__tests__/**/*.jsx'],
            testTimeout: 10000,
        },
        resolve: {
            alias: {
                react: path.resolve(__dirname, "node_modules/react"),
                "react-dom": path.resolve(__dirname, "node_modules/react-dom"),
                "@discobole/common-ui/dist/common-ui.css": path.resolve(__dirname, "node_modules/@discobole/common-ui/dist/common-ui.css"),
            }
        },
        server: {
            port: 3005,
            proxy: createProxyConfig(mainPaths)
        },
        preview: {
            port: 3002,
            proxy: createProxyConfig(mainPaths)
        },
        build: {
            target: "esnext",
            minify: false,
            cssCodeSplit: false,
            commonjsOptions: {include: [/common-ui/, /node_modules/]},
            rollupOptions: {
                output: {
                    globals: {react: "React", "react-dom": "ReactDOM"}
                }
            }
        }
    };
});