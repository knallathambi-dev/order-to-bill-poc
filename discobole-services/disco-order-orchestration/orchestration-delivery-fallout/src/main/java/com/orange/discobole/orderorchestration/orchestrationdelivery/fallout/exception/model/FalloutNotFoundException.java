// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;


import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode;

public class FalloutNotFoundException extends FalloutException implements FalloutKafkaException {

    private final Class<?> entityClass;
    private final String id;

    public FalloutNotFoundException(String message, Class<?> entityClass, String id) {
        super(message);
        this.code = ExceptionCode.FALLOUT_NOT_FOUND_EXCEPTION;
        this.entityClass = entityClass;
        this.id = id;
    }

    @Override
    public boolean getIsHandled() {
        return this.isHandled;
    }

    @Override
    public void setIsHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }

    public String getLogMessage() {
        return "FALLOUT_NOT_FOUND_EXCEPTION | message: {%S} | entity: {%S} | id: {id}".formatted(getMessage(), entityClass.getName(), this.id);
    }
}
