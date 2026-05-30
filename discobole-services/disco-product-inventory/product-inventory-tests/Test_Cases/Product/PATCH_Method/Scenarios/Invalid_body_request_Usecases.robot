*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_without_body_request
    Get_token
    ${valid_id}             Post_valid          Post_body_request
    patch_bad_body          ${valid_id}         ${Empty}                Invalid Input

Invalid_Patch_with_nullable_body_request
    Get_token
    ${valid_id}             Post_valid          Post_body_request
    patch_bad_body          ${valid_id}         ${NULL}                 Invalid Input

Invalid_Patch_with_empty_list
    Get_token
    ${valid_id}             Post_valid          Post_body_request
    ${Empty_list}           Create List
    patch_empty_list        ${valid_id}         ${Empty_list}

Invalid_Patch_with_empty_json_list
    Get_token
    ${valid_id}             Post_valid          Post_body_request
    ${My_list}              Create List
    ${Empty_json}           Create Dictionary
    Append to list          ${My_list}          ${Empty_json}
    patch_bad_list          ${valid_id}         ${My_list}

Invalid_Patch_with_worng_key_value
    Get_token
    ${valid_id}             Post_valid          Post_body_request
    ${My_list}              Create List
    ${My_json}              Create Dictionary   nonExistant_key         nonExistant_value
    Append to list          ${My_list}          ${My_json}
    patch_bad_list          ${valid_id}         ${My_list}
