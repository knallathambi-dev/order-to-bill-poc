// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceSpecificationMapper {
    List<ServiceSpecification> toServiceSpecification(List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ServiceSpecification> serviceSpecification);
}
