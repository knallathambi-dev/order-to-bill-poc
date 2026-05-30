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
oc delete all,pvc,is,route,secret --selector="app=${environment_name}" # Nuke all Openshift objects created by a deployment
