*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_Patch_with_Active/Terminated_operationalStatus
    Get_token
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Terminated       Terminated

Valid_Patch_with_PendingModification/Terminated_operationalStatus
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingModification
    patch_product_by_id_with_statuses        ${valid_id}         Product    Terminated       Terminated

Valid_Patch_with_PendingTerminate/Terminated_operationalStatus
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_product_by_id_with_statuses        ${valid_id}         Product    Created          Confirmed
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           Active
    patch_product_by_id_with_statuses        ${valid_id}         Product    Active           PendingTerminate
    patch_product_by_id_with_statuses        ${valid_id}         Product    Terminated       Terminated

Invalid_Patch_Terminated_for_Contract_Product_with_PhysicalProduct_&_Active_BundleProductOffering
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_product_by_id_with_statuses                    ${Product_id}[3]             PhysicalProduct   Sold                         Sold
    patch_product_by_id_with_statuses                    ${Product_id}[2]             PhysicalProduct   Sold                         Sold
    patch_product_by_id_with_statuses                    ${Product_id}[1]             Product    Active                       Active
    patch_product_by_id_with_statuses                    ${Product_id}[0]             Product    Active                       Active
    invalid_terminated_status       ${product_id}[0]             Terminated                   Terminated                ${Product_id}[1]

Valid_Patch_with_All Children Terminated
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Terminated.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}        Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}         ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom              ${Product_id}[3]                   ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                                ${Product_id}[3]                    PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                    PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id}          Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id}        Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Spec_ProductOffering_id2}         Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Atomic_ProductOffering_id2}       Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All Children Aborted
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Aborted.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                       Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}        ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}       ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                        ${Product_id}[3]                        PhysicalProduct    Sold                               Sold
    patch_product_by_id_with_statuses                        ${Product_id}[2]                        PhysicalProduct    Sold                               Sold
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}             Product    Created                            Confirmed
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id}              Product    Created                            Confirmed
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}             Product    Created                            PendingActive
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id}              Product    Created                            PendingActive
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}             Product    Aborted                            Aborted
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id}              Product    Aborted                            Aborted
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}           Product    Created                            Confirmed
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id}            Product    Created                            Confirmed
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}           Product    Created                            PendingActive
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id}            Product    Created                            PendingActive
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}           Product    Aborted                            Aborted
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id}            Product    Aborted                            Aborted
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toPendingActive_toAborted
    [Tags]  IPCEISCPIB-486
    [Documentation]
    Get_token
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    ${product_id}                   Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}   ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                    ${Product_id}[3]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                    ${Product_id}[2]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                    ${Spec_ProductOffering_id}              Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Atomic_ProductOffering_id}            Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Spec_ProductOffering_id2}             Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Atomic_ProductOffering_id2}           Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toPendingActive_toLocked_toAborted
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                       Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}        ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}       ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                        ${Product_id}[3]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                        ${Product_id}[2]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id}              Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id}            Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}             Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}           Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]

Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toAborted
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Status for the bundle : created to aborted.
    #    Operational status for the bundle product : confirmed to aborted.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    ${product_id}                   Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}   ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                    ${Product_id}[3]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                    ${Product_id}[2]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                    ${Spec_ProductOffering_id}              Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Atomic_ProductOffering_id}            Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Spec_ProductOffering_id2}             Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                    ${Atomic_ProductOffering_id2}           Product    Cancelled                       Cancelled
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]

Valid_Patch_with_All Children Sold
    [Tags]  IPCEISCPIB-486
    [Documentation]
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Sold.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                               Post_valid_physical                     Post_body_request_for_physical_product
    log                                         ${Product_id}[0]
    patch_product_by_id_with_statuses                                ${Product_id}[3]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[2]                        PhysicalProduct    Sold                            Sold
    patch_product_by_id_with_statuses                                ${Product_id}[1]                        Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[0]                        Product    Active                          Active
    patch_product_by_id_with_statuses                                ${Product_id}[1]                        Product    Terminated                      Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[0]                        Product    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                        Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]



Valid_Patch_with_Mixed Status Children
    [Tags]  IPCEISCPIB-486
    [Documentation]
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: Child products have a mix  statuses.
    #    Action: Attempt to update the parent product to Terminated.
    #    Expected Result: Update fails; error message indicating that not all child products meet the status requirement.
    Get_token
    ${product_id}                       Post_valid_physical                 Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}        ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering            ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}       ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_product_by_id_with_statuses                        ${Product_id}[3]                    PhysicalProduct    Sold                       Sold
    patch_product_by_id_with_statuses                        ${Product_id}[2]                    PhysicalProduct    Sold                       Sold
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id}          Product    Cancelled                  Cancelled
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id}        Product    Cancelled                  Cancelled
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}         Product    Active                     Active
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}       Product    Active                     Active
    patch_product_by_id_with_statuses                        ${Spec_ProductOffering_id2}         Product    Terminated                 Terminated
    patch_product_by_id_with_statuses                        ${Atomic_ProductOffering_id2}       Product    Terminated                 Terminated
    patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Active                          Active
        patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Active                          Active
        patch_product_by_id_with_statuses                                ${Product_id}[1]                    Product    Terminated                      Terminated
        patch_product_by_id_with_statuses                                ${Product_id}[0]                    Product    Terminated                      Terminated
        valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
        log                                         ${Product_id}[0]


