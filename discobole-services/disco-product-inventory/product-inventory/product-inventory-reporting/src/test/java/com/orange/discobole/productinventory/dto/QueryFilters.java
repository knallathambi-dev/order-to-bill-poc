// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class QueryFilters {
    private List<String> productOfferId;
    private LocalDate startDate;
    private java.time.LocalDate endDate;
    private LocalDate date;
    private Integer dayOfMonth;
    private Integer isoDayOfWeek;
    private Integer dayOfYear;
}
