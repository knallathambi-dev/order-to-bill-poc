*** Settings ***
Resource            ../Global_Configuration/Global_Variables.robot
Resource           ../Global_Configuration/Integration_Variables.robot
Library     RequestsLibrary
*** Variables ***

${GLOBAL_CONTRACT_IDS}
${GLOBAL_BUNDLE_IDS}
${GLOBAL_MOBILELINE_IDS}
${GLOBAL_TIMEBUNDLE_IDS}
${GLOBAL_RINGBACKTONE_IDS}
${GLOBAL_SMS_IDS}

*** Keywords ***

This token must be created
     ${KC_Username_Value}=    Get Environment Variable    KC_USERNAME_ID
            ${KC_Password_Value}=    Get Environment Variable    KC_PASSWORD_ID
            ${KC_ClientID_Value}=    Get Environment Variable    KC_CLIENT_ID
            ${KC_ClientSECRET_Value}=    Get Environment Variable    KC_CLIENT_SECRET

            ${EndPoint_Configurator}=    Get Environment Variable    ENDPOINT_CONFIG
            ${EndPoint_Om}=    Get Environment Variable    ENDPOINT_OM_OC
            ${EndPoint_poi}=    Get Environment Variable    ENDPOINT_POI
            ${EndPoint_pi}=    Get Environment Variable    ENDPOINT_PI
            ${ENDPOINT_CATALOG}=    Get Environment Variable    ENDPOINT_CATALOG
            ${KEYCLOAK_URL_Var}=    Get Environment Variable    KEYCLOAK_URL_VAR
            ${ENDPOINT_SETTING}=    Get Environment Variable    ENDPOINT_SETTING

            Set Global Variable    ${EndPoint_Product_Configurator}    ${EndPoint_Configurator}
            Set Global Variable    ${EndPoint_Om_Oc}    ${EndPoint_Om}
            Set Global Variable    ${EndPoint_POI}    ${EndPoint_poi}
            Set Global Variable    ${EndPoint_PI}    ${EndPoint_pi}
            Set Global Variable    ${KEYCLOAK_URL_var}    ${KEYCLOAK_URL_Var}
            Set Global Variable    ${EndPoint_Catalog}    ${ENDPOINT_CATALOG}
            Set Global Variable    ${EndPoint_Setting}    ${ENDPOINT_SETTING}

            ${USERNAME}=    set variable    %{OM_USER=${KC_Username_Value}}
            ${PASSWORD}=    set variable    %{OM_PASSWORD=${KC_Password_Value}}
            ${CLIENT_ID}=    set variable    %{AUTH_CLIENT_ID=${KC_ClientID_Value}}
            ${CLIENT_SECRET}=    set variable    %{AUTH_CLIENT_SECRET=${KC_ClientSECRET_Value}}
            ${KEYCLOAK_URL}=    set variable    %{KEYCLOAK_URL=${KEYCLOAK_URL_var}}
            Create Session      keycloak        ${KEYCLOAK_URL}
    Set Log Level    NONE
    Create Session      keycloak        ${KEYCLOAK_URL}
    ${headers}=    Create Dictionary    Content-Type=application/x-www-form-urlencoded
    ${data}=    Create Dictionary    grant_type=password    username=${USERNAME}    client_secret=${CLIENT_SECRET}    password=${PASSWORD}    scope=openid profile email    client_id=${CLIENT_ID}
    ${response}=    POST On Session    keycloak    /protocol/openid-connect/token    headers=${headers}    data=${data}
    Should Be Equal As Strings    ${response.status_code}    200
    ${OM_TOKEN}=    Set Variable    ${response.json()["access_token"]}
    Set Log Level    INFO
    Set Global Variable    ${OM_TOKEN}
    [Return]    ${OM_TOKEN}    ${EndPoint_PI}    ${EndPoint_POI}    ${EndPoint_Om_Oc}    ${EndPoint_Product_Configurator}    ${EndPoint_Catalog}    ${EndPoint_Setting}

extract all ids from CPIB of "${relatedEntity_id}"

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

Set Commercial Eligibility to False
    &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive

    &{payload}=    Create Dictionary
    ...    reservePhysicalResourceEnabled=true
    ...    reserveLogicalResourceEnabled=false
    ...    checkCommercialEligibilityEnabled=false
    ...    checkPaymentRefEnabled=false
    ...    checkBillingAccountRefEnabled=false
    ...    checkAndSetBillCycleDateEnabled=true
    ...    checkPartyManagementEnabled=true

    Create Session    session    ${EndPoint_Setting}

    ${response}=    POST Request    session    /orderCapture/v1/setting    headers=&{headers}    json=&{payload}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

    Log    Set eligibility settings successfully.

    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Log    Response data after update: ${Return_response}
Set Commercial Eligibility to true
    &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive

    &{payload}=    Create Dictionary
    ...    reservePhysicalResourceEnabled=true
    ...    reserveLogicalResourceEnabled=false
    ...    checkCommercialEligibilityEnabled=true
    ...    checkPaymentRefEnabled=false
    ...    checkBillingAccountRefEnabled=false
    ...    checkAndSetBillCycleDateEnabled=true
    ...    checkPartyManagementEnabled=true

    Create Session    session    ${EndPoint_Setting}

    ${response}=    POST Request    session    /orderCapture/v1/setting    headers=&{headers}    json=&{payload}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

    Log    Set eligibility settings successfully.

    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Log    Response data after update: ${Return_response}


 Teardown Test Case CPIB ID's
    # Reset global variables
        Set Global Variable    ${computedProductConfigurationID}    None
        Set Global Variable    ${productConfiguration}    None
        Set Global Variable    ${GLOBAL_CONFIG_IDS}    None
        Set Global Variable    ${RingBackTonePCId}    None
