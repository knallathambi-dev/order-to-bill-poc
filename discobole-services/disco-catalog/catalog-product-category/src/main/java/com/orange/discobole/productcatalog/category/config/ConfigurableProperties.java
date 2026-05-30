// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code com.orange.bos.catalogconfigurator.config.ConfigurableProperties}
 * class is used to read properties from config-server. All the properties are
 * required to be configured in this class.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "config")
public class ConfigurableProperties {

    private List<String> eventType;
    private String cfsQuery;
    private String productSpecQuery;
    private String categoryUrl;
    private String categoryEntityUrl;
    private String marketUrl;
    private String channelUrl;
    private String productOfferingUrl;
    private String productOfferingPriceUrl;
    private String stockItemTypeUrl;
    private String stockItemUrl;
    private String catprodcaturl;

    /**
     * @return the channelUrl
     */
    public String getChannelUrl() {
        return channelUrl;
    }

    /**
     * @param channelUrl the channelUrl to set
     */
    public void setChannelUrl(final String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public List<String> getEventType() {
        return eventType;
    }

    public void setEventType(final List<String> eventType) {
        this.eventType = eventType;
    }

    public String getCfsQuery() {
        return cfsQuery;
    }

    public String getProductSpecQuery() {
        return productSpecQuery;
    }

    public void setCfsQuery(String cfsQuery) {
        this.cfsQuery = cfsQuery;
    }

    public void setProductSpecQuery(final String productSpecQuery) {
        this.productSpecQuery = productSpecQuery;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public String getCategoryEntityUrl() {
        return categoryEntityUrl;
    }

    public void setCategoryEntityUrl(String categoryEntityUrl) {
        this.categoryEntityUrl = categoryEntityUrl;
    }

    public void setCategoryUrl(final String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getMarketUrl() {
        return marketUrl;
    }

    public void setMarketUrl(final String marketUrl) {
        this.marketUrl = marketUrl;
    }

    public String getProductOfferingUrl() {
        return productOfferingUrl;
    }

    public void setProductOfferingUrl(String relationshipUrl) {
        this.productOfferingUrl = relationshipUrl;
    }

    public String getProductOfferingPriceUrl() {
        return productOfferingPriceUrl;
    }

    public void setProductOfferingPriceUrl(String productOfferingPriceUrl) {
        this.productOfferingPriceUrl = productOfferingPriceUrl;
    }

    public String getStockItemTypeUrl() {
        return stockItemTypeUrl;
    }

    public void setStockItemTypeUrl(String stockItemTypeUrl) {
        this.stockItemTypeUrl = stockItemTypeUrl;
    }

    public String getStockItemUrl() {
        return stockItemUrl;
    }

    public void setStockItemUrl(String stockItemUrl) {
        this.stockItemUrl = stockItemUrl;
    }

    public String getCatprodcaturl() {
        return catprodcaturl;
    }

    public void setCatprodcaturl(String catprodcaturl) {
        this.catprodcaturl = catprodcaturl;
    }
}
