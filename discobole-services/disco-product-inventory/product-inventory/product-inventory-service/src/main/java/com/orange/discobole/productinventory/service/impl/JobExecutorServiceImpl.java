// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.service.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JobExecutorServiceImpl implements JobExecutorService {
    private final ExportProductService exportProductService;
    private final PurgeService purgeService;
    private final TerminateProductService terminateProductService;
    private final ImportJobService importJobService;

    @Override
    public void execute(JobSpecificationEntity jobSpecification, JobEntity jobEntity) throws IOException {
            if (jobSpecification == null) {
                throw new IllegalArgumentException("JobSpecification must not be null");
            }
            if (jobSpecification.getId() == null) {
                throw new IllegalArgumentException("JobSpecification Id must not be null");
            }
            if (jobSpecification.getAtType() == null) {
                throw new IllegalArgumentException("JobSpecification @type must not be null");
            }
            switch (jobSpecification.getAtType()) {
                case TERMINATIONJOBSPECIFICATION ->
                        terminateProductService.terminateProducts(jobEntity);
                case EXPORTJOBSPECIFICATION -> {
                    String fileName = exportProductService.exportProducts(jobSpecification, jobEntity.getFileName());
                    jobEntity.setFileName(fileName);
                }
                case PURGEJOBSPECIFICATION -> purgeService.purge(jobSpecification);
                case IMPORTJOBSPECIFICATION ->
                        importJobService.executeImport(jobSpecification, jobEntity);
                default -> throw new UnsupportedTypeException(jobSpecification.getAtType());
            }

    }
}
