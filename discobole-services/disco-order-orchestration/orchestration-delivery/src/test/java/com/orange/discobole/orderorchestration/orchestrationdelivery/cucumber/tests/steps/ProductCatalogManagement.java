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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.TimeRange;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecCharacteristicValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.AddressCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.kafka.ProductOrderStateChangeEventConsumer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.OrchestrationPlanBuilderServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.Field;
import java.time.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.fixedInstant;

@Slf4j
@RequiredArgsConstructor
public class ProductCatalogManagement {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    List<ProductSpecification> productSpecifications = new ArrayList<>();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductOrderStateChangeEventConsumer productOrderStateChangeEventConsumer;

    ProductOrder productOrder;

    List<ProductOrderItem> productOrderItems = new ArrayList<>();

    OrchestrationPlan orchestrationPlan;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    private KafkaTemplate<String, ProductOrderStateChangeEvent> kafkaTemplate;

    private final OrchestrationPlanBuilderServiceImpl orchestrationPlanBuilderService;

    @And("Product Catalog has no characteristics for spec {string}")
    public void productCatalogHasNoCharacteristicsForSpec(String specId) {
        ProductSpecification productSpecification =
                ProductSpecification.builder()
                        .id(specId)
                        .serviceSpecification(List.of())
                        .relatedResource(List.of())
                        .build();
        productSpecifications.add(productSpecification);
    }

    @And("Product Catalog has the following characteristics for spec {string}")
    public void productCatalogHasTheFollowingCharacteristicsForSpec(String specId, List<ProductCharacteristicRecord> specCharacteristics) {
        String VALIDITY_CHARACTERISTICS = "Validity";
        List<ProductSpecificationCharacteristic> productSpecificationCharacteristic = specCharacteristics.stream()
                .map(spcCharacteristic -> {
                    ProductSpecCharacteristicValue.ProductSpecCharacteristicValueBuilder productSpecCharacteristicValueBuilder = ProductSpecCharacteristicValue.builder()
                            .value(Objects.nonNull(spcCharacteristic.value()) ? spcCharacteristic.value() : "")
                            .unitOfMeasure(spcCharacteristic.unitOfMeasure());

                    if (Objects.equals(spcCharacteristic.name(), VALIDITY_CHARACTERISTICS)) {
                        productSpecCharacteristicValueBuilder
                                .timeRange(new TimeRange(spcCharacteristic.validFrom(), spcCharacteristic.validTo()));
                    }

                    return ProductSpecificationCharacteristic.builder()
                            .name(spcCharacteristic.name())
                            .type(spcCharacteristic.type())
                            .productSpecCharacteristicValue(List.of(productSpecCharacteristicValueBuilder.build()))
                            .build();
                }).toList();

        ProductSpecification productSpecification =
                ProductSpecification.builder()
                        .id(specId)
                        .productSpecificationCharacteristic(productSpecificationCharacteristic)
                        .serviceSpecification(List.of())
                        .relatedResource(List.of())
                        .productSpecificationRelationship(List.of(ProductSpecificationRelationship.builder()
                                .relationshipType(ProductSpecificationRelationshipType.RELIES_ON).build()))
                        .build();

        productSpecifications.add(productSpecification);

    }

    @And("OM has product order {string} with status {string}")
    public void OMHasProductOrderFromTheTopicWithStatus(String productOrderId, String status) throws JsonProcessingException {
        productOrder = Instancio.create(ProductOrder.class);
        productOrder.setRequestedCompletionDate(Instant.now());
        productOrder.setId(productOrderId);
        productOrder.setState(ProductOrderStateType.fromValue(status));

        wireMockServer.stubFor(get(urlPathEqualTo("/productCatalogManagement/v1/productSpecification"))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(productSpecifications)).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("order item {string} with spec id {string} has the following characteristics")
    public void orderItemHasTheFollowingCharacteristics(String orderItemId, String specId, List<OrderItemCharacteristicRecord> orderItemCharacteristicRecordList) {
        List<com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic> characteristics = orderItemCharacteristicRecordList.stream().map(orderItem -> {

                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic stringCharacteristic = com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic.builder()
                            .atType(com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic.class.getSimpleName())
                            .name(orderItem.name())
                            .value(orderItem.value())
                            .build();

                    if (orderItem.type() != null) {
                        return switch (orderItem.type().toLowerCase()) {
                            case "validitycharacteristic" ->
                                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic.builder()
                                            .name(orderItem.name())
                                            .atType(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic.class.getSimpleName())
                                            .value(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityValue.builder()
                                                    .value(orderItem.value() != null ? Integer.valueOf(orderItem.value()) : null)
                                                    .unitOfMeasure(orderItem.unitOfMeasure())
                                                    .validTo(orderItem.validTo() != null ? Instant.parse(orderItem.validTo()) : null)
                                                    .validFrom(orderItem.validFrom() != null ? Instant.parse(orderItem.validFrom()) : null)
                                                    .build())
                                            .build();
                            case "addresscharacteristic" ->
                                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic.builder()
                                            .atType(com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic.class.getSimpleName())
                                            .name(orderItem.name())
                                            .addressId(orderItem.addressId())
                                            .country(orderItem.country())
                                            .city(orderItem.city())
                                            .streetName(orderItem.streetName())
                                            .postcode(orderItem.postCode())
                                            .build();
                            case "datecharacteristic" ->
                                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic.builder()
                                            .atType(com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic.class.getSimpleName())
                                            .name(orderItem.name())
                                            .value(Instant.parse(orderItem.value()))
                                            .build();
                            default -> stringCharacteristic;
                        };
                    } else {
                        return stringCharacteristic;
                    }
                })
                .toList();


        Product product1 = Product.builder()
                .id(orderItemId)
                .productCharacteristic(characteristics)
                .productSpecification(ProductSpecificationRef.builder()
                        .id(specId)
                        .atType(ProductSpecificationRef.class.getSimpleName())
                        .build())
                .build();

        // search already existing items from other steps
        // if exists append to it
        // else create new one
        productOrderItems.stream()
                .filter(productOrderItem -> Objects.equals(productOrderItem.getId(), orderItemId))
                .findFirst()
                .ifPresentOrElse(
                        productOrderItem -> {
                            if (productOrderItem.getProduct() instanceof Product product) {
                                product.setProductCharacteristic(characteristics);
                            } else {
                                productOrderItem.setProduct(product1);
                            }
                        }
                        // else
                        , () -> {
                            ProductOrderItem productOrderItem = ProductOrderItem.builder()
                                    .atType(ProductOrderItem.class.getSimpleName())
                                    .id(orderItemId)
                                    .product(product1)
                                    .action(ItemActionType.ADD)
                                    .build();

                            productOrderItems.add(productOrderItem);
                        }
                );
    }


    @And("product order {string} has the following product order items")
    public void productOrderHasTheFollowingProductOrderItems(String productOrderId, List<ProductOrderItemRecord> orderItemsList) {

        productOrderItems = orderItemsList.stream()
                .map(orderItem -> ProductOrderItem.builder()
                        .atType("ProductOrderItem")
                        .id(orderItem.orderItemId())
                        .product(Product.builder()
                                .atType("Product")
                                .productCharacteristic(List.of(Instancio.create(com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic.class)))
                                .productSpecification(ProductSpecificationRef.builder()
                                        .atType("ProductSpecificationRef")
                                        .id(orderItem.specificationId())
                                        .atBaseType(orderItem.atBaseType())
                                        .build())
                                .build())
                        .action(ItemActionType.fromValue(orderItem.action()))
                        .productOrderItemRelationship(
                                List.of(OrderItemRelationship.builder()
                                        .atType("OrderItemRelationship")
                                        .id(orderItem.relatedOrderItemId() != null ? orderItem.relatedOrderItemId() : null)
                                        .relationshipType(orderItem.relationType() != null ? RelationshipType.fromValue(orderItem.relationType()) : null)
                                        .build()))
                        .build())
                .collect(Collectors.toList());

        if (Objects.isNull(productOrder)) {
            productOrder = Instancio.create(ProductOrder.class);
            productOrder.setRequestedCompletionDate(Instant.now());
            productOrder.setId(productOrderId);
            productOrder.setState(ProductOrderStateType.ACCEPTED);
        }

        productOrder.setProductOrderItem(productOrderItems);
    }


    @And("the system consumes product order {string} from the topic {string}")
    public void theSystemConsumesProductOrderFromTheTopic(String productOrderId, String topicName) {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            Instant currentTime = Objects.nonNull(fixedInstant) ? fixedInstant : Instant.now();
            mockedInstant.when(Instant::now).thenReturn(currentTime);
            productOrder.setProductOrderItem(productOrderItems);

            ProductOrderPayloadEvent productOrderStateChangePayloadEvent = ProductOrderPayloadEvent.builder()
                    .productOrder(productOrder).build();

            ProductOrderStateChangeEvent event = ProductOrderStateChangeEvent.builder()
                    .event(productOrderStateChangePayloadEvent)
                    .eventType(ProductOrderStateChangeEvent.class.getSimpleName())
                    .eventId(UUID.randomUUID().toString())
                    .eventTime(Instant.now())
                    .build();

                Message<ProductOrderStateChangeEvent> message =
                        MessageBuilder.withPayload(event).build();

                productOrderStateChangeEventConsumer.listen(message);
        }
    }


    @And("the plan with product order {string} will have the following characteristics for order item {string}")
    public void thePlanWithProductOrderHasTheFollowingDataForOrderItem(String productOrderId, String orderItemId, List<ProductCharacteristicRecord> productCharacteristics) {

        Set<Characteristic> productCharacteristicSet = productCharacteristics.stream().map(productCharacteristic -> switch (Objects.nonNull(productCharacteristic.type()) ? productCharacteristic.type() : StringCharacteristic.class.getSimpleName()) {
                    case "StringCharacteristic" -> StringCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .value(productCharacteristic.value())
                            .atType(StringCharacteristic.class.getSimpleName())
                            .build();
                    case "ValidityCharacteristic" -> ValidityCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(ValidityCharacteristic.class.getSimpleName())
                            .value(ValidityValue.builder()
                                    .unitOfMeasure(productCharacteristic.unitOfMeasure())
                                    .value(Objects.nonNull(productCharacteristic.value()) ? Integer.valueOf(productCharacteristic.value()) : null)
                                    .validFrom(Objects.nonNull(productCharacteristic.validFrom()) ? OffsetDateTime.parse(productCharacteristic.validFrom()) : null)
                                    .validTo(Objects.nonNull(productCharacteristic.validTo()) ? OffsetDateTime.parse(productCharacteristic.validTo()) : null)
                                    .build())
                            .build();
                    case "AddressCharacteristic" -> AddressCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(AddressCharacteristic.class.getSimpleName())
                            .addressId(productCharacteristic.addressId())
                            .country(productCharacteristic.country())
                            .city(productCharacteristic.city())
                            .streetName(productCharacteristic.streetName())
                            .postcode(productCharacteristic.postCode())
                            .build();
                    case "ObjectCharacteristic" -> ObjectCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(ObjectCharacteristic.class.getSimpleName())
                            .value(Map.of(
                                    "value", productCharacteristic.value(),
                                    "unitOfMeasure", productCharacteristic.unitOfMeasure()
                            )).build();
                    case "DateCharacteristic" -> DateCharacteristic.builder()
                            .name(productCharacteristic.name())
                            .atType(DateCharacteristic.class.getSimpleName())
                            .value(Objects.nonNull(productCharacteristic.value()) ? OffsetDateTime.parse(productCharacteristic.value()) : null)
                            .build();
                    default -> throw new IllegalStateException("Unexpected value: " + productCharacteristic.valueType());
                }
        ).collect(Collectors.toSet());

        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(productOrderId));
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan savedOrchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(savedOrchestrationPlan).isNotNull();
            Set<OrchestrationPlanNode> orchestrationPlanNodeSet = savedOrchestrationPlan.getOrchestrationPlanNodes();
            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanNodeSet.stream()
                    .filter(orchestrationPlanNode1 -> orchestrationPlanNode1.getRelatedProductOrderItem().get(0).getId().equals(orderItemId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No OrchestrationPlanNode found with the given orderId"));

            Set<Characteristic> nodeProductCharacteristics = orchestrationPlanNode.getRelatedProduct().stream().filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.DELIVERS) || relatedProduct.getRelationshipType().equals(RelatedProductRelationType.DELIVER_WITH)).findFirst().get().getProductCharacteristic();

            assertThat(nodeProductCharacteristics).hasSameSizeAs(productCharacteristicSet);
            assertThat(nodeProductCharacteristics)
                    .isEqualTo(productCharacteristicSet);
        });
    }


    @Then("The system will create orchestration plan for order id {string} with {string} state")
    public void theSystemWillCreateOrchestrationPlanForOrderIdWithState(String orderId, String state) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            assertThat(orchestrationPlan.getState()).isEqualTo(State.fromValue(state));
        });
    }

    @Given("There is no orchestration plan created")
    public void thereIsNoOrchestrationPlanCreated() {
        Query query = new Query();
        orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
        assertThat(orchestrationPlan).isNull();

    }

    @And("order item {string} with spec id {string} has no characteristics")
    public void orderItemWithSpecIdHasNoCharacteristics(String orderItemId, String specId) {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .atType(ProductOrderItem.class.getSimpleName())
                .id(orderItemId)
                .product(Product.builder()
                        .productCharacteristic(List.of())
                        .productSpecification(ProductSpecificationRef.builder()
                                .id(specId)
                                .atType(ProductSpecificationRef.class.getSimpleName())
                                .build())
                        .build())
                .action(ItemActionType.ADD)
                .build();


        productOrderItems.add(productOrderItem);
    }

    @And("OM has product order {string} with status {string} and requested delivery date is {string}")
    public void OMHasProductOrderWithStatusAndRequestedDeliveryDateIs(String productOrderId, String status, String requestedDeliveryDate) throws JsonProcessingException {
        productOrder = Instancio.create(ProductOrder.class);
        productOrder.setRequestedCompletionDate(Instant.parse(requestedDeliveryDate));
        productOrder.setId(productOrderId);
        productOrder.setState(ProductOrderStateType.fromValue(status));

        wireMockServer.stubFor(get(urlPathEqualTo("/productCatalogManagement/v1/productSpecification"))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(productSpecifications)).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @Given("the current system timestamp is {string}")
    public void theCurrentSystemTimestampIs(String currentSystemTime) {
        fixedInstant = Instant.parse(currentSystemTime);
    }

    @When("The topic {string} receives an event with product order id {string} and state {string} and the following product order items:")
    public void theTopicReceivesAnEventWithProductOrderIdAndStateAndProductOrderItemsWithTheFollowingData(String topicName, String productOrderId, String state, List<ProductOrderItemRecord> productOrderItemsList) {
        publishProductOrderStateChangeEvent(topicName, productOrderId, state, null, productOrderItemsList);
    }

    @When("The topic {string} receives an event with product order id {string} and state {string} and requested delivery date is {string} and the following product order items:")
    public void theTopicReceivesAnEventWithProductOrderIdAndStateAndAndRequestedDeliveryDateProductOrderItemsWithTheFollowingData(String topicName, String productOrderId, String state, String requestedDeliveryDate, List<ProductOrderItemRecord> productOrderItemsList) {
        publishProductOrderStateChangeEvent(topicName, productOrderId, state, requestedDeliveryDate, productOrderItemsList);
    }

    private void publishProductOrderStateChangeEvent(String topicName, String productOrderId, String state, String requestedDeliveryDate, List<ProductOrderItemRecord> productOrderItemsList) {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            Instant currentTime = Objects.nonNull(fixedInstant) ? fixedInstant : Instant.now();
            mockedInstant.when(Instant::now).thenReturn(currentTime);

            ProductOrder productOrder = Instancio.create(ProductOrder.class);
            productOrder.setRequestedCompletionDate(requestedDeliveryDate != null ? Instant.parse(requestedDeliveryDate) : null);
            productOrder.setId(productOrderId);
            productOrder.setState(ProductOrderStateType.fromValue(state));

            List<PartyRefOrPartyRoleRef> partyRefList = new ArrayList<>();

            PartyRef partyRef = PartyRef.builder()
                    .id("123")
                    .name("Sample Party")
                    .href("href")
                    .atType("type")
                    .build();

            partyRefList.add(partyRef);

            RelatedPartyRefOrPartyRoleRef relatedPartyRefOrPartyRoleRef = RelatedPartyRefOrPartyRoleRef.builder()
                    .partyOrPartyRole(partyRefList.get(0))
                    .atType("type")
                    .build();

            productOrder.setRelatedParty(List.of(relatedPartyRefOrPartyRoleRef));

            List<ProductOrderItem> orderItems = new ArrayList<>(productOrderItemsList.stream().map(orderItem ->
                            ProductOrderItem.builder()
                                    .atType("ProductOrderItem")
                                    .id(orderItem.orderItemId())
                                    .product(Product.builder()
                                            .atType("Product")
                                            .productCharacteristic(List.of(Instancio.create(com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic.class)))
                                            .productSpecification(Objects.isNull(orderItem.specificationId()) ? null : ProductSpecificationRef.builder()
                                                    .atType("ProductSpecificationRef")
                                                    .id(orderItem.specificationId())
                                                    .build())
                                            .build())
                                    .action(Objects.nonNull(orderItem.action()) ? ItemActionType.fromValue(orderItem.action()) : null)
                                    .productOffering(ProductOfferingRef.builder()
                                            .id(UUID.randomUUID().toString())
                                            .atType(Objects.requireNonNullElse(orderItem.productOfferingType(), "AtomicProductOffering"))
                                            .name(orderItem.productOfferingName())
                                            .build())
                                    .productOrderItemRelationship(
                                            List.of(OrderItemRelationship.builder()
                                                    .atType("OrderItemRelationship")
                                                    .id(Objects.nonNull(orderItem.relatedOrderItemId()) ? orderItem.relatedOrderItemId() : null)
                                                    .relationshipType(Objects.nonNull(orderItem.relationType()) ? RelationshipType.fromValue(orderItem.relationType()) : null)
                                                    .build()))
                                    .build()
                    ).collect(Collectors.toMap(
                            ProductOrderItem::getId,
                            item -> item,
                            (existing, duplicate) -> {

                                // Merge productOrderItemRelationship lists
                                List<OrderItemRelationship> mergedRelationships = new ArrayList<>();
                                if (existing.getProductOrderItemRelationship() != null) {
                                    mergedRelationships.addAll(existing.getProductOrderItemRelationship());
                                }
                                if (duplicate.getProductOrderItemRelationship() != null) {
                                    mergedRelationships.addAll(duplicate.getProductOrderItemRelationship());
                                }

                                existing.setProductOrderItemRelationship(mergedRelationships);

                                return existing;
                            }
                    ))
                    .values());

            orderItems.forEach(orderItem -> {
                if (Objects.isNull(orderItem.getAction())) {
                    throw new IllegalArgumentException("action is {null} for order item: " + orderItem.getId());
                }
            });

            productOrder.setProductOrderItem(orderItems);

            ProductOrderStateChangeEvent event = ProductOrderStateChangeEvent.builder()
                    .eventType(ProductOrderStateChangeEvent.class.getSimpleName())
                    .eventId(UUID.randomUUID().toString())
                    .eventTime(Instant.now())
                    .event(ProductOrderPayloadEvent.builder().productOrder(productOrder).build()).build();

            if (Objects.nonNull(fixedInstant)) {
                Message<ProductOrderStateChangeEvent> message =
                        MessageBuilder.withPayload(event).build();

                productOrderStateChangeEventConsumer.listen(message);
            } else {
                kafkaTemplate.send(topicName, event);
            }
        }
    }


    @And("the plan with product order {string} has order start date {string}")
    public void thePlanWithProductOrderHasOrderStartDate(String productOrderId, String orderStartDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(productOrderId));
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan savedOrchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(savedOrchestrationPlan).isNotNull();
            assertThat(savedOrchestrationPlan.getOrchestrationPlanSchedule().getOrderStartDate()).isEqualTo(orderStartDate);
        });
    }

    @And("the plan with product order {string} will have product order item {string} with order item start date {string}")
    public void thePlanWithProductOrderWillHaveProductOrderItemWithOrderItemStartDate(String productOrderId, String orderItemId, String orderItemStartDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(productOrderId));
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan savedOrchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(savedOrchestrationPlan).isNotNull();
            Set<OrchestrationPlanNode> orchestrationPlanNodeSet = savedOrchestrationPlan.getOrchestrationPlanNodes();
            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanNodeSet.stream()
                    .filter(orchestrationPlanNode1 -> orchestrationPlanNode1.getRelatedProductOrderItem().get(0).getId().equals(orderItemId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No OrchestrationPlanNode found with the given orderId"));

            assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getOrderItemStartDate()).isEqualTo(orderItemStartDate);
        });
    }

    @And("the plan with product order {string} will have the following data")
    public void thePlanWithProductOrderWillHaveTheFollowingData(String orderId, List<OrchestrationPlanRecord> orchestrationPlanRecordList) {
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                    .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                    .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

            OrchestrationPlanRecord record = orchestrationPlanRecordList.get(0);

            if (Objects.nonNull(record.orderStartDate())) {
                assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getOrderStartDate()).isEqualTo(record.orderStartDate());
            }
            if (Objects.nonNull(record.actualOrderStartDate())) {
                assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getActualOrderStartDate()).isEqualTo(record.actualOrderStartDate());
            }
            if (Objects.nonNull(record.actualOrderCompletionDate())) {
                assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getActualOrderCompletionDate()).isEqualTo(record.actualOrderCompletionDate());
            }
            if (Objects.nonNull(record.expectedOrderCompletionDate())) {
                assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getExpectedOrderCompletionDate()).isEqualTo(record.expectedOrderCompletionDate());
            }
            if (Objects.nonNull(record.estimatedOrderDeliveryLeadTime())) {
                assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getEstimatedOrderDeliveryLeadTime()).isEqualTo(Long.parseLong(record.estimatedOrderDeliveryLeadTime()));
            }
        });
    }

    @And("orchestration plan node with id {string} in plan {string} will have the following data")
    public void orchestrationPlanNodeWithIdInPlanWillHaveTheFollowingData(String nodeId, String planId, List<OrchestrationPlanNodeRecord> orchestrationPlanNodeRecordList) {
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                    .findOrchestrationPlanById(planId)
                    .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with id " + planId));

            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId)
                    .orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));

            OrchestrationPlanNodeRecord record = orchestrationPlanNodeRecordList.get(0);

            if (Objects.nonNull(record.orderItemStartDate())) {
                assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getOrderItemStartDate()).isEqualTo(record.orderItemStartDate());
            }
            if (Objects.nonNull(record.actualOrderItemStartDate())) {
                assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getActualOrderItemStartDate()).isEqualTo(record.actualOrderItemStartDate());
            }
            if (Objects.nonNull(record.actualOrderItemCompletionDate())) {
                assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getActualOrderItemCompletionDate()).isEqualTo(record.actualOrderItemCompletionDate());
            }
            if (Objects.nonNull(record.expectedOrderItemCompletionDate())) {
                assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getExpectedOrderItemCompletionDate()).isEqualTo(record.expectedOrderItemCompletionDate());
            }
            if (Objects.nonNull(record.estimatedOrderItemDeliveryLeadTime())) {
                assertThat(orchestrationPlanNode.getOrchestrationNodeSchedule().getEstimatedOrderItemDeliveryLeadTime()).isEqualTo(Long.parseLong(record.estimatedOrderItemDeliveryLeadTime()));
            }
        });
    }

    @And("the plan with product order {string} has product order item {string} with order item start date {string}")
    public void thePlanWithProductOrderHasProductOrderItemWithOrderItemStartDate(String orderId, String orderItemId, String orderItemStartDate) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        orchestrationPlan.getOrchestrationPlanNodeByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("No orchestration plan with order item id " + orderItemId))
                .setOrchestrationNodeSchedule(OrchestrationNodeSchedule.builder()
                        .orderItemStartDate(Instant.parse(orderItemStartDate))
                        .build());

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the plan with product order {string} has the following product order item {string}")
    public void thePlanWithProductOrderHasProductOrderItemWithTheFollowingData(String orderId, String orderItemId, List<OrchestrationPlanNodeRecord> orchestrationPlanNodeRecordList) {
        String orderItemStartDate = orchestrationPlanNodeRecordList.get(0).orderItemStartDate();
        String actualOrderItemStartDate = orchestrationPlanNodeRecordList.get(0).actualOrderItemStartDate();
        String actualOrderItemCompletionDate = orchestrationPlanNodeRecordList.get(0).actualOrderItemCompletionDate();

        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("No orchestration plan with order item id " + orderItemId));

        if (orderItemStartDate != null) {
            orchestrationPlanNode.getOrchestrationNodeSchedule().setOrderItemStartDate(Instant.parse(orderItemStartDate));
        }

        if (actualOrderItemStartDate != null) {
            orchestrationPlanNode.getOrchestrationNodeSchedule().setActualOrderItemStartDate(Instant.parse(actualOrderItemStartDate));
        }

        if (actualOrderItemCompletionDate != null) {
            orchestrationPlanNode.getOrchestrationNodeSchedule().setActualOrderItemCompletionDate(Instant.parse(actualOrderItemCompletionDate));
        }

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("Adaptive Orchestration Plan Scheduling is {string}")
    public void adaptiveOrchestrationPlanSchedulingIs(String orchestrationPlanScheduling) throws IllegalAccessException, NoSuchFieldException {
        Class<?> orchestrationPlanBuilderServiceClass = orchestrationPlanBuilderService.getClass();
        Field field = orchestrationPlanBuilderServiceClass.getDeclaredField("adaptiveOrchestrationPlanScheduling");
        field.setAccessible(true);
        field.set(orchestrationPlanBuilderService, !Objects.equals(orchestrationPlanScheduling, "OFF"));
    }

    @And("the plan with product order {string} has the following order dates:")
    public void thePlanWithProductOrderHasTheFollowingData(String orderId, List<OrchestrationPlanRecord> orchestrationPlanRecordList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        OrchestrationPlanRecord record = orchestrationPlanRecordList.get(0);

        OrchestrationPlanSchedule.OrchestrationPlanScheduleBuilder orchestrationPlanScheduleBuilder = OrchestrationPlanSchedule.builder();
        if (Objects.nonNull(record.orderStartDate())) {
            orchestrationPlanScheduleBuilder.orderStartDate(Instant.parse(record.orderStartDate()));
        }
        if (Objects.nonNull(record.actualOrderStartDate())) {
            orchestrationPlanScheduleBuilder.actualOrderStartDate(Instant.parse(record.actualOrderStartDate()));
        }

        orchestrationPlan.setOrchestrationPlanSchedule(orchestrationPlanScheduleBuilder.build());

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the plan with product order {string} has no order start date")
    public void thePlanWithProductOrderHasNoOrderStartDate(String orderId) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));


        OrchestrationPlanSchedule.OrchestrationPlanScheduleBuilder orchestrationPlanScheduleBuilder = OrchestrationPlanSchedule.builder();
        orchestrationPlanScheduleBuilder.orderStartDate(null);

        orchestrationPlan.setOrchestrationPlanSchedule(orchestrationPlanScheduleBuilder.build());

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the tangible product {string} is not linked to any shipping order item")
    public void theTangibleProductIsNotLinkedToAnyShippingOrderItem(String productId) {
        // the tangible product is not linked to any shipping order item
    }

    @When("The topic {string} receives event {string} with product order id {string} and state {string}")
    public void theTopicReceivesEventWithProductOrderIdAndState(String topicName, String eventName, String productOrderId, String state) {
        productOrder = Instancio.create(ProductOrder.class);
        productOrder.setRequestedCompletionDate(Instant.now());
        productOrder.setId(productOrderId);
        productOrder.setState(ProductOrderStateType.fromValue(state));
    }

    @And("the plan with product order {string} has the following characteristics for order item {string} with spec id {string}")
    public void thePlanWithProductOrderHasTheFollowingCharacteristicsForOrderItemWithSpecId(String productOrderId, String orderItemId, String specId, List<OrderItemCharacteristicRecord> orderItemCharacteristics) {

        Set<Characteristic> productCharacteristicSet = orderItemCharacteristics.stream()
                .map(orderItem -> StringCharacteristic.builder()
                        .name(orderItem.name())
                        .value(orderItem.value())
                        .atType(StringCharacteristic.class.getSimpleName())
                        .build())
                .collect(Collectors.toSet());

        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(productOrderId));

        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan savedOrchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(savedOrchestrationPlan).isNotNull();

            Set<OrchestrationPlanNode> orchestrationPlanNodeSet = savedOrchestrationPlan.getOrchestrationPlanNodes();
            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanNodeSet.stream()
                    .filter(node -> node.getRelatedProductOrderItem().get(0).getId().equals(orderItemId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No OrchestrationPlanNode found with the given orderId"));

            RelatedProduct nodeRelatedProduct = orchestrationPlanNode.getRelatedProduct().stream()
                    .filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.DELIVERS))
                    .findFirst()
                    .get();

            Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> nodeProductCharacteristics = nodeRelatedProduct.getProductCharacteristic();
            String nodeSpecId = nodeRelatedProduct.getProductSpecification().getId();

            assertThat(nodeSpecId).isEqualTo(specId);
            assertThat(nodeProductCharacteristics).hasSameSizeAs(productCharacteristicSet);
            assertThat(nodeProductCharacteristics)
                    .isEqualTo(productCharacteristicSet);
        });
    }

    @And("product order item with id {string} has no prerequisites")
    public void productOrderItemWithIdHasNoPrerequisites(String orderItem) {
        // product order item with this id has no prerequisites
    }

    @And("product order item with id {string} has no relationType {string} with product order item with id {string}")
    public void productOrderItemWithIdHasNoRelationTypeWithProductOrderItemWithId(String orderItem, String relationType, String relatedOrderItem) {
        // product order item with this id has no relationType with product order item relatedOrderItem
    }
}


