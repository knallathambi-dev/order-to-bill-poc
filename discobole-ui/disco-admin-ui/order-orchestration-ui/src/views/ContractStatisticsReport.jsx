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
import {CONTRACT_NAME, CUSTOM_PERIOD} from '../utils/constants.js';
import StatisticsReport from '../components/Reporting/StatisticsReport';

const customField = {
    name: CONTRACT_NAME,
    type: 'text',
    placeholder: 'Contract Name',
    label: 'Contract Name',
    [CUSTOM_PERIOD]: 'always',
};

const ContractStatisticsReport = () => (
    <MonitoringPage title="Contract Statistics Report">
        <StatisticsReport reportBy={CONTRACT_NAME} customField={customField}/>
    </MonitoringPage>
);

export default ContractStatisticsReport;