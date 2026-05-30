Feature: Close Fallout Incident As Resolved
  Scenario: Closing Fallout Incident With Status Held as Resolved
    Given a fallout incident with status Held
    And the related orchestration node status is also "Held"
    And the logged in user has the Fallout_Manager role
    When the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body "update/processFlowPatchRequest.json"
    Then after the fallout incident is closed, its status should be set to Completed

  Scenario: Unexpected Error When Closing Fallout Incident
    Given a fallout incident with status Held
    And the related orchestration node status is also "Held"
    And the logged in user has the Fallout_Manager role
    When the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body "update/processFlowPatchRequestInvalid.json"
    And a generic error occurs during the closure process
    Then the system should display a generic error message indicating the issue for resolved incident
    And the fallout incident status should remain unchanged

  Scenario: Closing Fallout Incident Related to Node With Status Other Than Held as Unresolved
    Given a fallout incident with status "Held"
    And the logged in user has the Fallout_Manager role
    When the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body "update/processFlowPatchRequestInvalid.json"
    And an error occurs during the closure process
    Then the system should send an error message indicating that the incident cant be closed because its related nodes current status is different than Held
    And the fallout incident status should remain unchanged
    And the orchestration node status should remain unchanged

  Scenario: Closing Fallout Incident Without Reason
    Given a fallout incident with status Held
    And the related orchestration node status is also "Held"
    And the logged in user has the Fallout_Manager role
    And the user set the resolution state to "resolved"
    And they didn't set a reason for closing the fallout incident
    When the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body "update/processFlowPatchRequestWithoutReason.json"
    Then the system should prompt the user to set a reason for closing the fallout incident as resolved
    And the fallout incident status should remain unchanged

  Scenario: Closing Fallout Incident With Reason Exceeding Max Allowed Chars
    Given a fallout incident with status Held
    And the related orchestration node status is also "Held"
    And the logged in user has the Fallout_Manager role
    And the user set the resolution state to "resolved"
    And they didn't set a reason for closing the fallout incident
    When the COOD administrator chooses to close the fallout incident with Resolved and reason patch json body "update/processFlowPatchRequestReasonMaxExceed.json"
    Then the system should prompt the user to set a reason for closing the fallout incident as resolved
    And the fallout incident status should remain unchanged
