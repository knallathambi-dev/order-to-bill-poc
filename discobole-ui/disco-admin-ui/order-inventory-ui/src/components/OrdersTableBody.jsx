// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Link} from 'react-router-dom';
import React from 'react';
import {formatToLocalDateTime} from "@discobole/common-ui";
import {getOperation, ORDER_STATUSES} from "./OrderDetails/utils/orderUtils";

function OrdersTableBody({data, fields, allFields}) {

    return (
        <tbody>
        {data.length > 0 &&
            data.map(order => {
                const operation = getOperation(order.productOrderItem);

                return (
                    <tr key={order.id}>
                        {fields && fields.map((field, index) => (
                            <React.Fragment key={index}>
                                {(field.name === allFields[0].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        {order.relatedParty && order.relatedParty[0]?.partyOrPartyRole && order.relatedParty[0]?.partyOrPartyRole.id}
                                    </td>
                                }

                                {(field.name === allFields[1].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        {order.relatedParty && order.relatedParty[0]?.partyOrPartyRole && order.relatedParty[0]?.partyOrPartyRole.name}
                                    </td>
                                }

                                {(field.name === allFields[2].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        <Link to={'/order-inventory/orders-details-page/' + order.id}>
                                            {order.id}
                                        </Link>
                                    </td>
                                }

                                {(field.name === allFields[3].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        {formatToLocalDateTime(order.creationDate)}
                                    </td>
                                }

                                {field.name === allFields[4].name && field.checked && (
                                    <td key={`${field.name}-${order.id}`}>
                                        {operation}
                                    </td>
                                )}

                                {(field.name === allFields[5].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        {order.relatedParty && order.relatedParty[0]?.role}
                                    </td>
                                }

                                {(field.name === allFields[6].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        <p className="mb-0">
                                            <span className={`tag tag-sm status-value ${(order.state).toLowerCase()}`}>
                                                {ORDER_STATUSES[order.state]}
                                            </span>
                                        </p>
                                    </td>
                                }

                                {(field.name === allFields[7].name && field.checked) &&
                                    <td key={field.name + order.id}>
                                        {order.channel?.[0]?.channel?.name || ''}
                                    </td>
                                }
                            </React.Fragment>
                        ))}
                    </tr>
                );
            })
        }
        </tbody>
    );
}

export default OrdersTableBody;