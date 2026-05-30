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
Migration use case to Relax
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    ac691fc8-1077-44ca-965c-3e2bff3177d3
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"

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
                               And the nextTaskstoBePerformed List of "${response}" is empty for Modification
             #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
              #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                               #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                              # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                               #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
       [Teardown]             Teardown Modification

Migration use case to comfort
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    3b3433bb-5ae2-49ea-ad14-b4060b95677c
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"
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
                               And the nextTaskstoBePerformed List of "${response}" is empty for Modification
             #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
              #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                               #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                              # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                               #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
       [Teardown]             Teardown Modification

Migration use case to comfort from relax
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    3b3433bb-5ae2-49ea-ad14-b4060b95677c
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"
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
       [Teardown]             Teardown Modification


Migration use case to Max plus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    35a28e56-9a19-4360-b76b-6d931900e916
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"

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
                               And the nextTaskstoBePerformed List of "${response}" is empty for Modification
             #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
              #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                               #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                              # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                               #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
       [Teardown]             Teardown Modification

Migration use case to Max plus from max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    35a28e56-9a19-4360-b76b-6d931900e916
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"

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

       [Teardown]             Teardown Modification


Migration use case to Max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    137e4fc3-e86d-44da-868c-d275eb682371
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"

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
                               And the nextTaskstoBePerformed List of "${response}" is empty for Modification
             #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
              #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"

                               #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                              # The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                               #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
       [Teardown]             Teardown Modification


Migration use case to Max from max plus
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Migration_use_case
    [Arguments]         ${relatedEntity_id_Comfort}
   Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
       and Teardown Test Case
       ${ProductId}            mod extract Product id from PI ${relatedEntity_id_Comfort}
       ${Product_Offering_Id_Migration}=            Set Variable    137e4fc3-e86d-44da-868c-d275eb682371
                               and Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Completed of "${relatedEntity_id_Comfort}"
       ${response}             the customer create for modificaion a product Configuration for ${ProductId} to ${Product_Offering_Id_Migration}
                               extract id from Configurator "${response}"

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
       [Teardown]             Teardown Modification