// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;

@Service
public interface ProductOfferingPriceService {

	List<ProductOfferingPrice> getProductOfferingPrices(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;

	ProductOfferingPrice getProductOfferingPriceById(String productOfferingPriceId);

	Object getProductOfferingPriceById(String productOfferingPriceId, List<String> fieldList);

	void saveProductOfferingPrice(ProductOfferingPrice productOfferingPrice);

	void removeProductOfferingPrice(String id);

	void updateProductOfferingPrice(String productOfferingPriceId, Update update);

	void saveProductOfferingPriceWithRandomId(ProductOfferingPrice productOfferingPrice);

	long countProductOfferingPrice(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchProductOfferingPriceWithCount(Map<String, Object> requestParams, Long skip, Long limit,
			String fields) throws UnsupportedEncodingException;

	List<ProductOfferingPrice> getProductOfferingPricesByIdsWithSubtypes(Set<String> ids);
}
