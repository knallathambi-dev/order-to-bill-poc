// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;
/*
 * package com.orange.bos.catalogconfigurator.service.impl;
 * 
 * import static org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertNotNull; import static
 * org.junit.jupiter.api.Assertions.assertTrue;
 * 
 * import java.time.OffsetDateTime; import java.util.ArrayList; import
 * java.util.HashSet; import java.util.List; import java.util.Set;
 * 
 * import org.apache.logging.log4j.LogManager; import
 * org.apache.logging.log4j.Logger; import org.junit.jupiter.api.Test; import
 * org.mockito.Mock; import org.mockito.Mockito; import
 * org.springframework.messaging.MessageChannel; import
 * org.springframework.test.util.ReflectionTestUtils;
 * 
 * import com.orange.bos.catalogconfigurator.aggregate.CategoryAggregate; import
 * com.orange.bos.catalogconfigurator.command.category.modify.
 * ModifyAssociateEntityCommand; import
 * com.orange.bos.catalogconfigurator.dto.generated.common.Category; import
 * com.orange.bos.catalogconfigurator.dto.generated.common.
 * CategoryEntityRelationship; import
 * com.orange.bos.catalogconfigurator.dto.generated.common.CategoryEntityType;
 * import com.orange.bos.catalogconfigurator.dto.generated.common.CategoryRef;
 * import
 * com.orange.bos.catalogconfigurator.dto.generated.common.ProductOffering;
 * import
 * com.orange.bos.catalogconfigurator.dto.generated.common.ProductOfferingRef;
 * import com.orange.bos.catalogconfigurator.dto.generated.productoffering.
 * ProductOfferingLifecycle; import
 * com.orange.bos.catalogconfigurator.dto.generated.productoffering.
 * ProductOfferingType; import
 * com.orange.bos.catalogconfigurator.event.category.
 * AssociateEntitySelectedEvent; import
 * com.orange.bos.catalogconfigurator.event.category.CategoryCancelledEvent;
 * import
 * com.orange.bos.catalogconfigurator.event.category.CategoryCreationEvent;
 * import com.orange.bos.catalogconfigurator.event.category.CategoryEvent;
 * import com.orange.bos.catalogconfigurator.event.category.
 * CategoryIdentityDataDefinedEvent; import
 * com.orange.bos.catalogconfigurator.event.category.EntityTypeSelectedEvent;
 * import
 * com.orange.bos.catalogconfigurator.event.category.SubcategoryDefinedEvent;
 * import com.orange.bos.catalogconfigurator.event.category.modify.
 * CategoryIdentityDataModifiedEvent; import
 * com.orange.bos.catalogconfigurator.event.category.modify.
 * CategoryModificationCancelledEvent; import
 * com.orange.bos.catalogconfigurator.event.category.modify.
 * CategoryModificationValidatedEvent; import
 * com.orange.bos.catalogconfigurator.event.category.modify.
 * AssociatedEntityModifiedEvent; import
 * com.orange.bos.catalogconfigurator.event.category.modify.
 * SubCategoryModifiedEvent; import
 * com.orange.bos.catalogconfigurator.event.productoffering.
 * ProductOfferingEvent; import
 * com.orange.bos.catalogconfigurator.event.productoffering.modify.
 * AtomicProductOfferingCategoryModifiedEvent; import
 * com.orange.bos.catalogconfigurator.eventstore.EventStoreImpl; import
 * com.orange.bos.catalogconfigurator.eventstore.InMemoryEventStore; import
 * com.orange.bos.catalogconfigurator.projection.CategoryProjector; import
 * com.orange.bos.catalogconfigurator.service.CategoryService; import
 * com.orange.bos.catalogconfigurator.service.ModifyCategoryService; import
 * com.orange.bos.catalogconfigurator.service.QueryService; import
 * com.orange.bos.catalogconfigurator.useractions.category.modify.
 * ModifyAssociatedEntityActionTest; import com.orange.bos.envelope.event.Event;
 * import com.orange.bos.envelope.infra.EnvelopeEventStore; import
 * com.orange.bos.envelope.infra.Publisher;
 * 
 * public class ModifyCategoryServiceImplTest { private static final Logger
 * LOGGER = LogManager.getLogger(CategoryServiceImplTest.class);
 * 
 * EnvelopeEventStore eventStore = new InMemoryEventStore(); Publisher publisher
 * = new Publisher(eventStore); ModifyCategoryService categoryService = new
 * ModifyCategoryServiceImpl(); QueryService queryService =
 * Mockito.mock(QueryService.class); CategoryProjector projector = new
 * CategoryProjector();
 * 
 * @Mock CategoryAggregate categoryAggregate;
 * 
 * @Mock private EventStoreImpl eventStoreimpl; public
 * ModifyCategoryServiceImplTest() {
 * ReflectionTestUtils.setField(categoryService, "eventStore", eventStore);
 * ReflectionTestUtils.setField(categoryService, "publisher", publisher);
 * ReflectionTestUtils.setField(categoryService, "queryService", queryService);
 * 
 * ReflectionTestUtils.setField(projector, "category", (MessageChannel)
 * (message, l) -> { LOGGER.info(message); return true; });
 * publisher.register(CategoryIdentityDataModifiedEvent.class,
 * projector::handle); publisher.register(AssociatedEntityModifiedEvent.class,
 * projector::handle); publisher.register(SubCategoryModifiedEvent.class,
 * projector::handle); }
 * 
 * 
 * 
 * @Test void triggerCategoryIdentityDataModifiedEvent() { String categoryId =
 * "categoryId1"; List<Event> history = new ArrayList<>(); history.add(new
 * EntityTypeSelectedEvent(categoryId,
 * CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue(),
 * OffsetDateTime.now())); OffsetDateTime.now(); publisher.publish(categoryId,
 * history); categoryService.modifyCategoryIdentityData(categoryId, "cname",
 * "cdescription", true, null); List<Event> actualEvents =
 * eventStore.fetch(categoryId); assertTrue(actualEvents.get(1) instanceof
 * CategoryEvent); //assertEquals(categoryId,
 * ((CategoryIdentityDataModifiedEvent) actualEvents.get(1)).getCategoryId());
 * 
 * }
 * 
 * @Test void triggerAssociateEntityModifiedEvent() throws InterruptedException
 * {
 * 
 * String categoryId = "categoryId2"; List<Event> history = new ArrayList<>();
 * List<String> pos = new ArrayList<String>(); pos.add("PO1"); history.add(new
 * EntityTypeSelectedEvent(categoryId,
 * CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString(),
 * OffsetDateTime.now())); ProductOffering po = new
 * ProductOffering().id("PO1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
 * .type(ProductOfferingType.ATOMICPRODUCTOFFERING); ProductOffering po2= new
 * ProductOffering().id("PO2").lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
 * .type(ProductOfferingType.ATOMICPRODUCTOFFERING); ProductOfferingRef
 * productOfferingRef=new
 * ProductOfferingRef().id(po2.getId()).type(po2.getType().toString()); Category
 * category=new
 * Category().id(categoryId).type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.
 * getValue()).productOffering(Set.of(productOfferingRef));
 * CategoryEntityRelationship categoryEntity=new
 * CategoryEntityRelationship().id(categoryId).productOfferings(Set.of(
 * productOfferingRef
 * )).type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue ());
 * Mockito.when(queryService.fetchProductOfferingById("PO1")).thenReturn(po);
 * Mockito.when(queryService.fetchCategoryById(categoryId)).thenReturn(category)
 * ; Mockito.when(queryService.fetchCategoryEntityById(categoryId)).thenReturn(
 * categoryEntity); publisher.publish(categoryId, history);
 * categoryService.modifyAssociatedEntity(categoryId, pos); List<Event>
 * actualEvents = eventStore.fetch(categoryId); List<Event> po2Events =
 * eventStore.fetch("PO2"); List<Event> po1Events = eventStore.fetch("PO1");
 * assertTrue(actualEvents.get(1) instanceof CategoryEvent);
 * assertTrue(po2Events.get(0) instanceof ProductOfferingEvent);
 * assertTrue(po1Events.get(0) instanceof ProductOfferingEvent);
 * 
 * }
 * 
 * @Test void triggerSubcategoryDefinedEvent() throws InterruptedException {
 * 
 * String categoryId = "categoryId3"; List<Event> history = new ArrayList<>();
 * OffsetDateTime.now(); Set<ProductOfferingRef> refs = new
 * HashSet<ProductOfferingRef>(); history.add(new
 * EntityTypeSelectedEvent(categoryId,
 * CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue(),
 * OffsetDateTime.now()));
 * Mockito.when(queryService.fetchCategoryById("PO2")).thenReturn(new
 * Category().id("PO2").lifecycleStatus("active")); List<String> scIds = new
 * ArrayList<String>(); scIds.add("PO2"); publisher.publish(categoryId,
 * history); categoryService.modifySubCategory(categoryId, scIds);
 * 
 * List<Event> actualEvents = eventStore.fetch(categoryId);
 * assertTrue(actualEvents.get(1) instanceof CategoryEvent);
 * //assertEquals(categoryId, ((SubCategoryModifiedEvent)
 * actualEvents.get(1)).getCategoryId());
 * 
 * }
 * 
 * @Test void ModifyCategoryCancelledEvent() throws InterruptedException {
 * 
 * String categoryId = "categoryId4"; List<Event> history = new ArrayList<>();
 * OffsetDateTime.now(); Set<ProductOfferingRef> refs = new
 * HashSet<ProductOfferingRef>(); history.add(new
 * EntityTypeSelectedEvent(categoryId,
 * CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue(),
 * OffsetDateTime.now())); publisher.publish(categoryId, history);
 * categoryService.cancelCategoryModification(categoryId); List<Event>
 * actualEvents = eventStore.fetch(categoryId); assertTrue(actualEvents.get(1)
 * instanceof CategoryEvent); }
 * 
 * @Test void triggerCategoryCreationEvent() throws InterruptedException {
 * String categoryId = "categoryId6";
 * 
 * ProductOfferingRef po1=new
 * ProductOfferingRef().id("po1").name("po").type(CategoryEntityType.
 * PRODUCTOFFERINGCATEGORY.getValue()); ProductOfferingRef po2=new
 * ProductOfferingRef().id("po2").name("po").type(CategoryEntityType.
 * PRODUCTOFFERINGCATEGORY.getValue()); CategoryRef categoryRef=new
 * CategoryRef().id(categoryId).type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.
 * getValue()); List<Event> history = new ArrayList<>(); List<Event> historyPo1
 * = new ArrayList<>(); List<Event> historyPo2 = new ArrayList<>();
 * OffsetDateTime.now(); history.add(new EntityTypeSelectedEvent(categoryId,
 * CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue(),
 * OffsetDateTime.now())); history.add(new
 * CategoryIdentityDataModifiedEvent(categoryId, "cname", "cdescription", true,
 * null, OffsetDateTime.now())); history.add(new
 * AssociatedEntityModifiedEvent(categoryId,Set.of(po1),Set.of(po2),
 * OffsetDateTime.now())); historyPo1.add(new
 * AtomicProductOfferingCategoryModifiedEvent("po1",Set.of(categoryRef),null,
 * OffsetDateTime.now())); historyPo2.add(new
 * AtomicProductOfferingCategoryModifiedEvent("po2",null,Set.of(categoryRef),
 * OffsetDateTime.now()));
 * 
 * publisher.publish(categoryId, history); publisher.publish("po1", historyPo1);
 * publisher.publish("po2", historyPo2);
 * categoryService.validateModifyCategory(categoryId); List<Event> actualEvents
 * = eventStore.fetch(categoryId); assertTrue(actualEvents.get(3) instanceof
 * CategoryEvent);
 * 
 * }
 * 
 * 
 * 
 * }
 */