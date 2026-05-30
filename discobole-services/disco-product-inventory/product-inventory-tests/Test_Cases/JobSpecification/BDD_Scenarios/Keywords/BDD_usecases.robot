*** Settings ***
Resource    ../../../JobSpecification/POST_Method/Keywords/post_JobSpecifications_usecases.robot
Resource    ../../../JobSpecification/GET_Method/Keywords/get_JobSpecifications_usecases.robot
Resource    ../../../Job/GET_Method/Keywords/get_Job_usecases.robot
Resource    ../../../JobSpecification/POST_Method/Keywords/post_JobSpecifications_usecases.robot

*** Variables ***
${Upload_file}          set as variable             T.txt               // emplacement du fichier dans data
${file}                 set as variable             T2.txt              // emplacement du fichier dans data

*** Keywords ***

# **********************************************************************************************************************
# Valid Tests for One-Time JobSpec
# **********************************************************************************************************************

Valid - Test One-Time ExportJob Creation and Execution
    [Documentation]    Test the creation and execution of a One-Time ExportJob.
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT}        ${VALID_START_DATE}         ${VALID_END_DATE}       ${PRODUCT_STATUS}       ${BODY_REQUEST}

                                                                Given create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}"
    ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
                                                                When the JobSpec "${jobSpec_id}" status should transition to "Active"
    ${job_id}       ${Return_response}      ${job_status}=      And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${jobSpec_status}       ${link}=                            And the JobSpec "${jobSpec_id}" status is "Terminated" after completion of job ${job_id} and Download File
                                                                And Record In "${OUTPUT FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec Id}" Status: "${job Status}"; Job "${job Id}" Status: ${job Status}" And Downloadlink : "${link}"
    RETURN       ${jobSpec_id}        ${job_id}

Valid Test Specification Job Creation and Execution

    [Documentation]    Test the creation and execution of a One-Time ExportJob.
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT}        ${VALID_START_DATE}         ${VALID_END_DATE}       ${PRODUCT_STATUS}       ${BODY_REQUEST}

                                                                Given create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}"
    ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
                                                                When the JobSpec "${jobSpec_id}" status should transition to "Active"
    ${job_id}       ${Return_response}      ${job_status}=      And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${jobSpec_status}       ${link}=                            And the JobSpec "${jobSpec_id}" status is "Terminated" after completion of job ${job_id} and Download File
                                                                And Record In "${OUTPUT FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec Id}" Status: "${job Status}"; Job "${job Id}" Status: ${job Status}" And Downloadlink : "${link}"
    RETURN       ${jobSpec_id}        ${job_id}


Valid - Test One-Time ExportJob with Future Date
    [Documentation]    Test the creation of a One-Time ExportJob with a future date.
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${FUTURE_PLANED_DATE}       ${EXPORT_FORMAT}        ${VALID_START_DATE}         ${VALID_END_DATE}       ${PRODUCT_STATUS}       ${BODY_REQUEST}

                                                                Given create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${FUTURE_PLANED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}"
    ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
                                                                When the JobSpec "${jobSpec_id}" status should transition to "Active"
    ${job_id}       ${Return_response}=                         And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${job_status}=                                              And the JobSpec "${jobSpec_id}" status is "Terminated" after completion of job ${job_id} and Download File
                                                                And record in "${OUTPUT_FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec_id}" status: "${job_status}"; Job "${job_id}"

Valid - Test One-Time ImportJob Creation and Execution
    [Documentation]    Test the creation and execution of a One-Time ExportJob.
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${VALID_PLANNED_DATE}       ${IMPORT_FORMAT}        ${IMPORT_TYPE_FORMAT}       ${BODY_REQUEST}
                                                                Given Import create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", with content type "${IMPORT_FORMAT}", with import type "${IMPORT_TYPE_FORMAT}", with body request "${BODY_REQUEST}"
    ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
                                                                When the JobSpec "${jobSpec_id}" status should transition to "Active"
    ${job_id}       ${Return_response}      ${job_status}=      And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${jobSpec_status}       ${link}=                            And the JobSpec "${jobSpec_id}" status is "NotStarted" after completion of job ${job_id} and upload File ${Upload_file} ${file}
                                                                And Record In "${OUTPUT FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec Id}" Status: "${job Status}"; Job "${job Id}" Status: ${job Status}" And Downloadlink : "${link}"
    RETURN       ${jobSpec_id}        ${job_id}

Valid - Test Immediate ImportJob Creation and Execution
    [Documentation]    Test the creation and execution of a One-Time ExportJob.
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${IMPORT_FORMAT}        ${IMPORT_TYPE_FORMAT}       ${BODY_REQUEST}

                                                                Given Given create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}", with content type "${IMPORT_FORMAT}", with import type "${IMPORT_TYPE_FORMAT}", with body request "${BODY_REQUEST}"
    ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
                                                                # When the JobSpec "${jobSpec_id}" status should transition to "Active"
    ${job_id}       ${Return_response}      ${job_status}=      And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${jobSpec_status}       ${link}=                            And the JobSpec "${jobSpec_id}" status is "NotStarted" after completion of job ${job_id} and upload File ${Upload_file} ${file}
                                                                And Record In "${OUTPUT FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec Id}" Status: "${job Status}"; Job "${job Id}" Status: ${job Status}" And Downloadlink : "${link}"
    RETURN       ${jobSpec_id}        ${job_id}

#Valid - Test One-Time ExportJob with Created Column
#    [Tags]  IPCEISCPIB-434
#    [Documentation]    As an administrator, I can select which columns to display in the exported CSV file.
#    Post_JobSpecification_with_created_column    ExportJobSpecification    scheduleImmediate    Post_ExportJobSpecification_JobSpecification_body_request_with_column_option
#
#Valid - Test One-Time ExportJob with Empty Column
#    [Tags]  IPCEISCPIB-434
#    [Documentation]    As an administrator, if no columns are selected, all columns will be included in the exported CSV file by default.
#    Post_JobSpecification_with_created_empty_column    ExportJobSpecification    scheduleImmediate    Post_ExportJobSpecification_JobSpecification_body_request_with_empty_column_option


InValid - Test Immediate ExportJob Creation and Execution
    [Documentation]
    [Tags]     IPCEISCPIB-515
    [Arguments]     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT}        ${VALID_START_DATE}         ${VALID_END_DATE}       ${PRODUCT_STATUS}       ${BODY_REQUEST}         ${code_verif}       ${reason-verif}     ${message_verif}
        Create Invalid JobSpecification With Custom Values      ${@TYPE_JobSpec_VALUE}    ${@TYPE_Schedule_VALUE}    ${VALID_PLANNED_DATE}      ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${VALID_START_DATE}    ${VALID_END_DATE}        ${BODY_REQUEST}     ${code_verif}       ${reason-verif}     ${message_verif}
#                                                                    Given create a invalid "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}" and Enriched the body with "${FIELD1}" also "${FIELD2}"
#        ${response}=                                                Then the JobSpec "${jobSpec_id}" should be created successfully
#                                                                    When the JobSpec "${jobSpec_id}" status should transition to "Active"
#        ${job_id}       ${Return_response}      ${job_status}=      And the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
#        ${jobSpec_status}       ${link}=                            And the JobSpec "${jobSpec_id}" status is "Terminated" after completion of job ${job_id} and Download File
#                                                                    And Record In "${OUTPUT FILE}" : "OK - Test One-Time JobSpec Creation And Execution Passed; JobSpec "${jobSpec Id}" Status: "${job Status}"; Job "${job Id}" Status: ${job Status}" And Downloadlink : "${link}"
#        [Return]       ${jobSpec_id}        ${job_id}

# Invalid Tests for One-Time JobSpec
Invalid - Test One-Time ExportJob with Invalid Date
    [Documentation]    Test the creation of a One-Time JobSpec with an invalid date.
    [Arguments]     ${INVALID_DATE}    ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${VALID_START_DATE}    ${VALID_END_DATE}

    Given I create a One-Time JobSpec with invalid planned date ${INVALID_DATE}, export format ${EXPORT_FORMAT}, product status ${PRODUCT_STATUS}, start date ${VALID_PLANNED_DATE}, end date ${VALID_PLANNED_DATE}
    Then The JobSpec creation should fail
    And the error message contains "Invalid Date"
    And the JobSpec status is "Failed"
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time JobSpec Invalid Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Past Date
    [Documentation]    Test the creation of a One-Time JobSpec with a past date.
    [Arguments]     ${PAST_DATE}    ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${VALID_START_DATE}    ${VALID_END_DATE}

    ${response}=    Given I create a One-Time JobSpec with planned date ${PAST_DATE}, export format ${EXPORT_FORMAT}, product status ${PRODUCT_STATUS}, start date ${VALID_PLANNED_DATE}, end date ${VALID_PLANNED_DATE}
                    Then The JobSpec creation should fail
                    And The error message contains expected phrase    ${response}    Date cannot be in the past    Invalid frequency
                    And Record in ${OUTPUT_FILE}: "OK - Test One-Time JobSpec Past Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid Frequency
    [Documentation]    Test the creation of a One-Time JobSpec with an invalid frequency.
    [Arguments]     ${VALID_PLANNED_DATE}    ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${INVALID_FREQUENCY}    ${VALID_END_DATE}

    ${response}=    Given I create a One-Time JobSpec with planned date ${VALID_PLANNED_DATE}, export format ${EXPORT_FORMAT}, product status ${PRODUCT_STATUS}, start date ${INVALID_FREQUENCY}, end date ${VALID_PLANNED_DATE}
                    Then The JobSpec creation should fail
                    And The error message contains expected phrase    ${response}    Date cannot be in the past    Invalid frequency
                    And Record in ${OUTPUT_FILE}: "OK - Test One-Time JobSpec Invalid Frequency passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

# Invalid Tests for Export Job Specifications
Invalid - Test One-Time ExportJob with Bad @type
    [Documentation]    Test the creation of a One-Time ExportJob with an invalid @type.
    Post_JobSpecifications_with_bad_@typeJob    ExportJobSpecification    worng_@type    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Bad @type passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Empty @type
    [Documentation]    Test the creation of a One-Time ExportJob with an empty @type.
    Post_JobSpecifications_with_bad_@typeJob    ExportJobSpecification    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty @type passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid Creation Date (gte)
    [Documentation]    Test creating an ExportJob with an invalid creation date (gte).
    Get_token
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${creationDate.gte_value}    worng_date    Post_ExportJobSpecification_JobSpecification_body_request
   # And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid Creation Date (gte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid Creation Date (lte)
    [Documentation]    Test creating an ExportJob with an invalid creation date (lte).
    Get_token
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${creationDate.lte_value}    worng_date    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid Creation Date (lte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Empty Creation Date (gte)
    [Documentation]    Test creating an ExportJob with an empty creation date (gte).
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${creationDate.gte_value}    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty Creation Date (gte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Empty Creation Date (lte)
    [Documentation]    Test creating an ExportJob with an empty creation date (lte).
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${creationDate.lte_value}    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty Creation Date (lte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

# Additional Invalid Query Status Tests
Invalid - Test One-Time ExportJob with No Existent Query Status
    [Documentation]    Test the creation of a One-Time ExportJob with a non-existent query status.
    Post_JobSpecification_with_bad_field    ExportJobSpecification    NoExistent_status    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with No Existent Query Status passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Empty Query Status
    [Documentation]    Test the creation of a One-Time ExportJob with an empty query status.
    Post_JobSpecification_with_bad_field    ExportJobSpecification    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty Query Status passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

# Multiple Query Status Tests
Invalid - Test One-Time ExportJob with one No Existent Query Status
    [Documentation]    Test the creation of a One-Time ExportJob with a non-existent query status.
    Post_JobSpecification_with_bad_multiple_QueryStatus    ExportJobSpecification    Created    NoExistent_status    Post_ExportJobSpecification_JobSpecification_multiple_status_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with No Existent Query Status passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with one Empty Query Status
    [Documentation]    Test the creation of a One-Time ExportJob with an empty query status.
    Post_JobSpecification_with_bad_multiple_QueryStatus    ExportJobSpecification    Created    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_multiple_status_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty Query Status passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Empty Date
    [Documentation]    Test the creation of a One-Time ExportJob with an empty date.
    invalid_once_scheduled_ExportJobSpecification    ExportJobSpecification    OneTimeJobScheduler    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_Once_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Empty Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Wrong Date
    [Documentation]    Test the creation of a One-Time ExportJob with a wrong date.
    invalid_once_scheduled_ExportJobSpecification    ExportJobSpecification    OneTimeJobScheduler    worng_date    Post_ExportJobSpecification_JobSpecification_Once_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Wrong Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Wrong @Type
    [Documentation]    Test the creation of a One-Time ExportJob with a wrong @Type.
    invalid_once_scheduled_ExportJobSpecification    ExportJobSpecification    worng_@type    ${FUTURE_DATE}    Post_ExportJobSpecification_JobSpecification_Once_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Wrong @Type passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Passed Date
    [Documentation]    Test the creation of a One-Time ExportJob with a passed date.
    valid_once_scheduled_ExportJobSpecification    ExportJobSpecification    OneTimeJobScheduler    ${PAST_DATE}    Post_ExportJobSpecification_JobSpecification_Once_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Passed Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Future Date
    [Documentation]    Test the creation of a One-Time ExportJob with a future date.
    valid_once_scheduled_ExportJobSpecification    ExportJobSpecification    OneTimeJobScheduler    ${FUTURE_DATE}    Post_ExportJobSpecification_JobSpecification_Once_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Future Date passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

# Start Date Use Cases

Invalid - Test One-Time ExportJob with Invalid StartDate (gte)
    [Documentation]    Test the creation of a One-Time ExportJob with an invalid start date (gte).
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${startDate.gte_value}    worng_date    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid StartDate (gte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid StartDate (lte)
    [Documentation]    Test the creation of a One-Time ExportJob with an invalid start date (lte).
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${startDate.lte_value}    worng_date    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid StartDate (lte) passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid StartDate (gte) Empty
    [Documentation]    Test the creation of a One-Time ExportJob with an invalid start date (gte) and empty value.
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${startDate.gte_value}    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid StartDate (gte) Empty passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"

Invalid - Test One-Time ExportJob with Invalid StartDate (lte) Empty
    [Documentation]    Test the creation of a One-Time ExportJob with an invalid start date (lte) and empty value.
    Post_JobSpecification_with_bad_date    ExportJobSpecification    ${startDate.lte_value}    ${EMPTY}    Post_ExportJobSpecification_JobSpecification_body_request
    And Record in ${OUTPUT_FILE}: "OK - Test One-Time ExportJob with Invalid StartDate (lte) Empty passed; JobSpec ${job_spec_id} status: ${job_spec_status}; Job ${job_id} status: ${job_status}"


# BDD keywords

create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}"
        ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
        ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
        ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
        #${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${PRODUCT_STATUS}')}
        ${json_string_2}    Set Variable    ${Content_File.replace('@type_value', '${@TYPE_JobSpec_VALUE}')}
        ${json_string_3}    Set Variable    ${json_string_2.replace('scheduleType_value', '${@TYPE_Schedule_VALUE}')}
        ${json_string_4}    Set Variable    ${json_string_3.replace('plannedDate_value', '${VALID_PLANNED_DATE}')}
        ${json_string_5}    Set Variable    ${json_string_4.replace('contentType_value', '${EXPORT_FORMAT}')}
        ${json_string_6}    Set Variable    ${json_string_5.replace('query_value', 'startDate.gte=${VALID_START_DATE}&startDate.lte=${VALID_END_DATE}&status=${PRODUCT_STATUS}')}

        ${JSON_DATA}=       Evaluate    json.dumps(${json_string_6})        string
        ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
        Create Session    session    ${end-point-cpib}
        ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}
        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['@type']}             ${@TYPE_JobSpec_VALUE}
        Should Not Be Empty             ${Return_response['id']}
        Should Be Equal As Strings      ${Return_response['lifecycleStatus']}       Created
        IF    '${@TYPE_JobSpec_VALUE}' == 'ExportJobSpecification'
            Should Be Equal As Strings      ${Return_response['contentType']}       ${EXPORT_FORMAT}
        END
        Should Not Be Empty             ${Return_response['creationDate']}
        Set Suite Variable    ${jobSpec_id}    ${response.json()['id']}
        Set Suite Variable    ${jobSpec_lifecycleStatus}    ${response.json()['lifecycleStatus']}
        RETURN        ${jobSpec_id}      ${jobSpec_lifecycleStatus}
        
create a invalid "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", with body request "${BODY_REQUEST}" and Enriched the body with "${FIELD1}" also "${FIELD2}"

        ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
        ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
        ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
        #${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${PRODUCT_STATUS}')}
        ${json_string_2}    Set Variable    ${Content_File.replace('@type_value', '${@TYPE_JobSpec_VALUE}')}
        ${json_string_3}    Set Variable    ${json_string_2.replace('scheduleType_value', '${@TYPE_Schedule_VALUE}')}
        ${json_string_4}    Set Variable    ${json_string_3.replace('plannedDate_value', '${VALID_PLANNED_DATE}')}
        ${json_string_5}    Set Variable    ${json_string_4.replace('contentType_value', '${EXPORT_FORMAT}')}
        ${json_string_6}    Set Variable    ${json_string_5.replace('query_value', 'startDate.gte=${VALID_START_DATE}&startDate.lte=${VALID_END_DATE}&status=${PRODUCT_STATUS}')}

        ${JSON_DATA}=       Evaluate    json.dumps(${json_string_6})        string
        ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
        Create Session    session    ${end-point-cpib}
        ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}

        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['@type']}             ${@TYPE_JobSpec_VALUE}
        Should Not Be Empty             ${Return_response['id']}
        Should Be Equal As Strings      ${Return_response['lifecycleStatus']}       Created
        IF    '${@TYPE_JobSpec_VALUE}' == 'ExportJobSpecification'
            Should Be Equal As Strings      ${Return_response['contentType']}       ${EXPORT_FORMAT}
        END
        Should Not Be Empty             ${Return_response['creationDate']}
        Set Suite Variable    ${jobSpec_id}    ${response.json()['id']}
        Set Suite Variable    ${jobSpec_lifecycleStatus}    ${response.json()['lifecycleStatus']}
        RETURN        ${jobSpec_id}      ${jobSpec_lifecycleStatus}



Invalid_UC_ImmediateExportJob_CreationAndExecution

        [Arguments]     ${@TYPE_JobSpec_VALUE}    ${@TYPE_Schedule_VALUE}    ${VALID_PLANNED_DATE}      ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${VALID_START_DATE}    ${VALID_END_DATE}        ${BODY_REQUEST}     ${code_verif}       ${reason-verif}     ${message_verif}
        ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
        ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
        ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
        #${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${PRODUCT_STATUS}')}
        ${json_string_2}    Set Variable    ${Content_File.replace('@type_value', '${@TYPE_JobSpec_VALUE}')}
        ${json_string_3}    Set Variable    ${json_string_2.replace('scheduleType_value', '${@TYPE_Schedule_VALUE}')}
        ${json_string_4}    Set Variable    ${json_string_3.replace('plannedDate_value', '${VALID_PLANNED_DATE}')}
        ${json_string_5}    Set Variable    ${json_string_4.replace('contentType_value', '${EXPORT_FORMAT}')}
        ${json_string_6}    Set Variable    ${json_string_5.replace('query_value', 'startDate.gte=${VALID_START_DATE}&startDate.lte=${VALID_END_DATE}&status=${PRODUCT_STATUS}')}
        ${JSON_DATA}=       Evaluate    json.dumps(${json_string_6})        string
        ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
        Create Session    session    ${end-point-cpib}
        ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}

    # Ensure a BAD_REQUEST response is returned
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
        Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
        Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}


#Create Invalid JobSpecification With Custom Values
#    [Arguments]    ${@TYPE_JobSpec_VALUE}    ${@TYPE_Schedule_VALUE}    ${VALID_PLANNED_DATE}
#    ...    ${EXPORT_FORMAT}    ${PRODUCT_STATUS}    ${VALID_START_DATE}    ${VALID_END_DATE}
#    ...    ${BODY_REQUEST}    ${FIELD1}    ${FIELD2}
#    ...    ${code_verif}       ${reason-verif}     ${message_verif}
#    [Documentation]    Create an invalid JobSpecification with type "${@TYPE_JobSpec_VALUE}", schedule type "${@TYPE_Schedule_VALUE}", planned date "${VALID_PLANNED_DATE}", export format "${EXPORT_FORMAT}", product status "${PRODUCT_STATUS}", start date "${VALID_START_DATE}", end date "${VALID_END_DATE}", using body request "${BODY_REQUEST}" and enriching the body with "${FIELD1}" and "${FIELD2}".
#    [Return]    ${jobSpec_id}    ${jobSpec_lifecycleStatus}
#
#    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
#    ${log-tag}=    Catenate   post-JobSpecification-with-status-    ${uuid}
#    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
#
#    # Load JSON file
#    ${file_content}=    OperatingSystem.Get File    Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
#    ${json_data}=       Evaluate    json.loads('''${file_content}''')    json
#
#    # Update main fields
#    Set To Dictionary    ${json_data}    @type=${@TYPE_JobSpec_VALUE}
#    Set To Dictionary    ${json_data}    scheduleType=${@TYPE_Schedule_VALUE}
#    Set To Dictionary    ${json_data}    plannedDate=${VALID_PLANNED_DATE}
#    Set To Dictionary    ${json_data}    contentType=${EXPORT_FORMAT}
#
#    ${query}=    Set Variable    startDate.gte=${VALID_START_DATE}&startDate.lte=${VALID_END_DATE}&status=${PRODUCT_STATUS}
#    Set To Dictionary    ${json_data}    query=${query}
#
#    # Add custom field (enrichment)
#    Set To Dictionary    ${json_data}    ${FIELD1}=${FIELD2}
#    Log    Final JSON payload:\n${json_data}
#
#    # Prepare headers and send POST request
#    &{headers}=    Create Dictionary
#    ...    Content-Type=application/json
#    ...    Accept=*/*
#    ...    Accept-Encoding=gzip, deflate, br
#    ...    Connection=keep-alive
#    ...    Authorization=Bearer ${CPIB_TOKEN}
#
#    Create Session    session    ${end-point-cpib}
#    ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification    headers=&{headers}    json=${json_data}
#    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
#        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
#        Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
#        Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
#        Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

the JobSpec "${jobSpec_id}" should be created successfully
    valid_get_jobSpec       ${jobSpec_id}

the JobSpec "${jobSpec_id}" status should transition to "Active"
    Sleep    120
    ${Return_response}=     valid_get_jobSpec      ${jobSpec_id}
    ${lifecycle_changes}=    Get From Dictionary    ${Return_response}    lifeCycleStatusChange
    ${is_active_present}=    Evaluate    any(change["lifecycleStatus"] == "Active" for change in ${lifecycle_changes})
    Should Be True    ${is_active_present}    Lifecycle status "Active" is not present.

the JobSpec "${jobSpec_id}" is executed and the Job status should transition to to "Succeeded" or "Failed"
    ${job_id}       ${Return_response}=      valid_get_with_jobSpecification_id      ${jobSpec_id}
    ${job_status}=  Get From Dictionary    ${Return_response}[0]    status
    RETURN    ${job_id}       ${Return_response}      ${job_status}

the JobSpec "${jobSpec_id}" status is "Terminated" after completion of job ${job_id} and Download File
    Sleep    120
    ${Return_response}=     valid_get_jobSpec      ${jobSpec_id}
    ${lifecycle_changes}=    Get From Dictionary    ${Return_response}    lifeCycleStatusChange
    ${jobSpec_status}=    Evaluate    any(change["lifecycleStatus"] == "Terminated" for change in ${lifecycle_changes})
    Should Be True    ${jobSpec_status}    Lifecycle status "Terminated" is not present.
    ${link}=    download_link       ${job_id}
    RETURN       ${jobSpec_status}    ${link}

the JobSpec "${jobSpec_id}" status is "NotStarted" after completion of job ${job_id} and Download File
    Sleep    120
    ${Return_response}=     valid_get_jobSpec      ${jobSpec_id}
    ${lifecycle_changes}=    Get From Dictionary    ${Return_response}    lifeCycleStatusChange
    ${jobSpec_status}=    Evaluate    any(change["lifecycleStatus"] == "NotStarted" for change in ${lifecycle_changes})
    Should Be True    ${jobSpec_status}    Lifecycle status "NotStarted" is not present.
    ${link}=    download_link       ${job_id}
    RETURN       ${jobSpec_status}    ${link}


the JobSpec "${jobSpec_id}" status is "NotStarted" after completion of job ${job_id} and upload File ${Upload_file} ${file}
    Sleep    120
    ${Return_response}=     valid_get_jobSpec      ${jobSpec_id}
    ${lifecycle_changes}=    Get From Dictionary    ${Return_response}    lifeCycleStatusChange
    ${jobSpec_status}=    Evaluate    any(change["lifecycleStatus"] == "NotStarted" for change in ${lifecycle_changes})
    Should Be True    ${jobSpec_status}    Lifecycle status "NotStarted" is not present.
    ${link}=    Upload_file       ${Job_id}         ${Upload_file}          ${file}
    RETURN       ${jobSpec_status}    ${link}


create a One-Time JobSpec with invalid planned date ${INVALID_DATE}, export format ${EXPORT_FORMAT}, product status ${PRODUCT_STATUS}, start date ${START_DATE}, end date ${END_DATE}
    ${response}=    POST Request    ${API_URL}    json={"planned_date": "${INVALID_DATE}", "export_format": "${EXPORT_FORMAT}", "product_status": "${PRODUCT_STATUS}", "start_date": "${START_DATE}", "end_date": "${END_DATE}"}
    Should Be Equal As Strings    ${response.status_code}    400

the JobSpec creation should fail
    Should Be Equal As Strings    ${response.status_code}    400

the error message contains "Invalid Date"
    Should Contain    ${response.json()['error']}    "Invalid Date"

the JobSpec status is "Failed"
    Should Be Equal As Strings    ${response.json()['status']}    "Failed"

The error message contains expected phrase
    [Documentation]    Verifies that the error message contains one of the specified phrases.
    [Arguments]    ${response}    ${expected_msg1}    ${expected_msg2}
    ${error_message}=    Set Variable    ${response.json()['error']}
    ${contains_msg1}=    Run Keyword And Return Status    Should Contain    ${error_message}    ${expected_msg1}
    ${contains_msg2}=    Run Keyword And Return Status    Should Contain    ${error_message}    ${expected_msg2}
    Should Be True    ${contains_msg1} or ${contains_msg2}    msg=The error message contains neither "${expected_msg1}" nor "${expected_msg2}".
    Run Keyword If    ${contains_msg1}    Log    The error message contains "${expected_msg1}"
    Run Keyword If    ${contains_msg2}    Log    The error message contains "${expected_msg2}"

Setup Suite
    [Documentation]    This keyword sets up the environment before any tests run.
    Log    Setting up the test environment
    # Example: Create a temporary directory for test files
    Create Directory    /path/to/temp
    # Example: Prepare a configuration file
    Create File    /path/to/temp/config.json    {"key": "value"}

Teardown Suite
    [Documentation]    This keyword cleans up the environment after all tests have run.
    Log    Tearing down the test environment
    # Example: Remove temporary files
    Remove File    /path/to/temp/config.json
    Remove Directory    /path/to/temp

Create JobSpec
    ${response}=    POST    ${API_URL}    json={"name": "Test JobSpec"}    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}
    Should Be Equal As Strings    ${response.status_code}    201
    ${jobspec_id}=    Set Variable    ${response.json()['id']}
    Set Test Variable    ${jobspec_id}

Approve JobSpec
    PUT    ${API_URL}/${jobspec_id}/approve    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}

Deactivate JobSpec
    PUT    ${API_URL}/${jobspec_id}/deactivate    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}

Reactivate JobSpec
    PUT    ${API_URL}/${jobspec_id}/reactivate    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}

Abandon JobSpec
    PUT    ${API_URL}/${jobspec_id}/abandon    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}

Terminate JobSpec
    PUT    ${API_URL}/${jobspec_id}/terminate    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}

Verify JobSpec State
    [Arguments]    ${expected_state}
    ${response}=    GET    ${API_URL}/${jobspec_id}    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}
    Should Be Equal As Strings    ${response.json()['state']}    ${expected_state}

Create and Approve JobSpec
    Create JobSpec
    Approve JobSpec

Create Suspended JobSpec
    Create and Approve JobSpec
    Deactivate JobSpec

Wait For 1 Minute
    Sleep    1 minute

Start Task
    [Arguments]    ${keyword}
    Start Process    python    -c    "from robot.libraries.Remote import Remote; Remote().run_keyword('${keyword}', {})"

Wait For Tasks
    [Arguments]    @{tasks}
    FOR    ${task}    IN    @{tasks}
        Wait For Process    ${task}
    END

Get JobSpec State
    ${response}=    GET    ${API_URL}/${jobspec_id}    headers={"Authorization": "Bearer ${AUTH_TOKEN}"}
    RETURN    ${response.json()['state']}

Start Long Running Process
    [Arguments]    ${jobspec_id}
    Start Process    python    -c    "import time; time.sleep(30)"    alias=long_process

Verify Process Stopped
    [Arguments]    ${jobspec_id}
    ${result}=    Wait For Process    long_process
    Should Be Equal As Integers    ${result.rc}    0

Switch To Unauthorized User
    Set Test Variable    ${AUTH_TOKEN}    invalid_token

Switch To Authorized User
    Set Test Variable    ${AUTH_TOKEN}    your_auth_token_here

Test Created to Active Transition
    [Documentation]    Verify JobSpec transitions from Created to Active when approved
    Create JobSpec
    Approve JobSpec
    Verify JobSpec State    Active

Test Active to Suspended Transition
    [Documentation]    Verify JobSpec transitions from Active to Suspended when deactivated
    Create and Approve JobSpec
    Deactivate JobSpec
    Verify JobSpec State    Suspended

Test Suspended to Active Transition
    [Documentation]    Verify JobSpec transitions from Suspended to Active when reactivated
    Create Suspended JobSpec
    Reactivate JobSpec
    Verify JobSpec State    Active

Test Created to Terminated Transition
    [Documentation]    Verify JobSpec transitions from Created to Terminated when abandoned
    Create JobSpec
    Abandon JobSpec
    Verify JobSpec State    Terminated

Test Active State Stability
    [Documentation]    Verify Active state remains stable unless deactivated
    Create and Approve JobSpec
    Verify JobSpec State    Active
    Wait For 1 Minute
    Verify JobSpec State    Active

Test Suspended State Stability
    [Documentation]    Verify Suspended state remains stable unless reactivated
    Create Suspended JobSpec
    Verify JobSpec State    Suspended
    Wait For 1 Minute
    Verify JobSpec State    Suspended

Test Full Lifecycle Transition
    [Documentation]    Verify complete lifecycle: Created -> Active -> Suspended -> Active -> Terminated
    Create JobSpec
    Approve JobSpec
    Verify JobSpec State    Active
    Deactivate JobSpec
    Verify JobSpec State    Suspended
    Reactivate JobSpec
    Verify JobSpec State    Active
    Terminate JobSpec
    Verify JobSpec State    Terminated

Test Invalid Created to Suspended Transition
    [Documentation]    Verify JobSpec cannot transition from Created to Suspended without activation
    Create JobSpec
    Run Keyword And Expect Error    *    Deactivate JobSpec
    Verify JobSpec State    Created

Test Invalid Reactivation
    [Documentation]    Verify JobSpec cannot be reactivated unless in Suspended state
    Create and Approve JobSpec
    Run Keyword And Expect Error    *    Reactivate JobSpec
    Verify JobSpec State    Active

Test Invalid Active to Terminated Transition
    [Documentation]    Verify system prevents illogical transition from Active to Terminated
    Create and Approve JobSpec
    Run Keyword And Expect Error    *    Terminate JobSpec
    Verify JobSpec State    Active

Test Concurrent State Change
    [Documentation]    Verify system handles concurrent state change requests correctly
    Create JobSpec
    ${task1}=    Start Task    Approve JobSpec
    ${task2}=    Start Task    Abandon JobSpec
    Wait For Tasks    ${task1}    ${task2}
    ${final_state}=    Get JobSpec State
    Should Be True    '${final_state}' == 'Active' or '${final_state}' == 'Terminated'

Test State Change During Processing
    [Documentation]    Verify system handles state change request during ongoing processing
    Create and Approve JobSpec
    Start Long Running Process    ${jobspec_id}
    Deactivate JobSpec
    Verify JobSpec State    Suspended
    Verify Process Stopped    ${jobspec_id}

Test Rapid State Transitions
    [Documentation]    Verify system handles rapid state transitions correctly
    Create JobSpec
    Approve JobSpec
    Deactivate JobSpec
    Reactivate JobSpec
    Deactivate JobSpec
    Reactivate JobSpec
    Verify JobSpec State    Active

Test State Change with Invalid Parameters
    [Documentation]    Verify system handles state change requests with invalid parameters
    Create JobSpec
    Run Keyword And Expect Error    *    Approve JobSpec    invalid_param=true
    Verify JobSpec State    Created

Test State Transition Permissions
    [Documentation]    Verify only authorized users can change JobSpec state
    Create JobSpec
    Switch To Unauthorized User
    Run Keyword And Expect Error    *    Approve JobSpec
    Switch To Authorized User
    Approve JobSpec
    Verify JobSpec State    Active

record in "${OUTPUT_FILE}" : "OK - Test JobSpec Creation And Execution Passed; JobSpec "${jobSpec_id}" status: "${job_status}"; Job "${job_id}" status: ${job_status}" and downloadlink : "${link}"


    ${message}=    Set Variable    OK - Test One-Time JobSpec Creation and Execution passed; JobSpec ${job_spec_id} status: ${job_status}; Job ${job_id} status: ${job_status}
    ${message2}=    Set Variable   Link : ${link}
    ${message3}=    Set Variable   -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    ${current_datetime}=    Get Current Date    result_format=%Y-%m-%d %H:%M:%S
    ${timestamp}=    Set Variable    [${current_datetime}] - Test Executed
    Append To File    ${OUTPUT_FILE}    ${timestamp}\n
    Append To File    ${OUTPUT_FILE}    ${message}\n
    Append To File    ${OUTPUT_FILE}    ${message2}\n
    Append To File    ${OUTPUT_FILE}    ${message3}\n

#------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# Import
#------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Given Import create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}" with planned date planned date "${VALID_PLANNED_DATE}", with content type "${IMPORT_FORMAT}", with import type "${IMPORT_TYPE_FORMAT}", with body request "${BODY_REQUEST}"
        ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
        ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
        ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
        #${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${PRODUCT_STATUS}')}
        ${json_string_2}    Set Variable    ${Content_File.replace('@type_value', '${@TYPE_JobSpec_VALUE}')}
        ${json_string_3}    Set Variable    ${json_string_2.replace('scheduleType_value', '${@TYPE_Schedule_VALUE}')}
        ${json_string_4}    Set Variable    ${json_string_3.replace('plannedDate_value', '${VALID_PLANNED_DATE}')}
        ${json_string_5}    Set Variable    ${json_string_4.replace('contentType_value', '${IMPORT_FORMAT}')}
        ${json_string_6}    Set Variable    ${json_string_5.replace('importType_value', '${IMPORT_TYPE_FORMAT}')}
        ${JSON_DATA}=       Evaluate    json.dumps(${json_string_6})        string
        ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
        Create Session    session    ${end-point-cpib}
        ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}
        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['@type']}             ${@TYPE_JobSpec_VALUE}
        Should Not Be Empty             ${Return_response['id']}
        Should Be Equal As Strings      ${Return_response['lifecycleStatus']}       Created
        IF    '${@TYPE_JobSpec_VALUE}' == 'ExportJobSpecification'
            Should Be Equal As Strings      ${Return_response['contentType']}       ${IMPORT_FORMAT}
        END
        Should Not Be Empty             ${Return_response['creationDate']}
        Set Suite Variable    ${jobSpec_id}    ${response.json()['id']}
        Set Suite Variable    ${jobSpec_lifecycleStatus}    ${response.json()['lifecycleStatus']}
        RETURN        ${jobSpec_id}      ${jobSpec_lifecycleStatus}


Given create a "${@TYPE_JobSpec_VALUE}" and "${@TYPE_Schedule_VALUE}", with content type "${IMPORT_FORMAT}", with import type "${IMPORT_TYPE_FORMAT}", with body request "${BODY_REQUEST}"
        ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
        ${log-tag}=    Catenate   post-JobSpecification-with-status-      ${uuid}
        ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
        ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/JobSpecification/Data/${BODY_REQUEST}.json
        ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
        #${json_string}    Set Variable    ${Content_File.replace('status_product_value', '${PRODUCT_STATUS}')}
        ${json_string_2}    Set Variable    ${Content_File.replace('@type_value', '${@TYPE_JobSpec_VALUE}')}
        ${json_string_3}    Set Variable    ${json_string_2.replace('scheduleType_value', '${@TYPE_Schedule_VALUE}')}
        ${json_string_4}    Set Variable    ${json_string_3.replace('contentType_value', '${IMPORT_FORMAT}')}
        ${json_string_5}    Set Variable    ${json_string_4.replace('importType_value', '${IMPORT_TYPE_FORMAT}')}
        ${JSON_DATA}=       Evaluate    json.dumps(${json_string_5})        string
        ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
        Create Session    session    ${end-point-cpib}
        ${response}=    POST Request    session  /productInventoryManagement/v1/jobSpecification       headers=&{headers}         data=${MY_DATA}
        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['@type']}             ${@TYPE_JobSpec_VALUE}
        Should Not Be Empty             ${Return_response['id']}
        Should Be Equal As Strings      ${Return_response['lifecycleStatus']}       Created
        IF    '${@TYPE_JobSpec_VALUE}' == 'ExportJobSpecification'
            Should Be Equal As Strings      ${Return_response['contentType']}       ${IMPORT_FORMAT}
        END
        Should Not Be Empty             ${Return_response['creationDate']}
        Set Suite Variable    ${jobSpec_id}    ${response.json()['id']}
        Set Suite Variable    ${jobSpec_lifecycleStatus}    ${response.json()['lifecycleStatus']}
        RETURN        ${jobSpec_id}      ${jobSpec_lifecycleStatus}

Save ID
    [Arguments]    ${key}    ${id}
    ${entry}=    Create Dictionary    ${key}=${id}
    ${json}=     Evaluate    json.dumps(${entry})    json
    Append To File    ids.json    ${json}\n
    Log    Appended: ${key} = ${id}

Delete ids.json File
    Run Keyword And Ignore Error    Remove File    ids.json
    Log    ids.json deleted (if existed)
