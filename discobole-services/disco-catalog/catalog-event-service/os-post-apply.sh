# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

oc patch dc/${environment_name} --type=json -p="[{\"op\":\"replace\", \"path\":\"/spec/template/spec/serviceAccountName\", \"value\":\"ci-cd\"}]" | grep -q "not patched" || true
