*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_with_empty_relatedParty
    Post_with_bad_list          relatedParty                    ${EMPTY}

Create_Product_with_empty_productRelationship
    Post_with_bad_list          productRelationship             ${EMPTY}

Create_Product_with_empty_productCharacteristic
    Post_with_bad_list          productCharacteristic           ${EMPTY}

Create_Product_with_empty_productPrice
    Post_with_bad_list          productPrice                    ${EMPTY}

Create_Product_with_nullable_relatedParty
    Post_with_bad_list          relatedParty                    ${NULL}

Create_Product_with_nullable_productRelationship
    Post_with_bad_list          productRelationship             ${NULL}

Create_Product_with_nullable_productCharacteristic
    Post_with_bad_list          productCharacteristic           ${NULL}

Create_Product_with_nullable_productPrice
    Post_with_bad_list          productPrice                    ${NULL}

