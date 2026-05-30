// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum State {
    INITIAL_AUTOMATED_TASK("InitialAutomatedTask"),
    INITIAL_CHOICE("InitialChoice"),
    CREATED("Created"),
    ANALYSING("Analysing"),
    HELD("Held"),
    COMPLETE("Complete"),
    COMPLETED("Completed"),
    CANCELED("Canceled");

    private final String value;

    public static State fromValue(String value) {
        for (State state : State.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
