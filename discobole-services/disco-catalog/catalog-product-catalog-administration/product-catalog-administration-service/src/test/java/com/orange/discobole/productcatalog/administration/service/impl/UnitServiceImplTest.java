// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.Unit;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.UnitRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UnitServiceImplTest {

    @InjectMocks
    private UnitServiceImpl unitServiceImpl;
    private Unit validUnit;
    Unit unitBlankCode;
    Unit unit1;
    @Mock
    private UnitRepository unitRepository;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validUnit = new Unit();
        validUnit.setUnitOfMeasure("KB");
        validUnit.setId("1");
        unit1 = new Unit();
        unit1.setUnitOfMeasure("GB");
        unit1.setId("3");
        unitBlankCode= new Unit();
        unitBlankCode.setUnitOfMeasure("");
        unitBlankCode.setId("2");

    }

    @Test
    void createUnitTest() {
        // Mock the save method of the repository to return the currency object
        when(unitRepository.save(validUnit)).thenReturn(validUnit);
        // Call the service method
        Unit createdUnit = unitServiceImpl.createUnit(validUnit);
        // Verify the result
        assertNotNull(createdUnit);
        assertEquals(validUnit.getUnitOfMeasure(),createdUnit.getUnitOfMeasure());
        assertNotNull(createdUnit.getId());
        assertNotNull(createdUnit.getLastUpdate());

        Mockito.verify(unitRepository, times(1)).save(validUnit);


    }
    @Test
    public void CreateUnit_BlankMeasureTest_ThrowsMissingBodyFieldException() {


        // Call the service method and expect an exception
        Exception exception = assertThrows(MissingBodyFieldException.class, () -> {
            unitServiceImpl.createUnit(unitBlankCode);
        });

        // Verify exception details

        assertEquals("NOT FOUND", ((MissingBodyFieldException) exception).getMessage());

        // Verify that save method of repository was not called
        Mockito.verify(unitRepository, never()).save(validUnit);
    }

    @Test
    void getAllUnitTypes() {
        List<Unit> units = Arrays.asList(validUnit, unit1);
        when(unitServiceImpl.getAllUnit()).thenReturn(units);

        List<Unit> unitList = unitServiceImpl.getAllUnit();
        Mockito.verify(unitRepository).findAll();
    }

    @Test
    void getUnitById() {
        String unitId = "USD";
        Unit expectedUnit = new Unit();
        expectedUnit.setId(unitId);

        Optional<Unit> optionalUnit = Optional.of(expectedUnit);

        // Mock behavior of the repository
        when(unitRepository.findById(unitId)).thenReturn(optionalUnit);

        // Act
        Unit retrievedUnit = unitServiceImpl.getUnitById(unitId);

        // Assert
        assertNotNull(retrievedUnit);
        assertEquals(unitId, retrievedUnit.getId());

        // Verify that findById method was called once with the correct currencyId
        Mockito.verify(unitRepository, times(1)).findById(unitId);
    }

    @Test
    void updateUnit() {
        Unit existingUnit = new Unit();
        existingUnit.setId("1");
        existingUnit.setUnitOfMeasure("KB");
        existingUnit.setHref("http://example.com/eur");
        existingUnit.setAtBaseType("Base");
        Optional<Unit> optionalUnit = Optional.of(validUnit);
        // Mock behavior of the repository
        when(unitRepository.findById("1")).thenReturn(optionalUnit);
        when(unitRepository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        Unit updatedUnit = unitServiceImpl.updateUnit(existingUnit);
        // Assert
        assertNotNull(updatedUnit);
        assertEquals(existingUnit.getId(), updatedUnit.getId());
        assertEquals(existingUnit.getUnitOfMeasure(), updatedUnit.getUnitOfMeasure());
        assertEquals(existingUnit.getHref(), updatedUnit.getHref());
        assertEquals(existingUnit.getAtBaseType(), updatedUnit.getAtBaseType());
        Mockito.verify(unitRepository, times(1)).findById("1");
        Mockito.verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    void updateFrequency_throwException() {
        Unit existingUnit = new Unit();
        existingUnit.setId("1");
        existingUnit.setUnitOfMeasure("GB");
        existingUnit.setHref("http://example.com/eur");
        existingUnit.setAtBaseType("Base");
        Optional<Unit> goptionalUnit = Optional.of(validUnit);
        // Call the service method and expect an exception
        Exception exception = assertThrows(MissingBodyFieldException.class, () -> {
            unitServiceImpl.updateUnit(existingUnit);
        });
        // Mock behavior of the repository
        assertThrows(MissingBodyFieldException.class, () -> unitServiceImpl.updateUnit(existingUnit));
        assertEquals("NOT FOUND", ((MissingBodyFieldException) exception).getMessage());
        when(unitRepository.findById("1")).thenReturn(goptionalUnit);
        when(unitRepository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act




    }
    @Test
    void deleteFrequencyType() {
        Unit existingUnit = new Unit();
        existingUnit.setId("1");
        existingUnit.setUnitOfMeasure("KB");
        existingUnit.setHref("http://example.com/eur");
        existingUnit.setAtBaseType("Base");
        Optional<Unit> optionalUnit = Optional.of(existingUnit);
        when(unitRepository.findById("1")).thenReturn(optionalUnit);
        // Act
        assertDoesNotThrow(() -> unitServiceImpl.deleteUnit("1"));

        // Verify that delete method was called once with the correct currency
        Mockito.verify(unitRepository, times(1)).delete(existingUnit);


    }
}