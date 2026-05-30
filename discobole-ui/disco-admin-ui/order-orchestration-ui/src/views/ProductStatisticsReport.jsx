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

import StatisticsReport from '../components/Reporting/StatisticsReport';
import {CUSTOM_PERIOD, SPEC_ID} from '../utils/constants.js';

const CUSTOM_FIELD = {
    name: SPEC_ID,
    type: 'text',
    placeholder: 'Specification ID',
    label: 'Specification ID',
    [CUSTOM_PERIOD]: 'always',
};

const ProductStatisticsReport = () => (
    <MonitoringPage title="Product Statistics Report">
        <StatisticsReport reportBy={SPEC_ID} customField={CUSTOM_FIELD}/>
    </MonitoringPage>
);

export default ProductStatisticsReport;