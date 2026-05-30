// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.validators.FieldsValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.validators.OrchestrationPlanFieldValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.OrchestrationPlanResponse;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.OrchestrationPlanValidationException;
import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.predicts.OrchestrationPlanPredicts;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.PageableHeader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.orange.discobole.orderorchestration.exception.handler.ExceptionThrower.doThrow;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.ORCHESTRATION_PLAN_QUERY_PARAMETER_EMPTY_OR_NULL;

@Service
@Slf4j
public class OrchestrationManagementServiceImpl implements OrchestrationManagementService {

    private static final String NONE = "none";
    private static final String NULL = "null";
    private static final String REGEX = "(?:\\s+,|,\\s+|\\s+;|;\\s+|^\\s+|\\s+$)";
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX); //NOSONAR

    private final OrchestrationPlanMapper orchestrationPlanMapper;

    private final MongoTemplateWrapperService mongoTemplateWrapperService;

    private final OrchestrationPlanFieldValidator productFieldValidator;

    private final String[] requiredFields = new String[]{OrchestrationPlan.Fields.id};

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrchestrationManagementServiceImpl(OrchestrationPlanMapper orchestrationPlanMapper, MongoTemplateWrapperService mongoTemplateWrapperService, OrchestrationPlanFieldValidator productFieldValidator) {
        this.orchestrationPlanMapper = orchestrationPlanMapper;
        this.mongoTemplateWrapperService = mongoTemplateWrapperService;
        this.productFieldValidator = productFieldValidator;
    }

    private static OrchestrationPlanResponse orchestrationPlanResponseBuilder(
            List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanList, HttpHeaders headers,
            HttpStatus httpStatus) {
        OrchestrationPlanResponse orchestrationPlanResponse = new OrchestrationPlanResponse();
        orchestrationPlanResponse.setOrchestrationPlans(orchestrationPlanList);
        orchestrationPlanResponse.setResponseHeaders(headers);
        orchestrationPlanResponse.setHttpStatus(httpStatus);
        return orchestrationPlanResponse;
    }

    private static void validateSpaces(String fieldsQueryParam) {

        // Use the precompiled pattern for matching
        Matcher matcher = REGEX_PATTERN.matcher(fieldsQueryParam);
        boolean containsSpaces = matcher.find();

        // Throw an exception if the input contains invalid spaces
        if (containsSpaces) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Query param [ Fields ] should be valid");
        }
    }

    @Override
    public OrchestrationPlan getOrchestrationPlanById(String id, String fields) throws OrchestrationPlanValidationException, CoodNotFoundException {
        if ((Objects.nonNull(fields) && fields.isBlank()) || Objects.isNull(id)) {
            throw new OrchestrationPlanValidationException(ORCHESTRATION_PLAN_QUERY_PARAMETER_EMPTY_OR_NULL, 28, "Query param [ Fields ] should valid");
        }
        Query query = OrchestrationPlanPredicts.createFilterQueryById(id, fields);
        OrchestrationPlan orchestrationPlan = mongoTemplateWrapperService.findOne(query, OrchestrationPlan.class);
        if (orchestrationPlan == null) {
            log.error("OrchestrationManagementServiceImpl | getOrchestrationPlanById | The orchestration plan  with id {} does not exist", id);
            doThrow(() -> new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND, id));
        }
        log.debug("OrchestrationManagementServiceImpl | getOrchestrationPlanById | Orchestration plan exists: {}", id);

        return orchestrationPlan;
    }

    private HttpStatus determineHttpStatus(int size, long totalCount) {
        if (size >= totalCount) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.PARTIAL_CONTENT;
        }
    }

    private void checkIfOffsetInvalid(long totalCount, Integer offset) {
        if (totalCount != 0 && offset >= totalCount) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Invalid Offset value.");
        }
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

    private static void addFieldIfNotExists(String fieldsQueryParam, String requiredField, StringBuilder stringBuilder) {
        if (!fieldsQueryParam.contains(requiredField)) {
            stringBuilder.append(",");
            stringBuilder.append(requiredField);
        }
    }

    @Override
    public OrchestrationPlanResponse getOrchestrationPlans(OrchestrationPlanFilter orchestrationPlanFilter, List<String> sorts) {
        log.debug("OrchestrationManagementServiceImpl | getOrchestrationPlans | Getting products with attributes: {} with filter {} and sorts {}", orchestrationPlanFilter.getFields(), orchestrationPlanFilter, sorts);
        String[] fieldArray = extractFields(orchestrationPlanFilter, requiredFields);
        validateFieldsToFetch(productFieldValidator, fieldArray);
        Query query = OrchestrationPlanPredicts.createFilterQuery(
                orchestrationPlanFilter,
                Arrays.toString(fieldArray).replaceAll("[\\[\\]\\s]", "")
        );
        setSortInQuery(query, sorts);
        long totalCount = mongoTemplateWrapperService.count(query, OrchestrationPlan.class);
        checkIfOffsetInvalid(totalCount, orchestrationPlanFilter.getOffset());
        OrchestrationPlanPredicts.setOrchestrationPlanNodePageable(query, orchestrationPlanFilter.getOffset(), orchestrationPlanFilter.getLimit());
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> orchestrationPlanDTOList = orchestrationPlanMapper
                .toDtoList(mongoTemplateWrapperService.find(query, OrchestrationPlan.class));
        HttpHeaders headers = PageableHeader.buildPaginationHeaders(orchestrationPlanFilter, totalCount, orchestrationPlanDTOList.size(), sorts);
        HttpStatus httpStatus = determineHttpStatus(orchestrationPlanDTOList.size(), totalCount);
        return orchestrationPlanResponseBuilder(orchestrationPlanDTOList, headers, httpStatus);
    }

    public void validateFieldsToFetch(FieldsValidator validator, String... fieldArray) {
        for (String field : fieldArray) {
            validator.validate(field);
        }
    }

    public String[] extractFields(OrchestrationPlanFilter orchestrationPlanFilter, String... requiredFields) {
        if (Objects.nonNull(orchestrationPlanFilter.getFields()) && !orchestrationPlanFilter.getFields().isEmpty()) {
            log.debug("Extracting fields: {}", orchestrationPlanFilter.getFields());
            String fieldsQueryParam = orchestrationPlanFilter.getFields();
            if (NULL.equalsIgnoreCase(fieldsQueryParam)) {
                throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Query param [ Fields ] should valid");
            }
            validateSpaces(fieldsQueryParam);
            if (!NONE.equalsIgnoreCase(fieldsQueryParam)) {
                StringBuilder fieldsStringBuilder = new StringBuilder(fieldsQueryParam);
                for (String requiredField : requiredFields) {
                    addFieldIfNotExists(fieldsQueryParam, requiredField, fieldsStringBuilder);
                }
                log.debug("Extracted fields: [{}]", fieldsStringBuilder);
                return Arrays.stream(fieldsStringBuilder.toString().split(",")).distinct().toArray(String[]::new);
            } else {
                log.debug("Extracted fields: " + Arrays.toString(requiredFields));
                return requiredFields;
            }
        }
        return new String[0];
    }

}
