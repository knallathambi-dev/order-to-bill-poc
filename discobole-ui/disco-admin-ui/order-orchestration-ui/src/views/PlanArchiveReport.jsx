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
import {ARCHIVED_ORCHESTRATION_PLANS_UI_URL, UNARCHIVED_ORCHESTRATION_PLANS_UI_URL,} from '../utils/constants.js';

const VALUES = ['true', 'false'];

const ARCHIVE_DATA = [
    {
        id: 'archived',
        label: 'Archived',
        key: 'true',
        color: '#e2821b',
        link: ARCHIVED_ORCHESTRATION_PLANS_UI_URL,
    },
    {
        id: 'unarchived',
        label: 'Unarchived',
        key: 'false',
        color: '#29bece',
        link: UNARCHIVED_ORCHESTRATION_PLANS_UI_URL,
    },
];

const getChartData = (numOfPlans) =>
    ARCHIVE_DATA.map(({id, label, key, color, link}) => ({
        id,
        label,
        value: numOfPlans?.[key] || 0,
        color,
        link,
    }));

const PlanArchiveReport = () => (
    <MonitoringPage title="Archived/Unarchived Orchestration Plans">
        <ReportInSpecificTime getChartData={getChartData} paramKey="archived" values={VALUES}/>
    </MonitoringPage>
);

export default PlanArchiveReport;