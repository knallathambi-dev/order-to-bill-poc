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
import java.util.ArrayList;
import java.util.List;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecificationStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecExpurgedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.StatusVerifiedEvent;

public class ServiceSpecAggregateTest extends ProductSpecificationApplicationTests {

	private com.orange.discobole.productcatalog.productspecification.dto.generated.Event event;

	private ServiceSpecification serviceSpecification;

	private ServiceSpecification existingServiceSpecification;

	private FixtureConfiguration<ServiceSpecAggregate> fixture;
	private static String aggregateId = "1";

	@BeforeEach
	void setUp() {

		fixture = new AggregateTestFixture<>(ServiceSpecAggregate.class);
		List<RelatedResource> rList=new ArrayList<>();
		RelatedResource res=new RelatedResource();
		res.setId("1");
		res.setHref("test");
		res.setName("test");
		res.setRole("admin");
		rList.add(res);
		
		serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
		serviceSpecification.setLifecycleStatus("active");
		serviceSpecification.setHref("http://test");
		serviceSpecification.setRelatedResource(rList);
		event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
		event.setEventId("eventId1");
		event.setEventType("ServiceSpecificationStateChange");
		event.setEvent(serviceSpecification);
		
		existingServiceSpecification = new ServiceSpecification();
		existingServiceSpecification.setId("cfs1");
		existingServiceSpecification.setLifecycleStatus("active");
		existingServiceSpecification.setRelatedResource(rList);
		
	}

	@Test
	void processCfsTestWhenActiveCfsIsReceived() {
		String cfsId = aggregateId;
		String lifecycleStatus = "active";
		fixture.given().when(new ServiceSpecificationStateChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification),
						new StatusVerifiedEvent(aggregateId, cfsId, ServiceSpecLifeCycleEnum.from(lifecycleStatus),null),
						new ServiceSpecReplicatedEvent(aggregateId, serviceSpecification),
						new ServiceSpecNotificationSentEvent(aggregateId, serviceSpecification));

	}

	@Test
	void processCfsTestWhenInvalidStatusCfsIsReceived() {

		String lifecycleStatus = "not available";
		serviceSpecification.setLifecycleStatus(lifecycleStatus);

		fixture.given().when(new ServiceSpecificationStateChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification));
	}

	@Test
	void updateCfsWhenEarlyTimeModifiedCfsReceive() {

		String cfsId = aggregateId;
		String lifecycleStatus = "active";

		fixture.given(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification),
				new StatusVerifiedEvent(aggregateId, cfsId, ServiceSpecLifeCycleEnum.from(lifecycleStatus), null),
				new ServiceSpecReplicatedEvent(aggregateId, serviceSpecification),
				new ServiceSpecNotificationSentEvent(aggregateId, serviceSpecification))
				.when(new ServiceSpecAttributeValueChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification));

	}

	@Test
	void updateCfsWhenModifiedCfsReceive() {
		String cfsId = aggregateId;
		String lifecycleStatus = "active";

		event.setTimeOcurred(OffsetDateTime.now().plusHours(1L));
		event.setEventType("ServiceSpecificationAttributeValueChange");
		serviceSpecification.setLastUpdate(event.getTimeOcurred());
		
		ServiceSpecification exSpecification = new ServiceSpecification();
		exSpecification.setId("1");
		exSpecification.setLifecycleStatus(lifecycleStatus);
		exSpecification.setLastUpdate(OffsetDateTime.now());

		fixture.given(new ServiceSpecExpurgedEvent(aggregateId, exSpecification),
				new StatusVerifiedEvent(aggregateId, cfsId, ServiceSpecLifeCycleEnum.from(lifecycleStatus),
						ServiceSpecLifeCycleEnum.from(lifecycleStatus)),
				new ServiceSpecReplicatedEvent(aggregateId, exSpecification),
				new ServiceSpecNotificationSentEvent(aggregateId, exSpecification))
				.when(new ServiceSpecAttributeValueChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification),
						new ServiceSpecAttributeUpdatedEvent(aggregateId, serviceSpecification));

	}

	@Test
	void updateCfsWhenInValidCfsStatusEventReceive() {
		String cfsId = aggregateId;
		String lifecycleStatus = "launched";

		event.setTimeOcurred(OffsetDateTime.now().plusHours(1L));
		event.setEventType("ServiceSpecificationAttributeValueChange");
		serviceSpecification.setLastUpdate(event.getTimeOcurred());


		ServiceSpecification exSpecification = new ServiceSpecification();
		exSpecification.setId("1");
		exSpecification.setLifecycleStatus(lifecycleStatus);
		exSpecification.setLastUpdate(OffsetDateTime.now());
		
		fixture.given(new ServiceSpecExpurgedEvent(aggregateId, exSpecification),
				new StatusVerifiedEvent(aggregateId, cfsId, ServiceSpecLifeCycleEnum.from(lifecycleStatus),
						ServiceSpecLifeCycleEnum.from(lifecycleStatus)),
				new ServiceSpecReplicatedEvent(aggregateId, exSpecification),
				new ServiceSpecNotificationSentEvent(aggregateId, exSpecification))
				.when(new ServiceSpecAttributeValueChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification));

	}

	@Test
	void updateCfsWhenCfsStatusModifiedEventReceive() {
		String cfsId = aggregateId;
		String lifecycleStatus = "active";

		OffsetDateTime time = OffsetDateTime.now();

		serviceSpecification.setLifecycleStatus("launched");
		serviceSpecification.setLastUpdate(time);
		event.setEvent(serviceSpecification);
		event.setTimeOcurred(time);

		ServiceSpecification exSpecification = new ServiceSpecification();
		exSpecification.setId("1");
		exSpecification.setLifecycleStatus(lifecycleStatus);
		exSpecification.setLastUpdate(event.getTimeOcurred().minusMonths(1));

		fixture.given(new ServiceSpecExpurgedEvent(aggregateId, exSpecification),
				new StatusVerifiedEvent(aggregateId, cfsId, ServiceSpecLifeCycleEnum.from(lifecycleStatus),
						ServiceSpecLifeCycleEnum.from(lifecycleStatus)),
				new ServiceSpecReplicatedEvent(aggregateId, exSpecification),
				new ServiceSpecNotificationSentEvent(aggregateId, exSpecification))
				.when(new ServiceSpecAttributeValueChangeCommand(aggregateId, event))
				.expectEvents(new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification),
						new StatusVerifiedEvent(aggregateId, cfsId,
								ServiceSpecLifeCycleEnum.from(serviceSpecification.getLifecycleStatus()),
								ServiceSpecLifeCycleEnum.from(lifecycleStatus)),
						new ServiceSpecStatusUpdatedEvent(aggregateId, cfsId,
								ServiceSpecLifeCycleEnum.from(serviceSpecification.getLifecycleStatus()),
								serviceSpecification.getLastUpdate()));

	}

	@Test
	void updateCfsWhenNotAlreadyCfsExistedEventReceive() {
		fixture.given(new ServiceSpecExpurgedEvent(aggregateId, existingServiceSpecification),
				new StatusVerifiedEvent(aggregateId, existingServiceSpecification.getId(),
						ServiceSpecLifeCycleEnum.from(existingServiceSpecification.getLifecycleStatus()), null),
				new ServiceSpecReplicatedEvent(aggregateId, existingServiceSpecification),
				new ServiceSpecNotificationSentEvent(aggregateId, existingServiceSpecification))
				.when(new ServiceSpecAttributeValueChangeCommand(aggregateId, event)).expectEvents();

	}

}