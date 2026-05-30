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
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.ProductInventoryConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CPIBConfigServiceImplTest {

    @Mock
    private ProductInventoryConfigRepository cpibConfigRepository;

    @InjectMocks
    private ProductInventoryCheckServiceImpl cpibConfigService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCPIBConfig() {
        ProductInventoryConfiguration inputConfig = new ProductInventoryConfiguration();
        inputConfig.setCpibCheck(true);
        when(cpibConfigRepository.save(any())).thenReturn(inputConfig);

        ProductInventoryConfiguration createdConfig = cpibConfigService.createCPIBConfig(inputConfig);

        assertNotNull(createdConfig.getId());
        assertEquals(inputConfig.getCpibCheck(), createdConfig.getCpibCheck());
        verify(cpibConfigRepository, times(1)).save(inputConfig);
    }

    @Test
    public void testGetCPIBConfigById() {
        String configId = UUID.randomUUID().toString();
        ProductInventoryConfiguration expectedConfig = new ProductInventoryConfiguration();
        expectedConfig.setId(configId);
        when(cpibConfigRepository.findById(configId)).thenReturn(Optional.of(expectedConfig));

        ProductInventoryConfiguration retrievedConfig = cpibConfigService.getCPIBConfigById(configId);

        assertEquals(expectedConfig.getId(), retrievedConfig.getId());
        verify(cpibConfigRepository, times(1)).findById(configId);
    }

    @Test
    public void testUpdateCPIBConfig() {
        String configId = UUID.randomUUID().toString();
        ProductInventoryConfiguration existingConfig = new ProductInventoryConfiguration();
        existingConfig.setId(configId);
        existingConfig.setCpibCheck(true);
        ProductInventoryConfiguration updatedConfig = new ProductInventoryConfiguration();
        updatedConfig.setId(configId);
        updatedConfig.setCpibCheck(false);

        when(cpibConfigRepository.findById(configId)).thenReturn(Optional.of(existingConfig));
        when(cpibConfigRepository.save(any())).thenReturn(updatedConfig);

        ProductInventoryConfiguration resultConfig = cpibConfigService.updateCPIBConfig(updatedConfig);

        assertEquals(updatedConfig.getCpibCheck(), resultConfig.getCpibCheck());
        verify(cpibConfigRepository, times(1)).findById(configId);
        verify(cpibConfigRepository, times(1)).save(any());
    }

    @Test
    public void testGetAllCPIBConfig() {
        List<ProductInventoryConfiguration> configList = new ArrayList<>();
        configList.add(new ProductInventoryConfiguration());
        when(cpibConfigRepository.findAll()).thenReturn(configList);

        List<ProductInventoryConfiguration> result = cpibConfigService.getAllCPIBConfig();

        assertEquals(configList.size(), result.size());
        verify(cpibConfigRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteCPIBConfig() {
        String configId = UUID.randomUUID().toString();
        ProductInventoryConfiguration existingConfig = new ProductInventoryConfiguration();
        existingConfig.setId(configId);

        when(cpibConfigRepository.findById(configId)).thenReturn(Optional.of(existingConfig));

        cpibConfigService.deleteCPIBConfig(configId);

        verify(cpibConfigRepository, times(1)).delete(existingConfig);
    }

    @Test
    public void testDeleteCPIBConfig_NotFound() {
        String configId = UUID.randomUUID().toString();
        when(cpibConfigRepository.findById(configId)).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> cpibConfigService.deleteCPIBConfig(configId));

        verify(cpibConfigRepository, never()).delete(any());
    }
}
