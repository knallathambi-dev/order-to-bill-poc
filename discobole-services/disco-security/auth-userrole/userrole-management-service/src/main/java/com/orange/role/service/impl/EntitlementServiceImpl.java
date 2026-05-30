// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service.impl;

import com.orange.discobole.permission.Entitlement;
import com.orange.discobole.permission.UserRole;
import com.orange.role.handler.DiscoClientException;
import com.orange.role.service.EntitlementService;
import com.orange.role.util.QueryParamUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Service
public class EntitlementServiceImpl implements EntitlementService {

    private static final Logger LOGGER = LogManager.getLogger(EntitlementServiceImpl.class);
    public static final String INVOLVEMENT_ROLE = "involvementRole";

    @Value("${spring.keycloakUrl}")
    private String keycloakUrl;

    @Value("${spring.keycloakclientId}")
    private String clientId;

    @Value("${spring.realm}")
    private String realm;

    private MongoTemplate mongoTemplate;

    @Autowired
    public EntitlementServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void deleteEntitlement(String id, String token) {
        try {
            Query query = Query.query(Criteria.where("id").is(id));
            mongoTemplate.remove(query, Entitlement.class);
        } catch (Exception e) {
            throw new DiscoClientException("Failed to delete " + e.getMessage());
        }
    }


    /**
     * @param entitlement
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public List<Entitlement> createEntitlement(List<Entitlement> entitlements, String token) throws IOException {

        try {
            return (List<Entitlement>) mongoTemplate.insertAll(entitlements);
        } catch (Exception e) {
            LOGGER.error("Failed to save list of entitlements {} " , e.getMessage());
            throw new DiscoClientException("Failed to save list of entitlements: " + e.getMessage());
        }

    }

    /**
     * @param entitlement
     * @param token
     * @return
     * @throws IOException
     */

    @Override
    public Entitlement fetchEntitlementById(String id) {
        return mongoTemplate.findById(id, Entitlement.class);
    }

    @Override
    public Entitlement fetchEntitlementById(final String id, List<String> fieldList) {
        Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
        return mongoTemplate.findOne(query, Entitlement.class);
    }

    public List<Entitlement> fetchEntitlements(final Map<String, Object> requestParams, Long skip, Long limit, String fields)
            throws UnsupportedEncodingException {
        ArrayList<AggregationOperation> aggregations = new ArrayList<>();
        MatchOperation filter = null;
        SortOperation sortBy = null;
        Aggregation aggregation = null;
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);


        Criteria criteria = new Criteria();
        boolean criteriaInitialized = false;

        for (Map.Entry<String, Set<Object>> paramElement : params.entrySet()) {
            String key = paramElement.getKey();
            Set<Object> values = paramElement.getValue();


            List<Pattern> caseInsensitivePatterns = values.stream()
                    .map(value -> Pattern.compile((String) value, Pattern.CASE_INSENSITIVE))
                    .collect(Collectors.toList());
            if (!criteriaInitialized) {
                Criteria.where(key).in(caseInsensitivePatterns);
                criteriaInitialized = true;
            }
            criteria = criteria.and(key).in(caseInsensitivePatterns);


        }
        if (criteria != null) {
            filter = match(criteria);
        }

        if (requestParams.containsKey("sort") && requestParams.get("sort") != null) {
            String[] values = requestParams.get("sort").toString().split(",");
            Set<Object> sortParams = new LinkedHashSet<>(Arrays.asList(values));
            List<Sort.Order> orders = QueryParamUtil.sortParameters(UserRole.class, sortParams);
            sortBy = sort(Sort.by(orders));
        }
        if (filter != null && sortBy != null) {
            aggregations.addAll(List.of(filter, sortBy));
        } else if (sortBy != null) {
            aggregations.add(sortBy);
        } else if (filter != null) {
            aggregations.add(filter);
        }
        if (skip != null) {
            aggregations.add(Aggregation.skip(skip));
        }
        if (fields != null) {
            aggregations.add(Aggregation.project(fields.split(",")));
        }
        if (limit != null) {
            aggregations.add(Aggregation.limit(limit));
        }

        if (!aggregations.isEmpty()) {
            aggregation = Aggregation.newAggregation(aggregations);

        }

        return aggregation != null ? mongoTemplate.aggregate(aggregation, "entitlement", Entitlement.class).getMappedResults()
                : mongoTemplate.findAll(Entitlement.class);
    }

    /**
     * @param entitlement
     * @param id
     */
    @Override
    public Entitlement updateEntitlement(Entitlement entitlement, String id) {
        return mongoTemplate.save(entitlement);
    }

    @Override
    public long countEntitlement(Map<String, Object> requestParams) {
        Query query = new Query();
        if (requestParams.get("_id") != null) {
            query.addCriteria(Criteria.where("_id").is(requestParams.get("_id")));
        }
        if (requestParams.get(INVOLVEMENT_ROLE) != null) {
            query.addCriteria(Criteria.where(INVOLVEMENT_ROLE).is(requestParams.get(INVOLVEMENT_ROLE)));
        }
        if (requestParams.get("type") != null) {
            query.addCriteria(Criteria.where("type").is(requestParams.get("type")));
        }
        return mongoTemplate.count(query, Entitlement.class);
    }

}
