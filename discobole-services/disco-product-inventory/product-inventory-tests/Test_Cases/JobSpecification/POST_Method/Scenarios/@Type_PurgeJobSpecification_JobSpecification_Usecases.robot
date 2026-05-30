*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Test Cases ***
# Functional verification complete: All tests passed and functionality is confirmed.
Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Bad_@type
    Get_token
    Post_JobSpecifications_with_bad_@typeJob    PurgeJobSpecification       worng_@type     Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Bad_@type
    Post_JobSpecifications_with_bad_@typeJob    PurgeJobSpecification       worng_@type     Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_@type
    Post_JobSpecifications_with_bad_@typeJob    PurgeJobSpecification       ${EMPTY}        Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_JobSpecifications_with_Empty_@type
    Post_JobSpecifications_with_bad_@typeJob    PurgeJobSpecification       ${EMPTY}        Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

