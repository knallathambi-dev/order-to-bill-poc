// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.RelatedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FalloutMapper {

    @Mapping(target = "role", source = "role.value")
    RelatedEntity toDto(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.RelatedEntity relatedEntity);

    @Mapping(target = "resolution", source = "resolution")
    @Mapping(target = "state", source = "state.value")
    FalloutIncident toDto(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident fallout);

    List<FalloutIncident> toDtoList(List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident> fallout);

}
