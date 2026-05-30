*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_productCharacteristic_with_Valid_String
    Get_token
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   String                 cpib_string

Invalid_Patch_productCharacteristic_with_Invalid_String
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   String                 ${TRUE}

Invalid_Patch_productCharacteristic_with_Empty_String
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   String                 ${EMPTY}

Invalid_Patch_productCharacteristic_with_Nullable_String
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   String                 ${NULL}

Valid_Patch_productCharacteristic_with_Valid_boolean
    Get_token
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_valid_boolean_productCharacteristic   ${valid_id}                   boolean                ${FALSE}                   false

Valid_Patch_productCharacteristic_with_Invalid_boolean
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   boolean                worng_value

Invalid_Patch_productCharacteristic_with_Empty_boolean
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   boolean                ${EMPTY}

Invalid_Patch_productCharacteristic_with_Nullable_boolean
    ${valid_id}                                 Post_valid_for_productCharacteristic                 Post_body_request
    patch_bad_productCharacteristic             ${valid_id}                   boolean                ${NULL}
