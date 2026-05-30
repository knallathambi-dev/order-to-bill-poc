// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductCreateEventProducerImpl;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductDeleteEventProducerImpl;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductStateChangeEventProducerImpl;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.*;
import com.orange.discobole.productinventory.repository.CustomProductRepository;
import com.orange.discobole.productinventory.repository.ProductRepository;
import com.orange.discobole.productinventory.service.CatalogService;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.service.HrefGeneratorService;
import com.orange.discobole.productinventory.service.ProductService;
import com.orange.discobole.productinventory.util.CurrencyUtils;
import com.orange.discobole.productinventory.util.ProductEntityUtil;
import com.orange.discobole.productinventory.validation.ProductDatesChecker;
import com.orange.discobole.productinventory.validation.ProductEntityValidationHandler;
import com.orange.discobole.productinventory.validation.pageable.impl.ProductFieldFetcher;
import com.orange.discobole.productinventory.validation.status.StatusChecker;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.enumerate.PublishEventEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static com.orange.discobole.productinventory.util.ProductEntityUtil.getProductRelationshipsByType;
import static com.orange.discobole.productinventory.util.ProductEntityUtil.removeRelationShipFromProduct;
import static com.orange.discobole.productinventory.validation.ProductAttributesValidator.validateForUnpatchableAttributes;
import static com.orange.discobole.productinventory.validation.ProductCharacteristicsValidator.checkOnlyOneOccurrenceValidityCharacteristic;
import static com.orange.discobole.productinventory.validation.ProductCharacteristicsValidator.validateDateTimeCharacteristic;
import static com.orange.discobole.productinventory.validation.status.StatusCheckerContext.getStatusChecker;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductServiceImpl implements ProductService {

    //TODO we can have new enum that includes all expected query params name ProductApiParamsEnum
    // FIELDS("fields"),  LIMIT("limit"), PRODUCT_ORDER_ITEM_PRODUCT_ID("productOrderItem.productOrderId")
    // to use these constants easily in the code instead of write them again.

    private static final String[] requiredFields = REQUIRED_PRODUCT_FIELDS.toArray(new String[0]);
    private final ApplicationConfigProperties applicationConfigProperties;
    private final CatalogService catalogService;
    private final ProductMapper productMapper;
    private final HrefGeneratorService generatorHref;
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private final FilterQueryService filterQueryService;
    private final ProductEntityValidationHandler validator;
    private final TransactionTemplate transactionTemplate;
    private final CustomProductRepository customProductRepository;

    private final ProductDeleteEventProducerImpl productDeleteEventProducer;

    private final ProductCreateEventProducerImpl productCreateEventProducer;

    private final ProductStateChangeEventProducerImpl productStateChangeEventProducer;
    private final ObjectMapper objectMapper;
    @Value("${config.pagination.limit}")
    private int paginationLimit;

    public static String[] getIgnoredPropertyNames(Object source) {
        //TODO it can be moved to Static utility class that can be helper for whole cpib
        // CPIBApisUtility
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyProperties = new HashSet<>();
        //use streams instead of
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyProperties.add(pd.getName());
            } else {
                if (srcValue instanceof List && ((List<?>) srcValue).isEmpty()) {
                    emptyProperties.add(pd.getName());
                }
            }
        }
        emptyProperties.add(QueryFields.START_DATE);
        emptyProperties.addAll(NOT_PATCHABLE_ATTRIBUTES.values());
        return emptyProperties.toArray(new String[0]);
    }

    private void setDefaultFieldValues(Product product) {
        if (product.getName() == null) {
            String name;
            if (product.getProductOffering() != null) {
                name = product.getProductOffering().getName();
            } else {
                name = (product.getProductSpecification() != null) ? product.getProductSpecification().getName() : null;
            }
            product.setName(name);
        }
        if (Objects.isNull(product.getCreationDate())) {
            product.setCreationDate(OffsetDateTime.now());
        }
        addStatusChange(product, product.getStatus());
        addOperationalStatusChange(product, product.getOperationalStatus());
    }

    private void addStatusChange(Product product, ProductStatusType status) {
        StatusChange statusChange = StatusChange.builder().changeDate(OffsetDateTime.now()).status(status).build();
        List<StatusChange> stateChangeList = Objects.nonNull(product.getStatusChange()) ? product.getStatusChange() : new ArrayList<>();
        stateChangeList.add(statusChange);
        product.setStatusChange(stateChangeList);
    }

    private void addOperationalStatusChange(Product product, ProductOperationalStatusType operationalStatus) {
        OperationalStatusChange operationalStatusChange = OperationalStatusChange.builder().changeDate(OffsetDateTime.now()).status(operationalStatus).build();
        List<OperationalStatusChange> operationalStateChangeList = Objects.nonNull(product.getOperationalStatusChange()) ? product.getOperationalStatusChange() : new ArrayList<>();
        operationalStateChangeList.add(operationalStatusChange);
        product.setOperationalStatusChange(operationalStateChangeList);
    }

    private List<ProductRelationshipEntity> retrieveAndRemoveRelationShipHasParentFrom(ProductEntity product) {
        List<ProductRelationshipEntity> hasParentList = ProductEntityUtil.getProductRelationshipsByType(product, ProductRelationshipType.HASPARENT);

        Optional.ofNullable(product.getProductRelationship())
                .ifPresent(relationships -> relationships.removeAll(hasParentList));

        return hasParentList;
    }


    private ProductRelationshipEntity createRelationShipFrom(ProductEntity productEntity, ProductRelationshipEntity hasParent) {
        ProductEntity parent = getProductEntity(hasParent.getProduct().getId(), null);
        if (ProductEntityUtil.isSellsRelationShip(productEntity, parent)) {
            return new ProductRelationshipEntity(ProductRelationshipType.SELLS.getValue(), ProductRefEntity.builder().id(new ObjectId(productEntity.getId())).build());
        }
        return new ProductRelationshipEntity(ProductRelationshipType.BUNDLES.getValue(), ProductRefEntity.builder().id((new ObjectId(productEntity.getId()))).build());
    }

    private void checkParentProductStatus(ProductEntity product) {

        log.info("product with id: {} exist", product.getId());
        List<ProductStatusType> validStatus = List.of(ProductStatusType.ACTIVE, ProductStatusType.CREATED);
        if (!validStatus.contains(product.getStatus())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID);
        }
        List<ProductOperationalStatusType> validOpStatus = List.of(ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.PENDINGCANCEL);
        if (validOpStatus.contains(product.getOperationalStatus())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    THE_OPERATIONAL_STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID);
        }
    }
    private void validatePriceTypeForInstallmentCharge(ProductEntity product) {
        List<ProductPriceEntity> productPrices = product.getProductPrice();

        if (productPrices == null) {
            return;
        }

        for (ProductPriceEntity price : productPrices) {
            boolean isInstallmentCharge = INSTALLMENT_CHARGE.equals(price.getAtType());
            boolean isPriceTypeMissing = price.getPriceType() == null || price.getPriceType().isBlank();
            if (!isInstallmentCharge && isPriceTypeMissing) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PRICE_TYPE_IS_MANDATORY_EXCEPT_FOR_NON_INSTALLMENT_CHARGE);
            }
        }
    }

    private void validateProduct(ProductEntity product) {
        checkOnlyOneOccurrenceValidityCharacteristic(product);
        validateDateTimeCharacteristic(product);
        validatePriceTypeForInstallmentCharge(product);
        validator.validate(product);
    }


    /**
     * @param productEntity : the product entity
     * @return an optional ProductRelationshipPatch which can be empty if no hasParentRelationship is parent,
     * else it should contain the hasparent relationship to be pushed,
     * also it adds the parentContract relationship to the product that is reflected from the parent, meaning if the parent is root than "parentContract" is "parentID"
     * else "parentContract" is the "parentContract" of the "parent".
     */
    private Optional<ProductServiceImpl.ProductRelationshipPatch> validateAndRetrieveHasParentRelationshipPatches(ProductEntity productEntity) {
        return retrieveAndRemoveRelationShipHasParentFrom(productEntity).stream()
                .findFirst()
                .map(hasParentRelationshipEntity -> {
                    ProductRefEntity productRef = hasParentRelationshipEntity.getProduct();
                    if (Objects.isNull(productRef.getId())) {
                        log.error("Product reference ID is missing: {}", productRef);
                        throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PRODUCT_REF_MISSING);
                    }

                    ProductEntity parent = getProductEntity(productRef.getId(), null);
                    checkParentProductStatus(parent);
                    return new ProductServiceImpl.ProductRelationshipPatch(productRef.getId(), createRelationShipFrom(productEntity, hasParentRelationshipEntity));
                });
    }


    public Product getProductById(String id, String fields) {
        ProductEntity product = getProductEntity(id, fields);
        log.debug("Product exists: {}", id);
        Product productDto = productMapper.toDtoWithProductIdOnly(product);
        generatorHref.generateHrefProductRelationships(Collections.singletonList(productDto));
        return productDto;
    }

    private String generateHref(ProductEntity productEntity) {
        Product productDto = productMapper.toDtoWithProductIdOnly(productEntity);
        generatorHref.generateHrefProductRelationships(Collections.singletonList(productDto));
        return productDto.getHref();
    }

    @Override
    public ProductEntity getProductEntity(String id, String fields) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Product.Fields.id).is(id));
        String[] fieldArray = filterQueryService.extractFields(fields, requiredFields);
        query.fields().include(fieldArray);
        ProductEntity product = mongoTemplate.findOne(query, ProductEntity.class);
        if (product == null) {
            log.error("The product with id {} does not exist", id);
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, id));
        }
        calculateTaxIncludedAmount(product);
        return product;
    }

    private ProductEntity getProductEntity(ObjectId id, String fields) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Product.Fields.id).is(id));
        String[] fieldArray = filterQueryService.extractFields(fields, requiredFields);
        query.fields().include(fieldArray);
        ProductEntity product = mongoTemplate.findOne(query, ProductEntity.class);
        if (product == null) {
            log.error("The product with id {} does not exist", id);
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(),
                    String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, id));
        }
        return product;
    }

    private static BigDecimal getPriceValue(PriceEntity p) {
        if (p == null) {
            return null;
        }
        MoneyEntity df = p.getDutyFreeAmount();
        if (df != null && df.getValue() != null && df.getValue() > 0) {
            return BigDecimal.valueOf(df.getValue());
        }
        MoneyEntity ti = p.getTaxIncludedAmount();
        if (ti != null && ti.getValue() != null && ti.getValue() > 0) {
            return BigDecimal.valueOf(ti.getValue());
        }
        return null;
    }

    private static BigDecimal alterationValueOnBase(PriceEntity p, BigDecimal base) {
        if (p == null || base == null || base.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (p.getPercentage() != null && p.getPercentage() > 0) {
            return base.multiply(BigDecimal.valueOf(p.getPercentage()))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        }

        BigDecimal flat = getPriceValue(p);
        return flat != null ? flat : BigDecimal.ZERO;
    }

    private static boolean isValidAlterationAny(PriceAlterationEntity alteration) {
        if (!hasRequiredValues(alteration)) {
            return false;
        }

        TimePeriodEntity vf = alteration.getValidFor();
        if (vf != null) {
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

            // 🟢 For DISCOUNT → only apply if it's in the future (not expired)
            if (DISCOUNT.equalsIgnoreCase(alteration.getAtType())) {
                if (vf.getStartDateTime() != null && now.isBefore(vf.getStartDateTime())) {
                    return false;
                }
                if (vf.getEndDateTime() != null && now.isAfter(vf.getEndDateTime())) {
                    return false;
                }
            }

            // 🟡 For TAX → always valid, even if in the past
            //    (you could still keep optional validation if desired)
        }

        return true;
    }

    private static boolean hasRequiredValues(PriceAlterationEntity alteration) {
        Predicate<String> isValidAtType = atType -> DISCOUNT.equalsIgnoreCase(atType) || TAX.equalsIgnoreCase(atType);
        Predicate<PriceEntity> isValidPriceValue = price -> (price.getPercentage() != null && price.getPercentage() > 0)
                || getPriceValue(price) != null;

        return Objects.nonNull(alteration) && Objects.nonNull(alteration.getPrice())
                && isValidAtType.test(alteration.getAtType())
                && isValidPriceValue.test(alteration.getPrice());
    }

    private static BigDecimal applyAllByPriority(ProductPriceEntity priceEntity, BigDecimal startAmount) {
        if (priceEntity == null || priceEntity.getProductPriceAlteration() == null) {
            return startAmount;
        }

        List<PriceAlterationEntity> validAlterations =
                priceEntity.getProductPriceAlteration().stream()
                        .filter(ProductServiceImpl::isValidAlterationAny)
                        .toList();

        if (validAlterations.isEmpty()) {
            return startAmount;
        }

        BigDecimal current = applyDiscounts(validAlterations, startAmount);
        BigDecimal taxes = applyTaxes(validAlterations, current);

        return current.add(taxes);
    }
    private static BigDecimal applyDiscounts(List<PriceAlterationEntity> alterations, BigDecimal base) {

        List<PriceAlterationEntity> discounts = alterations.stream()
                .filter(a -> DISCOUNT.equalsIgnoreCase(a.getAtType()) && a.getPriority() != null)
                .toList();

        if (discounts.isEmpty()) {
            return base;
        }

        Map<Integer, List<PriceAlterationEntity>> byPriority = discounts.stream().collect(Collectors.groupingBy(PriceAlterationEntity::getPriority));

        BigDecimal current = base;

        for (Integer prio : byPriority.keySet().stream().sorted().toList()) {
            BigDecimal delta = BigDecimal.ZERO;

            for (PriceAlterationEntity a : byPriority.get(prio)) {
                delta = delta.add(alterationValueOnBase(a.getPrice(), current));
            }

            current = current.subtract(delta);
        }

        return current;
    }
    private static BigDecimal applyTaxes(List<PriceAlterationEntity> alterations, BigDecimal base) {

        BigDecimal totalTax = BigDecimal.ZERO;

        for (PriceAlterationEntity tax : alterations) {
            if (TAX.equalsIgnoreCase(tax.getAtType())) {
                totalTax = totalTax.add(alterationValueOnBase(tax.getPrice(), base));
            }
        }

        return totalTax;
    }

    public static void calculateTaxIncludedAmount(ProductEntity product) {
        if (product == null || product.getProductPrice() == null) {
            return;
        }

        for (ProductPriceEntity priceEntity : product.getProductPrice()) {

            PriceEntity basePrice = priceEntity.getPrice();
            if (basePrice == null || basePrice.getDutyFreeAmount() == null) {
                continue;
            }

            MoneyEntity dutyFree = basePrice.getDutyFreeAmount();
            BigDecimal dutyFreeAmount = BigDecimal.valueOf(dutyFree.getValue());

            BigDecimal finalAmount = applyAllByPriority(priceEntity, dutyFreeAmount);

            String currencyCode = dutyFree.getUnit() != null ? dutyFree.getUnit() : "EUR";

            BigDecimal rounded = CurrencyUtils.roundByCurrency(finalAmount.doubleValue(), currencyCode);

            basePrice.setTaxIncludedAmount(new MoneyEntity(currencyCode, rounded.floatValue()));
        }
    }

    @Override
    public List<ProductEntity> getProducts(PageableTMF pageable) {
        log.debug("Getting products with attributes: {}", pageable);

        Query query = filterQueryService.createAndValidateQuery(pageable, new ProductFieldFetcher(), requiredFields);

        // Check if the requested offset exceeds the allowed pagination limit
        if (query.getSkip() >= paginationLimit) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), String.format(DOCUMENT_COUNT_EXCEEDED_PAGINATION_LIMIT, paginationLimit));
        }

        // Check if the combined skip and limit exceed the pagination limit
        if (query.getSkip() + query.getLimit() > paginationLimit) {
            query.limit((int) (paginationLimit - query.getSkip()));
        }

        return mongoTemplate.find(query, ProductEntity.class);
    }


    @Override
    public int getTotalCount(MultiValueMap<String, Object> filter) {
        Query query = filterQueryService.createQuery(filter);
        query.limit(paginationLimit);
        int totalCount = (int) mongoTemplate.count(query, ProductEntity.class);
        log.debug("Total count of products: {}", totalCount);
        return totalCount;
    }

    @Override
    public Product updateProduct(String id, Product product) {
        ProductEntity productPatched = productMapper.toEntity(product);
        if (Objects.isNull(productPatched)) {
            throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), INVALID_PRODUCT);
        }
        ProductEntity existingProduct = this.getProductEntity(id, null);
        final String href = this.generateHref(existingProduct);
        existingProduct.setHref(href);
        final ProductStatusType oldStatus = existingProduct.getStatus();
        validateForUnpatchableAttributes(existingProduct, productPatched);
        if (productPatched.getStatus() != null || productPatched.getOperationalStatus() != null) {
            ProductStatusType statusNew = Objects.nonNull(productPatched.getStatus()) ? productPatched.getStatus() : existingProduct.getStatus();
            ProductOperationalStatusType operationalStatusNew = Objects.nonNull(productPatched.getOperationalStatus()) ? productPatched.getOperationalStatus() : existingProduct.getOperationalStatus();
            getStatusChecker(productPatched.getAtType()).validateProductStatus(existingProduct.getStatus(), existingProduct.getOperationalStatus(), statusNew, operationalStatusNew);
        }
        BeanUtils.copyProperties(productPatched, existingProduct, getIgnoredPropertyNames(productPatched));
        ProductDatesChecker.validateProductDates(existingProduct);
        StatusChecker.addStatusChange(existingProduct);
        if (productPatched.getStatus() != null && productPatched.getStatus().equals(ProductStatusType.ACTIVE)) {
            ProductDatesChecker.setTerminationDate(existingProduct);
        }

        productRepository.save(existingProduct);
        Product productById1 = getProductById(id, null);
        if (!oldStatus.equals(productById1.getStatus())) {
            productStateChangeEventProducer.publishEvent(StateChangeProduct.fromProduct(productById1, oldStatus), PRODUCT_STATE_CHANGE.name(), PRODUCT_STATE_CHANGE.getDomain());
        }
        return productById1;
    }

    @Override
    @Transactional
    public List<ProductEntity> updateProducts(List<ProductEntity> patchedProducts) {
        ProductPriceNormalizationService.normalizeBeforeSave(patchedProducts);
        return productRepository.saveAll(patchedProducts);
    }

    @Override
    public List<ProductEntity> getListOfProductEntityBy(Set<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Product.Fields.id).in(ids));
        List<ProductEntity> products = mongoTemplate.find(query, ProductEntity.class);
        List<String> existingIds = products.stream().map(ProductEntity::getId).toList();
        List<String> missingIds = ids.stream().filter(id -> !existingIds.contains(id)).toList();
        if (!CollectionUtils.isEmpty(missingIds)) {
            log.error("The product with ids {} does not exist", ids);
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THE_PRODUCTS_WITH_IDS_S_DOES_NOT_EXIST, missingIds));
        }
        return products;
    }

    @Override
    public List<ProductEntity> getListOfProductEntityByIds(Set<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Product.Fields.id).in(ids));
        return mongoTemplate.find(query, ProductEntity.class);
    }

    @Transactional
    @Override
    public void deleteProduct(String id) {
        ProductEntity productEntity = getProductEntity(id, null);
        mongoTemplate.remove(productEntity);
        customProductRepository.cleanUpReferences(id);
        Product productPublishEvent = productMapper.toDtoWithProductIdOnly(productEntity);
        productDeleteEventProducer.publishEvent(productPublishEvent, PRODUCT_DELETE_EVENT.getTitle(), PRODUCT_DELETE_EVENT.getDomain());

    }


    private ProductEntity wrapProductWithItsParentFromHasParentRelationship(ProductEntity product, ProductRelationship hasParent) {
        ProductEntity parent = getProductEntity(((ProductRef) hasParent.getProduct()).getId(), null);
        if (CollectionUtils.isEmpty(parent.getProductRelationship())) {
            parent.setProductRelationship(new ArrayList<>());
        }
        if (ProductEntityUtil.isSellsRelationShip(product, parent)) {
            parent.getProductRelationship().add(new ProductRelationshipEntity(ProductRelationshipType.SELLS.getValue(), new ProductRefEntity(product.getId())));
        } else {
            parent.getProductRelationship().add(new ProductRelationshipEntity(ProductRelationshipType.BUNDLES.getValue(), new ProductRefEntity(product.getId())));
        }
        return parent;
    }

    @SneakyThrows
    @Override
    public Product createProduct(Product product) {
        List<Product> allProducts = new ArrayList<>();
        List<ProductRelationship> hasParentRelationship = getProductRelationshipsByType(product, ProductRelationshipType.HASPARENT);
        hasParentRelationship = hasParentRelationship.stream().filter(productRelationship -> ProductTypeEnum.PRODUCT_REF.getValue().equals(productRelationship.getProduct().getAtType())).toList();
        extractProductsList(product, allProducts);
        for (Product p : allProducts) {
            p.setId(new ObjectId().toString());
            setDefaultFieldValues(p);
        }

        String rootProductId = addRootProductRelationship(allProducts);

        List<ProductEntity> collect = allProducts.stream().map(productMapper::toEntity).toList();
        List<ProductRelationshipPatch> productRelationshipPatches = new ArrayList<>();
        processProductEntities(collect, allProducts, rootProductId, productRelationshipPatches);
        performCatalogCheck(collect, hasParentRelationship);

        ProductPriceNormalizationService.normalizeBeforeSave(collect);
        transactionTemplate.execute(status -> {
            Collection<ProductEntity> productEntities = mongoTemplate.insertAll(collect);

            for (ProductServiceImpl.ProductRelationshipPatch productRelationshipPatch : productRelationshipPatches) {
                productRepository.findAndPushProductRelationshipByIdIn(Set.of(productRelationshipPatch.id()), productRelationshipPatch.relationship());
            }
            // Injection des champs générés + prix normalisé
            Map<String, ProductEntity> entityById = productEntities.stream()
                    .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

            allProducts.forEach(p -> {
                ProductEntity entity = entityById.get(p.getId());
                if (entity != null) {
                    p.setCreationDate(entity.getCreationDate());
                    p.setLastUpdateDate(entity.getLastUpdateDate());
                    p.setName(entity.getName());

                    if (entity.getProductPrice() != null) {
                        p.setProductPrice(
                                entity.getProductPrice().stream()
                                        .map(productMapper::productPriceFromEntity)
                                        .toList()
                        );
                    }
                }
            });
            return null;

        });
        productCreateEventProducer.publishEvents(new HashSet<>(allProducts), PRODUCT_CREATE_EVENT.getTitle(), PRODUCT_CREATE_EVENT.getDomain());
        return allProducts.get(0);
    }
    private void processProductEntities(List<ProductEntity> collect, List<Product> allProducts, String rootProductId,  List<ProductRelationshipPatch> productRelationshipPatches) {

        for (ProductEntity productEntity : collect) {
            if (productEntity.getId().equals(rootProductId)) {
                productEntity.setIsRootProduct(true);
            }
            Optional<ProductRelationshipPatch> productRelationshipPatch = validateAndRetrieveHasParentRelationshipPatches(productEntity);
            productRelationshipPatch.ifPresent(productRelationshipPatches::add);

            allProducts.stream()
                    .filter(product1 -> product1.getId().equals(productEntity.getId()))
                    .findAny().ifPresent(product1 -> removeRelationShipFromProduct(product1, ProductRelationshipType.HASPARENT));

            validateProduct(productEntity);

        }

    }
    private void performCatalogCheck(List<ProductEntity> collect, List<ProductRelationship> hasParentRelationship) {
        if (applicationConfigProperties.isEnableProductCatalogCheck()) {
            // Assuming that modification use case will have a simple product with hasParent relationship on the first level and so will return an error if we have any more products
            if (!hasParentRelationship.isEmpty() && collect.size() < hasParentRelationship.size()) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), INVALID_MODIFICATION_USE_CASE);
            }
            List<ProductEntity> collectionForValidation = collect;
            if (!hasParentRelationship.isEmpty()) {
                collectionForValidation = new ArrayList<>(collectionForValidation);
                // add it as first element since it is the parent,
                collectionForValidation.add(0, wrapProductWithItsParentFromHasParentRelationship(collectionForValidation.get(0), hasParentRelationship.get(0)));
            }
            catalogService.checkCatalogService(collectionForValidation.get(0), collectionForValidation);
        }
    }

    private String addRootProductRelationship(List<Product> allProducts) {
        String parentContractId = getParentId(allProducts.stream().findFirst().orElseThrow(() ->
                new ProductInventoryException(HttpStatus.BAD_REQUEST, MISSING_INPUT.getCode(), MISSING_INPUT.getStatus(), THE_LIST_OF_PRODUCTS_SHOULD_NOT_BE_EMPTY)));
        for (Product product1 : allProducts) {
            if (product1.getId().equals(parentContractId)) {
                continue;
            }
            if (product1.getProductRelationship() == null) {
                product1.setProductRelationship(new ArrayList<>());
            }

            product1.getProductRelationship().add(
                    ProductRelationship.builder()
                            .relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue())
                            .product(ProductRef.builder()
                                    .id(parentContractId)
                                    .build())
                            .build()
            );
        }
        return parentContractId;
    }

    private String getParentId(Product product) {
        //this should work assuming that HasParent relationships cannot be embedded, otherwise we should change the logic
        List<ProductRelationship> productRelationShipBy = ProductEntityUtil.getProductRelationshipsByType(product, ProductRelationshipType.HASPARENT);
        if (!productRelationShipBy.isEmpty()) {
            String hasParentId = ((ProductRef) productRelationShipBy.get(0).getProduct()).getId();
            Product parent = getProductById(hasParentId, null);
            List<ProductRelationship> productRelationshipsByType = getProductRelationshipsByType(parent, ProductRelationshipType.ROOTPRODUCT);
            if (!productRelationshipsByType.isEmpty()) {
                return ((ProductRef) productRelationshipsByType.get(0).getProduct()).getId();
            } else {
                return parent.getId();
            }
        } else {
            return (product.getId());
        }
    }

    private void extractProductsList(Product rootProduct, List<Product> allProducts) {
        if (rootProduct == null) {
            return;
        }
        allProducts.add(rootProduct);
        if (!CollectionUtils.isEmpty(rootProduct.getProductRelationship())) {
            rootProduct
                    .getProductRelationship()
                    .stream()
                    .map(ProductRelationship::getProduct)
                    .filter(product -> !ProductTypeEnum.PRODUCT_REF.getValue().equals(product.getAtType()))
                    .map(Product.class::cast)
                    .forEach(product -> extractProductsList(product, allProducts));
        }
    }

    record ProductRelationshipPatch(ObjectId id, ProductRelationshipEntity relationship) {  //NOSONAR
        // this is a record, it does not need any implementation
    }
}

