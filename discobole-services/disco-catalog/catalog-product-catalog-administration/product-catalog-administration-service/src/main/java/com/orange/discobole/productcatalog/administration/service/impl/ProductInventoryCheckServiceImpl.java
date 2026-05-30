// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.ProductInventoryConfiguration;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.ProductInventoryConfigRepository;
import com.orange.discobole.productcatalog.administration.service.ProductInventoryConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductInventoryCheckServiceImpl implements ProductInventoryConfigService {


    private ProductInventoryConfigRepository cpibConfigRepository;

    @Autowired
    public ProductInventoryCheckServiceImpl(ProductInventoryConfigRepository cpibConfigRepository) {
        this.cpibConfigRepository = cpibConfigRepository;
    }

    @Override
    public ProductInventoryConfiguration createCPIBConfig(ProductInventoryConfiguration cpibConfiguration) {
        if (cpibConfiguration.getCpibCheck() == null) {
            throw new MissingBodyFieldException(23, "Please provide mandatory fields CpibCheck", Constants.ERROR_NOT_FOUND);
        } else {
            cpibConfiguration.setId(UUID.randomUUID().toString());
            cpibConfiguration.setLastUpdate(LocalDateTime.now());
            return cpibConfigRepository.save(cpibConfiguration);
        }
    }

    @Override
    public ProductInventoryConfiguration getCPIBConfigById(String cpibConfigId) {
        Optional<ProductInventoryConfiguration> cpibConfiguration = this.cpibConfigRepository.findById(cpibConfigId);

        if (cpibConfiguration.isPresent()) {
            return cpibConfiguration.get();
        } else {
            throw new MissingBodyFieldException(60, Constants.ERROR_CONFIGURATION_NOT_FOUND, Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public ProductInventoryConfiguration updateCPIBConfig(ProductInventoryConfiguration cpibConfiguration) {
        Optional<ProductInventoryConfiguration> cpibConfigurationDB = this.cpibConfigRepository.findById(cpibConfiguration.getId());

        if (cpibConfigurationDB.isPresent()) {
            if (!(cpibConfiguration.getCpibCheck() == null)) {
                ProductInventoryConfiguration cpibConfigurationUpdate = cpibConfigurationDB.get();
                cpibConfigurationUpdate.setCpibCheck(cpibConfiguration.getCpibCheck());
                cpibConfigurationUpdate.setAtBaseType(cpibConfiguration.getAtBaseType());
                cpibConfigurationUpdate.setHref(cpibConfiguration.getHref());
                cpibConfigurationUpdate.setAtType(cpibConfiguration.getAtType());
                cpibConfigurationUpdate.setSchemaLocation(cpibConfiguration.getSchemaLocation());
                cpibConfigRepository.save(cpibConfigurationUpdate);
                return cpibConfigurationUpdate;
            } else {
                throw new MissingBodyFieldException(23, "Missing Body Field CpibCheck", Constants.ERROR_NOT_FOUND);
            }
        } else {
            throw new MissingBodyFieldException(60, Constants.ERROR_CONFIGURATION_NOT_FOUND, Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public List<ProductInventoryConfiguration> getAllCPIBConfig() {
        return cpibConfigRepository.findAll();
    }

    @Override
    public void deleteCPIBConfig(String cpibConfigId) {
        if(cpibConfigId.contains(",")) {
            List<String> ids = Arrays.asList(cpibConfigId.split(","));
            this.cpibConfigRepository.deleteAllById(ids);
        }
        else {
            Optional<ProductInventoryConfiguration> cpibConfigurationDB = this.cpibConfigRepository.findById(cpibConfigId);
            if (cpibConfigurationDB.isPresent()) {
                this.cpibConfigRepository.delete(cpibConfigurationDB.get());
            } else {
                throw new MissingBodyFieldException(60, "CpibConfiguration Not Found", "Not Found");
            }
        }
    }
}
