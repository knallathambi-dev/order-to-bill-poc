// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;
// package com.orange.bos.productoffering.event.productoffering;

// import java.time.OffsetDateTime;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.orange.bos.productoffering.ProductOfferingApplicationTests;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingType;

// public class ProductOfferingTypeSelectedEventTest extends ProductOfferingApplicationTests {

//     private static final Logger LOGGER = LogManager.getLogger(ProductOfferingTypeSelectedEventTest.class);
    
//     private ProductOfferingTypeSelectedEvent productOfferingTypeSelectedEvent;

//     @BeforeEach
//     void setUp() {
//     	String productOffId = "PROD1";
//         OffsetDateTime lastUpdate = OffsetDateTime.now();
//         productOfferingTypeSelectedEvent = new ProductOfferingTypeSelectedEvent(productOffId, ProductOfferingLifecycle.INSTUDY, lastUpdate, ProductOfferingType.BUNDLEPRODUCTOFFERING);
//     }
    
//     @Test
//     void producOfferingTypeSelectedTest() {
//     	Assertions.assertAll("productOfferingTypeSelectedEvent",
//                 () -> Assertions.assertEquals(productOfferingTypeSelectedEvent.getProductOfferingId(), "PROD1"));
//         LOGGER.info("ProductOfferingTypeSelectedEvent: {}", productOfferingTypeSelectedEvent);
//     }
// }
