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
import {Link} from 'react-router-dom';

const LegendData = ({data, legendStyle}) => {
    return (
        <table className="table chart-legend">
            <tbody>
            {legendStyle === 'table' &&
                data.map((legend) => {
                    return (
                        <tr key={legend.label}>
                            <td style={{width: '10%'}}>
                                <div
                                    style={{
                                        width: '20px',
                                        height: '20px',
                                        backgroundColor: legend.color,
                                    }}
                                ></div>
                            </td>
                            <td style={{width: '70%'}}>
                                {legend?.link ? (
                                    <Link to={legend.link}>{legend.label}</Link>
                                ) : (
                                    <span>{legend.label}</span>
                                )}
                            </td>
                            {legend.label !== 'No content' && (
                                <td style={{width: '20%'}}>
                                    <strong>{legend.value}</strong>
                                </td>
                            )}
                        </tr>
                    );
                })}
            {legendStyle === 'row' && (
                <tr className="d-flex justify-content-center">
                    {data.map((legend) => (
                        <td className="d-flex align-items-center" key={legend.label}>
                                <span
                                    style={{
                                        width: '20px',
                                        height: '20px',
                                        display: 'inline-block',
                                        marginRight: '5px',
                                        backgroundColor: legend.color,
                                    }}
                                ></span>
                            {legend?.link ? (
                                <Link to={legend.link}>{legend.label}</Link>
                            ) : (
                                <span>{legend.label}</span>
                            )}
                        </td>
                    ))}
                </tr>
            )}
            </tbody>
        </table>
    );
};

export default LegendData;

LegendData.propTypes = {
    data: PropTypes.array.isRequired,
    legendStyle: PropTypes.string.isRequired,
};
