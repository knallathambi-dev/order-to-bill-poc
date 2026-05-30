// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.orange.discobole.productcatalog.catalog.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Date;

@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFileAndGetPresignedUrl(String fileName, byte[] fileData) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileData.length);

        // File upload to S3
        amazonS3.putObject(bucketName, fileName,
                new ByteArrayInputStream(fileData), metadata);

        // Presigned Download URL generate (24hrs valid)
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));

        return amazonS3.generatePresignedUrl(request).toString();
    }
}