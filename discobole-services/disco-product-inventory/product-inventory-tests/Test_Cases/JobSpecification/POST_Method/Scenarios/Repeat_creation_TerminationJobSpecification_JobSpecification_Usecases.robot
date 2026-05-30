*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${Future_Planed_Date}                   2030-05-20T00:00:00Z
${Past_Planed_Date}                     2020-05-29T00:00:00Z
${Valid_startSchedule_Date}             2025-01-01
${Valid_endSchedule_Date}               2025-01-10


*** Test Cases ***

# ImmediateJobScheduler Usecases

Invalid_Creation_Immediate_Scheduled_TerminationJobSpecification_JobSpecification_for_Products_with_Empty_@Type
    Get_token
    invalid_immediate_scheduled_TerminationJobSpecification        TerminationJobSpecification           ${EMPTY}                        Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Immediate_Scheduled_TerminationJobSpecification_JobSpecification_for_Products_with_Worng_@Type
    invalid_immediate_scheduled_TerminationJobSpecification        TerminationJobSpecification           worng_@type                     Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request


Valid_Creation_Immediate_Scheduled_TerminationJobSpecification_JobSpecification-for_Products
    valid_immediate_scheduled_TerminationJobSpecification          TerminationJobSpecification           ImmediateJobScheduler           Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

# OneTimeJobScheduler Usecases

Invalid_Creation_Once_Scheduled_TerminationJobSpecification_JobSpecification_for_Products_with_Empty_Date
    invalid_once_scheduled_TerminationJobSpecification          TerminationJobSpecification          OneTimeJobScheduler          ${EMPTY}                      Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Once_Scheduled_TerminationJobSpecification_JobSpecification_for_Products_with_Worng_Date
    invalid_once_scheduled_TerminationJobSpecification           TerminationJobSpecification          OneTimeJobScheduler          worng_date                   Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@Type
    invalid_once_scheduled_TerminationJobSpecification           TerminationJobSpecification          ${EMPTY}              ${Future_Planed_Date}               Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Invalid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Worng_@Type
    invalid_once_scheduled_TerminationJobSpecification           TerminationJobSpecification          worng_@type           ${Future_Planed_Date}               Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Passed_Date
    invalid_once_scheduled_TerminationJobSpecification             TerminationJobSpecification          OneTimeJobScheduler          ${Past_Planed_Date}          Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

Valid_Creation_Once_Scheduled_PurgeJobSpecification_JobSpecification_for_Products_with_Future_Date
    valid_once_scheduled_TerminationJobSpecification             TerminationJobSpecification          OneTimeJobScheduler          ${Future_Planed_Date}        Post_TerminationJobSpecification_JobSpecification_for_Products_Immediate_body_request

