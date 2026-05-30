*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../../GET_Method/Keywords/get_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***


Valid_Patch_with_Confirmed/Active_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingActive/Active_operationalStatus
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          PendingActive
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingModification_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingModification
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingTerminate_operationalStatus
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingTerminate

Invalid_Patch_Contract_with_not_sold_physicalProduct
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    invalid_patch_product_by_id_with_statuses               ${Product_id}[0]             Active           Active

Invalid_Patch_Contract_with_sold_physicalProduct
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    patch_product_by_id_with_statuses                ${Product_id}[3]             PhysicalProduct           Sold             Sold
    invalid_patch_product_by_id_with_statuses        ${Product_id}[0]             Active           Active

Invalid_Patch_BundleProductOffering_with_not_sold_physicalProduct
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    invalid_patch_product_by_id_with_statuses        ${Product_id}[1]             Active           Active

Valid_Patch_with_physicalProduct
    Get_token
    ${Product_id}               Post_valid_physical          Post_body_request_for_physical_product
    patch_product_by_id_with_statuses                ${Product_id}[3]             PhysicalProduct       Sold                         Sold
    patch_product_by_id_with_statuses                ${Product_id}[2]             PhysicalProduct       Sold                         Sold
    patch_product_by_id_with_statuses                ${Product_id}[1]             Product       Active                       Active
    patch_product_by_id_with_statuses                ${Product_id}[0]             Product       Active                       Active

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

    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product                Active                          Active
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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product                Active                          Active
    log                                                              ${Product_id}[0]

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
       patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct     Sold                            Sold
       patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct     Sold                            Sold
       patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product             Active                          Active
       patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product             Active                          Active
       log                                                              ${Product_id}[0]

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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product                Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_Contract_id_with_PendingMigrate_operationalStatus_From_Active
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product        Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           PendingMigrate

Valid_Patch_Contract_id_with_Active_operationalStatus_From_PendingMigrate
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product        Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           PendingMigrate
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           Active

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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product        Active                          PendingMigrate
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
    patch_product_by_id_with_statuses           ${Product_id}[3]                    PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses           ${Product_id}[2]                    PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id}          Product            Active                          Active
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id}          Product            Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id}        Product            Active                          Active
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id}        Product            Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id2}         Product            Active                          Active
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id2}         Product            Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id2}       Product            Active                          Active
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id2}       Product            Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Product_id}[1]                    Product            Active                          Active
    patch_product_by_id_with_statuses           ${Product_id}[1]                    Product            Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Product_id}[0]                    Product            Active                          Active
    patch_product_by_id_with_statuses           ${Product_id}[0]                    Product            Active                          PendingMigrate
    log                                         ${Product_id}[0]

#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- by ID -----------------
# Valid_Patch_ProductRelationship_ById_With_Confirmed_Then_Active
#    Get_token
#    ${valid_id}=    Post_valid    Post_body_request_for_Patch
#
#    patch_product_by_id_with_statuses       ${valid_id}     Created     Confirmed
#    patch_product_by_id_with_statuses       ${valid_id}     Active      Active

Valid_Patch_with_Confirmed/Active_operationalStatus_ById
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingActive/Active_operationalStatus_ById
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          PendingActive
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingModification_operationalStatus_ById
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingModification
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active

Valid_Patch_with_PendingTerminate_operationalStatus_ById
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingTerminate

Valid_Patch_with_Contract_and_All_Children_Status_Active_ById
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

    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product                Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Modification_ById
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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product    Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product    Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product    Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product    Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_with_Contract_and_All_Children_Status_Sold_ById
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
       patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct     Sold                            Sold
       patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct     Sold                            Sold
       patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product             Active                          Active
       patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product             Active                          Active
       log                                         ${Product_id}[0]

Valid_Patch_with_Mixed_Operational_Status_Children_ById
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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product                Active                          PendingModification
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product                Active                          Active
    log                                         ${Product_id}[0]

Valid_Patch_Contract_id_with_PendingMigrate_operationalStatus_From_Active_ById
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingMigrate

Valid_Patch_Contract_id_with_Active_operationalStatus_From_PendingMigrate_ById
    [Tags]      IPCEISCPIB-489

    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product        Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           PendingMigrate
    patch_product_by_id_with_statuses        ${valid_id}         Product        Active           Active

Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Migrate_ById
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
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering               ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering               ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses           ${Product_id}[3]                    PhysicalProduct                                     Sold                            Sold
    patch_product_by_id_with_statuses           ${Product_id}[2]                    PhysicalProduct                                     Sold                            Sold
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id}          Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id}          Product                                             Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id}        Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id}        Product                                             Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id2}         Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Spec_ProductOffering_id2}         Product                                             Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id2}       Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Atomic_ProductOffering_id2}       Product                                             Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Product_id}[1]                    Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Product_id}[1]                    Product                                             Active                          PendingMigrate
    patch_product_by_id_with_statuses           ${Product_id}[0]                    Product                                             Active                          Active
    patch_product_by_id_with_statuses           ${Product_id}[0]                    Product                                             Active                          PendingMigrate
    log                                         ${Product_id}[0]


Valid_Patch_with_Contract_and_All_Children_Operational_Status_Pending_Migrate_ById
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
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct        Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product        Active                          PendingMigrate
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product        Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product        Active                          PendingMigrate
    log                                                              ${Product_id}[0]
