// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CharacteristicMapperImpl.class)
class CharacteristicMapperImplTest {

    @Autowired
    private CharacteristicMapperImpl mapper;

    @Test
    void givenServiceCharacteristic_whenFromDtoCharacteristic_thenReturnMapped() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic input = com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("STRING")
                .build();

        Characteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("STRING");
    }

    @Test
    void givenServiceCharacteristicValidity_whenFromDtoCharacteristic_thenReturnMapped() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic input = com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("Validity")
                .build();

        Characteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("Validity");
    }

    @Test
    void givenCharacteristic_whenFromCharacteristic_thenReturnMapped() {
        Characteristic input = Characteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("STRING")
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("STRING");
    }

    @Test
    void givenAddressCharacteristic_whenFromCharacteristic_thenReturnMapped() {
        AddressCharacteristic input = AddressCharacteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("Address")
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.AddressCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("Address");
    }

    @Test
    void givenServiceCharacteristicAddress_whenFromDtoCharacteristic_thenReturnMapped() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.AddressCharacteristic input = com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.AddressCharacteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("Address")
                .build();

        AddressCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("Address");
    }

    @Test
    void givenDateCharacteristic_whenFromCharacteristic_thenReturnMapped() {
        DateCharacteristic input = DateCharacteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("Date")
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.DateCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("Date");
    }

    @Test
    void givenServiceCharacteristicDate_whenFromDtoCharacteristic_thenReturnMapped() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.DateCharacteristic input = com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.DateCharacteristic.builder()
                .id("id1")
                .name("name1")
                .valueType("Date")
                .build();

        DateCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id1");
        assertThat(result.getName()).isEqualTo("name1");
        assertThat(result.getValueType()).isEqualTo("Date");
    }

    @Test
    void givenNullBooleanCharacteristic_whenFromBooleanCharacteristic_thenReturnNull() {
        assertThat(mapper.from((BooleanCharacteristic) null)).isNull();
    }

    @Test
    void givenBooleanCharacteristic_whenFromBooleanCharacteristic_thenMapAllFields() {
        BooleanCharacteristic input = BooleanCharacteristic.builder()
                .id("idB")
                .name("boolName")
                .valueType("BOOLEAN")
                .value(true)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idB");
        assertThat(result.getName()).isEqualTo("boolName");
        assertThat(result.getValueType()).isEqualTo("BOOLEAN");
        assertThat(result.getValue()).isTrue();
    }

    @Test
    void givenBooleanArrayCharacteristic_whenFromBooleanArrayCharacteristic_thenMapAllFields() {
        BooleanArrayCharacteristic input = BooleanArrayCharacteristic.builder()
                .id("idBA")
                .name("boolArray")
                .valueType("BOOLEAN_ARRAY")
                .value(Arrays.asList(true, false))
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic result =
                mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idBA");
        assertThat(result.getName()).isEqualTo("boolArray");
        assertThat(result.getValueType()).isEqualTo("BOOLEAN_ARRAY");
        assertThat(result.getValue()).containsExactly(true, false);
    }

    @Test
    void givenBooleanArrayCharacteristicWithNullValue_whenFromBooleanArrayCharacteristic_thenValueIsNull() {
        BooleanArrayCharacteristic input = BooleanArrayCharacteristic.builder()
                .id("idNull")
                .name("nullArray")
                .valueType("BOOLEAN_ARRAY")
                .value(null)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic result =
                mapper.from(input);

        assertThat(result.getValue()).isNull();
    }

    @Test
    void givenNullFloatCharacteristic_whenFromFloatCharacteristic_thenReturnNull() {
        assertThat(mapper.from((FloatCharacteristic) null)).isNull();
    }

    @Test
    void givenFloatCharacteristic_whenFromFloatCharacteristic_thenMapAllFields() {
        FloatCharacteristic input = FloatCharacteristic.builder()
                .id("idF")
                .name("floatName")
                .valueType("FLOAT")
                .value(12.34f)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.FloatCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idF");
        assertThat(result.getName()).isEqualTo("floatName");
        assertThat(result.getValueType()).isEqualTo("FLOAT");
        assertThat(result.getValue()).isEqualTo(12.34f);
    }

    @Test
    void givenNullFloatArrayCharacteristic_whenFromFloatArrayCharacteristic_thenReturnNull() {
        assertThat(mapper.from((FloatArrayCharacteristic) null)).isNull();
    }

    @Test
    void givenFloatArrayCharacteristic_whenFromFloatArrayCharacteristic_thenMapAllFields() {
        FloatArrayCharacteristic input = FloatArrayCharacteristic.builder()
                .id("idFA")
                .name("floatArray")
                .valueType("FLOAT_ARRAY")
                .value(List.of(1.1f, 2.2f))
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.FloatArrayCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idFA");
        assertThat(result.getName()).isEqualTo("floatArray");
        assertThat(result.getValueType()).isEqualTo("FLOAT_ARRAY");
        assertThat(result.getValue().get(0)).isCloseTo(1.1f, within(0.01f));
        assertThat(result.getValue().get(1)).isCloseTo(2.2f, within(0.01f));
    }

    @Test
    void givenNullIntegerCharacteristic_whenFromIntegerCharacteristic_thenReturnNull() {
        assertThat(mapper.from((IntegerCharacteristic) null)).isNull();
    }

    @Test
    void givenIntegerCharacteristic_whenFromIntegerCharacteristic_thenMapAllFields() {
        IntegerCharacteristic input = IntegerCharacteristic.builder()
                .id("idI")
                .name("intName")
                .valueType("INTEGER")
                .value(123)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.IntegerCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idI");
        assertThat(result.getName()).isEqualTo("intName");
        assertThat(result.getValueType()).isEqualTo("INTEGER");
        assertThat(result.getValue()).isEqualTo(123);
    }

    @Test
    void givenNullIntegerArrayCharacteristic_whenFromIntegerArrayCharacteristic_thenReturnNull() {
        assertThat(mapper.from((com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.IntegerArrayCharacteristic) null)).isNull();
    }

    @Test
    void givenIntegerArrayCharacteristic_whenFromIntegerArrayCharacteristic_thenMapAllFields() {
        IntegerArrayCharacteristic input = IntegerArrayCharacteristic.builder()
                .id("idIA")
                .name("intArray")
                .valueType("INTEGER_ARRAY")
                .value(Arrays.asList(1, 2, 3))
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.IntegerArrayCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idIA");
        assertThat(result.getName()).isEqualTo("intArray");
        assertThat(result.getValueType()).isEqualTo("INTEGER_ARRAY");
        assertThat(result.getValue()).containsExactly(1, 2, 3);
    }

    @Test
    void givenNullStringCharacteristic_whenFromStringCharacteristic_thenReturnNull() {
        assertThat(mapper.from((StringCharacteristic) null)).isNull();
    }

    @Test
    void givenStringCharacteristic_whenFromStringCharacteristic_thenMapAllFields() {
        StringCharacteristic input = StringCharacteristic.builder()
                .id("idS")
                .name("strName")
                .valueType("STRING")
                .value("abc")
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idS");
        assertThat(result.getName()).isEqualTo("strName");
        assertThat(result.getValueType()).isEqualTo("STRING");
        assertThat(result.getValue()).isEqualTo("abc");
    }

    @Test
    void givenNullStringArrayCharacteristic_whenFromStringArrayCharacteristic_thenReturnNull() {
        assertThat(mapper.from((StringArrayCharacteristic) null)).isNull();
    }

    @Test
    void givenStringArrayCharacteristic_whenFromStringArrayCharacteristic_thenMapAllFields() {
        StringArrayCharacteristic input = StringArrayCharacteristic.builder()
                .id("idSA")
                .name("strArray")
                .valueType("STRING_ARRAY")
                .value(Arrays.asList("a", "b"))
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringArrayCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idSA");
        assertThat(result.getName()).isEqualTo("strArray");
        assertThat(result.getValueType()).isEqualTo("STRING_ARRAY");
        assertThat(result.getValue()).containsExactly("a", "b");
    }

    @Test
    void givenNullObjectCharacteristic_whenFromObjectCharacteristic_thenReturnNull() {
        assertThat(mapper.from((ObjectCharacteristic) null)).isNull();
    }

    @Test
    void givenObjectCharacteristic_whenFromObjectCharacteristic_thenMapAllFields() {
        ObjectCharacteristic input = ObjectCharacteristic.builder()
                .id("idO")
                .name("objName")
                .valueType("OBJECT")
                .value(new Object())
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ObjectCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idO");
        assertThat(result.getName()).isEqualTo("objName");
        assertThat(result.getValueType()).isEqualTo("OBJECT");
        assertThat(result.getValue()).isNotNull();
    }

    @Test
    void givenNullObjectArrayCharacteristic_whenFromObjectArrayCharacteristic_thenReturnNull() {
        assertThat(mapper.from((ObjectArrayCharacteristic) null)).isNull();
    }

    @Test
    void givenObjectArrayCharacteristic_whenFromObjectArrayCharacteristic_thenMapAllFields() {
        ObjectArrayCharacteristic input = ObjectArrayCharacteristic.builder()
                .id("idOA")
                .name("objArray")
                .valueType("OBJECT_ARRAY")
                .value(Arrays.asList(new Object(), new Object()))
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ObjectArrayCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idOA");
        assertThat(result.getName()).isEqualTo("objArray");
        assertThat(result.getValueType()).isEqualTo("OBJECT_ARRAY");
        assertThat(result.getValue()).hasSize(2);
    }

    @Test
    void givenNullValidityCharacteristic_whenFromValidityCharacteristic_thenReturnNull() {
        assertThat(mapper.from((ValidityCharacteristic) null)).isNull();
    }

    @Test
    void givenValidityCharacteristic_whenFromValidityCharacteristic_thenMapAllFields() {
        ValidityValue value = ValidityValue.builder()
                .value(3)
                .unitOfMeasure("days")
                .build();

        ValidityCharacteristic input = ValidityCharacteristic.builder()
                .id("idV")
                .name("validity")
                .valueType("VALIDITY")
                .value(value)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic result = mapper.from(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("idV");
        assertThat(result.getName()).isEqualTo("validity");
        assertThat(result.getValueType()).isEqualTo("VALIDITY");
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getValue()).isEqualTo(3);
        assertThat(result.getValue().getUnitOfMeasure()).isEqualTo("days");
    }

    @Test
    void givenDtoBooleanArrayCharacteristic_whenMappingToModel_thenFieldsAreMappedCorrectly() {
        // GIVEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic dto =
                new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic();
        dto.setAtType("BooleanArrayCharacteristic");
        dto.setValue(List.of(true, false, true));

        // WHEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic result = mapper.from(dto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getAtType()).isEqualTo("BooleanArrayCharacteristic");
        assertThat(result.getValue())
                .containsExactly(true, false, true);
    }

    @Test
    void givenModelBooleanArrayCharacteristic_whenMappingToDto_thenFieldsAreMappedCorrectly() {
        // GIVEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic model =
                new com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic(List.of(false, true));
        model.setAtType("BooleanArrayCharacteristic");

        // WHEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic result =
                mapper.from(model);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getAtType()).isEqualTo("BooleanArrayCharacteristic");
        assertThat(result.getValue())
                .containsExactly(false, true);
    }

    @Test
    void givenDtoWithNullValue_whenMappingToModel_thenValueIsNull() {
        // GIVEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic dto =
                new com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic();
        dto.setAtType("BooleanArrayCharacteristic");
        dto.setValue(null);

        // WHEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic result = mapper.from(dto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNull();
    }

    @Test
    void givenModelWithEmptyValue_whenMappingToDto_thenValueIsEmpty() {
        // GIVEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic model =
                new com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.BooleanArrayCharacteristic(List.of());
        model.setAtType("BooleanArrayCharacteristic");

        // WHEN
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic result =
                mapper.from(model);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEmpty();
    }

}
