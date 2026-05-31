export const decodeJwtPayload = (token) => {
    try {
        const [, payload] = String(token || "").split(".");
        if (!payload) return null;
        return JSON.parse(Buffer.from(payload, "base64url").toString("utf8"));
    } catch {
        return null;
    }
};

export const isTokenExpired = (token, bufferSeconds = 30) => {
    const payload = decodeJwtPayload(token);
    if (!payload?.exp) return true;
    return payload.exp <= (Math.floor(Date.now() / 1000) + bufferSeconds);
};

export const buildUserFromToken = (accessToken) => {
    const payload = decodeJwtPayload(accessToken) || {};
    const email = payload.email || null;
    const stableFallbackId = email ? email.replace(/[^A-Za-z0-9_-]/g, "-") : null;

    return {
        username: payload.given_name || payload.preferred_username || null,
        email,
        relatedPartyId: payload.relatedPartyId || stableFallbackId,
        relatedPartyRole: payload.relatedPartyRole || "customer",
    };
};
