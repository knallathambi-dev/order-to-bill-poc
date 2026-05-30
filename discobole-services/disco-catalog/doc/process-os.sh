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

## -- Application (API and Portal) Part


# Get route in Openshift based on its application name.
# Route has been created with helm template.
# $1: project_json
# return: $host, $version, creation_timestamp
function process_route_application() {
  project_json="$1"  
  project_name=$(echo "$project_json" | jq -r .path)
  project_id=$(echo "$project_json" | jq -r .id)
  project_branch="${CI_COMMIT_REF_NAME}"
  label_app="app.kubernetes.io/name"

  # Application name might be set in .gitlab-ci.yml (OS|HELM_BASE_APP_NAME might be overridden in .gitlab-ci.yml)
  log_info "Downloading .gitlab-ci.yml of \\e[33;1m$project_name\\e[0m in \\e[33;1m$tmp_dir/.gitlab-ci.yml\\e[0m..."
  http_status=$(curl -w "%{http_code}" -s -R -H "$AUTH_HEADER" --output "$tmp_dir/.gitlab-ci.yml" "$API_URL/projects/$project_id/repository/files/.gitlab-ci.yml/raw?ref=${project_branch}")   
  if [ "$http_status" != 200 ]; then
    #echo curl -R -H "$AUTH_HEADER" "$API_URL/projects/$project_id/repository/files/.gitlab-ci.yml/raw?ref=${project_branch}"
    log_error "404 File Not Found; missing .gitlab-ci.yml  on branch \\e[33;1m$project_branch\\e[0m."
    unset route_json 
    host="unknown-host"
    version="     " #"unknown-version"
    creation_timestamp="" #"unknown"
    return
  fi

  app_name=$(grep -e "^  HELM_BASE_APP_NAME:" -m 1 "$tmp_dir/.gitlab-ci.yml" | awk -F '#' '{print $1}') 
  [[ -z "$app_name" ]] && app_name=$(grep -e "^  OS_BASE_APP_NAME:" "$tmp_dir/.gitlab-ci.yml" | awk -F '#' '{print $1}')
  [[ -n "$app_name" ]] && app_name=${app_name#*: } && app_name=${app_name//\"/}
  app_name=${app_name:-$project_name} # value from .gitlab-ci.yml, or GitLab project name (default value)

  # Search in OpenShift the route from label "app.kubernetes.io/name=$app_name" 
  route_json=$(oc get route -l "$label_app"="$app_name" -o json)

  host=$(echo "$route_json" | jq '.items[0].spec.host')
  if [ "$host" == null ]; then
    log_error "Unable to find route of \\e[33;1m$project_name\\e[0m with label \\e[32m\"app.kubernetes.io/name=$app_name\"\e[0m. Please fix Helm manifest files."
    host="unknown-host"
    version="     " #"unknown-version"
    creation_timestamp="" #"unknown"
  else
    host=${host//\"/}
    version=$(echo "$route_json" | jq '.items[0].metadata.labels."app.kubernetes.io/version"') 
    version=${version//\"/}
    creation_timestamp=$(echo "$route_json" | jq '.items[0].metadata.creationTimestamp')
    creation_timestamp=${creation_timestamp//\"/}
  fi
}

# Determines whether the given project is a module project or not
# If so, find release number and URL of the project 
# $1: project JSON representation
# $2: group name
# $3: group acronym
function process_module() {
  project_json="$1"
  group_name="$2"
  group_acronym="$3"
  project_name=$(echo "$project_json" | jq -r .path)
  project_web_url=$(echo "$project_json" | jq -r .web_url)
  project_branch="${CI_COMMIT_REF_NAME}"

  # Git project MUST have the topic either "frontend" or "openapi" (we mean Swagger 2 or 3).
  # It is a way to recognize the nature of the Git project concerns.
  project_topic=$(echo "$project_json" | jq -r '.topics[] | select(. == "openapi" or . == "frontend")')
  if [[ -z $project_topic ]]; then 
    log_info "... Skipping application's URL of \\e[33;1m$project_name\\e[0m"
    return
  fi

  log_info "... Searching application's URL of \\e[33;1m$project_name\\e[0m"

  process_route_application "$project_json" # helm used to deploy application (except mkdocs projects)
  if [[ -z $route_json ]]; then
    log_error "Missing application's URL of \\e[33;1m$project_name\\e[0m"
  else 
    log_info "Adding application's URL of \\e[33;1m$project_name\\e[0m in \\e[33;1m$tmp_dir/$project_topic.md\\e[0m..."  
  fi

  # Add new entry in $tmp_dir/{openapi|frontend}.md. Used later in process-doc.sh
  APP_NAME=${project_name//-/ }
  APP_ENVIRONMENT_URL="https://$host"
  [ "$project_topic" == "openapi" ] && APP_ENVIRONMENT_URL="$APP_ENVIRONMENT_URL/swagger-ui.html" # swagger-ui.html required for certain services
  APP_VERSION_NUMBER=$version
  APP_VERSION_TYPE=$ENV_VERSION_TYPE
  app_version_type=$(echo "$ENV_VERSION_TYPE" | tr '[:upper:]' '[:lower:]')
  APP_ENVIRONMENT_TYPE=$ENV_TYPE
  APP_CREATION_TIMESTAMP=$creation_timestamp
  APP_GIT_WEB_URL=$(echo "$project_json" | jq -r .web_url)
  APP_GIT_WEB_URL=${APP_GIT_WEB_URL}/-/tree/${CI_COMMIT_REF_NAME}
  export APP_NAME APP_ENVIRONMENT_URL APP_ENVIRONMENT_TYPE  
  export APP_VERSION_NUMBER APP_VERSION_TYPE app_version_type
  export APP_CREATION_TIMESTAMP APP_GIT_WEB_URL

  [[ "$group_acronym" == null ]] && COMPONENT_NAME="$group_name"
  [[ "$group_acronym" != null ]] && COMPONENT_NAME="$group_name ($group_acronym)"
  export COMPONENT_NAME

  MODULE_DIR="deployments"
  templateFile="$DOC_OUT/$MODULE_DIR/template-$project_topic.md"
  [[ ! -f $templateFile ]] && log_error "Unable to find $templateFile. Did you define the right Git topic (openapi or frontend) in $project_name project?"
  awkenvsubst < "$templateFile" >> "$MARKDOWN_FILES_DIR/$project_topic.md"

  # Also, build pipeline urls in build.md
  echo "    - \`gitlab\`: [$APP_NAME]($project_web_url/-/pipelines) [![pipeline status]($project_web_url/badges/$project_branch/pipeline.svg)]($project_branch/commits/$project_branch)" >> "$MARKDOWN_FILES_DIR/$project_topic-pipeline.md"
}

# Iterates over all projects from given group with given visibility and tries to process it as a disco module project
# $1: GitLab group path
# $2: visibility to scan through
# $3: excluded projects
# $4: group name
# $5: group acronym
function process_modules_from_group() {
  group_path="$1"
  group_id=${group_path//\//%2f}
  visibility="$2"
  exclude_projects="$3"
  group_name="$4"
  group_acronym="$5"
  log_info "--- Looking for projects with visibility \\e[33;1m${visibility}\\e[0m from group \\e[32m${group_path}\\e[0m..."

  for project_b64 in $(curl -sSf -H "$AUTH_HEADER" "$API_URL/groups/$group_id/projects?archived=false&visibility=${visibility}&per_page=100" | jq -r '.[] | @base64')
  do
    project_json=$(echo "$project_b64" | base64 -d)
    project_path=$(echo "$project_json" | jq -r '.path')
    if echo "$exclude_projects" | grep -e "^${project_path}\$"
    then
      log_info "Project \\e[33;1m${project_path}\\e[0m matches excludes: skip"
    else
      process_module "$project_json" "$group_name" "$group_acronym"
    fi
  done
}


## DISCO Documentation Part

# Get DISCO documentation URL
# $1: $CI_PROJECT_ROOT_NAMESPACE 
function process_disco_documentation() {
  group_path="$1"
  group_id=${group_path//\//%2f}

  log_info "--- Looking for DISCO documentation with topic \\e[33;1mmkdocs\\e[0m from group \\e[32m${group_path}\\e[0m..."
  for project_b64 in $(curl -sSf -H "$AUTH_HEADER" "$API_URL/groups/$group_id/projects?topic=mkdocs&per_page=100" | jq -r '.[] | @base64')
  do
    project_json=$(echo "$project_b64" | base64 -d) # supposed to be unique
  done
  if [[ -n "$project_json" ]]; then
    if [[ -n "$CI_COMMIT_TAG" ]]; then # git tag exposed as GitLab pages
      project_id=$(echo "$project_json" | jq .id)
      project_pages=$(curl -sSf -H "$AUTH_HEADER" "$API_URL/projects/$project_id/pages")
      if [[ -z $project_pages ]]; then # don't know why /pages returns 403 error
        web_url=$(echo "$project_json" | jq .web_url)
        host=${web_url/disco\//}
        host=${host/https:\/\//$CI_PROJECT_ROOT_NAMESPACE.pages.}
        host=${host//\"/}
        DISCO_DOCUMENTATION_URL="https://$host"
      else 
        DISCO_DOCUMENTATION_URL=$(echo "$project_pages" | jq -r '.url')
      fi      
    else # non-production and production branches deployed on Openshift
      process_disco_documentation "$project_json" # get route created by openshift template
      DISCO_DOCUMENTATION_URL="https://$host"
    fi
  fi
  echo "$DISCO_DOCUMENTATION_URL" > "$MARKDOWN_FILES_DIR/documentation-url.md"
}

## -- Component Part


# Get route create by openshift template
# $1: project_json
# return: $host, $version, creation_timestamp
function process_route_documentation() {
  project_json="$1"  
  project_name=$(echo "$project_json" | jq -r .path)
  project_id=$(echo "$project_json" | jq -r .id)
  project_branch=${CI_COMMIT_REF_NAME}
  label_app="app"

  log_info "... Searching documentation's URL of \\e[33;1m$project_name\\e[0m"

  # Application name might be set in .gitlab-ci.yml (OS|HELM_BASE_APP_NAME might be overridden in .gitlab-ci.yml)
  log_info "Downloading .gitlab-ci.yml of \\e[33;1m$project_name\\e[0m in \\e[33;1m$tmp_dir/.gitlab-ci.yml\\e[0m..."
  http_status=$(curl -w "%{http_code}" -s -R -H "$AUTH_HEADER" --output "$tmp_dir/.gitlab-ci.yml" "$API_URL/projects/$project_id/repository/files/.gitlab-ci.yml/raw?ref=${project_branch}")   
  if [ "$http_status" != 200 ]; then
    #echo curl -R -H "$AUTH_HEADER" "$API_URL/projects/$project_id/repository/files/.gitlab-ci.yml/raw?ref=${project_branch}"
    log_error "404 File Not Found on branch \\e[33;1m$project_branch\\e[0m"
    base_app_name=$project_name # $CI_PROJECT_NAME 
  fi
  base_app_name=$(grep -e "^  OS_BASE_APP_NAME:" "$tmp_dir/.gitlab-ci.yml")
  [[ -n "$base_app_name" ]] && base_app_name=${base_app_name#*: } && base_app_name=${base_app_name//\"/}
  base_app_name=${base_app_name:-$project_name} # value from .gitlab-ci.yml, or GitLab project name (default value)

  if [[ "$ENV_TYPE" =~ review ]] || [[ "$ENV_TYPE" == "integration" ]] || [[ "$ENV_TYPE" == "staging" ]] ; then 
    app_name="$base_app_name-$CI_ENVIRONMENT_SLUG"
  else
    app_name="$base_app_name"
  fi

  # Search in OpenShift the route from label "app.kubernetes.io/name=$app_name" 
  route_json=$(oc get route -l "$label_app"="$app_name" -o json)
  host=$(echo "$route_json" | jq '.items[0].spec.host')

  if [ "$host" == null ]; then
    log_error "Unable to find route of \\e[33;1m$project_name\\e[0m with label \\e[32m\"$label_app=$app_name\"\\e[0m. Are you sure it is deployed? With this label also?"
    # Plan B (-l app=$base_app_name)
    route_json=$(oc get route -l "$label_app"="$base_app_name" -o json)
    host=$(echo "$route_json" | jq '.items[0].spec.host')
    if [ "$host" == null ]; then
      log_error "Unable to find route of \\e[33;1m$project_name\\e[0m with label \\e[32m\"$label_app=$base_app_name\"\\e[0m. Are you sure it is deployed? With this label also?"
    fi
  fi

  if [ "$host" == null ]; then  
    # Plan C (gitlab pages URL)
    web_url=$(echo "$project_json" | jq .web_url)
    host=${web_url/disco\//}
    host=${host/https:\/\//$CI_PROJECT_ROOT_NAMESPACE.pages.}
    host=${host//\"/}
    log_warn "Build GitLab pages URL \\e[33;1m$host\\e[0m"
    version="unknown-version"
    creation_timestamp="unknown"
  else
    # Plans A & B (-l app=$app_name OR -l app=$base_app_name)
    host=${host//\"/}
    creation_timestamp=$(echo "$route_json" | jq '.items[0].metadata.creationTimestamp')
    creation_timestamp=${creation_timestamp//\"/}
  fi
}

# Iterates over all components 
# $1: group path
# $2: group name
# $3: API Designer URL
# $4: project name of the component documentation
function process_component_from_group() {
  group_path="$1"
  group_id=${group_path//\//%2f}
  group_name="$2"
  group_specification="$3"
  #group_website="$4" # TODO - useful later

  project_branch=${CI_COMMIT_REF_NAME}

  # Get documentation version from the $project_branch branch of the website project 
  export COMPONENT_DOCUMENTATION_VERSION="1.0.0" # TODO - call Git to fetch version in Dockerfile
  if [[ -n  "$CI_COMMIT_TAG" ]]; then 
    component_documentation_version_type="ga" && COMPONENT_DOCUMENTATION_VERSION_TYPE=GA
  elif [[ $CI_COMMIT_REF_NAME =~ $PROD_REF ]]; then # production branch
    component_documentation_version_type="main" && COMPONENT_DOCUMENTATION_VERSION_TYPE=MAIN
  else # non-production branch
    component_documentation_version_type="snapshot" && COMPONENT_DOCUMENTATION_VERSION_TYPE=SNAPSHOT
  fi

  # Get documentation URL from GitLab pages or Openshift (depend on the branch/tag used)
  log_info "--- Looking for component's documentation with topic \\e[33;1mmkdocs\\e[0m from group \\e[32m${group_path}\\e[0m..."
  for project_b64 in $(curl -sSf -H "$AUTH_HEADER" "$API_URL/groups/$group_id/projects?topic=mkdocs&per_page=100" | jq -r '.[] | @base64')
  do
    project_json=$(echo "$project_b64" | base64 -d) # supposed to be unique
  done
  if [[ -n "$project_json" ]]; then
    if [[ -n "$CI_COMMIT_TAG" ]]; then # git tag exposed as GitLab pages
      project_id=$(echo "$project_json" | jq .id)
      project_pages=$(curl -sSf -H "$AUTH_HEADER" "$API_URL/projects/$project_id/pages")
      if [[ -z $project_pages ]]; then # don't know why /pages returns 403 error
        web_url=$(echo "$project_json" | jq .web_url)
        host=${web_url/disco\//}
        host=${host/https:\/\//$CI_PROJECT_ROOT_NAMESPACE.pages.}
        host=${host//\"/}
        COMPONENT_DOCUMENTATION_URL="https://$host"
      else 
        COMPONENT_DOCUMENTATION_URL=$(echo "$project_pages" | jq -r '.url')
      fi
    else # non-production and production branches deployed on Openshift
      process_route_documentation "$project_json" # get route created by openshift template
      COMPONENT_DOCUMENTATION_URL="https://$host"
    fi
  fi

  COMPONENT_SPECIFICATION_URL="$group_specification"
  COMPONENT_NAME="$group_name"
  component_name=$(echo "$group_name" | tr '[:upper:]' '[:lower:]' )
  component_name=${component_name// /-}
  documentation_name=documentation-${component_name}

  export COMPONENT_NAME COMPONENT_SPECIFICATION_URL
  export component_documentation_version_type COMPONENT_DOCUMENTATION_URL
  export COMPONENT_DOCUMENTATION_VERSION COMPONENT_DOCUMENTATION_VERSION_TYPE

  MODULE_DIR="components"
  templateFile="$DOC_OUT/$MODULE_DIR/template-documentation.md"
  awkenvsubst < "$templateFile" > "$MARKDOWN_FILES_DIR/$documentation_name.md"

  # Used later to fill components/introduction.md
  echo "$COMPONENT_DOCUMENTATION_URL" >> "$MARKDOWN_FILES_DIR/$documentation_name-url.md"
}


function awkenvsubst() {
  # performs variables escaping: '&' for gsub + JSON chars ('\' and '"')
  awk '{while(match($0,"[$%]{[^}]*}")) {var=substr($0,RSTART+2,RLENGTH-3);val=ENVIRON[var];gsub(/["\\&]/,"\\\\&",val);gsub("[$%]{"var"}",val)}}1'
}

### ---------------------------------------------------------------------------
###
### MAIN
### 
### ---------------------------------------------------------------------------

# Init 
source setenv.sh "$@"

# Will not fetch remote data 
if [ "$STATIC_ENABLED" == true ]; then
  log_info "Skipping \e[33;1mprocess-os.sh\\e[0m (keep MD files generated previously)"
  exit 0
fi

log_info "=========================================================================================="
log_info "process-doc.sh processes DISCOBOLE modules:"
log_info "- GitLab API url     (--api)                    : \\e[33;1m${API_URL}\\e[0m"
log_info "- Output docs dir    (--doc-out)                : \\e[33;1m${DOC_OUT}\\e[0m"
log_info "- Static enabled     (--static-enabled)         : \\e[33;1m${STATIC_ENABLED}\\e[0m"
log_info "- CI commit ref name (--branch)                 : \\e[33;1m${CI_COMMIT_REF_NAME}\\e[0m"
log_info "- CI commit tag      (--tag)                    : \\e[33;1m${CI_COMMIT_TAG}\\e[0m"
log_info "- CI project name    (--project)                : \\e[33;1m${CI_PROJECT_ROOT_NAMESPACE}\\e[0m"
log_info "- OpenShift project  (--os-project)             : \\e[33;1m${ENV_PROJECT:-$OS_PROJECT}\\e[0m"
log_info "- OpenShift API URL  (--os-url)                 : \\e[33;1m${OS_URL}\\e[0m"
log_info "- Groups to process  (--groups)                 : \\e[33;1m$(echo "${GITLAB_GROUPS}" | jq -c '.[] | .path')\\e[0m"

eval "$TRACE"

# Create a temporary directory to download files
tmp_dir=$(mktemp -d)
mkdir -p "$tmp_dir/toc"

log_info "Working in: \\e[33;1m${tmp_dir}\\e[0m"

# Create a file to pass the generated markdown files
MARKDOWN_FILES_DIR="markdown_files"
mkdir -p $MARKDOWN_FILES_DIR
rm $MARKDOWN_FILES_DIR/*.* 2>/dev/null

log_info "Generating output files in: \\e[33;1m${MARKDOWN_FILES_DIR}\\e[0m"

## ----------------------------------------------------------------------------
## PART I - Component and Deployment (RUN start part)
## ----------------------------------------------------------------------------

# Connect to OpenShift project
echo "${CUSTOM_CA_CERTS:-$DEFAULT_CA_CERTS}" > "$tmp_dir/cert.crt"
oc login "${ENV_API_URL:-$OS_URL}" --token="${ENV_TOKEN:-$OS_TOKEN}" -n "${ENV_PROJECT:-$OS_PROJECT}" --certificate-authority="$tmp_dir/cert.crt"

# Iterate on groups
extensions_array_json='[]'
for group_b64 in $(echo "$GITLAB_GROUPS" | jq -r '.[] | @base64')
do
  group_json=$(echo "$group_b64" | base64 -d)
  # Process modules from group
  group_path=$(echo "$group_json" | jq -r .path)
  group_visibility=$(echo "$group_json" | jq -r .visibility)
  exclude=$(echo "$group_json" | jq -r ".exclude[]? // empty")
  group_name=$(echo "$group_json" | jq -r .name)
  group_acronym=$(echo "$group_json" | jq -r .acronym)
  process_modules_from_group "$group_path" "$group_visibility" "$exclude" "$group_name" "$group_acronym"

  # Append extension if any
  group_extension=$(echo "$group_json" | jq .extension)
  if [[ "$group_extension" != "null" ]]
  then
    extensions_array_json=$(echo "$extensions_array_json" | jq ". += [$group_extension]")
  fi

  # Process group when it is an ODA component
  group_type=$(echo "$group_json" | jq -r .type)
  if [[ $group_type == "oda-component" ]]; then
    group_specification=$(echo "$group_json" | jq -r .specification)
    # Process component from group
    group_name=$(echo "$group_json" | jq -r .name)
    process_component_from_group "$group_path" "$group_name" "$group_specification"
  fi
done
