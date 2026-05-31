// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import "./config/env.mjs";

import express from "express";
import crypto from "crypto";
import helmet from "helmet";
import cookieParser from "cookie-parser";
import session from "express-session";
import {sessionConfig} from "./config/session.mjs";
import proxyRouter from "./proxyRouter.mjs";
import authRouter from "./authRouter.mjs";

const app = express();
const PORT = process.env.PORT || 5000;

app.set("trust proxy", 1);

app.use(
    helmet({
        crossOriginResourcePolicy: false,
    })
);

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(cookieParser());

app.use(session(sessionConfig));

const parseAllowedOrigins = () =>
    (process.env.ALLOWED_ORIGINS || "")
        .split(",")
        .map(s => s.trim())
        .filter(Boolean);

const hostMatchesAllowed = (host, origins) => {
    if (!host || !origins.length) return false;
    try {
        const allowedHosts = origins.map(o => new URL(o).host);
        return allowedHosts.includes(host);
    } catch {
        return false;
    }
};

const verifyOrigin = (req, res, next) => {
    const isProd = process.env.NODE_ENV === "production";

    if (!isProd) {
        return next();
    }

    const origins = parseAllowedOrigins();
    const origin = req.get("origin");
    const host = req.get("host");

    if (origin) {
        const ok = origins.some(o => origin.startsWith(o));
        if (!ok) {
            return res.status(403).json({error: "Forbidden (origin not allowed)"});
        }
        return next();
    }

    if (!hostMatchesAllowed(host, origins)) {
        return res.status(403).json({error: "Forbidden (no Origin and host not allowed)"});
    }
    return next();
};

app.get("/health", (_req, res) => {
    res.json({status: "healthy", timestamp: new Date().toISOString()});
});

const phase7Configurations = new Map();

const findRequestedOffering = (body = {}) => {
    const items = body.requestProductConfigurationItem || [];
    const firstConfig = items[0]?.productConfiguration || {};
    return firstConfig.productOffering || firstConfig.product || {};
};

const buildPhase7Configuration = (body = {}) => {
    const requested = findRequestedOffering(body);
    const configurationId = body.id || crypto.randomUUID();
    const itemId = body.requestProductConfigurationItem?.[0]?.id || `${configurationId}-fiber-broadband`;
    const offeringId = requested.id || "phase7-fiber-broadband-300";
    const offeringName = requested.name || "Fiber Broadband 300 Mbps";
    const incomingCharacteristics = body.requestProductConfigurationItem?.[0]?.productConfiguration?.configurationCharacteristic;

    return {
        id: configurationId,
        state: "done",
        channel: body.channel || [{id: "Selfcare", name: "Selfcare"}],
        relatedParty: body.relatedParty || [],
        computedProductConfigurationItem: [
            {
                id: itemId,
                "@type": "TargetQueryProductConfigurationItem",
                productConfiguration: {
                    id: `${itemId}-configuration`,
                    isVisible: true,
                    isSelected: true,
                    productOffering: {
                        id: offeringId,
                        name: offeringName,
                        "@type": "ProductOfferingRef",
                        "@referredType": "Contract",
                    },
                    configurationAction: [
                        {
                            action: "add",
                            isSelected: true,
                            "@type": "ConfigurationAction",
                        },
                    ],
                    configurationCharacteristic: incomingCharacteristics || [
                        {
                            id: "installation-address",
                            name: "Installation address",
                            isConfigurable: true,
                            "@type": "AddressCharacteristic",
                            configurationCharacteristicValues: [],
                        },
                    ],
                    configurationPrice: [],
                    "@type": "ProductConfiguration",
                },
                productConfigurationItemRelationship: [],
            },
        ],
        "@type": "QueryProductConfiguration",
    };
};

const isProd = process.env.NODE_ENV === "production";
const basePath = isProd ? "/api" : "";

if (!isProd) {
    app.post(["/v1/queryProductConfiguration", "/api/v1/queryProductConfiguration"], (req, res) => {
        const configuration = buildPhase7Configuration(req.body);
        phase7Configurations.set(configuration.id, configuration);
        res.json(configuration);
    });

    app.get(["/v1/queryProductConfiguration/:id", "/api/v1/queryProductConfiguration/:id"], (req, res) => {
        const configuration = phase7Configurations.get(req.params.id);
        if (!configuration) {
            return res.status(404).json({error: "Configuration not found"});
        }
        res.json(configuration);
    });
}

app.use(`${basePath}/auth`, authRouter);
if (!isProd) {
    app.use("/api/auth", authRouter);
}

if (isProd) {
    app.use(basePath || "/", verifyOrigin, proxyRouter);
} else {
    app.use(basePath || "/", proxyRouter);
}

app.use((_req, res) => {
    res.status(404).json({error: "Not found"});
});

app.use((err, _req, res, _next) => {
    console.error("Server error:", err);
    res.status(err.status || 500).json({error: "Internal server error"});
});

app.listen(PORT, () => {
    console.log(`[SERVER] Running on http://localhost:${PORT}`);
});
