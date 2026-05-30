// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateAttributes {
    private List<IncrementAttributes> incrementAttributes;
    private LocalDate date;
    private Integer dayOfMonth;
    private Integer dayOfYear;
    private Integer isoDayOfWeek;
    private String productOfferId;
    private String productOfferName;
    private String productOfferType;

    @Data
    @AllArgsConstructor
    public static class IncrementAttributes {
        private String field;
        private Long increment;
    }
}
