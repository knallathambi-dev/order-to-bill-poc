// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecDuplicatedEvent;

public class ServiceSpecDuplicatedEventTest extends ProductSpecificationApplicationTests {

    @Test
    void getServiceSpecificationTest() {
        ServiceSpecification serviceSpecification=new ServiceSpecification();
        ServiceSpecDuplicatedEvent serviceSpecDuplicatedEvent=new ServiceSpecDuplicatedEvent(serviceSpecification);
        ServiceSpecification serviceSpecification1 = serviceSpecDuplicatedEvent.getServiceSpecification();
        Assertions.assertNotNull(serviceSpecification1);
        Assertions.assertEquals(serviceSpecification.hashCode(),serviceSpecification1.hashCode());
        Assertions.assertEquals(serviceSpecification,serviceSpecification1);
    }
}