// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.interceptor;

import com.orange.discobole.orderorchestration.interceptor.mapper.ConsumerRecordTextMapperGetter;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerRecordInterceptor<K, V> implements RecordInterceptor<K, V> {

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> consumerRecord, Consumer<K, V> consumer) {
        log.info("Intercepting consumerRecord started...");
        try {
            Context extractedContext = GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
                    .extract(Context.current(), consumerRecord, new ConsumerRecordTextMapperGetter());
            try (var scope = extractedContext.makeCurrent()) {
                return consumerRecord;
            }
        } catch (Exception e) {
            log.error("Error occurred while intercepting consumerRecord", e);
            return consumerRecord;
        }
    }
}
