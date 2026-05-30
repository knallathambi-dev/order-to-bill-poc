// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.predicts.FalloutPredict;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FalloutPredictTest {

    @Test
    void givenFalloutFilter_whenCreateFilterQuery_thenReturnsExpectedQuery() {
        // Given
        FalloutFilter falloutFilter = new FalloutFilter();
        falloutFilter.setId("123");
        falloutFilter.setState("active");
        falloutFilter.setCreationDateGte(OffsetDateTime.now().minusDays(1));
        falloutFilter.setCreationDateLte(OffsetDateTime.now());
        falloutFilter.setLastModifiedDateGte(OffsetDateTime.now().minusDays(2));
        falloutFilter.setLastModifiedDateLte(OffsetDateTime.now().minusDays(1));
        falloutFilter.setRelatedPartyId("party123");
        falloutFilter.setRelatedPartyRole("role123");
        falloutFilter.setRelatedPartyName("name123");
        falloutFilter.setRelatedEntityId("entity123");
        falloutFilter.setRelatedEntityState("state123");

        // When
        Query query = FalloutPredict.createFilterQuery(falloutFilter);

        // Then
        assertNotNull(query);
        assertTrue(query.getQueryObject().containsKey("id"));
        assertTrue(query.getQueryObject().containsKey("state"));
        assertTrue(query.getQueryObject().containsKey("creationDate"));
        assertTrue(query.getQueryObject().containsKey("modificationDate"));
        assertTrue(query.getQueryObject().containsKey("relatedParty.id"));
        assertTrue(query.getQueryObject().containsKey("relatedParty.role"));
        assertTrue(query.getQueryObject().containsKey("relatedParty.name"));
        assertTrue(query.getQueryObject().containsKey("relatedEntity.id"));
        assertTrue(query.getQueryObject().containsKey("relatedEntity.state"));
    }

    @Test
    void givenIdAndFields_whenCreateFilterQueryById_thenReturnsExpectedQuery() {
        // Given
        String id = "123";
        String fields = "field1,field2";

        // When
        Query query = FalloutPredict.createFilterQueryById(id, fields);

        // Then
        assertNotNull(query);
        assertTrue(query.getQueryObject().containsKey("id"));
        assertTrue(query.getFieldsObject().containsKey("field1"));
        assertTrue(query.getFieldsObject().containsKey("field2"));
    }

    @Test
    void givenOffsetAndLimit_whenSetFalloutPageable_thenQueryIsPaginated() {
        // Given
        Query query = new Query();
        Integer offset = 10;
        Integer limit = 5;

        // When
        FalloutPredict.setFalloutPageable(query, offset, limit);

        // Then
        assertEquals(10, query.getSkip());
        assertEquals(5, query.getLimit());
    }

    @Test
    void givenNullOffsetAndLimit_whenSetFalloutPageable_thenQueryIsNotPaginated() {
        // Given
        Query query = new Query();
        Integer offset = null;
        Integer limit = null;

        // When
        FalloutPredict.setFalloutPageable(query, offset, limit);

        // Then
        assertEquals(0, query.getSkip());
        assertEquals(0, query.getLimit());
    }
}

