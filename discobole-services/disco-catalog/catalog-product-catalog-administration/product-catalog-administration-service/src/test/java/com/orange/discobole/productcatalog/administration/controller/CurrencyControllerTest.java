// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Currency;
import com.orange.discobole.productcatalog.administration.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CurrencyControllerTest {

    @InjectMocks
    private currencyController currencyController;

    @Mock
    private CurrencyService currencyService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateCurrency() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setIsDefault(true);
        when(currencyService.createCurrency(currency)).thenReturn(currency);

        ResponseEntity<Currency> responseEntity = currencyController.createCurrency(currency);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currency, responseEntity.getBody());

        verify(currencyService, times(1)).createCurrency(currency);
    }

    @Test
    void testGetAllCurrency() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setIsDefault(true);
        Currency currency1 = new Currency();
        currency1.setLabel("JPY");
        currency1.setCode("JPY");
        currency1.setIsDefault(false);
        List<Currency> currencies = Arrays.asList(currency, currency1);
        // Mock to accept any map
        when(currencyService.getAllCurrency(anyMap())).thenReturn(currencies);

// Call with the same map object
        ResponseEntity<List<Currency>> responseEntity = currencyController.getAllCurrency(null,null);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currencies, responseEntity.getBody());
    }
    @Test
    void testGetAllCurrencyIsDefault() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setIsDefault(true);
        Currency currency1 = new Currency();
        currency1.setLabel("JPY");
        currency1.setCode("JPY");
        currency1.setIsDefault(false);
        List<Currency> currencies = Arrays.asList(currency, currency1);
        // Mock to accept any map
        when(currencyService.getAllCurrency(anyMap())).thenReturn(currencies);

// Call with the same map object
        ResponseEntity<List<Currency>> responseEntity = currencyController.getAllCurrency(null,true);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currency, responseEntity.getBody().get(0));
    }
    @Test
    void testGetAllCurrencyDesc() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setIsDefault(true);
        Currency currency1 = new Currency();
        currency1.setLabel("JPY");
        currency1.setCode("JPY");
        currency1.setIsDefault(false);
        List<Currency> currencies = Arrays.asList(currency, currency1);
        // Mock to accept any map
        when(currencyService.getAllCurrency(anyMap())).thenReturn(currencies);

// Call with the same map object
        ResponseEntity<List<Currency>> responseEntity = currencyController.getAllCurrency("-label ",null);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currencies, responseEntity.getBody());
    }

    @Test
    void testGetCurrencyById() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setId("1");
        Currency currency1 = new Currency();
        currency1.setLabel("INR");
        currency1.setCode("INR");
        currency1.setId("2");


        when(currencyService.getCurrencyById("2")).thenReturn(currency);

        ResponseEntity<Currency> responseEntity = currencyController.getCurrencyById("2");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currency, responseEntity.getBody());

        verify(currencyService, times(1)).getCurrencyById("2");
    }

    @Test
    public void testUpdateCurrency() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setId("1");
        Currency currency1 = new Currency();
        currency1.setLabel("USD");
        currency1.setCode("USD");
        currency1.setId("2");
        when(currencyService.updateCurrency(currency)).thenReturn(currency);

        ResponseEntity<Currency> responseEntity = currencyController.updateCurrency("2", currency);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currency, responseEntity.getBody());

        verify(currencyService, times(1)).updateCurrency(currency);
    }

    @Test
    public void testDeleteCurrency() {
        Currency currency = new Currency();
        currency.setLabel("INR");
        currency.setCode("INR");
        currency.setId("1");
        Currency currency1 = new Currency();
        currency1.setLabel("USD");
        currency1.setCode("USD");
        currency1.setId("2");
        HttpStatus expectedStatus = HttpStatus.OK;

        HttpStatus actualStatus = currencyController.deleteCurrency("2");

        assertEquals(expectedStatus, actualStatus);

        verify(currencyService, times(1)).deleteCurrency("2");
    }
}
