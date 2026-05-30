*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${startDate.gte_value}          2024-05-15T00:00:00Z
${startDate.lte_value}          2024-05-30T00:00:00Z

*** Test Cases ***

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Invalid_startDate.gte
    Get_token
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.gte_value}          worng_date          Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Invalid_startDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.gte_value}          worng_date          Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_with_Invalid_startDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.gte_value}          ${EMPTY}            Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Invalid_startDate.gte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.gte_value}          ${EMPTY}            Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Invalid_startDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.lte_value}          worng_date          Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Invalid_startDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.lte_value}          worng_date          Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Invalid_startDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.lte_value}          ${EMPTY}            Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Invalid_startDate.lte
    Post_JobSpecification_with_bad_date     PurgeJobSpecification           ${startDate.lte_value}          ${EMPTY}            Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request
