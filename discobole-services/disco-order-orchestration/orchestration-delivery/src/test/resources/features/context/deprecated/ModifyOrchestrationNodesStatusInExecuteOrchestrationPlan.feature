Feature: Modify Orchestration nodes status in execute orchestration plan

  @BeforeModifyOrchestrationNodesStatusInExecuteOrchestrationPlan
  Scenario: Node State is Held
    Given an Orchestration plan exists with 3 nodes
      | id |     state    | relatedNodes                                                |
      | A  | Acknowledged | [{"relatedNodeId":"C", "relationshipType": "DeliverAfter"}] |
      | B  | Acknowledged | [{"relatedNodeId":"C", "relationshipType": "DeliverAfter"}] |
      | C  | InProgress   | []                                                          |
    When Node "C" state is changed to "Held" and NodeStateChange event is consumed
    Then Node "A" and Node "B" state Not changed