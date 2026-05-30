// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from 'prop-types';

const PartyDetails = ({party}) => (
    <div className="card h-100">
        <div className="card-header py-3">
            <div className="card-title">
                <h3 className="mb-0">Party Details</h3>
            </div>
        </div>
        <div className="card-body pb-0">
            <div className="table-responsive">
                <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                    <tbody className="fw-semibold">
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Party ID
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {party.partyOrPartyRole?.id}
                        </td>
                    </tr>
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Party Name
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {party.partyOrPartyRole?.name}
                        </td>
                    </tr>
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Party Role
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {party.role}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
);

PartyDetails.propTypes = {
    party: PropTypes.object.isRequired
};

export default PartyDetails;