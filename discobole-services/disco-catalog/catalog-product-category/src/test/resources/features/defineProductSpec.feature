Feature: Define a new product specification from a cfs spec when supportEntity = CFSSpec
  Update the values of product specification in restriction with CFSSpec.
  
  Scenario : process 'Description' for a new Product Specification using API TMF 701
    Given admin catalog is logged
    Given process for the creation of product Specification exists
    Given ProductSpecification ID is known
    Given process Description (not in  [CFS] ServiceSpecification) is in pending mode
    When Admin Catalog fills all fields needed in order to answer the needed elements
    Then body response contains all Description elements
    Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
    Then Process saved information in db using SID model thanks to the ProductSpecification ID
    Then Process needs from Front End to pick and select operation(s) for ProductSpecification
    Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process 'Operation' for a new ProductSpecification using API TMF 701
    Given admin catalog is logged
    Given process for the description of product Specification exists
    Given process Operation is in pending mode
    Then Admin Catalog selects available Operations for ProductSpecification from [CFS] ServiceSpecification/OperationSpecification
    Then body response contains all Selected Operations elements
    Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
    Then Process saved information in db using SID model thanks to the ProductSpecification ID
    Then Process needs from Front End to select Characteristic(s) for ProductSpecification
    Then Process needs from Front End the value(s) for each selected Characteristic for ProductSpecification
    Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process define 'Characteristic' for a new ProductSpecification using API TMF 701
    Given admin catalog is logged
	Given process for the Operation of ProductSpecification exists
	Given process definition Characteristic is in pending mode
	Then Admin Catalog selects available Characteristic+s+ for ProductSpecification from [CFS] ServiceSpecification/CharacteristicSpecification
	And Admin Catalog selects available Characteristic+s+ Values of each CharacteristicSpecification for ProductSpecification from [CFS] ServiceSpecification/CharacteristicSpecification/CharacteristicValueSpecification
	Then body response contains all Selected Characteristic+s+ elements and Values
	Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
	Then Process saved information in db using SID model thanks to the ProductSpecification ID 
	Then Process needs from Front End to manage Characteristic(s) Relationships for ProductSpecification
	Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process definition 'Characteristics Relationships' for a new ProductSpecification using API TMF 701
	Given admin catalog is logged
	Given process Characteristic of ProductSpecification exists
	Given process definition CharacteristicsRelationships is in pending mode
	Then Process saved information in db using SID model thanks to the ProductSpecification ID
	Then Process needs from Front End to manage UsageSpecification for ProductSpecification
	Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process pick 'UsageSpecification' for a new ProductSpecification using API TMF 701
    Given admin catalog is logged
	Given process Characteristics Relationships of ProductSpecification exists
	Given process pick UsageSpecification+s+  is in pending mode
	Then Admin Catalog selects UsageSpecification+s+ for Product Specifications from [CFS] ServiceSpecification/UsageSpecification
	Then body response contains all UsageSpecifications elements
	Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
	Then Process saved information in db using SID model thanks to the ProductSpecification ID
	Then Process needs from Front End to define ProductSpecRelationships from the CFS
	Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process define 'ProductSpecRelationships' for a new Product Specification using API TMF 701
	Given admin catalog is logged
	Given process UsageSpecification of ProductSpecification exists
	Given process definition ProductSpecRelationships is in pending mode
	Then Admin Catalog defines ProductSpecRelationships for ProductSpecification from [CFS] ServiceSpecification/ServiceSpecRelationship
	Then body response contains ProductSpecRelationships elements
	Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
	Then Process saved information in db using SID model thanks to the ProductSpecification ID
	Then Process needs from Front End to describe Party and RelatedResource
	Then GUI receives a response from Back End which contain the needed elements from Process / Back End
 
  Scenario : process define 'Party' and 'RelatedResource' for a new Product Specification using API TMF 701
 	Given admin catalog is logged
	Given process 'Product Spec Relationships' exists
	Given process definition Party and RelatedResource is in pending mode
	Then Party and RelatedResource are filled using [CFS] ServiceSpecification/Party and RelatedResource
	Then Admin Catalog can amends / complete 'Party' and 'RelatedResource' for ProductSpecifications
	Then body response contains Party and RelatedResource elements
	Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
	Then Process saved information in db using SID model thanks to the ProductSpecification ID
	Then Process needs from Front End to submit the product for validation
	Then GUI receives a response from Back End which contain the needed elements from Process / Back End
  
  Scenario : process define validFor for a new ProductSpecification using API TMF 701
	Given admin catalog is logged
	Given process Party and RelatedResource of ProductSpecification exists
	Given process definition validFor is in pending mode
	Then GUI is able to check the validFor limitations from [CFS] ServiceSpecification value of ValidFor
	Then Admin Catalog define validFor for ProductSpecification respecting the validFor limitations
	Then body response contains validFor elements which are respecting the CFS validFor limitation
	Then GUI sends to process using URL (specific) , method (patch) and body to the Process back-end
	Then Process validates the ValidFor limitation thanks to the CFS validFor information
	Then Process saved information in db using SID model thanks to the ProductSpecification ID
	Then GUI receives a response from Back End which contain the completed creation for ProductSpecification from Process / Back End
	Then ProductSpecification version is generated
	Then Process sends an event to the event BUS containing the new ProductSpecification information
	Then Process Creation of new ProductSpecification is ended 