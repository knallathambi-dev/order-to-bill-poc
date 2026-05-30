// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller.impl;

import com.orange.discobole.productinventory.api.v1.ReportingApi;
import com.orange.discobole.productinventory.dto.v1.ProductOfferingRef;
import com.orange.discobole.productinventory.dto.v1.Report;
import com.orange.discobole.productinventory.dto.v1.ReportGranularity;
import com.orange.discobole.productinventory.dto.v1.ReportType;
import com.orange.discobole.productinventory.service.ReportService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@RequestMapping("")
public class ReportingApiImpl implements ReportingApi {
    private final ReportService reportService;

    @Override
    public ResponseEntity<List<Report>> listReports(
            ReportType atType,
            LocalDate collectDate,
            LocalDate collectDateLte,
            LocalDate collectDateGte,
            ReportGranularity granularity,
            List<String> productOfferId
    ) {
        // TODO: add pagination support if applicable
        List<Report> reports = this.reportService.getReports(atType, collectDate, collectDateLte, collectDateGte, granularity, productOfferId);
        return ResponseEntity.ok(reports);
    }

    @Override
    public ResponseEntity<List<ProductOfferingRef>> listProductOfferingOptions() {
        List<ProductOfferingRef> productOfferingOptions = this.reportService.getProductOfferingOptions();
        return ResponseEntity.ok(productOfferingOptions);
    }

    @Override
    public ResponseEntity<Report> retrievesReport(String id, ReportType atType) {
        return ResponseEntity.ok(this.reportService.getReport(id, atType));

    }
}