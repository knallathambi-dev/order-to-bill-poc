// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.AppConfig;
import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductStateChangeEventProducerImpl;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.JobReportProductRefEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.repository.CustomProductRepository;
import com.orange.discobole.productinventory.repository.JobReportRepository;
import com.orange.discobole.productinventory.service.ProductService;
import com.orange.discobole.productinventory.service.TerminateProductService;
import com.orange.discobole.productinventory.util.ProductEntityUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.enumerate.PublishEventEnum.BATCH;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.CREATED_PRODUCT_ATTEMPTED_TO_BE_MODIFIED_TO_ABORTED_INSTEAD_OF_TERMINATED;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PRODUCT_WITH_INVALID_STATUS_CAN_NOT_BE_TERMINATED;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class TerminateProductServiceImpl implements TerminateProductService {
    public static final List<ProductRelationshipType> RELATED_PRODUCT_RELATIONSHIP_TYPE_TO_TERMINATE = List.of(ProductRelationshipType.RELIESFROM, ProductRelationshipType.BUNDLES, ProductRelationshipType.SELLS);
    private final CustomProductRepository productRepository;
    private final ProductService productService;
    private final TransactionTemplate transactionTemplate;
    private final AppConfig appConfig;
    private final ProductMapper productMapper;
    private final ProductStateChangeEventProducerImpl eventProducer;
    private final JobReportRepository jobReportRepository;

    private List<ProductEntity> distributeProductEntitiesAndGetNewProductsToBeUpdated(List<ProductEntity> productsToBeSplit,
                                                                                      Set<ProductEntity> productsToBeAborted,
                                                                                      Set<ProductEntity> productsToBeTerminated,
                                                                                      Set<ProductEntity> productsToBeSkipped) {
        productsToBeSkipped.addAll(productsToBeSplit);

        List<ProductEntity> createdProducts = ProductEntityUtil.getProductsByStatusFrom(productsToBeSplit, ProductStatusType.CREATED);
        productsToBeAborted.addAll(createdProducts);
        createdProducts.forEach(productsToBeSkipped::remove);

        List<ProductEntity> activeProducts = ProductEntityUtil.getProductsByStatusFrom(productsToBeSplit, ProductStatusType.ACTIVE);
        log.info("activeProducts {}", activeProducts.stream().map(ProductEntity::getId).toList());

        productsToBeTerminated.addAll(activeProducts);
        activeProducts.forEach(productsToBeSkipped::remove);

        List<ProductEntity> productToBeUpdated = new ArrayList<>();
        productToBeUpdated.addAll(activeProducts);
        productToBeUpdated.addAll(createdProducts);
        return productToBeUpdated;
    }

    private static JobReportProductRefEntity createProductRefEntity(ProductEntity product) {
        return JobReportProductRefEntity.builder().id(product.getId()).name(product.getName()).build();
    }

    private static void handleTerminationException(Exception e, AtomicBoolean containError, Set<ProductEntity> productsToBeAborted, Set<ProductEntity> productsToBeTerminated, List<JobReportProductRefEntity> productsTerminatedWithFailure) {
        String messageError = "Failed to update product status. Rolling back changes.";
        log.error(messageError, e);
        containError.set(true);
        Set<ProductEntity> productIdsToBeUpdated = new HashSet<>();
        productIdsToBeUpdated.addAll(productsToBeAborted);
        productIdsToBeUpdated.addAll(productsToBeTerminated);
        productIdsToBeUpdated.forEach(product ->
                productsTerminatedWithFailure.add(createProductRefEntity(product))
        );
    }

    private static void appendFailedProductsToTheJob(Set<ProductEntity> failedProducts, AtomicBoolean containError, String errorMessage, List<JobReportProductRefEntity> productsTerminatedWithFailure) {
        if (!failedProducts.isEmpty()) {
            containError.set(true);
            failedProducts.forEach(product -> {
                        String messageError = errorMessage.formatted(product.getId());
                        log.error(messageError, product.getId());
                        productsTerminatedWithFailure.add(createProductRefEntity(product));
                    }
            );
        }
    }

    @Override
    public void terminateProducts(JobEntity job) {
        AtomicBoolean containError = new AtomicBoolean(false);
        List<JobReportProductRefEntity> productsTerminatedWithFailure = new ArrayList<>();
        List<JobReportProductRefEntity> productsTerminatedWithSuccess = new ArrayList<>();

        for (int i = 0; i < appConfig.getMaxRuns(); i++) {
            List<ProductEntity> productsWithPastTerminationDate = productRepository.findProductsWithPastTerminationDate(appConfig.getMaxCount());
            if (productsWithPastTerminationDate.isEmpty()) {
                break;
            }
            Set<ProductEntity> productsToBeAborted = new HashSet<>();
            Set<ProductEntity> productsToBeTerminated = new HashSet<>();
            Set<ProductEntity> productsToBeSkipped = new HashSet<>();

            retrieveRelatedProductsToBeUpdated(productsWithPastTerminationDate,
                    productsToBeAborted, productsToBeTerminated, productsToBeSkipped);

            Set<ProductEntity> retrievedProductToBeUpdated = new HashSet<>();
            retrievedProductToBeUpdated.addAll(productsToBeTerminated);
            retrievedProductToBeUpdated.addAll(productsToBeAborted);

            Set<ProductEntity> relatedAtomicProducts = extractAtomicProductToBeUpdated(retrievedProductToBeUpdated);
            distributeProductEntitiesAndGetNewProductsToBeUpdated(relatedAtomicProducts.stream().toList(),
                    productsToBeAborted, productsToBeTerminated, productsToBeSkipped);

            appendFailedProductsToTheJob(productsToBeSkipped, containError, PRODUCT_WITH_INVALID_STATUS_CAN_NOT_BE_TERMINATED, productsTerminatedWithFailure);
            try {
                Map<String, ProductStatusType> idAndStatus = Stream.concat(productsToBeTerminated.stream(), Stream.concat(productsToBeAborted.stream(), productsToBeSkipped.stream())).collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getStatus));
                Set<String> updatedProductsIds = updateOperations(ProductEntityUtil.getIds(productsToBeTerminated), ProductEntityUtil.getIds(productsToBeAborted), ProductEntityUtil.getIds(productsToBeSkipped));
                appendFailedProductsToTheJob(productsToBeAborted, containError, CREATED_PRODUCT_ATTEMPTED_TO_BE_MODIFIED_TO_ABORTED_INSTEAD_OF_TERMINATED, productsTerminatedWithFailure);
                productsToBeTerminated.forEach(product -> {
                    if (ObjectId.isValid(product.getId())) {
                        productsTerminatedWithSuccess.add(createProductRefEntity(product));
                    }
                });
                List<ProductEntity> updatedProduct = productRepository.findProductForEventStatusChangeBy(updatedProductsIds);
                // TODO: check toDtoWithProductIdOnly is returning the required attributes
                eventProducer.publishEvents(updatedProduct.stream().map(productMapper::toDtoWithProductIdOnly).map(product -> StateChangeProduct.fromProduct(product, idAndStatus.get(product.getId()))).collect(Collectors.toSet()), BATCH.getTitle(), BATCH.getDomain());
            } catch (Exception e) {
                handleTerminationException(e, containError, productsToBeAborted, productsToBeTerminated, productsTerminatedWithFailure);
            }
        }
        jobReportRepository.save(
                JobReportEntity
                        .builder()
                        .succeededProducts(productsTerminatedWithSuccess)
                        .failedProducts(productsTerminatedWithFailure)
                        .jobId(job.getId())
                        .jobSpecificationId(job.getJobSpecification().getId())
                        .build()
        );
    }

    private Set<ProductEntity> extractAtomicProductToBeUpdated(Set<ProductEntity> retrievedProductToBeUpdated) {
        Set<ProductEntity> productSpecificationToBeTerminated = retrievedProductToBeUpdated.stream()
                .filter(ProductEntityUtil::isProductSpecification).collect(Collectors.toSet());

        Function<ProductEntity, List<ProductEntity>> productEntityToRelatedSellsListFunction = getProductEntityToRelatedSellsListFunction();

        Set<ProductEntity> relatedAtomicProducts = retrievedProductToBeUpdated.stream()
                .filter(product -> ProductEntityUtil.isProductOfferingType(product, ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING))
                .flatMap(product -> productEntityToRelatedSellsListFunction.apply(product).stream()
                        .filter(productSpecificationToBeTerminated::contains)
                        .map(productSP -> {
                            productSpecificationToBeTerminated.remove(productSP);
                            return product;
                        }))
                .collect(Collectors.toSet());
        if (!productSpecificationToBeTerminated.isEmpty()) {
            Set<String> remainingPSsToBeTerminatedIds = productSpecificationToBeTerminated.stream()
                    .map(ProductEntity::getId).collect(Collectors.toSet());
            List<ProductEntity> atomics = productRepository.findAtomicProductOfferingSells(remainingPSsToBeTerminatedIds);
            relatedAtomicProducts.addAll(atomics);
        }
        return relatedAtomicProducts;
    }

    private Function<ProductEntity, List<ProductEntity>> getProductEntityToRelatedSellsListFunction() {
        return product -> productService.getListOfProductEntityByIds(
                ProductEntityUtil
                        .getProductRelationshipsByType(
                                product,
                                ProductRelationshipType.SELLS
                        )
                        .stream()
                        .map(ProductRelationshipEntity::getProduct)
                        .map(ProductRefEntity::getId)
                        .map(ObjectId::toString)
                        .collect(Collectors.toSet()));
    }

    private void retrieveRelatedProductsToBeUpdated(List<ProductEntity> productsWithPastTerminationDate,
                                                    Set<ProductEntity> productsToBeAborted,
                                                    Set<ProductEntity> productsToBeTerminated,
                                                    Set<ProductEntity> productsToBeSkipped) {
        if (!CollectionUtils.isEmpty(productsWithPastTerminationDate)) {
            List<ProductEntity> collectedProductsToBeUpdated = distributeProductEntitiesAndGetNewProductsToBeUpdated(productsWithPastTerminationDate,
                    productsToBeAborted, productsToBeTerminated, productsToBeSkipped);

            Set<ObjectId> relatedProductsIds = ProductEntityUtil.extractRelatedProduct(collectedProductsToBeUpdated, RELATED_PRODUCT_RELATIONSHIP_TYPE_TO_TERMINATE);
            List<ProductEntity> relatedProducts = productRepository.findNotTerminatedProductsByIds(relatedProductsIds);
            retrieveRelatedProductsToBeUpdated(relatedProducts, productsToBeAborted, productsToBeTerminated, productsToBeSkipped);
        }
    }

    public Set<String> updateOperations(Set<String> productIdsToBeTerminated, Set<String> productIdsToBeAborted, Set<String> productIdsToBeSkipped) {
        Set<String> updatedProductIds = new HashSet<>();
        updatedProductIds.addAll(productIdsToBeAborted);
        updatedProductIds.addAll(productIdsToBeTerminated);
        updatedProductIds.addAll(productIdsToBeSkipped);
        transactionTemplate.execute(status -> {
            log.info("Updating status and operational status to terminated for {} products.", productIdsToBeTerminated.size());
            productRepository.updateStatusAndOperationalStatusOf(productIdsToBeTerminated, ProductStatusType.TERMINATED, ProductOperationalStatusType.TERMINATED);
            productRepository.updateTerminationDateIfNotExist(productIdsToBeTerminated);
            if (!productIdsToBeAborted.isEmpty()) {
                log.info("Updating status to aborted for {} atomic products.", productIdsToBeAborted.size());
                productRepository.updateStatusAndOperationalStatusOf(productIdsToBeAborted, ProductStatusType.ABORTED, ProductOperationalStatusType.ABORTED);
            }
            productRepository.markProcessedProduct(updatedProductIds);
            return status;
        });
        return updatedProductIds;
    }

}
