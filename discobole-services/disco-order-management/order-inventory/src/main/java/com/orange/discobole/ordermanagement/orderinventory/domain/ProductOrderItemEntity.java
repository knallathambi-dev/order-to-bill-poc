// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import java.net.URI;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductOrderItemEntity {
    private String id;
    private Integer quantity;
    private ItemActionType action;
    private BillingAccountRefEntity billingAccount;
    private List<OrderPriceEntity> itemPrice;
    private List<OrderTermEntity> itemTerm;
    private List<PaymentRefEntity> payment;
    private ProductRefOrValueEntity product;
    private ProductOfferingRefEntity productOffering;
    private List<OrderItemRelationshipEntity> productOrderItemRelationship;
    private ProductOrderItemStateType state;
    private String atBaseType;
    private URI atSchemaLocation;
    private String atType;
    private AppointmentRefEntity appointment;
    private Boolean isInstallable;
}