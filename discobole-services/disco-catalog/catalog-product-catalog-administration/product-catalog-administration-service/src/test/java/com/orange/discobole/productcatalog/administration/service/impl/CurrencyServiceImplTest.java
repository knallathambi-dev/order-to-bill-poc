// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.Currency;
import com.orange.discobole.productcatalog.administration.exception.DiscoException;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.CurrencyRepository;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MongoConverter mongoConverter;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency validCurrency;

    @BeforeEach
    void setUp() {
        validCurrency = new Currency();
        validCurrency.setId("1");
        validCurrency.setCode("USD");
        validCurrency.setLabel("USD");
        validCurrency.setIsDefault(false);

    }

    //------------------ CREATE CURRENCY ------------------
    @Test
    void createCurrency_whenIsDefaultNull_shouldSetFalse() {
        validCurrency.setIsDefault(null);

        when(currencyRepository.findByLabelOrCode(any(), any())).thenReturn(Optional.empty());
        when(currencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Currency result = currencyService.createCurrency(validCurrency);

        assertNotNull(result.getId());
        assertFalse(result.getIsDefault());
        verify(currencyRepository).save(any());
    }

    @Test
    void createCurrency_whenIsDefaultTrue_shouldUnsetPreviousDefault() {
        validCurrency.setIsDefault(true);

        when(currencyRepository.findByLabelOrCode(any(), any())).thenReturn(Optional.empty());
        when(currencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Currency result = currencyService.createCurrency(validCurrency);

        verify(mongoTemplate).updateMulti(any(Query.class), any(Update.class), eq(Currency.class));
        assertTrue(result.getIsDefault());
    }

    @Test
    void createCurrency_whenInvalidIsoCurrency_shouldThrowDiscoException() {
        validCurrency.setCode("ABC");
        validCurrency.setLabel("ABC");

        when(currencyRepository.findByLabelOrCode(any(), any())).thenReturn(Optional.empty());

        assertThrows(DiscoException.class,
                () -> currencyService.createCurrency(validCurrency));
    }

    @Test
    void createCurrency_whenBlankCodeOrLabel_shouldThrowMissingBodyFieldException() {
        Currency invalid = new Currency();
        invalid.setCode("");
        invalid.setLabel("");

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.createCurrency(invalid));
    }

    // ---------------- GET BY ID ----------------
    @Test
    void getCurrencyById_whenFound_shouldReturnCurrency() {
        when(currencyRepository.findById("1")).thenReturn(Optional.of(validCurrency));

        Currency result = currencyService.getCurrencyById("1");

        assertEquals("USD", result.getCode());
    }

    @Test
    void getCurrencyById_whenNotFound_shouldThrowException() {
        when(currencyRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.getCurrencyById("1"));
    }

    // ---------------- UPDATE CURRENCY ----------------
    @Test
    void updateCurrency_whenCurrencyNotFound_shouldThrowException() {
        when(currencyRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.updateCurrency(validCurrency));
    }

    @Test
    void updateCurrency_whenMissingCodeOrLabel_shouldThrowException() {
        validCurrency.setCode("");

        when(currencyRepository.findById("1")).thenReturn(Optional.of(new Currency()));

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.updateCurrency(validCurrency));
    }

    @Test
    void updateCurrency_whenDuplicateCurrency_shouldThrowException() {
        Currency existing = new Currency();
        existing.setId("2");

        when(currencyRepository.findById("1")).thenReturn(Optional.of(new Currency()));
        when(currencyRepository.findByLabelOrCode(any(), any())).thenReturn(Optional.of(existing));

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.updateCurrency(validCurrency));
    }

    @Test
    void updateCurrency_whenDefaultChanged_shouldUnsetPreviousDefault() {
        Currency dbCurrency = new Currency();
        dbCurrency.setId("1");
        dbCurrency.setIsDefault(false);

        validCurrency.setIsDefault(true);

        when(currencyRepository.findById("1")).thenReturn(Optional.of(dbCurrency));
        when(currencyRepository.findByLabelOrCode(any(), any())).thenReturn(Optional.empty());
        when(currencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Currency result = currencyService.updateCurrency(validCurrency);

        verify(mongoTemplate).updateMulti(any(Query.class), any(Update.class), eq(Currency.class));
        assertTrue(result.getIsDefault());
    }

    // ---------------- DELETE CURRENCY ----------------
    @Test
    void deleteCurrency_whenMultipleIds_shouldDeleteAll() {
        currencyService.deleteCurrency("1,2,3");

        verify(currencyRepository).deleteAllById(List.of("1", "2", "3"));
    }

    @Test
    void deleteCurrency_whenSingleIdFound_shouldDelete() {
        when(currencyRepository.findById("1")).thenReturn(Optional.of(validCurrency));

        currencyService.deleteCurrency("1");

        verify(currencyRepository).delete(validCurrency);
    }

    @Test
    void deleteCurrency_whenSingleIdNotFound_shouldThrowException() {
        when(currencyRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class,
                () -> currencyService.deleteCurrency("1"));
    }

    // ---------------- GET ALL CURRENCY ----------------
    @Test
    void getAllCurrency_whenSortDescending_shouldReturnData() {
        Map<String, Object> requestParams = Map.of("sort", "-label");

        Currency currency = new Currency();
        currency.setLabel("USD");

        Document doc = new Document("label", "USD");
        Document mongoResult = new Document();
        mongoResult.put("data", List.of(doc));
        mongoResult.put("count", List.of(new Document("count", 1)));

        when(mongoTemplate.getConverter()).thenReturn(mongoConverter);
        AggregationResults<Document> aggregationResults = new AggregationResults<>(List.of(mongoResult), new Document());

        when(mongoTemplate.aggregate(any(org.springframework.data.mongodb.core.aggregation.Aggregation.class),
                eq("currency"), eq(Document.class))).thenReturn(aggregationResults);

        when(mongoConverter.read(eq(Currency.class), any(Document.class))).thenReturn(currency);

        List<Currency> result = currencyService.getAllCurrency(requestParams);

        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getLabel());
    }

    @Test
    void getAllCurrency_whenIsDefaultTrue_shouldReturnData() {
        Map<String, Object> requestParams = Map.of("isDefault", "true");

        Currency currency = new Currency();
        currency.setIsDefault(true);

        Document doc = new Document("isDefault", true);
        Document mongoResult = new Document();
        mongoResult.put("data", List.of(doc));
        mongoResult.put("count", List.of(new Document("count", 1)));

        AggregationResults<Document> aggregationResults = new AggregationResults<>(List.of(mongoResult), new Document());

        when(mongoTemplate.getConverter()).thenReturn(mongoConverter);
        when(mongoTemplate.aggregate(any(org.springframework.data.mongodb.core.aggregation.Aggregation.class),
                eq("currency"), eq(Document.class))).thenReturn(aggregationResults);

        when(mongoConverter.read(eq(Currency.class), any(Document.class))).thenReturn(currency);

        List<Currency> result = currencyService.getAllCurrency(requestParams);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsDefault());
    }

    @Test
    void getAllCurrency_whenNoData_shouldReturnEmptyList() {
        Map<String, Object> requestParams = new HashMap<>();

        Document mongoResult = new Document(); // no "data"

        AggregationResults<Document> aggregationResults = new AggregationResults<>(List.of(mongoResult), new Document());

        when(mongoTemplate.aggregate(any(org.springframework.data.mongodb.core.aggregation.Aggregation.class),
                eq("currency"), eq(Document.class))).thenReturn(aggregationResults);

        List<Currency> result = currencyService.getAllCurrency(requestParams);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
