// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service;

import com.orange.discobole.productcatalog.productofferingprice.event.*;

public interface ProductOfferingPriceEventHandlerService {

	/**
	 * Axon Event Handler for ProductOfferingPriceInitiatedEvent event.
	 * 
	 * @param event :ProductOfferingPriceInitiatedEvent
	 */
	public void handle(ProductOfferingPriceInitiatedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceChargeIdentityDataDescribedEvent event.
	 * 
	 * @param event :ProductOfferingPriceChargeIdentityDataDescribedEvent
	 */
	public void handle(ProductOfferingPriceChargeIdentityDataDescribedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceAlterationIdentityDataDescribedEvent event.
	 * 
	 * @param event :ProductOfferingPriceAlterationIdentityDataDescribedEvent
	 */
	public void handle(ProductOfferingPriceAlterationIdentityDataDescribedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceRelationshipDefinedEvent event.
	 * 
	 * @param event :ProductOfferingPriceRelationshipDefinedEvent
	 */
	public void handle(ProductOfferingPriceRelationshipDefinedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceValidatedEvent event.
	 * 
	 * @param event :ProductOfferingPriceValidatedEvent
	 */
	public void handle(ProductOfferingPriceValidatedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceVersionCreatedEvent event.
	 * 
	 * @param event :ProductOfferingPriceVersionCreatedEvent
	 */
	public void handle(ProductOfferingPriceVersionCreatedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceCreationCompletedEvent event.
	 * 
	 * @param event :ProductOfferingPriceCreationCompletedEvent
	 */
	public void handle(ProductOfferingPriceCreationCompletedEvent event);

	/**
	 * Axon Event Handler for ProductOfferingPriceCancelledEvent event.
	 * 
	 * @param event :ProductOfferingPriceCancelledEvent
	 */
	public void handle(ProductOfferingPriceCancelledEvent event);
	
	/**
	 * Axon Event Handler for ProductOfferingPriceDeleteEvent event.
	 * 
	 * @param event :ProductOfferingPriceDeleteEvent
	 */
	
	public void handle(ProductOfferingPriceDeleteEvent event);

	public void handle(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent event);

	public void handle(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent event);


}
