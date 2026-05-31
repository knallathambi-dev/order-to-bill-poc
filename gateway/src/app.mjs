import express from "express";
import helmet from "helmet";
import cookieParser from "cookie-parser";
import session from "express-session";
import cors from "cors";
import {config} from "./config.mjs";
import {sessionConfig} from "./session.mjs";
import authRouter from "./authRouter.mjs";
import proxyRouter from "./proxyRouter.mjs";
import {
    getProductConfigurationFallback,
    postProductConfigurationFallback,
} from "./productConfigurationFallback.mjs";
import {normalizePath} from "./routeRegistry.mjs";

export const createApp = () => {
    const app = express();

    app.set("trust proxy", config.trustProxy ? 1 : 0);

    app.use(helmet({crossOriginResourcePolicy: false}));
    app.use(cors({
        origin(origin, callback) {
            if (!origin || config.allowedOrigins.includes(origin)) return callback(null, true);
            return callback(new Error("Origin not allowed by CORS"));
        },
        credentials: true,
    }));
    app.use(express.json({limit: "5mb"}));
    app.use(express.urlencoded({extended: true, limit: "5mb"}));
    app.use(cookieParser());
    app.use(session(sessionConfig));

    app.get("/health", (_req, res) => {
        res.json({status: "healthy", service: "poc-gateway", timestamp: new Date().toISOString()});
    });

    app.use((req, _res, next) => {
        req.normalizedPath = normalizePath(req.originalUrl).split("?")[0];
        next();
    });

    app.use(["/auth", "/api/auth"], authRouter);

    app.post(["/v1/queryProductConfiguration", "/api/v1/queryProductConfiguration"], postProductConfigurationFallback);
    app.get(["/v1/queryProductConfiguration/:id", "/api/v1/queryProductConfiguration/:id"], getProductConfigurationFallback);

    app.use(proxyRouter);

    app.use((_req, res) => res.status(404).json({error: "Not found"}));

    app.use((err, _req, res, _next) => {
        const message = err?.message || "Internal server error";
        const status = message.includes("CORS") || message.includes("Origin not allowed") ? 403 : (err.status || 500);
        res.status(status).json({error: message});
    });

    return app;
};

export default createApp;
