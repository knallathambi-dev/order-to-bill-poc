*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_with_invalid_id
    Post_with_bad_productOffering    ["productOffering"]     id      worng-id                       ${code_24}       ${invalid_reason}     Product Offering with id(s) : worng-id doesn't exist in catalog

Create_Product_with_empty_id
    Post_with_bad_productOffering    ["productOffering"]     id      ${EMPTY}                       ${code_24}       ${invalid_reason}     Product offering id cannot be empty

Create_Product_with_nullable_id
    Post_with_bad_productOffering    ["productOffering"]     id      ${NULL}                        ${code_23}       ${missing_reason}     productOffering.id must not be null

Create_Product_without_@type
    Post_with_remove_field           ["productOffering"]     @type                                                                         productOffering.atType must not be null

Create_Product_with_another_@type
    Post_with_valid_productOffering    ["productOffering"]     @type   AtomicProductOffering

Create_Product_with_nonExistent_@type
    Post_with_valid_productOffering    ["productOffering"]     @type   Worng_@type

Create_Product_with_empty_@type
    Post_with_bad_productOffering    ["productOffering"]     @type   ${EMPTY}                       ${code_24}       ${invalid_reason}     productOffering.atType must not be empty

Create_Product_with_nullable_@type
    Post_with_bad_productOffering    ["productOffering"]     @type   ${NULL}                        ${code_23}       ${missing_reason}     productOffering.atType must not be null

Create_Product_without_@type_in_productRelationship
    Post_with_remove_field            ["productRelationship"][0]["product"]["productOffering"]      @type                                  productRelationship[0].product.productOffering.atType must not be null

Create_Product_with_another_@type_in_productRelationship
    Post_with_valid_productOffering    ["productRelationship"][0]["product"]["productOffering"]       @type           AtomicProductOffering

Create_Product_with_empty_@type_in_productRelationship
    Post_with_bad_productOffering    ["productRelationship"][0]["product"]["productOffering"]       @type           ${EMPTY}               ${code_24}             ${invalid_reason}     productRelationship[0].product.productOffering.atType must not be empty

Create_Product_with_nullable_@type_in_productRelationship
    Post_with_bad_productOffering    ["productRelationship"][0]["product"]["productOffering"]       @type           ${NULL}                ${code_23}             ${missing_reason}     productRelationship[0].product.productOffering.atType must not be null

Create_Atomic_Product_atomic_and_productSpecification
    Get_token
    Post_valid      Post_body_request_for_fusion_PO_PS
