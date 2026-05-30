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
import static org.junit.Assert.assertThrows;
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
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductSpecUsageSpecification;


 class CharacteristicUtilTest extends ProductSpecificationApplicationTests {

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private Characteristic characteristic;

	@Mock
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@BeforeEach
	public void init() {
		Characteristic characteristic = new StringCharacteristic().value("Character")
				.name(ProductSpecConstants.USAGE_SPEC);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(characteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + ProductSpecUsageSpecification.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.USAGE_SPEC)
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
				ProductSpecConstants.USAGE_SPEC, characteristicList).getName())
						.isEqualTo(ProductSpecConstants.USAGE_SPEC);

	}

	@Test()
	 void getCharacteristicWhenNameNullTest() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> CharacteristicUtil
				.getCharacteristic(taskFlowUpdate.getCharacteristic(), null, characteristicList));
	}

}
