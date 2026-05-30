*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***
${startDate.gte_value}          2024-05-15T00:00:00Z
${startDate.lte_value}          2024-05-30T00:00:00Z

*** Test Cases ***

Create_Invalid_ExportJobSpecification_JobSpecification_with_Invalid_startDate.gte1
    Get_token
    Post_JobSpecification_with_bad_date     ExportJobSpecification           ${startDate.gte_value}          worng_date          Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_Invalid_startDate.gte2
    Get_token
    Post_JobSpecification_with_bad_date     ExportJobSpecification           ${startDate.gte_value}          ${EMPTY}            Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_Invalid_startDate.lte1
    Get_token
    Post_JobSpecification_with_bad_date     ExportJobSpecification           ${startDate.lte_value}          worng_date          Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_Invalid_startDate.lte2
    Get_token
    Post_JobSpecification_with_bad_date     ExportJobSpecification           ${startDate.lte_value}          ${EMPTY}            Post_ExportJobSpecification_JobSpecification_body_request


