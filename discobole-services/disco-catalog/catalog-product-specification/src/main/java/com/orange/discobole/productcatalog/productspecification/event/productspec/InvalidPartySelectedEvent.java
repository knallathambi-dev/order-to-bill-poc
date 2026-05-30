// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.Set;

public class InvalidPartySelectedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final Set<String> invalidPartySelected;

	private InvalidPartySelectedEvent() {
		this.productSpecId = null;
		this.invalidPartySelected = null;
	}

	public InvalidPartySelectedEvent(String productSpecId, Set<String> invalidPartySelected) {
		this.productSpecId = productSpecId;
		this.invalidPartySelected = invalidPartySelected;
	}

	@Override
	public String toString() {
		return "InvalidPartySelectedEvent [productSpecId=" + productSpecId
				+ ", invalidPartySelected=" + invalidPartySelected + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public Set<String> getInvalidPartySelected() {
		return invalidPartySelected;
	}

}
