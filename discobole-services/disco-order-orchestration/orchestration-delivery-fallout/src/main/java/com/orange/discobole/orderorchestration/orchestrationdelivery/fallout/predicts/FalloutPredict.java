// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.predicts;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;
import java.util.Objects;

public class FalloutPredict {

    public static final String MODIFICATION_DATE = "modificationDate";
    public static final String CREATION_DATE = "creationDate";
    private static final Logger log = LoggerFactory.getLogger(FalloutPredict.class);

    private FalloutPredict() {
    }

    public static Query createFilterQuery(FalloutFilter falloutFilter) {
        Query query = new Query();
        setId(query, falloutFilter.getId());
        setState(query, falloutFilter.getState());
        setCreationDate(query, falloutFilter.getCreationDateGte(), falloutFilter.getCreationDateLte());
        setLastModifiedDate(query, falloutFilter.getLastModifiedDateGte(), falloutFilter.getLastModifiedDateLte());
        setRelatedPartyId(query, falloutFilter.getRelatedPartyId());
        setRelatedPartyRole(query, falloutFilter.getRelatedPartyRole());
        setRelatedPartyName(query, falloutFilter.getRelatedPartyName());
        setRelatedEntityId(query, falloutFilter.getRelatedEntityId());
        setRelatedEntityState(query, falloutFilter.getRelatedEntityState());
        log.debug("OrchestrationPlanPredicts | createFilterQuery | Filter query: {}", query);
        return query;
    }

    public static Query createFilterQueryById(String id, String fields) {
        Query query = new Query();
        setId(query, id);
        setFalloutFields(query, fields);
        return query;
    }

    private static void setFalloutFields(Query query, String fields) {
        if (Objects.nonNull(fields)) {
            query.fields().include(fields.split(","));
        }
    }


    public static void setFalloutPageable(Query query, Integer offset, Integer limit) {
        if (!Objects.isNull(limit)) {
            query.limit(limit);
        }
        if (!Objects.isNull(offset)) {
            int numberOfSkippedDocument = offset;
            query.skip(numberOfSkippedDocument);
        }
    }

    private static void setRelatedEntityId(Query query, String relatedEntityId) {
        if (Objects.nonNull(relatedEntityId)) {
            query.addCriteria(Criteria.where("relatedEntity.id").is(relatedEntityId));
        }
    }

    private static void setRelatedEntityState(Query query, String relatedEntityState) {
        if (Objects.nonNull(relatedEntityState)) {
            query.addCriteria(Criteria.where("relatedEntity.state").is(relatedEntityState));
        }
    }

    private static void setRelatedPartyRole(Query query, String relatedPartyRole) {
        if (Objects.nonNull(relatedPartyRole)) {
            query.addCriteria(Criteria.where("relatedParty.role").is(relatedPartyRole));
        }
    }

    private static void setRelatedPartyName(Query query, String relatedPartyName) {
        if (Objects.nonNull(relatedPartyName)) {
            query.addCriteria(Criteria.where("relatedParty.name").is(relatedPartyName));
        }
    }

    private static void setRelatedPartyId(Query query, String relatedPartyId) {
        if (Objects.nonNull(relatedPartyId)) {
            query.addCriteria(Criteria.where("relatedParty.id").is(relatedPartyId));
        }
    }

    private static void setCreationDate(Query query, OffsetDateTime creationDateStart, OffsetDateTime creationDateEnd) {
        if (query.getQueryObject().containsKey(CREATION_DATE)) {
            return;
        }
        Criteria dateCriteria = new Criteria(CREATION_DATE);
        if (Objects.nonNull(creationDateStart)) {
            dateCriteria.gte(creationDateStart);
        }
        if (Objects.nonNull(creationDateEnd) && Objects.nonNull(creationDateStart)) {
            dateCriteria.andOperator(new Criteria(CREATION_DATE).lte(creationDateEnd));
        } else if (Objects.nonNull(creationDateEnd)) {
            dateCriteria.lte(creationDateEnd);
        }
        if (Objects.nonNull(creationDateStart) || Objects.nonNull(creationDateEnd)) {
            query.addCriteria(dateCriteria);
        }
    }

    private static void setLastModifiedDate(Query query, OffsetDateTime modificationDateStart, OffsetDateTime modificationDateEnd) {
        if (query.getQueryObject().containsKey(MODIFICATION_DATE)) {
            return;
        }
        Criteria dateCriteria = new Criteria(MODIFICATION_DATE);
        if (modificationDateStart != null) {
            dateCriteria.gte(modificationDateStart);
        }
        if (Objects.nonNull(modificationDateEnd) && Objects.nonNull(modificationDateStart)) {
            dateCriteria.andOperator(new Criteria(MODIFICATION_DATE).lte(modificationDateEnd));
        } else if (Objects.nonNull(modificationDateEnd)) {
            dateCriteria.lte(modificationDateEnd);
        }
        if (Objects.nonNull(modificationDateStart) || Objects.nonNull(modificationDateEnd)) {
            query.addCriteria(dateCriteria);
        }
    }

    private static void setState(Query query, String state) {
        if (Objects.nonNull(state)) {

            query.addCriteria(Criteria.where(FalloutIncident.Fields.state).is(state));
        }
    }

    private static void setId(Query query, String id) {
        if (Objects.nonNull(id)) {
            query.addCriteria(Criteria.where(FalloutIncident.Fields.id).is(id));
        }
    }


}
