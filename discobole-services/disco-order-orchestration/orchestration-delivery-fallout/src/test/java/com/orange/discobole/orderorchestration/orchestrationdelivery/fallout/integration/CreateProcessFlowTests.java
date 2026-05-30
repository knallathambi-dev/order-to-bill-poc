// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.integration;

import base.AbstractIntegrationUtil;
import base.BaseAbstractionIntegrationTest;
import base.debezium.EnableDebeziumIntegration;
import base.testutil.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.ErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableDebeziumIntegration
public class CreateProcessFlowTests extends BaseAbstractionIntegrationTest {


    @Value("disco.order-orchestration-fallout.falloutIncidentStateChange-event")
    private String outputTopicDestination;

    @Test
    void givenProcessFlowFile_whenCreateProcessFlowFile_thenProcessFlowFileCreated() throws Exception {
        ProcessFlowCreate processFlowCreate = JsonUtil.readObjectFromResource("/integration/create/processFlowCreateFallout.json", new TypeReference<>() {
        });

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/processManagement/v1/processFlow")
                        .content(JsonUtil.toJsonStringFromObject(processFlowCreate))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        FalloutIncident actualFallout = mongoTemplate.findAll(FalloutIncident.class).get(0);
        assertThat(actualFallout.getId()).isNotNull();
        assertThat(actualFallout.getErrorMessage()).isNotNull();
        ObjectCharacteristic characteristic = (ObjectCharacteristic) processFlowCreate.getCharacteristic().stream().filter(c -> c.getName().equals("eventError")).findFirst().get();
        assertThat(actualFallout.getErrorMessage()).isEqualTo(objectMapper.convertValue(characteristic.getValue(), ErrorMessage.class));
        assertThat(actualFallout.getRelatedEntity().size()).isEqualTo(3);
        ObjectCharacteristic falloutCharacteristic = (ObjectCharacteristic) processFlowCreate.getCharacteristic().stream().filter(c -> c.getValueType().equals("Fallout")).findFirst().get();
        FalloutIncident expectedFallout = objectMapper.convertValue(falloutCharacteristic.getValue(), FalloutIncident.class);
        assertThat(actualFallout.getRelatedEntity()).isEqualTo(expectedFallout.getRelatedEntity());
        assertThat(actualFallout.getRelatedParty()).isEqualTo(expectedFallout.getRelatedParty());
        KafkaConsumer<String, FalloutIncidentStateChangeEvent> falloutIncidentStateChangeEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(outputTopicDestination, "test-output-group-1", FalloutIncidentStateChangeEvent.class);
        ConsumerRecords<String, FalloutIncidentStateChangeEvent> records = falloutIncidentStateChangeEventKafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records).isNotEmpty();
        records.forEach(record -> {
            assertThat(record.value().getEvent()).usingRecursiveComparison()
                    .isInstanceOf(FalloutIncident.class);
        });
    }
}
