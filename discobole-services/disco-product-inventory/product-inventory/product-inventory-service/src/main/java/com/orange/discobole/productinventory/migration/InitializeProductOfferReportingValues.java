// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.dto.v1.ProductOfferingRef;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductOfferEntity;
import com.orange.discobole.productinventory.model.ProductOfferReportEntity;
import com.orange.discobole.productinventory.model.StatusReportingFields;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.kafka.consumer.impl.reports.ReportingConsumer.STATUS_FIELD_MAP;

@ChangeUnit(id = "initialize-reporting-values-product-offer", order = "009", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class InitializeProductOfferReportingValues {

    public static final String STATUS = "status";
    public static final String PRODUCT_OFFER_ID = "productOffering._id";
    public static final String COUNT = "count";
    public static final String PRODUCT_OFFERING_NAME = "productOffering.name";
    public static final String PRODUCT_OFFERING_AT_TYPE = "productOffering.atType";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String ID = "_id";
    private final TransactionTemplate transactionTemplate;
    private final MongoTemplate mongoTemplate;

    private static void addNameAndTypeToStatusOfferCountOffers(List<StatusOfferCount> statusOfferCounts, List<ProductOfferingRef> productOfferingRefs) {
        for (StatusOfferCount statusOfferCount : statusOfferCounts) {
            Optional<ProductOfferingRef> any = productOfferingRefs.stream().filter(productOfferingRef -> productOfferingRef.getId().equals(statusOfferCount.offer.getId())).findAny();
            if (any.isPresent()) {
                ProductOfferingRef productOfferingRef = any.get();
                statusOfferCount.offer.setName(productOfferingRef.getName());
                statusOfferCount.offer.setAtType(productOfferingRef.getAtType());
            }
        }
    }

    @Execution
    public void execute() {
        try {
            log.info("Starting aggregation to calculate product counts by status and product offer...");

            // Step 1: Aggregate by status and productOfferId without name or type
            List<StatusOfferCount> statusOfferCounts = getStatusOfferCounts();
            // Step 2 : collect the product offers names & types
            List<ProductOfferingRef> productOfferingRefs = getProductOfferingRefs(statusOfferCounts);
            // Step 3 : add the product offers names & types to the statusOfferCounts list
            addNameAndTypeToStatusOfferCountOffers(statusOfferCounts, productOfferingRefs);
            // Step 4 : save the entities

            transactionTemplate.execute(ss -> {

                // Step 5 : save ReportingProductOffers ProductOfferEntity.class
                saveReportingProductOffers(statusOfferCounts);
                // Step 6 : save ReportingProductOffers ProductOfferReportEntity.class
                saveProductOfferingReports(statusOfferCounts);
                return null;
            });
            log.info("Aggregation completed successfully.");
        } catch (Exception e) {
            log.error("Failed to calculate and save product counts by status and product offer", e);
            throw e;
        }
    }

    private void saveProductOfferingReports(List<StatusOfferCount> statusOfferCounts) {
        LocalDate now = LocalDate.now();
        List<ProductOfferReportEntity> productOfferReportEntities = statusOfferCounts
                .stream()
                .collect(Collectors.groupingBy((StatusOfferCount o) -> o.offer.getId()))
                .values()
                .stream()
                .map(statusCounts -> {
                    ProductOfferReportEntity.ProductOfferReportEntityBuilder<?, ?> builder = ProductOfferReportEntity.builder();
                    for (StatusOfferCount statusCount : statusCounts) {
                        String field = STATUS_FIELD_MAP.get(ProductStatusType.valueOf(statusCount.status));
                        if (field == null) {
                            continue;
                        }
                        switch (field) {
                            case StatusReportingFields.Fields.activeCount: {
                                builder.activeCount(statusCount.count);
                                break;
                            }
                            case StatusReportingFields.Fields.terminatedCount: {
                                builder.terminatedCount(statusCount.count);
                                break;
                            }
                            case StatusReportingFields.Fields.cancelledCount: {
                                builder.cancelledCount(statusCount.count);
                                break;
                            }
                            case StatusReportingFields.Fields.abortedCount: {
                                builder.abortedCount(statusCount.count);
                                break;
                            }
                            case StatusReportingFields.Fields.createdCount: {
                                builder.createdCount(statusCount.count);
                                break;
                            }
                            case StatusReportingFields.Fields.soldCount: {
                                builder.soldCount(statusCount.count);
                                break;
                            }
                            default:break;
                        }
                        builder.productOfferId(statusCount.offer.getId());
                        builder.productOfferType(statusCount.offer.getAtType());
                        builder.productOfferName(statusCount.offer.getName());
                    }
                    builder.date(now);
                    builder.dayOfMonth(now.getDayOfMonth());
                    builder.dayOfYear(now.getDayOfYear());
                    builder.isoDayOfWeek(now.getDayOfWeek().getValue());
                    return builder.build();
                })
                .collect(Collectors.toList());

        mongoTemplate.remove(new Query(), ProductOfferReportEntity.class);
        log.info("Saving {} productOfferReportEntities.", productOfferReportEntities.size());
        mongoTemplate.insertAll(productOfferReportEntities);
        log.info("saved {} productOfferReportEntities.", productOfferReportEntities.size());

    }

    private void saveReportingProductOffers(List<StatusOfferCount> statusOfferCounts) {
        Set<ProductOfferEntity> offerEntities = statusOfferCounts.stream()
                .map(statusOfferCount -> ProductOfferEntity.builder()
                        .productOfferName(statusOfferCount.offer.getName())
                        .productOfferType(statusOfferCount.offer.getAtType())
                        .id(statusOfferCount.offer.getId())
                        .build())
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ProductOfferEntity::getId))));

        mongoTemplate.remove(new Query(), ProductOfferEntity.class);
        log.info("Saving {} ProductOfferEntities.", offerEntities.size());
        mongoTemplate.insertAll(offerEntities);
        log.info("saved {} ProductOfferEntities.", offerEntities.size());

    }

    private List<StatusOfferCount> getStatusOfferCounts() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and(STATUS).as(STATUS)
                        .andExpression("{ $getField: { field: '_id', input: '$productOffering' } }")
                        .as(PRODUCT_OFFER_ID),
                Aggregation.group(STATUS, PRODUCT_OFFER_ID)
                        .count().as(COUNT)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, ProductEntity.class, Document.class);
        List<StatusOfferCount> statusOfferCounts = new ArrayList<>();
        log.info("aggregated results {}", results);
        results.getMappedResults().forEach(doc -> {
            Document id = doc.get(ID, Document.class);
            String status = id.getString(STATUS);
            String productOfferId = id.getString(ID);
            Number countNumber = doc.get(COUNT, Number.class);
            long count = countNumber != null ? countNumber.longValue() : 0;
            if (status != null && productOfferId != null) {
                statusOfferCounts.add(new StatusOfferCount(status, ProductOfferingRef.builder().id(productOfferId).build(), count));
            }
        });
        return statusOfferCounts;
    }

    private List<ProductOfferingRef> getProductOfferingRefs(List<StatusOfferCount> statusOfferCounts) {
        Set<String> offerIds = statusOfferCounts.stream()
                .map(StatusOfferCount::offer)
                .map(ProductOfferingRef::getId)
                .collect(Collectors.toSet());

        // First aggregation: where both name and atType are not null
        Aggregation aggregationBothNotNull = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(PRODUCT_OFFER_ID)
                        .in(offerIds)
                        .and(PRODUCT_OFFERING_NAME).ne(null)
                        .and(PRODUCT_OFFERING_AT_TYPE).ne(null)),
                Aggregation.group(PRODUCT_OFFER_ID)
                        .first(PRODUCT_OFFERING_NAME).as(NAME)
                        .first(PRODUCT_OFFERING_AT_TYPE).as(TYPE)
        );

        // Execute first aggregation
        List<Document> resultsBothNotNull = mongoTemplate.aggregate(aggregationBothNotNull, ProductEntity.class, Document.class).getMappedResults();

        // Extract IDs from first aggregation
        Set<String> matchedIdsBothNotNull = resultsBothNotNull.stream()
                .map(doc -> doc.getString(ID))
                .collect(Collectors.toSet());

        // Remove matched IDs from the original offerIds set
        offerIds.removeAll(matchedIdsBothNotNull);
        List<Document> resultsNameNotNull = new ArrayList<>();
        List<Document> resultsAtTypeNotNull = new ArrayList<>();

        if (!offerIds.isEmpty()) {
            Aggregation aggregationNameNotNull = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where(PRODUCT_OFFER_ID)
                            .in(offerIds)
                            .and(PRODUCT_OFFERING_NAME).ne(null)
                            .and(PRODUCT_OFFERING_AT_TYPE).is(null)),
                    Aggregation.group(PRODUCT_OFFER_ID)
                            .first(PRODUCT_OFFERING_NAME).as(NAME)
                            .first(PRODUCT_OFFERING_AT_TYPE).as(TYPE)
            );

            // Execute second aggregation
            resultsNameNotNull = mongoTemplate.aggregate(aggregationNameNotNull, ProductEntity.class, Document.class).getMappedResults();

            // Extract IDs from second aggregation
            Set<String> matchedIdsNameNotNull = resultsNameNotNull.stream()
                    .map(doc -> doc.getString(ID))
                    .collect(Collectors.toSet());

            // Remove matched IDs from the remaining offerIds set
            offerIds.removeAll(matchedIdsNameNotNull);
            if (!offerIds.isEmpty()) {
                // Third aggregation: where atType is not null (and name is null)
                Aggregation aggregationAtTypeNotNull = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where(PRODUCT_OFFER_ID)
                                .in(offerIds)
                                .and(PRODUCT_OFFERING_NAME).is(null)
                                .and(PRODUCT_OFFERING_AT_TYPE).ne(null)),
                        Aggregation.group(PRODUCT_OFFER_ID)
                                .first(PRODUCT_OFFERING_NAME).as(NAME)
                                .first(PRODUCT_OFFERING_AT_TYPE).as(TYPE)
                );

                // Execute third aggregation
                resultsAtTypeNotNull = mongoTemplate.aggregate(aggregationAtTypeNotNull, ProductEntity.class, Document.class).getMappedResults();


            }
        }
        // Combine all results from the three aggregations
        List<Document> allResults = Stream.of(resultsBothNotNull, resultsNameNotNull, resultsAtTypeNotNull)
                .flatMap(Collection::stream)
                .toList();
        // Convert results to List<ProductOfferingRef>
        return allResults.stream()
                .map(doc -> ProductOfferingRef.builder()
                        .id(doc.getString(ID))
                        .name(doc.getString(NAME))
                        .atType(doc.getString(TYPE))
                        .build())
                .collect(Collectors.toList());
    }


    @RollbackExecution
    public void rollback() {
        log.debug("Rollback execution for InitializeProductOfferReportingValues");
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public record StatusOfferCount(String status, ProductOfferingRef offer, Long count) { //NOSONAR

    }

}
