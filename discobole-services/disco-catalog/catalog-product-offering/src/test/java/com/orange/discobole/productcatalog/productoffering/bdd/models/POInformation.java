// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.bdd.models;

import java.io.Serializable;

public class POInformation implements Serializable {

	private static final long serialVersionUID = -3639013005206762052L;
	String poId;
	String type;
	String lifeCycleStatus;

	public POInformation(String poId, String type, String lifeCycleStatus) {
		super();
		this.poId = poId;
		this.type = type;
		this.lifeCycleStatus = lifeCycleStatus;
	}
	
	public String getLifeCycleStatus() {
		return lifeCycleStatus;
	}

	public void setLifeCycleStatus(String lifeCycleStatus) {
		this.lifeCycleStatus = lifeCycleStatus;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "POInformation [poId=" + poId + ", type=" + type + ", lifeCycleStatus=" + lifeCycleStatus + "]";
	}

	

}
