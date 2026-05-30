// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service;

import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants;
import com.orange.discobole.productinventory.dto.v1.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductDateHelperTest {

    private static final float AMOUNT = 3f;
    private static final String MODIFY = "modify";
    private static final String MONTH = "month";
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final OffsetDateTime PRODUCT_START_DATE = OffsetDateTime.now();

    @InjectMocks
    private ProductDateHelper productDateHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForExclusionCases")
    void testShouldUpdatePriceDateWhenUpdateProductPriceDate(String productOrderId, ProductStatusType statusType,
                                                             List<ProductPrice> productPrices, OffsetDateTime startDate) {
        //given
        Product product = createProduct();
        product.setStatus(statusType);
        product.setProductPrice(productPrices);
        product.setStartDate(startDate);

        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, productOrderId);
        //then
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @ParameterizedTest
    @MethodSource("provideProductTermTestDataForExclusionCases")
    void testShouldNotUpdateProductTermWhenUpdateProductTerm(String productOrderId, ProductStatusType statusType,
                                                             List<ProductTerm> productTerms, OffsetDateTime startDate) {
        //given
        Product product = createProduct();
        product.setStatus(statusType);
        product.setProductTerm(productTerms);
        product.setStartDate(startDate);

        //when
        List<PatchDTO> result = productDateHelper.updateProductTermDate(product, productOrderId);
        //then
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @ParameterizedTest
    @MethodSource("provideProductTermTestDataForValidCases")
    void testShouldUpdateProductTermWhenUpdateProductTerm(String productOrderId) {
        //given
        ProductTerm productTermWithNoValidFor = ProductTerm.builder()
                .duration(Duration.builder()
                        .units("Month")
                        .amount(12)
                        .build())
                .build();
        Product product = createProduct();
        product.setStatus(ProductStatusType.ACTIVE);
        product.setProductTerm(List.of(productTermWithNoValidFor));
        product.setStartDate(PRODUCT_START_DATE);

        //when
        List<PatchDTO> result = productDateHelper.updateProductTermDate(product, productOrderId);
        //then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());

        TimePeriod validFor = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(PRODUCT_START_DATE.plusYears(1))
                .build();
        Assertions.assertEquals(validFor, result.get(0).getValue());
    }


    @ParameterizedTest
    @MethodSource("provideTestDataForOnlyValidForStartDateCases")
    void testSetValidForStartDateWhenUpdateProductPriceDate(List<ProductPrice> productPrices, String path) {
        //given
        Product product = createProduct();
        product.setProductPrice(productPrices);
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(path, result.get(0).getPath());

        TimePeriod validFor = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .build();
        Assertions.assertEquals(validFor, result.get(0).getValue());
    }

    @Test
    void testUpdateProductPriceDateWithInvalidApplicationDurationUnit() {
        //given
        Product product = createProduct();

        Quantity applicationDuration = Quantity.builder()
                .amount(AMOUNT)
                .units("invalidUnit")
                .build();
        MeasuredValue recurringChargePeriod = MeasuredValue.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        ProductPrice productPrice = ProductPrice.builder()
                .applicationDuration(applicationDuration)
                .recurringChargePeriod(recurringChargePeriod)
                .build();

        product.setProductPrice(Collections.singletonList(productPrice));
        //when
        assertThrows(IllegalArgumentException.class, () -> productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1));
    }

    @Test
    void testUpdateProductPriceDate() {
        //given
        Product product = createProduct();
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then
        OffsetDateTime endDate = PRODUCT_START_DATE.plusMonths(3);

        TimePeriod validFor = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(endDate)
                .build();

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI, result.get(0).getPath());
        Assertions.assertEquals(validFor, result.get(0).getValue());


        Assertions.assertEquals(PatchOperationType.ADD, result.get(1).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + PRODUCT_PRICE_ALTERATION_URI + "0" + VALID_FOR_URI, result.get(1).getPath());
        Assertions.assertEquals(validFor, result.get(1).getValue());
    }

    @Test
    void testUpdateProductPriceDateForInstallment() {
        //given
        Product product = createProductForInstallment();
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then
        OffsetDateTime endDate = PRODUCT_START_DATE.plusMonths(3);

        TimePeriod validFor = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(endDate)
                .build();

        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI, result.get(0).getPath());
        Assertions.assertEquals(validFor, result.get(0).getValue());

    }

    @Test
    void testUpdateProductPriceDateForAlterationWithApplicationOffsetHavingRecurringDiscountAndApplicationDuration() {
        //given
        Quantity applicationDuration = Quantity.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();
        Product product = createProductWithApplicationOffset(applicationDuration, FollowUpConstants.RECURRING_DISCOUNT);
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then

        TimePeriod validForWithNullEndDate = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(null)
                .build();


        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI, result.get(0).getPath());
        Assertions.assertEquals(validForWithNullEndDate, result.get(0).getValue());
    }

    @Test
    void testUpdateProductPriceDateForAlterationWithApplicationOffsetHavingRecurringDiscountAndNoApplicationDuration() {
        //given
        Product product = createProductWithApplicationOffset(null, FollowUpConstants.RECURRING_DISCOUNT);
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then

        TimePeriod validForWithNullEndDate = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(null)
                .build();

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI, result.get(0).getPath());
        Assertions.assertEquals(validForWithNullEndDate, result.get(0).getValue());
    }

    @Test
    void testUpdateProductPriceDateForAlterationWithApplicationOffsetHavingNonRecurringDiscount() {
        //given
        Product product = createProductWithApplicationOffset(null, FollowUpConstants.NON_RECURRING_DISCOUNT);
        //when
        List<PatchDTO> result = productDateHelper.updateProductPriceDate(product, PRODUCT_ORDER_ID_1);
        //then

        TimePeriod validForWithNullEndDate = TimePeriod.builder()
                .startDateTime(PRODUCT_START_DATE)
                .endDateTime(null)
                .build();

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(PatchOperationType.ADD, result.get(0).getOp());
        Assertions.assertEquals(PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI, result.get(0).getPath());
        Assertions.assertEquals(validForWithNullEndDate, result.get(0).getValue());
    }

    private static Product createProduct() {
        RelatedProductOrderItem productOrderItem = RelatedProductOrderItem.builder()
                .orderItemAction(ADD)
                .productOrderId(PRODUCT_ORDER_ID_1)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .build();

        RelatedProductOrderItem productOrderItem2 = RelatedProductOrderItem.builder()
                .orderItemAction(MODIFY)
                .productOrderId(PRODUCT_ORDER_ID_2)
                .orderItemId(PRODUCT_ITEM_ID_2)
                .build();

        RelatedProductOrderItem productOrderItem3 = RelatedProductOrderItem.builder()
                .orderItemAction(MIGRATE)
                .productOrderId(PRODUCT_ORDER_ID_3)
                .orderItemId(PRODUCT_ITEM_ID_3)
                .build();
        List<RelatedProductOrderItem> relatedProductOrderItems = List.of(productOrderItem, productOrderItem2, productOrderItem3);

        Quantity applicationDuration = Quantity.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        MeasuredValue measuredValue = MeasuredValue.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        PriceAlteration priceAlteration = PriceAlteration.builder()
                .applicationDuration(applicationDuration)
                .recurringChargePeriod(measuredValue)
                .build();

        ProductPrice productPrice = ProductPrice.builder()
                .applicationDuration(applicationDuration)
                .recurringChargePeriod(measuredValue)
                .productPriceAlteration(Collections.singletonList(priceAlteration))
                .build();


        return Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItems)
                .status(ProductStatusType.ACTIVE)
                .productPrice(Collections.singletonList(productPrice))
                .startDate(PRODUCT_START_DATE)
                .build();
    }

    private Product createProductForInstallment() {
        RelatedProductOrderItem productOrderItem = RelatedProductOrderItem.builder()
                .orderItemAction(ADD)
                .productOrderId(PRODUCT_ORDER_ID_1)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .build();

        List<RelatedProductOrderItem> relatedProductOrderItems = List.of(productOrderItem);

        Quantity applicationDuration = Quantity.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        InstallmentCharge installmentCharge = InstallmentCharge.builder()
                .applicationDuration(applicationDuration)
                .atType("InstallmentCharge")
                .build();


        return Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItems)
                .status(ProductStatusType.ACTIVE)
                .productPrice(Collections.singletonList(installmentCharge))
                .startDate(PRODUCT_START_DATE)
                .build();
    }

    private Product createProductWithApplicationOffset(Quantity applicationDuration, String priceType) {
        RelatedProductOrderItem productOrderItem = RelatedProductOrderItem.builder()
                .orderItemAction(ADD)
                .productOrderId(PRODUCT_ORDER_ID_1)
                .orderItemId(PRODUCT_ITEM_ID_1)
                .build();

        List<RelatedProductOrderItem> relatedProductOrderItems = List.of(productOrderItem);


        ProductPrice productPrice = ProductPrice.builder()
                .recurringChargePeriod(MeasuredValue.builder()
                        .amount(2f)
                        .units("month")
                        .build())
                .productPriceAlteration(List.of(PriceAlteration.builder()
                        .applicationDuration(applicationDuration)
                        .applicationOffset(3)
                        .priceType(priceType)
                        .atType("discountPriceAlteration")
                        .build()))
                .priceType("recurringCharge")
                .atType("ProductPrice")
                .build();


        return Product.builder()
                .id(PRODUCT_ID_1)
                .productOrderItem(relatedProductOrderItems)
                .status(ProductStatusType.ACTIVE)
                .productPrice(Collections.singletonList(productPrice))
                .startDate(PRODUCT_START_DATE)
                .build();
    }

    static Stream<Arguments> provideTestDataForExclusionCases() {
        ProductPrice noRecurrringProductPrice = ProductPrice.builder().build();

        PriceAlteration priceAlteration = PriceAlteration.builder()
                .build();

        ProductPrice noRecurringProductAlterationPrice = ProductPrice.builder()
                .productPriceAlteration(Collections.singletonList(priceAlteration))
                .build();

        return Stream.of(
                Arguments.of(PRODUCT_ORDER_ID_2, ProductStatusType.ACTIVE, null, PRODUCT_START_DATE), //no Add action for this order
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.TERMINATED, null, PRODUCT_START_DATE), //no Active Or Sold status
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, null, PRODUCT_START_DATE), //No Product Price
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, Collections.singletonList(noRecurrringProductPrice), PRODUCT_START_DATE), //no Recurring ProductPrice
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, Collections.singletonList(noRecurringProductAlterationPrice), PRODUCT_START_DATE), //no recurring charge for price alteration
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, Collections.singletonList(noRecurringProductAlterationPrice), null) //product start date null

        );
    }

    static Stream<Arguments> provideProductTermTestDataForExclusionCases() {

        ProductTerm productTermWithValidFor = ProductTerm.builder()
                .duration(Duration.builder()
                        .units("Month")
                        .amount(12)
                        .build())
                .validFor(TimePeriod.builder()
                        .startDateTime(OffsetDateTime.now())
                        .build())
                .build();

        return Stream.of(
                Arguments.of(PRODUCT_ORDER_ID_2, ProductStatusType.ACTIVE, null, PRODUCT_START_DATE), //no Add action for this order
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.TERMINATED, null, PRODUCT_START_DATE), //no Active Or Sold status
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, null, PRODUCT_START_DATE), //No Product Term
                Arguments.of(PRODUCT_ORDER_ID_1, ProductStatusType.ACTIVE, Collections.singletonList(productTermWithValidFor), null) //No product start date

        );
    }

    static Stream<Arguments> provideProductTermTestDataForValidCases() {

        return Stream.of(
                Arguments.of(PRODUCT_ORDER_ID_1), // Add action for this order
                Arguments.of(PRODUCT_ORDER_ID_3) //Migrate action for this order

        );
    }

    static Stream<Arguments> provideTestDataForOnlyValidForStartDateCases() {

        Quantity applicationDuration = Quantity.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        MeasuredValue measuredValue = MeasuredValue.builder()
                .amount(AMOUNT)
                .units(MONTH)
                .build();

        ProductPrice productPriceWithoutApplicationDuration = ProductPrice.builder()
                .recurringChargePeriod(measuredValue)
                .build();

        ProductPrice productPriceWithApplicationDurationNullAmount = ProductPrice.builder()
                .applicationDuration(applicationDuration.amount(null))
                .recurringChargePeriod(measuredValue)
                .build();

        ProductPrice productPriceWithApplicationDurationNullUnit = ProductPrice.builder()
                .applicationDuration(applicationDuration.units(null))
                .recurringChargePeriod(measuredValue)
                .build();


        PriceAlteration priceAlteration = PriceAlteration.builder()
                .recurringChargePeriod(measuredValue)
                .build();

        ProductPrice productPriceAlterationWithoutApplicationDuration = ProductPrice.builder()
                .productPriceAlteration(Collections.singletonList(priceAlteration))
                .build();


        return Stream.of(
                Arguments.of(Collections.singletonList(productPriceWithoutApplicationDuration), PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI), //product price without applicationDuration
                Arguments.of(Collections.singletonList(productPriceWithApplicationDurationNullAmount), PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI), //product price with applicationDuration amount null
                Arguments.of(Collections.singletonList(productPriceWithApplicationDurationNullUnit), PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + VALID_FOR_URI), //product price without applicationDuration units null
                Arguments.of(Collections.singletonList(productPriceAlterationWithoutApplicationDuration), PRODUCT_INVENTORY_URI + PRODUCT_ID_1 + PRODUCT_PRICE_URI + "0" + PRODUCT_PRICE_ALTERATION_URI + "0" + VALID_FOR_URI) //product price alteration without applicationDuration

        );
    }
}
