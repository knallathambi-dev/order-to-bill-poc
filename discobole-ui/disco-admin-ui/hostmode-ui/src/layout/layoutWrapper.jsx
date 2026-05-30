// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useMemo} from "react";
import {useLocation, useNavigate} from "react-router-dom";

import {AuthService, ENTITLEMENTS, FederationProvider, Header, SideMenuProvider} from "@discobole/common-ui";
import RemoteWrapperOrderInventory from "../remotes/RemoteWrapperOrderInventory.jsx";
import RemoteWrapperOrderOrchestration from "../remotes/RemoteWrapperOrderOrchestration.jsx";
import RemoteWrapperProductInventory from "../remotes/RemoteWrapperProductInventory.jsx";
import RemoteErrorBoundary from "../remotes/RemoteErrorBoundary.jsx";
import {env} from "../utils/env-helper.js";

const MFE_CONFIG = [
    {
        key: "OM",
        label: "OM",
        name: "Order Inventory",
        path: "/order-inventory",
        tooltipContent: "Order Management",
        entitlement: ENTITLEMENTS.VIEW_ORDERS,
        envKey: "FEATURE_ORDER_MANAGEMENT",
        component: RemoteWrapperOrderInventory,
    },
    {
        key: "CPIB",
        label: "CPIB",
        name: "Product Inventory",
        path: "/product-inventory",
        tooltipContent: "Commercial Product Installed Base",
        entitlement: ENTITLEMENTS.VIEW_PRODUCTS,
        envKey: "FEATURE_PRODUCT_INVENTORY",
        component: RemoteWrapperProductInventory,
    },
    {
        key: "COOD",
        label: "COOD",
        name: "Order Orchestration",
        path: "/orchestration-delivery",
        tooltipContent: "Customer Order Orchestration & Distribution",
        entitlement: ENTITLEMENTS.VIEW_ORCHESTRATION_PLANS,
        envKey: "FEATURE_ORDER_ORCHESTRATION",
        component: RemoteWrapperOrderOrchestration,
    },
];

const isMfeEnabled = (envKey) => env[envKey] === "true";

export const LayoutWrapper = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const enabledMfes = useMemo(
        () => MFE_CONFIG.filter((mfe) => isMfeEnabled(mfe.envKey)),
        []
    );

    const defaultMfe = enabledMfes[0] || null;

    const isKnownRoute = MFE_CONFIG.some((mfe) =>
        location.pathname.startsWith(mfe.path)
    );

    const activeMfe = useMemo(() => {
        return enabledMfes.find((mfe) =>
            location.pathname.startsWith(mfe.path)
        ) || null;
    }, [location.pathname, enabledMfes]);

    const disabledMfe = useMemo(() => {
        if (activeMfe) return null;
        return MFE_CONFIG.find(
            (mfe) => location.pathname.startsWith(mfe.path) && !isMfeEnabled(mfe.envKey)
        ) || null;
    }, [location.pathname, activeMfe]);

    useEffect(() => {
        if (!defaultMfe) return;
        if (isKnownRoute) return;
        navigate(defaultMfe.path, {replace: true});
    }, [location.pathname, defaultMfe, isKnownRoute, navigate]);

    const renderMFE = () => {
        if (!defaultMfe) {
            return (
                <div className="container">
                    <div className="row justify-content-center mt-5 pt-5">
                        <div className="col-12 col-md-8 col-lg-6">
                            <div className="card border-0 shadow-sm text-center p-4">
                                <div className="card-body">
                                    <div className="mb-3" style={{fontSize: "3rem"}}>⚠️</div>
                                    <h3 className="card-title fw-bold mb-3">No modules available</h3>
                                    <p className="text-muted mb-0">
                                        All application modules are currently disabled.
                                        Please contact your administrator.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        if (disabledMfe) {
            return (
                <div className="container">
                    <div className="row justify-content-center mt-5 pt-5">
                        <div className="col-12 col-md-8 col-lg-6">
                            <div className="card border-0 shadow-sm text-center p-4">
                                <div className="card-body">
                                    <div className="mb-3" style={{fontSize: "3rem"}}>🚫</div>
                                    <h3 className="card-title fw-bold mb-3">
                                        {disabledMfe.name}
                                    </h3>
                                    <p className="text-muted mb-4">
                                        This module is currently unavailable.
                                        Please select one of the available modules below.
                                    </p>
                                    <hr className="mb-4"/>
                                    <div className="d-grid gap-2 d-sm-flex justify-content-center">
                                        {enabledMfes.map((mfe) => (
                                            <button
                                                key={mfe.key}
                                                className="btn btn-outline-dark btn-lg px-4"
                                                onClick={() => navigate(mfe.path)}
                                            >
                                                {mfe.tooltipContent}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        if (!activeMfe) return null;

        const MfeComponent = activeMfe.component;
        return (
            <RemoteErrorBoundary key={activeMfe.key} name={activeMfe.name}>
                <MfeComponent/>
            </RemoteErrorBoundary>
        );
    };

    const tabs = enabledMfes.map((mfe) => ({
        ariaContent: mfe.label,
        tooltipContent: mfe.tooltipContent,
        to: mfe.path,
        isAuthorized: AuthService.hasEntitlement(mfe.entitlement),
        label: mfe.label,
        disabled: false,
    }));

    const federationConfig = useMemo(() => ({
        featureOrderManagement: isMfeEnabled("FEATURE_ORDER_MANAGEMENT"),
        featureProductInventory: isMfeEnabled("FEATURE_PRODUCT_INVENTORY"),
        featureOrderOrchestration: isMfeEnabled("FEATURE_ORDER_ORCHESTRATION"),
    }), []);

    return (
        <FederationProvider config={federationConfig}>
            <SideMenuProvider>
                <Header
                    logout={AuthService.logout}
                    userInfo={AuthService.getUserInfo()}
                    link={defaultMfe?.path || "/"}
                    tabs={tabs}
                />
                <main>
                    {renderMFE()}
                </main>
            </SideMenuProvider>
        </FederationProvider>
    );
};