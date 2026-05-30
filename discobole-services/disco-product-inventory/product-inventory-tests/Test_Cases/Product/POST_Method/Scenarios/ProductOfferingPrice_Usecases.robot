*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_with_invalid_id_productOfferingPrice
    Post_with_bad_id_productOfferingPrice           worng_id             ${code_24}             ${invalid_reason}      Product Offering Price with id(s) : worng_id doesn't exist in catalog

Create_Product_with_empty_id_productOfferingPrice
    Post_with_bad_id_productOfferingPrice           ${EMPTY}             ${code_24}             ${invalid_reason}      Empty product offering price ID detected

Create_Product_with_nullable_id_productOfferingPrice
    Post_with_bad_id_productOfferingPrice           ${NULL}              ${code_23}             ${missing_reason}      productRelationship[0].product.productRelationship[0].product.productPrice[0].productOfferingPrice.id must not be null

Invalid_Product_Creation_with_string_amount_in_recurringChargePeriod
    Get_token
    Post_with_bad_field_recurringChargePeriod       amount               string_value           ${code_24}             ${invalid_reason}      amount field should be integer

Valid_Product_Creation_with_recurringChargePeriod
    Get_token
    Post_valid      Post_body_for_recurringChargePeriod

Valid_Product_Creation_with_nullable_price
    Get_token
    Post_valid      Post_body_for_with_nullable_price
