*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

Post_JobSpecification_with_QueryStatus
    [Arguments]          ${@type_value}            ${lifecycleStatus_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${lifecycleStatus_value}')}
    ${json_string_2}    Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_2})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}            #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['@type']}             ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}            Created
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}       json
    END
    Should Not Be Empty             ${Return_response['creationDate']}
    RETURN             ${Return_response['id']}

Post_JobSpecification_with_multiple_QueryStatus
    [Arguments]          ${@type_value}         ${status_value_1}          ${status_value_2}            ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-with-multiple-status-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}    Set Variable    ${Content_File.replace('status_value_1', '${status_value_1}')}
    ${json_string_2}    Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${modified_json_string}    Set Variable    ${json_string_2.replace('status_value_2', '${status_value_2}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}            #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['@type']}              ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}            Created
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}       json
    END
    Should Not Be Empty             ${Return_response['creationDate']}

Post_JobSpecification_with_bad_field
    [Arguments]          ${@type_value}         ${status_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-bad-field-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('status_product_value', '${status_value}')}
    ${modified_json_string}    Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['message']}           Invalid 'query.status[0]' Field
    ELSE
        IF    '${MY_DATA['purgeType']}' == 'PurgeProduct'
            Should Be Equal As Strings      ${Return_response['message']}           All status values should be one of this list: [Terminated, Aborted, Cancelled].
        ELSE
            Should Be Equal As Strings      ${Return_response['message']}           The query should include at least one of these lifecycleStatus values: [Terminated, Created].
        END
    END

Post_JobSpecifications_with_bad_@typeJob
    [Arguments]          ${@type_option}         ${@type_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-bad-@typeJob-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    IF    '${@type_value}' == 'ExportJobSpecification'
        ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Created')}
    ELSE
        IF    '${file_name}' == 'Post_PurgeJobSpecification_JobSpecification_for_Products_body_request'
            ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Terminated')}
        ELSE
            ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Done')}
        END
    END
    ${modified_json_string}    Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['message']}               Invalid '@type' Field


Post_JobSpecification_with_bad_multiple_QueryStatus
    [Arguments]        ${@type_value}        ${status_value_1}          ${status_value_2}             ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-with-bad-multiple-status-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}    Set Variable    ${Content_File.replace('status_value_1', '${status_value_1}')}
    ${json_string_2}    Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${modified_json_string}    Set Variable    ${json_string_2.replace('status_value_2', '${status_value_2}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}            #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['message']}       Invalid 'query.status[1]' Field      #Invalid Input
    ELSE
        IF    '${MY_DATA['purgeType']}' == 'PurgeProduct'
            Should Be Equal As Strings      ${Return_response['message']}           All status values should be one of this list: [Terminated, Aborted, Cancelled].
        ELSE
            Should Be Equal As Strings      ${Return_response['message']}           All status values should be one of this list: [Done, TerminatedWithError].
        END
    END

Post_JobSpecification_with_bad_date
    [Arguments]      ${@type_value}           ${field_value}         ${new_field_value}              ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-JobSpecification-bad-field-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    IF    '${@type_value}' == 'ExportJobSpecification'
        ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Created')}
    ELSE
        IF    '${file_name}' == 'Post_PurgeJobSpecification_JobSpecification_for_Products_body_request'
            ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Terminated')}
        ELSE
            ${json_string}      Set Variable    ${Content_File.replace('status_product_value', 'Done')}
        END
    END
    ${modified_json_string}      Set Variable    ${json_string.replace('${field_value}', '${new_field_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}               Invalid Input

invalid_once_scheduled_ExportJobSpecification
    [Arguments]         ${@type_value}           ${schedule_value}         ${plannedDate_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-once-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('plannedDate_value', '${plannedDate_value}')}
    ${json_string_2}      Set Variable    ${json_string.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string_2.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/JobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                      ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                    ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                    BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}                  Invalid Input

invalid_immediate_scheduled_ExportJobSpecification
    [Arguments]         ${@type_value}           ${schedule_value}                 ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-immediate-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}               Invalid Input

invalid_repeat_scheduled_ExportJobSpecification
    [Arguments]    ${@type_value}           ${field_name}         ${field_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-once-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('${field_name}_value', '${field_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}               Invalid Input

invalid_date_repeat_scheduled
    [Arguments]     ${@type_value}      ${startSchedule_value}         ${endSchedule_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-once-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('startSchedule_value', '${startSchedule_value}')}
    ${json_string_2}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('endSchedule_value', '${endSchedule_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}               Invalid Input

valid_once_scheduled_ExportJobSpecification
    [Arguments]    ${@type_value}           ${schedule_value}         ${plannedDate_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('plannedDate_value', '${plannedDate_value}')}
    ${json_string_2}      Set Variable    ${json_string.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string_2.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')               json
    Should Be Equal As Strings      ${Return_response['@type']}                         ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}                        Created
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}                   json
    END
    Should Not Be Empty             ${Return_response['creationDate']}
    Should Be Equal As Strings      ${Return_response['schedule']['@type']}             ${schedule_value}
    Should Be Equal As Strings      ${Return_response['schedule']['plannedDate']}       ${plannedDate_value}

valid_immediate_scheduled_ExportJobSpecification
    [Arguments]    ${@type_value}           ${schedule_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-scheduled-ExportJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')               json
    Should Be Equal As Strings      ${Return_response['@type']}                         ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}                        Created
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}                   json
    END
    Should Not Be Empty             ${Return_response['creationDate']}
    Should Be Equal As Strings      ${Return_response['schedule']['@type']}             ${schedule_value}

valid_immediate_scheduled_TerminationJobSpecification
    [Arguments]    ${@type_value}           ${schedule_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-scheduled-TerminationJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')               json
    Should Be Equal As Strings      ${Return_response['@type']}                         ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}                        Created
    IF    '${@type_value}' == 'ExportJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}                   json
    END
    Should Not Be Empty             ${Return_response['creationDate']}
    Should Be Equal As Strings      ${Return_response['schedule']['@type']}             ${schedule_value}


invalid_immediate_scheduled_TerminationJobSpecification
    [Arguments]         ${@type_value}           ${schedule_value}                 ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-immediate-scheduled-TerminationJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                  ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}               Invalid Input


invalid_once_scheduled_TerminationJobSpecification
    [Arguments]         ${@type_value}           ${schedule_value}         ${plannedDate_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-once-scheduled-TerminationJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/jobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('plannedDate_value', '${plannedDate_value}')}
    ${json_string_2}      Set Variable    ${json_string.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string_2.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/JobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['code']}                      ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}                    ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['status']}                    BAD_REQUEST
    #Should Be Equal As Strings      ${Return_response['message']}                  Invalid Input

valid_once_scheduled_TerminationJobSpecification
    [Arguments]    ${@type_value}           ${schedule_value}         ${plannedDate_value}          ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-scheduled-TerminationJobSpecification-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${file_name}.json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}      Set Variable    ${Content_File.replace('plannedDate_value', '${plannedDate_value}')}
    ${json_string_2}      Set Variable    ${json_string.replace('schedule_value', '${schedule_value}')}
    ${json_string_modified}      Set Variable    ${json_string_2.replace('@type_value', '${@type_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${json_string_modified})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}        #
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')               json
    Should Be Equal As Strings      ${Return_response['@type']}                         ${@type_value}
    Should Not Be Empty             ${Return_response['id']}
    Should Be Equal As Strings      ${Return_response['lifecycleStatus']}                        Created
    IF    '${@type_value}' == 'TerminationJobSpecification'
        Should Be Equal As Strings      ${Return_response['contentType']}                   json
    END
    Should Not Be Empty             ${Return_response['creationDate']}
    Should Be Equal As Strings      ${Return_response['schedule']['@type']}             ${schedule_value}
    Should Be Equal As Strings      ${Return_response['schedule']['plannedDate']}       ${plannedDate_value}
