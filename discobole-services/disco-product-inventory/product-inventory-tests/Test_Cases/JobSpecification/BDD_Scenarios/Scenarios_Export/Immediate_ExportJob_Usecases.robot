*** Settings ***
Resource    ../../../JobSpecification/BDD_Scenarios/Keywords/BDD_usecases.robot
Resource    ../../../../Resources/Common_Keywords.robot
Library    BuiltIn
Library    OperatingSystem
Library    Collections

#Suite Setup    Setup Suite
#Suite Teardown    Teardown Suite

*** Variables ***
${EXPORT_FORMAT_CSV}    csv
${EXPORT_FORMAT_JSON}   json
${CONTENT_TYPE}         application/json
${OUTPUT_FILE}          test_results.txt

# DATE
${creationDate.gte_value}          		2024-05-20T00:00:00Z
${creationDate.lte_value}          		2024-05-29T00:00:00Z
${Valid_startSchedule_Date}             2025-01-01
${Valid_endSchedule_Date}               2025-01-10
${startDate.gte_value}          		2024-05-15T00:00:00Z
${startDate.lte_value}          		2024-05-30T00:00:00Z

${VALID_PLANNED_DATE}                   2025-04-04T22:00:00Z
${INVALID_DATE}                         2024-13-01T00:00:00Z
${PAST_DATE}                            2020-05-29T00:00:00Z
${FUTURE_PLANED_DATE}                   2025-01-01T00:00:00Z
${PAST_PLANED_DATE}                     2010-01-01T00:00:00Z
${VALID_START_DATE}                     2024-12-10T00:00:00Z
${VALID_END_DATE}                       2024-12-20T00:00:00Z
# ${DATE}=    Add Date     ${VALID_PLANNED_DATE}    2

# Frequencies
${INVALID_FREQUENCY}                    invalid_frequency

# Product status
${PRODUCT_STATUS_CREATED}               Created
${PRODUCT_STATUS_CANCELLED}             Cancelled
${PRODUCT_STATUS_ABORTED}               Aborted
${PRODUCT_STATUS_ACTIVE}                Active
${PRODUCT_STATUS_TERMINATED}            Terminated
${PRODUCT_STATUS_SOLD}                  Sold

${@TYPE_JobSpec_VALUE}                          ExportJobSpecification
${@TYPE_Schedule_Immediate_VALUE}               ImmediateJobScheduler

*** Test Cases ***

# ----------------------------------------------------------------------
# Run Valid Tests - Export format CSV
# ----------------------------------------------------------------------

Valid - Test Immediate ExportJob Creation and Execution - CREATED - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status CREATED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id1}      ${Job_id1}=      Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Delete ids.json File
    Save ID    ExportJob_CSV_CREATED_id       ${JobSpec_id1}

Valid - Test Immediate ExportJob Creation and Execution - CANCELLED - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status CANCELLED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id2}      ${Job_id2}=     Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CANCELLED}         BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_CSV_CANCELLED_id    ${JobSpec_id2}

Valid - Test Immediate ExportJob Creation and Execution - ABORTED - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status ABORTED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id3}     ${Job_id3}=     Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_ABORTED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_CSV_ABORTED_id    ${JobSpec_id3}

Valid - Test Immediate ExportJob Creation and Execution - ACTIVE - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status ACTIVE
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id4}     ${Job_id4}=     Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_ACTIVE}            BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_CSV_ACTIVE_id    ${JobSpec_id4}

Valid - Test Immediate ExportJob Creation and Execution - SOLD - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status SOLD
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id5}     ${Job_id5}=    Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_SOLD}              BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_CSV_SOLD_id    ${JobSpec_id5}

Valid - Test Immediate ExportJob Creation and Execution - TERMINATED - CSV
    [Documentation]    Tests creation and execution of an immediate ExportJob in CSV format for product status TERMINATD
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id6}     ${Job_id6}=     Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_TERMINATED}        BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_CSV_TERMINATED_id    ${JobSpec_id6}

# ----------------------------------------------------------------------
# Run Valid Tests - Export format Json
# ----------------------------------------------------------------------

Valid - Test Immediate ExportJob Creation and Execution - CREATED - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status CREATED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id7}     ${Job_id7}=     Valid Test Specification Job Creation and Execution     ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}     ${VALID_PLANNED_DATE}     ${EXPORT_FORMAT_JSON}     ${VALID_START_DATE}     ${VALID_END_DATE}     ${PRODUCT_STATUS_CREATED}     BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_CREATED_id    ${JobSpec_id7}

Valid - Test Immediate ExportJob Creation and Execution - CANCELLED - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status CANCELLED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id8}     ${Job_id8}=     Valid Test Specification Job Creation and Execution      ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_JSON}       ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CANCELLED}         BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_CANCELLED_id    ${JobSpec_id8}

Valid - Test Immediate ExportJob Creation and Execution - ABORTED - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status ABORTED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id9}     ${Job_id10}=     Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_JSON}       ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_ABORTED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_ABORTED_id    ${JobSpec_id9}

Valid - Test Immediate ExportJob Creation and Execution - ACTIVE - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status ACTIVE
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id10}     ${Job_id11}=     Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_JSON}       ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_ACTIVE}            BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_ACTIVE_id    ${JobSpec_id10}

Valid - Test Immediate ExportJob Creation and Execution - TERMINATED - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status TERMINATED
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id11}     ${Job_id12}=     Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_JSON}       ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_TERMINATED}        BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_TERMINATED_id    ${JobSpec_id11}

Valid - Test Immediate ExportJob Creation and Execution - SOLD - JSON
    [Documentation]    Tests creation and execution of an immediate ExportJob in JSON format for product status SOLD
    [Tags]     ExportJob    bug_IPCEISCPIB-4497
    Get_token
    ${JobSpec_id12}     ${Job_id13}=     Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}      ${@TYPE_Schedule_Immediate_VALUE}      ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_JSON}       ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_SOLD}              BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Save ID    ExportJob_JSON_SOLD_id    ${JobSpec_id12}

