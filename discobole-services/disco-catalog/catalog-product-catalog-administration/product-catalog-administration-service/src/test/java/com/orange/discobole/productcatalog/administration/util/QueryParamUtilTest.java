// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.util;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;

import java.security.InvalidParameterException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryParamUtilTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MongoConverter mongoConverter;

    @Mock
    private AggregationResults<Document> aggregationResults;

    // ---------------- Dummy Entity ----------------
    static class TestEntity {
        private String name;
        private Boolean active;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }

    // ---------------- buildSort ----------------

    @Test
    void buildSort_whenNoSortParam_shouldReturnNull() {
        Map<String, Object> params = new HashMap<>();
        SortOperation result = QueryParamUtil.buildSort(params, TestEntity.class);
        assertNull(result);
    }

    @Test
    void buildSort_whenValidAscSort_shouldReturnSortOperation() {
        Map<String, Object> params = Map.of("sort", "name");
        SortOperation result = QueryParamUtil.buildSort(params, TestEntity.class);
        assertNotNull(result);
    }

    @Test
    void buildSort_whenValidDescSort_shouldReturnSortOperation() {
        Map<String, Object> params = Map.of("sort", "-name");
        SortOperation result = QueryParamUtil.buildSort(params, TestEntity.class);
        assertNotNull(result);
    }

    @Test
    void buildSort_whenInvalidField_shouldThrowException() {
        Map<String, Object> params = Map.of("sort", "invalid");
        assertThrows(InvalidParameterException.class,
                () -> QueryParamUtil.buildSort(params, TestEntity.class));
    }

    // ---------------- sortParameters ----------------

    @Test
    void sortParameters_whenAsc_shouldCreateAscOrder() {
        Set<String> params = Set.of("name");
        List<Order> orders = QueryParamUtil.sortParameters(TestEntity.class, params);
        assertEquals(1, orders.size());
        assertEquals(Sort.Direction.ASC, orders.get(0).getDirection());
    }

    @Test
    void sortParameters_whenDesc_shouldCreateDescOrder() {
        Set<String> params = Set.of("-name");
        List<Order> orders = QueryParamUtil.sortParameters(TestEntity.class, params);
        assertEquals(1, orders.size());
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());
    }

    @Test
    void sortParameters_whenInvalidField_shouldThrowException() {
        Set<String> params = Set.of("invalid");
        assertThrows(InvalidParameterException.class,
                () -> QueryParamUtil.sortParameters(TestEntity.class, params));
    }

    // ---------------- isValid ----------------

    @Test
    void isValid_whenFieldExists_shouldReturnTrue() {
        assertTrue(QueryParamUtil.isValid(TestEntity.class, "name"));
    }

    @Test
    void isValid_whenFieldDoesNotExist_shouldReturnFalse() {
        assertFalse(QueryParamUtil.isValid(TestEntity.class, "missing"));
    }

    // ---------------- mapper ----------------

    @Test
    void mapper_whenCommaSeparatedString_shouldSplitValues() {
        Map<String, Object> params = Map.of("name", "a,b");
        Map<String, Set<Object>> result = QueryParamUtil.mapper(params);
        assertEquals(Set.of("a", "b"), result.get("name"));
    }

    @Test
    void mapper_whenNonStringValue_shouldAddDirectly() {
        Map<String, Object> params = Map.of("active", true);
        Map<String, Set<Object>> result = QueryParamUtil.mapper(params);
        assertEquals(Set.of(true), result.get("active"));
    }

    @Test
    void mapper_shouldIgnoreSortParam() {
        Map<String, Object> params = Map.of("sort", "name");
        Map<String, Set<Object>> result = QueryParamUtil.mapper(params);
        assertTrue(result.isEmpty());
    }

    // ---------------- buildCriteria ----------------

    @Test
    void buildCriteria_whenEmptyParams_shouldReturnNull() {
        Criteria criteria = QueryParamUtil.buildCriteria(new HashMap<>());
        assertNull(criteria);
    }

    @Test
    void buildCriteria_whenBooleanValue_shouldCreateBooleanCriteria() {
        Map<String, Object> params = Map.of("active", "true");
        Criteria criteria = QueryParamUtil.buildCriteria(params);
        assertNotNull(criteria);
    }

    @Test
    void buildCriteria_whenMultipleValues_shouldUseInClause() {
        Map<String, Object> params = Map.of("name", "a,b");
        Criteria criteria = QueryParamUtil.buildCriteria(params);
        assertNotNull(criteria);
    }

    // ---------------- applyDefaultCriteria ----------------

    @Test
    void applyDefaultCriteria_whenBooleanString_shouldUseBooleanIs() {
        Criteria criteria = QueryParamUtil.applyDefaultCriteria(
                new Criteria(), "active", Set.of("true"), false);
        assertNotNull(criteria);
    }

    @Test
    void applyDefaultCriteria_whenSingleValue_shouldUseInOrIs() {
        Criteria criteria = QueryParamUtil.applyDefaultCriteria(
                new Criteria(), "name", Set.of("abc"), false);
        assertNotNull(criteria);
    }

    // ---------------- fetchEntityFilteredFacet ----------------

    @Test
    void fetchEntityFilteredFacet_whenNoFilters_shouldCreateFacetAggregation() {
        Aggregation aggregation = QueryParamUtil.fetchEntityFilteredFacet(new HashMap<>(), TestEntity.class);
        assertNotNull(aggregation);
    }

    @Test
    void fetchEntityFilteredFacet_withSortAndFilter_shouldCreateAggregation() {
        Map<String, Object> params = Map.of("name", "abc", "sort", "name");
        Aggregation aggregation = QueryParamUtil.fetchEntityFilteredFacet(params, TestEntity.class);
        assertNotNull(aggregation);
    }

    // ---------------- fetchEntityMap ----------------


    @Test
    void fetchEntityMap_whenDataAndCountPresent_shouldReturnValues() {
        doReturn(mongoConverter).when(mongoTemplate).getConverter();

        Document doc = new Document("name", "abc");
        Document countDoc = new Document("count", 1);
        Document mongoResult = new Document();
        mongoResult.put("data", List.of(doc));
        mongoResult.put("count", List.of(countDoc));

        lenient().when(mongoTemplate.aggregate(any(Aggregation.class), anyString(), eq(Document.class)))
                .thenReturn(aggregationResults);
        lenient().when(aggregationResults.getUniqueMappedResult()).thenReturn(mongoResult);
        lenient().when(mongoConverter.read(eq(TestEntity.class), any(Document.class)))
                .thenReturn(new TestEntity());

        Map<String, Object> result =
                QueryParamUtil.fetchEntityMap(new HashMap<>(), "test", TestEntity.class, mongoTemplate);

        assertEquals(1L, result.get("count"));
        assertEquals(1, ((List<?>) result.get("data")).size());
    }


}
