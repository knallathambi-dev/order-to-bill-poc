*** Settings ***
Resource            ../../Global_Configuration/Global_Variables.robot
Resource        ../Keywords/step_definition.robot
Resource            ../../Global_Configuration/Integration_Variables.robot


Library             String

*** Variables ***
${PRICE}

${DESCRIPTION_FIELD}    description
*** Keywords ***
termination mobile Offer
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Aquisition_use_case
    [Arguments]         ${relatedEntity_id}
    ${ProductId}        get Product id from PI ${relatedEntity_id}
                                                     Wait Until Keyword Succeeds    7 min    10s    Check Operational Status Terminate Completed of "${relatedEntity_id}"
                             ${response}    the customer create for termination a product Configuration for ${ProductId}
                             and extract id from Configurator for termination "${response}"
                             ${configID}=    Strip String    ${CONFIG_IDS}

    ${response}             Given executes for termination the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow "${ProductId}" for termination
                            And the "${response}" status code should be "201" for termination

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When uses the PATCH request body of "confirm_configuration"
    ${request_body}         And enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}" for termination
    ${updated_request_body}    Set Variable    ${request_body}
    ${updated_request_body}    And enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}" for termination
    ${updated_request_body}    And enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}" for termination
    ${response}             Then executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}" for termination
                            And the "${response}" status code should be "200" for termination

    ${relatedEntity_id}     And get the relatedEntity ID from "${response}" for termination
                            And checks that the status of productOrder "${relatedEntity_id}" is "draft" for termination
    ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When uses the PATCH request body of "validate_order"
    ${request_body}         And enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}" for termination
    ${request_body}         And enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}" for termination
    ${request_body}         And enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}" for termination
    ${response}             Then executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}" for termination
                   #         And checks that the Operationalstatus PI of productOrder "${GLOBAL_CONTRACT_IDS}" is "PendingTerminate" for Termination
    [Teardown]             Teardown Termination