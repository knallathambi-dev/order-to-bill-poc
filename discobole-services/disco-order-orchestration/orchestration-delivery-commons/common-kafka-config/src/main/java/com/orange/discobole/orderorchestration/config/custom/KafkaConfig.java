// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.config.custom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.converter.MessagingMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfig {

    private static final String HEADERS_VAR_NAME = "headers";

    private final ObjectMapper objectMapper;

    @Bean
    public MessagingMessageConverter converter() {
        MessagingMessageConverter converter = new MessagingMessageConverter();
        converter.setHeaderMapper(customKafkaHeaderMapper());
        return converter;
    }

    @Bean
    public KafkaHeaderMapper customKafkaHeaderMapper() {
        return new DefaultKafkaHeaderMapper() {

            @Override
            public void toHeaders(Headers source, Map<String, Object> headers) {
                super.toHeaders(source, headers);
                source.forEach(header -> {
                    if (HEADERS_VAR_NAME.equals(header.key())) {
                        try {
                            HashMap<String, String> map = objectMapper.readValue(header.value(), new TypeReference<>() {
                            });
                            headers.put(header.key(), map);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                });
            }
        };
    }
}
