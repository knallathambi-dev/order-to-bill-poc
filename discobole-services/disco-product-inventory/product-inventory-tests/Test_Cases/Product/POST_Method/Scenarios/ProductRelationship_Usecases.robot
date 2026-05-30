*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

#Sells

Invalid_Creation_Product_Sells_relationshipType_with_productOffering_without_productSpecification
    Post_with_valid_productOffering    ["productRelationship"][0]       relationshipType           sells

Invalid_Creation_Product_Sells_relationshipType_without_productOffering_&_productSpecification
    Post_with_removed_data           ${EMPTY}       productOffering      ${code_24}             ${invalid_reason}      Null productOffering & null productSpecification cannot be accepted     Post_body_request_for_sells

Invalid_Creation_Product_Sells_relationshipType_without_productOffering_with_productSpecification
    Post_valid_with_json           Post_body_request_for_sells

Invalid_Creation_Product_Sells_relationshipType_without_productOrderItem
    Post_with_removed_data           ${EMPTY}       productOrderItem      ${code_24}             ${invalid_reason}    productOrderItem must not be empty    Post_body_request_for_sells

Invalid_Creation_Product_Sells_relationshipType_with_@Type_Contract
    Post_with_valid_productOffering_for_sells           ["productOffering"]       @type           Contract

Invalid_Creation_Product_Sells_relationshipType_with_@Type_BundleProductOffering
    Post_with_valid_productOffering_for_sells           ["productOffering"]       @type           BundleProductOffering

Valid_Creation_Product_Sells_relationshipType_with_@Type_AtomicProductOffering
    Post_with_valid_productOffering_for_sells           ["productOffering"]       @type           AtomicProductOffering

# Bundles

Valid_Creation_Product_Bundles_relationshipType_with_productOffering
    Post_valid_with_json        Post_body_request

Invalid_Creation_Product_Bundles_relationshipType_without_productOffering_&_productSpecification
    Post_with_removed_data           ${EMPTY}       productOffering       ${code_24}             ${invalid_reason}     Null productOffering & null productSpecification cannot be accepted     Post_body_request

Invalid_Creation_Product_Bundles_relationshipType_without_productOrderItem
    Get_token
    Post_with_removed_data           ${EMPTY}       productOrderItem      ${code_24}             ${invalid_reason}     productOrderItem must not be empty     Post_body_request

Valid_Creation_Product_Bundles_relationshipType_with_@Type_Contract
    Post_with_valid_productOffering     ["productOffering"]       @type             Contract

Invalid_Creation_Product_Bundles_relationshipType_with_@Type_BundleProductOffering
    Post_with_valid_productOffering     ["productOffering"]       @type             BundleProductOffering

Invalid_Creation_Product_Bundles_relationshipType_with_@Type_AtomicProductOffering
    Post_with_valid_productOffering       ["productOffering"]       @type           AtomicProductOffering

#isSold

Create_Product_with_invalid_isSold_relationshipType
    Post_with_valid_productOffering    ["productRelationship"][0]       relationshipType           isSold

# Others

Create_Product_with_empty_relationshipType
    Post_with_valid_productOffering    ["productRelationship"][0]       relationshipType           ${EMPTY}

Create_Product_with_nullable_relationshipType
    Post_with_bad_productOffering    ["productRelationship"][0]       relationshipType              ${NULL}             ${code_23}             ${missing_reason}      productRelationship[0].relationshipType must not be null

Create_Product_with_invalid_Bundles_productRelationship
    Post_with_Bundles                Invalid product with product relationshipType: Bundles
