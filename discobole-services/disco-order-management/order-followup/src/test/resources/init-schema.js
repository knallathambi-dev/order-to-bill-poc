// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

db = db.getSiblingDB('orderFollowUpTest');

db.createUser({
    user: _getEnv('TEST_MONGO_USERNAME'),
    pwd: _getEnv('TEST_MONGO_PASSWORD'),
    roles: [
        {role: 'readWrite', db: 'orderFollowUpTest'}
    ]
});