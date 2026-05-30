// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProceesEntityCleanUpDTO;

public interface MongodbDataFilterService {

    Long filterProductOfferingData(Long days);

	Long filterProductOfferingData(OffsetDateTime delDate,Long interval,String intervalUnit);
	
	Long filterProductSpecificationData(Long days);

	long filterProductOfferingPriceData(OffsetDateTime lastUpdateDateTime,Long interval,String intervalUnit);

	Long filterProductSpecificsationData(OffsetDateTime delDate, Long interval, String intervalUnit);
	
	Long filterTemporaryProductSpecificsationData();
	
	Long filterTemporaryProductOfferingData();

	Long filterProductSpecificationEventsData(OffsetDateTime delDate, Long interval, String intervalUnit);
	
	
	Long filterProductOfferingEventsData(OffsetDateTime delDate, Long interval, String intervalUnit);

	Long filterProductOfferingPriceEventData(OffsetDateTime lastUpdateDateTime, Long interval, String intervalUnit);

	String processEntityCleanUp(ProceesEntityCleanUpDTO processCleanUpEntity) ;
}
