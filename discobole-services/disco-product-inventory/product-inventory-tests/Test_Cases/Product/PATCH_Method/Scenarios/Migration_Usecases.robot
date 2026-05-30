*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../../GET_Method/Keywords/get_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_relationship_migrateFrom
    [Tags]      IPCEISCPIB-640      migration
    Get_token
    ${product_id1}                               Post_valid_physical                 Post_body_request_for_physical_product
    log     ${product_id1}
    patch_with_migrateFrom                      ${product_id1}[0]          ${product_id1}[1]





