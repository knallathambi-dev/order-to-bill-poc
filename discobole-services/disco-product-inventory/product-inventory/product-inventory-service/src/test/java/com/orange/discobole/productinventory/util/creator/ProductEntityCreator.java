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
import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.model.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.AMOUNT;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ProductEntityCreator {

    public static final int STRING_SIZE = 10;

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductSpecificationEntityBuilder(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, Boolean isBundle, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct).productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType("ProductSpecification").build()).productRelationship(List.of());
    }

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductSpecificationEntityBuilder(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String atType, Boolean isBundle, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct)
                .productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType(atType).build());
    }

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductEntityBuilderWithProductSpecificationReliesFrom(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, Boolean isBundle, String reliesFromID, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct)
                .productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType("ProductSpecification").build())
                .productRelationship(List.of(ProductRelationshipEntity.builder()
                        .relationshipType(ProductRelationshipType.RELIESFROM.getValue())
                        .product(new ProductRefEntity(reliesFromID))
                        .build()));
    }

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductEntityBuilderWithProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, Boolean isBundle, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct).productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType("ProductSpecification").build()).productRelationship(List.of());
    }

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductEntityBuilderWithRelationShipRootProduct(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, Boolean isBundle, String productId, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct)
                .productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType("ProductSpecification").build())
                .productRelationship(List.of(ProductRelationshipEntity.builder()
                        .relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue())
                        .product(new ProductRefEntity(productId))
                        .build()));

    }

    @SneakyThrows
    public static ProductEntity.ProductEntityBuilder createProductEntityBuilderWithAtTypeProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String atType, Boolean isBundle, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct)
                .productSpecification(ProductSpecificationRefEntity.builder().id(productSpecificationId).name(randomAlphabetic(STRING_SIZE)).atType(atType).build())
                .productRelationship(List.of());
    }

    public static ProductEntity.ProductEntityBuilder commonProductBuilder(ProductStatusType status, String productOrderId, String orderItemId, Boolean isBundle, String typeProduct) {
        return ProductEntity.builder().status(status).atType(typeProduct).operationalStatus(ProductOperationalStatusType.CREATED).isBundle(isBundle)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder(randomAlphabetic(STRING_SIZE)).build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(List.of(getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()))
                .productPrice(List.of(ProductPriceEntity.builder().atType("ProductPrice").description(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).priceType("NRC").unitOfMeasure(randomAlphabetic(5)).applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceEntityBuilder().build())
                        .productPriceAlteration(List.of(PriceAlterationEntity.builder().applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build()).description(randomAlphabetic(STRING_SIZE))
                                .price(getPriceEntityBuilder().build()).validFor(getTimePeriodBuilder().build())
                                .unitOfMeasure(randomAlphabetic(STRING_SIZE)).priority(RandomUtils.nextInt()).recurringChargePeriod(MeasuredValue.builder().amount(11F).units(randomAlphabetic(STRING_SIZE)).build()).priceType(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).build()))
                        .billingAccount(getBillingBuilder().build())
                        .build()))
                .productTerm(List.of(ProductTermEntity.builder().description(randomAlphabetic(STRING_SIZE))
                        .duration(DurationEntity.builder().amount(RandomUtils.nextInt()).build()).name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder().build())
                        .build()))
                .billingAccount(getBillingBuilder().build());
    }

    private static PriceEntity.PriceEntityBuilder getPriceEntityBuilder() {
        return PriceEntity.builder().taxRate(RandomUtils.nextFloat()).taxIncludedAmount(getMoneyEntity()).dutyFreeAmount(getMoneyEntity());
    }

    private static BillingAccountRefEntity.BillingAccountRefEntityBuilder getBillingBuilder() {
        return BillingAccountRefEntity.builder().id(randomAlphabetic(10)).name(randomAlphabetic(10)).ratingType(randomAlphabetic(10)).name(randomAlphabetic(10));
    }

    private static TimePeriodEntity.TimePeriodEntityBuilder getTimePeriodBuilder() {
        return TimePeriodEntity.builder().endDateTime(OffsetDateTime.now()).startDateTime(OffsetDateTime.now());
    }

    public static RelatedProductOrderItemEntity.RelatedProductOrderItemEntityBuilder getRelatedProductOrderItemBuilder(String productOrderId, String orderItemId) {
        return RelatedProductOrderItemEntity.builder().productOrderId(productOrderId).orderItemId(orderItemId).atReferredType("productOrder").orderItemAction("add").role("change management order");
    }

    public static RelatedPartyEntity.RelatedPartyEntityBuilder getRelatedPartyBuilder(String name) {
        return RelatedPartyEntity.builder().id(randomAlphabetic(STRING_SIZE)).atType("PartyRef").name(name).atReferredType("Individual").role("Customer").partyName("Jean Doe").partyId("111");
    }

    private static AgreementItemRefEntity.AgreementItemRefEntityBuilder getAgreementItemRefBuilder() {
        return AgreementItemRefEntity.builder().id(randomAlphabetic(STRING_SIZE)).agreementItemId(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }

    private static RelatedPlaceRefOrValueEntity.RelatedPlaceRefOrValueEntityBuilder getRelatedPlaceRefOrValueBuilder() {
        return RelatedPlaceRefOrValueEntity.builder().id(randomAlphabetic(STRING_SIZE)).role(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE));
    }

    private static ServiceRefEntity.ServiceRefEntityBuilder getServiceRefBuilder() {
        return ServiceRefEntity.builder().id(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }

    private static ResourceRefEntity.ResourceRefEntityBuilder getResourceRefBuilder() {
        return ResourceRefEntity.builder().id(randomAlphabetic(STRING_SIZE)).atReferredType(randomAlphabetic(STRING_SIZE)).name(randomAlphabetic(STRING_SIZE)).atType(randomAlphabetic(STRING_SIZE));
    }

    @SneakyThrows
    private static ProductEntity.ProductEntityBuilder createProductBuilderWithProductOffering(ProductStatusType status, String productOrderId, String orderItemId, String atType, Boolean isBundle, String productOfferingId, String typeProduct) {
        return commonProductBuilder(status, productOrderId, orderItemId, isBundle, typeProduct)
                .productOffering(ProductOfferingRefEntity.builder().id(productOfferingId).name(randomAlphabetic(STRING_SIZE)).atType(atType).atReferredType(randomAlphabetic(STRING_SIZE)).build());
    }

    private static MoneyEntity getMoneyEntity() {
        return MoneyEntity.builder().unit("EUR").value(RandomUtils.nextFloat()).build();
    }

    public static ProductEntity.ProductEntityBuilder createProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String type, String relatedProductId, Boolean isBundle, String productOfferingId, String typeProduct) {
        return createProductBuilderWithProductOffering(status, productOrderId, orderItemId, type, isBundle, productOfferingId, typeProduct)
                .productRelationship(List.of(ProductRelationshipEntity.builder()
                        .relationshipType(ProductRelationshipType.SELLS.getValue())
                        .product(new ProductRefEntity(relatedProductId))
                        .build()));
    }

    public static ProductEntity.ProductEntityBuilder createProductEntityWithCharacteristics(List<CharacteristicEntity> characteristic, String typeProduct) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, typeProduct).productCharacteristic(characteristic);
    }

    public static ProductEntity createProductContract(ProductEntity bundledProduct) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, "Product")
                .id("contract")
                .productOffering(ProductOfferingRefEntity.builder().id("id").name(randomAlphabetic(STRING_SIZE)).atType(ProductOfferingTypeEnum.CONTRACT.getValue()).atReferredType(randomAlphabetic(STRING_SIZE)).build())
                .productRelationship(List.of(ProductRelationshipEntity.builder()
                        .relationshipType(ProductRelationshipType.BUNDLES.getValue())
                        .product(new ProductRefEntity(bundledProduct.getId()))
                        .build())).build();
    }

    public static ProductEntity createBundledProduct(ProductEntity... atomics) {
        ProductEntity.ProductEntityBuilder productEntityBuilder = commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, "Product")
                .id(ObjectId.get().toString())
                .productOffering(ProductOfferingRefEntity.builder().id("id").name(randomAlphabetic(STRING_SIZE)).atType(ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()).atReferredType(randomAlphabetic(STRING_SIZE)).build());
        List<ProductRelationshipEntity> productRelationshipEntities = new ArrayList<>();
        for (ProductEntity atomic : atomics) {
            productRelationshipEntities.add(
                    ProductRelationshipEntity.builder()
                            .relationshipType(ProductRelationshipType.BUNDLES.getValue())
                            .product(new ProductRefEntity(atomic.getId()))
                            .build()
            );
        }
        return productEntityBuilder.productRelationship(productRelationshipEntities).build();
    }

    public static ProductEntity.ProductEntityBuilder createAtomic(String id, ProductEntity productSpecification, ProductStatusType status, ProductOperationalStatusType operationalStatusType) {
        return commonProductBuilder(status, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, "Product")
                .id(id)
                .operationalStatus(operationalStatusType)
                .productOffering(ProductOfferingRefEntity.builder().id("id").name(randomAlphabetic(STRING_SIZE)).atType(ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING.getValue()).atReferredType(randomAlphabetic(STRING_SIZE)).build())
                .productRelationship(List.of(ProductRelationshipEntity.builder()
                        .relationshipType(ProductRelationshipType.SELLS.getValue())
                        .product(new ProductRefEntity(productSpecification.getId()))
                        .build()));
    }

    public static ProductEntity.ProductEntityBuilder createProductSpecification(String id, ProductStatusType status, ProductOperationalStatusType operationalStatusType, ProductEntity... reliesFroms) {
        ProductEntity.ProductEntityBuilder productSpec = commonProductBuilder(status, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, "Product")
                .id(id)
                .operationalStatus(operationalStatusType)
                .productSpecification(ProductSpecificationRefEntity.builder().id("id").name(randomAlphabetic(STRING_SIZE)).atType("ProductSpecificationRef").build());
        List<ProductRelationshipEntity> productRelationshipEntities = new ArrayList<>();
        for (ProductEntity ps : reliesFroms) {
            productRelationshipEntities.add(
                    ProductRelationshipEntity.builder()
                            .relationshipType(ProductRelationshipType.RELIESFROM.getValue())
                            .product(new ProductRefEntity(ps.getId()))
                            .build()
            );
        }
        return productSpec.productRelationship(productRelationshipEntities);
    }

    public static ProductEntity.ProductEntityBuilder createProductWithoutRelationShip(String productOfferingId, String productOfferingType) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), false, "Product")
                .productOffering(ProductOfferingRefEntity.builder().id(productOfferingId).name(randomAlphabetic(STRING_SIZE)).atType(productOfferingType).atReferredType(randomAlphabetic(STRING_SIZE)).build());

    }
}
