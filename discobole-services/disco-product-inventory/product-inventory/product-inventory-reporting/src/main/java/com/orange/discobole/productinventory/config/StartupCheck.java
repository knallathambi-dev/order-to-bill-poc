// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import com.orange.discobole.productinventory.service.ReportCronRunStateService;
import com.orange.discobole.productinventory.service.ReportingScheduledTasksService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Component
@Slf4j
public class StartupCheck {

    private final ReportCronRunStateService reportCronRunStateService;
    private final ReportingScheduledTasksService reportingScheduledTasksService;
    private final TransactionTemplate transactionTemplate;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        LocalDate today = LocalDate.now();
        /**
         * Using TransactionTemplate ensures that the operations within the transaction
         * (checking if the task has run, marking it as run, and importing data) are executed
         * atomically. This prevents race conditions and guarantees that importPreviousDayData
         * runs only once per day.
         */
        log.info("Application started. Checking if daily report task has run for date: {}", today);
        transactionTemplate.execute(status -> {
            if (!reportCronRunStateService.hasRunForDate(today)) {
                log.info("Daily report task has not run for date: {}. Proceeding with task execution.", today);
                reportingScheduledTasksService.markForDateAndImport(today);
                log.info("Daily report task completed for date: {}", today);
            } else {
                log.info("Daily report task has already run for date: {}", today);
            }
            return null;
        });
    }
}
