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
import {formatDateTimeValue, LoadingIndicator, MonitoringPage, StatusPanel} from '@discobole/common-ui';

import BarChart from '../components/charts/BarChart';
import LegendData from '../components/charts/LegendData';
import ReportingFilter from '../components/Reporting/ReportingFilter';
import api from '../service/OrchestrationDeliveryAPI.js';
import {setFiltersToParams} from '../service/orchestrationUtils.js';
import {
    CUSTOM_PERIOD,
    DAY_IN_MS,
    NUMBER_OF_COLUMNS,
    ORCHESTRATION_PLANS_ENDPOINT,
    PERIOD,
    PERIOD_DAY,
    PERIOD_MONTH,
    PERIOD_WEEK,
    RECEIVED_DATE_END,
    RECEIVED_DATE_START,
    STATECOLORS,
    UNARCHIVED_ORCHESTRATION_PLANS_UI_URL,
} from '../utils/constants.js';

const PAGE_LIMIT = 100;

const INPUTS_DATA = {
    [RECEIVED_DATE_START]: 'receivedDate.gte',
    [RECEIVED_DATE_END]: 'receivedDate.lte',
};

const GRAPH_DEFAULTS = {
    period: PERIOD_DAY.toString(),
    numberOfColumns: '7',
    customPeriod: false,
};

const formatDateToYYYYMMDD = (date) => formatDateTimeValue(date).substring(0, 10);

const getTotalPages = (totalItems) =>
    Math.ceil(parseInt((totalItems - 1) / PAGE_LIMIT) + 1);

const buildGraphDefaults = (filterParams) => ({
    period: filterParams.get(PERIOD) || GRAPH_DEFAULTS.period,
    numberOfColumns: filterParams.get(NUMBER_OF_COLUMNS) || GRAPH_DEFAULTS.numberOfColumns,
    customPeriod: filterParams.get(CUSTOM_PERIOD) === 'true' || GRAPH_DEFAULTS.customPeriod,
});

const buildLegendData = () =>
    Object.keys(STATECOLORS).map((state) => ({
        label: state[0].toUpperCase() + state.substring(1),
        link: `${UNARCHIVED_ORCHESTRATION_PLANS_UI_URL}?state=${state.toUpperCase()}`,
        color: STATECOLORS[state],
    }));

const editDateByPeriod = (date, period, startDate) => {
    const diffDays = (date.getTime() - startDate.getTime()) / DAY_IN_MS;
    if (diffDays < period && diffDays > 2) {
        date.setDate(date.getDate() - diffDays);
        return;
    }

    switch (period) {
        case PERIOD_MONTH:
            date.setMonth(date.getMonth() - 1);
            break;
        case PERIOD_WEEK:
            date.setDate(date.getDate() - 7);
            break;
        case PERIOD_DAY:
        default:
            date.setDate(date.getDate() - 1);
    }
};

const getColumnDates = (numberOfColumns, period, filteredValues, customPeriod) => {
    const allDates = [];
    const startDate = new Date(filteredValues[INPUTS_DATA[RECEIVED_DATE_START]]);
    const endDate = new Date(filteredValues[INPUTS_DATA[RECEIVED_DATE_END]]);
    const date = filteredValues[INPUTS_DATA[RECEIVED_DATE_END]] ? endDate : new Date();

    if (!customPeriod) {
        allDates.push(formatDateToYYYYMMDD(date));
        const cols = period === PERIOD_DAY ? numberOfColumns - 1 : numberOfColumns;

        for (let i = +cols; i > 0; i--) {
            editDateByPeriod(date, period, startDate);
            allDates.push(formatDateToYYYYMMDD(date));
        }
    } else {
        const diffTime = date.getTime() - startDate.getTime();
        const diffDates = Math.ceil(diffTime / (period * DAY_IN_MS));

        allDates.push(formatDateToYYYYMMDD(date.setDate(date.getDate() + 1)));

        for (let i = 1; i <= Math.ceil(diffDates); i++) {
            editDateByPeriod(date, period, startDate);
            if (date >= startDate && date <= endDate) {
                allDates.push(formatDateToYYYYMMDD(date.setDate(date.getDate())));
            }
        }
    }

    return allDates.reverse();
};

const applyDateLabels = (data, period, customPeriod) => {
    if (+period === PERIOD_DAY || !data.length) return;

    const lastIndex = data.length - 1;
    const beforeLastIndex = lastIndex - 1;

    data.forEach((colData, i) => {
        if (i < lastIndex) {
            const nextDate = new Date(data[i + 1].date);
            const endColumnDate =
                i === beforeLastIndex && !customPeriod
                    ? nextDate.setDate(nextDate.getDate())
                    : nextDate.setDate(nextDate.getDate() - 1);
            colData.date = `${colData.date} / ${formatDateToYYYYMMDD(endColumnDate)}`;
        }
    });
};

const mergeLastColumnIntoPrevious = (data, period, customPeriod) => {
    if ((+period === PERIOD_DAY && !customPeriod) || !data.length) return data;

    const lastIndex = data.length - 1;
    const beforeLastIndex = lastIndex - 1;

    Object.keys(data[0]).forEach((key) => {
        if (typeof data[beforeLastIndex][key] === 'number') {
            data[beforeLastIndex][key] += data[lastIndex][key];
        }
    });

    data.splice(lastIndex);
    return data;
};

const formatChartData = (dates, numOfEachState) =>
    dates.map((date) =>
        Object.keys(STATECOLORS).reduce(
            (acc, state) => {
                acc[state] = numOfEachState[date]?.[state] || 0;
                acc[`${state}Color`] = STATECOLORS[state];
                acc.isNotEmpty = acc.isNotEmpty || !!acc[state];
                return acc;
            },
            {date, isNotEmpty: false}
        )
    );

const HistoricalReport = () => {
    const [filterParams, setFilterParams] = useSearchParams();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [refresh, setRefresh] = useState(false);
    const [dates, setDates] = useState([]);
    const [chartData, setChartData] = useState([]);
    const [numOfEachState, setNumOfEachState] = useState({});
    const [filteredValues, setFilteredValues] = useState({});
    const [graphData, setGraphData] = useState(() => buildGraphDefaults(filterParams));

    const params = useMemo(() => new URLSearchParams({}), []);

    useEffect(() => {
        const initialParams = Object.fromEntries(filterParams.entries());
        initialParams.customPeriod =
            initialParams.customPeriod === 'true' || initialParams.customPeriod === true;
        if (initialParams.customPeriod) {
            setFilteredValues({...initialParams});
        }
    }, []);

    useEffect(() => {
        setFilterParams({...filteredValues, ...graphData});
    }, [graphData, filteredValues]);

    const fetchData = useCallback(async () => {
        const stateCounts = {};
        let currentPage = 1;
        let totalPages = 1;

        setNumOfEachState({});
        setLoading(true);
        params.set('fields', 'state,id,receivedDate');

        try {
            while (currentPage <= totalPages) {
                params.set('offset', PAGE_LIMIT * (currentPage - 1));
                const response = await api.get(ORCHESTRATION_PLANS_ENDPOINT, params);

                if (response.status === 204) {
                    throw new Error('No Data to display');
                }

                if (currentPage === 1) {
                    totalPages = getTotalPages(response.headers['x-total-count']);
                }

                const responseData = response.data;

                if (responseData.some((order) => !order.state)) {
                    setNumOfEachState({});
                    throw new Error('missing data');
                }

                responseData.forEach((plan) => {
                    let planDate = plan.receivedDate.substring(0, 10);
                    const planState = plan.state.toLowerCase();

                    if (dates.length) {
                        const columnDate = dates.filter(
                            (date, i) => planDate >= date && planDate < dates[i + 1]
                        );
                        planDate = columnDate[0] || planDate;
                    }

                    if (!stateCounts[planDate]) {
                        stateCounts[planDate] = {};
                    }
                    stateCounts[planDate][planState] = (stateCounts[planDate][planState] || 0) + 1;
                });

                setError(null);
                currentPage++;
            }

            setNumOfEachState(stateCounts);
        } catch (err) {
            setError(err.message);
            setNumOfEachState({});
        } finally {
            setLoading(false);
        }
    }, [dates, params]);

    useEffect(() => {
        const allDates = getColumnDates(
            +graphData.numberOfColumns,
            +graphData.period,
            filteredValues,
            graphData.customPeriod
        );
        setDates(allDates);
    }, [graphData.numberOfColumns, graphData.period, filteredValues]);

    useEffect(() => {
        if (dates.length === 0) return;

        if (graphData.customPeriod) {
            setFiltersToParams(INPUTS_DATA, filterParams, params);
        } else {
            params.delete(INPUTS_DATA[RECEIVED_DATE_END]);
            filterParams.set(INPUTS_DATA[RECEIVED_DATE_START], `${dates[0]}T00:00:00Z`);
            setFiltersToParams(INPUTS_DATA, filterParams, params);
        }

        fetchData();
    }, [dates, refresh, fetchData]);

    useEffect(() => {
        if (!Object.keys(numOfEachState).length) {
            setChartData([]);
            return;
        }

        const data = formatChartData(dates, numOfEachState);
        applyDateLabels(data, graphData.period, graphData.customPeriod);

        const isNoData = data.every((item) => !item.isNotEmpty);
        if (isNoData) {
            setChartData([]);
        } else {
            setChartData(mergeLastColumnIntoPrevious(data, graphData.period, graphData.customPeriod));
        }
    }, [numOfEachState, dates, graphData.period, graphData.customPeriod]);

    const changeGraphData = (changeCase, values) => {
        switch (changeCase) {
            case 'set':
                setGraphData({
                    period: values.period || filterParams.get(PERIOD) || GRAPH_DEFAULTS.period,
                    numberOfColumns:
                        values.numberOfColumns ||
                        filterParams.get(NUMBER_OF_COLUMNS) ||
                        GRAPH_DEFAULTS.numberOfColumns,
                    customPeriod:
                        values.customPeriod ||
                        filterParams.get(CUSTOM_PERIOD) === 'true' ||
                        GRAPH_DEFAULTS.customPeriod,
                });
                break;
            case PERIOD:
                setGraphData((prev) => ({...prev, period: GRAPH_DEFAULTS.period}));
                break;
            case CUSTOM_PERIOD:
                setGraphData((prev) => ({...prev, customPeriod: !prev.customPeriod}));
                break;
            case 'clearForm':
            default:
                setGraphData({...GRAPH_DEFAULTS});
        }
    };

    const hasData = Object.keys(numOfEachState).length > 0 && chartData.length > 0;
    const showEmptyState = !loading && (error || chartData.length === 0) && !Object.keys(numOfEachState).length;

    return (
        <MonitoringPage title="Orchestration Plans History">
            <ReportingFilter
                changeGraphData={changeGraphData}
                from="history"
                graphDefaultValuesWithoutParams={GRAPH_DEFAULTS}
                graphDefaultValues={buildGraphDefaults(filterParams)}
                inputsData={INPUTS_DATA}
                filterParams={filterParams}
                onFilterSubmit={(filteredData) => setFilteredValues({...filteredData})}
            />

            {loading && <LoadingIndicator/>}

            {showEmptyState && (
                <StatusPanel
                    variant={error ? "error" : "info"}
                    title={error ? "Failed to load report" : "No results found"}
                    message={error || "Try adjusting your filters or refreshing the page."}
                    onAction={() => setRefresh((prev) => !prev)}
                    actionLabel={error ? "Try again" : "Refresh"}
                />
            )}

            {hasData && !loading && !error && (
                <div className="mt-5 overflow-visible">
                    <LegendData data={buildLegendData()} legendStyle="row"/>
                    <div style={{height: '75vh'}}>
                        <BarChart
                            data={chartData}
                            keys={Object.keys(STATECOLORS)}
                            axisBottom="date"
                            axisLeft="state"
                        />
                    </div>
                </div>
            )}
        </MonitoringPage>
    );
};

export default HistoricalReport;