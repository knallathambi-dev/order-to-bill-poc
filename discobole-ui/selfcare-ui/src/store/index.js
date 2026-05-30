// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {configureStore} from '@reduxjs/toolkit';
import sessionStorage from 'redux-persist/lib/storage/session';
import {persistReducer, persistStore} from 'redux-persist';
import Reducers from './reducers';

const persistConfig = {
    key: 'root',
    storage: sessionStorage
};

const persistedReducer = persistReducer(persistConfig, Reducers);

export const store = configureStore({
    reducer: persistedReducer,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: false,
            immutableCheck: false
        }),
});

export const persistor = persistStore(store);