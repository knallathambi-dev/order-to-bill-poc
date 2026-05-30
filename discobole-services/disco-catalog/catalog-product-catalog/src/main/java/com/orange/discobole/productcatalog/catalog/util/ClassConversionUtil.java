// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.util;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;

public class ClassConversionUtil {
    private ClassConversionUtil() {
        throw new IllegalStateException("Class Conversion Utility class");
    }

    public static ProductOfferingPriceDto convertProductOfferingPriceInDTO(ProductOfferingPrice productOfferingPrice) {
        if (productOfferingPrice == null) {
            return null;
        }

        ProductOfferingPriceDto dto = new ProductOfferingPriceDto();
        dto.setId(productOfferingPrice.getId());
        dto.setHref(productOfferingPrice.getHref());
        dto.setDescription(productOfferingPrice.getDescription());

        dto.setLastUpdate(productOfferingPrice.getLastUpdate());
        dto.setLifecycleStatus(productOfferingPrice.getLifecycleStatus());
        dto.setName(productOfferingPrice.getName());


        dto.setValidFor(productOfferingPrice.getValidFor());
        dto.setBaseType(productOfferingPrice.getBaseType());
        dto.setSchemaLocation(productOfferingPrice.getSchemaLocation());

        // 🔥 Access subclass fields safely
        if (productOfferingPrice instanceof ProductOfferingPriceAlteration alteration) {


            dto.setPercentage(alteration.getPercentage());
            dto.setProrationType(alteration.getProrationType());
        }
        return dto;
    }

    public static ProductOfferingDto convertProductOfferingInDTO(ProductOffering productOffering) {
        if (productOffering == null) {
            return null;
        }

        ProductOfferingDto dto = new ProductOfferingDto();
        dto.setId(productOffering.getId());
        dto.setHref(productOffering.getHref());
        dto.setDescription(productOffering.getDescription());
        dto.setIsBundle(productOffering.getIsBundle());
        dto.setIsSellable(productOffering.getIsSellable());
        dto.setIsVisible(productOffering.getIsVisible());
        dto.setIsInstallable(productOffering.isIsInstallable());
        dto.setLastUpdate(productOffering.getLastUpdate());
        dto.setLifecycleStatus(productOffering.getLifecycleStatus());
        dto.setName(productOffering.getName());
        dto.setStatusReason(productOffering.getStatusReason());
        dto.setVersion(productOffering.getVersion());
        dto.setBillingType(productOffering.getBillingType());
        dto.setBundledProductOffering(productOffering.getBundledProductOffering());
        dto.setCategory(productOffering.getCategory());
        dto.setChannel(productOffering.getChannel());
        dto.setMarketSegment(productOffering.getMarketSegment());
        dto.setPolicyRuleRef(productOffering.getPolicyRuleRef());
        dto.setProdSpecCharValueUse(productOffering.getProdSpecCharValueUse());
        dto.setProductOfferingPrice(productOffering.getProductOfferingPrice());
        dto.setProductOfferingTerm(productOffering.getProductOfferingTerm());
        dto.setProductSpecification(productOffering.getProductSpecification());
        dto.setBrand(productOffering.getBrand());
        dto.setValidFor(productOffering.getValidFor());
        dto.setCommercialOperation(productOffering.getCommercialOperation());
        dto.setProductOfferingRelationship(productOffering.getProductOfferingRelationship());
        dto.setNumberBundledOfferLowerLimit(productOffering.getNumberBundledOfferLowerLimit());
        dto.setNumberBundledOfferUpperLimit(productOffering.getNumberBundledOfferUpperLimit());
        dto.setGlobalMinCardinality(productOffering.getGlobalMinCardinality());
        dto.setGlobalMaxCardinality(productOffering.getGlobalMaxCardinality());
        dto.setBaseType(productOffering.getBaseType());
        dto.setSchemaLocation(productOffering.getSchemaLocation());
        dto.setType(productOffering.getType());
        dto.setRelatedParty(productOffering.getRelatedParty());
        dto.setAllowedAction(productOffering.getAllowedAction());
        return dto;
    }
}
