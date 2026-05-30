*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot
Test Setup      Get_token

*** Test Cases ***
# In progress # dev
# QueryStatus supported [ Created, Terminated ]
Create_Valid_PurgeJobSpecification_JobSpecification_with_Created_QuerylifecycleStatus
    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Created                        Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

#Create_Valid_PurgeJobSpecification_JobSpecification_with_Running_QueryStatus
#    [Tags]    outdated
#    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Running                           Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Valid_PurgeJobSpecification_JobSpecification_with_Terminated_QuerylifecycleStatus
    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Terminated                         Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

#Create_Valid_PurgeJobSpecification_JobSpecification_with_Failed_QueryStatus
#    [Tags]    outdated
#    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Failed                            Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_with_Empty_QueryStatus
    Post_JobSpecification_with_bad_field        PurgeJobSpecification            ${EMPTY}                           Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_body_request

Create_Valid_PurgeJobSpecification_JobSpecification_with_tow_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus                  PurgeJobSpecification          Created                              Terminated                                  Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_multiple_status_body_request

Create_Valid_PurgeJobSpecification_JobSpecification_with_tow_same_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus                  PurgeJobSpecification          Terminated                              Terminated                                    Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_multiple_status_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_with_One_NoExistent_QueryStatus
    Post_JobSpecification_with_bad_multiple_QueryStatus              PurgeJobSpecification          Terminated                              NoExistent_status                       Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_multiple_status_body_request
    

#Create_Invalid_PurgeJobSpecification_JobSpecification_with_One_Empty_QueryStatus
#    Get_token
#    Post_JobSpecification_with_bad_multiple_QueryStatus              PurgeJobSpecification          Terminated                              ${EMPTY}                                Post_PurgeJobSpecification_JobSpecification_for_JobSpecifications_multiple_status_body_request
  


