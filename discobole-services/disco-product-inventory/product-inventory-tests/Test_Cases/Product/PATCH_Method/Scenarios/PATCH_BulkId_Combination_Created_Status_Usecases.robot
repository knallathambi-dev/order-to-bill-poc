*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_Patch_with_Confirmed_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          Confirmed

Valid_Patch_with_PendingActive_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          PendingActive

Valid_Patch_with_PendingCancel_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          PendingActive
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          PendingCancel

Valid_Patch_with_Locked_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          PendingActive
    patch_product_by_id_with_statuses        ${valid_id}         Product   Created          Locked

