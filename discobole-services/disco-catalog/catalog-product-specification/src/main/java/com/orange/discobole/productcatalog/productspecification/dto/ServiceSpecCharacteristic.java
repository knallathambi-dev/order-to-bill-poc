// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto;

import java.util.List;

import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicValueSpecification;

public class ServiceSpecCharacteristic {

	private String id;
	private List<CharacteristicValueSpecification> serviceSpeccharValue;

	public ServiceSpecCharacteristic() {
	}

	public ServiceSpecCharacteristic(String id, List<CharacteristicValueSpecification> serviceSpeccharValue) {
		this.id = id;
		this.serviceSpeccharValue = serviceSpeccharValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<CharacteristicValueSpecification> getServiceSpeccharValue() {
		return serviceSpeccharValue;
	}

	public void setServiceSpeccharValue(List<CharacteristicValueSpecification> serviceSpeccharValue) {
		this.serviceSpeccharValue = serviceSpeccharValue;
	}

	public ServiceSpecCharacteristic serviceSpeccharValue(List<CharacteristicValueSpecification> serviceSpeccharValue) {
		this.serviceSpeccharValue = serviceSpeccharValue;
		return this;
	}

}
