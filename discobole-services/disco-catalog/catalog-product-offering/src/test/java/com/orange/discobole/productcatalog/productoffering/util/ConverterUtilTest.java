// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.util.ConverterUtil;

class ConverterUtilTest extends ProductOfferingApplicationTests {


	@Test
	void productSpecificationCharacteristicValueConvertTest() {
		ProductCharValue serviceSpecCharacteristicValue = new ProductCharValue();
		serviceSpecCharacteristicValue.setIsDefault(true);
		assertThat(ConverterUtil.convert(serviceSpecCharacteristicValue).isIsDefault()).isTrue();
	}

}
