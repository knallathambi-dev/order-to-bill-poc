// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;
import com.orange.disco.admin.MarketSegment;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.repository.MarketSegmentRepository;
import com.orange.discobole.productcatalog.administration.service.MarketSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MarketSegmentImpl implements MarketSegmentService{


    private MarketSegmentRepository marketSegmentRepository;

    @Autowired
    public MarketSegmentImpl(MarketSegmentRepository marketSegmentRepository) {
        this.marketSegmentRepository = marketSegmentRepository;
    }

    @Override
    public MarketSegment createMarketSegment(MarketSegment marketSegment) {
        if (marketSegment == null) {
            throw new MissingBodyFieldException(23, "Missing Body marketSegment", Constants.ERROR_NOT_FOUND );
        } else if ( marketSegment.getName().isBlank()) {
            throw new MissingBodyFieldException(23, "Name cannot be blank", "Name cannot be blank");
        }

        // Set the ID from the user request if present, else generate a random ID
        if (marketSegment.getId() == null || marketSegment.getId().isBlank()) {
            marketSegment.setId(UUID.randomUUID().toString());
        }


        if (marketSegmentRepository.existsById(marketSegment.getId())) {

            throw new MissingBodyFieldException(23, "Duplicate ID: A marketSegment with this ID already exists", "Conflict");
        }

        marketSegment.setLastUpdate(LocalDateTime.now());
        return marketSegmentRepository.save(marketSegment);  // Save the channel


    }

    @Override
    public List<MarketSegment> getAllMarketSegment() {
        return this.marketSegmentRepository.findAll();
    }

    @Override
    public MarketSegment getMarketSegmentById(String marketSegmentId) {
        Optional<MarketSegment> marketSegmentDb = this.marketSegmentRepository.findById(marketSegmentId);

        if (marketSegmentDb.isPresent()) {
            return marketSegmentDb.get();
        }
        else{
            throw new MissingBodyFieldException(60,Constants.ERROR_MARKET_SEGMENT_NOT_FOUND,Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public MarketSegment updateMarketSegment(MarketSegment marketSegment) {
        Optional <MarketSegment> marketSegmentDb = this.marketSegmentRepository.findById(marketSegment.getId());
        if (marketSegmentDb.isPresent()) {
            if (!( marketSegment.getName().isBlank())  ) {
                MarketSegment marketSegmentUpdate = marketSegmentDb.get();
                marketSegmentUpdate.setId(marketSegment.getId());
                marketSegmentUpdate.setName(marketSegment.getName());
                marketSegmentUpdate.setHref(marketSegment.getHref());
                marketSegmentUpdate.setAtBaseType(marketSegment.getAtBaseType());
                marketSegmentUpdate.setAtType(marketSegment.getAtType());
                marketSegmentUpdate.setLastUpdate(marketSegment.getLastUpdate());
                marketSegmentRepository.save(marketSegmentUpdate);
                return marketSegmentUpdate;
            }
            else {
                throw new MissingBodyFieldException(23,"Missing Body Field MarketSegmentName",Constants.ERROR_NOT_FOUND);

            }
        }
        else {
            throw new MissingBodyFieldException(60, Constants.ERROR_MARKET_SEGMENT_NOT_FOUND,Constants.ERROR_NOT_FOUND);

        }
    }

    @Override
    public void deleteMarketSegment(String id) {
        if (id.contains(",")) {
            List<String> ids = Arrays.asList(id.split(","));
            this.marketSegmentRepository.deleteAllById(ids);
        } else {
            Optional<MarketSegment> marketSegmentDb = this.marketSegmentRepository.findById(id);

            if (marketSegmentDb.isPresent()) {
                this.marketSegmentRepository.delete(marketSegmentDb.get());
            } else {

                throw new MissingBodyFieldException(60, "MarketSegment Not Found", "Not Found");
            }
        }
    }
    }
