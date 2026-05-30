// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;


public class FalloutMappingException extends FalloutException implements FalloutKafkaException {

    private final Class<?> entityName;
    private final Class<?> targetEntityName;

    public FalloutMappingException(String message, Class<?> entityName, Class<?> targetEntityName, Exception e) {
        super(message, e);
        this.entityName = entityName;
        this.targetEntityName = targetEntityName;
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
        return "FALLOUT_MAPPING_EXCEPTION | entityName: {%s} | targetEntityName: {%s} | message: {%s}".formatted(entityName.getName(), targetEntityName.getName(),
                super.getMessage());
    }
}
