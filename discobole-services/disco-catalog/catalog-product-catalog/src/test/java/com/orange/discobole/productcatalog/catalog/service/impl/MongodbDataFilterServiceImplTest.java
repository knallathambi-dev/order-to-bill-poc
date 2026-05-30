// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.client.result.DeleteResult;
import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.service.impl.MongodbDataFilterServiceImpl;

class MongodbDataFilterServiceImplTest extends CatalogApplicationTests{

@InjectMocks
private MongodbDataFilterServiceImpl mongodbDataFilterService;

@Mock
private MongoTemplate mongoTemplate;

ProductOfferingPrice productOfferingPrice;
ProductOffering productOffering;
ProductSpecification productSpecification;

@BeforeEach
void setUp() {
	productOfferingPrice = new ProductOfferingPrice();
	productOffering=new ProductOffering();
	productSpecification=new ProductSpecification();
	ReflectionTestUtils.setField(mongodbDataFilterService, "mongoTemplate", mongoTemplate);
}
@Test
void filterProductOfferingPriceDataTest() {
    productOfferingPrice.setId("product_id123");
    productOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE);
    OffsetDateTime delDate=OffsetDateTime.now();
    productOfferingPrice.setLastUpdate(delDate.minusDays(40));
    Query query =  Query.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingPriceLifecycle.UNAVAILABLE).and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusDays(20)));
    DeleteResult res=new DeleteResult() {

        @Override
        public boolean wasAcknowledged() {
            return false;
        }

        @Override
        public long getDeletedCount() {
            
            return 0;
        }
    };
    Mockito.when(mongoTemplate.remove(query,ProductOfferingPrice.class)).thenReturn(res);
    mongodbDataFilterService.filterProductOfferingPriceData(delDate,20L,"DAYS" );
    Mockito.verify(mongoTemplate).remove(query, ProductOfferingPrice.class);
}

@Test
void filterProductOfferingDataTest() {
	productOffering.setId("productOfferingId");
	productOffering.setLifecycleStatus(ProductOfferingLifecycle.INSTUDY);
	OffsetDateTime delDate=OffsetDateTime.now();
	productOffering.setLastUpdate(delDate.minusDays(40));
	Query query =  Query.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusDays(20)));
	DeleteResult res=new DeleteResult() {
		
		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}
		
		@Override
		public long getDeletedCount() {
			
			return 0;
		}
	};
	Mockito.when(mongoTemplate.remove(query,ProductOffering.class)).thenReturn(res);
	mongodbDataFilterService.filterProductOfferingData(delDate,20L,"DAYS" );
	Mockito.verify(mongoTemplate).remove(query, ProductOffering.class);
}
@Test
void filterProductSpecificsationDataTest() {
	productSpecification.setId("product123");
	productSpecification.setLifecycleStatus(ProductSpecificationLifecycle.INSTUDY);
	OffsetDateTime delDate=OffsetDateTime.now();
	productSpecification.setLastUpdate(delDate.minusDays(40));
	Query query =  Query.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusHours(20)));
	DeleteResult res=new DeleteResult() {
		
		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}
		
		@Override
		public long getDeletedCount() {
			
			return 0;
		}
	};
	Mockito.when(mongoTemplate.remove(query,ProductSpecification.class)).thenReturn(res);
	mongodbDataFilterService.filterProductSpecificsationData(delDate,20L,"HOURS");
	Mockito.verify(mongoTemplate).remove(query, ProductSpecification.class);
}



@Test
void filterProductSpecificsationDataTest_Events() {
	productSpecification.setId("product123");
	productSpecification.setLifecycleStatus(ProductSpecificationLifecycle.INSTUDY);
	OffsetDateTime delDate = OffsetDateTime.now();
	productSpecification.setLastUpdate(delDate.minusDays(40));
	Query query = Query
			.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY)
					.and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusHours(20)));
	DeleteResult res = new DeleteResult() {

		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}

		@Override
		public long getDeletedCount() {
			
			return 3;
		}
	};

	List<String> productSpecificationIds = new ArrayList<String>();
	productSpecificationIds.add(productSpecification.getId());
	Query query2 = new Query(Criteria.where("aggregateIdentifier").in(productSpecificationIds));

	Mockito.when(mongoTemplate.find(query, ProductSpecification.class))
			.thenReturn(Arrays.asList(productSpecification));
	Mockito.when(mongoTemplate.remove(query2, "domainevents")).thenReturn(res);
	mongodbDataFilterService.filterProductSpecificationEventsData(delDate, 20L, "HOURS");
	Mockito.verify(mongoTemplate).remove(query2, "domainevents");
}

@Test
void FilterProductOfferingEventsDataTest_Events() {
	productOffering.setId("productOfferingId");
	productOffering.setLifecycleStatus(ProductOfferingLifecycle.INSTUDY);
	OffsetDateTime delDate=OffsetDateTime.now();
	productOffering.setLastUpdate(delDate.minusDays(40));
	Query query = Query
			.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingLifecycle.INSTUDY)
					.and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusHours(20)));
	DeleteResult res = new DeleteResult() {

		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}

		@Override
		public long getDeletedCount() {
			
			return 3;
		}
	};

	List<String> productOfferingIds = new ArrayList<String>();
	productOfferingIds.add(productOffering.getId());
	Query query2 = new Query(Criteria.where("aggregateIdentifier").in(productOfferingIds));

	Mockito.when(mongoTemplate.find(query, ProductOffering.class))
			.thenReturn(Arrays.asList(productOffering));
	Mockito.when(mongoTemplate.remove(query2, "domainevents")).thenReturn(res);
	mongodbDataFilterService.filterProductOfferingEventsData(delDate, 20L, "HOURS");
	Mockito.verify(mongoTemplate).remove(query2, "domainevents");
}

@Test
void filterProductSpecificsationDataTest_EventsNoData() {
	OffsetDateTime delDate = OffsetDateTime.now();
	Query query = Query
			.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY)
					.and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusHours(20)));
	DeleteResult res = new DeleteResult() {

		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}

		@Override
		public long getDeletedCount() {
			
			return 0;
		}
	};

	List<String> productSpecificationIds = new ArrayList<String>();
	Query query2 = new Query(Criteria.where("aggregateIdentifier").in(productSpecificationIds));

	Mockito.when(mongoTemplate.find(query, ProductSpecification.class))
			.thenReturn(new ArrayList<ProductSpecification>());
	Mockito.when(mongoTemplate.remove(query2, "domainevents")).thenReturn(res);
	mongodbDataFilterService.filterProductSpecificationEventsData(delDate, 20L, "HOURS");
	Mockito.verify(mongoTemplate).find(query, ProductSpecification.class);
}

@Test
void filterProductOfferingPriceEventDataTest() {
    productOfferingPrice.setId("product_id123");
    productOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE);
    OffsetDateTime delDate=OffsetDateTime.now();
    productOfferingPrice.setLastUpdate(delDate.minusDays(40));
    Query query =  Query.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingPriceLifecycle.UNAVAILABLE).and(ProductOfferingConstants.LAST_UPDATE).lt(delDate.minusDays(20)));
    DeleteResult res=new DeleteResult() {

        @Override
        public boolean wasAcknowledged() {
            return false;
        }

        @Override
        public long getDeletedCount() {
            return 1;
        }
    };
    Mockito.when(mongoTemplate.find(query,ProductOfferingPrice.class)).thenReturn(Arrays.asList(productOfferingPrice));
    Query query2 = new Query(Criteria.where("aggregateIdentifier").in(Arrays.asList("product_id123")));
    Mockito.when(mongoTemplate.remove(query2, "domainevents")).thenReturn(res);
    mongodbDataFilterService.filterProductOfferingPriceEventData(delDate,20L,"DAYS" );
    Mockito.verify(mongoTemplate).remove(query2, "domainevents");

}

@Test
void filterTemporaryProductSpecificsationData() {
	productSpecification.setId("product123");
	productSpecification.setLifecycleStatus(ProductSpecificationLifecycle.INSTUDY);
	OffsetDateTime delDate=OffsetDateTime.now();
	productSpecification.setLastUpdate(delDate.minusDays(40));
	Query query =  Query.query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).exists(false));
	DeleteResult res=new DeleteResult() {
		
		@Override
		public boolean wasAcknowledged() {
			
			return false;
		}
		
		@Override
		public long getDeletedCount() {
			
			return 0;
		}
	};
	Mockito.when(mongoTemplate.remove(query,ProductSpecification.class)).thenReturn(res);
	mongodbDataFilterService.filterTemporaryProductSpecificsationData();
	Mockito.verify(mongoTemplate).remove(query, ProductSpecification.class);
}

}
