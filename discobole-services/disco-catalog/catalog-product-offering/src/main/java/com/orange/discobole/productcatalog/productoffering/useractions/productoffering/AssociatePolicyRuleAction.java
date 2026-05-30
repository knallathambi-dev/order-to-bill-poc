// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.AssociatePolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineRelationship;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.EntityRelationships;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PolicyRuleAssociation;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class DefineProductOfferingRelatioshipAction provides the user input to
 * define relationship for product offering.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Component("ProductOfferingCreation.policyRuleAssociation")
public class AssociatePolicyRuleAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productoffering/" + PolicyRuleAssociation.class.getSimpleName() + ".json");
			Object defineRelation = null;
			defineRelation = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.POLICY_RULE_ASSOCIATION)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(defineRelation)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to define relationships for product
	 * offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) {
		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.POLICY_RULE_ASSOCIATION, characteristicList);
		PolicyRuleAssociation entityRelationships = new PolicyRuleAssociation();
		if (null != characteristic) {
			entityRelationships = (PolicyRuleAssociation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productOffering", "policyRuleAssociation");
		}
		
		
		List<PolicyRuleRef> policyRules = new ArrayList<>();
		if(entityRelationships != null) {
			for (AssociatePolicyRuleRef associatePolicy :
				  entityRelationships.getAssociatePolicyRuleRef()) { PolicyRuleRef policyRule =
				  new PolicyRuleRef();
				  policyRule.id(associatePolicy.getId()).name(associatePolicy.getName());
				  policyRules.add(policyRule); }	
		}
		  
		  productOfferingService.defineProductOfferingPolicyRuleAssociation(productOffId,policyRules);
		 
		return new HashMap<>();
	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductOffConstants.RELATIONSHIP + "-" + characteristicIndex);
		return characteristicList;
	}

}
