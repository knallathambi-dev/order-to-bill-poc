// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.orange.discobole.productinventory.annotation.IntegrationTest;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.PriceAlterationEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductPriceEntity;
import io.mongock.test.springboot.junit5.MongockSpringbootJUnit5IntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static com.orange.discobole.productinventory.dto.v1.ProductRelationshipType.ROOTPRODUCT;
import static com.orange.discobole.productinventory.enumerate.ProductTypeEnum.PRODUCT;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Mostafa Saied
 */

@Slf4j
@IntegrationTest
class MongockIntegrationTest extends MongockSpringbootJUnit5IntegrationTestBase {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * This explicit call for mongockAfterEach() is some kind of hack,
     * to make sure to clear migration history from db that may be added by regular non-mongock integration tests.
     * Normally this method is called after every mongock integration test automatically.
     */

    @AfterEach
    void clearMigrationHistory() {
        mongoTemplate.remove(new Query(), "products");
        mongockAfterEach();
    }

    @Test
    void shouldSucceedFully() {
        executeMongock();
        Assertions.assertTrue(true);
    }

    @Test
    void addingAtType_shouldSucceedFully_atTypeExists() {
        String expectedProductId = new ObjectId().toHexString();

        ProductEntity expectedProductEntity = createProductSpecificationEntityBuilder(ProductStatusType.CREATED,
                randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10),
                false, null)
                .description(randomAlphabetic(10))
                .id(expectedProductId)
                .build();

        ProductEntity savedProduct = mongoTemplate.save(expectedProductEntity);

        assertThat(savedProduct.getAtType()).isNull();

        executeMongock();

        ProductEntity productEntity = mongoTemplate.findOne(new Query(), ProductEntity.class);

        assertThat(productEntity).isNotNull();
        assertThat(productEntity.getAtType()).isEqualTo(PRODUCT.getValue());
    }


    @Test
    void updateRelationShipTypeRootProduct_shouldSucceedFully_ParentContractLowercase() {
        ProductEntity product = createProductEntityBuilderWithRelationShipRootProduct(ProductStatusType.CREATED, randomAlphabetic(10), randomAlphabetic(10), randomAlphabetic(10), false, ObjectId.get().toString(), PRODUCT.getValue()).description(randomAlphabetic(10)).id(randomAlphabetic(10)).build();
        product.setId(ObjectId.get().toString());
        ProductEntity savedProduct = mongoTemplate.save(product);
        executeMongock();
        ProductEntity productEntity = mongoTemplate.findById(savedProduct.getId(), ProductEntity.class);
        assertThat(productEntity).isNotNull();
        assertThat(productEntity.getProductRelationship().get(0).getRelationshipType()).isEqualTo(ROOTPRODUCT.getValue());
    }

    @Test
    void shouldMigratePrimitiveApplicationDurationInProductPriceToMeasuredValue() {
        // GIVEN
        ProductEntity product = createProductEntityBuilderWithRelationShipRootProduct(
                ProductStatusType.CREATED,
                randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10),
                false,
                ObjectId.get().toString(),
                PRODUCT.getValue()
        )
                .description(randomAlphabetic(10))
                .id(ObjectId.get().toString()) // use real ObjectId
                .productPrice(List.of(
                        ProductPriceEntity.builder()
                                .name(randomAlphabetic(STRING_SIZE))
                                .description(randomAlphabetic(STRING_SIZE))
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .build()
                ))
                .build();

        // Save it normally
        ProductEntity savedProduct = mongoTemplate.save(product);

        // Simulate legacy DB state: overwrite applicationDuration with primitive value
        mongoTemplate.getCollection("products").updateOne(
                Filters.eq("_id", new ObjectId(savedProduct.getId())),
                Updates.set("productPrice.0.applicationDuration", 3) // simulate old format
        );

        // WHEN: run the migration
        executeMongock();

        // THEN: verify the applicationDuration is now a MeasuredValue
        ProductEntity updatedProduct = mongoTemplate.findById(savedProduct.getId(), ProductEntity.class);

        assertThat(updatedProduct).isNotNull();
        ProductPriceEntity price = updatedProduct.getProductPrice().get(0);
        assertThat(price.getApplicationDuration()).isNotNull();
        assertThat(price.getApplicationDuration().getAmount()).isEqualTo(3.0f);
        // assertThat(price.getApplicationDuration().getUnits()).isEqualTo("month"); // can't test it because there's a migration that removes the recurring charge that runs before this one.
    }

    @Test
    void shouldMigratePrimitiveApplicationInPriceAlterationDurationToMeasuredValue() {
        // GIVEN
        ProductEntity product = createProductEntityBuilderWithRelationShipRootProduct(
                ProductStatusType.CREATED,
                randomAlphabetic(10),
                randomAlphabetic(10),
                randomAlphabetic(10),
                false,
                ObjectId.get().toString(),
                PRODUCT.getValue()
        )
                .description(randomAlphabetic(10))
                .id(ObjectId.get().toString()) // use real ObjectId
                .productPrice(List.of(
                        ProductPriceEntity.builder()
                                .name(randomAlphabetic(STRING_SIZE))
                                .description(randomAlphabetic(STRING_SIZE))
                                .priceType(randomAlphabetic(STRING_SIZE))
                                .productPriceAlteration(List.of(PriceAlterationEntity.builder().build()))
                                .build()
                ))
                .build();

        // Save it normally
        ProductEntity savedProduct = mongoTemplate.save(product);

        // Simulate legacy DB state: overwrite applicationDuration with primitive value
        mongoTemplate.getCollection("products").updateOne(
                Filters.eq("_id", new ObjectId(savedProduct.getId())),
                Updates.set("productPrice.0.productPriceAlteration.0.applicationDuration", 3) // simulate old format
        );

        // WHEN: run the migration
        executeMongock();

        // THEN: verify the applicationDuration is now a MeasuredValue
        ProductEntity updatedProduct = mongoTemplate.findById(savedProduct.getId(), ProductEntity.class);

        assertThat(updatedProduct).isNotNull();
        ProductPriceEntity price = updatedProduct.getProductPrice().get(0);
        assertThat(price.getProductPriceAlteration().get(0).getApplicationDuration()).isNotNull();
        assertThat(price.getProductPriceAlteration().get(0).getApplicationDuration().getAmount()).isEqualTo(3.0f);
    }
}
