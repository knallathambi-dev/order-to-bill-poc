// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useTranslation} from 'react-i18next';
import {useDispatch, useSelector} from 'react-redux';
import {toggleLoading} from "../../store/actions/loadingActions";
import {languageConfig} from './index';
import {useEffect} from 'react';
import {setLanguage} from "../../store/actions/authActions";

export const useTranslations = () => {
    const {t, i18n} = useTranslation(['translation', 'notification']);
    const dispatch = useDispatch();
    const currentLang = useSelector((state) => state.auth.lang);

    useEffect(() => {
        if (currentLang && i18n.language !== currentLang) {
            i18n.changeLanguage(currentLang);
        }
    }, [currentLang, i18n]);

    useEffect(() => {
        const handleLanguageChange = (lng) => {
            if (lng !== currentLang) {
                dispatch(setLanguage(lng));
            }
        };

        i18n.on('languageChanged', handleLanguageChange);

        return () => {
            i18n.off('languageChanged', handleLanguageChange);
        };
    }, [currentLang, dispatch, i18n]);

    const changeLanguage = async (newLang) => {
        dispatch(toggleLoading(true));

        const minDelay = new Promise((resolve) => setTimeout(resolve, 500));

        try {
            const langToSet = languageConfig.supported.includes(newLang)
                ? newLang
                : languageConfig.supported[0];

            await Promise.all([i18n.changeLanguage(langToSet), minDelay]);
            dispatch(setLanguage(langToSet));
        } catch (error) {
            const fallback = languageConfig.supported[0];
            await i18n.changeLanguage(fallback);
            dispatch(setLanguage(fallback));
        } finally {
            dispatch(toggleLoading(false));
        }
    };

    const tNotification = (key, options) => t(key, {ns: 'notification', ...options});

    const tSafe = (key, fallback = key) => {
        const translated = t(key);
        return translated === key ? fallback : translated;
    };

    const tPlural = (key, count, options = {}) => {
        return t(key, {count, ...options});
    };

    return {
        t,
        tNotification,
        tSafe,
        tPlural,
        isLoading: !i18n.isInitialized,

        language: {
            current: i18n.language,
            change: changeLanguage,
            supported: languageConfig.supported,
            names: languageConfig.names,
            codes: languageConfig.codes,
            isRTL: i18n.dir() === 'rtl'
        }
    };
};

export default useTranslations;