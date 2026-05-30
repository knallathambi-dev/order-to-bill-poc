// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import vitest from "@vitest/eslint-plugin";
import {defineConfig, globalIgnores} from "eslint/config";

export default defineConfig([
    globalIgnores(["dist"]),
    {
        files: ["**/*.{js,jsx}"],
        extends: [
            js.configs.recommended,
            reactHooks.configs["recommended-latest"],
            reactRefresh.configs.vite,
        ],
        languageOptions: {
            ecmaVersion: "latest",
            globals: {
                ...globals.browser,
                ...globals.node,
                ...vitest.environments.env.globals,
            },
            parserOptions: {
                ecmaFeatures: {jsx: true},
                sourceType: "module",
            },
        },
        plugins: {
            vitest,
        },
        rules: {
            "no-unused-vars": ["error", {varsIgnorePattern: "^[A-Z_]"}],
        },
    },
]);