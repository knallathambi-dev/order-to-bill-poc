*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${Future_Planed_Date}                   2030-05-20T00:00:00Z
${Past_Planed_Date}                     2020-05-29T00:00:00Z
${Valid_startSchedule_Date}             2025-01-01
${Valid_endSchedule_Date}               2025-01-10


*** Test Cases ***

# Immediate Usecases

Invalid_Creation_Immediate_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_@Type
    Get_token
    invalid_immediate_scheduled_ExportJobSpecification        ExportJobSpecification           ${EMPTY}                             Post_ExportJobSpecification_JobSpecification_Immediate_body_request

Invalid_Creation_Immediate_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_@Type
    invalid_immediate_scheduled_ExportJobSpecification        ExportJobSpecification           worng_@type                          Post_ExportJobSpecification_JobSpecification_Immediate_body_request

Valid_Creation_Immediate_Scheduled_ExportJobSpecification_JobSpecification
    valid_immediate_scheduled_ExportJobSpecification          ExportJobSpecification           ImmediateJobScheduler                Post_ExportJobSpecification_JobSpecification_Immediate_body_request

# Once Usecases

Invalid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_Date
    invalid_once_scheduled_ExportJobSpecification        ExportJobSpecification           OneTimeJobScheduler                    ${EMPTY}                        Post_ExportJobSpecification_JobSpecification_Once_body_request
    # !! to check !! Need to check with PO : code 60 instead 24  > 	${response} = <Response [404]> instead 400
Invalid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_Date
    invalid_once_scheduled_ExportJobSpecification        ExportJobSpecification           OneTimeJobScheduler                    worng_date                      Post_ExportJobSpecification_JobSpecification_Once_body_request
    # !! to check !! Need to check with PO : code 60 instead 24  > 	${response} = <Response [404]> instead 400
Invalid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_@Type
    invalid_once_scheduled_ExportJobSpecification        ExportJobSpecification           ${EMPTY}                        ${Future_Planed_Date}           Post_ExportJobSpecification_JobSpecification_Once_body_request
    # !! to check !! Need to check with PO : code 60 instead 24  > 	${response} = <Response [404]> instead 400
Invalid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_@Type
    invalid_once_scheduled_ExportJobSpecification        ExportJobSpecification           worng_@type                     ${Future_Planed_Date}           Post_ExportJobSpecification_JobSpecification_Once_body_request
    # !! to check !! Need to check with PO : code 60 instead 24  > 	${response} = <Response [404]> instead 400
InValid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Passed_Date
    invalid_once_scheduled_ExportJobSpecification          ExportJobSpecification           OneTimeJobScheduler                    ${Past_Planed_Date}             Post_ExportJobSpecification_JobSpecification_Once_body_request

Valid_Creation_Once_Scheduled_ExportJobSpecification_JobSpecification_with_Future_Date
    valid_once_scheduled_ExportJobSpecification          ExportJobSpecification           OneTimeJobScheduler                    ${Future_Planed_Date}           Post_ExportJobSpecification_JobSpecification_Once_body_request

# Repeat Usecases

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_@Type
    invalid_repeat_scheduled_ExportJobSpecification      ExportJobSpecification           schedule                        ${EMPTY}                        Post_ExportJobSpecification_JobSpecification_Repeat_for_@type_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_@Type
    invalid_repeat_scheduled_ExportJobSpecification      ExportJobSpecification           schedule                        worng_@type                     Post_ExportJobSpecification_JobSpecification_Repeat_for_@type_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_period
    invalid_repeat_scheduled_ExportJobSpecification      ExportJobSpecification           period                          ${EMPTY}                        Post_ExportJobSpecification_JobSpecification_Repeat_for_period_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_period
    invalid_repeat_scheduled_ExportJobSpecification      ExportJobSpecification           period                          worng_period                    Post_ExportJobSpecification_JobSpecification_Repeat_for_period_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_startScheduleDate
    invalid_date_repeat_scheduled           ExportJobSpecification           ${EMPTY}                        ${Valid_endSchedule_Date}       Post_ExportJobSpecification_JobSpecification_Repeat_for_date_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_startScheduleDate
    invalid_date_repeat_scheduled           ExportJobSpecification           worng_startSchedule             ${Valid_endSchedule_Date}       Post_ExportJobSpecification_JobSpecification_Repeat_for_date_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Empty_endScheduleDate
    invalid_date_repeat_scheduled           ExportJobSpecification           ${Valid_startSchedule_Date}     ${EMPTY}                        Post_ExportJobSpecification_JobSpecification_Repeat_for_date_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_Worng_endScheduleDate
    invalid_date_repeat_scheduled           ExportJobSpecification           ${Valid_startSchedule_Date}     worng_endSchedule               Post_ExportJobSpecification_JobSpecification_Repeat_for_date_body_request

Invalid_Creation_Repeat_Scheduled_ExportJobSpecification_JobSpecification_with_startScheduleDate_gratter_than_endScheduleDate
    invalid_date_repeat_scheduled           ExportJobSpecification           ${Valid_endSchedule_Date}       ${Valid_startSchedule_Date}     Post_ExportJobSpecification_JobSpecification_Repeat_for_date_body_request
