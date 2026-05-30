// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.config.AppConfig;
import com.orange.discobole.ordermanagement.orderinventory.controller.ProductOrderControllerImpl;
import com.orange.discobole.ordermanagement.orderinventory.domain.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.ProductOrderResponse;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.SortEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.BusinessException;
import com.orange.discobole.ordermanagement.orderinventory.repository.ProductOrderRepository;
import com.orange.discobole.ordermanagement.orderinventory.service.FilterQueryService;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.ProductOrderAttributeValueChangeEventProducer;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.ProductOrderEventProducer;
import com.orange.discobole.ordermanagement.orderinventory.service.mapper.ProductOrderItemMapper;
import com.orange.discobole.ordermanagement.orderinventory.service.mapper.ProductOrderMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT;
import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT;
import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.*;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum.*;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages.INVALID_CANCELLATION_REASON_CANCELLATION_DATE_ORDER_DATE;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages.MINIMUM_PRODUCT_ORDER_ITEM_REQUIRED;
import static java.lang.String.format;

/**
 * Service Implementation for managing {@link ProductOrder}.
 */
@Service
public class ProductOrderServiceImpl implements ProductOrderService {
    private final Logger log = LoggerFactory.getLogger(ProductOrderServiceImpl.class);
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderEventProducer productOrderEventProducer;
    private final ProductOrderAttributeValueChangeEventProducer productOrderAttributeValueChangeEventProducer;
    private final MongoTemplate mongoTemplate;
    private final ProductOrderMapper productOrderMapper;
    private final ProductOrderItemMapper productOrderItemMapper;
    private final FilterQueryService filterQueryService;
    private final AppConfig appConfig;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderServiceImpl(
            ProductOrderRepository productOrderRepository,
            ProductOrderEventProducer productOrderEventProducer,
            ProductOrderAttributeValueChangeEventProducer productOrderAttributeValueChangeEventProducer,
            ProductOrderMapper productOrderMapper, MongoTemplate mongoTemplate,
            ProductOrderItemMapper productOrderItemMapper, FilterQueryService filterQueryService, AppConfig appConfig) {
        this.productOrderRepository = productOrderRepository;
        this.productOrderEventProducer = productOrderEventProducer;
        this.productOrderAttributeValueChangeEventProducer = productOrderAttributeValueChangeEventProducer;
        this.productOrderMapper = productOrderMapper;
        this.mongoTemplate = mongoTemplate;
        this.productOrderItemMapper = productOrderItemMapper;
        this.filterQueryService = filterQueryService;
        this.appConfig = appConfig;
    }

    @Override
    public void updateState(ProductOrder productOrder) {
        log.debug("Request to update ProductOrder state: {}", productOrder);

        if (productOrder.getId() != null) {
            ProductOrderEntity productOrderEntity = productOrderMapper.mapToEntity(productOrder);

            Query stateQuery = new Query(Criteria.where(ID_FIELD).is(productOrderEntity.getId()));
            stateQuery.fields().include("state");

            ProductOrderStateType oldState = Objects.requireNonNull(mongoTemplate.findOne(stateQuery, ProductOrderEntity.class)).getState();

            ProductOrderStateType newState = productOrderEntity.getState();

            ProductOrderItemStateType itemState = mapOrderStateToItemState(newState);

            Query query = new Query(Criteria.where("id").is(productOrderEntity.getId()));

            Update update = new Update()
                    .set(PRODUCT_ORDER_STATE_FIELD, newState.getValue())
                    .set(PRODUCT_ORDER_ITEM_STATE_FIELD, itemState.getValue());

            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
            ProductOrderEntity updatedProductOrderEntity = mongoTemplate.findAndModify(query, update, options, ProductOrderEntity.class);


            if (!(oldState.equals(ProductOrderStateType.DRAFT) && newState.equals(ProductOrderStateType.CANCELLED))) {
                ProductOrder updatedProductOrder = productOrderMapper.mapToDto(updatedProductOrderEntity);
                productOrderEventProducer.publishEvent(updatedProductOrder, PRODUCT_ORDER_STATE_CHANGE_EVENT);
            }
            processCompletedOrderItems(updatedProductOrderEntity);
        }
    }

    private void processCompletedOrderItems(ProductOrderEntity productOrderEntity) {
        if (Objects.nonNull(productOrderEntity) && ProductOrderStateType.ACCEPTED.equals(productOrderEntity.getState())) {
            processNoChangeAtomicItemsStateUpdate(productOrderEntity);
            processBundleItemWithoutChildrenStateUpdate(productOrderEntity);
        }
    }

    private void processNoChangeAtomicItemsStateUpdate(ProductOrderEntity productOrderEntity) {
        Set<ProductOrderItemEntity> noChangeAtomicItems = productOrderEntity.getProductOrderItem().stream()
                .filter(productOrderItem -> ItemActionType.NOCHANGE.equals(productOrderItem.getAction())
                        && ATOMIC_PRODUCT_OFFERING.equals(productOrderItem.getProductOffering().getAtType()))
                .collect(Collectors.toSet());

        processProductOrderItemsStateUpdate(productOrderEntity, noChangeAtomicItems);
    }

    private void processProductOrderItemsStateUpdate(ProductOrderEntity productOrderEntity, Set<ProductOrderItemEntity> productOrderItems) {
        if (!productOrderItems.isEmpty()) {
            productOrderItems.forEach(productOrderItem -> {
                List<ProductOrderItemEntity> updatedOrderItems = new ArrayList<>();
                updatedOrderItems.add(productOrderItem);
                ProductOrderStateType initialOrderState = productOrderEntity.getState();

                updateNoChangeItemStateHierarchy(productOrderEntity, productOrderItem, updatedOrderItems);
                updateProductOrderState(productOrderEntity);

                publishEvents(productOrderEntity, initialOrderState, updatedOrderItems);
            });

            productOrderRepository.save(productOrderEntity);
        }
    }

    private void processBundleItemWithoutChildrenStateUpdate(ProductOrderEntity productOrderEntity) {
        Set<ProductOrderItemEntity> bundleItemsWithoutChildren = productOrderEntity.getProductOrderItem()
                .stream()
                .filter(productOrderItem -> !ATOMIC_PRODUCT_OFFERING.equals(productOrderItem.getProductOffering().getAtType()))
                .filter(productOrderItem -> getProductOrderItemChildren(productOrderEntity, productOrderItem).isEmpty())
                .collect(Collectors.toSet());

        processProductOrderItemsStateUpdate(productOrderEntity, bundleItemsWithoutChildren);

    }

    private void updateNoChangeItemStateHierarchy(ProductOrderEntity productOrderEntity, ProductOrderItemEntity productOrderItem, List<ProductOrderItemEntity> updatedItems) {
        productOrderItem.setState(ProductOrderItemStateType.COMPLETED);
        List<ProductOrderItemEntity> productOrderItemParents = getProductOrderItemParents(productOrderEntity, productOrderItem);
        processUpdateStateHierarchyRecursively(productOrderEntity, productOrderItemParents, updatedItems);
    }

    private void processUpdateStateHierarchyRecursively(ProductOrderEntity productOrderEntity, List<ProductOrderItemEntity> productOrderItemParents, List<ProductOrderItemEntity> updatedItems) {
        if (CollectionUtils.isEmpty(productOrderItemParents)) {
            return;
        }

        for (ProductOrderItemEntity productOrderItemEntity : productOrderItemParents) {
            updateOrderItemStateIfChanged(productOrderEntity, productOrderItemEntity, updatedItems);

            List<ProductOrderItemEntity> newProductOrderItemParents = getProductOrderItemParents(productOrderEntity, productOrderItemEntity);
            processUpdateStateHierarchyRecursively(productOrderEntity, newProductOrderItemParents, updatedItems);
        }
    }

    private void updateOrderItemStateIfChanged(ProductOrderEntity productOrderEntity, ProductOrderItemEntity productOrderItemEntity, List<ProductOrderItemEntity> updatedItems) {
        ProductOrderItemStateType oldState = productOrderItemEntity.getState();

        List<ProductOrderItemEntity> children = getProductOrderItemChildren(productOrderEntity, productOrderItemEntity);
        updateProductOrderItemState(productOrderItemEntity, children);

        if (productOrderItemEntity.getState() != oldState) {
            updatedItems.add(productOrderItemEntity);
        }
    }

    private ProductOrderItem getProductOrderItemById(ProductOrder productOrder, String id) {
        return productOrder.getProductOrderItem()
                .stream()
                .filter(productOrderItem -> id.equals(productOrderItem.getId()))
                .findFirst()
                .orElse(null);

    }

    @Override
    public void updateProductOrderRelatedParties(ProductOrder productOrder) {
        log.debug("Request to update product order related party: {}", productOrder.getRelatedParty());
        Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId()));
        ProductOrderEntity productOrderEntity = productOrderMapper.mapToEntity(productOrder);
        Update update = new Update()
                .set(RELATED_PARTY_FIELD, productOrderEntity.getRelatedParty());
        mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
    }

    @Override
    public ProductOrder saveProductOrder(ProductOrder productOrder) throws ProductOrderInventoryException {
        log.debug("Request to save product order : {}", productOrder);
        ProductOrderEntity productOrderEntity = productOrderMapper.mapToEntity(productOrder);
        if (CollectionUtils.isEmpty(productOrderEntity.getProductOrderItem())) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    MISSING_INPUT.getCode(),
                    MISSING_INPUT.getStatus(),
                    MINIMUM_PRODUCT_ORDER_ITEM_REQUIRED
            );
        } else if (productOrderEntity.getCancellationDate() != null || productOrderEntity.getCancellationReason() != null) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_INPUT.getCode(),
                    INVALID_INPUT.getStatus(),
                    INVALID_CANCELLATION_REASON_CANCELLATION_DATE_ORDER_DATE
            );
        }

        if (Objects.isNull(productOrderEntity.getCreationDate())) {
            productOrderEntity.setCreationDate(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        }

        if (Objects.isNull(productOrderEntity.getState())) {
            productOrderEntity.setState(ProductOrderStateType.DRAFT);
        }

        productOrderEntity = productOrderRepository.save(productOrderEntity);

        ProductOrder savedProductOrder = productOrderMapper.mapToDto(productOrderEntity);
        addHypermediaLinks(savedProductOrder);

        return savedProductOrder;
    }

    @Override
    public ProductOrder getProductOrderById(String id, String fields) {
        log.debug("Request to get product order with id");
        Query query = new Query();
        query.addCriteria(Criteria.where(ID_FIELD).is(id));
        String[] fieldArray = filterQueryService.extractAndCacheFields(fields);
        filterQueryService.validateFieldsToFetch(fieldArray);

        query.fields().include(fieldArray);

        ProductOrderEntity productOrderEntity = mongoTemplate.findOne(query, ProductOrderEntity.class);

        if (productOrderEntity == null) {
            log.error("The product order with id: {} does not exist", id);
            throw new ProductOrderInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus());
        }

        log.debug("Product order exists with id : {}", id);
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        addHypermediaLinks(productOrder);
        return productOrder;
    }

    @Override
    public ProductOrderResponse getProductOrders(MultiValueMap<String, Object> queryParametersMap) {
        log.debug("Request to get product orders");

        Integer requestedOffset = extractIntegerParameter(queryParametersMap, OFFSET);
        Integer requestedLimit = extractIntegerParameter(queryParametersMap, LIMIT);
        Integer effectiveLimit = (requestedLimit != null) ? requestedLimit : appConfig.getDefaultPageSize();

        validatePaginationParameters(requestedOffset, effectiveLimit);

        String fieldsParameter = extractStringParameter(queryParametersMap);
        List<SortEnum> sortParameter = extractSortParameter(queryParametersMap);

        removePaginationAndProjectionParameters(queryParametersMap);

        String[] projectionFields = filterQueryService.extractAndCacheFields(fieldsParameter);
        Query mongoQuery = filterQueryService.createOptimizedQuery(queryParametersMap, projectionFields);

        long totalRecordCount = mongoTemplate.count(mongoQuery, ProductOrderEntity.class);

        if (requestedOffset != null && requestedOffset > totalRecordCount) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    BusinessException.ERROR_IN_OFFSET
            );
        }

        configurePaginationAndSorting(mongoQuery, requestedOffset, effectiveLimit, sortParameter);

        List<ProductOrderEntity> productOrderEntities = mongoTemplate.find(mongoQuery, ProductOrderEntity.class);

        List<ProductOrder> productOrders = mapEntitiesToDTOsWithLinks(productOrderEntities);

        HttpHeaders responseHeaders = buildResponseHeaders(totalRecordCount, productOrders.size());
        HttpStatus httpStatus = determineHttpStatus(productOrders.size(), totalRecordCount);

        log.debug("Total count of product orders: {}", totalRecordCount);

        return new ProductOrderResponse(responseHeaders, httpStatus, productOrders);
    }

    @Override
    public void updateRequestedCompletionDate(ProductOrder productOrder) {
        log.debug("Request to update the requestedCompletionDate: {}", productOrder);

        if (productOrder.getId() != null) {
            Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId()));
            Update update = new Update().set(REQUESTED_COMPLETION_DATE, productOrder.getRequestedCompletionDate());

            mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
        }
    }

    private void removePaginationAndProjectionParameters(MultiValueMap<String, Object> queryParametersMap) {
        queryParametersMap.remove(OFFSET);
        queryParametersMap.remove(LIMIT);
        queryParametersMap.remove(FIELDS);
        queryParametersMap.remove(SORT);
    }

    private Integer extractIntegerParameter(MultiValueMap<String, Object> parametersMap, String parameterKey) {
        List<Object> parameterValues = parametersMap.get(parameterKey);
        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        }

        Object firstValue = parameterValues.get(0);
        if (firstValue == null) {
            return null;
        }

        try {
            return Integer.parseInt(firstValue.toString());
        } catch (NumberFormatException exception) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    format(BusinessException.VALUE_IS_NOT_A_VALID_INTEGER, parameterKey)
            );
        }
    }

    private String extractStringParameter(MultiValueMap<String, Object> parametersMap) {
        List<Object> parameterValues = parametersMap.get(FIELDS);
        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        }
        Object firstValue = parameterValues.get(0);
        return (firstValue != null) ? firstValue.toString() : null;
    }

    private List<SortEnum> extractSortParameter(MultiValueMap<String, Object> parametersMap) {
        List<Object> parameterValues = parametersMap.get(SORT);
        return Optional.ofNullable(parameterValues)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .filter(SortEnum.class::isInstance)
                .map(SortEnum.class::cast)
                .toList();
    }

    private void validatePaginationParameters(@Nullable Integer offsetValue, @Nullable Integer limitValue) {
        boolean isOffsetNegative = offsetValue != null && offsetValue < 0;
        boolean isLimitNegative = limitValue != null && limitValue < 0;

        if (isOffsetNegative && isLimitNegative) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    BusinessException.OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE
            );
        }

        if (isOffsetNegative) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    BusinessException.OFFSET_SHOULD_NOT_BE_NEGATIVE
            );
        }

        if (isLimitNegative) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    BusinessException.LIMIT_SHOULD_NOT_BE_NEGATIVE
            );
        }
    }

    private void configurePaginationAndSorting(Query mongoQuery,
                                               Integer offsetValue,
                                               Integer limitValue,
                                               List<SortEnum> sortParameters) {
        if (offsetValue != null) {
            mongoQuery.skip(offsetValue);
        }
        if (limitValue != null) {
            mongoQuery.limit(limitValue);
        }

        if (sortParameters != null && !sortParameters.isEmpty()) {
            applySortingWithValidation(mongoQuery, sortParameters);
        }
    }

    private void applySortingWithValidation(Query mongoQuery, List<SortEnum> sortParameters) {
        List<Sort.Order> sortOrders = new ArrayList<>(sortParameters.size());
        Set<String> processedSortFields = new HashSet<>(sortParameters.size());

        for (SortEnum sortEnumValue : sortParameters) {
            String sortFieldWithDirection = sortEnumValue.getValue();
            String sortFieldName = extractSortFieldName(sortFieldWithDirection);

            if (!processedSortFields.add(sortFieldName)) {
                throw new ProductOrderInventoryException(
                        HttpStatus.BAD_REQUEST,
                        INVALID_QUERY_STRING_PARAMETER.getCode(),
                        INVALID_QUERY_STRING_PARAMETER.getStatus(),
                        BusinessException.DUPLICATE_SORT_PARAMETER
                );
            }

            Sort.Order sortOrder = createSortOrder(sortFieldWithDirection, sortFieldName);
            sortOrders.add(sortOrder);
        }

        mongoQuery.with(Sort.by(sortOrders));
    }

    private String extractSortFieldName(String sortFieldWithDirection) {
        return sortFieldWithDirection.startsWith("-")
                ? sortFieldWithDirection.substring(1)
                : sortFieldWithDirection;
    }

    private Sort.Order createSortOrder(String sortFieldWithDirection, String sortFieldName) {
        boolean isDescending = sortFieldWithDirection.startsWith("-");
        return isDescending
                ? Sort.Order.desc(sortFieldName)
                : Sort.Order.asc(sortFieldName);
    }

    private HttpHeaders buildResponseHeaders(long totalCount, int resultCount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT, String.valueOf(totalCount));
        headers.set(X_RESULT_COUNT, String.valueOf(resultCount));
        headers.setAccessControlExposeHeaders(List.of(X_TOTAL_COUNT, X_RESULT_COUNT));
        return headers;
    }

    private HttpStatus determineHttpStatus(int resultCount, long totalCount) {
        return resultCount < totalCount ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
    }

    private List<ProductOrder> mapEntitiesToDTOsWithLinks(List<ProductOrderEntity> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProductOrder> productOrders = new ArrayList<>(entities.size());

        for (ProductOrderEntity entity : entities) {
            ProductOrder dto = productOrderMapper.mapToDto(entity);
            addHypermediaLinks(dto);
            productOrders.add(dto);
        }

        return productOrders;
    }

    private ProductOrderItemStateType mapOrderStateToItemState(ProductOrderStateType orderState) {
        return switch (orderState) {
            case DRAFT -> ProductOrderItemStateType.DRAFT;
            case ACKNOWLEDGED -> ProductOrderItemStateType.ACKNOWLEDGED;
            case ACCEPTED -> ProductOrderItemStateType.ACCEPTED;
            case INPROGRESS -> ProductOrderItemStateType.INPROGRESS;
            case HELD -> ProductOrderItemStateType.HELD;
            case PENDING -> ProductOrderItemStateType.PENDING;
            case PENDINGCANCELLATION -> ProductOrderItemStateType.PENDINGCANCELLATION;
            case ASSESSINGCANCELLATION -> ProductOrderItemStateType.ASSESSINGCANCELLATION;
            case CANCELLED -> ProductOrderItemStateType.CANCELLED;
            case REJECTED -> ProductOrderItemStateType.REJECTED;
            case COMPLETED -> ProductOrderItemStateType.COMPLETED;
            case FAILED -> ProductOrderItemStateType.FAILED;
            default -> throw new IllegalArgumentException("Unhandled ProductOrderStateType: " + orderState);
        };
    }

    @Override
    public void updateProductOrderPayment(ProductOrder productOrder) {
        log.debug("Request to partially update product order payment: {}", productOrder);
        if (productOrder.getId() != null) {
            for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
                Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId())
                        .and(PRODUCT_ORDER_ITEM_ID_FIELD).is(productOrderItem.getId()));
                ProductOrderItemEntity productOrderItemEntity = productOrderItemMapper.mapToEntity(productOrderItem);
                Update update = new Update().set(PAYMENT_FIELD, productOrderItemEntity.getPayment());

                mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
            }
        }
    }

    @Override
    public void updateProductOrderBillingAccount(ProductOrder productOrder) {
        log.debug("Request to partially update product order billing account: {}", productOrder);

        if (productOrder.getId() != null) {
            for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
                Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId())
                        .and(PRODUCT_ORDER_ITEM_ID_FIELD).is(productOrderItem.getId()));
                ProductOrderItemEntity productOrderItemEntity = productOrderItemMapper.mapToEntity(productOrderItem);
                Update update = new Update().set(BILLING_ACCOUNT_FIELD, productOrderItemEntity.getBillingAccount());

                mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
            }
        }
    }

    @Override
    public void updateProductOrderAppointment(ProductOrder productOrder) {
        log.debug("Request to partially update product order Appointment: {}", productOrder);
        if (productOrder.getId() != null) {
            for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
                Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId())
                        .and(PRODUCT_ORDER_ITEM_ID_FIELD).is(productOrderItem.getId()));
                ProductOrderItemEntity productOrderItemEntity = productOrderItemMapper.mapToEntity(productOrderItem);
                Update update = new Update().set(APPOINTMENT_FIELD, productOrderItemEntity.getAppointment());
                mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
            }
        }
    }

    @Override
    public void updateProductOrderProduct(ProductOrder productOrder) {
        log.debug("Request to partially update product order item product ref: {}", productOrder);

        if (productOrder.getId() != null) {
            for (ProductOrderItem item : productOrder.getProductOrderItem()) {
                Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId())
                        .and(PRODUCT_ORDER_ITEM_ID_FIELD).is(item.getId()));
                ProductOrderItemEntity productOrderItemEntity = productOrderItemMapper.mapToEntity(item);
                Update update = new Update().set(PRODUCT_FIELD, productOrderItemEntity.getProduct());

                mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
            }
        }
    }

    @Override
    public void updateProductOrderRealizingResource(ProductOrder productOrder) {
        log.debug("Request to partially update product order resource: {}", productOrder);

        if (productOrder.getId() != null) {
            for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
                Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId())
                        .and(PRODUCT_ORDER_ITEM_ID_FIELD).is(productOrderItem.getId()));
                ProductOrderItemEntity productOrderItemEntity = productOrderItemMapper.mapToEntity(productOrderItem);
                ProductEntity productEntity = (ProductEntity) productOrderItemEntity.getProduct();
                Update update = new Update().set(REALIZING_RESOURCE_FIELD, productEntity.getRealizingResource());

                mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
            }
        }
    }

    @Override
    public void updateOrderItemsAndOrderTotalPrice(ProductOrder productOrder) {
        log.debug("Request to update all product order items and order total price: {}", productOrder);

        if (productOrder.getId() != null) {
            Query query = new Query(Criteria.where(ID_FIELD).is(productOrder.getId()));
            List<ProductOrderItemEntity> productOrderItemEntity = productOrderItemMapper.mapToEntityList(productOrder.getProductOrderItem());
            Update update = new Update().set(PRODUCT_ORDER_ITEM_FIELD, productOrderItemEntity)
                    .set(ORDER_TOTAL_PRICE, productOrder.getOrderTotalPrice());

            mongoTemplate.updateFirst(query, update, ProductOrderEntity.class);
        }
    }

    @Override
    public void updateProductOrderHierarchy(String productOrderId, String productOrderItemId, ProductOrderItemStateType newState) {
        log.debug("Request to update product order hierarchy with id: {}", productOrderId);

        if (productOrderId == null) {
            log.warn("Invalid input parameter: productOrderId is null (productOrderItemId={}, newState={})", productOrderItemId, newState);
            return;
        }

        ProductOrderEntity productOrder = productOrderRepository.findById(productOrderId).orElse(null);
        if (!isValidProductOrder(productOrder, productOrderItemId)) {
            log.warn("Product order not found or invalid: productOrderId={}, productOrderItemId={}", productOrderId, productOrderItemId);
            return;
        }

        ProductOrderItemEntity deliveredProductOrderItem = updateDeliveredProductOrderItem(productOrder.getProductOrderItem(), productOrderItemId, newState);

        if (deliveredProductOrderItem != null) {
            List<ProductOrderItemEntity> updatedItems = new ArrayList<>();
            updatedItems.add(deliveredProductOrderItem);

            List<ProductOrderItemEntity> productOrderItemParents = getProductOrderItemParents(productOrder, deliveredProductOrderItem);
            processUpdateStateHierarchyRecursively(productOrder, productOrderItemParents, updatedItems);

            ProductOrderStateType initialOrderState = productOrder.getState();
            updateProductOrderState(productOrder);

            productOrderRepository.save(productOrder);

            publishEvents(productOrder, initialOrderState, updatedItems);
        }
    }

    private void publishEvents(ProductOrderEntity productOrderEntity, ProductOrderStateType initialOrderState, List<ProductOrderItemEntity> updatedItems) {
        publishAttributeValueChangeEvents(productOrderEntity, updatedItems);
        publishStateChangeEventIfNeeded(productOrderEntity, initialOrderState);
    }

    private void publishStateChangeEventIfNeeded(ProductOrderEntity productOrderEntity, ProductOrderStateType initialOrderState) {
        ProductOrderStateType currentState = productOrderEntity.getState();
        if (!Objects.equals(currentState, initialOrderState)) {
            ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
            productOrderEventProducer.publishEvent(productOrder, PRODUCT_ORDER_STATE_CHANGE_EVENT);
        }
    }

    private void publishAttributeValueChangeEvents(ProductOrderEntity productOrderEntity, List<ProductOrderItemEntity> updatedItems) {
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        for (ProductOrderItemEntity updatedItem : updatedItems) {
            ProductOrder productOrderToPublish = createProductOrder(productOrder, updatedItem.getId());
            productOrderAttributeValueChangeEventProducer.publishEvent(productOrderToPublish, PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT);
        }
    }

    private ProductOrder createProductOrder(ProductOrder productOrder, String productOrderItemId) {
        return ProductOrder.builder()
                .id(productOrder.getId())
                .relatedParty(productOrder.getRelatedParty())
                .state(productOrder.getState())
                .productOrderItem(Collections.singletonList(getProductOrderItemById(productOrder, productOrderItemId)))
                .atType(productOrder.getAtType())
                .build();
    }

    private boolean isValidProductOrder(ProductOrderEntity productOrder, String productOrderItemId) {
        return productOrder != null
                && !CollectionUtils.isEmpty(productOrder.getProductOrderItem())
                && productOrder.getProductOrderItem()
                .stream()
                .anyMatch(productOrderItem -> productOrderItem.getId().equals(productOrderItemId));
    }

    private void updateProductOrderItemState(ProductOrderItemEntity parentItem, List<ProductOrderItemEntity> childrenItems) {
        if (containsState(childrenItems, ProductOrderItemStateType.HELD)) {
            parentItem.setState(ProductOrderItemStateType.HELD);
        } else if (containsState(childrenItems, ProductOrderItemStateType.INPROGRESS)) {
            parentItem.setState(ProductOrderItemStateType.INPROGRESS);
        } else if (
                containsState(childrenItems, ProductOrderItemStateType.ACCEPTED) &&
                        containsState(
                                childrenItems,
                                ProductOrderItemStateType.COMPLETED,
                                ProductOrderItemStateType.FAILED,
                                ProductOrderItemStateType.PARTIAL
                        )
        ) {
            parentItem.setState(ProductOrderItemStateType.INPROGRESS);
        } else if (allMatchStateWithType(childrenItems, ProductOrderItemStateType.FAILED)) {
            parentItem.setState(ProductOrderItemStateType.FAILED);
        } else if (allMatchStateWithType(childrenItems, ProductOrderItemStateType.COMPLETED)) {
            parentItem.setState(ProductOrderItemStateType.COMPLETED);
        } else {
            // (failed & completed)|| partial
            //completed & partial
            //failed & partial
            parentItem.setState(ProductOrderItemStateType.PARTIAL);
        }
    }

    private void updateProductOrderState(ProductOrderEntity productOrder) {
        List<ProductOrderItemEntity> productOrderItems = productOrder.getProductOrderItem();

        if (containsState(productOrderItems, ProductOrderItemStateType.HELD)) {
            productOrder.setState(ProductOrderStateType.HELD);
        } else if (containsState(productOrderItems, ProductOrderItemStateType.INPROGRESS)) {
            productOrder.setState(ProductOrderStateType.INPROGRESS);
        } else if (
                containsState(productOrderItems, ProductOrderItemStateType.ACCEPTED) &&
                        containsState(
                                productOrderItems,
                                ProductOrderItemStateType.COMPLETED,
                                ProductOrderItemStateType.FAILED,
                                ProductOrderItemStateType.PARTIAL
                        )
        ) {
            productOrder.setState(ProductOrderStateType.INPROGRESS);
        } else if (containsState(productOrderItems, ProductOrderItemStateType.PARTIAL)) {
            productOrder.setState(ProductOrderStateType.PARTIAL);
        } else if (allMatchStateWithType(productOrderItems, ProductOrderItemStateType.FAILED)) {
            productOrder.setState(ProductOrderStateType.FAILED);
        } else if (allMatchStateWithType(productOrderItems, ProductOrderItemStateType.COMPLETED)) {
            productOrder.setState(ProductOrderStateType.COMPLETED);
        } else if (
                containsState(productOrderItems, ProductOrderItemStateType.FAILED) &&
                        containsState(productOrderItems, ProductOrderItemStateType.COMPLETED, ProductOrderItemStateType.PARTIAL)
        ) {
            productOrder.setState(ProductOrderStateType.PARTIAL);
        }
    }

    private boolean containsState(List<ProductOrderItemEntity> productOrderItems, ProductOrderItemStateType... stateTypes) {
        return productOrderItems
                .stream()
                .map(ProductOrderItemEntity::getState)
                .anyMatch(state -> Arrays.asList(stateTypes).contains(state));
    }

    private boolean allMatchStateWithType(List<ProductOrderItemEntity> productOrderItems, ProductOrderItemStateType stateType) {
        return productOrderItems.stream().allMatch(item -> item.getState() == stateType);
    }

    private List<ProductOrderItemEntity> getProductOrderItemChildren(ProductOrderEntity productOrder, ProductOrderItemEntity productOrderItem) {
        List<String> productItemIds = getProductItemIdsByRelationType(productOrderItem, RelationshipType.BUNDLES);
        return getProductItemById(productItemIds, productOrder.getProductOrderItem());
    }

    private List<String> getProductItemIdsByRelationType(ProductOrderItemEntity productOrderItem, RelationshipType relationshipType) {
        return Optional.ofNullable(productOrderItem.getProductOrderItemRelationship())
                .orElse(Collections.emptyList())
                .stream()
                .filter(orderItemRelationship ->
                        Objects.nonNull(orderItemRelationship.getRelationshipType()) &&
                                orderItemRelationship.getRelationshipType().equals(relationshipType) &&
                                Objects.nonNull(orderItemRelationship.getId())
                )
                .map(OrderItemRelationshipEntity::getId)
                .toList();
    }

    private List<ProductOrderItemEntity> getProductItemById(List<String> childIds, List<ProductOrderItemEntity> productOrderItems) {
        return productOrderItems
                .stream()
                .filter(productOrderItem -> childIds.contains(productOrderItem.getId()))
                .toList();
    }

    private List<ProductOrderItemEntity> getProductOrderItemParents(ProductOrderEntity productOrder, ProductOrderItemEntity productOrderItem) {
        List<String> isChildIds = getProductItemIdsByRelationType(productOrderItem, RelationshipType.ISCHILD);
        return getProductItemById(isChildIds, productOrder.getProductOrderItem());
    }

    private ProductOrderItemEntity updateDeliveredProductOrderItem(
            List<ProductOrderItemEntity> productOrderItems,
            String productOrderItemId,
            ProductOrderItemStateType nodeState
    ) {
        return productOrderItems
                .stream()
                .filter(productOrderItem -> productOrderItem.getId().equals(productOrderItemId))
                .findFirst()
                .map(productOrderItem -> {
                    productOrderItem.setState(nodeState);
                    return productOrderItem;
                })
                .orElse(null);
    }

    private void addHypermediaLinks(ProductOrder productOrder) {
        String orderId = productOrder.getId();
        String selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductOrderControllerImpl.class)
                        .retrieveProductOrder(orderId, null))
                .withSelfRel()
                .toUri()
                .toString();

        productOrder.setHref(selfLink);
    }
}