// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import {ResponsiveBar} from '@nivo/bar';
import PropTypes from 'prop-types';
import {SAMPLE_WINDOW} from "../../utils/constants.js";

const BarChart = ({
                      data,
                      keys,
                      axisBottom,
                      axisLeft,
                      grouped = false,
                      hideXAxisLabel = false,
                      dataLengthNoRotationLimit = 7,
                      enableLabel = true,
                      enableTotals = true,
                      customTooltip = false,
                  }) => {
    const TICK_ROTATION_DEGREE_MINUS_35 = -35;
    const TICK_ROTATION_DEGREE_ZERO = 0;

    return (
        <ResponsiveBar
            data={data}
            keys={keys}
            indexBy={axisBottom}
            margin={{top: 35, right: 130, bottom: 50, left: 60}}
            padding={0.3}
            valueScale={{type: 'linear'}}
            indexScale={{type: 'band', round: true}}
            colors={({id, data}) => String(data[`${id}Color`])}
            tooltip={
                customTooltip
                    ? ({id, color, data}) => (
                        <div
                            style={{
                                padding: '6px 8px',
                                background: '#fff',
                                border: `1px solid ${color}`,
                                borderRadius: '4px',
                            }}
                        >
                            <strong style={{color}}>{id}</strong>: {data[id + '_formatted']}
                            <br/>
                            <span style={{fontSize: '12px'}}>
                                  {customTooltip?.label}: {data[customTooltip.key]}
                              </span>
                        </div>
                    )
                    : undefined
            }
            defs={[
                {
                    id: 'dots',
                    type: 'patternDots',
                    background: 'inherit',
                    color: '#38bcb2',
                    size: 4,
                    padding: 1,
                    stagger: true,
                },
                {
                    id: 'lines',
                    type: 'patternLines',
                    background: 'inherit',
                    color: '#eed312',
                    rotation: -45,
                    lineWidth: 6,
                    spacing: 10,
                },
            ]}
            borderColor={{
                from: 'color',
                modifiers: [['darker', 1.6]],
            }}
            axisTop={null}
            axisRight={null}
            axisBottom={{
                format: (value) => (axisBottom === SAMPLE_WINDOW ? value.split('_')[0] : value),
                tickSize: 5,
                tickPadding: 5,
                tickRotation:
                    data.length > dataLengthNoRotationLimit
                        ? TICK_ROTATION_DEGREE_MINUS_35
                        : TICK_ROTATION_DEGREE_ZERO,
                legend: hideXAxisLabel ? '' : axisBottom,
                legendPosition: 'end',
                legendOffset: 10,
                truncateTickAt: 0,
            }}
            axisLeft={{
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: axisLeft,
                legendPosition: 'middle',
                legendOffset: -50,
                truncateTickAt: 0,
            }}
            labelSkipWidth={12}
            labelSkipHeight={12}
            labelTextColor={{
                from: 'color',
                modifiers: [['darker', 1.6]],
            }}
            role="application"
            ariaLabel="Nivo bar chart demo"
            barAriaLabel={(e) => e.id + ': ' + e.formattedValue + ` in ${axisBottom}: ` + e.indexValue}
            enableTotals={enableTotals}
            totalsOffset={10}
            enableLabel={enableLabel}
            groupMode={grouped ? 'grouped' : 'stacked'}
        />
    );
};

export default BarChart;

BarChart.propTypes = {
    data: PropTypes.array.isRequired,
    keys: PropTypes.array.isRequired,
    axisBottom: PropTypes.string.isRequired,
    axisLeft: PropTypes.string.isRequired,
    grouped: PropTypes.bool,
    hideXAxisLabel: PropTypes.bool,
    dataLengthNoRotationLimit: PropTypes.number,
    enableLabel: PropTypes.bool,
    enableTotals: PropTypes.bool,
    customTooltip: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
};