// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.outbox.creator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.orderorchestration.outbox.internal.CDCEventInterface;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface EventEntityCreator<T, E extends Enum<E> & CDCEventInterface> {
    EventEntity create(T t, Map<String, String> headers) throws JsonProcessingException;

    E getCDCEvent();
}
