// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import PropTypes from 'prop-types';
import {LoadingIndicator, StatusPanel} from '@discobole/common-ui';

import ReportingFilter from './ReportingFilter';
import LegendData from '../charts/LegendData.jsx';
import PieChart from '../charts/PieChart.jsx';
import api from '../../service/OrchestrationDeliveryAPI.js';
import {setFiltersToParams} from '../../service/orchestrationUtils.js';
import {ORCHESTRATION_PLANS_ENDPOINT, RECEIVED_DATE_END, RECEIVED_DATE_START} from '../../utils/constants.js';

const ReportInSpecificTime = ({getChartData, values, paramKey}) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [refresh, setRefresh] = useState(false);
    const [filteredValues, setFilteredValues] = useState(null);
    const [chartData, setChartData] = useState([]);
    const [numOfPlans, setNumOfPlans] = useState({});
    const [filterParams, setFilterParams] = useSearchParams();

    const params = new URLSearchParams({
        limit: 1,
        offset: 0,
    });

    const inputsData = {
        [RECEIVED_DATE_START]: 'receivedDate.gte',
        [RECEIVED_DATE_END]: 'receivedDate.lte',
    };

    useEffect(() => {
        setLoading(true);
        setFiltersToParams(inputsData, filterParams, params);
        fetchData(values);
    }, [refresh, filteredValues, values]);

    useEffect(() => {
        setChartData(getChartData(numOfPlans));
    }, [numOfPlans, getChartData]);

    const fetchPlanData = async () => {
        params.set('fields', 'state,id');
        return await api.get(ORCHESTRATION_PLANS_ENDPOINT, params);
    };

    const fetchData = async (values) => {
        const responseStatuses = [];
        setError('');

        try {
            await Promise.all(
                values.map(async (value) => {
                    params.set(paramKey, value);
                    const response = await fetchPlanData();

                    responseStatuses.push(response.status);

                    setNumOfPlans((prev) => ({
                        ...prev,
                        [value]: response.headers['x-total-count'],
                    }));

                    return response.data;
                })
            );

            if (responseStatuses.every((status) => status === 204)) {
                throw new Error('No data to display');
            }
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const onFilterSubmit = (filteredData) => {
        setFilteredValues({...filteredData});
        setFilterParams({...filteredData});
    };

    const hasData = Object.keys(numOfPlans).length > 0 && !error && !loading;
    const showLoading = loading && Object.keys(numOfPlans).length === 0;
    const showError = !loading && !!error;

    return (
        <>
            <ReportingFilter
                onFilterSubmit={onFilterSubmit}
                inputsData={inputsData}
                filterParams={filterParams}
            />

            {showLoading && <LoadingIndicator/>}

            {showError && (
                <StatusPanel
                    variant="error"
                    message={error}
                    onAction={() => setRefresh((prev) => !prev)}
                    actionLabel="Try again"
                />
            )}

            {hasData && (
                <div className="row align-items-center mt-4">
                    <div style={{height: '75vh'}} className="col-8">
                        <PieChart data={chartData}/>
                    </div>
                    <div className="col-4">
                        <LegendData data={chartData} legendStyle="table"/>
                    </div>
                </div>
            )}
        </>
    );
};

ReportInSpecificTime.propTypes = {
    getChartData: PropTypes.func.isRequired,
    values: PropTypes.array.isRequired,
    paramKey: PropTypes.string.isRequired,
};

export default ReportInSpecificTime;