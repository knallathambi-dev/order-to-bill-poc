// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import PropTypes from 'prop-types';
import {formatToLocalDateTime} from "@discobole/common-ui";

const ShippingDetails = ({shippingDetails}) => (
    shippingDetails &&
    <div className="card h-100">
        <div className="card-header py-3">
            <div className="card-title">
                <h3 className="mb-0">Shipping Details</h3>
            </div>
        </div>
        <div className="card-body pb-0">
            <div className="table-responsive">
                <table className="table align-middle table-row-bordered mb-0 fs-6 gy-5">
                    <tbody className="fw-semibold">
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Shipping Mode
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {shippingDetails.shippingMode ? shippingDetails.shippingMode : "Not Available"}
                        </td>
                    </tr>
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Shipping Address
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {shippingDetails.shippingAddress ? shippingDetails.shippingAddress : "Not Available"}
                        </td>
                    </tr>
                    <tr>
                        <td className="text-muted" style={{width: '20%'}}>
                            <div className="d-flex align-items-center">
                                Requested Delivery Date
                            </div>
                        </td>
                        <td className="fw-bold text-end">
                            {shippingDetails.requestedDeliveryDay ? formatToLocalDateTime(shippingDetails.requestedDeliveryDay) : "Not Available"}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
);

ShippingDetails.propTypes = {
    shippingDetails: PropTypes.object.isRequired
};

export default ShippingDetails;