*** Settings ***
Resource            ../../Global_Configuration/Integration_Variables.robot
Resource            ../../Global_Configuration/Global_Variables.robot
Resource            ../../Global_Configuration/Manage_Token.robot
Library    RequestsLibrary
Library    Collections
Library    JSONLibrary
Library             String

*** Variables ***
${operationalStatusContract}

${GLOBAL_CONTRACT_IDS}
${GLOBAL_BUNDLE_IDS}
${GLOBAL_MOBILELINE_IDS}
${GLOBAL_Connectivity_IDS}
${GLOBAL_DATABundle_IDS}
${GLOBAL_DataPass_IDS}
${GLOBAL_SimCard_IDS}
*** Keywords ***

Check Operational Status Completed of "${relatedEntity_id}"
     &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
     Create Session    session    ${EndPoint_PI}
     ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
     Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
    ${status}=    Get From Dictionary    ${Return_response[0]}    status
    ${Operationalstatus}=    Get From Dictionary    ${Return_response[0]}    operationalStatus
    Should Be Equal As Strings    ${status}    Active
    Should Be Equal As Strings    ${operationalStatus}    Active

the custumer executes the POST api "${api}" with the endpoint "${end-point}" to create a process flow "${related_EntityID}" for Modification
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=     OperatingSystem.Get File      Global_Configuration/DATA/DATAModification/body_for_PF_creation_Modif.json
    ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${body['relatedEntity'][0]}    id=${related_EntityID}
    Log    ${body}

    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF    '${productOffering_referredType}' == 'productOffering'
                &{headers}=    Create Dictionary    Content-Type=application/json    Authorization=Bearer ${OM_TOKEN}    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
                                      Create Session    session    ${end-point}
                                      ${response}=    POST Request    session    ${api}    headers=&{headers}    data=${body}
           ELSE
                     Fail    The '@referredType' in ${MY_FILE} does not equal "productOffering". Cannot proceed with the test.
           END

    Create Session    session    ${end-point}
    ${response}=    POST Request    session  ${api}?${log-tag}          headers=&{headers}         data=${body}

the "${response}" status code should be "${code_response}" for Modification
    Log  ${response.content}
    Should Be Equal As Strings    ${response.status_code}    ${code_response}

the custumer enters the value "${value}" in the "${key}" field on the path "${path}" in the "${request_body}" for Modification
    [Return]            ${request_body}
    Set To Dictionary   ${request_body['characteristic']${path}}               ${key}                    ${value}

the custumer uses the PATCH request body of "${file_name}"
    [Return]            ${Content_File}
    IF          '${file_name}' == 'selectOfferOrContract'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_select_Max.json
    ELSE IF     '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_confirm_Max.json
    ELSE IF     '${file_name}' == 'validate_order'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_validate_Max.json
    ELSE
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_pay_Max.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json

the custumer executes the PATCH api with the "${nextTaskToBePerformend}" href "${api}" of the "${response}" using specific "${request_body}" for Modification
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF          '${nextTaskToBePerformend}' != 'validate_order'
        ${response}=        evaluate    json.loads('''${response.content}''')      json
    END
    Create Session    session    ${EndPoint_Om_Oc}
    ${response}=    PATCH Request    session  ${response${api}}?${log-tag}          headers=&{headers}         data=${request_body}

the custumer get the relatedEntity ID from "${response}" for Modification
    [Return]       ${data['relatedEntity'][0]['id']}
    ${data}=        evaluate    json.loads('''${response.content}''')      json
${relatedEntity_id} =    Run Keyword If    "'relatedEntity' in ${data} and ${data['relatedEntity']}",    Get From List,    ${data['relatedEntity']},    0

Parse Response Data
    [Arguments]    ${response_data}
    ${json}=    Evaluate    json.loads($response_data)    json
    ${filtered_products}=    Create List
    FOR    ${product}    IN    @{json}
        ${product_type}=    Run Keyword And Return Status    Evaluate    type(${product}) == dict    # Check if the type is a dictionary
        Run Keyword If    ${product_type}    Log    Product is a dictionary
    END

Log Product ID If Found
    [Arguments]    ${products}
    Run Keyword If    ${products}    Iterate Products    ${products}

Iterate Products
    [Arguments]    ${products}
    FOR    ${product}    IN    @{products}
        ${product_id}=    Get From Dictionary    ${product}    id
        Log    Product ID with productOffering @type 'Contract': ${product_id}
    END

the custumer checks that the Operationalstatus PI of productOrder "${relatedEntity_id}" is "PendingModification" for Modification
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
           FOR    ${item}    IN    @{Return_response}
               ${typeContract}=    Get From Dictionary    ${item['productOffering']}    @type
               Run Keyword If    '${typeContract}' == 'Contract'    Set Global Variable    ${operationalStatusContract}    ${operationalStatusContract} ${item['operationalStatus']}
           END

           # Log the results
           Log    ${operationalStatusContract}
           ${operationalStatusContract} =    Evaluate    '${operationalStatusContract}'.strip()
           Should Be Equal As Strings    ${operationalStatusContract}    PendingModification

the custumer checks that the status of productOrder "${relatedEntity_id}" is "${state_verif}" for Modification
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_POI}
    ${response}=    GET Request    session  /productOrderingManagement/v1/productOrder/${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    Should Contain              ${response.text}                "state":"${state_verif}"
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['state']}         ${state_verif}

the custumer checks that the status PI of productOrder "${relatedEntity_id}" is "${state_verif}" for Modification
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${expected_status}=    Set Variable    ${Return_response[0]['operationalStatusChange'][1]['status']}
    Should Be Equal As Strings      ${expected_status}         ${state_verif}

the custumer checks on POI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
      &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      Create Session    session    ${EndPoint_POI}
      ${response}=    GET Request    session    /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
      Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
      ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
      FOR    ${item}    IN    @{Return_response['productOrderItem']}
          ${product_spec}=    Get From Dictionary    ${item}    product    productSpecification
          Log   ${product_spec}
          Run Keyword If    ${product_spec} and 'version' in ${product_spec}    Log    Version exists and is: ${product_spec['version']}

      END
the custumer checks on POI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
      &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      Create Session    session    ${EndPoint_POI}
      ${response}=    GET Request    session    /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
      Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
      ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
      FOR    ${item}    IN    @{Return_response['productOrderItem']}
          ${product_offer}=    Get From Dictionary    ${item}    productOffering
          Log   ${product_offer}
          Run Keyword If    ${product_offer} and 'version' in ${product_offer}    Log    Version exists and is: ${product_offer['version']}

      END

the custumer checks on PI that the version PS of productOrder "${relatedEntity_id}" exist for Modification
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
          Create Session    session    ${EndPoint_PI}
          ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
          Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
          ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          FOR    ${product}    IN    @{Return_response}
                  ${product_Spec}=    Get From Dictionary    ${product}    productSpecification    default=None
                  Log    ${product_Spec}
                  Run Keyword If    ${product_Spec} and 'version' in ${product_Spec}
                  ...    Log    Version exists and is: ${product_Spec['version']}
                  ...  ELSE
                  ...    Log    'productSpecification' does not contain 'version' or is None
              END


the custumer checks on PI that the version PO of productOrder "${relatedEntity_id}" exist for Modification
      &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
               Create Session    session    ${EndPoint_PI}
               ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
               Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
               ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
               FOR    ${product}    IN    @{Return_response}
                      ${product_offer}=    Get From Dictionary    ${product}    productOffering
                      Log   ${product_offer}
                      Run Keyword If    ${product_offer} and 'version' in ${product_offer}    Log    Version exists and is: ${product_offer['version']}
               END

the nextTaskstoBePerformed List of "${response}" is empty for Modification
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['_links']['nextTaskstoBePerformed']}       []

the custumer continues the operation in the SHOP instead of the WEB for Modification
    [Return]       ${last_object}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_Om_Oc}
    ${response}=    GET Request    session  /processManagement/v1/processFlow?relatedParty.id=231-mf20        headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${last_object}=    Get From List    ${Return_response}    -1

the custumer executes the POST api for modification "${api}" with the endpoint "${end-point}" to create a process flow for Modification
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=     OperatingSystem.Get File      DATA/body_for_PF_creation_for_modification.json
    ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF    '${productOffering_referredType_modif}' == 'product'
                &{headers}=    Create Dictionary    Content-Type=application/json    Authorization=Bearer ${OM_TOKEN}    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
                                      Create Session    session    ${end-point}
                                      ${response}=    POST Request    session    ${api}    headers=&{headers}    data=${body}
           ELSE
                     Fail    The '@referredType' in ${MY_FILE} does not equal "product". Cannot proceed with the test.
           END

    Create Session    session    ${end-point}
    ${response}=    POST Request    session  ${api}?${log-tag}          headers=&{headers}         data=${body}
the custumer uses the PATCH request body of "${file_name}" for modification
    [Return]            ${Content_File}
    IF          '${file_name}' == 'selectOfferOrContract'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_select_for_modification.json
    ELSE IF     '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_confirm_for_modification.json
    ELSE IF     '${file_name}' == 'validate_order'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_validate_for_modification.json
    ELSE
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_pay_for_modification.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json

The custumer checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}" for Modification
    &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
    Create Session    session    ${EndPoint_POI}
    ${response}=    GET Request    session  /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Contain    ${Return_response}    orderTotalPrice
    ${order_total_price}=    Get From Dictionary    ${Return_response}    orderTotalPrice
    Length Should Be Greater Than    ${order_total_price}    0

Length Should Be Greater Than
    [Arguments]    ${list}    ${length}
    ${list_length}=    Get Length    ${list}
    Should Be True    ${list_length} > ${length}

Reset Test Environment
    ${reset_actions}    Create List
    Run Keyword If    '${reset_actions}' != '[]'    Run Keyword And Ignore Error    @{reset_actions}

the custumer uses the PATCH request body of "${file_name}" for unqualified for Modification
    [Return]            ${Content_File}
    IF          '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    DataUnqualified/body_confirm.json
    ELSE IF     '${file_name}' == 'identify_party'
            ${MY_FILE}=         OperatingSystem.Get File                    DataUnqualified/body_identify.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
the custumer get the response "${response}" for Modification
    [Return]       ${data}
    ${data}=        evaluate    json.loads('''${response.content}''')      json


the customer create for modificaion a product Configuration for ${GLOBAL_CONTRACT_IDS} to ${Package_ID}
     [Return]             ${response.content}
     &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
     ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/POST Product Configurator Migration.json
     ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
         Log    ${body}
     Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['product']}      id=${GLOBAL_CONTRACT_IDS}
     Set To Dictionary      ${body['requestProductConfigurationItem'][1]['productConfiguration']['productOffering']}      id=${Package_ID}
     ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
     Log    ${jsonMisAJour}
     Create Session    session    ${EndPoint_Product_Configurator}
     Set Log Level    NONE
                                   ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                   Set Log Level    INFO
                                       # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                   Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                   Log    PATCH Response: Status=${response.content}

extract id from Configurator for modification "${response}"
          ${Return_response}=    Evaluate    json.loads('''${response}''')    json
          Log    ${Return_response}
          ${Product_Conf}=    Get From Dictionary    ${Return_response}    id
          Set Global Variable    ${CONFIG_IDS}    ${CONFIG_IDS} ${Return_response['id']}
          Log    ${CONFIG_IDS}


mod extract Product id from PI ${relatedEntity_id_Max}
    [RETURN]    ${ProductId}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id_Max}         headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
    ${first_object}=   Get From List    ${Return_response}    0
    ${ProductId}=       Get From Dictionary    ${first_object}    id
    Log    The first id is: ${ProductId}


mod extract id from Configurator "${response}"
          ${Return_response}=    Evaluate    json.loads('''${response}''')    json
          Log    ${Return_response}
          ${Product_Conf}=    Get From Dictionary    ${Return_response}    id
          Set Global Variable    ${GLOBAL_CONFIG_IDS}    ${GLOBAL_CONFIG_IDS} ${Return_response['id']}
          Log    ${GLOBAL_CONFIG_IDS}
           FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
               ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
               ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
               Run Keyword If    '${product_offering_name}' == 'Shipment for tangible products'    Set Global Variable    ${productConfiguration}    ${productConfiguration} ${item['productConfiguration']['id']}
               Run Keyword If    '${product_offering_name}' == 'Shipment for tangible products'    Set Global Variable    ${computedProductConfigurationID}    ${computedProductConfigurationID} ${item['id']}
           END
           FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
               ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
               ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
               Run Keyword If    '${product_offering_name}' == 'Ring Back Tone'    Set Global Variable    ${RingBackTonePCId}    ${RingBackTonePCId} ${item['id']}
           END
           FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
               ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
               ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
               Run Keyword If    '${product_offering_name}' == 'SMS Option'    Set Global Variable    ${SMSPCId}    ${SMSPCId} ${item['id']}
           END

           FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
               ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
               ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
               Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${SamsungID}    ${SamsungID} ${item['id']}
               Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${HandsetPOID}    ${HandsetPOID} ${item['productConfiguration']['productOffering']['id']}
               Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${HandsetPCID}    ${HandsetPCID} ${item['productConfiguration']['productSpecification']['id']}
           END
           # Log the results
           Log    ${computedProductConfigurationID}
           Log    ${productConfiguration}
           Log    ${RingBackTonePCId}
           Log    ${SamsungID}
           Log    ${HandsetPOID}
           Log    ${HandsetPCID}

Migration extract all ids from CPIB of ${relatedEntity_id}

  [Return]      ${GLOBAL_CONTRACT_IDS}         ${GLOBAL_BUNDLE_IDS}           ${GLOBAL_MOBILELINE_IDS}          ${GLOBAL_TIMEBUNDLE_IDS}            ${GLOBAL_RINGBACKTONE_IDS}          ${GLOBAL_SMS_IDS}
  &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
          Create Session    session    ${EndPoint_PI}
          ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
          Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
          ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
           Log    ${Return_response}
           FOR    ${product}    IN    @{Return_response}
               ${productOffering_type}=    Get From Dictionary    ${product['productOffering']}    @type
               Run Keyword If    '${productOffering_type}' == 'Contract'    Set Global Variable    ${GLOBAL_CONTRACT_IDS}    ${GLOBAL_CONTRACT_IDS} ${product['id']}
           END

           # Extract BundleProductOffering IDs
           FOR    ${product}    IN    @{Return_response}
               ${productOffering_type}=    Get From Dictionary    ${product['productOffering']}    @type
               Run Keyword If    '${productOffering_type}' == 'BundleProductOffering'    Set Global Variable    ${GLOBAL_BUNDLE_IDS}    ${GLOBAL_BUNDLE_IDS} ${product['id']}
           END

           # Extract MobileLine IDs
           FOR    ${product}    IN    @{Return_response}
               ${productOffering_name}=    Get From Dictionary    ${product}    name
               Run Keyword If    '${productOffering_name}' == 'Mobile Line'    Set Global Variable    ${GLOBAL_MOBILELINE_IDS}    ${GLOBAL_MOBILELINE_IDS} ${product['id']}
           END

           # Extract connectivity IDs
           FOR    ${product}    IN    @{Return_response}
               ${product_name}=    Get From Dictionary    ${product}    name
               Run Keyword If    '${product_name}' == 'Connectivity'    Set Global Variable    ${GLOBAL_Connectivity_IDS}    ${GLOBAL_Connectivity_IDS} ${product['id']}
           END

           # Extract data pass IDs
           FOR    ${product}    IN    @{Return_response}
               ${product_name}=    Get From Dictionary    ${product}    name
               Run Keyword If    '${product_name}' == 'Data Pass'    Set Global Variable    ${GLOBAL_DataPass_IDS}    ${GLOBAL_DataPass_IDS} ${product['id']}
           END

           # Extract data bundle IDs
           FOR    ${product}    IN    @{Return_response}
               ${product_name}=    Get From Dictionary    ${product}    name
               Run Keyword If    '${product_name}' == 'Data Bundle'    Set Global Variable    ${GLOBAL_DATABundle_IDS}    ${GLOBAL_DATABundle_IDS} ${product['id']}
           END
           # Extract data bundle IDs
           FOR    ${product}    IN    @{Return_response}
               ${product_name}=    Get From Dictionary    ${product}    name
               Run Keyword If    '${product_name}' == 'SIM Card'    Set Global Variable    ${GLOBAL_SimCard_IDS}    ${GLOBAL_SimCard_IDS} ${product['id']}
           END
mod Product Configuration "${shpping}"
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${computedProductConfigID}=      Strip String    ${computedProductConfigurationID}
    ${productConfig}=  Strip String    ${productConfiguration}
      &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Shipement.json
      ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

      ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
      ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}
      ${NEW_PRODUCT_ID}                   Set Variable        ${productConfig}
      ${NEW_SHIPPING_VALUE}               Set Variable        ${shpping}


      # Update main ID
      Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

      # Update IDs in requestProductConfigurationItem
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_PRODUCT_ID}

      # Update Memory Value
      # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
      # Accéder au dictionnaire de la caractéristique
      Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${NEW_SHIPPING_VALUE}
      ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
      Log    ${jsonMisAJour}
      Create Session    session    ${EndPoint_Product_Configurator}
        Set Log Level    NONE
                                      ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                      Set Log Level    INFO
                                          # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                      Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                      Log    PATCH Response: Status=${response.content}

mod Characteristique Ring "${ring type}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${RingBackTonePCId}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Ring.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${ring type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

mod Characteristique SMS
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${SMSPCId}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Selected.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
            Set Log Level    NONE
                                          ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                          Set Log Level    INFO
                                              # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                          Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                          Log    PATCH Response: Status=${response.content}

mod Product Configurator handset memory "${memory}"
       ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductID}=    Strip String    ${SamsungID}
        &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
        ${MY_FILE}=    OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Memory.json
        ${body}=    evaluate    json.loads('''${MY_FILE}''')    json

        ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
        ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
        # Update main ID
        Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

        # Update IDs in requestProductConfigurationItem
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
        # Update Colour Value (assuming it's the first characteristic)
       Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${memory}
        # Convert updated body to JSON
        ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
        Log    ${jsonMisAJour}

        Create Session    session    ${EndPoint_Product_Configurator}
        ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}

        # Log response status and content
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response Content: ${response.content}





Teardown Modification
    # Reset global variables to empty string (as a null alternative)
    Set Global Variable    ${operationalStatusContract}    ${EMPTY}