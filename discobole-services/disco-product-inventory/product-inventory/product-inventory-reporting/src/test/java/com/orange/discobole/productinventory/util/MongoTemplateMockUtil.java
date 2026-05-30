// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.mongodb.client.result.UpdateResult;
import com.orange.discobole.productinventory.dto.QueryFilters;
import com.orange.discobole.productinventory.dto.UpdateAttributes;
import com.orange.discobole.productinventory.model.BaseDateDerivedFields;
import com.orange.discobole.productinventory.model.ProductOfferReportEntity;
import com.orange.discobole.productinventory.model.StatusReportEntity;
import com.orange.discobole.productinventory.model.StatusReportingFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MongoTemplateMockUtil extends MockUtil {
    private List<StatusReportEntity> statusReportEntities = new ArrayList<>();
    private List<ProductOfferReportEntity> productOfferReportEntities = new ArrayList<>();

    public List<StatusReportEntity> getStatusReportEntities() {
        return List.copyOf(statusReportEntities);
    }

    public List<ProductOfferReportEntity> getProductOfferReportEntities() {
        return List.copyOf(productOfferReportEntities);

    }

    /**
     * Registers a mocked response for a query condition.
     *
     */
    public void registerMockResponse() {
        LocalDate from = LocalDate.now().minusMonths(2);
        LocalDate to = LocalDate.now();
        statusReportEntities = generateStatusReports(from, to);
        productOfferReportEntities = generateProductOfferReports(from, to);
    }

    /**
     * Returns the mocked response for a given query.
     *
     * @param <T>   The type of the result list.
     * @param query The query being executed.
     * @param clazz
     * @return The mocked result list.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getMockFindResponse(Query query, Class<?> clazz) {
        QueryFilters queryFilters = extractFilters(query);
        log.info("query {} res {}", query, queryFilters);

        // Start with the appropriate collection based on the productOfferId filter
        Stream<T> reportEntitiesStream = null;
        if (clazz.equals(ProductOfferReportEntity.class)) {
            reportEntitiesStream = (Stream<T>) productOfferReportEntities.stream();
            // Apply the filter only if productOfferId is not null
            if (queryFilters.getProductOfferId() != null) {
                reportEntitiesStream = reportEntitiesStream.filter(o -> queryFilters.getProductOfferId().contains(((ProductOfferReportEntity) o).getProductOfferId()));
            }
        } else {
            reportEntitiesStream = (Stream<T>) statusReportEntities.stream();
        }

        // Apply filters dynamically
        if (queryFilters.getStartDate() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getDate().isAfter(queryFilters.getStartDate()));
        }
        if (queryFilters.getEndDate() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getDate().isBefore(queryFilters.getEndDate()));
        }
        if (queryFilters.getDate() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getDate().equals(queryFilters.getDate()));
        }
        if (queryFilters.getIsoDayOfWeek() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getIsoDayOfWeek().equals(queryFilters.getIsoDayOfWeek()));
        }
        if (queryFilters.getDayOfMonth() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getDayOfMonth().equals(queryFilters.getDayOfMonth()));
        }
        if (queryFilters.getDayOfYear() != null) {
            reportEntitiesStream = reportEntitiesStream.filter(o -> ((BaseDateDerivedFields) o).getDayOfYear().equals(queryFilters.getDayOfYear()));
        }

        // Collect filtered results and return
        return reportEntitiesStream.collect(Collectors.toList());
    }

    public UpdateResult getMockUpdateResponse(Query query, Update update, Class<?> clazz) {
        Object mockFindResponse = getOneOrNew(getMockFindResponse(query, clazz), clazz);
        UpdateAttributes updateAttributes = extractUpdate(update);

        if (mockFindResponse instanceof BaseDateDerivedFields baseDateDerivedFields) {
            baseDateDerivedFields.setDate(safeGetObject(baseDateDerivedFields.getDate(), updateAttributes.getDate()));
            baseDateDerivedFields.setDayOfMonth(safeGetObject(baseDateDerivedFields.getDayOfMonth(), updateAttributes.getDayOfMonth()));
            baseDateDerivedFields.setDayOfYear(safeGetObject(baseDateDerivedFields.getDayOfYear(), updateAttributes.getDayOfYear()));
            baseDateDerivedFields.setIsoDayOfWeek(safeGetObject(baseDateDerivedFields.getIsoDayOfWeek(), updateAttributes.getIsoDayOfWeek()));
        }
        if (mockFindResponse instanceof StatusReportingFields statusReportingFields) {
            for (UpdateAttributes.IncrementAttributes incrementAttribute : updateAttributes.getIncrementAttributes()) {
                switch (incrementAttribute.getField()) {
                    case StatusReportingFields.Fields.activeCount: {
                        statusReportingFields.setActiveCount(
                                safeGetCount(statusReportingFields.getActiveCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    case StatusReportingFields.Fields.terminatedCount: {
                        statusReportingFields.setTerminatedCount(
                                safeGetCount(statusReportingFields.getTerminatedCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    case StatusReportingFields.Fields.cancelledCount: {
                        statusReportingFields.setCancelledCount(
                                safeGetCount(statusReportingFields.getCancelledCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    case StatusReportingFields.Fields.abortedCount: {
                        statusReportingFields.setAbortedCount(
                                safeGetCount(statusReportingFields.getAbortedCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    case StatusReportingFields.Fields.createdCount: {
                        statusReportingFields.setCreatedCount(
                                safeGetCount(statusReportingFields.getCreatedCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    case StatusReportingFields.Fields.soldCount: {
                        statusReportingFields.setSoldCount(
                                safeGetCount(statusReportingFields.getSoldCount()) + incrementAttribute.getIncrement()
                        );
                        break;
                    }
                    default:
                        break;
                }


            }
        }
        if (mockFindResponse instanceof ProductOfferReportEntity productOfferReportEntity) {
            productOfferReportEntity.setProductOfferId(safeGetObject(productOfferReportEntity.getProductOfferId(), updateAttributes.getProductOfferId()));
            productOfferReportEntity.setProductOfferName(safeGetObject(productOfferReportEntity.getProductOfferName(), updateAttributes.getProductOfferName()));
            productOfferReportEntity.setProductOfferType(safeGetObject(productOfferReportEntity.getProductOfferType(), updateAttributes.getProductOfferType()));

        }
        return UpdateResult.acknowledged(1L, 1L, null);
    }


}
