// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from 'react';
import {MonitoringPage} from '@discobole/common-ui';
import {CUSTOM_PERIOD, DELIVERY_FACTORY} from '../utils/constants.js';
import StatisticsReport from '../components/Reporting/StatisticsReport';

const REVIEW = 'review';
const INTEGRATION = 'integration';
const STAGING = 'staging';

const buildDeliveryFactoryOptions = (env) => ({
    Shipment: 'Shipment',
    [`https://mobile-factory${env ? `-${env}` : ''}.apps.fr01.paas.tech.orange${env === REVIEW ? '/tmf-api' : ''}/serviceOrdering/v1/serviceOrder`]:
        'Mobile Delivery Factory',
    [`https://fix-factory${env ? `-${env}` : ''}.apps.fr01.paas.tech.orange${env === REVIEW ? '/tmf-api' : ''}/serviceOrdering/v1/serviceOrder`]:
        'Fixed Delivery Factory',
    [`https://partner-factory${env ? `-${env}` : ''}.apps.fr01.paas.tech.orange${env === REVIEW ? '/tmf-api' : ''}/serviceOrdering/v1/serviceOrder`]:
        'Partner Delivery Factory',
});

const DeliveryFactoryStatisticsReport = () => {
    const [env, setEnv] = useState('');

    const deliveryFactoryOptions = buildDeliveryFactoryOptions(env);

    const customField = {
        name: DELIVERY_FACTORY,
        type: 'select',
        placeholder: 'Select Delivery Factory Name',
        label: 'Delivery Factory Name',
        options: Object.values(deliveryFactoryOptions),
        optionsValues: Object.keys(deliveryFactoryOptions),
        [CUSTOM_PERIOD]: 'always',
    };

    useEffect(() => {
        try {
            const proxyURL = env.GATEWAY_URL;

            if (proxyURL.includes(REVIEW)) setEnv(REVIEW);
            else if (proxyURL.includes(INTEGRATION)) setEnv(INTEGRATION);
            else if (proxyURL.includes(STAGING)) setEnv(STAGING);
            else setEnv('');
        } catch (err) {
            console.error(err);
        }
    }, []);

    return (
        <MonitoringPage title="Delivery Factory Statistics Report">
            <StatisticsReport
                reportBy={DELIVERY_FACTORY}
                customField={customField}
                deliveryFactoryOptions={deliveryFactoryOptions}
            />
        </MonitoringPage>
    );
};

export default DeliveryFactoryStatisticsReport;