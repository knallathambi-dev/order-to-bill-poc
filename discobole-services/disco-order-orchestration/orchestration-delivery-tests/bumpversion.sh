#!/usr/bin/env bash

# Software Name: orchestration-delivery-tests
# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

function log_info() {
  >&2 echo -e "[\\e[1;94mINFO\\e[0m] $*"
}

function log_warn() {
  >&2 echo -e "[\\e[1;93mWARN\\e[0m] $*"
}

function log_error() {
  >&2 echo -e "[\\e[1;91mERROR\\e[0m] $*"
}

# check number of arguments
if [[ "$#" -le 2 ]]; then
  log_error "Missing arguments"
  log_error "Usage: $0 <current version> <next version>"
  exit 1
fi

curVer=$1
nextVer=$2
relType=$3

if [[ "$curVer" ]]; then
  log_info "Bump version from \\e[33;1m${curVer}\\e[0m to \\e[33;1m${nextVer}\\e[0m (release type: $relType)..."
else
  log_info "Bump version to \\e[33;1m${nextVer}\\e[0m (release type: $relType): this is the first release (skip)..."
fi
