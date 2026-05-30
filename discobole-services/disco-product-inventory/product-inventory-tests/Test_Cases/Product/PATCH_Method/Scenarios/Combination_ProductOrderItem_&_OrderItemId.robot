*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_with_orderItemId_&_productOrderId_exists
    Get_token
    ${valid_id}                     Post_valid          Post_body_request
    patch_exist_productOrderItem    ${valid_id}         The couple orderItemId and productOrderId already exist

Invalid_Patch_with_empty_productOrderId
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_productOrderItem      ${valid_id}         ${EMPTY}                ${code_24}       ${invalid_reason}      Empty product order ID detected

Invalid_Patch_with_nullable_productOrderId
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_productOrderItem      ${valid_id}         ${NULL}                 ${code_23}       ${missing_reason}      productOrderItem[1].productOrderId must not be null

Invalid_Patch_with_empty_orderItemId
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_orderItemId           ${valid_id}         1001ddrr8ssx            ${EMPTY}         ${code_24}       ${invalid_reason}         Empty order item ID detected

Invalid_Patch_with_nullable_orderItemId
    ${valid_id}                     Post_valid          Post_body_request
    patch_bad_orderItemId           ${valid_id}         1001ddrr8ssx            ${NULL}          ${code_23}       ${missing_reason}         productOrderItem[1].orderItemId must not be null

Invalid_Patch_with_duplicates_couple
    Get_token
    ${valid_id}                     Post_valid          Post_body_request
    patch_duplicates_couple         ${valid_id}         The couple orderItemId and productOrderId is duplicated

Valid_Patch_with_orderItemId_&_productOrderId
    ${valid_id}                     Post_valid          Post_body_request
    patch_valid_productOrderItem    ${valid_id}


