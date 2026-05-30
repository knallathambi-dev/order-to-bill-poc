// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;
import com.orange.discobole.productcatalog.administration.service.ProductInventoryConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.orange.disco.admin.ProductInventoryConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CPIBConfigControllerTest {

    @Mock
    private ProductInventoryConfigService cpibConfigService;

    @InjectMocks
    private ProductInventoryConfigController cpibConfigController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCPIBConfig() {
        ProductInventoryConfiguration inputConfig = new ProductInventoryConfiguration();
        inputConfig.setCpibCheck(true);

        ProductInventoryConfiguration savedConfig = new ProductInventoryConfiguration();
        savedConfig.setId(UUID.randomUUID().toString());
        savedConfig.setCpibCheck(inputConfig.getCpibCheck());

        when(cpibConfigService.createCPIBConfig(any())).thenReturn(savedConfig);

        ResponseEntity<ProductInventoryConfiguration> responseEntity = cpibConfigController.createCPIBConfig(inputConfig);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(savedConfig, responseEntity.getBody());
        verify(cpibConfigService, times(1)).createCPIBConfig(any());
    }

    @Test
    public void testGetAllCPIBConfig() {
        ProductInventoryConfiguration config1 = new ProductInventoryConfiguration();
        config1.setId(UUID.randomUUID().toString());
        ProductInventoryConfiguration config2 = new ProductInventoryConfiguration();
        config2.setId(UUID.randomUUID().toString());

        List<ProductInventoryConfiguration> configList = Arrays.asList(config1, config2);

        when(cpibConfigService.getAllCPIBConfig()).thenReturn(configList);

        ResponseEntity<List<ProductInventoryConfiguration>> responseEntity = cpibConfigController.getAllCPIBConfig();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(configList, responseEntity.getBody());
        verify(cpibConfigService, times(1)).getAllCPIBConfig();
    }

    @Test
    public void testGetCPIBConfigById() {
        String configId = UUID.randomUUID().toString();
        ProductInventoryConfiguration expectedConfig = new ProductInventoryConfiguration();
        expectedConfig.setId(configId);

        when(cpibConfigService.getCPIBConfigById(configId)).thenReturn(expectedConfig);

        ResponseEntity<ProductInventoryConfiguration> responseEntity = cpibConfigController.getCPIBConfigById(configId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedConfig, responseEntity.getBody());
        verify(cpibConfigService, times(1)).getCPIBConfigById(configId);
    }

    @Test
    public void testUpdateCPIBConfig() {
        String configId = UUID.randomUUID().toString();
        ProductInventoryConfiguration updatedConfig = new ProductInventoryConfiguration();
        updatedConfig.setId(configId);
        updatedConfig.setCpibCheck(false);

        when(cpibConfigService.updateCPIBConfig(any())).thenReturn(updatedConfig);

        ResponseEntity<ProductInventoryConfiguration> responseEntity = cpibConfigController.updateCPIBConfig(configId, updatedConfig);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedConfig, responseEntity.getBody());
        verify(cpibConfigService, times(1)).updateCPIBConfig(any());
    }

    @Test
    public void testDeleteCPIBConfig() {
        String configId = UUID.randomUUID().toString();

        HttpStatus expectedStatus = HttpStatus.OK;

        HttpStatus actualStatus = cpibConfigController.deleteCPIBConfig(configId);

        assertEquals(expectedStatus, actualStatus);
        verify(cpibConfigService, times(1)).deleteCPIBConfig(configId);
    }
}
