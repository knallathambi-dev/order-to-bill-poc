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
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.CONTRACT;
import static com.orange.discobole.productinventory.util.creator.CommonCreator.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ShipmentProductCreator {


    @SneakyThrows
    public static ShipmentProduct.ShipmentProductBuilder createShipmentProductBuilderWithProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId) {
        ShipmentProduct.ShipmentProductBuilder shipmentProductBuilder = commonShipmentProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED);
        shipmentProductBuilder.productSpecification(ProductSpecificationRef.builder()
                .id(productSpecificationId)
                .name(randomAlphabetic(STRING_SIZE))
                .atType("ProductSpecificationRef")
                .build()
        );
        return shipmentProductBuilder;
    }


    public static ShipmentProduct.ShipmentProductBuilder commonShipmentProductBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return
                ShipmentProduct.builder()
                        .status(status)
                        .operationalStatus(operationalStatus)
                        .atType(ProductTypeEnum.SHIPMENT_PRODUCT.getValue())
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
                                .price(getPriceBuilder().build())
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
    public static ShipmentProduct.ProductBuilder createShipmentProductBuilderWithProductOffering(ProductStatusType status, ProductOperationalStatusType operationalStatusType, String productOrderId, String orderItemId, String productOfferingId, String productOfferingType) {
        return commonShipmentProductBuilder(status, productOrderId, orderItemId, operationalStatusType)
                .productOffering(ProductOfferingRef.builder()
                        .id(productOfferingId)
                        .name(randomAlphabetic(STRING_SIZE))
                        .atType(productOfferingType)
                        .atReferredType(CONTRACT.getValue())
                        .build());

    }


    public static ShipmentProduct.ProductBuilder createShipmentProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType) {
        return createShipmentProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(relationshipType.getValue())
                                .product(createShipmentProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId)
                                        .build())
                                .build()
                        )
                );
    }


    public static  Product.ProductBuilder createShipmentProductInnerRelationShip(String productOfferingId, String productSpecificationId,
                                                                                 ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                                                String productOfferingAtType)  {
        return Product.builder().atType(ProductTypeEnum.PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingAtType).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createShipmentProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), ProductRelationshipType.SELLS).build()).build()));

    }

    public static Product.ProductBuilder createShipmentProductWithAtomicProductInnerRelationShip(String productOfferingId, String productSpecificationId,
                                                                                                 ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                                                 String productOfferingAtType) {
        return Product.builder().atType(ProductTypeEnum.PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingAtType).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createInvalidShipmentProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, productOfferingId, ATOMIC_PRODUCT_OFFERING.getValue(), ProductRelationshipType.SELLS).build()).build()));

    }
    public static Product.ProductBuilder createInvalidShipmentProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType) {
        return ProductCreator.createProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(relationshipType.getValue())
                                .product(createShipmentProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId)
                                        .build())
                                .build()
                        )
                );
    }
    public static Product.ProductBuilder createContractShipmentProduct(String productOfferingId, String productSpecificationId,
                                                                       ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                       String productOfferingAtType) {
        return ShipmentProduct.builder().atType(ProductTypeEnum.SHIPMENT_PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingAtType).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createShipmentProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, productOfferingId, ATOMIC_PRODUCT_OFFERING.getValue(), ProductRelationshipType.SELLS).build()).build()));

    }
}

