*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Invalid_Create_Product_with_AtomicPO
    Post_with_valid_hasParent       AtomicProductOffering

Invalid_Create_Product_with_BundlePO
    Post_with_valid_hasParent       BundleProductOffering

