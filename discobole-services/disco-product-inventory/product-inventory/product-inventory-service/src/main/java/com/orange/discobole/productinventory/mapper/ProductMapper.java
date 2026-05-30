// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.model.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.SneakyThrows;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Set;


@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    private ObjectMapper objectMapper;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
            justification = "to allow auto wire for objectMapper")
    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRelationshipDtoToProductRelationshipEntity")
    public abstract ProductEntity toEntity(Product product);

    @Named("mapProductRelationshipDtoToProductRelationshipEntity")
    protected ProductRelationshipEntity mapProductRelationshipDtoToProductRelationshipEntity(ProductRelationship productRelationship) {
        ProductRefEntity prd = null;
        if (productRelationship.getProduct() != null) {
            if (productRelationship.getProduct() instanceof Product) {
                prd = new ProductRefEntity(((Product) productRelationship.getProduct()).getId());
            } else if (productRelationship.getProduct() instanceof ProductRef) {
                prd = new ProductRefEntity(((ProductRef) productRelationship.getProduct()).getId());
            }
        }
        return ProductRelationshipEntity
                .builder()
                .product(prd)
                .relationshipType(productRelationship.getRelationshipType())
                .build();
    }

    @AfterMapping
    void afterMapping(@MappingTarget ProductEntity.ProductEntityBuilder productEntityBuilder, Product product) {
        if (product.getProductRelationship() != null && !product.getProductRelationship().isEmpty()) {
            productEntityBuilder.isRootProduct(product.getProductRelationship().stream().noneMatch(relationship -> ProductRelationshipType.ROOTPRODUCT.getValue().equals(relationship.getRelationshipType())));
        }
    }

    @Named("productFullMapping")
    public Product toDtoWithFullMapping(ProductEntity product) {
        Product result;
        if (Objects.isNull(product.getAtType())) {
            result = toDtoWithFullMappingProduct(product);
        } else {
            result = switch (product.getAtType()) {
                case "PhysicalProduct" -> toDtoWithFullMappingPhysicalProduct(product);
                case "ShipmentProduct" -> toDtoWithFullMappingShipmentProduct(product);
                case "Offer" -> toDtoWithFullMappingOffer(product);
                case "SimCard" -> toDtoWithFullMappingSimCard(product);
                case "MobileLine" -> toDtoWithFullMappingMobileLine(product);
                case "Service" -> toDtoWithFullMappingService(product);
                default -> toDtoWithFullMappingProduct(product);
            };
        }
        return result;
    }

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @Mapping(target = "atType", constant = "Product")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Product toDtoWithFullMappingProduct(ProductEntity product);

    @Named("physicalProductFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract PhysicalProduct toDtoWithFullMappingPhysicalProduct(ProductEntity product);

    @Named("shipmentProductFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract ShipmentProduct toDtoWithFullMappingShipmentProduct(ProductEntity product);

    @Named("offerFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Offer toDtoWithFullMappingOffer(ProductEntity product);

    @Named("simCardFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract SimCard toDtoWithFullMappingSimCard(ProductEntity product);

    @Named("mobileLineFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract MobileLine toDtoWithFullMappingMobileLine(ProductEntity product);

    @Named("serviceFullMapping")
    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "mapProductRefOrProduct")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Service toDtoWithFullMappingService(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("productOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    public Product toDtoWithProductIdOnly(ProductEntity product) {
        Product result;
        if (Objects.isNull(product.getAtType())) {
            result = toDtoWithFullMappingProduct(product);
        } else {
            result = switch (product.getAtType()) {
                case "PhysicalProduct" -> toPhysicalProductDtoWithProductIdOnly(product);
                case "ShipmentProduct" -> toShipmentProductDtoWithProductIdOnly(product);
                case "Offer" -> toOfferDtoWithProductIdOnly(product);
                case "SimCard" -> toSimCardDtoWithProductIdOnly(product);
                case "MobileLine" -> toMobileLineDtoWithProductIdOnly(product);
                case "Service" -> toServiceDtoWithProductIdOnly(product);
                default -> toProductDtoWithProductIdOnly(product);
            };
        }
        return result;
    }

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("productOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Product toProductDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("productOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract PhysicalProduct toPhysicalProductDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("productOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract ShipmentProduct toShipmentProductDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("offerOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Offer toOfferDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("simCardOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract SimCard toSimCardDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("mobileLineOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract MobileLine toMobileLineDtoWithProductIdOnly(ProductEntity product);

    @Mapping(target = "productRelationship", source = "productRelationship", qualifiedByName = "productRelationshipOnlyIdMapping")
    @Named("serviceOnlyIdMapping")
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    protected abstract Service toServiceDtoWithProductIdOnly(ProductEntity product);

    @Named("productRelationshipOnlyIdMapping")
    @Mapping(source = "product", target = "product", qualifiedByName = "mapProductIdOnly")
    protected abstract ProductRelationship toProductRelationshipDtoWithIdOnly(ProductRelationshipEntity productRelationship);

    @Named("toProductRelationshipDtoWithFullMapping")
    @Mapping(source = "product", target = "product", qualifiedByName = "productDtoWithFullMapping")
    protected abstract ProductRelationship toProductRelationshipDtoWithFullMapping(ProductRelationshipEntity productRelationship);

    @Named("mapProductIdOnly")
    protected ProductRefOrValue mapProductIdOnly(ProductRefEntity value) {
        return ProductRef.builder().id(value.getId() != null ? value.getId().toString() : null).build();
    }

    @Named("mapProductRefOrProduct")
    protected ProductRelationship mapProductRefOrProductDto(ProductRelationshipEntity productRelationship) {
        if (productRelationship == null || productRelationship.getRelationshipType() == null) {
            return null;
        }
        Set<ProductRelationshipType> relationshipTypesToMapToProductRef = Set.of(
                ProductRelationshipType.ROOTPRODUCT,
                ProductRelationshipType.RELIESFROM,
                ProductRelationshipType.RELIESON
        );
        if (relationshipTypesToMapToProductRef.stream().map(ProductRelationshipType::getValue)
                .anyMatch(value -> value.equals(productRelationship.getRelationshipType()))) {
            return toProductRelationshipDtoWithIdOnly(productRelationship);
        } else {
            return toProductRelationshipDtoWithFullMapping(productRelationship);
        }
    }

    @Named("productDtoWithFullMapping")
    protected ProductRefOrValue toProductDtoWithFullMapping(ProductEntity productEntity) {
        return toDtoWithFullMapping(productEntity);
    }


    @Named("productDtoWithFullMapping")
    protected ProductRefOrValue toProductDtoWithFullMapping(ProductRefEntity value) {
        return ProductRef.builder().id(value.getId() != null ? value.getId().toString() : null).build();

    }

    @SneakyThrows
    protected Characteristic characteristicFromEntity(CharacteristicEntity characteristic) {
        return this.objectMapper.convertValue(characteristic, Characteristic.class);
    }

    protected CharacteristicEntity characteristicToCharacteristicEntity(Characteristic characteristic) {
        return objectMapper.convertValue(characteristic, CharacteristicEntity.class);
    }

    protected ProductRefOrValue map(ProductRefEntity value) {
        return ProductRef.builder().id(value.getId() != null ? value.getId().toString() : null).build();
    }

    protected RelatedPartyEntity relatedPartyOrPartyRoleToRelatedPartyEntity(RelatedPartyOrPartyRole relatedPartyOrPartyRole) {
        if (relatedPartyOrPartyRole == null) {
            return null;
        }
        RelatedPartyEntity.RelatedPartyEntityBuilder relatedPartyEntity = RelatedPartyEntity.builder();
        relatedPartyEntity.role(relatedPartyOrPartyRole.getRole());
        if (relatedPartyOrPartyRole.getPartyOrPartyRole() != null) {
            if (relatedPartyOrPartyRole.getPartyOrPartyRole() instanceof PartyRef partyRef) {
                relatedPartyEntity.id(partyRef.getId());
                relatedPartyEntity.name(partyRef.getName());
                relatedPartyEntity.atReferredType(partyRef.getAtReferredType());
                relatedPartyEntity.atType(partyRef.getAtType());
            } else if (relatedPartyOrPartyRole.getPartyOrPartyRole() instanceof PartyRoleRef partyRoleRef) {
                relatedPartyEntity.id(partyRoleRef.getId());
                relatedPartyEntity.name(partyRoleRef.getName());
                relatedPartyEntity.atReferredType(partyRoleRef.getAtReferredType());
                relatedPartyEntity.atType(partyRoleRef.getAtType());
                relatedPartyEntity.partyId(partyRoleRef.getPartyId());
                relatedPartyEntity.partyName(partyRoleRef.getPartyName());
            }
        }
        return relatedPartyEntity.build();
    }

    protected RelatedPartyOrPartyRole relatedPartyEntityToRelatedPartyOrPartyRole(RelatedPartyEntity relatedPartyEntity) {
        if (relatedPartyEntity == null) {
            return null;
        }

        RelatedPartyOrPartyRole.RelatedPartyOrPartyRoleBuilder<?, ?> relatedPartyOrPartyRole = RelatedPartyOrPartyRole.builder();

        relatedPartyOrPartyRole.atType("RelatedPartyRefOrPartyRoleRef");
        relatedPartyOrPartyRole.role(relatedPartyEntity.getRole());
        if (relatedPartyEntity.getAtType() != null) {
            if (relatedPartyEntity.getAtType().equals("PartyRef")) {
                relatedPartyOrPartyRole.partyOrPartyRole(PartyRef
                        .builder()
                        .id(relatedPartyEntity.getId())
                        .atType(relatedPartyEntity.getAtType())
                        .name(relatedPartyEntity.getName())
                        .atReferredType(relatedPartyEntity.getAtReferredType())
                        .build());

            } else if (relatedPartyEntity.getAtType().equals("PartyRoleRef")) {
                relatedPartyOrPartyRole.partyOrPartyRole(PartyRoleRef
                        .builder()
                        .id(relatedPartyEntity.getId())
                        .name(relatedPartyEntity.getName())
                        .atType(relatedPartyEntity.getAtType())
                        .partyId(relatedPartyEntity.getPartyId())
                        .partyName(relatedPartyEntity.getPartyName())
                        .atReferredType(relatedPartyEntity.getAtReferredType())
                        .build());
            }
        }


        return relatedPartyOrPartyRole.build();
    }
    @SneakyThrows
    public ProductPrice productPriceFromEntity(ProductPriceEntity productPrice) {
        return this.objectMapper.convertValue(productPrice, ProductPrice.class);
    }

    protected ProductPriceEntity productPriceToProductPriceEntity(ProductPrice productPrice) {
        return objectMapper.convertValue(productPrice, ProductPriceEntity.class);
    }
}
