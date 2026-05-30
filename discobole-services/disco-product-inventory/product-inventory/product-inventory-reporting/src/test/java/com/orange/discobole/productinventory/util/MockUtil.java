// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.productinventory.dto.QueryFilters;
import com.orange.discobole.productinventory.dto.UpdateAttributes;
import com.orange.discobole.productinventory.model.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.TestConstants.PRODUCT_OFFERING_JSON;

@Slf4j
public class MockUtil {
    public static final ObjectMapper mapper = createObjectMapper();

    private static List<StatusReportingFields> generateReports(LocalDate from, LocalDate to) {
        List<StatusReportingFields> reports = new ArrayList<>();
        Random random = new Random();
        long activeCount = 0;
        long terminatedCount = 0;
        long cancelledCount = 0;
        long abortedCount = 0;
        long createdCount = 0;
        long soldCount = 0;

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            activeCount += random.nextInt(100) + 1;
            terminatedCount += random.nextInt(100) + 1;
            cancelledCount += random.nextInt(100) + 1;
            abortedCount += random.nextInt(100) + 1;
            createdCount += random.nextInt(100) + 1;
            soldCount += random.nextInt(100) + 1;
            StatusReportingFields report = StatusReportingFields
                    .builder()
                    .activeCount(activeCount)
                    .terminatedCount(terminatedCount)
                    .cancelledCount(cancelledCount)
                    .abortedCount(abortedCount)
                    .createdCount(createdCount)
                    .soldCount(soldCount)
                    .dayOfMonth(date.getDayOfMonth())
                    .isoDayOfWeek(date.getDayOfWeek().getValue())
                    .dayOfYear(date.getDayOfYear())
                    .date(date)
                    .build();

            reports.add(report);
        }

        return reports;
    }

    public static List<StatusReportEntity> generateStatusReports(LocalDate from, LocalDate to) {
        List<StatusReportingFields> statusReportingFields = generateReports(from, to);
        return statusReportingFields.stream().map(srf -> {
            StatusReportEntity srf1 = new StatusReportEntity(srf);
            srf1.setId(ObjectId.get());
            return srf1;
        }).collect(Collectors.toList());
    }

    public static List<ProductOfferReportEntity> generateProductOfferReports(LocalDate from, LocalDate to) {
        List<StatusReportingFields> statusReportingFieldsList = generateReports(from, to);
        List<ProductOfferEntity> productOffers = getProductOffers();
        List<ProductOfferReportEntity> res = new ArrayList<>();
        Map<String, List<ProductOfferEntity>> collect = productOffers.stream().collect(Collectors.groupingBy(ProductOfferEntity::getProductOfferType));
        for (List<ProductOfferEntity> value : collect.values()) {
            for (ProductOfferEntity productOfferEntity : value) {
                res.addAll(statusReportingFieldsList.stream().map(srf -> {
                    ProductOfferReportEntity srf1 = new ProductOfferReportEntity(srf);
                    srf1.setId(ObjectId.get());
                    srf1.setProductOfferId(productOfferEntity.getId());
                    srf1.setProductOfferName(productOfferEntity.getProductOfferName());
                    srf1.setProductOfferType(productOfferEntity.getProductOfferType());
                    return srf1;
                }).toList());
            }
        }
        return res;
    }

    public static List<ProductOfferEntity> getProductOffers() {
        try {
            return List.of(mapper.readValue(Paths.get(PRODUCT_OFFERING_JSON).toFile(), ProductOfferEntity[].class));
        } catch (IOException e) {
            log.error("error while getProductOffers", e);
            return new ArrayList<>();
        }
    }

    public static ProductOfferEntity getRandomOffer() {
        return getProductOffers().stream().findAny().orElseThrow();
    }

    static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    public static QueryFilters extractFilters(Query query) {
        // Get the query's underlying Document
        Document queryObject = query.getQueryObject();
        log.info(String.valueOf(queryObject));

        QueryFilters.QueryFiltersBuilder builder = QueryFilters.builder();
        // Convert to a map for easier inspection
        for (Map.Entry<String, Object> stringObjectEntry : queryObject.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();
            if (key.equals(ProductOfferReportEntity.Fields.productOfferId) && value instanceof String productOfferId) {
                builder.productOfferId(List.of(productOfferId));
            }
            if (key.equals(ProductOfferReportEntity.Fields.productOfferId) && value instanceof Collection<?> collection && collection.stream().allMatch(String.class::isInstance)) {
                // the above if Ensures all elements in the collection are Strings && we Safe cast
                builder.productOfferId(new ArrayList<>((Collection<String>) collection));

            }
            if (key.equals(BaseDateDerivedFields.Fields.date) && value instanceof Document dateDoc) {
                for (Map.Entry<String, Object> dateDocEntry : dateDoc.entrySet()) {
                    if (dateDocEntry.getKey().equals("$gte") && dateDocEntry.getValue() instanceof LocalDate startDate) {
                        builder.startDate(startDate);
                    }
                    if (dateDocEntry.getKey().equals("$lte") && dateDocEntry.getValue() instanceof LocalDate endDate) {
                        builder.endDate(endDate);
                    }
                }
            }
            if (key.equals(BaseDateDerivedFields.Fields.date) && value instanceof LocalDate date) {
                builder.date(date);
            }
            if (key.equals(BaseDateDerivedFields.Fields.dayOfMonth) && value instanceof Integer dayOfMonth) {
                builder.dayOfMonth(dayOfMonth);
            }
            if (key.equals(BaseDateDerivedFields.Fields.isoDayOfWeek) && value instanceof Integer isoDayOfWeek) {
                builder.isoDayOfWeek(isoDayOfWeek);
            }
            if (key.equals(BaseDateDerivedFields.Fields.dayOfYear) && value instanceof Integer dayOfYear) {
                builder.dayOfYear(dayOfYear);

            }
        }

        return builder.build();
    }

    public Object getOneOrNew(List<Object> mockFindResponse, Class<?> clazz) {
        if (!mockFindResponse.isEmpty()) {
            return mockFindResponse.get(0);
        }
        if (ProductOfferReportEntity.class.isAssignableFrom(clazz)) {
            return ProductOfferReportEntity.builder().build();

        } else {
            return StatusReportingFields.builder().build();
        }

    }

    public Long safeGetCount(Long count) {
        return count != null ? count : 0;
    }

    public <T> T safeGetObject(T oldVal, T newVal) {
        return newVal != null ? newVal : oldVal;
    }

    public UpdateAttributes extractUpdate(Update update) {
        Document updateObject = update.getUpdateObject();
        UpdateAttributes.UpdateAttributesBuilder builder = UpdateAttributes.builder();
        List<UpdateAttributes.IncrementAttributes> incrementAttributes = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : updateObject.entrySet()) {
            if (stringObjectEntry.getKey().equals("$inc")) {
                Document incs = (Document) stringObjectEntry.getValue();
                for (Map.Entry<String, Object> objectEntry : incs.entrySet()) {
                    incrementAttributes.add(new UpdateAttributes.IncrementAttributes(objectEntry.getKey(), (Long) objectEntry.getValue()));
                }
            }
            if (stringObjectEntry.getKey().equals("$setOnInsert")) {
                Document sets = (Document) stringObjectEntry.getValue();
                for (Map.Entry<String, Object> objectEntry : sets.entrySet()) {
                    switch (objectEntry.getKey()) {
                        case ProductOfferReportEntity.Fields.productOfferName:
                            builder.productOfferName((String) objectEntry.getValue());
                            break;
                        case ProductOfferReportEntity.Fields.productOfferType:
                            builder.productOfferType((String) objectEntry.getValue());
                            break;
                        case ProductOfferReportEntity.Fields.productOfferId:
                            builder.productOfferId((String) objectEntry.getValue());
                            break;
                        case BaseDateDerivedFields.Fields.date:
                            builder.date((LocalDate) objectEntry.getValue());
                            break;
                        case BaseDateDerivedFields.Fields.isoDayOfWeek:
                            builder.isoDayOfWeek((Integer) objectEntry.getValue());
                            break;
                        case BaseDateDerivedFields.Fields.dayOfMonth:
                            builder.dayOfMonth((Integer) objectEntry.getValue());
                            break;
                        case BaseDateDerivedFields.Fields.dayOfYear:
                            builder.dayOfYear((Integer) objectEntry.getValue());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        builder.incrementAttributes(incrementAttributes);
        return builder.build();
    }
}
