*** Settings ***
Resource        ../Keywords/delete_usecases.robot
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../../GET_Method/Keywords/get_usecases.robot

*** Test Cases ***

Delete_Product_by_Valid_Id
    [Tags]  Delete_Method
    Get_token
    ${id}=                  Post_valid      Post_body_request
    delete_product          ${id}
    invalid_get             ${id}

Delete_Product_by_NonExistent_Id
    [Tags]  Delete_Method    bug_IPCEISCPIB-4167
    Get_token
    bad_delete_product      worng_id        ${code_60}              ${notfound_reason}          The product with id worng_id does not exist

Delete_Product_by_Empty_Id
    [Tags]  Delete_Method
    Get_token
    bad_delete_product      ${EMPTY}        ${code_60}              ${notfound_reason}          The requested URI or the requested resource does not exist.

Check_Relationship_for_BundleProductOffering_after_Delete_Product
    [Tags]  Delete_Method
    Get_token
    ${id}                                   Post_valid_physical                     Post_body_request_for_physical_product
    delete_product                          ${id}[0]
    check_remove_relationships              ${id}[1]

Check_Relationship_for_AtomicProductOffering_after_Delete_Product
    [Tags]  Delete_Method    bug_IPCEISCPIB-4167
    Get_token
    ${id}                                   Post_valid_physical                     Post_body_request_for_physical_product
    delete_product                          ${id}[0]
    check_remove_relationships              ${id}[2]
