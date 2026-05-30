// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;


import com.orange.discobole.productinventory.service.S3Service;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Implementation of the S3Service interface for interacting with Amazon S3.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner preSigner;
    private final S3AsyncClient s3AsyncClient;
    @Value("${config.aws.s3.linkExpirationMinutes}")
    private long linkExpirationMinutes;
    @Value("${config.aws.s3.bucketName}")
    private String bucketName;

    /**
     * Generates a pre-signed URL for accessing a file in the S3 bucket.
     *
     * @param fileKey the key of the file in the S3 bucket
     * @return the pre-signed URL as a String
     */
    @Override
    public PresignedGetObjectRequest generatePresignedDownloadUrl(String fileKey) {
        GetObjectPresignRequest getObjectPresignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(linkExpirationMinutes))
                        .getObjectRequest(b -> b.bucket(bucketName).key(fileKey))
                        .build();
        PresignedGetObjectRequest presignedGetObjectRequest =
                preSigner.presignGetObject(getObjectPresignRequest);
        preSigner.close();
        return presignedGetObjectRequest;

    }

    /**
     * Generates a pre-signed URL for uploading a file to the S3 bucket.
     *
     * @param fileKey the key of the file in the S3 bucket
     * @return the pre-signed upload URL as a String
     */
    @Override
    public PresignedPutObjectRequest generatePresignedUploadUrl(String fileKey) {
        PutObjectPresignRequest putObjectPresignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(linkExpirationMinutes))
                        .putObjectRequest(b -> b.bucket(bucketName).key(fileKey))
                        .build();

        PresignedPutObjectRequest presignedPutObjectRequest =
                preSigner.presignPutObject(putObjectPresignRequest);

        preSigner.close();
        return presignedPutObjectRequest;
    }
    /**
     * Uploads a file to the S3 bucket from a specified Path.
     *
     * @param filePath the Path of the file to upload
     */
    @Override
    public void uploadFile(Path filePath, String key) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(Mimetype.MIMETYPE_OCTET_STREAM)
                .build(), RequestBody.fromFile(filePath));

    }


    /**
     * Uploads a file to the S3 bucket from a specified InputStream.
     *
     * @param fileName    the name of the file to upload
     * @param inputStream the InputStream of the file to upload
     * @throws IOException if an I/O error occurs reading from the file
     */
    @Override
    public void uploadFile(String fileName, InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, baos);
            byte[] contentBytes = baos.toByteArray();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(contentBytes)
            );
        } catch (S3Exception e) {
            log.error("Failed to upload to S3", e);
            throw e;
        }
    }

    @Override
    public CompletableFuture<PutObjectResponse> uploadStream(String key, InputStream stream) {
        BlockingInputStreamAsyncRequestBody body =
                AsyncRequestBody.forBlockingInputStream(null); // 'null' indicates a stream will be provided later.

        CompletableFuture<PutObjectResponse> responseFuture =
                s3AsyncClient.putObject(r -> r.bucket(bucketName).key(key), body);

        body.writeInputStream(stream);

        return responseFuture;
    }

    @Override
    public void deleteFiles(List<String> fileNamesBatch) {
        List<ObjectIdentifier> deletedKeys = fileNamesBatch.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(deleteBuilder -> deleteBuilder.objects(deletedKeys))
                .build();
        s3Client.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public boolean fileExists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            log.warn("File with key '{}' does not exist in S3.", key);
            return false;
        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getFile(String key) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            log.error("Failed to retrieve file from S3: {}", key, e);
            throw e;
        }
    }

}
