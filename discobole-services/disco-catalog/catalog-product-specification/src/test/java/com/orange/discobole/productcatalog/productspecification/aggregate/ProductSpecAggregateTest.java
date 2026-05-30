// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.aggregate;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.command.productspec.*;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicValueSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.IgnoreField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProductSpecAggregateTest extends ProductSpecificationApplicationTests {

    private final String serviceSpecId = "701";
    private final String productSpecId = "product_spec_id";
    private final String brand = "Cisco";
    private final String stockItemTypeId = "SIT1";
    private final String stockItemId = "StockItem1";

    private QueryService queryService;
    private Map<String, ProductSpecification> productSpecificationMap;
    private FixtureConfiguration<ProductSpecAggregate> fixture;
    @InjectMocks
    private AccessTokenInterceptor accessToken;
    @BeforeEach
    void setup() {
        productSpecificationMap = new HashMap<>();
        queryService = Mockito.mock(QueryService.class);
        fixture = new AggregateTestFixture<>(ProductSpecAggregate.class);
        fixture.registerFieldFilter(new IgnoreField(ProductSpecInitiatedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecIdentityDataEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecOpDefinedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecCharacteristicsDefinedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecRelationDefinedEvent.class, "productSpecRelationships"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecRelationDefinedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecValidatedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecVersionCreatedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(LinkProductSpecificationToStockItemEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ComputeProductConfigurationEvent.class, "productConfiguration"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecModificationInitiatedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecDefineIdentityModifiedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecModificationValidatedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecRelationModifiedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecCharacteristicsModifiedEvent.class, "lastUpdate"));
        fixture.registerFieldFilter(new IgnoreField(ProductSpecModificationValidatedEvent.class, "storedProductSpecification"));

    }

    @Test
    void raiseInvalidProductSpecStatusEventWhenProcessProductSpecificationInitiationWithIncorrectLifecycleStatus() {

        InitiateProductSpecCommand command = new InitiateProductSpecCommand("1", serviceSpecId);
        ServiceSpecification service = new ServiceSpecification();
        service.lifecycleStatus("inStudy").id(serviceSpecId);

        when(queryService.getServiceSpecById(command.getServiceSpecId(),accessToken.getToken())).thenReturn(service);

        ServiceSpecificationRef serviceSpecRef = new ServiceSpecificationRef();
        serviceSpecRef.id(serviceSpecId);

        fixture.registerInjectableResource(queryService).given().when(command)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseServiceSpecNotExistEventsWhenProcessProductSpecificationInitiation() {

        InitiateProductSpecCommand command = new InitiateProductSpecCommand(productSpecId, serviceSpecId);
        when(queryService.getServiceSpecById(Mockito.anyString(),eq(accessToken.getToken()))).thenReturn(null);

        fixture.registerInjectableResource(queryService).given().when(command).expectEvents();

    }

    @Test
    void raiseEventsWhenProcessProductSpecificationDescription() {

        List<RelatedResource> relatedResources = new ArrayList<>();
        relatedResources.add(new RelatedResource().id("1").role("admin").referredType("test"));

        OffsetDateTime startingDate = OffsetDateTime.now();
        ServiceSpecification serviceSpec = new ServiceSpecification().id(serviceSpecId).lifecycleStatus("active")
                .description("Mobile Access").name("MobileAccess").validFor(new TimePeriod().startDateTime(startingDate));
        serviceSpec.setRelatedResource(relatedResources);

        IdentityData desc=new IdentityData();
        desc.setBrand(brand);
        desc.setDescription("description_1");
        desc.setName("name_1");
        desc.setProductNumber( "pn123");

        List<RelatedParty> relatedPartyList=new ArrayList<>();
        RelatedParty relatedParty=new RelatedParty();
        relatedParty.setId("1001");
        relatedParty.setReferredType("individual");
        relatedParty.setRole("admin");
        relatedPartyList.add(relatedParty);

        DefineIdentityProductSpecCommand command = new DefineIdentityProductSpecCommand(productSpecId,desc,relatedPartyList,
                relatedResources, new TimePeriod().startDateTime(startingDate.plusYears(1)), EntityType.PRODUCTSPECIFICATION);
        Assertions.assertNotNull(command);
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));

        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(serviceSpec);
    }

    @Test
    void raiseEventsWhenProcessProductSpecificationDescription_stock() {

        List<RelatedResource> relatedResources = new ArrayList<>();
        relatedResources.add(new RelatedResource().id("1").role("admin").referredType("test"));

        OffsetDateTime startingDate = OffsetDateTime.now();
        IdentityData desc=new IdentityData();
        desc.setBrand(brand);
        desc.setDescription("description_1");
        desc.setName("name_1");
        desc.setProductNumber( "pn123");

        List<RelatedParty> relatedPartyList=new ArrayList<>();
        RelatedParty relatedParty=new RelatedParty();
        relatedParty.setId("1001");
        relatedParty.setReferredType("individual");
        relatedParty.setRole("admin");
        relatedPartyList.add(relatedParty);

        DefineIdentityProductSpecCommand command = new DefineIdentityProductSpecCommand(productSpecId, desc,
                relatedPartyList, relatedResources, new TimePeriod().startDateTime(startingDate.plusYears(1)),
                EntityType.PRODUCTSPECIFICATION);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId(stockItemId);
        stockItemType.setValidFor(new TimePeriod().startDateTime(startingDate));


        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemId));

        history.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
                SupportEntity.STOCKITEMTYPE.toString(), null, null, stockItemType,null));

        List<StockItem> stockItemList = new ArrayList<>();
        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemId);
        stockItem.setState("active");
        stockItemList.add(stockItem);

        when(queryService.getStockItemByStockItemTypeId(stockItemId,accessToken.getToken())).thenReturn(stockItemList);
        when(queryService.getStockItemTypeById(stockItemId,accessToken.getToken())).thenReturn(stockItemType);
        Assertions.assertNotNull(command);
    }

    @Test
    void raiseEventsWhenProcessProductSpecificationDescription_stock_invalid_state() {
        IdentityData desc=new IdentityData();
        desc.setBrand(brand);
        desc.setDescription("description_1");
        desc.setName("name_1");
        desc.setProductNumber( "pn123");

        DefineIdentityProductSpecCommand command = new DefineIdentityProductSpecCommand(productSpecId, desc,
                new ArrayList<RelatedParty>(), new ArrayList<RelatedResource>(), new TimePeriod(),
                EntityType.PRODUCTSPECIFICATION);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId(stockItemId);

        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemId));

        history.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
                SupportEntity.STOCKITEMTYPE.toString(), null, null, stockItemType,null));

        List<StockItem> stockItemList = new ArrayList<>();
        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemId);
        stockItem.setState("inStudy");
        stockItemList.add(stockItem);

        when(queryService.getStockItemByStockItemTypeId(stockItemId,accessToken.getToken())).thenReturn(stockItemList);
        List<StockItem> fetchedStockItems = queryService.getStockItemByStockItemTypeId(stockItemId,accessToken.getToken());
        Assertions.assertNotNull(fetchedStockItems);
    }

    @Test
    void raiseInvalidEventWhenProductSpecificationDescripedWithInvalidLifecycleStatus() {

        IdentityData desc=new IdentityData();
        desc.setBrand(brand);
        desc.setDescription("description_1");
        desc.setName("name_1");
        desc.setProductNumber( "pn123");

        DefineIdentityProductSpecCommand command = new DefineIdentityProductSpecCommand(productSpecId, desc,
                new ArrayList<RelatedParty>(), new ArrayList<RelatedResource>(), new TimePeriod(),
                EntityType.PRODUCTSPECIFICATION);
         Assertions.assertNotNull(command);
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(new ServiceSpecification().id(serviceSpecId)
                .lifecycleStatus("inStudy").description("Mobile Access").name("MobileAccess"));
      

    }

    @Test
    void raiseEventsWhenProcessProductSpecificationInitiation_stock() {

        InitiateStockItemProductSpecCommand command = new InitiateStockItemProductSpecCommand(productSpecId,
                stockItemId);
        ServiceSpecification service = new ServiceSpecification();
        service.lifecycleStatus("active").id(stockItemId);

        StockItemType stockItemType = new StockItemType();
        stockItemType.setId(stockItemId);

        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(new StockItemSelectedEvent(productSpecId, stockItemId));
        expectedEvents.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
                SupportEntity.STOCKITEMTYPE.toString(), null, null, stockItemType,null));

        when(queryService.getStockItemTypeById(stockItemId,accessToken.getToken())).thenReturn(stockItemType);
        fixture.registerInjectableResource(queryService).given().when(command).expectEvents(expectedEvents.get(0),
                expectedEvents.get(1));

    }

    @Test
    void raiseEventsWhenProcessProductSpecificationInitiation_stock_null() {

        InitiateStockItemProductSpecCommand command = new InitiateStockItemProductSpecCommand(productSpecId,
                stockItemId);
        ServiceSpecification service = new ServiceSpecification();
        service.lifecycleStatus("active").id(stockItemId);

        StockItemType stockItemType = new StockItemType();
        stockItemType.setId(stockItemId);

        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(new StockItemSelectedEvent(productSpecId, stockItemId));
        expectedEvents.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
                SupportEntity.STOCKITEMTYPE.toString(), null, null, stockItemType,null));

        when(queryService.getStockItemTypeById(stockItemId,accessToken.getToken())).thenReturn(null);
        fixture.registerInjectableResource(queryService).given().when(command).expectEvents();

    }


    @Test
    void raiseEventWhenProcessProductSpecificationOperation() {

        OperationSpecification operationSpecification = new OperationSpecification();
        operationSpecification.id("1").name("Create").validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
        List<OperationSpecification> operationSpecificationList = new ArrayList<>();
        operationSpecificationList.add(operationSpecification);

        ProductSpecOperationCommand command = new ProductSpecOperationCommand(productSpecId,
                operationSpecificationList);

        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(new ServiceSpecification().id(serviceSpecId)
                .lifecycleStatus("active").operationSpecification(operationSpecificationList));

        fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
                new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                new ProductSpecOpDefinedEvent(productSpecId, operationSpecificationList, OffsetDateTime.now()));

    }

    @Test
    void raiseInvalidEventWhenProcessProductSpecificationOperationSelectedWithInvalidLifecycleStatus() {
        OperationSpecification operationSpecification = new OperationSpecification();
        operationSpecification.id("1").name("Create");
        List<OperationSpecification> operationSpecificationList = new ArrayList<>();
        operationSpecificationList.add(operationSpecification);

        ProductSpecOperationCommand command = new ProductSpecOperationCommand(productSpecId,
                operationSpecificationList);

        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("inStudy"));

        fixture.registerInjectableResource(queryService).given(history).when(command)
                .expectException(DiscoManagedClientException.class);

    }


    @Test
    void raiseEventWhenProcessProductSpecificationOperation_stock() {
        OffsetDateTime currentTime = OffsetDateTime.now();
        TimePeriod current = new TimePeriod().startDateTime(currentTime);
        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("active")
                        .operationSpecification(List.of(new OperationSpecification().id("1").validFor(current)))
                        .validFor(current));

        StockItemType stockItemType=new StockItemType();
        stockItemType.setId("1");
        stockItemType.name("sim");


        when(queryService.getStockItemTypeById("1",accessToken.getToken()))
                .thenReturn(stockItemType);

        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, serviceSpecId));

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);
        ProductSpecOperationCommand command = new ProductSpecOperationCommand(productSpecId,
                List.of(new OperationSpecification().id("1")
                        .validFor(new TimePeriod().startDateTime(currentTime.minusDays(1)))));

        OperationSpecification operationSpecification1 = new OperationSpecification();
        operationSpecification1.id("1");
        operationSpecification1.name("Add");
        operationSpecification1.description("add a product");
        OperationSpecification operationSpecification2 = new OperationSpecification();
        operationSpecification2.id("2");
        operationSpecification2.name("Return");
        operationSpecification2.description("Return a product");
        OperationSpecification operationSpecification3 = new OperationSpecification();
        operationSpecification3.id("3");
        operationSpecification3.name("Replace");
        operationSpecification3.description("Replace a product");

        List<OperationSpecification> operationSelected = new ArrayList<>();
        operationSelected.add(operationSpecification1);
        operationSelected.add(operationSpecification2);
        operationSelected.add(operationSpecification3);

        fixture.registerInjectableResource(queryService).given(history).when(command)
                .expectEvents(new ProductSpecOpDefinedEvent(productSpecId, operationSelected, OffsetDateTime.now()));

    }

    @Test
    void raiseEventWhenProcessProductSpecValidate() {
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        List<ServiceSpecificationRef> serviceSpec = new ArrayList<>();
        serviceSpec.add(serviceSpecificationRef);

        ProductSpecification productSpecification = new ProductSpecification();
        productSpecification.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.fromValue("inTest"))
                .supportEntity(SupportEntity.CFSSPEC).serviceSpecification(serviceSpec).version("0.1");

        when(queryService.getServiceSpecById(any(String.class),eq(accessToken.getToken())))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("active"));

        ProductSpecValidatedCommand validatedCommand = new ProductSpecValidatedCommand(productSpecId);

        fixture.registerInjectableResource(queryService).given(history).when(validatedCommand).expectEvents(
                new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                new ProductSpecValidatedEvent(productSpecification.getId(), ProductSpecificationLifecycle.INTEST,
                        lastUpdate),
                new ProductSpecVersionCreatedEvent(productSpecification.getId(), "0.1", lastUpdate),
                new ProductSpecCreationCompletedEvent(productSpecId, productSpecification));

    }

    @Test
    void raiseInValidEventWhenProcessProductSpecValidate() {
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "unavailable"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                lastUpdate, null,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getServiceSpecById(any(String.class),eq(accessToken.getToken())))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("unavailable"));

        ProductSpecValidatedCommand validatedCommand = new ProductSpecValidatedCommand(productSpecId);
        fixture.registerInjectableResource(queryService).given(history).when(validatedCommand).expectException(DiscoManagedClientException.class);
    }

    @Test
    @DisplayName(value = "Product Spec Cancel Event Test")
    void raiseEventWhenProcessProductSpecCancelTest() {
        // given
        String productSpecId = "productSpecId1";
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null));
        ProductSpecCancelCommand cancelCommand = new ProductSpecCancelCommand(productSpecId);

        List<ServiceSpecificationRef> serviceSpec = new ArrayList<>();
        serviceSpec.add(new ServiceSpecificationRef().id(serviceSpecId));

        ProductSpecification productSpec = new ProductSpecification().id(productSpecId)
                .lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).supportEntity(SupportEntity.CFSSPEC)
                .serviceSpecification(serviceSpec);

        fixture.registerInjectableResource(queryService).given(history).when(cancelCommand)
                .expectEvents(new ProductSpecCancelledEvent(productSpecId, productSpec));
    }

    @Test
    @DisplayName(value = "Invalid Product Spec. Cancel Event Test")
    void raiseInvalidEventWhenProcessProductSpecCancelTest() {
        // given
        String productSpecId = "productSpecId1";
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INTEST, "CFSSpec",
                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null));
        ProductSpecCancelCommand cancelCommand = new ProductSpecCancelCommand(productSpecId);
        fixture.registerInjectableResource(queryService).given(history).when(cancelCommand)
                .expectException(DiscoManagedClientException.class);
    }

    @Test
    void raiseEventsWhenSelectStockItemProductSpecCharacteristicCommandRaised_StockItemTypeIsNull() {
        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemTypeId));
        history.add(new StockItemStateVerifiedEvent(productSpecId, stockItemTypeId, StockItemLifeCycleEnum.ACTIVE));

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        StockItemCharacteristic stockItemCharacteristic = new StockItemCharacteristic().id("SIT1")
                .name("StockItemType for Mobile phone");
        List<StockItemCharacteristic> stockItemCharacteristics = new ArrayList<>();
        stockItemCharacteristics.add(stockItemCharacteristic);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        stockItemType.setValidFor(validFor);
        stockItemType.setStockItemCharacteristic(stockItemCharacteristics);

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);

        when(queryService.getStockItemByStockItemTypeId(any(String.class),eq(accessToken.getToken()))).thenReturn(null);
        SelectStockItemProductSpecCharacteristicCommand command = new SelectStockItemProductSpecCharacteristicCommand(
                productSpecId, new ArrayList<ProductSpecificationCharacteristic>());

        fixture.registerInjectableResource(queryService).given(history).when(command)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseEventsWhenSelectStockItemProductSpecCharacteristicCommandRaised_StockItemIsNull() {
        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemTypeId));
        history.add(new StockItemStateVerifiedEvent(productSpecId, stockItemTypeId, StockItemLifeCycleEnum.ACTIVE));

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        StockItemCharacteristic stockItemCharacteristic = new StockItemCharacteristic().id("SIT1")
                .name("StockItemType for Mobile phone");
        List<StockItemCharacteristic> stockItemCharacteristics = new ArrayList<>();
        stockItemCharacteristics.add(stockItemCharacteristic);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        stockItemType.setValidFor(validFor);
        stockItemType.setStockItemCharacteristic(stockItemCharacteristics);

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);

        List<StockItem> stockList = new ArrayList<>();
        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemTypeId);
        stockList.add(stockItem);

        when(queryService.getStockItemByStockItemTypeId(any(String.class),eq(accessToken.getToken()))).thenReturn(stockList);
        when(queryService.getStockItemTypeById(any(String.class),eq(accessToken.getToken()))).thenReturn(null);
        SelectStockItemProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectStockItemProductSpecCharacteristicCommand(
                productSpecId, new ArrayList<ProductSpecificationCharacteristic>());

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseEventsWhenSelectStockItemProductSpecCharacteristicCommandRaised_ProductspecCharIdIsLessThanInStockCharId() {
        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemTypeId));
        history.add(new StockItemStateVerifiedEvent(productSpecId, stockItemTypeId, StockItemLifeCycleEnum.ACTIVE));

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        StockItemCharacteristic stockItemCharacteristic = new StockItemCharacteristic().id("SIT1")
                .name("StockItemType for Mobile phone");
        List<StockItemCharacteristic> stockItemCharacteristics = new ArrayList<>();
        stockItemCharacteristics.add(stockItemCharacteristic);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        stockItemType.setValidFor(validFor);
        stockItemType.setStockItemCharacteristic(stockItemCharacteristics);

        List<StockItemCharacteristicValue> characteristicValueList1 = new ArrayList<>();
        StockItemCharacteristicValue stockItemCharacteristicValue1 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue1.setValue("Blue");
        StockItemCharacteristicValue stockItemCharacteristicValue2 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue2.setValue("Red");
        characteristicValueList1.add(stockItemCharacteristicValue1);
        characteristicValueList1.add(stockItemCharacteristicValue2);

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);

        List<StockItem> stockList = new ArrayList<>();
        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemTypeId);
        stockList.add(stockItem);

        List<ProductSpecificationCharacteristic> productSpecificationCharacteristicList = new ArrayList<ProductSpecificationCharacteristic>();

        when(queryService.getStockItemByStockItemTypeId(any(String.class),eq(accessToken.getToken()))).thenReturn(stockList);
        when(queryService.getStockItemTypeById(any(String.class),eq(accessToken.getToken()))).thenReturn(stockItemType);
        SelectStockItemProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectStockItemProductSpecCharacteristicCommand(
                productSpecId, productSpecificationCharacteristicList);

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseEventsWhenSelectStockItemProductSpecCharacteristicCommandRaised_passs() {
        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemTypeId));
        history.add(new StockItemStateVerifiedEvent(productSpecId, stockItemTypeId, StockItemLifeCycleEnum.ACTIVE));

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        StockItemCharacteristic stockItemCharacteristic = new StockItemCharacteristic().id("1")
                .name("StockItemType for Mobile phone");
        List<StockItemCharacteristic> stockItemCharacteristics = new ArrayList<>();
        stockItemCharacteristics.add(stockItemCharacteristic);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("STK1");
        stockItemType.setValidFor(validFor);
        stockItemType.setStockItemCharacteristic(stockItemCharacteristics);

        List<StockItemCharacteristicValue> characteristicValueList1 = new ArrayList<>();
        StockItemCharacteristicValue stockItemCharacteristicValue1 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue1.setValue("Blue");
        stockItemCharacteristicValue1.setStockItemCharacteristic(stockItemCharacteristic);
        StockItemCharacteristicValue stockItemCharacteristicValue2 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue2.setValue("Red");
        stockItemCharacteristicValue2.setStockItemCharacteristic(stockItemCharacteristic);
        characteristicValueList1.add(stockItemCharacteristicValue1);
        characteristicValueList1.add(stockItemCharacteristicValue2);

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);

        List<StockItem> stockList = new ArrayList<>();
        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemTypeId);
        stockItem.setStockItemCharacteristicValue(characteristicValueList1);
        stockList.add(stockItem);

        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList = new ArrayList<>();
        ProductSpecificationCharacteristicValue value1 = new ProductSpecificationCharacteristicValue();
        value1.setCharacteristicReferenceValue("Red");
        productSpecificationCharacteristicValueList.add(value1);

        ProductSpecificationCharacteristic productSpecificationCharacteristic1 = new ProductSpecificationCharacteristic()
                .id("1").configurable(true).name("StockItemType for Mobile phone").validFor(validFor)
                .productSpecCharacteristicValue(productSpecificationCharacteristicValueList);

        List<ProductSpecificationCharacteristic> productSpecificationCharacteristicList = new ArrayList<ProductSpecificationCharacteristic>();
        productSpecificationCharacteristicList.add(productSpecificationCharacteristic1);

        when(queryService.getStockItemByStockItemTypeId(any(String.class),eq(accessToken.getToken()))).thenReturn(stockList);
        when(queryService.getStockItemTypeById(any(String.class),eq(accessToken.getToken()))).thenReturn(stockItemType);
        SelectStockItemProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectStockItemProductSpecCharacteristicCommand(
                productSpecId, productSpecificationCharacteristicList);

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectEvents(
                        new ProductSpecCharacteristicsSelectedEvent(productSpecId,
                                selectProductSpecCharacteristicCommand.getProductSpecCharacteristics()),
                        new ProductSpecCharacteristicsDefinedEvent(productSpecId,
                                productSpecificationCharacteristicList,new ArrayList<UsageSpecification>(), OffsetDateTime.now()));

    }

    @Test
    void raiseEventsWhenComputeProductConfiguration() {
        List<Event> history = new ArrayList<>();
        history.add(new StockItemSelectedEvent(productSpecId, stockItemTypeId));
        history.add(new StockItemStateVerifiedEvent(productSpecId, stockItemTypeId, StockItemLifeCycleEnum.ACTIVE));

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        StockItemCharacteristic stockItemCharacteristic = new StockItemCharacteristic().id("SIT1")
                .name("StockItemType for Mobile phone");
        List<StockItemCharacteristic> stockItemCharacteristics = new ArrayList<>();
        stockItemCharacteristics.add(stockItemCharacteristic);
        StockItemType stockItemType = new StockItemType();
        stockItemType.setId("SIT1");
        stockItemType.setValidFor(validFor);
        stockItemType.setStockItemCharacteristic(stockItemCharacteristics);

        List<StockItemCharacteristicValue> characteristicValueList1 = new ArrayList<>();
        StockItemCharacteristicValue stockItemCharacteristicValue1 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue1.setValue("Blue");
        StockItemCharacteristicValue stockItemCharacteristicValue2 = new StockItemCharacteristicValue();
        stockItemCharacteristicValue2.setValue("Red");
        characteristicValueList1.add(stockItemCharacteristicValue1);
        characteristicValueList1.add(stockItemCharacteristicValue2);

        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.STOCKITEMTYPE.toString(), null,
                OffsetDateTime.now(), stockItemType,null);
        history.add(productSpecInitiatedEvent);

        List<ProductSpecificationCharacteristicValue> characteristicValueList = new ArrayList<>();
        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue1 = new ProductSpecificationCharacteristicValue();
        productSpecificationCharacteristicValue1.value("Blue");
        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue2 = new ProductSpecificationCharacteristicValue();
        productSpecificationCharacteristicValue2.value("256Gb");
        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue3 = new ProductSpecificationCharacteristicValue();
        productSpecificationCharacteristicValue3.value("Red");
        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue4 = new ProductSpecificationCharacteristicValue();
        productSpecificationCharacteristicValue4.value("128Gb");
        characteristicValueList.add(productSpecificationCharacteristicValue1);
        characteristicValueList.add(productSpecificationCharacteristicValue2);
        characteristicValueList.add(productSpecificationCharacteristicValue3);
        characteristicValueList.add(productSpecificationCharacteristicValue4);

        List<ProductSpecificationCharacteristic> productSpecificationCharacteristicList = new ArrayList<>();
        ProductSpecificationCharacteristic productSpecificationCharacteristic1 = new ProductSpecificationCharacteristic();
        productSpecificationCharacteristic1.id("1");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList1 = new ArrayList<>();
        productSpecificationCharacteristicValueList1.add(productSpecificationCharacteristicValue1);
        productSpecificationCharacteristic1
                .productSpecCharacteristicValue(productSpecificationCharacteristicValueList1);

        ProductSpecificationCharacteristic productSpecificationCharacteristic2 = new ProductSpecificationCharacteristic();
        productSpecificationCharacteristic2.id("2");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList2 = new ArrayList<>();
        productSpecificationCharacteristicValueList2.add(productSpecificationCharacteristicValue2);
        productSpecificationCharacteristic2
                .productSpecCharacteristicValue(productSpecificationCharacteristicValueList2);


        ProductSpecificationCharacteristic productSpecificationCharacteristic3 = new ProductSpecificationCharacteristic();
        productSpecificationCharacteristic3.id("2");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList3 = new ArrayList<>();
        productSpecificationCharacteristicValueList3.add(productSpecificationCharacteristicValue2);
        productSpecificationCharacteristic3
                .productSpecCharacteristicValue(productSpecificationCharacteristicValueList3);

        ProductSpecificationCharacteristic productSpecificationCharacteristic4 = new ProductSpecificationCharacteristic();
        productSpecificationCharacteristic4.id("2");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValueList4 = new ArrayList<>();
        productSpecificationCharacteristicValueList4.add(productSpecificationCharacteristicValue4);
        productSpecificationCharacteristic4
                .productSpecCharacteristicValue(productSpecificationCharacteristicValueList4);


        productSpecificationCharacteristicList.add(productSpecificationCharacteristic1);
        productSpecificationCharacteristicList.add(productSpecificationCharacteristic2);
        productSpecificationCharacteristicList.add(productSpecificationCharacteristic3);
        productSpecificationCharacteristicList.add(productSpecificationCharacteristic4);

        ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
                .addProductSpecCharacteristicItem(new ProductSpecificationCharacteristic()
                        .productSpecCharacteristicValue(characteristicValueList))
                .stockItemType(stockItemType)
                .lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);


        ProductSpecCharacteristicsDefinedEvent productSpecCharacteristicsDefinedEvent = new ProductSpecCharacteristicsDefinedEvent(
                productSpecId, productSpecificationCharacteristicList,new ArrayList<UsageSpecification>(), OffsetDateTime.now());
        history.add(productSpecCharacteristicsDefinedEvent);

        when(queryService.getStockItemTypeById(stockItemTypeId,accessToken.getToken())).thenReturn(stockItemType);
        when(queryService.fetchProductSpecById(productSpecId,accessToken.getToken())).thenReturn(productSpecification);
        ComputeProductConfigurationCommand computeProductConfigurationCommand = new ComputeProductConfigurationCommand(
                productSpecId);

        fixture.registerInjectableResource(queryService).given(history).when(computeProductConfigurationCommand)
                .expectEvents(new ComputeProductConfigurationEvent(new ArrayList<>(), productSpecId));

    }

    @Test
    void raiseInvalidProductSpecValidForEventWhenSPSCCIsRaised() {
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                OffsetDateTime.now(), null,null);
        history.add(productSpecInitiatedEvent);

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("2");
        List<CharacteristicSpecification> serviceSpecCharacteristics = new ArrayList<>();
        serviceSpecCharacteristics.add(serviceSpecCharacteristic);

        ServiceSpecification serviceSpecification = new ServiceSpecification();
        serviceSpecification.id(serviceSpecId).lifecycleStatus("active").validFor(validFor)
                .serviceSpecCharacteristic(serviceSpecCharacteristics);

        when(queryService.getServiceSpecById(any(String.class),eq(accessToken.getToken()))).thenReturn(serviceSpecification);

        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
                .value("Dummy_value");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
        productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

        ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
                .id("2").configurable(true).name("MSISDN").validFor(validFor)
                .productSpecCharacteristicValue(productSpecificationCharacteristicValues);

        List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
        productSpecCharacteristics.add(productSpecificationCharacteristic);
        SelectProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectProductSpecCharacteristicCommand(
                productSpecId, productSpecCharacteristics,new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseInvalidProductSpecValidForEventWhenSPSCCIsRaised_exception() {
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                OffsetDateTime.now(), null,null);
        history.add(productSpecInitiatedEvent);

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("2");
        List<CharacteristicSpecification> serviceSpecCharacteristics = new ArrayList<>();
        serviceSpecCharacteristics.add(serviceSpecCharacteristic);

        ServiceSpecification serviceSpecification = new ServiceSpecification();
        serviceSpecification.id(serviceSpecId).lifecycleStatus("inStudy").validFor(validFor)
                .serviceSpecCharacteristic(serviceSpecCharacteristics);

        when(queryService.getServiceSpecById(any(String.class), eq(accessToken.getToken()))).thenReturn(serviceSpecification);

        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
                .value("Dummy_value");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
        productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

        ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
                .id("2").configurable(true).name("MSISDN").validFor(validFor)
                .productSpecCharacteristicValue(productSpecificationCharacteristicValues);

        List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
        productSpecCharacteristics.add(productSpecificationCharacteristic);
        SelectProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectProductSpecCharacteristicCommand(
                productSpecId, productSpecCharacteristics,new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectException(DiscoManagedClientException.class);

    }

    @Test
    void raiseInvalidProductSpecValidForEventWhenSPSCCIsRaised_exception_8989() {
        List<Event> history = new ArrayList<>();
        history.add(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId));
        history.add(new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"));
        ServiceSpecificationRef serviceSpecificationRef = new ServiceSpecificationRef();
        serviceSpecificationRef.id(serviceSpecId);
        ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent(productSpecId,
                ProductSpecificationLifecycle.INSTUDY, SupportEntity.CFSSPEC.toString(), serviceSpecificationRef,
                OffsetDateTime.now(), null,null);
        history.add(productSpecInitiatedEvent);

        TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusDays(10));

        ServiceSpecification serviceSpecification = new ServiceSpecification();
        serviceSpecification.id(serviceSpecId).lifecycleStatus("active").validFor(validFor)
                .serviceSpecCharacteristic(new ArrayList<CharacteristicSpecification>());

        when(queryService.getServiceSpecById(any(String.class), eq(accessToken.getToken()))).thenReturn(serviceSpecification);

        ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
                .value("Dummy_value");
        List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
        productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

        ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
                .id("2").configurable(true).name("MSISDN").validFor(validFor)
                .productSpecCharacteristicValue(productSpecificationCharacteristicValues);

        List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
        productSpecCharacteristics.add(productSpecificationCharacteristic);
        SelectProductSpecCharacteristicCommand selectProductSpecCharacteristicCommand = new SelectProductSpecCharacteristicCommand(
                productSpecId, productSpecCharacteristics,new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService).given(history).when(selectProductSpecCharacteristicCommand)
                .expectException(DiscoManagedClientException.class);

    }
    @Test
    void processProductSpecModificationCommandTest_whenProductSpecIsNull() {

        when(queryService.fetchProductSpecById(productSpecId, accessToken.getToken())).thenReturn(null);

        ProductSpecModificationCommand command = new ProductSpecModificationCommand(productSpecId);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processProductSpecModificationCommandTest_ServiceSpecLifeCycleNotValid() {

        List<ServiceSpecificationRef> serviceSpecRefList = new ArrayList<>();
        ServiceSpecificationRef serviceSpecRef = new ServiceSpecificationRef();
        serviceSpecRef.setId(serviceSpecId);
        serviceSpecRefList.add(serviceSpecRef);

        ProductSpecification prodSpec = new ProductSpecification();
        prodSpec.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
                .serviceSpecification(serviceSpecRefList).supportEntity(SupportEntity.CFSSPEC);

        when(queryService.fetchProductSpecById(productSpecId, accessToken.getToken())).thenReturn(prodSpec);
        when(queryService.getServiceSpecById(serviceSpecId, accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("inStudy"));

        ProductSpecModificationCommand command = new ProductSpecModificationCommand(productSpecId);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processProductSpecModificationCommandTest_ProductSpecLifeCycleInStudy() {

        List<ServiceSpecificationRef> serviceSpecRefList = new ArrayList<>();
        ServiceSpecificationRef serviceSpecRef = new ServiceSpecificationRef();
        serviceSpecRef.setId(serviceSpecId);
        serviceSpecRefList.add(serviceSpecRef);

        ProductSpecification prodSpec = new ProductSpecification();
        prodSpec.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
                .serviceSpecification(serviceSpecRefList).supportEntity(SupportEntity.CFSSPEC);

        when(queryService.fetchProductSpecById(productSpecId, accessToken.getToken())).thenReturn(prodSpec);
        when(queryService.getServiceSpecById(serviceSpecId, accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("launched"));

        ProductSpecModificationCommand command = new ProductSpecModificationCommand(productSpecId);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processProductSpecModificationCommandTest() {

        List<ServiceSpecificationRef> serviceSpecRefList = new ArrayList<>();
        ServiceSpecificationRef serviceSpecRef = new ServiceSpecificationRef();
        serviceSpecRef.setId(serviceSpecId);
        serviceSpecRefList.add(serviceSpecRef);

        ProductSpecification prodSpec = new ProductSpecification();
        prodSpec.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED)
                .serviceSpecification(serviceSpecRefList).supportEntity(SupportEntity.CFSSPEC);

        when(queryService.fetchProductSpecById(productSpecId, accessToken.getToken())).thenReturn(prodSpec);
        when(queryService.getServiceSpecById(serviceSpecId, accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("launched"));

        ProductSpecModificationCommand command = new ProductSpecModificationCommand(productSpecId);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command)
                .expectEvents(new ServiceSpecStateVerifiedEvent(this.productSpecId, serviceSpecId, "launched"),
                        new ProductSpecStatusVerifiedEvent(productSpecId, prodSpec.getLifecycleStatus()),
                        new ProductSpecModificationInitiatedEvent(productSpecId, prodSpec, OffsetDateTime.now()));
    }

    @Test
    void processModifyProductSpecDescribeCommandTest_ServiceSpecLifeCycleNotValid() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        ProductSpecification prodSpec = new ProductSpecification();
        prodSpec.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED).supportEntity(SupportEntity.CFSSPEC);

        when(queryService.fetchProductSpecById(productSpecId,accessToken.getToken())).thenReturn(prodSpec);
        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken()))
                .thenReturn(new ServiceSpecification().id(serviceSpecId).lifecycleStatus("inStudy"));

        ModifyDefineIdentityProductSpecCommand command = new ModifyDefineIdentityProductSpecCommand(productSpecId,
                identityData,null,null,null, EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processModifyProductSpecDescribeCommandTest_ProductSpecLifeCycleNotValid() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        ServiceSpecification serviceSpec=new ServiceSpecification().id(serviceSpecId).lifecycleStatus("active");
        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken()))
                .thenReturn(serviceSpec);
        when(queryService.fetchProductSpecById(productSpecId, accessToken.getToken())).thenReturn(getStoredProductSpecification());

        ModifyDefineIdentityProductSpecCommand command = new ModifyDefineIdentityProductSpecCommand(productSpecId,
                identityData,null,null,null, EntityType.PRODUCTSPECIFICATION,null);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processModifyProductSpecDescribeCommandTest() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        OffsetDateTime startDateTime=OffsetDateTime.now();

        ServiceSpecification serviceSpec=new ServiceSpecification().id(serviceSpecId).lifecycleStatus("active").validFor(new TimePeriod().startDateTime(startDateTime));
        serviceSpec.setRelatedResource(new ArrayList<RelatedResource>());
        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken()))
                .thenReturn(serviceSpec);
        
        when(queryService.fetchProductSpecById(productSpecId,accessToken.getToken())).thenReturn(new ProductSpecification().id(productSpecId).name("orange").lifecycleStatus(ProductSpecificationLifecycle.INTEST));
        when(queryService.fetchProductOfferingsByProductSpecId(productSpecId,accessToken.getToken())).thenReturn(new ArrayList<ProductOffering>());

        ModifyDefineIdentityProductSpecCommand command = new ModifyDefineIdentityProductSpecCommand(productSpecId,
                identityData, new ArrayList<RelatedParty>(), new ArrayList<RelatedResource>(), new TimePeriod().startDateTime(startDateTime.plusYears(1)), EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "inTest"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INTEST, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command)
                .expectEvents(new ServiceSpecStateVerifiedEvent(this.productSpecId, serviceSpecId, "active"),
                        new ProductSpecRelResourceSelectedEvent(productSpecId, serviceSpec.getRelatedResource()),
                        new ProductSpecDefineIdentityModifiedEvent(productSpecId, command.getDefineIdentityData(),
                                command.getRelatedParty(), command.getRelatedResource(), command.getValidFor(),
                                OffsetDateTime.now(), command.getType(),ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.INTEST));
    }


    ProductSpecification getStoredProductSpecification() {
        OperationSpecification os3=new OperationSpecification().id(".os1").description("osDescription1").name("osName1");
        OperationSpecification os4=new OperationSpecification().id(".os2").description("osDescription2").name("osName2");

        ProductSpecificationRelationship psr3=new ProductSpecificationRelationship().id("psr1").relationshipType(ProductSpecRelationshipType.RELIESON);
        ProductSpecificationRelationship psr4=new ProductSpecificationRelationship().id("psr2").relationshipType(ProductSpecRelationshipType.RELIESON);

        PolicyRuleRef pol1 = new PolicyRuleRef().id("1").name("pol1");
        PolicyRuleRef pol2 = new PolicyRuleRef().id("2").name("pol2");

        RelatedParty rp3=new RelatedParty().id("rp2").name("rpname2").role("rpRole2").referredType("rf2");
        RelatedParty rp4=new RelatedParty().id("rp1").name("rpname1").role("rpRole1").referredType("rf1");

        RelatedResource rr3=new RelatedResource().id("rr2").name("rrName2").role("rrRole2").referredType("rf2");
        RelatedResource rr4=new RelatedResource().id("rr1").name("rrName1").role("rrRole1").referredType("rf1");

        ProductSpecificationCharacteristicRelationship pscr1=new ProductSpecificationCharacteristicRelationship().id("pscr1").name("pscrName1").relationshipType("pscrRelation1");
        ProductSpecificationCharacteristicRelationship pscr2=new ProductSpecificationCharacteristicRelationship().id("pscr2").name("pscrName2").relationshipType("pscrRelation2");

        ProductSpecificationCharacteristicValue pscv1=new ProductSpecificationCharacteristicValue().
                isDefault(true).valueFrom("vf1").rangeInterval("ri1").regex("r1").unitOfMeasure("uom1").valueTo("vt1").valueType("vt1");
        ProductSpecificationCharacteristicValue pscv4=new ProductSpecificationCharacteristicValue().
                isDefault(true).valueFrom("vf2").rangeInterval("ri2").regex("r2").unitOfMeasure("uom2").valueTo("vt2").valueType("vt2");

        ProductSpecificationCharacteristic psc3=new ProductSpecificationCharacteristic().id(".psc2").name("pscName2").description("pscDescription2")
                .valueType("string").addProductSpecCharRelationshipItem(pscr2).addProductSpecCharacteristicValueItem(pscv4);
        ProductSpecificationCharacteristic psc4=new ProductSpecificationCharacteristic().id(".psc1").name("pscName1").description("pscDescription1")
                .valueType("string").addProductSpecCharRelationshipItem(pscr1).addProductSpecCharacteristicValueItem(pscv1);

        UsageSpecification us1=new UsageSpecification().id(".us1").name("usname1");
        UsageSpecification us2=new UsageSpecification().id(".us2").name("usname2");

        ProductSpecification storedproductSpecification=new ProductSpecification().id(productSpecId).name("ps1").description("description").productNumber("productNumber").
                brand("brand").addOperationSpecificationItem(os3).addOperationSpecificationItem(os4).addPolicyRuleItem(pol1).addPolicyRuleItem(pol2).
                addProductSpecificationRelationshipItem(psr3).addProductSpecificationRelationshipItem(psr4).addRelatedPartyItem(rp3).addRelatedPartyItem(rp4)
                .addRelatedResourceItem(rr3).addRelatedResourceItem(rr4).addProductSpecCharacteristicItem(psc3).addProductSpecCharacteristicItem(psc4).validFor(new TimePeriod());
        storedproductSpecification.setUsageSpecification(List.of(us1,us2));
        storedproductSpecification.setLifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
        storedproductSpecification.setVersion("0.1");
        storedproductSpecification.getValidFor().setStartDateTime(OffsetDateTime.now());
        return storedproductSpecification;
    }

    ProductSpecRelationModifiedEvent getProductSpecRelationModifiedEvent(){
        ProductSpecificationRelationship psr1=new ProductSpecificationRelationship().id("psr1").relationshipType(ProductSpecRelationshipType.RELIESON);
        ProductSpecificationRelationship psr2 = new ProductSpecificationRelationship().id("psr2")
                .relationshipType(ProductSpecRelationshipType.RELIESON);
        PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
        PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
        return new ProductSpecRelationModifiedEvent(productSpecId, new ArrayList<>(List.of(psr1, psr2)),
                new ArrayList<>(List.of(policyRef1, policyRef2)), null);
    }

    List<RelatedParty> getProductSpecRelatedPartyModifiedEvent(){
        RelatedParty rp1=new RelatedParty().id("rp1").name("rpname1").role("rpRole1").referredType("rf1");
        RelatedParty rp2=new RelatedParty().id("rp2").name("rpname2").role("rpRole2").referredType("rf2");
        return new ArrayList<>(List.of(rp1,rp2));
    }

    List<RelatedResource> getProductSpecRelResourceModifiedEvent(){
        RelatedResource rr1=new RelatedResource().id("rr1").name("rrName1").role("rrRole1").referredType("rf1");
        RelatedResource rr2=new RelatedResource().id("rr2").name("rrName2").role("rrRole2").referredType("rf2");
        return new ArrayList<>(List.of(rr1,rr2));
    }

    ProductSpecCharacteristicsModifiedEvent getProductSpecCharacteristicsModifiedEvent(){
        ProductSpecificationCharacteristicRelationship pscr3=new ProductSpecificationCharacteristicRelationship().id("pscr1").name("pscrName1").relationshipType("pscrRelation1");
        ProductSpecificationCharacteristicRelationship pscr4=new ProductSpecificationCharacteristicRelationship().id("pscr2").name("pscrName2").relationshipType("pscrRelation2");

        ProductSpecificationCharacteristicValue pscv2=new ProductSpecificationCharacteristicValue().
                isDefault(true).valueFrom("vf2").rangeInterval("ri2").regex("r2").unitOfMeasure("uom2").valueTo("vt2").valueType("vt2");
        ProductSpecificationCharacteristicValue pscv3=new ProductSpecificationCharacteristicValue().
                isDefault(true).valueFrom("vf1").rangeInterval("ri1").regex("r1").unitOfMeasure("uom1").valueTo("vt1").valueType("vt1");

        ProductSpecificationCharacteristic psc1=new ProductSpecificationCharacteristic().id(".psc1").name("pscName1").description("pscDescription1")
                .valueType("string").addProductSpecCharRelationshipItem(pscr3).addProductSpecCharacteristicValueItem(pscv3);
        ProductSpecificationCharacteristic psc2=new ProductSpecificationCharacteristic().id(".psc2").name("pscName2").description("pscDescription2")
                .valueType("string").addProductSpecCharRelationshipItem(pscr4).addProductSpecCharacteristicValueItem(pscv2);


        return new ProductSpecCharacteristicsModifiedEvent(productSpecId,new ArrayList<>(List.of(psc1,psc2)),getProductSpecUsageModified(), null);
    }

    List<UsageSpecification> getProductSpecUsageModified(){
        UsageSpecification us1=new UsageSpecification().id(".us1").name("usname1");
        UsageSpecification us2=new UsageSpecification().id(".us2").name("usname2");
        return new ArrayList<>(List.of(us1,us2));
    }

    @Test
    void testProductSpecValidationCommandWithMinorModificationInIdentityData() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        IdentityData identityData2 = new IdentityData();
        identityData2.setBrand("ciso updated");
        identityData2.setName("orange");
        identityData2.setProductNumber("ps1");
        identityData2.setDescription("description");

        ProductSpecification storedPS=getStoredProductSpecification();
        Mockito.when(queryService.fetchProductSpecById(Mockito.anyString(),eq(accessToken.getToken()))).thenReturn(storedPS);
        ProductSpecModificationValidatedCommand command = new ProductSpecModificationValidatedCommand(productSpecId);
        
     
        fixture.registerInjectableResource(queryService).given(
                new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                        new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null),
                new ProductSpecIdentityDataEvent(productSpecId, identityData, new ArrayList<>(), new ArrayList<>(),
                        new TimePeriod(), OffsetDateTime.now(), EntityType.PRODUCTSPECIFICATION, "http://localhost:8080"),
                new ProductSpecCharacteristicsDefinedEvent(productSpecId, getStoredProductSpecification().getProductSpecCharacteristic(), getStoredProductSpecification().getUsageSpecification(), null),
                new ProductSpecRelationDefinedEvent(productSpecId, getStoredProductSpecification().getProductSpecificationRelationship(), null, getStoredProductSpecification().getPolicyRuleRef()),
                new ProductSpecDefineIdentityModifiedEvent(productSpecId, identityData2, storedPS.getRelatedParty(),
                		storedPS.getRelatedResource(), storedPS.getValidFor(), OffsetDateTime.now(), EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE),
                new ProductSpecCharacteristicsModifiedEvent(productSpecId,
                        getStoredProductSpecification().getProductSpecCharacteristic(),
                        getStoredProductSpecification().getUsageSpecification(), null),
                new ProductSpecRelationModifiedEvent(productSpecId,
                        getStoredProductSpecification().getProductSpecificationRelationship(),
                        getStoredProductSpecification().getPolicyRuleRef(), null),
                new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null)).when(command)
                .expectEvents(new ProductSpecModificationValidatedEvent(command.getProductSpecId(),
                        ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now(), "0.2",
                        getStoredProductSpecification()));

    }

    @Test()
    void testProductSpecValidationCommandWithMinorModificationCharacterSpecification() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        ProductSpecificationCharacteristicRelationship pscr3 = new ProductSpecificationCharacteristicRelationship()
                .id("pscr1").name("pscrName1").relationshipType("pscrRelation1");
        ProductSpecificationCharacteristicRelationship pscr4 = new ProductSpecificationCharacteristicRelationship()
                .id("pscr2").name("pscrName2").relationshipType("pscrRelation2");

        ProductSpecificationCharacteristicValue pscv2 = new ProductSpecificationCharacteristicValue().isDefault(true)
                .valueFrom("vf22").rangeInterval("ri2").regex("r2").unitOfMeasure("uom2").valueTo("vt2")
                .valueType("vt2");
        ProductSpecificationCharacteristicValue pscv3 = new ProductSpecificationCharacteristicValue().isDefault(true)
                .valueFrom("vf1").rangeInterval("ri1").regex("r1").unitOfMeasure("uom1").valueTo("vt1")
                .valueType("vt1");

        ProductSpecificationCharacteristic psc1 = new ProductSpecificationCharacteristic().id(".psc1").name("pscName1")
                .description("pscDescription1").valueType("string").addProductSpecCharRelationshipItem(pscr3)
                .addProductSpecCharacteristicValueItem(pscv3);
        ProductSpecificationCharacteristic psc2 = new ProductSpecificationCharacteristic().id(".psc2").name("pscName2")
                .description("pscDescription2").valueType("string").addProductSpecCharRelationshipItem(pscr4)
                .addProductSpecCharacteristicValueItem(pscv2);
        ProductSpecModificationValidatedCommand command = new ProductSpecModificationValidatedCommand(productSpecId);
        ProductSpecification storedPS=getStoredProductSpecification();
        Mockito.when(queryService.fetchProductSpecById(Mockito.anyString(),eq(accessToken.getToken()))).thenReturn(storedPS);
        
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null),
                        getProductSpecRelationModifiedEvent(), getProductSpecRelatedPartyModifiedEvent(),
                        getProductSpecRelResourceModifiedEvent(), getProductSpecCharacteristicsModifiedEvent(),
                        new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null),
                        new ProductSpecDefineIdentityModifiedEvent(productSpecId, identityData, storedPS.getRelatedParty(), storedPS.getRelatedResource(),storedPS.getValidFor(), null,
                                EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE),
                        new ProductSpecCharacteristicsModifiedEvent(productSpecId, new ArrayList<>(List.of(psc1, psc2)),
                                null, null))
                .when(command)
                .expectEvents(new ProductSpecModificationValidatedEvent(command.getProductSpecId(),
                        ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now(), "0.2",
                        getStoredProductSpecification()));

    }

    @Test
    void testProductSpecValidationCommandWithMajorModificationCharacterSpecification() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("brand");
        identityData.setName("ciso");
        identityData.setProductNumber("productNumber");
        identityData.setDescription("description");

        ProductSpecificationCharacteristicRelationship pscr3 = new ProductSpecificationCharacteristicRelationship()
                .id("pscr1").name("pscrName1").relationshipType("pscrRelation1");
        ProductSpecificationCharacteristicRelationship pscr4 = new ProductSpecificationCharacteristicRelationship()
                .id("pscr2").name("pscrName2").relationshipType("pscrRelation2");

        ProductSpecificationCharacteristicValue pscv2 = new ProductSpecificationCharacteristicValue().isDefault(true)
                .valueFrom("vf2").rangeInterval("ri2").regex("r2").unitOfMeasure("uom2").valueTo("vt2")
                .valueType("vt2");
        ProductSpecificationCharacteristicValue pscv3 = new ProductSpecificationCharacteristicValue().isDefault(true)
                .valueFrom("vf1").rangeInterval("ri1").regex("r1").unitOfMeasure("uom1").valueTo("vt1")
                .valueType("vt1");

        ProductSpecificationCharacteristic psc1 = new ProductSpecificationCharacteristic().id(".psc11").name("pscName1")
                .description("pscDescription1").valueType("string").addProductSpecCharRelationshipItem(pscr3)
                .addProductSpecCharacteristicValueItem(pscv3);
        ProductSpecificationCharacteristic psc2 = new ProductSpecificationCharacteristic().id(".psc2").name("pscName2")
                .description("pscDescription2").valueType("string").addProductSpecCharRelationshipItem(pscr4)
                .addProductSpecCharacteristicValueItem(pscv2);
        ProductSpecModificationValidatedCommand command = new ProductSpecModificationValidatedCommand(productSpecId);
        Mockito.when(queryService.fetchProductSpecById(Mockito.anyString(),eq(accessToken.getToken())))
                .thenReturn(getStoredProductSpecification());
        fixture.registerInjectableResource(queryService).given(
                new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                        new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null),
                new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null),

                new ProductSpecIdentityDataEvent(productSpecId, identityData, new ArrayList<>(), new ArrayList<>(),
                        new TimePeriod(), OffsetDateTime.now(), EntityType.PRODUCTSPECIFICATION, "http://localhost:8080"),
                new ProductSpecCharacteristicsDefinedEvent(productSpecId,
                        getStoredProductSpecification().getProductSpecCharacteristic(),
                        getStoredProductSpecification().getUsageSpecification(), null),
                new ProductSpecRelationDefinedEvent(productSpecId,
                        getStoredProductSpecification().getProductSpecificationRelationship(), null, getStoredProductSpecification().getPolicyRuleRef()),
                new ProductSpecDefineIdentityModifiedEvent(productSpecId, identityData, new ArrayList<>(),
                        new ArrayList<>(), new TimePeriod(), OffsetDateTime.now(), EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE),
                new ProductSpecCharacteristicsModifiedEvent(productSpecId, new ArrayList<>(List.of(psc1, psc2)),
                        getStoredProductSpecification().getUsageSpecification(), null),
                new ProductSpecRelationModifiedEvent(productSpecId,
                        getStoredProductSpecification().getProductSpecificationRelationship(),
                        getStoredProductSpecification().getPolicyRuleRef(), null),
                new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null)

        ).when(command).expectEvents(new ProductSpecModificationValidatedEvent(command.getProductSpecId(),
                ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now(), "0.2", getStoredProductSpecification()));

    }


    @Test
    void testProductSpecValidationCommandWithMajorModificationInUsageSpecification() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        UsageSpecification us1 = new UsageSpecification().id(".us11").name("usname1");
        UsageSpecification us2 = new UsageSpecification().id(".us2").name("usname2");
        ProductSpecModificationValidatedCommand command = new ProductSpecModificationValidatedCommand(productSpecId);
        Mockito.when(queryService.fetchProductSpecById(Mockito.anyString(),eq(accessToken.getToken())))
                .thenReturn(getStoredProductSpecification());
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null),
                        getProductSpecRelationModifiedEvent(),
                        new ProductSpecDefineIdentityModifiedEvent(productSpecId, identityData,
                                getProductSpecRelatedPartyModifiedEvent(), getProductSpecRelResourceModifiedEvent(),
                                new TimePeriod().startDateTime(OffsetDateTime.now()), null, EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE),
                        new ProductSpecCharacteristicsModifiedEvent(productSpecId,
                                getStoredProductSpecification().getProductSpecCharacteristic(),
                                new ArrayList<>(List.of(us1, us2)), null),
                        new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null))
                .when(command)
                .expectEvents(new ProductSpecModificationValidatedEvent(command.getProductSpecId(),
                        ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now(), "0.2",
                        getStoredProductSpecification()));
    }

    @Test
    void testProductSpecValidationCommandWithMinorModificationInUsageSpecification() {

        IdentityData identityData = new IdentityData();
        identityData.setBrand("ciso");
        identityData.setName("orange");
        identityData.setProductNumber("ps1");
        identityData.setDescription("description");

        UsageSpecification us1 = new UsageSpecification().id(".us1").name("usname111");
        UsageSpecification us2 = new UsageSpecification().id(".us2").name("usname2");

        ProductSpecification storedPS=getStoredProductSpecification();

        Mockito.when(queryService.fetchProductSpecById(Mockito.anyString(),eq(accessToken.getToken()))).thenReturn(storedPS);
        
        
        ProductSpecModificationValidatedCommand command = new ProductSpecModificationValidatedCommand(productSpecId);
        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null),
                        getProductSpecRelationModifiedEvent(),
                        new ProductSpecDefineIdentityModifiedEvent(productSpecId, identityData,
                        		storedPS.getRelatedParty(), storedPS.getRelatedResource(),
                                storedPS.getValidFor(), null, EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE),

                        new ProductSpecCharacteristicsModifiedEvent(productSpecId,
                                getStoredProductSpecification().getProductSpecCharacteristic(),
                                new ArrayList<>(List.of(us1, us2)), null),
                        new ProductSpecVersionCreatedEvent(productSpecId, "0.1", null))
                .when(command)
                .expectEvents(new ProductSpecModificationValidatedEvent(command.getProductSpecId(),
                        ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now(), "0.2",
                        getStoredProductSpecification()));

    }

    @Test
    void processProductSpecCancelModificationCommandTest() {

        List<ServiceSpecificationRef> refList = new ArrayList<ServiceSpecificationRef>();
        ServiceSpecificationRef ref = new ServiceSpecificationRef();
        ref.setId(serviceSpecId);
        refList.add(ref);

        ProductSpecification prodSpec = new ProductSpecification();
        prodSpec.id(productSpecId).supportEntity(SupportEntity.CFSSPEC)
                .lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).serviceSpecification(refList);

        ProductSpecCancelModificationCommand command = new ProductSpecCancelModificationCommand(productSpecId);

        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command)
                .expectEvents(new ProductSpecModificationCancelledEvent(command.getProductSpecId(), prodSpec));
    }
    @Test
    void processModifyProductSpecCharacteristicCommandTest_ServiceSpecInvalidLifecycle() {
        ServiceSpecification serviceSpec = new ServiceSpecification();
        serviceSpec.id(serviceSpecId).lifecycleStatus("inStudy")
                .validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

        List<ProductSpecificationCharacteristic> charList = new ArrayList<>();
        ProductSpecificationCharacteristic productSpecChar = new ProductSpecificationCharacteristic();
        productSpecChar.id("1");
        charList.add(productSpecChar);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(serviceSpec);
        ModifyProductSpecCharacteristicCommand command = new ModifyProductSpecCharacteristicCommand(productSpecId,
                charList, new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processModifyProductSpecCharacteristicCommandTest() {

        List<CharacteristicValueSpecification> serviceSpecCharValues=new ArrayList<>();
        CharacteristicValueSpecification serviceSpecChar=new CharacteristicValueSpecification();
        serviceSpecChar.setValue("test");
        serviceSpecChar.validFor(new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusYears(1)));
        serviceSpecCharValues.add(serviceSpecChar);

        List<ProductSpecificationCharacteristicValue> prodSpecCharValue = new ArrayList<>();
        ProductSpecificationCharacteristicValue value = new ProductSpecificationCharacteristicValue();
        value.setValue("test");
        value.setUnitOfMeasure("test");
        value.setCharacteristicReferenceValue("test");
        value.validFor(new TimePeriod().startDateTime(OffsetDateTime.now().minusMonths(8))
                .endDateTime(OffsetDateTime.now().minusMonths(1)));
        prodSpecCharValue.add(value);

        List<ProductSpecificationCharacteristic> charList = new ArrayList<>();
        ProductSpecificationCharacteristic productSpecChar = new ProductSpecificationCharacteristic();
        productSpecChar.id("1").name("test").productSpecCharacteristicValue(prodSpecCharValue);
        charList.add(productSpecChar);
        Assertions.assertNotNull(productSpecChar);

        List<CharacteristicSpecification> serviceSpecCharacteristics = new ArrayList<>();
        CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("1").name("test");
        serviceSpecCharacteristic.setCharacteristicValueSpecification(serviceSpecCharValues);
        serviceSpecCharacteristics.add(serviceSpecCharacteristic);

        ServiceSpecification serviceSpec = new ServiceSpecification();
        serviceSpec
                .id(serviceSpecId).lifecycleStatus("active").validFor(new TimePeriod()
                .startDateTime(OffsetDateTime.now().minusYears(1l)).endDateTime(OffsetDateTime.now()))
                .serviceSpecCharacteristic(serviceSpecCharacteristics);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(serviceSpec);
        ModifyProductSpecCharacteristicCommand command = new ModifyProductSpecCharacteristicCommand(productSpecId,
                charList, new ArrayList<UsageSpecification>());
    }

    @Test
    void processModifyProductSpecCharacteristicCommandTest_InvalidValidForRange() {

        List<CharacteristicValueSpecification> serviceSpecCharValues=new ArrayList<>();
        CharacteristicValueSpecification serviceSpecChar=new CharacteristicValueSpecification();
        serviceSpecChar.setValue("test");
        serviceSpecChar.validFor(new TimePeriod().startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plusYears(1)));
        serviceSpecCharValues.add(serviceSpecChar);


        List<ProductSpecificationCharacteristicValue> prodSpecCharValue = new ArrayList<>();
        ProductSpecificationCharacteristicValue value = new ProductSpecificationCharacteristicValue();
        value.setValue("test");
        value.setCharacteristicReferenceValue("test");
        prodSpecCharValue.add(value);

        List<ProductSpecificationCharacteristic> charList = new ArrayList<>();
        ProductSpecificationCharacteristic productSpecChar = new ProductSpecificationCharacteristic();
        productSpecChar.id("1").name("test").productSpecCharacteristicValue(prodSpecCharValue);
        charList.add(productSpecChar);

        List<CharacteristicSpecification> serviceSpecCharacteristics = new ArrayList<>();
        CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("1").name("Sim");
        serviceSpecCharacteristic.setCharacteristicValueSpecification(serviceSpecCharValues);
        serviceSpecCharacteristics.add(serviceSpecCharacteristic);

        ServiceSpecification serviceSpec = new ServiceSpecification();
        serviceSpec.id(serviceSpecId).lifecycleStatus("active")
                .validFor(new TimePeriod().startDateTime(OffsetDateTime.now()))
                .serviceSpecCharacteristic(serviceSpecCharacteristics);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(serviceSpec);
        ModifyProductSpecCharacteristicCommand command = new ModifyProductSpecCharacteristicCommand(productSpecId,
                charList, new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void processModifyProductSpecCharacteristicCommandTest_InvalidCharacteristics() {

        List<ProductSpecificationCharacteristicValue> prodSpecCharValue = new ArrayList<>();
        ProductSpecificationCharacteristicValue value = new ProductSpecificationCharacteristicValue();
        value.setValue("test");
        value.validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
        prodSpecCharValue.add(value);

        List<ProductSpecificationCharacteristic> charList = new ArrayList<>();
        ProductSpecificationCharacteristic productSpecChar = new ProductSpecificationCharacteristic();
        productSpecChar.id("1").productSpecCharacteristicValue(prodSpecCharValue);
        charList.add(productSpecChar);

        CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("2");
        List<CharacteristicSpecification> serviceSpecCharacteristics = new ArrayList<>();
        serviceSpecCharacteristics.add(serviceSpecCharacteristic);

        ServiceSpecification serviceSpec = new ServiceSpecification();
        serviceSpec.id(serviceSpecId).lifecycleStatus("active")
                .validFor(new TimePeriod().startDateTime(OffsetDateTime.now()))
                .serviceSpecCharacteristic(serviceSpecCharacteristics);

        when(queryService.getServiceSpecById(serviceSpecId,accessToken.getToken())).thenReturn(serviceSpec);
        ModifyProductSpecCharacteristicCommand command = new ModifyProductSpecCharacteristicCommand(productSpecId,
                charList, new ArrayList<UsageSpecification>());

        fixture.registerInjectableResource(queryService)
                .given(new ServiceSpecSelectedEvent(productSpecId, serviceSpecId),
                        new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active"),
                        new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE, "CFSSpec",
                                new ServiceSpecificationRef().id(serviceSpecId), OffsetDateTime.now(), null,null))
                .when(command).expectException(DiscoManagedClientException.class);
    }

    @Test
    void raiseProductSpecificationDeleteEventTest() {
        // given
        String productSpecId = "productSpecId1";
        ProductSpecificationDeleteCommmand command = new ProductSpecificationDeleteCommmand(productSpecId,
                OffsetDateTime.now(), 40L, "HOURS");

        // action
        fixture.registerInjectableResource(queryService).given().when(command)
                .expectEvents(new ProductSpecificationDeleteEvent(command.getAggregateId(),
                        command.getLastUpdateDateTime(), command.getInterval(), command.getIntervalUnit()));
    }

}
