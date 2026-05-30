// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.builder.OrchestrationPlanBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl.ProductManagementServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.ProductUnExpectedStateException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliveryOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl.DeliverSelectedNodesServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl.MaintainDeliveryNodeDeliveryNodeRelatedProductImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl.VerifyNodeServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationPlanServiceImpl;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.ServiceRef;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;

import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl.DeliverSelectedNodesServiceImpl.STATUS_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
public class VerifyDependentsForDeleteAction {

    OrchestrationPlanNode orchestrationPlanNode;

    OrchestrationPlan orchestrationPlan;

    Product productDTO;

    @Mock
    ProductManagementServiceImpl productManagementService;

    @Mock
    OrchestrationPlanServiceImpl orchestrationPlanStateService;

    @Mock
    OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Mock
    OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;

    @Mock
    OrchestrationDeliveryFalloutManagement requestScopedPersistenceService;

    @Mock
    OrchestrationPlanService orchestrationPlanService;

    VerifyNodeServiceImpl verifyNodeDeliveryService;

    final ObjectMapper objectMapper = new ObjectMapper();

    DeliverSelectedNodesServiceImpl deliverSelectedNodesService;
    @Spy
    @InjectMocks
    MaintainDeliveryNodeDeliveryNodeRelatedProductImpl maintainDeliveryNodeRelatedProduct;

    @Mock
    EventPublisher eventPublisher;

    @Mock
    DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    @Mock
    DeliveryOrderService deliveryOrderService;

    CoodRecoverableAndNonRetryableException exception;

    ProductUnExpectedStateException productUnExpectedStateException;

    ProductValidationException productValidationExceptionn;

    @Mock
    UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    @Mock
    CharacteristicMapper characteristicMapper;

    @Given("the previous prerequisites")
    public void thePreviousPrerequisites() {
        // the previous prerequisites
    }

    @Before("@VerifyDependentsForDeleteAction")
    public void setup() {
        MockitoAnnotations.openMocks(this);
        verifyNodeDeliveryService = Mockito.spy(new VerifyNodeServiceImpl(productManagementService));
        //TODO: find better way to initialize deliverSelectedNodesService, couldn't figure out a working solution using @Spy,@injectMock annotations
        deliverSelectedNodesService = new DeliverSelectedNodesServiceImpl(orchestrationPlanStateService,
                productManagementService,
                maintainDeliveryNodeRelatedProduct,
                verifyNodeDeliveryService,
                dataPersistenceKafkaSessionService,
                updateNodeAndProductStateHandler,
                orchestrationPlanRepository,
                deliveryOrderService,
                new ObjectMapper()
        );
    }

    @Given("an OrchestrationNode {string}")
    public void anOrchestrationNode(String node) throws JsonProcessingException {
        orchestrationPlanNode = objectMapper.readValue(node, new TypeReference<>() {
        });

        orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .orchestrationPlanNodes(Set.of(orchestrationPlanNode))
                .build();
    }

    @And("a product from product inventory {string}")
    public void aProductFromProductInventory(String product) throws JsonProcessingException {
        productDTO = objectMapper.readValue(product, new TypeReference<>() {
        });

        productDTO.setRealizingService(List.of(ServiceRef.builder()
                .id("service")
                .href("href")
                .build()));


        productDTO.setProductSpecification(ProductSpecificationRef.builder().id("id").build());
        productDTO.setStatus(ProductStatusType.ACTIVE);
    }

    @Then("a service order request is created to deliver this node with id={string}")
    public void aServiceOrderRequestIsCreatedToDeliverThisNodeWithId(String nodeId) {
        Mockito.verify(deliveryOrderService, Mockito.times(1))
                .publishStartDeliveryEvent(any(), any(), any());
    }

    @And("a relatedProduct with relationship type {string} is added with relatedProduct.id = {string} to the node with id={string}")
    public void aRelatedProductWithRelationshipTypeIsAddedWithRelatedProductIdToTheNodeWithId(String relationShipType, String relatedProductId, String nodeId) {
        RelatedProduct relatedProduct = orchestrationPlanNode.getRelatedProduct().stream().filter(product -> product.getRelationshipType().getValue().equals(relationShipType)).findAny().get();
        Assertions.assertEquals(relatedProductId, relatedProduct.getId());
    }

    @Given("an OrchestrationPlan {string}")
    public void anOrchestrationPlan(String orchestrationPlanString) throws JsonProcessingException {
        verifyNodeDeliveryService = new VerifyNodeServiceImpl(productManagementService);
        orchestrationPlan = objectMapper.readValue(orchestrationPlanString, new TypeReference<>() {
        });
        orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream().findAny().get();
    }

    @When("COOD retrieves the related product from CPIB with id = {string} and id = {string}, CPIB returns state = {string} and state = {string} respectively")
    public void coodRetrievesTheRelatedProductFromCPIBWithIdAndIdCPIBReturnsStateAndStateRespectively(String relatedProductId1, String relatedProductId2, String state1, String state2) {

        Product relatedProductDTO1 = Product.builder()
                .id(relatedProductId1)
                .status(ProductStatusType.fromValue(state1))
                .build();

        Product relatedProductDTO2 = Product.builder()
                .id(relatedProductId2)
                .status(ProductStatusType.fromValue(state2))
                .build();


        mocking(productDTO, List.of(relatedProductDTO1, relatedProductDTO2));
    }

    @And("a relatedProduct with relationship type {string} is added with relatedProduct.id = {string} and relatedProduct.id = {string} to the node with id={string}")
    public void aRelatedProductWithRelationshipTypeIsAddedWithRelatedProductIdAndRelatedProductIdToTheNodeWithId(String relationShipType, String relatedProductId1, String relatedProductId2, String nodeId) {
        orchestrationPlanNode.getRelatedProduct().stream()
                .filter(product -> product.getRelationshipType().getValue().equals(relationShipType))
                .forEach(relatedProduct -> Assertions.assertTrue(List.of(relatedProductId1, relatedProductId2).contains(relatedProduct.getId())));

    }

    @Then("no service order request is created for this orchestration node with id {string}")
    public void noServiceOrderRequestIsCreatedForThisOrchestrationNodeWithId(String nodeId) {
        Mockito.verify(eventPublisher, never()).publishEvent(any(), any());
    }


    @And("errorMessage.code = {string}")
    public void errorMessageCode(String errorCode) {
        assertEquals(errorCode, productUnExpectedStateException.getCoodError().code());
    }

    @And("errorMessage.message = {string}")
    public void errorMessageMessage(String errorMessage) {
        assertEquals(errorMessage, productUnExpectedStateException.getCoodError().message());
    }

    @And("errorMessage.reason = {string}")
    public void errorMessageReason(String errorMessageReason) {
        assertEquals(errorMessageReason, productUnExpectedStateException.getCoodError().reason());
    }

    @And("update OrchestrationNode status to {string}")
    public void updateOrchestrationNodeStatusTo(String updatedNoteStatus) {
        Assertions.assertEquals(updatedNoteStatus, orchestrationPlanNode.getState().value());
    }

    @And("publish OrchestrationNodeStateChangeEvent")
    public void publishOrchestrationNodeStateChangeEvent() {
        Mockito.verify(orchestrationPlanStateService, Mockito.times(1))
                .updateOrchestrationPlanNodeState(any(), eq(OrchestrationPlanNodeState.HELD));
    }

    private void mocking(Product productDTO, List<Product> relatedProductDTOs) {
        when(productManagementService.getProductsByOrderIdAndItemIds(any(), anyString())).thenReturn(List.of(productDTO));
        List<String> relatedProductDtoIds = relatedProductDTOs.stream().map(Product::getId).toList();
        when(productManagementService.getProductsByFields(relatedProductDtoIds, List.of(STATUS_FIELD))).thenReturn(relatedProductDTOs);
        doNothing().when(orchestrationPlanStateService).updateNodeRelatedProducts(any());
        doNothing().when(orchestrationPlanStateService).updateOrchestrationPlanNodeStateWithErrorMessage(any(), any());
    }

    @And("CPIB has product with id = {string} and state = {string}")
    public void cpibHasProductWithIdAndState(String productId, String state) {
        Product relatedProduct = Product.builder()
                .id(productId)
                .productSpecification(ProductSpecificationRef.builder().id("id").build())
                .build();
        relatedProduct = relatedProduct.status(ProductStatusType.fromValue(state));
        when(productManagementService.getProductsByFields(eq(List.of(productId)), any())).thenReturn(List.of(relatedProduct));
        mocking(productDTO, List.of(relatedProduct));
    }

    @When("COOD delivers the node with id={string}")
    public void coodDeliversTheNodeWithId(String nodeId) {
        OrchestrationPlanNode orchestrationPlanNode1 = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).get();
        when(deliveryOrderService.buildOrderItemRef(orchestrationPlanNode1)).thenReturn(OrderItemRef.builder().build());

        try {
            deliverSelectedNodesService.startDeliverSelectedNode(orchestrationPlan, orchestrationPlanNode1);
        } catch (CoodRecoverableAndNonRetryableException coodRecoverableAndNonRetryableException) {
            this.exception = coodRecoverableAndNonRetryableException;
        }

    }

    @And("CPIB has no related products for this node with id={string}")
    public void cpibHasNoRelatedProductsForThisNodeWithId(String nodeId) {
        mocking(productDTO, List.of());
    }

    @And("CPIB has product with id = {string} and state = {string} and product with id = {string} and state = {string}")
    public void cpibHasProductWithIdAndStateAndProductWithIdAndState(String productId1, String productState1, String productId2, String productState2) {
        Product relatedProduct = Product.builder()
                .id(productId1)
                .status(ProductStatusType.fromValue(productState1))
                .productSpecification(ProductSpecificationRef.builder().id("id").build())
                .build();
        Product relatedProduct2 = Product.builder()
                .id(productId2)
                .status(ProductStatusType.fromValue(productState2))
                .productSpecification(ProductSpecificationRef.builder().id("id").build())
                .build();
        when(productManagementService.getProductsByFields(eq(List.of(productId1)), any())).thenReturn(List.of(relatedProduct));
        mocking(productDTO, List.of(relatedProduct, relatedProduct2));
    }

    @And("an unexpected product state exception will be fired")
    public void anUnexpectedProductStateExceptionWillBeFired() {
        this.productUnExpectedStateException = (ProductUnExpectedStateException) this.exception.getCause();
    }
}
