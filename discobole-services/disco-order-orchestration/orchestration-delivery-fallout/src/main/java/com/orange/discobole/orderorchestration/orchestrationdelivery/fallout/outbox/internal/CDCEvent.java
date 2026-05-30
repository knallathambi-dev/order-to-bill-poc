// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CDCEvent {
    FALLOUT_STATE_CHANGE_EVENT(EventType.FALLOUT_STATE_CHANGE_EVENT, "disco.order-orchestration-fallout.falloutIncidentStateChange-event");

    private final EventType type;

    private final String topicName;
}
