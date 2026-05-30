// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.util;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.processflow.pojo.Property;
import com.orange.discobole.processflow.annotation.Format;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class CreateJsonFromPOJOTest {

	@InjectMocks
	private CreateJsonFromPOJO createJsonFromPOJO;

	@BeforeEach
	void setUp() {
		createJsonFromPOJO = new CreateJsonFromPOJO();
	}

	// Test class with private fields annotated
	public static class AnnotationTestClass {
		@Format("date-time")
		private String dateField;
		@ReadOnly(true)
		private String readOnlyField;
	}

	@Test
	void testGetNonNullFields() {
		List<String> fields = createJsonFromPOJO.getNonNullFields(SamplePojo.class);
		assertTrue(fields.contains("name"));
		assertTrue(fields.contains("age"));
	}

	@Test
	void testIsPrimitiveOrPrimitiveWrapper() {
		assertTrue(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(int.class));
		assertTrue(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(Integer.class));
		assertTrue(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(double.class));
		assertTrue(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(Double.class));
		assertFalse(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(String.class));
		assertFalse(CreateJsonFromPOJO.isPrimitiveOrPrimitiveWrapper(Object.class));
	}

	@Test
	void testCreateJsonObject(){
		Property prop = createJsonFromPOJO.createJson(SamplePojo.class);
		assertEquals("object", prop.getType());
		assertTrue(prop.getProperties().containsKey("name"));
		assertTrue(prop.getProperties().containsKey("age"));
		assertTrue(prop.getProperties().containsKey("active"));
		assertTrue(prop.getProperties().containsKey("dateTime"));
		assertTrue(prop.getProperties().containsKey("sampleEnum"));
		assertTrue(prop.getProperties().containsKey("innerList"));
	}

	@Test
	void testReadPOJOPrimitiveTypes() throws Exception {
		Method readPOJOMethod = CreateJsonFromPOJO.class.getDeclaredMethod("readPOJO", Class.class);
		readPOJOMethod.setAccessible(true);
		Map<String, Property> props =
				(Map<String, Property>) readPOJOMethod.invoke(createJsonFromPOJO, SamplePojo.class);

		assertTrue(props.containsKey("name"));
		assertTrue(props.containsKey("age"));
		assertTrue(props.containsKey("active"));
		assertTrue(props.containsKey("dateTime"));
		assertTrue(props.containsKey("sampleEnum"));
		assertTrue(props.containsKey("innerList"));

		assertEquals("string", props.get("name").getType());
		assertEquals("number", props.get("age").getType());
		assertEquals("array", props.get("innerList").getType());
		assertEquals("string", props.get("sampleEnum").getType());
		assertEquals("string", props.get("dateTime").getType());
	}

	@Test
	void testSetDefaultValuePrivate() throws Exception {
		Field mockField = SamplePojo.class.getDeclaredField("age");
		mockField.setAccessible(true);
		Method setDefaultValueMethod = CreateJsonFromPOJO.class.getDeclaredMethod("setDefaultValue", Field.class, Property.class);
		setDefaultValueMethod.setAccessible(true);
		Property property = new Property();
		setDefaultValueMethod.invoke(createJsonFromPOJO, mockField, property);
		assertEquals(42, ((Number) property.getDefaultValue()).intValue());
	}

	@Test
	void testSetCustomFieldValueFormatReadOnly() throws Exception {
		Field formatField = AnnotationTestClass.class.getDeclaredField("dateField");
		formatField.setAccessible(true);
		Property propertyFormat = new Property();
		Method methodFormat = CreateJsonFromPOJO.class.getDeclaredMethod("setCustomFieldValue", Field.class, Property.class);
		methodFormat.setAccessible(true);
		methodFormat.invoke(createJsonFromPOJO, formatField, propertyFormat);
		assertEquals("date-time", propertyFormat.getFormat());

		Field readOnlyField = AnnotationTestClass.class.getDeclaredField("readOnlyField");
		readOnlyField.setAccessible(true);
		Property propertyReadOnly = new Property();
		methodFormat.invoke(createJsonFromPOJO, readOnlyField, propertyReadOnly);
		assertEquals(Boolean.TRUE, propertyReadOnly.getReadOnly());
	}

	@Test
	void testMainMethod() throws Exception {
		String[] args = new String[0];
		CreateJsonFromPOJO.main(args);
		Assertions.assertNotNull(args);
	}

	// Sample POJO for testing
	public static class SamplePojo {
		@NotNull
		@JsonProperty("name")
		private String name;

		@NotEmpty
		@DefaultValue("42")
		private int age;

		@SuppressWarnings("unused")
		private boolean active;
		@SuppressWarnings("unused")
		private OffsetDateTime dateTime;
		@SuppressWarnings("unused")
		private SampleEnum sampleEnum;
		@SuppressWarnings("unused")
		private List<InnerPojo> innerList;
	}

	public static class InnerPojo {
		@SuppressWarnings("unused")
		private String innerField;
	}

	public enum SampleEnum {
		@SuppressWarnings("unused")
		VALUE1, VALUE2
	}
}
