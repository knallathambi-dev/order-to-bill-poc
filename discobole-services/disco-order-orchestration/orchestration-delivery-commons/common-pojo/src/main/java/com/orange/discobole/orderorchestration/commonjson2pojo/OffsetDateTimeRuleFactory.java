// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.commonjson2pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.FormatRule;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

public class OffsetDateTimeRuleFactory extends RuleFactory {

    @Override
    public Rule<JType, JType> getFormatRule() {
        return new MySchemaRule(this);
    }

    private class MySchemaRule extends FormatRule {

        protected MySchemaRule(RuleFactory ruleFactory) {
            super(ruleFactory);
        }

        @Override
        public JType apply(String nodeName, JsonNode node, JsonNode parent, JType baseType, Schema schema) {
            JType type = super.apply(nodeName, node, parent, baseType, schema);

            if ("orderDate".equals(nodeName) && !parent.findValuesAsText("description").isEmpty() && parent.findValuesAsText("description").get(0).contains("Date when the order was created")) {
                // Customize the type for the "orderDate" field based on your logic
                type = generateOffsetDateTimeType(baseType.owner());
            }
            return type;
        }

        private JType generateOffsetDateTimeType(JCodeModel codeModel) {
            // Get a reference to java.time.OffsetDateTime
            JClass LocalDateTimeClass = codeModel.ref("java.time.LocalDateTime");

            // Create a JType representing OffsetDateTime
            return LocalDateTimeClass;
        }

    }
}
