package com.orange.discobole.productcatalog.policyrule.config;//// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Profile("!openshift-redis")
public class RedisTemplateConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new DummyRedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet(); // Important
        return template;
    }

    static class DummyRedisConnectionFactory implements RedisConnectionFactory {


        @Override
        public RedisConnection getConnection() {
            throw new UnsupportedOperationException(PolicyRuleConstants.DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED);
        }

        @Override
        public RedisClusterConnection getClusterConnection() {
            throw new UnsupportedOperationException(PolicyRuleConstants.DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED);        }

        @Override
        public boolean getConvertPipelineAndTxResults() {
            throw new UnsupportedOperationException(PolicyRuleConstants.DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED);        }

        @Override
        public RedisSentinelConnection getSentinelConnection() {
            throw new UnsupportedOperationException(PolicyRuleConstants.DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED);        }

        @Override
        public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
            throw new UnsupportedOperationException(PolicyRuleConstants.DUMMY_REDIS_CONNECTION_CAN_NOT_BE_USED);        }
    }
}
