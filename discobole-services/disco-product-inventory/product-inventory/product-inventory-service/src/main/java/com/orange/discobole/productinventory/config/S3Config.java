// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.Objects;

@Configuration
@Profile("!test")
public class S3Config {

    @Value("${config.aws.s3.bucketHost}")
    private String bucketHost;

    @Value("${config.aws.s3.bucketPort}")
    private int bucketPort;

    @Value("${config.aws.s3.bucketRegion}")
    private String bucketRegion;

    @Value("${config.aws.s3.accessKeyId}")
    private String accessKeyId;

    @Value("${config.aws.s3.secretAccessKey}")
    private String secretAccessKey;

    @Value("${config.aws.s3.httpProxy:#{null}}")
    private String httpProxy;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
        if (Objects.nonNull(httpProxy)) {
            httpClientBuilder = httpClientBuilder
                    .proxyConfiguration(ProxyConfiguration.builder()
                            .endpoint(URI.create("http://cs.pr-proxy.service.sd.diod.tech:3128"))
                            .build());
        }
        return S3Client.builder()
                .httpClient(httpClientBuilder.build())
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(bucketRegion))
                .endpointOverride(URI.create(bucketHost + ":" + bucketPort))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(bucketRegion))
                .endpointOverride(URI.create(bucketHost + ":" + bucketPort))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(bucketRegion))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(URI.create(bucketHost + ":" + bucketPort))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
