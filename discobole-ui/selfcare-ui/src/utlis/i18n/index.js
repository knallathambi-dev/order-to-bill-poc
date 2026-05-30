// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

// Language configuration - centralized and maintainable
export const languageConfig = {
    supported: ['en', 'fr', 'es', 'de', 'pt', 'it', 'ro'],
    default: 'en',
    names: {
        en: 'English',
        fr: 'Français',
        es: 'Español',
        de: 'Deutsch',
        pt: 'Português',
        it: 'Italiano',
        ro: 'Română'
    },
    codes: {
        en: 'EN',
        fr: 'FR',
        es: 'ES',
        de: 'DE',
        pt: 'PT',
        it: 'IT',
        ro: 'RO'
    }
};

// Helper function to safely load translation files
const loadTranslations = (lang, namespace) => {
    try {
        return require(`./locales/${lang}/${namespace}.json`);
    } catch (error) {
        return {};
    }
};

// Build resources dynamically
const buildResources = () => {
    const resources = {};

    languageConfig.supported.forEach(lang => {
        resources[lang] = {
            translation: loadTranslations(lang, 'translation'),
            notification: loadTranslations(lang, 'notification')
        };
    });

    return resources;
};

// Initialize resources
const resources = buildResources();

// i18n configuration options
const i18nConfig = {
    resources,
    fallbackLng: languageConfig.default,
    supportedLngs: languageConfig.supported,

    // Namespaces
    ns: ['translation', 'notification'],
    defaultNS: 'translation',

    // Disable debug logs in production
    debug: false,

    // Language detection configuration
    detection: {
        order: ['localStorage', 'navigator', 'htmlTag'],
        caches: ['localStorage'],
        lookupLocalStorage: 'i18nextLng',
        checkWhitelist: true
    },

    // React interpolation
    interpolation: {
        escapeValue: false, // React already escapes values
    },

    // React-specific configuration
    react: {
        useSuspense: false,
        bindI18n: 'languageChanged loaded',
        bindI18nStore: 'added removed',
    },

    // Performance optimizations
    load: 'languageOnly', // Load only 'en' instead of 'en-US'
    cleanCode: true, // Clean language codes

    // Disable missing key features (no logs)
    saveMissing: false,

    // Fallback behavior
    returnEmptyString: false, // Return key instead of empty string
    returnNull: false, // Don't return null
};

// Initialize i18n
const initializeI18n = async () => {
    try {
        await i18n
            .use(LanguageDetector)
            .use(initReactI18next)
            .init(i18nConfig);
    } catch (error) {
        // Minimal fallback initialization
        await i18n.init({
            lng: languageConfig.default,
            fallbackLng: languageConfig.default,
            resources: {
                [languageConfig.default]: {
                    translation: resources[languageConfig.default]?.translation || {}
                }
            },
            interpolation: {
                escapeValue: false,
            },
            react: {
                useSuspense: false,
            },
            debug: false
        });
    }
};

// Initialize
initializeI18n();

export default i18n;