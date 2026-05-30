// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.units.mapper;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapperImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CharacteristicMapperTest {

    private final CharacteristicMapper mapper = new CharacteristicMapperImpl();

    @Test
    void givenNullList_whenFrom_thenReturnEmptySet() {
        assertTrue(mapper.from((List<Characteristic>) null).isEmpty());
    }

    @Test
    void givenEmptyList_whenFrom_thenReturnEmptySet() {
        assertTrue(mapper.from(List.of()).isEmpty());
    }

    @Test
    void givenBooleanCharacteristic_whenFrom_thenMapAllFields() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic();
        source.setId("id1");
        source.setName("name1");
        source.setValueType("BOOLEAN");
        source.setAtBaseType("base");
        source.setAtType("type");
        source.setValue(true);

        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic result =
                (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic) mapper.from(source);

        assertEquals("id1", result.getId());
        assertEquals("name1", result.getName());
        assertEquals(true, result.getValue());
    }

    @Test
    void givenValidityCharacteristic_whenFrom_thenMapDatesToOffsetDateTime() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic();
        source.setId("idVal");
        source.setValueType("VALIDITY");
        source.setAtBaseType("base");
        source.setAtType("type");

        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityValue value = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityValue();
        Instant now = Instant.now();
        value.setValidFrom(now);
        value.setValidTo(now.plusSeconds(3600));
        source.setValue(value);

        ValidityCharacteristic result =
                mapper.from(source);

        assertNotNull(result.getValue().getValidFrom());
        assertEquals(now.atOffset(java.time.ZoneOffset.UTC), result.getValue().getValidFrom());
    }

    @Test
    void givenListWithMixedTypes_whenFrom_thenReturnMappedSet() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic boolChar = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic();
        boolChar.setId("bool1");
        boolChar.setName("bool");

        com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic stringChar = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic();
        stringChar.setId("str1");
        stringChar.setName("string");

        Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> result =
                mapper.from(List.of(boolChar, stringChar));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic));
        assertTrue(result.stream().anyMatch(c -> c instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic));
    }

    @Test
    void givenInstant_whenFromInstant_thenReturnOffsetDateTimeUTC() {
        Instant now = Instant.parse("2025-01-01T12:00:00Z");
        OffsetDateTime result = mapper.fromInstant(now);
        assertEquals(now.atOffset(java.time.ZoneOffset.UTC), result);
    }

    @Test
    void givenNullInstant_whenFromInstant_thenReturnNull() {
        assertNull(mapper.fromInstant(null));
    }

    @Test
    void givenStringCharacteristic_whenFrom_thenMapAllFields() {
        var source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic();
        source.setId("id2");
        source.setName("name2");
        source.setValueType("STRING");
        source.setAtBaseType("base2");
        source.setAtType("type2");
        source.setValue("test-value");

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic) mapper.from(source);

        assertEquals("id2", result.getId());
        assertEquals("name2", result.getName());
        assertEquals("STRING", result.getValueType());
        assertEquals("base2", result.getAtBaseType());
        assertEquals("type2", result.getAtType());
        assertEquals("test-value", result.getValue());
    }

    @Test
    void givenIntegerCharacteristic_whenFrom_thenMapAllFields() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerCharacteristic source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerCharacteristic();
        source.setId("id3");
        source.setName("name3");
        source.setValueType("INTEGER");
        source.setAtBaseType("base3");
        source.setAtType("type3");
        source.setValue(12345);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic) mapper.from(source);

        assertEquals("id3", result.getId());
        assertEquals("name3", result.getName());
        assertEquals("INTEGER", result.getValueType());
        assertEquals("base3", result.getAtBaseType());
        assertEquals("type3", result.getAtType());
        assertEquals(12345, result.getValue());
    }

    @Test
    void givenFloatCharacteristic_whenFrom_thenMapAllFields() {
        var source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatCharacteristic();
        source.setId("id4");
        source.setName("name4");
        source.setValueType("FLOAT");
        source.setAtBaseType("base4");
        source.setAtType("type4");
        source.setValue(3.14159f);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic) mapper.from(source);

        assertEquals("id4", result.getId());
        assertEquals("name4", result.getName());
        assertEquals("FLOAT", result.getValueType());
        assertEquals("base4", result.getAtBaseType());
        assertEquals("type4", result.getAtType());
        assertEquals(3.14159f, result.getValue());
    }

    @Test
    void givenBooleanArrayCharacteristic_whenFrom_thenMapAllFields() {
        var source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanArrayCharacteristic();
        source.setId("id5");
        source.setName("name5");
        source.setValueType("BOOLEAN_ARRAY");
        source.setAtBaseType("base5");
        source.setAtType("type5");
        List<Boolean> boolList = List.of(true, false, true);
        source.setValue(boolList);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic) mapper.from(source);

        assertEquals("id5", result.getId());
        assertEquals("name5", result.getName());
        assertEquals("BOOLEAN_ARRAY", result.getValueType());
        assertEquals("base5", result.getAtBaseType());
        assertEquals("type5", result.getAtType());
        assertNotSame(boolList, result.getValue());
        assertEquals(boolList, result.getValue());
    }

    @Test
    void givenStringArrayCharacteristic_whenFrom_thenMapAllFields() {
        var source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringArrayCharacteristic();
        source.setId("id6");
        source.setName("name6");
        source.setValueType("STRING_ARRAY");
        source.setAtBaseType("base6");
        source.setAtType("type6");
        List<String> strList = List.of("a", "b", "c");
        source.setValue(strList);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic) mapper.from(source);

        assertEquals("id6", result.getId());
        assertEquals("name6", result.getName());
        assertEquals("STRING_ARRAY", result.getValueType());
        assertEquals("base6", result.getAtBaseType());
        assertEquals("type6", result.getAtType());
        assertNotSame(strList, result.getValue());
        assertEquals(strList, result.getValue());
    }

    @Test
    void givenIntegerArrayCharacteristic_whenFrom_thenMapAllFields() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerArrayCharacteristic source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerArrayCharacteristic();
        source.setId("id7");
        source.setName("name7");
        source.setValueType("INTEGER_ARRAY");
        source.setAtBaseType("base7");
        source.setAtType("type7");
        List<Integer> intList = List.of(1, 2, 3, 4);
        source.setValue(intList);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerArrayCharacteristic) mapper.from(source);

        assertEquals("id7", result.getId());
        assertEquals("name7", result.getName());
        assertEquals("INTEGER_ARRAY", result.getValueType());
        assertEquals("base7", result.getAtBaseType());
        assertEquals("type7", result.getAtType());
        assertNotSame(intList, result.getValue());
        assertEquals(intList, result.getValue());
    }

    @Test
    void givenFloatArrayCharacteristic_whenFrom_thenMapAllFields() {
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatArrayCharacteristic source = new com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatArrayCharacteristic();
        source.setId("id8");
        source.setName("name8");
        source.setValueType("FLOAT_ARRAY");
        source.setAtBaseType("base8");
        source.setAtType("type8");
        List<Float> floatList = List.of(1.1f, 2.2f, 3.3f);
        source.setValue(floatList);

        var result = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic) mapper.from(source);

        assertEquals("id8", result.getId());
        assertEquals("name8", result.getName());
        assertEquals("FLOAT_ARRAY", result.getValueType());
        assertEquals("base8", result.getAtBaseType());
        assertEquals("type8", result.getAtType());
        assertNotSame(floatList, result.getValue());
        assertEquals(floatList, result.getValue());
    }

    @Test
    void givenIntegerCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic();
        source.setId("id3");
        source.setName("name3");
        source.setValueType("INTEGER");
        source.setAtType("type3");
        source.setValue(12345);

        var result = (com.orange.discobole.productinventory.dto.v1.IntegerCharacteristic) mapper.from(source);

        assertEquals("id3", result.getId());
        assertEquals("name3", result.getName());
        assertEquals("INTEGER", result.getValueType());
        assertEquals("type3", result.getAtType());
        assertEquals(12345, result.getValue());
    }

    @Test
    void givenFloatArrayCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic();
        source.setId("id8");
        source.setName("name8");
        source.setValueType("FLOAT_ARRAY");
        source.setAtType("type8");
        List<Float> floatList = List.of(1.1f, 2.2f, 3.3f);
        source.setValue(floatList);

        var result = (com.orange.discobole.productinventory.dto.v1.NumberArrayCharacteristic) mapper.from(source);

        assertEquals("id8", result.getId());
        assertEquals("name8", result.getName());
        assertEquals("FLOAT_ARRAY", result.getValueType());
        assertEquals("type8", result.getAtType());
        assertNotSame(floatList, result.getValue());
        assertEquals(floatList, result.getValue());
    }

    @Test
    void givenStringArrayCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic();
        source.setId("id6");
        source.setName("name6");
        source.setValueType("STRING_ARRAY");
        source.setAtType("type6");
        List<String> strList = List.of("a", "b", "c");
        source.setValue(strList);

        var result = (com.orange.discobole.productinventory.dto.v1.StringArrayCharacteristic) mapper.from(source);

        assertEquals("id6", result.getId());
        assertEquals("name6", result.getName());
        assertEquals("STRING_ARRAY", result.getValueType());
        assertEquals("type6", result.getAtType());
        assertNotSame(strList, result.getValue());
        assertEquals(strList, result.getValue());
    }

    @Test
    void givenBooleanArrayCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic();
        source.setId("id5");
        source.setName("name5");
        source.setValueType("BOOLEAN_ARRAY");
        source.setAtType("type5");
        List<Boolean> boolList = List.of(true, false, true);
        source.setValue(boolList);

        var result = (com.orange.discobole.productinventory.dto.v1.BooleanArrayCharacteristic) mapper.from(source);

        assertEquals("id5", result.getId());
        assertEquals("name5", result.getName());
        assertEquals("BOOLEAN_ARRAY", result.getValueType());
        assertEquals("type5", result.getAtType());
        assertNotSame(boolList, result.getValue());
        assertEquals(boolList, result.getValue());
    }

    @Test
    void givenFloatCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic();
        source.setId("id4");
        source.setName("name4");
        source.setValueType("FLOAT");
        source.setAtType("type4");
        source.setValue(3.14159f);

        var result = (com.orange.discobole.productinventory.dto.v1.NumberCharacteristic) mapper.from(source);

        assertEquals("id4", result.getId());
        assertEquals("name4", result.getName());
        assertEquals("FLOAT", result.getValueType());
        assertEquals("type4", result.getAtType());
        assertEquals(3.14159f, result.getValue());
    }

    @Test
    void givenBooleanCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic();
        source.setId("id1");
        source.setName("name1");
        source.setValueType("BOOLEAN");
        source.setAtType("type");
        source.setValue(true);

        var result = (com.orange.discobole.productinventory.dto.v1.BooleanCharacteristic) mapper.from(source);

        assertEquals("id1", result.getId());
        assertEquals("name1", result.getName());
        assertEquals(true, result.getValue());
    }

    @Test
    void givenValidityCharacteristic_whenFrom_thenMapDatesToOffsetDateTimeForProductInventory() {
        var source = new ValidityCharacteristic();
        source.setId("idVal");
        source.setValueType("VALIDITY");
        source.setAtType("type");

        var value = new ValidityValue();
        OffsetDateTime now = OffsetDateTime.now();
        value.setValidFrom(now);
        value.setValidTo(now.plusSeconds(3600));
        source.setValue(value);

        var result = (com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic) mapper.from((com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic) source);

        assertNotNull(result.getValue().getValidFrom());
        assertEquals(now, result.getValue().getValidFrom());
    }

    @Test
    void givenStringCharacteristic_whenFrom_thenMapAllFieldsForProductInventory() {
        var source = new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic();
        source.setId("id2");
        source.setName("name2");
        source.setValueType("STRING");
        source.setAtType("type2");
        source.setValue("test-value");

        var result = (com.orange.discobole.productinventory.dto.v1.StringCharacteristic) mapper.from(source);

        assertEquals("id2", result.getId());
        assertEquals("name2", result.getName());
        assertEquals("STRING", result.getValueType());
        assertEquals("type2", result.getAtType());
        assertEquals("test-value", result.getValue());
    }
}