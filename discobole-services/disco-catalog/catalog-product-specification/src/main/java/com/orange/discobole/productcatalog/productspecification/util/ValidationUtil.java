// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.util;

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
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.deserialier.BeanValidationDeserializer;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.pojo.*;
import com.orange.discobole.productcatalog.productspecification.pojo.modify.ModifyIdentityData;

import jakarta.validation.ConstraintViolationException;

import java.util.List;


/**
 * Utility class, use to map the object into required pojo.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ValidationUtil {
    
	private ValidationUtil() {
	}
	
	private static final String SCHEMANOTMATCHED = " does not match with the schema";  
	private static final String CANNOTDESERIALIZE = "Cannot deserialize, the type of property ";

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
	 * Gets class to convert object in particular pojo.
	 *
	 * @param className the class name
	 * @return the class to convert
	 */
	public static Class getClassToConvert(String className) {
		switch (className) {
		case "selectSupportEntity":
			return SelectSupportEntity.class;
		case "defineIdentityData":
			return DefineIdentityData.class;
		case "pickCharacteristicSpecification":
			return PickCharacteristicSpecification.class;
		case "entityRelationships":
			return EntityRelationships.class;
		case "validateEntityOperation":
			return ValidateEntityOperation.class;
		case "cancelEntityOperation":
			return CancelEntityOperation.class;
		case "pickStockCharacteristicSpecification":
			return PickStockCharacteristicSpecification.class;
		case "selectProductSpecification":
			return SelectProductSpecification.class;
		case "selectProductSpecificationVersion":
			return SelectProductSpecificationVersion.class;
		case "modifyDefineIdentityData":
			return ModifyIdentityData.class;
		default:
			throw new IllegalArgumentException("Converting into " + className + " is not possible");
		}
	}


	/**
	 * Validate object.
	 *
	 * @param value     the data value that needs to be validated
	 * @param className the class name used to map the object into a particular class
	 * @return the object of the required class
	 */
	public static Object validatePojo(Object value, String entity, String className) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        objectMapper.registerModule(getSimpleModule());
	        objectMapper.registerModule(new JavaTimeModule());
	        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	        
	        Class<?> entityClass = getClassToConvert(className);
	        return objectMapper.readValue(objectMapper.writeValueAsString(value), entityClass);
	    } catch (InvalidFormatException e) {
	        throw new InvalidParameterException(
	            "Cannot deserialize, format of " + extractFieldName(e) + 
	            " is wrong. Should be in yyyy-MM-dd'T'HH:mm:ss.SSSX pattern"
	        );
	    } catch (UnrecognizedPropertyException e) {
	        throw new InvalidParameterException(
	            "Cannot deserialize, unrecognized property found " + extractFieldName(e)
	        );
	    } catch (MismatchedInputException e) {
	        throw new InvalidParameterException(
	            "Cannot deserialize " + extractFieldName(e) + " - Schema does not match"
	        );
	    } catch (ConstraintViolationException | JsonMappingException e) {
			int index = e.getMessage().indexOf("(");
			String result = (index >0) ? e.getMessage().substring(0, index-1) : e.getMessage();
			throw new DiscoManagedClientException(ProductSpecConstants.DISCO_INVALID_JSON_BODY,e.getMessage(),result);
	    } catch (Exception e) {
	        throw new DiscoClientException(e.getMessage());
	    }
	}

	/**
	 * Extracts the most relevant field name from a JsonMappingException.
	 * 
	 * @param e the JsonMappingException
	 * @return the relevant field name
	 */
	private static String extractFieldName(JsonMappingException e) {
	    List<JsonMappingException.Reference> references = e.getPath();
	    if (references.isEmpty()) {
	        return "unknown";
	    }
	    return references.get(references.size() - 1).getFieldName();
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
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        objectMapper.registerModule(getSimpleModule());
	        objectMapper.registerModule(new JavaTimeModule());
	        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        Class<?> entityClass = getClassToConvert(className);
	        return objectMapper.readValue(objectMapper.writeValueAsString(value),
	                objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass));
	    } catch (InvalidFormatException e) {
	        throw new InvalidParameterException("Cannot deserialize, format of " +
	                getFieldNameFromException(e, 2) +
	                " is wrong. Should be in yyyy-MM-dd'T'HH:mm:ss.SSSX pattern");
	    } catch (UnrecognizedPropertyException e) {
	        throw new InvalidParameterException("Cannot deserialize, unrecognized property " +
	                getFieldNameFromException(e, 1));
	    } catch (MismatchedInputException e) {
	        throw new InvalidParameterException(CANNOTDESERIALIZE +
	                getFieldNameFromException(e, 1) + SCHEMANOTMATCHED);
	    } catch (ConstraintViolationException | JsonMappingException e) {
	        throw new InvalidParameterException(e.getMessage());
	    } catch (Exception e) {
	        throw new DiscoClientException(e.getMessage());
	    }
	}

	/**
	 * Extracts the field name from a JsonMappingException to provide a meaningful error message.
	 */
	private static String getFieldNameFromException(JsonMappingException e, int fallbackIndex) {
	    List<JsonMappingException.Reference> references = e.getPath();
	    int size = references.size();
	    return size > fallbackIndex ? references.get(size - 1).getFieldName() : references.get(0).getFieldName();
	}


}
