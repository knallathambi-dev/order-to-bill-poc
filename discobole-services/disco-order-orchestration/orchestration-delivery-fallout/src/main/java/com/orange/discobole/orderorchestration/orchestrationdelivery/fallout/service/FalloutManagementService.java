// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.response.FalloutResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutIncidentApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.suppliers.NotFoundExceptionSuppliers;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.mapper.FalloutMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.predicts.FalloutPredict;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.PageableHeader;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.handler.ExceptionThrower.doThrow;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ValidationExceptionReason.FALLOUT_QUERY_PARAMETER_EMPTY_OR_NULL;

@Slf4j
@Service
public class FalloutManagementService {

    private final MongoTemplateWrapperService mongoTemplateWrapperService;

    private final FalloutMapper falloutMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FalloutManagementService(MongoTemplateWrapperService mongoTemplateWrapperService, FalloutMapper falloutMapper) {
        this.mongoTemplateWrapperService = mongoTemplateWrapperService;
        this.falloutMapper = falloutMapper;
    }

    private static FalloutResponse orchestrationPlanResponseBuilder(
            List<FalloutIncident> falloutList, HttpHeaders headers,
            HttpStatus httpStatus) {
        FalloutResponse orchestrationPlanResponse = new FalloutResponse();
        orchestrationPlanResponse.setFalloutList(falloutList);
        orchestrationPlanResponse.setResponseHeaders(headers);
        orchestrationPlanResponse.setHttpStatus(httpStatus);
        return orchestrationPlanResponse;
    }

    public FalloutResponse getFalloutList(FalloutFilter falloutFilter, List<String> sorts) {
        Query query = FalloutPredict.createFilterQuery(falloutFilter);
        setSortInQuery(query, sorts);
        long totalCount = mongoTemplateWrapperService.count(query, com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class);

        checkIfOffsetInvalid(totalCount, falloutFilter.getOffset());
        FalloutPredict.setFalloutPageable(query, falloutFilter.getOffset(), falloutFilter.getLimit());
        List<FalloutIncident> falloutList = falloutMapper
                .toDtoList(mongoTemplateWrapperService.find(query, com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class));
        HttpHeaders headers = PageableHeader.buildPaginationHeaders(falloutFilter, totalCount, falloutList.size(), sorts);
        return orchestrationPlanResponseBuilder(falloutList, headers, HttpStatus.OK);
    }

    private void checkIfOffsetInvalid(long totalCount, Integer offset) {
        if (totalCount != 0 && offset >= totalCount) {
            throw new FalloutIncidentApiQueryParamException(FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION, "Invalid Offset value.");
        }
    }

    public com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident getFalloutById(String id, String fields) throws FalloutNotFoundException {
        if ((Objects.nonNull(fields) && fields.isBlank()) || Objects.isNull(id)) {
            throw new InvalidParameterException(FALLOUT_QUERY_PARAMETER_EMPTY_OR_NULL.toString());
        }
        Query query = FalloutPredict.createFilterQueryById(id, fields);
        com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident falloutIncident = mongoTemplateWrapperService.findOne(query, com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class);
        if (falloutIncident == null) {
            log.error("FalloutServiceImpl | getFalloutById | The fallout with id {} does not exist", id);
            doThrow(NotFoundExceptionSuppliers.falloutNotFoundException(id));
        }
        log.debug("FalloutServiceImpl | getFalloutById | fallout exists: {}", id);

        return falloutIncident;
    }

    private void setSortInQuery(Query query, List<String> sorts) {
        if (Objects.isNull(sorts) || sorts.isEmpty()) {
            return;
        }
        for (String field : sorts) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (field.contains("-")) {
                direction = Sort.Direction.DESC;
                field = field.replace("-", "");
            } else if (field.contains("+")) {
                field = field.replace("+", "");
            }
            query.with(Sort.by(direction, field));
        }
    }
}
