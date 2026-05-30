// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;


import com.orange.disco.admin.Unit;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.UnitRepository;
import com.orange.discobole.productcatalog.administration.service.UnitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UnitServiceImpl implements UnitService {
    private static final Logger LOGGER = LogManager.getLogger(UnitServiceImpl.class);

    private UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public Unit createUnit(Unit unit) {
        if(unit == null){
            throw new MissingBodyFieldException(23,"Missing Body Unit", Constants.ERROR_NOT_FOUND);
        }
       else if ( unit.getUnitOfMeasure().isBlank()  ) {
            LOGGER.info("in if UnitOfMeasure is blank");
            throw new MissingBodyFieldException(23,"Missing Body Field UnitOfMeasure", Constants.ERROR_NOT_FOUND);
            
        }
        if (unitRepository.existsByUnitOfMeasure(unit.getUnitOfMeasure())) {
            throw new MissingBodyFieldException(
                    6,
                    "Please provide a unique unit of measure",
                    "Unit of measure is not unique"
            );
        }


        else {
            unit.setId(UUID.randomUUID().toString());
            unit.setLastUpdate(LocalDateTime.now());
            return unitRepository.save(unit);
        }
    }

    @Override
    public List<Unit> getAllUnit() {
        return this.unitRepository.findAll();
    }

    @Override
    public Unit getUnitById(String unitId) {

        Optional < Unit > unitDb = this.unitRepository.findById(unitId);

    if (unitDb.isPresent()) {
        return unitDb.get();
    }
    else{
        throw new MissingBodyFieldException(60,Constants.ERROR_UNIT_NOT_FOUND, Constants.ERROR_NOT_FOUND);
    }


    }

    @Override
    public Unit updateUnit(Unit unit) {
        Optional < Unit > unitDb = this.unitRepository.findById(unit.getId());

        if (unitDb.isPresent()) {
            if (!(unit.getUnitOfMeasure().isBlank())  ) {
                Optional<Unit> existingUnit = unitRepository.findByUnitOfMeasure(unit.getUnitOfMeasure());
                if (existingUnit.isPresent() && !existingUnit.get().getId().equals(unit.getId())) {
                    throw new MissingBodyFieldException(
                            6,
                            "Please provide a unique unit of measure",
                            "Unit of measure is not unique"
                    );
                }
                LOGGER.info("in update unit");
                Unit unitUpdate = unitDb.get();
                unitUpdate.setId(unit.getId());
                unitUpdate.setUnitOfMeasure(unit.getUnitOfMeasure());
                unitUpdate.setHref(unit.getHref());
                unitUpdate.setAtBaseType(unit.getAtBaseType());
                unitUpdate.setAtType(unit.getAtType());
                unitUpdate.setLastUpdate(unit.getLastUpdate());
                unitRepository.save(unitUpdate);
                return unitUpdate;
            }
            else {
                throw new MissingBodyFieldException(23,"Missing Body Field UnitOfMeasure", Constants.ERROR_NOT_FOUND);

            }
        }
        else {
            throw new MissingBodyFieldException(60,Constants.ERROR_UNIT_NOT_FOUND, Constants.ERROR_NOT_FOUND);

        }
    }

    @Override
    public void deleteUnit(String id) {
        if(id.contains(",")){
            List<String> ids = Arrays.asList(id.split(","));
            this.unitRepository.deleteAllById(ids);
        }
        else {
            Optional<Unit> unitDb = this.unitRepository.findById(id);
            if (unitDb.isPresent()) {
                this.unitRepository.delete(unitDb.get());
            } else {
                throw new MissingBodyFieldException(60, "Unit Not Found", "Not Found");
            }
        }
    }

}
