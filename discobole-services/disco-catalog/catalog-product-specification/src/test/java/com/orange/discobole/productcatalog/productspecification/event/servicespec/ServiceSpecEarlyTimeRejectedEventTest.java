// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecEarlyTimeRejectedEvent;

class ServiceSpecEarlyTimeRejectedEventTest {

    private String cfsId;
    private OffsetDateTime newTimeOccurred;
    private OffsetDateTime oldTimeOccurred;

    @BeforeEach
    void setUp() {
        cfsId = "1";
        LocalDateTime dateTime = LocalDateTime.now();
        newTimeOccurred = OffsetDateTime.now();
        oldTimeOccurred = OffsetDateTime.now().plusHours(1L);
    }

    @Test
    void serviceSpecEarlyTimeRejectedTest() {
        ServiceSpecEarlyTimeRejectedEvent earlyTimeRejectedEvent = new ServiceSpecEarlyTimeRejectedEvent(cfsId, newTimeOccurred, oldTimeOccurred);
        Assertions.assertEquals(cfsId, earlyTimeRejectedEvent.getCfsId());
		Assertions.assertEquals(newTimeOccurred, earlyTimeRejectedEvent.getNewTimeOccurred());
        Assertions.assertEquals(oldTimeOccurred, earlyTimeRejectedEvent.getOldTimeOccurred());
    }

}