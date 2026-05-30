// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import {MonitoringPage} from '@discobole/common-ui';

import ReportInSpecificTime from '../components/Reporting/ReportInSpecificTime';
import {
    STATECOLORS,
    STATUS_ABORTED,
    STATUS_ACKNOWLEDGED,
    STATUS_EXECUTED,
    STATUS_HELD,
    STATUS_IN_PROGRESS,
    STATUS_INITIALIZED,
    STATUS_PLANNED,
    STATUS_REJECTED,
    UNARCHIVED_ORCHESTRATION_PLANS_UI_URL,
} from '../utils/constants.js';

const ALL_STATES = Object.keys(STATECOLORS).map((state) => state.toUpperCase());

const STATUS_DATA = [
    {status: STATUS_ACKNOWLEDGED, label: 'Acknowledged'},
    {status: STATUS_IN_PROGRESS, label: 'In progress'},
    {status: STATUS_INITIALIZED, label: 'Initialized'},
    {status: STATUS_PLANNED, label: 'Planned'},
    {status: STATUS_HELD, label: 'Held'},
    {status: STATUS_ABORTED, label: 'Aborted'},
    {status: STATUS_REJECTED, label: 'Rejected'},
    {status: STATUS_EXECUTED, label: 'Executed'},
];

const getChartData = (numOfEachState) =>
    STATUS_DATA.map(({status, label}) => ({
        id: status.toLowerCase(),
        label,
        value: numOfEachState?.[status.toUpperCase()] || 0,
        color: STATECOLORS[status.toLowerCase()],
        link: `${UNARCHIVED_ORCHESTRATION_PLANS_UI_URL}?state=${status.toUpperCase()}`,
    }));

const OrchestrationReporting = () => (
    <MonitoringPage title="Orchestration Plans Status">
        <ReportInSpecificTime getChartData={getChartData} values={ALL_STATES} paramKey="state"/>
    </MonitoringPage>
);

export default OrchestrationReporting;