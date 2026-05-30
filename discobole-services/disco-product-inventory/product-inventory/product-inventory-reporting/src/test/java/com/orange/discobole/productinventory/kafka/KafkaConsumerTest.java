// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka;

import com.orange.discobole.productinventory.dto.kafka.*;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.ProductOfferEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public abstract class KafkaConsumerTest extends AbstractTest {

    protected static @NotNull ProductCreateEvent getProductCreateEvent(ProductOfferEntity randomOffer, ProductStatusType statusType) {
        Product product = createProduct(randomOffer, statusType);

        ProductCreateEventPayload productEvent = new ProductCreateEventPayload();
        productEvent.setProduct(product);

        ProductCreateEvent productCreateEvent = new ProductCreateEvent();
        productCreateEvent.setEvent(productEvent);
        return productCreateEvent;
    }

    protected static @NotNull ProductDeleteEvent getProductDeleteEvent(ProductOfferEntity randomOffer, ProductStatusType statusType) {
        Product product = createProduct(randomOffer, statusType);

        ProductDeleteEventPayload productEvent = new ProductDeleteEventPayload();
        productEvent.setProduct(product);

        ProductDeleteEvent productDeleteEvent = new ProductDeleteEvent();
        productDeleteEvent.setEvent(productEvent);
        return productDeleteEvent;
    }

    protected static @NotNull ProductStateChangeEvent getProductStateChangeEvent(ProductOfferEntity randomOffer, ProductStatusType statusType, ProductStatusType oldStatus) {
        StateChangeProduct product = createStateChangeProduct(randomOffer, statusType, oldStatus);

        ProductStateChangeEventPayload productEvent = new ProductStateChangeEventPayload();
        productEvent.setProduct(product);

        ProductStateChangeEvent productStateChangeEvent = new ProductStateChangeEvent();
        productStateChangeEvent.setEvent(productEvent);
        return productStateChangeEvent;
    }

    private static @NotNull Product createProduct(ProductOfferEntity randomOffer, ProductStatusType statusType) {
        Product product = new Product();
        product.setStatus(statusType);
        product.setProductOffering(ProductOfferingRef.builder()
                .id(randomOffer.getId())
                .name(randomOffer.getProductOfferName())
                .atType(randomOffer.getProductOfferType())
                .build());
        return product;
    }

    private static @NotNull StateChangeProduct createStateChangeProduct(ProductOfferEntity randomOffer, ProductStatusType statusType, ProductStatusType oldStatus) {
        return StateChangeProduct.fromProduct(createProduct(randomOffer, statusType), oldStatus);
    }


    protected void statusChangeCountAssertions(Report reportByDateBefore, Report reportByDateAfter, ProductStatusType newStatus, ProductStatusType oldStatus) {
        Optional<ReportDetails> beforeOldStatusDetails = reportByDateBefore
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, oldStatus))
                .findAny();
        assertTrue(beforeOldStatusDetails.isPresent());

        Optional<ReportDetails> afterOldStatusDetails = reportByDateAfter
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, oldStatus))
                .findAny();
        assertTrue(afterOldStatusDetails.isPresent());

        Optional<ReportDetails> beforeNewStatusDetails = reportByDateBefore
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, newStatus))
                .findAny();
        assertTrue(beforeNewStatusDetails.isPresent());

        Optional<ReportDetails> afterNewStatusDetails = reportByDateAfter
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, newStatus))
                .findAny();
        assertTrue(afterNewStatusDetails.isPresent());

        assertTrue(beforeOldStatusDetails.get().getCount() > afterOldStatusDetails.get().getCount());
        assertTrue(beforeNewStatusDetails.get().getCount() < afterNewStatusDetails.get().getCount());
    }

    protected void statusChangeCountAssertions(Report reportByDateBefore, Report reportByDateAfter, ProductStatusType statusType, BiPredicate<Long, Long> countCondition) {
        Optional<ReportDetails> beforeOldStatusDetails = reportByDateBefore
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, statusType))
                .findAny();
        assertTrue(beforeOldStatusDetails.isPresent());

        Optional<ReportDetails> afterOldStatusDetails = reportByDateAfter
                .getDetails()
                .stream()
                .filter(reportDetails -> statusPredicate.test(reportDetails, statusType))
                .findAny();
        assertTrue(afterOldStatusDetails.isPresent());

        assertTrue(countCondition.test(beforeOldStatusDetails.get().getCount(), afterOldStatusDetails.get().getCount()));
    }

    protected void assertProductOfferUpsert(ProductOfferEntity offer) {
        // Capture the Query argument
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        // Capture the Update argument
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        // Verify that upsert was called and capture arguments
        verify(mongoTemplate).upsert(queryCaptor.capture(), updateCaptor.capture(), eq(ProductOfferEntity.class));

        Query capturedQuery = queryCaptor.getValue();
        Update capturedUpdate = updateCaptor.getValue();

        // Verify Query criteria
        Criteria expectedCriteria = Criteria.where(ProductOfferEntity.Fields.id).is(offer.getId());
        assertEquals(expectedCriteria.getCriteriaObject(), capturedQuery.getQueryObject());

        // Extract update fields
        Document updateDocument = capturedUpdate.getUpdateObject();

        // Extract fields safely
        Document setOnInsertFields = (Document) updateDocument.get("$setOnInsert");
        Document setFields = (Document) updateDocument.get("$set");

        // Verify fields are correctly updated
        assertEquals(offer.getId(), setOnInsertFields.getString(ProductOfferEntity.Fields.id));
        assertEquals(offer.getProductOfferName(), setFields.getString(ProductOfferEntity.Fields.productOfferName));
        assertEquals(offer.getProductOfferType(), setFields.getString(ProductOfferEntity.Fields.productOfferType));
    }

}
