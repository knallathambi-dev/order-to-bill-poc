// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.customAnnotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jsonschema2pojo.AbstractAnnotator;

import java.util.List;

/**
 * <p><b>LombokAnnotator</b></p>
 * Annotator implementation that adds support to Lombok from JSON schemas.
 * It will replace all getters, setters and builders with Lombok annotations.
 */
public class LombokAnnotator extends AbstractAnnotator {
    /**
     * This will set up all properties as private.
     *
     * @param field        Field to be set.
     * @param clazz        Class type.
     * @param propertyName Name of property.
     * @param propertyNode Node of property.
     */
    @Override
    public void propertyField(
            JFieldVar field,
            JDefinedClass clazz,
            String propertyName,
            JsonNode propertyNode
    ) {
        field.mods().setPrivate();
        super.propertyField(field, clazz, propertyName, propertyNode);
    }

    /**
     * Add to the class some of the lombok annotations.
     *
     * @param clazz        Class type.
     * @param propertyNode Node of property.
     */
    @Override
    public void propertyInclusion(JDefinedClass clazz, JsonNode propertyNode) {
        clazz.annotate(Data.class);
        clazz.annotate(NoArgsConstructor.class);
        clazz.annotate(AllArgsConstructor.class);
        JsonNode superClassTitle = propertyNode.get("title");

        if (superClassTitle != null && List.of("BaseEvent", "Command", "Characteristic").contains(superClassTitle.asText())) {
            clazz.annotate(SuperBuilder.class);
        } else if (propertyNode.get("extends") != null) {
            clazz.annotate(SuperBuilder.class);
        } else {
            clazz.annotate(Builder.class);
        }
    }
}
