// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.SelectPOPType;
import com.orange.discobole.processflow.exception.InvalidParameterException;

class CharacteristicUtilTest extends ProductOfferingPriceApplicationTests {

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private Characteristic characteristic;

	@Mock
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@BeforeEach
	public void init() {
		Characteristic generatedCharacteristicList = new StringCharacteristic().value("Character")
				.name(ProductOfferingPriceConstants.SELECT_POP_TYPE);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(generatedCharacteristicList);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productofferingprice/" + SelectPOPType.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOfferingPriceConstants.SELECT_POP_TYPE)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Test
	void getCharacteristicTest() {
		assertThat(CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.SELECT_POP_TYPE, characteristicList).getName())
						.isEqualTo(ProductOfferingPriceConstants.SELECT_POP_TYPE);

	}

	@Test
	void getCharacteristicWhenNameNullTest() {
		assertThrows(IllegalArgumentException.class, () ->
						CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(), null, characteristicList),
				"Expected IllegalArgumentException to be thrown when taskCharacteristic is null");
//		try {
//			CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(), null, characteristicList);
//			Assert.fail("Expected an IllegalArgumentException to be thrown");
//		} catch(IllegalArgumentException e){
//
//		}catch (Exception e) {
//		}
	}

	@Test
	void getCharacteristicNull() {
		assertThrows(InvalidParameterException.class, () -> {
			characteristicList.get(0).setMinCardinality(1);
			CharacteristicUtil.getCharacteristic(null, ProductOfferingPriceConstants.SELECT_POP_TYPE, characteristicList);
		},"Expected an InvalidParameterException to be thrown");
//		try {
//			characteristicList.get(0).setMinCardinality(1);
//			CharacteristicUtil.getCharacteristic(null, ProductOfferingPriceConstants.SELECT_POP_TYPE,
//					characteristicList);
//			Assert.fail("Expected an InvalidParameterException to be thrown");
//		} catch (Exception e) {
//		}
	}
}
