// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import java.util.List;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.AssociatePOPtoOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import jakarta.validation.ConstraintViolationException;
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
import com.orange.discobole.productcatalog.productoffering.deserialier.BeanValidationDeserializer;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.*;

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
	 * Gets class to convert object in particular pojo.
	 *
	 * @param className the class name
	 * @return the class to convert
	 */
	public static Class<?> getClassToConvert(String className) {
		switch (className) {
		case "SelectProductOfferingType":
			return SelectProductOfferingType.class;
		case "selectSupportEntity":
			return SelectSupportEntity.class;
		case "selectPO":
			return SelectPO.class;
		case "defineIdentityData":
			return DefineIdentityData.class;
		case "defineContractProductOfferingIdentityData":
			return DefineContractProductOfferingIdentityData.class;
		case "pickOperationSpecification":
			return PickOperationSpecification.class;
		case "pickCharacteristicSpecification":
			return PickCharacteristicSpecification.class;
		case "pickUsageSpecification":
			return PickUsageSpecification.class;
		case "entityRelationships":
			return EntityRelationships.class;
		case "selectRelatedParty":
			return SelectRelatedParty.class;
		case "selectRelatedResource":
			return SelectRelatedResource.class;
		case "defineEntityValidityPeriod":
			return DefineEntityValidityPeriod.class;
		case "validateEntityOperation":
			return ValidateEntityOperation.class;
		case "cancelEntityOperation":
			return CancelEntityOperation.class;
		case "manageProductOfferingTerm":
			return ManageProductOfferingTerm.class;
		case "defineProductOfferingIdentityData":
			return DefineProductOfferingIdentityData.class;
		case "defineCategory":
			return DefineProductOfferingCategory.class;
		case "defineProductOfferingSaleChannel":
			return DefineProductOfferingSaleChannel.class;
		case "defineProductOfferingMarketSegment":
			return DefineProductOfferingMarketSegment.class;
		case "associatePOPtoOperationSpecification":
			return AssociatePOPtoOperationSpecification.class;
		case "definePORelationship":
			return com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineRelationship.class;
		case "defineBundledPOOperationSpecification":
			return DefineBundledOperationSpecification.class;
		case "definePOOperationSpecification":
			return DefineOperationSpecification.class;
		case "pickStockCharacteristicSpecification":
			return PickStockCharacteristicSpecification.class;
		case "pickAtomicProductOfferingCharacteristic":
			return PickAtomicProductOfferingCharacteristic.class;
		case "selectProductSpecification":
			return SelectProductSpecification.class;
		case "defineProductOfferingBundling":
			return ManageProductOfferingBundling.class;
		case "selectProductOfferingVersion":
			return SelectProductOfferingVersion.class;
		case "modifyDefineProductOfferingIdentityData":
			return ModifyDefineProductOfferingIdentityData.class;
		case "modifyDefineContractProductOfferingIdentityData":
			return ModifyDefineContractProductOfferingIdentityData.class;
		case "policyRuleAssociation":
			return PolicyRuleAssociation.class;
		case "selectAllowedAction":
			return SelectAllowedActionData.class;

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
		LOGGER.info("In validateArrayOfPojo method with entity : {} ",entity);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(getSimpleModule());
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			Class<?> entityClass = getClassToConvert(className);
			
			return objectMapper.readValue(objectMapper.writeValueAsString(value), entityClass);
		} catch (InvalidFormatException e) {
			List<JsonMappingException.Reference> references = e.getPath();
			String fieldName = references.get(0).getFieldName() == null ? references.get(2).getFieldName()
					: references.get(0).getFieldName();
			throw new InvalidParameterException("Cannot deserialize, format of " + fieldName
					+ " is wrong. Should be in yyyy-MM-dd'T'HH:mm:ss.SSSX pattern");
		} catch (UnrecognizedPropertyException e) {
			return handleUnrecognizedException(e);
		} catch (MismatchedInputException e) {
			return handlerMismatchedInputException(e);
		} catch (ConstraintViolationException | JsonMappingException e) {
			int index = e.getMessage().indexOf("(");
			String result = (index >0) ? e.getMessage().substring(0, index-1) : e.getMessage();
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_INVALID_JSON_BODY,e.getMessage(),result);
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
		LOGGER.info("In validateArrayOfPojo method with entity : {} ",entity);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(getSimpleModule());
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			Class<?> entityClass = getClassToConvert(className);
			
			return objectMapper.readValue(objectMapper.writeValueAsString(value),
					objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass));
		} catch (InvalidFormatException e) {
			List<JsonMappingException.Reference> references = e.getPath();
			String fieldName = references.get(0).getFieldName() == null ? references.get(2).getFieldName()
					: references.get(0).getFieldName();
			throw new InvalidParameterException("Cannot deserialize, format of " + fieldName
					+ " is wrong. Should be in yyyy-MM-dd'T'HH:mm:ss.SSSX pattern");
		} catch (UnrecognizedPropertyException e) {
			return handleUnrecognizedException(e);
		} catch (MismatchedInputException e) {
			return handlerMismatchedInputException(e);
		} catch (ConstraintViolationException | JsonMappingException e) {
			throw new InvalidParameterException(e.getMessage());
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}
	
	private static List<Object> handleUnrecognizedException(UnrecognizedPropertyException e) {
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

	private static List<Object> handlerMismatchedInputException(MismatchedInputException e) {
		List<JsonMappingException.Reference> references = e.getPath();
		String fieldName = references.get(0).getFieldName() == null ? references.get(1).getFieldName()
				: references.get(0).getFieldName();
		int size = references.size();
		if (size > 2) {
			throw new InvalidParameterException(
					"Cannot deserialize, the type of property " + references.get(size - 1).getFieldName() + " in "
							+ fieldName + " does not match with the schema");
		} else {
			throw new InvalidParameterException(
					"Cannot deserialize, the type of property " + fieldName + " does not match with the schema");
		}
	}

}
