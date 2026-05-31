import session from "express-session";
import FileStoreFactory from "session-file-store";
import {config} from "./config.mjs";

const FileStore = FileStoreFactory(session);
const isProd = config.env === "production";

export const sessionConfig = {
    ...(isProd && {
        store: new FileStore({
            path: "./sessions",
            ttl: 3600,
            retries: 1,
            reapInterval: 600,
        }),
    }),
    secret: config.sessionSecret,
    name: "poc-gateway.sid",
    resave: false,
    saveUninitialized: false,
    rolling: true,
    cookie: {
        httpOnly: true,
        secure: config.secureCookies,
        sameSite: isProd ? "strict" : "lax",
        maxAge: 60 * 60 * 1000,
        path: "/",
    },
};

export const saveSession = (req) => new Promise((resolve, reject) => {
    req.session.save((err) => err ? reject(err) : resolve());
});

export const clearUserSession = async (req) => {
    delete req.session.tokens;
    delete req.session.user;
    await saveSession(req).catch(() => {});
};
