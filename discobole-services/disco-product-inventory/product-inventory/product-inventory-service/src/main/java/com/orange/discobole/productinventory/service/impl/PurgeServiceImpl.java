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
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductDeleteEventProducerImpl;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.repository.JobReportRepository;
import com.orange.discobole.productinventory.repository.JobSpecificationRepository;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.service.JobService;
import com.orange.discobole.productinventory.service.PurgeService;
import com.orange.discobole.productinventory.service.S3Service;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import com.orange.discobole.productinventory.validation.pageable.impl.FieldFetcher;
import com.orange.discobole.productinventory.validation.pageable.impl.ProductFieldFetcher;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.enumerate.PublishEventEnum.PRODUCT_DELETE_EVENT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_VALUE_OF_PURGE_TYPE;
import static com.orange.discobole.productinventory.util.QueryUtils.parseQueryToMultiValueMap;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class PurgeServiceImpl implements PurgeService {

    private static final ProductFieldFetcher PRODUCT_FIELD_FETCHER = new ProductFieldFetcher();
    private static final FieldFetcher<JobSpecification> JOB_SPECIFICATION_FIELD_FETCHER = new FieldFetcher<>(JobSpecification.class);

    private final MongoTemplate mongoTemplate;
    private final FilterQueryService filterQueryService;
    private final S3Service s3Service;
    private final TransactionTemplate transactionTemplate;
    private final JobService jobService;
    private final JobSpecificationRepository jobSpecificationRepository;
    private final JobReportRepository jobReportRepository;
    private final AppConfig appConfig;
    private final ProductDeleteEventProducerImpl productDeleteEventProducer;

    private final ProductMapper productMapper;


    @Override
    public void purge(JobSpecificationEntity jobSpecificationEntity) {
        switch (jobSpecificationEntity.getPurgeType()) {
            case PURGEPRODUCT -> deleteProduct(jobSpecificationEntity);
            case PURGEJOB -> deleteJob(jobSpecificationEntity);
            default ->
                    throw new IllegalArgumentException(String.format(INVALID_VALUE_OF_PURGE_TYPE, PurgeJobSpecification.Fields.purgeType, Arrays.stream(PurgeTypeEnum.values()).map(Object::toString).toList()));
        }
    }

    private Query prepareQuery(String purgeQuery, FieldsFetcher fieldsFetcher) {
        MultiValueMap<String, Object> multiValueMap = parseQueryToMultiValueMap(purgeQuery, fieldsFetcher);
        return filterQueryService.createQuery(multiValueMap);
    }
    private void purgeExportedFiles(List<String> fileNamesList, String purgeJobId) {
        try {
            int totalFiles = fileNamesList.size();
            int batchSize = appConfig.getPurgeBatchSize();

            for (int i = 0; i < totalFiles; i += batchSize) {
                int end = Math.min(i + batchSize, totalFiles);
                List<String> fileNamesBatch = fileNamesList.subList(i, end);
                s3Service.deleteFiles(fileNamesBatch);
            }
        } catch (IOException e) {
            log.error("Purge job ID {} : Impossible to purge exported files", purgeJobId, e);
        }
    }



    private void deleteProduct(JobSpecificationEntity purgeJob) {
        log.info("Purge job ID {} : delete products", purgeJob.getId());
        Query baseQuery = prepareQuery(purgeJob.getQuery(), PRODUCT_FIELD_FETCHER).limit(1000);
        List<ProductEntity> productsToDelete;
        do {
            productsToDelete = mongoTemplate.find(baseQuery, ProductEntity.class);
            if (!productsToDelete.isEmpty()) {
                List<String> productIds = productsToDelete.stream().map(ProductEntity::getId).toList();
                Query deleteQuery = new Query(Criteria.where("_id").in(productIds));
                mongoTemplate.remove(deleteQuery, ProductEntity.class);
                List<Product> productsPublishEvent = productsToDelete.stream().map(productMapper::toDtoWithProductIdOnly).toList();
                productDeleteEventProducer.publishEvents(new HashSet<>(productsPublishEvent), PRODUCT_DELETE_EVENT.getTitle(), PRODUCT_DELETE_EVENT.getDomain());
            }
        } while (!productsToDelete.isEmpty());
    }

    private void deleteJob(JobSpecificationEntity purgeJob) {
        log.info("Purge job ID {} : delete jobSpecification", purgeJob.getId());
        Query query = prepareQuery(purgeJob.getQuery(), JOB_SPECIFICATION_FIELD_FETCHER);
        Set<String> jobSpecificationIds = new HashSet<>();
        Set<String> fileNamesToDelete = new HashSet<>();

        try (Stream<JobSpecificationEntity> stream = mongoTemplate.stream(query, JobSpecificationEntity.class)) {
            stream.forEach(jobSpecificationEntity -> {
                jobSpecificationIds.add(jobSpecificationEntity.getId());
                if (jobSpecificationIds.size() >= appConfig.getPurgeBatchSize()) {
                    fileNamesToDelete.addAll(getFileNamesToDelete(jobSpecificationIds));
                    processBatch(jobSpecificationIds, fileNamesToDelete, purgeJob.getId());
                    jobSpecificationIds.clear();
                    fileNamesToDelete.clear();
                }
            });
        }
        if (!jobSpecificationIds.isEmpty() || !fileNamesToDelete.isEmpty()) {
            fileNamesToDelete.addAll(getFileNamesToDelete(jobSpecificationIds));
            processBatch(jobSpecificationIds, fileNamesToDelete, purgeJob.getId());
        }
    }

    private Collection<String> getFileNamesToDelete(Set<String> jobSpecificationIds) {
        Set<String> fileNamesToDelete = new HashSet<>();
        Query jobsByJobSpecificationIdQuery = Query.query(Criteria.where("jobSpecification.id").in(jobSpecificationIds));
        try (Stream<JobEntity> jobEntityStream = mongoTemplate.stream(jobsByJobSpecificationIdQuery, JobEntity.class)) {
            jobEntityStream.forEach(jobEntity -> {
                if (JobTypeEnum.EXPORTJOB.equals(jobEntity.getAtType()) && Objects.nonNull(jobEntity.getFileName())) {
                    fileNamesToDelete.add(jobEntity.getFileName());
                }
            });
        }
        return fileNamesToDelete;
    }

    private void processBatch(Set<String> jobSpecificationIds, Set<String> fileNamesToDelete, String purgeJobId) {
        if (!fileNamesToDelete.isEmpty()) {
            purgeExportedFiles(new ArrayList<>(fileNamesToDelete), purgeJobId);
        }

        transactionTemplate.execute(status -> {
            // Prior to this change, the code could result in orphaned jobSpecification executions. The removal of the status limitation addresses this issue.
            // Additionally, I've introduced a verification mechanism (jobSchedulerService:executeJob) to handle cases where an execution is already in progress. And its parent deleted
            jobService.deleteByJobSpecificationIdIn(jobSpecificationIds);
            jobSpecificationRepository.deleteByIdIn(jobSpecificationIds);
            jobReportRepository.deleteByJobSpecificationIdIn(jobSpecificationIds);
            return null;
        });
    }
}
