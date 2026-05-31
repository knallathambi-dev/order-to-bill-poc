// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import crypto from "crypto";
import session from "express-session";
import FileStoreFactory from "session-file-store";

const FileStore = FileStoreFactory(session);

const isProd = process.env.NODE_ENV === "production";
const secret = process.env.SESSION_SECRET || crypto.randomBytes(32).toString("hex");
const cookieSecure = process.env.SESSION_COOKIE_SECURE
    ? ["1", "true", "yes"].includes(process.env.SESSION_COOKIE_SECURE.toLowerCase())
    : isProd;

if (!process.env.SESSION_SECRET && isProd) {
    console.warn("[Session] WARNING: SESSION_SECRET not set. Using random value — sessions will not survive restarts.");
}

const store = isProd
    ? new FileStore({
        path: "./sessions",
        ttl: 3600,
        retries: 1,
        reapInterval: 600,
    })
    : undefined;

export const sessionConfig = {
    ...(store && {store}),
    secret,
    name: "selfcare.sid",
    resave: false,
    saveUninitialized: false,
    rolling: true,
    cookie: {
        httpOnly: true,
        secure: cookieSecure,
        sameSite: isProd ? "strict" : "lax",
        maxAge: 60 * 60 * 1000,
        path: "/",
    },
};
