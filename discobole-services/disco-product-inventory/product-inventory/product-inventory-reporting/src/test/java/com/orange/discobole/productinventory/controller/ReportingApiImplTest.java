// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.orange.discobole.productinventory.controller.impl.ReportingApiImpl;
import com.orange.discobole.productinventory.dto.v1.ProductOfferingRef;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.dto.v1.ReportGranularity;
import com.orange.discobole.productinventory.dto.v1.ReportType;
import com.orange.discobole.productinventory.exception.ReportingException;
import com.orange.discobole.productinventory.service.ReportService;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.util.MockUtil.getProductOffers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class ReportingApiImplTest extends AbstractTest {
    @Mock
    protected ReportService reportService;
    @InjectMocks
    private ReportingApiImpl reportingApi;

    @Test
    void whenAllDateParamsAreNull_throwException() {
        ReportType reportType = ReportType.REPORTPRODUCTBYSTATUS;
        doThrow(new ReportingException(
                HttpStatus.BAD_REQUEST,
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                AT_LEAST_ONE_OF_COLLECT_DATE_COLLECT_DATE_LTE_OR_COLLECT_DATE_GTE_MUST_BE_PROVIDED
        )).when(reportService).getReports(
                reportType,
                null,
                null,
                null,
                null,
                Collections.emptyList()
        );
        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportingApi, reportType, null, null, null));

        assertEquals(AT_LEAST_ONE_OF_COLLECT_DATE_COLLECT_DATE_LTE_OR_COLLECT_DATE_GTE_MUST_BE_PROVIDED, exception.getExceptionResponse().getMessage());
    }

    @Test
    void whenCollectDateAndDateRangeProvided_throwException() {
        LocalDate collectDate = LocalDate.now();
        LocalDate collectDateLte = LocalDate.now().minusDays(1);
        ReportType reportType = ReportType.REPORTPRODUCTBYSTATUS;

        doThrow(new ReportingException(
                HttpStatus.BAD_REQUEST,
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                SPECIFY_EITHER_COLLECT_DATE_OR_COLLECT_DATE_LTE_COLLECT_DATE_GTE_NOT_BOTH
        )).when(reportService).getReports(
                reportType,
                collectDate,
                collectDateLte,
                null,
                null,
                Collections.emptyList()
        );
        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportingApi, reportType, collectDate, collectDateLte, null));

        assertEquals(SPECIFY_EITHER_COLLECT_DATE_OR_COLLECT_DATE_LTE_COLLECT_DATE_GTE_NOT_BOTH, exception.getExceptionResponse().getMessage());
    }

    @Test
    void whenCollectDateLteIsBeforeCollectDateGte_throwException() {
        LocalDate collectDateLte = LocalDate.now().minusDays(2);
        LocalDate collectDateGte = LocalDate.now().plusDays(5);
        ReportType reportType = ReportType.REPORTPRODUCTBYSTATUS;

        doThrow(new ReportingException(
                HttpStatus.BAD_REQUEST,
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                COLLECT_DATE_GTE_MUST_BE_AFTER_COLLECT_DATE_LTE
        )).when(reportService).getReports(
                reportType,
                null,
                collectDateLte,
                collectDateGte,
                null,
                Collections.emptyList()
        );
        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportingApi, reportType, null, collectDateLte, collectDateGte));

        assertEquals(COLLECT_DATE_GTE_MUST_BE_AFTER_COLLECT_DATE_LTE, exception.getExceptionResponse().getMessage());
    }


    @Test
    void whenValidFiltersProvided_returnCorrectStatusReports() {
        // Arrange
        LocalDate collectDateGte = LocalDate.now().minusDays(5);
        LocalDate collectDateLte = LocalDate.now();
        ReportGranularity granularity = ReportGranularity.DAY;
        ReportType atType = ReportType.REPORTPRODUCTBYSTATUS;
        List<String> productOfferId = Collections.emptyList();
        doReturn(getRandomReports(atType)).when(reportService).getReports(
                atType,
                null,
                collectDateLte,
                collectDateGte,
                granularity,
                productOfferId
        );
        // Act
        ResponseEntity<List<Report>> listResponseEntity = reportingApi.listReports(atType, null, collectDateLte, collectDateGte, granularity, productOfferId);
        assertTrue(listResponseEntity.getStatusCode().is2xxSuccessful());
        List<Report> actualReports = listResponseEntity.getBody();
        // Assert
        assertNotNull(actualReports);
        assertFalse(actualReports.isEmpty(), "The report list should not be empty");
        assertEquals(ReportType.REPORTPRODUCTBYSTATUS.getValue(), actualReports.get(0).getAtType(), String.format("The reports type should be %s", ReportType.REPORTPRODUCTBYSTATUS.getValue()));

    }

    @Test
    void whenValidFiltersProvided_returnCorrectOfferReports() {
        // Arrange
        LocalDate collectDateGte = LocalDate.now().minusDays(5);
        LocalDate collectDateLte = LocalDate.now();
        ReportGranularity granularity = ReportGranularity.DAY;
        ReportType atType = ReportType.REPORTPRODUCTBYOFFER;
        List<String> productOfferId = Collections.emptyList();
        doReturn(getRandomReports(atType)).when(reportService).getReports(
                atType,
                null,
                collectDateLte,
                collectDateGte,
                granularity,
                productOfferId
        );
        // Act
        ResponseEntity<List<Report>> listResponseEntity = reportingApi.listReports(atType, null, collectDateLte, collectDateGte, granularity, productOfferId);
        assertTrue(listResponseEntity.getStatusCode().is2xxSuccessful());
        List<Report> actualReports = listResponseEntity.getBody();
        // Assert
        assertNotNull(actualReports);
        assertFalse(actualReports.isEmpty(), "The report list should not be empty");
        assertEquals(ReportType.REPORTPRODUCTBYOFFER.getValue(), actualReports.get(0).getAtType(), String.format("The reports type should be %s", ReportType.REPORTPRODUCTBYOFFER.getValue()));
    }

    @Test
    void whenListProductOfferingOptions_returnProductOfferingOptions() {
        // Arrange

        doReturn(
                getProductOffers().stream()
                        .map(productOfferEntity -> ProductOfferingRef
                                .builder()
                                .id(productOfferEntity.getId())
                                .atType(productOfferEntity.getProductOfferType())
                                .name(productOfferEntity.getProductOfferName())
                                .build())
                        .collect(Collectors.toList())
        ).when(reportService).getProductOfferingOptions();
        // Act
        ResponseEntity<List<ProductOfferingRef>> listProductOfferingOptions = reportingApi.listProductOfferingOptions();
        assertTrue(listProductOfferingOptions.getStatusCode().is2xxSuccessful());
        List<ProductOfferingRef> actualProductOfferingOptions = listProductOfferingOptions.getBody();
        // Assert
        assertNotNull(actualProductOfferingOptions);
        assertFalse(actualProductOfferingOptions.isEmpty(), "The Product Offering Options list should not be empty");
    }

    @Test
    void whenGetReportByIdAndType_returnReportId() {
        // Arrange

        doReturn(getRandomReport(ReportType.REPORTPRODUCTBYSTATUS))
                .when(reportService)
                .getReport(any(String.class), any(ReportType.class));
        // Act
        ResponseEntity<Report> retrievesReportRes = reportingApi.retrievesReport("", ReportType.REPORTPRODUCTBYSTATUS);
        assertTrue(retrievesReportRes.getStatusCode().is2xxSuccessful());
        Report report = retrievesReportRes.getBody();
        // Assert
        assertNotNull(report);
        assertNotNull(report.getAtType());
        assertEquals(ReportType.REPORTPRODUCTBYSTATUS.getValue(), report.getAtType(), String.format("The retrieve report type should be %s", ReportType.REPORTPRODUCTBYSTATUS.getValue()));
    }
}
