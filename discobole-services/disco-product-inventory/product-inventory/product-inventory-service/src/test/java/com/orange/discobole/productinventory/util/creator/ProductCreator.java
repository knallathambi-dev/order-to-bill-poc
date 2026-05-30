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
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;

import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.HASPARENT;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.SELLS;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.*;
import static com.orange.discobole.productinventory.util.creator.CommonCreator.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ProductCreator {



    @SneakyThrows
    public static Product.ProductBuilder createProductBuilderWithProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId) {
        return commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                .productSpecification(ProductSpecificationRef.builder()
                        .id(productSpecificationId)
                        .name("Mobile Handset")
                        .atType("ProductSpecificationRef")
                        .build()
                );
    }

    @SneakyThrows
    public static Product.ProductBuilder createProductBuilderWithBundleAndAtomicProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String bundleProductOfferingId, String atomicProductOfferingId) {
        return commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(bundleProductOfferingId).atType(ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue())
                        .product(
                                commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                                        .productOffering(
                                                ProductOfferingRef
                                                        .builder()
                                                        .id(atomicProductOfferingId)
                                                        .atType(ATOMIC_PRODUCT_OFFERING.getValue()).build())
                                        .name("Mobile Handset")
                                        .productSpecification(ProductSpecificationRef.builder()
                                                .id(productSpecificationId)
                                                .name("Mobile Handset")
                                                .atType("ProductSpecificationRef")
                                                .build()
                                        )
                                        .build())
                        .build()));
    }

    @SneakyThrows
    public static Product.ProductBuilder createProductBuilderWithBundleAndAtomicProductOfferingWithSellsSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String bundleProductOfferingId, String atomicProductOfferingId) {
        return commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(bundleProductOfferingId).atType(ProductOfferingTypeEnum.BUNDLE_PRODUCT_OFFERING.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(ProductRelationshipType.BUNDLES.getValue())
                        .product(
                                commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                                        .productOffering(
                                                ProductOfferingRef
                                                        .builder()
                                                        .id(atomicProductOfferingId)
                                                        .atType(ATOMIC_PRODUCT_OFFERING.getValue()).build())
                                        .name("Mobile Handset")
                                        .productRelationship(List.of(ProductRelationship.builder().relationshipType(ProductRelationshipType.SELLS.getValue())
                                                .product(
                                                        commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                                                                .productSpecification(ProductSpecificationRef.builder()
                                                                        .id(productSpecificationId)
                                                                        .name("Mobile Handset")
                                                                        .atType("ProductSpecificationRef")
                                                                        .build()
                                                                )
                                                                .build())
                                                .build()))

                                        .build())
                        .build()));
    }


    @SneakyThrows
    public static Product.ProductBuilder createProductBuilderWithProductSpecificationWithHasParent(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String idParent) {
        return commonProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED)
                .productSpecification(ProductSpecificationRef.builder()
                        .id(productSpecificationId)
                        .name(randomAlphabetic(STRING_SIZE))
                        .atType("ProductSpecificationRef")
                        .build()
                )
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(HASPARENT.getValue())
                                .product(ProductRef.builder().id(idParent).atType(ProductTypeEnum.PRODUCT_REF.getValue()).build())
                                .build()
                        )
                );
    }

    public static Product.ProductBuilder commonProductBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return Product.builder()
                .status(status)
                .operationalStatus(operationalStatus)
                .atType(ProductTypeEnum.PRODUCT.getValue())
                .isBundle(false)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()
                        )
                )
                .productPrice(List.of(ProductPrice.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .name(randomAlphabetic(STRING_SIZE))
                        .priceType("NRC")
                        .applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceBuilder()
                                .build())
                        //todo must use PriceAlteration from dto package
                        .productPriceAlteration(List.of(PriceAlteration.builder()
                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder()
                                        .taxRate(RandomUtils.nextFloat())
                                        .taxIncludedAmount(getMoney())
                                        .dutyFreeAmount(getMoney())
                                        .build())
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .validFor(getTimePeriodBuilder()
                                        .build())
                                .build()))
                        .build()))
                .productTerm(List.of(ProductTerm.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .duration(Duration.builder()
                                .amount(RandomUtils.nextInt()).build())
                        .name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder()
                                .build())
                        .build()));
    }

    public static Offer.ProductBuilder commonProductOfferBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return Offer.builder()
                .status(status)
                .operationalStatus(operationalStatus)
                .atType(ProductTypeEnum.PRODUCT.getValue())
                .isBundle(false)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()
                        )
                )
                .productPrice(List.of(ProductPrice.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .name(randomAlphabetic(STRING_SIZE))
                        .priceType("NRC")
                        .applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceBuilder()
                                .build())
                        //todo must use PriceAlteration from dto package
                        .productPriceAlteration(List.of(PriceAlteration.builder()
                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder()
                                        .taxRate(RandomUtils.nextFloat())
                                        .taxIncludedAmount(getMoney())
                                        .dutyFreeAmount(getMoney())
                                        .build())
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .build()))
                        .build()))
                .productTerm(List.of(ProductTerm.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .duration(Duration.builder()
                                .amount(RandomUtils.nextInt()).build())
                        .name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder()
                                .build())
                        .build()));
    }

    public static SimCard.ProductBuilder commonProductSimCardBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return SimCard.builder()
                .status(status)
                .operationalStatus(operationalStatus)
                .atType(ProductTypeEnum.PRODUCT.getValue())
                .isBundle(false)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()
                        )
                )
                .productPrice(List.of(ProductPrice.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .name(randomAlphabetic(STRING_SIZE))
                        .priceType("NRC")
                        .applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceBuilder()
                                .build())
                        //todo must use PriceAlteration from dto package
                        .productPriceAlteration(List.of(PriceAlteration.builder()
                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder()
                                        .taxRate(RandomUtils.nextFloat())
                                        .taxIncludedAmount(getMoney())
                                        .dutyFreeAmount(getMoney())
                                        .build())
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .validFor(getTimePeriodBuilder()
                                        .build())
                                .build()))
                        .build()))
                .productTerm(List.of(ProductTerm.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .duration(Duration.builder()
                                .amount(RandomUtils.nextInt()).build())
                        .name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder()
                                .build())
                        .build()));
    }

    public static MobileLine.ProductBuilder commonProductMobileLineBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return MobileLine.builder()
                .status(status)
                .operationalStatus(operationalStatus)
                .atType(ProductTypeEnum.PRODUCT.getValue())
                .isBundle(false)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()
                        )
                )
                .productPrice(List.of(ProductPrice.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .name(randomAlphabetic(STRING_SIZE))
                        .priceType("NRC")
                        .applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceBuilder()
                                .build())
                        //todo must use PriceAlteration from dto package
                        .productPriceAlteration(List.of(PriceAlteration.builder()
                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder()
                                        .taxRate(RandomUtils.nextFloat())
                                        .taxIncludedAmount(getMoney())
                                        .dutyFreeAmount(getMoney())
                                        .build())
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .validFor(getTimePeriodBuilder()
                                        .build())
                                .build()))
                        .build()))
                .productTerm(List.of(ProductTerm.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .duration(Duration.builder()
                                .amount(RandomUtils.nextInt()).build())
                        .name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder()
                                .build())
                        .build()));
    }

    public static Service.ProductBuilder commonProductServiceBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return Service.builder()
                .status(status)
                .operationalStatus(operationalStatus)
                .atType(ProductTypeEnum.PRODUCT.getValue())
                .isBundle(false)
                .realizingService(List.of(getServiceRefBuilder().build()))
                .realizingResource(List.of(getResourceRefBuilder().build()))
                .place(List.of(getRelatedPlaceRefOrValueBuilder().build()))
                .agreement(List.of(getAgreementItemRefBuilder().build()))
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .description(randomAlphabetic(STRING_SIZE))
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(productOrderId, orderItemId).build()
                        )
                )
                .productPrice(List.of(ProductPrice.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .name(randomAlphabetic(STRING_SIZE))
                        .priceType("NRC")
                        .applicationDuration(Quantity.builder().amount(5F).units("day").build())
                        .price(getPriceBuilder()
                                .build())
                        //todo must use PriceAlteration from dto package
                        .productPriceAlteration(List.of(PriceAlteration.builder()
                                .applicationDuration(Quantity.builder().amount(AMOUNT).units("day").build())
                                .description(randomAlphabetic(STRING_SIZE))
                                .price(Price.builder()
                                        .taxRate(RandomUtils.nextFloat())
                                        .taxIncludedAmount(getMoney())
                                        .dutyFreeAmount(getMoney())
                                        .build())
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .validFor(getTimePeriodBuilder()
                                        .build())
                                .build()))
                        .build()))
                .productTerm(List.of(ProductTerm.builder()
                        .description(randomAlphabetic(STRING_SIZE))
                        .duration(Duration.builder()
                                .amount(RandomUtils.nextInt()).build())
                        .name(randomAlphabetic(STRING_SIZE))
                        .validFor(getTimePeriodBuilder()
                                .build())
                        .build()));
    }

    @SneakyThrows
    public static Product.ProductBuilder createProductBuilderWithProductOffering(ProductStatusType status, ProductOperationalStatusType operationalStatusType, String productOrderId, String orderItemId, String productOfferingId, String productOfferingType) {
        return commonProductBuilder(status, productOrderId, orderItemId, operationalStatusType)
                .productOffering(ProductOfferingRef.builder()
                        .id(productOfferingId)
                        .name(randomAlphabetic(STRING_SIZE))
                        .atType(productOfferingType)
                        .atReferredType(CONTRACT.getValue())
                        .build());

    }


    public static Product.ProductBuilder createProductBuilderWithProductOffering(String productOfferingId, String type) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(type).build());
    }


    public static Product.ProductBuilder createProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType) {
        return createProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(relationshipType.getValue())
                                .product(createProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId)
                                        .build())
                                .build()
                        )
                );
    }


    public static Product.ProductBuilder createProductBuilderWithRelationChildHasParent(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType, String idParent) {
        return createProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                        .relationshipType(relationshipType.getValue())
                                        .product(createProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId)
                                                .build())
                                        .build(),
                                ProductRelationship.builder()
                                        .relationshipType(HASPARENT.getValue())
                                        .product(ProductRef.builder().id(idParent).atType(ProductTypeEnum.PRODUCT_REF.getValue()).build())
                                        .build()
                        )
                );
    }

    public static Product.ProductBuilder createProductBuilderWithRelationChildHasParentAndChildContainHasParent(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType, String idParent) {
        return createProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                        .relationshipType(relationshipType.getValue())
                                        .product(
                                                createProductBuilderWithProductSpecificationWithHasParent(status, productOrderId, orderItemId, productSpecificationId, idParent)
                                                        .build())
                                        .build(),
                                ProductRelationship.builder()
                                        .relationshipType(HASPARENT.getValue())
                                        .product(ProductRef.builder().id(idParent).atType(ProductTypeEnum.PRODUCT_REF.getValue()).build())
                                        .build()
                        )
                );
    }

    public static Product.ProductBuilder createContractProductWithBundleAndAtomicProductOfferingAndSpecificationAndRelationships(String contractProductOfferingId, String productSpecificationId, String bundleProductOfferingId, String atomicProductOfferingId) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(contractProductOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(
                        List.of(
                                ProductRelationship
                                        .builder()
                                        .relationshipType(ProductRelationshipType.BUNDLES.getValue())
                                        .product(
                                                createProductBuilderWithBundleAndAtomicProductSpecification(
                                                        CREATED,
                                                        randomAlphabetic(STRING_SIZE),
                                                        randomAlphabetic(STRING_SIZE),
                                                        productSpecificationId,
                                                        bundleProductOfferingId,
                                                        atomicProductOfferingId).build())
                                        .build()));
    }

    public static Product.ProductBuilder createContractProductWithBundleAndAtomicProductOfferingAndSellsRelationshipWithSpecificationAndRelationships(String contractProductOfferingId, String productSpecificationId, String bundleProductOfferingId, String atomicProductOfferingId) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(contractProductOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(
                        List.of(
                                ProductRelationship
                                        .builder()
                                        .relationshipType(ProductRelationshipType.BUNDLES.getValue())
                                        .product(
                                                createProductBuilderWithBundleAndAtomicProductOfferingWithSellsSpecification(
                                                        CREATED,
                                                        randomAlphabetic(STRING_SIZE),
                                                        randomAlphabetic(STRING_SIZE),
                                                        productSpecificationId,
                                                        bundleProductOfferingId,
                                                        atomicProductOfferingId).build())
                                        .build()));
    }

    public static Offer.ProductBuilder createProductOfferBuilderWithProductOfferingAndRelationship(String productOfferingId, String productSpecificationId, ProductRelationshipType productRelationshipType) {
        return commonProductOfferBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), productSpecificationId).build())
                        .build()));
    }

    public static MobileLine.ProductBuilder createProductMobileLineBuilderWithProductOfferingAndRelationship(String productOfferingId, String productSpecificationId, ProductRelationshipType productRelationshipType) {
        return commonProductMobileLineBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), productSpecificationId).build())
                        .build()));
    }

    public static SimCard.ProductBuilder createProductSimCardBuilderWithProductOfferingAndRelationship(String productOfferingId, String productSpecificationId, ProductRelationshipType productRelationshipType) {
        return commonProductSimCardBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), productSpecificationId).build())
                        .build()));
    }

    public static Service.ProductBuilder createProductServiceBuilderWithProductOfferingAndRelationship(String productOfferingId, String productSpecificationId, ProductRelationshipType productRelationshipType) {
        return commonProductServiceBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), productSpecificationId).build())
                        .build()));
    }

    public static Product.ProductBuilder createProductBuilderInnerRelationShip(String productOfferingId, String productSpecificationId,
                                                                               ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                               String productOfferingAtType) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).name("Mobile Package 1").atType(productOfferingAtType).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS).build()).build()));

    }

    public static Product.ProductBuilder createProductBuilderInnerRelationShipValidRelationShip(
            String productOfferingId,
            String productSpecificationId,
            ProductRelationshipType productRelationshipType,
            ProductStatusType productStatusType) {

        return createProductBuilderInnerRelationShipValidRelationShip(
                productOfferingId,
                productSpecificationId,
                productRelationshipType,
                productStatusType,
                VALID_BUNDLE_PRODUCT_OFFERING_ID);
    }

    public static Product.ProductBuilder createProductBuilderInnerRelationShipValidRelationShip(
            String productOfferingId,
            String productSpecificationId,
            ProductRelationshipType productRelationshipType,
            ProductStatusType productStatusType,
            String bundleProductOfferingId) {

        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .id(ObjectId.get().toString())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(CONTRACT.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductOffering(bundleProductOfferingId, BUNDLE_PRODUCT_OFFERING.getValue())
                                .id(ObjectId.get().toString())
                                .productRelationship(List.of(ProductRelationship.builder()
                                        .relationshipType(productRelationshipType.getValue())
                                        .product(createProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                                productSpecificationId, VALID_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS)
                                                .id("P3")
                                                .build())
                                        .build()))
                                .build())
                        .build()));
    }


    public static Product.ProductBuilder createProductBuilderInnerRelation(String productOfferingId, ProductRelationshipType productRelationshipType, String productOfferingType, Product.ProductBuilder innerProduct) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED).id("pA")
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingType).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductOffering(VALID_BUNDLE_PRODUCT_OFFERING_ID, BUNDLE_PRODUCT_OFFERING.getValue()).id("P2")
                                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                                        .product(innerProduct.build())
                                        .build()))
                                .build())
                        .build()));
    }

    public static Product.ProductBuilder createProductWithCharacteristics(String productOfferingId, String productSpecificationId, ProductRelationshipType productRelationshipType, List<Characteristic> characteristic) {
        return commonProductBuilder(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), ProductOperationalStatusType.CREATED)
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(ATOMIC_PRODUCT_OFFERING.getValue()).build())
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), productSpecificationId).productCharacteristic(characteristic).build())
                        .build()));
    }
}
