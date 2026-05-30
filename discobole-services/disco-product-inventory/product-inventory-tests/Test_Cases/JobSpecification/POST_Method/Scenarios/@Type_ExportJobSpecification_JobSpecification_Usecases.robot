*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Test Cases ***
 # Functional verification complete: All tests passed and functionality is confirmed.

Create_Invalid_ExportJob_Task_with_Bad_@type
    Get_token
    Post_JobSpecifications_with_bad_@typeJob    ExportJobSpecification       worng_@type     Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJob_Task_with_Empty_@type
    Post_JobSpecifications_with_bad_@typeJob    ExportJobSpecification       ${EMPTY}        Post_ExportJobSpecification_JobSpecification_body_request
