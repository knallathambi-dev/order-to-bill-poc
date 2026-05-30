// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.projection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.*;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
public class CfsSpecProjectorTest {
    private String cfsId;
    private String lifeCycleStatus;
    private ServiceSpecProjector serviceSpecProjector = null;
    private ServiceSpecification serviceSpecification = null;


    @MockBean
    private LinkDiscoverers linkDiscoverers;

    private StreamBridge bridge;

    @BeforeEach
    void setUp() {
        cfsId = "1";
        lifeCycleStatus = "active";
        serviceSpecification = new ServiceSpecification();
        serviceSpecification.setId(cfsId);
        serviceSpecification.setLifecycleStatus(lifeCycleStatus);
        serviceSpecification.setLastUpdate(OffsetDateTime.now());
        serviceSpecProjector = new ServiceSpecProjector();
        bridge = Mockito.mock(StreamBridge.class);
        ReflectionTestUtils.setField(serviceSpecProjector, "bridge", bridge);
    }

    @Test
    void handleServiceSpecReplicatedEvent() {
        serviceSpecProjector.handle(new ServiceSpecReplicatedEvent(serviceSpecification.getId(), serviceSpecification));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleServiceSpecNotificationSentEvent() {
        serviceSpecProjector.handle(new ServiceSpecNotificationSentEvent(serviceSpecification.getId(), serviceSpecification));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleServiceSpecDuplicatedEvent() {
        serviceSpecProjector.handle(new ServiceSpecDuplicatedEvent(serviceSpecification));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleCurrentServiceSpecNotAlreadyExistedEvent() {
        serviceSpecProjector.handle(new CurrentServiceSpecNotAlreadyExistedEvent(serviceSpecification));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleServiceSpecEarlyTimeRejectedEvent() {
        serviceSpecProjector.handle(new ServiceSpecEarlyTimeRejectedEvent(cfsId,
                OffsetDateTime.now().minusHours(1L), OffsetDateTime.now()));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleInvalidStatusReceivedEvent() {
        lifeCycleStatus = "launched";
        serviceSpecProjector.handle(new InvalidStatusReceivedEvent(cfsId, ServiceSpecLifeCycleEnum.from(lifeCycleStatus), ServiceSpecLifeCycleEnum.from("active")));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleServiceSpecStatusUpdated() {
        serviceSpecProjector.handle(new ServiceSpecStatusUpdatedEvent(serviceSpecification.getId(), cfsId,
                ServiceSpecLifeCycleEnum.from(lifeCycleStatus), OffsetDateTime.now()));

        assertNotNull(serviceSpecProjector);
    }

    @Test
    void handleServiceSpecAttributeValueChange() {
        serviceSpecProjector.handle(new ServiceSpecAttributeUpdatedEvent(serviceSpecification.getId(), serviceSpecification));

        assertNotNull(serviceSpecProjector);
    }
}