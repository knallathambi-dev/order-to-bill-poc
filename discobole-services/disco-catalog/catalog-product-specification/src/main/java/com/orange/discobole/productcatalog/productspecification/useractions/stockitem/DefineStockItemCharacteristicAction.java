// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.stockitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.IndividualRole;
import com.orange.discobole.productcatalog.productspecification.constant.OrgnizationRole;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.CharacteristicValueSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.PickStockCharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.ConverterUtil;
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
 * @author Varshika Choudhary
 * @since 1.0
 *
 */
@Component("ProductSpecCreation.defStockItemCharacteristics")
public class DefineStockItemCharacteristicAction implements UserAction {


    private static final String DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY = "DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY";

	@Resource
    private ProductSpecService productSpecService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private QueryService queryService;

    @Resource
    private ConfigurableProperties configurableProperties;

    private List<CharacteristicSpecification> characteristicList;

    @PostConstruct
    public void init() {
        characteristicList = new ArrayList<>();
        try {
            String file = FileUtil.read("/schemas/" + PickStockCharacteristicSpecification.class.getSimpleName() + ".json");
            Object charSpec = null;
            charSpec = objectMapper.readValue(file, Object.class);
            characteristicList.add(new CharacteristicSpecification()
                    .name(ProductSpecConstants.STOCK_ITEM_CHARACTERISTICS).valueType(List.class.getSimpleName())
                    .minCardinality(1).maxCardinality(1)
                    .characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
                            .value(charSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
            throws ParameterException {
        String productSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
                .get(ProductSpecConstants.PRODUCT_SPEC_ID);
        Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
                ProductSpecConstants.STOCK_ITEM_CHARACTERISTICS, characteristicList);
        List<ProductSpecificationCharacteristic> prodSpecCharacteristics = new ArrayList<>();
        if (null != characteristic) {
            createCharacteristicSpecification(characteristic, prodSpecCharacteristics);
        }else{
            throw new DiscoManagedClientException(DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY);
        }
        productSpecService.updateStockItemProductSpecCharacteristics(productSpecId, prodSpecCharacteristics);
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

    @SuppressWarnings("unchecked")
    private void createCharacteristicSpecification(Characteristic characteristic,
                                                   List<ProductSpecificationCharacteristic> stockItemProductSpecCharacteristics) {
        List<PickStockCharacteristicSpecification> charSpec = (List<PickStockCharacteristicSpecification>) (Object) ValidationUtil
                .validateArrayOfPojo(characteristic.getValue(), "productSpecification","pickStockCharacteristicSpecification");
        for (PickStockCharacteristicSpecification pickStockCharacteristicSpecification : charSpec) {
            ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic();
            List<ProductSpecificationCharacteristicValue> characteristicValueList = new ArrayList<>();
            if (null != pickStockCharacteristicSpecification.getCharacteristicValueSpecification()) {
                for (CharacteristicValueSpecification characteristicValueSpecification : pickStockCharacteristicSpecification
                        .getCharacteristicValueSpecification()) {
                	if(characteristicValueSpecification.getIsSelectable() == null) {
						characteristicValueSpecification.setIsSelectable(true);
					}
                    characteristicValueList.add(ConverterUtil.convert(characteristicValueSpecification));
                }
            }

            productSpecificationCharacteristic.id(pickStockCharacteristicSpecification.getId()).name(pickStockCharacteristicSpecification.getName()).description(pickStockCharacteristicSpecification.getDescription())
                    .validFor(TimePeriodMapper.toGenerated(pickStockCharacteristicSpecification.getValidFor()))
                    .configurable(pickStockCharacteristicSpecification.getConfigurable()).isUnique(pickStockCharacteristicSpecification.getIsUnique())
					.extensible(pickStockCharacteristicSpecification.getExtensible())
                    .productSpecCharacteristicValue(characteristicValueList);
            stockItemProductSpecCharacteristics.add(productSpecificationCharacteristic);
        }
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        int characteristicIndex = 0;
        characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
                + ProductSpecConstants.STOCK_ITEM_CHARACTERISTICS + "-" + characteristicIndex);
        return characteristicList;
    }

}