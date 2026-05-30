// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.aggregate;
//package com.orange.bos.productoffering.aggregate;

 import static org.axonframework.test.matchers.Matchers.*;
 import static org.hamcrest.MatcherAssert.assertThat;
 import static org.hamcrest.Matchers.*;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertThrows;
 import static org.mockito.ArgumentMatchers.anyString;
 import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.Mockito.*;

// import java.time.OffsetDateTime;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

 import com.orange.discobole.processflow.exception.DiscoManagedClientException;
 import com.orange.discobole.processflow.infra.Publisher;
 import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
 import com.orange.discobole.productcatalog.productoffering.command.productoffering.InitiateProductOfferingCommand;
 import com.orange.discobole.productcatalog.productoffering.command.productoffering.ProductOfferingIdentityDataCommand;
 import com.orange.discobole.productcatalog.productoffering.command.productoffering.SelectProductOfferingCharacteristicCommand;
 import com.orange.discobole.productcatalog.productoffering.command.productoffering.SelectProductOfferingTypeCommand;
 import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
 import com.orange.discobole.processflow.event.Event;
 import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
 import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
 import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
 import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
 import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;
 import com.orange.discobole.productcatalog.productoffering.interceptor.AccessTokenInterceptor;
 import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
 import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
 import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
 import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
 import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
 import com.orange.discobole.productcatalog.productoffering.service.QueryService;
 import com.orange.discobole.productcatalog.productoffering.service.impl.ModifyCategoryServiceImpl;
 import com.orange.discobole.productcatalog.productoffering.util.ChannelCache;
 import com.orange.discobole.productcatalog.productoffering.util.ConverterUtil;
 import com.orange.discobole.productcatalog.productoffering.util.MarketSegmentCache;
 import org.axonframework.eventsourcing.eventstore.EventStoreException;
 import org.axonframework.test.aggregate.AggregateTestFixture;
 import org.axonframework.test.aggregate.FixtureConfiguration;
 import org.axonframework.test.matchers.IgnoreField;
 import org.axonframework.test.matchers.Matchers;
 import org.jetbrains.annotations.NotNull;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;


 import java.time.OffsetDateTime;
 import java.util.*;

 import org.axonframework.eventhandling.EventMessage;
 import org.mockito.Mockito;
 import org.mockito.MockitoAnnotations;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.mockito.InjectMocks;

// import com.orange.bos.category.event.category.modify.AssociatedEntityModifiedEvent;
// import com.orange.bos.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
// import com.orange.bos.envelope.event.Event;
// import com.orange.bos.envelope.exception.BosException;
// import com.orange.bos.envelope.infra.Publisher;
// import com.orange.bos.productoffering.command.productoffering.AssociatePOPtoOperationSpecificationCommand;
// import com.orange.bos.productoffering.command.productoffering.AtomicProductOfferingDescriptionCommand;
// import com.orange.bos.productoffering.command.productoffering.DefineProductOfferingCategoryCommand;
// import com.orange.bos.productoffering.command.productoffering.InitiateProductOfferingCommand;
// import com.orange.bos.productoffering.command.productoffering.ManageProductOfferingBundlingCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOffCancelCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingDeleteCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingMarketCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingOperationCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingRelatedPartyCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingRelationshipCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingTermCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingValidForCommand;
// import com.orange.bos.productoffering.command.productoffering.ProductOfferingValidatedCommand;
// import com.orange.bos.productoffering.command.productoffering.SelectProductOfferingChannelCommand;
// import com.orange.bos.productoffering.command.productoffering.SelectProductOfferingCharacteristicCommand;
// import com.orange.bos.productoffering.command.productoffering.SelectProductOfferingTypeCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.AtomicProductOfferingModifiedValidatedCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyAssociatePOPtoOperationSpecificationCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingCategoryCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingChannelCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingCharacteristicCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingDescriptionCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingMarketCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingOperationCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingRelatedPartyCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingRelationshipCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingTermCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.ModifyProductOfferingValidForCommand;
// import com.orange.bos.productoffering.command.productoffering.modify.POModificationCommand;
// import com.orange.bos.productoffering.dto.generated.common.BundledProductOffering;
// import com.orange.bos.productoffering.dto.generated.common.BundledProductOfferingOption;
// import com.orange.bos.productoffering.dto.generated.common.Category;
// import com.orange.bos.productoffering.dto.generated.common.CategoryEntityRelationship;
// import com.orange.bos.productoffering.dto.generated.common.CategoryRef;
// import com.orange.bos.productoffering.dto.generated.common.ChannelRef;
// import com.orange.bos.productoffering.dto.generated.common.MarketSegmentRef;
// import com.orange.bos.productoffering.dto.generated.common.ProductOffering;
// import com.orange.bos.productoffering.dto.generated.common.ProductOfferingPrice;
// import com.orange.bos.productoffering.dto.generated.common.ProductOfferingRef;
// import com.orange.bos.productoffering.dto.generated.common.ProductOfferingTerm;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecification;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationCharacteristic;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationCharacteristicValue;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationCharacteristicValueUse;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationLifecycle;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationRef;
// import com.orange.bos.productoffering.dto.generated.common.ProductSpecificationRelationship;
// import com.orange.bos.productoffering.dto.generated.common.Quantity;
// import com.orange.bos.productoffering.dto.generated.common.RelatedParty;
// import com.orange.bos.productoffering.dto.generated.common.TimePeriod;
// import com.orange.bos.productoffering.dto.generated.common.VersionType;
// import com.orange.bos.productoffering.dto.generated.productoffering.CommercialOperation;
// import com.orange.bos.productoffering.dto.generated.productoffering.OperationSpecification;
// import com.orange.bos.productoffering.dto.generated.productoffering.PartyType;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingPriceLifecycle;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingPriceType;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
// import com.orange.bos.productoffering.dto.generated.productoffering.ProductOfferingType;
// import com.orange.bos.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
// import com.orange.bos.productoffering.event.bundleproductoffering.BundleProductOfferingDescribedEvent;
// import com.orange.bos.productoffering.event.bundleproductoffering.BundleProductOfferingOperDefinedEvent;
// import com.orange.bos.productoffering.event.bundleproductoffering.BundleProductOfferingSelectedEvent;
// import com.orange.bos.productoffering.event.bundleproductoffering.BundleProductOfferingTermDefinedEvent;
// import com.orange.bos.productoffering.event.bundleproductoffering.BundleProductOfferingValidForDefinedEvent;
// import com.orange.bos.productoffering.event.bundleproductoffering.ChildPOInfoAddedEvent;
 //import com.orange.bos.productoffering.event.bundleproductoffering.CreateBundleProductOfferingEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingBundleDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingCategoryDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingChannelDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingCharacteristicsDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingCreationCompletedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingDescribedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingInitiatedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingMarketDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingOperDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingRelatedPartyDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingRelationshipDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingTermDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingValidForDefinedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingValidatedEvent;
// import com.orange.bos.productoffering.event.productoffering.AtomicProductOfferingVersionCreatedEvent;
// import com.orange.bos.productoffering.event.productoffering.LinkPOPtoOperEvent;
// import com.orange.bos.productoffering.event.productoffering.ProductOffCancelledEvent;
// import com.orange.bos.productoffering.event.productoffering.ProductOfferingDeleteEvent;
// import com.orange.bos.productoffering.event.productoffering.ProductOfferingInternalProjectionEvent;
// import com.orange.bos.productoffering.event.productoffering.ProductOfferingTypeSelectedEvent;
// import com.orange.bos.productoffering.event.productoffering.ProductSpecStateVerifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingChannelModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingCharacteristicsModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingDescribedModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingMarketModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingModificationValidatedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingOperationModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingRelatedPartyModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingRelationshipModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingTermModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.AtomicProductOfferingValidForModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.LinkPOPtoOperModifiedEvent;
// import com.orange.bos.productoffering.event.productoffering.modify.ProductOfferingModificationInitiatedEvent;
// import com.orange.bos.productoffering.exception.BosInvalidEventException;
// import com.orange.bos.productoffering.mapper.TimePeriodMapper;
// import com.orange.bos.productoffering.pojo.ProductCharValue;
// import com.orange.bos.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
// import com.orange.bos.productoffering.service.QueryService;
// import com.orange.bos.productoffering.service.impl.ModifyCategoryServiceImpl;
// import com.orange.bos.productoffering.util.ConverterUtil;

@ExtendWith(MockitoExtension.class)
 class ProductOfferingAggregateTest {

	private final String productSpecId = "productSpec_id";
	private final String productOfferingId = "product_offering_id";
	private final String productOfferingId2 = "product_offering_id2";
	private OffsetDateTime psStartDateTime = OffsetDateTime.now();
	private OffsetDateTime psEndDateTime = OffsetDateTime.now().plusYears(1);

	private QueryService queryService;
	private ModifyProductOfferingService modifyProductOfferingService;
	private FixtureConfiguration<ProductOfferingAggregate> fixture;

// 	private final String productSpecId = "1";
// 	private final String productOfferingId = "product_offering_id";

 	private final String aggregateId = "1";

// 	private QueryService queryService;
 	private Publisher publisher;
// 	private FixtureConfiguration<ProductOfferingAggregate> fixture;
 	private ModifyCategoryServiceImpl modifyCategoryServiceImpl;
	private  AccessTokenInterceptor  accessTokenInterceptor;

private ChannelCache channelCache;
private MarketSegmentCache mktSegmentCache;
private OffsetDateTime currentTime=OffsetDateTime.now();






// 	@BeforeEach
// 	void setup() {
//
// 		queryService = Mockito.mock(QueryService.class);
// 		publisher = Mockito.mock(Publisher.class);
// 		modifyCategoryServiceImpl = Mockito.mock(ModifyCategoryServiceImpl.class);
//		 accessTokenInterceptor=Mockito.mock(AccessTokenInterceptor.class);
//		channelCache = Mockito.mock(ChannelCache.class);
//		mktSegmentCache =  Mockito.mock(MarketSegmentCache.class);
//
//		MockitoAnnotations.openMocks(this);
//
//
//
////
////		when(accessTokenInterceptor.getToken())
////				.thenReturn(accessToken);
//
// 		fixture = new AggregateTestFixture<>(ProductOfferingAggregate.class);
// 		fixture.registerFieldFilter(new IgnoreField(ProductOfferingTypeSelectedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(CreateBundleProductOfferingEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingInitiatedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingDescribedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AssociatedEntityModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCategoryDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingChannelDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingMarketDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingRelatedPartyDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingOperDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(LinkPOPtoOperEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingTermDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingTermDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidForDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingValidForDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingBundleDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidatedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingVersionCreatedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(
// 				new IgnoreField(AtomicProductOfferingCreationCompletedEvent.class, "productOffering"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingRelationshipDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(
// 				new IgnoreField(AtomicProductOfferingCharacteristicsDefinedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(ProductOffCancelledEvent.class, "productOffering"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingDescribedModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCategoryModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingChannelModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingMarketModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(LinkPOPtoOperModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(
// 				new IgnoreField(AtomicProductOfferingRelatedPartyModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingTermModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(
// 				new IgnoreField(AtomicProductOfferingCharacteristicsModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(
// 				new IgnoreField(AtomicProductOfferingRelationshipModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidForModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingOperationModifiedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(ProductOfferingModificationInitiatedEvent.class, "lastUpdate"));
//
// 		fixture.registerFieldFilter(new IgnoreField(ChildPOInfoAddedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingSelectedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingModificationValidatedEvent.class, "lastUpdate"));
// 		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingModificationValidatedEvent.class, "productOffering"));
// 		fixture.registerInjectableResource(modifyCategoryServiceImpl);
// 	}

	@BeforeEach
		void setup() {
		queryService = Mockito.mock(QueryService.class);
		modifyProductOfferingService = Mockito.mock(ModifyProductOfferingService.class);

		fixture = new AggregateTestFixture<>(ProductOfferingAggregate.class);
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingTypeSelectedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CreateBundleProductOfferingEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingDescribedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AssociatedEntityModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCategoryDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingChannelDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingMarketDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingRelatedPartyDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingOperDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(LinkPOPtoOperEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingTermDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingTermDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidForDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingValidForDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingBundleDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingVersionCreatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCreationCompletedEvent.class, "productOffering"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingRelationshipDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCharacteristicsDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOffCancelledEvent.class, "productOffering"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingDescribedModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCategoryModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingChannelModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingMarketModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(LinkPOPtoOperModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingRelatedPartyModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingTermModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingCharacteristicsModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingValidForModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingOperationModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingModificationInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ChildPOInfoAddedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(BundleProductOfferingSelectedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingModificationValidatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AtomicProductOfferingModificationValidatedEvent.class, "productOffering"));

		//register resources
		fixture.registerInjectableResource(queryService);
		fixture.registerInjectableResource(modifyProductOfferingService);

		//mock services (DB calls)
		ProductSpecification productSpecification = getProductSpecification();
		Mockito.lenient().when(queryService.fetchProductSpecById(productSpecId, null)).thenReturn(productSpecification);

	}


	private ProductSpecification getProductSpecification() {
		return new ProductSpecification()
				.id(productSpecId)
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
				.description("Mobile Line")
				.brand("Mobile Line")
				.name("Mobile Line")
				.validFor(new TimePeriod().startDateTime(psStartDateTime).endDateTime(psEndDateTime))
				.productSpecCharacteristic(List.of(new ProductSpecificationCharacteristic()
						.id(productSpecId + ".1")
						.name("Data Bundle")
						.configurable(true)
						.productSpecCharacteristicValue(List.of(new ProductSpecificationCharacteristicValue()
								.validFor(new TimePeriod().startDateTime(psStartDateTime).endDateTime(psEndDateTime))
								.value("20")
								.unitOfMeasure("GB")
								.characteristicReferenceValue("20")
								.type("ObjectCharacteristic")))
						.type("ObjectCharacteristic")
						.validFor(new TimePeriod().startDateTime(psStartDateTime).endDateTime(psEndDateTime))));
	}

	private ProductOffering getAtomicProductOffering() {

		return new ProductOffering()
				.id(productOfferingId)
				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
				.name("Mobile Line")
				.brand("orange")
				.validFor(new TimePeriod().startDateTime(currentTime.plusDays(1)).endDateTime(currentTime.plusDays(21)))
				.channel(List.of(new ChannelRef().id("Selfcare").name("Selfcare"), new ChannelRef().id("Web").name("Web")))
				.marketSegment(List.of(new MarketSegmentRef().id("B2B").name("B2B"), new MarketSegmentRef().id("B2C").name("B2C")))
				.category(Set.of(new CategoryRef().id("category1").name("category-name")))
				.commercialOperation(List.of(new CommercialOperation().id("1").name("Add")))
				.prodSpecCharValueUse(getPSPOCharacteristicValueUse())
				.productOfferingRelationship(List.of(new ProductOfferingRelationship().id(productOfferingId2).relationshipType(ProductOfferingRelationshipType.REQUIRES)));
	}
	private List<ProductSpecificationCharacteristicValueUse> getPSPOCharacteristicValueUse(){
		//PS Char
		List<ProductCharValue> productCharValues1 = new ArrayList<>();
		ProductCharValue productCharValue = new ProductCharValue();
		productCharValue.setCharacteristicReferenceValue("20");
		productCharValue.setUnitOfMeasure("GB");
		productCharValue.setValue("20");
		productCharValue.setValidFor(new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod().startDateTime(psStartDateTime.plusDays(1)).endDateTime(psEndDateTime.minusDays(1)));
		productCharValues1.add(productCharValue);

		//PO Char
		com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod poCharTimePeriod=new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod().startDateTime(currentTime.plusDays(1)).endDateTime(currentTime.plusDays(21));
		List<ProductCharValue> productCharValues2 = new ArrayList<>();
		ProductCharValue productCharValue1 = new ProductCharValue();
		productCharValue1.setValue("Yes");
		productCharValue1.setValidFor(poCharTimePeriod);
		ProductCharValue productCharValue2 = new ProductCharValue();
		productCharValue2.setValue("No");
		productCharValue2.setValidFor(poCharTimePeriod);
		productCharValues2.add(productCharValue1);
		productCharValues2.add(productCharValue2);

		List<ProductSpecificationCharacteristicValueUse> characteristics=new ArrayList<>();
		characteristics.add(new ProductSpecificationCharacteristicValueUse()
				.id(productOfferingId + ".1")
				.name("TEST PO Char")
				.description("TEST PO Char")
				.validFor(TimePeriodMapper.toGenerated(poCharTimePeriod))
				.baseType(ProductOffConstants.USER_PRODOFF_CHAR)
				.minCardinality(1).maxCardinality(1).type("StringCharacteristic")
				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues2)));

		characteristics.add(new ProductSpecificationCharacteristicValueUse().minCardinality(0).maxCardinality(1)
				.id(productSpecId + ".1")
				.name("Data Bundle")
				.validFor(new TimePeriod().startDateTime(psStartDateTime.plusDays(1)).endDateTime(psEndDateTime.minusDays(1)))
				.type("ObjectCharacteristic")
				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues1)));
		return characteristics;
	}

	private List<Event> atomicProductOfferingHistory() {
		ProductOffering atomicProductOffering = getAtomicProductOffering();

		DefineIdentityData defineIdentityData = new DefineIdentityData();
		defineIdentityData.setBrand(atomicProductOffering.getBrand());
		defineIdentityData.setIsInstallable(atomicProductOffering.isIsInstallable());
		defineIdentityData.setIsVisible(atomicProductOffering.isIsVisible());
		defineIdentityData.setIsSellable(atomicProductOffering.isIsSellable());
		defineIdentityData.setName(atomicProductOffering.getName());
		defineIdentityData.setProductNumber("123");

		List<Event> history = new ArrayList<>();
		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, OffsetDateTime.now(), true, false, true, ProductOfferingType.ATOMICPRODUCTOFFERING, ProductOfferingLifecycle.INSTUDY));

		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId, ProductSpecificationLifecycle.ACTIVE));
		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId), productOfferingId, ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));

		history.add(new AtomicProductOfferingIdentityDataDefinedEvent(productOfferingId, defineIdentityData, atomicProductOffering.getChannel(), atomicProductOffering.getMarketSegment(), atomicProductOffering.getRelatedParty(), atomicProductOffering.getProductOfferingTerm(), atomicProductOffering.getValidFor(), ProductOfferingType.ATOMICPRODUCTOFFERING, null, OffsetDateTime.now(), atomicProductOffering.getHref()));

		history.add(new AtomicProductOfferingCategoryDefinedEvent("1", atomicProductOffering.getCategory(), OffsetDateTime.now()));

		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId, atomicProductOffering.getCommercialOperation(), atomicProductOffering.getProductOfferingTerm(), OffsetDateTime.now()));

		history.add(new AtomicProductOfferingCharacteristicsDefinedEvent(productOfferingId, atomicProductOffering.getProdSpecCharValueUse(), OffsetDateTime.now()));

		history.add(new AtomicProductOfferingRelationshipDefinedEvent(productOfferingId, atomicProductOffering.getProductOfferingRelationship(), OffsetDateTime.now()));
		return history;
	}
	 @Test
	 void shouldApplyProductOfferingTypeSelectedEventOnSelectProductOfferingTypeCommand() {
		 // Given
		 String aggregateId = "1";
		 ProductOfferingType type = ProductOfferingType.BUNDLEPRODUCTOFFERING;
		 boolean isSellable = true;
		 boolean isBundle = false;
		 boolean isInstallable = true;

		 SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(
				 aggregateId, type, isSellable, isBundle, isInstallable);

		 // When / Then
		 fixture.givenNoPriorActivity()
				 .when(command).expectEvents(
						 new ProductOfferingTypeSelectedEvent(
								 aggregateId,
								 OffsetDateTime.now(),  // <-- You might want to use a matcher for time
								 isSellable,
								 isBundle,
								 isInstallable,
								 type,
								 ProductOfferingLifecycle.INSTUDY
						 )
				 );
	 }

	 @Test
	 void raiseEventsWhenProcessProductOfferingSelectProductOfferingTypeASBPO() {
		 String aggregateId = "1";
		 ProductOfferingType productType = ProductOfferingType.BUNDLEPRODUCTOFFERING;
		 boolean isSellable = true;
		 boolean isBundle = false;
		 boolean isInstallable = true;

		 SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(
				 aggregateId, productType, isSellable, isBundle, isInstallable);

		 fixture.givenNoPriorActivity()
				 .when(command)
				 .expectEventsMatching(exactSequenceOf(
						 messageWithPayload(allOf(
								 instanceOf(ProductOfferingTypeSelectedEvent.class),
								 hasProperty("productOfferingId", org.hamcrest.Matchers.equalTo(aggregateId)),
								 hasProperty("isSellable", org.hamcrest.Matchers.equalTo(isSellable)),
								 hasProperty("isBundle", org.hamcrest.Matchers.equalTo(isBundle)),
								 hasProperty("isInstallable", org.hamcrest.Matchers.equalTo(isInstallable)),
								 hasProperty("type", org.hamcrest.Matchers.equalTo(productType)),
								 hasProperty("lifeCycleStatus", org.hamcrest.Matchers.equalTo(ProductOfferingLifecycle.INSTUDY))

						 ))
				 ));
	 }


// 	@Test
// 	void raiseEventsWhenProcessProductOfferingSelectProductOfferingTypeASAPO() {

// 		ProductOfferingType productType = ProductOfferingType.ATOMICPRODUCTOFFERING;
// 		boolean isSellable = true;
// 		boolean isBundle = false;
// 		boolean isInstallable = true;

// 		SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(aggregateId, productType,
// 				isSellable, isBundle, isInstallable);

// 		fixture.registerInjectableResource(queryService).given().when(command)
// 				.expectEvents(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.INSTUDY,
// 						OffsetDateTime.now(), productType));
// 	}


	@Test
	void shouldApplyEventsOnValidProductSpeconPOCreation() {
		// Arrange
		String aggregateId = "123";
		String productSpecId = "123";

		InitiateProductOfferingCommand command = new InitiateProductOfferingCommand(aggregateId, productSpecId);

		ProductSpecification mockSpec = new ProductSpecification();
		mockSpec.setId(productSpecId);
		mockSpec.setLifecycleStatus(ProductSpecificationLifecycle.ACTIVE); // Or LAUNCHED
		TimePeriod timePeriod = new TimePeriod();
		timePeriod.setEndDateTime(OffsetDateTime.now().plusDays(5)); // Valid
		mockSpec.setValidFor(timePeriod);

		// Stubbing queryService
		when(queryService.fetchProductSpecById("123", null)).thenReturn(mockSpec);

		// Simulate prior event to "create" aggregate instance
		ProductOfferingTypeSelectedEvent createdEvent = new ProductOfferingTypeSelectedEvent(aggregateId,
				OffsetDateTime.now(),  // <-- You might want to use a matcher for time
				true,
				true,
				true,
				ProductOfferingType.BUNDLEPRODUCTOFFERING,
				ProductOfferingLifecycle.INSTUDY);

		fixture.registerInjectableResource(queryService)
				.given(createdEvent) // simulate existing aggregate
				.when(command)
				.expectEventsMatching(exactSequenceOf(
						messageWithPayload(instanceOf(ProductSpecStateVerifiedEvent.class)),
						messageWithPayload(instanceOf(AtomicProductOfferingInitiatedEvent.class))
				));
	}



	@Test
	void raiseInvalidProductSpecStatusEventWhenProcessProductOfferingInitiationWithIncorrectLifecycleStatustest() {
		boolean isSellable = true;
		boolean isBundle = false;
		boolean isInstallable = true;
		String aggregateId = "product_offering_id";
		ProductOfferingType productType = ProductOfferingType.BUNDLEPRODUCTOFFERING;

		SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(
				aggregateId, productType, isSellable, isBundle, isInstallable);

		InitiateProductOfferingCommand command1 = new InitiateProductOfferingCommand(aggregateId, productSpecId);

		TimePeriod validTimePeriod = new TimePeriod();
		validTimePeriod.setStartDateTime(OffsetDateTime.now().minusDays(1));
		validTimePeriod.setEndDateTime(OffsetDateTime.now().plusDays(5));

		ProductSpecification invalidProductSpec = new ProductSpecification()
				.id(productSpecId)
				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY) // Invalid lifecycle status
				.validFor(validTimePeriod);

		//String fakeToken = "dummy-token";


//		when(accessTokenInterceptor.getToken())
//				.thenReturn(accessToken);
		when(queryService.fetchProductSpecById(productSpecId, null))
				.thenReturn(invalidProductSpec);

//		when(queryService.fetchProductSpecById(eq(command1.getProductSpecId()), any()))
		fixture.registerInjectableResource(queryService).given(
						new ProductOfferingTypeSelectedEvent(
								productOfferingId,
								OffsetDateTime.now(),
								true,  // or your isSellable variable
								false, // or your isBundle variable
								true,  // or your isInstallable variable
								productType,
								ProductOfferingLifecycle.INSTUDY
						),
						new CreateBundleProductOfferingEvent(productOfferingId, OffsetDateTime.now(),
								command.getIsSellable(), command.getIsBundle(), command.getIsInstallable(),
								command.getProductOfferingType()))
				.when(command1)
				.expectException(DiscoManagedClientException.class); // More accurate than EventStoreException
	}


	@Test
	void shouldThrowExceptionWhenProductSpecValidityExpired() {
		// Arrange
		String aggregateId = "product_offering_id";
		ProductOfferingType productType = ProductOfferingType.ATOMICPRODUCTOFFERING;
		boolean isSellable = true;
		boolean isBundle = false;
		boolean isInstallable = true;

		SelectProductOfferingTypeCommand selectCommand = new SelectProductOfferingTypeCommand(
				aggregateId, productType, isSellable, isBundle, isInstallable);

		InitiateProductOfferingCommand initiateCommand = new InitiateProductOfferingCommand(aggregateId, productSpecId);

		// Set expired time period
		TimePeriod expiredTimePeriod = new TimePeriod();
		expiredTimePeriod.setStartDateTime(OffsetDateTime.now().minusDays(10));
		expiredTimePeriod.setEndDateTime(OffsetDateTime.now().minusDays(1)); // Already expired

		ProductSpecification expiredProductSpec = new ProductSpecification()
				.id(productSpecId)
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
				.validFor(expiredTimePeriod);

		when(queryService.fetchProductSpecById(productSpecId, null))
				.thenReturn(expiredProductSpec);

		fixture.registerInjectableResource(queryService).given(
						new ProductOfferingTypeSelectedEvent(
								aggregateId,
								OffsetDateTime.now(),
								isSellable,
								isBundle,
								isInstallable,
								productType,
								ProductOfferingLifecycle.INSTUDY
						),
						new CreateBundleProductOfferingEvent(
								aggregateId,
								OffsetDateTime.now(),
								isSellable,
								isBundle,
								isInstallable,
								productType
						)
				)
				.when(initiateCommand)
				.expectException(DiscoManagedClientException.class);

	}









	@Test
	void raiseEventsWhenSelectProductSpecCharacteristicCommandRaised() {
		ProductOffering expectedProductOffering = getAtomicProductOffering();
		List<Event> eventsHistory = atomicProductOfferingHistory();

		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = requestedCharacteristic();

		//command for characteristic
		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(productOfferingId, pickAtomicProductOfferingCharacteristic);

		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectEvents(new AtomicProductOfferingCharacteristicsDefinedEvent(productOfferingId, expectedProductOffering.getProdSpecCharValueUse(), OffsetDateTime.now()));
	}
	@Test
	void raiseExceptionWhenSelectProductSpecCharacteristicCommandRaisedForDuplicateName() {
		List<Event> eventsHistory = atomicProductOfferingHistory();

		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = requestedCharacteristic();
		for( PickAtomicProductOfferingCharacteristic characteristic:pickAtomicProductOfferingCharacteristic){
			characteristic.setName("Duplicate Name");
		}
		//command for characteristic
		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(productOfferingId, pickAtomicProductOfferingCharacteristic);

		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void raiseExceptionWhenSelectProductSpecCharacteristicCommandRaisedForConstraints() {
		List<Event> eventsHistory = atomicProductOfferingHistory();

		PickAtomicProductOfferingCharacteristic characteristic = new PickAtomicProductOfferingCharacteristic();
		//command for characteristic
		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(productOfferingId, List.of(characteristic));
		characteristic.setBaseType("TESTPOChar");
		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectException(DiscoManagedClientException.class);
		characteristic.setBaseType(ProductOffConstants.USER_PRODOFF_CHAR);

		//non null char Id
		characteristic.setId("TEST_ID");

		//null name and description
		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectException(DiscoManagedClientException.class);
		characteristic.setName("Test");
		characteristic.setDescription("Test");
		//null charValue
		characteristic.setProductSpecCharacteristicValue(List.of(new ProductCharValue(),new ProductCharValue()));
		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectException(DiscoManagedClientException.class);
		characteristic.getProductSpecCharacteristicValue().get(0).setValue("Yes");
		characteristic.getProductSpecCharacteristicValue().get(1).setValue("Yes");
		//duplicate charValue
		fixture.given(eventsHistory).when(selectProductOfferingCharacteristicCommand)
				.expectException(DiscoManagedClientException.class);

	}

	private List<PickAtomicProductOfferingCharacteristic> requestedCharacteristic() {
		com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod timePeriod = new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod();
		timePeriod.setStartDateTime(psStartDateTime.plusDays(1));
		timePeriod.endDateTime(psEndDateTime.minusDays(1));

		//PS Char
		List<ProductCharValue> productCharValues1 = new ArrayList<>();
		ProductCharValue productCharValue = new ProductCharValue();
		productCharValue.setCharacteristicReferenceValue("20");
		productCharValue.setUnitOfMeasure("GB");
		productCharValue.setValue("20");
		productCharValue.setValidFor(timePeriod);
		productCharValues1.add(productCharValue);
		//PO Char
		List<ProductCharValue> productCharValues2 = new ArrayList<>();
		ProductCharValue productCharValue1 = new ProductCharValue();
		productCharValue1.setValue("Yes");
		ProductCharValue productCharValue2 = new ProductCharValue();
		productCharValue2.setValue("No");
		productCharValues2.add(productCharValue1);
		productCharValues2.add(productCharValue2);

		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics = new ArrayList<>();
		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristic1 = getPickAtomicProductOfferingCharacteristic(productCharValues1,true, timePeriod);
		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristic2 = getPickAtomicProductOfferingCharacteristic(productCharValues2,false, timePeriod);
		pickAtomicProductOfferingCharacteristics.add(pickAtomicProductOfferingCharacteristic1);
		pickAtomicProductOfferingCharacteristics.add(pickAtomicProductOfferingCharacteristic2);

		return pickAtomicProductOfferingCharacteristics;
	}

	private @NotNull PickAtomicProductOfferingCharacteristic getPickAtomicProductOfferingCharacteristic(List<ProductCharValue> productCharValues,boolean isPSChar, com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod timePeriod) {
		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
		if(isPSChar){
			pickAtomicProductOfferingCharacteristics.setName("Data Bundle");
			pickAtomicProductOfferingCharacteristics.setId(productSpecId + ".1");
		pickAtomicProductOfferingCharacteristics.setMinCardinality(0);
		pickAtomicProductOfferingCharacteristics.setMaxCardinality(1);
			pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
		}
		else{
			pickAtomicProductOfferingCharacteristics.setName("TEST PO Char");
			pickAtomicProductOfferingCharacteristics.setBaseType(ProductOffConstants.USER_PRODOFF_CHAR);
			pickAtomicProductOfferingCharacteristics.setDescription("TEST PO Char");
			pickAtomicProductOfferingCharacteristics.setValidFor(new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod().startDateTime(currentTime.plusDays(5)).endDateTime(currentTime.plusDays(20)));
		}

		return pickAtomicProductOfferingCharacteristics;
	}












// 	@Test
// 	void raiseInvalidProductSpecStatusEventWhenProcessProductOfferingInitiationWithIncorrectLifecycleStatus() {
// 		boolean isSellable = true;
// 		boolean isBundle = false;
// 		boolean isInstallable = true;
// 		ProductOfferingType productType = ProductOfferingType.BUNDLEPRODUCTOFFERING;

// 		SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(aggregateId, productType,
// 				isSellable, isBundle, isInstallable);
// 		InitiateProductOfferingCommand command1 = new InitiateProductOfferingCommand(aggregateId, productSpecId);
// 		when(queryService.fetchProductSpecById(command1.getProductSpecId())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));

// 		fixture.registerInjectableResource(queryService).given(
// 				new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 						OffsetDateTime.now(), productType),
// 				new CreateBundleProductOfferingEvent(productOfferingId, OffsetDateTime.now(), command.getIsSellable(),
// 						command.getIsBundle(), command.getIsInstallable(), command.getProductOfferingType()))
// 				.when(command).expectException(EventStoreException.class);

// 	}

// 	@Test
// 	void raiseProductSpecStatusEventWhenProcessProductOfferingInitiation() {
// 		boolean isSellable = true;
// 		boolean isBundle = false;
// 		boolean isInstallable = true;
// 		ProductOfferingType productType = ProductOfferingType.ATOMICPRODUCTOFFERING;
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
// 		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
// 				.name(productSpecification.getName()).href(productSpecification.getHref())
// 				.version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
// 				.schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		SelectProductOfferingTypeCommand command = new SelectProductOfferingTypeCommand(productOfferingId, productType,
// 				isSellable, isBundle, isInstallable);
// 		InitiateProductOfferingCommand command1 = new InitiateProductOfferingCommand(productOfferingId, productSpecId);

// 		when(queryService.fetchProductSpecById(command1.getProductSpecId())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		fixture.registerInjectableResource(queryService).given(
// 				new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.ACTIVE,
// 						OffsetDateTime.now(), productType),
// 				new CreateBundleProductOfferingEvent(productOfferingId, OffsetDateTime.now(), command.getIsSellable(),
// 						command.getIsBundle(), command.getIsInstallable(), command.getProductOfferingType()))
// 				.when(command1).expectEvents(
// 						new ProductSpecStateVerifiedEvent(productOfferingId, command1.getProductSpecId(),
// 								ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(productSpecificationRef, productOfferingId,
// 								ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));

// 	}

// //		InitiateProductOfferingCommand command = new InitiateProductOfferingCommand(productSpecId);
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(List.of(), queryService);
// //		when(queryService.fetchProductSpecById(command.getProductSpecId())).thenReturn(
// //				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(2, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof ProductSpecSelectedEvent);
// //		ProductSpecSelectedEvent productSpecSelectedEvent = (ProductSpecSelectedEvent) eventList.get(0);
// //		assertNotNull(productSpecSelectedEvent.getProductSpecId());
// //		assertEquals(command.getProductSpecId(), productSpecSelectedEvent.getProductSpecId());
// //		assertEquals(OdacaConstants.PRODUCT_OFFERING, productSpecSelectedEvent.aggregateName());
// //
// //		assertTrue(eventList.get(1) instanceof InvalidProductSpecStatusEvent);
// //		InvalidProductSpecStatusEvent InvalidProductSpecStatusEvent = (InvalidProductSpecStatusEvent) eventList.get(1);
// //		assertNotNull(InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertNotNull(InvalidProductSpecStatusEvent.getResourceState());
// //		assertEquals(command.getProductSpecId(), InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertTrue(ProductSpecificationLifecycle.ACTIVE != InvalidProductSpecStatusEvent.getResourceState()
// 	// && ProductSpecificationLifecycle.LAUNCHED !=
// 	// InvalidProductSpecStatusEvent.getResourceState());
// //	}
// //
// //	@Test
// //	 void raiseEventsWhenProcessProductOfferingInitiation() {
// //		InitiateProductOfferingCommand command = new InitiateProductOfferingCommand(productSpecId);
// //
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById(command.getProductSpecId())).thenReturn(
// //				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(3, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof ProductSpecSelectedEvent);
// //		ProductSpecSelectedEvent productSpecSelectedEvent = (ProductSpecSelectedEvent) eventList.get(0);
// //		assertNotNull(productSpecSelectedEvent.getProductSpecId());
// //		assertEquals(command.getProductSpecId(), productSpecSelectedEvent.getProductSpecId());
// //
// //		assertTrue(eventList.get(1) instanceof ProductSpecStateVerifiedEvent);
// //		ProductSpecStateVerifiedEvent productSpecStateVerifiedEvent = (ProductSpecStateVerifiedEvent) eventList.get(1);
// //		assertNotNull(productSpecStateVerifiedEvent.getProductSpecId());
// //		assertNotNull(productSpecStateVerifiedEvent.getResourceState());
// //		assertEquals(command.getProductSpecId(), productSpecStateVerifiedEvent.getProductSpecId());
// //		assertTrue(ProductSpecificationLifecycle.ACTIVE == productSpecStateVerifiedEvent.getResourceState()
// //				|| ProductSpecificationLifecycle.LAUNCHED == productSpecStateVerifiedEvent.getResourceState());
// //
// //		assertTrue(eventList.get(2) instanceof AtomicProductOfferingInitiatedEvent);
// //		AtomicProductOfferingInitiatedEvent productOfferingInitiatedEvent = (AtomicProductOfferingInitiatedEvent) eventList
// //				.get(2);
// //		assertEquals(command.getProductSpecId(), productOfferingInitiatedEvent.getProductSpec().getId());
// //		assertNotNull(productOfferingInitiatedEvent.getProductOfferingId());
// //		assertEquals(ProductOfferingLifecycle.INSTUDY, productOfferingInitiatedEvent.getLifecycleStatus());
// //	}
// //
// 	@Test
// 	void raiseEventsWhenProcessProductOfferingDescription() {

// 		AtomicProductOfferingDescriptionCommand command = new AtomicProductOfferingDescriptionCommand("1",
// 				"Mobile Access", "Mobile Access", "status_reason", "Mobile Access",
// 				ProductOfferingType.ATOMICPRODUCTOFFERING, true);

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
// //		  ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
// //	                .name(productSpecification.getName()).href(productSpecification.getHref())
// //	                .version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
// //	                .schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());
// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()))
// 				.when(command)
// 				.expectEvents(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason",
// 						"Mobile Access", "Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true,
// 						OffsetDateTime.now()));

// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent("product_spec_id", ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("product_spec_id"),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingDescribedEvent);
// //		AtomicProductOfferingDescribedEvent productOfferingDescribedEvent = (AtomicProductOfferingDescribedEvent) eventList
// //				.get(0);
// //		assertNotNull(productOfferingDescribedEvent.getProductOfferingId());
// //		assertEquals(productOfferingId, productOfferingDescribedEvent.getProductOfferingId());
// //
// //		assertNotNull(productOfferingDescribedEvent.getDescription());
// //		assertEquals("Mobile Access", productOfferingDescribedEvent.getDescription());
// //
// //		assertNotNull(productOfferingDescribedEvent.getBrand());
// //		assertEquals("MobileAccess", productOfferingDescribedEvent.getBrand());
// //
// //		assertNotNull(productOfferingDescribedEvent.getName());
// //		assertEquals("MobileAccess", productOfferingDescribedEvent.getName());
// //
// //		assertNotNull(productOfferingDescribedEvent.getStatusReason());
// //		assertEquals("status_reason", productOfferingDescribedEvent.getStatusReason());
// //
// //		assertNotNull(productOfferingDescribedEvent.getType());
// //		assertEquals(ProductOfferingType.ATOMICPRODUCTOFFERING, productOfferingDescribedEvent.getType());

// 	}

// //
// 	@Test
// 	void raiseInvalidEventWhenProductOfferingDescribedWithInvalidLifecycleStatus() {

// 		AtomicProductOfferingDescriptionCommand command = new AtomicProductOfferingDescriptionCommand("1",
// 				"Mobile Access", "Mobile Access", "status_reason", "Mobile Access",
// 				ProductOfferingType.ATOMICPRODUCTOFFERING, true);

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY);
// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now(),
// 						ProductOfferingType.BUNDLEPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.INSTUDY),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()))
// 				.when(command).expectException(BosInvalidEventException.class);

// //		AtomicProductOfferingDescriptionCommand command = new AtomicProductOfferingDescriptionCommand("description",
// //				"name", "status_reason", "brand", ProductOfferingType.ATOMICPRODUCTOFFERING, true);
// //
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent("product_spec_id", ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("product_spec_id"),
// //				"product_off_id", ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidProductSpecStatusEvent);
// //		InvalidProductSpecStatusEvent InvalidProductSpecStatusEvent = (InvalidProductSpecStatusEvent) eventList.get(0);
// //		assertNotNull(InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertNotNull(InvalidProductSpecStatusEvent.getResourceState());
// //		assertEquals("product_spec_id", InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertTrue(ProductSpecificationLifecycle.ACTIVE != InvalidProductSpecStatusEvent.getResourceState()
// //				&& ProductSpecificationLifecycle.LAUNCHED != InvalidProductSpecStatusEvent.getResourceState());
// 	}

// //	@Test
// //	 void raiseEventsWhenProcessProductOfferingValidFor() {
// //		String productSpecId = "productSpecId6";
// //		String productOfferingId = "productOfferingId7";
// //		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
// //				.endDateTime(OffsetDateTime.now().plusDays(10));
// //		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand(validFor);
// //
// //		List<Event> history = new ArrayList<>();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "Mobile Access", "status_reason",
// //				"MobileAccess", "Cisco", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //
// //		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(
// //				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor));
// //
// //		when(queryService.fetchProductOfferingById(anyString())).thenReturn(new ProductOffering().id("123")
// //				.productSpecification(new ProductSpecificationRef().id("product_spec_id")));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingValidForDefinedEvent);
// //		AtomicProductOfferingValidForDefinedEvent atomicProductOfferingValidForDefinedEvent = (AtomicProductOfferingValidForDefinedEvent) eventList
// //				.get(0);
// //		assertEquals(productOfferingId, atomicProductOfferingValidForDefinedEvent.getProductOffId());
// //		assertNotNull(atomicProductOfferingValidForDefinedEvent.getValidFor());
// //		assertEquals(validFor, atomicProductOfferingValidForDefinedEvent.getValidFor());
// //	}
// //
// 	@Test
// 	void raiseEventsWhenProcessDefiningProductOfferingCategory() {

// 		List<String> categories = new ArrayList<>();
// 		categories.add("BOS_B2B_Classification");

// 		ProductOffering productOffering = new ProductOffering().id("1").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 				.baseType(null).href(null).schemaLocation(null);
// 		String productOffering_type = productOffering.getType().toString();
// 		when(queryService.fetchCategory()).thenReturn(List.of(new Category().id("BOS_B2B_Classification")
// 				.name("B2B Offer").isRoot(Boolean.TRUE).lifecycleStatus("active").type("AtomicProductOffering")));
// 		when(queryService.fetchCategoryById("BOS_B2B_Classification"))
// 				.thenReturn((new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)
// 						.lifecycleStatus("active")).type("AtomicProductOffering"));
// 		DefineProductOfferingCategoryCommand command = new DefineProductOfferingCategoryCommand(aggregateId,
// 				categories);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
// 		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
// 				.name(productSpecification.getName()).href(productSpecification.getHref())
// 				.version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
// 				.schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());

// 		Set<ProductOfferingRef> productOfferings = new HashSet<>();
// 		productOfferings.add(new ProductOfferingRef().id("1").type("AtomicProductOffering").referredType(null)
// 				.baseType(null).href(null).schemaLocation(null));
// 		Set<CategoryRef> addCategories = new HashSet<>();

// 		CategoryRef categoryRef = new CategoryRef().baseType(null).href(null).id("BOS_B2B_Classification")
// 				.schemaLocation(null).type("AtomicProductOffering").version(null).referredType("category");
// 		addCategories.add(categoryRef);
// //		        CategoryRef categoryRef1 = new CategoryRef().baseType(null).href(null).id("BOS_B2B_Classification")
// //		                .schemaLocation(null).type("AtomicProductOffering")
// //		                .version(null).referredType("category");
// //		        addCategories.add(categoryRef1);
// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		doNothing().when(publisher).project(List.of(new ProductOfferingCategoryAssociationEvent()));
// 		fixture.registerInjectableResource(queryService).registerInjectableResource(publisher)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()))
// 				.when(command)
// 				.expectEvents(new AtomicProductOfferingCategoryDefinedEvent("1", addCategories, OffsetDateTime.now()));

// 	}

// //
// 	@Test
// 	void raiseInvalidCategorySelectedEventWhenProcessDefiningProductOfferingCategoryWithInvalidCategory() {
// 		List<String> categories = new ArrayList<>();
// 		categories.add("BOS_B2B_Classification");

// 		ProductOffering productOffering = new ProductOffering().id("1").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 				.baseType(null).href(null).schemaLocation(null);
// 		String productOffering_type = productOffering.getType().toString();
// 		when(queryService.fetchCategory()).thenReturn(List.of(new Category().id("BOS_B2B_Classification1")
// 				.name("B2B Offer").isRoot(Boolean.TRUE).lifecycleStatus("active").type("AtomicProductOffering")));
// 		when(queryService.fetchCategoryById("BOS_B2B_Classification1"))
// 				.thenReturn((new Category().id("BOS_B2B_Classification1").name("B2B Offer").isRoot(Boolean.TRUE)
// 						.lifecycleStatus("active")).type("AtomicProductOffering"));
// 		DefineProductOfferingCategoryCommand command = new DefineProductOfferingCategoryCommand(aggregateId,
// 				categories);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
// 		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
// 				.name(productSpecification.getName()).href(productSpecification.getHref())
// 				.version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
// 				.schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());

// 		Set<ProductOfferingRef> productOfferings = new HashSet<>();
// 		productOfferings.add(new ProductOfferingRef().id("1").type("AtomicProductOffering").referredType(null)
// 				.baseType(null).href(null).schemaLocation(null));
// 		Set<CategoryRef> addCategories = new HashSet<>();

// 		CategoryRef categoryRef = new CategoryRef().baseType(null).href(null).id("BOS_B2B_Classification")
// 				.schemaLocation(null).type("AtomicProductOffering").version(null).referredType("category");
// 		addCategories.add(categoryRef);
// //
// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		doNothing().when(publisher).project(List.of(new ProductOfferingCategoryAssociationEvent()));
// 		fixture.registerInjectableResource(queryService).registerInjectableResource(publisher)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// //		when(queryService.fetchProductSpecById(anyString()))
// //				.thenReturn(new ProductSpecification().lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
// //		when(queryService.fetchCategory()).thenReturn(
// //				List.of(new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)));
// //
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<String> categories = new ArrayList<>();
// //		categories.add("BOS_Classification");
// //		categories.add("MDT_Classe2Famille");
// //		categories.add("Classif_OCIT");
// //
// //		DefineProductOfferingCategoryCommand command = new DefineProductOfferingCategoryCommand(categories);
// //		try {
// //		productOfferingAggregate.process(command);
// //		}
// //		catch(BosException exception) {
// //		assertEquals("category id not valid",exception.getReason() );

// 	}

// //
// //	}
// //
// 	@Test
// 	void raiseInvalidProductSpecStatusEventWhenProcessDefiningProductOfferingCategory() {

// 		List<String> categories = new ArrayList<>();
// 		categories.add("BOS_B2B_Classification");

// 		ProductOffering productOffering = new ProductOffering().id("1").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 				.baseType(null).href(null).schemaLocation(null);
// 		String productOffering_type = productOffering.getType().toString();
// 		when(queryService.fetchCategory()).thenReturn(List.of(new Category().id("BOS_B2B_Classification")
// 				.name("B2B Offer").isRoot(Boolean.TRUE).lifecycleStatus("active").type("AtomicProductOffering")));
// 		when(queryService.fetchCategoryById("BOS_B2B_Classification"))
// 				.thenReturn((new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)
// 						.lifecycleStatus("active")).type("AtomicProductOffering"));
// 		DefineProductOfferingCategoryCommand command = new DefineProductOfferingCategoryCommand(aggregateId,
// 				categories);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY);
// 		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
// 				.name(productSpecification.getName()).href(productSpecification.getHref())
// 				.version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
// 				.schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());

// 		Set<ProductOfferingRef> productOfferings = new HashSet<>();
// 		productOfferings.add(new ProductOfferingRef().id("1").type("AtomicProductOffering").referredType(null)
// 				.baseType(null).href(null).schemaLocation(null));
// 		Set<CategoryRef> addCategories = new HashSet<>();

// 		CategoryRef categoryRef = new CategoryRef().baseType(null).href(null).id("BOS_B2B_Classification")
// 				.schemaLocation(null).type("AtomicProductOffering").version(null).referredType("category");
// 		addCategories.add(categoryRef);
// //		        CategoryRef categoryRef1 = new CategoryRef().baseType(null).href(null).id("BOS_B2B_Classification")
// //		                .schemaLocation(null).type("AtomicProductOffering")
// //		                .version(null).referredType("category");
// //		        addCategories.add(categoryRef1);
// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		doNothing().when(publisher).project(List.of(new ProductOfferingCategoryAssociationEvent()));
// 		fixture.registerInjectableResource(queryService).registerInjectableResource(publisher)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);
// 		;

// //		when(queryService.fetchProductSpecById(anyString()))
// //				.thenReturn(new ProductSpecification().lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));
// //		when(queryService.fetchCategory()).thenReturn(
// //				List.of(new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)));
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<String> categories = new ArrayList<>();
// //		categories.add("BOS_B2B_Classification");
// //		categories.add("BOS_Classification");
// //		categories.add("MDT_Classe2Famille");
// //		categories.add("Classif_OCIT");
// //
// //		DefineProductOfferingCategoryCommand command = new DefineProductOfferingCategoryCommand(categories);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidProductSpecStatusEvent);
// //		InvalidProductSpecStatusEvent invalidProductSpecStatusEvent = (InvalidProductSpecStatusEvent) eventList.get(0);
// //		assertNotNull(invalidProductSpecStatusEvent.getProductSpecId());
// //		assertNotNull(invalidProductSpecStatusEvent.getResourceState());
// 	}

// //
// 	@Test
// 	void raiseEventsWhenProcessProductOfferingMarketSegment() {
// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		List<String> marketSegments = new ArrayList<>();
// 		marketSegments.add("1266");

// 		ProductOfferingMarketCommand command = new ProductOfferingMarketCommand(this.aggregateId, marketSegments);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()))
// 				.when(command).expectEvents(new AtomicProductOfferingMarketDefinedEvent("1",
// 						List.of(new MarketSegmentRef().id("1266")), OffsetDateTime.now()));

// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// //				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<String> marketSegments = new ArrayList<>();
// //		marketSegments.add("1266");
// //
// //		ProductOfferingMarketCommand command = new ProductOfferingMarketCommand(marketSegments);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingMarketDefinedEvent);
// //		AtomicProductOfferingMarketDefinedEvent event1 = (AtomicProductOfferingMarketDefinedEvent) eventList.get(0);
// //		assertNotNull(event1.getProductOfferingId());
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //
// //		assertNotNull(event1.getMarketSegments());
// //		assertEquals("1266", event1.getMarketSegments().get(0).getId());

// 	}

// //
// 	// @Test
// 	void raiseInvalidEventsWhenProcessProductOfferingMarketSegment() {

// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		List<String> marketSegments = new ArrayList<>();
// 		marketSegments.add("1266");

// 		List<String> marketSegments1 = new ArrayList<>();
// 		marketSegments.add("1267");

// 		ProductOfferingMarketCommand command = new ProductOfferingMarketCommand(this.aggregateId, marketSegments1);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// //		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// //				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)); //
// //		when(queryService.fetchMarketSegments()).thenReturn(List.of(new MarketSegmentRef().id("1266")
// //				.href("https://mycsp.com:8080/tmf-api/productOfferingReferences/v4/marketSegmentRef/1266")
// //				.name("North Region")));
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// //				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate)); //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<String> marketSegments = new ArrayList<>();
// //		marketSegments.add("1266");
// //		marketSegments.add("1267");
// //		ProductOfferingMarketCommand command = new ProductOfferingMarketCommand(marketSegments);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //		assertTrue(eventList.get(0) instanceof InvalidProductOfferingMarketSelectedEvent);
// //		InvalidProductOfferingMarketSelectedEvent event1 = (InvalidProductOfferingMarketSelectedEvent) eventList.get(0);
// //		assertNotNull(event1.getProductOfferingId());
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //		assertNotNull(event1.getInvalidMarkets());
// //		assertEquals("1267", event1.getInvalidMarkets().get(0));
// 	}

// //
// //	@Test
// //	 void
// //
// //			raiseInvalidEventsWhenProcessAtomicProductOfferingCharacteristicsSelectedEvent() {
// //		List<Event> history = new ArrayList<>();
// //		String productSpecId = "productSpecId17";
// //		String productOfferingId = "productOfferingId17";
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		OffsetDateTime dateTime = OffsetDateTime.now();
// //		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));
// //
// //		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
// //				.validFor(validFor).valueFrom("1").valueTo("20");
// //		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
// //		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);
// //
// //		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);
// //
// //		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
// //				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
// //				.validFor(validFor);
// //		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
// //		productSpecificationCharacteristics.add(productSpecificationCharacteristic);
// //
// //		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// //				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor)
// //						.productSpecCharacteristic(productSpecificationCharacteristics));
// //
// //		com.orange.bos.productoffering.pojo.TimePeriod timePeriod = new com.orange.bos.productoffering.pojo.TimePeriod();
// //		timePeriod.setStartDateTime(dateTime.plusDays(1));
// //		timePeriod.endDateTime(dateTime.plusDays(9));
// //
// //		ProductCharValue productCharValue = new ProductCharValue();
// //		productCharValue.setValue("30");
// //		productCharValue.setValidFor(timePeriod);
// //		List<ProductCharValue> productCharValues = new ArrayList<>();
// //		productCharValues.add(productCharValue);
// //
// //		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
// //		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
// //		pickAtomicProductOfferingCharacteristics.setId(productSpecId);
// //		pickAtomicProductOfferingCharacteristics.setMinCardinality(1);
// //		pickAtomicProductOfferingCharacteristics.setMaxCardinality(4);
// //		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
// //		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
// //		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);
// //
// //		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(
// //				pickAtomicProductOfferingCharacteristic);
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //
// //		List<Event> eventList = productOfferingAggregate.process(selectProductOfferingCharacteristicCommand);
// //
// //		assertNotNull(eventList);
// //
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidAtomicProductOfferingCharacteristicsSelectedEvent);
// //		InvalidAtomicProductOfferingCharacteristicsSelectedEvent invalidAtomicProductOfferingCharacteristicsSelectedEvent = (InvalidAtomicProductOfferingCharacteristicsSelectedEvent) eventList
// //				.get(0);
// //		assertEquals("productOfferingId17",
// //				invalidAtomicProductOfferingCharacteristicsSelectedEvent.getProductOfferingId());
// //		String reason = invalidAtomicProductOfferingCharacteristicsSelectedEvent.getCharacteristics().iterator().next()
// //				.getReason();
// //		assertEquals("Characteristic value should match or be within range of Product Spec", reason);
// //
// //	}
// //
// 	@Test
// 	void raiseEventsWhenSelectProductSpecCharacteristicCommandRaised() {
// 		List<Event> history = new ArrayList<>();

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		OffsetDateTime dateTime = OffsetDateTime.now();
// 		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));

// 		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
// 				.validFor(validFor).valueFrom("1").valueTo("20");
// 		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
// 				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
// 				.validFor(validFor);
// 		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
// 		productSpecificationCharacteristics.add(productSpecificationCharacteristic);

// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor)
// 						.productSpecCharacteristic(productSpecificationCharacteristics));

// 		com.orange.bos.productoffering.pojo.TimePeriod timePeriod = new com.orange.bos.productoffering.pojo.TimePeriod();
// 		timePeriod.setStartDateTime(dateTime.plusDays(1));
// 		timePeriod.endDateTime(dateTime.plusDays(9));

// 		ProductCharValue productCharValue = new ProductCharValue();
// 		productCharValue.setValue("10");
// 		productCharValue.setValidFor(timePeriod);
// 		List<ProductCharValue> productCharValues = new ArrayList<>();
// 		productCharValues.add(productCharValue);

// 		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
// 		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
// 		pickAtomicProductOfferingCharacteristics.setId(productSpecId);
// 		pickAtomicProductOfferingCharacteristics.setMinCardinality(0);
// 		pickAtomicProductOfferingCharacteristics.setMaxCardinality(1);
// 		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
// 		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
// 		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

// 		ProductSpecificationCharacteristicValueUse productSpecificationCharacteristicValueUse = new ProductSpecificationCharacteristicValueUse();
// 		productSpecificationCharacteristicValueUse.minCardinality(0).maxCardinality(1)
// 				.validFor(TimePeriodMapper.toGenerated(timePeriod))
// 				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues));
// 		List<ProductSpecificationCharacteristicValueUse> productSpecCharacteristicValueUseList = new ArrayList<>();
// 		productSpecCharacteristicValueUseList.add(productSpecificationCharacteristicValueUse);

// 		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(
// 				"1", pickAtomicProductOfferingCharacteristic);

// 		fixture.registerInjectableResource(queryService).given(history).when(selectProductOfferingCharacteristicCommand)
// 				.expectEvents(new AtomicProductOfferingCharacteristicsDefinedEvent("1",
// 						productSpecCharacteristicValueUseList, OffsetDateTime.now()));
// 	}

// 	@Test
// 	void raiseInvalidProductSpecStatusEventWhenProcessDefiningProductOfferingCharacteristics() {

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		OffsetDateTime dateTime = OffsetDateTime.now();
// 		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));

// 		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
// 				.validFor(validFor).valueFrom("1").valueTo("20");
// 		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
// 				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
// 				.validFor(validFor);
// 		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
// 		productSpecificationCharacteristics.add(productSpecificationCharacteristic);

// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor)
// 						.productSpecCharacteristic(productSpecificationCharacteristics));

// 		com.orange.bos.productoffering.pojo.TimePeriod timePeriod = new com.orange.bos.productoffering.pojo.TimePeriod();
// 		timePeriod.setStartDateTime(dateTime.plusDays(1));
// 		timePeriod.endDateTime(dateTime.plusDays(9));

// 		ProductCharValue productCharValue = new ProductCharValue();
// 		productCharValue.setValue("10");
// 		productCharValue.setValidFor(timePeriod);
// 		List<ProductCharValue> productCharValues = new ArrayList<>();
// 		productCharValues.add(productCharValue);

// 		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
// 		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
// 		pickAtomicProductOfferingCharacteristics.setId(productSpecId);
// 		pickAtomicProductOfferingCharacteristics.setMinCardinality(1);
// 		pickAtomicProductOfferingCharacteristics.setMaxCardinality(4);
// 		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
// 		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
// 		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

// 		ProductSpecificationCharacteristicValueUse productSpecificationCharacteristicValueUse = new ProductSpecificationCharacteristicValueUse();
// 		productSpecificationCharacteristicValueUse.minCardinality(1).maxCardinality(4)
// 				.validFor(TimePeriodMapper.toGenerated(timePeriod))
// 				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues));
// 		List<ProductSpecificationCharacteristicValueUse> productSpecCharacteristicValueUseList = new ArrayList<>();
// 		productSpecCharacteristicValueUseList.add(productSpecificationCharacteristicValueUse);

// 		SelectProductOfferingCharacteristicCommand selectProductOfferingCharacteristicCommand = new SelectProductOfferingCharacteristicCommand(
// 				"1", pickAtomicProductOfferingCharacteristic);

// 		fixture.registerInjectableResource(queryService).given(history).when(selectProductOfferingCharacteristicCommand)
// 				.expectException(BosException.class);
// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingMarketWithInvalidLifecycleStatus() {
// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));

// 		List<Event> history = new ArrayList<>();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		List<String> marketSegments = new ArrayList<>();
// 		marketSegments.add("1266");

// 		ProductOfferingMarketCommand command = new ProductOfferingMarketCommand(this.aggregateId, marketSegments);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);
// 	}

// 	@Test
// 	void raiseEventsWhenProductOfferingChannelIsSelected() {
// 		List<String> channel = new ArrayList<>();
// 		channel.add("channelid1");
// 		// channel.add("channelid2");
// 		SelectProductOfferingChannelCommand command = new SelectProductOfferingChannelCommand(this.aggregateId,
// 				channel);

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);

// 		List<ChannelRef> channelList = new ArrayList<>();
// 		channelList.add(new ChannelRef().id("channelid1"));
// 		// channelList.add(new ChannelRef().id("channelid2"));
// 		// channelList.add(new ChannelRef().id("channelid3").name("mobile"));
// 		when(queryService.fetchChannels()).thenReturn(channelList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()))
// 				.when(command).expectEvents(new AtomicProductOfferingChannelDefinedEvent("1",
// 						List.of(new ChannelRef().id("channelid1")), OffsetDateTime.now()));

// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent("product_spec_id", ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
// //
// //		List<ChannelRef> channelList = new ArrayList<>();
// //		channelList.add(new ChannelRef().id("channelid1").name("web"));
// //		channelList.add(new ChannelRef().id("channelid2").name("ussd"));
// //		channelList.add(new ChannelRef().id("channelid3").name("mobile"));
// //		when(queryService.fetchChannels()).thenReturn(channelList);
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingChannelDefinedEvent);
// //		AtomicProductOfferingChannelDefinedEvent productOfferChannelDefinedEvent = (AtomicProductOfferingChannelDefinedEvent) eventList
// //				.get(0);
// //
// //		assertNotNull(productOfferChannelDefinedEvent);
// //		assertEquals("product_offering_id", productOfferChannelDefinedEvent.getProductOfferingId());
// //		assertEquals(2, productOfferChannelDefinedEvent.getChannel().size());
// 	}

// //
// 	@Test
// 	void raiseInvalidChannelSelectedEventsWhenProductOfferingInvalidChannelIsSelected() {

// 		List<String> channel = new ArrayList<>();
// 		channel.add("channelid1");
// 		// channel.add("channelid2");
// 		SelectProductOfferingChannelCommand command = new SelectProductOfferingChannelCommand(this.aggregateId,
// 				channel);

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);

// 		List<ChannelRef> channelList = new ArrayList<>();
// 		channelList.add(new ChannelRef().id("channelid2"));
// 		// channelList.add(new ChannelRef().id("channelid2"));
// 		// channelList.add(new ChannelRef().id("channelid3").name("mobile"));
// 		when(queryService.fetchChannels()).thenReturn(channelList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// 	}

// //
// 	@Test
// 	void raiseEventWhenProductOfferingRelatedPartyIsSelected() {
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("productSpecId")).thenReturn(new ProductSpecification()
// 				.id("productSpecId").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).relatedParty(List.of(
// 						new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()))));

// 		List<RelatedParty> relatedParties = List
// 				.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));

// 		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(this.aggregateId,
// 				relatedParties);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectEvents(
// 						new AtomicProductOfferingRelatedPartyDefinedEvent("1", relatedParties, OffsetDateTime.now()));

// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// //				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));
// //		history.add(new AtomicProductOfferingCategoryDefinedEvent(productOfferingId,
// //				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// //				OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingMarketDefinedEvent(productOfferingId,
// //				List.of(new MarketSegmentRef().id("1266").name("North Region")), OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingChannelDefinedEvent(productOfferingId,
// //				List.of(new ChannelRef().id("channelid1").name("web")), OffsetDateTime.now()));
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<RelatedParty> relatedParties = List
// //				.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));
// //		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(relatedParties);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingRelatedPartyDefinedEvent);
// //		AtomicProductOfferingRelatedPartyDefinedEvent event1 = (AtomicProductOfferingRelatedPartyDefinedEvent) eventList
// //				.get(0);
// //		assertNotNull(event1.getProductOfferingId());
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //
// //		assertNotNull(event1.getRelatedParties());
// //		assertEquals("SOM1", event1.getRelatedParties().get(0).getId());
// //		assertEquals("saleBy", event1.getRelatedParties().get(0).getRole());
// 	}

// //
// 	// @Test
// 	void raiseInvalidEventWhenProductOfferingRelatedPartyIsSelected() {

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("productSpecId")).thenReturn(new ProductSpecification()
// 				.id("productSpecId").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).relatedParty(List.of(
// 						new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()))));

// 		List<RelatedParty> relatedParties = List
// 				.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));

// 		List<RelatedParty> relatedParties1 = List
// 				.of(new RelatedParty().id("SOM2").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));

// 		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(this.aggregateId,
// 				relatedParties1);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectException(BosInvalidEventException.class);

// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(new ProductSpecification()
// //				.id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).relatedParty(List.of(
// //						new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()))));
// //		List<Event> history = new ArrayList<>();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// //				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingCategoryDefinedEvent(productOfferingId,
// //				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// //				OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingMarketDefinedEvent(productOfferingId,
// //				List.of(new MarketSegmentRef().id("1266").name("North Region")), OffsetDateTime.now()));
// //		history.add(new AtomicProductOfferingChannelDefinedEvent(productOfferingId,
// //				List.of(new ChannelRef().id("channelid1").name("web")), OffsetDateTime.now()));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<RelatedParty> relatedParties = new ArrayList<>();
// //		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(relatedParties);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //		assertTrue(eventList.get(0) instanceof InvalidProductOfferingRelatedPartyEvent);
// //		InvalidProductOfferingRelatedPartyEvent event1 = (InvalidProductOfferingRelatedPartyEvent) eventList.get(0);
// //		assertNotNull(event1.getProductOfferingId()); //
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //		assertNotNull(event1.getInvalidRelatedParties());
// //		assertEquals(relatedParties, event1.getInvalidRelatedParties());
// 	}

// //
// 	@Test
// 	void

// 			raiseInvalidEventWhenProductOfferingRelatedPartyWithInvalidLifecycleStatus() {

// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("productSpecId")).thenReturn(new ProductSpecification()
// 				.id("productSpecId").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).relatedParty(List.of(
// 						new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()))));

// 		List<RelatedParty> relatedParties = List
// 				.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));

// 		List<RelatedParty> relatedParties1 = List
// 				.of(new RelatedParty().id("SOM2").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));

// 		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(this.aggregateId,
// 				relatedParties1);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"),
// 								productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectException(BosInvalidEventException.class);

// //		List<RelatedParty> relatedParties = new ArrayList<>();
// //		ProductOfferingRelatedPartyCommand command = new ProductOfferingRelatedPartyCommand(relatedParties);
// //
// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// //				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));
// //		history.add(new AtomicProductOfferingCategoryDefinedEvent(productOfferingId,
// //				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// //				OffsetDateTime.now()));
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidProductSpecStatusEvent);
// //		InvalidProductSpecStatusEvent InvalidProductSpecStatusEvent = (InvalidProductSpecStatusEvent) eventList.get(0);
// //		assertNotNull(InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertNotNull(InvalidProductSpecStatusEvent.getResourceState());
// //		assertEquals("product_spec_id", InvalidProductSpecStatusEvent.getProductSpecId());
// //		assertTrue(ProductSpecificationLifecycle.ACTIVE != InvalidProductSpecStatusEvent.getResourceState()
// //				&& ProductSpecificationLifecycle.LAUNCHED != InvalidProductSpecStatusEvent.getResourceState());
// 	}

// //
// 	@Test
// 	void raiseEventsWhenProcessDefiningProductOfferingRelationship() {
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess")
// 						.productSpecificationRelationship(new ArrayList<ProductSpecificationRelationship>()));
// 		when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// 		when(queryService.fetchProductOfferingById(anyString()))
// 				.thenReturn(new ProductOffering().id("123").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 						.productSpecification(new ProductSpecificationRef().id("1")));
// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// 		productOfferingRelationship.setId("rel_1");
// 		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.ADD);
// 		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// 		productOfferingRelationships.add(productOfferingRelationship);

// 		ProductOfferingRelationshipCommand command = new ProductOfferingRelationshipCommand("1",
// 				productOfferingRelationships);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingRelationshipDefinedEvent("1",
// 						command.getProductOfferingRelationships(), OffsetDateTime.now()));

// 	}

// 	@Test
// 	void raiseInvalidProductOfferingRelationshipSelectedEventWhenProcessDefiningProductOfferingRelationshipWithInvalidRelationship() {
// 		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// 				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// 		when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(new ProductOffering().id("123")
// 				.productSpecification(new ProductSpecificationRef().id("product_spec_id")));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// 		productOfferingRelationship.setId("rel_1");
// 		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// 		productOfferingRelationships.add(productOfferingRelationship);

// 		ProductOfferingRelationshipCommand command = new ProductOfferingRelationshipCommand("1",
// 				productOfferingRelationships);

// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents();
// 	}

// 	@Test
// 	void raiseInvalidProductSpecStatusEventWhenProcessDefiningProductOfferingRelationship() {
// 		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// 				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// 		when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(new ProductOffering().id("123")
// 				.productSpecification(new ProductSpecificationRef().id("product_spec_id")));
// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// 		productOfferingRelationship.setId("rel_1");
// 		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// 		productOfferingRelationships.add(productOfferingRelationship);

// 		ProductOfferingRelationshipCommand command = new ProductOfferingRelationshipCommand("1",
// 				productOfferingRelationships);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);

// 	}

// 	@Test
// 	void raiseEventsWhenProductOfferingTermCommandIsSelected() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingTermCommand command = new ProductOfferingTermCommand("1",
// 				List.of(new ProductOfferingTerm().duration(new Quantity().amount(12f).units("day"))
// 						.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10)))));
// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new AtomicProductOfferingTermDefinedEvent("1", command.getProductOfferingTerm(), lastUpdate),
// 				new BundleProductOfferingTermDefinedEvent("1", command.getProductOfferingTerm(), lastUpdate));

// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWhenEndDateIsAfterRestriction() {

// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().startDateTime(current).endDateTime(current.plusDays(11)));
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);

// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWhenStartDateIsBeforeRestriction() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().startDateTime(current.plusDays(-1)).endDateTime(current.plusDays(10)));
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWithNoEndDateTime() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().startDateTime(current));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWithNoStartDateTime() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().endDateTime(current.plusDays(10)));
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);

// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWithNoInput() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1", null);
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWithNoTimePeriod() {
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(null));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1", null);
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForCommandIsSelectedWithStartDateAfterEndDate() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		when(queryService.fetchProductSpecById("1")).thenReturn(new ProductSpecification().id("1")
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).description("Mobile Access")
// 				.brand("MobileAccess").name("MobileAccess")
// 				.relatedParty(List
// 						.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString())))
// 				.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().startDateTime(current.plusDays(10)).endDateTime(current));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);

// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidForWithInvalidLifecycleStatus() {
// 		OffsetDateTime current = OffsetDateTime.now();
// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productSpecId, "1", ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingValidForCommand command = new ProductOfferingValidForCommand("1",
// 				new TimePeriod().startDateTime(current.plusDays(10)).endDateTime(current));

// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseEventsWhenProductOfferingValidatedCommandIsSelected() {
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		List<String> categoriesId = new ArrayList<String>();
// 		categoriesId.add("BOS_B2B_Classification");

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", null, null,
// 				ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// //     history.add(new AtomicProductOfferingChannelDefinedEvent( "1", List.of(new ChannelRef().id("channelid1")),
// //                  OffsetDateTime.now())
// 		// );

// 		ProductOfferingValidatedCommand command = new ProductOfferingValidatedCommand("1");
// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new AtomicProductOfferingBundleDefinedEvent("1", false, lastUpdate),
// 				new AtomicProductOfferingValidatedEvent("1", ProductOfferingLifecycle.INTEST, lastUpdate),
// 				new AtomicProductOfferingVersionCreatedEvent("1", "0.1.0", lastUpdate),
// 				new AtomicProductOfferingCreationCompletedEvent("1",
// 						new ProductOffering().id("1").description("Mobile Access").isBundle(false).isInstallable(true)
// 								.lifecycleStatus(ProductOfferingLifecycle.INTEST).statusReason("status_reason")
// 								.version("0.10").productSpecification(new ProductSpecificationRef().id("1"))
// 								.category(Set.of(new CategoryRef().id("BOS_B2B_Classification")
// 										.name("BOS_B2B_Classification")))),
// 				new ProductOfferingInternalProjectionEvent("1", categoriesId));

// 	}

// 	@Test
// 	void raiseInvalidEventWhenProductOfferingValidatedWithInvalidLifecycleStatus() {
// 		ProductOfferingValidatedCommand command = new ProductOfferingValidatedCommand("1");

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosInvalidEventException.class);
// 	}

// 	@Test
// 	void raiseEventWhenProcessProductSpecificationOperation() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		CommercialOperation operationSpecification = new CommercialOperation();
// 		operationSpecification.id("1").name("Create").validFor(new TimePeriod().startDateTime(lastUpdate));
// 		List<CommercialOperation> operationSpecificationList = new ArrayList<>();
// 		operationSpecificationList.add(operationSpecification);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.operationSpecification(List.of(new OperationSpecification().id("1").name("Create")
// 								.validFor(new TimePeriod().startDateTime(lastUpdate)))));

// 		ProductOfferingOperationCommand command = new ProductOfferingOperationCommand("1", operationSpecificationList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectEvents(new AtomicProductOfferingOperDefinedEvent("1", operationSpecificationList,
// 						OffsetDateTime.now()));

// 	}

// //
// 	@Test
// 	void raiseEventWhenProcessInvalidProductSpecificationOperationSelected() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		CommercialOperation operationSpecification = new CommercialOperation();
// 		operationSpecification.id("1").name("Create").validFor(new TimePeriod().startDateTime(lastUpdate));
// 		List<CommercialOperation> operationSpecificationList = new ArrayList<>();
// 		operationSpecificationList.add(operationSpecification);
// 		List<CommercialOperation> operationSpecificationList1 = new ArrayList<>();
// 		CommercialOperation operationSpecification1 = new CommercialOperation();
// 		operationSpecification1.id("2").name("Createtest").validFor(new TimePeriod().startDateTime(lastUpdate));
// 		operationSpecificationList1.add(operationSpecification1);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.operationSpecification(List.of(new OperationSpecification().id("1").name("Create")
// 								.validFor(new TimePeriod().startDateTime(lastUpdate)))));

// 		ProductOfferingOperationCommand command = new ProductOfferingOperationCommand("1", operationSpecificationList1);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// 	}

// 	@Test

// 	@DisplayName(value = "Product Offering Cancel Event Test")
// 	void raiseEventWhenProcessProductOffCancelTest() {
// 		List<Event> history = new ArrayList<>();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 				ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));
// 		ProductOffCancelCommand cancelCommand = new ProductOffCancelCommand("1");

// 		fixture.registerInjectableResource(queryService).given(history).when(cancelCommand)
// 				.expectEvents(new ProductOffCancelledEvent("1", new ProductOffering()));

// 	}

// 	@Test

// 	@DisplayName(value = "Invalid Product Off. Cancel Event Test")
// 	void raiseInvalidEventWhenProcessProductOffCancelTest() {

// 		List<Event> history = new ArrayList<>();
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));
// 		ProductOffCancelCommand cancelCommand = new ProductOffCancelCommand("1");

// 		fixture.registerInjectableResource(queryService).given(history).when(cancelCommand)
// 				.expectException(BosInvalidEventException.class);

// 	}

// 	@Test
// 	void raiseEventWhenAssociatePOPtoOperationSpec() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();

// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		CommercialOperation operation1 = new CommercialOperation().id("operation_spec_id");

// 		List<CommercialOperation> operationHistory = new ArrayList<>();
// 		operationHistory.add(operation1);

// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		AssociatePOPtoOperationSpecificationCommand command = new AssociatePOPtoOperationSpecificationCommand("1",
// 				associatePOPtoOperationSpecList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingOperDefinedEvent("1", operationHistory, OffsetDateTime.now()))
// 				.when(command).expectEvents(new LinkPOPtoOperEvent("1", operationHistory, OffsetDateTime.now()));

// 	}

// 	@Test
// 	void raiseEventWhenPopIsNull() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		AssociatePOPtoOperationSpecificationCommand command = new AssociatePOPtoOperationSpecificationCommand("1",
// 				associatePOPtoOperationSpecList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingOperDefinedEvent("1", new ArrayList<>(), OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// 	}

// 	@Test
// 	void raiseEventWhenAssociatePOPtoOperationSpecInvalid() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operationspecid");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		AssociatePOPtoOperationSpecificationCommand command = new AssociatePOPtoOperationSpecificationCommand("1",
// 				associatePOPtoOperationSpecList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingOperDefinedEvent("1", new ArrayList<>(), OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);
// //		List<Event> history = new ArrayList<>();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// //				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// //				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// //				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// //				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// //				.schemaLocation(null));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidLinkPOPtoOperEvent);
// //		assertEquals(productOfferingId, ((InvalidLinkPOPtoOperEvent) eventList.get(0)).getProductOfferingId());
// 	}

// //
// 	@Test
// 	void raiseEventWhenInvalidPOPStatusSelected() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		AssociatePOPtoOperationSpecificationCommand command = new AssociatePOPtoOperationSpecificationCommand("1",
// 				associatePOPtoOperationSpecList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingOperDefinedEvent("1", new ArrayList<>(), OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// 	}

// 	@Test
// 	void raiseEventWhenInvalidProductSpecStatusSelected() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));

// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		AssociatePOPtoOperationSpecificationCommand command = new AssociatePOPtoOperationSpecificationCommand("1",
// 				associatePOPtoOperationSpecList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingOperDefinedEvent("1", new ArrayList<>(), OffsetDateTime.now()))
// 				.when(command).expectException(BosException.class);

// //		List<Event> history = new ArrayList<>();
// //		history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// //				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// //				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE)
// //				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// //				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// //				.schemaLocation(null));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidProductSpecStatusEvent);
// //		assertEquals(productSpecId, ((InvalidProductSpecStatusEvent) eventList.get(0)).getProductSpecId());
// 	}

// //	@Test
// //	 void raiseProductOfferingDeleteEventTest() {
// //		List<Event> history = new ArrayList<>();
// //		ProductOfferingDeleteCommand command = new ProductOfferingDeleteCommand(OffsetDateTime.now(), 40L, "HOURS");
// //
// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertEquals(1, eventList.size());
// //		assertTrue(eventList.get(0) instanceof ProductOfferingDeleteEvent);
// //	}
// //
// 	@Test
// 	void modifyProductOfferingDescription() {
// 		ModifyProductOfferingDescriptionCommand command = new ModifyProductOfferingDescriptionCommand("1",
// 				"Mobile Access", "MobileAccess", "status_reason", "MobileAccess",
// 				ProductOfferingType.ATOMICPRODUCTOFFERING, true);

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, lastUpdate,
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", "product_spec_id", ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("product_spec_id"), "1",
// 				ProductOfferingLifecycle.ACTIVE, lastUpdate));

// 		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// 				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingDescribedModifiedEvent("1", "Mobile Access", "status_reason",
// 						"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));

// //		ProductOfferingAggregate productOfferingAggregate = new ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// //						.description("Mobile Access").brand("MobileAccess").name("MobileAccess"));
// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingDescribedModifiedEvent);
// //		AtomicProductOfferingDescribedModifiedEvent productOfferingDescribedEvent = (AtomicProductOfferingDescribedModifiedEvent) eventList
// //				.get(0);
// //		assertNotNull(productOfferingDescribedEvent.getProductOfferingId());
// //		assertEquals(productOfferingId, productOfferingDescribedEvent.getProductOfferingId());
// //
// //		assertNotNull(productOfferingDescribedEvent.getDescription());
// //		assertEquals("Mobile Access", productOfferingDescribedEvent.getDescription());
// //
// //		assertNotNull(productOfferingDescribedEvent.getBrand());
// //		assertEquals("MobileAccess", productOfferingDescribedEvent.getBrand());
// //
// //		assertNotNull(productOfferingDescribedEvent.getName());
// //		assertEquals("MobileAccess", productOfferingDescribedEvent.getName());
// //
// //		assertNotNull(productOfferingDescribedEvent.getStatusReason());
// //		assertEquals("status_reason", productOfferingDescribedEvent.getStatusReason());
// //
// //		assertNotNull(productOfferingDescribedEvent.getType());
// //		assertEquals(ProductOfferingType.ATOMICPRODUCTOFFERING, productOfferingDescribedEvent.getType());

// 	}

// 	@Test
// 	void modifyProductOfferingCategory() {
// 		Set<CategoryRef> categoryRefs = new HashSet<CategoryRef>();
// 		categoryRefs.add(new CategoryRef().id("BOS_B2B_Classification").name("B2B Offer"));
// 		when(queryService.fetchProductSpecById(anyString()))
// 				.thenReturn(new ProductSpecification().lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
// 		when(queryService.fetchCategory()).thenReturn(
// 				List.of(new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)));
// 		when(queryService.fetchProductOfferingById(productOfferingId))
// 				.thenReturn(new ProductOffering().id(productOfferingId).type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 						.category(Set.of(new CategoryRef().id("BOS_B2B_Classification").name("B2B Offer"))));
// 		when(queryService.fetchCategoryEntityById(productOfferingId))
// 				.thenReturn(new CategoryEntityRelationship().id(productOfferingId).categories(categoryRefs));
// 		when(queryService.fetchCategoryById("BOS_B2B_Classification"))
// 				.thenReturn(new Category().id("BOS_B2B_Classification").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("BOS_Classification"))
// 				.thenReturn(new Category().id("BOS_Classification").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("MDT_Classe2Famille"))
// 				.thenReturn(new Category().id("MDT_Classe2Famille").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("Classif_OCIT"))
// 				.thenReturn(new Category().id("Classif_OCIT").name("B2B Offer").lifecycleStatus("active"));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.ACTIVE, lastUpdate,
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.ACTIVE, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		List<String> categories = new ArrayList<>();
// 		categories.add("BOS_B2B_Classification");
// //		categories.add("BOS_Classification");
// //		categories.add("MDT_Classe2Famille");
// //		categories.add("Classif_OCIT");

// 		ModifyProductOfferingCategoryCommand command = new ModifyProductOfferingCategoryCommand(productOfferingId,
// 				categories, true);
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(4, eventList.size());
// 		doNothing().when(publisher).project(List.of(new ProductOfferingCategoryAssociationEvent()));
// 		fixture.registerInjectableResource(queryService).registerInjectableResource(publisher).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingCategoryModifiedEvent(productOfferingId, categoryRefs,
// 						categoryRefs, OffsetDateTime.now()));

// 	}

// 	@Test
// 	void raiseInvalidModifyingProductOfferingCategory() {
// 		Set<CategoryRef> categoryRefs = new HashSet<CategoryRef>();
// 		categoryRefs.add(new CategoryRef().id("BOS_B2B_Classification").name("B2B Offer"));

// 		ProductOffering productOffering = new ProductOffering();
// 		productOffering.id(productOfferingId).type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 				.category(Set.of(new CategoryRef().id("BOS_B2B_Classification").name("B2B Offer")));

// 		Set<ProductOfferingRef> productOfferings = new HashSet<>();
// 		productOfferings.add(new ProductOfferingRef().id(productOffering.getId())
// 				.type(productOffering.getType().getValue()).referredType(null).baseType(productOffering.getBaseType())
// 				.href(productOffering.getHref()).schemaLocation(productOffering.getSchemaLocation()));

// 		when(queryService.fetchProductSpecById(anyString()))
// 				.thenReturn(new ProductSpecification().lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));
// 		when(queryService.fetchCategory()).thenReturn(
// 				List.of(new Category().id("BOS_B2B_Classification").name("B2B Offer").isRoot(Boolean.TRUE)));
// 		when(queryService.fetchProductOfferingById(productOfferingId)).thenReturn(productOffering);
// 		when(queryService.fetchCategoryEntityById(productOfferingId))
// 				.thenReturn(new CategoryEntityRelationship().id(productOfferingId).categories(categoryRefs));
// 		when(queryService.fetchCategoryById("BOS_B2B_Classification"))
// 				.thenReturn(new Category().id("BOS_B2B_Classification").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("BOS_Classification"))
// 				.thenReturn(new Category().id("BOS_Classification").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("MDT_Classe2Famille"))
// 				.thenReturn(new Category().id("MDT_Classe2Famille").name("B2B Offer").lifecycleStatus("active"));
// 		when(queryService.fetchCategoryById("Classif_OCIT"))
// 				.thenReturn(new Category().id("Classif_OCIT").name("B2B Offer").lifecycleStatus("active"));
// 		doNothing().when(modifyCategoryServiceImpl).modifyAssociatedEntity("BOS_B2B_Classification", productOfferings,
// 				false);

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		List<String> categories = new ArrayList<>();
// 		categories.add("BOS_B2BB_Classification");
// //		categories.add("BOS_Classification");
// //		categories.add("MDT_Classe2Famille");
// //		categories.add("Classif_OCIT");
// 		doNothing().when(publisher).project(List.of(new ProductOfferingCategoryAssociationEvent()));
// 		ModifyProductOfferingCategoryCommand command = new ModifyProductOfferingCategoryCommand(productOfferingId,
// 				categories, true);
// 		fixture.registerInjectableResource(queryService).registerInjectableResource(publisher).given(history).when(command)
// 				.expectException(BosException.class);

// 	}

// 	@Test
// 	void modifyProductOfferingChannelIsSelected() {
// 		List<String> channel = new ArrayList<>();
// 		channel.add("channelid1");
// 		channel.add("channelid2");
// 		ModifyProductOfferingChannelCommand command = new ModifyProductOfferingChannelCommand(productOfferingId,
// 				channel);

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("productOfferingId", "product_spec_id",
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// 				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		List<ChannelRef> channelList = new ArrayList<>();
// 		channelList.add(new ChannelRef().id("channelid1").name("web"));
// 		channelList.add(new ChannelRef().id("channelid2").name("ussd"));
// 		// channelList.add(new ChannelRef().id("channelid3").name("mobile"));
// 		when(queryService.fetchChannels()).thenReturn(channelList);

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());

// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new AtomicProductOfferingChannelModifiedEvent(productOfferingId, channelList, OffsetDateTime.now()));

// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingChannelModifiedEvent);
// //		AtomicProductOfferingChannelModifiedEvent productOfferChannelDefinedEvent = (AtomicProductOfferingChannelModifiedEvent) eventList
// //				.get(0);
// //
// //		assertNotNull(productOfferChannelDefinedEvent);
// //		assertEquals("product_offering_id", productOfferChannelDefinedEvent.getProductOfferingId());
// //		assertEquals(2, productOfferChannelDefinedEvent.getChannel().size());
// 	}

// //	@Test
// //	 void raiseInvalidEventWhenModifyingProductOfferingInvalidChannel() {
// //		List<String> channel = new ArrayList<>();
// //		channel.add("channelid1");
// //		channel.add("invalid_channelid");
// //		List<String> channel1 = new ArrayList<>();
// //		channel.add("channelid2");
// //		channel.add("invalid_channelid");
// //		ModifyProductOfferingChannelCommand command = new ModifyProductOfferingChannelCommand(productOfferingId,channel1);

// //		List<Event> history = new ArrayList<>();
// //		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 	// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// //		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// //				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// //		history.add(new ProductSpecStateVerifiedEvent(productOfferingId,"product_spec_id", ProductSpecificationLifecycle.INSTUDY));
// //		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// //				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// //		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// //				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 	// ProductOfferingAggregate productOfferingAggregate = new
// 	// ProductOfferingAggregate(history, queryService);
// //		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(
// //				new ProductSpecification().id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.INSTUDY));

// //		List<ChannelRef> channelList = new ArrayList<>();
// //		channelList.add(new ChannelRef().id("channelid3"));
// //		channelList.add(new ChannelRef().id("channel1"));
// 	// channelList.add(new ChannelRef().id("channelid3").name("mobile"));
// 	// when(queryService.fetchChannels()).thenReturn(channelList);

// //		fixture.registerInjectableResource(queryService).given(history)
// //		 .when(command).
// //		  expectException(BosException.class);

// 	// List<Event> eventList = productOfferingAggregate.process(command);
// 	// assertNotNull(eventList);
// 	// assertEquals(1, eventList.size());

// //		assertTrue(eventList.get(0) instanceof InvalidProductOfferingChannelSelectedEvent);
// //		InvalidProductOfferingChannelSelectedEvent productOfferInvalidChannelEvent = (InvalidProductOfferingChannelSelectedEvent) eventList
// //				.get(0);

// //		assertNotNull(productOfferInvalidChannelEvent);
// //		assertEquals("product_offering_id", productOfferInvalidChannelEvent.getProductOfferingId());
// //		assertEquals(1, productOfferInvalidChannelEvent.getInvalidChannel().size());
// //		assertEquals("invalid_channelid", productOfferInvalidChannelEvent.getInvalidChannel().get(0));
// 	// }

// 	@Test
// 	void modifyProductOfferingMarketSegment() {
// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// 				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		List<String> marketSegments = new ArrayList<>();
// 		marketSegments.add("1266");

// 		ModifyProductOfferingMarketCommand command = new ModifyProductOfferingMarketCommand(productOfferingId,
// 				marketSegments);
// 		// List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());

// 		// assertTrue(eventList.get(0) instanceof
// 		// AtomicProductOfferingMarketModifiedEvent);
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingMarketModifiedEvent(productOfferingId,
// 						List.of(new MarketSegmentRef().id("1266")), OffsetDateTime.now()));
// //		AtomicProductOfferingMarketModifiedEvent event1 = (AtomicProductOfferingMarketModifiedEvent) eventList.get(0);
// //		assertNotNull(event1.getProductOfferingId());
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //
// //		assertNotNull(event1.getMarketSegments());
// //		assertEquals("1266", event1.getMarketSegments().get(0).getId());

// 	}

// 	@Test
// 	void modifyProductOfferingRelatedParty() {
// 		when(queryService.fetchProductSpecById("product_spec_id")).thenReturn(new ProductSpecification()
// 				.id("product_spec_id").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).relatedParty(List.of(
// 						new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()))));

// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
// 				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, lastUpdate));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent(productOfferingId,
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingMarketDefinedEvent(productOfferingId,
// 				List.of(new MarketSegmentRef().id("1266").name("North Region")), OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent(productOfferingId,
// 				List.of(new ChannelRef().id("channelid1").name("web")), OffsetDateTime.now()));

// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		List<RelatedParty> relatedParties = List
// 				.of(new RelatedParty().id("SOM1").role("saleBy").referredType(PartyType.INDIVIDUAL.toString()));
// 		ModifyProductOfferingRelatedPartyCommand command = new ModifyProductOfferingRelatedPartyCommand(
// 				productOfferingId, relatedParties);
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingRelatedPartyModifiedEvent(productOfferingId, relatedParties,
// 						OffsetDateTime.now()));
// 		// List<Event> eventList = productOfferingAggregate.process(command);

// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());

// 		// assertTrue(eventList.get(0) instanceof
// 		// AtomicProductOfferingRelatedPartyModifiedEvent);
// 		// AtomicProductOfferingRelatedPartyModifiedEvent event1 =
// 		// (AtomicProductOfferingRelatedPartyModifiedEvent) eventList
// 		// .get(0);
// //		assertNotNull(event1.getProductOfferingId());
// //		assertEquals(productOfferingId, event1.getProductOfferingId());
// //
// //		assertNotNull(event1.getRelatedParties());
// //		assertEquals("SOM1", event1.getRelatedParties().get(0).getId());
// //		assertEquals("saleBy", event1.getRelatedParties().get(0).getRole());
// 	}

// 	@Test
// 	void modifyAssociatePOPtoOperationSpec() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// //		AssociatePOPtoOperationSpec associatePOPtoOperationSpec2 = new AssociatePOPtoOperationSpec();
// //		associatePOPtoOperationSpec2.setProductOfferingPriceId("product_offering_price_id2");
// //		associatePOPtoOperationSpec2.setOperationSpecId("operation_spec_id");
// //		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec2);
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);
// //		AssociatePOPtoOperationSpec associatePOPtoOperationSpec1 = new AssociatePOPtoOperationSpec();
// //		associatePOPtoOperationSpec1.setProductOfferingPriceId("product_offering_price_id1");
// //		associatePOPtoOperationSpec1.setOperationSpecId("operation_spec_id");
// //		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec1);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		ModifyAssociatePOPtoOperationSpecificationCommand command = new ModifyAssociatePOPtoOperationSpecificationCommand(
// 				productOfferingId, associatePOPtoOperationSpecList);
// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		CommercialOperation operation1 = new CommercialOperation().id("operation_spec_id");
// 		// CommercialOperation operation2 = new
// 		// CommercialOperation().id("operation_spec_id1");
// 		List<CommercialOperation> operationHistory = new ArrayList<>();
// 		operationHistory.add(operation1);
// 		// operationHistory.add(operation2);
// 		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId, operationHistory, lastUpdate));

// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));
// //		when(queryService.getProductOfferingPrice("product_offering_price_id1")).thenReturn(new ProductOfferingPrice()
// //				.id("product_offering_price_id1").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// //				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// //				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// //				.schemaLocation(null));
// //		when(queryService.getProductOfferingPrice("product_offering_price_id2")).thenReturn(new ProductOfferingPrice()
// //				.id("product_offering_price_id2").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// //				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// //				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// //				.schemaLocation(null));

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());

// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new LinkPOPtoOperModifiedEvent(productOfferingId, operationHistory, OffsetDateTime.now()));

// //		assertTrue(eventList.get(0) instanceof LinkPOPtoOperModifiedEvent);
// //		LinkPOPtoOperModifiedEvent linkPOPtoOperEvent = (LinkPOPtoOperModifiedEvent) eventList.get(0);
// //
// //		assertNotNull(linkPOPtoOperEvent.getOperationList());
// //		assertEquals(2, linkPOPtoOperEvent.getOperationList().size());
// //		assertEquals("operation_spec_id1", linkPOPtoOperEvent.getOperationList().get(0).getId());
// //		assertEquals("product_offering_price_id2",
// //				linkPOPtoOperEvent.getOperationList().get(0).getCarries().get(0).getId());
// //		assertEquals("product_offering_price_id1",
// //				linkPOPtoOperEvent.getOperationList().get(1).getCarries().get(1).getId());
// //		assertEquals("operation_spec_id", linkPOPtoOperEvent.getOperationList().get(1).getId());
// //		assertEquals("product_offering_price_id",
// //				linkPOPtoOperEvent.getOperationList().get(1).getCarries().get(0).getId());
// 	}

// 	@Test
// 	void raiseEventModifyWhenAssociatePoPIsNull() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		ModifyAssociatePOPtoOperationSpecificationCommand command = new ModifyAssociatePOPtoOperationSpecificationCommand(
// 				productOfferingId, associatePOPtoOperationSpecList);
// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));

// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(null);

// 		// List<CommercialOperation> operationHistory = new ArrayList<>();
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());

// //		assertTrue(eventList.get(0) instanceof InvalidPopIdSelectedEvent);
// //		assertEquals(productOfferingId, ((InvalidPopIdSelectedEvent) eventList.get(0)).getProdOfferingId());
// 	}

// 	@Test
// 	void raiseEventWhenModifyAssociatePOPtoOperationSpecInvalid() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operationspecid");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		ModifyAssociatePOPtoOperationSpecificationCommand command = new ModifyAssociatePOPtoOperationSpecificationCommand(
// 				productOfferingId, associatePOPtoOperationSpecList);
// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		// List<CommercialOperation> operationHistory = new ArrayList<>();
// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidLinkPOPtoOperEvent);
// //		assertEquals(productOfferingId, ((InvalidLinkPOPtoOperEvent) eventList.get(0)).getProductOfferingId());
// 	}

// //
// 	@Test
// 	void raiseEventWhenModifyInvalidPOPStatusSelected() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
// 		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
// 		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
// 		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
// 		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(new ProductSpecification().id(productSpecId)
// 				.operationSpecification(List.of(new OperationSpecification().id("operation_spec_id")))
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));

// 		ModifyAssociatePOPtoOperationSpecificationCommand command = new ModifyAssociatePOPtoOperationSpecificationCommand(
// 				productOfferingId, associatePOPtoOperationSpecList);
// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);
// 		when(queryService.getProductOfferingPrice("product_offering_price_id")).thenReturn(new ProductOfferingPrice()
// 				.id("product_offering_price_id").lifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE)
// 				.lastUpdate(lastUpdate).description("POP Charge").name("POPC")
// 				.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE).href(null).baseType("ProdOfferingPrice")
// 				.schemaLocation(null));

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof InvalidPOPStatusSelectedEvent);
// //		assertEquals(productOfferingId, ((InvalidPOPStatusSelectedEvent) eventList.get(0)).getProductOfferingId());
// 	}

// 	@Test
// 	void raiseEventWhenModifyTermCommand() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, lastUpdate));
// 		history.add(new AtomicProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.ATOMICPRODUCTOFFERING, false, lastUpdate));
// 		ProductOfferingTerm term = new ProductOfferingTerm();
// 		term.setName("cs1");
// 		term.description("cs");
// 		List<ProductOfferingTerm> productOfferingTerm = new ArrayList<>();
// 		productOfferingTerm.add(term);
// 		ModifyProductOfferingTermCommand command = new ModifyProductOfferingTermCommand(productOfferingId,
// 				productOfferingTerm);
// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new AtomicProductOfferingTermModifiedEvent(productOfferingId, productOfferingTerm, lastUpdate));

// 	}

// 	@Test
// 	void raiseEventshenModifyProductOfferingCharacteristicCommandRaised() {
// 		List<Event> history = new ArrayList<>();

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		OffsetDateTime dateTime = OffsetDateTime.now();
// 		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));

// 		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
// 				.validFor(validFor).valueFrom("1").valueTo("20");
// 		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
// 				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
// 				.validFor(validFor);
// 		List<ProductSpecificationCharacteristic> productSpecificationCharacteristicsList = new ArrayList<>();
// 		productSpecificationCharacteristicsList.add(productSpecificationCharacteristic);

// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(
// 				new ProductOffering().id("1").productSpecification(new ProductSpecificationRef().id(productSpecId)));

// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor)
// 						.productSpecCharacteristic(productSpecificationCharacteristicsList));

// 		com.orange.bos.productoffering.pojo.TimePeriod timePeriod = new com.orange.bos.productoffering.pojo.TimePeriod();
// 		timePeriod.setStartDateTime(dateTime.plusDays(1));
// 		timePeriod.endDateTime(dateTime.plusDays(9));

// 		ProductCharValue productCharValue = new ProductCharValue();
// 		productCharValue.setValue("10");
// 		productCharValue.setValidFor(timePeriod);
// 		List<ProductCharValue> productCharValues = new ArrayList<>();
// 		productCharValues.add(productCharValue);

// 		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
// 		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
// 		pickAtomicProductOfferingCharacteristics.setId(productSpecId);
// 		pickAtomicProductOfferingCharacteristics.setMinCardinality(0);
// 		pickAtomicProductOfferingCharacteristics.setMaxCardinality(1);
// 		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
// 		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
// 		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

// 		ProductSpecificationCharacteristicValueUse productSpecificationCharacteristicValueUse = new ProductSpecificationCharacteristicValueUse();
// 		productSpecificationCharacteristicValueUse.minCardinality(0).maxCardinality(1)
// 				.validFor(TimePeriodMapper.toGenerated(timePeriod))
// 				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues));
// 		List<ProductSpecificationCharacteristicValueUse> productSpecCharacteristicValueUseList = new ArrayList<>();
// 		productSpecCharacteristicValueUseList.add(productSpecificationCharacteristicValueUse);

// 		ModifyProductOfferingCharacteristicCommand modifyProductOfferingCharacteristicCommand = new ModifyProductOfferingCharacteristicCommand(
// 				"1", pickAtomicProductOfferingCharacteristic);

// 		fixture.registerInjectableResource(queryService).given(history).when(modifyProductOfferingCharacteristicCommand)
// 				.expectEvents(new AtomicProductOfferingCharacteristicsModifiedEvent("1",
// 						productSpecCharacteristicValueUseList, OffsetDateTime.now()));
// 	}

// 	@Test
// 	void raiseEventshenModifyInvalidProductOfferingCharacteristicCommandRaised() {
// 		List<Event> history = new ArrayList<>();

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		OffsetDateTime dateTime = OffsetDateTime.now();
// 		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));

// 		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
// 				.validFor(validFor).valueFrom("1").valueTo("20");
// 		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

// 		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
// 				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
// 				.validFor(validFor);
// 		List<ProductSpecificationCharacteristic> productSpecificationCharacteristicsList = new ArrayList<>();
// 		productSpecificationCharacteristicsList.add(productSpecificationCharacteristic);

// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(
// 				new ProductOffering().id("1").productSpecification(new ProductSpecificationRef().id(productSpecId)));

// 		when(queryService.fetchProductSpecById(anyString())).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor)
// 						.productSpecCharacteristic(productSpecificationCharacteristicsList));

// 		com.orange.bos.productoffering.pojo.TimePeriod timePeriod = new com.orange.bos.productoffering.pojo.TimePeriod();
// 		timePeriod.setStartDateTime(dateTime.plusDays(1));
// 		timePeriod.endDateTime(dateTime.plusDays(9));

// 		ProductCharValue productCharValue = new ProductCharValue();
// 		productCharValue.setValue("10");
// 		productCharValue.setValidFor(timePeriod);
// 		List<ProductCharValue> productCharValues = new ArrayList<>();
// 		productCharValues.add(productCharValue);

// 		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
// 		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
// 		pickAtomicProductOfferingCharacteristics.setId("123");
// 		pickAtomicProductOfferingCharacteristics.setMinCardinality(0);
// 		pickAtomicProductOfferingCharacteristics.setMaxCardinality(1);
// 		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
// 		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
// 		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

// 		ProductSpecificationCharacteristicValueUse productSpecificationCharacteristicValueUse = new ProductSpecificationCharacteristicValueUse();
// 		productSpecificationCharacteristicValueUse.minCardinality(0).maxCardinality(1)
// 				.validFor(TimePeriodMapper.toGenerated(timePeriod))
// 				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues));
// 		List<ProductSpecificationCharacteristicValueUse> productSpecCharacteristicValueUseList = new ArrayList<>();
// 		productSpecCharacteristicValueUseList.add(productSpecificationCharacteristicValueUse);

// 		ModifyProductOfferingCharacteristicCommand modifyProductOfferingCharacteristicCommand = new ModifyProductOfferingCharacteristicCommand(
// 				"1", pickAtomicProductOfferingCharacteristic);

// 		fixture.registerInjectableResource(queryService).given(history).when(modifyProductOfferingCharacteristicCommand)
// 				.expectException(BosException.class);
// 	}

// 	@Test
// 	void raiseEventsWhenmodifyProductOfferingRelationship() {
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess")
// 						.productSpecificationRelationship(new ArrayList<ProductSpecificationRelationship>()));
// 		when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// 		when(queryService.fetchProductOfferingById(anyString()))
// 				.thenReturn(new ProductOffering().id("123").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 						.productSpecification(new ProductSpecificationRef().id("1")));
// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// 		productOfferingRelationship.setId("rel_1");
// 		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.ADD);
// 		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// 		productOfferingRelationships.add(productOfferingRelationship);

// 		ModifyProductOfferingRelationshipCommand command = new ModifyProductOfferingRelationshipCommand("1",
// 				productOfferingRelationships);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectEvents(new AtomicProductOfferingRelationshipModifiedEvent("1",
// 						command.getProductOfferingRelationships(), OffsetDateTime.now()));

// 	}

// 	@Test
// 	void raiseEventsWhenmodifyInvalidProductOfferingRelationshipType() {
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess")
// 						.productSpecificationRelationship(new ArrayList<ProductSpecificationRelationship>()));
// 		when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// 		when(queryService.fetchProductOfferingById(anyString()))
// 				.thenReturn(new ProductOffering().id("123").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// 						.productSpecification(new ProductSpecificationRef().id("1")));
// 		List<Event> history = new ArrayList<>();
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();

// 		history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 				ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), productOfferingId,
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 				"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingCategoryDefinedEvent("1",
// 				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 				OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 				OffsetDateTime.now()));

// 		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// 		productOfferingRelationship.setId("rel_1");
// 		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// 		productOfferingRelationships.add(productOfferingRelationship);

// 		ModifyProductOfferingRelationshipCommand command = new ModifyProductOfferingRelationshipCommand("1",
// 				productOfferingRelationships);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);

// 	}

// //	@Test
// //	 void raiseEventsWhenModifyEmptyProductOffering() {
// //		   when(queryService.fetchProductSpecById("1"))
// //           .thenReturn(new ProductSpecification().id("12").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// //                        .description("Mobile Access").brand("MobileAccess").name("MobileAccess")
// //                         .productSpecificationRelationship(new ArrayList<ProductSpecificationRelationship>()));
// ////when(queryService.fetchProductOffering()).thenReturn(List.of(new ProductOffering().id("123")));
// ////when(queryService.fetchProductOfferingById(new ProductOffering().getProductSpecification().id("123")).thenReturn(new ProductOffering().id("123"));
// //when(queryService.fetchProductOfferingById("123"))
// //           .thenReturn(new ProductOffering().id("123").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
// //                         .productSpecification(new ProductSpecificationRef().id("124")));
// //List<Event> history = new ArrayList<>();
// //OffsetDateTime lastUpdate = OffsetDateTime.now();
// //
// //history.add(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE,
// //           OffsetDateTime.now(), ProductOfferingType.ATOMICPRODUCTOFFERING));
// //history.add(new ProductSpecStateVerifiedEvent( "1",productSpecId, ProductSpecificationLifecycle.ACTIVE));
// //history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("12"),
// //           productOfferingId, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()));
// //history.add(new AtomicProductOfferingDescribedEvent( "1", "Mobile Access", "status_reason", "Mobile Access",
// //           "Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
// //history.add(new AtomicProductOfferingCategoryDefinedEvent( "1",
// //           Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// //           OffsetDateTime.now()));
// //history.add(new AtomicProductOfferingChannelDefinedEvent( "1", List.of(new ChannelRef().id("channelid1")),
// //           OffsetDateTime.now()));
// //
// //ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
// //productOfferingRelationship.setId("rel_1");
// //productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.ADD);
// //List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
// //productOfferingRelationships.add(productOfferingRelationship);
// //
// //ModifyProductOfferingRelationshipCommand command = new ModifyProductOfferingRelationshipCommand("1",
// //           productOfferingRelationships);
// //
// //fixture.registerInjectableResource(queryService).given(history).when(command)
// //           .expectException(BosException.class);
// //
// //	}

// 	@Test
// 	void raiseEventsWhenModifyProductOfferingValidFor() {
// 		String productSpecId = "productSpecId6";
// 		String productOfferingId = "productOfferingId7";
// 		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
// 				.endDateTime(OffsetDateTime.now().plusDays(10));
// 		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(
// 				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.description("Mobile Access").brand("MobileAccess").name("MobileAccess").validFor(validFor));
// 		ModifyProductOfferingValidForCommand command = new ModifyProductOfferingValidForCommand("productOfferingId7",
// 				validFor);

// 		List<Event> history = new ArrayList<>();
// 		// history.add(new ProductSpecSelectedEvent("product_spec_id"));
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
// 		history.add(new ProductSpecStateVerifiedEvent(productOfferingId, productSpecId,
// 				ProductSpecificationLifecycle.ACTIVE));
// 		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
// 				productOfferingId, ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));
// 		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "Mobile Access", "status_reason",
// 				"MobileAccess", "Cisco", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));

// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);

// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(
// 				new ProductOffering().id("123").productSpecification(new ProductSpecificationRef().id(productSpecId)));

// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new AtomicProductOfferingValidForModifiedEvent(productOfferingId, validFor, OffsetDateTime.now()));

// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(1, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof AtomicProductOfferingValidForDefinedEvent);
// //		AtomicProductOfferingValidForDefinedEvent atomicProductOfferingValidForDefinedEvent = (AtomicProductOfferingValidForDefinedEvent) eventList
// //				.get(0);
// //		assertEquals(productOfferingId, atomicProductOfferingValidForDefinedEvent.getProductOffId());
// //		assertNotNull(atomicProductOfferingValidForDefinedEvent.getValidFor());
// //		assertEquals(validFor, atomicProductOfferingValidForDefinedEvent.getValidFor());
// 	}

// 	@Test
// 	void raiseEventWhenModifyProductSpecificationOperation() {
// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		CommercialOperation operationSpecification = new CommercialOperation();
// 		operationSpecification.id("1").name("Create").validFor(new TimePeriod().startDateTime(lastUpdate));
// 		List<CommercialOperation> operationSpecificationList = new ArrayList<>();
// 		operationSpecificationList.add(operationSpecification);
// 		ProductSpecification productSpecification = new ProductSpecification().id(productSpecId)
// 				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);

// 		when(queryService.fetchProductSpecById("1")).thenReturn(productSpecification);
// 		when(queryService.fetchProductSpecById("1"))
// 				.thenReturn(new ProductSpecification().id("1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
// 						.operationSpecification(List.of(new OperationSpecification().id("1").name("Create")
// 								.validFor(new TimePeriod().startDateTime(lastUpdate)))));
// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(
// 				new ProductOffering().id("123").productSpecification(new ProductSpecificationRef().id("1")));

// 		ModifyProductOfferingOperationCommand command = new ModifyProductOfferingOperationCommand("1",
// 				operationSpecificationList);

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1", ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(),
// 						ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						new ProductSpecStateVerifiedEvent("1", productSpecId, ProductSpecificationLifecycle.ACTIVE),
// 						new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id("1"), "1",
// 								ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now()),
// 						new AtomicProductOfferingDescribedEvent("1", "Mobile Access", "status_reason", "Mobile Access",
// 								"Mobile Access", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()),
// 						new AtomicProductOfferingCategoryDefinedEvent("1",
// 								Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingChannelDefinedEvent("1", List.of(new ChannelRef().id("channelid1")),
// 								OffsetDateTime.now()),
// 						new AtomicProductOfferingMarketDefinedEvent("1", List.of(new MarketSegmentRef().id("1266")),
// 								OffsetDateTime.now()))
// 				.when(command).expectEvents(new AtomicProductOfferingOperationModifiedEvent("1",
// 						operationSpecificationList, OffsetDateTime.now()));

// 	}

// 	@Test
// 	void raiseEventsWhenModifyProductOfferingProductOfferingTypeASAPO() {

// 		when(queryService.fetchProductOfferingById(anyString())).thenReturn(new ProductOffering().id("1234")
// 				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).lifecycleStatus(ProductOfferingLifecycle.ACTIVE));

// 		POModificationCommand command = new POModificationCommand("1234");

// 		fixture.registerInjectableResource(queryService)
// 				.given(new ProductOfferingTypeSelectedEvent("1234", ProductOfferingLifecycle.ACTIVE,
// 						OffsetDateTime.now(), ProductOfferingType.ATOMICPRODUCTOFFERING))
// 				.when(command)
// 				.expectEvents(new ProductOfferingModificationInitiatedEvent("1234",
// 						new ProductOffering().id("1234").lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
// 								.type(ProductOfferingType.ATOMICPRODUCTOFFERING),
// 						OffsetDateTime.now(), ProductOfferingLifecycle.ACTIVE));
// 	}

// 	@Test
// 	void raiseEventsWhenModifyProductOfferingProductOfferingisEmpty() {

// 		when(queryService.fetchProductOfferingById(null)).thenReturn(new ProductOffering());

// 		POModificationCommand command = new POModificationCommand("");

// 		fixture.registerInjectableResource(queryService).given(new ProductOfferingTypeSelectedEvent("",
// 				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(), ProductOfferingType.ATOMICPRODUCTOFFERING))
// 				.when(command).expectException(BosException.class);
// 	}

// //
// //	/**
// //	 * Manage the Bundled Product Offering command and define Bundle Product Offerings
// //	 * in product offering.
// //	 * @author Varshika Choudhary
// //	 */
// 	@Test
// 	void raiseEventsForProductOfferingBundling() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		int globalMinCardinality = 0;
// 		int globalMaxCardinality = 1;
// 		List<BundledProductOffering> bundleProductOfferings = new ArrayList<>();

// 		BundledProductOffering bpo1 = new BundledProductOffering();
// 		bpo1.setId("bpo1");
// 		bpo1.setName("bpo1");
// 		BundledProductOfferingOption bundledProductOfferingOption1 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption1.setNumberRelOfferLowerLimit(0);
// 		bundledProductOfferingOption1.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption1.setNumberRelOfferDefault(1);
// 		bpo1.bundledProductOfferingOption(bundledProductOfferingOption1);

// 		BundledProductOffering bpo2 = new BundledProductOffering();
// 		bpo2.setId("bpo2");
// 		bpo2.setName("bpo2");
// 		BundledProductOfferingOption bundledProductOfferingOption2 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption2.setNumberRelOfferLowerLimit(1);
// 		bundledProductOfferingOption2.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption2.setNumberRelOfferDefault(1);
// 		bpo2.bundledProductOfferingOption(bundledProductOfferingOption2);

// 		BundledProductOffering bpo3 = new BundledProductOffering();
// 		bpo3.setId("bpo3");
// 		bpo3.setName("bpo3");
// 		BundledProductOfferingOption bundledProductOfferingOption3 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption3.setNumberRelOfferLowerLimit(1);
// 		bundledProductOfferingOption3.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption3.setNumberRelOfferDefault(1);
// 		bpo3.bundledProductOfferingOption(bundledProductOfferingOption3);

// 		bundleProductOfferings.add(bpo1);
// 		bundleProductOfferings.add(bpo2);
// 		bundleProductOfferings.add(bpo3);

// 		ManageProductOfferingBundlingCommand command = new ManageProductOfferingBundlingCommand(productOfferingId,
// 				bundleProductOfferings, globalMinCardinality, globalMaxCardinality);

// 		List<Event> history = new ArrayList<>();
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.BUNDLEPRODUCTOFFERING));
// 		history.add(new BundleProductOfferingSelectedEvent(productOfferingId, bundleProductOfferings, lastUpdate,
// 				globalMinCardinality, globalMaxCardinality));
// 		history.add(new BundleProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// 		history.add(new BundleProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.BUNDLEPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);

// 		fixture.registerInjectableResource(queryService).given(history).when(command).expectEvents(
// 				new ChildPOInfoAddedEvent(productOfferingId, bundleProductOfferings, OffsetDateTime.now(),
// 						globalMinCardinality, globalMaxCardinality),
// 				new BundleProductOfferingSelectedEvent(productOfferingId, bundleProductOfferings, OffsetDateTime.now(),
// 						globalMinCardinality, globalMaxCardinality));

// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(2, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof ChildPOInfoAddedEvent);
// //		assertTrue(eventList.get(1) instanceof BundleProductOfferingSelectedEvent);
// //		BundleProductOfferingSelectedEvent bundleProductOfferingSelectedEvent = (BundleProductOfferingSelectedEvent) eventList
// //				.get(1);
// //		assertNotNull(bundleProductOfferingSelectedEvent.getProductOfferingId());
// //		assertEquals(bundleProductOfferingSelectedEvent.getProductOfferingId(), productOfferingId);
// 	}

// 	@Test
// 	void raiseInvalidEventsForEmptyProductOfferingBundling() {

// 		OffsetDateTime lastUpdate = OffsetDateTime.now();
// 		int globalMinCardinality = 0;
// 		int globalMaxCardinality = 1;
// 		List<BundledProductOffering> bundleProductOfferings = new ArrayList<>();

// 		BundledProductOffering bpo1 = new BundledProductOffering();
// 		bpo1.setId("bpo1");
// 		bpo1.setName("bpo1");
// 		BundledProductOfferingOption bundledProductOfferingOption1 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption1.setNumberRelOfferLowerLimit(0);
// 		bundledProductOfferingOption1.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption1.setNumberRelOfferDefault(1);
// 		bpo1.bundledProductOfferingOption(bundledProductOfferingOption1);

// 		BundledProductOffering bpo2 = new BundledProductOffering();
// 		bpo2.setId("bpo2");
// 		bpo2.setName("bpo2");
// 		BundledProductOfferingOption bundledProductOfferingOption2 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption2.setNumberRelOfferLowerLimit(1);
// 		bundledProductOfferingOption2.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption2.setNumberRelOfferDefault(1);
// 		bpo2.bundledProductOfferingOption(bundledProductOfferingOption2);

// 		BundledProductOffering bpo3 = new BundledProductOffering();
// 		bpo3.setId("bpo3");
// 		bpo3.setName("bpo3");
// 		BundledProductOfferingOption bundledProductOfferingOption3 = new BundledProductOfferingOption();
// 		bundledProductOfferingOption3.setNumberRelOfferLowerLimit(1);
// 		bundledProductOfferingOption3.setNumberRelOfferUpperLimit(1);
// 		bundledProductOfferingOption3.setNumberRelOfferDefault(1);
// 		bpo3.bundledProductOfferingOption(bundledProductOfferingOption3);

// //		bundleProductOfferings.add(bpo1);
// //		bundleProductOfferings.add(bpo2);
// //		bundleProductOfferings.add(bpo3);

// 		ManageProductOfferingBundlingCommand command = new ManageProductOfferingBundlingCommand(productOfferingId,
// 				bundleProductOfferings, globalMinCardinality, globalMaxCardinality);

// 		List<Event> history = new ArrayList<>();
// 		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
// 				lastUpdate, ProductOfferingType.BUNDLEPRODUCTOFFERING));
// 		history.add(new BundleProductOfferingSelectedEvent(productOfferingId, bundleProductOfferings, lastUpdate,
// 				globalMinCardinality, globalMaxCardinality));
// 		history.add(new BundleProductOfferingOperDefinedEvent(productOfferingId,
// 				List.of(new CommercialOperation().id("operation_spec_id")), lastUpdate));
// 		history.add(new BundleProductOfferingDescribedEvent(productOfferingId, "description", "statusReason", "name",
// 				"brand", ProductOfferingType.BUNDLEPRODUCTOFFERING, false, lastUpdate));
// 		// ProductOfferingAggregate productOfferingAggregate = new
// 		// ProductOfferingAggregate(history, queryService);

// 		fixture.registerInjectableResource(queryService).given(history).when(command)
// 				.expectException(BosException.class);

// //
// //		List<Event> eventList = productOfferingAggregate.process(command);
// //		assertNotNull(eventList);
// //		assertEquals(2, eventList.size());
// //
// //		assertTrue(eventList.get(0) instanceof ChildPOInfoAddedEvent);
// //		assertTrue(eventList.get(1) instanceof BundleProductOfferingSelectedEvent);
// //		BundleProductOfferingSelectedEvent bundleProductOfferingSelectedEvent = (BundleProductOfferingSelectedEvent) eventList
// //				.get(1);
// //		assertNotNull(bundleProductOfferingSelectedEvent.getProductOfferingId());
// //		assertEquals(bundleProductOfferingSelectedEvent.getProductOfferingId(), productOfferingId);
// 	}

// 	@Test
// 	void testProductOfferingDeleteCommand() {
// 		OffsetDateTime delTime = OffsetDateTime.now();
// 		ProductOfferingDeleteCommand command = new ProductOfferingDeleteCommand(aggregateId, delTime, 40L, "HOURS");
// 		fixture.given().when(command).expectEvents(new ProductOfferingDeleteEvent(aggregateId, delTime, 40L, "HOURS"));
// 	}

// 	private ProductOffering getStoredProductOffering() {
// 		ProductOffering productOffering=new ProductOffering();
// 		CategoryRef category1=new CategoryRef().id("c1").name("c1name");
// 		CategoryRef category2=new CategoryRef().id("c2").name("c2name");
// 		ChannelRef channel1=new ChannelRef().id("ch1").name("channel1");
// 		ChannelRef channel2=new ChannelRef().id("ch2").name("channel2");
// 		MarketSegmentRef ms1=new MarketSegmentRef().id("ms1").name("ms1name");
// 		MarketSegmentRef ms2=new MarketSegmentRef().id("ms2").name("ms2name");
// 		ProductSpecificationCharacteristicValue value1=new ProductSpecificationCharacteristicValue().value("value1");
// 		ProductSpecificationCharacteristicValue value2=new ProductSpecificationCharacteristicValue().value("value2");
// 		//ProductSpecificationRef ps1=new ProductSpecificationRef().id("ps1");
// 		ProductSpecificationCharacteristicValueUse valueUse1=new ProductSpecificationCharacteristicValueUse().name("one").description("one").valueType("one")
// 		.productSpecCharacteristicValue(List.of(value1,value2));
// 		ProductSpecificationCharacteristicValueUse valueUse2=new ProductSpecificationCharacteristicValueUse().name("two").description("two").valueType("two")
// 				.productSpecCharacteristicValue(List.of(value1,value2));
// 		ProductOfferingTerm pt1=new ProductOfferingTerm().name("pt1").description("pt1").duration(new Quantity()).validFor(new TimePeriod());
// 		ProductOfferingTerm pt2=new ProductOfferingTerm().name("pt2").description("pt2").duration(new Quantity()).validFor(new TimePeriod());
// 		CommercialOperation co1=new CommercialOperation().id(".co1").name("co1").description("co1");
// 		CommercialOperation co2=new CommercialOperation().id(".co2").name("co2").description("co2");
// 		ProductOfferingRelationship pr1=new ProductOfferingRelationship().id("pr1").relationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		ProductOfferingRelationship pr2=new ProductOfferingRelationship().id("pr2").relationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		RelatedParty rp1=new RelatedParty().id("rp1");
// 		RelatedParty rp2=new RelatedParty().id("rp2");
// 		productOffering.id("po1").description("poDescription").name("poName").
// 		isBundle(Boolean.TRUE).isSellable(Boolean.TRUE).isInstallable(Boolean.TRUE).lifecycleStatus(ProductOfferingLifecycle.ACTIVE).type(ProductOfferingType.ATOMICPRODUCTOFFERING).brand("poBrand").
// 		statusReason("poStatus").version("0.1.0").category(Set.of(category1,category2)).channel(List.of(channel1,channel2)).marketSegment
// 				(List.of(ms1,ms2)).prodSpecCharValueUse(new ArrayList<>(List.of(valueUse1,valueUse2))).productOfferingTerm(List.of(pt1,pt2)).
// 				commercialOperation(List.of(co1,co2)).productOfferingRelationship(List.of(pr1,pr2)).relatedParty(List.of(rp1,rp2)).validFor(new TimePeriod());
// 		return productOffering;
// 		}
// 	private AtomicProductOfferingDescribedModifiedEvent getAtomicProductOfferingDescribedModifiedEvent() {
// 		return new AtomicProductOfferingDescribedModifiedEvent("po1","poDescription","poStatus","poName","poBrand",ProductOfferingType.ATOMICPRODUCTOFFERING,Boolean.TRUE,OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingCategoryModifiedEvent getAtomicProductOfferingCategoryModifiedEvent() {
// 		CategoryRef category1=new CategoryRef().id("c1").name("c1name");
// 		CategoryRef category2=new CategoryRef().id("c2").name("c2name");
// 		return new AtomicProductOfferingCategoryModifiedEvent("po1",Set.of(category1,category2),null,OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingChannelModifiedEvent getAtomicProductOfferingChannelModifiedEvent() {
// 		ChannelRef channel1=new ChannelRef().id("ch1").name("channel1");
// 		ChannelRef channel2=new ChannelRef().id("ch2").name("channel2");
// 		return new AtomicProductOfferingChannelModifiedEvent("po1",List.of(channel1,channel2),OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingMarketModifiedEvent getAtomicProductOfferingMarketModifiedEvent() {
// 		MarketSegmentRef ms1=new MarketSegmentRef().id("ms1").name("ms1name");
// 		MarketSegmentRef ms2=new MarketSegmentRef().id("ms2").name("ms2name");
// 		return new AtomicProductOfferingMarketModifiedEvent("po1",List.of(ms1,ms2),OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingRelatedPartyModifiedEvent getAtomicProductOfferingRelatedPartyModifiedEvent() {
// 		RelatedParty rp1=new RelatedParty().id("rp1");
// 		RelatedParty rp2=new RelatedParty().id("rp2");
// 		return new AtomicProductOfferingRelatedPartyModifiedEvent("po1",List.of(rp1,rp2),OffsetDateTime.now());
// 	}

// 	private AtomicProductOfferingOperationModifiedEvent getAtomicProductOfferingOperationModifiedEvent() {
// 		CommercialOperation co1=new CommercialOperation().id(".co1");
// 		CommercialOperation co2=new CommercialOperation().id(".co2");
// 		return new AtomicProductOfferingOperationModifiedEvent("po1",List.of(co1,co2),OffsetDateTime.now());
// 	}
// 	private LinkPOPtoOperModifiedEvent getLinkPOPtoOperModifiedEvent() {
// 		CommercialOperation co1=new CommercialOperation().id(".co1").name("co1").description("co1");
// 		CommercialOperation co2=new CommercialOperation().id(".co2").name("co2").description("co2");
// 		return new LinkPOPtoOperModifiedEvent("po1",List.of(co1,co2),OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingTermModifiedEvent getAtomicProductOfferingTermModifiedEvent() {
// 		ProductOfferingTerm pt1=new ProductOfferingTerm().name("pt1").description("pt1").duration(new Quantity()).validFor(new TimePeriod());
// 		ProductOfferingTerm pt2=new ProductOfferingTerm().name("pt2").description("pt2").duration(new Quantity()).validFor(new TimePeriod());
// 		return new AtomicProductOfferingTermModifiedEvent("po1",List.of(pt1,pt2),OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingCharacteristicsModifiedEvent getAtomicProductOfferingCharacteristicsModifiedEvent() {
// 		ProductSpecificationCharacteristicValue value1=new ProductSpecificationCharacteristicValue().value("value1");
// 		ProductSpecificationCharacteristicValue value2=new ProductSpecificationCharacteristicValue().value("value2");
// 		//ProductSpecificationRef ps1=new ProductSpecificationRef().id("ps1");
// 		ProductSpecificationCharacteristicValueUse valueUse1=new ProductSpecificationCharacteristicValueUse().name("one").description("one").valueType("one")
// 		.productSpecCharacteristicValue(new ArrayList<>(List.of(value1,value2)));
// 		ProductSpecificationCharacteristicValueUse valueUse2=new ProductSpecificationCharacteristicValueUse().name("two").description("two").valueType("two")
// 		.productSpecCharacteristicValue(new ArrayList<>(List.of(value1,value2)));
// 		return new AtomicProductOfferingCharacteristicsModifiedEvent("po1",new ArrayList<>(List.of(valueUse1,valueUse2)),OffsetDateTime.now());
// 	}
// 	private AtomicProductOfferingRelationshipModifiedEvent getAtomicProductOfferingRelationshipModifiedEvent() {
// 		ProductOfferingRelationship pr1=new ProductOfferingRelationship().id("pr1").relationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		ProductOfferingRelationship pr2=new ProductOfferingRelationship().id("pr2").relationshipType(ProductOfferingRelationshipType.AGGREGATES);
// 		return new AtomicProductOfferingRelationshipModifiedEvent("po1",List.of(pr1,pr2),null);
// 	}
// 	private AtomicProductOfferingValidForModifiedEvent getAtomicProductOfferingValidForModifiedEvent() {
// 		TimePeriod tp=new TimePeriod();
// 		return new AtomicProductOfferingValidForModifiedEvent("po1",tp,null);
// 	}

// 	@Test
// 	public void testProductOferingValidationCommandWithMinorModificationInIdentityData() {

// 		AtomicProductOfferingModifiedValidatedCommand command=new AtomicProductOfferingModifiedValidatedCommand("po1",VersionType.MINOR.getValue());
// 		ProductOffering po=getStoredProductOffering();
// 		Mockito.when(queryService.fetchProductOfferingById(Mockito.anyString())).thenReturn(po);

// 		ProductSpecificationRef ps1=new ProductSpecificationRef().id("ps1");
// 		fixture.registerInjectableResource(queryService)
// 		.given(
// 				new ProductOfferingTypeSelectedEvent("po1",ProductOfferingLifecycle.INSTUDY,null,ProductOfferingType.ATOMICPRODUCTOFFERING),
// 				new AtomicProductOfferingInitiatedEvent(ps1,"po1", ProductOfferingLifecycle.ACTIVE,OffsetDateTime.now()),
// 				new AtomicProductOfferingDescribedModifiedEvent("po1","poDescriptionmodified","poStatus","poName","poBrand",ProductOfferingType.ATOMICPRODUCTOFFERING,Boolean.TRUE,OffsetDateTime.now()),
// 				getAtomicProductOfferingCategoryModifiedEvent(),getAtomicProductOfferingChannelModifiedEvent(),getAtomicProductOfferingMarketModifiedEvent(),
// 				getAtomicProductOfferingRelatedPartyModifiedEvent(),getAtomicProductOfferingOperationModifiedEvent(),getLinkPOPtoOperModifiedEvent(),getAtomicProductOfferingTermModifiedEvent(),
// 				getAtomicProductOfferingCharacteristicsModifiedEvent(),getAtomicProductOfferingRelationshipModifiedEvent(),getAtomicProductOfferingValidForModifiedEvent(),getAtomicProductOfferingValidForModifiedEvent(),
// 				new AtomicProductOfferingVersionCreatedEvent("po1","0.1.0",null))
// 		.when(command).expectEvents(new AtomicProductOfferingModificationValidatedEvent("po1",
// 				getStoredProductOffering(),ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now(),"0.2.0"));

// 	}

}
