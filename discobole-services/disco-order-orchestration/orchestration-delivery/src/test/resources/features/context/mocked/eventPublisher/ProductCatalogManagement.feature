Feature: [IPCEISCOOD-572] Update Product order by Retrieving delivery-focused characteristics

  Scenario: 1 Update Product order by Retrieving non repeating characteristics
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 1              |               |
      | Validity Of Rollovers   | 1              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec2"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 2              |               |
      | Validity Of Rollovers   | 2              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec3"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 3             |               |
      | Validity Of Rollovers   | 3             | month         |
      | Rollover usage priority | consumedAfter |               |
    And Product Catalog has the following characteristics for spec "spec4"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 4             |               |
      | Validity Of Rollovers   | 4             | month         |
      | Rollover usage priority | consumedAfter |               |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | name   | value |
      | volume | 1hr   |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | name  | value      |
      | ICCID | ICCIDValue |
    And  order item "orderItem3" with spec id "spec3" has the following characteristics
      | name   | value |
      | volume | 2hr   |
    And  order item "orderItem4" with spec id "spec4" has the following characteristics
      | name  | value      |
      | ICCID | ICCIDValue |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name                    | value          |
      | Number Of Rollovers     | 1              |
      | Validity Of Rollovers   | 1month         |
      | Rollover usage priority | consumedBefore |
      | volume                  | 1hr            |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name                    | value          |
      | ICCID                   | ICCIDValue     |
      | Number Of Rollovers     | 2              |
      | Validity Of Rollovers   | 2month         |
      | Rollover usage priority | consumedBefore |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem3"
      | name                    | value         |
      | volume                  | 2hr           |
      | Number Of Rollovers     | 3             |
      | Validity Of Rollovers   | 3month        |
      | Rollover usage priority | consumedAfter |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem4"
      | name                    | value         |
      | ICCID                   | ICCIDValue    |
      | Number Of Rollovers     | 4             |
      | Validity Of Rollovers   | 4month        |
      | Rollover usage priority | consumedAfter |


  Scenario: 2 repeated characteristics not added to product order
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 1              |               |
      | Validity Of Rollovers   | 1              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec2"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 2              |               |
      | Validity Of Rollovers   | 2              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec3"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 3             |               |
      | Validity Of Rollovers   | 3             | month         |
      | Rollover usage priority | consumedAfter |               |
    And Product Catalog has the following characteristics for spec "spec4"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 4             |               |
      | Validity Of Rollovers   | 4             | month         |
      | Rollover usage priority | consumedAfter |               |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | name                | value |
      | Number Of Rollovers | 1     |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | name                  | value  |
      | Validity Of Rollovers | 2month |
    And  order item "orderItem3" with spec id "spec3" has the following characteristics
      | name   | value |
      | volume | 2hr   |
    And  order item "orderItem4" with spec id "spec4" has the following characteristics
      | name  | value      |
      | ICCID | ICCIDValue |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name                    | value          |
      | Number Of Rollovers     | 1              |
      | Validity Of Rollovers   | 1month         |
      | Rollover usage priority | consumedBefore |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name                    | value          |
      | Number Of Rollovers     | 2              |
      | Validity Of Rollovers   | 2month         |
      | Rollover usage priority | consumedBefore |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem3"
      | name                    | value         |
      | volume                  | 2hr           |
      | Number Of Rollovers     | 3             |
      | Validity Of Rollovers   | 3month        |
      | Rollover usage priority | consumedAfter |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem4"
      | name                    | value         |
      | ICCID                   | ICCIDValue    |
      | Number Of Rollovers     | 4             |
      | Validity Of Rollovers   | 4month        |
      | Rollover usage priority | consumedAfter |

  Scenario: 3 repeated characteristics from catalog side with different value not added to product order characteristics
    Given There is no orchestration plan created
    And Product Catalog has the following characteristics for spec "spec1"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 1              |               |
      | Validity Of Rollovers   | 1              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec2"
      | name                    | value          | unitOfMeasure |
      | Number Of Rollovers     | 2              |               |
      | Validity Of Rollovers   | 2              | month         |
      | Rollover usage priority | consumedBefore |               |
    And Product Catalog has the following characteristics for spec "spec3"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 3             |               |
      | Validity Of Rollovers   | 3             | month         |
      | Rollover usage priority | consumedAfter |               |
    And Product Catalog has the following characteristics for spec "spec4"
      | name                    | value         | unitOfMeasure |
      | Number Of Rollovers     | 4             |               |
      | Validity Of Rollovers   | 4             | month         |
      | Rollover usage priority | consumedAfter |               |
    And OM has product order "order1" with status "accepted"
    And  order item "orderItem1" with spec id "spec1" has the following characteristics
      | name                | value |
      | Number Of Rollovers | 2     |
    And  order item "orderItem2" with spec id "spec2" has the following characteristics
      | name                  | value  |
      | Validity Of Rollovers | 4month |
    And  order item "orderItem3" with spec id "spec3" has the following characteristics
      | name   | value |
      | volume | 2hr   |
    And  order item "orderItem4" with spec id "spec4" has the following characteristics
      | name  | value      |
      | ICCID | ICCIDValue |
    When the system consumes product order "order1" from the topic "disco.order-management.productOrderStateChange-event"
    Then The system will create orchestration plan for order id "order1" with "Acknowledged" state
    And the plan with product order "order1" will have the following characteristics for order item "orderItem1"
      | name                    | value          |
      | Number Of Rollovers     | 2              |
      | Validity Of Rollovers   | 1month         |
      | Rollover usage priority | consumedBefore |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem2"
      | name                    | value          |
      | Number Of Rollovers     | 2              |
      | Validity Of Rollovers   | 4month         |
      | Rollover usage priority | consumedBefore |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem3"
      | name                    | value         |
      | volume                  | 2hr           |
      | Number Of Rollovers     | 3             |
      | Validity Of Rollovers   | 3month        |
      | Rollover usage priority | consumedAfter |
    And the plan with product order "order1" will have the following characteristics for order item "orderItem4"
      | name                    | value         |
      | ICCID                   | ICCIDValue    |
      | Number Of Rollovers     | 4             |
      | Validity Of Rollovers   | 4month        |
      | Rollover usage priority | consumedAfter |
