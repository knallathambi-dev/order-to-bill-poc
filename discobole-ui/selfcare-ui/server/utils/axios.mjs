// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import "../config/env.mjs";

import axios from "axios";
import https from "https";
import http from "http";
import {HttpsProxyAgent} from "https-proxy-agent";

const isDev = process.env.NODE_ENV !== "production";
const corporateProxy = isDev ? process.env.EXPRESS_APP_CORPORATE_PROXY : null;
const requestTimeout = Number(process.env.AXIOS_REQUEST_TIMEOUT) || 30000;

const agentOptions = {
    rejectUnauthorized: false,
    keepAlive: true,
    timeout: requestTimeout,
};

const httpsAgent = corporateProxy
    ? new HttpsProxyAgent(corporateProxy, agentOptions)
    : new https.Agent(agentOptions);

const httpAgent = new http.Agent({
    keepAlive: true,
    timeout: requestTimeout,
});

const instance = axios.create({
    timeout: requestTimeout,
    httpsAgent,
    httpAgent,
    proxy: false,
    maxRedirects: 5,
});

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error?.response?.status;
        const url = error?.config?.url || "";
        console.error(`[AXIOS] ${error.config?.method?.toUpperCase() || "?"} ${url} → ${status || "network error"}: ${error.message}`);
        return Promise.reject(error);
    }
);

export default instance;