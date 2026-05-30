*** Settings ***
Resource        ../Keywords/post_JobSpecifications_usecases.robot

*** Test Cases ***
Create_Valid_PurgeJobSpecification_JobSpecification_for_Products_with_Sold_QueryStatus
    Get_token
    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Sold                              Post_PurgeJobSpecification_JobSpecification_for_Products_body_request
 # bug :   IPCEISCPIB-545
Create_Valid_PurgeJobSpecification_JobSpecification_for_Products_with_Aborted_QueryStatus
    Post_JobSpecification_with_QueryStatus       PurgeJobSpecification            Aborted                           Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Valid_PurgeJobSpecification_JobSpecification_for_Products_with_Cancelled_QueryStatus
    Post_JobSpecification_with_QueryStatus      PurgeJobSpecification            Cancelled                          Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Valid_PurgeJobSpecification_JobSpecification_for_Products_with_Terminated_QueryStatus
    Post_JobSpecification_with_QueryStatus      PurgeJobSpecification            Terminated                         Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_NoExistent_QueryStatus
    Post_JobSpecification_with_bad_field        PurgeJobSpecification            NoExistent_status                  Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Invalid_PurgeJobSpecification_JobSpecification_for_Products_with_Empty_QueryStatus
    Post_JobSpecification_with_bad_field        PurgeJobSpecification            ${EMPTY}                           Post_PurgeJobSpecification_JobSpecification_for_Products_body_request

Create_Valid_ExportJobSpecification_JobSpecification_for_Products_with_tow_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Aborted                 Cancelled                     Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Aborted                 Terminated                    Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Cancelled               Terminated                    Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request

Create_Valid_ExportJobSpecification_JobSpecification_for_Products_with_tow_same_QueryStatus
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Aborted                 Aborted                       Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Cancelled               Cancelled                     Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_multiple_QueryStatus              PurgeJobSpecification          Terminated              Terminated                    Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_for_Products_with_One_NoExistent_QueryStatus
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Aborted                 NoExistent_status             Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Cancelled               NoExistent_status             Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Terminated              NoExistent_status             Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request

Create_Invalid_ExportJobSpecification_JobSpecification_for_Products_with_One_Empty_QueryStatus
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Created                 ${EMPTY}                      Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Cancelled               ${EMPTY}                      Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
    Post_JobSpecification_with_bad_multiple_QueryStatus          PurgeJobSpecification          Terminated              ${EMPTY}                      Post_PurgeJobSpecification_JobSpecification_for_Products_multiple_status_body_request
