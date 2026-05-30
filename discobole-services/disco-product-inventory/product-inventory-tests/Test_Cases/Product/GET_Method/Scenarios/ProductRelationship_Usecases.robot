*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_bundles_relationshipType
    [Tags]  Get_Method
    Get_token
    get_valid_relationshipType         bundles

# Obselete use case
#Get_Product_By_valid_sells_relationshipType
#    [Tags]  Get_Method
#    Get_token
#    get_valid_relationshipType             sells

Get_Product_By_valid_isSold_relationshipType
    [Tags]  Get_Method
    Get_token
    get_bad_relationshipType_Allowed           isSold

Get_Product_By_invalid_relationshipType
    [Tags]  Get_Method
    [Documentation]     Actually we accept any string
    Get_token
    get_bad_relationshipType_Allowed           worng_relationshipType

Get_Product_By_empty_relationshipType
    [Tags]  Get_Method
    [Documentation]     Actually we accept any string
    Get_token
    get_bad_relationshipType           ${EMPTY}                                 productRelationship.relationshipType must not be empty

Get_Product_By_nonExistent_relationshipType
    [Tags]  Get_Method
    [Documentation]     Actually we accept any string
    Get_token
    get_bad_relationshipType_Allowed           _relationshipType

Get_Products_By_valid_ID_product_relationshipType
    [Tags]  Get_Method
    Get_token
    ${id}=      Post_valid      Post_body_request
    get_relationshipType_productId          ${id}

Get_Product_By_nonExistent_ID_product_relationshipType
    [Tags]  Get_Method
    Get_token
    get_relationshipType_bad_productId     worng_id             	Invalid format for product relationShip product.id

Get_Product_By_Empty_ID_product_relationshipType
    [Tags]  Get_Method
    Get_token
    get_relationshipType_bad_productId     ${EMPTY}                 productRelationship.product.id must not be empty
