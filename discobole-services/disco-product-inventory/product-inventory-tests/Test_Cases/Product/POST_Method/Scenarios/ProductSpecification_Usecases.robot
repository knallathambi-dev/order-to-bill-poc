*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_with_invalid_id_productSpecification
    Post_with_bad_field_productSpecification        id          worng_id             ${code_24}             ${invalid_reason}      Product Offering with id : 39d17453-6b6c-4902-9743-a50ec9ad5e67 has invalid relationship that doesn't exists in catalog with id worng_id

Create_Product_with_empty_id_productSpecification
    Post_with_bad_field_productSpecification        id          ${EMPTY}             ${code_24}             ${invalid_reason}      Empty product specification ID detected

Create_Product_with_nullable_id_productSpecification
    Post_with_bad_field_productSpecification        id          ${NULL}              ${code_23}             ${missing_reason}      productRelationship[0].product.productRelationship[0].product.productRelationship[0].product.productSpecification.id must not be null

Create_Product_with_empty_@tType_productSpecification
    Post_with_bad_field_productSpecification        @type       ${EMPTY}             ${code_24}             ${invalid_reason}      productRelationship[0].product.productRelationship[0].product.productRelationship[0].product.productSpecification.atType must not be empty               #Empty product specification ID detected

Create_Product_with_nullable_@tType_productSpecification
    Post_with_bad_field_productSpecification        @type       ${NULL}              ${code_23}             ${missing_reason}      productRelationship[0].product.productRelationship[0].product.productRelationship[0].product.productSpecification.atType must not be null

