// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.controller.impl.ReportingApiImpl;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.mapper.ReportMapper;
import com.orange.discobole.productinventory.service.ReportService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows non-static @BeforeAll
public abstract class AbstractTest {
    protected final BiPredicate<ReportDetails, ProductStatusType> statusPredicate = (ReportDetails reportDetails, ProductStatusType status) -> reportDetails
            .getCharacteristic()
            .stream()
            .anyMatch(reportCharacteristic -> reportCharacteristic.getName().equals("status")
                    && reportCharacteristic.getValue().equals(status.getValue()));
    @InjectMocks
    protected ReportService reportService;
    @Spy
    @SuppressWarnings("unused")
    protected ReportMapper reportMapper;
    protected MongoTemplateMockUtil mockUtil = new MongoTemplateMockUtil();
    @Mock
    protected MongoTemplate mongoTemplate;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUtil.registerMockResponse();
        // Mock behavior for MongoTemplate.find
        when(mongoTemplate.find(any(Query.class), Mockito.any(Class.class)))
                .thenAnswer(invocation -> {
                    Query query = invocation.getArgument(0);
                    Class<?> clazz = invocation.getArgument(1);
                    return mockUtil.getMockFindResponse(query, clazz);
                });
        // Mock behavior for MongoTemplate.upsert
        when(mongoTemplate.upsert(any(Query.class), any(Update.class), Mockito.any(Class.class)))
                .thenAnswer(invocation -> {
                    Query query = invocation.getArgument(0);
                    Update update = invocation.getArgument(1);
                    Class<?> clazz = invocation.getArgument(2);
                    return mockUtil.getMockUpdateResponse(query, update, clazz);
                });
    }

    protected Report getReportByDate(LocalDate date) {
        List<Report> actualReports = reportService.getReports(ReportType.REPORTPRODUCTBYSTATUS, date, null, null, null, Collections.emptyList());
        assertFalse(actualReports.isEmpty());
        return actualReports.get(0);
    }

    protected Report getReportByDateAndOffer(LocalDate date, String offerId) {
        List<Report> actualReports = reportService.getReports(ReportType.REPORTPRODUCTBYOFFER, date, null, null, null, offerId);
        assertFalse(actualReports.isEmpty());
        return actualReports.get(0);
    }

    protected List<Report> getRandomReports(ReportType reportType) {
        List<Report> reports;

        switch (reportType) {
            case REPORTPRODUCTBYSTATUS -> {
                reports = mockUtil.getStatusReportEntities()
                        .parallelStream()
                        .limit(10)
                        .map(reportMapper::toDto)
                        .collect(Collectors.toList());
            }
            case REPORTPRODUCTBYOFFER -> {
                reports = mockUtil.getProductOfferReportEntities()
                        .parallelStream()
                        .limit(10)
                        .map(reportMapper::toDto)
                        .collect(Collectors.toList());
            }
            default -> {
                return new ArrayList<>();
            }
        }

        // Shuffle the reports and limit to 10
        Collections.shuffle(reports);
        return reports.stream().limit(10).collect(Collectors.toList());
    }

    protected Report getRandomReport(ReportType reportType) {
        Random rand = new Random();
        switch (reportType) {
            case REPORTPRODUCTBYSTATUS -> {
                return reportMapper.toDto(mockUtil.getStatusReportEntities().get(rand.nextInt(0, mockUtil.getStatusReportEntities().size())));
            }
            case REPORTPRODUCTBYOFFER -> {
                return reportMapper.toDto(mockUtil.getProductOfferReportEntities().get(rand.nextInt(0, mockUtil.getProductOfferReportEntities().size())));

            }
            default -> {
                return Report.builder().build();
            }
        }
    }
    protected void invokeListReports(ReportingApiImpl reportingApi,
                                     ReportType reportType,
                                     LocalDate collectDate,
                                     LocalDate collectDateLte,
                                     LocalDate collectDateGte
    ) {
        reportingApi.listReports(reportType, collectDate, collectDateLte, collectDateGte, null, Collections.emptyList());
    }

    protected void invokeListReports(ReportService reportService, ReportType reportType, LocalDate collectDate, LocalDate collectDateLte, LocalDate collectDateGte, ReportGranularity reportGranularity) {
        reportService.getReports(reportType, collectDate, collectDateLte, collectDateGte, reportGranularity, Collections.emptyList());
    }
}
