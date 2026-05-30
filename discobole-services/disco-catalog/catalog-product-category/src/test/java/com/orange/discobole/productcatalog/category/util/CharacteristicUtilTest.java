// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.DefineSubcategory;

public class CharacteristicUtilTest extends CategoryApplicationTests {

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@Test
	public void getCharacteristicTest() {


		Characteristic characteristic = new StringCharacteristic().value("Character")
				.name(CategoryConstants.DEFINE_SUBCATEGORY);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(characteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + DefineSubcategory.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_SUBCATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	
		assertThat(CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				CategoryConstants.DEFINE_SUBCATEGORY, characteristicList).getName())
						.isEqualTo(CategoryConstants.DEFINE_SUBCATEGORY);

	}

	@Test
	public void getCharacteristicWhenNameNullTest() throws Exception {
		Characteristic characteristic = new StringCharacteristic().value("Character")
				.name(CategoryConstants.DEFINE_SUBCATEGORY);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(characteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + DefineSubcategory.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_SUBCATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	
		assertThrows(IllegalArgumentException.class, () -> CharacteristicUtil
				.getCharacteristic(taskFlowUpdate.getCharacteristic(), null, characteristicList));
	}
	@Test
	public void getEmptyCharacterStic(){

		List<Characteristic> characteristics = new ArrayList<>();
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + DefineSubcategory.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_SUBCATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}


		assertThrows(InvalidParameterException.class, () -> CharacteristicUtil
				.getCharacteristic(taskFlowUpdate.getCharacteristic(),CategoryConstants.DEFINE_SUBCATEGORY, characteristicList));
	
	}
	@Test
	public void getEmptyCharacterStic2(){

		Characteristic characteristic = new StringCharacteristic().value("")
				.name(CategoryConstants.DEFINE_SUBCATEGORY);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(characteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + DefineSubcategory.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_SUBCATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(2).maxCardinality(3)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}


		assertThrows(InvalidParameterException.class, () -> CharacteristicUtil
				.getCharacteristic(taskFlowUpdate.getCharacteristic(),CategoryConstants.DEFINE_SUBCATEGORY, characteristicList));
	
	
	}

	@Test
	public void getEmptyCharacterStic4(){

		Characteristic characteristic = new StringCharacteristic().value("")
				.name(CategoryConstants.DEFINE_SUBCATEGORY);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(characteristic);
		when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristics);
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + DefineSubcategory.class.getSimpleName() + ".json");
			Object pickUsageSpec = null;
			pickUsageSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_SUBCATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(1).maxCardinality(0)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(pickUsageSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}


		assertThrows(InvalidParameterException.class, () -> CharacteristicUtil
				.getCharacteristic(taskFlowUpdate.getCharacteristic(),CategoryConstants.DEFINE_SUBCATEGORY, characteristicList));
		
	
	}
	
	

}
