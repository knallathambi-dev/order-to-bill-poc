// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {LoadingIndicator, MonitoringPage, StatusPanel} from '@discobole/common-ui';
import FalloutIncidentsDetails from '../components/Administration/FalloutIncidentsDetails';
import {FALLOUT_INCIDENT_ENDPOINT, ORCHESTRATION_PLANS_ENDPOINT} from '../utils/constants.js';
import api from '../service/OrchestrationDeliveryAPI.js';

function FalloutIncidentsDetailsPage() {
    const [data, setData] = useState();
    const [relatedOrchestrationPlanNodes, setRelatedOrchestrationPlanNodes] = useState();
    const [nodeDetails, setNodeDetails] = useState();
    const [planDetails, setPlanDetails] = useState();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const {id} = useParams();

    const fetchData = () => {
        setLoading(true);
        setError(null);

        api.get(`${FALLOUT_INCIDENT_ENDPOINT}/${id}`)
            .then((response) => {
                let resRelatedEntity = {};
                response.data.relatedEntity.forEach((res) => {
                    resRelatedEntity[res.role] = {
                        id: res.id,
                        '@referredType': res['@referredType'],
                    };
                });

                const responseData = {
                    ...response.data,
                    relatedEntity: resRelatedEntity,
                };

                setData(responseData);
                fetchRelatedNodeDetails(
                    responseData.relatedEntity.relatedOrchestrationPlan.id,
                    responseData.relatedEntity.initiator.id
                );
            })
            .catch((error) => {
                setError(error);
                setLoading(false);
            });
    };

    const fetchRelatedNodeDetails = (parentRelatedEntityId, relatedEntityId) => {
        api.get(`${ORCHESTRATION_PLANS_ENDPOINT}/${parentRelatedEntityId}`)
            .then((response) => {
                const nodes = response.data.orchestrationPlanNodes;
                const plan = response.data;

                setPlanDetails(plan);
                setRelatedOrchestrationPlanNodes(nodes);
                setNodeDetails(nodes?.find((node) => node.id === relatedEntityId));
                setLoading(false);
            })
            .catch((error) => {
                setError(error);
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchData();
    }, [id]);

    return (
        <MonitoringPage>
            {loading && <LoadingIndicator/>}

            {!loading && error && (
                <StatusPanel
                    variant="error"
                    message={error.message}
                    onAction={fetchData}
                    actionLabel="Try again"
                />
            )}

            {!loading && !error && (
                <FalloutIncidentsDetails
                    dto={data}
                    relatedOrchestrationPlanNodes={relatedOrchestrationPlanNodes}
                    nodeDetails={nodeDetails}
                    planDetails={planDetails}
                    onCloseSubmitSuccess={fetchData}
                />
            )}
        </MonitoringPage>
    );
}

export default FalloutIncidentsDetailsPage;