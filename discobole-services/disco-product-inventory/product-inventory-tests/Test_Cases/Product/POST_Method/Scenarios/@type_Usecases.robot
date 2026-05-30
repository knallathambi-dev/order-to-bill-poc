*** Settings ***
Resource            ../Keywords/post_usecases.robot


*** Test Cases ***

Creation_with_valid_@Type_for_Product
    Get_token
    Post_valid_@type        Product

Creation_with_valid_@Type_for_Offer
    Get_token
    Post_valid_@type        Offer

Creation_with_valid_@Type_for_SimCard
    Get_token
    Post_valid_@type        SimCard

Creation_with_valid_@Type_for_MobileLine
    Get_token
    Post_valid_@type        MobileLine

Creation_with_valid_@Type_for_Service
    Get_token
    Post_valid_@type        Service

Invalid_Creation_with_Empty_@Type_for_Product
    Get_token
    Post_empty_@type                Product                             ${EMPTY}

Invalid_Creation_with_nonExistant_@Type_for_BundleProductOffering
    Get_token
    Post_nonExistant_@type          BundleProductOffering               nonExistant_@Type

Invalid_Creation_with_Empty_@Type_for_BundleProductOffering
    Get_token
    Post_invalid_@type              BundleProductOffering               ${EMPTY}

Invalid_Creation_with_invalid_@Type_for_AtomicProductOffering
    Get_token
    Post_nonExistant_@type          AtomicProductOffering               nonExistant_@Type

Invalid_Creation_with_Empty_@Type_for_AtomicProductOffering
    Get_token
    Post_invalid_@type              AtomicProductOffering               ${EMPTY}
