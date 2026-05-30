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
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.FrequencyTypesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FrequencyTypeServiceImplTest {

    @InjectMocks
    private FrequencyTypeServiceImpl frequencyTypeServiceImpl;
    private FrequencyTypes validFrequency;
    FrequencyTypes frequencyBlankCode;
    FrequencyTypes frequency1;
    @Mock
    private FrequencyTypesRepository frequencyRepository;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validFrequency = new FrequencyTypes();
        validFrequency.setFrequencyLabel("INR");
        validFrequency.setFrequencyCode("INR");
        validFrequency.setId("1");
        frequency1 = new FrequencyTypes();
        frequency1.setFrequencyLabel("USD");
        frequency1.setFrequencyCode("USD");
        frequency1.setId("3");
        frequencyBlankCode= new FrequencyTypes();
        frequencyBlankCode.setFrequencyLabel("");
        frequencyBlankCode.setFrequencyCode("");
        frequencyBlankCode.setId("2");

        frequencyBlankCode= new FrequencyTypes();
        frequencyBlankCode.setFrequencyLabel("");
        frequencyBlankCode.setFrequencyCode("");
        frequencyBlankCode.setId("2");
    }


    @Test
    void createFrequencyTest() {
        // Mock the save method of the repository to return the currency object
        when(frequencyRepository.save(validFrequency)).thenReturn(validFrequency);
        // Call the service method
        FrequencyTypes createdFrequency = frequencyTypeServiceImpl.createFrequencyTypes(validFrequency);
        // Verify the result
        assertNotNull(createdFrequency);
        assertEquals(validFrequency.getFrequencyCode(),createdFrequency.getFrequencyCode());
        assertEquals(validFrequency.getFrequencyLabel(),createdFrequency.getFrequencyLabel());
        assertNotNull(createdFrequency.getId());
        assertNotNull(createdFrequency.getLastUpdate());

        Mockito.verify(frequencyRepository, times(1)).save(validFrequency);


    }

    @Test
    public void CreateFrequency_BlankCodeTest_ThrowsMissingBodyFieldException() {


        // Call the service method and expect an exception
        Exception exception = assertThrows(MissingBodyFieldException.class, () -> {
            frequencyTypeServiceImpl.createFrequencyTypes(frequencyBlankCode);
        });

        // Verify exception details

        assertEquals("NOT FOUND", ((MissingBodyFieldException) exception).getMessage());

        // Verify that save method of repository was not called
        Mockito.verify(frequencyRepository, never()).save(validFrequency);
    }

    @Test
    void getAllFrequencyTypes() {
        List<FrequencyTypes> frequencies = Arrays.asList(validFrequency, frequency1);
        when(frequencyTypeServiceImpl.getAllFrequencyTypes()).thenReturn(frequencies);

        List<FrequencyTypes> frequencyList = frequencyTypeServiceImpl.getAllFrequencyTypes();
        Mockito.verify(frequencyRepository).findAll();
    }

    @Test
    void getFrequencyTypesById() {
        String frequencyId = "USD";
        FrequencyTypes expectedFrequency = new FrequencyTypes();
        expectedFrequency.setId(frequencyId);

        Optional<FrequencyTypes> optionalFrequencyTypes = Optional.of(expectedFrequency);

        // Mock behavior of the repository
        when(frequencyRepository.findById(frequencyId)).thenReturn(optionalFrequencyTypes);

        // Act
        FrequencyTypes retrievedFrequency = frequencyTypeServiceImpl.getFrequencyTypesById(frequencyId);

        // Assert
        assertNotNull(retrievedFrequency);
        assertEquals(frequencyId, retrievedFrequency.getId());

        // Verify that findById method was called once with the correct currencyId
        Mockito.verify(frequencyRepository, times(1)).findById(frequencyId);
    }

    @Test
    void updateFrequencyTypes() {
        FrequencyTypes existingFrequency = new FrequencyTypes();
        existingFrequency.setId("1");
        existingFrequency.setFrequencyCode("EUR");
        existingFrequency.setFrequencyLabel("EUR");
        existingFrequency.setHref("http://example.com/eur");
        existingFrequency.setAtBaseType("Base");
        Optional<FrequencyTypes> optionalFrequencyTypes = Optional.of(validFrequency);
        // Mock behavior of the repository
        when(frequencyRepository.findById("1")).thenReturn(optionalFrequencyTypes);
        when(frequencyRepository.save(any(FrequencyTypes.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        FrequencyTypes updatedFrequency = frequencyTypeServiceImpl.updateFrequencyTypes(existingFrequency);
        // Assert
        assertNotNull(updatedFrequency);
        assertEquals(existingFrequency.getId(), updatedFrequency.getId());
        assertEquals(existingFrequency.getFrequencyCode(), updatedFrequency.getFrequencyCode());
        assertEquals(existingFrequency.getFrequencyLabel(), updatedFrequency.getFrequencyLabel());
        assertEquals(existingFrequency.getHref(), updatedFrequency.getHref());
        assertEquals(existingFrequency.getAtBaseType(), updatedFrequency.getAtBaseType());
        Mockito.verify(frequencyRepository, times(1)).findById("1");
        Mockito.verify(frequencyRepository, times(1)).save(any(FrequencyTypes.class));
    }

    @Test
    void updateFrequency_throwException() {
        FrequencyTypes existingFrequency = new FrequencyTypes();
        existingFrequency.setId("1");
        existingFrequency.setFrequencyLabel("EU");
        existingFrequency.setHref("http://example.com/eur");
        existingFrequency.setAtBaseType("Base");
        Optional<FrequencyTypes> optionalCurrency = Optional.of(validFrequency);
        // Call the service method and expect an exception
        Exception exception = assertThrows(MissingBodyFieldException.class, () -> {
            frequencyTypeServiceImpl.updateFrequencyTypes(existingFrequency);
        });
        // Mock behavior of the repository
        assertThrows(MissingBodyFieldException.class, () -> frequencyTypeServiceImpl.updateFrequencyTypes(existingFrequency));
        assertEquals("NOT FOUND", ((MissingBodyFieldException) exception).getMessage());
        when(frequencyRepository.findById("1")).thenReturn(optionalCurrency);
        when(frequencyRepository.save(any(FrequencyTypes.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act




    }
    @Test
    void deleteFrequencyType() {
        FrequencyTypes existingFrequencyTypes = new FrequencyTypes();
        existingFrequencyTypes.setId("1");
        existingFrequencyTypes.setFrequencyLabel("EU");
        existingFrequencyTypes.setHref("http://example.com/eur");
        existingFrequencyTypes.setAtBaseType("Base");
        Optional<FrequencyTypes> optionalFrequency = Optional.of(existingFrequencyTypes);
        when(frequencyRepository.findById("1")).thenReturn(optionalFrequency);
        // Act
        assertDoesNotThrow(() -> frequencyTypeServiceImpl.deleteFrequencyType("1"));

        // Verify that delete method was called once with the correct currency
        Mockito.verify(frequencyRepository, times(1)).delete(existingFrequencyTypes);


    }
}