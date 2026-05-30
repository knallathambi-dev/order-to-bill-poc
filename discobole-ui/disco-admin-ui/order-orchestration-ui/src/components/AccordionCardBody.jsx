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

const AccordionCardBody = ({children}) => {
    return (
        <div className="card bg-body-tertiary border-1 mb-4 pt-3">
            <div className="card-body">{children}</div>
        </div>
    );
};

export default AccordionCardBody;

AccordionCardBody.propTypes = {
    children: PropTypes.oneOfType([PropTypes.array, PropTypes.object]),
};