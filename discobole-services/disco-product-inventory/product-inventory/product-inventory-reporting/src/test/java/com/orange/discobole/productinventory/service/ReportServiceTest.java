// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.v1.ProductOfferingRef;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.dto.v1.ReportGranularity;
import com.orange.discobole.productinventory.dto.v1.ReportType;
import com.orange.discobole.productinventory.exception.ReportingException;
import com.orange.discobole.productinventory.repository.ProductOfferEntityRepository;
import com.orange.discobole.productinventory.util.AbstractTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.util.MockUtil.getProductOffers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@Slf4j
class ReportServiceTest extends AbstractTest {
    @Mock
    private ProductOfferEntityRepository productOfferEntityRepository;

    @Test
    void whenAllDateParamsAreNull_throwException() {
        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportService, ReportType.REPORTPRODUCTBYSTATUS, null, null, null, ReportGranularity.DAY));

        assertEquals(AT_LEAST_ONE_OF_COLLECT_DATE_COLLECT_DATE_LTE_OR_COLLECT_DATE_GTE_MUST_BE_PROVIDED, exception.getExceptionResponse().getMessage());
    }

    @Test
    void whenCollectDateAndDateRangeProvided_throwException() {
        LocalDate collectDate = LocalDate.now();
        LocalDate collectDateLte = LocalDate.now().minusDays(1);

        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportService, ReportType.REPORTPRODUCTBYSTATUS, collectDate, collectDateLte, null, ReportGranularity.DAY));

        assertEquals(SPECIFY_EITHER_COLLECT_DATE_OR_COLLECT_DATE_LTE_COLLECT_DATE_GTE_NOT_BOTH, exception.getExceptionResponse().getMessage());
    }

    @Test
    void whenCollectDateLteIsBeforeCollectDateGte_throwException() {
        LocalDate collectDateLte = LocalDate.now().minusDays(2);
        LocalDate collectDateGte = LocalDate.now().plusDays(5);

        ReportingException exception = assertThrows(ReportingException.class, () -> invokeListReports(reportService, ReportType.REPORTPRODUCTBYSTATUS, null, collectDateLte, collectDateGte, ReportGranularity.DAY));

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

        // Act
        List<Report> actualReports = reportService.getReports(atType, null, collectDateLte, collectDateGte, granularity, productOfferId);

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

        // Act
        List<Report> actualReports = reportService.getReports(atType, null, collectDateLte, collectDateGte, granularity, productOfferId);

        // Assert
        assertNotNull(actualReports);
        assertFalse(actualReports.isEmpty(), "The report list should not be empty");
        assertEquals(ReportType.REPORTPRODUCTBYOFFER.getValue(), actualReports.get(0).getAtType(), String.format("The reports type should be %s", ReportType.REPORTPRODUCTBYOFFER.getValue()));
    }

    @Test
    void whenListProductOfferingOptions_returnProductOfferingOptions() {
        // Arrange
        doReturn(
                getProductOffers()
        ).when(productOfferEntityRepository).findAll();
        // Act
        List<ProductOfferingRef> actualProductOfferingOptions = reportService.getProductOfferingOptions();
        // Assert
        assertNotNull(actualProductOfferingOptions);
        assertFalse(actualProductOfferingOptions.isEmpty(), "The Product Offering Options list should not be empty");
    }
}
