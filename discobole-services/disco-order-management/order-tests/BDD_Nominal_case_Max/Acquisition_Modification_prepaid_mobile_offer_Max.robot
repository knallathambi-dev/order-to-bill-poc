*** Settings ***
Resource            ../Termination_use_case/Scenarios/Termination_prepaid_mobile_offer.robot
Resource            ../Modification_use_case/Scenarios/Modification_prepaid_mobile_offer.robot
Resource            ../Global_Configuration/Integration_Variables.robot
Resource            ../Global_Configuration/Global_Variables.robot
Resource            ../Global_Configuration/configurator_keywords.robot
Resource            ../Global_Configuration/Global_Keyword.robot
Library             String

*** Variables ***
${PRICE}
${DESCRIPTION_FIELD}    description
${name}     Mobile Package Max
*** Test Cases ***


Valid Prepaid Mobile Offer Max Aquisition Instore + modification add Ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification

                            Given This token must be created
                             ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
                        #    And the user checks that the status PI of productOrder "${relatedEntity_id_Max}" is "Confirmed"
                         # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mobile Offer Max Add Ring         ${relatedEntity_id_Max}
                            Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and termination mobile Offer         ${relatedEntity_id_Max}
    [Teardown]                  Teardown Test Case
Valid Prepaid Mobile Offer Max Aquisition Instore + modification add SMS
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                         # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                            Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mobile Offer Max Add SMS          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification add SMS Ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
                     #       And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "accepted"
                     #       And the user checks that the status PI of productOrder "${relatedEntity_id_Max}" is "Confirmed"
                            # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification modify ring VIP
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mod Ring Mobile Offer Max VIP Tone          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification modify ring Basic
        [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
        [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                                Given This token must be created
                                ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Ring "Free Tone"
                                # Step 1 : Create Process Flow
        ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                # Step  : Identify party

                                # Step 3 : Confirm Configuration [Valid]
        ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
        ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
        ${updated_request_body}    Set Variable    ${request_body}
        ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
        ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
        ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                                And the "${response}" status code should be "200"

        ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                              and extract all ids from CPIB of "${relatedEntity_id_Max}"
                               Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                               and Modification Mod Ring Mobile Offer Max Basic Tone          ${relatedEntity_id_Max}

        [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification Add SMS modify ring VIP
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                            # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Modify Ring VIP Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification Add SMS modify ring Basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
                            and Update "add" Characteristique Ring "VIP Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                           # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Modify Ring Basic Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Max Aquisition Instore + modification Delete ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                           # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Delete Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition Instore + modification add SMS Delete ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Instore"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                         # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Delete Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case


#**************************** home delevery********************************************

Valid Prepaid Mobile Offer Max Aquisition ome delivery + modification add Ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification

                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
                      #      And the user checks that the status PI of productOrder "${relatedEntity_id_Max}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mobile Offer Max Add Ring         ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification add SMS
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                            Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mobile Offer Max Add SMS         ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification add SMS Ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            # Step 1 : Create Process Flow
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_Max}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
                            ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification modify ring VIP
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mod Ring Mobile Offer Max VIP Tone          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification modify ring Basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                          # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Mod Ring Mobile Offer Max Basic Tone          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Max Aquisition home delivery + modification Add SMS modify ring VIP
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                         # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Modify Ring VIP Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification Add SMS modify ring Basic
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Add SMS Modify Ring Basic Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Max Aquisition home delivery + modification Delete ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                             and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"
                           and Modification Delete Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Max Aquisition home delivery + modification add SMS Delete ring
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Max_Aquisition_And_Modification
                            Given This token must be created
                            ${Product_Offering_Id_MAX}      the customer get product id from catalog for ${name}
                            ${response}    the customer create a product Configuration for ${Product_Offering_Id_MAX}
                            and extract id from Configurator "${response}"
                            and Update Product Configuration "Home delivery"
                            and Update "add" Characteristique Ring "Free Tone"
                            # Step 1 : Create Process Flow
    ${response}             And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id_MAX}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id_Max}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id_Max}" is "draft"
                            # step 4 : Cross channel Modification
   ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id_Max}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
          #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
           #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                           # Assuming you have extracted all the required IDs into variables
                           and extract all ids from CPIB of "${relatedEntity_id_Max}"
                           Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id_Max}"

                           and Modification Add SMS Delete Ring Mobile Offer Max          ${relatedEntity_id_Max}

    [Teardown]             Teardown Test Case

