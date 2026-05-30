// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface S3Service {
    PresignedGetObjectRequest generatePresignedDownloadUrl(String fileKey);

    PresignedPutObjectRequest generatePresignedUploadUrl(String fileKey);

    void uploadFile(Path filePath, String key) throws IOException;

    void uploadFile(String fileName, InputStream inputStream) throws IOException;

    CompletableFuture<PutObjectResponse> uploadStream(String key, InputStream stream) throws IOException;

    void deleteFiles(List<String> fileNamesBatch) throws IOException;

    boolean fileExists(String key);

    ResponseInputStream<GetObjectResponse> getFile(String key);
}
