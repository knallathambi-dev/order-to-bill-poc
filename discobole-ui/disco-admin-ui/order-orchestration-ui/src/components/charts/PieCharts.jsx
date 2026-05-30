// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from 'prop-types';
import React from 'react';
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from 'recharts';

export default function PieCharts({data, colors, dataKey, nameKey, typeData, displayToolTip}) {
    return (
        <ResponsiveContainer width="100%" height="100%">
            <PieChart>
                <Pie
                    dataKey={dataKey}
                    nameKey={nameKey}
                    isAnimationActive={false}
                    data={data}
                    cx="50%"
                    cy="50%"
                    outerRadius="63%"
                    fill="#8884d8"
                    label={({
                                name,
                                percent
                            }) => displayToolTip ? `${(percent * 100).toFixed(0)}%` : null}
                    labelLine={displayToolTip}
                >
                    {data.map((entry, index) => (
                        <Cell key={`cell-${colors[index % colors.length]}`}
                              fill={colors[index % colors.length]}/>
                    ))}
                </Pie>
                {displayToolTip && (
                    <Tooltip
                        formatter={(value, name, props) => [`${value} ${typeData}`, `${props.payload.status}`]}/>
                )}
                <Legend align="right" layout="vertical"/>
            </PieChart>
        </ResponsiveContainer>
    );
}

PieCharts.propTypes = {
    data: PropTypes.arrayOf(PropTypes.object).isRequired,
    colors: PropTypes.array.isRequired,
    dataKey: PropTypes.string.isRequired,
    nameKey: PropTypes.string.isRequired,
    typeData: PropTypes.string.isRequired,
    displayToolTip: PropTypes.bool.isRequired,
}