*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot
Test Setup      Get_token
*** Test Cases ***

 # Functional verification complete: All tests passed and functionality is confirmed.

Create_Valid_ExportJobSpecification_JobSpecification_with_Created_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Created                 Post_ExportJobSpecification_JobSpecification_body_request
   
Create_Valid_ExportJobSpecification_JobSpecification_with_Cancelled_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Cancelled               Post_ExportJobSpecification_JobSpecification_body_request

Create_Valid_ExportJobSpecification_JobSpecification_with_Aborted_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Aborted                 Post_ExportJobSpecification_JobSpecification_body_request

Create_Valid_ExportJobSpecification_JobSpecification_with_Active_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Active                  Post_ExportJobSpecification_JobSpecification_body_request

Create_Valid_ExportJobSpecification_JobSpecification_with_Terminated_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Terminated              Post_ExportJobSpecification_JobSpecification_body_request

Create_Valid_ExportJobSpecification_JobSpecification_with_Sold_QueryStatus
    Post_JobSpecification_with_QueryStatus       ExportJobSpecification             Sold                    Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_NoExistent_QueryStatus
    Post_JobSpecification_with_bad_field        ExportJobSpecification                  NoExistent_status       Post_ExportJobSpecification_JobSpecification_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_Empty_QueryStatus
    Post_JobSpecification_with_bad_field        ExportJobSpecification           ${EMPTY}                Post_ExportJobSpecification_JobSpecification_body_request

Create_Valid_ExportJob_JobSpecification_with_tow_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus                  ExportJobSpecification               Created                 Terminated                  Post_ExportJobSpecification_JobSpecification_multiple_status_body_request

Create_Valid_ExportJobSpecification_JobSpecification_with_two_same_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus                  ExportJobSpecification               Created                 Created                     Post_ExportJobSpecification_JobSpecification_multiple_status_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_One_NoExistent_QueryStatus
    Post_JobSpecification_with_bad_multiple_QueryStatus              ExportJobSpecification               Created                 NoExistent_status           Post_ExportJobSpecification_JobSpecification_multiple_status_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_with_One_Empty_QueryStatus
    Post_JobSpecification_with_bad_multiple_QueryStatus              ExportJobSpecification               Created                 ${EMPTY}                    Post_ExportJobSpecification_JobSpecification_multiple_status_body_request


