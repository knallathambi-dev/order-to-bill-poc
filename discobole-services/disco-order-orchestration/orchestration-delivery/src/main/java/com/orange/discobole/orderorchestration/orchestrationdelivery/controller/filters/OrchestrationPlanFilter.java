// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@Builder
public class OrchestrationPlanFilter {

    private String id;
    private String state;
    private OffsetDateTime receivedDate;
    private String relatedPartyId;
    private String relatedPartyRole;
    private String relatedPartyHref;
    private String relatedPartyName;
    private String relatedProductOrderId;
    private String orchestrationPlanNodesState;
    private String orchestrationPlanNodesRelatedServiceOrderId;
    private String orchestrationPlanNodesRelatedProductOrderItemId;
    private String orchestrationPlanNodesRelatedProductId;
    private String fields;
    private Integer offset;
    private Integer limit;
    private OffsetDateTime receivedDateGte;
    private OffsetDateTime receivedDateLts;
    private OffsetDateTime requestedDeliveryDateGte;
    private OffsetDateTime requestedDeliveryDateLts;
    private OffsetDateTime requestedDeliveryDate;

    private Boolean archived;

}
