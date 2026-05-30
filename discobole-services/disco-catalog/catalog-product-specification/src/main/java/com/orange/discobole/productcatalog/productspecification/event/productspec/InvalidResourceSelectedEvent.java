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

public class InvalidResourceSelectedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final Set<String> invalidResourcesSelected;

	private InvalidResourceSelectedEvent() {
		this.productSpecId = null;
		this.invalidResourcesSelected = null;
	}

	public InvalidResourceSelectedEvent(String productSpecId, Set<String> invalidResourcesSelected) {

		this.productSpecId = productSpecId;
		this.invalidResourcesSelected = invalidResourcesSelected;
	}

	@Override
	public String toString() {
		return "InvalidResourceSelectedEvent [productSpecId=" + productSpecId + ", invalidResourcesSelected="
				+ invalidResourcesSelected + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public Set<String> getInvalidResourcesSelected() {
		return invalidResourcesSelected;
	}

}
