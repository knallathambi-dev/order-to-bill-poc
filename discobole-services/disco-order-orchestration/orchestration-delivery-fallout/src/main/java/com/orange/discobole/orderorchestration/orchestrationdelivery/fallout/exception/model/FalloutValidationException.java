// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;


public class FalloutValidationException extends FalloutException implements FalloutKafkaException {

    protected FalloutValidationException(String message) {
        super(message);
    }

    public String getLogMessage() {
        return "FALLOUT_VALIDATION_EXCEPTION | message: {%S} ".formatted(getMessage());
    }

    @Override
    public boolean getIsHandled() {
        return this.isHandled;
    }

    @Override
    public void setIsHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }
}
