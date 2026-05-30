// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

import com.orange.discobole.admin.*;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyActionOperationRepository;
import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyActionOperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.util.ReflectionTestUtils;

public class PolicyActionOperationServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private PolicyActionOperationRepository policyActionOperationRepository;

    @Mock
    private ProductCatalogQueryService productCatalogQueryService;

    @InjectMocks
    private PolicyActionOperationServiceImpl policyActionOperationService;

    @Mock
    private MongoTemplate mongoTemplate;

    private PolicyActionOperation policyActionOperation;
    private PolicyActionOperation policyActionOperation1;
    private Value value;


    @BeforeEach
    public void setup() {
        policyActionOperation = new PolicyActionOperation();
        PolicyAction policyAction = new PolicyAction();
        PolicyActionValue policyActionValue = new PolicyActionValue();
        policyAction.setActionStrategy("strategy");
        policyActionOperation.setPolicyAction(Arrays.asList(policyAction));
        policyActionOperation.setFields("fields");
        policyActionOperation.setActionValue(policyActionValue);
        value = new Value();
        value.setObjectId("123");
        value.setObjectType(ObjectType.PRODUCTOFFERINGPRICE);
        value.setObjectName("name");
        policyActionValue.setValue(value);

        policyActionOperation1 = new PolicyActionOperation();
        policyActionOperation1.setFields("fields1");
        policyActionOperation1.setActionValue(new PolicyActionValue().value(value));

        PolicyAction policyAction1 = new PolicyAction();
        policyAction1.setActionStrategy("strategy1");
        policyActionOperation1.setPolicyAction(Arrays.asList(policyAction1));
        ReflectionTestUtils.setField(policyActionOperationService, "mongoTemplate", mongoTemplate);
    }

    @Test
    public void testCreatePolicyActionOperationSuccess() {
        when(productCatalogQueryService.fetchProductofferingPriceById(anyString())).thenReturn("123");
        when(policyActionOperationRepository.save(any(PolicyActionOperation.class))).thenReturn(policyActionOperation);

        PolicyActionOperation createdPolicyActionOperation = policyActionOperationService.createPolicyActionOperation(policyActionOperation);

        assertNotNull(createdPolicyActionOperation);
        assertEquals("fields", createdPolicyActionOperation.getFields());
        verify(policyActionOperationRepository, times(1)).save(any(PolicyActionOperation.class));
    }

    @Test
    public void testCreatePolicyActionOperationMissingFieldsException() {
        policyActionOperation.setFields(null);
        assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.createPolicyActionOperation(policyActionOperation);
        });
    }

    @Test
    public void testCreatePolicyActionOperationMissingActionStrategyException() {
        policyActionOperation.getPolicyAction().get(0).setActionStrategy(null);
        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.createPolicyActionOperation(policyActionOperation);
        });
        assertEquals("Missing Body PolicyAction ActionStrategy", exception.getMessage());
    }

    @Test
    public void testCreatePolicyActionOperationPOPNotFoundException() {
        when(productCatalogQueryService.fetchProductofferingPriceById(anyString())).thenReturn(null);
        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.createPolicyActionOperation(policyActionOperation);
        });
        assertEquals("POP not found", exception.getMessage());
    }

    @Test
    public void testGetPolicyActionOperationByIdSuccess() {
        when(policyActionOperationRepository.findById(anyString())).thenReturn(Optional.of(policyActionOperation));

        PolicyActionOperation result = policyActionOperationService.getPolicyActionOperationById("someId");

        assertNotNull(result);
        verify(policyActionOperationRepository, times(1)).findById(anyString());
    }

    @Test
    public void testGetPolicyActionOperationByIdNotFoundException() {
        when(policyActionOperationRepository.findById(anyString())).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.getPolicyActionOperationById("someId");
        });
        assertEquals("PolicyActionOperation Not Found", exception.getMessage());
    }

    @Test
    public void testUpdatePolicyActionOperationNotFoundException() {
        when(policyActionOperationRepository.findById(anyString())).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.updatePolicyActionOperation(policyActionOperation);
        });
        assertEquals("PolicyActionOperation Not Found", exception.getMessage());
    }

    @Test
    public void testDeletePolicyActionOperationSuccess() {
        when(policyActionOperationRepository.findById(anyString())).thenReturn(Optional.of(policyActionOperation));

        assertDoesNotThrow(() -> policyActionOperationService.deletePolicyActionOperation("someId"));
        verify(policyActionOperationRepository, times(1)).delete(any(PolicyActionOperation.class));
    }

    @Test
    public void testDeletePolicyActionOperationNotFoundException() {
        when(policyActionOperationRepository.findById(anyString())).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyActionOperationService.deletePolicyActionOperation("someId");
        });
        assertEquals("PolicyActionOperation Not Found", exception.getMessage());
    }

    @Test
    public void testGetAllPolicyActionOperation_with_pagination() {
        AggregationResults<PolicyActionOperation> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getMappedResults()).thenReturn(Arrays.asList(policyActionOperation, policyActionOperation1));
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("policyActionOperation"), eq(PolicyActionOperation.class)))
                .thenReturn(aggregationResults);

        List<PolicyActionOperation> result = policyActionOperationService.getAllPolicyActionOperation(0L,5L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("fields", result.get(0).getFields());
        assertEquals("fields1", result.get(1).getFields());
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("policyActionOperation"), eq(PolicyActionOperation.class));
    }

    @Test
    public void testGetAllPolicyActionOperation_without_pagination() {
        when(mongoTemplate.findAll(PolicyActionOperation.class)).thenReturn(Arrays.asList(policyActionOperation, policyActionOperation1));

        List<PolicyActionOperation> result = policyActionOperationService.getAllPolicyActionOperation(null,null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("fields", result.get(0).getFields());
        assertEquals("fields1", result.get(1).getFields());
        verify(mongoTemplate, times(1)).findAll(PolicyActionOperation.class);
    }

    @Test
    public void testUpdatePolicyActionOperation() {
        policyActionOperation1.setPolicyAction(new ArrayList<>());
        policyActionOperation1.setNote(new ArrayList<>());
        policyActionOperation1.setActionValue(null);

        policyActionOperation.setPolicyAction(new ArrayList<>());
        policyActionOperation.setNote(new ArrayList<>());
        policyActionOperation.setActionValue(new PolicyActionValue().value(value));

        when(productCatalogQueryService.fetchProductofferingPriceById(anyString())).thenReturn("123");
        policyActionOperationService.updatePolicyActionOperation(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getPolicyAction());
        assertNotNull(policyActionOperation1.getNote());
        assertNotNull(policyActionOperation1.getActionValue());
    }

    @Test
    public void testUpdateOldActionValue() {
        policyActionOperation.setActionValue(new PolicyActionValue().value(value));
        when(productCatalogQueryService.fetchProductofferingPriceById(anyString())).thenReturn("123");

        policyActionOperationService.updateOldActionValue(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getActionValue());
        assertEquals("123", policyActionOperation1.getActionValue().getValue().getObjectId());
    }

    @Test
    public void testSetOperationNewNote() {
        Note note = new Note();
        note.setText("Test Note");
        policyActionOperation.setNote(Arrays.asList(note));

        policyActionOperationService.setOperationNewNote(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getNote());
        assertEquals(1, policyActionOperation1.getNote().size());
        assertEquals("Test Note", policyActionOperation1.getNote().get(0).getText());
    }

    @Test
    public void testUpdateOperationOldNote() {
        Note oldNote = new Note();
        oldNote.setId(UUID.randomUUID().toString());
        oldNote.setText("Old Note");
        policyActionOperation1.setNote(Arrays.asList(oldNote));

        Note newNote = new Note();
        newNote.setText("New Note");
        policyActionOperation.setNote(Arrays.asList(newNote));

        policyActionOperationService.updateOperationOldNote(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getNote());
        assertEquals(1, policyActionOperation1.getNote().size());
        assertEquals("New Note", policyActionOperation1.getNote().get(0).getText());
        assertEquals(oldNote.getId(), policyActionOperation1.getNote().get(0).getId());
    }

    @Test
    public void testSetNewPolicyAction() {
        policyActionOperation.setPolicyAction(new ArrayList<>());

        policyActionOperationService.setNewPolicyAction(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getPolicyAction());
        assertTrue(policyActionOperation1.getPolicyAction().isEmpty());
    }

    @Test
    public void testUpdateOldPolicyAction() {
        PolicyAction oldPolicyAction = new PolicyAction();
        oldPolicyAction.setActionStrategy("Old Strategy");
        policyActionOperation1.setPolicyAction(Arrays.asList(oldPolicyAction));

        PolicyAction newPolicyAction = new PolicyAction();
        newPolicyAction.setActionStrategy("New Strategy");
        policyActionOperation.setPolicyAction(Arrays.asList(newPolicyAction));

        policyActionOperationService.updateOldPolicyAction(policyActionOperation1, policyActionOperation);

        assertNotNull(policyActionOperation1.getPolicyAction());
        assertEquals(1, policyActionOperation1.getPolicyAction().size());
        assertEquals("New Strategy", policyActionOperation1.getPolicyAction().get(0).getActionStrategy());
    }

    @Test
    public void testSetNewNote() {
        PolicyAction policyAction = new PolicyAction();
        policyAction.setActionStrategy("Strategy");
        Note newNote = new Note();
        newNote.setText("New Note");
        policyActionOperation.setPolicyAction(Arrays.asList(policyAction));
        policyActionOperation.getPolicyAction().get(0).setNote(Arrays.asList(newNote));

        PolicyAction policyActionUpdate = new PolicyAction();
        policyActionOperation1.setPolicyAction(Arrays.asList(policyActionUpdate));

        policyActionOperationService.setNewNote(policyActionOperation, policyActionUpdate);

        assertNotNull(policyActionUpdate.getNote());
        assertEquals(1, policyActionUpdate.getNote().size());
        assertEquals("New Note", policyActionUpdate.getNote().get(0).getText());
    }

    @Test
    public void testUpdateOldNote() {
        Note oldNote = new Note();
        oldNote.setId(UUID.randomUUID().toString());
        oldNote.setText("Old Note");
        PolicyAction policyActionUpdate = new PolicyAction();
        policyActionUpdate.setNote(Arrays.asList(oldNote));
        policyActionOperation1.setPolicyAction(Arrays.asList(policyActionUpdate));

        Note newNote = new Note();
        newNote.setText("New Note");
        PolicyAction policyAction = new PolicyAction();
        policyAction.setNote(Arrays.asList(newNote));
        policyActionOperation.setPolicyAction(Arrays.asList(policyAction));

        policyActionOperationService.updateOldNote(policyActionOperation, policyActionUpdate);

        assertNotNull(policyActionUpdate.getNote());
        assertEquals(1, policyActionUpdate.getNote().size());
        assertEquals("New Note", policyActionUpdate.getNote().get(0).getText());
        assertEquals(oldNote.getId(), policyActionUpdate.getNote().get(0).getId());
    }
}
