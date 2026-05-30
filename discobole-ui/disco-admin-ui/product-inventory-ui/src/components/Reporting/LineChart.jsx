// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {ResponsiveLine} from '@nivo/line';
import PropTypes from 'prop-types';
import React from 'react';
import {formatChartValue} from "../../common/constants.js";

const LineChart = ({data, axisBottomLegend, axisLeftLegend, colorsResolver}) => {

    return (
        <ResponsiveLine
            data={data}
            margin={{top: 50, right: 200, bottom: 50, left: 60}}
            xScale={{type: 'point'}}
            yScale={{
                type: 'linear',
                min: 'auto',
                max: 'auto',
                stacked: false,
                reverse: false,
            }}
            yFormat=" >-.2f"
            axisTop={null}
            axisRight={null}
            axisBottom={{
                orient: 'bottom',
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: axisBottomLegend,
                legendOffset: 36,
                legendPosition: 'middle',
            }}
            axisLeft={{
                orient: 'left',
                tickSize: 5,
                tickPadding: 5,
                tickRotation: 0,
                legend: axisLeftLegend,
                legendOffset: -40,
                legendPosition: 'middle',
                format: formatChartValue
            }}
            pointSize={10}
            pointBorderWidth={2}
            pointBorderColor={{from: 'serieColor'}}
            pointLabelYOffset={-12}
            useMesh={true}
            legends={[
                {
                    anchor: 'right',
                    direction: 'column',
                    justify: false,
                    translateX: 100,
                    translateY: 0,
                    itemsSpacing: 0,
                    itemDirection: 'left-to-right',
                    itemWidth: 80,
                    itemHeight: 20,
                    itemOpacity: 0.75,
                    symbolSize: 12,
                    symbolShape: 'circle',
                    symbolBorderColor: 'rgba(0, 0, 0, .5)',
                    effects: [
                        {
                            on: 'hover',
                            style: {
                                itemBackground: 'rgba(0, 0, 0, .03)',
                                itemOpacity: 1,
                            },
                        },
                    ],
                },
            ]}
            colors={({id}) => colorsResolver(id)} // Extract color from data or default to black
        />
    );
};

export default LineChart;

LineChart.propTypes = {
    data: PropTypes.array.isRequired,
    axisBottomLegend: PropTypes.string.isRequired,
    axisLeftLegend: PropTypes.string.isRequired,
    colorsResolver: PropTypes.func.isRequired,
};