*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

get_all_JobSpecifications
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-all-tasks-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}

    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}

    # Première requête pour obtenir la réponse et les en-têtes
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?offset=0   headers=&{headers}
    Should Be True  '${response.status_code}' == '200' or '${response.status_code}' == '206'

    # Récupération du nombre total de jobspecs à partir des en-têtes
    ${total_count}=    Get From Dictionary    ${response.headers}    x-total-count
    Log    Total number of job specifications: ${total_count}

    # Calcul de l'offset en fonction du nombre total
    ${max_offset}=    Evaluate    min(int(${total_count}), 10)    # Remplacer 10 par la taille de la page souhaitée
    Log    Max offset: ${max_offset}

    # Requête avec le bon offset
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?offset=${max_offset}   headers=&{headers}
    Should Be True  '${response.status_code}' == '200' or '${response.status_code}' == '206'

    Should Contain    ${response.text}    "id":"
    Should Contain    ${response.text}    "name":"
    Should Contain    ${response.text}    "@type":"
    Should Contain    ${response.text}    "creationDate":"
    Should Contain    ${response.text}    "lifecycleStatus":"
    Should Contain         ${response.text}        "startDateTime":"
    Should Contain         ${response.text}        "processedProductsCount":[
    # Analyse des résultats et validation de la réponse
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR    ${object}    IN    @{Return_response}
        IF    '${object['@type']}' != 'ExportJobSpecification'
            ${length}=    Get Length    ${object["processedProductsCount"]}
            IF    ${length} != 0
                FOR    ${element}    IN    @{object["processedProductsCount"]}
                    Dictionary Should Contain Key    ${element}    status
                    Dictionary Should Contain Key    ${element}    count
                END
            END
        END
    END

get_bad_field_JobSpecification
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-JobSpecification-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${field}=${field_value}         headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF    '${field}' != '@type'
        Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
        Should Be Equal As Strings      ${Return_response}          []
    ELSE
        Should Be True  '${response.status_code}'=='400'
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       @type value is not a valid type
    END

get_valid_field_JobSpecification
    [Arguments]    ${field}    ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-JobSpecification-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${field}=${field_value}          headers=&{headers}
    Should Be True  '${response.status_code}' == '200' or '${response.status_code}' == '206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not Be Empty     ${Return_response}

    FOR    ${object}    IN    @{Return_response}
        Should Be Equal As Strings    ${object["${field}"]}    ${field_value}
        Should Not Be Empty            ${object["creationDate"]}
        Should Not Be Empty            ${object["id"]}
        Should Not Be Empty            ${object["name"]}
        Should Not Be Empty            ${object["lifecycleStatus"]}
        Should Not Be Empty            ${object["@type"]}


        # Vérification
#        ${type_value}=    Get From Dictionary    ${object}    @type
#        Run Keyword If    '${type_value}' != 'ExportJobSpecification'    Verify Schedule For Purge    ${object}
#
#        Should Not Be Empty            ${object["startDateTime"]}
#        Should Not Be Empty            ${object["endDateTime"]}
    END

Verify Schedule For Purge
    [Arguments]    ${object}
    # Vérifier que la structure 'schedule' est présente et contient la bonne structure
    Should Not Be Empty    ${object["schedule"]}
    ${schedule}=    Get From Dictionary    ${object}    schedule
    Dictionary Should Contain Key    ${schedule}    @type
    Should Be Equal As Strings    ${schedule["@type"]}    RecurringJobScheduler
    Dictionary Should Contain Key    ${schedule}    frequency
    Dictionary Should Contain Key    ${schedule["frequency"]}    amount
    Dictionary Should Contain Key    ${schedule["frequency"]}    timePeriod
    Dictionary Should Contain Key    ${schedule}    scheduledPeriod
    Dictionary Should Contain Key    ${schedule["scheduledPeriod"]}    startDateTime
    Dictionary Should Contain Key    ${schedule["scheduledPeriod"]}    endDateTime
    Dictionary Should Contain Key    ${schedule}    executionTime
    Dictionary Should Contain Key    ${schedule}    contentType
    Dictionary Should Contain Key    ${schedule}    query

    # Vérifier que les dates sont valides
    Should Be Equal As Strings    ${schedule["scheduledPeriod"]["startDateTime"]}    2024-11-26
    Should Be Equal As Strings    ${schedule["scheduledPeriod"]["endDateTime"]}    2024-11-27

get_empty_field_JobSpecification
    [Arguments]          ${field}       ${field_value}          ${code_verif}      ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-name-product_  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${field}=${field_value}         headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_LifeCycleStatus
    [Arguments]          ${status}          ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?status=${status}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_exist_LifeCycleStatus
    [Arguments]          ${status}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-exist-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?lifecycleStatus=${status}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
    ${modified_json_objects}    Create List
    FOR    ${object}    IN    @{json_data}
        ${modified_object}    Create Dictionary        id=${object['id']}    lifecycleStatus=${object['lifecycleStatus']}
        Append To List    ${modified_json_objects}    ${modified_object}
        Should Not be empty                 ${object["creationDate"]}
    END
    FOR    ${object}    IN    @{modified_json_objects}
        Should Be Equal As Strings      ${object["lifecycleStatus"]}                ${status}
    END
    RETURN        ${response}


get_with_offset
    [Arguments]          ${offset}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?offset=${offset}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_bad_parameter
    [Arguments]          ${parameter}          ${code_verif}           ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-parameter-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${parameter}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_offset_or_limit
    [Arguments]          ${offset}     ${limit}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-parameter-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?offset=${offset}&limit=${limit}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_limit
    [Arguments]          ${limit}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-limit-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?limit=${limit}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_offset_&_limit
    [Arguments]          ${offset}          ${limit}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-and-limit-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?offset=${offset}&limit=${limit}    headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${object_count}    Get Length    ${Return_response}
    Log    Number of objects: ${object_count}
    Should Be Equal As Strings    ${object_count}         ${limit}
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_date
    [Arguments]    ${date_filed}      ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_filed}=${date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${JobSpecification}  IN  @{Return_response}
        ${actual_value}=  Set Variable  ${JobSpecification}[${date_filed}]
        Should Be Equal As Strings  ${actual_value}  ${date_input}
        Should Not be empty                 ${JobSpecification["creationDate"]}
    END

get_anded_with_2_dates
    [Arguments]      ${date_filed}     ${min_date_input}      ${max_date_input}          ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${min_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${min_date_input}''', '${format}').timestamp()
    ${max_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${max_date_input}''', '${format}').timestamp()
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${JobSpecification}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${JobSpecification}[${date_filed}]
        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
         #for MIN
        ${difference_with_min}           Evaluate    ${timestamp_actual} - ${min_timestamp_input}
        ${difference_with_min}           Convert To Integer    ${difference_with_min}
        ${is_greater_min}                BuiltIn.Evaluate     ${difference_with_min} >= 0
        Should be True      ${is_greater_min}
         #for MAX
        ${difference_with_max}           Evaluate    ${timestamp_actual} - ${max_timestamp_input}
        ${difference_with_max}           Convert To Integer    ${difference_with_max}
        ${is_greater_max}                BuiltIn.Evaluate     ${difference_with_max} <= 0
        Should be True      ${is_greater_max}
        Should Not be empty                 ${JobSpecification["creationDate"]}
    END

bad_get_anded_with_2_dates
    [Arguments]     ${date_filed}      ${min_date_input}      ${max_date_input}          ${format}        ${op}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${min_date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE IF     '${min_date_input}' == '${NULL}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    ELSE IF     '${max_date_input}' == '${NULL}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    ELSE IF     '${max_date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    END

get_with_bad_date
    [Arguments]      ${date_field}    ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-bad-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_field}=${date_input}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_field} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_field} value is not a valid type
    END

get_with_op_bad_date
    [Arguments]    ${date_filed}         ${op}       ${op_diff}       ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-op-bad-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_filed}.${op}=${date_input}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    END

get_with_op_date
    [Arguments]    ${date_filed}     ${op}       ${op_diff}       ${date_input}      ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-op-and-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${date_filed}.${op}=${date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not Be Empty    ${Return_response}
    ${timestamp_input}=    Evaluate    datetime.datetime.strptime('''${date_input}''', '${format}').timestamp()
    FOR  ${JobSpecification}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${JobSpecification}[${date_filed}.${op}]
        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
        ${difference}           Evaluate    ${timestamp_actual} - ${timestamp_input}
        ${difference}           Convert To Integer    ${difference}
        ${is_greater}           BuiltIn.Evaluate     ${difference} ${op_diff} 0
        Should be True      ${is_greater}
        Should Not be empty                 ${JobSpecification["creationDate"]}
    END

get_with_bad_field
    [Arguments]          ${field}    ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?fields=${field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_without_fields
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-without-fields-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Not Be Equal As Strings        ${response.text}        []
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_specific_field
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?fields=${field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "${field}"
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_activePeriod
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?${field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "${field}"
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_with_sorting
    [Arguments]    ${Sort-Direction}         ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?sort=${Sort-Direction}${Sort-Field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"

get_duplicated_sorting
   [Arguments]    ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-duplicated-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?sort=-${Sort-Field},${Sort-Field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       sort value is not a valid type

get_invalid_sorting
    [Arguments]    ${Sort-Field}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-invalid-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification?sort=${Sort-Field}&=${log-tag}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

valid_get_jobSpec
    [Arguments]          ${id_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${id_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['id']}     ${id_value}
    RETURN        ${Return_response}

invalid_get
    [Arguments]          ${id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${id}            headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The jobSpecification with id worng_id does not exist

invalid_get_JobSpec
    [Arguments]          ${id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${id}            headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The jobSpecification with id ${id} does not exist
otherType_download_link
    [Arguments]          ${Task_id}      ${status_verif}       ${code_verif}       ${reason_verif}          ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${Task_id}/download-link             headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        501
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        ${status_verif}
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

empty_get
    [Arguments]          ${id}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The requested URI or the requested resource does not exist.

get_one_with_bad_field
    [Arguments]          ${id}          ${field}        ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${id}?fields=${field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_one_with_field
    [Arguments]          ${id}          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/jobSpecification/${id}?fields=${field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id":"${id}"
    Should Contain         ${response.text}        "@type":"
    Should Contain         ${response.text}        "creationDate":"
    IF    '${field}' == 'worng_field'
        Log    No fields to display
    ELSE
        # For nested fields (e.g., activePeriod.startDateTime), check if field contains a dot
        ${contains_dot}=    Run Keyword And Return Status    Should Contain    ${field}    .
        IF    ${contains_dot}
            # Extract the last part after the dot for nested fields
            ${field_parts}=    Split String    ${field}    .
            ${last_field}=    Get From List    ${field_parts}    -1
            Should Contain         ${response.text}        "${last_field}":
        ELSE
            # For simple fields, check the field as is
            Should Contain         ${response.text}        "${field}":
        END
    END





