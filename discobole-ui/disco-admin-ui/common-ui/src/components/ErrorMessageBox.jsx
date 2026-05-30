// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from 'prop-types';
import {formatToLocalDateTime} from '../utils/helper';

const ErrorField = ({label, value, muted}) => (
    <li className={`list-group-item ${muted ? 'text-muted' : 'text-dark'}`}>
        <span className="fw-bold me-1">{label}:</span>
        {value ?? '-'}
    </li>
);

ErrorField.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.node,
    muted: PropTypes.bool,
};

function ErrorMessageBox({errorMessages, isResolved, title}) {
    const resolved = isResolved ?? false;

    const heading = title
        ?? (isResolved != null ? 'Error Messages' : 'Error Log');

    const cardBorder = resolved ? 'border-light bg-body-secondary' : 'border-danger';
    const titleColor = resolved ? 'text-muted' : 'text-danger';
    const fieldMuted = resolved;

    return (
        <div className={`card bg-error-light ${cardBorder} border-1 mb-3`}>
            <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                    <h5 className={`card-title ${titleColor}`}>{heading}</h5>

                    {isResolved != null && (
                        <p className={`tag tag-sm status-value ${resolved ? 'completed' : 'aborted'}`}>
                            {resolved ? 'Resolved' : 'Unresolved'}
                        </p>
                    )}
                </div>

                {errorMessages.map((error, i) => {
                    if (!error.timeStamp && !error.message) return null;

                    const key = [error.code, error.timeStamp, i].filter(Boolean).join('_');

                    return (
                        <div key={key} className="scrollable-area">
                            <div className="break-paragrah">
                                <ul className="list-group list-group-flush borderless">
                                    {error.timeStamp && (
                                        <ErrorField
                                            label={resolved ? 'Resolution Date' : 'Error Date'}
                                            value={formatToLocalDateTime(error.timeStamp)}
                                            muted={fieldMuted}
                                        />
                                    )}
                                    {error.code != null && (
                                        <ErrorField
                                            label="Code"
                                            value={error.code}
                                            muted={fieldMuted}
                                        />
                                    )}
                                    {error.message != null && (
                                        <ErrorField
                                            label="Message"
                                            value={error.message}
                                            muted={fieldMuted}
                                        />
                                    )}
                                    {error.reason != null && (
                                        <ErrorField
                                            label="Comment"
                                            value={error.reason}
                                            muted={fieldMuted}
                                        />
                                    )}
                                </ul>

                                {i < errorMessages.length - 1 && (
                                    <div className="border-top border-dark my-3 border-1"/>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

export default ErrorMessageBox;

ErrorMessageBox.propTypes = {
    errorMessages: PropTypes.arrayOf(
        PropTypes.shape({
            timeStamp: PropTypes.string,
            message: PropTypes.string,
            code: PropTypes.string,
            reason: PropTypes.string,
        }),
    ).isRequired,
    isResolved: PropTypes.bool,
    title: PropTypes.string,
};