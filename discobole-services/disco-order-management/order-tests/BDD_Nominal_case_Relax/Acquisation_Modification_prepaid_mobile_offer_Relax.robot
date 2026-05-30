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
${Product_Offering_Id_MAX}
${Product_Offering_Id}  ac691fc8-1077-44ca-965c-3e2bff3177d3
${name}     Mobile Package Relax
*** Test Cases ***


Valid Prepaid Mobile Offer Relax add Data bundle 10 modify Data bundle 20
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data bundle 20 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case
Valid Prepaid Mobile Offer Relax add Data bundle 10 modify Data bundle 50
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data bundle 50 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Data bundle 10 modify Data bundle 80
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                             and Modification Data bundle 80 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Data bundle 80 modify Data bundle 10
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data bundle 10 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Data bundle 80 modify Data bundle 20
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data bundle 20 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Data bundle 80 modify Data bundle 50
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data bundle 50 for relax Package       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax modify Data pass 2
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data Pass for relax Package 2GB       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax modify Data pass 1
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification Data Pass for relax Package 1GB       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add samsung 256 GB Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add samsung 256 Black       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add samsung 256 GB Purple
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add samsung 256 purple       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add samsung 512 GB Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add samsung 512 Black       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add samsung 512 GB Purple
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add samsung 512 purple       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Pixel 256 GB Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add Pixel 256 Black       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Pixel 256 GB Pink
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add Pixel 256 Pink       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Pixel 128 GB Black
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add Pixel 128 Black       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax add Pixel 128 GB Pink
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition_Modification

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                                And the "${response}" status code should be "201"
                                # Step 2 : Select Offer [Valid]
        ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
        ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
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
                             and extract all ids from CPIB of "${relatedEntity_id_Comfort}"
                            and Modification add Pixel 128 Pink       ${relatedEntity_id_Comfort}
    [Teardown]             Teardown Test Case
