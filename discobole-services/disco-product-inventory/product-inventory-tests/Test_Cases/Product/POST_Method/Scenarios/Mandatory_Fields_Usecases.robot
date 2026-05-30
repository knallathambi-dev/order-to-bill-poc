*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_without_status
    Get_token
    Post_with_remove_field           ${EMPTY}                                status                  status must not be null

Create_Product_without_relatedParty
   [Tags]  Bug    check_expected_result
    Get_token
    Post_with_remove_field           ["relatedParty"][0]                     id                      relatedParty[0].id must not be null

Create_Product_without_relationshipType
    Get_token
    Post_with_remove_field           ["productRelationship"][0]              relationshipType        productRelationship[0].relationshipType must not be null

Create_Product_without_productOrderItem
    Get_token
    Post_with_remove_field           ["productRelationship"][0]["product"]   productOrderItem        productRelationship[0].product.productOrderItem must not be empty
