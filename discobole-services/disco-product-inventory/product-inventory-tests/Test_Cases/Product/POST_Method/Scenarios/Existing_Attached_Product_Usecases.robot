*** Settings ***
Library  RequestsLibrary
Resource        ../Keywords/post_usecases.robot
Resource        ../../PATCH_Method/Keywords/patch_usecases.robot

*** Test Cases ***

# Valid Combination Usecases


Valid_Attach_BundleProductOffering_to_Contract
    Get_token
    ${Contract_Product_ID}                  Post_valid                                                     Post_body_request_for_attach_product
    ${BundleProductOffering_ID}            Post_for_attach             ${Contract_Product_ID}              Post_body_request_for_BundleProductOffering

Valid_Attach_AtomicproductOffering_to_BundleProductOffering

    ${Contract_Product_ID}                  Post_valid                                                     Post_body_request_for_attach_product
    ${BundleProductOffering_ID}            Post_for_attach             ${Contract_Product_ID}              Post_body_request_for_BundleProductOffering
    Post_for_attach                                                     ${BundleProductOffering_ID}        Post_body_request_for_AtomicProductOffering

Valid_Attach_ProductSpecifications_to_AtomicproductOffering

    ${Contract_Product_ID}                  Post_valid                                                     Post_body_request_for_attach_product
    ${BundleProductOffering_ID}            Post_for_attach             ${Contract_Product_ID}              Post_body_request_for_BundleProductOffering
    ${AtomicProductOffering_ID}             Post_for_attach             ${BundleProductOffering_ID}        Post_body_request_for_AtomicProductOffering
    Post_for_attach                                                     ${AtomicProductOffering_ID}        Post_body_request_for_ProductSpecifications

# Invalid Combination Usecases

# ID Usecases

Invalid_Attach_with_Empty_ID

    Post_for_attach_with_bad_id             ${EMPTY}              Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Nullable_ID

    Post_for_attach_with_bad_id             ${Null}               Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Inexisting_ID

    Post_for_attach_with_bad_id             Worng_ID              Post_body_request_for_BundleProductOffering

# Status Usecases

Invalid_Attach_with_Empty_Status

    ${Contract_Product_ID}                  Post_valid                                                  Post_body_request_for_attach_product
    Post_for_attach_with_bad_field          status          ${EMPTY}        ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Nullable_Status

    ${Contract_Product_ID}                  Post_valid                                                  Post_body_request_for_attach_product
    Post_for_attach_with_bad_field          status          ${NULL}         ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Cancelled_Status

    ${Contract_Product_ID}                  Post_valid                                                  Post_body_request_for_attach_product
    Bad_post_for_attach                     Cancelled       Cancelled       ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Aborted_Status

    ${Contract_Product_ID}                  Post_valid                                                  Post_body_request_for_attach_product
    Bad_post_for_attach                     Aborted         Aborted         ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Terminated_Status

    ${Contract_Product_ID}                  Post_valid                                                  Post_body_request_for_attach_product
    Bad_post_for_attach                     Terminated      Terminated      ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

# OperatonalStatus Usecases

Invalid_Attach_with_Empty_OperatonalStatus
    Get_token
    ${Contract_Product_ID}                  Post_valid                                                                  Post_body_request_for_attach_product
    Post_for_attach_with_bad_field          operationalStatus           ${EMPTY}            ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_Nullable_OperatonalStatus
    Get_token
    ${Contract_Product_ID}                          Post_valid                                                                  Post_body_request_for_attach_product
    Post_for_attach_with_operationalStatus          operationalStatus           ${NULL}             ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_PendingCancel_OperatonalStatus

    ${Contract_Product_ID}                  Post_valid                                                                  Post_body_request_for_attach_product
    Bad_post_for_attach_status                  Created                     PendingCancel       ${Contract_Product_ID}      Post_body_request_for_BundleProductOffering

Invalid_Attach_with_PendingTerminate_OperatonalStatus
    Get_token
    ${Contract_Product_ID}          Post_valid                                                                  Post_body_request_for_Patch
    patch_status                    ${Contract_Product_ID}          Created          Confirmed
    Bad_post_for_attach             ${Contract_Product_ID}          Active           PendingTerminate           Post_body_request_for_BundleProductOffering
