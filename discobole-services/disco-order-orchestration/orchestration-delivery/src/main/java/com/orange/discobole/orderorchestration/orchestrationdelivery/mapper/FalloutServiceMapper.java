// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface FalloutServiceMapper {

    @Mapping(target = "schemaLocation", ignore = true)
    RelatedParty toRelatedParty(com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty relatedParty);

}
