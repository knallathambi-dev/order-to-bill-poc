// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.MarketSegmentEnum;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingMarketSegment;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingSaleChannel;
import com.orange.discobole.productcatalog.productoffering.pojo.ManageProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractProductOfferingIdentityData;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.CommonUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class DefineContractProductOffDescAction provides the user input to
 * describe Contract product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingCreation.contractDescription")
public class DefineContractProductOffDescAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(DefineContractProductOffDescAction.class);

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingService productOfferingService;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productoffering/"
					+ DefineContractProductOfferingIdentityData.class.getSimpleName() + ".json");
			Object contractProductOfferingIdentityData = null;
			contractProductOfferingIdentityData = objectMapper.readValue(file, Object.class);
			characteristicList
					.add(new CharacteristicSpecification().name(ProductOffConstants.CONTRACT_IDENTITY_DATA)
							.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
							.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
									.value(contractProductOfferingIdentityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to describe product offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
			final TaskFlowUpdate taskFlowUpdate) throws ParameterException {
		LOGGER.info("DefineContractProductOffDescAction perform method");
		DefineContractProductOfferingIdentityData identityData = new DefineContractProductOfferingIdentityData();
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.CONTRACT_IDENTITY_DATA, characteristicList);
		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		if (null != characteristic) {
			identityData = (DefineContractProductOfferingIdentityData) ValidationUtil.validatePojo(
					characteristic.getValue(), "productOffering", "defineContractProductOfferingIdentityData");
		}
		if (null == identityData.getDefineData()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_IDENTITY_DATA_CANNOT_NULL);
		}
		Set<String> channelIds = new HashSet<>();
		if (null != identityData.getChannels()) {
			channelIds = identityData.getChannels().stream().map(DefineProductOfferingSaleChannel::getChannelId)
					.collect(Collectors.toSet());
		}
		Set<String> marketSegmentIds = new HashSet<>();
		if (null != identityData.getMarketSegments()) {
			marketSegmentIds = identityData.getMarketSegments().stream()
					.map(DefineProductOfferingMarketSegment::getSegmentId)
					.collect(Collectors.toSet());
		}
		Set<RelatedParty> relParty = new HashSet<>();
		if (null != identityData.getRelatedParties()) {
			for (SelectRelatedParty value : identityData.getRelatedParties()) {
				if (StringUtils.isBlank(value.getId()) && StringUtils.isBlank(value.getName())) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_PROVIDE_IDORNAME);
				}
				relParty.add(new RelatedParty().id(value.getId()).name(value.getName()).role(value.getRole())
						.referredType(value.getReferredType().toString()));
			}
		}
		Set<ProductOfferingTerm> prodOffTerms = new HashSet<>();
		if (null != identityData.getPoTerms()) {
			for (ManageProductOfferingTerm value : identityData.getPoTerms()) {
				prodOffTerms.add(new ProductOfferingTerm().duration(CommonUtil.convertToDuration(value.getDuration()))
						.name(value.getName()).description(value.getDescription())
						.validFor(TimePeriodMapper.toGenerated(value.getValidFor())));
			}
		}
		if (null == identityData.getValidityPeriod()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_VALIDITY_MUST_NOTNULL);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		productOfferingService.defineContractProductOffDesc(productOffId, identityData.getDefineData(), channelIds,
				marketSegmentIds, relParty, prodOffTerms,
				TimePeriodMapper.toGenerated(identityData.getValidityPeriod().getValidFor()),
				ProductOfferingType.fromValue((String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOffConstants.PRODUCTOFFERINGTYPE)),
				"Contract Product Offering initial description", lastUpdate);
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
				+ ProductOffConstants.CONTRACT_IDENTITY_DATA + "-" + characteristicIndex);
		return characteristicList;

	}
}
