*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_Patch_Sold_with_Created_operationalStatus_for_PhysicalProduct
    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_status                    ${product_id}[3]             Sold          Sold

Valid_Patch_Sold_with_Created_operationalStatus_for_ShipmentProduct
     [Documentation]
        #        This test verifies that a ShipmentProduct can be updated to 'Sold' status, with only partial dependency setup.
        #
        #        INPUTS:
        #        - Auth token
        #        - Shipment and Physical product creation payloads
        #
        #        STEPS:
        #        1. Authenticate and get token.
        #        2. Create a Shipment product.
        #        3. Create a Physical product.
        #        4. (Skipped) Attach Product Offerings and set full dependencies.
        #        5. Patch reliesOn relationship: Shipment product → Physical product.
        #        6. Patch Shipment product status to 'Sold'.
        #        7. Log the Shipment product ID.
        #
        #        OUTPUT:
        #        - Patched Shipment product ID
        #
        #        EXPECTED RESULT:
        #        - Shipment product is updated to 'Sold' even with minimal dependency setup.

    Get_token
    ${product_id1}                              Post_valid_Shipment                      Post_body_request_for_shipment_product
    ${product_id2}                              Post_valid_physical                      Post_body_request_for_physical_product
    #    ${Atomic_ProductOffering_id}           ${Spec_ProductOffering_id}               Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    #    ${Atomic_ProductOffering_id2}          ${Spec_ProductOffering_id2}              Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    #    patch_with_reliesOn_reliesFrom         ${Spec_ProductOffering_id}               ${Spec_ProductOffering_id2}
    #    patch_with_reliesOn_reliesFrom         ${Product_id2}[3]                        ${Spec_ProductOffering_id}
    patch_with_reliesOn_reliesFrom              ${Product_id1}[1]                        ${Product_id2}[3]
    patch_status                                ${Product_id1}[1]                        Sold                           Sold
    log     ${Product_id1}[1]

Valid_Patch_Sold_with_Confirmed_operationalStatus_for_PhysicalProduct
    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_status                    ${product_id}[3]             Created       Confirmed
    patch_status                    ${product_id}[3]             Sold          Sold

Valid_Patch_Sold_with_Confirmed_operationalStatus_for_ShipmentProduct
    [Documentation]
    #    This test case verifies the status patching workflow for Shipment and Physical products, ensuring proper transitions through operational statuses.
    #
    #    INPUTS:
    #    - Request bodies for creating Shipment and Physical products.
    #    - Product IDs returned after creation.
    #
    #    STEPS:
    #    1. Create Shipment product and store its ID.
    #    2. Create Physical product and store its ID.
    #    3. Patch a dependency relationship: Shipment product reliesOn Physical product.
    #    4. Patch Shipment product status from 'Created' to 'Confirmed'.
    #    5. Patch Shipment product status from 'Sold' to 'Sold' (final status).
    #    6. Log the Shipment product ID for verification.
    #
    #    OUTPUTS:
    #    - Product IDs of created Shipment and Physical products.
    #    - Updated status of Shipment product.
    #
    #    EXPECTED RESULT:
    #    - Shipment product status updates successfully follow the expected lifecycle states.
    #    - Dependency between Shipment and Physical product is established.

    ${product_id1}                              Post_valid_Shipment                      Post_body_request_for_shipment_product
    ${product_id2}                              Post_valid_physical                      Post_body_request_for_physical_product

    patch_with_reliesOn_reliesFrom              ${Product_id1}[1]                        ${Product_id2}[3]
    patch_status                                ${product_id1}[1]                        Created                        Confirmed
    patch_status                                ${Product_id1}[1]                        Sold                           Sold
    log                                         ${Product_id1}[1]

Invalid_Patch_Sold_for_Contract_with_PhysicalProduct
    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_worng_status              ${product_id}[0]             Sold          Sold                         The status Created cannot be modified into Sold

Invalid_Patch_Sold_for_Contract_with_ShipmentProduct
    Get_token
    ${product_id1}                              Post_valid_Shipment                      Post_body_request_for_shipment_product
    ${product_id2}                              Post_valid_physical                      Post_body_request_for_physical_product
    patch_with_reliesOn_reliesFrom              ${Product_id1}[1]                        ${Product_id2}[3]
    ${Atomic_ProductOffering_id}           ${Spec_ProductOffering_id}               Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}          ${Spec_ProductOffering_id2}              Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom         ${Spec_ProductOffering_id}               ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom         ${Product_id2}[3]                        ${Spec_ProductOffering_id}
    patch_worng_status                     ${Product_id2}[0]                        Sold                   Sold                          The status Created cannot be modified into Sold

Invalid_Patch_Sold_for_BundleProductOffering_with_PhysicalProduct
    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_worng_status              ${product_id}[1]             Sold          Sold                         The status Created cannot be modified into Sold

Invalid_Patch_Sold_for_BundleProductOffering_with_ShipmentProduct
    Get_token
    ${product_id1}                              Post_valid_Shipment                      Post_body_request_for_shipment_product
    ${product_id2}                              Post_valid_physical                      Post_body_request_for_physical_product
    patch_with_reliesOn_reliesFrom              ${Product_id1}[1]                        ${Product_id2}[3]
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}               Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}              Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}               ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id2}[3]                        ${Spec_ProductOffering_id}
    patch_worng_status                          ${Product_id2}[1]                        Sold                   Sold                          The status Created cannot be modified into Sold


Invalid_Patch_Sold_for_PhysicalProduct
    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    invalid_patch_status            ${product_id}[2]             Sold          Sold

Invalid_Patch_Sold_for_ShipmentProduct
    [Tags]      Bug-bad-status

    Get_token
    ${product_id}                               Post_valid_Shipment                      Post_body_request_for_shipment_product
    ${product_id2}                              Post_valid_physical                      Post_body_request_for_physical_product
    patch_with_reliesOn_reliesFrom              ${Product_id}[1]                         ${Product_id2}[3]
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}               Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}              Post_for_attach_AtomicProductOffering                ${Product_id2}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}               ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id2}[3]                        ${Spec_ProductOffering_id}
    log         ${Product_id}[1]
    Log         ${Product_id2}[3]
    invalid_patch_status                        ${product_id}[0]                         Sold                           Sold
