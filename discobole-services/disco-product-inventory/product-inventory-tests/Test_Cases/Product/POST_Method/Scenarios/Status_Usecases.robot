*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***


Create_Product_with_Cancelled_status
    Get_token
    Post_with_status     status                  Cancelled       ${code_24}       ${invalid_reason}       The status should be Created

Create_Product_with_Aborted_status
    Get_token
    Post_with_status     status                  Aborted         ${code_24}       ${invalid_reason}       The status should be Created

Create_Product_with_Active_status
    Get_token
    Post_with_status     status                  Active          ${code_24}       ${invalid_reason}       The status should be Created

Create_Product_with_Terminated_status
    Get_token
    Post_with_status     status                  Terminated      ${code_24}       ${invalid_reason}       The status should be Created

Create_Product_with_Empty_status
    Get_token
    Post_with_status     status                  ${EMPTY}        ${code_24}       ${invalid_reason}       Invalid 'status' Field

Create_Product_with_Nullable_status
    Get_token
    Post_with_status     status                  ${NULL}         ${code_23}       ${missing_reason}       status must not be null

Create_Product_with_Created_status_&_PendingActive_operationalStatus
    Get_token
    Post_with_status     operationalStatus       PendingActive   ${code_24}       ${invalid_reason}       The operational status should be created or confirmed

Create_Product_with_Created_status_&_PendingCancel_operationalStatus
    Get_token
    Post_with_status     operationalStatus       PendingCancel   ${code_24}       ${invalid_reason}       The operational status should be created or confirmed

Create_Product_with_Created_status_&_Locked_operationalStatus
    Get_token
    Post_with_status     operationalStatus       Locked          ${code_24}       ${invalid_reason}       The operational status should be created or confirmed

Created_Product_with_Created_status_&_Created_operationalStatus
    Get_token
    Post_with_valid_status_&_operationalStatus                 Created           Created

Created_Product_with_Created_status_&_Confirmed_operationalStatus
    Get_token
    Post_with_valid_status_&_operationalStatus                 Created           Confirmed
