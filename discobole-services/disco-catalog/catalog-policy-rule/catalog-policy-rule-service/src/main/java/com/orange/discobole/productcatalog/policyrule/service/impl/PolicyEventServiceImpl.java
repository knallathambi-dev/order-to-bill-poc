// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service.impl;

import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.admin.PolicyRule;
import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyEventRepository;
import com.orange.discobole.productcatalog.policyrule.service.PolicyEventService;
import com.orange.discobole.productcatalog.policyrule.service.ProductCatalogQueryService;
import com.orange.discobole.productcatalog.policyrule.utils.QueryParamUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PolicyEventServiceImpl implements PolicyEventService {

    private final PolicyEventRepository policyEventRepository;
    private final ProductCatalogQueryService productCatalogQueryService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PolicyEventServiceImpl(PolicyEventRepository policyEventRepository, ProductCatalogQueryService productCatalogQueryService, MongoTemplate mongoTemplate) {
        this.policyEventRepository = policyEventRepository;
        this.productCatalogQueryService = productCatalogQueryService;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public PolicyEvent createPolicyEvent(PolicyEvent policyEvent) {
        if (policyEvent.getQuery() == null || policyEvent.getQuery().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.SELECT_A_VALID_PRODUCT_OFFERING, PolicyRuleConstants.SELECT_A_VALID_PRODUCT_OFFERING);
        }
        String query = policyEvent.getQuery();
        String[] queryResult = null;
        if(query.contains("productOffering")){
            queryResult = query.split("productConfiguration.productOffering.id==");
            String[] poIdArray = queryResult[1].split("&&");
            String poId = splitString(poIdArray[0]);
            String poIdCatalog = productCatalogQueryService.fetchProductOfferingById(poId);
            if(!poId.equals(poIdCatalog)){
              throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
            }
        }else if(query.contains("productSpecification")){
            queryResult = query.split("productConfiguration.productSpecification.id==");
            String[] psIdArray = queryResult[1].split("&&");
            String psId = splitString(psIdArray[0]);
            String psIdCatalog = productCatalogQueryService.fetchProductSpecificationById(psId);
            if(!psId.equals(psIdCatalog)){
                throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
            }
        }else{
            throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
        }
        policyEvent.setCreationDate(LocalDateTime.now());
        policyEvent.setLastUpdate(LocalDateTime.now());
        return policyEventRepository.save(policyEvent);
    }

    private String splitString(String string) {
        string = string.replaceAll("[')]", "");
        string = string.replace("\\)", "");
        return string.trim();
    }

    @Override
    public List<PolicyEvent> getAllPolicyEvent(Long skip,Long limit) {
        ArrayList<AggregationOperation> aggregations = new ArrayList<>();
        Aggregation aggregation = null;

        if (skip != null) {
            aggregations.add(Aggregation.skip(skip));
        }
        if (limit != null) {
            aggregations.add(Aggregation.limit(limit));
        }

        if (!aggregations.isEmpty()) {
            aggregation = Aggregation.newAggregation(aggregations);
        }

        return aggregation != null
                ? mongoTemplate.aggregate(aggregation, "policyEvent", PolicyEvent.class).getMappedResults()
                : mongoTemplate.findAll(PolicyEvent.class);
    }

    @Override
    public PolicyEvent getPolicyEventById(String policyEventId) {
        Optional<PolicyEvent> policyEvent = this.policyEventRepository.findById(policyEventId);
        if (policyEvent.isPresent()) {
            return policyEvent.get();
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND);
        }
    }

    @Override
    public PolicyEvent updatePolicyEvent(PolicyEvent policyEvent) {
        if (policyEvent.getQuery() == null || policyEvent.getQuery().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.SELECT_A_VALID_PRODUCT_OFFERING, PolicyRuleConstants.SELECT_A_VALID_PRODUCT_OFFERING);
        }
        Optional<PolicyEvent> policyEventOptional = this.policyEventRepository.findById(policyEvent.getId());
        if(policyEventOptional.isPresent()){
            PolicyEvent policyEventDB = policyEventOptional.get();
            String query = policyEvent.getQuery();
            String[] queryResult = null;
            if(query.contains("productOffering")){
                queryResult = query.split("productConfiguration.productOffering.id==");
                String[] poIdArray = queryResult[1].split("&&");
                String poId = splitString(poIdArray[0]);
                String poIdCatalog = productCatalogQueryService.fetchProductOfferingById(poId);
                if(!poId.equals(poIdCatalog)){
                    throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
                }
            }else if(query.contains("productSpecification")){
                queryResult = query.split("productConfiguration.productSpecification.id==");
                String[] psIdArray = queryResult[1].split("&&");
                String psId = splitString(psIdArray[0]);
                String psIdCatalog = productCatalogQueryService.fetchProductOfferingById(psId);
                if(!psId.equals(psIdCatalog)){
                    throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
                }
            }else{
                throw new MissingBodyFieldException(60, PolicyRuleConstants.PO_NOT_FOUND, PolicyRuleConstants.PO_NOT_FOUND);
            }
            policyEventDB.setEvent(policyEvent.getEvent());
            policyEventDB.setAtType(policyEvent.getAtType());
            policyEventDB.setHref(policyEvent.getHref());
            policyEventDB.setName(policyEvent.getName());
            policyEventDB.setAtSchemaLocation(policyEvent.getAtSchemaLocation());
            policyEventDB.setAtBaseType(policyEvent.getAtBaseType());
            policyEventDB.setDescription(policyEvent.getDescription());
            policyEventDB.setQuery(policyEvent.getQuery());
            policyEventDB.setLastUpdate(LocalDateTime.now());
            return policyEventRepository.save(policyEventDB);
        }else{
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND);
        }
    }

    @Override
    public void deletePolicyEvent(String policyEventId) {
        Optional<PolicyEvent> policyEvent = this.policyEventRepository.findById(policyEventId);

        if (policyEvent.isPresent()) {
            this.policyEventRepository.delete(policyEvent.get());
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND, PolicyRuleConstants.POLICY_EVENT_NOT_FOUND);
        }
    }

    @Override
    public List<PolicyEvent> getPolicyEventByIds(List<String> ids) {
        return this.policyEventRepository.findAllById(ids);
    }

    @Override
    public Map<String, Object> fetchPolicyEventWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields) {
        return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, "policyEvent", PolicyEvent.class, mongoTemplate);
    }
}
