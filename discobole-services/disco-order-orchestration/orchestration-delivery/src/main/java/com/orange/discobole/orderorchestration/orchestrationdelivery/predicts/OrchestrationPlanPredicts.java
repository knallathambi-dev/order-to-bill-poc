// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.predicts;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;
import java.util.Objects;

public class OrchestrationPlanPredicts {

    private static final String REQUESTED_DELIVERY_DATE = "requestedDeliveryDate";
    private static final String RECEIVED_DATE = "receivedDate";
    private static final Logger log = LoggerFactory.getLogger(OrchestrationPlanPredicts.class);

    private OrchestrationPlanPredicts() {
    }

    public static Query createFilterQuery(OrchestrationPlanFilter orchestrationPlanFilter, String fieldArray) {
        Query query = new Query();
        setId(query, orchestrationPlanFilter.getId());
        setState(query, orchestrationPlanFilter.getState());
        setReceivedDate(query, orchestrationPlanFilter.getReceivedDate());
        setRequestedDeliveryDate(query, orchestrationPlanFilter.getRequestedDeliveryDate());
        setRelatedPartyId(query, orchestrationPlanFilter.getRelatedPartyId());
        setRelatedPartyRole(query, orchestrationPlanFilter.getRelatedPartyRole());
        setRelatedPartyHref(query, orchestrationPlanFilter.getRelatedPartyHref());
        setRelatedPartyName(query, orchestrationPlanFilter.getRelatedPartyName());
        setRelatedProductOrderId(query, orchestrationPlanFilter.getRelatedProductOrderId());
        setOrchestrationPlanNodeState(query, orchestrationPlanFilter.getOrchestrationPlanNodesState());
        setOrchestrationPlanNodeRelatedServiceOrderId(query, orchestrationPlanFilter.getOrchestrationPlanNodesRelatedServiceOrderId());
        setOrchestrationPlanNodeRelatedProductOrderItemId(query, orchestrationPlanFilter.getOrchestrationPlanNodesRelatedProductOrderItemId());
        setOrchestrationPlanNodeRelatedProductId(query, orchestrationPlanFilter.getOrchestrationPlanNodesRelatedProductId());
        setOrchestrationPlanNodeFields(query, fieldArray);
        setOrchestrationPlanReceivedDate(query, orchestrationPlanFilter.getReceivedDateGte(), orchestrationPlanFilter.getReceivedDateLts());
        setOrchestrationPlanDeliveryDate(query, orchestrationPlanFilter.getRequestedDeliveryDateGte(), orchestrationPlanFilter.getRequestedDeliveryDateLts());
        setArchived(query, orchestrationPlanFilter.getArchived());
        log.debug("OrchestrationPlanPredicts | createFilterQuery | Filter query: {}", query);
        return query;
    }

    private static void setRequestedDeliveryDate(Query query, OffsetDateTime requestedDeliveryDate) {
        if (Objects.nonNull(requestedDeliveryDate)) {
            query.addCriteria(Criteria.where(OrchestrationPlan.Fields.requestedDeliveryDate).is(requestedDeliveryDate));

        }
    }

    public static Query createFilterQueryById(String id, String fields) {
        Query query = new Query();
        setId(query, id);
        setOrchestrationPlanNodeFields(query, fields);
        return query;
    }

    private static void setOrchestrationPlanDeliveryDate(Query query, OffsetDateTime requestedDeliveryDateStart, OffsetDateTime requestedDeliveryDateEnd) {

        if (query.getQueryObject().containsKey(REQUESTED_DELIVERY_DATE)) {
            return;
        }
        Criteria dateCriteria = new Criteria();
        if (requestedDeliveryDateStart != null) {
            dateCriteria.and(REQUESTED_DELIVERY_DATE).gte(requestedDeliveryDateStart.toLocalDateTime());
        }
        if (requestedDeliveryDateEnd != null) {
            dateCriteria.andOperator(Criteria.where(REQUESTED_DELIVERY_DATE).lte(requestedDeliveryDateEnd.toLocalDateTime()));
        }
        if (requestedDeliveryDateStart != null || requestedDeliveryDateEnd != null) {
            query.addCriteria(dateCriteria);
        }
    }

    private static void setOrchestrationPlanReceivedDate(Query query, OffsetDateTime receivedDateStart, OffsetDateTime receivedDateEnd) {
        if (query.getQueryObject().containsKey(RECEIVED_DATE)) {
            return;
        }
        Criteria dateCriteria = new Criteria();
        if (receivedDateStart != null) {
            dateCriteria.and(RECEIVED_DATE).gte(receivedDateStart.toLocalDateTime());
        }
        if (receivedDateEnd != null) {
            dateCriteria.andOperator(Criteria.where(RECEIVED_DATE).lte(receivedDateEnd.toLocalDateTime()));
        }
        if (receivedDateStart != null || receivedDateEnd != null) {
            query.addCriteria(dateCriteria);
        }
    }

    public static void setOrchestrationPlanNodePageable(Query query, Integer offset, Integer limit) {
        if (!Objects.isNull(limit)) {
            query.limit(limit);
        }
        if (!Objects.isNull(offset)) {
            int numberOfSkippedDocument = offset;
            query.skip(numberOfSkippedDocument);
        }
    }

    private static void setOrchestrationPlanNodeFields(Query query, String fields) {
        if (Objects.nonNull(fields) && !fields.isEmpty() && !fields.equals("[]")) {
            query.fields().include(fields.split(","));
        }
    }

    private static void setOrchestrationPlanNodeRelatedProductId(Query query, String orchestrationPlanNodesRelatedProductId) {

        if (StringUtils.isNotEmpty(orchestrationPlanNodesRelatedProductId)) {
            query.addCriteria(Criteria.where("orchestrationPlanNodes.relatedProduct._id").is(new ObjectId(orchestrationPlanNodesRelatedProductId)));
        }
    }

    private static void setOrchestrationPlanNodeRelatedProductOrderItemId(Query query, String orchestrationPlanNodesRelatedProductOrderItemId) {

        if (StringUtils.isNotEmpty(orchestrationPlanNodesRelatedProductOrderItemId)) {
            query.addCriteria(Criteria.where("orchestrationPlanNodes.relatedProductOrderItem._id").is(orchestrationPlanNodesRelatedProductOrderItemId));
        }
    }

    private static void setOrchestrationPlanNodeRelatedServiceOrderId(Query query, String orchestrationPlanNodesRelatedServiceOrderId) {
        if (StringUtils.isNotEmpty(orchestrationPlanNodesRelatedServiceOrderId)) {
            query.addCriteria(Criteria.where("orchestrationPlanNodes.relatedServiceOrder.id").is(orchestrationPlanNodesRelatedServiceOrderId));
        }
    }

    private static void setOrchestrationPlanNodeState(Query query, String orchestrationPlanNodesState) {
        if (StringUtils.isNotEmpty(orchestrationPlanNodesState)) {
            query.addCriteria(Criteria.where("orchestrationPlanNodes.state").is(orchestrationPlanNodesState));
        }
    }

    private static void setRelatedProductOrderId(Query query, String relatedProductOrderId) {
        if (StringUtils.isNotEmpty(relatedProductOrderId)) {
            query.addCriteria(Criteria.where("relatedProductOrder._id").is(relatedProductOrderId));
        }
    }

    private static void setRelatedPartyRole(Query query, String relatedPartyRole) {
        if (StringUtils.isNotEmpty(relatedPartyRole)) {
            query.addCriteria(Criteria.where("relatedParty.role").is(relatedPartyRole));
        }
    }

    private static void setRelatedPartyHref(Query query, String relatedPartyHref) {
        if (StringUtils.isNotEmpty(relatedPartyHref)) {
            query.addCriteria(Criteria.where("relatedParty.href").is(relatedPartyHref));
        }
    }

    private static void setRelatedPartyName(Query query, String relatedPartyName) {
        if (StringUtils.isNotEmpty(relatedPartyName)) {
            query.addCriteria(Criteria.where("relatedParty.name").is(relatedPartyName));
        }
    }

    private static void setRelatedPartyId(Query query, String relatedPartyId) {
        if (StringUtils.isNotEmpty(relatedPartyId)) {
            query.addCriteria(Criteria.where("relatedParty.id").is(relatedPartyId));
        }
    }

    private static void setReceivedDate(Query query, OffsetDateTime receivedDate) {
        if (Objects.nonNull(receivedDate)) {
            query.addCriteria(Criteria.where(OrchestrationPlan.Fields.receivedDate).is(receivedDate));
        }
    }

    private static void setState(Query query, String state) {
        if (StringUtils.isNotEmpty(state)) {

            query.addCriteria(Criteria.where(OrchestrationPlan.Fields.state).is(state));
        }
    }

    private static void setArchived(Query query, Boolean archived) {
        if (Objects.nonNull(archived)) {
            query.addCriteria(Criteria.where(OrchestrationPlan.Fields.archived).is(archived));
        }
    }

    private static void setId(Query query, String id) {
        if (StringUtils.isNotEmpty(id)) {
            query.addCriteria(Criteria.where(OrchestrationPlan.Fields.id).is(id));
        }
    }

}
