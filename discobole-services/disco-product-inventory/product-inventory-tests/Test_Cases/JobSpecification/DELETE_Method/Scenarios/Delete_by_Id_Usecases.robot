*** Settings ***
Resource        ../Keywords/delete_tasks_usecases.robot
Resource        ../../POST_Method/Keywords/post_JobSpecifications_usecases.robot
Resource        ../../GET_Method/Keywords/get_JobSpecifications_usecases.robot
Resource        ../../../JobSpecification/BDD_Scenarios/Keywords/BDD_usecases.robot
Resource        ../../../JobSpecification/DELETE_Method/Keywords/delete_tasks_usecases.robot

*** Variables ***
${EXPORT_FORMAT_CSV}    csv
${EXPORT_FORMAT_JSON}   json
${CONTENT_TYPE}         application/json
${OUTPUT_FILE}          test_results.txt

# DATE
${creationDate.gte_value}          		2025-05-10T00:00:00Z
${creationDate.lte_value}          		2025-05-20T00:00:00Z
${Valid_startSchedule_Date}             2025-01-01
${Valid_endSchedule_Date}               2025-01-10
${startDate.gte_value}          		2025-05-15T00:00:00Z
${startDate.lte_value}          		2025-05-30T00:00:00Z

${VALID_PLANNED_DATE}                   2025-02-23T07:32:00Z
${INVALID_DATE}                         2025-03-01T00:00:00Z
${PAST_DATE}                            2020-05-29T00:00:00Z
${FUTURE_PLANED_DATE}                   2025-01-01T00:00:00Z
${PAST_PLANED_DATE}                     2010-01-01T00:00:00Z
${VALID_START_DATE}                     2025-02-10T00:00:00Z
${VALID_END_DATE}                       2025-02-20T00:00:00Z

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

${@TYPE_JobSpec_VALUE}                  ExportJobSpecification
${@TYPE_Schedule_Immediate_VALUE}       ImmediateJobScheduler
${@TYPE_Schedule_OneTime_VALUE}         OneTimeJobScheduler

*** Test Cases ***

Delete_ExportJob_jobSpecification_by_Valid_Id
    [tags]    smoke_test
    Get_token
    #${jobSpec_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_OneTimeJobScheduler_body_request
    ${jobSpec_id}        ${job_id}=       Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ImmediateJobScheduler     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    Sleep    200
    delete_jobSpecification     ${jobSpec_id}
    invalid_get_JobSpec         ${jobSpec_id}
    invalid_download_link          ${job_id}          NOT_FOUND           ${code_60}          ${notfound_reason}              The Job with id worng_id does not exist


Delete_JobSpecification_by_NonExistent_Id
    Get_token
    bad_delete_jobSpecification             worng_id        ${code_60}              ${notfound_reason}          JobSpecification with Id worng_id not found

Delete_JobSpecification_by_Empty_Id
    Get_token
    bad_delete_jobSpecification             ${EMPTY}        ${code_60}              ${notfound_reason}          The requested URI or the requested resource does not exist.
