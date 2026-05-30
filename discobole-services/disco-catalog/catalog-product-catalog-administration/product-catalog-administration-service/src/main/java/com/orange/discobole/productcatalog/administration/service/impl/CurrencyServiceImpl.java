// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.Currency;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.CurrencyRepository;
import com.orange.discobole.productcatalog.administration.service.CurrencyService;
import com.orange.discobole.productcatalog.administration.util.QueryParamUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;


@Service
public class CurrencyServiceImpl implements CurrencyService {
    private static final Logger LOGGER = Logger.getLogger(CurrencyServiceImpl.class.getName());

    private static final String CURRENCY = "currency";
    private  final CurrencyRepository currencyRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CurrencyServiceImpl(CurrencyRepository currencyRepository,MongoTemplate mongoTemplate) {
        this.currencyRepository = currencyRepository;
        this.mongoTemplate=mongoTemplate;
    }

    @Override
    @Transactional
    public Currency createCurrency(Currency currency){

        validateCurrencyDetails(currency);
        if(currency.getIsDefault()==null)
            currency.setIsDefault(false);
        else unsetPrevDefaultCurrency(currency);
        currency.setId(UUID.randomUUID().toString());
        currency.setLastUpdate(LocalDateTime.now());
        return currencyRepository.save(currency);

    }
    private void unsetPrevDefaultCurrency(Currency currency){
        if (currency.getIsDefault()) {
            Query q = Query.query(Criteria.where("isDefault").is(true));
            Update u = new Update().set("isDefault", false);
            mongoTemplate.updateMulti(q, u, Currency.class);
        }
    }

    @Override
    public List<Currency> getAllCurrency(Map<String, Object> requestParams) {
        Map<String, Object> result =
                QueryParamUtil.fetchEntityMap(
                        requestParams,
                        CURRENCY,          // collection name
                        Currency.class,
                        mongoTemplate
                );

        return (List<Currency>) result.getOrDefault("data", Collections.emptyList());
    }

    @Override
    public Currency getCurrencyById(String currencyId) {
        Optional< Currency > currencyDb = this.currencyRepository.findById(currencyId);

        if (currencyDb.isPresent()) {
            return currencyDb.get();
        }
        else{
            throw new MissingBodyFieldException(60,Constants.ERROR_CURRENCY_NOT_FOUND,Constants.ERROR_NOT_FOUND);
        }
    }


@Override
public Currency updateCurrency(Currency currency) {
    Currency currencyUpdate = currencyRepository.findById(currency.getId())
            .orElseThrow(() -> new MissingBodyFieldException(60, Constants.ERROR_CURRENCY_NOT_FOUND, Constants.ERROR_NOT_FOUND));

    if (currency.getCode() == null || currency.getCode().isBlank()
            || currency.getLabel() == null || currency.getLabel().isBlank()) {
        throw new MissingBodyFieldException(23, "Missing Body Field Label or code", Constants.ERROR_NOT_FOUND);
    }

    validateCurrencyDetails(currency);
    updateCurrencyFields(currencyUpdate, currency);
    currencyRepository.save(currencyUpdate);
    return currencyUpdate;
}

private void validateCurrencyDetails(Currency currency){
    if (currency.getCode() == null || currency.getCode().isBlank()
            || currency.getLabel() == null || currency.getLabel().isBlank()) {
        LOGGER.info("in if Currency label or code is blank");
        throw new MissingBodyFieldException(23, "Please provide mandatory fields code and label", Constants.ERROR_NOT_FOUND);

    }
    else if (currency.getCode().length() != 3 || !currency.getCode().toUpperCase().equals(currency.getCode())|| !currency.getCode().matches(Constants.REGULAR_EXPRESSION)) {
        LOGGER.info("in if Currency code is not uppercase or Please mention 3 letter code for currency compliant with ISO4217");
        throw new MissingBodyFieldException(999, Constants.MESSAGE_FOR_3LETTER_CODE, Constants.ERROR_BAD_REQUEST);
    }
    else {
        // Check if a currency with the same label or code already exists
        Optional<Currency> existingCurrency = currencyRepository.findByLabelOrCode(currency.getLabel(),currency.getCode());
        //if different error messages required, inspect existingCurrency
        if (existingCurrency.isPresent() && !existingCurrency.get().getId().equals(currency.getId())) {
            throw new MissingBodyFieldException(004, Constants.ERROR_LABEL_OR_CODE_ARE_NOT_UNIQUE, Constants.ERROR_FOR_LABEL_CODE);
        }
    }
    try {
        //To check if currency exist as standard currency
        java.util.Currency.getInstance(currency.getCode());
    } catch (IllegalArgumentException e) {
        // not ISO currency
        throw new MissingBodyFieldException(005,Constants.DISCO_ADMIN_INVALID_CURRENCY,Constants.MESSAGE_FOR_3LETTER_CODE);
    }
}

    private void updateCurrencyFields(Currency currencyUpdate, Currency currency) {
        currencyUpdate.setId(currency.getId());
        currencyUpdate.setCode(currency.getCode());
        currencyUpdate.setHref(currency.getHref());
        currencyUpdate.setLabel(currency.getLabel());
        currencyUpdate.setAtBaseType(currency.getAtBaseType());
        if(currency.getIsDefault()!=null && !currency.getIsDefault().equals(currencyUpdate.getIsDefault())) {
            currencyUpdate.setIsDefault(currency.getIsDefault());
            unsetPrevDefaultCurrency(currencyUpdate);

        }
        currencyUpdate.setLastUpdate(LocalDateTime.now());
    }


    @Override
    public void deleteCurrency(String currencyId) {
        if(currencyId.contains(",")){
            List<String> ids = Arrays.asList(currencyId.split(","));
            this.currencyRepository.deleteAllById(ids);
        }
        else {
            Optional<Currency> currencyDb = this.currencyRepository.findById(currencyId);

            if (currencyDb.isPresent()) {
                this.currencyRepository.delete(currencyDb.get());
            } else {
                throw new MissingBodyFieldException(60, "Currency Not Found", "Not Found");


            }
        }
    }
}
