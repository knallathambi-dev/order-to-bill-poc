// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.AttributeMap;

import java.net.URI;

import static software.amazon.awssdk.http.SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES;

@Testcontainers
@TestConfiguration
@Profile("test")
public class S3TestConfig {

    @Container
    private static final S3MockContainer s3Mock = new S3MockContainer(
            DockerImageName.parse(
                            TestConfigurationProperties.getDockerProxyIfExists() + "adobe/s3mock:latest")
                    .asCompatibleSubstituteFor("adobe/s3mock")
    );

    @Value("${config.aws.s3.bucketRegion}")
    private String bucketRegion;

    @Value("${config.aws.s3.accessKeyId}")
    private String accessKeyId;

    @Value("${config.aws.s3.secretAccessKey}")
    private String secretAccessKey;

    @PostConstruct
    public void s3Startup() {
        s3Mock.start();
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        var httpClient = UrlConnectionHttpClient.builder()
                .buildWithDefaults(AttributeMap.builder()
                        .put(TRUST_ALL_CERTIFICATES, Boolean.TRUE)
                        .build());
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(bucketRegion))
                .endpointOverride(URI.create(s3Mock.getHttpsEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .httpClient(httpClient)
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(bucketRegion))
                .endpointOverride(URI.create(s3Mock.getHttpsEndpoint()))
                .build();

    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(bucketRegion))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(URI.create(s3Mock.getHttpsEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
