#!/bin/bash

# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

#
# You can put her any commands to customize the deployment of application.
# it will be execute BEFORE the manifests are applied.

env | grep "^CI_" |sort
env | grep "^DOCKER_" |sort
env | grep "^DDIOD_" |sort
env | grep "^GITLAB_" |sort
env | grep "^KEYCLOAK_" |sort
env | grep "^MAVEN_" |sort
env | grep "^HELM_" |sort
env | grep -E '_pkg|appname|docker|env|helm|hostname|project|stage' |sort