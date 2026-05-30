// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.util.List;

import com.orange.discobole.productcatalog.productofferingprice.event.*;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceEventHandlerService;

import jakarta.annotation.Resource;

@Service
public class ProductOfferingPriceEventHandlerServiceImpl implements ProductOfferingPriceEventHandlerService {

	@Resource
	private Publisher publisher;

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceInitiatedEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceChargeIdentityDataDescribedEvent event) {

		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceAlterationIdentityDataDescribedEvent event) {

		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceRelationshipDefinedEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceValidatedEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceVersionCreatedEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceCreationCompletedEvent event) {
		// uncomment for future use
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceCancelledEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);

	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceDeleteEvent event) {
		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent event) {

		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

	@EventHandler
	@Override
	public void handle(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent event) {

		List<Event> eventList = List.of(event);
		publisher.project(eventList);
	}

}
