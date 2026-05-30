// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.leadtimestatistics;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.ContractLeadTimeStatisticsResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.response.NodeLeadTimeStatisticsResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.LeadTimeHistorySampledStatistics;

import java.time.Instant;
import java.util.List;

import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.ContractLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.ContractLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.LeadTimeHistorySample;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.dto.v1.NodeLeadTimeHistoryStatistics;
import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadTimeStatisticsServiceImpl implements LeadTimeStatisticsService {

    // Tune as needed
    private static final int APPROX_BATCH_SIZE = 100;

    /**
     * Maximum sample limit to prevent memory exhaustion.
     * Approximate counting operations are performed in-memory, requiring
     * a cap on the dataset size to ensure system stability.
     */
    private static final int APPROX_COUNT_MAX_RECORD_SIZE = 1000;

    private final MongoTemplateWrapperService mongo;

    // ----------------------- Node -----------------------

    @Override
    public NodeLeadTimeStatisticsResponse getNodeLeadTimeHistoryStatistics(
            String productSpecId,
            String deliveryFactoryName,
            boolean includeMin,
            boolean includeMax,
            boolean includeAvg,
            Instant timePeriodStart,
            Instant timePeriodEnd,
            Integer approximateCount,
            List<String> responseProjectionFields,
            int offset,
            int limit,
            String sortSampleWindow
    ) {
        try {
            Criteria criteria = null;
            if (productSpecId != null) {
                criteria = Criteria.where(NodeLeadTimeHistorySampledStatistics.Fields.productSpecId).is(productSpecId);
            } else {
                criteria = Criteria.where(NodeLeadTimeHistorySampledStatistics.Fields.deliveryFactoryName).is(deliveryFactoryName);
            }

            Query projection = buildProjection(includeMin, includeMax, includeAvg); // include sampleSize

            final boolean byTimeWindow = timePeriodStart != null && timePeriodEnd != null;
            final List<NodeLeadTimeHistorySampledStatistics> pageItems;
            final int totalCount;
            final Sort sort = toSpringSort(sortSampleWindow);

            if (byTimeWindow) {
                Criteria timeCriteria = criteria.and(LeadTimeHistorySampledStatistics.Fields.sampleWindow).gte(timePeriodStart).lt(timePeriodEnd);
                Query countQ = new Query(timeCriteria);
                totalCount = Math.toIntExact(mongo.count(countQ, NodeLeadTimeHistorySampledStatistics.class));

                Query q = new Query(timeCriteria);
                applySort(q, sort);
                applyPagination(q, offset, limit);
                applyProjection(q, projection);
                pageItems = mongo.find(q, NodeLeadTimeHistorySampledStatistics.class);
            } else {
                // approximateCount mode: collect latest samples until cumulative sampleSize >= approximateCount
                List<NodeLeadTimeHistorySampledStatistics> candidates =
                        collectUntilSampleSize(
                                criteria,
                                projection,
                                approximateCount,
                                NodeLeadTimeHistorySampledStatistics.class
                        );

                // Total before pagination is the number of documents included to reach the threshold
                totalCount = candidates.size();

                // Re-sort by requested sort (default ASC by sampleWindow) and page in-memory
                List<NodeLeadTimeHistorySampledStatistics> sorted = resortInMemory(candidates, sort);
                pageItems = pageInMemory(sorted, offset, limit);
            }

            NodeLeadTimeHistoryStatistics body = new NodeLeadTimeHistoryStatistics();

            // apply response fields projection
            if (responseProjectionFields.contains(NodeLeadTimeHistoryStatistics.Fields.productSpecId)) {
                body.setProductSpecId(productSpecId);
            }
            if (deliveryFactoryName != null && !deliveryFactoryName.isBlank() && responseProjectionFields.contains(NodeLeadTimeHistoryStatistics.Fields.deliveryFactoryName)) {
                body.setDeliveryFactoryName(deliveryFactoryName);
            }
            if (responseProjectionFields.contains(NodeLeadTimeHistoryStatistics.Fields.statistics)) {
                body.setStatistics(pageItems.stream().map(doc -> mapSample(doc, includeMin, includeMax, includeAvg)).collect(Collectors.toList()));
            }

            int resultCount = pageItems.size();

            return NodeLeadTimeStatisticsResponse.builder()
                    .body(body)
                    .resultCount(resultCount)
                    .totalCount(totalCount)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (CoodDBException e) {
            log.error("Error fetching node lead time statistics", e);
            return NodeLeadTimeStatisticsResponse.builder()
                    .body(null)
                    .resultCount(0)
                    .totalCount(0)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error fetching node lead time statistics", e);
            return NodeLeadTimeStatisticsResponse.builder()
                    .body(null)
                    .resultCount(0)
                    .totalCount(0)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // ----------------------- Contract -----------------------

    @Override
    public ContractLeadTimeStatisticsResponse getContractLeadTimeHistoryStatistics(
            String contractName,
            boolean includeMin,
            boolean includeMax,
            boolean includeAvg,
            Instant timePeriodStart,
            Instant timePeriodEnd,
            Integer approximateCount,
            List<String> responseProjectionFields,
            int offset,
            int limit,
            String sortSampleWindow
    ) {
        try {
            Criteria criteria = Criteria.where(ContractLeadTimeHistorySampledStatistics.Fields.contractName).is(contractName);
            Query projection = buildProjection(includeMin, includeMax, includeAvg); // include sampleSize

            final boolean byTimeWindow = timePeriodStart != null && timePeriodEnd != null;
            final List<ContractLeadTimeHistorySampledStatistics> pageItems;
            final int totalCount;
            final Sort sort = toSpringSort(sortSampleWindow);

            if (byTimeWindow) {
                Criteria timeCriteria = criteria.and(LeadTimeHistorySampledStatistics.Fields.sampleWindow).gte(timePeriodStart).lt(timePeriodEnd);
                Query countQ = new Query(timeCriteria);
                totalCount = Math.toIntExact(mongo.count(countQ, ContractLeadTimeHistorySampledStatistics.class));

                Query q = new Query(timeCriteria);
                applySort(q, sort);
                applyPagination(q, offset, limit);
                applyProjection(q, projection);
                pageItems = mongo.find(q, ContractLeadTimeHistorySampledStatistics.class);
            } else {
                // approximateCount mode: collect latest samples until cumulative sampleSize >= approximateCount
                List<ContractLeadTimeHistorySampledStatistics> candidates =
                        collectUntilSampleSize(
                                criteria,
                                projection,
                                approximateCount,
                                ContractLeadTimeHistorySampledStatistics.class
                        );

                // Total before pagination is the number of documents included to reach the threshold
                totalCount = candidates.size();

                // Re-sort by requested sort (default ASC by sampleWindow) and page in-memory
                List<ContractLeadTimeHistorySampledStatistics> sorted = resortInMemory(candidates, sort);
                pageItems = pageInMemory(sorted, offset, limit);
            }

            ContractLeadTimeHistoryStatistics body = new ContractLeadTimeHistoryStatistics();

            // apply response fields projection
            if (responseProjectionFields.contains(ContractLeadTimeHistoryStatistics.Fields.contractName)) {
                body.setContractName(contractName);
            }
            if (responseProjectionFields.contains(ContractLeadTimeHistoryStatistics.Fields.statistics)) {
                body.setStatistics(pageItems.stream().map(doc -> mapSample(doc, includeMin, includeMax, includeAvg)).collect(Collectors.toList()));
            }

            int resultCount = pageItems.size();

            return ContractLeadTimeStatisticsResponse.builder()
                    .body(body)
                    .resultCount(resultCount)
                    .totalCount(totalCount)
                    .httpStatus(HttpStatus.OK)
                    .build();

        } catch (CoodDBException e) {
            log.error("Error fetching contract lead time statistics", e);
            return ContractLeadTimeStatisticsResponse.builder()
                    .body(null)
                    .resultCount(0)
                    .totalCount(0)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error fetching contract lead time statistics", e);
            return ContractLeadTimeStatisticsResponse.builder()
                    .body(null)
                    .resultCount(0)
                    .totalCount(0)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // ----------------------- Helpers -----------------------

    private Query buildProjection(boolean includeMin, boolean includeMax, boolean includeAvg) {
        Query q = new Query();
        // Always include sampleWindow
        q.fields().include(LeadTimeHistorySampledStatistics.Fields.sampleWindow);
        q.fields().include(LeadTimeHistorySampledStatistics.Fields.sampleSize);
        if (includeMin) {
            q.fields().include(LeadTimeHistorySampledStatistics.Fields.minActualLeadTime);
        }
        if (includeMax) {
            q.fields().include(LeadTimeHistorySampledStatistics.Fields.maxActualLeadTime);
        }
        if (includeAvg) {
            q.fields().include(LeadTimeHistorySampledStatistics.Fields.averageActualLeadTime);
        }
        return q;
    }

    private void applyProjection(Query target, Query projection) {
        if (projection == null) {
            return;
        }
        // Spring doesn't expose a direct merge; we copy fields to target
        projection.getFieldsObject().forEach((k, v) -> target.fields().include(k));
    }

    private void applySort(Query q, Sort sort) {
        if (sort != null && sort.isSorted()) {
            q.with(sort);
        } else {
            q.with(Sort.by(Sort.Direction.ASC, LeadTimeHistorySampledStatistics.Fields.sampleWindow));
        }
    }

    private void applyPagination(Query q, int offset, int limit) {
        if (offset > 0) {
            q.skip(offset);
        }
        if (limit > 0) {
            q.limit(limit);
        }
    }

    private Sort toSpringSort(String sortSampleWindow) {
        if (sortSampleWindow == null) {
            return Sort.by(Sort.Direction.ASC, LeadTimeHistorySampledStatistics.Fields.sampleWindow);
        }
        // Only sampleWindow is supported; take the first item if multiple
        boolean desc = sortSampleWindow.startsWith("-");
        return Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, LeadTimeHistorySampledStatistics.Fields.sampleWindow);
    }

    private <T extends LeadTimeHistorySampledStatistics> List<T> resortInMemory(List<T> items, Sort sort) {
        if (items == null || items.size() <= 1) {
            return items;
        }

        boolean desc = sort != null
                && sort.isSorted()
                && sort.getOrderFor(LeadTimeHistorySampledStatistics.Fields.sampleWindow) != null
                && Objects.requireNonNull(sort.getOrderFor(LeadTimeHistorySampledStatistics.Fields.sampleWindow)).isDescending();

        Comparator<T> cmp = Comparator.comparing(
                LeadTimeHistorySampledStatistics::getSampleWindow,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        items.sort(desc ? cmp.reversed() : cmp);
        return items;
    }

    private <T extends LeadTimeHistorySampledStatistics> List<T> pageInMemory(List<T> items, int offset, int limit) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        int from = Math.min(offset, items.size());
        int to = Math.min(from + limit, items.size());
        if (from >= to) {
            return Collections.emptyList();
        }
        return new ArrayList<>(items.subList(from, to));
    }

    // Map Sampled Statistics doc -> API sample
    private LeadTimeHistorySample mapSample(LeadTimeHistorySampledStatistics doc,
                                            boolean includeMin, boolean includeMax, boolean includeAvg) {
        LeadTimeHistorySample s = new LeadTimeHistorySample();
        s.setSampleWindow(doc.getSampleWindow().atOffset(java.time.ZoneOffset.UTC));
        if (includeMin) {
            s.setMinActualLeadTime(doc.getMinActualLeadTime());
        }
        if (includeMax) {
            s.setMaxActualLeadTime(doc.getMaxActualLeadTime());
        }
        if (includeAvg) {
            s.setAverageActualLeadTime(doc.getAverageActualLeadTime());
        }
        s.setSampleSize(doc.getSampleSize());
        return s;
    }

    /**
     * Collects latest documents (sorted by sampleWindow DESC) until the cumulative sum of sampleSize
     * reaches or exceeds approximateCount. Uses seek pagination (sampleWindow lt lastWindow) to avoid skip.
     */
    private <T extends LeadTimeHistorySampledStatistics> List<T> collectUntilSampleSize(
            Criteria baseCriteria,
            Query projection,            // must include sampleWindow and sampleSize
            int approximateCount,
            Class<T> clazz
    ) throws CoodDBException {
        List<T> collected = new ArrayList<>();
        AtomicInteger cumulative = new AtomicInteger(0);
        Instant lastWindow = null;
        final Sort descByWindow = Sort.by(Sort.Direction.DESC, LeadTimeHistorySampledStatistics.Fields.sampleWindow);

        // Cap the maximum number of samples to prevent memory overflow, as approximate counting is performed in-memory.
        do {
            Criteria pageCriteria = (lastWindow == null)
                    ? baseCriteria
                    : new Criteria().andOperator(baseCriteria, Criteria.where(LeadTimeHistorySampledStatistics.Fields.sampleWindow).lt(lastWindow)
            );

            Query q = new Query(pageCriteria).with(descByWindow).limit(APPROX_BATCH_SIZE);
            // Ensure we always have fields needed for selection logic
            q.fields().include(LeadTimeHistorySampledStatistics.Fields.sampleWindow).include(LeadTimeHistorySampledStatistics.Fields.sampleSize);
            applyProjection(q, projection);

            List<T> batch = mongo.find(q, clazz);
            if (batch == null || batch.isEmpty()) {
                break; // no more data
            }

            batch.stream().takeWhile(item -> cumulative.get() < approximateCount)
                    .forEach(item -> {
                        collected.add(item);
                        cumulative.addAndGet(Objects.requireNonNullElse(item.getSampleSize(), 0));
                    });

            // Prepare next page: strictly older than the oldest we just saw
            lastWindow = batch.get(batch.size() - 1).getSampleWindow();

        } while (cumulative.get() < approximateCount && collected.size() < APPROX_COUNT_MAX_RECORD_SIZE && lastWindow != null);
        return collected;
    }
}
