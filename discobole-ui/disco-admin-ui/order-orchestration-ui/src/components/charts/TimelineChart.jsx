// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useMemo, useState} from 'react';
import PropTypes from 'prop-types';
import {ResponsiveBar} from '@nivo/bar';
import {formatToLocalDateTime} from "@discobole/common-ui";
import {getLeadTime} from "../../service/orchestrationUtils.js";

// Configuration constants
const CHART_CONFIG = {
    LABEL_MIN_BAR_PX: 24,           // Minimum bar width for labels
    LABEL_CHAR_PX: 7,               // Pixels per character estimate
    MINUTE_IN_MS: 60 * 1000,        // Milliseconds per minute
    DEFAULT_HEIGHT: 400,            // Default chart height
    MARGINS: {top: 60, right: 30, bottom: 30, left: 30},
};

// Color palette fallback
const DEFAULT_COLORS = [
    '#d6e8d5', '#f8cecc', '#e1d5e7', '#dbe7fc',
    '#f5f5dc', '#ffd59e', '#b2dfdb', '#c5cae9'
];


/**
 * Timeline chart component that visualizes orchestration plan execution
 * @param {Array} items - Timeline items to display
 * @param {number} height - Chart height in pixels
 * @param {Object} colorByType - Color mapping for different item types
 */
const TimelineChart = ({items, height = CHART_CONFIG.DEFAULT_HEIGHT, colorByType}) => {
    const [hoveredBar, setHoveredBar] = useState(null);

    // Parse and transform data for chart rendering
    const chartData = useMemo(() => {
        if (!Array.isArray(items) || items.length === 0) {
            return getEmptyChartData();
        }

        const parsedItems = parseTimelineItems(items);
        if (parsedItems.length === 0) {
            return getEmptyChartData();
        }

        const minDate = Math.min(...parsedItems.map(d => d.startMs));
        const transformedData = transformDataForChart(parsedItems, minDate);
        const colorMap = buildColorMap(transformedData, colorByType);
        const baseTickValues = generateTickValues(transformedData);

        return {
            dataForChart: transformedData.slice().reverse(), // Reverse for proper display order
            minDateMs: minDate,
            minuteInMilliSeconds: CHART_CONFIG.MINUTE_IN_MS,
            uniqueTypes: Array.from(new Set(transformedData.map(d => d.type))).filter(t => t !== 'Separator'),
            resolvedColorByType: colorMap,
            baseTickValues,
            scaleMax: Math.max(...transformedData.map(d => d.offset + d.duration)),
        };
    }, [items, colorByType]);

    const {
        dataForChart,
        minDateMs,
        minuteInMilliSeconds,
        uniqueTypes,
        resolvedColorByType,
        baseTickValues,
        scaleMax
    } = chartData;

    // Calculate tick values dynamically based on hover state (for immediate updates)
    const tickValues = useMemo(() => {
        if (!dataForChart.length) return baseTickValues;

        let tickVals = [...baseTickValues];

        // If hovering over a bar, add its start and end ticks
        if (hoveredBar) {
            const hoveredData = dataForChart.find(d => d.id === hoveredBar);
            if (hoveredData) {
                const hoveredStart = hoveredData.offset;
                const hoveredEnd = hoveredData.offset + hoveredData.duration;

                // Special handling for overall plan - just show base ticks (no duplicates)
                if (hoveredBar === 'overall-plan') {
                    tickVals = [...baseTickValues];
                } else {
                    // Find first and last non-separator nodes by time (excluding overall plan)
                    const nonSeparatorNodes = dataForChart.filter(d => !d.isSeparator && d.id !== 'overall-plan');

                    const tenSecondsInMinutes = 10 / 60; // Convert 10 seconds to minutes

                    // Find all nodes that start earliest (within 10 seconds of the absolute earliest)
                    const earliestStartTime = Math.min(...nonSeparatorNodes.map(node => node.offset));
                    const earliestNodes = nonSeparatorNodes.filter(node =>
                        (node.offset - earliestStartTime) <= tenSecondsInMinutes
                    );

                    // Find all nodes that end latest (within 10 seconds of the absolute latest)
                    const latestEndTime = Math.max(...nonSeparatorNodes.map(node => node.offset + node.duration));
                    const latestNodes = nonSeparatorNodes.filter(node =>
                        (latestEndTime - (node.offset + node.duration)) <= tenSecondsInMinutes
                    );

                    // Start with base tick values
                    let filteredBaseTicks = [...baseTickValues];
                    const originalEndIndex = baseTickValues.length - 1;

                    // If hovering any of the earliest nodes, always hide overall start tick
                    const isHoveringEarliestNode = earliestNodes.some(node => node.id === hoveredBar);
                    if (isHoveringEarliestNode) {
                        filteredBaseTicks = filteredBaseTicks.filter((_, index) => index !== 0);
                    }

                    // If hovering any of the latest nodes, always hide overall end tick but show the node's end tick
                    const isHoveringLatestNode = latestNodes.some(node => node.id === hoveredBar);
                    if (isHoveringLatestNode) {
                        // Use the original end value to filter, not the index
                        const endTickValue = baseTickValues[originalEndIndex];
                        filteredBaseTicks = filteredBaseTicks.filter(tick => tick !== endTickValue);
                    }

                    // Add hovered node's start and end ticks
                    tickVals = [...new Set([...filteredBaseTicks, hoveredStart, hoveredEnd])].sort((a, b) => a - b);
                }
            }
        }
        return tickVals;
    }, [baseTickValues, dataForChart, hoveredBar]);

    if (!dataForChart.length) {
        return (
            <div className="p-5 text-center">There is no data to display.</div>
        );
    }

    return (
        <div style={{height, width: '100%'}}>
            <ResponsiveBar
                data={dataForChart}
                keys={["offset", "duration"]}
                indexBy="id"
                layout="horizontal"
                groupMode="stacked"
                margin={CHART_CONFIG.MARGINS}
                padding={0.1}
                axisTop={null}
                axisRight={null}
                axisBottom={{
                    renderTick: (tick) => renderCustomTick(tick, minDateMs, minuteInMilliSeconds),
                    tickValues,
                    legendOffset: 40,
                    legendPosition: 'middle',
                    tickSize: 5,
                    tickPadding: 5,
                    tickRotation: 0,
                }}
                axisLeft={null}
                enableLabel={false}
                // Fit the X scale snugly around the bars (0 .. last bar end)
                valueScale={{type: 'linear', min: 0, max: scaleMax}}
                colors={({id, data}) => {
                    if (id === 'offset') return 'rgba(0,0,0,0)';
                    return resolvedColorByType[data.type] || '#69b3a2';
                }}
                borderWidth={0}
                enableGridX={false}
                enableGridY={false}
                isInteractive={true}
                onMouseEnter={(bar) => {
                    if (bar.id === 'duration') {
                        setHoveredBar(bar.data.id);
                    }
                }}
                onMouseLeave={() => {
                    setHoveredBar(null);
                }}
                animate={false}
                tooltip={({id, data}) => {
                    // Skip tooltips for separator items
                    if (data.isSeparator) return null;

                    if (id === 'duration') {
                        // Use actual lead time from node data instead of calculating
                        const actualLeadTimeSeconds = data.actualLeadTimeSeconds;

                        return (
                            <div style={{
                                background: 'white',
                                padding: '8px 12px',
                                border: '1px solid #ccc',
                                borderRadius: '4px',
                                fontSize: '12px',
                                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                            }}>
                                <div style={{margin: '4px'}}>{data.label}</div>
                                <strong>Lead Time
                                    :</strong> {actualLeadTimeSeconds ? getLeadTime(actualLeadTimeSeconds) : 'N/A'}<br/>
                                <strong>Start Date
                                    :</strong> {data.startDate ? formatToLocalDateTime(new Date(data.startDate).getTime()) : 'N/A'}<br/>
                                <strong>Actual Start Date
                                    :</strong> {formatToLocalDateTime(data.actualStartDate)}<br/>
                                <strong>Actual Completion Date
                                    :</strong> {formatToLocalDateTime(data.actualCompletionDate)}
                            </div>
                        );
                    }
                    return null;
                }}
                legends={[
                    {
                        anchor: 'top',
                        direction: 'row',
                        translateX: 10,
                        translateY: -60,
                        itemWidth: 140,
                        itemHeight: 6,
                        itemsSpacing: 10,
                        symbolSize: 16,
                        symbolShape: 'square',
                        itemDirection: "top-to-bottom",
                        data: uniqueTypes.map((t) => ({
                            id: t,
                            label: t,
                            color: resolvedColorByType[t]
                        })),
                    },
                ]}
                layers={[
                    'grid',
                    'axes',
                    (props) => renderStartGuideLines(props, dataForChart),
                    'bars',
                    (props) => renderBarBorders(props, dataForChart, resolvedColorByType),
                    (props) => renderBarLabels(props, dataForChart),
                    'legends',
                    'markers',
                ]}
            />
        </div>
    );
};

export default TimelineChart;

TimelineChart.propTypes = {
    items: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
            label: PropTypes.string,
            type: PropTypes.string,
            startDate: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date)]).isRequired,
            actualStartDate: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date)]).isRequired,
            actualCompletionDate: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date)]).isRequired,
            actualLeadTimeSeconds: PropTypes.number,
            isRootNode: PropTypes.bool,
            isSeparator: PropTypes.bool,
        })
    ),
    height: PropTypes.number,
    colorByType: PropTypes.objectOf(PropTypes.string),
};

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

/**
 * Darkens a hex color by a given percentage
 * @param {string} hex - The hex color (e.g., '#B5E8F7')
 * @param {number} percent - The percentage to darken (0-100)
 * @returns {string} The darkened hex color
 */
function darkenColor(hex, percent = 40) {
    // Remove # if present
    hex = hex.replace(/^#/, '');

    // Parse RGB values
    const r = parseInt(hex.substring(0, 2), 16);
    const g = parseInt(hex.substring(2, 4), 16);
    const b = parseInt(hex.substring(4, 6), 16);

    // Darken each component
    const darken = (component) => Math.max(0, Math.floor(component * (1 - percent / 100)));

    // Convert back to hex
    const toHex = (n) => n.toString(16).padStart(2, '0');
    return `#${toHex(darken(r))}${toHex(darken(g))}${toHex(darken(b))}`;
}

/**
 * Returns empty chart data structure
 */
function getEmptyChartData() {
    return {
        dataForChart: [],
        minDateMs: 0,
        minuteInMilliSeconds: CHART_CONFIG.MINUTE_IN_MS,
        uniqueTypes: [],
        resolvedColorByType: {},
        baseTickValues: [],
        scaleMax: 0,
    };
}

/**
 * Parses and validates timeline items
 */
function parseTimelineItems(items) {
    return items
        .filter(item => item?.actualStartDate && item?.actualCompletionDate)
        .map(item => {
            const startMs = new Date(item.actualStartDate).getTime();
            const endMs = new Date(item.actualCompletionDate).getTime();
            return {
                id: item.id,
                label: item.label || item.id,
                type: item.type || 'Other',
                startMs,
                endMs,
                isRootNode: item.isRootNode,
                isSeparator: item.isSeparator,
                // Preserve date properties for tooltip
                startDate: item.startDate,
                actualStartDate: item.actualStartDate,
                actualCompletionDate: item.actualCompletionDate,
                orderItemStartDate: item.orderItemStartDate,
                actualLeadTimeSeconds: item.actualLeadTimeSeconds,
            };
        })
        .filter(item =>
            Number.isFinite(item.startMs) &&
            Number.isFinite(item.endMs) &&
            item.endMs >= item.startMs
        );
}

/**
 * Transforms parsed items into chart-compatible format
 */
function transformDataForChart(parsedItems, minDate) {
    return parsedItems.map(item => ({
        id: item.id,
        label: item.label,
        type: item.type,
        startKey: item.startMs,
        offset: (item.startMs - minDate) / CHART_CONFIG.MINUTE_IN_MS,
        duration: (item.endMs - item.startMs) / CHART_CONFIG.MINUTE_IN_MS,
        isRootNode: item.isRootNode,
        isSeparator: item.isSeparator,
        actualLeadTimeSeconds: item.actualLeadTimeSeconds,
        startDate: item.startDate,
        actualStartDate: item.actualStartDate,
        actualCompletionDate: item.actualCompletionDate,
        orderItemStartDate: item.orderItemStartDate,
    }));
}

/**
 * Builds color mapping for chart items
 */
function buildColorMap(data, colorByType) {
    const legendTypes = Array.from(new Set(data.map(d => d.type))).filter(t => t !== 'Separator');
    const resolvedColors = {};

    legendTypes.forEach((type, idx) => {
        resolvedColors[type] = colorByType?.[type] || DEFAULT_COLORS[idx % DEFAULT_COLORS.length];
    });

    // Add separator color if provided
    if (colorByType?.['Separator']) {
        resolvedColors['Separator'] = colorByType['Separator'];
    }

    return resolvedColors;
}

/**
 * Generates base tick values (only start and end of timeline)
 * Uses overall plan item boundaries if available, otherwise falls back to all data
 */
function generateTickValues(data) {
    // Look for the overall plan item first
    const overallPlanItem = data.find(d => d.id === 'overall-plan');

    if (overallPlanItem) {
        // Use overall plan item's start and end
        const minOffset = overallPlanItem.offset;
        const maxEndValue = overallPlanItem.offset + overallPlanItem.duration;
        return [minOffset, maxEndValue];
    }

    // Fallback to calculating from all data if no overall plan item
    const minOffset = Math.min(...data.map(d => d.offset));
    const maxEndValue = Math.max(...data.map(d => d.offset + d.duration));
    return [minOffset, maxEndValue];
}

/**
 * Renders custom two-line tick labels
 */
function renderCustomTick(tick, minDateMs, minuteInMilliSeconds) {
    const actualDateMs = minDateMs + (Number(tick.value) * minuteInMilliSeconds);
    const date = new Date(actualDateMs);

    const timeStr = date.toLocaleTimeString('en-GB', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    });
    const dateStr = formatToLocalDateTime(date).split(' ')[0];

    return (
        <g transform={`translate(${tick.x},${tick.y + 12})`}>
            <text textAnchor="middle" dominantBaseline="central" style={{fontSize: 11}}>
                <tspan x={0} dy={-4}>{timeStr}</tspan>
                <tspan x={0} dy={14}>{dateStr}</tspan>
            </text>
        </g>
    );
}

/**
 * Renders vertical guide lines
 */
function renderStartGuideLines(props, dataForChart) {
    const {xScale, innerHeight} = props;

    return (
        <g>
            {dataForChart.map((d, i) => {
                if (d.isSeparator) return null;

                const xStart = xScale(d.offset);
                return (
                    <g key={`guides-${d.id}`}>
                        <line
                            x1={xStart} x2={xStart} y1={0} y2={innerHeight}
                            stroke="#999" strokeDasharray="4" strokeWidth={1}
                        />
                        {i === 0 && (
                            <line
                                x1={xScale(d.offset + d.duration)}
                                x2={xScale(d.offset + d.duration)}
                                y1={0} y2={innerHeight}
                                stroke="#999" strokeDasharray="4" strokeWidth={1}
                            />
                        )}
                    </g>
                );
            })}
        </g>
    );
}

/**
 * Renders borders around bars
 */
function renderBarBorders(props, dataForChart, colorByType) {
    const {xScale, yScale} = props;
    const bandwidth = typeof yScale.bandwidth === 'function' ? yScale.bandwidth() : 24;

    return (
        <g>
            {dataForChart.map((d) => {
                if (d.isSeparator) return null;

                const y = yScale(d.id);
                const xStart = xScale(d.offset);
                const xEnd = xScale(d.offset + d.duration);
                const width = xEnd - xStart;

                // Get the base color and darken it for the border
                const baseColor = colorByType[d.type] || '#69b3a2';
                const borderColor = darkenColor(baseColor, 30);

                return (
                    <rect
                        key={`duration-border-${d.id}`}
                        x={xStart} y={y} width={width} height={bandwidth}
                        fill="none" stroke={borderColor} strokeWidth={1} rx={2}
                    />
                );
            })}
        </g>
    );
}

/**
 * Renders truncated labels on bars
 */
function renderBarLabels(props, dataForChart) {
    const {xScale, yScale} = props;
    const bandwidth = typeof yScale.bandwidth === 'function' ? yScale.bandwidth() : 24;

    return (
        <g>
            {dataForChart.map((d) => {
                if (d.isSeparator) return null;

                const y = yScale(d.id);
                const xStart = xScale(d.offset);
                const xEnd = xScale(d.offset + d.duration);
                const midX = (xStart + xEnd) / 2;
                const width = Math.max(0, xEnd - xStart);

                if (width < CHART_CONFIG.LABEL_MIN_BAR_PX) return null;

                const maxChars = Math.max(1, Math.floor((width - 8) / CHART_CONFIG.LABEL_CHAR_PX));
                const fullLabel = String(d.label || '');
                const displayLabel = fullLabel.length > maxChars
                    ? `${fullLabel.slice(0, Math.max(0, maxChars - 1))}…`
                    : fullLabel;

                return (
                    <text
                        key={`label-${d.id}`}
                        x={midX} y={y + bandwidth / 2}
                        dominantBaseline="middle" textAnchor="middle"
                        fill="#000000" fontSize={12} fontWeight="500" pointerEvents="none"
                    >
                        {displayLabel}
                    </text>
                );
            })}
        </g>
    );
}