// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
@Document(collection = "events")
public class EventEntity {

    private String id;

    @Field("aggregatetype")
    private String aggregateType;

    @Field("aggregateid")
    private String aggregateId;

    private String payload;

    private Long timestamp;

    private String type;

    public static EventEntity create(String aggregateId, CDCEvent eventType, String payload) {
        return EventEntity.builder()
                .aggregateId(aggregateId)
                .aggregateType(eventType.getType().getValue())
                .type(eventType.getTopicName())
                .payload(payload)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
