// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.producer;


import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductAttributeValueChangeEventProducerImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.orange.discobole.productinventory.constant.TestConstant.STRING_SIZE;
import static com.orange.discobole.productinventory.constant.TestConstant.VALID_PRODUCT_SPECIFICATION_ID;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForAttributeChangeEvent;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createProductBuilderWithProductSpecification;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;



public class ProductAttributeValueChangeEventProducerTests extends AbstractTest {

    @Autowired
    private ProductAttributeValueChangeEventProducerImpl productAttributeValueChangeEventProducer;

    @Test
    void whenProductAttributeValueChangeEventProduced_thenProductAttributeValueChangeEventProducerIsCalled() {
        Product product = createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).id(UUID.randomUUID().toString()).build();
        productAttributeValueChangeEventProducer.publishEvent(product, "SOME_TITLE", "SOME_DOMAIN");
        // Consume messages
        consumeAndAssertEqualityForAttributeChangeEvent(product);
    }

    @Test
    void whenProductAttributeValueChangeEventsProduced_thenProductAttributeValueChangeEventProducerIsCalledForEachProduct() {
        // Given
        Set<Product> products = new HashSet<>();
        int numProducts = 5; // Choose the desired number of products
        for (int i = 0; i < numProducts; i++) {
            products.add(
                    createProductBuilderWithProductSpecification(CREATED, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).id(UUID.randomUUID().toString()).build()
            );
        }

        // When
        productAttributeValueChangeEventProducer.publishEvents(products, "SOME_TITLE", "SOME_DOMAIN");
        consumeAndAssertEqualityForAttributeChangeEvent(products);
    }

}
