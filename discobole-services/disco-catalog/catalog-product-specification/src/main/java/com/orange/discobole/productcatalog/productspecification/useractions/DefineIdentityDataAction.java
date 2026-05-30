// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.IndividualRole;
import com.orange.discobole.productcatalog.productspecification.constant.OrgnizationRole;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.PartyType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedResource;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Component("ProductSpecCreation.defineIdentityData")
public class DefineIdentityDataAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(DefineIdentityDataAction.class);

	private static final String DISCO_PS_IDENTITYDATA_MUST_NOTNULL = "DISCO_PS_IDENTITYDATA_MUST_NOTNULL";

	private static final String DISCO_PS_PROVIDE_IDORNAME = "DISCO_PS_PROVIDE_IDORNAME";

	private static final String DISCO_PS_VALIDITY_MUST_NOTNULL = "DISCO_PS_VALIDITY_MUST_NOTNULL";

	private static final String DISCO_PS_VALID_REFERREDTYPE = "DISCO_PS_VALID_REFERREDTYPE";

	private static final String DISCO_PS_VALID_ROLE = "DISCO_PS_VALID_ROLE";
			

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + DefineIdentityData.class.getSimpleName() + ".json");
			Object identityData = null;
			identityData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.IDENTITY_DATA)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(identityData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
	        throws ParameterException {

	    DefineIdentityData identityData = getIdentityDataFromTaskFlowUpdate(taskFlowUpdate);
	    String productSpecId = getProductSpecIdFromTransition(stateMachineTransition);

	    validateIdentityData(identityData);
	    
	    List<RelatedResource> relatedResources = processRelatedResources(identityData.getRelatedResource());
	    List<RelatedParty> relatedParties = processRelatedParties(identityData.getRelatedParty(), stateMachineTransition);

	    validateValidityPeriod(identityData);

	    productSpecService.initiateProductSpecDef(productSpecId, identityData.getIdentityData(), 
	            relatedParties, relatedResources, TimePeriodMapper.toGenerated(identityData.getValidityPeriod().getValidFor()), 
	            EntityType.PRODUCTSPECIFICATION);

	    return new HashMap<>();
	}

	private DefineIdentityData getIdentityDataFromTaskFlowUpdate(TaskFlowUpdate taskFlowUpdate) throws ParameterException {
	    Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
	            ProductSpecConstants.IDENTITY_DATA, characteristicList);
	    DefineIdentityData identityData = new DefineIdentityData();

	    if (characteristic != null) {
	        identityData = (DefineIdentityData) ValidationUtil.validatePojo(characteristic.getValue(),
	                "productSpecification", "defineIdentityData");
	    }

	    return identityData;
	}

	private String getProductSpecIdFromTransition(StateMachineTransition stateMachineTransition) {
	    return (String) stateMachineTransition.getVariablesFromUserActions().get(ProductSpecConstants.PRODUCT_SPEC_ID);
	}

	private void validateIdentityData(DefineIdentityData identityData) {
	    if (identityData.getIdentityData() == null) {
	        throw new DiscoManagedClientException(DISCO_PS_IDENTITYDATA_MUST_NOTNULL);
	    }
	}

	private List<RelatedResource> processRelatedResources(List<SelectRelatedResource> relatedResources) {
	    List<RelatedResource> relResource = new ArrayList<>();
	    if (relatedResources != null && !relatedResources.isEmpty()) {
	        for (SelectRelatedResource value : relatedResources) {
	            validateIdOrName(value.getId(), value.getName());
	            relResource.add(new RelatedResource().id(value.getId()).name(value.getName())
	                    .referredType(value.getReferredType()).role(value.getRole()));
	        }
	    }
	    return relResource;
	}

	private List<RelatedParty> processRelatedParties(List<SelectRelatedParty> relatedParties, StateMachineTransition stateMachineTransition) {
	    List<RelatedParty> relParty = new ArrayList<>();
	    if (relatedParties != null && !relatedParties.isEmpty()) {
	        for (SelectRelatedParty value : relatedParties) {
	            validateIdOrName(value.getId(), value.getName());
	            if (stateMachineTransition.getVariablesFromUserActions().containsKey(ProductSpecConstants.STOCK_ITEM_ID)) {
	                validateReferredTypeAndRole(value);
	            }
	            relParty.add(new RelatedParty().id(value.getId()).name(value.getName())
	                    .role(value.getRole()).referredType(value.getReferredType().toString()));
	        }
	    }
	    return relParty;
	}

	private void validateIdOrName(String id, String name) {
	    if ((id == null || id.isEmpty()) && (name == null || name.isEmpty())) {
	        throw new DiscoManagedClientException(DISCO_PS_PROVIDE_IDORNAME);
	    }
	}

	private void validateValidityPeriod(DefineIdentityData identityData) {
	    if (identityData.getValidityPeriod() == null || identityData.getValidityPeriod().getValidFor() == null) {
	        throw new DiscoManagedClientException(DISCO_PS_VALIDITY_MUST_NOTNULL);
	    }
	}

	
	/**
	 * validate 
	 * 
	 * @param selectedRelatedParty
	 */
	private void validateReferredTypeAndRole(SelectRelatedParty selectedRelatedParty) {
		boolean flag=true;
		if(PartyType.ORGANIZATION.equals(selectedRelatedParty.getReferredType()))
		{
			flag=validateOrgnizationRole(selectedRelatedParty);
		}
		else if(PartyType.INDIVIDUAL.equals(selectedRelatedParty.getReferredType()))
		{
			flag=validateIndividualRole(selectedRelatedParty);
		}
		else
		{
			throw new DiscoManagedClientException(DISCO_PS_VALID_REFERREDTYPE);
		}
		if(!flag)
		{
			throw new DiscoManagedClientException(DISCO_PS_VALID_ROLE);
		}
	}

	/**
	 * @param selectedRelatedParty
	 */
	private boolean validateIndividualRole(SelectRelatedParty selectedRelatedParty) {
		return Arrays.stream(IndividualRole.values()).anyMatch(e->e.getValue().equals(selectedRelatedParty.getRole()));
	}

	/**
	 * @param selectedRelatedParty
	 */
	private boolean validateOrgnizationRole(SelectRelatedParty selectedRelatedParty) {
		return Arrays.stream(OrgnizationRole.values()).anyMatch(e->e.getValue().equals(selectedRelatedParty.getRole()));
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.IDENTITY_DATA + "-" + characteristicIndex);
		return characteristicList;

	}

}
