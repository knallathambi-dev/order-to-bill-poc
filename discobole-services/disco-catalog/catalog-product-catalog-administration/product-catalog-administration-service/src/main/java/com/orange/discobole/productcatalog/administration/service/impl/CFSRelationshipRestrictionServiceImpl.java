// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.CFSRelationshipRestriction;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.CFSRelationshipRestrictionRepository;
import com.orange.discobole.productcatalog.administration.repository.ChannelRepository;
import com.orange.discobole.productcatalog.administration.repository.ProductInventoryConfigRepository;
import com.orange.discobole.productcatalog.administration.service.CFSRelationshipRestrictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class CFSRelationshipRestrictionServiceImpl implements CFSRelationshipRestrictionService {
    private CFSRelationshipRestrictionRepository cfsRelationshipRestrictionRepository;

    @Autowired
    public CFSRelationshipRestrictionServiceImpl(CFSRelationshipRestrictionRepository cfsRelationshipRestrictionRepository)
    {
        this.cfsRelationshipRestrictionRepository = cfsRelationshipRestrictionRepository;
    }

    @Override
    public CFSRelationshipRestriction createCFSRelationship(CFSRelationshipRestriction cfsRelationship) {
        if (cfsRelationship == null) {
            throw new MissingBodyFieldException(23, "Missing Body channel", Constants.ERROR_NOT_FOUND);
        } else if ( cfsRelationship.getIsRelationshipRestricted()== null) {
            throw new MissingBodyFieldException(23, "Name cannot be blank", "Name cannot be blank");
        }

        // Check if ANY record exists
        if (!cfsRelationshipRestrictionRepository.findAll().isEmpty()) {
            throw new MissingBodyFieldException(156, "A CFSRelationshipRestriction already exists.", "Only one resource is allowed.");
        }

        // Set the ID from the user request if present, else generate a random ID
        if (cfsRelationship.getId() == null || cfsRelationship.getId().isBlank()) {
            cfsRelationship.setId(UUID.randomUUID().toString());
        }

        cfsRelationship.setLastUpdate(LocalDateTime.now());
        return cfsRelationshipRestrictionRepository.save(cfsRelationship);
    }

    @Override
    public CFSRelationshipRestriction getCFSRelationshipById(String cfsRelationshipId) {
        Optional <CFSRelationshipRestriction> relationshipDb = this.cfsRelationshipRestrictionRepository.findById(cfsRelationshipId);

        if (relationshipDb.isPresent()) {
            return relationshipDb.get();
        }
        else{
            throw new MissingBodyFieldException(60,Constants.ERROR_CHANNEL_NOT_FOUND,Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public CFSRelationshipRestriction updateCFSRelationship(CFSRelationshipRestriction cfsRelationship) {
        Optional<CFSRelationshipRestriction> relationshipDb = this.cfsRelationshipRestrictionRepository.findById(cfsRelationship.getId());

        if (relationshipDb.isPresent()) {
            if (!(cfsRelationship.getIsRelationshipRestricted() == null)) {
                CFSRelationshipRestriction cfsRelationshipUpdate = relationshipDb.get();
                cfsRelationshipUpdate.setIsRelationshipRestricted(cfsRelationship.getIsRelationshipRestricted());
                cfsRelationshipUpdate.setAtBaseType(cfsRelationship.getAtBaseType());
                cfsRelationshipUpdate.setHref(cfsRelationship.getHref());
                cfsRelationshipUpdate.setAtType(cfsRelationship.getAtType());
                cfsRelationshipUpdate.setSchemaLocation(cfsRelationship.getSchemaLocation());
                cfsRelationshipRestrictionRepository.save(cfsRelationshipUpdate);
                return cfsRelationshipUpdate;
            } else {
                throw new MissingBodyFieldException(23, "Missing Body Field RelationshipRestricted", Constants.ERROR_NOT_FOUND);
            }
        } else {
            throw new MissingBodyFieldException(60, Constants.ERROR_CONFIGURATION_NOT_FOUND, Constants.ERROR_NOT_FOUND);
        }

    }

    @Override
    public List<CFSRelationshipRestriction> getAllCFSRelationship() {
        return this.cfsRelationshipRestrictionRepository.findAll();
    }

}
