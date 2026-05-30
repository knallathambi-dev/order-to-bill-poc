// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
public class StateMachineUtil {
    public static final String GUARD = "guard";
    public static final String ON_METHOD = "onMethod";
    public static final String DESCRIPTION = "message";

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    private StateMachineUtil() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }

    public static String getStringValue(StateContext<String, String> context, String keyName) {
        return (String) context.getExtendedState().getVariables().getOrDefault(keyName, null);
    }

    public static Boolean getBooleanValue(StateContext<String, String> context, String keyName, boolean defaultValue) {
        return (Boolean) context.getExtendedState().getVariables().getOrDefault(keyName, defaultValue);
    }

    public static <T> T getObjectValue(StateContext<String, String> context, String keyName, Class<T> clazz) {
        try {
            Object o = context.getExtendedState().getVariables().get(keyName);
            if (o != null) {
                return mapper.convertValue(o, clazz);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unable to convert Object Value [{}]:", e.getMessage(), e);
        }
        return null;
    }

    public static <T> List<T> getListValue(Map<Object, Object> map, Object key, Class<T> elementType) {
        Object value = map.get(key);

        if (Objects.isNull(value)) {
            return Collections.emptyList();
        }

        if (value instanceof List<?> rawList) {
            List<T> typedList = new ArrayList<>();
            for (Object item : rawList) {
                if (elementType.isInstance(item)) {
                    typedList.add(elementType.cast(item));
                } else {
                    throw new IllegalArgumentException("Element in the list is not of the expected type: " + elementType.getName());
                }
            }
            return typedList;
        } else {
            throw new IllegalArgumentException("Value corresponding to the key is not a List.");
        }
    }

    public static <K, V> Map<K, V> getMapValue(Map<Object, Object> map, Object key) {
        try {
            Object rawMap = map.get(key);

            if (rawMap instanceof Map<?, ?>) {
                return getGenericMap((Map<?, ?>) rawMap);
            } else {
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            log.error("Unable to get Map Value [{}]:", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> getGenericMap(Map<?, ?> rawMap) {
        Map<K, V> genericMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            genericMap.put((K) entry.getKey(), (V) entry.getValue());
        }
        return genericMap;
    }

    public static void setGuardContext(StateContext<String, String> context, boolean result, String guardName) {
        Map<String, Boolean> guardMap = new HashMap<>();
        if (context.getExtendedState().getVariables().get(GUARD) != null) {
            guardMap = getMapValue(context.getExtendedState().getVariables(), GUARD);
        }
        guardMap.put(guardName, result);
        context.getExtendedState().getVariables().put(GUARD, guardMap);
    }

    public static Mono<Boolean> getGuardResult(StateContext<String, String> context, String guardName) {
        Map<String, Boolean> guardVariable = getMapValue(context.getExtendedState().getVariables(), GUARD);
        Boolean guardResult = guardVariable.getOrDefault(guardName, Boolean.FALSE);
        return Mono.just(guardResult);
    }

    public static Boolean isReExecutionAction(StateContext<String, String> context) {
        return Objects.nonNull(context.getExtendedState().getVariables().get(ON_METHOD));
    }

    public static void setDescriptionContext(StateContext<String, String> context, String message) {
        context.getExtendedState().getVariables().put(DESCRIPTION, message);
    }

    public static void setNextTaskToNullInCaseOfUnreachableService(StateContext<String, String> context, String currentContextDescription, String unreachableServiceDescription) {
        if (unreachableServiceDescription.equals(currentContextDescription)) {
            setNextTaskToNull(context);
        }
    }

    public static void setNextTaskToNull(StateContext<String, String> context) {
        context.getStateMachine().getTransitions().clear();
    }
}