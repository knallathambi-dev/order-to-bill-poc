// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;
import com.orange.disco.admin.FrequencyTypes;
import com.orange.discobole.productcatalog.administration.service.FrequencyTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FrequencyTypeControllerTest {

    @InjectMocks
    FrequencyTypeController frequencyTypeController;

    @Mock
    private FrequencyTypeService frequencyService;

    private FrequencyTypes freqTypes;
    private FrequencyTypes freqTypes1;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setUp() {
         freqTypes = new FrequencyTypes();
        freqTypes.setFrequencyCode("HOUR");
        freqTypes.setFrequencyLabel("HOUR");
        freqTypes1 = new FrequencyTypes();
        freqTypes1.setFrequencyCode("HOUR");
        freqTypes1.setFrequencyLabel("HOUR");
        freqTypes.setId("2");
    }

    @Test
    void createFrequencyTypeTest() {

        when(frequencyService.createFrequencyTypes(any(FrequencyTypes.class))).thenReturn(freqTypes);
        ResponseEntity responseEntity = frequencyTypeController.createFrequencyType(freqTypes);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(freqTypes,responseEntity.getBody());
    }

    @Test
    void getAllFrequencyTypesTest() {
        List<FrequencyTypes> frequencies = Arrays.asList(freqTypes,freqTypes1);
        when(frequencyService.getAllFrequencyTypes()).thenReturn(frequencies);
        ResponseEntity<List<FrequencyTypes>> responseEntity = frequencyTypeController.getAllFrequencyTypes();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(frequencies, responseEntity.getBody());

        verify(frequencyService, times(1)).getAllFrequencyTypes();

    }

    @Test
    void getfrequencyTypesByIdTest() {
        when(frequencyService.getFrequencyTypesById("2")).thenReturn(freqTypes1);
        ResponseEntity<FrequencyTypes> responseEntity = frequencyTypeController.getfrequencyTypesById("2");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(freqTypes1, responseEntity.getBody());

        verify(frequencyService, times(1)).getFrequencyTypesById("2");

    }

    @Test
    void updatefrequencyTypeTest() {
        when(frequencyService.updateFrequencyTypes(freqTypes)).thenReturn(freqTypes);
        ResponseEntity<FrequencyTypes> responseEntity = frequencyTypeController.updatefrequencyType("2",freqTypes1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(freqTypes, responseEntity.getBody());

        verify(frequencyService, times(1)).updateFrequencyTypes(freqTypes1);
    }

    @Test
    void deletefrequencyTypeTest() {

        HttpStatus expectedStatus = HttpStatus.OK;

        HttpStatus actualStatus = frequencyTypeController.deletefrequencyType("2");

        assertEquals(expectedStatus, actualStatus);

        verify(frequencyService, times(1)).deleteFrequencyType("2");
    }
}