*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_realtionship_with_same_product_Id
    Get_token
    ${valid_id}                     Post_valid          Post_body_request
    patch_same_relationship          ${valid_id}         sells                      ${valid_id}       One or more operation fields (op) must be verified

Invalid_Patch_realtionship_with_empty_product_Id
    Get_token
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_relationship          ${valid_id}         sells                      ${EMPTY}           One or more operation fields (op) must be verified

Invalid_Patch_realtionship_with_nullable_product_Id
    Get_token
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_relationship          ${valid_id}         sells                      ${Null}           One or more operation fields (op) must be verified
