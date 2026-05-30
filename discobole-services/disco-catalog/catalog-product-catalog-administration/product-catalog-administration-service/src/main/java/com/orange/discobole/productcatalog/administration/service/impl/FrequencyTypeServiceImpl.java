// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;
import com.orange.disco.admin.FrequencyTypes;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.FrequencyTypesRepository;
import com.orange.discobole.productcatalog.administration.service.FrequencyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class FrequencyTypeServiceImpl implements FrequencyTypeService {
    private static final Logger LOGGER = Logger.getLogger(FrequencyTypeServiceImpl.class.getName());


    private FrequencyTypesRepository frequencyTypeRepository;

    @Autowired
    public FrequencyTypeServiceImpl(FrequencyTypesRepository frequencyTypeRepository) {
        this.frequencyTypeRepository = frequencyTypeRepository;
    }
    @Override
    public FrequencyTypes createFrequencyTypes(FrequencyTypes frequencyType) {
        if (frequencyType.getFrequencyLabel().isBlank()) {
            LOGGER.info("in if  frequency type is blank");
            throw new MissingBodyFieldException(23,"Missing Body Field Frequency Label", Constants.ERROR_NOT_FOUND);
        }
        // Check if a currency with the same label already exists
        Optional<FrequencyTypes> existingFrequency = frequencyTypeRepository.findByfrequencyLabel(frequencyType.getFrequencyLabel());
        if (existingFrequency.isPresent()) {
            throw new MissingBodyFieldException(004,  Constants.ERROR_LABEL_OR_CODE_ARE_NOT_UNIQUE, Constants.ERROR_FOR_LABEL_CODE);
        }
        // Check if a currency with the same label already exists
        Optional<FrequencyTypes> existingFrequencyWithCode = frequencyTypeRepository.findByfrequencyCode(frequencyType.getFrequencyCode());
        if (existingFrequencyWithCode.isPresent()) {
            throw new MissingBodyFieldException(004, Constants.ERROR_LABEL_OR_CODE_ARE_NOT_UNIQUE, Constants.ERROR_FOR_LABEL_CODE);
        }
        else {
            frequencyType.setId(UUID.randomUUID().toString());
            frequencyType.setFrequencyCode(frequencyType.getFrequencyLabel());
            frequencyType.setLastUpdate(LocalDateTime.now());
            return frequencyTypeRepository.save(frequencyType);
        }
    }

    @Override
    public List<FrequencyTypes> getAllFrequencyTypes() {
        return this.frequencyTypeRepository.findAll();
    }

    @Override
    public FrequencyTypes getFrequencyTypesById(String frequencyTypeId) {
        Optional<FrequencyTypes> frequencyTypesDb = this.frequencyTypeRepository.findById(frequencyTypeId);

        if (frequencyTypesDb.isPresent()) {
            return frequencyTypesDb.get();
        }
        else{
            throw new MissingBodyFieldException(60,"FrequencyType Not Found",Constants.ERROR_NOT_FOUND);
        }
//
    }

    @Override
    public FrequencyTypes updateFrequencyTypes(FrequencyTypes frequencyType) {
        Optional < FrequencyTypes > frequencyTypeDb = this.frequencyTypeRepository.findById(frequencyType.getId());

        if (frequencyTypeDb.isPresent()) {
            // Check if a currency with the same label already exists
            Optional<FrequencyTypes> existingFrequency = frequencyTypeRepository.findByfrequencyLabel(frequencyType.getFrequencyLabel());
            if (existingFrequency.isPresent()) {
                throw new MissingBodyFieldException(004, Constants.ERROR_LABEL_OR_CODE_ARE_NOT_UNIQUE, Constants.ERROR_FOR_LABEL_CODE);
            }
            // Check if a currency with the same label already exists
            Optional<FrequencyTypes> existingFrequencyWithCode = frequencyTypeRepository.findByfrequencyCode(frequencyType.getFrequencyCode());
            if (existingFrequencyWithCode.isPresent()) {
                throw new MissingBodyFieldException(004,  Constants.ERROR_LABEL_OR_CODE_ARE_NOT_UNIQUE, Constants.ERROR_FOR_LABEL_CODE);
            }
            if (!( frequencyType.getFrequencyLabel().isBlank())) {
                FrequencyTypes frequencyTypeUpdate = frequencyTypeDb.get();
                frequencyTypeUpdate.setId(frequencyType.getId());
                frequencyTypeUpdate.setFrequencyLabel(frequencyType.getFrequencyLabel());
                frequencyTypeUpdate.setHref(frequencyType.getHref());
                frequencyTypeUpdate.setAtBaseType(frequencyType.getAtBaseType());
                frequencyTypeUpdate.setLastUpdate(frequencyType.getLastUpdate());
                frequencyTypeUpdate.setFrequencyCode(frequencyType.getFrequencyLabel());
                frequencyTypeRepository.save(frequencyTypeUpdate);
                return frequencyTypeUpdate;
            } else {
                throw new MissingBodyFieldException(23, "Missing Body Field Frequency label", Constants.ERROR_NOT_FOUND);

            }
        }
        else {
            throw new MissingBodyFieldException(60,"FrequencyType Not Found",Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public void deleteFrequencyType(String frequencyTypeId) {
        if(frequencyTypeId.contains(",")){
            List<String> ids = Arrays.asList(frequencyTypeId.split(","));
            this.frequencyTypeRepository.deleteAllById(ids);
        }
        else {
            Optional<FrequencyTypes> FrequencyTypeDb = this.frequencyTypeRepository.findById(frequencyTypeId);

            if (FrequencyTypeDb.isPresent()) {
                this.frequencyTypeRepository.delete(FrequencyTypeDb.get());
            } else {
                throw new MissingBodyFieldException(60, "Frequency Type Not Found", "Not Found");
            }
        }
    }
}
