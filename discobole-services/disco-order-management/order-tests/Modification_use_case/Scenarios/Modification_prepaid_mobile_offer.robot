*** Settings ***
Resource            ../../Global_Configuration/Global_Variables.robot
Resource        ../Keywords/step_definition.robot
Resource            ../../Global_Configuration/Integration_Variables.robot
Resource            ../../Global_Configuration/configurator_keywords.robot

Library             String

*** Variables ***
${PRICE}

${DESCRIPTION_FIELD}    description
*** Keywords ***
Modification Mobile Offer Max Add Ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "add" Characteristique Ring "Free tone"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                            Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
           #                 And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Mobile Offer Max Add SMS
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update Characteristique SMS
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                            Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            # step 4 : Cross channel Modification

    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                           # And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                                # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Add SMS Ring Mobile Offer Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case

    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "add" Characteristique Ring "Free tone"
                            Update Characteristique SMS
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                              Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                           # And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
                            and Teardown Modification
Modification Mod Ring Mobile Offer Max VIP Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "modify" Characteristique Ring "VIP Tone"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                               Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          # And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Mod Ring Mobile Offer Max Basic Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "modify" Characteristique Ring "Basic Tone"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                               Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Add SMS Modify Ring VIP Mobile Offer Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "modify" Characteristique Ring "Vip Tone"
                            Update Characteristique SMS
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]d
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Add SMS Modify Ring Basic Mobile Offer Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "modify" Characteristique Ring "Basic Tone"
                            Update Characteristique SMS
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Delete Ring Mobile Offer Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "terminate" Characteristique Ring "Vip Tone"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Add SMS Delete Ring Mobile Offer Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case

    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update "terminate" Characteristique Ring "Vip Tone"
                            Update Characteristique SMS
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification



#----------------------------------------------- MaxPlus----------------------------------------------------------------------------------------------------

Modification Add handset Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            and Update Product Configurator handset memory "128"
                            and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification



                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                            Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                           # And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"

    [Teardown]             Teardown Modification

Modification Add handset and SMS Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case

    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update Product Configurator handset memory "128"
                            Update Characteristique SMS
                            and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                               Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                           # And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Add handset and Ring VIP tone Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}

    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update Product Configurator handset memory "128"
                            Update "add" Characteristique Ring "Vip Tone"
                            and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                   Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add handset and Ring Free tone Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
        ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
        ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                extract id from Configurator "${response}"
                                Update Product Configurator handset memory "128"
                                Update "add" Characteristique Ring "Free Tone"
                                and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
            ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                       Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add handset and Ring Basic tone Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
        ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
        ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                extract id from Configurator "${response}"
                                Update Product Configurator handset memory "128"
                                Update "add" Characteristique Ring "Basic Tone"
                                and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
                ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                           Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                            #    And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the custumer uses the PATCH request body of "pay_order"
        ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                                And the "${response}" status code should be "200" for Modification
                                    And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification



 Modification Add handset, SMS and Ring VIP tone Mobile Offer MaxPlus
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
             ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                     Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
             ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                     extract id from Configurator "${response}"
                                     Update Product Configurator handset memory "128"
                                     Update "add" Characteristique Ring "VIP Tone"
                                     Update Characteristique SMS
                                     and Update Product Configuration "Instore"
     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
                     ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                                Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                             #   And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the custumer uses the PATCH request body of "pay_order"
        ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                                And the "${response}" status code should be "200" for Modification
                                    And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
                             and the custumer checks on POI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on POI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

 Modification Add handset, SMS and Ring Free tone Mobile Offer MaxPlus
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                  ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                          Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                  ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                          extract id from Configurator "${response}"
                                          Update Product Configurator handset memory "128"
                                          Update "add" Characteristique Ring "Free Tone"
                                          Update Characteristique SMS
                                          and Update Product Configuration "Instore"
     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
      ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
                          ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                                     Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                              #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the custumer uses the PATCH request body of "pay_order"
        ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                                And the "${response}" status code should be "200" for Modification
                                    And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

                             and the custumer checks on POI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on POI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

 Modification Add handset, SMS and Ring Basic tone Mobile Offer MaxPlus
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]                   ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
     ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    and Teardown Test Case
                  ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                          Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                  ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                          extract id from Configurator "${response}"
                                          Update Product Configurator handset memory "128"
                                          Update "add" Characteristique Ring "Basic Tone"
                                          Update Characteristique SMS
                                          and Update Product Configuration "Instore"
     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
     ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                              #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification

                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the custumer uses the PATCH request body of "pay_order"
        ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                                And the "${response}" status code should be "200" for Modification
                                    And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

                             and the custumer checks on POI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on POI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
                             and the custumer checks on PI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

Modification Add Handset Mod Ring Mobile Offer MaxPlus VIP Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                               Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                       ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                               extract id from Configurator "${response}"
                                               Update Product Configurator handset memory "128"
                                               Update "modify" Characteristique Ring "VIP Tone"
                                               and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
         ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                    Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add Handset Mod Ring Mobile Offer MaxPlus Basic Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                               Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                       ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                               extract id from Configurator "${response}"
                                               Update Product Configurator handset memory "128"
                                               Update "modify" Characteristique Ring "Basic Tone"
                                               and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                        Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add Handset Del Ring Mobile Offer MaxPlus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                   ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                               Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                       ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                               extract id from Configurator "${response}"
                                               Update Product Configurator handset memory "128"
                                               Update "terminate" Characteristique Ring "Basic Tone"
                                               and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
     ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                        Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Add Handset Add SMS Mod Ring Mobile Offer MaxPlus Basic Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                               Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                       ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                               extract id from Configurator "${response}"
                                               Update Product Configurator handset memory "128"
                                               Update "modify" Characteristique Ring "Basic Tone"
                                               Update Characteristique SMS
                                               and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                               Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                         #   And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add Handset Add SMS Mod Ring Mobile Offer MaxPlus VIP Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                            and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
    ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                            extract id from Configurator "${response}"
                            Update Product Configurator handset memory "128"
                            Update "modify" Characteristique Ring "VIP Tone"
                            Update Characteristique SMS
                            and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                   Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                          #  And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add Handset Add SMS Del Ring Mobile Offer MaxPlus Basic Tone
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]                    ${relatedEntity_id_Max}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
                            ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Max}
                                                    Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Max}"
                            ${response}             the customer create for modificaion a product Configuration for ${ProductId}
                                                    extract id from Configurator "${response}"
                                                    Update Product Configurator handset memory "128"
                                                    Update "modify" Characteristique Ring "VIP Tone"
                                                    Update Characteristique SMS
                                                    and Update Product Configuration "Instore"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                               Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                         #   And the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id_Max}" is "PendingModification" for Modification
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification





#----------------------------------------------- relax----------------------------------------------------------------------------------------------------

Modification Data bundle 10 for relax Package
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]         ${relatedEntity_id_Comfort}
     ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
     Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
     and Teardown Test Case
     ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                             Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
     ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                             and extract id from Configurator "${response}"
                             and Update "modify" Characteristique Data bundle "10"

     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
         ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                 Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                             # Delete    -   Step 6 : Pay Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "pay_order"
     ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification
                                 And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification


Modification Data bundle 20 for relax Package
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]         ${relatedEntity_id_Comfort}
     ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
     Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
     and Teardown Test Case
     ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                             Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
     ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                             and extract id from Configurator "${response}"
                             and Update "modify" Characteristique Data bundle "20"

     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
         ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                 Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                             # Delete    -   Step 6 : Pay Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "pay_order"
     ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification
                                 And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

Modification Data bundle 50 for relax Package
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]         ${relatedEntity_id_Comfort}
     ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
     Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
     and Teardown Test Case
     ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                             Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
     ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                             and extract id from Configurator "${response}"
                             and Update "modify" Characteristique Data bundle "50"

     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
         ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                 Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                             # Delete    -   Step 6 : Pay Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "pay_order"
     ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification
                                 And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

Modification Data bundle 80 for relax Package
     [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
     [Tags]                      Aquisition_use_case
     [Arguments]         ${relatedEntity_id_Comfort}
     ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
     Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
     and Teardown Test Case
     ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                             Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
     ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                             and extract id from Configurator "${response}"
                             and Update "modify" Characteristique Data bundle "80"

     ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                             And the "${response}" status code should be "201" for Modification

                             # Step 3 : Confirm Configuration [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
     ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${updated_request_body}    Set Variable    ${request_body}
     ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
         ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                 Log    "${glob_cong_ids}"
     ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification

     ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                             And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                             # step 4 : Cross channel Modification
     ${response}             the custumer get the response "${response}" for Modification
                             # Step 5 : Validate Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "validate_order"
     ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
     ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                             # Delete    -   Step 6 : Pay Order [Valid]
     ${request_body}         When the custumer uses the PATCH request body of "pay_order"
     ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                             And the "${response}" status code should be "200" for Modification
                                 And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                             #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                             #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
     [Teardown]             Teardown Modification

Modification Data Pass for relax Package 1GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Data pass "1"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Data Pass for relax Package 2GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Data pass "2"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification add samsung 256 Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator samsung memory "256GB" and Color "Black"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification add samsung 256 purple
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator samsung memory "256GB" and Color "Purple"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification add samsung 512 Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator samsung memory "512GB" and Color "Black"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification add samsung 512 purple
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator samsung memory "512GB" and Color "Purple"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification



Modification add Pixel 256 Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator Pixel memory "256GB" and Color "Black"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification add Pixel 256 Pink
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator Pixel memory "256GB" and Color "Pink"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification add Pixel 128 Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator Pixel memory "128GB" and Color "Black"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification add Pixel 128 Pink
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update Product Configurator Pixel memory "128GB" and Color "Pink"
                            and Update Product Configuration "Instore"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

#----------------------------------------------- Comfort----------------------------------------------------------------------------------------------------


Modification Data Pass for comfort Package 1GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "add" Characteristique Data pass "1"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${contract_ids}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification

                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Data Pass for comfort Package 2GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "add" Characteristique Data pass "2"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


############################ mobile smart contract ###############################################
Modification Device Insurance Premium
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique device Insurance "Premium"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Device Insurance basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique device Insurance "Basic"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Standard
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add-on HBO
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique HBO "HBO"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Add-on HBO Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique HBO "HBO Max"

    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Netflix and device insurance Premium
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique device Insurance "Premium"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix and device insurance Basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique device Insurance "Basic"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Data extra Bundle 100
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Extra Bundle "100"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Data extra Bundle 50
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Extra Bundle "50"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Basic and data bundle 50
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "50"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Netflix Standard and data bundle 50
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "50"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Netflix Basic and data bundle 100
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "100"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Netflix Standard and data bundle 100
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "100"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification


Modification Netflix Basic and data bundle 50 and tv channel extended
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "50"
                            and Update "modify" Characteristique TV Channels "Extended_167"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Basic and data bundle 50 and tv channel basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "50"
                            and Update "modify" Characteristique TV Channels "Basic_146"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Standard and data bundle 50 and tv channel extended
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "50"
                            and Update "modify" Characteristique TV Channels "Extended_167"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Standard and data bundle 50 and tv channel basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "50"
                            and Update "modify" Characteristique TV Channels "Basic_146"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Basic and data bundle 100 and tv channel extanded
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "100"
                            and Update "modify" Characteristique TV Channels "Extended_167"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification

Modification Netflix Basic and data bundle 100 and tv channel basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Basic"
                            and Update "modify" Characteristique Extra Bundle "100"
                            and Update "modify" Characteristique TV Channels "Basic_146"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Netflix Standard and data bundle 100 and tv channel extanded
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "100"
                            and Update "modify" Characteristique TV Channels "Extended_167"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification
Modification Netflix Standard and data bundle 100 and tv channel basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
    ${contract_ids}=    Strip String    ${GLOBAL_CONTRACT_IDS}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    and Teardown Test Case
    ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
                            Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
    ${response}             and the customer create for modificaion a product Configuration for ${ProductId}
                            and extract id from Configurator "${response}"
                            and Update "modify" Characteristique Netflix "Standard"
                            and Update "modify" Characteristique Extra Bundle "100"
                            and Update "modify" Characteristique TV Channels "Basic_146"
    ${response}             Given the custumer executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for Modification
                            And the "${response}" status code should be "201" for Modification

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the custumer enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${updated_request_body}    Set Variable    ${request_body}
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the custumer enters the value "${glob_cong_ids}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
                                Log    "${glob_cong_ids}"
    ${updated_request_body}    And the custumer enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification

    ${relatedEntity_id}     And the custumer get the relatedEntity ID from "${response}" for Modification
                            And the custumer checks that the status of productOrder "${relatedEntity_id}" is "draft" for Modification
                            # step 4 : Cross channel Modification
    ${response}             the custumer get the response "${response}" for Modification
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "validate_order"
    ${request_body}         And the custumer enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${request_body}         And the custumer enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for Modification
    ${response}             Then the custumer executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification


                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the custumer uses the PATCH request body of "pay_order"
    ${response}             Then the custumer executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for Modification
                            And the "${response}" status code should be "200" for Modification
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                           # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Modification