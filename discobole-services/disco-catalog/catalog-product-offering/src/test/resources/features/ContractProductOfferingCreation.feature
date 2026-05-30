Feature: The goal is to manage the creation of contract product offering

  Scenario: Creation of "Contract Product Offering" entities defined in catalog
    Given catalog admin is logged in
    Given catalog admin starts the 'ProductOfferingCreation' flow  
    When Product Offering Creation will start with "ProductOfferingCreation"
    Then Response should be 201

	Scenario: Select the offering type as "Contract"
    Given catalog admin is at offering type step
    When admin select the offering type as "Contract"
    Then Response should be 200
    Then Contract Product Offering ID is generated 
    Then lifecycle is equal to "inStudy"
    Then Next Task to be performed list should be populated
    
	Scenario: catalog admin is at DefineProductOfferingIdentityData step
    Given catalog admin is at DefineProductOfferingIdentityData step
    When admin enters mandatory fields ProductOfferingIdentityData
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: catalog admin is at DefineProductOfferingCategory step
    Given catalog admin is at DefineProductOfferingCategory step
    When admin enters mandatory fields productCategoryId
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: Catalog Admin is at DefineProductOfferingSaleChannel step
    Given catalog admin is at DefineProductOfferingSaleChannel step
    When admin enters mandatory fields channelId
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: Catalog Admin is at DefineProductOfferingMarketSegment step
    Given catalog admin is at DefineProductOfferingMarketSegment step
    When admin enters mandatory fields for MarketSegment
    Then Response should be 200
    Then Next Task to be performed list should be populated

    Scenario: Catalog Admin is at SelectRelatedParty step
    Given catalog admin is at SelectRelatedParty step
    When admin enters mandatory fields for RelatedParty
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: Catalog Admin is at DefineBundledOperationSpecification step
    Given catalog admin is at DefineBundledOperationSpecification step
    When admin enters mandatory fields for BundledOperationSpecification
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: Catalog Admin is at AssociatePOPtoOperationSpecification step
    Given catalog admin is at AssociatePOPtoOperationSpecification step
    When admin enters mandatory fields for POPtoOperationSpecification
    Then Response should be 200
    Then Next Task to be performed list should be populated
    
	Scenario: Catalog Admin is at ManageProductOfferingTerm step
    Given catalog admin is at ManageProductOfferingTerm step
    When admin enters mandatory fields for ProductOfferingTerm
    Then Response should be 200
    Then Next Task to be performed list should be populated

	Scenario: Catalog Admin is at ManageProductOfferingBundling step
    Given catalog admin is at ManageProductOfferingBundling step
    When admin enters mandatory fields for ProductOfferingBundling 
    Then Response should be 200
    Then Next Task to be performed list should be populated


	Scenario: Catalog Admin is at DefineRelationship step
    Given catalog admin is at DefineRelationship step
    When admin enters mandatory fields for Relationship
    Then Response should be 200
    Then Next Task to be performed list should be populated



	Scenario: Catalog Admin is at validity step
    Given catalog admin is at validity step
    When admin enters mandatory fields for validity
    Then Response should be 200
    Then Next Task to be performed list should be populated
    
	Scenario: Catalog admin is at validate step
    Given catalog admin is at validate step
    When admin enters mandatory fields for validate
    Then Response should be 200
    Then nexttasktobeperformed list should be empty
    Then lifecycle status should changed to "InTest"
    