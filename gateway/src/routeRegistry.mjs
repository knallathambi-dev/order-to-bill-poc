const routeTarget = (name, fallback) => (process.env[`ROUTE_${name}_URL`] || fallback).replace(/\/+$/, "");

export const POLICY = {
    PUBLIC_SERVICE: "public-service",
    USER_REQUIRED: "user-required",
    USER_OR_INTERNAL: "user-or-internal",
    INTERNAL_ONLY: "internal-only",
};

export const normalizePath = (rawPath = "/") => {
    const [pathPart, queryPart] = String(rawPath || "/").split("?");
    let path = pathPart || "/";
    if (!path.startsWith("/")) path = `/${path}`;
    path = path.replace(/\/{2,}/g, "/");
    if (path !== "/" && path.endsWith("/")) path = path.slice(0, -1);
    path = path.replace(/^\/api(?=\/|$)/, "") || "/";
    return queryPart ? `${path}?${queryPart}` : path;
};

export const pathOnly = (rawPath = "/") => normalizePath(rawPath).split("?")[0] || "/";

const routeDefinitions = [
    {
        prefix: "productCatalogManagement",
        targetName: "PRODUCT_CATALOG_MANAGEMENT",
        fallbackTarget: "http://localhost:18086",
        policy: POLICY.PUBLIC_SERVICE,
        publicMethods: ["GET"],
    },
    {
        prefix: "orderCapture",
        targetName: "ORDER_CAPTURE",
        fallbackTarget: "http://localhost:18080",
        stripPrefix: true,
        policy: POLICY.USER_REQUIRED,
    },
    {
        prefix: "productOrderingManagement",
        targetName: "PRODUCT_ORDERING_MANAGEMENT",
        fallbackTarget: "http://localhost:18081",
        policy: POLICY.USER_REQUIRED,
    },
    {
        prefix: "productInventory",
        targetName: "PRODUCT_INVENTORY",
        fallbackTarget: "http://localhost:18089",
        policy: POLICY.USER_REQUIRED,
    },
    {
        prefix: "cood",
        targetName: "COOD",
        fallbackTarget: "http://localhost:18082",
        stripPrefix: true,
        policy: POLICY.USER_OR_INTERNAL,
    },
    {
        prefix: "fallout",
        targetName: "FALLOUT",
        fallbackTarget: "http://localhost:18084",
        stripPrefix: true,
        policy: POLICY.USER_OR_INTERNAL,
    },
    {
        prefix: "userRolePermission",
        targetName: "USER_ROLE_PERMISSION",
        fallbackTarget: "http://localhost:18085",
        policy: POLICY.USER_OR_INTERNAL,
    },
    {
        prefix: "productOfferingQualification",
        targetName: "PRODUCT_OFFERING_QUALIFICATION",
        fallbackTarget: "http://localhost:18080",
        policy: POLICY.PUBLIC_SERVICE,
        publicMethods: ["GET", "POST"],
    },
    {
        prefix: "processManagement",
        targetName: "PROCESS_MANAGEMENT",
        fallbackTarget: "http://localhost:18080",
        policy: POLICY.USER_REQUIRED,
    },
    {
        prefix: "v1",
        targetName: "V1",
        fallbackTarget: "http://localhost:18080",
        policy: POLICY.PUBLIC_SERVICE,
        publicMethods: ["GET", "POST"],
    },
];

export const routeDefinitionsForTests = routeDefinitions;

export const resolveRoute = (rawPath = "/") => {
    const normalized = normalizePath(rawPath);
    const requestPath = normalized.split("?")[0];
    const query = normalized.includes("?") ? `?${normalized.split("?").slice(1).join("?")}` : "";

    const route = routeDefinitions.find(({prefix}) =>
        requestPath === `/${prefix}` || requestPath.startsWith(`/${prefix}/`)
    );

    if (!route) return null;

    const upstreamPath = route.stripPrefix
        ? requestPath.replace(new RegExp(`^/${route.prefix}`), "") || "/"
        : requestPath;

    return {
        ...route,
        normalizedPath: requestPath,
        upstreamPath,
        target: routeTarget(route.targetName, route.fallbackTarget),
        upstreamUrl: `${routeTarget(route.targetName, route.fallbackTarget)}${upstreamPath}${query}`,
    };
};

export const isMethodPublicForRoute = (route, method) => {
    if (route?.policy !== POLICY.PUBLIC_SERVICE) return false;
    return (route.publicMethods || ["GET"]).includes(String(method || "GET").toUpperCase());
};
