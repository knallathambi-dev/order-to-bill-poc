#!/usr/bin/env bash

# Copyright (C) 2024 Orange & contributors
# SPDX-FileCopyrightText: 2025, 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

## ----------------------------------------------------------------------------
## PART I - Parse parameterts
## ----------------------------------------------------------------------------

source setenv.sh "$@"

## ----------------------------------------------------------------------------
## PART II - Pre-processing of the documentation (find routes)
## ----------------------------------------------------------------------------

# Will not fetch remote data 
if [ "$STATIC_ENABLED" == true ]; then
  log_info "Skipping \e[33;1mprocess-os.sh\\e[0m (keep MD files generated previously)"
else 
  # Fetch data and pass the result to process-doc.sh
  $([ "$DRY_RUN" ] && echo log_dryrun) process-os.sh "$@"
fi

## ----------------------------------------------------------------------------
## PART III - Build the pages (fetch README.md, ADRs, etc.) and TOC
## ----------------------------------------------------------------------------

# Will not fetch remote data on Git
if [ "$STATIC_ENABLED" == true ]; then
  log_info "Skipping \\e[33;1mprocess-doc.sh\\e[0m (keep MD files generated previously)"
else
  # Generate table of contents and remaining markdown files
  $([ "$DRY_RUN" ] && echo log_dryrun) process-doc.sh "$@"
fi 

exit 0