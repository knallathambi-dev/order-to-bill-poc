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
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelatedPartyMapperTest {

    private RelatedPartyMapper relatedPartyMapper;

    @BeforeEach
    void setUp() {
        relatedPartyMapper = Mappers.getMapper(RelatedPartyMapper.class);
    }

    @Test
    void givenRelatedPartyWithHref_whenMap_thenCorrectlyMapsToRelatedPartyWithContactInfo() {
        // Given
        RelatedParty relatedParty = Instancio.create(RelatedParty.class);

        // When
        RelatedPartyWithContactInfo result = relatedPartyMapper.map(relatedParty);

        // Then
        assertNotNull(result, "Resulting object should not be null");
        assertRelatedPartyModelMapped(result, relatedParty);
    }

    @Test
    void givenRelatedPartyWithNullHref_whenMap_thenHrefIsNullInRelatedPartyWithContactInfo() {
        // Given
        RelatedParty relatedParty = Instancio.create(RelatedParty.class);
        relatedParty.setHref(null);

        // When
        RelatedPartyWithContactInfo result = relatedPartyMapper.map(relatedParty);

        // Then
        assertRelatedPartyModelMapped(result, relatedParty);

    }

    private static void assertRelatedPartyModelMapped(RelatedPartyWithContactInfo result, RelatedParty relatedParty) {
        assertNotNull(result, "Resulting object should not be null");
        if (relatedParty.getHref() == null) {
            assertNull(result.getHref());
        } else {
            assertEquals(URI.create(relatedParty.getHref()), result.getHref(), "Href should be correctly mapped");
        }
        assertEquals(relatedParty.getId(), result.getId(), "Id should be correctly mapped");
        assertEquals(relatedParty.getName(), result.getName(), "Name should be correctly mapped");
        assertEquals(relatedParty.getRole(), result.getRole(), "Role should be correctly mapped");
    }

    @Test
    void givenListOfRelatedParties_whenMap_thenCorrectlyMapsToListOfRelatedPartyWithContactInfo() {
        // Given
        RelatedParty relatedParty1 = Instancio.create(RelatedParty.class);

        RelatedParty relatedParty2 = Instancio.create(RelatedParty.class);

        List<RelatedParty> relatedParties = Arrays.asList(relatedParty1, relatedParty2);

        // When
        List<RelatedPartyWithContactInfo> result = relatedPartyMapper.map(relatedParties);

        // Then
        assertNotNull(result, "Resulting list should not be null");
        assertEquals(2, result.size(), "Resulting list should have two elements");
        assertRelatedPartyModelMapped(result.get(0), relatedParty1);
        assertRelatedPartyModelMapped(result.get(1), relatedParty2);

    }
}
