// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.api.v1.FalloutIncidentApi;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.response.FalloutResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutIncidentApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.mapper.FalloutMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.FalloutManagementService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FalloutManagementApiImpl implements FalloutIncidentApi {
    private final FalloutManagementService falloutManagementService;

    private final FalloutMapper falloutMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FalloutManagementApiImpl(FalloutManagementService falloutManagementService, FalloutMapper falloutMapper) {
        this.falloutManagementService = falloutManagementService;
        this.falloutMapper = falloutMapper;
    }

    private static boolean offsetAndLimitAreNegative(Integer offset, Integer limit) {
        return (offset != null && offset < 0) && (limit != null && limit < 0);
    }

    @Override
    public ResponseEntity<List<FalloutIncident>> getFallouts(String id,
                                                             String state,
                                                             String relatedPartyId,
                                                             String relatedPartyRole,
                                                             String relatedPartyName,
                                                             String relatedEntityState,
                                                             String relatedEntityId,
                                                             String relatedEntityRole,
                                                             String fields,
                                                             Integer offset,
                                                             Integer limit,
                                                             List<String> sort,
                                                             OffsetDateTime creationDateGte,
                                                             OffsetDateTime creationDateLte,
                                                             OffsetDateTime lastModifiedDateLte,
                                                             OffsetDateTime lastModifiedDateGte
    ) {
        log.info("FalloutManagementApiImpl | getFallouts | Received request to get fallouts");
        FalloutFilter falloutFilter = FalloutFilter.builder()
                .id(id)
                .state(Objects.isNull(state) ? null : state)
                .relatedEntityId(relatedEntityId)
                .relatedEntityRole(relatedEntityRole)
                .relatedEntityState(relatedEntityState)
                .relatedPartyId(relatedPartyId)
                .relatedPartyRole(relatedPartyRole)
                .relatedPartyName(relatedPartyName)
                .creationDateGte(creationDateGte)
                .creationDateLte(creationDateLte)
                .lastModifiedDateGte(lastModifiedDateGte)
                .lastModifiedDateLte(lastModifiedDateLte)
                .fields(fields)
                .offset(offset)
                .limit(limit).build();
        validateOrchestrationPlanFilterApi(falloutFilter, sort);

        FalloutResponse response = falloutManagementService.getFalloutList(falloutFilter, sort);
        return new ResponseEntity<>(response.getFalloutList(), response.getResponseHeaders(), response.getHttpStatus());

    }

    @Override
    public ResponseEntity<FalloutIncident> getFalloutById(String id, String fields) {
        log.info("OrchestrationManagementApiImpl | getOrchestrationPlanById | Received request to get Orchestration Plan by id {}, fields {}", id, fields);
        com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident fallout = falloutManagementService.getFalloutById(id, fields);
        FalloutIncident falloutDto = falloutMapper.toDto(fallout);
        return ResponseEntity.ok(falloutDto);
    }

    private void validateOrchestrationPlanFilterApi(FalloutFilter falloutFilter, @Valid List<String> sort) {
        validateSort(sort);

        if (offsetAndLimitAreNegative(falloutFilter.getOffset(), falloutFilter.getLimit())) {
            throw new FalloutIncidentApiQueryParamException(RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Limit and Offset can't be negative.");
        }

        if (falloutFilter.getLimit() < 0) {
            throw new FalloutIncidentApiQueryParamException(RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Invalid limit value.");
        }

        if (falloutFilter.getLimit() > 1000) {
            throw new FalloutIncidentApiQueryParamException(RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Limit exceed max value.");
        }

        if (falloutFilter.getOffset() < 0) {
            throw new FalloutIncidentApiQueryParamException(RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Invalid offset value.");
        }
    }

    private void validateSort(List<String> sorts) {
        if (Objects.isNull(sorts)) {
            return;
        }
        Set<String> fieldsName = Arrays.stream(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        Set<String> sortFieldsWithoutSign = sorts.stream().map(sortField -> (sortField.charAt(0) == '-' || sortField.charAt(0) == '+') ? sortField.substring(1) : sortField).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(sorts) && !fieldsName.containsAll(sortFieldsWithoutSign)) {
            throw new FalloutIncidentApiQueryParamException(RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Invalid sort value.");
        }
    }
}


