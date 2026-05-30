// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service.impl;

import com.orange.discobole.admin.*;
import com.orange.discobole.productcatalog.policyrule.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import com.orange.discobole.productcatalog.policyrule.dto.*;
import com.orange.discobole.productcatalog.policyrule.enums.PolicyRuleState;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.handler.RestTemplateResponseErrorHandler;
import com.orange.discobole.productcatalog.policyrule.repository.*;
import com.orange.discobole.productcatalog.policyrule.service.PolicyRuleService;
import com.orange.discobole.productcatalog.policyrule.service.ProductCatalogQueryService;
import com.orange.discobole.productcatalog.policyrule.service.RedisService;
import com.orange.discobole.productcatalog.policyrule.utils.QueryParamUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class PolicyRuleServiceImpl implements PolicyRuleService {
    private static final Logger LOGGER = LogManager.getLogger(PolicyRuleServiceImpl.class);
    private final PolicyRuleRepository policyRuleRepository;
    private final PolicyDomainRepository policyDomainRepository;
    private final PolicyEventRepository policyEventRepository;
    private final PolicyActionOperationRepository policyActionOperationRepository;
    private final PolicyConditionRepository policyConditionRepository;
    private final ProductCatalogQueryService productCatalogQueryService;
    private final ConfigurableProperties configurableProperties;
    private final MongoTemplate mongoTemplate;
    @Autowired
    private StreamBridge bridge;
    @Resource
    private RedisService redisService;

    @Autowired
    public PolicyRuleServiceImpl(PolicyRuleRepository policyRuleRepository, PolicyDomainRepository policyDomainRepository, PolicyEventRepository policyEventRepository, PolicyActionOperationRepository policyActionOperationRepository, PolicyConditionRepository policyConditionRepository, ProductCatalogQueryService productCatalogQueryService, ConfigurableProperties configurableProperties, MongoTemplate mongoTemplate) {
        this.policyRuleRepository = policyRuleRepository;
        this.policyDomainRepository = policyDomainRepository;
        this.policyEventRepository = policyEventRepository;
        this.policyActionOperationRepository = policyActionOperationRepository;
        this.policyConditionRepository = policyConditionRepository;
        this.productCatalogQueryService = productCatalogQueryService;
        this.configurableProperties = configurableProperties;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public PolicyRule createPolicyRule(PolicyRule policyRule) {
        if (policyRule.getPolicyDomain()== null || policyRule.getPolicyDomain().isEmpty()) {
            throw new MissingBodyFieldException(23, "Missing Body PolicyDomain", "Missing Body PolicyDomain");
        }
        isValidPolicyRule(policyRule);
        policyRule.setState(PolicyRuleState.INTEST.getValue());
        policyRule.setVersion("1.0");
        policyRule.setId(String.valueOf(UUID.randomUUID()));
        policyRule.setCreationDate(LocalDateTime.now());
        policyRule.setLastUpdate(LocalDateTime.now());
        PolicyRule policyRuleData = policyRuleRepository.save(policyRule);
        String href = configurableProperties.getCatprodcaturl() +"?id="+ policyRuleData.getId();
        policyRuleData.setHref(href);
        return policyRuleRepository.save(policyRuleData);
    }

    private void isValidPolicyRule(PolicyRule policyRule) {
        PolicyDomainRef policyDomain = policyRule.getPolicyDomain().get(0);
        Optional<PolicyDomainRef> policyDomainDB = policyDomainRepository.findById(policyDomain.getId());
        if(policyDomainDB.isEmpty()){
            throw new MissingBodyFieldException(60, "policyDomain is not found", "policyDomain is not found");
        }
        PolicyEventRef policyEvent = policyRule.getPolicyEvent();
        if(policyEvent != null){
            Optional<PolicyEvent> policyEventDB = policyEventRepository.findById(policyEvent.getId());
            if(policyEventDB.isEmpty()){
                throw new MissingBodyFieldException(60, "PolicyEvent is not found", "PolicyEvent is not found");
            }
        }else if(PolicyRuleState.ACTIVE.getValue().equals(policyRule.getState())){
            throw new MissingBodyFieldException(23, "Missing Body PolicyEvent", "Missing Body PolicyEvent");
        }
        if(!CollectionUtils.isEmpty(policyRule.getPolicyAction())){
            PolicyActionRef policyActionOperation = policyRule.getPolicyAction().get(0);
            Optional<PolicyActionOperation> policyActionOperationDB = policyActionOperationRepository.findById(policyActionOperation.getId());
            if(policyActionOperationDB.isEmpty()){
                throw new MissingBodyFieldException(60, "PolicyActionOperation is not found", "PolicyActionOperation is not found");
            }
        }else if(PolicyRuleState.ACTIVE.getValue().equals(policyRule.getState())){
            throw new MissingBodyFieldException(23, "Missing Body PolicyAction", "Missing Body PolicyAction");
        }

        PolicyConditionRef policyCondition = policyRule.getPolicyCondition();
        if(policyCondition != null){
            Optional<PolicyCondition> policyConditionDB = policyConditionRepository.findById(policyCondition.getId());
            if(policyConditionDB.isEmpty()){
                throw new MissingBodyFieldException(60, "PolicyCondition is not found", "PolicyCondition is not found");
            }
        }else if(PolicyRuleState.ACTIVE.getValue().equals(policyRule.getState())){
            throw new MissingBodyFieldException(23, "Missing Body PolicyCondition", "Missing Body PolicyCondition");
        }
    }

    @Override
    public List<PolicyRule> getAllPolicyRule(Long skip, Long limit) {
        try {
            if (skip == null && limit == null) {
                return mongoTemplate.findAll(PolicyRule.class);
            }
            List<AggregationOperation> aggregations = new ArrayList<>();
            if (skip != null) {
                aggregations.add(Aggregation.skip(skip));
            }
            if (limit != null) {
                aggregations.add(Aggregation.limit(limit));
            }
            CompletableFuture<List<PolicyRule>> futureResult = CompletableFuture.supplyAsync(() -> {
                Aggregation aggregation = Aggregation.newAggregation(aggregations);
                return mongoTemplate.aggregate(aggregation, "policyRule", PolicyRule.class).getMappedResults();
            });
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching policy rules", e);
        }
    }


    @Override
    public PolicyRule getPolicyRuleById(String policyRuleId) {
        Optional<PolicyRule> policyRule = this.policyRuleRepository.findById(policyRuleId);
        if (policyRule.isPresent()) {
            return policyRule.get();
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_RULE_NOT_FOUND + policyRuleId, PolicyRuleConstants.POLICY_RULE_NOT_FOUND + policyRuleId);
        }
    }

    @Override
    public PolicyRule updatePolicyRule(PolicyRule policyRule) {
        Optional<PolicyRule> policyRuleDB = policyRuleRepository.findById(policyRule.getId());
        if(policyRuleDB.isPresent()){
            PolicyRule policyRuleUpdate = policyRuleDB.get();
            if (policyRuleUpdate.getState().equals(PolicyRuleState.ACTIVE.getValue())) {
                if (policyRule.getState().equals(PolicyRuleState.TERMINATED.getValue())) {
                    policyRuleUpdate.setState(PolicyRuleState.TERMINATED.getValue());
                    policyRuleUpdate.setLastUpdate(LocalDateTime.now());
                    return policyRuleRepository.save(policyRuleUpdate);
                } else {
                    throw new MissingBodyFieldException(23, "Only 'State' Can Be Modified If PolicyRule is in Active State", "Only 'State' Can Be Modified If PolicyRule is in Active State");
                }
            }else if(policyRuleUpdate.getState().equals(PolicyRuleState.TERMINATED.getValue())){
                throw new MissingBodyFieldException(23, "PolicyRule Can Not Be Modified In Terminated State", "PolicyRule Can Not Be Modified In Terminated State");
            }
            isValidPolicyRuleToUpdate(policyRuleUpdate);
            isValidPolicyRule(policyRule);
            String state = policyRule.getState();
            if(policyRule.getState() == null || policyRule.getState().isBlank()){
                throw new MissingBodyFieldException(23, "Missing Body PolicyRule State", "Missing Body PolicyRule State");
            }
            if(!(state.equals(PolicyRuleState.INTEST.getValue()))  && !(state.equals(PolicyRuleState.ACTIVE.getValue())) && !(state.equals(PolicyRuleState.TERMINATED.getValue()))){
                throw new MissingBodyFieldException(23, "Invalid Policy Rule State", "Invalid Policy Rule State");
            }
            policyRuleUpdate.setState(policyRule.getState());
            policyRuleUpdate.setPolicyAction(policyRule.getPolicyAction());
            policyRuleUpdate.setPolicyCondition(policyRule.getPolicyCondition());
            policyRuleUpdate.setPolicyEvent(policyRule.getPolicyEvent());
            policyRuleUpdate.setPolicyDomain(policyRule.getPolicyDomain());
            policyRuleUpdate.setDescription(policyRule.getDescription());
            policyRuleUpdate.setAtSchemaLocation(policyRule.getAtSchemaLocation());
            policyRuleUpdate.setAtBaseType(policyRule.getAtBaseType());
            policyRuleUpdate.setAtType(policyRule.getAtType());
            policyRuleUpdate.setExecutionStrategy(policyRule.getExecutionStrategy());
            policyRuleUpdate.setIsConjustiveNormalForm(policyRule.getIsConjustiveNormalForm());
            policyRuleUpdate.setName(policyRule.getName());
            policyRuleUpdate.setNote(policyRule.getNote());
            policyRuleUpdate.setSequencedValue(policyRule.getSequencedValue());
            policyRuleUpdate.setHref(policyRule.getHref());
            policyRuleUpdate.setRelatedParty(policyRule.getRelatedParty());
            policyRuleUpdate.setSequencedAction(policyRule.getSequencedAction());
            policyRuleUpdate.setLastUpdate(LocalDateTime.now());
            return policyRuleRepository.save(policyRuleUpdate);
        }else{
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_RULE_NOT_FOUND+policyRule.getId(), PolicyRuleConstants.POLICY_RULE_NOT_FOUND+policyRule.getId());
        }
    }

    public void isValidPolicyRuleToUpdate(PolicyRule policyRuleUpdate) {
        if(!policyRuleUpdate.getState().equals(PolicyRuleConstants.IN_TEST)){
            throw new MissingBodyFieldException(23, PolicyRuleConstants.POLICYRULE_MUST_BE_INSTEST_STATE, PolicyRuleConstants.POLICYRULE_MUST_BE_INSTEST_STATE);
        }
        List<ProductOffering> productOfferings = productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRuleUpdate.getId());
        List<ProductSpecification> productSpecifications = productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRuleUpdate.getId());
        // Policy Rule can be updated even if policy rule is not associated with any PO/PS
       if(productOfferings != null && !productOfferings.isEmpty()){
           productOfferings = productOfferings.stream().filter(productOffering -> (!productOffering.getLifecycleStatus().equals(PolicyRuleConstants.IN_TEST)) && !productOffering.getLifecycleStatus().equals("active")).collect(Collectors.toList());
           if(!productOfferings.isEmpty()){
               throw new MissingBodyFieldException(23, "ProductOfferings Must Be inTest or Active State", "ProductOfferings Must Be inTest or Active State");
           }
       }
       if(productSpecifications != null && !productSpecifications.isEmpty()){
           productSpecifications = productSpecifications.stream().filter(productSpecification -> (!productSpecification.getLifecycleStatus().equals(PolicyRuleConstants.IN_TEST)) && (!productSpecification.getLifecycleStatus().equals("active"))).collect(Collectors.toList());
           if(!productSpecifications.isEmpty()){
               throw new MissingBodyFieldException(23, "ProductSpecifications Must Be inTest or Active State", "ProductSpecifications Must Be inTest or Active State");
           }
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePolicyRule(String policyRuleId) {
        Optional<PolicyRule> policyRule = this.policyRuleRepository.findById(policyRuleId);
        if (policyRule.isPresent()) {
            if (!isValidPolicyRuleToDelete(policyRule.get())) {
                throw new IllegalStateException(
                        "PolicyRule cannot be deleted due to business validation failure: " + policyRuleId
                );
            }
            List<String> cachedKeys = getCachedKeys(policyRule.get());
            try {
                PolicyRule rule = policyRule.get();
                // DB delete (safe even if concurrent delete happens)
                this.policyRuleRepository.deleteById(rule.getId());
                Optional.ofNullable(rule.getPolicyCondition()).map(PolicyConditionRef::getId).ifPresent(policyConditionRepository::deleteById);
                Optional.ofNullable(rule.getPolicyAction()).orElse(Collections.emptyList()).stream().map(PolicyActionRef::getId).forEach(policyActionOperationRepository::deleteById);
            } catch (EmptyResultDataAccessException ex) {
                // Another request already deleted it → treat as success
                return;
            }
            // External delete should be idempotent
            try {
                Event event =new Event();
                event.setEventId(UUID.randomUUID().toString());
                event.setEventTime(LocalDateTime.now());
                PolicyOfferingEvent policyOfferingEvent = new PolicyOfferingEvent();
                policyOfferingEvent.setPolciyRuleId(policyRuleId);
                List<ProductOffering> offerings = productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRuleId);
                if(ObjectUtils.isEmpty(offerings)){
                    offerings = new ArrayList<>();
                }
                policyOfferingEvent.setProductOfferingList(offerings);
                event.setEvent(policyOfferingEvent);
                bridge.send("policyNotification-out-0", MessageBuilder.withPayload(event)
                        .setHeader("partitionKey", event.getEventId()).build());
            } catch (Exception ex) {
                // propagate to rollback DB delete
                throw ex;
            }
            try {
                if(redisService != null){
                    redisService.deletePolicyRule(cachedKeys);
                }
            }catch (Exception ex) {
                // Log and ignore cache delete failure, as DB delete has been done and cache will eventually be consistent
                // (especially with Redis expiration)
                LOGGER.error("Failed to delete cache for policy rule {} : {}",   policyRuleId, ex.getMessage());
            }
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICY_RULE_NOT_FOUND+policyRuleId, PolicyRuleConstants.POLICY_RULE_NOT_FOUND+policyRuleId);
        }
    }

    private List<String> getCachedKeys(PolicyRule policyRule) {
        List<String> keys = new ArrayList<>();
        keys.add("policy-rule-" + policyRule.getId());
        Optional.ofNullable(policyRule.getPolicyCondition()).map(PolicyConditionRef::getId).map(id -> "policy-condition-" + id).ifPresent(keys::add);
        Optional.ofNullable(policyRule.getPolicyEvent()).map(PolicyEventRef::getId).map(id -> "policy-event-" + id).ifPresent(keys::add);
        Optional.ofNullable(policyRule.getPolicyAction()).orElse(Collections.emptyList()).stream().map(PolicyActionRef::getId).map(id -> "policy-action-" + id).forEach(keys::add);
        return keys;
    }

    @Override
    public List<PolicyRule> getPolicyRuleByIds(List<String> ids) {
        return this.policyRuleRepository.findAllById(ids);
    }

    @Override
    public List<ProductOfferingPrice> getPOPByPO(String poId) {
        ProductOffering productOffering = productCatalogQueryService.fetchPOById(poId);
        if(ObjectUtils.isEmpty(productOffering)){
            return Collections.emptyList();
        }
        List<PolicyRuleRef> policyRuleRefs = productOffering.getPolicyRuleRef();
        if (CollectionUtils.isEmpty(policyRuleRefs)) {
            return Collections.emptyList();
        }
        List<String> popIds = policyRuleRefs.stream()
                .map(ref -> policyRuleRepository.findById(ref.getId()).orElse(null))
                .filter(Objects::nonNull)
                .flatMap(policyRule -> policyRule.getPolicyAction().stream())
                .map(action -> policyActionOperationRepository.findById(action.getId()).orElse(null))
                .filter(Objects::nonNull)
                .flatMap(policyActionOperation -> {
                    List<String> ids = new ArrayList<>();
                    // Extract from path if present
                    String path = policyActionOperation.getPath();
                    if (StringUtils.hasText(path)) {
                        String popIdFromPath = path.replaceAll(".*'([0-9a-fA-F\\-]{36})'.*", "$1");
                        ids.add(popIdFromPath);
                    }
                    // Extract from actionValue if present
                    if (policyActionOperation.getActionValue() != null
                            && policyActionOperation.getActionValue().getValue() != null
                            && policyActionOperation.getActionValue().getValue().getObjectId() != null) {
                        ids.add(policyActionOperation.getActionValue().getValue().getObjectId());
                    }
                    return ids.stream();
                })
                .distinct() // avoid duplicates
                .toList();

        if (popIds.isEmpty()) {
            return Collections.emptyList();
        }
        return productCatalogQueryService.fetchProductOfferingPriceByIds(popIds);
    }

    @Override
    public Map<String, Object> fetchPolicyRuleWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields) {
        return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, "policyRule", PolicyRule.class, mongoTemplate);

    }

    public boolean isValidPolicyRuleToDelete(PolicyRule policyRule) {
        if(!policyRule.getState().equals(PolicyRuleConstants.IN_TEST)){
            throw new MissingBodyFieldException(23, PolicyRuleConstants.POLICYRULE_MUST_BE_INSTEST_STATE, PolicyRuleConstants.POLICYRULE_MUST_BE_INSTEST_STATE);
        }
        List<ProductOffering> productOfferings = productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId());
        List<ProductSpecification> productSpecifications = productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId());
        if(productOfferings != null && !productOfferings.isEmpty()){
            productOfferings = productOfferings.stream().filter(productOffering -> productOffering.getLifecycleStatus().equals("launched")).collect(Collectors.toList());
            if(!productOfferings.isEmpty()){
                throw new MissingBodyFieldException(HttpStatus.OK, 99, PolicyRuleConstants.POLICYRULE_CANNOT_BE_DELETED, PolicyRuleConstants.POLICYRULE_CANNOT_BE_DELETED );
            }
        }
        if(productSpecifications != null && !productSpecifications.isEmpty()){
            productSpecifications = productSpecifications.stream().filter(productSpecification -> productSpecification.getLifecycleStatus().equals("launched")).collect(Collectors.toList());
            if(!productSpecifications.isEmpty()){
                throw new MissingBodyFieldException(HttpStatus.OK, 99, PolicyRuleConstants.POLICYRULE_CANNOT_BE_DELETED, PolicyRuleConstants.POLICYRULE_CANNOT_BE_DELETED);
            }
        }
        return true;
    }
}
