// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType.MIGRATE;
import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelationshipType.MIGRATEFROM;
import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelationshipType.MIGRATETO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        OrchestrationPlanMapperImpl.class,
        EnumMapperImpl.class,
        ObjectMapper.class,
        CharacteristicMapperImpl.class
})
class OrchestrationPlanMapperImplTest {

    public static final String PRODUCT_ORDER_DTO_JSON_FILE = "/ProductOrderDTO.json";

    @Autowired
    private OrchestrationPlanMapper orchestrationPlanMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @DisplayName("given ProductOrder" + "when mapping ProductOrder to OrchestrationPlan" + " then return OrchestrationPlan")
    @Test
    void mapToOrchestrationPlanTest() {

        //given
        ProductOrder productOrder = createProductOrderFromJsonFile();
        List<RelatedPartyRefOrPartyRoleRef> relatedPartyList = new ArrayList<>(productOrder.getRelatedParty());
        OrchestrationPlan orchestrationPlanResult = orchestrationPlanMapper.toOrchestrationPlan(productOrder);
        Assertions.assertEquals(productOrder.getId(), orchestrationPlanResult.getRelatedProductOrder().getId());
        Assertions.assertEquals(productOrder.getRequestedCompletionDate(), orchestrationPlanResult.getRequestedDeliveryDate());
        Assertions.assertEquals(State.INITIALIZED, orchestrationPlanResult.getState());
        if (relatedPartyList.get(0).getPartyOrPartyRole() instanceof PartyRef partyRef) {
            assertEquals(partyRef.getName(), orchestrationPlanResult.getRelatedParty().get(0).getName());
            assertEquals(partyRef.getId(), orchestrationPlanResult.getRelatedParty().get(0).getId());
        }
    }

    @DisplayName("given Initialized OrchestrationPlan & Filtered ProductOrderItems" + "when adding ProductOrderItems to the OrchestrationPlan as Nodes"
            + " then returning OrchestrationPlan with Nodes")
    @Test
    void maptoOrchestrationPlanNodeTest() {
        //given... grab event as mockedJSON of ProductOrder
        ProductOrder productOrder = createProductOrderFromJsonFile();
        List<ProductOrderItem> productOrderItems = new ArrayList<>(productOrder.getProductOrderItem());
        OrchestrationPlanNode nodeResult = orchestrationPlanMapper.toInitializedOrchestrationPlanNode(productOrderItems.get(0));
        Assertions.assertEquals(OrchestrationPlanNodeState.INITIALIZED, nodeResult.getState());
    }

    public ProductOrder createProductOrderFromJsonFile() {
        return JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });
    }

    @DisplayName("given Completed orchestration plan" + "when mapping orchestration plan from model to dto" + " then return full dto OrchestrationPlan")
    @Test
    void given_Completed_orchestration_planWhen_mapping_orchestration_plan_from_model_to_dtoThen_return_full_dto_OrchestrationPlan() {
        //given
        OrchestrationPlan orchestrationPlanDocument = JsonUtil.readObjectFromResource("/db_documents/completedOrchestrationPlan.json", new TypeReference<>() {
        });
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan expectedOrchestrationPlanDTO = JsonUtil.readObjectFromResource("/CompletedOrchestrationPlanDTO.json", new TypeReference<>() {
        });
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan actualOrchestrationPlanDTO = orchestrationPlanMapper.toDto(orchestrationPlanDocument);
        assertThat(actualOrchestrationPlanDTO)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .ignoringActualNullFields()
                .ignoringFieldsMatchingRegexes("receivedDate")
                .isEqualTo(expectedOrchestrationPlanDTO)
        ;
        Assertions.assertEquals(false, actualOrchestrationPlanDTO.getArchived());
    }


    @Test
    void map_stringCharacteristic_shouldMapCorrectly() {
        StringCharacteristic stringCharacteristic = new StringCharacteristic();
        stringCharacteristic.setName("StringName");
        stringCharacteristic.setValueType("String");
        stringCharacteristic.setValue("StringValue");

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic) characteristicMapper.from(stringCharacteristic);
        assertThat(mapped.getName()).isEqualTo(stringCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(stringCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(stringCharacteristic.getValue());
    }

    @Test
    void map_booleanCharacteristic_shouldMapCorrectly() {
        BooleanCharacteristic booleanCharacteristic = new BooleanCharacteristic();
        booleanCharacteristic.setName("BooleanName");
        booleanCharacteristic.setValueType("Boolean");
        booleanCharacteristic.setValue(true);

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic) characteristicMapper.from(booleanCharacteristic);
        assertThat(mapped.getName()).isEqualTo(booleanCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(booleanCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(booleanCharacteristic.getValue());
    }

    @Test
    void map_floatCharacteristic_shouldMapCorrectly() {
        FloatCharacteristic floatCharacteristic = new FloatCharacteristic();
        floatCharacteristic.setName("FloatName");
        floatCharacteristic.setValueType("Float");
        floatCharacteristic.setValue(3.14f);

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic) characteristicMapper.from(floatCharacteristic);
        assertThat(mapped.getName()).isEqualTo(floatCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(floatCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(floatCharacteristic.getValue());
    }

    @Test
    void map_integerCharacteristic_shouldMapCorrectly() {
        IntegerCharacteristic integerCharacteristic = new IntegerCharacteristic();
        integerCharacteristic.setName("IntegerName");
        integerCharacteristic.setValueType("Integer");
        integerCharacteristic.setValue(42);

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic) characteristicMapper.from(integerCharacteristic);
        assertThat(mapped.getName()).isEqualTo(integerCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(integerCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(integerCharacteristic.getValue());
    }

    @Test
    void map_objectCharacteristic_shouldMapCorrectly() {
        ObjectCharacteristic objectCharacteristic = new ObjectCharacteristic();
        objectCharacteristic.setName("ObjectName");
        objectCharacteristic.setValueType("Object");
        objectCharacteristic.setValue(new TestObject("testObject"));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectCharacteristic) characteristicMapper.from(objectCharacteristic);
        assertThat(mapped.getName()).isEqualTo(objectCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(objectCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(objectCharacteristic.getValue());
    }

    @Test
    void map_stringArrayCharacteristic_shouldMapCorrectly() {
        StringArrayCharacteristic stringArrayCharacteristic = new StringArrayCharacteristic();
        stringArrayCharacteristic.setName("StringArrayName");
        stringArrayCharacteristic.setValueType("StringArray");
        stringArrayCharacteristic.setValue(List.of("value1", "value2"));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic) characteristicMapper.from(stringArrayCharacteristic);
        assertThat(mapped.getName()).isEqualTo(stringArrayCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(stringArrayCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(stringArrayCharacteristic.getValue());
    }

    @Test
    void map_booleanArrayCharacteristic_shouldMapCorrectly() {
        BooleanArrayCharacteristic booleanArrayCharacteristic = new BooleanArrayCharacteristic();
        booleanArrayCharacteristic.setName("BooleanArrayName");
        booleanArrayCharacteristic.setValueType("BooleanArray");
        booleanArrayCharacteristic.setValue(List.of(true, false));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic) characteristicMapper.from(booleanArrayCharacteristic);
        assertThat(mapped.getName()).isEqualTo(booleanArrayCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(booleanArrayCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(booleanArrayCharacteristic.getValue());
    }

    @Test
    void map_floatArrayCharacteristic_shouldMapCorrectly() {
        FloatArrayCharacteristic floatArrayCharacteristic = new FloatArrayCharacteristic();
        floatArrayCharacteristic.setName("FloatArrayName");
        floatArrayCharacteristic.setValueType("FloatArray");
        floatArrayCharacteristic.setValue(List.of(1.1f, 2.2f));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic) characteristicMapper.from(floatArrayCharacteristic);
        assertThat(mapped.getName()).isEqualTo(floatArrayCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(floatArrayCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(floatArrayCharacteristic.getValue());
    }

    @Test
    void map_integerArrayCharacteristic_shouldMapCorrectly() {
        IntegerArrayCharacteristic integerArrayCharacteristic = new IntegerArrayCharacteristic();
        integerArrayCharacteristic.setName("IntegerArrayName");
        integerArrayCharacteristic.setValueType("IntegerArray");
        integerArrayCharacteristic.setValue(List.of(10, 20));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerArrayCharacteristic) characteristicMapper.from(integerArrayCharacteristic);
        assertThat(mapped.getName()).isEqualTo(integerArrayCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(integerArrayCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(integerArrayCharacteristic.getValue());
    }

    @Test
    void map_objectArrayCharacteristic_shouldMapCorrectly() {
        ObjectArrayCharacteristic objectArrayCharacteristic = new ObjectArrayCharacteristic();
        objectArrayCharacteristic.setName("ObjectArrayName");
        objectArrayCharacteristic.setValueType("ObjectArray");
        objectArrayCharacteristic.setValue(List.of(new TestObject("obj1"), new TestObject("obj2")));

        var mapped = (com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectArrayCharacteristic) characteristicMapper.from(objectArrayCharacteristic);
        assertThat(mapped.getName()).isEqualTo(objectArrayCharacteristic.getName());
        assertThat(mapped.getValueType()).isEqualTo(objectArrayCharacteristic.getValueType());
        assertThat(mapped.getValue()).isEqualTo(objectArrayCharacteristic.getValue());
    }

    @Setter
    @Getter
    private static class TestObject {
        private String name;

        public TestObject(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    private ProductOrderItem buildContractItem(String name, String atType, ItemActionType action, RelationshipType relationshipType) {
        ProductOfferingRef offering = new ProductOfferingRef();
        offering.setName(name);
        offering.setAtType(atType);

        OrderItemRelationship rel = null;
        if (relationshipType != null) {
            rel = new OrderItemRelationship();
            rel.setRelationshipType(relationshipType);
        }

        ProductOrderItem item = new ProductOrderItem();
        item.setProductOffering(offering);
        item.setAction(action);

        if (rel != null) {
            item.setProductOrderItemRelationship(List.of(rel));
        }

        return item;
    }

    @Test
    void givenNonMigrationContract_whenMapContractName_thenReturnFirstContractName() {
        // Given
        var contract = buildContractItem("Basic Contract", "Contract", ItemActionType.ADD, null);

        // When
        String result = orchestrationPlanMapper.mapContractName(List.of(contract));

        // Then
        assertThat(result).isEqualTo("Basic Contract");
    }

    @Test
    void givenMigrationContracts_whenMapContractName_thenReturnContractWithMigrateFrom() {
        // Given
        var migrateFrom = buildContractItem("New Contract", "Contract", MIGRATE, MIGRATEFROM);
        var migrateTo = buildContractItem("Old Contract", "Contract", MIGRATE, MIGRATETO);

        // When
        String result = orchestrationPlanMapper.mapContractName(List.of(migrateTo, migrateFrom));

        // Then
        assertThat(result).isEqualTo("New Contract");
    }

    @Test
    void givenNoContractItems_whenMapContractName_thenReturnNull() {
        // Given
        var nonContract = buildContractItem("Other", "Service", ItemActionType.ADD, null);

        // When
        String result = orchestrationPlanMapper.mapContractName(List.of(nonContract));

        // Then
        assertThat(result).isNull();
    }

    @Test
    void givenEmptyList_whenMapContractName_thenReturnNull() {
        // Given
        List<ProductOrderItem> emptyList = List.of();

        // When
        String result = orchestrationPlanMapper.mapContractName(emptyList);

        // Then
        assertThat(result).isNull();
    }
}


