// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemEntity;
import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemStateChangedEvent;
import com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType;
import com.orange.discobole.ordermanagement.orderfollowup.mapper.product.order.ProductOrderMapper;
import com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation.ProductDeliveredEvent;
import com.orange.discobole.ordermanagement.orderfollowup.repository.ProductOrderEventRepository;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;
import com.orange.discobole.processflow.service.ProcessFlowCommandService;
import com.orange.discobole.processflow.service.TaskFLowCommandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.*;

@Component
@Slf4j
public class ProductOrderEventServiceImpl implements ProductOrderEventService {
    private final ProductOrderEventRepository productOrderEventRepository;
    private final ProcessFlowCommandService processFlowService;
    private final TaskFLowCommandService taskFlowService;
    private final MongoTemplate mongoTemplate;
    private final CacheManager cacheManager;
    private final ProductOrderMapper productOrderMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderEventServiceImpl(ProductOrderEventRepository productOrderEventRepository, ProcessFlowCommandService processFlowService, TaskFLowCommandService taskFlowService, MongoTemplate mongoTemplate, CacheManager cacheManager, ProductOrderMapper productOrderMapper) {
        this.productOrderEventRepository = productOrderEventRepository;
        this.processFlowService = processFlowService;
        this.taskFlowService = taskFlowService;
        this.mongoTemplate = mongoTemplate;
        this.cacheManager = cacheManager;
        this.productOrderMapper = productOrderMapper;
    }

    @Override
    public ProductOrderItemStateChangedEvent save(ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent) {
        log.info("Saving ProductOrderItemStateChangedEvent for productOrderId: {}", productOrderItemStateChangedEvent.getProductOrderId());
        log.debug("ProductOrderItemStateChangedEvent details: {}", productOrderItemStateChangedEvent);
        ProductOrderItemStateChangedEvent savedEvent = productOrderEventRepository.save(productOrderItemStateChangedEvent);
        log.debug("ProductOrderItemStateChangedEvent saved successfully with id: {}", savedEvent.getId());
        return savedEvent;
    }

    @Override
    public Optional<ProductOrderItemStateChangedEvent> findByProductOrderId(String productOrderId) {
        log.debug("Looking up ProductOrderItemStateChangedEvent for productOrderId: {}", productOrderId);
        Optional<ProductOrderItemStateChangedEvent> result = productOrderEventRepository.findByProductOrderId(productOrderId);
        if (result.isPresent()) {
            log.debug("Found existing ProductOrderItemStateChangedEvent for productOrderId: {}", productOrderId);
        } else {
            log.debug("No ProductOrderItemStateChangedEvent found for productOrderId: {}", productOrderId);
        }
        return result;
    }

    @Override
    public void deleteByProductOrderId(String productOrderId) {
        log.info("Deleting all events for productOrderId: {}", productOrderId);

        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_ORDER_ID).is(productOrderId));

        mongoTemplate.remove(query, ProductOrderItemStateChangedEvent.class, PRODUCT_ORDER_ITEM_EVENT_COLLECTION);
        log.info("Successfully deleted events for productOrderId: {} - evicting caches", productOrderId);
        evictAllCaches();
    }

    @Override
    public synchronized ProductOrderItemStateChangedEvent createProductOrderItemStateChangedEvent(ProductOrder productOrder, Instant eventTime) {
        log.info("Creating ProductOrderItemStateChangedEvent for productOrderId: {}, eventTime: {}", productOrder.getId(), eventTime);
        try {
            Optional<ProductOrderItemStateChangedEvent> productOrderItemStateChangedEventOptional = findByProductOrderId(productOrder.getId());
            if (productOrderItemStateChangedEventOptional.isEmpty()) {
                log.debug("No existing event found - creating new ProcessFlow for productOrderId: {}", productOrder.getId());
                ProcessFlowCreate processFlowCreate = new ProcessFlowCreate();
                processFlowCreate.setProcessFlowSpecification(ORDER_FOLLOW_UP);
                processFlowCreate.setRelatedParty(productOrderMapper.toRelatedPartyList(productOrder.getRelatedParty()));
                ProcessFlow processFlow = processFlowService.createProcessFlow(processFlowCreate);
                Assert.notNull(processFlow, ExceptionMessage.PROCESS_FLOW_MAY_NOT_BE_NULL);
                log.debug("ProcessFlow created with id: {} for productOrderId: {}", processFlow.getId(), productOrder.getId());
                TaskLink nextTaskTobePerformed = getNextTaskTobePerformed(processFlow);
                Assert.notNull(nextTaskTobePerformed, ExceptionMessage.NEXT_TASK_TO_BE_PERFORMED_MAY_NOT_BE_NULL);
                log.debug("Next task to be performed: {} for productOrderId: {}", nextTaskTobePerformed.getTaskFlowSpecificationId(), productOrder.getId());
                ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = buildProductOrderItemStateChangedEvent(productOrder, processFlow, nextTaskTobePerformed, eventTime);
                ProductOrderItemStateChangedEvent savedEvent = this.save(productOrderItemStateChangedEvent);
                log.info("Successfully created ProductOrderItemStateChangedEvent for productOrderId: {}, processId: {}",
                        productOrder.getId(), processFlow.getId());
                return savedEvent;
            }
            log.info("ProductOrderItemStateChangedEvent already exists for productOrderId: {} - returning existing event", productOrder.getId());
            return productOrderItemStateChangedEventOptional.get();
        } catch (Exception e) {
            log.error("Failed to create ProductOrderItemStateChangedEvent for productOrderId: {} - {}", productOrder.getId(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void addProductOrderItem(ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent) {
        String eventId = productOrderAttributeValueChangeEvent.getEventId();
        String productOrderId = productOrderAttributeValueChangeEvent.getEvent().getProductOrder().getId();
        log.info("Adding product order item from attribute value change event - eventId: {}, productOrderId: {}", eventId, productOrderId);
        try {
            ProductOrder productOrder = productOrderAttributeValueChangeEvent.getEvent().getProductOrder();
            ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = createProductOrderItemStateChangedEvent(productOrder, productOrderAttributeValueChangeEvent.getEventTime());
            updateProductOrderItems(productOrderAttributeValueChangeEvent, productOrderItemStateChangedEvent);
            evictAllCaches();
            log.info("Successfully added product order item - eventId: {}, productOrderId: {}", eventId, productOrderId);
        } catch (Exception e) {
            log.error("Failed to add product order item - eventId: {}, productOrderId: {} - {}", eventId, productOrderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateProductItemOfupState(String productOrderId, String eventId, OfupStateType ofupState) {
        log.info("Updating OFUP state - productOrderId: {}, eventId: {}, newState: {}", productOrderId, eventId, ofupState);
        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_ORDER_ID).is(productOrderId)
                .and(PRODUCT_ORDER_ITEMS_EVENT_ID).is(eventId)
        );
        Update update = new Update();
        update.set("productOrderItems.$.ofupState", ofupState);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ProductOrderItemStateChangedEvent.class, PRODUCT_ORDER_ITEM_EVENT_COLLECTION);

        if (updateResult.getModifiedCount() > 0) {
            log.info("OFUP state updated successfully - productOrderId: {}, eventId: {}, newState: {}, modifiedCount: {}",
                    productOrderId, eventId, ofupState, updateResult.getModifiedCount());
        } else {
            log.warn("OFUP state update had no effect - productOrderId: {}, eventId: {}, targetState: {} - document may not exist or state is already set",
                    productOrderId, eventId, ofupState);
        }
        evictAllCaches();
    }

    @Override
    public void reinitializeProductItemOfupState() {
        log.info("Reinitializing product order item OFUP state from IN_PROGRESS to NEW");

        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_ORDER_ITEM_OFUP_STATE_TYPE).is(OfupStateType.IN_PROGRESS));

        Update update = new Update();
        update.set("productOrderItems.$.ofupState", OfupStateType.NEW);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ProductOrderItemStateChangedEvent.class, PRODUCT_ORDER_ITEM_EVENT_COLLECTION);
        log.info("OFUP state reinitialization completed - {} document(s) reset from IN_PROGRESS to NEW", updateResult.getModifiedCount());
    }

    @Override
    @Cacheable(PRODUCTS_CACHE)
    public List<ProductOrderItemStateChangedEvent> getNewProductOrderItemEvents() {
        log.debug("Querying for product order item events with OFUP state NEW");

        Query query = new Query();
        query.addCriteria(
                new Criteria().andOperator(
                        Criteria.where(PRODUCT_ORDER_ITEM_OFUP_STATE_TYPE).is(OfupStateType.NEW),
                        Criteria.where(PRODUCT_ORDER_ITEM_OFUP_STATE_TYPE).ne(OfupStateType.IN_PROGRESS)
                )
        );
        List<ProductOrderItemStateChangedEvent> events = mongoTemplate.find(query, ProductOrderItemStateChangedEvent.class, PRODUCT_ORDER_ITEM_EVENT_COLLECTION);
        log.debug("Found {} product order item event(s) with OFUP state NEW", events.size());
        return events;
    }

    @Async
    @Override
    public void executeReceiveNewProductStateChangeEventTaskFlow(ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent) {
        String productOrderId = productOrderItemStateChangedEvent.getProductOrderId();
        String processId = productOrderItemStateChangedEvent.getProcessId();
        log.info("Executing async ReceiveNewProductStateChangeEvent task flow - productOrderId: {}, processId: {}", productOrderId, processId);

        ProductOrderItemEntity productOrderItem = getOldestProductOrderItemToBeProceeded(productOrderItemStateChangedEvent);
        try {
            Assert.notNull(productOrderItem, ExceptionMessage.PRODUCT_ITEM_MAY_NOT_BE_NULL);
            String productOrderItemId = productOrderItem.getProductOrderItemId();
            String eventId = productOrderItem.getEventId();

            log.debug("Processing oldest pending item - productOrderId: {}, productOrderItemId: {}, eventId: {}, state: {}",
                    productOrderId, productOrderItemId, eventId, productOrderItem.getState());

            this.updateProductItemOfupState(productOrderId, eventId, OfupStateType.IN_PROGRESS);

            ProductDeliveredEvent productDeliveredEvent = buildProductDeliveredEvent(productOrderItem, productOrderId, productOrderItemId);
            TaskFlowUpdate taskFlowUpdate = buildTaskFlowUpdate(productDeliveredEvent);

            log.debug("Updating task flow - processId: {}, nextTask: {}", processId, productOrderItemStateChangedEvent.getNextTaskToBePerformed());
            taskFlowService.updateTaskFlow(processId, productOrderItemStateChangedEvent.getNextTaskToBePerformed(), taskFlowUpdate);

            log.info("Task flow execution completed successfully - productOrderId: {}, productOrderItemId: {}, eventId: {}",
                    productOrderId, productOrderItemId, eventId);
        } catch (TaskFlowNotFoundException e) {
            log.error("Task flow not found during execution - productOrderId: {}, processId: {}, nextTask: {} - marking item as DEPRECATED",
                    productOrderId, processId, productOrderItemStateChangedEvent.getNextTaskToBePerformed(), e);
            if (productOrderItem != null) {
                this.updateProductItemOfupState(productOrderId, productOrderItem.getEventId(), OfupStateType.DEPRECATED);
            }
        } catch (RuntimeException e) {
            log.error("Unexpected error during task flow execution - productOrderId: {}, processId: {} - resetting item to NEW",
                    productOrderId, processId, e);
            if (productOrderItem != null) {
                this.updateProductItemOfupState(productOrderId, productOrderItem.getEventId(), OfupStateType.NEW);
            }
        }
    }

    private ProductDeliveredEvent buildProductDeliveredEvent(ProductOrderItemEntity productOrderItem, String productOrderId, String productOrderItemId) {
        return ProductDeliveredEvent
                .builder()
                .productOrderId(productOrderId)
                .productOrderItemId(productOrderItemId)
                .productOrderItemEventId(productOrderItem.getEventId())
                .productState(productOrderItem.getState())
                .relatedProductOrderState(productOrderItem.getRelatedProductOrderState())
                .build();
    }

    private ProductOrderItemStateChangedEvent buildProductOrderItemStateChangedEvent(ProductOrder productOrder, ProcessFlow processFlow, TaskLink nextTaskTobePerformed, Instant eventTime) {
        return ProductOrderItemStateChangedEvent
                .builder()
                .productOrderId(productOrder.getId())
                .eventTime(eventTime)
                .productOrderState(productOrder.getState())
                .processId(processFlow.getId())
                .nextTaskToBePerformed(nextTaskTobePerformed.getTaskFlowSpecificationId())
                .build();
    }

    private void updateProductOrderItems(ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent, ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent) {
        ProductOrder productOrder = productOrderAttributeValueChangeEvent.getEvent().getProductOrder();
        ProductOrderItem productOrderItemDTO = getFirstProductOrderItem(productOrder);
        ProductOrderItemEntity productOrderItem = buildProductOrderItem(productOrderAttributeValueChangeEvent, productOrder, productOrderItemDTO);
        if (!isAlreadyEventAdded(productOrderItemStateChangedEvent.getProductOrderItems(), productOrderItem)) {
            log.debug("New product order item detected - persisting item for productOrderId: {}, orderItemId: {}",
                    productOrder.getId(), productOrderItemDTO.getId());
            updateProductOrderItem(productOrder, productOrderItem);
        } else {
            log.debug("Duplicate product order item detected - skipping persist for productOrderId: {}, eventId: {}",
                    productOrder.getId(), productOrderAttributeValueChangeEvent.getEventId());
        }
    }

    private void updateProductOrderItem(ProductOrder productOrder, ProductOrderItemEntity productOrderItem) {
        Update update = new Update();
        update.push(PRODUCT_ORDER_ITEMS).each(productOrderItem);
        update.set(PRODUCT_ORDER_STATE, productOrder.getState());
        Query query = new Query();
        query.addCriteria(Criteria.where(PRODUCT_ORDER_ID).is(productOrder.getId()));
        mongoTemplate.updateFirst(query, update, ProductOrderItemStateChangedEvent.class, PRODUCT_ORDER_ITEM_EVENT_COLLECTION);
    }

    private ProductOrderItemEntity buildProductOrderItem(ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent, ProductOrder
            productOrder, ProductOrderItem productOrderItemDTO) {
        return ProductOrderItemEntity
                .builder()
                .eventId(productOrderAttributeValueChangeEvent.getEventId())
                .eventTime(productOrderAttributeValueChangeEvent.getEventTime())
                .relatedProductOrderState(productOrder.getState())
                .productOrderItemId(productOrderItemDTO.getId())
                .state(productOrderItemDTO.getState())
                .ofupState(OfupStateType.NEW)
                .build();
    }

    private ProductOrderItem getFirstProductOrderItem(ProductOrder productOrder) {
        List<ProductOrderItem> productOrderItems = productOrder.getProductOrderItem();
        List<ProductOrderItem> productOrderItemDTOS = new ArrayList<>(productOrderItems);
        return productOrderItemDTOS.get(0);
    }

    private TaskLink getNextTaskTobePerformed(ProcessFlow processFlow) {
        TaskLink taskLink = processFlow
                .getLinks()
                .getNextTaskstoBePerformed()
                .stream()
                .filter(tl -> tl.getTitle().equalsIgnoreCase(NEW_PRODUCT_ORDER_ITEM_STATE_CHANGE_EVENT_TITLE))
                .findFirst()
                .orElse(null);
        if (taskLink == null) {
            log.warn("No task link found matching title '{}' in processFlow: {}", NEW_PRODUCT_ORDER_ITEM_STATE_CHANGE_EVENT_TITLE, processFlow.getId());
        }
        return taskLink;
    }

    private TaskFlowUpdate buildTaskFlowUpdate(ProductDeliveredEvent productDeliveredEvent) {
        ObjectCharacteristic objectCharacteristic = new ObjectCharacteristic();
        objectCharacteristic.setValueType(OBJECT);
        objectCharacteristic.setType(OBJECT_CHARACTERISTIC);
        objectCharacteristic.setName(PRODUCT_DELIVERED_EVENT_CLASS);
        objectCharacteristic.setValue(productDeliveredEvent);
        List<Characteristic> characteristic = Collections.singletonList(objectCharacteristic);
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristic);
        return taskFlowUpdate;
    }

    private ProductOrderItemEntity getOldestProductOrderItemToBeProceeded(ProductOrderItemStateChangedEvent
                                                                                  productOrderItemStateChangedEvent) {
        List<ProductOrderItemEntity> newProductOrderItems = productOrderItemStateChangedEvent.getProductOrderItems().stream().filter(productOrderItem -> productOrderItem.getOfupState().equals(OfupStateType.NEW)).toList();
        log.debug("Found {} NEW product order item(s) for productOrderId: {}", newProductOrderItems.size(), productOrderItemStateChangedEvent.getProductOrderId());
        ProductOrderItemEntity oldest = newProductOrderItems.stream().min(Comparator.comparing(ProductOrderItemEntity::getEventTime, Comparator.nullsLast(Comparator.naturalOrder()))).orElse(null);
        if (oldest != null) {
            log.debug("Selected oldest pending item - eventId: {}, eventTime: {}", oldest.getEventId(), oldest.getEventTime());
        } else {
            log.warn("No NEW product order item found to process for productOrderId: {}", productOrderItemStateChangedEvent.getProductOrderId());
        }
        return oldest;
    }

    public void evictAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        log.debug("Evicting all caches: {}", cacheNames);
        cacheNames.forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    private boolean isAlreadyEventAdded(List<ProductOrderItemEntity> productOrderItems, ProductOrderItemEntity productOrderItem) {
        if (productOrderItems == null) {
            return false;
        }

        return productOrderItems.stream().anyMatch(existingItem -> {
            boolean sameEvent = existingItem.getEventId().equals(productOrderItem.getEventId());
            boolean sameItemAndState = existingItem.getProductOrderItemId().equals(productOrderItem.getProductOrderItemId())
                    && existingItem.getState().equals(productOrderItem.getState());
            return sameEvent || sameItemAndState;
        });
    }
}