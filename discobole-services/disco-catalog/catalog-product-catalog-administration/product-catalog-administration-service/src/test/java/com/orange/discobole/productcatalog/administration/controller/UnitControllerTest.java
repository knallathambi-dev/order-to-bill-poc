// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Unit;
import com.orange.discobole.productcatalog.administration.service.UnitService;
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

class UnitControllerTest {

    @InjectMocks
    UnitController unitController;

    @Mock
    private UnitService unitService;

    private Unit unit;
    private Unit unit1;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setUnitOfMeasure("KB");
        unit1 = new Unit();
        unit1.setUnitOfMeasure("GB");
        unit1.setId("2");
    }

    @Test
    void createUnitTest() {

        when(unitService.createUnit(any(Unit.class))).thenReturn(unit);
        ResponseEntity responseEntity = unitController.createUnit(unit);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(unit,responseEntity.getBody());
    }

    @Test
    void getAllUnitsTest() {
        List<Unit> units = Arrays.asList(unit,unit1);
        when(unitService.getAllUnit()).thenReturn(units);
        ResponseEntity<List<Unit>> responseEntity = unitController.getAllUnit();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(units, responseEntity.getBody());

        verify(unitService, times(1)).getAllUnit();

    }

    @Test
    void getunitByIdTest() {
        when(unitService.getUnitById("2")).thenReturn(unit1);
        ResponseEntity<Unit> responseEntity = unitController.getUnitById("2");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(unit1, responseEntity.getBody());

        verify(unitService, times(1)).getUnitById("2");

    }

    @Test
    void updateUnitTest() {
        when(unitService.updateUnit(unit)).thenReturn(unit);
        ResponseEntity<Unit> responseEntity = unitController.updateUnit("2",unit);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(unit, responseEntity.getBody());

        verify(unitService, times(1)).updateUnit(unit);
    }

    @Test
    void deleteUnitTest() {

        HttpStatus expectedStatus = HttpStatus.OK;

        HttpStatus actualStatus = unitController.deleteUnit("2");

        assertEquals(expectedStatus, actualStatus);

        verify(unitService, times(1)).deleteUnit("2");
    }
}