// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.controller;

import com.orange.discobole.orderorchestration.orchestrationdelivery.api.v1.OrchestrationPlanApi;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.OrchestrationPlanResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanNodeStateEnum;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanStateEnum;
import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationManagementServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.OrchestrationPlanHrefSetter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.INVALID_QUERY_STRING_PARAMETER;

@Slf4j
@RestController
public class OrchestrationManagementApiImpl implements OrchestrationPlanApi {
    private final OrchestrationManagementServiceImpl orchestrationManagementService;

    private final OrchestrationPlanMapper orchestrationPlanMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrchestrationManagementApiImpl(OrchestrationManagementServiceImpl orchestrationManagementService, OrchestrationPlanMapper orchestrationPlanMapper) {
        this.orchestrationManagementService = orchestrationManagementService;
        this.orchestrationPlanMapper = orchestrationPlanMapper;
    }

    @Override
    @PreAuthorize("hasAuthority(@SecurityConfigurationProperties.getRoles())")
    public ResponseEntity<OrchestrationPlan> getOrchestrationPlanById(String id, String fields) {
        log.info("OrchestrationManagementApiImpl | getOrchestrationPlanById | Received request to get Orchestration Plan by id {}, fields {}", id, fields);
        com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan orchestrationPlan = orchestrationManagementService.getOrchestrationPlanById(id, fields);
        OrchestrationPlan orchestrationPlanDto = orchestrationPlanMapper.toDto(orchestrationPlan);
        orchestrationPlanDto.setHref(OrchestrationPlanHrefSetter.generateHref(id, fields));
        return ResponseEntity.ok(orchestrationPlanDto);
    }

    @Override
    @PreAuthorize("hasAuthority(@SecurityConfigurationProperties.getRoles())")
    public ResponseEntity<List<OrchestrationPlan>> getOrchestrationPlans(@Valid String id,
                                                                         @Valid OrchestrationPlanStateEnum state,
                                                                         @Valid OffsetDateTime receivedDate,
                                                                         @Valid String relatedPartyId,
                                                                         @Valid String relatedPartyRole,
                                                                         @Valid String relatedPartyHref,
                                                                         @Valid String relatedPartyName,
                                                                         @Valid String relatedProductOrderId,
                                                                         @Valid OrchestrationPlanNodeStateEnum orchestrationPlanNodesState,
                                                                         @Valid String orchestrationPlanNodesRelatedServiceOrderId,
                                                                         @Valid String orchestrationPlanNodesRelatedProductOrderItemId,
                                                                         @Valid String orchestrationPlanNodesRelatedProductId,
                                                                         @Valid String fields,
                                                                         @Valid Integer offset,
                                                                         @Valid Integer limit,
                                                                         @Valid OffsetDateTime requestedDeliveryDate,
                                                                         @Valid List<String> sort,
                                                                         @Valid Boolean archived,
                                                                         @Valid OffsetDateTime receivedDateGte,
                                                                         @Valid OffsetDateTime receivedDateLte,
                                                                         @Valid OffsetDateTime requestedDeliveryDateGte,
                                                                         @Valid OffsetDateTime requestedDeliveryDateLte) {

        log.info("OrchestrationManagementApiImpl | getOrchestrationPlans | Received request to get plans");
        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .id(id)
                .state(Objects.isNull(state) ? null : state.getValue())
                .receivedDate(receivedDate)
                .relatedPartyId(relatedPartyId)
                .relatedPartyRole(relatedPartyRole)
                .relatedPartyHref(relatedPartyHref)
                .relatedPartyName(relatedPartyName)
                .relatedProductOrderId(relatedProductOrderId)
                .orchestrationPlanNodesState(Objects.isNull(orchestrationPlanNodesState) ? null : orchestrationPlanNodesState.getValue())
                .orchestrationPlanNodesRelatedServiceOrderId(orchestrationPlanNodesRelatedServiceOrderId)
                .orchestrationPlanNodesRelatedProductOrderItemId(orchestrationPlanNodesRelatedProductOrderItemId)
                .orchestrationPlanNodesRelatedProductId(orchestrationPlanNodesRelatedProductId)
                .fields(Objects.nonNull(fields) && !fields.isBlank() ? fields : null)
                .offset(offset)
                .limit(limit)
                .receivedDateGte(receivedDateGte)
                .receivedDateLts(receivedDateLte)
                .requestedDeliveryDateGte(requestedDeliveryDateGte)
                .requestedDeliveryDateLts(requestedDeliveryDateLte)
                .requestedDeliveryDate(requestedDeliveryDate)
                .archived(archived).build();
        validateOrchestrationPlanFilterApi(orchestrationPlanFilter, sort);
        OrchestrationPlanResponse response = orchestrationManagementService.getOrchestrationPlans(orchestrationPlanFilter, sort);
        if (response.getOrchestrationPlans().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        OrchestrationPlanHrefSetter.setHrefForOrchestrationPlans(response.getOrchestrationPlans(), null);
        return new ResponseEntity<>(response.getOrchestrationPlans(), response.getResponseHeaders(), response.getHttpStatus());
    }

    private void validateOrchestrationPlanFilterApi(OrchestrationPlanFilter orchestrationPlanFilter, @Valid List<String> sort) {
        validateSort(sort);

        if (offsetAndLimitAreNegative(orchestrationPlanFilter.getOffset(), orchestrationPlanFilter.getLimit())) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Limit and Offset can't be negative");
        }

        if (orchestrationPlanFilter.getLimit() < 0) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid limit value.");
        }

        if (orchestrationPlanFilter.getLimit() > 1000) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Limit exceed max value.");
        }

        if (orchestrationPlanFilter.getOffset() < 0) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid offset value.");
        }
    }

    private void validateSort(List<String> sorts) {
        if (Objects.isNull(sorts)) {
            return;
        }
        Set<String> fieldsName = Arrays.stream(OrchestrationPlan.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        String orderStartDate = "orchestrationPlanSchedule.orderStartDate";
        fieldsName.add(orderStartDate);
        Set<String> sortFieldsWithoutSign = sorts.stream().map(sortField -> (sortField.charAt(0) == '-' || sortField.charAt(0) == '+') ? sortField.substring(1) : sortField).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(sorts) && !fieldsName.containsAll(sortFieldsWithoutSign)) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid sort value.");
        }
    }

    private static boolean offsetAndLimitAreNegative(Integer offset, Integer limit) {
        return (offset != null && offset < 0) && (limit != null && limit < 0);
    }
}
