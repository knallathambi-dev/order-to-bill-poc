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

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.RelatedPartyWithContactInfo;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderRelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface RelatedPartyMapper {

    @Mapping(target = "href", source = "href", qualifiedByName = "mapHref")
    RelatedPartyWithContactInfo map(RelatedParty relatedParty);

    List<RelatedPartyWithContactInfo> map(List<RelatedParty> relatedParties);

    List<ServiceOrderRelatedParty> mapToServiceOrderRelatedParty(List<RelatedParty> relatedParties);

    @Named("mapHref")
    default URI mapHref(String href) {
        return Objects.nonNull(href) ? URI.create(href) : null;
    }

}
