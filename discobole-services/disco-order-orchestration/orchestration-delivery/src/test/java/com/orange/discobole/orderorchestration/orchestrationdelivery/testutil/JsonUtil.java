// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.testutil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static <T> T readObjectFromString(String json, TypeReference<T> typeReference) {
        T result;

        try {
            result = objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static <T> T readObjectFromFile(String filePath, TypeReference<T> typeReference) {
        return readObjectFromString(readStringFromFile(filePath), typeReference);
    }

    public static String readStringFromFile(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist at path: " + filePath);
        }

        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readObjectFromResource(String resourcePath, TypeReference<T> typeReference) {
        URL resource = JsonUtil.class.getResource(resourcePath);
        if (Objects.nonNull(resource)) {
            return readObjectFromFile(resource.getPath(), typeReference);
        } else {
            throw new RuntimeException("The following resource cannot be found "+ resourcePath);
        }
    }

    public static String readStringFromResource(String resourcePath) {
        URL resource = JsonUtil.class.getResource(resourcePath);
        if (Objects.nonNull(resource)) {
            return readStringFromFile(JsonUtil.class.getResource(resourcePath).getPath());
        } else {
            throw new RuntimeException("The following resource cannot be found "+ resourcePath);
        }
    }

    public static <T> T readStringToObject(String value) {
        try {
            return objectMapper.readValue(toJsonStringFromObject(value), new TypeReference<T>(){} );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonStringFromObject(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
