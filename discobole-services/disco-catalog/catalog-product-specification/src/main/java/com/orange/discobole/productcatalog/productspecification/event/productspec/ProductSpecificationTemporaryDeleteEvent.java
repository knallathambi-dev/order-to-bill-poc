// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

public class ProductSpecificationTemporaryDeleteEvent implements ProductSpecEvent {
	private final String aggregateId;

	public ProductSpecificationTemporaryDeleteEvent(String aggregateId) {
		super();
		this.aggregateId = aggregateId;
	}
	public ProductSpecificationTemporaryDeleteEvent() {
		super();
		this.aggregateId = null;
	}
	public String getAggregateId() {
		return aggregateId;
	}
	@Override
	public String toString() {
		return "ProductSpecificationTemporaryDeleteEvent [aggregateId=" + aggregateId + "]";
	}
	
	
}
