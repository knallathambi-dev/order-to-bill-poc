// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.interceptor.mapper;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsumerRecordTextMapperGetter implements TextMapGetter<ConsumerRecord<?, ?>> {

    @Override
    public Iterable<String> keys(ConsumerRecord<?, ?> consumerRecord) {
        List<String> keys = new ArrayList<>();
        consumerRecord.headers().forEach(header -> keys.add(header.key()));
        return keys;
    }

    @Override
    public String get(ConsumerRecord<?, ?> consumerRecord, String key) {
        if (consumerRecord.headers().lastHeader(key) != null) {
            return new String(consumerRecord.headers().lastHeader(key).value());
        }

        return null;
    }
}
