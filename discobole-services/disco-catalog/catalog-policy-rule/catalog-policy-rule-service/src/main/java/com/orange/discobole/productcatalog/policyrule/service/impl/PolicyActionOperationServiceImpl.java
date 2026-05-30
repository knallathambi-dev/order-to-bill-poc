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
import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyActionOperationRepository;
import com.orange.discobole.productcatalog.policyrule.service.PolicyActionOperationService;
import com.orange.discobole.productcatalog.policyrule.service.ProductCatalogQueryService;
import com.orange.discobole.productcatalog.policyrule.utils.QueryParamUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PolicyActionOperationServiceImpl implements PolicyActionOperationService {

    private final PolicyActionOperationRepository policyActionOperationRepository;
    private final ProductCatalogQueryService productCatalogQueryService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PolicyActionOperationServiceImpl(
            PolicyActionOperationRepository policyActionOperationRepository, ProductCatalogQueryService productCatalogQueryService, MongoTemplate mongoTemplate) {
        this.policyActionOperationRepository = policyActionOperationRepository;
        this.productCatalogQueryService = productCatalogQueryService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PolicyActionOperation createPolicyActionOperation(PolicyActionOperation policyActionOperation) {
        if (policyActionOperation == null || policyActionOperation.getFields() == null || policyActionOperation.getFields().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_POLICYACTIONOPERATION_FIELDS, PolicyRuleConstants.MISSING_BODY_POLICYACTIONOPERATION_FIELDS);
        }
        PolicyAction policyAction = policyActionOperation.getPolicyAction().get(0);
        if (policyAction.getActionStrategy() == null || policyAction.getActionStrategy().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_POLICYACTION_ACTIONSTRATEGY, PolicyRuleConstants.MISSING_BODY_POLICYACTION_ACTIONSTRATEGY);
        }
        policyAction.setCreationDate(LocalDateTime.now());
        policyAction.setId(UUID.randomUUID().toString());
        if(policyAction.getValidFor() == null){
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setStartDateTime(LocalDateTime.now());
            policyAction.setValidFor(timePeriod);
        }
        policyActionOperation.setCreationDate(LocalDateTime.now());
        if (policyActionOperation.getValidFor() == null) {
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setStartDateTime(LocalDateTime.now());
            policyActionOperation.setValidFor(timePeriod);
        }
        if(policyAction.getNote() != null && !policyActionOperation.getNote().isEmpty() && policyAction.getNote().get(0) != null){
            policyAction.getNote().get(0).setId(UUID.randomUUID().toString());
        }
        if(policyActionOperation.getActionValue() == null){
            throw new MissingBodyFieldException(23, "Missing Body PolicyActionOperation ActionValue", "Missing Body PolicyActionOperation ActionValue");
        }else{
            setPolicyActionValue(policyActionOperation);
        }
        if(policyActionOperation.getNote() != null && !policyActionOperation.getNote().isEmpty() && policyActionOperation.getNote().get(0) != null){
            policyActionOperation.getNote().get(0).setId(UUID.randomUUID().toString());
        }
        policyActionOperation.setLastUpdate(LocalDateTime.now());
        return policyActionOperationRepository.save(policyActionOperation);
    }

    private void setPolicyActionValue(PolicyActionOperation policyActionOperation) {
        PolicyActionValue actionValue = policyActionOperation.getActionValue();
        Value value = actionValue.getValue();
        if(value != null && value.getObjectId() != null && !value.getObjectId().isBlank() && value.getObjectType() != null && value.getObjectName() != null){
            String popId = value.getObjectId();
           String popIdCatalog =  productCatalogQueryService.fetchProductofferingPriceById(popId);
            if(!popId.equals(popIdCatalog)){
                throw new MissingBodyFieldException(60, PolicyRuleConstants.POP_NOT_FOUND, PolicyRuleConstants.POP_NOT_FOUND);
            }
        }else{
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE);
        }
    }

    @Override
    public List<PolicyActionOperation> getAllPolicyActionOperation(Long skip,Long limit) {
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
                ? mongoTemplate.aggregate(aggregation, "policyActionOperation", PolicyActionOperation.class).getMappedResults()
                : mongoTemplate.findAll(PolicyActionOperation.class);
    }

    @Override
    public PolicyActionOperation getPolicyActionOperationById(String policyActionOperationId) {
        Optional<PolicyActionOperation> policyActionOperation = this.policyActionOperationRepository.findById(policyActionOperationId);
        if (policyActionOperation.isPresent()) {
            return policyActionOperation.get();
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND);
        }
    }

    @Override
    public PolicyActionOperation updatePolicyActionOperation(PolicyActionOperation policyActionOperation) {
        if (policyActionOperation == null || policyActionOperation.getFields() == null || policyActionOperation.getFields().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_POLICYACTIONOPERATION_FIELDS, PolicyRuleConstants.MISSING_BODY_POLICYACTIONOPERATION_FIELDS);
        }
        PolicyAction policyAction = policyActionOperation.getPolicyAction().get(0);
        if (policyAction.getActionStrategy() == null || policyAction.getActionStrategy().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_POLICYACTION_ACTIONSTRATEGY, PolicyRuleConstants.MISSING_BODY_POLICYACTION_ACTIONSTRATEGY);
        }
        Optional<PolicyActionOperation> policyActionOperationDB = policyActionOperationRepository.findById(policyActionOperation.getId());
        if (policyActionOperationDB.isPresent()) {
            PolicyActionOperation policyActionOperationUpdate = policyActionOperationDB.get();
            updatePolicyActionOperation(policyActionOperationUpdate, policyActionOperation);
            policyActionOperationUpdate.setLastUpdate(LocalDateTime.now());
            return policyActionOperationRepository.save(policyActionOperationUpdate);
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND);
        }
    }

    public void updatePolicyActionOperation(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
        if (policyActionOperationUpdate.getPolicyAction() != null && !policyActionOperationUpdate.getPolicyAction().isEmpty()) {
            updateOldPolicyAction(policyActionOperationUpdate, policyActionOperation);
        } else {
           setNewPolicyAction(policyActionOperationUpdate, policyActionOperation);
        }
        if(policyActionOperationUpdate.getNote() != null && !policyActionOperationUpdate.getNote().isEmpty()){
            updateOperationOldNote(policyActionOperationUpdate, policyActionOperation);
        }else{
            setOperationNewNote(policyActionOperationUpdate, policyActionOperation);
        }
        if(policyActionOperationUpdate.getActionValue() != null){
            updateOldActionValue(policyActionOperationUpdate, policyActionOperation);
        }else{
            setNewActionValue(policyActionOperationUpdate, policyActionOperation);
        }
        policyActionOperationUpdate.setOperation(policyActionOperation.getOperation());
        policyActionOperationUpdate.setActionSequence(policyActionOperation.getActionSequence());
        policyActionOperationUpdate.setActionStrategy(policyActionOperation.getActionStrategy());
        policyActionOperationUpdate.setActionCondition(policyActionOperation.getActionCondition());
        policyActionOperationUpdate.atBaseType(policyActionOperation.getAtBaseType());
        policyActionOperationUpdate.setAtSchemaLocation(policyActionOperation.getAtSchemaLocation());
        policyActionOperationUpdate.setAtType(policyActionOperation.getAtType());
        policyActionOperationUpdate.setDescription(policyActionOperation.getDescription());
        policyActionOperationUpdate.setFields(policyActionOperation.getFields());
        policyActionOperationUpdate.setFilter(policyActionOperation.getFilter());
        policyActionOperationUpdate.setHref(policyActionOperation.getHref());
        policyActionOperationUpdate.setName(policyActionOperation.getName());
        policyActionOperationUpdate.setPath(policyActionOperation.getPath());
        policyActionOperationUpdate.setValidFor(policyActionOperation.getValidFor());
        policyActionOperationUpdate.setVersion(policyActionOperation.getVersion());
    }

    public void setNewActionValue(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
       PolicyActionValue actionValue = policyActionOperation.getActionValue();
       if(actionValue != null && actionValue.getValue()!= null){
           String popId = actionValue.getValue().getObjectId();
           String popIdCatalog = productCatalogQueryService.fetchProductofferingPriceById(popId);
           if(!popId.equals(popIdCatalog)){
               throw new MissingBodyFieldException(60, PolicyRuleConstants.POP_NOT_FOUND, PolicyRuleConstants.POP_NOT_FOUND);
           }
           policyActionOperationUpdate.actionValue(actionValue);
       }else{
           throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE);
       }
    }

    public void updateOldActionValue(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
        PolicyActionValue newActionValue = policyActionOperation.getActionValue();
        if(newActionValue != null && newActionValue.getValue()!= null){
            String popId = newActionValue.getValue().getObjectId();
            String popIdCatalog = productCatalogQueryService.fetchProductofferingPriceById(popId);
            if(!popId.equals(popIdCatalog)){
                throw new MissingBodyFieldException(60, PolicyRuleConstants.POP_NOT_FOUND, PolicyRuleConstants.POP_NOT_FOUND);
            }
            policyActionOperationUpdate.setActionValue(newActionValue);
        }else{
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE, PolicyRuleConstants.MISSING_BODY_ACTIONVALUE_VALUE);
        }
    }

    public void setOperationNewNote(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
        List<Note> notes = new ArrayList<>();
        if (policyActionOperation.getNote() != null && !policyActionOperation.getNote().isEmpty()) {
            Note newNote = policyActionOperation.getNote().get(0);
            newNote.setId(UUID.randomUUID().toString());
            notes.add(newNote);
        }
        policyActionOperationUpdate.setNote(notes);
    }

    public void updateOperationOldNote(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
        Note oldNote = policyActionOperationUpdate.getNote().get(0);
        List<Note> notes = new ArrayList<>();
        if (policyActionOperation.getNote() != null && !policyActionOperation.getNote().isEmpty()) {
            Note newNote = policyActionOperation.getNote().get(0);
            String oldNoteId = oldNote.getId();
            BeanUtils.copyProperties(newNote, oldNote, "id");
            oldNote.setId(oldNoteId);
            notes.add(oldNote);
        }
        policyActionOperationUpdate.setNote(notes);
    }

    public void setNewPolicyAction(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
       List<PolicyAction> policyActions = new ArrayList<>();
        if(policyActionOperation.getPolicyAction() != null && !policyActionOperation.getPolicyAction().isEmpty()){
            PolicyAction policyAction = policyActionOperation.getPolicyAction().get(0);
            policyAction.setId(UUID.randomUUID().toString());
            if(policyAction.getNote() != null && !policyAction.getNote().isEmpty()){
                updateOldNote(policyActionOperation, policyAction);
            }else{
                setNewNote(policyActionOperation, policyAction);
            }
            policyActions.add(policyAction);
        }
        policyActionOperationUpdate.setPolicyAction(policyActions);
    }

    public void updateOldPolicyAction(PolicyActionOperation policyActionOperationUpdate, PolicyActionOperation policyActionOperation) {
        List<PolicyAction> policyActions = new ArrayList<>();
        PolicyAction policyActionUpdate = policyActionOperationUpdate.getPolicyAction().get(0);
        if (policyActionUpdate.getNote() != null && !policyActionUpdate.getNote().isEmpty()) {
            updateOldNote(policyActionOperation, policyActionUpdate);
        } else {
            setNewNote(policyActionOperation, policyActionUpdate);
        }
        if(policyActionOperation.getPolicyAction() != null && !policyActionOperation.getPolicyAction().isEmpty()){
            PolicyAction newPolicyAction = policyActionOperation.getPolicyAction().get(0);
            policyActionUpdate.setActionSequence(newPolicyAction.getActionSequence());
            policyActionUpdate.setActionCondition(newPolicyAction.getActionCondition());
            policyActionUpdate.setAtBaseType(newPolicyAction.getAtBaseType());
            policyActionUpdate.setAtSchemaLocation(newPolicyAction.getAtSchemaLocation());
            policyActionUpdate.setVersion(newPolicyAction.getVersion());
            policyActionUpdate.setValidFor(newPolicyAction.getValidFor());
            policyActionUpdate.setName(newPolicyAction.getName());
            policyActionUpdate.setHref(newPolicyAction.getHref());
            policyActionUpdate.setDescription(newPolicyAction.getDescription());
            policyActionUpdate.setAtType(newPolicyAction.getAtType());
            policyActionUpdate.setActionStrategy(newPolicyAction.getActionStrategy());
            policyActions.add(policyActionUpdate);
            policyActionOperationUpdate.setPolicyAction(policyActions);
        }else{
            policyActionOperationUpdate.setPolicyAction(policyActions);
        }
    }

    public void setNewNote(PolicyActionOperation policyActionOperation, PolicyAction policyActionUpdate) {
        List<Note> notes = new ArrayList<>();
        if (policyActionOperation.getPolicyAction().get(0).getNote() != null && !policyActionOperation.getPolicyAction().get(0).getNote().isEmpty()) {
            Note newNote = policyActionOperation.getPolicyAction().get(0).getNote().get(0);
            newNote.setId(UUID.randomUUID().toString());
            notes.add(newNote);
        }
        policyActionUpdate.setNote(notes);
    }

    public void updateOldNote(PolicyActionOperation policyActionOperation, PolicyAction policyActionUpdate) {
        Note oldNote = policyActionUpdate.getNote().get(0);
        List<Note> notes = new ArrayList<>();
        if (policyActionOperation.getPolicyAction().get(0).getNote() != null && !policyActionOperation.getPolicyAction().get(0).getNote().isEmpty()) {
            Note newNote = policyActionOperation.getPolicyAction().get(0).getNote().get(0);
            String oldNoteId = oldNote.getId();
            BeanUtils.copyProperties(newNote, oldNote, "id");
            oldNote.setId(oldNoteId);
            notes.add(oldNote);
        }
        policyActionUpdate.setNote(notes);
    }

    @Override
    public void deletePolicyActionOperation(String policyActionOperationId) {
        Optional<PolicyActionOperation> policyActionOperation = this.policyActionOperationRepository.findById(policyActionOperationId);

        if (policyActionOperation.isPresent()) {
            this.policyActionOperationRepository.delete(policyActionOperation.get());
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND, PolicyRuleConstants.POLICYACTIONOPERATION_NOT_FOUND);
        }
    }

    @Override
    public List<PolicyActionOperation> getPolicyActionOperationByIds(List<String> ids) {
        return this.policyActionOperationRepository.findAllById(ids);
    }

    @Override
    public Map<String, Object> policyActionOperationWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields) {
        return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, "policyActionOperation", PolicyActionOperation.class, mongoTemplate);
    }
}
