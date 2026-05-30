// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.mapper.product.order;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.CurrencyUtils;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.InstallmentCharge;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Characteristic;
import com.orange.discobole.productinventory.dto.v1.Money;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOfferingPriceRef;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import com.orange.discobole.productinventory.dto.v1.ProductRefOrValue;
import com.orange.discobole.productinventory.dto.v1.ProductRelationship;
import com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef;
import com.orange.discobole.productinventory.dto.v1.Quantity;
import com.orange.discobole.productinventory.dto.v1.ResourceRef;
import com.orange.discobole.productinventory.dto.v1.ServiceRef;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Component("productOrderItemMapper")
@Mapper(
        componentModel = "spring",
        uses = {CharacteristicMapper.class, PartyOrPartyRoleMapper.class})
public interface ProductOrderItemMapper {

    @Mapping(target = "productPrice", source = "itemPrice", qualifiedByName = "mapProductPrice")
    @Mapping(target = "productTerm", source = "itemTerm")
    @Mapping(target = "productOffering.atSchemaLocation", source = "productOffering.atSchemaLocation", qualifiedByName = "convertUriToString")
    @Mapping(target = "isBundle", source = "productOrderItem", qualifiedByName = "mapIsBundle")
    @Mapping(target = "realizingResource", source = "product", qualifiedByName = "mapRealizingResource")
    @Mapping(target = "realizingService", source = "product", qualifiedByName = "mapRealizingService")
    @Mapping(target = "productCharacteristic", source = "product", qualifiedByName = "mapProductCharacteristic")
    @Mapping(target = "productRelationship", source = "product", qualifiedByName = "mapProductRelationship")
    @Mapping(target = "productSpecification", source = "product", qualifiedByName = "mapProductSpecification")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atType", constant = "Product")
    @Mapping(target = "isCustomerVisible", source = "product", qualifiedByName = "mapIsCustomerVisibleField")
    Product mapProductOrderItemToProduct(ProductOrderItem productOrderItem);

    @Mapping(target = "productPrice", source = "itemPrice", qualifiedByName = "mapProductPrice")
    @Mapping(target = "productTerm", source = "itemTerm")
    @Mapping(target = "productOffering.atSchemaLocation", source = "productOffering.atSchemaLocation", qualifiedByName = "convertUriToString")
    @Mapping(target = "isBundle", source = "productOrderItem", qualifiedByName = "mapIsBundle")
    @Mapping(target = "realizingResource", source = "product", qualifiedByName = "mapRealizingResource")
    @Mapping(target = "realizingService", source = "product", qualifiedByName = "mapRealizingService")
    @Mapping(target = "productCharacteristic", source = "product", qualifiedByName = "mapProductCharacteristic")
    @Mapping(target = "productSpecification", source = "product", qualifiedByName = "mapProductSpecification")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atType", constant = "PhysicalProduct")
    @Mapping(target = "isCustomerVisible", source = "product", qualifiedByName = "mapIsCustomerVisibleField")
    PhysicalProduct mapProductOrderItemToPhysicalProduct(ProductOrderItem productOrderItem);

    @Mapping(target = "productPrice", source = "itemPrice", qualifiedByName = "mapProductPrice")
    @Mapping(target = "productTerm", source = "itemTerm")
    @Mapping(target = "productOffering.atSchemaLocation", source = "productOffering.atSchemaLocation", qualifiedByName = "convertUriToString")
    @Mapping(target = "isBundle", source = "productOrderItem", qualifiedByName = "mapIsBundle")
    @Mapping(target = "realizingResource", source = "product", qualifiedByName = "mapRealizingResource")
    @Mapping(target = "realizingService", source = "product", qualifiedByName = "mapRealizingService")
    @Mapping(target = "productCharacteristic", source = "product", qualifiedByName = "mapProductCharacteristic")
    @Mapping(target = "productSpecification", source = "product", qualifiedByName = "mapProductSpecification")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atType", constant = "ShipmentProduct")
    ShipmentProduct mapProductOrderItemToShipmentProduct(ProductOrderItem productOrderItem);

    @Named("mapProductRelationship")
    default List<ProductRelationship> mapProductRelationship(
            com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                && product.getProductRelationship() != null) {
            return product.getProductRelationship().stream()
                    .filter(productRelationship -> productRelationship.getRelationshipType().equals(OrderCaptureConstants.MIGRATE_FROM))
                    .map(this::mapSingleProductRelationship)
                    .toList();
        }
        return List.of();
    }

    @Mapping(target = "relationshipType", source = "relationshipType")
    @Mapping(target = "product", source = "product", qualifiedByName = "mapProductRefOrValue")
    ProductRelationship mapSingleProductRelationship(
            com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRelationship dto
    );

    @Named("mapProductRefOrValue")
    default ProductRefOrValue mapProductRefOrValue(
            com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue source
    ) {
        if (source == null) {
            return null;
        }
        if (source instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef p) {

            ProductRef target = new ProductRef();
            target.setId(p.getId());
            return target;
        }
        return null;
    }

    @Named("mapIsCustomerVisibleField")
    default Boolean mapIsCustomerVisibleField(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (Objects.isNull(productRefOrValue)) {
            return Boolean.FALSE;
        }

        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            Boolean isCustomerVisible = product.getIsCustomerVisible();
            return isCustomerVisible != null ? isCustomerVisible : Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    @Named("mapRealizingResource")
    default List<ResourceRef> mapRealizingResource(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            return mapResourceListToResourceRefList(product.getRealizingResource());
        }
        return List.of();
    }

    List<ResourceRef> mapResourceListToResourceRefList(List<com.orange.discobole.ordermanagement.orderinventory.dto.v1.ResourceRef> resources);

    ResourceRef mapResourceToResourceRef(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ResourceRef resource);

    @Named("mapRealizingService")
    default List<ServiceRef> mapRealizingService(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            return mapServiceListToServiceRefList(product.getRealizingService());
        }
        return List.of();
    }

    List<ServiceRef> mapServiceListToServiceRefList(List<com.orange.discobole.ordermanagement.orderinventory.dto.v1.ServiceRef> services);

    ServiceRef mapServiceToServiceRef(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ServiceRef service);

    @Named("mapProductCharacteristic")
    default List<Characteristic> mapProductCharacteristic(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            return mapCharacteristicList(product.getProductCharacteristic());
        }
        return List.of();
    }

    List<Characteristic> mapCharacteristicList(List<com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic> source);

    @Named("mapProductSpecification")
    default ProductSpecificationRef mapProductSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            return mapProductSpecificationToProductSpecificationRef(product.getProductSpecification());
        }
        return null;
    }

    ProductSpecificationRef mapProductSpecificationToProductSpecificationRef(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef productSpecification);

    @Condition
    default boolean isNotNull(Object obj) {
        return obj != null;
    }

    @Mapping(target = "productPriceAlteration", source = "priceAlteration")
    @Mapping(target = "productOfferingPrice", source = "productOfferingPrice", qualifiedByName = "mapProductOfferingPriceRefOrValue")
    @Mapping(target = "price.dutyFreeAmount", source = "productOfferingPrice", qualifiedByName = "mapPrice")
    @Mapping(target = "applicationDuration", source = "productOfferingPrice", qualifiedByName = "mapApplicationDuration")
    @Mapping(target = "price.percentage", ignore = true)
    @Mapping(target = "price.atBaseType", ignore = true)
    @Mapping(target = "price.atType", constant = "Price")
    @Mapping(target = "atType", constant = "ProductPrice")
    ProductPrice mapOrderPriceToProductPrice(OrderPrice itemPrice);


    @Named("mapProductOfferingPriceRefOrValue")
    default ProductOfferingPriceRef mapProductOfferingPriceRefOrValue(ProductOfferingPriceRefOrValue source) {
        if (source == null) {
            return null;
        }
        if (source instanceof InstallmentCharge installmentCharge) {
            return mapProductOfferingPriceInstalmentChargeToDto(installmentCharge);
        } else if (source instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
            return mapProductOfferingPriceChargeToDto(productOfferingPriceCharge);
        } else if (source instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingPriceRef productOfferingPriceRef) {
            return mapProductOfferingPriceRefToDto(productOfferingPriceRef);
        }
        return null;
    }
    @Named("mapApplicationDuration")
    default Quantity mapApplicationDuration(ProductOfferingPriceRefOrValue source) {
        if (source == null) {
            return null;
        }
        if (source instanceof InstallmentCharge installmentCharge) {
            return mapProductOfferingPriceApplicationDuration(installmentCharge.getApplicationDuration());
        } else if (source instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
            return mapProductOfferingPriceApplicationDuration(productOfferingPriceCharge.getApplicationDuration());
        }
        return null;
    }

    @Named("mapInterestRate")
    default Float mapInterestRate(ProductOfferingPriceRefOrValue source) {
        return source instanceof InstallmentCharge installmentCharge ? installmentCharge.getInterestRate() : null;
    }

    @Named("mapAndRoundDownPayment")
    default Float mapAndRoundDownPayment(ProductOfferingPriceRefOrValue source) {
        if (source instanceof InstallmentCharge installmentCharge) {
            return CurrencyUtils.roundFloat(installmentCharge.getDownPayment());
        }
        return null;
    }
    @Named("mapExternalId")
    default String mapExternalId(ProductOfferingPriceRefOrValue source) {
        return source instanceof InstallmentCharge installmentCharge ? installmentCharge.getExternalId() : null;
    }
    @Named("mapPartner")
    default String mapPartner(ProductOfferingPriceRefOrValue source) {
        return source instanceof InstallmentCharge installmentCharge ? installmentCharge.getPartner() : null;
    }

    Quantity mapProductOfferingPriceApplicationDuration(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Quantity quantity);


    @Named("mapPrice")
    default Money mapPrice(ProductOfferingPriceRefOrValue productOfferingPriceRefOrValue) {
        if (productOfferingPriceRefOrValue instanceof InstallmentCharge installmentCharge) {
            return mapProductOfferingPriceDtoPriceToPrice(installmentCharge.getPrice());
        } else if (productOfferingPriceRefOrValue instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
            return mapProductOfferingPriceDtoPriceToPrice(productOfferingPriceCharge.getPrice());
        }
        return null;
    }

    Money mapProductOfferingPriceDtoPriceToPrice(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Money money);

    @Named("mapProductPrice")
    default ProductPrice mapProductPrice(OrderPrice orderPrice) {
        if (orderPrice.getProductOfferingPrice() instanceof InstallmentCharge) {
            com.orange.discobole.productinventory.dto.v1.InstallmentCharge installmentCharge = mapInstallmentCharge(orderPrice);
            CurrencyUtils.applyRounding(installmentCharge);
            return installmentCharge;
        }
        ProductPrice productPrice = mapOrderPriceToProductPrice(orderPrice);
        CurrencyUtils.applyRounding(productPrice);
        return productPrice;
    }

    @Mapping(target = "productOfferingPrice", source = "productOfferingPrice", qualifiedByName = "mapProductOfferingPriceRefOrValue")
    @Mapping(target = "price.dutyFreeAmount", source = "productOfferingPrice", qualifiedByName = "mapPrice")
    @Mapping(target = "applicationDuration", source = "productOfferingPrice", qualifiedByName = "mapApplicationDuration")
    @Mapping(target = "interestRate", source = "productOfferingPrice", qualifiedByName = "mapInterestRate")
    @Mapping(target = "downPayment", source = "productOfferingPrice", qualifiedByName = "mapAndRoundDownPayment")
    @Mapping(target = "externalId", source = "productOfferingPrice", qualifiedByName = "mapExternalId")
    @Mapping(target = "partner", source = "productOfferingPrice", qualifiedByName = "mapPartner")
    @Mapping(target = "productPriceAlteration", source = "priceAlteration")
    @Mapping(target = "unitOfMeasure", ignore = true)
    @Mapping(target = "validFor", ignore = true)
    @Mapping(target = "price.percentage", ignore = true)
    @Mapping(target = "price.atBaseType", ignore = true)
    @Mapping(target = "price.atType", constant = "Price")
    @Mapping(target = "atType", constant = "InstallmentCharge")
    com.orange.discobole.productinventory.dto.v1.InstallmentCharge mapInstallmentCharge(OrderPrice orderPrice);


    @SuppressWarnings("NP_BOOLEAN_RETURN_NULL")
    @Named("mapIsBundle")
    default Boolean mapIsBundle(ProductOrderItem productOrderItem) {
        if (Objects.isNull(productOrderItem) || Objects.isNull(productOrderItem.getProduct())) {
            return Boolean.FALSE;
        }

        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue productRefOrValue = productOrderItem.getProduct();

        if (productRefOrValue instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            Boolean isBundle = product.getIsBundle();
            return isBundle != null ? isBundle : Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    @Named("convertUriToString")
    default String convertUriToString(URI uri) {
        return uri == null ? null : uri.toString();
    }

    @Mapping(target = "atSchemaLocation", source = "atSchemaLocation", qualifiedByName = "convertUriToString")
    ProductOfferingPriceRef mapProductOfferingPriceChargeToDto(ProductOfferingPriceCharge productOfferingPriceCharge);

    @Mapping(target = "atSchemaLocation", source = "atSchemaLocation", qualifiedByName = "convertUriToString")
    ProductOfferingPriceRef mapProductOfferingPriceInstalmentChargeToDto(InstallmentCharge installmentCharge);

    @Mapping(target = "atSchemaLocation", source = "atSchemaLocation", qualifiedByName = "convertUriToString")
    ProductOfferingPriceRef mapProductOfferingPriceRefToDto(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingPriceRef productOfferingPriceRef);

    RelatedPartyOrPartyRole mapRelatedPartyRefToRelatedParty(RelatedPartyRefOrPartyRoleRef relatedParty);

    List<RelatedPartyOrPartyRole> mapRelatedPartyRefsToRelatedParties(List<RelatedPartyRefOrPartyRoleRef> relatedParty);
}