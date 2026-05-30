*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${Future_Planed_Date}                   2030-05-20T00:00:00Z
${Past_Planed_Date}                     2020-05-29T00:00:00Z
${Valid_startSchedule_Date}             2025-01-01
${Valid_endSchedule_Date}               2025-01-10


*** Test Cases ***

# ImmediateJobScheduler Usecases

Invalid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@Type
    Get_token
    invalid_immediate_scheduled_ExportJobSpecification        PurgeJobSpecification           ${EMPTY}                        Post_PurgeJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_@Type
    invalid_immediate_scheduled_ExportJobSpecification        PurgeJobSpecification           ${EMPTY}                        Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_Immediate_body_request

Invalid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_@Type
    invalid_immediate_scheduled_ExportJobSpecification        PurgeJobSpecification           worng_@type                     Post_PurgeJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_@Type
    invalid_immediate_scheduled_ExportJobSpecification        PurgeJobSpecification           worng_@type                     Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_Immediate_body_request

Valid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification-for_Products
    valid_immediate_scheduled_ExportJobSpecification          PurgeJobSpecification           ImmediateJobScheduler               Post_PurgeJobSpecification_JobSpecification_for_Products_Immediate_body_request

Valid_Creation_Immediate_Scheduled_PurgeJobSpecification_JobSpecification-for_JobSpecifications
    valid_immediate_scheduled_ExportJobSpecification          PurgeJobSpecification           ImmediateJobScheduler               Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_Immediate_body_request

# OneTimeJobScheduler Usecases

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_Date
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          OneTimeJobScheduler          ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_Date
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          OneTimeJobScheduler          ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_Date
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          OneTimeJobScheduler          worng_date                   Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_Date
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          OneTimeJobScheduler          worng_date                   Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@Type
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          ${EMPTY}              ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_@Type
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          ${EMPTY}              ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_@Type
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          worng_@type           ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_@Type
    invalid_once_scheduled_ExportJobSpecification           PurgeJobSpecification          worng_@type           ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Passed_Date
    get_token
    invalid_once_scheduled_ExportJobSpecification             PurgeJobSpecification          OneTimeJobScheduler          ${Past_Planed_Date}          Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Passed_Date
    get_token
    invalid_once_scheduled_ExportJobSpecification             PurgeJobSpecification          OneTimeJobScheduler          ${Past_Planed_Date}          Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Future_Date
    valid_once_scheduled_ExportJobSpecification             PurgeJobSpecification          OneTimeJobScheduler          ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_Products_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Future_Date
    get_token
    valid_once_scheduled_ExportJobSpecification             PurgeJobSpecification          OneTimeJobScheduler          ${Future_Planed_Date}        Post_PurgeJobSpecification_JobSpecification_Once_for_JobSpecifications_body_request

# RecurringJobScheduler Usecases

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@Type
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           schedule             ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Repeat_for_@type_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_@Type
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           schedule             ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Repeat_for_@type_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_@Type
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           schedule             worng_@type                  Post_PurgeJobSpecification_JobSpecification_Repeat_for_@type_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_@Type
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           schedule             worng_@type                  Post_PurgeJobSpecification_JobSpecification_Repeat_for_@type_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_period
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           period               ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Repeat_for_period_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_period
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           period               ${EMPTY}                     Post_PurgeJobSpecification_JobSpecification_Repeat_for_period_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_period
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           period               worng_period                 Post_PurgeJobSpecification_JobSpecification_Repeat_for_period_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_period
    invalid_repeat_scheduled_ExportJobSpecification         PurgeJobSpecification           period               worng_period                 Post_PurgeJobSpecification_JobSpecification_Repeat_for_period_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_startScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${EMPTY}                              ${Valid_endSchedule_Date}               Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Tasks_with_Empty_startScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${EMPTY}                              ${Valid_endSchedule_Date}               Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_startScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           worng_startSchedule                   ${Valid_endSchedule_Date}               Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_startScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           worng_startSchedule                   ${Valid_endSchedule_Date}               Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_startSchedule_Date}           ${EMPTY}                                Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_startSchedule_Date}           ${EMPTY}                                Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_startSchedule_Date}           worng_endSchedule                       Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Worng_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_startSchedule_Date}           worng_endSchedule                       Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_JobSpecifications_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_startScheduleDate_gratter_than_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_endSchedule_Date}             ${Valid_startSchedule_Date}             Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_Products_body_request

Invalid_Creation_Repeat_Scheduled_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_startScheduleDate_gratter_than_endScheduleDate
    invalid_date_repeat_scheduled              PurgeJobSpecification           ${Valid_endSchedule_Date}             ${Valid_startSchedule_Date}             Post_PurgeJobSpecification_JobSpecification_Repeat_for_date_for_JobSpecifications_body_request
