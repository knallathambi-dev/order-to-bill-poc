// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.utils.query;

import com.orange.discobole.productinventory.dto.v1.ReportGranularity;
import com.orange.discobole.productinventory.model.BaseDateDerivedFields;
import com.orange.discobole.productinventory.model.ProductOfferReportEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportsQueryBuilder {
    private final Criteria criteria;
    private ReportGranularity granularity;
    private LocalDate startDate;
    private LocalDate date;

    public ReportsQueryBuilder() {
        this.criteria = new Criteria();
    }

    /**
     * Adds productOfferId to the query criteria.
     *
     * @param productOfferId The product offer ID to filter by.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withProductOfferId(List<String> productOfferId) {
        if (productOfferId != null && !productOfferId.isEmpty()) {
            Set<String> collect = productOfferId.stream().filter(s -> !s.isBlank()).collect(Collectors.toSet());
            if (collect.isEmpty()) {
                return this;
            }
            if (collect.size() == 1) {
                criteria.and(ProductOfferReportEntity.Fields.productOfferId).is(collect.iterator().next());
            } else {
                criteria.and(ProductOfferReportEntity.Fields.productOfferId).in(collect);
            }
        }
        return this;
    }

    /**
     * Adds a date range filter to the query criteria.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        if (startDate != null && endDate != null) {
            criteria.and(BaseDateDerivedFields.Fields.date).gte(startDate).lte(endDate);
        } else if (startDate != null) {
            criteria.and(BaseDateDerivedFields.Fields.date).gte(startDate);
        } else if (endDate != null) {
            criteria.and(BaseDateDerivedFields.Fields.date).lte(endDate);
        }
        return this;
    }

    /**
     * Sets the granularity for the report query.
     *
     * @param granularity The granularity of the report (e.g., DAY, MONTH, YEAR).
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withGranularity(ReportGranularity granularity) {
        this.granularity = granularity;
        return this;
    }

    /**
     * Sets the specific date for the report query.
     *
     * @param date The specific date to filter by.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withDate(LocalDate date) {
        this.date = date;
        if (date != null) {
            criteria.and(BaseDateDerivedFields.Fields.date).is(date);
        }
        return this;
    }

    /**
     * Adds a filter for the day of the month.
     *
     * @param dayOfMonth The day of the month to filter by.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withDayOfMonth(int dayOfMonth) {
        criteria.and(BaseDateDerivedFields.Fields.dayOfMonth).is(dayOfMonth);
        return this;
    }

    /**
     * Adds a filter for the ISO day of the week.
     *
     * @param isoDayOfWeek The ISO day of the week to filter by.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withIsoDayOfWeek(int isoDayOfWeek) {
        criteria.and(BaseDateDerivedFields.Fields.isoDayOfWeek).is(isoDayOfWeek);
        return this;
    }

    /**
     * Adds a filter for the day of the year.
     *
     * @param dayOfYear The day of the year to filter by.
     * @return The updated ReportsQuery object.
     */
    public ReportsQueryBuilder withDayOfYear(int dayOfYear) {
        criteria.and(BaseDateDerivedFields.Fields.dayOfYear).is(dayOfYear);
        return this;
    }


    /**
     * Builds the final query based on the given parameters.
     *
     * @return The constructed Query object.
     */
    public Query build() {
        // Apply granularity logic
        if (granularity != null) {
            LocalDate date = Optional.ofNullable(this.date).orElse(this.startDate);
            if (date != null) {
                switch (granularity) {
                    case WEEK -> withIsoDayOfWeek(date.getDayOfWeek().getValue());
                    case MONTH -> withDayOfMonth(date.getDayOfMonth());
                    case YEAR -> withDayOfYear(date.getDayOfYear());
                    default -> {
                    }
                }
            }
        }

        return new Query(criteria);
    }
}
