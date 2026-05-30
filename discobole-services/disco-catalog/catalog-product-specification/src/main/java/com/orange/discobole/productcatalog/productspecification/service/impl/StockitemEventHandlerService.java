// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.*;

import jakarta.annotation.Resource;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockitemEventHandlerService {
	@Resource
	Publisher publisher;

	@EventHandler
	public void handle(StockItemExpurgedEvent event) {
		publisher.project(List.of(event));
	}


	@EventHandler
	public void handle(StockItemReplicatedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(StockItemNotificationSentEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(StockItemAttributeUpdatedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(StockItemStatusUpdatedEvent event) {
		publisher.project(List.of(event));
	}


}
