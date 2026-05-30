Feature: The goal is to manage the minor versioning of product offering price (applicable to both Product Offering Price Charge & Product Offering Price Alteration) 

  Scenario: Version specification of a newly created "Product Offering Price"
    Given catalog admin is logged in
    Given catalog admin creates a new "Product Offering Price"
    When Product Offering Price entity is validated and the lifecycle status changes to "inTest" automatically
    Then the version will automatically will be set to "0.1.0"

  Scenario: Minor version increment of "Product Offering Price" entities defined in catalog
    Given catalog admin is logged in
    Given catalog admin is in 'Modify Product Offering Price' flow 
    Given Product Offering Price Type is selected
    Given Product Offering Price ID is selected
    Given version type 'Minor' is selected
    When Product Offering Price is in lifecycle state "launched"
    Then user modifies editable attributes for "Minor" version
    Then user saves the changes for the "Product Offering Price ID"
    Then "Minor" version is created for the Product Offering ID and version is incremented by 0.1.0
    Then the previous version of the Product Offering Price ID is set to lifecycle status "unavailable"
    Then the "Minor" version replaces the original "Product Offering Price ID" and all related entity associations are updated to the latest minor version
    Then there shall be no impact to the CPIB for existing customers
