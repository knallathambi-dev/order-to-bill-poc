// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.creator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventEntity;
import org.springframework.stereotype.Component;

@Component
public interface EventEntityCreator<T> {
    EventEntity create(T t) throws JsonProcessingException;

    CDCEvent getCDCEvent();
}
