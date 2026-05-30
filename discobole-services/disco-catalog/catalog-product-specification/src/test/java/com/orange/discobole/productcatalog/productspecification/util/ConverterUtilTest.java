// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductCharValue;


 class ConverterUtilTest extends ProductSpecificationApplicationTests {

	@Test
	 void productSpecificationCharacteristicConvertTest() {
		CharacteristicSpecification serviceSpecCharacteristic = new CharacteristicSpecification().id("1");
		assertThat(ConverterUtil.convert(serviceSpecCharacteristic).getId()).isEqualTo("1");
	}

	@Test
	 void productSpecificationCharacteristicValueConvertTest() {
		ProductCharValue serviceSpecCharacteristicValue = new ProductCharValue();
		serviceSpecCharacteristicValue.setIsDefault(true);
		assertThat(ConverterUtil.convert(serviceSpecCharacteristicValue).isIsDefault()).isTrue();
	}

}
