// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.eventstore;
/*
 * package com.orange.bos.catalogconfigurator.eventstore;
 * 
 * import com.fasterxml.jackson.core.JsonProcessingException; import
 * com.fasterxml.jackson.databind.ObjectMapper; import com.github.msemys.esjc.*;
 * import
 * com.github.msemys.esjc.proto.EventStoreClientMessages.ResolvedIndexedEvent;
 * import
 * com.orange.bos.catalogconfigurator.CatalogConfiguratorApplicationTests;
 * import com.orange.bos.envelope.event.Event; import
 * com.orange.bos.envelope.exception.BosException; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.*; import
 * org.mockito.internal.matchers.apachecommons.ReflectionEquals;
 * 
 * import java.util.ArrayList; import java.util.List; import
 * java.util.concurrent.CompletableFuture;
 * 
 * import static org.junit.jupiter.api.Assertions.assertThrows; import static
 * org.junit.jupiter.api.Assertions.assertTrue; import static
 * org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;
 * 
 * public class EventStoreImplTest extends CatalogConfiguratorApplicationTests {
 * 
 * String aggregateId; List<Event> eventList = new ArrayList<>();
 * 
 * @InjectMocks EventStoreImpl eventStoreImpl;
 * 
 * @Mock EventStore eventStore;
 * 
 * @Mock ObjectMapper mapper;
 * 
 * @Captor private ArgumentCaptor<List<EventData>> captor;
 * 
 * @BeforeEach public void setup() { aggregateId = "testAggregateId123";
 * 
 * Event event1 = new Event() {
 * 
 * @Override public String aggregateName() { return "aggregateName"; } };
 * eventList.add(event1);
 * 
 * Event event2 = new Event() {
 * 
 * @Override public String aggregateName() { return "aggregateName"; } };
 * eventList.add(event2); }
 * 
 * @Test public void addEventTest() { CompletableFuture<WriteResult> future =
 * new CompletableFuture<>(); WriteResult result = new WriteResult(0,
 * Position.END); when(eventStore.appendToStream(any(String.class),
 * any(Long.class), ArgumentMatchers.anyList())) .thenReturn(future);
 * future.complete(result); eventStoreImpl.add(aggregateId, eventList);
 * List<EventData> eventDataList = new ArrayList<>(); EventData event =
 * EventData.newBuilder().type("eventType").build(); eventDataList.add(event);
 * verify(eventStore, times(1)).appendToStream(any(String.class),
 * any(long.class), captor.capture()); assertTrue(new
 * ReflectionEquals("aggregateame".getBytes()).matches(captor.getValue().get(0).
 * metadata)); }
 * 
 * @Test public void jsonExceptionWhenAddTest() throws JsonProcessingException {
 * when(mapper.writeValueAsBytes(any(Event.class))).thenThrow(new
 * JsonProcessingException("") { private static final long serialVersionUID =
 * 1L; });
 * 
 * BosException thrown = assertThrows(BosException.class, () ->
 * eventStoreImpl.add(aggregateId, eventList));
 * assertTrue(thrown.getReason().contains("Unable to parse JSON")); }
 * 
 * @Test public void getEventListTest() { CompletableFuture<StreamEventsSlice>
 * future = new CompletableFuture<>(); List<ResolvedIndexedEvent> resolvedEvents
 * = new ArrayList<>(); StreamEventsSlice sliceEvent = new
 * StreamEventsSlice(SliceReadStatus.Success, aggregateId, 0,
 * ReadDirection.Forward, resolvedEvents, 0, 0, false);
 * 
 * when(eventStore.readStreamEventsForward(any(String.class), any(Long.class),
 * any(Integer.class), any(boolean.class))).thenReturn(future);
 * 
 * future.complete(sliceEvent); eventStoreImpl.fetch(aggregateId);
 * verify(eventStore, times(1)).readStreamEventsForward(any(String.class),
 * any(Long.class), any(Integer.class), any(boolean.class)); }
 * 
 * @Test public void deleteEventStreamTest() { CompletableFuture<DeleteResult>
 * future = new CompletableFuture<>(); DeleteResult deleteResult=new
 * DeleteResult(null); CompletableFuture<StreamMetadataResult> future1 = new
 * CompletableFuture<>(); StreamMetadataResult res=new
 * StreamMetadataResult(aggregateId, false, 0, null);
 * 
 * when(eventStore.getStreamMetadata(aggregateId)).thenReturn(future1);
 * when(eventStore.deleteStream(aggregateId,ExpectedVersion.ANY,
 * true)).thenReturn(future); future1.complete(res);
 * future.complete(deleteResult); eventStoreImpl.deleteStream(aggregateId);
 * verify(eventStore, times(1)).deleteStream(aggregateId,ExpectedVersion.ANY,
 * true); }
 * 
 * }
 */