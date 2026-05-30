// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.service.impl.ServiceSpecServiceImpl;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ServiceSpecServiceImplTest extends CatalogApplicationTests {

	private String cfsId;
	private String lifecycleStatus;
	private String name;
	private String version;
	private ServiceSpecification serviceSpecification;

	@InjectMocks
	private ServiceSpecServiceImpl serviceSpecService;

	@Mock
	MongoTemplate mongoTemplate;

	private Map<String, Object> requestParams = new HashMap<>();

	@BeforeEach
	void setUp() {
		cfsId = "1";
		lifecycleStatus = "active";
		name = "BOS Mobile line";
		version = "1";
		serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId(cfsId);
		serviceSpecification.setLifecycleStatus(lifecycleStatus);
		serviceSpecification.setName(name);
		serviceSpecification.setVersion(version);
		requestParams.put("name", name);
		serviceSpecService.saveServiceSpecification(serviceSpecification);
	}

	@Test
	void findServiceSpecificationsTest() throws UnsupportedEncodingException {
		List<ServiceSpecification> serviceSpec = new ArrayList<>();
		serviceSpec.add(serviceSpecification);
		requestParams.put("version", version);
		requestParams.put("lifecycleStatus", "launched");
		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

		Query query = new Query();

		for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
		}
		when(mongoTemplate.find(query, ServiceSpecification.class)).thenReturn(serviceSpec);
		List<ServiceSpecification> cfsSpecs = serviceSpecService.fetchServiceSpecifications(requestParams);
		Assertions.assertThat(cfsSpecs).hasSize(1);

	}

	@Test
	void findServiceSpecificationByIdTest() {
		when(mongoTemplate.findById("1", ServiceSpecification.class)).thenReturn(serviceSpecification);
		ServiceSpecification serviceSpec = serviceSpecService.fetchServiceSpecificationById(cfsId);
		Assertions.assertThat(serviceSpec.getId()).isEqualTo(cfsId);
		cfsId = "4";
		serviceSpec = serviceSpecService.fetchServiceSpecificationById(cfsId);
		Assertions.assertThat(serviceSpec).isNull();
	}

	@Test
	void fetchProductSpecificationByIdAndFieldListTest() {
		List<String> fieldList = List.of("validFor");
		OffsetDateTime startDateTime = OffsetDateTime.now();
		ServiceSpecification serviceSpec = new ServiceSpecification();
		serviceSpec.id("cfs1")
				.validFor(new TimePeriod().startDateTime(startDateTime).endDateTime(startDateTime.plusDays(7)));
		when(mongoTemplate.findOne(any(Query.class), eq(ServiceSpecification.class))).thenReturn(serviceSpec);
		ServiceSpecification returnedServiceSpec = serviceSpecService
				.fetchServiceSpecificationById("cfs1", fieldList);
		assertEquals(serviceSpec.getId(),returnedServiceSpec.getId());
		assertNotNull(returnedServiceSpec.getValidFor());
	}

	@Test
	void updateServiceSpecificationTest() {
		ServiceSpecification serviceSpec = new ServiceSpecification();
		cfsId = "1";
		serviceSpec.setId(cfsId);
		lifecycleStatus = "launched";
		serviceSpec.setLifecycleStatus(lifecycleStatus);
		serviceSpecService.saveServiceSpecification(serviceSpec);
		when(mongoTemplate.findById("1", ServiceSpecification.class)).thenReturn(serviceSpec);
		ServiceSpecification cfsSpecs = serviceSpecService.fetchServiceSpecificationById(cfsId);
		Assertions.assertThat(cfsSpecs.getLifecycleStatus()).isEqualTo("launched");
	}
}