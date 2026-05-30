// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class acts as a command that gets triggered by
 * {@code ProductSpecAggregate} to initiate the creation of
 * {@code ProductSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public final class InitiateProductSpecCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final String serviceSpecId;

	/**
	 * Constructs a command object that is supplied to {@code ProductSpecAggregate}
	 * to initiate the creation of {@code ProductSpecification}.
	 *
	 * @param serviceSpecId service specification id that has been selected for
	 *                      creation of {@code ProductSpecification}
	 */
	public InitiateProductSpecCommand(String productSpecId,String serviceSpecId) {
		this.productSpecId=productSpecId;
		this.serviceSpecId = serviceSpecId;
	}

	/**
	 * This method represents the {@code productSpecId,@code serviceSpecId} associated with
	 * {@code InitiateProductSpecCommand}
	 *
	 * @return string representation of {@code InitiateProductSpecCommand}
	 *         containing its data.
	 */
	@Override
	public String toString() {
		return "InitiateProductSpecCommand [productSpecId=" + productSpecId + ", serviceSpecId=" + serviceSpecId + "]";
	}

	/**
	 * This method is used to get the {@code serviceSpecId} associated with
	 * {@code InitiateProductSpecCommand}
	 *
	 * @return the {@code serviceSpecId} associated with
	 *         {@code InitiateProductSpecCommand}
	 */
	public String getServiceSpecId() {
		return serviceSpecId;
	}
	
	/**
	 * This method is used to get the {@code productSpecId} associated with
	 * {@code InitiateProductSpecCommand}
	 *
	 * @return the {@code productSpecId} associated with
	 *         {@code InitiateProductSpecCommand}
	 */
	public String getProductSpecId() {
		return productSpecId;
	}

}
