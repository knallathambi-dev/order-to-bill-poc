*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_Patch_with_Active/Terminated_operationalStatus
    [Tags]  Patch_Method
    Get_token
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Terminated       Terminated

Valid_Patch_with_PendingModification/Terminated_operationalStatus
    [Tags]  Patch_Method
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingModification
    patch_status        ${valid_id}         Terminated       Terminated

Valid_Patch_with_PendingTerminate/Terminated_operationalStatus
    [Tags]  Patch_Method
    ${valid_id}         Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Active           Active
    patch_status        ${valid_id}         Active           PendingTerminate
    patch_status        ${valid_id}         Terminated       Terminated

Invalid_Patch_Terminated_for_Contract_Product_with_PhysicalProduct_&_Active_BundleProductOffering
    [Tags]  Patch_Method
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    patch_status                    ${Product_id}[3]             Sold                         Sold
    patch_status                    ${Product_id}[2]             Sold                         Sold
    patch_status                    ${Product_id}[1]             Active                       Active
    patch_status                    ${Product_id}[0]             Active                       Active

    invalid_terminated_status       ${product_id}[0]             Terminated                   Terminated                ${Product_id}[1]

Valid_Patch_with_All Children Terminated
    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
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
    patch_status                                ${Product_id}[3]                    Sold                            Sold
    patch_status                                ${Product_id}[2]                    Sold                            Sold
    patch_status                                ${Spec_ProductOffering_id}          Active                          Active
    patch_status                                ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                                ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                                ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                                ${Spec_ProductOffering_id}          Terminated                      Terminated
    patch_status                                ${Atomic_ProductOffering_id}        Terminated                      Terminated
    patch_status                                ${Spec_ProductOffering_id2}         Terminated                      Terminated
    patch_status                                ${Atomic_ProductOffering_id2}       Terminated                      Terminated
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Terminated                      Terminated
    patch_status                                ${Product_id}[0]                    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All Children Aborted
    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
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
    patch_status                        ${Product_id}[3]                        Sold                               Sold
    patch_status                        ${Product_id}[2]                        Sold                               Sold
    patch_status                        ${Spec_ProductOffering_id2}             Created                            Confirmed
    patch_status                        ${Spec_ProductOffering_id}              Created                            Confirmed
    patch_status                        ${Spec_ProductOffering_id2}             Created                            PendingActive
    patch_status                        ${Spec_ProductOffering_id}              Created                            PendingActive
    patch_status                        ${Spec_ProductOffering_id2}             Aborted                            Aborted
    patch_status                        ${Spec_ProductOffering_id}              Aborted                            Aborted
    patch_status                        ${Atomic_ProductOffering_id2}           Created                            Confirmed
    patch_status                        ${Atomic_ProductOffering_id}            Created                            Confirmed
    patch_status                        ${Atomic_ProductOffering_id2}           Created                            PendingActive
    patch_status                        ${Atomic_ProductOffering_id}            Created                            PendingActive
    patch_status                        ${Atomic_ProductOffering_id2}           Aborted                            Aborted
    patch_status                        ${Atomic_ProductOffering_id}            Aborted                            Aborted
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Terminated                      Terminated
    patch_status                                ${Product_id}[0]                    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toPendingActive_toAborted
    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                   Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}   ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_status                    ${Product_id}[3]                        Sold                            Sold
    patch_status                    ${Product_id}[2]                        Sold                            Sold
    patch_status                    ${Spec_ProductOffering_id}              Cancelled                       Cancelled
    patch_status                    ${Atomic_ProductOffering_id}            Cancelled                       Cancelled
    patch_status                    ${Spec_ProductOffering_id2}             Cancelled                       Cancelled
    patch_status                    ${Atomic_ProductOffering_id2}           Cancelled                       Cancelled
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Terminated                      Terminated
    patch_status                                ${Product_id}[0]                    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toPendingActive_toLocked_toAborted
    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
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
    patch_status                        ${Product_id}[3]                        Sold                            Sold
    patch_status                        ${Product_id}[2]                        Sold                            Sold
    patch_status                        ${Spec_ProductOffering_id}              Cancelled                       Cancelled
    patch_status                        ${Atomic_ProductOffering_id}            Cancelled                       Cancelled
    patch_status                        ${Spec_ProductOffering_id2}             Cancelled                       Cancelled
    patch_status                        ${Atomic_ProductOffering_id2}           Cancelled                       Cancelled
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Terminated                      Terminated
    patch_status                                ${Product_id}[0]                    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]

Valid_Patch_with_All_Children_Cancelled_Operational_Status_Confirmed_toAborted
    [Tags]  Patch_Method
    [Documentation]
    #   IPCEISCPIB-486
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Status for the bundle : created to aborted.
    #    Operational status for the bundle product : confirmed to aborted.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    ${product_id}                       Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}        ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}       ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering                ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}              ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                        ${Spec_ProductOffering_id}
    patch_status                        ${Product_id}[3]                        Sold                            Sold
    patch_status                        ${Product_id}[2]                        Sold                            Sold
    patch_status                        ${Spec_ProductOffering_id}              Cancelled                       Cancelled
    patch_status                        ${Atomic_ProductOffering_id}            Cancelled                       Cancelled
    patch_status                        ${Spec_ProductOffering_id2}             Cancelled                       Cancelled
    patch_status                        ${Atomic_ProductOffering_id2}           Cancelled                       Cancelled
    patch_status                        ${Product_id}[1]                        Active                          Active
    patch_status                        ${Product_id}[0]                        Active                          Active
    patch_status                        ${Product_id}[1]                        Terminated                      Terminated
    patch_status                        ${Product_id}[0]                        Terminated                      Terminated
    valid_terminated_status             ${product_id}[0]                        Terminated                      Terminated                ${Product_id}[1]
    log                                 ${Product_id}[0]

Valid_Patch_with_All Children Sold

    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Sold.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.
    Get_token
    ${product_id}                               Post_valid_physical                     Post_body_request_for_physical_product
    log                                         ${Product_id}[0]
    patch_status                                ${Product_id}[3]                        Sold                            Sold
    patch_status                                ${Product_id}[2]                        Sold                            Sold
    patch_status                                ${Product_id}[1]                        Active                          Active
    patch_status                                ${Product_id}[0]                        Active                          Active
    patch_status                                ${Product_id}[1]                        Terminated                      Terminated
    patch_status                                ${Product_id}[0]                        Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                        Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]


Valid_Patch_with_Mixed Status Children

    [Tags]  Patch_Method
    [Documentation]
    #    IPCEISCPIB-486
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
    patch_status                        ${Product_id}[3]                    Sold                       Sold
    patch_status                        ${Product_id}[2]                    Sold                       Sold
    patch_status                        ${Spec_ProductOffering_id}          Cancelled                  Cancelled
    patch_status                        ${Atomic_ProductOffering_id}        Cancelled                  Cancelled
    patch_status                        ${Spec_ProductOffering_id2}         Active                     Active
    patch_status                        ${Atomic_ProductOffering_id2}       Active                     Active
    patch_status                        ${Spec_ProductOffering_id2}         Terminated                 Terminated
    patch_status                        ${Atomic_ProductOffering_id2}       Terminated                 Terminated
    patch_status                                ${Product_id}[1]                    Active                          Active
    patch_status                                ${Product_id}[0]                    Active                          Active
    patch_status                                ${Product_id}[1]                    Terminated                      Terminated
    patch_status                                ${Product_id}[0]                    Terminated                      Terminated
    valid_terminated_status                     ${product_id}[0]                    Terminated                      Terminated                ${Product_id}[1]
    log                                         ${Product_id}[0]

