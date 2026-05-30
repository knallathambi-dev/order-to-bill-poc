// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.status;

import com.orange.discobole.productinventory.dto.kafka.ProductStateChangeEvent;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.kafka.KafkaConsumerTest;
import com.orange.discobole.productinventory.kafka.consumer.impl.reports.status.ProductStatusChangeEventStatusReportConsumerImpl;
import com.orange.discobole.productinventory.model.ProductOfferEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.time.LocalDate;

import static com.orange.discobole.productinventory.util.MockUtil.getRandomOffer;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductStatusChangeEventStatusReportConsumerTest extends KafkaConsumerTest {

    @InjectMocks
    private ProductStatusChangeEventStatusReportConsumerImpl productStatusChangeEventStatusReportConsumer;


    @Test
    void whenInvokingConsumer_statusCountChanged() {
        // Arrange
        ProductOfferEntity randomOffer = getRandomOffer();
        Report reportByDateBefore = getReportByDate(LocalDate.now());
        Message<ProductStateChangeEvent> message = new GenericMessage<>(getProductStateChangeEvent(randomOffer, ProductStatusType.ACTIVE, ProductStatusType.CREATED));
        // Act
        productStatusChangeEventStatusReportConsumer.listen(message);

        Report reportByDateAfter = getReportByDate(LocalDate.now());
        assertNotNull(reportByDateAfter, "The report after consuming the message should not be null");
        statusChangeCountAssertions(reportByDateBefore, reportByDateAfter, ProductStatusType.ACTIVE, ProductStatusType.CREATED);

    }


}
