// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanErrorMessage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;

/**
 * this class allow copy from source object ot destination if field is not null
 * regarding arrays and sets matching the corresponding object from the source object to the matching target object based on id field
 * this class is created for testing purposes
 */
public class DeepCopyUtil {

    public static <T> void copyNonNullProperties(T source, T target) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target must not be null");
        }

        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            Object value = field.get(source);

            if (value != null) {
                Class<?> fieldType = field.getType();

                if (List.class.isAssignableFrom(fieldType)) {
                    List<?> sourceList = (List<?>) value;
                    List<?> targetList = (List<?>) field.get(target);
                    copyNonNullListById(sourceList, targetList, field, target);
                } else if (Set.class.isAssignableFrom(fieldType)) {
                    Set<?> sourceSet = (Set<?>) value;
                    Set<?> targetSet = (Set<?>) field.get(target);
                    copyNonNullSetById(sourceSet, targetSet, field, target);
                } else if (isPrimitiveOrWrapperOrString(fieldType)) {
                    field.set(target, value);
                } else {
                    Object nestedTarget = field.get(target);
                    if (nestedTarget == null) {
                        nestedTarget = fieldType.newInstance();
                        field.set(target, nestedTarget);
                    }
                    copyNonNullProperties(value, nestedTarget);
                }
            }
        }
    }

    private static void copyNonNullListById(List<?> sourceList, List<?> targetList, Field field, Object target) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        if (!sourceList.isEmpty() && (sourceList.get(0) instanceof OrchestrationNodeErrorMessage || sourceList.get(0) instanceof OrchestrationPlanErrorMessage)) {
            field.set(target, sourceList);
            return;
        }

        if (targetList == null) {
            targetList = new ArrayList<>();
            field.set(target, targetList);
        }

        for (Object sourceItem : sourceList) {
            Object sourceId = getFieldValue(sourceItem);

            for (Object targetItem : targetList) {
                Object targetId = getFieldValue(targetItem);

                if (Objects.equals(sourceId, targetId)) {
                    copyNonNullProperties(sourceItem, targetItem);
                    break;
                }
            }
        }
    }

    private static void copyNonNullSetById(Set<?> sourceSet, Set<?> targetSet, Field field, Object target) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        if (sourceSet.iterator().hasNext() && sourceSet.iterator().next() instanceof Characteristic) {
            field.set(target, sourceSet);
            return;
        }

        if (targetSet == null) {
            targetSet = new HashSet<>();
            field.set(target, targetSet);
        }

        for (Object sourceItem : sourceSet) {
            Object sourceId = getFieldValue(sourceItem);
            if (Objects.nonNull(sourceId)) {
                for (Object targetItem : targetSet) {
                    Object targetId = getFieldValue(targetItem);

                    if (Objects.equals(sourceId, targetId)) {
                        copyNonNullProperties(sourceItem, targetItem);
                        break;
                    }
                }
            }
        }
    }

    private static Object getFieldValue(Object object) throws IllegalAccessException {
        try {
            Field field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static boolean isPrimitiveOrWrapperOrString(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Character.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Short.class ||
                clazz.isEnum() ||
                clazz.isAssignableFrom(Instant.class);
    }
}


