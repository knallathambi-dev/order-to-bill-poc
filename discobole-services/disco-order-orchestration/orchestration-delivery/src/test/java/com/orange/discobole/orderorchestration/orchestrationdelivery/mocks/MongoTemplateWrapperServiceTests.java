// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mocks;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MongoTemplateWrapperServiceTests {

    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    MongoTemplateWrapperService mongoTemplateWrapperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOneException() {
        when(mongoTemplate.findOne(any(), any())).thenThrow(RuntimeException.class);
        Assertions.assertThrowsExactly(CoodDBException.class, () -> mongoTemplateWrapperService.findOne(any(), any()));
    }

    @Test
    void testFindException() {
        when(mongoTemplate.find(any(), any())).thenThrow(RuntimeException.class);
        Assertions.assertThrowsExactly(CoodDBException.class, () -> mongoTemplateWrapperService.find(any(), any()));
    }

    @Test
    void testCountException() {
        Class aClass = any(Class.class);
        when(mongoTemplate.count(any(), aClass)).thenThrow(RuntimeException.class);
        Assertions.assertThrowsExactly(CoodDBException.class, () -> mongoTemplateWrapperService.count(any(), aClass));
    }
}
