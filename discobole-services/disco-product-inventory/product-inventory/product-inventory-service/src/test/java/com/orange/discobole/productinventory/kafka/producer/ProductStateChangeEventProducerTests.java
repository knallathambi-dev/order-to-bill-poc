// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.producer;


import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductStateChangeEventProducerImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.orange.discobole.productinventory.constant.TestConstant.STRING_SIZE;
import static com.orange.discobole.productinventory.constant.TestConstant.VALID_PRODUCT_SPECIFICATION_ID;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE;
import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForStateChangeEvent;
import static com.orange.discobole.productinventory.util.creator.ProductCreator.createProductBuilderWithProductSpecification;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;


class ProductStateChangeEventProducerTests extends AbstractTest {

    @Autowired
    private ProductStateChangeEventProducerImpl productStateChangeEventProducer;

    @Test
    void whenProductStateChangeEventProduced_thenProductStateChangeEventProducerIsCalled() {
        StateChangeProduct product = StateChangeProduct.fromProduct(createProductBuilderWithProductSpecification(ACTIVE, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).id(UUID.randomUUID().toString()).build(), CREATED);
        productStateChangeEventProducer.publishEvent(product, "SOME_TITLE", "SOME_DOMAIN");
        consumeAndAssertEqualityForStateChangeEvent(product);
    }

    @Test
    void whenProductStateChangeEventsProduced_thenProductStateChangeEventProducerIsCalledForEachProduct() {
        // Given
        Set<StateChangeProduct> products = new HashSet<>();
        int numProducts = 5; // Choose the desired number of products
        for (int i = 0; i < numProducts; i++) {
            products.add(StateChangeProduct.fromProduct(createProductBuilderWithProductSpecification(ACTIVE, randomAlphabetic(STRING_SIZE), randomAlphabetic(STRING_SIZE), VALID_PRODUCT_SPECIFICATION_ID).id(UUID.randomUUID().toString()).build(), CREATED));
        }

        // When
        productStateChangeEventProducer.publishEvents(products, "SOME_TITLE", "SOME_DOMAIN");

        consumeAndAssertEqualityForStateChangeEvent(products);
    }

}
