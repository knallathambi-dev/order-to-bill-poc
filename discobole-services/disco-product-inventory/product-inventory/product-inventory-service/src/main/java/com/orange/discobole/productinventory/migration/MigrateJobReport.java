// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.migration;

import com.orange.discobole.productinventory.model.JobReportProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

@ChangeUnit(id = "migrate-job-report", order = "010", author = "khames")
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class MigrateJobReport {
    private final MongoTemplate mongoTemplate;

    @Execution
    public void execute() {
        List<Document> terminationJobReports = mongoTemplate.findAll(Document.class, "terminationJobReport");
        for (Document terminationJobReport : terminationJobReports) {
            try {
                JobReportEntity build = JobReportEntity
                        .builder()
                        .jobId(terminationJobReport.getString("jobId"))
                        .jobSpecificationId(terminationJobReport.getString("jobSpecificationId"))
                        .failedProducts(createProducts(terminationJobReport.getList("productsTerminatedWithFailure", ProductRefEntity.class)))
                        .succeededProducts(createProducts(terminationJobReport.getList("productsTerminatedWithSuccess", ProductRefEntity.class)))
                        .build();
                mongoTemplate.save(build);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }


    }

    private List<JobReportProductRefEntity> createProducts(List<ProductRefEntity> productsTerminatedWithFailure) {
        return Optional.ofNullable(productsTerminatedWithFailure)
                .orElse(List.of())
                .stream()
                .map(productRefEntity -> JobReportProductRefEntity
                        .builder()
                        .id(productRefEntity.getId().toString())
                        .atType(productRefEntity.getAtType())
                        .name(productRefEntity.getName())
                        .build()
                ).toList();
    }

    @RollbackExecution
    public void rollback() {
        log.debug("Rollback execution for InitializeProductOfferReportingValues");
    }

}
