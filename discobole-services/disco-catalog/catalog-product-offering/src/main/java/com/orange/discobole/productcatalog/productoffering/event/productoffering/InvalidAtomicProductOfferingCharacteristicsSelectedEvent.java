// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.Set;

import com.orange.discobole.productcatalog.productoffering.dto.InvalidCharacteristics;

/**
 * The Class InvalidAtomicProductOfferingCharacteristicsSelectedEvent.
 *
 * @author Diksha Srivastava
 */
public class InvalidAtomicProductOfferingCharacteristicsSelectedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final Set<InvalidCharacteristics> characteristics;

	public InvalidAtomicProductOfferingCharacteristicsSelectedEvent() {
		productOfferingId = null;
		characteristics = null;
	}

	public InvalidAtomicProductOfferingCharacteristicsSelectedEvent(String productOfferingId,
			Set<InvalidCharacteristics> characteristics) {
		this.productOfferingId = productOfferingId;
		this.characteristics = characteristics;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Set<InvalidCharacteristics> getCharacteristics() {
		return characteristics;
	}

	@Override
	public String toString() {
		return "InvalidAtomicProductOfferingCharacteristicsSelectedEvent [productOfferingId=" + productOfferingId
				+ ", characteristics=" + characteristics + "]";
	}

}
