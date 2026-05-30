*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Contract_Product_without_Name
    Post_valid_without_name                   Contract                        ${Mobile_Package__productOffering_name}

Create_BundlePO_Product_without_Name
    Post_valid_without_name                   BundleProductOffering          ${Mobile_Package__productOffering_name}

Create_AtomicPO_Product_without_Name
    Post_valid_without_name                   AtomicProductOffering           ${Mobile_Line_productSpecification_name}

Create_PS_Product_without_Name
    Post_valid_without_name                   ProductSpecification            ${Mobile_Line_productSpecification_name}

Create_Contract_Product_with_Name
    Post_valid_with_name                      Contract                        My Contract Product

Create_BundlePO_Product_with_Name
    Post_valid_with_name                      BundleProductOffering          My BundleProductOffering Product

Create_AtomicPO_Product_with_Name
    Post_valid_with_name                      AtomicProductOffering           My AtomicProductOffering Product

Create_PS_Product_with_Name
    Post_valid_with_name                      ProductSpecification            My ProductSpecification Product
