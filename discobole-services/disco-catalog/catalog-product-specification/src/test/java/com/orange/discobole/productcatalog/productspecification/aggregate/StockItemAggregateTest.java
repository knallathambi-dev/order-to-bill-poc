// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.aggregate;

import java.time.OffsetDateTime;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemExpurgedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusVerifyEvent;

public class StockItemAggregateTest extends ProductSpecificationApplicationTests {

       private com.orange.discobole.productcatalog.productspecification.dto.generated.Event event;

       private StockItem stockItem;

       private StockItem existingStockItem;

       private FixtureConfiguration<StockItemAggregate> fixture;
       private static String aggregateId = "1";

       @BeforeEach
       void setUp() {
             fixture = new AggregateTestFixture<>(StockItemAggregate.class);

             stockItem = new StockItem();
             stockItem.setId("1");
             stockItem.setState("active");

             event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
             event.setEventId("eventId1");
             event.setEventType("StockItemStateChange");
             event.setEvent(stockItem);

             existingStockItem = new StockItem();
             existingStockItem.setId("");
             existingStockItem.setState("active");

       }

       @Test
       void processStockItemTestWhenActiveStockItemIsReceived() {

             event.setTimeOcurred(OffsetDateTime.now());
             stockItem.setLastUpdate(event.getTimeOcurred());

             fixture.given().when(new StockItemStateChangeCommand(aggregateId, event)).expectEvents(
                          new StockItemExpurgedEvent(aggregateId, stockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from("active")),
                          new StockItemReplicatedEvent(aggregateId, stockItem),
                          new StockItemNotificationSentEvent(aggregateId, stockItem));
       }

       @Test
       void processStockItemTestWhenFirstTimeLaunchedStockItemIsReceived() {
             stockItem = new StockItem();
             stockItem.setId("1");
             stockItem.setState("launched");

             event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
             event.setEventId("1");
             event.setEventType("StockItemStateChange");
             event.setEvent(stockItem);

             fixture.given().when(new StockItemStateChangeCommand(aggregateId, event)).expectEvents(
                          new StockItemExpurgedEvent(aggregateId, stockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from("launched")),
                          new StockItemReplicatedEvent(aggregateId, stockItem),
                          new StockItemNotificationSentEvent(aggregateId, stockItem));

       }

       @Test
       void processStockItemTestWhenLaunchedStockItemIsReceived() {
             stockItem = new StockItem();
             stockItem.setId("1");
             stockItem.setState("launched");

             event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
             event.setEventId("1");
             event.setEventType("StockItemStateChange");
             event.setEvent(stockItem);

             fixture.given().when(new StockItemStateChangeCommand(aggregateId, event)).expectEvents(
                          new StockItemExpurgedEvent(aggregateId, stockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from("launched")),
                          new StockItemReplicatedEvent(aggregateId, stockItem),
                          new StockItemNotificationSentEvent(aggregateId, stockItem));

       }

       @Test
       void processStockItemTestWhenUnavailableStockItemIsReceived() {

             stockItem = new StockItem();
             stockItem.setId("1");
             stockItem.setState("unavailable");

             event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
             event.setEventId("1");
             event.setEventType("StockItemStateChange");
             event.setEvent(stockItem);

             fixture.given().when(new StockItemStateChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem));
       }

       @Test
       void processStockItemTestWhenInvalidStatusStockItemIsReceived() {

             stockItem = new StockItem();
             stockItem.setId("1");
             stockItem.setState("inactive");

             event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
             event.setEventId("1");
             event.setEventType("StockItemStateChange");
             event.setEvent(stockItem);

             fixture.given().when(new StockItemStateChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem));

       }

       @Test
       void processStockItemTestWhenInvalidStructureStockItemIsReceived() {
             event.setEvent(null);
             fixture.given().when(new StockItemAttributeValueChangeCommand(aggregateId, event)).expectEvents();
       }

       @Test
       void updateStockWhenEarlyTimeModifiedStockItemReceive() {

             TimePeriod timePeriod = new TimePeriod();
              timePeriod.setStartDateTime(event.getTimeOcurred());
             stockItem.setValidFor(timePeriod);

             fixture.given(new StockItemExpurgedEvent(aggregateId, stockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from("active")),
                          new StockItemReplicatedEvent(aggregateId, stockItem),
                          new StockItemNotificationSentEvent(aggregateId, stockItem))
                          .when(new StockItemAttributeValueChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem));

       }

       @Test
       void updateStockItemWhenModifiedStockItemReceive() {
             String lifecycleStatus = "active";

            event.setTimeOcurred(OffsetDateTime.now().plusHours(1L));
       event.setEventType("ServiceSpecificationAttributeValueChange");
             stockItem.setLastUpdate(event.getTimeOcurred());

             TimePeriod timePeriod = new TimePeriod();
              timePeriod.setStartDateTime(event.getTimeOcurred());
             stockItem.setValidFor(timePeriod);

             StockItem exStock = new StockItem();
             exStock.setId("1");
             exStock.setState(lifecycleStatus);
             exStock.setLastUpdate(OffsetDateTime.now());

             fixture.given(new StockItemExpurgedEvent(aggregateId, exStock),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from("active")),
                          new StockItemReplicatedEvent(aggregateId, exStock),
                          new StockItemNotificationSentEvent(aggregateId, exStock))
                          .when(new StockItemAttributeValueChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem),
                                       new StockItemAttributeUpdatedEvent(aggregateId, stockItem));

       }

       @Test
       void updateStockItemWhenInValidStockTypeStatusEventReceive() {
             String lifecycleStatus = "launched";

            event.setTimeOcurred(OffsetDateTime.now().plusHours(1L));
       event.setEventType("ServiceSpecificationAttributeValueChange");
             stockItem.setLastUpdate(event.getTimeOcurred());

             TimePeriod timePeriod = new TimePeriod();
              timePeriod.setStartDateTime(event.getTimeOcurred());
             stockItem.setValidFor(timePeriod);

             StockItem exStockItem = new StockItem();
             exStockItem.setId("1");
             exStockItem.setState(lifecycleStatus);
             exStockItem.setLastUpdate(OffsetDateTime.now());

             fixture.given(new StockItemExpurgedEvent(aggregateId, exStockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                        StockItemLifeCycleEnum.from(lifecycleStatus)),
                          new StockItemReplicatedEvent(aggregateId, exStockItem),
                          new StockItemNotificationSentEvent(aggregateId, exStockItem))
                          .when(new StockItemAttributeValueChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem));

       }

       @Test
       void updateStockItemWhenStockItemStatusModifiedEventReceive() {
             String lifecycleStatus = "active";
             OffsetDateTime time = OffsetDateTime.now();

             stockItem.setState("launched");
             stockItem.setLastUpdate(time);
             event.setEvent(stockItem);
             event.setTimeOcurred(time);

             TimePeriod timePeriod = new TimePeriod();
              timePeriod.setStartDateTime(event.getTimeOcurred());
             stockItem.setValidFor(timePeriod);

             StockItem exStockItem = new StockItem();
             exStockItem.setId("1");
             exStockItem.setState(lifecycleStatus);
       exStockItem.setLastUpdate(event.getTimeOcurred().minusMonths(1));

             fixture.given(new StockItemExpurgedEvent(aggregateId, exStockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(),
                                        StockItemLifeCycleEnum.from(lifecycleStatus), StockItemLifeCycleEnum.from(lifecycleStatus)),
                          new StockItemReplicatedEvent(aggregateId, exStockItem),
                          new StockItemNotificationSentEvent(aggregateId, exStockItem))
                          .when(new StockItemAttributeValueChangeCommand(aggregateId, event))
                          .expectEvents(new StockItemExpurgedEvent(aggregateId, stockItem),
                                       new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(),
                                                     StockItemLifeCycleEnum.from(lifecycleStatus),
                                                     StockItemLifeCycleEnum.from(stockItem.getState())),
                                       new StockItemStatusUpdatedEvent(aggregateId, stockItem.getId(),
                                                     StockItemLifeCycleEnum.from(stockItem.getState()), stockItem.getLastUpdate()));

       }

       @Test
       void updateStockItemWhenNotAlreadyStockItemExistedEventReceive() {
             fixture.given(new StockItemExpurgedEvent(aggregateId, existingStockItem),
                          new StockItemStatusVerifyEvent(aggregateId, stockItem.getId(), null,
                                    StockItemLifeCycleEnum.from(existingStockItem.getState())),
                          new StockItemReplicatedEvent(aggregateId, existingStockItem),
                          new StockItemNotificationSentEvent(aggregateId, existingStockItem))
                          .when(new StockItemAttributeValueChangeCommand(aggregateId, event)).expectEvents();

       }

}


