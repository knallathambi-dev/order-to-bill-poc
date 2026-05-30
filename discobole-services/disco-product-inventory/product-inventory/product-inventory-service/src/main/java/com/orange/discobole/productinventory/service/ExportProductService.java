// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.v1.ContentTypeEnum;
import com.orange.discobole.productinventory.dto.v1.ExportFileInformation;
import com.orange.discobole.productinventory.dto.v1.ExportJobSpecification;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.PipedInputStream;

public interface ExportProductService {

    ExportJobSpecification createExportJob(ExportJobSpecification jobSpecification);

    void exportProducts(MultiValueMap<String, Object> filters, PipedInputStream inputStream, ContentTypeEnum contentType) throws IOException;

    String exportProducts(JobSpecificationEntity jobSpecification, String fileName) throws IOException;

    ExportFileInformation getExportFileInformation(JobEntity jobEntity, JobSpecificationEntity jobSpecificationEntity);

}
