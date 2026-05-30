// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

public class InvalidPartyRoleSelectedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final String partyRole;

	private InvalidPartyRoleSelectedEvent() {
		this.productSpecId = null;
		this.partyRole = null;
	}

	public InvalidPartyRoleSelectedEvent(String productSpecId, String partyRole) {
		this.productSpecId = productSpecId;
		this.partyRole = partyRole;
	}

	@Override
	public String toString() {
		return "InvalidPartyRoleSelectedEvent [productSpecId=" + productSpecId
				+ ", partyRole=" + partyRole + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public String getPartyRole() {
		return partyRole;
	}

}
