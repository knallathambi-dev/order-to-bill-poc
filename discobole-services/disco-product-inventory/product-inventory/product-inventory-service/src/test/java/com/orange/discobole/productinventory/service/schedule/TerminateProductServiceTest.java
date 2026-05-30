// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.schedule;

import com.orange.discobole.productinventory.dto.kafka.StateChangeProduct;
import com.orange.discobole.productinventory.dto.v1.JobStatusType;
import com.orange.discobole.productinventory.dto.v1.JobTypeEnum;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.repository.CustomProductRepository;
import com.orange.discobole.productinventory.service.impl.JobSchedulerServiceImpl;
import com.orange.discobole.productinventory.util.AbstractTest;
import com.orange.discobole.productinventory.util.ProductAssertionUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.dto.v1.ProductStatusType.*;
import static com.orange.discobole.productinventory.util.AsyncAssertionUtil.consumeAndAssertEqualityForStateChangeEvent;
import static com.orange.discobole.productinventory.util.creator.ProductEntityCreator.*;
import static org.junit.jupiter.api.Assertions.*;

class TerminateProductServiceTest extends AbstractTest {

    @Autowired
    private JobSchedulerServiceImpl jobSchedulerService;
    @Autowired
    private CustomProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    @Test
    void whenTerminationProductJobExecuted_thenScheduledTerminationProductJobIsPerformed() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();
        ProductEntity updatedAtomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        assertNotNull(updatedAtomic2.getLastUpdateDate());
        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        assertEquals(1, mongoTemplate.findAll(JobSpecificationEntity.class).size());
        List<JobReportEntity> all = mongoTemplate.findAll(JobReportEntity.class);

        assertEquals(1, all.size());
        assertEquals(6, all.get(0).getFailedProducts().size() +
                all.get(0).getSucceededProducts().size());
    }

    private void executeProductTerminationJob() {
        Query query = new Query();
        query.addCriteria(Criteria.where(JobEntity.Fields.atType).is(JobTypeEnum.TERMINATIONJOB));
        JobEntity one = mongoTemplate.findOne(query, JobEntity.class);
        if (one != null) {
            jobSchedulerService.executeJob(one);
        }
    }

    @Test
    void whenTerminationProductJobExecutedWithStatusCancelled_thenScheduledTerminationProductJobIsPerformed() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CANCELLED, ProductOperationalStatusType.CANCELLED, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3));

        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, CANCELLED, ProductOperationalStatusType.CANCELLED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, ACTIVE, ProductOperationalStatusType.ACTIVE);
        assertEquals(1, mongoTemplate.findAll(JobSpecificationEntity.class).size());
        List<JobReportEntity> all = mongoTemplate.findAll(JobReportEntity.class);
        assertEquals(1, all.size());
        assertEquals(3, all.get(0).getFailedProducts().size() +
                all.get(0).getSucceededProducts().size());
    }

    @Test
    void whenTerminationProductJobExecutedAllProductWithTerminationDate_thenScheduledTerminationProductJobIsPerformed() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).terminationDate(OffsetDateTime.now()).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).terminationDate(OffsetDateTime.now()).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, CREATED, ProductOperationalStatusType.CREATED).terminationDate(OffsetDateTime.now()).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        assertEquals(1, mongoTemplate.findAll(JobSpecificationEntity.class).size());
        List<JobReportEntity> all = mongoTemplate.findAll(JobReportEntity.class);

        assertEquals(1, all.size());
        assertEquals(6, all.get(0).getFailedProducts().size() +
                all.get(0).getSucceededProducts().size());
    }

    @Test
    void givenProductSpecificationWithInvalidStatus_whenTerminationProductJobExecuted_thenStatusRemainsUnchanged() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();
        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, ABORTED, ProductOperationalStatusType.ABORTED);
    }

    @Test
    void givenProductSpecificationWithInvalidStatusCreated_whenTerminationProductJobExecuted_thenStatusRemainsUnchanged() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CREATED, ProductOperationalStatusType.CREATED, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();
        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, ABORTED, ProductOperationalStatusType.ABORTED);
    }

    @Test
    void givenProductSpecificationWithInvalidStatusCanceled_whenTerminationProductJobExecuted_thenStatusRemainsUnchanged() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CANCELLED, ProductOperationalStatusType.CANCELLED).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, CANCELLED, ProductOperationalStatusType.CANCELLED).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, CANCELLED, ProductOperationalStatusType.CANCELLED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, CANCELLED, ProductOperationalStatusType.CANCELLED);
    }

    @Test
    void givenProductSpecificationWithInvalidStatusForAtomicProduct_whenTerminationProductJobExecuted_thenExpectedStatusesUpdated() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, ABORTED, ProductOperationalStatusType.ABORTED);
    }


    @Test
    void givenValidHierarchyOfProductsWithTerminationDate_whenTerminationProductJobExecuted_thenExpectedProductsTerminated() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps4 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic4 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps4, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2, ps4).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps1).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3, atomic4));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ps4 = mongoTemplate.findById(ps4.getId(), ProductEntity.class);
        atomic4 = mongoTemplate.findById(atomic4.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps4, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic4, TERMINATED, ProductOperationalStatusType.TERMINATED);
        Set<String> updatedProductIds = List.of(ps2.getId(), ps4.getId(), ps1.getId(), atomic1.getId(), ps3.getId(), atomic2.getId(), atomic4.getId())
                .stream().collect(Collectors.toSet());
        List<ProductEntity> updatedProductEntities = productRepository.findProductForEventStatusChangeBy(updatedProductIds);
        Set<StateChangeProduct> updatedProductDTO =
                updatedProductEntities
                        .stream()
                        .map(product -> productMapper.toDtoWithProductIdOnly(product))
                        .map(product -> StateChangeProduct.fromProduct(product, ProductStatusType.CREATED))
                        .collect(Collectors.toSet());
        consumeAndAssertEqualityForStateChangeEvent(updatedProductDTO);
    }

    @Test
    void givenInValidHierarchyOfProductsWithTerminationDate_whenTerminationProductJobExecuted_thenExpectedProductsTerminated() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps4 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity atomic4 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps4, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2, ps4).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps1).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3, atomic4));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ps4 = mongoTemplate.findById(ps4.getId(), ProductEntity.class);
        atomic4 = mongoTemplate.findById(atomic4.getId(), ProductEntity.class);

        ProductAssertionUtil.assertProductExpectedStatus(ps2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps4, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ABORTED, ProductOperationalStatusType.ABORTED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, TERMINATED, ProductOperationalStatusType.TERMINATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic4, ABORTED, ProductOperationalStatusType.ABORTED);
        assertTrue(ps1.getExecuteTerminationProcess());
        List<JobEntity> jobs = mongoTemplate.findAll(JobEntity.class);
        assertEquals(2, jobs.size());
        assertEquals(JobStatusType.SUCCEEDED, jobs.get(0).getStatus());

        List<JobReportEntity> all = mongoTemplate.findAll(JobReportEntity.class);

        assertEquals(1, all.size());
        assertEquals(3, all.get(0).getFailedProducts().size());
        assertEquals(5, all.get(0).getSucceededProducts().size());
    }

    @Test
    void givenProductWithoutReliesFromRelationship_whenTerminationProductJobExecuted_thenNoChangeInStatus() {
        ProductEntity ps = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity atomic = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps, CREATED, ProductOperationalStatusType.CREATED).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps = mongoTemplate.findById(ps.getId(), ProductEntity.class);
        atomic = mongoTemplate.findById(atomic.getId(), ProductEntity.class);

        ProductAssertionUtil.assertProductExpectedStatus(ps, CREATED, ProductOperationalStatusType.CREATED);
        ProductAssertionUtil.assertProductExpectedStatus(atomic, CREATED, ProductOperationalStatusType.CREATED);
    }

    @Test
    void whenTerminationProductJobExecutedAllProductMarked_thenScheduledTerminationProductJobIsPerformed() {
        ProductEntity ps2 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).executeTerminationProcess(true).build());
        ProductEntity atomic2 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps2, ACTIVE, ProductOperationalStatusType.ACTIVE).executeTerminationProcess(true).terminationDate(OffsetDateTime.now()).build());
        ProductEntity ps1 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE, ps2).executeTerminationProcess(true).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic1 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps1, ACTIVE, ProductOperationalStatusType.ACTIVE).executeTerminationProcess(true).terminationDate(OffsetDateTime.now()).build());
        ProductEntity ps3 = mongoTemplate.save(createProductSpecification(ObjectId.get().toString(), ACTIVE, ProductOperationalStatusType.ACTIVE).executeTerminationProcess(true).terminationDate(OffsetDateTime.now()).build());
        ProductEntity atomic3 = mongoTemplate.save(createAtomic(ObjectId.get().toString(), ps3, ACTIVE, ProductOperationalStatusType.ACTIVE).executeTerminationProcess(true).terminationDate(OffsetDateTime.now()).build());
        ProductEntity bundled = mongoTemplate.save(createBundledProduct(atomic1, atomic2, atomic3));
        mongoTemplate.save(createProductContract(bundled));
        executeProductTerminationJob();

        ps2 = mongoTemplate.findById(ps2.getId(), ProductEntity.class);
        atomic2 = mongoTemplate.findById(atomic2.getId(), ProductEntity.class);
        ps1 = mongoTemplate.findById(ps1.getId(), ProductEntity.class);
        atomic1 = mongoTemplate.findById(atomic1.getId(), ProductEntity.class);
        ps3 = mongoTemplate.findById(ps3.getId(), ProductEntity.class);
        atomic3 = mongoTemplate.findById(atomic3.getId(), ProductEntity.class);
        ProductAssertionUtil.assertProductExpectedStatus(ps2, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(ps1, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(atomic1, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(ps3, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(atomic3, ACTIVE, ProductOperationalStatusType.ACTIVE);
        ProductAssertionUtil.assertProductExpectedStatus(atomic2, ACTIVE, ProductOperationalStatusType.ACTIVE);
    }
}
