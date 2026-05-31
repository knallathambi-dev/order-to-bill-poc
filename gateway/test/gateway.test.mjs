import test, {after, before, beforeEach} from "node:test";
import assert from "node:assert/strict";
import http from "node:http";
import express from "express";
import request from "supertest";

const encode = (value) => Buffer.from(JSON.stringify(value)).toString("base64url");
const jwt = (payload) => `${encode({alg: "none", typ: "JWT"})}.${encode(payload)}.signature`;
const future = () => Math.floor(Date.now() / 1000) + 3600;
const past = () => Math.floor(Date.now() / 1000) - 60;

const listen = (app) => new Promise((resolve) => {
    const server = http.createServer(app);
    server.listen(0, () => resolve(server));
});

const baseUrl = (server) => `http://127.0.0.1:${server.address().port}`;

const startMockKeycloak = async () => {
    const app = express();
    app.use(express.urlencoded({extended: true}));
    app.post("/realms/discobole/protocol/openid-connect/token", (req, res) => {
        const grantType = req.body.grant_type;
        if (grantType === "client_credentials") {
            return res.json({access_token: jwt({sub: "gateway", exp: future()})});
        }
        if (grantType === "password") {
            const expiredPasswordToken = req.body.username === "expired@otb.com";
            return res.json({
                access_token: jwt({
                    email: req.body.username,
                    given_name: "Customer",
                    relatedPartyId: "customer-1",
                    relatedPartyRole: "customer",
                    exp: expiredPasswordToken ? past() : future(),
                }),
                refresh_token: jwt({sub: "refresh", exp: future()}),
            });
        }
        if (grantType === "refresh_token") {
            return res.json({
                access_token: jwt({
                    email: "customer@otb.com",
                    given_name: "Customer",
                    relatedPartyId: "customer-1",
                    relatedPartyRole: "customer",
                    exp: future(),
                }),
                refresh_token: jwt({sub: "refresh-2", exp: future()}),
            });
        }
        res.status(400).json({error: "unsupported_grant_type"});
    });
    return listen(app);
};

const startMockUpstream = async (seen) => {
    const app = express();
    app.use(express.json());
    app.use((req, res) => {
        seen.push({method: req.method, path: req.path, auth: req.get("authorization") || ""});
        res.json({ok: true, path: req.path, auth: req.get("authorization") || ""});
    });
    return listen(app);
};

const setupApp = async (keycloakUrl, upstreamUrl) => {
    process.env.NODE_ENV = "test";
    process.env.KEYCLOAK_URL = keycloakUrl;
    process.env.KEYCLOAK_REALM = "discobole";
    process.env.GATEWAY_CLIENT_ID = "poc-gateway";
    process.env.GATEWAY_CLIENT_SECRET = "change-me";
    process.env.SELFCARE_CLIENT_ID = "selfcare-ui";
    process.env.SESSION_SECRET = "test-session-secret";
    process.env.INTERNAL_SECRET = "test-internal-secret";
    process.env.ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:3004,http://localhost:3006";
    process.env.ROUTE_PRODUCT_CATALOG_MANAGEMENT_URL = upstreamUrl;
    process.env.ROUTE_ORDER_CAPTURE_URL = upstreamUrl;
    process.env.ROUTE_PRODUCT_ORDERING_MANAGEMENT_URL = upstreamUrl;
    process.env.ROUTE_PRODUCT_INVENTORY_URL = upstreamUrl;
    process.env.ROUTE_COOD_URL = upstreamUrl;
    process.env.ROUTE_FALLOUT_URL = upstreamUrl;
    process.env.ROUTE_USER_ROLE_PERMISSION_URL = upstreamUrl;
    process.env.ROUTE_PRODUCT_OFFERING_QUALIFICATION_URL = upstreamUrl;
    process.env.ROUTE_PROCESS_MANAGEMENT_URL = upstreamUrl;
    process.env.ROUTE_V1_URL = upstreamUrl;

    const {createApp} = await import(`../src/app.mjs?cache=${Date.now()}-${Math.random()}`);
    return createApp();
};

let app;
let keycloak;
let upstream;
let seen = [];

before(async () => {
    keycloak = await startMockKeycloak();
    upstream = await startMockUpstream(seen);
    app = await setupApp(baseUrl(keycloak), baseUrl(upstream));
});

beforeEach(() => {
    seen.length = 0;
});

after(() => {
    keycloak?.close();
    upstream?.close();
});

test("normalizes /api prefixes and strips only gateway aliases", async () => {
    const {normalizePath, resolveRoute} = await import("../src/routeRegistry.mjs");

    assert.equal(normalizePath("/api/productCatalogManagement/v1/productOffering?x=1"), "/productCatalogManagement/v1/productOffering?x=1");
    assert.equal(resolveRoute("/api/productCatalogManagement/v1/productOffering").upstreamPath, "/productCatalogManagement/v1/productOffering");
    assert.equal(resolveRoute("/api/cood/orchestrationPlan?limit=1").upstreamPath, "/orchestrationPlan");
    assert.equal(resolveRoute("/fallout/processManagement/v1/processFlow").upstreamPath, "/processManagement/v1/processFlow");
});

test("auth routes never create browser-visible service sessions", async (t) => {
    await request(app).get("/api/auth/me").expect(401).expect((res) => {
        assert.equal(res.body.error, "Not authenticated");
    });
});

test("mutating auth routes require CSRF", async (t) => {
    await request(app)
        .post("/api/auth/login")
        .send({email: "customer@otb.com", password: "customer"})
        .expect(403)
        .expect((res) => assert.match(res.body.error, /CSRF/i));
});

test("anonymous catalog reads use gateway service token", async (t) => {
    await request(app).get("/api/productCatalogManagement/v1/productOffering").expect(200);

    assert.equal(seen[0].path, "/productCatalogManagement/v1/productOffering");
    assert.match(seen[0].auth, /^Bearer /);
});

test("anonymous order capture mutations return 401", async (t) => {
    await request(app)
        .post("/api/orderCapture/processManagement/v1/processFlow")
        .send({task: "OrderCapture.selectOfferOrContract"})
        .expect(401);
});

test("product configuration fallback includes fields required by Selfcare and Order Capture", async () => {
    const response = await request(app)
        .post("/api/v1/queryProductConfiguration")
        .send({
            requestProductConfigurationItem: [
                {
                    id: "item-1",
                    productConfiguration: {
                        productOffering: {id: "offer-1", name: "Fiber Broadband 300 Mbps"},
                    },
                },
            ],
        })
        .expect(200);

    assert.equal(response.body.requestProductConfigurationItem[0].productConfiguration.productOffering.id, "offer-1");
    assert.equal(response.body.requestProductConfigurationItem[0].productConfiguration.configurationAction[0].action, "add");
    assert.equal(response.body.computedProductConfigurationItem[0].productConfiguration.productOffering.id, "offer-1");

    await request(app)
        .get(`/api/v1/queryProductConfiguration/${response.body.id}`)
        .expect(200)
        .expect((res) => {
            assert.equal(res.body.requestProductConfigurationItem[0].productConfiguration.configurationAction[0].isSelected, true);
            assert.ok(Array.isArray(res.body.computedProductConfigurationItem));
        });

    await request(app)
        .post("/api/v1/queryProductConfiguration")
        .send({
            id: response.body.id,
            requestProductConfigurationItem: [
                {
                    id: "item-1",
                    productConfiguration: {
                        configurationCharacteristic: [{id: "installation-address"}],
                    },
                },
            ],
        })
        .expect(200)
        .expect((res) => {
            assert.equal(res.body.requestProductConfigurationItem[0].productConfiguration.productOffering.id, "offer-1");
            assert.equal(res.body.computedProductConfigurationItem[0].productConfiguration.productOffering.id, "offer-1");
        });
});

test("logged-in order capture mutations forward user token", async (t) => {
    const agent = request.agent(app);
    const csrf = await agent.get("/api/auth/csrf").expect(200);
    await agent
        .post("/api/auth/login")
        .set("X-CSRF-Token", csrf.body.csrfToken)
        .send({email: "customer@otb.com", password: "customer"})
        .expect(200);

    await agent
        .post("/api/orderCapture/processManagement/v1/processFlow")
        .send({task: "OrderCapture.selectOfferOrContract"})
        .expect(200);

    assert.equal(seen[0].path, "/processManagement/v1/processFlow");
    assert.match(seen[0].auth, /^Bearer /);
    assert.match(seen[0].auth, /^Bearer /);
});

test("login over local HTTP sets a non-secure session cookie", async () => {
    const agent = request.agent(app);
    const csrf = await agent.get("/api/auth/csrf").expect(200);
    const response = await agent
        .post("/api/auth/login")
        .set("X-CSRF-Token", csrf.body.csrfToken)
        .send({email: "customer@otb.com", password: "customer"})
        .expect(200);

    const setCookies = response.headers["set-cookie"] || [];
    assert.ok(setCookies.some((cookie) => cookie.startsWith("poc-gateway.sid=")));
    assert.ok(setCookies.every((cookie) => !/;\s*Secure/i.test(cookie)));
});

test("/auth/me succeeds after login because the session cookie is stored", async () => {
    const agent = request.agent(app);
    const csrf = await agent.get("/api/auth/csrf").expect(200);
    await agent
        .post("/api/auth/login")
        .set("X-CSRF-Token", csrf.body.csrfToken)
        .send({email: "customer@otb.com", password: "customer"})
        .expect(200);

    await agent.get("/api/auth/me").expect(200).expect((res) => {
        assert.equal(res.body.serviceSession, false);
        assert.equal(res.body.user.email, "customer@otb.com");
    });
});

test("expired user access token refreshes before proxying", async (t) => {
    const agent = request.agent(app);
    const csrf = await agent.get("/api/auth/csrf").expect(200);
    await agent
        .post("/api/auth/login")
        .set("X-CSRF-Token", csrf.body.csrfToken)
        .send({email: "expired@otb.com", password: "customer"})
        .expect(200);

    await agent
        .post("/api/orderCapture/processManagement/v1/processFlow")
        .send({task: "OrderCapture.validateOrder"})
        .expect(200);

    assert.equal(seen.length, 1);
    assert.match(seen[0].auth, /^Bearer /);
});
