*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

valid_get_job
    [Arguments]          ${Job_id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['id']}     ${Job_id}

valid_get_with_jobSpecification_id
    [Arguments]          ${JobSpecification_id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    # Get job by jobSpecification_id
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?jobSpecification.id=${JobSpecification_id}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${id}=  Get From Dictionary    ${Return_response}[0]      id
    # Get job by id
    valid_get_job       ${id}
    RETURN        ${id}       ${Return_response}

download_link
    [Arguments]          ${Job_id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}/exportFileInformation            headers=&{headers}
    # old conf ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}/downloadLink            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${link}=  Get From Dictionary    ${Return_response}    url
    RETURN        ${link}




invalid_get_job
    [Arguments]          ${Job_id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}           headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The Job with id ${Job_id} does not exist

empty_get_job
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The requested URI or the requested resource does not exist.

get_one_with_bad_field_job
    [Arguments]          ${Job_id}          ${field}        ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}?fields=${field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_one_with_field_job
    [Arguments]          ${Job_id}          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}?fields=${field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id":"${Job_id}"
    Should Contain         ${response.text}        "@type":"
    IF    '${field}' == 'worng_field'
        Log    No fields to display
    ELSE
        Should Contain         ${response.text}        "${field}":
    END

valid_download_link_job
    [Arguments]          ${Job_id}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-download-link-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session      /productInventoryManagement/v1/job/${Job_id}/downloadLink            headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json

    Should Not Be Empty        ${Return_response['link']}

invalid_download_link
    [Arguments]          ${Job_id}      ${status_verif}       ${code_verif}       ${reason_verif}          ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job/${Job_id}/downloadLink            headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        ${status_verif}
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_valid_field_Job
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-task-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${field}=${field_value}          headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    #Should Not be empty     ${Return_response}
    FOR    ${object}    IN    @{Return_response}
        Should Be Equal As Strings          ${object["${field}"]}        ${field_value}
    END

get_bad_field_Job
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-field-taskexcution-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${field}=${field_value}         headers=&{headers}
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

get_empty_field_Job
    [Arguments]          ${field}       ${field_value}          ${code_verif}      ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-field-taskexcution-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${field}=${field_value}         headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_status
    [Arguments]          ${status}          ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?status=${status}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_exist_status
    [Arguments]          ${status}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-exist-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?status=${status}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
#    ${modified_json_objects}    Create List
#    FOR    ${object}    IN    @{json_data}
#        ${modified_object}    Create Dictionary        id=${object['id']}    status=${object['status']}
#        Append To List    ${modified_json_objects}    ${modified_object}
#    END
#    FOR    ${object}    IN    @{modified_json_objects}
#        Should Be Equal As Strings      ${object["status"]}                ${status}
#    END

get_with_date
    [Arguments]    ${date_filed}      ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_filed}=${date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${task}  IN  @{Return_response}
        ${actual_value}=  Set Variable  ${task}[${date_filed}]
        Should Be Equal As Strings  ${actual_value}  ${date_input}
    END

get_anded_with_2_dates
    [Arguments]      ${date_filed}     ${min_date_input}      ${max_date_input}          ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${min_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${min_date_input}''', '${format}').timestamp()
    ${max_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${max_date_input}''', '${format}').timestamp()
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${task}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${task}[${date_filed}]
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
    END

bad_get_anded_with_2_dates
    [Arguments]     ${date_filed}      ${min_date_input}      ${max_date_input}          ${format}        ${op}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}     headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_field}=${date_input}     headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_filed}.${op}=${date_input}     headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${date_filed}.${op}=${date_input}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${timestamp_input}=    Evaluate    datetime.datetime.strptime('''${date_input}''', '${format}').timestamp()
#    FOR  ${task}  IN  @{Return_response}[0:3]
#        ${actual_value}         Set Variable  ${task}[${date_filed}]
#        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
#        ${difference}           Evaluate    ${timestamp_actual} - ${timestamp_input}
#        ${difference}           Convert To Integer    ${difference}
#        ${is_greater}           BuiltIn.Evaluate     ${difference} ${op_diff} 0
#        Should be True      ${is_greater}
#    END

get_with_bad_field
    [Arguments]          ${field}    ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?fields=${field}     headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Not Be Equal As Strings        ${response.text}        []
    Should Contain         ${response.text}        "@type":"

get_with_specific_field
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?fields=${field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "${field}"
    Should Contain         ${response.text}        "@type"

get_with_offset
    [Arguments]          ${offset}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?offset=${offset}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "@type"

get_with_bad_parameter
    [Arguments]          ${parameter}          ${code_verif}           ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-parameter-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?${parameter}    headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?offset=${offset}&limit=${limit}    headers=&{headers}
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
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?limit=${limit}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "@type"

get_with_offset_&_limit
    [Arguments]          ${offset}          ${limit}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-and-limit-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?offset=${offset}&limit=${limit}    headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${object_count}    Get Length    ${Return_response}
    Log    Number of objects: ${object_count}
    Should Be Equal As Strings    ${object_count}         ${limit}
    Should Contain         ${response.text}        "@type"

get_with_sorting
    [Arguments]    ${Sort-Direction}         ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?sort=${Sort-Direction}${Sort-Field}     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    Should Contain         ${response.text}        "@type"

get_duplicated_sorting
   [Arguments]    ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-duplicated-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?sort=-${Sort-Field},${Sort-Field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    206


get_invalid_sorting
    [Arguments]    ${Sort-Field}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-invalid-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/job?sort=${Sort-Field}&=${log-tag}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}
