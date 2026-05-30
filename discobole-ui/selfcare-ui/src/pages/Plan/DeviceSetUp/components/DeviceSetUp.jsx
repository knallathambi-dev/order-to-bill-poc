// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useDispatch, useSelector} from "react-redux";
import React, {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import Carousel from "react-multi-carousel";
import {responsiveCarouselDevice} from "../../../../utlis/helpers";
import {Breadcrumb} from "../../../../components";
import InstallmentPaymentTabs from "../../shared/components/InstallmentPaymentTabs";
import {useInstallmentConfiguration} from "../../shared/hooks/useInstallmentConfiguration";
import {calculatePricingForConfigurationItems, getDiscounts} from "../../shared/services/utils/utils";
import {
    getDefaultProductConfiguration,
    performActionAndGetCharacteristics
} from "../../shared/services/productConfigurationService";
import createProductConfigItemRequest from "../../shared/services/createProductConfigItemRequest";
import apiClient from "../../../../services/api/apiClient";
import {extractDeviceConfiguration} from "../services/productConfigurationService";
import {getProductType} from "../services/utils";
import {
    createFullPriceConfiguration,
    extractInstallmentCharges,
    extractInstallmentPeriodCharacteristic,
    extractPartnerCharacteristic,
    filterDisplayableCharacteristics,
    getAllAvailablePartners,
    groupInstallmentChargesByPartner,
} from "../../shared/services/priceService";
import {A55caseImages, colorVariations, deviceImages} from "../utils/deviceConstants";
import {setShippingConfigItems} from "../../../../store/actions/shippingActions";
import {setItemsRequiringShipping} from "../../../../store/actions/itemsRequiringShipping";
import {useTitlePage} from "../../../../hooks";
import {setPricing} from "../../../../store/actions/pricingActions";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {setOrderType} from "../../../../store/actions/orderActions";
import {toast} from "react-toastify";
import {UPFRONT_PAYMENT_VALUE} from "../../../../utlis/constants";
import {ConfigurationProvider} from "../../shared/context/ConfigurationContext";
import {setConfigurationId} from "../../../../store/actions/configurationActions";

const productConfiguratorUrl = process.env.REACT_APP_PRODUCT_CONFIGURATOR_URL;

const ProceedButton = ({onClick, t}) => (
    <button type="button" className="btn btn-primary" onClick={onClick}>
        {t("actions.proceedToOrder")}
    </button>
);

const resolveSelectedColor = (configurableCharacteristics) => {
    if (!colorVariations?.length || !configurableCharacteristics?.length) return null;

    const characteristicsByName = configurableCharacteristics.reduce((map, characteristic) => {
        map[characteristic.name.toLowerCase()] = characteristic;
        return map;
    }, {});

    for (const colorName of colorVariations) {
        const characteristic = characteristicsByName[colorName.toLowerCase()];
        if (!characteristic) continue;

        const selectedValue = characteristic.configurationCharacteristicValues.find(
            val => val.isSelected
        );

        if (selectedValue?.characteristic?.value) {
            return selectedValue.characteristic.value.toLowerCase();
        }
    }

    return null;
};

const resolveProductImage = (productType, selectedColor) => {
    if (!productType) return null;
    if (selectedColor && deviceImages[productType]?.[selectedColor]) {
        return deviceImages[productType][selectedColor];
    }
    return deviceImages[productType];
};

const deduplicateCharges = (allInstallmentConfigurations) => {
    const allCharges = Object.values(allInstallmentConfigurations).flat();
    const seen = new Set();

    return allCharges.filter(charge => {
        const offeringPrice = charge?.productOfferingPrice;
        const key = `${offeringPrice?.partner}-${offeringPrice?.applicationDuration?.amount}`;
        if (seen.has(key)) return false;
        seen.add(key);
        return true;
    });
};

const resolvePricing = (installmentPeriodCharacteristic, fullPricePricing, installmentPricing) => {
    const characteristicValues = installmentPeriodCharacteristic?.configurationCharacteristicValues;
    if (!characteristicValues) {
        return fullPricePricing || installmentPricing;
    }

    const selectedPeriod = characteristicValues.find(val => val.isSelected === true);

    if (selectedPeriod?.characteristic?.value === UPFRONT_PAYMENT_VALUE) {
        return fullPricePricing || installmentPricing;
    }

    if (selectedPeriod?.characteristic?.value) {
        return installmentPricing || fullPricePricing;
    }

    return fullPricePricing || installmentPricing;
};

const buildPricingForItem = (configurationItem, priceOverrides) => {
    if (!configurationItem) return null;

    const modifiedItem = {
        ...configurationItem,
        productConfiguration: {
            ...configurationItem.productConfiguration,
            configurationPrice: priceOverrides,
        },
    };

    return calculatePricingForConfigurationItems([modifiedItem]);
};

const fetchConfigurationForPeriod = async (configurationId, itemId, partnerChar, periodChar, partnerName, periodValue, relatedParty) => {
    const requestPayload = createProductConfigItemRequest(
        configurationId,
        itemId,
        [
            {id: partnerChar.id, value: partnerName, type: partnerChar.type},
            {id: periodChar.id, value: periodValue, type: periodChar.type},
        ],
        relatedParty,
        true,
        "add"
    );

    const response = await apiClient.post(productConfiguratorUrl, requestPayload);
    const {status, data} = response || {};

    if (status !== 200 || data?.state?.toLowerCase() !== "done") {
        return null;
    }

    const items = data?.computedProductConfigurationItem ?? [];
    const matchedItem = items.find(item => item?.id === itemId);

    if (!matchedItem) return null;

    return extractInstallmentCharges(
        matchedItem?.productConfiguration?.configurationPrice || []
    );
};

const sortPeriodsWithSelectedLast = (periods, selectedValue) => {
    const sorted = [...periods];
    if (selectedValue && sorted.includes(selectedValue)) {
        sorted.sort((a, b) => {
            if (a === selectedValue) return 1;
            if (b === selectedValue) return -1;
            return 0;
        });
    }
    return sorted;
};

const DeviceSetUp = () => {
    const {selectedOfferName, selectedOfferId, description} = useSelector(state => state.offer);

    useTitlePage("deviceSetUp", ["devicesAndElectronics", selectedOfferName, "deviceSetUp"]);

    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {relatedParty} = useSelector(state => state.auth);
    const {t, tNotification} = useTranslations();

    const [configuration, setConfiguration] = useState(null);
    const [allInstallmentConfigurations, setAllInstallmentConfigurations] = useState({});

    const relatedPartyRef = useRef(relatedParty);
    const tNotificationRef = useRef(tNotification);

    useEffect(() => {
        relatedPartyRef.current = relatedParty;
    }, [relatedParty]);

    useEffect(() => {
        tNotificationRef.current = tNotification;
    }, [tNotification]);

    useEffect(() => {
        if (!selectedOfferId) return;

        const loadConfiguration = async () => {
            const data = await getDefaultProductConfiguration(
                selectedOfferId,
                relatedPartyRef.current,
                "add",
                dispatch,
                tNotificationRef.current
            );
            if (data) {
                setConfiguration(data);
                dispatch(setConfigurationId(data.id));
            }
        };

        loadConfiguration();
    }, [selectedOfferId, dispatch]);

    const handleConfigurationChange = useCallback((updatedConfiguration) => {
        setConfiguration(updatedConfiguration);
    }, []);

    const {
        configurationItem,
        configurationPrice,
        id: deviceConfigItemId,
        productOffering,
        configurableCharacteristics,
        shippingConfigurationItem,
    } = useMemo(() => {
        if (!configuration) return {};
        return extractDeviceConfiguration(configuration);
    }, [configuration]);

    const installmentPeriodCharacteristic = useMemo(
        () => extractInstallmentPeriodCharacteristic(configurableCharacteristics),
        [configurableCharacteristics]
    );

    const hasInstallments = !!installmentPeriodCharacteristic;

    const installmentCharges = useMemo(
        () => extractInstallmentCharges(configurationPrice),
        [configurationPrice]
    );

    const fullPriceConfigurationPrice = useMemo(
        () => createFullPriceConfiguration(configurationPrice, installmentCharges),
        [configurationPrice, installmentCharges]
    );

    const fullPricePricing = useMemo(
        () => buildPricingForItem(configurationItem, fullPriceConfigurationPrice),
        [configurationItem, fullPriceConfigurationPrice]
    );

    const installmentPricing = useMemo(() => {
        if (!configurationItem) return null;
        return calculatePricingForConfigurationItems([configurationItem]);
    }, [configurationItem]);

    const pricing = useMemo(
        () => resolvePricing(installmentPeriodCharacteristic, fullPricePricing, installmentPricing),
        [installmentPeriodCharacteristic, fullPricePricing, installmentPricing]
    );

    const discounts = useMemo(() => {
        if (!fullPriceConfigurationPrice) return [];
        return getDiscounts(fullPriceConfigurationPrice);
    }, [fullPriceConfigurationPrice]);

    const periodOptions = useMemo(() => {
        const characteristicValues = installmentPeriodCharacteristic?.configurationCharacteristicValues;
        if (!characteristicValues) return [];
        return characteristicValues
            .map(val => val.characteristic.value)
            .filter(val => val && val !== UPFRONT_PAYMENT_VALUE);
    }, [installmentPeriodCharacteristic]);

    const partnerCharacteristic = useMemo(
        () => extractPartnerCharacteristic(configurableCharacteristics),
        [configurableCharacteristics]
    );

    const availablePartners = useMemo(
        () => getAllAvailablePartners(partnerCharacteristic),
        [partnerCharacteristic]
    );

    const periodCharId = installmentPeriodCharacteristic?.id;
    const periodCharType = installmentPeriodCharacteristic?.["@type"];
    const partnerCharId = partnerCharacteristic?.id;
    const partnerCharType = partnerCharacteristic?.["@type"];

    const fetchAllPeriodsForPartner = useCallback(
        async (partnerName) => {
            const hasMissingDependencies =
                !periodCharId || !periodCharType ||
                !partnerCharId || !partnerCharType ||
                !configuration?.id || !deviceConfigItemId ||
                periodOptions.length === 0;

            if (hasMissingDependencies) return;

            const selectedPeriodValue =
                installmentPeriodCharacteristic?.configurationCharacteristicValues?.find(
                    val => val.isSelected
                )?.characteristic?.value;

            setAllInstallmentConfigurations({});

            const orderedPeriods = sortPeriodsWithSelectedLast(periodOptions, selectedPeriodValue);
            const partnerChar = {id: partnerCharId, type: partnerCharType};
            const periodChar = {id: periodCharId, type: periodCharType};

            const configurations = {};

            for (const periodValue of orderedPeriods) {
                const charges = await fetchConfigurationForPeriod(
                    configuration.id,
                    deviceConfigItemId,
                    partnerChar,
                    periodChar,
                    partnerName,
                    periodValue,
                    relatedParty
                );

                if (charges) {
                    configurations[`${partnerName}-${periodValue}`] = charges;
                }
            }

            setAllInstallmentConfigurations(prev => ({...prev, ...configurations}));
        },
        [
            periodCharId, periodCharType,
            partnerCharId, partnerCharType,
            configuration?.id, deviceConfigItemId,
            periodOptions, relatedParty,
            installmentPeriodCharacteristic,
        ]
    );

    const displayableCharacteristics = useMemo(
        () => filterDisplayableCharacteristics(configurableCharacteristics),
        [configurableCharacteristics]
    );

    const chargesByPartner = useMemo(
        () => groupInstallmentChargesByPartner(installmentCharges),
        [installmentCharges]
    );

    const comparisonCharges = useMemo(
        () => deduplicateCharges(allInstallmentConfigurations),
        [allInstallmentConfigurations]
    );

    const selectedColor = useMemo(
        () => resolveSelectedColor(configurableCharacteristics),
        [configurableCharacteristics]
    );

    const handleCharacteristicChange = useCallback(
        async (characteristic, newSelectedValue) => {
            const characteristicId = characteristic?.id;
            const characteristicType = characteristic?.["@type"];

            if (!configuration?.id || !deviceConfigItemId || !characteristicId) {
                toast.error(tNotification("plan.updateCharacteristicFailed"));
                return;
            }

            const result = await performActionAndGetCharacteristics({
                configurationId: configuration.id,
                configurationItemId: deviceConfigItemId,
                characteristic: {
                    id: characteristicId,
                    value: newSelectedValue,
                    type: characteristicType,
                },
                isSelected: true,
                relatedParty,
                actionType: "add",
                dispatch,
                tNotification,
                onConfigurationChange: handleConfigurationChange,
            });

            if (!result) {
                toast.error(tNotification("plan.updateCharacteristicFailed"));
            }
        },
        [configuration?.id, deviceConfigItemId, relatedParty, dispatch, tNotification, handleConfigurationChange]
    );

    const proceedToOrder = () => {
        dispatch(setOrderType("AcquisitionAccessory"));
        dispatch(setItemsRequiringShipping([configurationItem]));
        dispatch(setShippingConfigItems([shippingConfigurationItem]));
        dispatch(setPricing(pricing));
        navigate("/shipping");
    };

    const productType = useMemo(() => {
        if (!productOffering) return null;
        return getProductType(productOffering);
    }, [productOffering]);

    const imageSrc = useMemo(
        () => resolveProductImage(productType, selectedColor),
        [productType, selectedColor]
    );

    const renderedImage = useMemo(() => {
        if (productType === "A55case") {
            const imagesArray = Object.values(A55caseImages);
            return (
                <Carousel
                    responsive={responsiveCarouselDevice}
                    infinite={true}
                    showDots={true}
                    dotListClass="custom-dot-list-style"
                >
                    {imagesArray.map((img, index) => (
                        <div key={index}>
                            <img
                                className="d-block w-100"
                                src={img}
                                alt={`Case ${index + 1}`}
                            />
                        </div>
                    ))}
                </Carousel>
            );
        }

        if (imageSrc) {
            return <img src={imageSrc} alt="product" className="w-100"/>;
        }

        return null;
    }, [productType, imageSrc]);

    const {
        activeTab,
        selectedPartner,
        selectedDuration,
        isCompareModalOpen,
        setCompareModalOpen,
        selectPartner,
        changeDuration,
        selectFullPriceTab,
        selectInstallmentTab,
        openCompareModal,
    } = useInstallmentConfiguration({
        installmentPeriodCharacteristic,
        chargesByPartner,
        availablePartners,
        hasInstallments,
        partnerCharacteristic,
        configurationId: configuration?.id,
        configurationItemId: deviceConfigItemId,
        relatedParty,
        dispatch,
        tNotification,
        onConfigurationChange: handleConfigurationChange,
        fetchAllPeriodsForPartner,
        periodOptions,
    });

    if (!configuration) return null;

    return (
        <ConfigurationProvider
            configuration={configuration}
            onConfigurationChange={handleConfigurationChange}
        >
            <main>
                <div className="container">
                    <Breadcrumb/>
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="banner-title mb-0">{t("device.title")}</h1>
                        </div>
                    </div>
                </div>
                <div className="container py-4">
                    <div className="row">
                        <div className="col-md-6">
                            <div className="card">
                                <div className="card-body p-5">{renderedImage}</div>
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="row">
                                <div className="col-md-12">
                                    <span className="badge rounded-pill text-bg-secondary type-badge mb-2">
                                        {t("pages.devicesAndElectronics")}
                                    </span>
                                    <h1>{selectedOfferName}</h1>
                                    <p className="p-3 bg-body-secondary">{description}</p>
                                    {displayableCharacteristics && displayableCharacteristics.length > 0 &&
                                        displayableCharacteristics
                                            .filter(char => char?.name)
                                            .map((characteristic, charIndex) => {
                                                const characteristicName = characteristic.name.toLowerCase();
                                                const isColorCharacteristic = colorVariations.includes(characteristicName);

                                                return (
                                                    <div className="row" key={`${characteristic.name}-${charIndex}`}>
                                                        <hr/>
                                                        <div className="col-md-12">
                                                            <div className={`${characteristicName}-wrapper mb-4`}>
                                                                <h6>{characteristic.name}</h6>
                                                                {isColorCharacteristic ? (
                                                                    <div
                                                                        className="btn-group"
                                                                        role="group"
                                                                        aria-label={`Toggle ${characteristic.name}`}
                                                                    >
                                                                        {characteristic.configurationCharacteristicValues?.map(
                                                                            (option, index) => {
                                                                                if (!option?.characteristic?.value) {
                                                                                    return null;
                                                                                }

                                                                                return (
                                                                                    <React.Fragment
                                                                                        key={`${option.characteristic.value}-${index}`}
                                                                                    >
                                                                                        <input
                                                                                            type="radio"
                                                                                            className="btn-check"
                                                                                            name={`btnradio-${charIndex}`}
                                                                                            id={`btnradio-${charIndex}-${index}`}
                                                                                            autoComplete="off"
                                                                                            checked={option.isSelected}
                                                                                            onChange={() =>
                                                                                                handleCharacteristicChange(
                                                                                                    characteristic,
                                                                                                    option.characteristic.value
                                                                                                )
                                                                                            }
                                                                                        />
                                                                                        <label
                                                                                            className={`btn btn-toggle color ${option.characteristic.value.toLowerCase()}`}
                                                                                            htmlFor={`btnradio-${charIndex}-${index}`}
                                                                                            style={{
                                                                                                backgroundColor:
                                                                                                    option.characteristic.value.toLowerCase(),
                                                                                            }}
                                                                                        ></label>
                                                                                    </React.Fragment>
                                                                                );
                                                                            }
                                                                        )}
                                                                    </div>
                                                                ) : (
                                                                    <div
                                                                        className="btn-group"
                                                                        role="group"
                                                                        aria-label={`Toggle ${characteristic.name}`}
                                                                    >
                                                                        <div
                                                                            className="btn-group d-flex flex-wrap gap-2">
                                                                            {characteristic.configurationCharacteristicValues?.map(
                                                                                (option, index) => {
                                                                                    if (!option?.characteristic?.value) {
                                                                                        return null;
                                                                                    }

                                                                                    return (
                                                                                        <div
                                                                                            className="d-flex align-items-center"
                                                                                            key={`${charIndex}-${index}`}
                                                                                        >
                                                                                            <input
                                                                                                type="radio"
                                                                                                className="btn-check"
                                                                                                name={`btnradio-${charIndex}`}
                                                                                                id={`btnradio-${charIndex}-${index}`}
                                                                                                autoComplete="off"
                                                                                                checked={option.isSelected}
                                                                                                onChange={() =>
                                                                                                    handleCharacteristicChange(
                                                                                                        characteristic,
                                                                                                        option.characteristic.value
                                                                                                    )
                                                                                                }
                                                                                            />
                                                                                            <label
                                                                                                className="btn btn-toggle"
                                                                                                htmlFor={`btnradio-${charIndex}-${index}`}
                                                                                            >
                                                                                                {option.characteristic.value}{" "}
                                                                                                {option.characteristic.unitOfMeasure || ""}
                                                                                            </label>
                                                                                        </div>
                                                                                    );
                                                                                }
                                                                            )}
                                                                        </div>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>
                                                );
                                            })}
                                    <hr/>
                                    <InstallmentPaymentTabs
                                        hasInstallments={hasInstallments}
                                        chargesByPartner={chargesByPartner}
                                        comparisonCharges={comparisonCharges}
                                        periodOptions={periodOptions}
                                        availablePartners={availablePartners}
                                        pricing={pricing}
                                        discounts={discounts}
                                        t={t}
                                        activeTab={activeTab}
                                        selectedPartner={selectedPartner}
                                        selectedDuration={selectedDuration}
                                        isCompareModalOpen={isCompareModalOpen}
                                        setCompareModalOpen={setCompareModalOpen}
                                        onPartnerSelect={selectPartner}
                                        onDurationChange={changeDuration}
                                        onFullPriceTabClick={selectFullPriceTab}
                                        onInstallmentTabClick={selectInstallmentTab}
                                        onOpenCompareModal={openCompareModal}
                                        renderProceedButton={() => <ProceedButton onClick={proceedToOrder} t={t}/>}
                                        priceColumnClass="col-md-6"
                                        containerClassName="payment-options"
                                        accordionClassName="accordion accordion-sm partners-accordion"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </ConfigurationProvider>
    );
};

export default DeviceSetUp;