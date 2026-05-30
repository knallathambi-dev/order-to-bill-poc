*** Settings ***
Resource            ../Keywords/get_Job_usecases.robot
Resource            ../../../JobSpecification/POST_Method/Keywords/post_JobSpecifications_usecases.robot
Resource            ../../../JobSpecification/BDD_Scenarios/Keywords/BDD_usecases.robot
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

${VALID_PLANNED_DATE}                   2024-12-23T07:32:00Z
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

${@TYPE_JobSpec_VALUE}                      ExportJobSpecification
${@TYPE_Schedule_OneTime_VALUE}             ImmediateJobScheduler
*** Test Cases ***

Get_Job_ExportJob_By_valid_id
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request

Get_Job_PurgeJob_Products_By_valid_id
    valid_get_job            ${Valid_Job_PurgeJobSpecification_Products_id}

Get_Job_By_nonExistent_id
    Get_token
    invalid_get_job         worng_id

Get_Job_By_empty_id
    Get_token
    empty_get_job

Get_One_Job_with_empty_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_bad_field_job      ${Valid_ExportJob_id}         ${EMPTY}              fields must not be empty

Get_One_Job_with_nonExistent_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_ExportJob_id}         worng_field

Get_One_Job_with_Id_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_ExportJob_id}         id

Get_One_Job_with_startDate_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_ExportJob_id}         executionPeriod.startDateTime

Get_One_Job_with_endDate_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job         ${Valid_ExportJob_id}         executionPeriod.endDateTime

# one time
Get_One_Job_with_plannedDate_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_ExportJob_id}         plannedDate

Get_One_Job_with_fileName_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_ExportJob_id}         fileName

Get_One_Job_with_Status_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_Job_ExportJobSpecification_id}         status

Get_One_Job_with_href_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_Job_ExportJobSpecification_id}         href

Get_One_Job_with_@Type_Field
    Get_token
    ${Valid_ExportJob_id}=         Valid - Test One-Time ExportJob Creation and Execution      ${@TYPE_JobSpec_VALUE}     ${@TYPE_Schedule_OneTime_VALUE}     ${VALID_PLANNED_DATE}       ${EXPORT_FORMAT_CSV}        ${VALID_START_DATE}     ${VALID_END_DATE}       ${PRODUCT_STATUS_CREATED}           BDD_Post_ExportJobSpecification_ImmediateJobScheduler_body_request
    get_one_with_field_job          ${Valid_Job_ExportJobSpecification_id}         @type
