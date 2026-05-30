*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../../GET_Method/Keywords/get_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

CheckingTheFieldPriceTypeIsMandatory
    [tags]      IPCEISCPIB-866
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch

Valid_Patch_with_Confirmed/Active_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active

Valid_Patch_with_PendingActive/Active_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Created          PendingActive
    patch_status        ${valid_id}         Active           Active

Valid_Patch_with_PendingModification_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingModification
    patch_status        ${valid_id}         Active           Active

Valid_Patch_with_PendingTerminate_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingTerminate

Invalid_Patch_Contract_with_not_sold_physicalProduct
    Get_token
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    invalid_patch_status        ${Product_id}[0]             Active           Active

Invalid_Patch_Contract_with_sold_physicalProduct
    Get_token
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    patch_status                ${Product_id}[3]             Sold             Sold
    invalid_patch_status        ${Product_id}[0]             Active           Active

Invalid_Patch_BundleProductOffering_with_not_sold_physicalProduct
    Get_token
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    invalid_patch_status        ${Product_id}[1]             Active           Active

Valid_Patch_with_physicalProduct
    Get_token
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    patch_status                ${Product_id}[3]             Sold                         Sold
    patch_status                ${Product_id}[2]             Sold                         Sold
    patch_status                ${Product_id}[1]             Active                       Active
    patch_status                ${Product_id}[0]             Active                       Active

Valid_Patch_with_Contract_and_All_Children_Status_Active
    [Tags]  IPCEISCPIB-485
    [Documentation]

    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
#    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
#    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}

    #patch_with_reliesOn_reliesFrom              ${Atomic_ProductOffering_id}          ${Product_id}[2]
    #patch_with_reliesOn_reliesFrom              ${Atomic_ProductOffering_id}         ${Atomic_ProductOffering_id}

    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Modification
    [Tags]  IPCEISCPIB-485
    [Documentation]

    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Spec_ProductOffering_id}          Active                          PendingModification
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          PendingModification
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          PendingModification
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          PendingModification
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Active                          PendingModification
    patch_status                                ${Product_id}[0]                    Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_with_Contract_and_All_Children_Status_Sold
    [Tags]  IPCEISCPIB-485
    [Documentation]

    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
       ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
       patch_status                                ${Product_id}[3]                    Sold                            Sold
       patch_status                                ${Product_id}[2]                    Sold                            Sold
       patch_status                                ${Product_id}[1]                    Active                          Active
       patch_status                                ${Product_id}[0]                    Active                          Active
       log                                         ${Product_id}[0]

Valid_Patch_with_Mixed_Operational_Status_Children
    [Tags]  IPCEISCPIB-485
    [Documentation]
    #
    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          PendingModification
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          PendingModification
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Active                          PendingModification
    patch_status                                ${Product_id}[0]                    Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_Contract_id_with_PendingMigrate_operationalStatus_From_Active
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingMigrate

Valid_Patch_Contract_id_with_Active_operationalStatus_From_PendingMigrate
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingMigrate
    patch_status        ${valid_id}         Active           Active

Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Migrate
    [Tags]  IPCEISCPIB-489      Migration
    [Documentation]

    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Spec_ProductOffering_id}          Active                          PendingMigrate
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          PendingMigrate
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          PendingMigrate
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          PendingMigrate
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Active                          PendingMigrate
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          PendingMigrate
    log                                         ${Product_id}[0]


Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Migrate
    [Tags]  IPCEISCPIB-489      Migration
    [Documentation]

    #    Valid Transition to Active/Operational Active:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition : For a parent product to transition to Active or Operational Active, there must be at least one direct child product in one of the following statuses:
    #    - Active/Active
    #    - Active/PendingModificate
    #    - Sold
    #    Action : Ensure that the parent product (Contract) status is updated to Active or Operational Active, provided the above conditions are met.
    #    Expected Result :
    #    - The parent product status is successfully updated to Active or Operational Active, respecting the main product status and operational status lifecycle.
    #    - The transition does not consider horizontal relationships (i.e., reliesOn/reliesFrom relationships are ignored during the status update).

    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Spec_ProductOffering_id}          Active                          PendingMigrate
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          PendingMigrate
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          PendingMigrate
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          PendingMigrate
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Active                          PendingMigrate
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          PendingMigrate
    log                                         ${Product_id}[0]


