// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.aggregate;


import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.command.category.*;
import com.orange.discobole.productcatalog.category.command.category.delete.CancelCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.DeleteValidateCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.SelectCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.*;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.dto.generated.common.*;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.category.event.category.*;
import com.orange.discobole.productcatalog.category.event.category.delete.*;
import com.orange.discobole.productcatalog.category.event.category.modify.*;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.category.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class CategoryAggregate handles the business logic of different commands.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class CategoryAggregate {

	private static final String CATEGORY_NAME = "category";

	@AggregateIdentifier
	private String categoryId;

	private static final Logger LOGGER = LogManager.getLogger(CategoryAggregate.class);

	@Resource
	private ConfigurableProperties configurableProperties;

	private Category category;

	@Resource
	private QueryService queryService;

	private static final String DISCO_CATEGORY_AND_DESCRIPTION_CANANNOT_BENULL = "DISCO_CATEGORY_AND_DESCRIPTION_CANANNOT_BENULL";

	private static final String DISCO_CATEGORY_PO_STATUS_SHOULD_ACTIVE_OR_LAUNCHED = "DISCO_CATEGORY_PO_STATUS_SHOULD_ACTIVE_OR_LAUNCHED";

	private static final String DISCO_CATEGORY_PO_ID_NOTVALID = "DISCO_CATEGORY_PO_ID_NOTVALID";

	private static final String DISCO_CATEGORY_SUBCATEGORY_DOESNOTEXSIST = "DISCO_CATEGORY_SUBCATEGORY_DOESNOTEXSIST";

	private static final String DISCO_CATEGORY_SUBCATEGORY_ANOTCATEGORY = "DISCO_CATEGORY_SUBCATEGORY_ANOTCATEGORY";

	private static final String DISCO_CATEGORY_PARENT_CANNOTNOTNULL = "DISCO_CATEGORY_PARENT_CANNOTNOTNULL";

	private static final String DISCO_CATEORY_CATEORY_NOT_FOUND = "DISCO_CATEORY_CATEORY_NOT_FOUND";

	private static final String DISCO_CATEGORY_CHECK3 = "DISCO_CATEGORY_CHECK3";

	private static final String DISCO_CATEGORY_CHECK2 = "DISCO_CATEGORY_CHECK2";

	private static final String DISCO_CATEGORY_CHECK1 = "DISCO_CATEGORY_CHECK1";

	private static final String DISCO_PO_CATEGORYCHECK1 = "DISCO_PO_CATEGORYCHECK1";

	private static final String INVALID_FIELD = "INVALID_FIELD";

	private static final String DISCO_CATEGORY_DELETION_1 = "DISCO_CATEGORY_DELETION_1";

	private static final String DISCO_CATEGORY_DELETION_2 = "DISCO_CATEGORY_DELETION_2";



	private String accessToken;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	public CategoryAggregate() {

	}

	@Autowired
	public CategoryAggregate(AccessTokenInterceptor accessTokenInterceptor) {
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean
	}


	/**
	 * CategoryAggregate.
	 *
	 * @param command the command
	 */


	/**
	 * on
	 *
	 * @param event the event
	 */


	/**
	 * process
	 *
	 * @param command the command
	 */
	@CommandHandler
	public  CategoryAggregate(DefineCategoryIdentityDataCommand command,QueryService queryService) {

		if (null == command.getName() || null == command.getDescription()) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_AND_DESCRIPTION_CANANNOT_BENULL);
		}
		this.categoryId = command.getCategoryId();
		String categoryId = command.getCategoryId();
		List<CategoryRef> categoryRefs = new ArrayList<>();
		String href = command.getHref() + "?id=" + this.categoryId;

		validateSubCategories(command.getSubCategoryIds(), categoryRefs, queryService);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		String categoryType = "ProductOfferingCategory";

		if (command.getProductOfferingIds() != null && !command.getProductOfferingIds().isEmpty()) {
			validateNoSubCategories(categoryId, queryService);
		}
		ProductOffering productOffering = null;
		Set<ProductOfferingRef> productOfferingrefs = new HashSet<>();
		for (String productOfferingRefId :command.getProductOfferingIds()) {
			productOffering = queryService.fetchProductOfferingById(productOfferingRefId,accessToken);
			validateProductOffering(productOffering,productOfferingRefId);
			ProductOfferingRef productOfferingRef=new ProductOfferingRef();
			productOfferingRef.id(productOffering.getId()).type(productOffering.getType().getValue()).referredType(null).baseType(productOffering.getBaseType()).href(productOffering.getHref()).schemaLocation(productOffering.getSchemaLocation());
			productOfferingrefs.add(productOfferingRef);
		}

		AggregateLifecycle.apply(new EntityTypeSelectedEvent(categoryId, categoryType, lastUpdate));
		AggregateLifecycle.apply(new CategoryIdentityDataDefinedEvent(categoryId, command.getName(),
				command.getDescription(), command.getIsRoot(), command.getParentId(), lastUpdate, categoryRefs,productOfferingrefs, href));
		AggregateLifecycle.apply(new AssociateEntitySelectedEvent(categoryId, productOfferingrefs, lastUpdate));
	}

	@EventSourcingHandler
	public void on(EntityTypeSelectedEvent event) {
		this.categoryId = event.getCategoryId();
		this.category = new Category();
		this.category.id(event.getCategoryId()).type(event.getCategoryType()).lastUpdate(event.getLastUpdate());
	}

	@EventSourcingHandler
	private void on(CategoryIdentityDataDefinedEvent event) {
		this.categoryId = event.getCategoryId();
		this.category.name(event.getName()).description(event.getDescription()).href(event.getHref())
				.isRoot(event.getIsRoot()).parentId(event.getParentId());
	}

	/**
	 * CategoryAggregate.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void process(SelectAssociateEntityCommand command,QueryService queryService,
						Publisher publisher) {
		if (command.getProductOfferingIds() != null && !command.getProductOfferingIds().isEmpty()) {
			validateNoSubCategories(categoryId, queryService);
		}
		ProductOffering productOffering = null;
		Set<ProductOfferingRef> productOfferingrefs = new HashSet<>();
		for (String productOfferingRefId :command.getProductOfferingIds()) {
			productOffering = queryService.fetchProductOfferingById(productOfferingRefId,accessToken);
			validateProductOffering(productOffering,productOfferingRefId);
			ProductOfferingRef productOfferingRef=new ProductOfferingRef();
			productOfferingRef.id(productOffering.getId()).type(productOffering.getType().getValue()).referredType(null).baseType(productOffering.getBaseType()).href(productOffering.getHref()).schemaLocation(productOffering.getSchemaLocation());
			productOfferingrefs.add(productOfferingRef);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new AssociateEntitySelectedEvent(categoryId, productOfferingrefs, lastUpdate));
	}
	private void validateNoSubCategories(String categoryId, QueryService queryService) {
		Category fetchedCategory  = queryService.fetchCategoryById(categoryId, accessToken);
		if (fetchedCategory  != null && fetchedCategory .getSubCategory() != null && !fetchedCategory .getSubCategory().isEmpty()) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_CHECK2);
		}//3
	}


	/**
	 * on.
	 *
	 * @param command the command
	 */
	@EventSourcingHandler
	public void on(AssociateEntitySelectedEvent event) {
		this.categoryId = event.getCategoryId();
		this.category.productOffering(event.getProductOfferings());
	}

	private void validateProductOffering(ProductOffering productOffering,String productOfferingRefId) {
		if (null == productOffering) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_PO_ID_NOTVALID,null,productOfferingRefId);
		}
		//5
		if (!Boolean.TRUE.equals(productOffering.isIsSellable())) {
			throw new DiscoManagedClientException(DISCO_PO_CATEGORYCHECK1, null, productOfferingRefId);
		}

		if (ProductOfferingLifecycle.ACTIVE != productOffering.getLifecycleStatus()
				&& ProductOfferingLifecycle.LAUNCHED != productOffering.getLifecycleStatus() && ProductOfferingLifecycle.INTEST != productOffering.getLifecycleStatus()) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_PO_STATUS_SHOULD_ACTIVE_OR_LAUNCHED);
		}
	}

	/**
	 * on.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void process(CancelCategoryCommand command) {

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new CategoryCancelledEvent(category, command.getCategoryId(), lastUpdate));
	}

	/**
	 * on.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void process(ValidateCategoryCommand command) {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		TimePeriod timePeriod = new TimePeriod();
		AggregateLifecycle.apply(new CategoryCreationEvent(command.getCategoryId(), category, lastUpdate));
	}
	@EventSourcingHandler
	private void on(CategoryCreationEvent event) {
		this.category=event.getCategory();
		this.categoryId=event.getCategoryId();
	}

	/**
	 * on.
	 *
	 * @param command the command
	 */
	private void validateSubCategories(List<String> subCategoryIds, List<CategoryRef> categoryRefs,QueryService queryService) {


		for (String subCategoryId : subCategoryIds) {
			Category subcategory = null;
			subcategory = queryService.fetchCategoryById(subCategoryId,accessToken);
			if (null == subcategory) {
				throw new DiscoManagedClientException(DISCO_CATEGORY_SUBCATEGORY_DOESNOTEXSIST, subCategoryId, subCategoryId);
			}
			if (Boolean.TRUE.equals(subcategory.isIsRoot())) {
				throw new DiscoManagedClientException(DISCO_CATEGORY_SUBCATEGORY_ANOTCATEGORY, subCategoryId, subCategoryId);
			}
			//2
			int depthBelow = calculateDepthBelow(subcategory, queryService);


			if (depthBelow >= 2) {
				throw new DiscoManagedClientException(DISCO_CATEGORY_CHECK3);
			}


			CategoryRef categoryRef = new CategoryRef();
			categoryRef.id(subcategory.getId()).type(subcategory.getType()).name(subcategory.getName())
					.href(subcategory.getHref()).version(subcategory.getVersion()).baseType(subcategory.getBaseType())
					.schemaLocation(subcategory.getSchemaLocation());
			categoryRefs.add(categoryRef);
		}
	}

	/**
	 * on.
	 *
	 * @param command the command
	 */





	private int calculateDepthBelow(Category category, QueryService queryService) {
		if (category == null || category.getSubCategory() == null || category.getSubCategory().isEmpty()) {
			return 0;
		}

		int maxChildDepth = 0;

		for (CategoryRef subRef : category.getSubCategory()) {

			Category childCategory = queryService.fetchCategoryById(subRef.getId(), accessToken);

			if (childCategory != null) {
				int childDepth = calculateDepthBelow(childCategory, queryService); // recursive call
				maxChildDepth = Math.max(maxChildDepth, 1 + childDepth);
			}

		}

		return maxChildDepth;
	}

	@CommandHandler
	public void process(ModifySelectCategoryCommand command,QueryService queryService) {
		this.categoryId = command.getCategoryId();
		Category Category = queryService.fetchCategoryById(categoryId, accessToken);
		if (null == Category) {
			throw new DiscoManagedClientException(DISCO_CATEORY_CATEORY_NOT_FOUND, categoryId, null);
		}

		CategoryLifeCycle lifeCycleStatus = CategoryLifeCycle.ACTIVE;
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle
				.apply(new CategoryModifiedInitiatedEvent(categoryId, Category, lastUpdate, lifeCycleStatus));


	}

	@EventSourcingHandler
	private void on(CategoryModifiedInitiatedEvent event) {
		this.categoryId=event.getCategoryId();
	}



	@CommandHandler
	public void process(CancelCategoryModificationCommand command) {

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new CategoryModificationCancelledEvent(command.getCategoryId(), category, lastUpdate));

	}

	@CommandHandler
	public void process(ModifyAssociateEntityCommand command,QueryService queryService) {

		if (command.getProductOfferingIds() != null && !command.getProductOfferingIds().isEmpty())
		{validateNoSubCategories(command.getCategoryId(), queryService);}
		List<String> selectedProductOfferingIds = command.getProductOfferingIds();
		String categoryId = this.category.getId();
		Category category = queryService.fetchCategoryById(categoryId, accessToken);
		Set<ProductOfferingRef> deleteProductOfferings = new HashSet<>();
		CategoryEntityRelationship entity = queryService.fetchCategoryEntityById(categoryId,accessToken);
		if (null != entity) {
			deleteProductOfferings = entity.getProductOfferings();
		}
		Set<ProductOfferingRef> addProductOfferings = new HashSet<>();

		for (ProductOfferingRef productOfferingRef : category.getProductOffering()) {
			if (selectedProductOfferingIds.remove(productOfferingRef.getId())) {
				deleteProductOfferings = deleteProductOfferings.stream().filter(x-> !x.getId().equals(productOfferingRef.getId())).collect(Collectors.toSet());
			}
		}
		for (String productOfferingId : selectedProductOfferingIds) {
			ProductOffering productOffering = queryService.fetchProductOfferingById(productOfferingId, accessToken);
			validateProductOffering(productOffering,productOfferingId);
			ProductOfferingRef productOfferingRef = convert(productOffering);
			addProductOfferings.add(productOfferingRef);
		}
		AggregateLifecycle.apply(new AssociatedEntityModifiedEvent(this.category.getId(), addProductOfferings,
				deleteProductOfferings, OffsetDateTime.now()));
	}

	@CommandHandler
	public void process(ModifyAssociateEntityCommandPOCreation command) {
		AggregateLifecycle.apply(new AssociatedEntityIndirectModifiedEvent(command.getCategoryId(),  command.getProductOfferingIds(),
				null, OffsetDateTime.now()));
	}

	@CommandHandler
	public void process(ModifyAssociateEntityCommandPOModification command) {
		AggregateLifecycle.apply(new AssociatedEntityIndirectModifiedEvent(command.getCategoryId(), null, command.getProductOfferingIds(),
				OffsetDateTime.now()));


	}

	@CommandHandler
	public void  process(ModifyCategoryIdentityDataCommand command) {
		List<CategoryRef> categoryRefs = new ArrayList<>();
		String categoryId = command.getCategoryId();

		if (null == command.getName() || null == command.getDescription()) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_AND_DESCRIPTION_CANANNOT_BENULL);
		}

		// isRoot validation
		if (command.getIsRoot() != null) {
			throw new DiscoManagedClientException(INVALID_FIELD);
		}

		// if isRoot null then preserve existing value
		Boolean isRootToSet = this.category.isIsRoot();
		String parentId = command.getParentId();
		if (Boolean.TRUE.equals(isRootToSet)){
			parentId = null;
		}

		validateSubCategories(command.getSubCategoryIds(), categoryRefs,queryService);
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		List<String> selectedProductOfferingIds = command.getProductOfferingIds();
		Category category = queryService.fetchCategoryById(categoryId, accessToken);
		Set<ProductOfferingRef> deleteProductOfferings = new HashSet<>();
		CategoryEntityRelationship entity = queryService.fetchCategoryEntityById(categoryId,accessToken);
		if (null != entity) {
			deleteProductOfferings = entity.getProductOfferings();
		}
		Set<ProductOfferingRef> addProductOfferings = new HashSet<>();

		for (ProductOfferingRef productOfferingRef : category.getProductOffering()) {
			if (selectedProductOfferingIds.remove(productOfferingRef.getId())) {
				deleteProductOfferings = deleteProductOfferings.stream().filter(x-> !x.getId().equals(productOfferingRef.getId())).collect(Collectors.toSet());
			}
		}
		for (String productOfferingId : selectedProductOfferingIds) {
			ProductOffering productOffering = queryService.fetchProductOfferingById(productOfferingId, accessToken);
			validateProductOffering(productOffering,productOfferingId);
			ProductOfferingRef productOfferingRef = convert(productOffering);
			addProductOfferings.add(productOfferingRef);
		}

		AggregateLifecycle.apply(new CategoryIdentityDataModifiedEvent(categoryId, command.getName(), command.getDescription(),
				isRootToSet, parentId, lastUpdate,categoryRefs,addProductOfferings));
		AggregateLifecycle.apply(new AssociatedEntityModifiedEvent(this.category.getId(), addProductOfferings,
				deleteProductOfferings, OffsetDateTime.now()));

	}

	@CommandHandler
	public void process(ModifyValidateCategoryCommand command) {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new CategoryModificationValidatedEvent(command.getCategoryId(),category,lastUpdate));

	}

	/**
	 * Convert.
	 *
	 * @param productOffering the productOffering
	 * @return ProductOfferingRef
	 */
	private ProductOfferingRef convert(final ProductOffering productOffering) {
		return new ProductOfferingRef().baseType(productOffering.getBaseType()).href(productOffering.getHref())
				.id(productOffering.getId()).schemaLocation(productOffering.getSchemaLocation())
				.type(productOffering.getType().getValue());
	}

	/**
	 * convert.
	 *
	 * @param category the category
	 */
	private CategoryRef convert(final Category category) {
		return new CategoryRef().id(category.getId()).type(category.getType());
	}

	@CommandHandler
	public void processSelectCategoryDeleteCommand(SelectCategoryDeleteCommand command,QueryService queryService) {
		LOGGER.info("Method processSelectCategoryDeleteCommand -> SelectCategoryDeleteCommand : {}", command);
		String categoryId = command.getcategoryId();
		Category category = queryService.fetchCategoryById(categoryId, accessToken);
		if (null == category) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_DELETION_1, categoryId, null);
		}

		if (category.getSubCategory() != null && !category.getSubCategory().isEmpty()) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_DELETION_2);
		}

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new SelectCategoryDeleteEvent(categoryId, category, lastUpdate ));

	}

	/**
	 * updates the state of aggregate after applying SelectCategoryDeleteEvent.
	 *
	 * @param event : SelectCategoryDeleteEvent
	 */
	@EventSourcingHandler
	public void on(SelectCategoryDeleteEvent event) {

		this.categoryId = event.getCategoryId();
	}

	@CommandHandler
	public void processCancelCategoryDeleteCommand(CancelCategoryDeleteCommand command,QueryService queryService) {
		LOGGER.info("Method processCancelCategoryDeleteCommand -> CancelCategoryDeleteCommand : {}", command);
		String categoryId = command.getCategoryId();
		Category category = queryService.fetchCategoryById(categoryId, accessToken);
		if (null == category) {
			throw new DiscoManagedClientException(DISCO_CATEORY_CATEORY_NOT_FOUND, categoryId, null);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new CancelCategoryDeleteEvent(categoryId, category, lastUpdate));
	}

	/**
	 * updates the state of aggregate after applying CancelCategoryDeleteEvent.
	 *
	 * @param event : CancelCategoryDeleteEvent
	 */
	@EventSourcingHandler
	public void on(CancelCategoryDeleteEvent event) {
		this.categoryId = event.getCategoryId();
	}

	/**
	 * This method will validate Category Deletion Process
	 *
	 * @param command : DeleteValidateCategoryCommand
	 * @param queryService
	 */
	@CommandHandler
	public void processDeleteValidateCategoryCommand(DeleteValidateCategoryCommand command,
													 QueryService queryService,Publisher publisher) {
		LOGGER.info("Method processDeleteValidateCategoryCommand -> DeleteValidateCategoryCommand : {}", command);
		String categoryId = command.getCategoryId();
		Category category = queryService.fetchCategoryById(categoryId, accessToken);
		if (null == category) {
			throw new DiscoManagedClientException(DISCO_CATEORY_CATEORY_NOT_FOUND, categoryId, null);
		}
		CategoryRef categoryRef = convert(category);



		if (Boolean.FALSE.equals(category.isIsRoot())) {
			List<Category> parent = queryService.fetchCategoryEntityBySubCategoryId(categoryId,accessToken);
			AggregateLifecycle.apply(new CategoryDeletedEvent(categoryId,parent));

		}
		else{
			AggregateLifecycle.apply(new CategoryDeletedEvent(categoryId,null));
		}
		AggregateLifecycle.apply(new CategoryAssociationDeletedEvent(categoryId));

	}


	@EventSourcingHandler
	public void on(CategoryDeletedEvent event) {
		this.categoryId = event.getCategoryId();

	}

	@EventSourcingHandler
	public void on(CategoryAssociationDeletedEvent event) {
		this.categoryId = event.getCategoryId();

	}


}
