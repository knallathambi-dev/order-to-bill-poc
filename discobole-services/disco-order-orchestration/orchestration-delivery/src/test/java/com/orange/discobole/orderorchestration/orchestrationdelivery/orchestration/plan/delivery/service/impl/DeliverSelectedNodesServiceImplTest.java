// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.builder.OrchestrationPlanBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.builder.OrchestrationPlanNodeBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliveryOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.MaintainDeliveryNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.VerifyNodeService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.builder.RelatedProductBuilder.getRelatedProductBuilder;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemRelationshipType.RELIES_ON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class DeliverSelectedNodesServiceImplTest {
    private static final String CREATE_SERVICE_ORDER_URL = "http://localhost:8082/serviceOrdering/v4/serviceOrder";
    @Mock
    DiscoServiceUrl discoServiceUrl;
    @Mock
    RestTemplate restTemplate;
    @Spy
    @InjectMocks
    DeliverSelectedNodesServiceImpl deliverSelectedNodesServiceImpl;
    @Mock
    MaintainDeliveryNodeRelatedProduct maintainDeliveryNodeRelatedProduct;
    @Mock
    VerifyNodeService verifyNodeService;
    @Mock
    OrchestrationPlanService orchestrationPlanService;
    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Mock
    OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Mock
    DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    @Mock
    private ProductManagementService productManagementService;

    @Mock
    UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    @Mock
    DeliveryOrderService deliveryOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    public List<ServiceSpecification> createServiceSpecificationResult() {
        List<ServiceSpecification> result = new ArrayList<>();
        ServiceSpecification service1 = new ServiceSpecification();
        service1.setId("112");
        result.add(service1);

        ServiceSpecification service2 = new ServiceSpecification();
        service2.setId("113");
        result.add(service2);

        ServiceSpecification service3 = new ServiceSpecification();
        service3.setId("114");
        result.add(service3);

        return result;
    }

    public ServiceOrder createServiceOrderRequest() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification serviceSpecification = com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        Characteristic serviceCharacteristic = StringCharacteristic.builder()
                .id("5243")
                .name("volume")
                .valueType("string")
                .value("1h")
                .build();
        List<Characteristic> serviceCharacteristicList = new ArrayList<>();
        serviceCharacteristicList.add(serviceCharacteristic);
        Service service = Service.builder()
                .serviceType("CFS")
                .serviceCharacteristic(serviceCharacteristicList)
                .serviceSpecification(serviceSpecification)
                .build();
        ServiceOrderItem serviceOrderItem = ServiceOrderItem.builder()
                .id("1")
                .action(ServiceOrderItem.Action.ADD)
                .serviceRelationship(List.of(ServiceRelationship.builder().serviceRelationshipType("reliesOn")
                        .service(ServiceRef.builder().id("realizingServiceId").href("href").build()).build()))
                .service(service)
                .build();
        List<ServiceOrderItem> serviceOrderItemList = new ArrayList<>();
        serviceOrderItemList.add(serviceOrderItem);
        ServiceOrderRelatedParty relatedParty = ServiceOrderRelatedParty.builder()
                .id("231-mf4")
                .name("Abir")
                .role("customer")
                .referredType("individual")
                .build();
        List<ServiceOrderRelatedParty> relatedPartyList = new ArrayList<>();
        relatedPartyList.add(relatedParty);

        return ServiceOrder.builder()
                .description("Service order Time Bundle")
                .category("string")
                .requestedCompletionDate(Instant.ofEpochSecond(1626436900L))
                .type("ServiceOrder")
                .relatedParty(relatedPartyList)
                .serviceOrderItem(serviceOrderItemList)
                .build();

    }

    public ServiceOrder createServiceOrderResponse() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification serviceSpecification = com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        Characteristic serviceCharacteristic = StringCharacteristic.builder()
                .id("5243")
                .name("volume")
                .valueType("string")
                .value("1h")
                .build();
        List<Characteristic> serviceCharacteristicList = new ArrayList<>();
        serviceCharacteristicList.add(serviceCharacteristic);
        Service service = Service.builder()
                .serviceType("CFS")
                .serviceCharacteristic(serviceCharacteristicList)
                .serviceSpecification(serviceSpecification)
                .build();
        ServiceOrderItem serviceOrderItem = ServiceOrderItem.builder()
                .id("1")
                .action(ServiceOrderItem.Action.ADD)
                .service(service)
                .build();
        List<ServiceOrderItem> serviceOrderItemList = new ArrayList<>();
        serviceOrderItemList.add(serviceOrderItem);
        ServiceOrderRelatedParty relatedParty = ServiceOrderRelatedParty.builder()
                .id("231-mf4")
                .name("Abir")
                .role("customer")
                .referredType("individual")
                .build();
        List<ServiceOrderRelatedParty> relatedPartyList = new ArrayList<>();
        relatedPartyList.add(relatedParty);

        return ServiceOrder.builder()
                .id("123")
                .state(ServiceOrder.State.ACKNOWLEDGED)
                .description("Service order Time Bundle")
                .category("string")
                .requestedCompletionDate(Instant.ofEpochSecond(1626436900L))
                .type("ServiceOrder")
                .relatedParty(relatedPartyList)
                .serviceOrderItem(serviceOrderItemList)
                .build();

    }

    @Test
    void testDeliverSelectedNodes() {
        // Mocking orchestrationPlan, nodes
        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .build();

        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(n -> n.getRelatedProduct().size() > 1).findFirst().get();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder()
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .productOrderId(orchestrationPlan.getRelatedProductOrder().getId())
                        .orderItemId(node.getActualOrderItemId())
                        .orderItemAction("add")
                        .build()))
                .build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);
        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(node)).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, node);

        // Verify that the relevant methods are called
        verify(productManagementService, times(1)).getProductsByOrderIdAndItemIds(any(), anyString());
        // Verify any other relevant assertions or method calls based on your implementation
    }

    @Test
    void testDeliverSelectedNodesWithPrerequisitesActive() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder().build();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(n -> n.getRelatedProduct().size() > 1).findFirst().get();
        // Mocking resultCPIB
        List<com.orange.discobole.productinventory.dto.v1.ServiceRef> realizingServices = new ArrayList<>();
        realizingServices.add(com.orange.discobole.productinventory.dto.v1.ServiceRef.builder().href("href").id("realizingServiceId").build());
        Product resultCPIB = Product.builder().id("id")
                .realizingService(realizingServices)
                .productSpecification(com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder().id("id").build())
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .orderItemId(orchestrationPlanNode.getActualOrderItemId())
                        .productOrderId(orchestrationPlan.getRelatedProductOrder().getId())
                        .orderItemAction("add")
                        .build()
                ))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .product(ProductRef.builder().id("product1").build())
                        .relationshipType(RELIES_ON.getValue())
                        .build())).build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);

        mockGetProductByFields(ProductStatusType.ACTIVE);

        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(any())).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, orchestrationPlanNode);

        verify(deliveryOrderService).publishStartDeliveryEvent(
                any(),
                anyList(),
                eq(DeliveryFactoryRef.builder().deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT).build()));
    }

    @Test
    void testDeliverSelectedNodeWithActionAddWithValidProductState() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode node = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                        .productOrderItemId("id1")
                        .build())))
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .build();

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .orchestrationPlanNodes(Set.of(node)).build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder()
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .productOrderId(orchestrationPlan.getRelatedProductOrder().getId())
                        .orderItemId("id1")
                        .orderItemAction("add")
                        .build()))
                .build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);

        mockGetProductByFields(ProductStatusType.ACTIVE);

        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(node)).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, node);

        verify(deliveryOrderService).publishStartDeliveryEvent(
                any(),
                anyList(),
                eq(DeliveryFactoryRef.builder().deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT).build()));
    }

    @Test
    void testDeliverSelectedNodeWithActionDeleteWithValidProductState() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode node = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                        .productOrderItemId("id1")
                        .build())))
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "delete", 1)))
                .build();

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .orchestrationPlanNodes(Set.of(node)).build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder()
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .productOrderId(orchestrationPlan.getRelatedProductOrder().getId())
                        .orderItemId(node.getActualOrderItemId())
                        .orderItemAction(node.getActualRelatedOrderItem().getAction())
                        .build()))
                .build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);

        mockGetProductByFields(ProductStatusType.ACTIVE);

        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(node)).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, node);

        verify(deliveryOrderService).publishStartDeliveryEvent(
                any(),
                anyList(),
                eq(DeliveryFactoryRef.builder().deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT).build()));
    }

    @Test
    void testDeliverSelectedNodeWithActionModifyWithValidProductState() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode node = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "modify", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                        .productOrderItemId("id1")
                        .build())))
                .build();

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .orchestrationPlanNodes(Set.of(node)).build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder().orderItemId(node.getRelatedProductOrderItem().get(0).getId())
                        .orderItemAction(node.getRelatedProductOrderItem().get(0).getAction())
                        .productOrderId(node.getRelatedProductOrder().getId()).build()))
                .build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);

        mockGetProductByFields(ProductStatusType.ACTIVE);

        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(node)).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, node);

        verify(deliveryOrderService).publishStartDeliveryEvent(
                any(),
                anyList(),
                eq(DeliveryFactoryRef.builder().deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT).build()));
    }

    @Test
    void testDeliverSelectedNodeWithValidProductSpecificationPrerequisites() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode node = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                        .productOrderItemId("id1")
                        .build())))
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .build();

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilder.getOrchestrationPlanBuilder()
                .orchestrationPlanNodes(Set.of(node)).build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder().orderItemId(orchestrationPlan.getOrchestrationPlanNodes().stream().toList().get(0).getRelatedProductOrderItem().get(0).getId())
                        .orderItemAction(node.getRelatedProductOrderItem().get(0).getAction())
                        .productOrderId(node.getRelatedProductOrder().getId()).build()))
                .build();
        mockGetProductAndUpdateNodeWithResultProduct(resultCPIB);

        mockGetProductByFields(ProductStatusType.ACTIVE);

        mockServiceOrder();

        when(deliveryOrderService.buildOrderItemRef(node)).thenReturn(OrderItemRef.builder().build());

        // Call the method being tested
        deliverSelectedNodesServiceImpl.startDeliverSelectedNode(orchestrationPlan, node);

        ArgumentCaptor<OrchestrationPlanNode> captor = ArgumentCaptor.forClass(OrchestrationPlanNode.class);
        verify(verifyNodeService, times(1)).verifyOrchestrationPlanNodeDelivery(captor.capture(), any());

        OrchestrationPlanNode orchestrationPlanNode = captor.getValue();
        assertEquals(OrchestrationPlanNodeState.IN_DELIVERY, orchestrationPlanNode.getState());
        assertNull(orchestrationPlanNode.getErrorMessage());
    }

    private void mockGetProductByFields(ProductStatusType active) {
        Product prerequisiteProduct = Product.builder().id("productId1")
                .status(active).build();
        when(productManagementService.getProductsByFields(any(), any())).thenReturn(List.of(prerequisiteProduct));
    }

    private void mockGetProductAndUpdateNodeWithResultProduct(Product resultCPIB) {
        when(productManagementService.getProductsByOrderIdAndItemIds(any(), anyString())).thenReturn(List.of(resultCPIB));
    }

    private void mockServiceOrder() {
        ServiceOrder serviceOrderRequest = createServiceOrderRequest();
        ResponseEntity<ServiceOrder> createdOrder = new ResponseEntity<>(serviceOrderRequest, HttpStatus.CREATED);
        when(discoServiceUrl.getServiceOrderingUrl()).thenReturn(CREATE_SERVICE_ORDER_URL);
        when(restTemplate.exchange(
                eq(CREATE_SERVICE_ORDER_URL),
                eq(HttpMethod.POST),
                any(),
                ArgumentMatchers.<ParameterizedTypeReference<ServiceOrder>>any()))
                .thenReturn(createdOrder);
    }

}
