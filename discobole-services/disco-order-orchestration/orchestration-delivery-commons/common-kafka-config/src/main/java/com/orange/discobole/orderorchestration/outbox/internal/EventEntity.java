// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.outbox.internal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

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

    private Map<String, String> headers;

    private String traceparent;

    public static EventEntity create(String aggregateId, String aggregateType, String topicName, String payload, Map<String, String> headers) {
        return EventEntity.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .type(topicName)
                .payload(payload)
                .timestamp(Instant.now().toEpochMilli())
                .headers(headers)
                .traceparent(getTraceparent())
                .build();
    }

    public static String getTraceparent() {
        SpanContext context = Span.current().getSpanContext();
        return String.format("00-%s-%s-0%s", context.getTraceId(), context.getSpanId(), context.getTraceFlags().asByte());
    }
}