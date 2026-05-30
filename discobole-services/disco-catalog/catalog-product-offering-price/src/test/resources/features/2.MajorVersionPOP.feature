Feature: The goal is to manage the major versioning of product offering price

  Scenario: Major version increment of "Product Offering Price" entities defined in catalog
    Given catalog admin is logged in
    Given catalog admin is in 'Modify Product Offering Price' flow
    Given Product Offering Price Type is selected
    Given Product Offering Price ID is selected
    Given version type 'Major' is selected
    When Product Offering Price is in lifecycle state "launched"
    Then user modifies editable attributes applicable for "Major" version
    Then user saves the changes for the "Product Offering Price ID"
    Then "Major" version is created for the Product Offering PriceID
    Then the previous version of the Product Offering Price ID is set to "unavailable"
    Then the "Major" version replaces the original "Product Offering Price ID" and all related entity associations are updated to the latest major version
    Then there shall be no impact to the CPIB for existing customers
