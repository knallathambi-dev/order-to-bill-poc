// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import React from "react";
import {Link} from "react-router-dom";
import {formatChartValue} from "../../common/constants.js";

const LegendData = ({data, legendStyle}) => {
    return (
        <table className="table chart-legend">
            <tbody>
            {legendStyle === "table" &&
                data.map((legend) => (
                    <tr key={legend.key}>
                        <td className="legend-col-color">
                            <div
                                className="legend-color-swatch"
                                style={{backgroundColor: legend.color}}
                            />
                        </td>
                        <td className="legend-col-label">
                            <Link to={legend.link}>{legend.label}</Link>
                        </td>
                        {legend.label !== "No content" && (
                            <td className="legend-col-value">
                                <strong>{formatChartValue(legend.value)}</strong>
                            </td>
                        )}
                    </tr>
                ))}
            {legendStyle === "row" && (
                <tr className="d-flex justify-content-center">
                    {data.map((legend) => (
                        <td className="d-flex align-items-center" key={legend.key}>
                            <span
                                className="legend-row-swatch"
                                style={{backgroundColor: legend.color}}
                            />
                            <Link to={legend.link}>{legend.label}</Link>
                        </td>
                    ))}
                </tr>
            )}
            </tbody>
        </table>
    );
};

LegendData.propTypes = {
    data: PropTypes.array.isRequired,
    legendStyle: PropTypes.string.isRequired,
};

export default LegendData;