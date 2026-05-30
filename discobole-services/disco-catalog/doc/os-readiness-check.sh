#!/bin/bash

# SPDX-FileCopyrightText: 2025, 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

# shellcheck disable=SC2154
for attempt in {1..20}
do
    echo "Testing application readiness ($attempt/20)..."
    if wget --no-check-certificate -T 2 --tries 1 "${environment_url}"
    then
        echo "[INFO] healthcheck response: OK"
        exit 0
    fi
    sleep 5
done

echo "[ERROR] max attempts reached: failed"
exit 1
