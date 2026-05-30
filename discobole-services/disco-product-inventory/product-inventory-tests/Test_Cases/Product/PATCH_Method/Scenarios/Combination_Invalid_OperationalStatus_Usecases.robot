*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_with_nonExistent_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              nonExistent             One or multiple issues are exist in the path or the value fields

Invalid_Patch_with_Empty_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              ${EMPTY}                One or multiple issues are exist in the path or the value fields

Invalid_Patch_with_Nullable_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              ${NULL}                 patchProducts.productPatch[0].value must not be null

# Created Status

Invalid_Patch_Created/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              PendingActive           The operational status Created cannot be modified into PendingActive

Invalid_Patch_Created/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              PendingCancel           The operational status Created cannot be modified into PendingCancel

Invalid_Patch_Created/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              Locked                  The operational status Created cannot be modified into Locked

Invalid_Patch_Created/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              Active                 The operational status Created cannot be modified into Active

Invalid_Patch_Created/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              PendingModification    The operational status Created cannot be modified into PendingModification

Invalid_Patch_Created/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              PendingTerminate       The operational status Created cannot be modified into PendingTerminate

Invalid_Patch_Created/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus              Terminated             The operational status Created cannot be modified into Terminated

# Confirmed Status

Invalid_Patch_Confirmed/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_invalid_body      ${valid_id}         operationalStatus              Created                The operational status Confirmed cannot be modified into Created

Invalid_Patch_Confirmed/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_invalid_body      ${valid_id}         operationalStatus              PendingCancel           The operational status Confirmed cannot be modified into PendingCancel

Valid_Patch_Confirmed/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        Locked


Valid_Patch_Confirmed/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Aborted                        Aborted


Invalid_Patch_Confirmed/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_invalid_body      ${valid_id}         operationalStatus              PendingModification    The operational status Confirmed cannot be modified into PendingModification

Invalid_Patch_Confirmed/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_invalid_body      ${valid_id}         operationalStatus              PendingTerminate       The operational status Confirmed cannot be modified into PendingTerminate

Invalid_Patch_Confirmed/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_invalid_body      ${valid_id}         operationalStatus              Terminated             The operational status Confirmed cannot be modified into Terminated

#  Cancelled Status

Invalid_Patch_Cancelled/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Created                 The operational status Cancelled cannot be modified into Created

Invalid_Patch_Cancelled/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Confirmed                 The operational status Cancelled cannot be modified into Confirmed

Invalid_Patch_Cancelled/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              PendingActive           The operational status Cancelled cannot be modified into PendingActive

Invalid_Patch_Cancelled/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              PendingCancel           The operational status Cancelled cannot be modified into PendingCancel

Invalid_Patch_Cancelled/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Locked                  The operational status Cancelled cannot be modified into Locked

Invalid_Patch_Cancelled/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Aborted                 The operational status Cancelled cannot be modified into Aborted

Invalid_Patch_Cancelled/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Active                   The operational status Cancelled cannot be modified into Active

Invalid_Patch_Cancelled/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              PendingModification    The operational status Cancelled cannot be modified into PendingModification

Invalid_Patch_Cancelled/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              PendingTerminate       The operational status Cancelled cannot be modified into PendingTerminate

Invalid_Patch_Cancelled/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled                      Cancelled
    patch_invalid_body      ${valid_id}         operationalStatus              Terminated             The operational status Cancelled cannot be modified into Terminated

#  PendingActive Status

Invalid_Patch_PendingActive/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              Created                 The operational status PendingActive cannot be modified into Created

Invalid_Patch_PendingActive/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              Confirmed                 The operational status PendingActive cannot be modified into Confirmed

Invalid_Patch_PendingActive/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              Cancelled                 The operational status PendingActive cannot be modified into Cancelled

Invalid_Patch_PendingActive/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              PendingModification    The operational status PendingActive cannot be modified into PendingModification

Invalid_Patch_PendingActive/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              PendingTerminate       The operational status PendingActive cannot be modified into PendingTerminate

Invalid_Patch_PendingActive/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                        Confirmed
    patch_status            ${valid_id}         Created                        PendingActive
    patch_invalid_body      ${valid_id}         operationalStatus              Terminated             The operational status PendingActive cannot be modified into Terminated

# PendingCancel Status

Invalid_Patch_PendingCancel/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Created                 The operational status PendingCancel cannot be modified into Created

Invalid_Patch_PendingCancel/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Confirmed                 The operational status PendingCancel cannot be modified into Confirmed

Invalid_Patch_PendingCancel/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             PendingActive           The operational status PendingCancel cannot be modified into PendingActive

Invalid_Patch_PendingCancel/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Locked                  The operational status PendingCancel cannot be modified into Locked

Invalid_Patch_PendingCancel/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Aborted                 The operational status PendingCancel cannot be modified into Aborted

Invalid_Patch_PendingCancel/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Active                   The operational status PendingCancel cannot be modified into Active

Invalid_Patch_PendingCancel/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             PendingModification    The operational status PendingCancel cannot be modified into PendingModification

Invalid_Patch_PendingCancel/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             PendingTerminate       The operational status PendingCancel cannot be modified into PendingTerminate

Invalid_Patch_PendingCancel/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       PendingCancel
    patch_invalid_body      ${valid_id}         operationalStatus             Terminated             The operational status PendingCancel cannot be modified into Terminated

# Locked Status

Invalid_Patch_Locked/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             Created                 The operational status Locked cannot be modified into Created

Invalid_Patch_Locked/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             Confirmed                 The operational status Locked cannot be modified into Confirmed

Invalid_Patch_Locked/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             Cancelled                 The operational status Locked cannot be modified into Cancelled

Invalid_Patch_Locked/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             PendingCancel           The operational status Locked cannot be modified into PendingCancel

Valid_Patch_Locked/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_status            ${valid_id}         Active                       Active

Invalid_Patch_Locked/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             PendingModification    The operational status Locked cannot be modified into PendingModification

Invalid_Patch_Locked/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             PendingTerminate       The operational status Locked cannot be modified into PendingTerminate

Invalid_Patch_Locked/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                       Confirmed
    patch_status            ${valid_id}         Created                       PendingActive
    patch_status            ${valid_id}         Created                       Locked
    patch_invalid_body      ${valid_id}         operationalStatus             Terminated             The operational status Locked cannot be modified into Terminated

#  Aborted Status

Invalid_Patch_Aborted/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Created                 The operational status Aborted cannot be modified into Created

Invalid_Patch_Aborted/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Confirmed                 The operational status Aborted cannot be modified into Confirmed

Invalid_Patch_Aborted/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Cancelled                 The operational status Aborted cannot be modified into Cancelled

Invalid_Patch_Aborted/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              PendingActive           The operational status Aborted cannot be modified into PendingActive

Invalid_Patch_Aborted/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              PendingCancel           The operational status Aborted cannot be modified into PendingCancel

Invalid_Patch_Aborted/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Locked                  The operational status Aborted cannot be modified into Locked

Invalid_Patch_Aborted/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Active                   The operational status Aborted cannot be modified into Active

Invalid_Patch_Aborted/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              PendingModification    The operational status Aborted cannot be modified into PendingModification

Invalid_Patch_Aborted/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              PendingTerminate       The operational status Aborted cannot be modified into PendingTerminate

Invalid_Patch_Aborted/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Aborted                      Aborted
    patch_invalid_body      ${valid_id}         operationalStatus              Terminated             The operational status Aborted cannot be modified into Terminated

#  Active Status

Invalid_Patch_Active/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               Created                 The operational status Active cannot be modified into Created

Invalid_Patch_Active/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               Confirmed                 The operational status Active cannot be modified into Confirmed

Invalid_Patch_Active/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               Cancelled                 The operational status Active cannot be modified into Cancelled

Invalid_Patch_Active/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               PendingActive           The operational status Active cannot be modified into PendingActive

Invalid_Patch_Active/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               PendingCancel           The operational status Active cannot be modified into PendingCancel

Invalid_Patch_Active/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               Locked                  The operational status Active cannot be modified into Locked


Invalid_Patch_Active/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_invalid_body      ${valid_id}         operationalStatus               Aborted                   The operational status Active cannot be modified into Aborted

#  PendingModification Status

Invalid_Patch_PendingModification/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               Created                 The operational status PendingModification cannot be modified into Created

Invalid_Patch_PendingModification/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               Confirmed                 The operational status PendingModification cannot be modified into Confirmed

Invalid_Patch_PendingModification/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               Cancelled                 The operational status PendingModification cannot be modified into Cancelled

Invalid_Patch_PendingModification/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               PendingActive           The operational status PendingModification cannot be modified into PendingActive

Invalid_Patch_PendingModification/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               PendingCancel           The operational status PendingModification cannot be modified into PendingCancel

Invalid_Patch_PendingModification/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               Locked                  The operational status PendingModification cannot be modified into Locked


Invalid_Patch_PendingModification/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created          Confirmed
    patch_status            ${valid_id}         Active           Active
    patch_status            ${valid_id}         Active           PendingModification
    patch_invalid_body      ${valid_id}         operationalStatus               Aborted                   The operational status PendingModification cannot be modified into Aborted

#  PendingTerminate Status

Invalid_Patch_PendingTerminate/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Created                 The operational status PendingTerminate cannot be modified into Created

Invalid_Patch_PendingTerminate/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Confirmed                 The operational status PendingTerminate cannot be modified into Confirmed

Invalid_Patch_PendingTerminate/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Cancelled                 The operational status PendingTerminate cannot be modified into Cancelled

Invalid_Patch_PendingTerminate/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               PendingActive           The operational status PendingTerminate cannot be modified into PendingActive

Invalid_Patch_PendingTerminate/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               PendingCancel           The operational status PendingTerminate cannot be modified into PendingCancel

Invalid_Patch_PendingTerminate/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Locked                  The operational status PendingTerminate cannot be modified into Locked

Invalid_Patch_PendingTerminate/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Aborted                   The operational status PendingTerminate cannot be modified into Aborted

Invalid_Patch_PendingTerminate/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               PendingModification             The operational status PendingTerminate cannot be modified into PendingModification

Invalid_Patch_PendingTerminate/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Active                          PendingTerminate
    patch_invalid_body      ${valid_id}         operationalStatus               Active             The operational status PendingTerminate cannot be modified into Active

#  Terminated Status

Invalid_Patch_Terminated/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Created                 The operational status Terminated cannot be modified into Created

Invalid_Patch_Terminated/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Confirmed                 The operational status Terminated cannot be modified into Confirmed

Invalid_Patch_Terminated/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Cancelled                 The operational status Terminated cannot be modified into Cancelled

Invalid_Patch_Terminated/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               PendingActive           The operational status Terminated cannot be modified into PendingActive

Invalid_Patch_Terminated/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               PendingCancel           The operational status Terminated cannot be modified into PendingCancel

Invalid_Patch_Terminated/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Locked                  The operational status Terminated cannot be modified into Locked

Invalid_Patch_Terminated/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Aborted                   The operational status Terminated cannot be modified into Aborted

Invalid_Patch_Terminated/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               Active                   The operational status Terminated cannot be modified into Active

Invalid_Patch_Terminated/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               PendingModification             The operational status Terminated cannot be modified into PendingModification

Invalid_Patch_Terminated/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created                         Confirmed
    patch_status            ${valid_id}         Active                          Active
    patch_status            ${valid_id}         Terminated                      Terminated
    patch_invalid_body      ${valid_id}         operationalStatus               PendingTerminate             The operational status Terminated cannot be modified into PendingTerminate

#  Sold Status

Invalid_Patch_Sold_operationalStatus_for_Product
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         operationalStatus               Sold                 	The operational status Created cannot be modified into Sold

Invalid_Patch_Sold/Created_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Created                 The operational status Sold cannot be modified into Created

Invalid_Patch_Sold/Confirmed_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Confirmed                 The operational status Sold cannot be modified into Confirmed

Invalid_Patch_Sold/Cancelled_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Cancelled                 The operational status Sold cannot be modified into Cancelled

Invalid_Patch_Sold/PendingActive_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               PendingActive           The operational status Sold cannot be modified into PendingActive

Invalid_Patch_Sold/PendingCancel_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               PendingCancel           The operational status Sold cannot be modified into PendingCancel

Invalid_Patch_Sold/Locked_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Locked                  The operational status Sold cannot be modified into Locked

Invalid_Patch_Sold/Aborted_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Aborted                   The operational status Sold cannot be modified into Aborted

Invalid_Patch_Sold/Active_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Active                   The operational status Sold cannot be modified into Active

Invalid_Patch_Sold/PendingModification_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               PendingModification             The operational status Sold cannot be modified into PendingModification

Invalid_Patch_Sold/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               PendingTerminate             The operational status Sold cannot be modified into PendingTerminate

Invalid_Patch_Sold/PendingTerminate_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               PendingTerminate             The operational status Sold cannot be modified into PendingTerminate

Invalid_Patch_Sold/Terminated_operationalStatus
    Get_token
    ${valid_id}             Post_valid_physical                                 Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                            Sold
    patch_invalid_body      ${valid_id}[3]      operationalStatus               Terminated             The operational status Sold cannot be modified into Terminated
