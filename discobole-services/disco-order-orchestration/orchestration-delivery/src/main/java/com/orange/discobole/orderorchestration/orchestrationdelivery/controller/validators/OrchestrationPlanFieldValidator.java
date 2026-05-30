// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.controller.validators;

import com.orange.discobole.orderorchestration.exception.model.validations.PlanApiQueryParamException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.productinventory.dto.v1.Characteristic;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import com.orange.discobole.productinventory.dto.v1.StringCharacteristic;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.INVALID_QUERY_STRING_PARAMETER;

@Service
public class OrchestrationPlanFieldValidator implements FieldsValidator {
    @Override
    public Field validate(String fieldParameter) {
        if (fieldParameter.endsWith(".")) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Query param [ Fields ] should valid");
        }
        String[] fieldParam = fieldParameter.split("\\.");

        if (fieldParam.length == 0) {
            return null;
        }
        try {
            Field field = OrchestrationPlan.class.getDeclaredField(fieldParam[0]);
            for (int i = 1; i < fieldParam.length; i++) {
                if (List.class.isAssignableFrom(field.getType())) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> elementType = (Class<?>) listType.getActualTypeArguments()[0];
                    if (elementType.isAssignableFrom(StringCharacteristic.class)) {
                        field = getStringCharacteristicField(fieldParam[i]);
                    } else {
                        field = elementType.getDeclaredField(fieldParam[i]);
                    }
                } else if (field.getType().isAssignableFrom(ProductRef.class)) {
                    field = ProductRef.class.getDeclaredField(fieldParam[i]);
                } else {
                    field = field.getType().getDeclaredField(fieldParam[i]);
                }
            }
            return field;
        } catch (NoSuchFieldException e) {
            throw new PlanApiQueryParamException(INVALID_QUERY_STRING_PARAMETER, 28, "Query param [ Fields ] should valid");
        }
    }

    private static Field getStringCharacteristicField(String fieldParam) throws NoSuchFieldException {
        Field field;
        try {
            field = StringCharacteristic.class.getDeclaredField(fieldParam);
        } catch (NoSuchFieldException ex) {
            field = Characteristic.class.getDeclaredField(fieldParam);
        }
        return field;
    }

}
