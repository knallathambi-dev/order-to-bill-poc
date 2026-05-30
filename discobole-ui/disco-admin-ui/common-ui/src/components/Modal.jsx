// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useRef} from "react";
import {Modal as BsModal} from "boosted";

const Modal = ({open, onClose, children}) => {
    const modalRef = useRef(null);
    const bsModalRef = useRef(null);

    useEffect(() => {
        if (modalRef.current) {
            bsModalRef.current = new BsModal(modalRef.current, {
                backdrop: true,
                keyboard: true,
            });
        }
        return () => {
            bsModalRef.current?.dispose();
        };
    }, []);

    useEffect(() => {
        if (!bsModalRef.current) return;
        if (open) {
            bsModalRef.current.show();
        } else {
            bsModalRef.current.hide();
        }
    }, [open]);

    useEffect(() => {
        const el = modalRef.current;
        if (!el) return;
        const handleHidden = () => onClose?.();
        el.addEventListener("hidden.bs.modal", handleHidden);
        return () => {
            el.removeEventListener("hidden.bs.modal", handleHidden);
        };
    }, [onClose]);

    return (
        <div ref={modalRef} className="modal fade" tabIndex="-1" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    {children}
                </div>
            </div>
        </div>
    );
};

export default Modal;