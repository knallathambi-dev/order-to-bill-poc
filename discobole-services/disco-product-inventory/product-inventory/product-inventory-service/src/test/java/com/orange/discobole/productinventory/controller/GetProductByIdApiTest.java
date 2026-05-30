// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.service.impl.ProductServiceImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.creator.ProductEntityCreator;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.productinventory.constant.TestConstant.AMOUNT;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PHYSICAL_PRODUCT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.NOT_BE_EMPTY;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductBuilderWithRelation;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.createProductSpecificationEntityBuilder;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetProductByIdApiTest extends AbstractTest {
    @Test
    void givenValidProductIdOfProductSpec_whenGetProductByIdWithNoFieldsDefined_thenAllProductFieldsRetrieved() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                false, PRODUCT.getValue())
                .description(randomAlphabetic(10))
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .description(randomAlphabetic(10))
                                .name(randomAlphabetic(10))
                                .priceType("NRC")
                                .price(PriceEntity.builder()
                                        .taxRate(10F)
                                        .taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(10F).build())
                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(5F).build())
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder().priceType("recurringDiscount")
                                                .atType("TaxProductOfferingPriceAlteration")
                                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                                .description(randomAlphabetic(10))
                                                .price(PriceEntity.builder()
                                                        .taxRate(RandomUtils.nextFloat())
                                                        .taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(10F).build())
                                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(5F).build())
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        ProductServiceImpl.calculateTaxIncludedAmount(expectedProductEntity);

        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertProductDtoEqualsToProductEntity(actualProduct, expectedProductEntity, true);
    }

    @Test
    void givenValidProductIdOfProductRelationShip_whenGetProductByIdWithNoFieldsDefined_thenAllProductFieldsRetrievedRecursively() throws Exception {
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId).build());

        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductBuilderWithRelation(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10),
                "AtomicOffering", childProductId, false, randomAlphabetic(10), PRODUCT.getValue())
                .description(randomAlphabetic(10))
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .description(randomAlphabetic(10))
                                .name(randomAlphabetic(10))
                                .priceType("NRC")
                                .price(PriceEntity.builder()
                                        .taxRate(10F)
                                        .taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(0F).build())
                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(5F).build())
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder().priceType("recurringDiscount")
                                                .atType("discountPriceAlteration")
                                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                                .description(randomAlphabetic(10))
                                                .price(PriceEntity.builder()
                                                        .taxRate(RandomUtils.nextFloat())
                                                        .taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(9F).build())
                                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(5F).build())
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        ProductServiceImpl.calculateTaxIncludedAmount(expectedProductEntity);

        mongoTemplate.save(expectedProductEntity);


        expectedProductEntity.getProductRelationship().get(0).setProduct(new ProductRefEntity(childProduct.getId()));
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertProductDtoEqualsToProductEntity(actualProduct, expectedProductEntity, true);
    }

    @Test
    void givenValidProductIdOfProductRelationShip_whenGetProductByIdWithNoFieldsDefined_thenAllProductFieldsRetrievedFirsLevel() throws Exception {
        String childProductId = ObjectId.get().toString();
        ProductEntity childProduct = mongoTemplate.save(createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(childProductId).build());

        String expectedProductId = ObjectId.get().toString();
        ProductEntity expectedProductEntity = createProductBuilderWithRelation(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), "AtomicOffering", childProduct.getId(), false, randomAlphabetic(10), PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .description(randomAlphabetic(10))
                                .name(randomAlphabetic(10))
                                .priceType("NRC")
                                .price(PriceEntity.builder().taxRate(10F).taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(0F).build())
                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(0F).build())
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .priceType("recurringDiscount")
                                                .atType("discountPriceAlteration")
                                                .priority(1)
                                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                                .description(randomAlphabetic(10))
                                                .price(PriceEntity.builder().taxRate(10F).taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(10F).build())
                                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(5F).build())
                                                        .build())
                                                .build(),

                                        PriceAlterationEntity.builder()
                                                .priceType("recurringDiscount")
                                                .atType("TaxProductOfferingPriceAlteration")
                                                .priority(2)
                                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                                .description(randomAlphabetic(10))
                                                .price(PriceEntity.builder().taxRate(10F).taxIncludedAmount(MoneyEntity.builder().unit("EUR").value(8F).build())
                                                        .dutyFreeAmount(MoneyEntity.builder().unit("EUR").value(4F).build())
                                                        .build())
                                                .build()
                                ))
                                .build()

                ))
                .build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertProductDtoEqualsToProductEntity(actualProduct, expectedProductEntity, false);
    }

    @Test
    void givenValidProductId_whenGetProductByIdWithSpecificFields_thenAllProductFieldsRetrieved() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId),
                param(Map.of("fields", "description")));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        Assertions.assertThat(actualProduct.getId()).isEqualTo(expectedProductId);
        Assertions.assertThat(actualProduct.getHref()).isNotBlank();
        Assertions.assertThat(actualProduct.getProductOrderItem()).isEmpty();
        Assertions.assertThat(actualProduct.getProductSpecification()).isNull();
        Assertions.assertThat(actualProduct.getDescription()).isEqualTo(expectedProductEntity.getDescription());
    }

    @Test
    void givenValidProductId_whenGetProductByIdWithSpecificInnerFields_thenAllProductFieldsRetrieved() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId),
                param(Map.of("fields", "productOrderItem.productOrderId")));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        Assertions.assertThat(actualProduct.getId()).isEqualTo(expectedProductId);
        Assertions.assertThat(actualProduct.getHref()).isNotBlank();
        Assertions.assertThat(actualProduct.getProductOrderItem()).isNotEmpty();
        Assertions.assertThat(actualProduct.getProductOrderItem().get(0).getProductOrderId()).isEqualTo(expectedProductEntity.getProductOrderItem().get(0).getProductOrderId());
        Assertions.assertThat(actualProduct.getProductOrderItem().get(0).getOrderItemId()).isNull();
    }

    @Test
    void givenValidProductId_whenGetProductByIdWithInvalidFields_thenAllProductFieldsRetrievedErrorIgnored() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId),
                param(Map.of("fields", "description,test")));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        Assertions.assertThat(actualProduct.getId()).isEqualTo(expectedProductId);
        Assertions.assertThat(actualProduct.getHref()).isNotBlank();
        Assertions.assertThat(actualProduct.getProductOrderItem()).isEmpty();
        Assertions.assertThat(actualProduct.getProductSpecification()).isNull();
        Assertions.assertThat(actualProduct.getProductCharacteristic()).isNull();
        Assertions.assertThat(actualProduct.getDescription()).isEqualTo(expectedProductEntity.getDescription());
    }

    @Test
    void givenValidProductId_whenGetProductByIdWithFieldsNone_thenAllProductFieldsRetrieved() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId),
                param(Map.of("fields", "none"))
        );
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        Assertions.assertThat(actualProduct.getId()).isEqualTo(expectedProductId);
        Assertions.assertThat(actualProduct.getHref()).isNotBlank();
        Assertions.assertThat(actualProduct.getProductOrderItem()).isEmpty();
        Assertions.assertThat(actualProduct.getProductSpecification()).isNull();
    }

    @Test
    void givenValidProductId_whenGetProductByIdWithEmptyFields_thenProductWithFilterNotBeEmpty() {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId),
                param(Map.of("fields", "")));
        Error error = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
        assertThat(error).hasToString(new Error(INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "fields" + NOT_BE_EMPTY, HttpStatus.BAD_REQUEST).toString());

    }

    @Test
    void givenInValidProductId_whenGetProductById_thenNotFound() throws Exception {
        String expectedProductId = "exampleProductId";
        ProductEntity expectedProductEntity = ProductEntity.builder().id(expectedProductId).build();
        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, randomAlphabetic(10)));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void givenPhysicalProductId_whenGetProductByIdAndFields_thenAtTypeReturned() throws Exception {
        String exampleProductId = "exampleProductId";
        mongoTemplate.save(ProductEntityCreator.createProductSpecificationEntityBuilder(CREATED, randomAlphabetic(10), randomAlphabetic(10),
                randomAlphabetic(10), "ProductSpecificationRef", false, PHYSICAL_PRODUCT.getValue()).operationalStatus(ProductOperationalStatusType.CREATED).id(exampleProductId).build());

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, exampleProductId),
                param(Map.of("fields", "description,test")));

        resultActions.andExpect(status().isOk());

        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });

        Assertions.assertThat(actualProduct.getId()).isEqualTo(exampleProductId);
        Assertions.assertThat(actualProduct.getAtType()).isEqualTo(PHYSICAL_PRODUCT.getValue());
    }

    @Test
    void givenProductWithDiscountAndTax_whenGetProduct_thenTaxIncludedAmountIsCorrectlyCalculated() throws Exception {
        String expectedProductId = "productWithTax";
        float dutyFreeAmount = 100.0f;
        float discountAmount = 10.0f;
        float taxPercentage = 20.0f;
        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, PRODUCT.getValue()).description(randomAlphabetic(10)).id(expectedProductId)
                .productPrice(List.of(ProductPriceEntity.builder().atType("ProductPrice").price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("TND", dutyFreeAmount)).build())
                        .productPriceAlteration(List.of(
                                PriceAlterationEntity.builder()
                                        .priceType("recurringDiscount").atType("discountPriceAlteration").priority(1)
                                        .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("TND", discountAmount)).build())
                                        .build(),
                                PriceAlterationEntity.builder()
                                        .priceType("recurringDiscount").atType("TaxProductOfferingPriceAlteration").priority(2)
                                        .price(PriceEntity.builder().percentage(taxPercentage).build())
                                        .build()
                        ))
                        .build()
                )).build();

        mongoTemplate.save(expectedProductEntity);
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());
        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});

        float expectedTaxIncluded = 108.0F;

        assertThat(actualProduct.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue()).isEqualTo(expectedTaxIncluded);
    }

    @Test
    void givenProductWithDiscountValidForNow_whenGetProduct_thenDiscountIsApplied() throws Exception {
        String expectedProductId = "productWithValidDiscount";
        float dutyFreeAmount = 100.0f;
        float discountAmount = 10.0f;

        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                false, PRODUCT.getValue())
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder()
                                        .dutyFreeAmount(new MoneyEntity("TND", dutyFreeAmount))
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .priceType("recurringDiscount")
                                                .atType("discountPriceAlteration")
                                                .priority(1)
                                                .validFor(TimePeriodEntity.builder()
                                                        .startDateTime(now.minusDays(1))
                                                        .endDateTime(now.plusDays(1))
                                                        .build())
                                                .price(PriceEntity.builder()
                                                        .dutyFreeAmount(new MoneyEntity("TND", discountAmount))
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(
                mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId)
        );
        resultActions.andExpect(status().isOk());

        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(actualProduct.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue())
                .isEqualTo(90.0F);
    }

    @Test
    void givenProductWithExpiredDiscount_whenGetProduct_thenDiscountIsIgnored() throws Exception {
        String expectedProductId = "productWithExpiredDiscount";
        float dutyFreeAmount = 100.0f;
        float discountAmount = 10.0f;

        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                false, PRODUCT.getValue())
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("TND", dutyFreeAmount)).build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .priceType("recurringDiscount")
                                                .atType("discountPriceAlteration")
                                                .validFor(new TimePeriodEntity(now.minusDays(5), now.minusDays(1)))
                                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("TND", discountAmount)).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId));
        resultActions.andExpect(status().isOk());

        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});

        assertThat(actualProduct.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue())
                .isEqualTo(100.0F);
    }

    @Test
    void givenSamePriority_whenTaxAndDiscountAppliedAtSameLevel_thenTaxIncludedIs10_00() throws Exception {
        String id = "prio_same_level_9_90";
        ProductEntity entity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6), false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("EUR", 10F)).build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .atType("TaxProductOfferingPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().taxIncludedAmount(new MoneyEntity("EUR", 1F)).build())
                                                .build(),
                                        PriceAlterationEntity.builder()
                                                .atType("discountPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().percentage(10F).build())
                                                .build()
                                ))
                                .build()))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id));
        ra.andExpect(status().isOk());
        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();

        assertThat(actual).isCloseTo(10.000F, within(0.001F));
    }

    @Test
    void givenTaxPriority1AndDiscountPriority2_whenAppliedSequentially_thenTaxIncludedIs11_00() throws Exception {
        String id = "prio_tax1_disc2_10_80";
        ProductEntity entity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6), false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("EUR", 10F)).build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .atType("TaxProductOfferingPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().taxIncludedAmount(new MoneyEntity("EUR", 2F)).build())
                                                .build(),
                                        PriceAlterationEntity.builder()
                                                .atType("discountPriceAlteration").priority(2)
                                                .price(PriceEntity.builder().percentage(10F).build())
                                                .build()
                                ))
                                .build()))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id));
        ra.andExpect(status().isOk());
        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();

        assertThat(actual).isCloseTo(11.000F, within(0.001F));
    }

    @Test
    void givenDiscountPriority1AndTaxPriority2_whenAppliedSequentially_thenTaxIncludedIs11_00() throws Exception {
        String id = "prio_disc1_tax2_11_00";
        ProductEntity entity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6), false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("EUR", 10F)).build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .atType("discountPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().percentage(10F).build())
                                                .build(),
                                        PriceAlterationEntity.builder()
                                                .atType("TaxProductOfferingPriceAlteration").priority(2)
                                                .price(PriceEntity.builder().taxIncludedAmount(new MoneyEntity("EUR", 2F)).build())
                                                .build()
                                ))
                                .build()))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id));
        ra.andExpect(status().isOk());
        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();
        assertThat(actual).isCloseTo(11.000F, within(0.001F));
    }

    @Test
    void givenSamePriorityWithMultipleTaxesAndDiscounts_whenAppliedAtSameLevel_thenTaxIncludedIs10_90() throws Exception {
        String id = "prio_same_level_multi_10_80";
        ProductEntity entity = createProductSpecificationEntityBuilder(ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6), false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder().dutyFreeAmount(new MoneyEntity("EUR", 10F)).build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .atType("TaxProductOfferingPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().taxIncludedAmount(new MoneyEntity("EUR", 1F)).build())
                                                .build(),
                                        PriceAlterationEntity.builder()
                                                .atType("TaxProductOfferingPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().percentage(10F).build())
                                                .build(),
                                        PriceAlterationEntity.builder()
                                                .atType("discountPriceAlteration").priority(1)
                                                .price(PriceEntity.builder().percentage(10F).build())
                                                .build()
                                ))
                                .build()))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(mockMvc, GET, String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id));
        ra.andExpect(status().isOk());
        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();

        assertThat(actual).isCloseTo(10.900F, within(0.001F));
    }
    @Test
    void givenProductWithFutureDiscount_whenGetProduct_thenDiscountIsNotApplied() throws Exception {
        String expectedProductId = "productWithFutureDiscount";
        float dutyFreeAmount = 100.0f;
        float discountAmount = 10.0f;

        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                false, PRODUCT.getValue())
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder()
                                        .dutyFreeAmount(new MoneyEntity("TND", dutyFreeAmount))
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .priceType("recurringDiscount")
                                                .atType("discountPriceAlteration")
                                                .priority(1)
                                                .validFor(TimePeriodEntity.builder()
                                                        .startDateTime(now.plusDays(1)) // future date
                                                        .endDateTime(now.plusDays(5))
                                                        .build())
                                                .price(PriceEntity.builder()
                                                        .dutyFreeAmount(new MoneyEntity("TND", discountAmount))
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(
                mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId)
        );
        resultActions.andExpect(status().isOk());

        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(actualProduct.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue())
                .isEqualTo(100.0F); // discount ignored since startDateTime is in the future
    }
    @Test
    void givenProductWithPastTax_whenGetProduct_thenTaxIsStillApplied() throws Exception {
        String expectedProductId = "productWithPastTax";
        float dutyFreeAmount = 100.0f;
        float taxPercentage = 20.0f;

        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10),
                false, PRODUCT.getValue())
                .id(expectedProductId)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder()
                                        .dutyFreeAmount(new MoneyEntity("TND", dutyFreeAmount))
                                        .build())
                                .productPriceAlteration(List.of(
                                        PriceAlterationEntity.builder()
                                                .priceType("recurringTax")
                                                .atType("TaxProductOfferingPriceAlteration")
                                                .priority(1)
                                                .validFor(TimePeriodEntity.builder()
                                                        .startDateTime(now.minusDays(5))
                                                        .endDateTime(now.minusDays(1)) // past
                                                        .build())
                                                .price(PriceEntity.builder()
                                                        .percentage(taxPercentage)
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        mongoTemplate.save(expectedProductEntity);

        ResultActions resultActions = callRestfulEndpoint(
                mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, expectedProductId)
        );
        resultActions.andExpect(status().isOk());

        Product actualProduct = readJsonFromAPIResponse(resultActions, new TypeReference<>() {});
        assertThat(actualProduct.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue())
                .isEqualTo(120.0F); // tax applied even though validity is in the past
    }
    @Test
    void givenYenCurrencyWithDecimals_whenGetProduct_thenAmountIsRoundedToZeroDecimals() throws Exception {
        String id = "rounding_jpy_0_decimals";

        ProductEntity entity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6),
                false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder()
                                        .dutyFreeAmount(new MoneyEntity("JPY", 100.6F))
                                        .build())
                                .build()
                ))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(
                mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id)
        );
        ra.andExpect(status().isOk());

        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();

        assertThat(actual).isEqualTo(101F);
    }
    @Test
    void givenEuroCurrencyWithMoreThanTwoDecimals_whenGetProduct_thenAmountIsRoundedToTwoDecimals() throws Exception {
        String id = "rounding_eur_2_decimals";

        ProductEntity entity = createProductSpecificationEntityBuilder(
                ProductStatusType.ACTIVE, randomAlphabetic(6), randomAlphabetic(6), randomAlphabetic(6),
                false, PRODUCT.getValue())
                .id(id)
                .productPrice(List.of(
                        ProductPriceEntity.builder().atType("ProductPrice")
                                .price(PriceEntity.builder()
                                        .dutyFreeAmount(new MoneyEntity("EUR", 10.005F))
                                        .build())
                                .build()
                ))
                .build();

        mongoTemplate.save(entity);

        ResultActions ra = callRestfulEndpoint(
                mockMvc, GET,
                String.format(PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI, id)
        );
        ra.andExpect(status().isOk());

        Product p = readJsonFromAPIResponse(ra, new TypeReference<>() {});
        float actual = p.getProductPrice().get(0).getPrice().getTaxIncludedAmount().getValue();

        assertThat(actual).isCloseTo(10.01F, within(0.001F));
    }
}