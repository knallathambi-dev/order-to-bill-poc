// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.scheduled;

import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemStateChangedEvent;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductOrderEventScheduleService {
    private final ProductOrderEventService productOrderEventService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderEventScheduleService(ProductOrderEventService productOrderEventService) {
        this.productOrderEventService = productOrderEventService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void processNewProductOrderEventScheduleTask() {
        log.debug("Scheduled task started - Processing new product order events");
        List<ProductOrderItemStateChangedEvent> productOrderItemStateChangedEvents = productOrderEventService.getNewProductOrderItemEvents();

        if (productOrderItemStateChangedEvents.isEmpty()) {
            log.debug("Scheduled task completed - No new product order events to process");
            return;
        }

        log.info("Found {} new product order event(s) to process", productOrderItemStateChangedEvents.size());
        productOrderItemStateChangedEvents.forEach(event -> {
            log.debug("Processing product order event for productOrderId: {}", event.getProductOrderId());
            productOrderEventService.executeReceiveNewProductStateChangeEventTaskFlow(event);
        });
        log.info("Scheduled task completed - Successfully processed {} product order event(s)", productOrderItemStateChangedEvents.size());
    }

    @PostConstruct
    void reinitializeProductItemOfupState() {
        log.info("Application startup - Reinitializing product order item OFUP state from PROGRESS to NEW");
        productOrderEventService.reinitializeProductItemOfupState();
        log.info("Application startup - Product order item OFUP state reinitialization completed");
    }
}