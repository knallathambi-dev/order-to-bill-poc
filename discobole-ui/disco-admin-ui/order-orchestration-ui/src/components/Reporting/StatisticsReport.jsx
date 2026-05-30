// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import PropTypes from 'prop-types';
import {formatToLocalDateTime, LoadingIndicator, StatusPanel} from '@discobole/common-ui';

import {
    APPROXIMATE_COUNT,
    AVERAGE_LEAD_TIME,
    AVG_ACTUAL_LEAD_TIME,
    CONTRACT_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT,
    CONTRACT_NAME,
    CUSTOM_PERIOD,
    DELIVERY_FACTORY,
    LEAD_TIME_COLORS,
    MAX_ACTUAL_LEAD_TIME,
    MIN_ACTUAL_LEAD_TIME,
    MIN_MAX_LEAD_TIME,
    NODE_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT,
    SAMPLE_WINDOW,
    SPEC_ID,
    TIME_PERIOD_END,
    TIME_PERIOD_START,
} from '../../utils/constants.js';
import {getFormattedValue, getLeadTime} from '../../service/orchestrationUtils.js';
import api from '../../service/OrchestrationDeliveryAPI.js';
import StatisticsFilter from './StatisticsFilter';
import LegendData from '../charts/LegendData.jsx';
import BarChart from '../charts/BarChart.jsx';

const REPORT_TITLES = {
    [CONTRACT_NAME]: 'Contract',
    [SPEC_ID]: 'Product',
    [DELIVERY_FACTORY]: 'Delivery Factory',
};

const NUMBER_OF_SECONDS_IN_DAY = 86400;
const NUMBER_OF_SECONDS_IN_HOUR = 3600;
const NUMBER_OF_SECONDS_IN_MINUTE = 60;

const formatAxisLeadTimeUnit = (statistics) => {
    const maxLeadTime = Math.max(
        ...statistics.map((statistic) =>
            statistic[MAX_ACTUAL_LEAD_TIME]
                ? statistic[MAX_ACTUAL_LEAD_TIME]
                : statistic[AVG_ACTUAL_LEAD_TIME]
        )
    );

    if (maxLeadTime >= NUMBER_OF_SECONDS_IN_DAY) return {divideBy: NUMBER_OF_SECONDS_IN_DAY, unit: 'days'};
    if (maxLeadTime >= NUMBER_OF_SECONDS_IN_HOUR) return {divideBy: NUMBER_OF_SECONDS_IN_HOUR, unit: 'hours'};
    if (maxLeadTime >= NUMBER_OF_SECONDS_IN_MINUTE) return {divideBy: NUMBER_OF_SECONDS_IN_MINUTE, unit: 'minutes'};
    return {divideBy: 1, unit: 'seconds'};
};

const StatisticsReport = ({reportBy, customField, deliveryFactoryOptions}) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [refresh, setRefresh] = useState(false);
    const [data, setData] = useState([]);
    const [values, setValues] = useState({});
    const [filterParams, setFilterParams] = useSearchParams();
    const [unit, setUnit] = useState('seconds');

    const params = useMemo(() => new URLSearchParams({}), []);

    const reportTitle = REPORT_TITLES[reportBy] || '';

    const formatData = (statistics) => {
        return statistics.map((statistic, i) => {
            const formattedDate = formatToLocalDateTime(statistic[SAMPLE_WINDOW], true);
            const {divideBy, unit} = formatAxisLeadTimeUnit(statistics);

            setUnit(unit);

            return {
                ...statistic,
                [SAMPLE_WINDOW]: `${formattedDate}_${i}`,
                [AVG_ACTUAL_LEAD_TIME]: isNaN(statistic[AVG_ACTUAL_LEAD_TIME] / divideBy)
                    ? 0
                    : (statistic[AVG_ACTUAL_LEAD_TIME] / divideBy).toFixed(2),
                [MIN_ACTUAL_LEAD_TIME]: isNaN(statistic[MIN_ACTUAL_LEAD_TIME] / divideBy)
                    ? 0
                    : (statistic[MIN_ACTUAL_LEAD_TIME] / divideBy).toFixed(2),
                [MAX_ACTUAL_LEAD_TIME]: isNaN(statistic[MAX_ACTUAL_LEAD_TIME] / divideBy)
                    ? 0
                    : (statistic[MAX_ACTUAL_LEAD_TIME] / divideBy).toFixed(2),
                [`${AVG_ACTUAL_LEAD_TIME}_formatted`]: getLeadTime(statistic[AVG_ACTUAL_LEAD_TIME]),
                [`${MIN_ACTUAL_LEAD_TIME}_formatted`]: getLeadTime(statistic[MIN_ACTUAL_LEAD_TIME]),
                [`${MAX_ACTUAL_LEAD_TIME}_formatted`]: getLeadTime(statistic[MAX_ACTUAL_LEAD_TIME]),
                ...LEAD_TIME_COLORS,
            };
        });
    };

    const handleValueByCustomPeriod = (values) => {
        if (values[CUSTOM_PERIOD]) {
            return {
                ...values,
                [APPROXIMATE_COUNT]: '',
                [TIME_PERIOD_START]: getFormattedValue(TIME_PERIOD_START, values[TIME_PERIOD_START]),
                [TIME_PERIOD_END]: getFormattedValue(TIME_PERIOD_END, values[TIME_PERIOD_END]),
            };
        }
        return {
            ...values,
            [APPROXIMATE_COUNT]: getFormattedValue(APPROXIMATE_COUNT, values[APPROXIMATE_COUNT]),
            [TIME_PERIOD_START]: '',
            [TIME_PERIOD_END]: '',
        };
    };

    const setParams = (values) => {
        const updatedValues = handleValueByCustomPeriod(values);

        params.set([reportBy], updatedValues[reportBy].trim());
        params.set('fields', `${[reportBy]},statistics`);
        params.set('min', updatedValues[MIN_MAX_LEAD_TIME]);
        params.set('max', updatedValues[MIN_MAX_LEAD_TIME]);
        params.set('average', updatedValues[AVERAGE_LEAD_TIME]);
        params.set(APPROXIMATE_COUNT, updatedValues[APPROXIMATE_COUNT].trim());
        params.set(TIME_PERIOD_START, updatedValues[TIME_PERIOD_START]);
        params.set(TIME_PERIOD_END, updatedValues[TIME_PERIOD_END]);
    };

    const fetchData = useCallback(
        async (values) => {
            const requestUrl =
                reportBy !== CONTRACT_NAME
                    ? NODE_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT
                    : CONTRACT_LEAD_TIME_HISTORY_STATISTICS_ENDPOINT;

            setError(null);
            setLoading(true);
            setParams(values);

            try {
                const response = await api.get(requestUrl, params);

                if (response.status === 204) {
                    throw new Error('No Data to display');
                }

                setData(formatData(response.data.statistics));
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        },
        [params, refresh]
    );

    const resetData = () => setData([]);

    const getKeys = () => {
        const keys = [];
        if (values[MIN_MAX_LEAD_TIME]) {
            keys.push(MIN_ACTUAL_LEAD_TIME, MAX_ACTUAL_LEAD_TIME);
        }
        if (values[AVERAGE_LEAD_TIME]) {
            keys.push(AVG_ACTUAL_LEAD_TIME);
        }
        return keys;
    };

    const getReportCaption = () => {
        switch (reportBy) {
            case CONTRACT_NAME:
                return `${reportTitle.toLowerCase()} ${values[reportBy]}`;
            case SPEC_ID:
                return `${reportTitle.toLowerCase()} with spec id ${values[reportBy]}`;
            case DELIVERY_FACTORY:
                return `products delivered to ${deliveryFactoryOptions[values[reportBy]]}`;
            default:
                return '';
        }
    };

    useEffect(() => {
        const params = Object.fromEntries(filterParams.entries());

        let initialParams = Object.fromEntries(
            Object.entries(params).map(([key, value]) => [key, getFormattedValue(key, value)])
        );

        initialParams = handleValueByCustomPeriod(initialParams);

        if (initialParams[reportBy]) {
            setValues(initialParams);
        }
    }, [reportBy]);

    useEffect(() => {
        if (values && (Object.keys(values).length > 0 || filterParams.entries().length)) {
            fetchData(values);
            setFilterParams({...values});
        }
    }, [refresh, values]);

    const getLegendData = () => [
        ...(values[MIN_MAX_LEAD_TIME]
            ? [
                {label: 'Min', color: LEAD_TIME_COLORS[`${MIN_ACTUAL_LEAD_TIME}Color`]},
                {label: 'Max', color: LEAD_TIME_COLORS[`${MAX_ACTUAL_LEAD_TIME}Color`]},
            ]
            : []),
        ...(values[AVERAGE_LEAD_TIME]
            ? [{label: 'Average', color: LEAD_TIME_COLORS[`${AVG_ACTUAL_LEAD_TIME}Color`]}]
            : []),
    ];

    const hasData = !loading && !error && data.length > 0;
    const showEmpty = !loading && !error && !data.length;
    const showError = !loading && !!error;

    return (
        <>
            <StatisticsFilter
                onFilterSubmit={(values) => setValues(values)}
                resetData={resetData}
                customField={customField}
                reportBy={reportBy}
                filterParams={filterParams}
            />

            {loading && <LoadingIndicator/>}

            {showError && (
                <StatusPanel
                    variant="error"
                    message={error}
                    onAction={() => setRefresh((prev) => !prev)}
                    actionLabel="Try again"
                />
            )}

            {showEmpty && (
                <StatusPanel
                    variant="info"
                    title="No data yet"
                    message={`Please enter ${reportBy === SPEC_ID ? 'specification id' : `${reportTitle.toLowerCase()} name`} to show report`}
                />
            )}

            {hasData && (
                <div className="mt-5 overflow-visible">
                    <h6 className="mt-5 text-center">
                        Lead time statistics for the {getReportCaption()}
                    </h6>
                    <LegendData data={getLegendData()} legendStyle="row"/>
                    <div style={{height: '75vh'}}>
                        <BarChart
                            data={data}
                            keys={getKeys()}
                            axisBottom={SAMPLE_WINDOW}
                            axisLeft={`Lead Time (${unit})`}
                            grouped={true}
                            hideXAxisLabel={true}
                            dataLengthNoRotationLimit={6}
                            enableLabel={false}
                            enableTotals={false}
                            customTooltip={{label: 'Sample Size', key: 'sampleSize'}}
                        />
                    </div>
                </div>
            )}
        </>
    );
};

StatisticsReport.propTypes = {
    reportBy: PropTypes.string.isRequired,
    customField: PropTypes.object,
    deliveryFactoryOptions: PropTypes.object,
};

export default StatisticsReport;