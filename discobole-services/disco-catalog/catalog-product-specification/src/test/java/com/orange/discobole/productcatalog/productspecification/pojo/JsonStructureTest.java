// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.pojo.Property;
import com.orange.discobole.productcatalog.productspecification.util.CreateJsonFromPOJO;

 class JsonStructureTest {

	String expectedJson;

	@BeforeEach
	void setUp() {
		expectedJson = "{\"required\":[\"name\",\"description\"],\"type\":\"object\",\"properties\":{\"brand\":{\"type\":\"string\",\"description\":\"Brand associated ith this Product specification\",\"default\":\"Orange\"},\"name\":{\"type\":\"string\",\"description\":\"Name of the Product specification\"},\"validFor\":{\"type\":\"object\",\"properties\":{\"startDateTime\":{\"type\":\"string\",\"description\":\"Start date and time of the period\",\"format\":\"date-time\"},\"endDateTime\":{\"type\":\"string\",\"description\":\"End date and time of the period\",\"format\":\"date-time\"}}},\"description\":{\"type\":\"string\",\"description\":\"Description of the Product specification\"}}}";
	}

	@Test
	void testJsonStructure() {
		Property obj = new Property();
		obj.setRequired(List.of("name", "description"));
		obj.setType("object");
		Property brand = new Property();
		brand.setType("string");
		brand.setDescription("Brand associated ith this Product specification");
		brand.setDefaultValue("Orange");
		Property name = new Property();
		name.setType("string");
		name.setDescription("Name of the Product specification");
		Property startDateTime = new Property();
		startDateTime.setType("string");
		startDateTime.setDescription("Start date and time of the period");
		startDateTime.setFormat("date-time");
		Property endDateTime = new Property();
		endDateTime.setType("string");
		endDateTime.setDescription("End date and time of the period");
		endDateTime.setFormat("date-time");
		Map<String, Property> validForMap = new LinkedHashMap<>();
		validForMap.put("startDateTime", startDateTime);
		validForMap.put("endDateTime", endDateTime);
		Property validFor = new Property();
		validFor.setType("object");
		validFor.setProperties(validForMap);
		Property description = new Property();
		description.setType("string");
		description.setDescription("Description of the Product specification");
		Map<String, Property> map = new LinkedHashMap<>();
		map.put("brand", brand);
		map.put("name", name);
		map.put("validFor", validFor);
		map.put("description", description);
		obj.setProperties(map);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String actualJson = objectMapper.writeValueAsString(obj);
			assertEquals(expectedJson, actualJson);
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void getNonNullFieldsFromPOJO() {
		CreateJsonFromPOJO nonNullFields = new CreateJsonFromPOJO();
		assertEquals(List.of("name", "description"),
				nonNullFields.getNonNullFields(IdentityData.class));
	}
}
