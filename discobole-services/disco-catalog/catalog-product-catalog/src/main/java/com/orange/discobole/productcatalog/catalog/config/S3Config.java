// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.net.URI;

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    // Optional
    @Value("${spring.cloud.aws.s3.httpProxy:}")
    private String httpProxy;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();

        // Apply proxy ONLY for this S3 client
        if (httpProxy != null && !httpProxy.trim().isEmpty()) {
            URI proxyUri = URI.create(httpProxy.trim());
            if (proxyUri.getHost() != null && proxyUri.getPort() > 0) {
                clientConfig.setProxyHost(proxyUri.getHost());
                clientConfig.setProxyPort(proxyUri.getPort());
            }
        }
        // Ignore SSL errors
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true)
                    .build();

            clientConfig.getApacheHttpClientConfig()
                    .setSslSocketFactory(new SSLConnectionSocketFactory(
                            sslContext,
                            NoopHostnameVerifier.INSTANCE
                    ));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure S3 SSL bypass", e);
        }

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withClientConfiguration(clientConfig)
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
