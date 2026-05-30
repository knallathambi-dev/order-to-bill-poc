// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.OrchestrationManagementApiImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.validators.OrchestrationPlanFieldValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.OrchestrationPlanResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.OrchestrationPlanValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.EnumMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        OrchestrationPlanMapperImpl.class,
        EnumMapperImpl.class
})
class OrchestrationManagementServiceImplTest {

    OrchestrationManagementServiceImpl orchestrationManagementService;

    @Autowired
    private OrchestrationPlanMapper orchestrationPlanMapper;

    @Mock
    private OrchestrationPlanFieldValidator orchestrationPlanFieldValidator;

    private MockMvc mockMvc;

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @Mock
    private MongoTemplateWrapperService mongoTemplateWrapperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orchestrationManagementService = new OrchestrationManagementServiceImpl(orchestrationPlanMapper, mongoTemplateWrapperService, orchestrationPlanFieldValidator);
        OrchestrationManagementApiImpl orchestrationManagementController = new OrchestrationManagementApiImpl(orchestrationManagementService, orchestrationPlanMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(orchestrationManagementController).build();
    }

    @Test
    void givenValidIdAndFields_whenGetOrchestrationPlanById_thenReturnOrchestrationPlan() {
        // Given
        String id = "64a58a9c52da86466322706e";
        String fields = "relatedParty,id";
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setId("231-mf4");
        relatedParty.setName("Abir");
        relatedParty.setRole("customer");
        List<RelatedParty> relatedParties = new ArrayList<>();
        relatedParties.add(relatedParty);
        OrchestrationPlan orchestrationPlan = new OrchestrationPlan();
        orchestrationPlan.setRelatedParty(relatedParties);
        orchestrationPlan.setId(id);
        Query expectedQuery = new Query();
        expectedQuery.addCriteria(Criteria.where("id").is(id));
        expectedQuery.fields().include("relatedParty").include("id");

        // When
        when(mongoTemplateWrapperService.findOne(any(), eq(OrchestrationPlan.class))).thenReturn(orchestrationPlan);
        OrchestrationPlan result = orchestrationManagementService.getOrchestrationPlanById(id, fields);

        // Then
        assertNotNull(result);
        assertEquals(orchestrationPlan.getId(), result.getId());
        assertEquals(orchestrationPlan.getState(), result.getState());
        verify(mongoTemplateWrapperService, times(1)).findOne(expectedQuery, OrchestrationPlan.class);
    }

    @Test
    void givenEmptyFieldsString_whenGetOrchestrationPlanById_thenInvalidQueryParamExceptionThrown() {
        // Given
        String id = "64a58a9c52da86466322706e";
        String fields = "";

        // When & Then
        assertThrows(OrchestrationPlanValidationException.class, () -> {
            orchestrationManagementService.getOrchestrationPlanById(id, fields);
        });
    }

    @Test
    void givenNonExistentId_whenGetOrchestrationPlanById_thenThrowOrchestrationPlanNotFoundException() {
        // Given
        String id = "nonexistent-id";
        String fields = "relatedParty";
        Query expectedQuery = new Query(Criteria.where("id").is(id));
        expectedQuery.fields().include("relatedParty").include("id");

        // When
        when(mongoTemplateWrapperService.findOne(expectedQuery, OrchestrationPlan.class)).thenReturn(null);

        // Then
        assertThrows(OrchestrationPlanNotFoundException.class, () -> orchestrationManagementService.getOrchestrationPlanById(id, fields));
    }

    @Test
    void givenValidFilter_whenGetOrchestrationPlans_thenReturnPlanResponse() {
        // Given
        List<OrchestrationPlan> orchestrationPlans = List.of(new OrchestrationPlan());
        when(mongoTemplateWrapperService.count(any(), eq(OrchestrationPlan.class))).thenReturn(1L);
        when(mongoTemplateWrapperService.find(any(), eq(OrchestrationPlan.class))).thenReturn(orchestrationPlans);

        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .offset(0)
                .limit(100).build();

        // When
        OrchestrationPlanResponse result = orchestrationManagementService.getOrchestrationPlans(orchestrationPlanFilter, null);

        // Then
        assertEquals(orchestrationPlanMapper.toDtoList(orchestrationPlans), result.getOrchestrationPlans());
    }

    @Test
    void givenDateRange_whenFilterByDateMatchesOne_returnValidList() throws Exception {
        // Given
        OrchestrationPlan orchestrationPlanOld = getOrchestrationPlanBuilder()
                .receivedDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC)).build();
        getOrchestrationPlanBuilder()
                .receivedDate(LocalDateTime.of(2023, Month.JULY, 21, 8, 16, 2).toInstant(ZoneOffset.UTC)).build();

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("receivedDateStart", "2023-06-01T12:00:00Z");
        param.add("receivedDateEnd", "2023-07-01T12:00:00Z");
        param.add("limit", "10");

        when(mongoTemplateWrapperService.find(any(), eq(OrchestrationPlan.class))).thenReturn(List.of(orchestrationPlanOld));
        when(mongoTemplateWrapperService.count(any(), eq(OrchestrationPlan.class))).thenReturn(1L);

        // When
        MvcResult mvcResult = mockMvc.perform(get("/orchestrationPlan")
                        .params(param)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        assertTrue(Objects.requireNonNull(mvcResult.getResponse().getHeader("X-Result-Count")).contains("1"));
        assertTrue(Objects.requireNonNull(mvcResult.getResponse().getHeader("X-Total-Count")).contains("1"));
    }

    @Test
    void givenDateRange_whenFilterByDateMatchesTwo_returnValidList() throws Exception {
        // Given
        OrchestrationPlan orchestrationPlanOld = getOrchestrationPlanBuilder()
                .receivedDate(LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC)).build();
        OrchestrationPlan orchestrationPlan = getOrchestrationPlanBuilder()
                .receivedDate(LocalDateTime.of(2023, Month.JULY, 21, 8, 16, 2).toInstant(ZoneOffset.UTC)).build();

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("receivedDateStart", "2023-06-01T12:00:00Z");
        param.add("receivedDateEnd", "2023-08-01T12:00:00Z");
        param.add("limit", "10");
        when(mongoTemplateWrapperService.find(any(), eq(OrchestrationPlan.class))).thenReturn(List.of(orchestrationPlanOld, orchestrationPlan));
        when(mongoTemplateWrapperService.count(any(), eq(OrchestrationPlan.class))).thenReturn(2L);

        // When
        MvcResult mvcResult = mockMvc.perform(get("/orchestrationPlan")
                .params(param).accept(MediaType.APPLICATION_JSON)).andReturn();

        // Then
        assertTrue(Objects.requireNonNull(mvcResult.getResponse().getHeader("X-Result-Count")).contains("2"));
        assertTrue(Objects.requireNonNull(mvcResult.getResponse().getHeader("X-Total-Count")).contains("2"));
    }

    @Test
    void givenInvalidFields_whenValidateSpaces_thenThrowPlanApiQueryParamException() {
        // Given
        String invalidFields = "field1 , field2";

        // When & Then
        assertThrows(PlanApiQueryParamException.class, () -> {
            ReflectionTestUtils.invokeMethod(
                    orchestrationManagementService,
                    "validateSpaces",
                    invalidFields
            );
        });
    }

    @Test
    void givenValidFields_whenValidateSpaces_thenNoException() {
        // Given
        String validFields = "field1,field2";

        // When & Then
        assertDoesNotThrow(() -> {
            ReflectionTestUtils.invokeMethod(
                    orchestrationManagementService,
                    "validateSpaces",
                    validFields
            );
        });
    }

    @Test
    void givenHttpStatusConditions_whenDetermineHttpStatus_thenReturnCorrectStatus() {
        // Given
        int size = 10;
        long totalCount = 10L;

        // When
        HttpStatus statusOK = ReflectionTestUtils.invokeMethod(
                orchestrationManagementService,
                "determineHttpStatus",
                size,
                totalCount
        );

        // Then
        assertEquals(HttpStatus.OK, statusOK);

        // When
        int partialSize = 5;
        HttpStatus statusPartial = ReflectionTestUtils.invokeMethod(
                orchestrationManagementService,
                "determineHttpStatus",
                partialSize,
                totalCount
        );

        // Then
        assertEquals(HttpStatus.PARTIAL_CONTENT, statusPartial);
    }

    @Test
    void givenOffset_whenCheckIfOffsetInvalid_thenThrowExceptionIfInvalid() {
        // Given
        long totalCount = 10L;
        int offset = 11;

        // When & Then
        assertThrows(PlanApiQueryParamException.class, () -> {
            ReflectionTestUtils.invokeMethod(
                    orchestrationManagementService,
                    "checkIfOffsetInvalid",
                    totalCount,
                    offset
            );
        });

        // Given
        int validOffset = 5;

        // When & Then
        assertDoesNotThrow(() -> {
            ReflectionTestUtils.invokeMethod(
                    orchestrationManagementService,
                    "checkIfOffsetInvalid",
                    totalCount,
                    validOffset
            );
        });
    }

    @Test
    void givenSorts_whenSetSortInQuery_thenQueryUpdated() {
        // Given
        Query query = new Query();
        List<String> sorts = Arrays.asList("field1", "-field2", "+field3");

        // When
        ReflectionTestUtils.invokeMethod(
                orchestrationManagementService,
                "setSortInQuery",
                query,
                sorts
        );

        // Then
        assertNotNull(query.getSortObject());
        assertThat(query.getSortObject()).containsEntry("field1", 1);
        assertThat(query.getSortObject()).containsEntry("field2", -1);
        assertThat(query.getSortObject()).containsEntry("field3", 1);
    }

    @Test
    void givenFieldNotExists_whenAddFieldIfNotExists_thenFieldAppended() {
        // Given
        String fieldsQueryParam = "field1,field2";
        String requiredField = "field3";
        StringBuilder stringBuilder = new StringBuilder(fieldsQueryParam);

        // When
        ReflectionTestUtils.invokeMethod(
                orchestrationManagementService,
                "addFieldIfNotExists",
                fieldsQueryParam,
                requiredField,
                stringBuilder
        );

        // Then
        assertTrue(stringBuilder.toString().contains("field3"));
    }

    @Test
    void givenFieldExists_whenAddFieldIfNotExists_thenNoFieldAppended() {
        // Given
        String fieldsQueryParam = "field1,field2";
        String requiredField = "field1";
        StringBuilder stringBuilder = new StringBuilder(fieldsQueryParam);

        // When
        ReflectionTestUtils.invokeMethod(
                orchestrationManagementService,
                "addFieldIfNotExists",
                fieldsQueryParam,
                requiredField,
                stringBuilder
        );

        // Then
        assertFalse(stringBuilder.toString().endsWith("," + requiredField));
    }

    @Test
    void givenFields_whenExtractFields_thenReturnFieldsIncludingRequired() {
        // Given
        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .fields("field1")
                .build();

        // When
        String[] fields = orchestrationManagementService.extractFields(orchestrationPlanFilter, "field2");

        // Then
        assertArrayEquals(new String[]{"field1", "field2"}, fields);
    }

    @Test
    void givenFieldsNone_whenExtractFields_thenReturnRequiredFields() {
        // Given
        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .fields("none")
                .build();

        // When
        String[] fields = orchestrationManagementService.extractFields(orchestrationPlanFilter, "field1");

        // Then
        assertArrayEquals(new String[]{"field1"}, fields);
    }

    @Test
    void givenFieldsNull_whenExtractFields_thenThrowPlanApiQueryParamException() {
        // Given
        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .fields("null")
                .build();

        // When & Then
        assertThrows(PlanApiQueryParamException.class, () -> {
            orchestrationManagementService.extractFields(orchestrationPlanFilter, "field1");
        });
    }

    @Test
    @SneakyThrows
    void givenFieldsToFetch_whenValidateFieldsToFetch_thenNoException() throws NoSuchFieldException {
        // Given
        String[] fields = {"field1", "field2"};
        when(orchestrationPlanFieldValidator.validate(anyString())).thenReturn(OrchestrationPlan.class.getDeclaredField("id"));

        // When & Then
        assertDoesNotThrow(() -> {
            orchestrationManagementService.validateFieldsToFetch(orchestrationPlanFieldValidator, fields);
        });
        verify(orchestrationPlanFieldValidator, times(2)).validate(anyString());
    }

    // Helper method for creating an OrchestrationPlan instance
    private OrchestrationPlan.OrchestrationPlanBuilder getOrchestrationPlanBuilder() {
        return OrchestrationPlan.builder().id(UUID.randomUUID().toString());
    }
}
