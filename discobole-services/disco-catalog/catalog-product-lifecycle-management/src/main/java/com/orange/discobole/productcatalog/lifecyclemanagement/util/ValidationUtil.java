// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.util;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productcatalog.lifecyclemanagement.deserialier.BeanValidationDeserializer;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.DefineLifecycleState;

import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * Utility class, use to map the object into required pojo.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ValidationUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(ValidationUtil.class);
	private static final String DISCO_INVALID_JSON_BODY="DISCO_INVALID_JSON_BODY";
	private ValidationUtil() {
	}

	/**
	 * Gets simple module to register with object mapper to call a custom validator.
	 *
	 * @return the simple module
	 */
	public static SimpleModule getSimpleModule() {
		SimpleModule validationModule = new SimpleModule();
		validationModule.setDeserializerModifier(new BeanDeserializerModifier() {
			@Override
			public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
					JsonDeserializer<?> deserializer) {
				if (deserializer instanceof BeanDeserializer) {
					return new BeanValidationDeserializer((BeanDeserializer) deserializer);
				}

				return deserializer;
			}
		});
		return validationModule;
	}
	
	/**
	 * Gets class of LifeCycle to convert object in particular pojo.
	 *
	 * @param className the class name
	 * @return the class to convert
	 */
	

	private static Class<?> getClassToConvertForLifeCycle(String className) {
		if (className.equals("DefineLifecycleState")) {
			return DefineLifecycleState.class;
		} else {
			throw new IllegalArgumentException("Converting into " + className + " is not possible");
		}
	}

	/**
	 * Validate object.
	 *
	 * @param value     the data value that needs to e validated
	 * @param className the class name is use to map the object into particular
	 *                  class
	 * @return the object of required class
	 */
	public static Object validatePojo(Object value, String entity, String className) {
		LOGGER.info("validatePojo method with entity : {} and value : {}", entity, value);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(getSimpleModule());
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			Class<?>  entityClass = getClassToConvertForLifeCycle(className);
			return objectMapper.readValue(objectMapper.writeValueAsString(value), entityClass);
			
		} catch (InvalidFormatException e) {
			List<JsonMappingException.Reference> references = e.getPath();
			String fieldName = references.get(0).getFieldName() == null ? references.get(2).getFieldName()
					: references.get(0).getFieldName();
			throw new InvalidParameterException("Cannot deserialize, format of " + fieldName
					+ " is wrong. Should be in yyyy-MM-dd'T'HH:mm:ss.SSSX pattern");
		} catch (UnrecognizedPropertyException e) {
			return methodToThrowUnrecognizedPropertyException(e);
		} catch (MismatchedInputException e) {
			return methodToThrowMismatchedInputException(e);
		} catch (ConstraintViolationException | JsonMappingException e) {

			int index = e.getMessage().indexOf("(");
			String result = (index >0) ? e.getMessage().substring(0, index-1) : e.getMessage();
			throw new DiscoManagedClientException(DISCO_INVALID_JSON_BODY,e.getMessage(),result);
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}


	/**
	 * this method will throw UnrecognizedProperty Exception
	 * @param UnrecognizedPropertyException e
	 * @return 
	 */
	private static List<Object> methodToThrowUnrecognizedPropertyException(UnrecognizedPropertyException e) {
		List<JsonMappingException.Reference> references = e.getPath();
		String fieldName = references.get(0).getFieldName() == null ? references.get(1).getFieldName()
				: references.get(0).getFieldName();
		int size = references.size();
		if (size > 2) {
			throw new InvalidParameterException("Cannot deserialize, unrecognized property "
					+ references.get(size - 1).getFieldName() + " in " + fieldName);
		} else {
			throw new InvalidParameterException("Cannot deserialize, unrecognized property found " + fieldName);
		}
	}
	
	/**
	 * this method will throw MismatchedInput Exception
	 * @param MismatchedInputException e
	 * @return
	 */
	private static List<Object> methodToThrowMismatchedInputException(MismatchedInputException e) {
		List<JsonMappingException.Reference> references = e.getPath();
		String fieldName = references.get(0).getFieldName() == null ? references.get(1).getFieldName()
				: references.get(0).getFieldName();
		int size = references.size();
		if (size > 2) {
			throw new InvalidParameterException("Cannot deserialize, the type of property "
					+ references.get(size - 1).getFieldName() + " in " + fieldName + " does not match with the schema");
		} else {
			throw new InvalidParameterException(
					"Cannot deserialize, the type of property " + fieldName + " does not match with the schema");
		}
	}
	
}
