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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "state_creation_date_index", def = "{'state': 1, 'creationDate': -1}")
@CompoundIndex(name = "channel_name_index", def = "{'channel.channel.name': 1}")
@CompoundIndex(name = "related_party_id_index", def = "{'relatedParty.partyOrPartyRole.id': 1}")
@CompoundIndex(name = "related_party_name_index", def = "{'relatedParty.partyOrPartyRole.name': 1}")
@Document(collection = "product_order")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductOrderEntity {
    @Id
    private String id;
    private String href;
    private Instant cancellationDate;
    private String cancellationReason;
    private String category;
    private Instant completionDate;
    private String description;
    private Instant expectedCompletionDate;
    private String notificationContact;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant creationDate;

    private String priority;
    private Instant requestedCompletionDate;
    private Instant requestedStartDate;
    private BillingAccountRefEntity billingAccount;
    private List<RelatedChannelEntity> channel;
    private List<OrderPriceEntity> orderTotalPrice;
    private List<PaymentRefEntity> payment;
    private List<ProductOrderItemEntity> productOrderItem;
    private List<RelatedPartyRefOrPartyRoleRefEntity> relatedParty;

    @Indexed
    private ProductOrderStateType state;

    private String atBaseType;
    private URI atSchemaLocation;
    private String atType;
}