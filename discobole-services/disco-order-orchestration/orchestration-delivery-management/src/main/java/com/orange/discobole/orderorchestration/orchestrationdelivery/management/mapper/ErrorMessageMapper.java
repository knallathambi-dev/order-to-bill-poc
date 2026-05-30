// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;


import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItemErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ErrorMessageMapper {

    default OrchestrationNodeErrorMessage map(Characteristic productCharacteristic) {
        return OrchestrationNodeErrorMessage.builder()
                .message(String.format("Service specification characteristic {%s} was not found in the service catalog", productCharacteristic.getName()))
                .reason(String.format("Service specification characteristic {%s}'s id is needed for the service order request creation",
                        productCharacteristic.getName()))
                .code("SERVICE_SPEC_CHARACTERISTICS_NOT_FOUND")
                .timeStamp(Instant.now()).build();
    }

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "mapTimestamp")
    CoodError map(ServiceOrderItemErrorMessage serviceOrderItemErrorMessage);

    default CoodError map(Throwable exception) {
        if (exception instanceof CoodException coodException) {
            return coodException.getCoodError();
        }

        return new CoodTechnicalException(exception.getMessage()).getCoodError();
    }

    @Named("mapTimestamp")
    default Instant mapTimestamp(String timestampString) {
        if (StringUtils.isNoneBlank(timestampString)) {
            return Instant.parse(timestampString);
        }
        return null;
    }
}
