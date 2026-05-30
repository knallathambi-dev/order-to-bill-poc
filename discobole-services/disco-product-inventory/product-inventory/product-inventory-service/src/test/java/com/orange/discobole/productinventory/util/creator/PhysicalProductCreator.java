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
import com.orange.discobole.productinventory.dto.v1.PhysicalProduct.PhysicalProductBuilder;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.enumerate.ResourceEntityType;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.productinventory.constant.TestConstant.*;
import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.SELLS;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.CONTRACT;
import static com.orange.discobole.productinventory.util.creator.CommonCreator.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class PhysicalProductCreator {


    @SneakyThrows
    public static PhysicalProduct.PhysicalProductBuilder createPhysicalProductBuilderWithProductSpecification(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String serialNum) {
        PhysicalProduct.PhysicalProductBuilder physicalProductBuilder = commonPhysicalProductBuilder(status, productOrderId, orderItemId, ProductOperationalStatusType.CREATED);
        physicalProductBuilder.productSpecification(ProductSpecificationRef.builder()
                        .id(productSpecificationId)
                        .name(randomAlphabetic(STRING_SIZE))
                        .atType("ProductSpecificationRef")
                        .build()
                )
                .productSerialNumber(serialNum);
        return physicalProductBuilder;
    }


    public static PhysicalProductBuilder commonPhysicalProductBuilder(ProductStatusType status, String productOrderId, String orderItemId, ProductOperationalStatusType operationalStatus) {
        return
                PhysicalProduct.builder()
                        .status(status)
                        .operationalStatus(operationalStatus)
                        .atType(ProductTypeEnum.PHYSICAL_PRODUCT.getValue())
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
    public static PhysicalProduct.ProductBuilder createPhysicalProductBuilderWithProductOffering(ProductStatusType status, ProductOperationalStatusType operationalStatusType, String productOrderId, String orderItemId, String productOfferingId, String productOfferingType) {
        return commonPhysicalProductBuilder(status, productOrderId, orderItemId, operationalStatusType)
                .productOffering(ProductOfferingRef.builder()
                        .id(productOfferingId)
                        .name(randomAlphabetic(STRING_SIZE))
                        .atType(productOfferingType)
                        .atReferredType(CONTRACT.getValue())
                        .build());

    }

    public static PhysicalProduct.ProductBuilder createPhysicalProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType, String serialNum) {
        return createPhysicalProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(relationshipType.getValue())
                                .product(createPhysicalProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId, serialNum)
                                        .build())
                                .build()
                        )
                );
    }

    public static Product.ProductBuilder createInvalidPhysicalProductBuilderWithRelation(ProductStatusType status, String productOrderId, String orderItemId, String productSpecificationId, String productOfferingId, String productOfferingType, ProductRelationshipType relationshipType) {
        return ProductCreator.createProductBuilderWithProductOffering(status, ProductOperationalStatusType.CREATED, productOrderId, orderItemId, productOfferingId, productOfferingType)
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(relationshipType.getValue())
                                .product(createPhysicalProductBuilderWithProductSpecification(status, productOrderId + "child", orderItemId + "child", productSpecificationId, null)
                                        .build())
                                .build()
                        )
                );
    }


    public static Product.ProductBuilder createContractPhysicalProduct(String productOfferingId, String productSpecificationId,
                                                                       ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                       String productOfferingAtType) {
        return PhysicalProduct.builder().atType(ProductTypeEnum.PHYSICAL_PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingAtType).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createPhysicalProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, productOfferingId, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, null).build()).build()));

    }

    public static Product.ProductBuilder createPhysicalProductInnerRelationShip(String productOfferingId, String productSpecificationId,
                                                                                ProductRelationshipType productRelationshipType, ProductStatusType productStatusType,
                                                                                String productOfferingAtType,
                                                                                String serialNum) {
        return Product.builder().atType(ProductTypeEnum.PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(productOfferingAtType).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(List.of(ProductRelationship.builder().relationshipType(productRelationshipType.getValue())
                        .product(createPhysicalProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS, serialNum).build()).build()));

    }

    public static PhysicalProduct.ProductBuilder createAtomicPhysicalProduct(String productOfferingId, String productSpecificationId, String serialNum) {
        return PhysicalProduct.builder().atType(ProductTypeEnum.PHYSICAL_PRODUCT.getValue())
                .productOffering(ProductOfferingRef.builder().id(productOfferingId).atType(ATOMIC_PRODUCT_OFFERING.getValue()).build())
                .status(CREATED).operationalStatus(ProductOperationalStatusType.CREATED)
                .productOrderItem(
                        List.of(
                                getRelatedProductOrderItemBuilder(randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE)).build()
                        )
                )
                .productRelationship(
                        List.of(ProductRelationship.builder()
                                .relationshipType(SELLS.getValue())
                                .product(
                                        createPhysicalProductBuilderWithProductSpecification(
                                                CREATED,
                                                randomAlphabetic(STRING_SIZE) + "child",
                                                randomAlphabetic(STRING_SIZE) + "child",
                                                productSpecificationId,
                                                serialNum)
                                                .build())
                                .build()
                        ));

    }

    public static Product.ProductBuilder createPhysicalProductWithAtomicProductInnerRelationShip(String productOfferingId, String productSpecificationId,
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
                        .product(createInvalidPhysicalProductBuilderWithRelation(productStatusType, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE),
                                productSpecificationId, productOfferingId, ATOMIC_PRODUCT_OFFERING.getValue(), SELLS).build()).build()));

    }

    public static List<ResourceRef> createRealizingResourceListOneIsPhysical() {
        List<ResourceRef> realizingResourceList = new ArrayList<>();

        ResourceRef physicalRealizingResource = getResourceRefBuilder().id(VALID_REALIZING_RESOURCE_ID).atType(ResourceEntityType.PHYSICAL_RESOURCE.getValue()).build();

        ResourceRef logicalRealizingResource = getResourceRefBuilder().build().id("ANY_ID").atType(ResourceEntityType.LOGICAL_RESOURCE.getValue());

        realizingResourceList.add(physicalRealizingResource);
        realizingResourceList.add(logicalRealizingResource);

        return  realizingResourceList;
    }

}

