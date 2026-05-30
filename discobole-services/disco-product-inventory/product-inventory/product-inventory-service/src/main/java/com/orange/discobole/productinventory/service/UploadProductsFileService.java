// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.v1.FileInformation;
import com.orange.discobole.productinventory.model.job.JobEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadProductsFileService {
    void uploadFile(MultipartFile file, JobEntity jobEntity) throws IOException;
    FileInformation getUploadFileUrl(String jobId) throws IOException;
}
