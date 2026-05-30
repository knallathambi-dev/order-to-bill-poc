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

const AccordionCardSection = ({title, children}) => (
    <div className="card border-1 mb-3">
        <div className="card-body">
            <h5 className="card-title">{title}</h5>
            <div className="table-responsive">
                <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                    <tbody className="fw-semibold">
                    {children}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
);

export default AccordionCardSection;

AccordionCardSection.propTypes = {
    children: PropTypes.array.isRequired,
    title: PropTypes.string.isRequired,
};