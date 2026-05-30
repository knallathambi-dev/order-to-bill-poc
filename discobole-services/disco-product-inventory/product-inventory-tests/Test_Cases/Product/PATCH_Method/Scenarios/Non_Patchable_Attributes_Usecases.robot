*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_for_ID
    Get_token
    ${valid_id}                         Post_valid          Post_body_request_for_Patch
    patch_invalid_attribute             ${valid_id}         id                 id_value             Product

Invalid_Patch_for_Href
    Get_token
    ${valid_id}                         Post_valid          Post_body_request_for_Patch
    patch_invalid_attribute             ${valid_id}         href               href_value           Product

Invalid_Patch_for_creationDate
    [Tags]      bug
    Get_token
    ${Date_Now}                         Get_date
    ${valid_id}                         Post_valid          Post_body_request_for_Patch
    patch_invalid_attribute             ${valid_id}         creationDate       ${Date_Now}          Product

Invalid_Patch_for_@type_PhysicalProduct
    Get_token
    ${valid_id}                         Post_valid          Post_body_request_for_Patch
    patch_invalid_@type                 ${valid_id}         PhysicalProduct

Invalid_Patch_for_@type_ShipmentProduct
    Get_token
    ${valid_id}                         Post_valid          Post_body_request_for_Patch
    patch_invalid_@type                 ${valid_id}         ShipmentProduct
