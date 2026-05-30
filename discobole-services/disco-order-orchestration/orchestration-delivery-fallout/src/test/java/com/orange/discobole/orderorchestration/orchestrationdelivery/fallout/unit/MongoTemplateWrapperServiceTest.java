// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.MongoTemplateWrapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoTemplateWrapperServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private FalloutRepository falloutRepository;

    private MongoTemplateWrapperService mongoTemplateWrapperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mongoTemplateWrapperService = new MongoTemplateWrapperService(mongoTemplate, falloutRepository);
    }

    @Test
    void givenValidQueryAndEntityClass_whenFindOne_thenReturnsExpectedEntity() throws FalloutDBException {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        FalloutIncident expectedEntity = new FalloutIncident();
        when(mongoTemplate.findOne(query, entityClass)).thenReturn(expectedEntity);

        // When
        FalloutIncident result = mongoTemplateWrapperService.findOne(query, entityClass);

        // Then
        assertEquals(expectedEntity, result);
        verify(mongoTemplate, times(1)).findOne(query, entityClass);
    }

    @Test
    void givenExceptionThrown_whenFindOne_thenThrowsFalloutDBException() {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        when(mongoTemplate.findOne(query, entityClass)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(FalloutDBException.class, () -> mongoTemplateWrapperService.findOne(query, entityClass));
    }

    @Test
    void givenValidQueryAndEntityClass_whenCount_thenReturnsExpectedCount() throws FalloutDBException {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        long expectedCount = 5;
        when(mongoTemplate.count(query, entityClass)).thenReturn(expectedCount);

        // When
        long result = mongoTemplateWrapperService.count(query, entityClass);

        // Then
        assertEquals(expectedCount, result);
        verify(mongoTemplate, times(1)).count(query, entityClass);
    }

    @Test
    void givenExceptionThrown_whenCount_thenThrowsFalloutDBException() {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        when(mongoTemplate.count(query, entityClass)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(FalloutDBException.class, () -> mongoTemplateWrapperService.count(query, entityClass));
    }

    @Test
    void givenValidQueryAndEntityClass_whenFind_thenReturnsExpectedEntities() throws FalloutDBException {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        List<FalloutIncident> expectedEntities = new ArrayList<>();
        when(mongoTemplate.find(query, entityClass)).thenReturn(expectedEntities);

        // When
        List<FalloutIncident> result = mongoTemplateWrapperService.find(query, entityClass);

        // Then
        assertEquals(expectedEntities, result);
        verify(mongoTemplate, times(1)).find(query, entityClass);
    }

    @Test
    void givenExceptionThrown_whenFind_thenThrowsFalloutDBException() {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        when(mongoTemplate.find(query, entityClass)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(FalloutDBException.class, () -> mongoTemplateWrapperService.find(query, entityClass));
    }

    @Test
    void givenValidQueryAndEntityClass_whenDelete_thenNoExceptionThrown() throws FalloutDBException {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;

        // When & Then
        assertDoesNotThrow(() -> mongoTemplateWrapperService.delete(query, entityClass));
        verify(mongoTemplate, times(1)).remove(query, entityClass);
    }

    @Test
    void givenExceptionThrown_whenDelete_thenThrowsFalloutDBException() {
        // Given
        Query query = new Query();
        Class<FalloutIncident> entityClass = FalloutIncident.class;
        doThrow(new RuntimeException("Database error")).when(mongoTemplate).remove(query, entityClass);

        // When & Then
        assertThrows(FalloutDBException.class, () -> mongoTemplateWrapperService.delete(query, entityClass));
    }

    @Test
    void givenValidEntity_whenSave_thenReturnsSavedEntity() throws FalloutDBException {
        // Given
        FalloutIncident entity = new FalloutIncident();
        FalloutIncident expectedEntity = new FalloutIncident();
        when(falloutRepository.save(entity)).thenReturn(expectedEntity);

        // When
        FalloutIncident result = mongoTemplateWrapperService.save(entity);

        // Then
        assertEquals(expectedEntity, result);
        verify(falloutRepository, times(1)).save(entity);
    }

    @Test
    void givenExceptionThrown_whenSave_thenThrowsFalloutDBException() {
        // Given
        FalloutIncident entity = new FalloutIncident();
        when(falloutRepository.save(entity)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(FalloutDBException.class, () -> mongoTemplateWrapperService.save(entity));
    }
}
