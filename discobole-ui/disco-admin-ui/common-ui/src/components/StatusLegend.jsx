// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";

function StatusLegend({types}) {
    return (
        <div className="status-legend">
            {types.map(({name, icon: Icon}) => (
                <div key={name} className="status-legend-item">
                    <Icon width="40" height="40"/>
                    <span>{name}</span>
                </div>
            ))}
        </div>
    );
}

StatusLegend.propTypes = {
    types: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
            icon: PropTypes.elementType.isRequired,
        })
    ).isRequired,
};

export default StatusLegend;