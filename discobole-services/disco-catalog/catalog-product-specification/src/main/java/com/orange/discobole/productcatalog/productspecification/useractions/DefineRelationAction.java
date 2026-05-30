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
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.IndividualRole;
import com.orange.discobole.productcatalog.productspecification.constant.OrgnizationRole;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.AssociatePolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineRelationship;
import com.orange.discobole.productcatalog.productspecification.pojo.EntityRelationships;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Component("ProductSpecCreation.defRelationship")
public class DefineRelationAction implements UserAction {

	private static final String DISCO_PS_DEFINERELATIONSHIP_CANNOT_EMPTY = "DISCO_PS_DEFINERELATIONSHIP_CANNOT_EMPTY";

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + EntityRelationships.class.getSimpleName() + ".json");
			Object defineRelation = null;
			defineRelation = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.RELATION_SPEC)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(defineRelation)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		String productSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductSpecConstants.PRODUCT_SPEC_ID);
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.RELATION_SPEC, characteristicList);
		
		EntityRelationships entityRelationships = new EntityRelationships();
		if (null != characteristic) {
			entityRelationships = (EntityRelationships) ValidationUtil.validatePojo(characteristic.getValue(),
					"productSpecification", "entityRelationships");
		} else {
			throw new DiscoManagedClientException(DISCO_PS_DEFINERELATIONSHIP_CANNOT_EMPTY);
		}
		List<ProductSpecificationRelationship> relationships = new ArrayList<>();
		for (DefineRelationship rel : entityRelationships.getDefineRelationship()) {
			ProductSpecificationRelationship productSpecRel = new ProductSpecificationRelationship();
			productSpecRel.id(rel.getId()).relationshipType(rel.getRelationshipType())
					.validFor(TimePeriodMapper.toGenerated(rel.getValidFor()));
			relationships.add(productSpecRel);
		}

		List<PolicyRuleRef> policyRules = new ArrayList<>();
		for (AssociatePolicyRuleRef associatePolicy : entityRelationships.getAssociatePolicyRuleRef()) {
			PolicyRuleRef policyRule = new PolicyRuleRef();
			policyRule.id(associatePolicy.getId()).name(associatePolicy.getName());
			policyRules.add(policyRule);
		}

		productSpecService.updateProductSpecRel(productSpecId, relationships, policyRules);
		final Map<String, Object> variables = new HashMap<>();
		final List<Characteristic> characteristics = new ArrayList<>();

		List<String> individualRole = new ArrayList<>();
		for (IndividualRole value : IndividualRole.values()) {
			individualRole.add(value.toString());
		}

		characteristics.add(new ObjectCharacteristic().value(individualRole).name(ProductSpecConstants.INDIVIDUAL_ROLE)
				.valueType(String.class.getSimpleName()).type(ObjectCharacteristic.class.getSimpleName()));

		List<String> orgnizationRole = new ArrayList<>();
		for (OrgnizationRole value : OrgnizationRole.values()) {
			orgnizationRole.add(value.toString());
		}

		characteristics
				.add(new ObjectCharacteristic().value(orgnizationRole).name(ProductSpecConstants.ORGANIZATIONAL_ROLE)
						.valueType(String.class.getSimpleName()).type(ObjectCharacteristic.class.getSimpleName()));

		variables.put(TaskConstants.CHARACTERISTIC, characteristics);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.RELATION_SPEC + "-" + characteristicIndex);
		return characteristicList;
	}
}
