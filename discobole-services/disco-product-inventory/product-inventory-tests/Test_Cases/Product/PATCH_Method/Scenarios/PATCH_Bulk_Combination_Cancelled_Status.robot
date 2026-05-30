*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Valid_Patch_with_Cancelled_operationalStatus
    Get_token
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Cancelled        Cancelled

Valid_Patch_with_Confirmed/Cancelled_operationalStatus
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Cancelled        Cancelled

Valid_Patch_with_PendingCancel/Cancelled_operationalStatus
    ${valid_id}=        Post_valid          Post_body_request_for_Patch
    patch_status        ${valid_id}         Created          Confirmed
    patch_status        ${valid_id}         Created          PendingActive
    patch_status        ${valid_id}         Created          PendingCancel
    patch_status        ${valid_id}         Cancelled        Cancelled

Valid_Patch_with_All Children Terminated
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Terminated.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    Get_token
    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}    ${Spec_ProductOffering_id2}        Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                    ${Product_id}[3]                    Sold                            Sold
    patch_status                    ${Product_id}[2]                    Sold                            Sold
    patch_status                    ${Spec_ProductOffering_id}          Active                          Active
    patch_status                    ${Atomic_ProductOffering_id}        Active                          Active
    patch_status                    ${Spec_ProductOffering_id2}         Active                          Active
    patch_status                    ${Atomic_ProductOffering_id2}       Active                          Active
    patch_status                    ${Spec_ProductOffering_id}          Terminated                      Terminated
    patch_status                    ${Atomic_ProductOffering_id}        Terminated                      Terminated
    patch_status                    ${Spec_ProductOffering_id2}         Terminated                      Terminated
    patch_status                    ${Atomic_ProductOffering_id2}       Terminated                      Terminated
    patch_status                    ${Product_id}[1]                    Cancelled                      Cancelled
    valid_terminated_status         ${product_id}[0]                    Cancelled                      Cancelled                ${Product_id}[1]
    log                             ${Product_id}[0]

Valid_Patch_with_All Children Cancelled
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Cancelled.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}    ${Spec_ProductOffering_id2}        Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                    ${Product_id}[3]                    Sold                            Sold
    patch_status                    ${Product_id}[2]                    Sold                            Sold
    patch_status                    ${Spec_ProductOffering_id}          Cancelled                      Cancelled
    patch_status                    ${Atomic_ProductOffering_id}        Cancelled                      Cancelled
    patch_status                    ${Spec_ProductOffering_id2}         Cancelled                      Cancelled
    patch_status                    ${Atomic_ProductOffering_id2}       Cancelled                      Cancelled
    patch_status                    ${Product_id}[1]                    Cancelled                      Cancelled
    valid_terminated_status         ${product_id}[0]                    Cancelled                      Cancelled                ${Product_id}[1]
    log                             ${Product_id}[0]

Valid_Patch_with_All Children Aborted
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    Valid Transition to Terminated:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Aborted.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Terminated.

    ${product_id}                   Post_valid_physical          Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}    ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}    ${Spec_ProductOffering_id2}        Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom      ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom      ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                        ${Product_id}[3]                    Sold                            Sold
    patch_status                        ${Product_id}[2]                    Sold                            Sold
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
    patch_status                        ${Product_id}[1]                        Cancelled                          Cancelled
    valid_terminated_status             ${product_id}[0]                        Cancelled                          Cancelled                ${Product_id}[1]
    log                                 ${Product_id}[0]

Valid_Patch_with_Mixed Status Children
    [Tags]  IPCEISCPIB-486
    [Documentation]

    #    - **Precondition:** Child products have a mix  status (Terminated,Cancelled) .
    #    - **Action:** Attempt to update the parent product to Terminated.
    #    - **Expected Result:** Update fails; error message indicating that not all child products meet the status requirement.

    ${product_id}                           Post_valid_physical                     Post_body_request_for_physical_product
    ${Atomic_ProductOffering_id}            ${Spec_ProductOffering_id}              Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}           ${Spec_ProductOffering_id2}             Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    patch_with_reliesOn_reliesFrom          ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
    patch_with_reliesOn_reliesFrom          ${Product_id}[3]                    ${Spec_ProductOffering_id}
    patch_status                            ${Product_id}[3]                        Sold                            Sold
    patch_status                            ${Product_id}[2]                        Sold                            Sold
    patch_status                            ${Spec_ProductOffering_id}              Cancelled                       Cancelled
    patch_status                            ${Atomic_ProductOffering_id}            Cancelled                       Cancelled
    patch_status                            ${Spec_ProductOffering_id2}             Active                          Active
    patch_status                    ${Atomic_ProductOffering_id2}           Active                          Active
    patch_status                    ${Spec_ProductOffering_id2}             Terminated                      Terminated
    patch_status                    ${Atomic_ProductOffering_id2}           Terminated                      Terminated
    patch_status                    ${Product_id}[1]                        Cancelled                       Cancelled
    patch_status                    ${Product_id}[0]                        Cancelled                       Cancelled
    valid_terminated_status         ${product_id}[0]                        Cancelled                       Cancelled                ${Product_id}[1]
    log                             ${Product_id}[0]

Valid_Patch_with_All Children Sold
    [Tags]  IPCEISCPIB-486
    [Documentation]
    #    Valid Transition to Cancelled:
    #    --------------------------------------------------------------------------------------------------
    #    Precondition: All child products are in the status: Sold.
    #    Action: Update the parent product to Terminated.
    #    Expected Result: Parent product status is successfully updated to Cancelled.

    ${product_id}                               Post_valid_physical                     Post_body_request_for_physical_product
    log                                         ${Product_id}[0]
    patch_status                                ${Product_id}[3]                        Sold                            Sold
    patch_status                                ${Product_id}[2]                        Sold                            Sold
    patch_status                                ${Product_id}[1]                        Cancelled                       Cancelled
    patch_status                                ${Product_id}[0]                        Cancelled                       Cancelled
    valid_aborted_status                        ${product_id}[0]                        Cancelled                       Cancelled                ${Product_id}[1]
    log                                         ${Product_id}[0]
