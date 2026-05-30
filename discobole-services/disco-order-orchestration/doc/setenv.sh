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

#set -e

function log_info() {
  echo -e "[\\e[1;94mINFO\\e[0m] $*"
}

function log_warn() {
  echo -e "[\\e[1;93mWARN\\e[0m] $*"
}

function log_error() {
  echo -e "[\\e[1;91mERROR\\e[0m] $*"
}

function fail() {
  log_error "$@"
  exit 1
}

function assert_defined() {
  if [[ -z "$1" ]]
  then
    fail "$2"
  fi
}

function parse_params() {

  # parse arguments
  while [[ $# -gt 0 ]]
  do
    key="$1"
    case $key in
        -h|--help)
        log_info "Usage: $0 "
        log_info "       [--api <GitLab API url>]"
        log_info "       [--token <GitLab API token>]"
        log_info "       [--doc-out <README output dir>]"
        log_info "       [--branch <GitLab branch> (default is develop)]"
        log_info "       [--tag <GitLab tag>]"
        log_info "       [--static-enabled <true | false> avoid the fetching of files from other Git repositories (default: true)"      
        log_info "       [--environment-name <review, integration, staging or production>] (default is integration)"
        log_info "       [--groups <JSON encoded GitLab groups to crawl> (defaults is ODA & portal components)]"
        log_info "       [--test test with ODACAT component]"
        log_info "       [--debug verbose mode for debugging]"
        echo
        log_info "Groups to crawl shall be formatted as follows:"
        log_info "[
          {
            \"path\": \"disco\",
            \"visibility\": \"public\"
          },
          {
            \"path\": \"some-path/to/another-group\",
            \"visibility\": \"internal\",
            \"exclude\": [\"project-2\", \"project-13\"]
          },
          {
            \"path\": \"some-path/to/another-group\",
            \"visibility\": \"public\"
          }
        ]"
        exit 0
        ;;
        --api)
        API_URL="$2"
        shift # past argument
        shift # past value
        ;;
        --token)
        GITLAB_TOKEN="$2"
        shift # past argument
        shift # past value
        ;;
        --project)
        CI_PROJECT_ROOT_NAMESPACE="$2"
        shift # past argument
        shift # past value
        ;;
        --branch)
        CI_COMMIT_REF_NAME="$2"
        shift # past argument
        shift # past value
        ;;
        --tag)
        export CI_COMMIT_TAG="$2"
        shift # past argument
        shift # past value
        ;;
        --static*)
        STATIC_ENABLED="$2"
        shift # past argument
        shift # past value
        ;;                       
        --groups)
        GITLAB_GROUPS="$2"
        shift # past argument
        shift # past value
        ;;       
       --doc-out)
        DOC_OUT="$2"
        shift # past argument
        shift # past value
        ;; 
        --test)
        TEST=$2
        shift # past argument
        shift # past value
        ;;                   
        --debug)
        TRACE="set -x"
        shift
        ;;
        *) # unknown option
        POSITIONAL+=("$1") # save it in an array for later
        shift # past argument
        ;;
    esac
  done
  export TRACE API_URL
  [[ -z "$TRACE" ]] && export CURL_SILENT="-sS"
}

# Setup GITLAB CI_* variables
function setup_ci() {
  export API_URL=${BASE_API_URL:-$CI_API_V4_URL} # API url default to local GitLab instance
  assert_defined "$API_URL" "Missing required env API_URL"  
  CI_COMMIT_REF_NAME=${CI_COMMIT_REF_NAME:-develop} 
  CI_PROJECT_ROOT_NAMESPACE=${CI_PROJECT_ROOT_NAMESPACE:-disco}
  CI_JOB_TOKEN=${CI_JOB_TOKEN:-$GITLAB_TOKEN}
  assert_defined "${CI_JOB_TOKEN:-$GITLAB_TOKEN}" 'Missing required GitLab access token'
  [[ "$GITLAB_TOKEN" ]] && export AUTH_HEADER="PRIVATE-TOKEN: $GITLAB_TOKEN"
}

# Set default values
function setup_mkdocs() {
  STATIC_ENABLED=${STATIC_ENABLED:-true}
  DOC_OUT=${DOC_OUT:-./docs}
  if [ -n "$TEST" ]; then  
    GITLAB_GROUPS='
    [
      {   
        "path": "disco/disco-oda-components/disco-order-orchestration",
        "visibility": "internal",
        "exclude": [ 
          "orchestration-delivery-performance",
          "orchestration-delivery-tests",
          "orchestration-delivery-helm", 
          "orchestration-delivery-commons", 
          "orchestration-delivery-bdd-tests", 
          "gitlab-profile", 
          "orchestration-delivery-architecture", 
          "doc"
        ],
        "type": "oda-component",
        "name": "Customer Order Orchestration Management and Delivery",
        "doc": "https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc/"
      } 
    ]'
  fi
  GITLAB_GROUPS=${GITLAB_GROUPS:-'
  [
      {   
        "path": "disco/disco-oda-components/disco-order-orchestration",
        "visibility": "internal",
        "exclude": [ 
          "orchestration-delivery-performance", 
          "orchestration-delivery-tests", 
          "orchestration-delivery-helm", 
          "orchestration-delivery-commons", 
          "orchestration-delivery-bdd-tests", 
          "gitlab-profile", 
          "orchestration-delivery-architecture",
          "orchestration-delivery-component",
          "doc"
        ],
        "type": "oda-component",
        "name": "Customer Order Orchestration Management and Delivery",
        "acronym": "COOD",
        "doc": "https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc/"
      }  
  ]
  '}
}


# 
# Main 
# 

parse_params "$@"
setup_ci
setup_mkdocs