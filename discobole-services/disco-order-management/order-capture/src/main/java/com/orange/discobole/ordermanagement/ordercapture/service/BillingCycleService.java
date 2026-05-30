// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.commons.dto.account.BillingAccount;
import com.orange.discobole.ordermanagement.commons.dto.account.BillingCycleSpecificationRefOrValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Service
@Slf4j
public class BillingCycleService {
    private final AccountManagementService accountManagementService;

    public BillingCycleService(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    public Instant getNextBillingDate(String relatedPartyId) {
        BillingAccount account =
                accountManagementService.fetchBillingAccountByRelatedPartyId(relatedPartyId);

        return calculateNextBillingCycleDate(
                account.getBillStructure().getCycleSpecification());
    }
    private Instant calculateNextBillingCycleDate(BillingCycleSpecificationRefOrValue cycleSpecification) {
        LocalDate now = LocalDate.now();
        int billingDay = Math.min(cycleSpecification.getDateShift(), 31);
        YearMonth currentMonth = YearMonth.from(now);
        LocalDate nextBillingDate;

        if (now.getDayOfMonth() < billingDay) {
            nextBillingDate = now.withDayOfMonth(Math.min(billingDay, currentMonth.lengthOfMonth()));
        } else {
            YearMonth nextMonth = currentMonth.plusMonths(1);
            nextBillingDate = nextMonth.atDay(Math.min(billingDay, nextMonth.lengthOfMonth()));
        }
        return nextBillingDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

}
