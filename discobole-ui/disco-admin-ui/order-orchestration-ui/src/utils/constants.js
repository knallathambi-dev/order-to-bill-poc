// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const ORCHESTRATION_SERVICE_URL = "/cood";
export const ORCHESTRATION_PLANS_ENDPOINT = `${ORCHESTRATION_SERVICE_URL}/orchestrationPlan`;
export const BASE_ORCHESTRATION_PLANS_UI_URL = '/orchestration-delivery/monitoring';
export const BASE_ORCHESTRATION_PLANS_REPORTING_UI_URL = '/orchestration-delivery/reporting';
export const BASE_FALLOUT_UI_URL = '/orchestration-delivery/administration';
export const USER_ROLES_URL = "/userRolePermission";

export const ARCHIVED_ORCHESTRATION_PLANS_PATH = '/archived-orchestration-plans';
export const UNARCHIVED_ORCHESTRATION_PLANS_PATH = '/orchestration-plans';
export const ORCHESTRATION_PLANS_STATUS_REPORTING_PATH = '/plans-statuses-reporting';
export const ORCHESTRATION_PLANS_ARCHIVEMENT_REPORTING_PATH = '/plans-archivement-reporting';
export const ORCHESTRATION_PLANS_HISTORICAL_REPORTING_PATH = '/plans-historical-report';
export const CONTRACT_STATISTICS_REPORT_PATH = '/contract-statistics-report';
export const PRODUCT_STATISTICS_REPORT_PATH = '/product-statistics-report';
export const DELIVERY_FACTORY_STATISTICS_REPORT_PATH = '/delivery-factory-statistics-report';
export const FALLOUT_INCIDENT_PATH = '/fallout-incidents';

export const ARCHIVED_ORCHESTRATION_PLANS_UI_URL = `${BASE_ORCHESTRATION_PLANS_UI_URL}${ARCHIVED_ORCHESTRATION_PLANS_PATH}`;
export const UNARCHIVED_ORCHESTRATION_PLANS_UI_URL = `${BASE_ORCHESTRATION_PLANS_UI_URL}${UNARCHIVED_ORCHESTRATION_PLANS_PATH}`;
export const ARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL = `${ARCHIVED_ORCHESTRATION_PLANS_UI_URL}/orchestration-details-page`;
export const UNARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL = `${UNARCHIVED_ORCHESTRATION_PLANS_UI_URL}/orchestration-details-page`;
export const FALLOUT_INCIDENT_UI_URL = `${BASE_FALLOUT_UI_URL}${FALLOUT_INCIDENT_PATH}`;

export const CONTRACT_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT = `${ORCHESTRATION_SERVICE_URL}/leadTimeHistoryStatistics/contract`;
export const NODE_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT = `${ORCHESTRATION_SERVICE_URL}/leadTimeHistoryStatistics/node`;
export const FALLOUT_MANAGEMENT_SERVICE_URL = "/fallout";

export const FALLOUT_INCIDENT_ENDPOINT = `${FALLOUT_MANAGEMENT_SERVICE_URL}/falloutIncident`;

export const PROCESS_MANAGEMENT_SERVICE_URL = "/fallout/processManagement/v1";

export const PROCESS_FLOW_ENDPOINT = `${PROCESS_MANAGEMENT_SERVICE_URL}/processFlow`;

export const DAY_IN_MS = 24 * 60 * 60 * 1000;
export const PERIOD_MONTH = 30;
export const PERIOD_WEEK = 7;
export const PERIOD_DAY = 1;

export const FALLOUT_ID = 'falloutId';
export const ORDER_ID = 'orderId';
export const CREATION_DATE_START = 'creationDateStart';
export const CREATION_DATE_END = 'creationDateEnd';
export const PARTY_ID = 'partyId';
export const PARTY_NAME = 'partyName';
export const PARTY_ROLE = 'partyRole';
export const STATUS = 'status';
export const PLAN_ID = 'planId';
export const RECEIVED_DATE_START = 'receivedDateStart';
export const RECEIVED_DATE_END = 'receivedDateEnd';
export const DELIVERY_DATE_START = 'deliveryDateStart';
export const DELIVERY_DATE_END = 'deliveryDateEnd';
export const NODE_STATUS = 'nodeStatus';
export const NODE_ID = 'nodeId';
export const CUSTOM_PERIOD = 'customPeriod';
export const PERIOD = 'period';
export const NUMBER_OF_COLUMNS = 'numberOfColumns';
export const CONTRACT_NAME = 'contractName';
export const SPEC_ID = 'productSpecId';
export const DELIVERY_FACTORY = 'deliveryFactoryName';
export const TIME_PERIOD_START = 'timePeriodStart';
export const TIME_PERIOD_END = 'timePeriodEnd';
export const APPROXIMATE_COUNT = 'approximateCount';
export const AVERAGE_LEAD_TIME = 'averageLeadTime';
export const MIN_MAX_LEAD_TIME = 'minMaxLeadTime';
export const SAMPLE_WINDOW = 'sampleWindow';
export const PRODUCT_ORDER_ID = 'Product Order Id';

export const STATUS_INITIALIZED = 'Initialized';
export const STATUS_ACKNOWLEDGED = 'Acknowledged';
export const STATUS_IN_PROGRESS = 'InProgress';
export const STATUS_IN_DELIVERY = 'InDelivery';
export const STATUS_HELD = 'Held';
export const STATUS_ABORTED = 'Aborted';
export const STATUS_REJECTED = 'Rejected';
export const STATUS_FAILED = 'Failed';
export const STATUS_COMPLETED = 'Completed';
export const STATUS_PLANNED = 'Planned';
export const STATUS_EXECUTED = 'Executed';

export const STATECOLORS = {
    [STATUS_ACKNOWLEDGED.toLowerCase()]: '#62BFF9',
    [STATUS_IN_PROGRESS.toLowerCase()]: '#527EDB',
    [STATUS_INITIALIZED.toLowerCase()]: '#707C83',
    [STATUS_PLANNED.toLowerCase()]: '#A0A7A8',
    [STATUS_HELD.toLowerCase()]: '#F28327',
    [STATUS_ABORTED.toLowerCase()]: '#FD6161',
    [STATUS_REJECTED.toLowerCase()]: '#FD6190',
    [STATUS_EXECUTED.toLowerCase()]: '#67CB67',
};

export const MIN_ACTUAL_LEAD_TIME = 'minActualLeadTime';
export const MAX_ACTUAL_LEAD_TIME = 'maxActualLeadTime';
export const AVG_ACTUAL_LEAD_TIME = 'averageActualLeadTime';

export const LEAD_TIME_COLORS = {
    [`${MIN_ACTUAL_LEAD_TIME}Color`]: '#A885D8',
    [`${MAX_ACTUAL_LEAD_TIME}Color`]: '#4BB4E6',
    [`${AVG_ACTUAL_LEAD_TIME}Color`]: '#FFD200',
};

export const UNDEFINED_LEAD_TIME_VALUE = "9223372036854775807"
export const UNDEFINED_DATE_VALUE = "9999-12-31T23:59:59.999Z"

export const getStatusOptions = (isArchived) =>
    isArchived
        ? [STATUS_REJECTED, STATUS_ABORTED, STATUS_EXECUTED]
        : [
            STATUS_PLANNED,
            STATUS_INITIALIZED,
            STATUS_ACKNOWLEDGED,
            STATUS_IN_PROGRESS,
            STATUS_REJECTED,
            STATUS_EXECUTED,
            STATUS_HELD,
            STATUS_ABORTED,
        ];

export const getNodeStatusOptions = (isArchived) =>
    isArchived
        ? [STATUS_ABORTED, STATUS_REJECTED, STATUS_FAILED, STATUS_COMPLETED]
        : [
            STATUS_INITIALIZED,
            STATUS_ACKNOWLEDGED,
            STATUS_IN_PROGRESS,
            STATUS_IN_DELIVERY,
            STATUS_HELD,
            STATUS_ABORTED,
            STATUS_REJECTED,
            STATUS_FAILED,
            STATUS_COMPLETED,
        ];

export const CHARACTERISTIC_VALIDITY_TYPE = 'ValidityCharacteristic';
export const CHARACTERISTIC_ADDRESS_TYPE = 'AddressCharacteristic';
export const CHARACTERISTIC_DATE_TYPE = 'DateCharacteristic';

export const ACTION_TYPES = {
    add: "Add",
    modify: "Modify",
    terminate: "Terminate",
    delete: "Delete",
    migrate: "Migrate",
};

export const ORDER_INVENTORY_URL = "/productOrderingManagement";
export const PRODUCT_INVENTORY_URL = "/productInventory/productInventoryManagement";