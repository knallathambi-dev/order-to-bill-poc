// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

public class DefineRecurringChargePeriod {
    private Integer recurringChargePeriodLength;
    private String recurringChargePeriodType;

    public Integer getRecurringChargePeriodLength() {
        return recurringChargePeriodLength;
    }

    public void setRecurringChargePeriodLength(Integer recurringChargePeriodLength) {
        this.recurringChargePeriodLength = recurringChargePeriodLength;
    }

    public String getRecurringChargePeriodType() {
        return recurringChargePeriodType;
    }

    public void setRecurringChargePeriodType(String recurringChargePeriodType) {
        this.recurringChargePeriodType = recurringChargePeriodType;
    }
}
