// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;

import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.util.AbstractTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.index.IndexField;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class DBIndexCreationTest extends AbstractTest {

    @Test
    void givenDB_whenProductCollectionIsCreated_thenIndexesWasCreated() {
        Set<Set<String>> fieldsToCreateFor = new HashSet<>(Set.of(
                Set.of("_id"),
                Set.of("productOrderItem.productOrderId"),
                Set.of("relatedParty._id"),
                Set.of("startDate"),
                Set.of("status"),
                Set.of("operationalStatus"),
                Set.of("productCharacteristic.name"),
                Set.of("productOffering._id"),
                Set.of("productOffering.atType"),
                Set.of("externalIdentifier._id"),
                Set.of("productSpecification.atType"),
                Set.of("productRelationship.product._id"),
                Set.of("status", "terminationDate"), // active_termination_date_index
                Set.of("productCharacteristic.name", "productCharacteristic.value"), // product_characteristic_index
                Set.of("relatedParty._id", "productOffering.atType"), // relatedParty_productOffering_contract_index
                Set.of("isRootProduct", "startDate") // is_root_product_start_date
        ));

        List<Set<String>> indexFieldSets = this.mongoTemplate.indexOps(ProductEntity.class).getIndexInfo().stream()
                .map(indexInfo -> indexInfo.getIndexFields().stream()
                        .map(IndexField::getKey)
                        .collect(Collectors.toSet()))
                .toList();

        indexFieldSets.forEach(fieldsToCreateFor::remove);

        Assertions.assertThat(fieldsToCreateFor).isEmpty();
    }


    @Test
    void givenDB_whenScheduledJobEntityCollectionIsCreated_thenIndexesWasCreated() {
        Set<String> fieldsToCreateFor = new HashSet<>(
                Set.of("status"));
        List<IndexInfo> indexInfoList = this.mongoTemplate.indexOps(JobEntity.class).getIndexInfo();
        for (IndexInfo indexInfo : indexInfoList) {
            for (IndexField indexField : indexInfo.getIndexFields()) {
                fieldsToCreateFor.remove(indexField.getKey());
            }
        }
        Assertions.assertThat(fieldsToCreateFor).isEmpty();
    }
}
