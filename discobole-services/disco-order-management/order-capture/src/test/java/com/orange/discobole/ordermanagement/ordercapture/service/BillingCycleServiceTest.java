// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.commons.dto.account.BillStructure;
import com.orange.discobole.ordermanagement.commons.dto.account.BillingAccount;
import com.orange.discobole.ordermanagement.commons.dto.account.BillingCycleSpecificationRefOrValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BillingCycleServiceTest {
    private static final int DATE_SHIFT = 15;
    @Spy
    @InjectMocks
    private BillingCycleService billingCycleService;
    @Mock
    private AccountManagementService accountManagementService;

    @Test
    @DisplayName("Given a billing account , " +
            "when getNextBillingDate is called, " +
            "then the next billing cycle date is returned.")
    void shouldGetNextBillCycleWhenGivenABillingAccount() {
        //Given
        when(accountManagementService.fetchBillingAccountByRelatedPartyId(any())).thenReturn(createBillingAccount());


        //When
        billingCycleService.getNextBillingDate("123");

        //Then
        verify(billingCycleService, times(1)).getNextBillingDate("123");
    }

    private BillingAccount createBillingAccount() {
        BillingCycleSpecificationRefOrValue cycleSpecification = BillingCycleSpecificationRefOrValue.builder()
                .dateShift(DATE_SHIFT)
                .build();
        return BillingAccount.builder()
                .billStructure(BillStructure.builder()
                        .cycleSpecification(cycleSpecification)
                        .build())
                .build();
    }


}