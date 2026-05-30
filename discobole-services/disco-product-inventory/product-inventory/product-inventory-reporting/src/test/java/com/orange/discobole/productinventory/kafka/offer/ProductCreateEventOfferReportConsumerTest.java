// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.offer;

import com.orange.discobole.productinventory.dto.kafka.ProductCreateEvent;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.kafka.KafkaConsumerTest;
import com.orange.discobole.productinventory.kafka.consumer.impl.reports.offer.ProductCreateEventOfferReportConsumerImpl;
import com.orange.discobole.productinventory.model.ProductOfferEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.time.LocalDate;

import static com.orange.discobole.productinventory.util.MockUtil.getRandomOffer;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductCreateEventOfferReportConsumerTest extends KafkaConsumerTest {


    @InjectMocks
    private ProductCreateEventOfferReportConsumerImpl productCreateEventOfferReportConsumer;


    @Test
    void whenInvokingConsumer_statusCountIncreased() {
        // Arrange
        ProductOfferEntity randomOffer = getRandomOffer();
        Report reportByDateBefore = getReportByDateAndOffer(LocalDate.now(), randomOffer.getId());


        Message<ProductCreateEvent> message = new GenericMessage<>(getProductCreateEvent(randomOffer, ProductStatusType.CREATED));

        // Act
        productCreateEventOfferReportConsumer.listen(message);

        Report reportByDateAfter = getReportByDateAndOffer(LocalDate.now(), randomOffer.getId());
        assertNotNull(reportByDateAfter, "The report after consuming the message should not be null");
        statusChangeCountAssertions(reportByDateBefore, reportByDateAfter, ProductStatusType.CREATED, (count1, count2) -> count1 < count2);
        assertProductOfferUpsert(randomOffer);
    }


}
