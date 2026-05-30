// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.util;

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
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.SelectLifecycleEntity;

class CharacteristicUtilTest extends ManageLifeCycleApplicationTests {

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private Characteristic characteristic;

	@Mock
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@BeforeEach
	void init() {
		Characteristic fetchedCharacteristic = new StringCharacteristic().value("Character")
				.name(LifeCycleConstants.PRODUCT_ENTITY);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(fetchedCharacteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + SelectLifecycleEntity.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(LifeCycleConstants.PRODUCT_ENTITY)
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
				LifeCycleConstants.PRODUCT_ENTITY, characteristicList).getName())
						.isEqualTo(LifeCycleConstants.PRODUCT_ENTITY);

	}

	@Test()
	void getCharacteristicWhenNameNullTest()  {
		List<Characteristic> taskCharacteristic=taskFlowUpdate.getCharacteristic();
		assertThrows(IllegalArgumentException.class, () -> CharacteristicUtil
				.getCharacteristic(taskCharacteristic, null, characteristicList));
	}

}
