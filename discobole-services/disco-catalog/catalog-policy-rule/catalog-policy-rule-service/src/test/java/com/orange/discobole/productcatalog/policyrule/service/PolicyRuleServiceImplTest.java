// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.*;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.policyrule.dto.PolicyRuleRef;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOffering;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.dto.ProductSpecification;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.*;
import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PolicyRuleServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private PolicyRuleRepository policyRuleRepository;

    @Mock
    private PolicyDomainRepository policyDomainRepository;

    @Mock
    private PolicyEventRepository policyEventRepository;

    @Mock
    private PolicyActionOperationRepository policyActionOperationRepository;

    @Mock
    private PolicyConditionRepository policyConditionRepository;

    @Mock
    private ProductCatalogQueryService productCatalogQueryService;

    @InjectMocks
    private PolicyRuleServiceImpl policyRuleService;

    @Mock
    private ConfigurableProperties configurableProperties;

    private StreamBridge bridge;

    @BeforeEach
    void setUp() {
        bridge = Mockito.mock(StreamBridge.class);
        ReflectionTestUtils.setField(policyRuleService, "bridge", bridge);
    }

    @Test
    void testCreatePolicyRule_withMissingFields_throwsException() {
        PolicyRule policyRule = new PolicyRule();

        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyDomain(Collections.emptyList());
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyDomain(List.of(new PolicyDomainRef()));
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyEvent(new PolicyEventRef());
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyAction(Collections.emptyList());
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyAction(List.of(new PolicyActionRef()));
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));

        policyRule.setPolicyCondition(new PolicyConditionRef());
        assertThrows(MissingBodyFieldException.class, () -> policyRuleService.createPolicyRule(policyRule));
    }

    @Test
    void testCreatePolicyRule_withValidPolicyRule_savesPolicyRule() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setPolicyDomain(List.of(new PolicyDomainRef()));
        policyRule.setPolicyEvent(new PolicyEventRef());
        policyRule.setPolicyAction(List.of(new PolicyActionRef()));
        policyRule.setPolicyCondition(new PolicyConditionRef());

        when(policyDomainRepository.findById(any())).thenReturn(Optional.of(new PolicyDomainRef()));
        when(policyEventRepository.findById(any())).thenReturn(Optional.of(new PolicyEvent()));
        when(policyActionOperationRepository.findById(any())).thenReturn(Optional.of(new PolicyActionOperation()));
        when(policyConditionRepository.findById(any())).thenReturn(Optional.of(new PolicyCondition()));
        when(policyRuleRepository.save(any(PolicyRule.class))).thenReturn(policyRule);

        PolicyRule createdPolicyRule = policyRuleService.createPolicyRule(policyRule);

        assertNotNull(createdPolicyRule);
        verify(policyRuleRepository, times(2)).save(policyRule);
    }

    @Test
    void testGetPolicyRuleById_withExistingId_returnsPolicyRule() {
        PolicyRule policyRule = new PolicyRule();
        when(policyRuleRepository.findById("someId")).thenReturn(Optional.of(policyRule));

        PolicyRule foundPolicyRule = policyRuleService.getPolicyRuleById("someId");

        assertNotNull(foundPolicyRule);
        assertEquals(policyRule, foundPolicyRule);
    }

    @Test
    void testGetPolicyRuleById_withNonExistingId_throwsException() {
        when(policyRuleRepository.findById("someId")).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.getPolicyRuleById("someId");
        });
        assertEquals(60, exception.getCode());
    }

    @Test
    void testDeletePolicyRule_withExistingId_deletesPolicyRule() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        when(policyRuleRepository.findById("123")).thenReturn(Optional.of(policyRule));
        when(bridge.send(anyString(), any())).thenReturn(true);
        policyRuleService.deletePolicyRule("123");

        verify(policyRuleRepository, times(1)).deleteById(policyRule.getId());
    }

    @Test
    void testDeletePolicyRule_withNonExistingId_throwsException() {
        when(policyRuleRepository.findById("someId")).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.deletePolicyRule("someId");
        });
        assertEquals(60, exception.getCode());
    }

    @Test
    void testUpdatePolicyRule_withNonExistingId_throwsException() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setId("someId");

        when(policyRuleRepository.findById("someId")).thenReturn(Optional.empty());

        MissingBodyFieldException exception = assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.updatePolicyRule(policyRule);
        });
        assertEquals(60, exception.getCode());
    }

    @Test
    public void testIsValidPolicyRuleToDelete_inTest() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());

        boolean result = policyRuleService.isValidPolicyRuleToDelete(policyRule);
        assertTrue(result);
    }

    @Test
    public void testIsValidPolicyRuleToDelete_notInTest() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("notInTest");

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToDelete(policyRule);
        });
    }

    @Test
    public void testIsValidPolicyRuleToDelete_withLaunchedProductOffering() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        ProductOffering launchedProductOffering = new ProductOffering();
        launchedProductOffering.setLifecycleStatus("launched");

        List<ProductOffering> productOfferings = Collections.singletonList(launchedProductOffering);

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(productOfferings);
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToDelete(policyRule);
        });
    }

    @Test
    public void testIsValidPolicyRuleToDelete_withLaunchedProductSpecification() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        ProductSpecification launchedProductSpecification = new ProductSpecification();
        launchedProductSpecification.setLifecycleStatus("launched");

        List<ProductSpecification> productSpecifications = Collections.singletonList(launchedProductSpecification);

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(productSpecifications);

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToDelete(policyRule);
        });
    }

    @Test
    public void testIsValidPolicyRuleToUpdate_inTest() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> policyRuleService.isValidPolicyRuleToUpdate(policyRule));
    }

    @Test
    public void testIsValidPolicyRuleToUpdate_notInTest() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("notInTest");

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToUpdate(policyRule);
        });
    }

    @Test
    public void testIsValidPolicyRuleToUpdate_withNonInTestProductOffering() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        ProductOffering nonInTestProductOffering = new ProductOffering();
        nonInTestProductOffering.setLifecycleStatus("launched");

        List<ProductOffering> productOfferings = Collections.singletonList(nonInTestProductOffering);

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(productOfferings);
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToUpdate(policyRule);
        });
    }

    @Test
    public void testIsValidPolicyRuleToUpdate_withNonInTestProductSpecification() {
        PolicyRule policyRule = new PolicyRule();
        policyRule.setState("inTest");
        policyRule.setId("testId");

        ProductSpecification nonInTestProductSpecification = new ProductSpecification();
        nonInTestProductSpecification.setLifecycleStatus("launched");

        List<ProductSpecification> productSpecifications = Collections.singletonList(nonInTestProductSpecification);

        when(productCatalogQueryService.fetchProductOfferingByPolicyRule(policyRule.getId())).thenReturn(Collections.emptyList());
        when(productCatalogQueryService.fetchProductSpecificationByPolicyRule(policyRule.getId())).thenReturn(productSpecifications);

        assertThrows(MissingBodyFieldException.class, () -> {
            policyRuleService.isValidPolicyRuleToUpdate(policyRule);
        });
    }

        @Test
        void getPOPByPO_shouldReturnEmptyList_whenNoPolicyRuleRefs() {
            ProductOffering offering = new ProductOffering();
            offering.setPolicyRuleRef(Collections.emptyList());

            Mockito.when(productCatalogQueryService.fetchPOById("po-1")).thenReturn(offering);

            List<ProductOfferingPrice> result = policyRuleService.getPOPByPO("po-1");

            assertTrue(result.isEmpty());
        }

        @Test
        void getPOPByPO_shouldReturnPricesFromPathAndActionValue() {
            String poId = "po-123";

            // Mock ProductOffering with 1 PolicyRuleRef
            PolicyRuleRef ruleRef = new PolicyRuleRef();
            ruleRef.setId("rule-1");
            ProductOffering offering = new ProductOffering();
            offering.setPolicyRuleRef(List.of(ruleRef));

            Mockito.when(productCatalogQueryService.fetchPOById(poId)).thenReturn(offering);

            // Mock PolicyRule
            PolicyRule policyRule = new PolicyRule();
            PolicyActionRef policyAction = new PolicyActionRef();
            policyAction.setId("action-1");
            policyRule.setPolicyAction(List.of(policyAction));
            Mockito.when(policyRuleRepository.findById("rule-1")).thenReturn(Optional.of(policyRule));

            // Mock PolicyActionOperation
            PolicyActionOperation operation = new PolicyActionOperation();
            operation.setPath("(@.something == '11111111-1111-1111-1111-111111111111')");
            PolicyActionValue value = new PolicyActionValue();
            Value details = new Value();
            details.setObjectId("22222222-2222-2222-2222-222222222222");
            value.setValue(details);
            operation.setActionValue(value);

            Mockito.when(policyActionOperationRepository.findById("action-1")).thenReturn(Optional.of(operation));

            // Mock ProductOfferingPrice results
            ProductOfferingPrice pop1 = new ProductOfferingPrice("11111111-1111-1111-1111-111111111111");
            ProductOfferingPrice pop2 = new ProductOfferingPrice("22222222-2222-2222-2222-222222222222");

            Mockito.when(productCatalogQueryService.fetchProductOfferingPriceByIds(
                    List.of("11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222")
            )).thenReturn(List.of(pop1, pop2));

            // Act
            List<ProductOfferingPrice> result = policyRuleService.getPOPByPO(poId);

            // Assert
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(pop -> pop.getId().equals("11111111-1111-1111-1111-111111111111")));
            assertTrue(result.stream().anyMatch(pop -> pop.getId().equals("22222222-2222-2222-2222-222222222222")));
        }

        @Test
        void getPOPByPO_shouldDeduplicateIds() {
            String poId = "po-dup";

            PolicyRuleRef ruleRef = new PolicyRuleRef();
            ruleRef.setId("rule-dup");
            ProductOffering offering = new ProductOffering();
            offering.setPolicyRuleRef(List.of(ruleRef));
            Mockito.when(productCatalogQueryService.fetchPOById(poId)).thenReturn(offering);

            PolicyRule rule = new PolicyRule();
            PolicyActionRef action = new PolicyActionRef();
            action.setId("action-dup");
            rule.setPolicyAction(List.of(action));
            Mockito.when(policyRuleRepository.findById("rule-dup")).thenReturn(Optional.of(rule));

            PolicyActionOperation op = new PolicyActionOperation();
            op.setPath("(@.id == '33333333-3333-3333-3333-333333333333')");
            PolicyActionValue val = new PolicyActionValue();
            Value details = new Value();
            details.setObjectId("33333333-3333-3333-3333-333333333333");
            val.setValue(details);
            op.setActionValue(val);

            Mockito.when(policyActionOperationRepository.findById("action-dup")).thenReturn(Optional.of(op));

            ProductOfferingPrice pop = new ProductOfferingPrice("33333333-3333-3333-3333-333333333333");
            Mockito.when(productCatalogQueryService.fetchProductOfferingPriceByIds(
                    List.of("33333333-3333-3333-3333-333333333333")
            )).thenReturn(List.of(pop));

            List<ProductOfferingPrice> result = policyRuleService.getPOPByPO(poId);

            assertEquals(1, result.size()); // deduplicated
        }
}
