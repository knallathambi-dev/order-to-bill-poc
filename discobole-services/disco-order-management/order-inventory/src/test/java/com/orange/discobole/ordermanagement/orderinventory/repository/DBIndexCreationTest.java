// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.repository;

import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexField;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@IntegrationTest
class DBIndexCreationTest {
    @Autowired
    protected MongoTemplate mongoTemplate;

    @Test
    void givenDB_whenProductOrderCollectionIsCreated_thenIndexesWasCreated() {
        Set<Set<String>> fieldsToCreateFor = new HashSet<>(Set.of(
                Set.of("_id"),
                Set.of("creationDate"),
                Set.of("state"),
                Set.of("state", "creationDate"),
                Set.of("channel.channel.name"),
                Set.of("relatedParty.partyOrPartyRole.id"),
                Set.of("relatedParty.partyOrPartyRole.name")
        ));

        List<Set<String>> indexFieldSets = this.mongoTemplate.indexOps(ProductOrderEntity.class).getIndexInfo().stream()
                .map(indexInfo -> indexInfo.getIndexFields().stream()
                        .map(IndexField::getKey)
                        .collect(Collectors.toSet()))
                .toList();

        indexFieldSets.forEach(fieldsToCreateFor::remove);

        Assertions.assertThat(fieldsToCreateFor).isEmpty();
    }
}
