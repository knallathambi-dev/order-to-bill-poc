// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code com.orange.bos.productofferingprice.config.ConfigurableProperties}
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

    private String productOfferingPriceUrl;
    private String productOfferingUrl;
    private String catprodcaturl;

    private String currencyUrl;

    private String frequencyUrl;

    public String getCurrencyUrl() {
        return currencyUrl;
    }

    public void setCurrencyUrl(String currencyUrl) {
        this.currencyUrl = currencyUrl;
    }

    public String getFrequencyUrl() {
        return frequencyUrl;
    }

    public void setFrequencyUrl(String frequencyUrl) {
        this.frequencyUrl = frequencyUrl;
    }

    public String getProductOfferingPriceUrl() {
        return productOfferingPriceUrl;
    }

    public void setProductOfferingPriceUrl(String productOfferingPriceUrl) {
        this.productOfferingPriceUrl = productOfferingPriceUrl;
    }

    public String getProductOfferingUrl() {
        return productOfferingUrl;
    }

    public void setProductOfferingUrl(String productOfferingUrl) {
        this.productOfferingUrl = productOfferingUrl;
    }

    public String getCatprodcaturl() {
        return catprodcaturl;
    }

    public void setCatprodcaturl(String catprodcaturl) {
        this.catprodcaturl = catprodcaturl;
    }
}
