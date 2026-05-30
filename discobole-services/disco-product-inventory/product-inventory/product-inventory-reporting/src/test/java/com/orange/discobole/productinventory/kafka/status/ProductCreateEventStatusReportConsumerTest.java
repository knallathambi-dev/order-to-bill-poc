// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.status;

import com.orange.discobole.productinventory.dto.kafka.ProductCreateEvent;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.kafka.KafkaConsumerTest;
import com.orange.discobole.productinventory.kafka.consumer.impl.reports.status.ProductCreateEventStatusReportConsumerImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.time.LocalDate;

import static com.orange.discobole.productinventory.util.MockUtil.getRandomOffer;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductCreateEventStatusReportConsumerTest extends KafkaConsumerTest {

    @InjectMocks
    private ProductCreateEventStatusReportConsumerImpl productCreateEventStatusReportConsumer;

    @Test
    void whenInvokingConsumer_statusCountIncreased() {
        // Arrange
        Report reportByDateBefore = getReportByDate(LocalDate.now());
        Message<ProductCreateEvent> message = new GenericMessage<>(getProductCreateEvent(getRandomOffer(), ProductStatusType.CREATED));

        // Act
        productCreateEventStatusReportConsumer.listen(message);

        Report reportByDateAfter = getReportByDate(LocalDate.now());
        assertNotNull(reportByDateAfter, "The report after consuming the message should not be null");

        statusChangeCountAssertions(reportByDateBefore, reportByDateAfter, ProductStatusType.CREATED, (count1, count2) -> count1 < count2);

    }

    @Test
    void whenInvokingConsumer_andDataBaseReturnsError_ExceptionThrown() {
        // Arrange
        Message<ProductCreateEvent> message = new GenericMessage<>(getProductCreateEvent(getRandomOffer(), ProductStatusType.CREATED));
        when(mongoTemplate.upsert(any(Query.class), any(Update.class), Mockito.any(Class.class)))
                .thenThrow(new RuntimeException("DB ERROR"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                productCreateEventStatusReportConsumer.listen(message));
    }


}
