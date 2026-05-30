// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.mapper.ProductMapperImpl;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.RelatedPartyEntity;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.util.DoubleComparator;
import org.assertj.core.util.FloatComparator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.HREF_FIELD;
import static com.orange.discobole.productinventory.constant.Constant.PRODUCT_ORDER_HREF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Component
public class ProductAssertionUtil {


    private static final RecursiveComparisonConfiguration recursiveComparisonConfiguration;
    private static final ProductMapper productMapper = new ProductMapperImpl();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    protected MongoTemplate mongoTemplate;

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new JavaTimeModule());
        recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
                .withComparatorForType(new FloatComparator(0.001f), Float.class)
                .withComparatorForType(new DoubleComparator(0.001d), Double.class)
                .withComparatorForType(new LocalDateTimeComparator(), LocalDateTime.class)
                .withComparatorForType(new OffsetDateTimeComparator(), OffsetDateTime.class)
                .withComparatorForType(new ListComparator(), List.class)
                .build();
        productMapper.setObjectMapper(objectMapper);
    }

    public void assertListProductDtoEqualsToListProductEntity(List<Product> products, List<ProductEntity> productEntities, boolean compareRelationRecursively) {
        assertThat(products).hasSameSizeAs(productEntities);
        if (!CollectionUtils.isEmpty(products)) {
            for (int i = 0; i < products.size(); i++) {
                assertProductDtoEqualsToProductEntity(products.get(i), productEntities.get(i), compareRelationRecursively);
            }
        }
    }

    public static void assertProductExpectedStatus(ProductEntity p, ProductStatusType terminated, ProductOperationalStatusType terminated1) {
        assert p != null;
        assertEquals(terminated, p.getStatus());
        assertEquals(terminated1, p.getOperationalStatus());
    }

    public static void assertProductEqualityOnIdAndAtType(Product product, Product product1) {
        String[] unSkippedFields = {Product.Fields.id, Product.Fields.atType};
        assertThat(product1).usingRecursiveComparison()
                .comparingOnlyFields(unSkippedFields)
                .isEqualTo(product);
    }

    public static String[] mergeArrays(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        String[] mergedArray = new String[length1 + length2];

        // Copy elements from array1 to mergedArray
        System.arraycopy(array1, 0, mergedArray, 0, length1);

        // Copy elements from array2 to mergedArray
        System.arraycopy(array2, 0, mergedArray, length1, length2);

        return mergedArray;
    }

    public void assertProductDtoEqualsToProductEntity(Product product, ProductEntity productEntity, boolean compareRelationRecursively, String... additionalSkippedFields) {
        String[] skippedFields = {
                HREF_FIELD,
                Product.Fields.productRelationship,
                PRODUCT_ORDER_HREF,
                Product.Fields.operationalStatusChange,
                Product.Fields.statusChange,
                Product.Fields.relatedParty,
        };

        assertThat(product).usingRecursiveComparison(recursiveComparisonConfiguration)
                .ignoringFields(mergeArrays(skippedFields, additionalSkippedFields))
                .isEqualTo(productMapper.toDtoWithFullMapping(productEntity));

        assertProductRelationshipEquality(product, productEntity, compareRelationRecursively);
        assertRelatedPartyEquality(product, productEntity);
    }

    private void assertProductRelationshipEquality(Product product, ProductEntity productEntity, boolean compareRelationRecursively) {
        assertThat(
                CollectionUtils.isEmpty(product.getProductRelationship())).isEqualTo(CollectionUtils.isEmpty(productEntity.getProductRelationship())
        );
        if (!CollectionUtils.isEmpty(product.getProductRelationship())) {
            List<ProductRelationship> productRelationships = product.getProductRelationship();
            List<ProductRelationshipEntity> productRelationshipsEntity = productEntity.getProductRelationship();
            for (int i = 0; i < productRelationships.size(); i++) {
                assertThat(productRelationships.get(i).getRelationshipType()).isEqualTo(productRelationshipsEntity.get(i).getRelationshipType());
                if (compareRelationRecursively && productRelationships.get(i).getProduct() instanceof Product p) {
                    assertProductDtoEqualsToProductEntity(p, mongoTemplate.findById(productRelationshipsEntity.get(i).getProduct().getId(), ProductEntity.class),
                            compareRelationRecursively);
                } else if (productRelationships.get(i).getProduct() instanceof ProductRef p) {
                    assertThat(new ObjectId(p.getId())).isEqualTo(productRelationshipsEntity.get(i).getProduct().getId());
                } else if (productRelationships.get(i).getProduct() instanceof Product p) {
                    assertThat(new ObjectId(p.getId())).isEqualTo(productRelationshipsEntity.get(i).getProduct().getId());
                }
            }
        }
    }

    private void assertRelatedPartyEquality(Product product, ProductEntity productEntity) {
        assertThat(
                CollectionUtils.isEmpty(product.getRelatedParty())).isEqualTo(CollectionUtils.isEmpty(productEntity.getRelatedParty())
        );
        if (!CollectionUtils.isEmpty(product.getRelatedParty())) {
            List<RelatedPartyOrPartyRole> relatedParties = product.getRelatedParty();
            List<RelatedPartyEntity> relatedPartyEntities = productEntity.getRelatedParty();
            for (int i = 0; i < relatedParties.size(); i++) {
                assertThat(relatedParties.get(i).getRole()).isEqualTo(relatedPartyEntities.get(i).getRole());
                if (relatedParties.get(i).getPartyOrPartyRole() != null) {
                    if (relatedParties.get(i).getPartyOrPartyRole() instanceof PartyRef partyRef) {
                        assertThat(partyRef.getAtType()).isEqualTo(relatedPartyEntities.get(i).getAtType());
                        assertThat(partyRef.getAtReferredType()).isEqualTo(relatedPartyEntities.get(i).getAtReferredType());
                        assertThat(partyRef.getName()).isEqualTo(relatedPartyEntities.get(i).getName());
                    } else if (relatedParties.get(i).getPartyOrPartyRole() instanceof PartyRoleRef partyRoleRef) {
                        assertThat(partyRoleRef.getAtType()).isEqualTo(relatedPartyEntities.get(i).getAtType());
                        assertThat(partyRoleRef.getAtReferredType()).isEqualTo(relatedPartyEntities.get(i).getAtReferredType());
                        assertThat(partyRoleRef.getName()).isEqualTo(relatedPartyEntities.get(i).getName());
                        assertThat(partyRoleRef.getPartyId()).isEqualTo(relatedPartyEntities.get(i).getPartyId());
                        assertThat(partyRoleRef.getPartyName()).isEqualTo(relatedPartyEntities.get(i).getPartyName());
                    }
                } else {
                    assertNull(relatedPartyEntities.get(i).getAtType());
                    assertNull(relatedPartyEntities.get(i).getAtReferredType());
                    assertNull(relatedPartyEntities.get(i).getName());
                    assertNull(relatedPartyEntities.get(i).getPartyId());
                    assertNull(relatedPartyEntities.get(i).getPartyName());
                }

            }
        }
    }
    static class LocalDateTimeComparator implements Comparator<LocalDateTime> {
        @Override
        public int compare(LocalDateTime dt1, LocalDateTime dt2) {
            if (dt1 == dt2) {
                return 0;
            }
            if (dt1 == null) {
                return -1;
            }
            if (dt2 == null) {
                return 1;
            }
            return dt1.truncatedTo(java.time.temporal.ChronoUnit.MINUTES).compareTo(dt2.truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
    }

    static class OffsetDateTimeComparator implements Comparator<OffsetDateTime> {
        @Override
        public int compare(OffsetDateTime dt1, OffsetDateTime dt2) {
            if (dt1 == dt2) {
                return 0;
            }
            if (dt1 == null) {
                return -1;
            }
            if (dt2 == null) {
                return 1;
            }
            return dt1.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(java.time.temporal.ChronoUnit.MINUTES).compareTo(dt2.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
    }

    static class ListComparator implements Comparator<List> {
        @Override
        public int compare(List dt1, List dt2) {
            if (!CollectionUtils.isEmpty(dt1) && !CollectionUtils.isEmpty(dt2)) {
                if (dt1.size() != dt2.size()) {
                    return -1;
                }
                for (int i = 0; i < dt1.size(); i++) {
                    try {
                        assertThat(dt1.get(i)).usingRecursiveComparison(recursiveComparisonConfiguration)
                                .ignoringFields(HREF_FIELD, Product.Fields.productRelationship)
                                .isEqualTo(dt2.get(i));
                        return 0;
                    } catch (AssertionError assertionError) {
                        return -1;
                    }
                }
            }
            return CollectionUtils.isEmpty(dt1) && CollectionUtils.isEmpty(dt2) ? 0 : -1;
        }
    }

}

