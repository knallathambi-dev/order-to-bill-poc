// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {
    ARCHIVED_ORCHESTRATION_PLANS_PATH,
    BASE_FALLOUT_UI_URL,
    BASE_ORCHESTRATION_PLANS_REPORTING_UI_URL,
    BASE_ORCHESTRATION_PLANS_UI_URL,
    CONTRACT_STATISTICS_REPORT_PATH,
    DELIVERY_FACTORY_STATISTICS_REPORT_PATH,
    FALLOUT_INCIDENT_PATH,
    ORCHESTRATION_PLANS_ARCHIVEMENT_REPORTING_PATH,
    ORCHESTRATION_PLANS_HISTORICAL_REPORTING_PATH,
    ORCHESTRATION_PLANS_STATUS_REPORTING_PATH,
    PRODUCT_STATISTICS_REPORT_PATH,
    UNARCHIVED_ORCHESTRATION_PLANS_PATH,
} from "../utils/constants.js";

export const OrchestrationDeliverySideMenuItems = [
    {
        name: "Monitoring",
        path: BASE_ORCHESTRATION_PLANS_UI_URL,
        requires: "canViewOrchestrationPlans",
        children: [
            {name: "Orchestration Plans", path: UNARCHIVED_ORCHESTRATION_PLANS_PATH},
            {name: "Archived Orchestration Plans", path: ARCHIVED_ORCHESTRATION_PLANS_PATH},
        ],
    },
    {
        name: "Reporting",
        path: BASE_ORCHESTRATION_PLANS_REPORTING_UI_URL,
        requires: "canViewOrchestrationPlans",
        children: [
            {name: "Orchestration Plans Status", path: ORCHESTRATION_PLANS_STATUS_REPORTING_PATH},
            {
                name: "Archived/Unarchived Orchestration Plans",
                path: ORCHESTRATION_PLANS_ARCHIVEMENT_REPORTING_PATH,
            },
            {name: "Orchestration Plans History", path: ORCHESTRATION_PLANS_HISTORICAL_REPORTING_PATH},
            {name: "Orchestration Plans Contract Lead Time Statistics", path: CONTRACT_STATISTICS_REPORT_PATH},
            {name: "Orchestration Plans Product Lead Time Statistics", path: PRODUCT_STATISTICS_REPORT_PATH},
            {
                name: "Orchestration Plans Product Delivery Factory Lead Time Statistics",
                path: DELIVERY_FACTORY_STATISTICS_REPORT_PATH
            },
        ],
    },
    {
        name: "Administration",
        path: BASE_FALLOUT_UI_URL,
        requires: "canViewFalloutIncidents",
        children: [
            {name: "Fallout Incidents", path: FALLOUT_INCIDENT_PATH},
        ],
    },
];