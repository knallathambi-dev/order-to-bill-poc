*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot


*** Keywords ***

export_json_all
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request         session          /productInventoryManagement/v1/product/export?contentType=json        headers=&{headers}            #&creationDate.gte=2024-09-01T00:00:00Z
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

export_csv_all
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&creationDate.gte=2024-10-01T00:00:00Z   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

#export_csv_all
#    ${random_number}=    Generate Random String    2    123456789
#    ${log-tag}=          Catenate   export-all-  ${random_number}
#    ${log-tag}=          Replace String    ${log-tag}    ${space}    ${empty}
#
#    &{headers}=    Create Dictionary
#    ...    Authorization=Bearer ${CPIB_TOKEN}
#    ...    Content-Type=application/json
#    ...    Accept=*/*
#    ...    Accept-Encoding=gzip, deflate, br
#    ...    Connection=keep-alive
#
#    Create Session    session    ${end-point-cpib}    headers=&{headers}    #verify=False
#    ${response}=    GET On Session    session    url=/productInventoryManagement/v1/product/export?contentType=csv&creationDate.gte=2025-01-01T00:00:00Z
#
#
##    ${response}=    GET On Session    session
##    ...    /productInventoryManagement/v1/product/export?contentType=csv&creationDate.gte=2025-01-01T00:00:00Z
#
#    Log    Status: ${response.status_code}
#    Log    Body: ${response.content}
#
#    Should Be True    '${response.status_code}' == '200' or '${response.status_code}' == '206'    #msg=Export CSV failed: ${response.status_code}

export_json_with_filter
    [Arguments]          ${filter_name}       ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=json&${filter_name}=${filter_value}&creationDate.gte=2026-04-01T00:00:00Z   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF    '${filter_value}' == 'worng_value'
        Should Be Equal As Strings           ${Return_response}         []
    ELSE
        Should Not be empty     ${Return_response}

        #Should Contain    ${Return_response}   "La taille maximale du fichier a été dépassée"
        FOR    ${object}    IN    @{Return_response}[0:3]
            IF    '${filter_name}' == 'relatedParty.id'
                Should Be Equal As Strings          ${object["relatedParty"][0]["id"]}        ${filter_value}
            ELSE IF     '${filter_name}' == 'status'
                Should Be Equal As Strings          ${object["status"]}        ${filter_value}
            END
        END
    END

export_csv_with_filter
    [Arguments]          ${filter_name}       ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&${filter_name}=${filter_value}&creationDate.gte=2026-04-01T00:00:00Z   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

check_ProductPrice_Subclass
        [Arguments]          ${filter_name}       ${filter_value}
        # step1 : export_csv_with_relatedParty_ID
        ${random_number}=    Generate Random String    2    123456789
        ${log-tag}=    Catenate   export-all-  ${random_number}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
        Create Session    session    ${end-point-cpib}
        ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&${filter_name}=${filter_value}&creationDate.gte=2024-10-01T00:00:00Z   headers=&{headers}
        Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
        # step2 : check_Header_Row_of_ProductPrice
        Should Contain    ${response.content.decode('utf-8')}    productPrice.description
        Should Contain    ${response.content.decode('utf-8')}    productPrice.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.priceType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.description
        Should Contain    ${response.content.decode('utf-8')}    productPrice.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.priceType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.recurringChargePeriod.amount
        Should Contain    ${response.content.decode('utf-8')}    productPrice.recurringChargePeriod.units
        Should Contain    ${response.content.decode('utf-8')}    productPrice.unitOfMeasure
        Should Contain    ${response.content.decode('utf-8')}    productPrice.validFor.endDateTime
        Should Contain    ${response.content.decode('utf-8')}    productPrice.validFor.startDateTime
        Should Contain    ${response.content.decode('utf-8')}    productPrice.billingAccount.id
        Should Contain    ${response.content.decode('utf-8')}    productPrice.billingAccount.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.billingAccount.ratingType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.billingAccount.atReferredType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.id
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.baseType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.schemaLocation
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.atType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productOfferingPrice.atReferredType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.applicationDuration
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.description
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.priceType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.priority
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.recurringChargePeriod.amount
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.recurringChargePeriod.units
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.unitOfMeasure
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.id
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.name
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.baseType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.schemaLocation
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.atType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.productOfferingPrice.atReferredType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.baseType
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.schemaLocation
        Should Contain    ${response.content.decode('utf-8')}    productPrice.productPriceAlteration.atType

export_json_with_date_filter
    [Arguments]          ${filter_name}            ${filter_op}             ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=json&${filter_name}.${filter_op}=${filter_value}&status=Aborted   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${timestamp_input}=    Evaluate    datetime.datetime.strptime('''${filter_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
    FOR  ${product}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${product}[${filter_name}]
        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
        ${difference}           Evaluate    ${timestamp_actual} - ${timestamp_input}
        ${difference}           Convert To Integer    ${difference}
        ${is_greater}           BuiltIn.Evaluate     ${difference} > 0
        Should be True      ${is_greater}
    END

export_csv_with_date_filter
    [Arguments]          ${filter_name}            ${filter_op}             ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&${filter_name}.${filter_op}=${filter_value}&status=Aborted   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

export_json_with_bad_filter
    [Arguments]          ${filter_name}       ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=json&${filter_name}=${filter_value}&creationDate.gte=2024-10-01T00:00:00Z    headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${filter_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name} must not be empty
    ELSE IF     '${filter_value}' == 'worng_value'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name} value is not a valid type
    END

export_csv_with_bad_filter
    [Arguments]          ${filter_name}       ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&${filter_name}=${filter_value}&creationDate.gte=2024-10-01T00:00:00Z    headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${filter_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name} must not be empty
    ELSE IF     '${filter_value}' == 'worng_value'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name} value is not a valid type
    END

export_json_with_bad_date_filter
    [Arguments]          ${filter_name}       ${filter_op}      ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=json&${filter_name}.${filter_op}=${filter_value}&status=Aborted       headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${filter_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name}.${filter_op} must not be empty
    ELSE IF     '${filter_value}' == 'worng_value'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name}.${filter_op} value is not a valid type
    END

export_csv_with_bad_date_filter
    [Arguments]          ${filter_name}       ${filter_op}      ${filter_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   export-all-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/export?contentType=csv&${filter_name}.${filter_op}=${filter_value}&status=Aborted       headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${filter_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name}.${filter_op} must not be empty
    ELSE IF     '${filter_value}' == 'worng_value'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${filter_name}.${filter_op} value is not a valid type
    END
