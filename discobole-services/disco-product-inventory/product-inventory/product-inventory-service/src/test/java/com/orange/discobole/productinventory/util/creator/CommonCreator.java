// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util.creator;

import com.orange.discobole.productinventory.dto.v1.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.orange.discobole.productinventory.constant.Constant.PRICE_TYPE_RECURRING;
import static com.orange.discobole.productinventory.constant.TestConstant.STRING_SIZE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class CommonCreator {


    @SneakyThrows
    public static ProductPrice.ProductPriceBuilder createBuilderProductPrice(String productOfferingPriceId) {
        return ProductPrice.builder().price(Price.builder().build()).productOfferingPrice(ProductOfferingPriceRef.builder().id(productOfferingPriceId).build()).priceType(randomAlphabetic(STRING_SIZE)).applicationDuration(Quantity.builder().amount(5F).units("day").build()).price(Price.builder().taxRate(RandomUtils.nextFloat()).build());

    }


    static TimePeriod.TimePeriodBuilder getTimePeriodBuilder() {
        return TimePeriod.builder().endDateTime(LocalDateTime.now().atOffset(ZoneOffset.UTC)).startDateTime(LocalDateTime.now().atOffset(ZoneOffset.UTC));
    }

    static Price.PriceBuilder getPriceBuilder() {
        return Price.builder().taxRate(RandomUtils.nextFloat()).percentage(RandomUtils.nextFloat()).taxIncludedAmount(getMoney()).dutyFreeAmount(getMoney());
    }

    static RelatedProductOrderItem.RelatedProductOrderItemBuilder getRelatedProductOrderItemBuilder(String productOrderId, String orderItemId) {
        return RelatedProductOrderItem.builder().productOrderId(productOrderId).atReferredType(randomAlphabetic(STRING_SIZE)).orderItemAction("add").orderItemId(orderItemId);
    }

    static RelatedPartyOrPartyRole.RelatedPartyOrPartyRoleBuilder getRelatedPartyBuilder() {
        return RelatedPartyOrPartyRole.builder()
                .atType("RelatedPartyRefOrPartyRoleRef")
                .role("Customer")
                .partyOrPartyRole(
                        PartyRoleRef
                                .builder()
                                .partyId(ObjectId.get().toString())
                                .partyName("Party Name")
                                .id(ObjectId.get().toString())
                                .name(ObjectId.get().toString())
                                .atType("PartyRef")
                                .atReferredType("Customer")
                                .build()
                );
    }

    static AgreementItemRef.AgreementItemRefBuilder getAgreementItemRefBuilder() {
        return AgreementItemRef.builder().id(randomAlphabetic(STRING_SIZE)).agreementItemId(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }

    static RelatedPlaceRefOrValue.RelatedPlaceRefOrValueBuilder getRelatedPlaceRefOrValueBuilder() {
        return RelatedPlaceRefOrValue.builder().id(randomAlphabetic(STRING_SIZE)).role(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE));
    }

    static ServiceRef.ServiceRefBuilder getServiceRefBuilder() {
        return ServiceRef.builder().id(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }

    public static ResourceRef.ResourceRefBuilder getResourceRefBuilder() {
        return ResourceRef.builder().id(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }


    static Money getMoney() {
        return Money.builder().unit("EUR").value(RandomUtils.nextFloat()).build();
    }
    public static ProductPrice.ProductPriceBuilder createBuilderProductPriceWithInvalidRecurringChargePeriodValidationBecauseOfNullValue(String productOfferingPriceId) {
        return ProductPrice.builder().price(Price.builder().build())
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(productOfferingPriceId).build()).priceType(PRICE_TYPE_RECURRING)
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build());

    }
    public static ProductPrice.ProductPriceBuilder createBuilderProductPriceWithRecurringChargePeriodValidationBecauseOfEmptyValue(String productOfferingPriceId) {
        return ProductPrice.builder().price(Price.builder().build())
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(productOfferingPriceId).build()).priceType(PRICE_TYPE_RECURRING)
                .recurringChargePeriod(new MeasuredValue())
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build());

    }
    public static ProductPrice.ProductPriceBuilder createBuilderProductPriceWithRecurringChargePeriodValidationBecauseOfBadValue(String productOfferingPriceId) {
        return ProductPrice.builder().price(Price.builder().build())
                .productOfferingPrice(ProductOfferingPriceRef.builder().id(productOfferingPriceId).build()).priceType(PRICE_TYPE_RECURRING)
                .recurringChargePeriod(MeasuredValue.builder().units("").build())
                .price(Price.builder().taxRate(RandomUtils.nextFloat()).build());

    }

}


