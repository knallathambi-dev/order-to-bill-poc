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
import {calculatePricingForOrder, getOperation, hasCharges, ORDER_STATUSES, renderPrices} from './utils/orderUtils';
import {formatToLocalDateTime} from "@discobole/common-ui";

const OrderInfo = ({order, creationDate, requestedCompletionDate, atomicItemsCount, orderState}) => {
    const pricing = calculatePricingForOrder(order);
    const operation = getOperation(order.productOrderItem);

    return (
        <div className="order-info">
            <div className="row row-cols-auto d-flex align-items-center">
                <div className="col">
                    <div className="order-info-item">
                        <p className="mb-0">Creation Date: <strong>{formatToLocalDateTime(creationDate)}</strong></p>
                    </div>
                </div>
                {requestedCompletionDate && (
                    <div className="col">
                        <div className="order-info-item">
                            <p className="mb-0">Requested Completion
                                Date: <strong>{formatToLocalDateTime(requestedCompletionDate)}</strong></p>
                        </div>
                    </div>
                )}
                <div className="col">
                    <div className="order-info-item">
                        <p className="mb-0">Order Items: <strong>{atomicItemsCount} items</strong></p>
                    </div>
                </div>
                {hasCharges(pricing.current) && (
                    <div className="col">
                        <div className="order-info-item">
                            <p className="mb-0">
                                Order Total Price <small className="text-muted" style={{fontSize: '12px'}}>(tax incl.)
                            </small>: <strong>{renderPrices(pricing.current, pricing.currency)}</strong>
                                {hasCharges(pricing.discount) && (
                                    <span className="text-danger fw-bold">
                                      {' '}(Discount: {renderPrices(pricing.discount, pricing.currency)})
                                     </span>
                                )}
                            </p>
                        </div>
                    </div>
                )}
                <div className="col">
                    <div className="order-info-item">
                        <div className="d-flex">
                            <p className="mb-0">Order State: <span
                                className={`tag tag-sm status-value ${(orderState).toLowerCase()}`}>
                                    {ORDER_STATUSES[orderState]}
                                </span>
                            </p>
                        </div>
                    </div>
                </div>
                {operation && (
                    <div className="col">
                        <div className="order-info-item">
                            <p className="mb-0">Operation: <strong>{operation}</strong></p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

OrderInfo.propTypes = {
    order: PropTypes.object.isRequired,
    creationDate: PropTypes.string.isRequired,
    requestedCompletionDate: PropTypes.string,
    atomicItemsCount: PropTypes.number.isRequired,
    orderState: PropTypes.string.isRequired
};

export default OrderInfo;