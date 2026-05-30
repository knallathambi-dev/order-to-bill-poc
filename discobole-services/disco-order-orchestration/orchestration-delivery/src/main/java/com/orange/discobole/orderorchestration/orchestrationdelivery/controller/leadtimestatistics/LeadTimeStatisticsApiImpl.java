// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.controller.leadtimestatistics;

import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.ContractLeadTimeStatisticsResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.NodeLeadTimeStatisticsResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.api.v1.LeadTimeHistoryStatisticsApi;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.ContractLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.NodeLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.LeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.leadtimestatistics.LeadTimeStatisticsService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.PageableHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.INVALID_QUERY_STRING_PARAMETER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LeadTimeStatisticsApiImpl implements LeadTimeHistoryStatisticsApi {

    private final LeadTimeStatisticsService leadTimeStatisticsService;

    @Override
    public ResponseEntity<NodeLeadTimeHistoryStatistics> getNodeLeadTimeHistoryStatistics(
            String productSpecId,
            String deliveryFactoryName,
            Boolean min,
            Boolean max,
            Boolean average,
            OffsetDateTime timePeriodStart,
            OffsetDateTime timePeriodEnd,
            Integer approximateCount,
            List<String> responseProjectionFields,
            Integer offset,
            Integer limit,
            String sortSampleWindow) {


        log.info("productSpecId={}, deliveryFactoryName={}, min={}, max={}, average={}, "
                        + "timePeriodStart={}, timePeriodEnd={}, approximateCount={}, responseProjectionFields={}, offset={}, limit={}, sortSampleWindow={}",
                productSpecId, deliveryFactoryName, min, max, average, timePeriodStart, timePeriodEnd, approximateCount, responseProjectionFields, offset, limit, sortSampleWindow);

        // Validate query params
        validateNodeSelector(productSpecId, deliveryFactoryName);
        validateStatSelection(min, max, average);
        validateTimeWindow(timePeriodStart, timePeriodEnd, approximateCount);
        int safeOffset = defaultOffset(offset);
        int safeLimit = defaultLimit(limit);
        validatePagination(safeOffset, safeLimit);
        String normalizedSort = normalizeAndValidateSort(sortSampleWindow);

        // Convert times
        Instant start = timePeriodStart != null ? timePeriodStart.toInstant() : null;
        Instant end = timePeriodEnd != null ? timePeriodEnd.toInstant() : null;

        // Delegate to service
        NodeLeadTimeStatisticsResponse serviceResponse = leadTimeStatisticsService.getNodeLeadTimeHistoryStatistics(
                productSpecId,
                deliveryFactoryName,
                Boolean.TRUE.equals(min),
                Boolean.TRUE.equals(max),
                Boolean.TRUE.equals(average),
                start,
                end,
                approximateCount,
                responseProjectionFields,
                safeOffset,
                safeLimit,
                normalizedSort
        );

        if (serviceResponse.getResultCount() == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(PageableHeader.X_RESULT_COUNT_HEADER, String.valueOf(serviceResponse.getResultCount()));
        headers.add(PageableHeader.X_TOTAL_COUNT_HEADER, String.valueOf(serviceResponse.getTotalCount()));

        return new ResponseEntity<>(serviceResponse.getBody(), headers, serviceResponse.getHttpStatus());
    }

    @Override
    public ResponseEntity<ContractLeadTimeHistoryStatistics> getContractLeadTimeHistoryStatistics(
            String contractName,
            Boolean min,
            Boolean max,
            Boolean average,
            OffsetDateTime timePeriodStart,
            OffsetDateTime timePeriodEnd,
            Integer approximateCount,
            List<String> responseProjectionFields,
            Integer offset,
            Integer limit,
            String sortSampleWindow) {

        log.info("contractName={}, min={}, max={}, average={}, timePeriodStart={}, "
                        + "timePeriodEnd={}, approximateCount={}, responseProjectionFields={}, offset={}, limit={}, sortSampleWindow={}",
                contractName, min, max, average, timePeriodStart, timePeriodEnd, approximateCount, responseProjectionFields, offset, limit, sortSampleWindow);

        // Validate query params
        validateContractSelector(contractName);
        validateStatSelection(min, max, average);
        validateTimeWindow(timePeriodStart, timePeriodEnd, approximateCount);
        int safeOffset = defaultOffset(offset);
        int safeLimit = defaultLimit(limit);
        validatePagination(safeOffset, safeLimit);
        String normalizedSort = normalizeAndValidateSort(sortSampleWindow);

        // Convert times
        Instant start = timePeriodStart != null ? timePeriodStart.toInstant() : null;
        Instant end = timePeriodEnd != null ? timePeriodEnd.toInstant() : null;

        // Delegate to service
        ContractLeadTimeStatisticsResponse serviceResponse = leadTimeStatisticsService.getContractLeadTimeHistoryStatistics(
                contractName,
                Boolean.TRUE.equals(min),
                Boolean.TRUE.equals(max),
                Boolean.TRUE.equals(average),
                start,
                end,
                approximateCount,
                responseProjectionFields,
                safeOffset,
                safeLimit,
                normalizedSort
        );

        if (serviceResponse.getResultCount() == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(PageableHeader.X_RESULT_COUNT_HEADER, String.valueOf(serviceResponse.getResultCount()));
        headers.add(PageableHeader.X_TOTAL_COUNT_HEADER, String.valueOf(serviceResponse.getTotalCount()));

        return new ResponseEntity<>(serviceResponse.getBody(), headers, serviceResponse.getHttpStatus());
    }

    // --------------- Validation helpers ---------------

    private void validateNodeSelector(String productSpecId, String deliveryFactoryName) {
        if ((productSpecId == null && deliveryFactoryName == null) || (productSpecId != null && deliveryFactoryName != null)) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "exactly one of productSpecId or deliveryFactoryName is required");
        }
    }

    private void validateContractSelector(String contractName) {
        if (contractName == null || contractName.isBlank()) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "contractName is required");
        }
    }

    private void validateStatSelection(Boolean min, Boolean max, Boolean average) {
        boolean anyRequested = Boolean.TRUE.equals(min) || Boolean.TRUE.equals(max) || Boolean.TRUE.equals(average);
        if (!anyRequested) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "At least one of min, max, average must be true");
        }
    }

    private void validateTimeWindow(OffsetDateTime start, OffsetDateTime end, Integer approximateCount) {
        boolean hasWindow = start != null || end != null;
        boolean hasApprox = approximateCount != null;

        if (hasWindow && hasApprox) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28,
                    "Provide either timePeriodStart/timePeriodEnd or approximateCount, not both");
        }
        if (hasWindow) {
            if (start == null || end == null) {
                throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28,
                        "Both timePeriodStart and timePeriodEnd must be provided together");
            }
            if (!end.isAfter(start)) {
                throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28,
                        "timePeriodEnd must be strictly after timePeriodStart");
            }
        }
        if (!hasWindow && !hasApprox) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28,
                    "One of (timePeriodStart + timePeriodEnd) or approximateCount must be provided");
        }
        if (hasApprox && approximateCount <= 0) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28,
                    "approximateCount must be > 0");
        }
    }

    private int defaultOffset(Integer offset) {
        return offset == null ? 0 : offset;
    }

    private int defaultLimit(Integer limit) {
        return limit == null ? 100 : limit;
    }

    private void validatePagination(Integer offset, Integer limit) {
        if (offset != null && offset < 0) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid offset value.");
        }
        if (limit != null && limit < 1) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid limit value.");
        }
        if (limit != null && limit > 1000) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Limit exceed max value.");
        }
    }

    private String normalizeAndValidateSort(String sortSampleWindow) {
        if (sortSampleWindow == null) {
            return "+" + LeadTimeHistorySampledStatistics.Fields.sampleWindow;
        }

        // Strip +/- and validate fields
        String sortSampleWindowWithoutSigns = (sortSampleWindow.charAt(0) == '-' || sortSampleWindow.charAt(0) == '+') ?
                sortSampleWindow.substring(1) : sortSampleWindow;

        if (!LeadTimeHistorySampledStatistics.Fields.sampleWindow.equals(sortSampleWindowWithoutSigns)) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid sort value.");
        }

        // Normalize: ensure each starts with + or -
        return sortSampleWindow.startsWith("-") || sortSampleWindow.startsWith("+") ?
                sortSampleWindow : "+" + sortSampleWindow;
    }

}
