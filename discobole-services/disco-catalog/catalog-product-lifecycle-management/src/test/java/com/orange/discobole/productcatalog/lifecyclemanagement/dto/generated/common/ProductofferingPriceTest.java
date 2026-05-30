// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.servicespec.ServiceSpecification;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

 class ProductofferingPriceTest extends ManageLifeCycleApplicationTests {

@Test
void createProductOfferingprice() {
ProductOfferingPrice pop=new ProductOfferingPrice().id("").baseType("").description(null).
duration(1).href("").isBundle(Boolean.TRUE).lastUpdate(OffsetDateTime.now()).lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE)
.name("").percentage(1.1f).popRelationship(List.of(new ProductOfferingPriceRelationship().href(null).
		id(null).name(null).relationshipType(POPRelationshipType.ALTEREDBY).schemaLocation("").type("")
		.validFor(new TimePeriod().endDateTime(null).startDateTime(null)))).
price(new Money().unit(null).value(1.1f)).priceType(PriceType.NRC).priority(1).recurringChargePeriodLength(1).schemaLocation("").tax(
		List.of(new TaxItem().baseType(null).schemaLocation(null).taxAmount(null).
				taxCategory(null).taxRate(1.1f).type(""))).type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).
unitOfMeasure(new com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.Quantity().amount(null).units(null)).validFor(new TimePeriod()).version(null);
assertNotNull(pop.toString());
assertEquals(false, pop.equals(new ProductOfferingPrice()));
}

@Test
void createProductSpecification() {
	ProductSpecification ps=new ProductSpecification().baseType(null).brand(null).
			description(null).description(null).href(null).id(null).isBundle(null).lastUpdate(null).
			lifecycleStatus(ProductSpecificationLifecycle.INDESIGN).name(null)
			.operationSpecification(List.of(new OperationSpecification().baseType(null).description(null).id(null).isQualificationRequested(null)
					.name(null).schemaLocation(null).type(null).validFor(null))).serviceSpecification(List.of(new ServiceSpecificationRef().
							baseType(null).href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null).version(null)));
	assertNotNull(ps.toString());
	assertEquals(false, ps.equals(new ProductSpecification()));
}
@Test
void createProductOffering() {
	ProductOffering po=new ProductOffering().baseType(null).brand(null).bundledProductOffering(List.of(new BundledProductOffering()
			.baseType(null).bundledProductOfferingOption(new BundledProductOfferingOption().baseType(null).numberRelOfferDefault(null).numberRelOfferLowerLimit(null)
					.numberRelOfferUpperLimit(null).schemaLocation(null).type(null)).href(null).id(null).lifecycleStatus(null)
			.name(null).schemaLocation(null).type(null))).category(Set.of(new CategoryRef().baseType(null).href(null).id(null)
					.name(null).referredType(null).schemaLocation(null).type(null).version(null))).channel(List.of(new ChannelRef().
							baseType(null).href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null)))
			.commercialOperation(List.of(new CommercialOperation().baseType(null).carries(List.of(new ProductOfferingPriceRef().baseType(null)
					.href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null))).description(null).id(null).name(null).
					schemaLocation(null).type(null).validFor(null))).description(null).globalMaxCardinality(null).globalMinCardinality(null).href(null).id(null).
			isBundle(null).isInstallable(null).isSellable(null).lastUpdate(null).lifecycleStatus(null).marketSegment(List.
					of(new MarketSegmentRef().baseType(null).href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null)))
			.numberBundledOfferLowerLimit(null).numberBundledOfferUpperLimit(null).prodSpecCharValueUse(
					List.of(new ProductSpecificationCharacteristicValueUse().baseType(null).description(null).maxCardinality(null).minCardinality(null)
							.name(null).productSpecCharacteristicValue(List.of(new ProductSpecificationCharacteristicValue().baseType(null).isDefault(null)
									.rangeInterval(null).regex(null).schemaLocation(null).characteristicReferenceValue(null).type(null).unitOfMeasure(null)
									.validFor(null).value(null).valueFrom(null).valueTo(null).valueType(null))).productSpecification(new ProductSpecificationRef()
											.baseType(null).href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null).version(null))
							.schemaLocation(null).type(null).validFor(null).valueType(null))).productOfferingPrice(null).
			productOfferingRelationship(List.of(new ProductOfferingRelationship().baseType(null).href(null).id(null).relationshipType(null).type(null).validFor(null)))
			.productOfferingTerm(List.of(new ProductOfferingTerm().baseType(null).description(null).duration(null).name(null).schemaLocation(null)
					.type(null).validFor(null))).relatedParty(List.of(new RelatedParty().baseType(null).
							href(null).id(null).name(null).referredType(null).role(null).schemaLocation(null))).schemaLocation(null).statusReason(null).type(null).validFor(null).version(null);
	assertNotNull(po.toString());
	assertEquals(false, po.equals(new ProductOffering()));
}
@Test
void createCategory() {
	Category category=new Category().baseType(null).
			description(null).href(null).id(null).isRoot(null).lastUpdate(null).lastUpdate(null)
			.lifecycleStatus(null).name(null).parentId(null).productOffering(
					Set.of(new ProductOfferingRef().baseType(null).href(null).id(null).name(null).referredType(null)
							.schemaLocation(null).type(null))).schemaLocation(null).subCategory(List.of(new CategoryRef()
									.baseType(null).href(null).id(null).name(null).referredType(null).schemaLocation(null).type(null).version(null))).type(null).validFor(null).version(null);
	assertNotNull(category.toString());
	assertEquals(false, category.equals(new Category()));
}
@Test
void createServiceSpecification() {
	ServiceSpecification serviceSpec=new ServiceSpecification().baseType(null).description(null).
			href(null).id(null).isBundle(null).lastUpdate(null).lifecycleStatus(null).name(null).schemaLocation(null).targetServiceSchema(null).
			operationSpecification(null).relatedParty(null).serviceSpecRelationship(null).resourceSpecification(null).schemaLocation(null).serviceLevelSpecification(null).
			targetServiceSchema(null).type(null).usageSpecification(null).version(null).validFor(null);
	assertNotNull(serviceSpec.toString());
	assertEquals(true, serviceSpec.equals(new ServiceSpecification()));
}
}
