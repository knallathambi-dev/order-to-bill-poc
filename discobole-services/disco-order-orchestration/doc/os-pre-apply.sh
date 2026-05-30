#!/usr/bin/env bash

# SPDX-FileCopyrightText: 2025, 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

oc create secret docker-registry registry-secret --docker-server="${GITLAB_TOKEN_URL}" --docker-username="${GITLAB_TOKEN_USERNAME}" --docker-password="${GITLAB_TOKEN_PASSWORD}" --docker-email="pipeline-deployer-review@orange.com" --dry-run=client -o yaml | oc apply -f -