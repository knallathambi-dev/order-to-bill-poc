// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from 'prop-types';
import {formatToLocalDateTime} from '@discobole/common-ui';

const isDateTitle = (title) => title?.toLowerCase().includes('date');

const AccordionCardRow = ({title, value}) => (
    <tr>
        <td className="text-muted">
            <div className="d-flex align-items-center">{title}</div>
        </td>
        <td className="fw-bold text-end">
            {isDateTitle(title) && value !== 'undefined'
                ? formatToLocalDateTime(value)
                : value}
        </td>
    </tr>
);

export default AccordionCardRow;

AccordionCardRow.propTypes = {
    title: PropTypes.string.isRequired,
    value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.array,
        PropTypes.number,
        PropTypes.object,
    ]),
};