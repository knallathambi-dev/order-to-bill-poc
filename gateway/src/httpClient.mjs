import axios from "axios";
import {config} from "./config.mjs";

export const httpClient = axios.create({
    timeout: config.requestTimeoutMs,
    validateStatus: () => true,
});
