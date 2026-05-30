// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productinventory.dto.v1.ExternalIdentifier;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@CompoundIndex(name = "active_termination_date_index", def = "{'status': 1, 'terminationDate': 1}", partialFilter = "{ 'status': 'ACTIVE', 'terminationDate': { '$exists': true } }")
@CompoundIndex(name = "product_characteristic_index", def = "{'productCharacteristic.name': 1, 'productCharacteristic.value': 1}", partialFilter = "{'productCharacteristic.valueType':'string'}")
@CompoundIndex(name = "product_relationship_product_id_index", def = "{'productRelationship.product._id': 1}")
@CompoundIndex(name = "relatedParty_productOffering_contract_index", def = "{'relatedParty._id': 1, 'productOffering.atType': 1}", partialFilter = "{'productOffering.atType': 'Contract'}")
@CompoundIndex(name = "external_identifier_id_index", def = "{'externalIdentifier._id': 1}")
@CompoundIndex(name = "is_root_product_start_date", def = "{ 'isRootProduct': 1, 'startDate': -1 }", partialFilter = "{ 'isRootProduct': true }")
@Document(collection = "products")
public class ProductEntity {
    //TODO verify its length should be <= 32 (default 24, verify from PO if this is ok)
    private String id;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    //TODO href musn't be presisted in db
    // should be autocalculated, same for any href
    private String href;
    //TODO add validation in dto and entity length: 1..4000 , notnull
    // same for any desc
    private String description;
    //TODO if true ProductBundle
    // if false ProductComponent
    // if null retrieve from the catalog to be used
    private Boolean isBundle;
    private Boolean isCustomerVisible;
    //TODO add validation in dto and entity length: 1..256 , notnull
    // same for any name
    @Indexed
    private String name;
    private OffsetDateTime orderDate;
    //TODO add max size 64 character
    private String productSerialNumber;
    @Indexed
    private OffsetDateTime startDate;
    private OffsetDateTime terminationDate;
    private List<AgreementItemRefEntity> agreement;
    private BillingAccountRefEntity billingAccount;
    private List<RelatedPlaceRefOrValueEntity> place;
    private List<CharacteristicEntity> productCharacteristic;
    private ProductOfferingRefEntity productOffering;
    private List<RelatedProductOrderItemEntity> productOrderItem;
    private List<ProductPriceEntity> productPrice;
    private List<ProductRelationshipEntity> productRelationship;
    private ProductSpecificationRefEntity productSpecification;
    private List<ProductTermEntity> productTerm;
    private List<ResourceRefEntity> realizingResource;
    private List<ServiceRefEntity> realizingService;
    private List<RelatedPartyEntity> relatedParty;
    private List<OperationalStatusChangeEntity> operationalStatusChange;
    private List<StatusChangeEntity> statusChange;
    @Indexed
    private ProductStatusType status;
    @Indexed
    private ProductOperationalStatusType operationalStatus;
    @Indexed
    private OffsetDateTime creationDate;
    @LastModifiedDate
    private OffsetDateTime lastUpdateDate;
    private Boolean executeTerminationProcess;
    private List<ExternalIdentifier> externalIdentifier;
    private Boolean isRootProduct;

    public void setProductRelationship(List<ProductRelationshipEntity> productRelationship) {
        this.productRelationship = productRelationship;
        if (productRelationship != null && !productRelationship.isEmpty()) {
            this.isRootProduct = productRelationship.stream().noneMatch(relationship -> ProductRelationshipType.ROOTPRODUCT.getValue().equals(relationship.getRelationshipType()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductEntity that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

