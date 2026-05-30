*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Variables ***

${creationDate.gte_value}          2024-05-20T00:00:00Z
${creationDate.lte_value}          2024-05-29T00:00:00Z

*** Test Cases ***

Create_Invalid_ExportJob_Task_with_Invalid_creationDate.gte
    Get_token
    Post_JobSpecification_with_bad_date         ExportJobSpecification           ${creationDate.gte_value}          worng_date               Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJob_Task_with_Invalid_creationDate.gte
    Post_JobSpecification_with_bad_date         ExportJobSpecification           ${creationDate.gte_value}          ${EMPTY}                 Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJob_Task_with_Invalid_creationDate.lte
    Post_JobSpecification_with_bad_date         ExportJobSpecification           ${creationDate.lte_value}          worng_date               Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJob_Task_with_Invalid_creationDate.lte
    Post_JobSpecification_with_bad_date         ExportJobSpecification           ${creationDate.lte_value}          ${EMPTY}                 Post_ExportJobSpecification_JobSpecification_body_request
