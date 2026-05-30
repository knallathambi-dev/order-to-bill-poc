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
import com.orange.discobole.productcatalog.policyrule.repository.PolicyConditionRepository;
import com.orange.discobole.productcatalog.policyrule.service.PolicyConditionService;
import com.orange.discobole.productcatalog.policyrule.utils.QueryParamUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PolicyConditionServiceImpl implements PolicyConditionService {

    private final PolicyConditionRepository policyConditionRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PolicyConditionServiceImpl(PolicyConditionRepository policyConditionRepository, MongoTemplate mongoTemplate) {
        this.policyConditionRepository = policyConditionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PolicyCondition createPolicyCondition(PolicyCondition policyCondition) {
        setPolicyConditionIds(policyCondition);
        if (policyCondition.getPolicyCondition() != null && !policyCondition.getPolicyCondition().isEmpty()) {
            policyCondition.getPolicyCondition().get(0).setId(UUID.randomUUID().toString());
            setPolicyConditionIds(policyCondition.getPolicyCondition().get(0));
        }
        policyCondition.setCreationDate(LocalDateTime.now());
        if (policyCondition.getIsConjustiveNormalForm() == null) {
            policyCondition.setIsConjustiveNormalForm(true);
        }
        policyCondition.setVersion("1.0");
        policyCondition.setPolicyConditionStrategy("1");
        policyCondition.setLastUpdate(LocalDateTime.now());
        return policyConditionRepository.save(policyCondition);
    }

    private void setPolicyConditionIds(PolicyCondition policyCondition) {
        if (policyCondition == null || policyCondition.getPolicyConditionStatement() == null || policyCondition.getPolicyConditionStatement().isEmpty() || policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable() == null) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_CONDITION_STATEMENT, PolicyRuleConstants.MISSING_CONDITION_STATEMENT);
        }
        String path = policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable().getPath();
        if (path == null || path.isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_CONDITION_STATEMENT, PolicyRuleConstants.MISSING_CONDITION_STATEMENT);
        }
        if (policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue() != null && !policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue().isEmpty()) {
            policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue().get(0).setId(UUID.randomUUID().toString());
        }
        if (policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable() != null) {
            policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable().setId(UUID.randomUUID().toString());
        }
        if (policyCondition.getNote() != null && !policyCondition.getNote().isEmpty()) {
            policyCondition.getNote().get(0).setId(UUID.randomUUID().toString());
        }
        if (policyCondition.getValidFor() == null) {
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setStartDateTime(LocalDateTime.now());
            policyCondition.setValidFor(timePeriod);
        }
    }

    @Override
    public List<PolicyCondition> getAllPolicyCondition(Long skip,Long limit) {
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
                ? mongoTemplate.aggregate(aggregation, "policyCondition", PolicyCondition.class).getMappedResults()
                : mongoTemplate.findAll(PolicyCondition.class);
    }

    @Override
    public PolicyCondition getPolicyConditionById(String policyConditionId) {
        Optional<PolicyCondition> policyCondition = this.policyConditionRepository.findById(policyConditionId);
        if (policyCondition.isPresent()) {
            return policyCondition.get();
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND);
        }
    }

    @Override
    public PolicyCondition updatePolicyCondition(PolicyCondition policyCondition) {
        isConditionPathExists(policyCondition);
        Optional<PolicyCondition> policyConditionDB = policyConditionRepository.findById(policyCondition.getId());
        if (policyConditionDB.isPresent()) {
            PolicyCondition policyConditionUpdate = policyConditionDB.get();
            if (policyConditionUpdate.getPolicyCondition() != null && !policyConditionUpdate.getPolicyCondition().isEmpty()) {
                updateoldPolicyCondition(policyConditionUpdate, policyCondition);
            } else {
                setNewPolicyCondition(policyConditionUpdate, policyCondition);
            }
            if (policyConditionUpdate.getPolicyConditionStatement() != null && !policyConditionUpdate.getPolicyConditionStatement().isEmpty()) {
                updateoldPolicyConditionStatement(policyConditionUpdate, policyCondition);
            } else {
                setNewPolicyConditionStatement(policyConditionUpdate, policyCondition);
            }
            policyConditionUpdate.setPolicyConditionStrategy(policyCondition.getPolicyConditionStrategy());
            policyConditionUpdate.setAtType(policyCondition.getAtType());
            policyConditionUpdate.setAtBaseType(policyCondition.getAtBaseType());
            policyConditionUpdate.setAtSchemaLocation(policyCondition.getAtSchemaLocation());
            policyConditionUpdate.setHref(policyCondition.getHref());
            policyConditionUpdate.setIsConjustiveNormalForm(policyCondition.getIsConjustiveNormalForm());
            policyConditionUpdate.setValidFor(policyCondition.getValidFor());
            if (policyConditionUpdate.getNote() != null && !policyConditionUpdate.getNote().isEmpty()) {
                updateOldNote(policyConditionUpdate, policyCondition);
            } else {
                setNewNote(policyConditionUpdate, policyCondition);
            }
            policyConditionUpdate.setName(policyCondition.getName());
            policyConditionUpdate.setLastUpdate(LocalDateTime.now());
            return policyConditionRepository.save(policyConditionUpdate);
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND);
        }
    }

    private void isConditionPathExists(PolicyCondition policyCondition) {
        if (policyCondition == null || policyCondition.getPolicyConditionStatement() == null || policyCondition.getPolicyConditionStatement().isEmpty() || policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable() == null) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_CONDITION_STATEMENT, PolicyRuleConstants.MISSING_CONDITION_STATEMENT);
        }
        String path = policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable().getPath();
        if (path == null || path.isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_CONDITION_STATEMENT, PolicyRuleConstants.MISSING_CONDITION_STATEMENT);
        }
    }

    private void setNewPolicyCondition(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        List<PolicyCondition> policyConditions = new ArrayList<>();
        if (policyCondition.getPolicyCondition() != null && !policyCondition.getPolicyCondition().isEmpty()) {
            PolicyCondition policyConditionNew = policyCondition.getPolicyCondition().get(0);
            isConditionPathExists(policyConditionNew);
            policyConditionNew.setId(UUID.randomUUID().toString());
            setPolicyConditionIds(policyConditionNew);
            policyConditions.add(policyConditionNew);
        }
        policyConditionUpdate.setPolicyCondition(policyConditions);
    }

    private void updateoldPolicyCondition(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        PolicyCondition policyConditionOld = policyConditionUpdate.getPolicyCondition().get(0);
        List<PolicyCondition> policyConditions = new ArrayList<>();
        if (policyCondition != null && policyCondition.getPolicyCondition() != null && !policyCondition.getPolicyCondition().isEmpty()) {
            PolicyCondition policyConditionNew = policyCondition.getPolicyCondition().get(0);
            policyConditionNew.setId(policyConditionOld.getId());
            if (policyConditionOld.getPolicyConditionStatement() != null && !policyConditionOld.getPolicyConditionStatement().isEmpty()) {
                updateoldPolicyConditionStatement(policyConditionOld, policyConditionNew);
            } else {
                setNewPolicyConditionStatement(policyConditionOld, policyConditionNew);
            }
            policyConditionOld.setPolicyConditionStrategy(policyConditionNew.getPolicyConditionStrategy());
            policyConditionOld.setAtType(policyConditionNew.getAtType());
            policyConditionOld.setAtBaseType(policyConditionNew.getAtBaseType());
            policyConditionOld.setAtSchemaLocation(policyConditionNew.getAtSchemaLocation());
            policyConditionOld.setHref(policyConditionNew.getHref());
            policyConditionOld.setIsConjustiveNormalForm(policyConditionNew.getIsConjustiveNormalForm());
            policyConditionOld.setValidFor(policyConditionNew.getValidFor());
            if (policyConditionOld.getNote() != null && !policyConditionOld.getNote().isEmpty()) {
                updateOldNote(policyConditionOld, policyConditionNew);
            } else {
                setNewNote(policyConditionOld, policyConditionNew);
            }
            policyConditionOld.setName(policyConditionNew.getName());
            policyConditions.add(policyConditionOld);
        }
        policyConditionUpdate.setPolicyCondition(policyConditions);
    }

    private void updateoldPolicyConditionStatement(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        PolicyConditionStatement policyConditionStatementNew = policyCondition.getPolicyConditionStatement().get(0);
        isConditionPathExists(policyCondition);
        List<PolicyConditionStatement> policyConditionStatements = new ArrayList<>();
        PolicyConditionStatement policyConditionStatementOld = policyConditionUpdate.getPolicyConditionStatement().get(0);
        if (policyConditionStatementNew.getPolicyConditionValue() != null && !policyConditionStatementNew.getPolicyConditionValue().isEmpty()) {
           if(policyConditionStatementOld.getPolicyConditionValue() != null && !policyConditionStatementOld.getPolicyConditionValue().isEmpty()){
               PolicyConditionValue policyConditionValueOld = policyConditionStatementOld.getPolicyConditionValue().get(0);
               policyConditionStatementNew.getPolicyConditionValue().get(0).setId(policyConditionValueOld.getId());
           }else{
               policyConditionStatementNew.getPolicyConditionValue().get(0).setId(UUID.randomUUID().toString());
           }
        }
        PolicyConditionVariable policyConditionVariableOld = policyConditionStatementOld.getPolicyConditionVariable();
        if (policyConditionStatementNew.getPolicyConditionVariable() != null) {
            policyConditionStatementNew.getPolicyConditionVariable().setId(policyConditionVariableOld.getId());
        }
        policyConditionStatements.add(policyConditionStatementNew);
        policyConditionUpdate.setPolicyConditionStatement(policyConditionStatements);
    }

    private void setNewPolicyConditionStatement(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        if (policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue() != null && !policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue().isEmpty()) {
            policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionValue().get(0).setId(UUID.randomUUID().toString());
        }
        if (policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable() != null) {
            policyCondition.getPolicyConditionStatement().get(0).getPolicyConditionVariable().setId(UUID.randomUUID().toString());
        }
        policyConditionUpdate.setPolicyConditionStatement(policyCondition.getPolicyConditionStatement());
    }

    private void setNewNote(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        if (policyCondition.getNote() != null && !policyCondition.getNote().isEmpty()) {
            policyCondition.getNote().get(0).setId(UUID.randomUUID().toString());
            policyConditionUpdate.setNote(policyCondition.getNote());
        } else {
            policyConditionUpdate.setNote(new ArrayList<>());
        }
    }

    private void updateOldNote(PolicyCondition policyConditionUpdate, PolicyCondition policyCondition) {
        Note note = policyConditionUpdate.getNote().get(0);
        List<Note> notes = new ArrayList<>();
        if (policyCondition.getNote() != null && !policyCondition.getNote().isEmpty()) {
            Note updatedNote = policyCondition.getNote().get(0);
            updatedNote.setId(note.getId());
            notes.add(updatedNote);
        }
        policyConditionUpdate.setNote(notes);
    }

    @Override
    public void deletePolicyCondition(String policyConditionId) {
        Optional<PolicyCondition> policyCondition = this.policyConditionRepository.findById(policyConditionId);

        if (policyCondition.isPresent()) {
            this.policyConditionRepository.delete(policyCondition.get());
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND, PolicyRuleConstants.POLICYCONDITION_NOT_FOUND);
        }
    }

    @Override
    public List<PolicyCondition> getPolicyConditionByIds(List<String> ids) {
        return this.policyConditionRepository.findAllById(ids);
    }

    @Override
    public Map<String, Object> fetchPolicyConditionWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields) {
        return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, "policyCondition", PolicyCondition.class, mongoTemplate);
    }
}
