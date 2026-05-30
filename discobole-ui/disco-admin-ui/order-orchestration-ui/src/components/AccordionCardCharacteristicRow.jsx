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
import AccordionCardRow from './AccordionCardRow';
import {CHARACTERISTIC_DATE_TYPE} from '../utils/constants.js';

const formatIfDate = (name, value, type) =>
    name?.toLowerCase().includes('date') || type === CHARACTERISTIC_DATE_TYPE
        ? formatToLocalDateTime(value)
        : value;

const AccordionCardCharacteristicRow = ({name, value, type}) => (
    <tr>
        <td colSpan="2" className="p-0">
            <table
                className="table characterstic-compact-table align-middle mb-0 fs-6"
                style={{border: 'none', borderCollapse: 'separate'}}
            >
                <tbody className="fw-semibold">
                <AccordionCardRow title="Type" value={type}/>
                <AccordionCardRow title="Name" value={name}/>
                {name?.toLowerCase() === 'validity' && Array.isArray(value)
                    ? value.map((val) => (
                        <AccordionCardRow
                            key={val.key}
                            title={val.key}
                            value={formatIfDate(name, val.value, type)}
                        />
                    ))
                    : (
                        <AccordionCardRow
                            title="Value"
                            value={
                                Array.isArray(value)
                                    ? value.map((val) => (
                                        <div key={val.key}>
                                            {val.key + ': ' + formatIfDate(name, val.value, type)}
                                        </div>
                                    ))
                                    : formatIfDate(name, value, type)
                            }
                        />
                    )}
                </tbody>
            </table>
        </td>
    </tr>
);

export default AccordionCardCharacteristicRow;

AccordionCardCharacteristicRow.propTypes = {
    name: PropTypes.string.isRequired,
    value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.array,
        PropTypes.number,
        PropTypes.object,
    ]),
    type: PropTypes.string,
};