// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.constant.Constant;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;

import java.io.IOException;
import java.io.PipedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

public class FileUtils {
    private FileUtils() {
    }

    public static Path writePipeOutputStreamToFile(PipedInputStream outputStream, String uploadPath, FileType fileType) throws IOException {
        String fileName = getProductExportFileName(fileType);
        Path path = Paths.get(uploadPath, fileName).normalize();

        try (var fileOutputStream = Files.newOutputStream(path)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = outputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
        return path;
    }

    public static void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
    public static String getProductExportFileName(FileType fileType) {
        return Constant.EXPORT_PRODUCT_FILE_PREFIX + UUID.randomUUID().toString().split("-")[0] + "-" + OffsetDateTime.now().toInstant().toEpochMilli() + fileType.getExtension();
    }

    public static String createExportFileName(JobSpecificationEntity jobSpecification) {
        OffsetDateTime executionDate = OffsetDateTime.now();
        String jobSpecificationName = jobSpecification.getName() != null ? jobSpecification.getName() + "-" : "";
        return Constant.EXPORT_PRODUCT_FILE_PREFIX + jobSpecificationName + UUID.randomUUID().toString().split("-")[0] + "-" + executionDate.toInstant().toEpochMilli() + jobSpecification.getFileType().getExtension();
    }

    public static String createUploadFileName(JobSpecificationEntity jobSpecification) {
        String jobSpecificationName = jobSpecification.getName() != null ? jobSpecification.getName() + "-" : "";
        return Constant.UPLOAD_PRODUCT_FILE_PREFIX + jobSpecificationName + UUID.randomUUID().toString().split("-")[0] + "-" + OffsetDateTime.now().toInstant().toEpochMilli() + jobSpecification.getFileType().getExtension();
    }


}
