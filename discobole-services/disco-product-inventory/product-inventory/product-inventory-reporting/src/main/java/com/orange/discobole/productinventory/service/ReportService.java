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
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.mapper.ReportMapper;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.repository.ProductOfferEntityRepository;
import com.orange.discobole.productinventory.utils.HrefGenerator;
import com.orange.discobole.productinventory.utils.query.ReportsQueryBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.constant.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.constant.ErrorCodeEnum.RESOURCE_NOT_FOUND;

@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Service
@Slf4j
public class ReportService {
    private final MongoTemplate mongoTemplate;
    private final ReportMapper reportMapper;
    private final ProductOfferEntityRepository productOfferEntityRepository;

    private static void validateRequest(LocalDate collectDate, LocalDate collectDateLte, LocalDate collectDateGte) {
        // Check if all inputs related to date are null
        if (collectDate == null && collectDateLte == null && collectDateGte == null) {
            throw new ReportingException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    AT_LEAST_ONE_OF_COLLECT_DATE_COLLECT_DATE_LTE_OR_COLLECT_DATE_GTE_MUST_BE_PROVIDED
            );
        }

        if (collectDate != null && (collectDateLte != null || collectDateGte != null)) {
            throw new ReportingException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    SPECIFY_EITHER_COLLECT_DATE_OR_COLLECT_DATE_LTE_COLLECT_DATE_GTE_NOT_BOTH
            );
        }
        // Validate that collectDateLte is not after collectDateGte
        if (collectDateLte != null && collectDateGte != null && collectDateLte.isBefore(collectDateGte)) {
            throw new ReportingException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(), COLLECT_DATE_GTE_MUST_BE_AFTER_COLLECT_DATE_LTE
            );
        }
    }

    public static Query getReportQuery(LocalDate date) {
        return new Query(Criteria
                .where(BaseDateDerivedFields.Fields.date)
                .is(date));
    }

    public static Query getReportQuery(LocalDate date, String productOfferId) {
        return new Query(Criteria
                .where(BaseDateDerivedFields.Fields.date)
                .is(date)
                .and(ProductOfferReportEntity.Fields.productOfferId)
                .is(productOfferId));
    }

    public static Query getReportQuery(String productOfferId) {
        return new Query(Criteria
                .where(ProductOfferReportEntity.Fields.productOfferId)
                .is(productOfferId));
    }

    public static Query getProductOfferReportQuery(String productOfferId) {
        return new Query(Criteria
                .where(ProductOfferEntity.Fields.id)
                .is(productOfferId));
    }

    public List<Report> getReports(
            ReportType atType,
            LocalDate collectDate,
            LocalDate collectDateLte,
            LocalDate collectDateGte,
            ReportGranularity granularity,
            List<String> productOfferId
    ) {
        granularity = granularity != null ? granularity : ReportGranularity.DAY;
        validateRequest(collectDate, collectDateLte, collectDateGte);
        ReportsQueryBuilder reportsQuery =
                new ReportsQueryBuilder()
                        .withDateRange(collectDateGte, collectDateLte)
                        .withDate(collectDate)
                        .withGranularity(granularity);
        // Handle report fetching based on the atType and date parameters
        return switch (atType) {
            case REPORTPRODUCTBYSTATUS -> {
                List<StatusReportEntity> reportsRecords = getReportsRecords(reportsQuery.build(), StatusReportEntity.class);
                yield reportsRecords
                        .stream()
                        .map(report -> {
                            Report dto = reportMapper.toDto(report);
                            HrefGenerator.generateHrefReport(dto);
                            return dto;
                        })
                        .collect(Collectors.toList());

            }
            case REPORTPRODUCTBYOFFER -> {
                reportsQuery.withProductOfferId(productOfferId);
                List<ProductOfferReportEntity> reportsRecords = getReportsRecords(reportsQuery.build(), ProductOfferReportEntity.class);
                yield reportsRecords
                        .stream()
                        .map(report -> {
                            Report dto = reportMapper.toDto(report);
                            HrefGenerator.generateHrefReport(dto);
                            return dto;
                        })
                        .collect(Collectors.toList());
            }
            default -> throw new UnsupportedTypeException(atType);
        };
    }

    public List<Report> getReports(
            ReportType atType,
            LocalDate collectDate,
            LocalDate collectDateLte,
            LocalDate collectDateGte,
            ReportGranularity granularity,
            String productOfferId
    ) {
        return getReports(atType, collectDate, collectDateLte, collectDateGte, granularity, List.of(productOfferId));
    }

    public <T extends BaseDateDerivedFields> List<T> getReportsRecords(Query query, Class<T> klass) {
        return mongoTemplate.find(query, klass);
    }

    public <T extends BaseDateDerivedFields> T getReportsRecord(String id, Class<T> klass) {
        return mongoTemplate.findById(id, klass);
    }

    public List<ProductOfferingRef> getProductOfferingOptions() {
        return productOfferEntityRepository
                .findAll()
                .stream()
                .map(productOfferEntity -> ProductOfferingRef
                        .builder()
                        .id(productOfferEntity.getId())
                        .atType(productOfferEntity.getProductOfferType())
                        .name(productOfferEntity.getProductOfferName())
                        .build())
                .collect(Collectors.toList());
    }

    public Report getReport(String id, ReportType atType) {
        ReportingException notFound = new ReportingException(
                HttpStatus.NOT_FOUND,
                RESOURCE_NOT_FOUND.getCode(),
                RESOURCE_NOT_FOUND.getStatus(),
                String.format(THE_REPORT_WITH_ID_S_AND_TYPE_DOES_NOT_EXIST, id, atType.getValue())
        );

        // Map ReportType to corresponding entity class
        Class<? extends StatusReportingFields> entityClass = switch (atType) {
            case REPORTPRODUCTBYSTATUS -> StatusReportEntity.class;
            case REPORTPRODUCTBYOFFER -> ProductOfferReportEntity.class;
            default -> throw new UnsupportedTypeException(atType);
        };

        // Fetch record and map to DTO
        return Optional.ofNullable(getReportsRecord(id, entityClass))
                .map(reportMapper::toDto)
                .orElseThrow(() -> notFound);
    }


}
