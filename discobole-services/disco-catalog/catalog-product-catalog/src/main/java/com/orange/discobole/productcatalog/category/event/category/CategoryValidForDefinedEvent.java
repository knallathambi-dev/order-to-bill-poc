// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;

public class CategoryValidForDefinedEvent implements CategoryEvent {
    private final String categoryId;
    private final TimePeriod validFor;
    private final OffsetDateTime lastUpdate;
    public CategoryValidForDefinedEvent(String categoryId, TimePeriod validFor, OffsetDateTime lastUpdate) {
        super();
        this.categoryId = categoryId;
        this.validFor = validFor;
        this.lastUpdate = lastUpdate;
    }
    public CategoryValidForDefinedEvent() {
        super();
        this.categoryId = null;
        this.validFor = null;
        this.lastUpdate = null;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public TimePeriod getValidFor() {
        return validFor;
    }
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }
    @Override
    public String toString() {
        return "CategoryValidForDefinedEvent [categoryId=" + categoryId + ", validFor=" + validFor + ", lastUpdate="
                + lastUpdate + "]";
    }
}
