// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto;

import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;

public class InvalidCharacteristics {

	private PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristic;
	private String reason;

	public PickAtomicProductOfferingCharacteristic getPickAtomicProductOfferingCharacteristic() {
		return pickAtomicProductOfferingCharacteristic;
	}

	public void setPickAtomicProductOfferingCharacteristic(
			PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristic) {
		this.pickAtomicProductOfferingCharacteristic = pickAtomicProductOfferingCharacteristic;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
