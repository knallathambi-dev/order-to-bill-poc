Feature: Verify Dependents for Delete Action
  Background:
    Given an OrchestrationPlan '{"relatedProductOrder": {"id":"PR0D3CT"}, "orchestrationPlanNodes": [{"id": "0rc34t31","relatedProductOrder": {"id":"PR0D3CT"}, "state": "InProgress", "relatedServiceOrder": {"id": "id"}, "relatedProduct": [{"id": "PR0D3CT", "relationshipType": "delivers", "@type":"CFS", "productSpecification": {"name":"spec","serviceSpecification":[]}, "isInstallable": true, "productOrderItemId":"ord3r" }], "relatedProductOrderItem": [{"id":"ord3r", "action": "delete", "quantity": 1}] }] }'
    And a product from product inventory '{ "@type": "Product","id": "PR0D3CT", "productRelationship": [{"product":{"@type": "ProductRef", "id": "D4T4BUNDL3"}, "relationshipType": "reliesFrom"}],"productOrderItem": [{"orderItemAction": "delete","orderItemId": "ord3r","productOrderId": "PR0D3CT"}]}'


  @VerifyDependentsForDeleteAction
  Scenario: 1 – delete with dependents count = 1 & its state is Terminated
    Given the previous prerequisites
    And CPIB has product with id = "D4T4BUNDL3" and state = "Terminated"
    When COOD delivers the node with id="0rc34t31"
    Then a service order request is created to deliver this node with id="0rc34t31"
    And a relatedProduct with relationship type "reliesFrom" is added with relatedProduct.id = "D4T4BUNDL3" to the node with id="0rc34t31"

  @VerifyDependentsForDeleteAction
  Scenario: 2 – delete with dependents count = 1 & its state is Aborted
    Given the previous prerequisites
    And CPIB has product with id = "D4T4BUNDL3" and state = "Aborted"
    When COOD delivers the node with id="0rc34t31"
    Then a service order request is created to deliver this node with id="0rc34t31"
    And a relatedProduct with relationship type "reliesFrom" is added with relatedProduct.id = "D4T4BUNDL3" to the node with id="0rc34t31"

  @VerifyDependentsForDeleteAction
  Scenario: 3 – delete with dependents count = 1 & its state is Cancelled
    Given the previous prerequisites
    And CPIB has product with id = "D4T4BUNDL3" and state = "Cancelled"
    When COOD delivers the node with id="0rc34t31"
    Then a service order request is created to deliver this node with id="0rc34t31"
    And a relatedProduct with relationship type "reliesFrom" is added with relatedProduct.id = "D4T4BUNDL3" to the node with id="0rc34t31"


  @VerifyDependentsForDeleteAction
  Scenario: 6 – delete with dependents count = 0
    Given a product from product inventory '{ "@type": "Product","id": "PR0D3CT", "productRelationship": [],"productOrderItem": [{"orderItemAction": "delete","orderItemId": "ord3r","productOrderId": "PR0D3CT"}]}'
    And CPIB has no related products for this node with id="0rc34t31"
    When COOD delivers the node with id="0rc34t31"
    Then a service order request is created to deliver this node with id="0rc34t31"

  @VerifyDependentsForDeleteAction
  Scenario: 7 – delete with dependents count = 0 but the product itself reliesOn on another one
    Given a product from product inventory '{ "@type": "Product","id": "PR0D3CT", "productRelationship": [{"product":{"@type": "ProductRef", "id": "D4T4BUNDL3"}, "relationshipType": "reliesOn"}],"productOrderItem": [{"orderItemAction": "delete","orderItemId": "ord3r","productOrderId": "PR0D3CT"}]}'
    And CPIB has product with id = "D4T4BUNDL3" and state = "Active"
    When COOD delivers the node with id="0rc34t31"
    Then a service order request is created to deliver this node with id="0rc34t31"