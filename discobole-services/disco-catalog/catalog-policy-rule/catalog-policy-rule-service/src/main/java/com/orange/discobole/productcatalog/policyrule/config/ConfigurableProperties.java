// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config")
public class ConfigurableProperties {
    private String productOfferingUrl;
    private String productSpecificationUrl;
    private String productOfferingPriceUrl;
    private String policyRuleURL;
    private String catprodcaturl;
    private String policyRuleRefUrl;

    public String getProductOfferingUrl() {
        return productOfferingUrl;
    }

    public void setProductOfferingUrl(String productOfferingUrl) {
        this.productOfferingUrl = productOfferingUrl;
    }

    public String getProductSpecificationUrl() {
        return productSpecificationUrl;
    }

    public void setProductSpecificationUrl(String productSpecificationUrl) {
        this.productSpecificationUrl = productSpecificationUrl;
    }

    public String getProductOfferingPriceUrl() {
        return productOfferingPriceUrl;
    }

    public void setProductOfferingPriceUrl(String productOfferingPriceUrl) {
        this.productOfferingPriceUrl = productOfferingPriceUrl;
    }

    public String getPolicyRuleURL() {
        return policyRuleURL;
    }

    public void setPolicyRuleURL(String policyRuleURL) {
        this.policyRuleURL = policyRuleURL;
    }

    public String getCatprodcaturl() {
        return catprodcaturl;
    }

    public void setCatprodcaturl(String catprodcaturl) {
        this.catprodcaturl = catprodcaturl;
    }

    public String getPolicyRuleRefUrl() {
        return policyRuleRefUrl;
    }

    public void setPolicyRuleRefUrl(String policyRuleRefUrl) {
        this.policyRuleRefUrl = policyRuleRefUrl;
    }
}
