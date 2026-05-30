// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect} from 'react';
import "./Modal.css"
import useTranslations from "../../utlis/i18n/useTranslations";

const Modal = ({
                   show,
                   title,
                   body,
                   onClose,
                   onSave,
                   hideFooter,
                   className,
                   saveDisabled = false
               }) => {
    const {t} = useTranslations();

    useEffect(() => {
        const backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';

        if (show) {
            document.body.appendChild(backdrop);
        } else if (!show && backdrop) {
            document.body.removeChild(backdrop);
        }
        return () => {
            if (document.body.contains(backdrop)) {
                document.body.removeChild(backdrop);
            }
        };
    }, [show]);

    if (!show) return null;
    const modalClass = `modal show ${className || ''}`.trim();

    return (
        <div className={modalClass} id="exampleModalLive" tabIndex="-1"
             aria-labelledby="exampleModalLiveLabel" style={{display: "block"}} aria-modal="true" role="dialog">
            <div className="modal-dialog modal-lg modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h1 className="modal-title h5" id="exampleModalLiveLabel">{title}</h1>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" data-bs-toggle="tooltip"
                                onClick={onClose} data-bs-placement="bottom" data-bs-title="Close">
                            <span className="visually-hidden">{t('actions.close')}</span>
                        </button>
                    </div>
                    <div className="modal-body">
                        {body}
                    </div>
                    {!hideFooter && (
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary"
                                    onClick={onClose}>{t('actions.close')}</button>
                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={onSave}
                                disabled={saveDisabled}
                            >
                                {t('actions.saveChanges')}
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Modal;