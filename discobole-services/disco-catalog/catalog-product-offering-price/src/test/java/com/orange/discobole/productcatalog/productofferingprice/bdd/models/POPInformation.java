// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.bdd.models;

import java.io.Serializable;

public class POPInformation implements Serializable {

	private static final long serialVersionUID = -3639013005206762052L;
	String popId;
	String type;

	public POPInformation(String popId, String type) {
		super();
		this.popId = popId;
		this.type = type;
	}

	public String getPopId() {
		return popId;
	}

	public void setPopId(String popId) {
		this.popId = popId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "POPInformation [popId=" + popId + ", type=" + type + "]";
	}

}
