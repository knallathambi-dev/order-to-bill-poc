// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {
    BootstrapTooltip,
    ENTITLEMENTS,
    AuthService,
    ReloadButton,
    useFederationConfig,
    useNavigation,
} from "@discobole/common-ui";
import {ORCHESTRATION_PLAN_DETAILS_URL} from "../../utils/constants.js";
import {env} from "../../utils/env-helper.js";
import service from "../../service/OrderInventoryService.js";

const DRAFT_STATES = ["draft", "acknowledged"];

const IconButton = ({onClick, disabled, iconClass, tooltip}) => {
    const button = (
        <button
            type="button"
            className="btn btn-icon order-header-btn ms-1"
            onClick={onClick}
            disabled={disabled}
        >
            <em className={`${iconClass} ${disabled ? "icon-disabled" : ""}`}/>
        </button>
    );

    if (disabled) return button;

    return (
        <BootstrapTooltip title={tooltip} placement="top">
            {button}
        </BootstrapTooltip>
    );
};

const OrderHeader = ({orderId, orderState, handleReload}) => {
    const {navigateTo} = useNavigation();
    const {featureProductInventory, featureOrderOrchestration} = useFederationConfig();
    const isStandalone = env.STANDALONE_MODE === "true";

    const canViewProduct = !isStandalone && AuthService.hasEntitlement(ENTITLEMENTS.VIEW_PRODUCTS);
    const canViewOrchestration = !isStandalone && AuthService.hasEntitlement(ENTITLEMENTS.VIEW_ORCHESTRATION_PLANS);

    const [hasOrchestrationPlan, setHasOrchestrationPlan] = useState(false);
    const [hasProduct, setHasProduct] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const checkAvailability = async () => {
            const checkOrchestration = !DRAFT_STATES.includes(orderState);
            const checkProduct = orderState !== "draft";

            setLoading(true);

            const [orchestrationId, productId] = await Promise.all([
                checkOrchestration ? service.getOrchestrationPlanId(orderId) : null,
                checkProduct ? service.getContractProductId(orderId) : null,
            ]);

            setHasOrchestrationPlan(!!orchestrationId);
            setHasProduct(!!productId);
            setLoading(false);
        };

        checkAvailability();
    }, [orderId, orderState]);

    const isOrchestrationDisabled =
        !canViewOrchestration || DRAFT_STATES.includes(orderState) || !hasOrchestrationPlan || loading;

    const isProductDisabled =
        !canViewProduct || orderState === "draft" || !hasProduct || loading;

    const handleOrchestrationClick = async () => {
        if (isOrchestrationDisabled) return;
        const id = await service.getOrchestrationPlanId(orderId);
        navigateTo(
            id ? `${ORCHESTRATION_PLAN_DETAILS_URL}/${id}?tab=graph` : null,
            "No orchestration plan found"
        );
    };

    const handleProductClick = async () => {
        if (isProductDisabled) return;
        const id = await service.getContractProductId(orderId);
        navigateTo(
            id ? `/product-inventory/products-details-page/${id}?tab=Full+Hierarchy` : null,
            "No contract product found"
        );
    };

    return (
        <div className="d-flex align-items-center justify-content-between">
            <div>
                <h1 className="h3 m-0 py-3 d-inline-block">Order ID #{orderId}</h1>

                {canViewOrchestration && featureOrderOrchestration && (
                    <IconButton
                        onClick={handleOrchestrationClick}
                        disabled={isOrchestrationDisabled}
                        iconClass="icon-orchestration"
                        tooltip="View Orchestration Plan"
                    />
                )}

                {canViewProduct && featureProductInventory && (
                    <IconButton
                        onClick={handleProductClick}
                        disabled={isProductDisabled}
                        iconClass="icon-contract"
                        tooltip="View Products"
                    />
                )}
            </div>
            <ReloadButton onClick={handleReload}/>
        </div>
    );
};

export default OrderHeader;