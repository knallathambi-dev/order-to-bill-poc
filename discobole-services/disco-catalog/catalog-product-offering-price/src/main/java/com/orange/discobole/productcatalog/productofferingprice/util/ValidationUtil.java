// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.util;

import java.util.List;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productcatalog.productofferingprice.deserialier.BeanValidationDeserializer;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.pojo.ValidateEntityOperation;

import jakarta.validation.ConstraintViolationException;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
/**
 * Utility class, use to map the object into required pojo.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ValidationUtil {

	private static final Logger LOGGER = LogManager.getLogger(ValidationUtil.class);

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
	 * Gets class of POP to convert object in particular pojo.
	 *
	 * @param className the class name
	 * @return the class to convert
	 */
	public static Class<?> getClassToConvertPOP(String className) {
		switch (className) {
			case "selectPOPType":
				return SelectPOPType.class;
			case "defineProductOfferingPriceChargeIdentity":
				return DefineProductOfferingPriceChargeIdentity.class;
			case "defineProductOfferingPriceAlterationIdentityData":
				return DefineProductOfferingPriceAlterationIdentity.class;
			case "defineProductOfferingPriceTaxAlterationIdentityData":
				return DefineTaxProductOfferingPriceAlterationIdentity.class;
			case "defineProductOfferingPriceInstallmentChargeIdentityData":
				return DefineInstallmentChargeProductOfferingPriceIdentity.class;
			case "defineRelationship":
				return com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineRelationship.class;
			case "definePOPStatusValidityPeriod":
				return DefinePOPStatusValidityPeriod.class;
			case "cancelEntityOperation":
				return CancelEntityOperation.class;
			case "selectPOP":
				return SelectPOP.class;
			case "validateEntityOperation":
				return ValidateEntityOperation.class;
			case "selectProductOfferingPriceVersion":
				return SelectProductOfferingPriceVersion.class;
			default:
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
			Class<?> entityClass = getClassToConvertPOP(className);
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
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_INVALID_JSON_BODY,e.getMessage(),result);
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Validate object.
	 *
	 * @param value     the data value that needs to e validated
	 * @param className the class name is use to map the object into particular
	 *                  class
	 * @return the list of object of required class
	 */
	public static List<Object> validateArrayOfPojo(Object value, String entity, String className) {
		LOGGER.info("validatePojo method with entity : {} and value : {}", entity, value);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(getSimpleModule());
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			Class<?> entityClass = getClassToConvertPOP(className);
			return objectMapper.readValue(objectMapper.writeValueAsString(value),
					objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass));
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
			throw new InvalidParameterException(e.getMessage());
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
