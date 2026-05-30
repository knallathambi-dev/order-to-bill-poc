// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationNodeSchedule {
    @JsonProperty("orderItemStartDate")
    private Instant orderItemStartDate;
    @JsonProperty("estimatedOrderItemDeliveryLeadTime")
    private Long estimatedOrderItemDeliveryLeadTime;
    @JsonProperty("actualOrderItemStartDate")
    private Instant actualOrderItemStartDate;
    @JsonProperty("actualOrderItemCompletionDate")
    private Instant actualOrderItemCompletionDate;

    public static final long UNDEFINED_LONG_VALUE = Long.MAX_VALUE;
    public static final Instant UNDEFINED_DATE_VALUE = Instant.parse("9999-12-31T23:59:59.999Z");

    @JsonIgnore
    public Instant getExpectedOrderItemCompletionDate() {
        Instant start = getOrderItemStartDate();
        Long leadSeconds = getEstimatedOrderItemDeliveryLeadTime();
        if (start == null || leadSeconds == null) {
            return null;
        }

        if(leadSeconds == UNDEFINED_LONG_VALUE) {
            return UNDEFINED_DATE_VALUE;
        }

        return start.plusSeconds(leadSeconds);
    }

    @JsonIgnore
    public Long getActualOrderItemDeliveryLeadTime() {
        if(Objects.isNull(this.getActualOrderItemStartDate()) ||  Objects.isNull(this.getActualOrderItemCompletionDate())){
            return null;
        }
        return Duration.between(getActualOrderItemStartDate(), getActualOrderItemCompletionDate()).getSeconds();
    }
}
