// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("openshift-redis")
public class RedisConfig {

    private static final Logger LOGGER = LogManager.getLogger(RedisConfig.class);
    @Value("${spring.redis.host}") String host;
    @Value("${spring.redis.port}") int port;
    @Value("${spring.redis.password}") String password;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        return new LettuceConnectionFactory(config);
    }


    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        LOGGER.info("connetion with redis started");
        RedisTemplate<String, String> template = new RedisTemplate<>();
        try{
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer()); // key as plain string
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // value as JSON
        }catch (Exception exception){
            LOGGER.info("connetion with redis failed");
        }
        LOGGER.info("connetion with redis completed");
        return template;
    }
}
