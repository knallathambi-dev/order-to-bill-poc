// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.constants;

public class PolicyRuleConstants {
    public static final String MISSING_BODY_POLICYACTION_ACTIONSTRATEGY = "Missing Body PolicyAction ActionStrategy";
    public static final String MISSING_BODY_ACTIONVALUE_VALUE = "Missing Body ActionValue Value";
    public static final String POP_NOT_FOUND = "POP not found";
    public static final String POLICYACTIONOPERATION_NOT_FOUND = "PolicyActionOperation Not Found";
    public static final String PO_NOT_FOUND = "PO not found";
    public static final String SELECT_A_VALID_PRODUCT_OFFERING = "Select a valid product offering";
    public static final String POLICY_EVENT_NOT_FOUND = "PolicyEvent Not Found";
    public static final String POLICY_RULE_NOT_FOUND = "PolicyRule Not Found: ";
    public static final String POLICYRULE_MUST_BE_INSTEST_STATE= "PolicyRule Must Be inTest State";
    public static final String POLICYRULE_CANNOT_BE_DELETED = "Policy rule cannot be deleted since it is associated to an entity which is launched";
    public static final String POLICYDOMAIN_NOT_FOUND = "PolicyDomain Not Found";
    public static final String MISSING_CONDITION_STATEMENT = "Missing Condition Statement, Please Add & Save";
    public static final String POLICYCONDITION_NOT_FOUND = "PolicyCondition Not Found";
    public static final String PATH_VARIABLE_ID = "/{id}";
    public static final String MISSING_BODY_POLICYDOMAIN = "Missing Body PolicyDomain";
    public static final String MISSING_BODY_FIELD_POLICYDOMAINNAME = "Missing Body Field PolicyDomainName";
    public static final String DUPLICATE_BODY_FIELD_POLICYDOMAINNAME =  "Duplicate Body Field PolicyDomainName";
    public static final String MISSING_BODY_POLICYACTIONOPERATION_FIELDS = "Missing Body PolicyActionOperation Fields";
    public static final String IN_TEST = "inTest";
    public static final String POLICY_RULE_ID = "/{policyRuleId}";

    public static final String DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED = "Dummy Redis connection cannot be used.";



    // Private constructor to prevent instantiation
    private PolicyRuleConstants() {
        throw new UnsupportedOperationException("This is a Constant class and cannot be instantiated");
    }
}
