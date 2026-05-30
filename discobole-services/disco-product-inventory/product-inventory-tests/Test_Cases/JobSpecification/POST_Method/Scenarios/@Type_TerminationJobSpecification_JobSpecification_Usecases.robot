*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Test Cases ***

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Bad_@type
    Get_token
    Post_JobSpecifications_with_bad_@typeJob    TerminationJobSpecification       worng_@type     Post_TerminationJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@type
    Post_JobSpecifications_with_bad_@typeJob    TerminationJobSpecification       ${EMPTY}        Post_TerminationJobSpecification_JobSpecification_for_Products_body_request

