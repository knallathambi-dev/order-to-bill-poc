// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.model.ProductOfferEntity;
import com.orange.discobole.productinventory.model.ProductOfferReportEntity;
import com.orange.discobole.productinventory.model.StatusReportEntity;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReportingScheduledTasksServiceTest {
    @Mock
    protected MongoTemplate mongoTemplate;
    @InjectMocks
    private ReportingScheduledTasksService reportingScheduledTasksService;
    @Mock
    private ReportCronRunStateService reportCronRunStateService;
    @Mock
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformDailyReport_whenTaskNotRun_shouldMarkAndImport() {
        LocalDate today = LocalDate.now();

        // Mocking
        when(reportCronRunStateService.hasRunForDate(today)).thenReturn(false);
        when(transactionTemplate.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null); // Simulate transaction execution
                });

        // Act
        reportingScheduledTasksService.performDailyReport();

        // Verify
        verify(reportCronRunStateService).hasRunForDate(today);
        verify(reportCronRunStateService).markAsRunForDate(today);
        verify(mongoTemplate, atLeastOnce()).upsert(any(Query.class), any(Update.class), eq(StatusReportEntity.class));
    }

    @Test
    void testPerformDailyReport_whenTaskAlreadyRun_shouldNotMarkAndImport() {
        LocalDate today = LocalDate.now();

        // Mocking
        when(reportCronRunStateService.hasRunForDate(today)).thenReturn(true);
        when(transactionTemplate.execute(any(TransactionCallback.class)))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null); // Simulate transaction execution
                });
        // Act
        reportingScheduledTasksService.performDailyReport();

        // Verify
        verify(reportCronRunStateService).hasRunForDate(today);
        verify(reportCronRunStateService, never()).markAsRunForDate(any());
        verify(mongoTemplate, never()).upsert(any(Query.class), any(Update.class), eq(StatusReportEntity.class));
    }

    @Test
    void testFindFirstStatusReportByOrderByDateDesc_shouldReturnResult() {
        // Mocking
        StatusReportEntity mockEntity = new StatusReportEntity();
        when(mongoTemplate.findOne(any(Query.class), eq(StatusReportEntity.class))).thenReturn(mockEntity);

        // Act
        Optional<StatusReportEntity> result = reportingScheduledTasksService.findFirstStatusReportByOrderByDateDesc();

        // Assert
        verify(mongoTemplate).findOne(any(Query.class), eq(StatusReportEntity.class));
        assert result.isPresent();
        assert result.get() == mockEntity;
    }

    @Test
    void testFindFirstStatusReportByOrderByDateDesc_shouldReturnEmpty() {
        // Mocking
        when(mongoTemplate.findOne(any(Query.class), eq(StatusReportEntity.class))).thenReturn(null);

        // Act
        Optional<StatusReportEntity> result = reportingScheduledTasksService.findFirstStatusReportByOrderByDateDesc();

        // Assert
        verify(mongoTemplate).findOne(any(Query.class), eq(StatusReportEntity.class));
        assert result.isEmpty();
    }

    @Test
    void testCreateAndSaveReport_shouldUpsertData() {
        // Mocking
        LocalDate date = LocalDate.now();
        Query mockQuery = new Query();
        Update mockUpdate = new Update();

        // Act
        reportingScheduledTasksService.createAndSaveReport(mockUpdate, mockQuery, date, StatusReportEntity.class);

        // Verify
        verify(mongoTemplate).upsert(mockQuery, mockUpdate, StatusReportEntity.class);
    }

    @Test
    void testImportStatusReportData_whenNoExistingReport_shouldCreateEmptyUpdate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Mocking
        when(mongoTemplate.findOne(any(Query.class), eq(StatusReportEntity.class))).thenReturn(null);

        // Act
        invokeImportStatusReportData(yesterday, today);
        // Verify
        verify(mongoTemplate).upsert(any(Query.class), any(Update.class), eq(StatusReportEntity.class));
    }

    private void invokeImportStatusReportData(LocalDate yesterday, LocalDate today) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = ReportingScheduledTasksService.class.getDeclaredMethod("importStatusReportData", LocalDate.class, LocalDate.class);
        method.setAccessible(true);
        method.invoke(reportingScheduledTasksService, yesterday, today);
    }

    private void invokeImportProductOfferReportData(LocalDate yesterday, LocalDate today) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = ReportingScheduledTasksService.class.getDeclaredMethod("importProductOfferReportData", LocalDate.class, LocalDate.class);
        method.setAccessible(true);
        method.invoke(reportingScheduledTasksService, yesterday, today);
    }

    @Test
    void testImportProductOfferReportData_shouldProcessEachOffer() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Mocking
        String mockId = ObjectId.get().toHexString();
        ProductOfferReportEntity mockEntity = new ProductOfferReportEntity();
        mockEntity.setDate(yesterday.minusDays(1));

        when(mongoTemplate.findDistinct(any(), eq(ProductOfferEntity.class), eq(String.class)))
                .thenReturn(List.of(mockId));
        when(mongoTemplate.findOne(any(Query.class), eq(ProductOfferReportEntity.class)))
                .thenReturn(mockEntity);

        // Act
        invokeImportProductOfferReportData(yesterday, today);

        // Verify
        verify(mongoTemplate, atLeastOnce()).upsert(any(Query.class), any(Update.class), eq(ProductOfferReportEntity.class));
    }

}
