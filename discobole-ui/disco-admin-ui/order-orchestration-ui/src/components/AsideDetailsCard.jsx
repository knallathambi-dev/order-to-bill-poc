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

export default function AsideDetailsCard({title, data}) {
    return (
        <div className="card mb-3">
            {title && <div className="card-header py-3">
                <div className="card-title">
                    <h3 className="mb-0">{title}</h3>
                </div>
            </div>}
            <div className="card-body">
                <div className="table-responsive">
                    <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                        <tbody className="fw-semibold">
                        {Object.keys(data).map((key) => {
                            return data[key] && (
                                <tr key={key}>
                                    <td className="text-muted">
                                        <div className="d-flex align-items-center">
                                            {key}
                                        </div>
                                    </td>
                                    {key === 'Plan Id' ? (
                                            <td className="fw-bold text-end text-break">
                                                {data[key]}
                                            </td>
                                        ) :
                                        <td className="fw-bold text-end">
                                            {key === 'State' && <p className="mb-0">
                                                        <span
                                                            className={`tag tag-sm status-value ${data[key]?.toLowerCase()}`}>
                                                            {data[key]}
                                                        </span>
                                            </p>}
                                            {key.toLowerCase().includes('date') && formatToLocalDateTime(data[key])}
                                            {(key !== "State" && !key.toLowerCase().includes('date')) && (data[key])}
                                        </td>
                                    }
                                </tr>)
                        })}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}

AsideDetailsCard.propTypes = {
    data: PropTypes.object.isRequired
};