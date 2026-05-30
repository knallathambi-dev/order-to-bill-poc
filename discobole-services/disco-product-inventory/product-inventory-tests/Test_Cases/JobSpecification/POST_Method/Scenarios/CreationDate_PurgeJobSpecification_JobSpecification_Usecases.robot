*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${creationDate.gte_value}          2024-05-20T00:00:00Z
${creationDate.lte_value}          2024-05-29T00:00:00Z

*** Test Cases ***

Create_Invalid_PurgeJob_Task_for_Products_with_Invalid_creationDate.gte
    Get_token
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.gte_value}          worng_date           Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJob_Task_for_Tasks_with_Invalid_creationDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.gte_value}          worng_date           Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJob_Task_Products_with_Invalid_creationDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.gte_value}          ${EMPTY}             Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJob_Task_Tasks_with_Invalid_creationDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.gte_value}          ${EMPTY}             Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJob_Task_Products_with_Invalid_creationDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.lte_value}          worng_date           Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJob_Task_Tasks_with_Invalid_creationDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.lte_value}          worng_date           Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJob_Task_Products_with_Invalid_creationDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.gte_value}          ${EMPTY}             Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJob_Task_Tasks_with_Invalid_creationDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification        ${creationDate.lte_value}          ${EMPTY}             Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request
