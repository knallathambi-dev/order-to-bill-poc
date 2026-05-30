// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {LoadingIndicator, StatusPanel, useSideMenu} from '@discobole/common-ui';
import OrchestrationPlanDetails from '../components/OrchestrationPlanDetails';
import {ORCHESTRATION_PLANS_ENDPOINT} from '../utils/constants.js';
import api from '../service/OrchestrationDeliveryAPI.js';

function OrchestrationPlanDetailsPage() {
    const {isActiveNav} = useSideMenu();

    const [data, setData] = useState();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const {id} = useParams();

    const fetchData = () => {
        setLoading(true);
        setError(null);

        api.get(`${ORCHESTRATION_PLANS_ENDPOINT}/${id}`)
            .then((response) => {
                setData(response.data || {});
            })
            .catch((err) => {
                if (err.response?.data?.reason === "orchestration plan doesn't exist") {
                    setError(new Error('No data found'));
                } else {
                    setError(err);
                }
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchData();
    }, [id]); // eslint-disable-line react-hooks/exhaustive-deps

    if (loading) {
        return <LoadingIndicator />;
    }

    return (
        <div
            className={isActiveNav ? 'py-1 content-wrapper active-cont' : 'py-1 content-wrapper'}
            data-testid="wrapper"
        >
            {error && error.message !== 'No data found' ? (
                <StatusPanel type="error" message={error.message} onRetry={fetchData} />
            ) : (
                <OrchestrationPlanDetails dto={data} error={error} refreshData={fetchData} />
            )}
        </div>
    );
}

export default OrchestrationPlanDetailsPage;