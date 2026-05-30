// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.publisher;
/*
 * package com.orange.bos.catalogconfigurator.publisher;
 * 
 * import
 * com.orange.bos.catalogconfigurator.CatalogConfiguratorApplicationTests;
 * import com.orange.bos.catalogconfigurator.dto.generated.servicespec.
 * ServiceSpecification; import
 * com.orange.bos.catalogconfigurator.event.servicespec.
 * ServiceSpecExpurgedEvent; import
 * com.orange.bos.catalogconfigurator.event.servicespec.
 * ServiceSpecReplicatedEvent; import com.orange.bos.envelope.event.Event;
 * import com.orange.bos.envelope.infra.EnvelopeEventStore; import
 * com.orange.bos.envelope.infra.Publisher; import
 * org.junit.jupiter.api.Assertions; import org.junit.jupiter.api.BeforeEach;
 * import org.junit.jupiter.api.Test;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map; import java.util.concurrent.ThreadLocalRandom; import
 * java.util.concurrent.atomic.AtomicBoolean; import
 * java.util.function.Consumer;
 * 
 * import static org.assertj.core.api.Assertions.assertThat;
 * 
 * public class PublisherTest extends CatalogConfiguratorApplicationTests {
 * 
 * private String lifeCycleStatus; private ServiceSpecification
 * serviceSpecification; private Publisher publisher;
 * 
 * @BeforeEach void setUp() { lifeCycleStatus = "Active"; serviceSpecification =
 * new ServiceSpecification();
 * serviceSpecification.setId(ThreadLocalRandom.current().ints(10,
 * 200).toString()); serviceSpecification.setLifecycleStatus(lifeCycleStatus); }
 * 
 * @Test public void storeEventsTest() { List<Event> eventList = new
 * ArrayList<>();
 * 
 * eventList.add(new ServiceSpecExpurgedEvent(serviceSpecification));
 * eventList.add(new ServiceSpecReplicatedEvent(serviceSpecification));
 * 
 * Map<String, List<Event>> eventStore = new HashMap<>();
 * 
 * publisher = new Publisher(new EnvelopeEventStore() {
 * 
 * @Override public void add(String aggregateId, List<Event> eventList) {
 * eventStore.put(aggregateId, eventList); }
 * 
 * @Override public List<Event> fetch(String aggregateId) { return null; }
 * 
 * @Override public List<Event> fetchBackward(String productSpecId) { // TODO
 * Auto-generated method stub return null; }
 * 
 * @Override public void deleteStream(String aggregateId) { // TODO
 * Auto-generated method stub
 * 
 * } }); publisher.publish("1", eventList);
 * assertThat(eventStore.get("1")).containsExactlyElementsOf(eventList); }
 * 
 * @Test public void callHandlersAfterStoringEventsTest() { List<Event>
 * eventList = new ArrayList<>(); eventList.add(new
 * ServiceSpecReplicatedEvent(serviceSpecification));
 * 
 * Map<String, List<Event>> eventStore = new HashMap<>();
 * 
 * publisher = new Publisher(new EnvelopeEventStore() {
 * 
 * @Override public void add(String aggregateId, List<Event> eventList) {
 * eventStore.put(aggregateId, eventList); }
 * 
 * @Override public List<Event> fetch(String aggregateId) { return null; }
 * 
 * @Override public List<Event> fetchBackward(String productSpecId) { // TODO
 * Auto-generated method stub return null; }
 * 
 * @Override public void deleteStream(String aggregateId) { // TODO
 * Auto-generated method stub
 * 
 * } });
 * 
 * AtomicBoolean flag = new AtomicBoolean(false);
 * Consumer<ServiceSpecReplicatedEvent> cfsDuplicatedEventHandler = (event) ->
 * flag.set(true);
 * 
 * publisher.register(ServiceSpecReplicatedEvent.class,
 * cfsDuplicatedEventHandler); publisher.publish("1", eventList);
 * Assertions.assertTrue(flag.get()); } }
 */