// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service.impl;

import com.orange.discobole.admin.PolicyDomainRef;
import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import com.orange.discobole.productcatalog.policyrule.exception.DuplicateBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyDomainRepository;
import com.orange.discobole.productcatalog.policyrule.service.PolicyDomainService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyDomainServiceImpl implements PolicyDomainService {

    private final PolicyDomainRepository policyDomainRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PolicyDomainServiceImpl(PolicyDomainRepository policyDomainRepository, MongoTemplate mongoTemplate) {
        this.policyDomainRepository = policyDomainRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PolicyDomainRef createPolicyDomain(PolicyDomainRef policyDomain) {
        if (policyDomain == null) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_POLICYDOMAIN, PolicyRuleConstants.MISSING_BODY_POLICYDOMAIN);
        } else if (policyDomain.getName() == null || policyDomain.getName().isBlank()) {
            throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_FIELD_POLICYDOMAINNAME, PolicyRuleConstants.MISSING_BODY_FIELD_POLICYDOMAINNAME);
        } else {
            PolicyDomainRef policyDomainRef = policyDomainRepository.findByName(policyDomain.getName());
            if(policyDomainRef != null && policyDomainRef.getName().equals(policyDomain.getName())){
               throw new DuplicateBodyFieldException(242, PolicyRuleConstants.DUPLICATE_BODY_FIELD_POLICYDOMAINNAME, PolicyRuleConstants.DUPLICATE_BODY_FIELD_POLICYDOMAINNAME);
            }
            policyDomain.setLastUpdate(LocalDateTime.now());
            return policyDomainRepository.save(policyDomain);
        }
    }

    @Override
    public List<PolicyDomainRef> getAllPolicyDomain(Long skip,Long limit) {
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
                ? mongoTemplate.aggregate(aggregation, "policyDomainRef", PolicyDomainRef.class).getMappedResults()
                : mongoTemplate.findAll(PolicyDomainRef.class);
    }

    @Override
    public PolicyDomainRef getPolicyDomainById(String policyDomainId) {
        Optional<PolicyDomainRef> policyDomain = this.policyDomainRepository.findById(policyDomainId);
        if (policyDomain.isPresent()) {
            return policyDomain.get();
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND);
        }
    }

    @Override
    public PolicyDomainRef updatePolicyDomain(PolicyDomainRef policyDomain) {
        Optional<PolicyDomainRef> policyDomainRef = this.policyDomainRepository.findById(policyDomain.getId());

        if (policyDomainRef.isPresent()) {
            if (!(policyDomain.getName() == null || policyDomain.getName().isBlank())) {
                PolicyDomainRef domainRef = this.policyDomainRepository.findByName(policyDomain.getName());
                if(domainRef != null && !domainRef.getId().equals(policyDomain.getId())){
                    throw new DuplicateBodyFieldException(242, PolicyRuleConstants.DUPLICATE_BODY_FIELD_POLICYDOMAINNAME, PolicyRuleConstants.DUPLICATE_BODY_FIELD_POLICYDOMAINNAME);
                }
                PolicyDomainRef policyDomainUpdate = policyDomainRef.get();
                policyDomainUpdate.setName(policyDomain.getName());
                policyDomainUpdate.setAtBaseType(policyDomain.getAtBaseType());
                policyDomainUpdate.setAtReferredType(policyDomain.getAtReferredType());
                policyDomainUpdate.setHref(policyDomain.getHref());
                policyDomainUpdate.setAtType(policyDomain.getAtType());
                policyDomainUpdate.setAtSchemaLocation(policyDomain.getAtSchemaLocation());
                policyDomainUpdate.setLastUpdate(LocalDateTime.now());
                policyDomainRepository.save(policyDomainUpdate);
                return policyDomainUpdate;
            } else {
                throw new MissingBodyFieldException(23, PolicyRuleConstants.MISSING_BODY_FIELD_POLICYDOMAINNAME, PolicyRuleConstants.MISSING_BODY_FIELD_POLICYDOMAINNAME);
            }
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND);
        }
    }

    @Override
    public void deletePolicyDomain(String policyDomainId) {
        Optional<PolicyDomainRef> policyDomain = this.policyDomainRepository.findById(policyDomainId);

        if (policyDomain.isPresent()) {
            this.policyDomainRepository.delete(policyDomain.get());
        } else {
            throw new MissingBodyFieldException(60, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND, PolicyRuleConstants.POLICYDOMAIN_NOT_FOUND);
        }
    }
}
