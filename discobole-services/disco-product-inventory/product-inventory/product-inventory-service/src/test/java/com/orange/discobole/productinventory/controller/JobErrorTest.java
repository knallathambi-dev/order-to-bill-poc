// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.service.impl.JobExecutorServiceImpl;
import com.orange.discobole.productinventory.service.impl.JobSchedulerServiceImpl;
import com.orange.discobole.productinventory.util.AbstractJobsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobErrorTest extends AbstractJobsTest {
    @MockBean
    private JobExecutorServiceImpl jobExecutorService;
    @Autowired
    private JobSchedulerServiceImpl jobSchedulerService;

    @Test
    void givenNonEmptyDatabase_whenCreateExportJobWithCsvContentType_thenValidFileCreated() throws Exception {
        String errorMsg = "JOB Exception Test";
        doThrow(new ProductInventoryException(
                errorMsg
        )).when(jobExecutorService).execute(
                any(JobSpecificationEntity.class),
                any(JobEntity.class)
        );
        mongoTemplate.save(getDefaultProductEntityBuilder().status(ProductStatusType.ABORTED).build());
        Map<String, String> filter = new HashMap<>();
        filter.put("contentType", ContentTypeEnum.JSON.getValue());
        ResultActions resultActions = createExportJob(
                "status=CREATED",
                ContentTypeEnum.CSV,
                ImmediateJobScheduler
                        .builder()
                        .build(),
                List.of("id", "status", "description"));
        resultActions.andExpect(status().isCreated());
        ExportJobSpecification exportJob = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        mongoTemplate.save(getDefaultProductEntityBuilder().build());
        jobSchedulerService.executeScheduledJobs();
        JobEntity byId = mongoTemplate.findOne(
                Query.query(Criteria.where("jobSpecification.id").is(exportJob.getId())),
                JobEntity.class
        );
        Assertions.assertNotNull(byId);
        Assertions.assertNotNull(byId.getErrorLog());
        Assertions.assertTrue(byId.getErrorLog().contains(errorMsg));
        Assertions.assertEquals(JobStatusType.FAILED, byId.getStatus());
    }
}
