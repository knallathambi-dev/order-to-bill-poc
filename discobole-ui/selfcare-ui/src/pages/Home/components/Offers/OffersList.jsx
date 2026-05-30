// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useState} from "react";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import OfferCard from "./Card/OfferCard";
import offer1 from "../../../../assests/imgs/offer-1.png";
import offer2 from "../../../../assests/imgs/offer-2.png";
import offer3 from "../../../../assests/imgs/offer-3.png";
import offer4 from "../../../../assests/imgs/offer-4.png";
import {responsive} from "../../../../utlis/helpers";
import OfferIncompatibilityModal from "../../../Plan/SetUpPlan/components/Modals/OfferIncompatibilityModal";
import {setOrderType} from "../../../../store/actions/orderActions";
import {Modal} from "../../../../components";
import postProcessFlowService from "../../services/postProcessFlowService";
import {isFiberOrConvergentOffer} from "../../../../utlis/utils";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {UNQUALIFIED_OFFER_MESSAGE} from "../../../../utlis/constants";

const offerImages = [offer1, offer2, offer3, offer4];

function OffersList({offers = []}) {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {relatedParty} = useSelector((state) => state.auth);

    const [isUnqualifiedModalOpen, setIsUnqualifiedModalOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");

    const {t, tNotification} = useTranslations();

    const filteredOffers =
        searchTerm.length > 2
            ? offers.filter(
                (offer) =>
                    offer.name &&
                    offer.name.toLowerCase().includes(searchTerm.toLowerCase())
            )
            : offers;

    const handleOrderNow = async (offerId, offerName) => {
        try {
            const processResponse = await postProcessFlowService(
                offerId,
                offerName,
                "",
                relatedParty,
                dispatch,
                tNotification
            );

            const data = processResponse?.data;
            if (!data) {
                toast.error(tNotification("home.offers.configureFailed"));
                return;
            }

            const nextTasks = data?._links?.nextTaskstoBePerformed;
            const isUnqualified =
                Array.isArray(nextTasks) &&
                nextTasks?.[1]?.title === "OrderCapture.selectOfferOrContract" &&
                data?.description === UNQUALIFIED_OFFER_MESSAGE;

            if (isUnqualified) {
                setIsUnqualifiedModalOpen(true);
                return;
            }

            dispatch(setOrderType("Acquisition"));

            const selectedOffer = offers.find((item) => item.id === offerId);
            if (!selectedOffer) {
                toast.error(tNotification("home.offers.configureFailed"));
                return;
            }

            navigate(
                isFiberOrConvergentOffer(selectedOffer) ? "/eligibility" : "/set-up-plan",
                {state: {offerId}}
            );
        } catch {
            toast.error(tNotification("home.offers.configureFailed"));
        }
    };

    const handleCloseUnqualifiedModal = () => {
        setIsUnqualifiedModalOpen(false);
    };

    return (
        <div className="container py-4">
            <div className="mt-3">
                <div className="row">
                    <div className="col-md-12">
                        <div className="d-flex justify-content-between align-content-center mb-3">
                            <h2 className="mb-0">{t("pages.offers")}</h2>
                            <div className="search-container">
                                <input
                                    type="text"
                                    className="form-control search-input"
                                    placeholder={t("forms.placeholders.searchOffers")}
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                {searchTerm && (
                                    <button
                                        type="button"
                                        className="btn btn-icon dismiss-btn"
                                        onClick={() => setSearchTerm("")}
                                    >
                                        ✕
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {filteredOffers.length === 0 ? (
                    <div className="text-center py-5">
                        <h4>{t("home.noMatchesFound")}</h4>
                    </div>
                ) : (
                    <Carousel responsive={responsive} autoPlay={false} infinite={true}>
                        {filteredOffers.map((offer, index) => (
                            <OfferCard
                                key={offer.id}
                                offer={{
                                    ...offer,
                                    imageUrl: offer.imageUrl || offerImages[index % offerImages.length],
                                }}
                                onOrderNowClick={() => handleOrderNow(offer.id, offer.name)}
                            />
                        ))}
                    </Carousel>
                )}
            </div>

            {isUnqualifiedModalOpen && (
                <Modal
                    show={isUnqualifiedModalOpen}
                    body={<OfferIncompatibilityModal/>}
                    onClose={handleCloseUnqualifiedModal}
                    hideFooter
                    className="offerIncompatible"
                />
            )}
        </div>
    );
}

export default OffersList;