import {config} from "./config.mjs";
import {buildUserFromToken, isTokenExpired} from "./jwt.mjs";
import {keycloakService} from "./keycloakService.mjs";
import {clearUserSession, saveSession} from "./session.mjs";

const refreshLocks = new Map();

export const saveUserSession = async (req, {accessToken, refreshToken}) => {
    req.session.tokens = {accessToken, refreshToken};
    req.session.user = buildUserFromToken(accessToken);
    await saveSession(req);
};

export const getFreshUserToken = async (req) => {
    const accessToken = req.session?.tokens?.accessToken;
    const refreshToken = req.session?.tokens?.refreshToken;

    if (!accessToken || !refreshToken) return {token: null, expired: true};

    if (!isTokenExpired(accessToken, config.tokenExpiryBufferSeconds)) {
        return {token: accessToken, expired: false, refreshed: false};
    }

    return refreshUserSession(req);
};

export const refreshUserSession = async (req) => {
    const sessionId = req.sessionID || "unknown";
    if (refreshLocks.has(sessionId)) return refreshLocks.get(sessionId);

    const refreshPromise = (async () => {
        const refreshToken = req.session?.tokens?.refreshToken;
        if (!refreshToken || isTokenExpired(refreshToken, 0)) {
            await clearUserSession(req);
            return {token: null, expired: true};
        }

        try {
            const result = await keycloakService.refreshUserToken(refreshToken);
            await saveUserSession(req, {
                accessToken: result.accessToken,
                refreshToken: result.refreshToken,
            });
            return {token: result.accessToken, expired: false, refreshed: true};
        } catch (error) {
            await clearUserSession(req);
            return {token: null, expired: error.invalidGrant || error.status === 400 || error.status === 401};
        } finally {
            refreshLocks.delete(sessionId);
        }
    })();

    refreshLocks.set(sessionId, refreshPromise);
    setTimeout(() => {
        if (refreshLocks.get(sessionId) === refreshPromise) refreshLocks.delete(sessionId);
    }, 15000);

    return refreshPromise;
};
