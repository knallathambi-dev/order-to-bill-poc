#!/usr/bin/env bash

# Copyright (C) 2021 Orange & contributors
# SPDX-FileCopyrightText: 2025, 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

# Determines whether the given project is a module project or not
# If so:
# - downloads the README.md file
# - downloads the api-getting-started.md file if present
# - downloads the api-compliance-report.pdf file is present
# $1: project JSON representation
# $2: group name used for module sorting
function process_module() {
  project_json="$1"
  group_name="$2"
  project_id=$(echo "$project_json" | jq -r .id)
  project_name=$(echo "$project_json" | jq -r .path)
  project_path=$(echo "$project_json" | jq -r .path_with_namespace)
  project_web_url=$(echo "$project_json" | jq -r .web_url)  
  project_branch=${GIT_BRANCH:-develop}
  project_default_branch=$(echo "$project_json" | jq -r .default_branch)

  MODULE_DIR="architecture/detailed-architecture"
  USER_GUIDES_DIR="api-references/user-guides"
  COMPLIANCE_REPORTS_DIR="api-references/compliance"
  CONFORMANCE_REPORTS_DIR="api-references/conformance"
  SPECIFICATIONS_DIR="api-references/specifications"

  mkdir -p "$DOC_OUT/$MODULE_DIR"
  mkdir -p "$DOC_OUT/$USER_GUIDES_DIR"
  mkdir -p "$DOC_OUT/$COMPLIANCE_REPORTS_DIR"
  mkdir -p "$DOC_OUT/$CONFORMANCE_REPORTS_DIR"
  mkdir -p "$DOC_OUT/$SPECIFICATIONS_DIR"

  log_info "Project \\e[32m${project_path}\\e[0m declares module \\e[32m${project_name}\\e[0m of kind \\e[33;1m${group_name}\\e[0m on \\e[33;1m${project_default_branch}\\e[0m branch"

  # get README.md file if present
  log_info " ... downloading \\e[32mREADME.md\\e[0m to \\e[33;1m$DOC_OUT/$MODULE_DIR/$project_name.md\\e[0m..."
  readme_filename="$DOC_OUT/$MODULE_DIR/$project_name.md"
  http_status=$(curl "$CURL_SILENT" -R -H "$AUTH_HEADER" --output "$readme_filename" -w "%{http_code}" "$API_URL/projects/$project_id/repository/files/README.md/raw?ref=$project_default_branch")
  
  if [ "$http_status" == 200 ]; then
    log_info " ... \\e[32mREADME.md\\e[0m downloaded successfully."
    toc_file="${tmp_dir}/toc/readmes"
    echo "- $project_name: $MODULE_DIR/$project_name.md" >> "$toc_file"
    # README might contain relative paths => use absolute paths for OW2 GitLab instance
    project_web_url=${project_web_url/gitlab.tech.orange\/disco/gitlab.ow2.org\/discobole}
    log_info " ... replacing relative paths by absolute paths in \\e[32mREADME.md\\e[0m of \\e[33;1m$group_name\\e[0m"
    sed -i \
      -e "s|(helm/chart/README.md)|($project_web_url/-/blob/$project_default_branch/helm/chart/README.md)|g" \
      -e "s|(.gitlab-ci.yml)|($project_web_url/-/blob/$project_default_branch/.gitlab-ci.yml)|g" \
      -e "s|(doc/|($project_web_url/-/blob/$project_default_branch/doc/|g" \
      -e "s|(CONTRIBUTING.md)|($project_web_url/-/blob/$project_default_branch/CONTRIBUTING.md)|g" \
      -e "s|(../../tags)|($project_web_url/-/tags)|g" \
      -e "s|(CONTRIBUTORS.md)|($project_web_url/-/blob/$project_default_branch/CONTRIBUTORS.md)|g" \
      -e "s|(LICENSE.txt)|($project_web_url/-/blob/$project_default_branch/LICENSE.txt)|g" \
      "$readme_filename"
    # Remove environment chapter (three lines to delete) as there is none deployment
	  start_line_number=$(grep -rne "## Environments" "$readme_filename" | cut -f1 -d:)
	  end_line_number=$((start_line_number + 3))
	  sed -i "$start_line_number,$end_line_number d" "$readme_filename"
    # README might contain planuml diagrams => replace them by mkdocs puml
    log_info " ... replacing plantuml by puml in \\e[32mREADME.md\\e[0m of \\e[33;1m$group_name\\e[0m"
    sed -i -e "s|````plantuml|````puml|g" "$readme_filename"  
  else
    rm -f "$readme_filename"
  fi

  #
  # User guide, conformance, compliance and specifications are related to API projects managed by maven ONLY
  #  

  # check if project is a maven project
  log_info " ... downloading \\e[32mpom.xml\\e[0mfrom $API_URL/projects/$project_id/repository/files/pom%2xml/raw?ref=$project_branch to \\e[33;1m$tmp_dir/pom.xml\\e[0m..."
  http_status=$(curl "$CURL_SILENT" -R -H "$AUTH_HEADER" --output "$tmp_dir/pom.xml" -w "%{http_code}" "$API_URL/projects/$project_id/repository/files/pom%2Exml/raw?ref=$project_branch")
  if [ "$http_status" == 200 ]; then
    # pom.xml downloaded successfully
    log_info " ... \\e[32mpom.xml\\e[0m downloaded successfully."
  else
    log_info " ... \\e[32mpom.xml\\e[0m not found: $API_URL/projects/$project_id/repository/files/pom.xml%2Exml/raw?ref=$project_branch"
    log_info " ... Skipping user guide, conformance, compliance and specifications for \\e[32m$project_name\\e[0m as it is not a maven project."
    rm -f "$tmp_dir/pom.xml"
    return
  fi

  # get user guide
  log_info " ... downloading \\e[32mapi-getting-started.md\\e[0m from $API_URL/projects/$project_id/repository/files/doc%2Fapi-getting-started%2Emd/raw?ref=$project_branch to \\e[33;1m$DOC_OUT/$USER_GUIDES_DIR/$project_name.md\\e[0m..."
  http_status=$(curl "$CURL_SILENT" -R -H "$AUTH_HEADER" --output "$DOC_OUT/$USER_GUIDES_DIR/$project_name.md" -w "%{http_code}" "$API_URL/projects/$project_id/repository/files/doc%2Fapi-getting-started%2Emd/raw?ref=$project_branch")
  if [ "$http_status" == 200 ]; then
    log_info " ... \\e[32mapi-getting-started.md\\e[0m downloaded successfully."
    toc_file="${tmp_dir}/toc/user-guides"
    echo "- $project_name: $USER_GUIDES_DIR/$project_name.md" >> "${toc_file}"
  else
    log_info " ... \\e[32mapi-getting-started.md\\e[0m not found: $API_URL/projects/$project_id/repository/files/doc%2Fapi-getting-started%2Emd/raw?ref=$project_branch"
    # remove file containing {"message":"404 File Not Found"}
    rm -f "$DOC_OUT/$USER_GUIDES_DIR/$project_name.md"
  fi

  # get conformance report if present
  log_info " ... downloading \\e[32mCTK-report.html\\e[0m from $API_URL/projects/$project_id/repository/files/doc%2FCTK-report%2Ehtml/raw?ref=$project_branch to \\e[33;1m$DOC_OUT/$CONFORMANCE_REPORTS_DIR/$project_name.html\\e[0m..."
  http_status=$(curl "$CURL_SILENT" -R -H "$AUTH_HEADER" --output "$DOC_OUT/$CONFORMANCE_REPORTS_DIR/$project_name.html" -w "%{http_code}" "$API_URL/projects/$project_id/repository/files/doc%2FCTK-report%2Ehtml/raw?ref=$project_branch")
  if [ "$http_status" == 200 ]; then
    log_info " ... \\e[32mCTK-report.html\\e[0m downloaded successfully."
    toc_file="${tmp_dir}/toc/conformance-reports"
    echo "- $project_name: $CONFORMANCE_REPORTS_DIR/$project_name.html" >> "${toc_file}"
  else
    log_info " ... \\e[32mCTK-report.html\\e[0m not found: $API_URL/projects/$project_id/repository/files/doc%2FCTK-report%2Ehtml/raw?ref=$project_branch"
    # remove file containing {"message":"404 File Not Found"}
    rm -f "$DOC_OUT/$CONFORMANCE_REPORTS_DIR/$project_name.html"
  fi

  # get compliance report if present
  log_info " ... downloading \\e[32mapi-compliance-report.pdf\\e[0m from $API_URL/projects/$project_id/repository/files/doc%2Fapi-compliance-report%2Epdf/raw?ref=$project_branch to \\e[33;1m$DOC_OUT/$COMPLIANCE_REPORTS_DIR/$project_name.pdf\\e[0m..."
  http_status=$(curl "$CURL_SILENT" -R -H "$AUTH_HEADER" --output "$DOC_OUT/$COMPLIANCE_REPORTS_DIR/$project_name.pdf" -w "%{http_code}" "$API_URL/projects/$project_id/repository/files/doc%2Fapi-compliance-report%2Epdf/raw?ref=$project_branch")
  if [ "$http_status" == 200 ]; then
    log_info " ... \\e[32mapi-compliance-report.pdf\\e[0m downloaded successfully."
    toc_file="${tmp_dir}/toc/compliance-reports"
    echo "- $project_name: $COMPLIANCE_REPORTS_DIR/$project_name.pdf" >> "${toc_file}"
  else
    log_info " ... \\e[32mapi-compliance-report\\e[0m not found: $API_URL/projects/$project_id/repository/files/doc%2Fapi-compliance-report%2Epdf/raw?ref=$project_branch"
    # remove file containing {"message":"404 File Not Found"}
    rm -f "$DOC_OUT/$COMPLIANCE_REPORTS_DIR/$project_name.pdf"
  fi

  # get openapi spec by cloning the project
  log_info " ... cloning project \\e[32m${project_name}\\e[0m in \\e[33;1m$tmp_dir/$project_path\\e[0m..."
  if ! git clone -q --depth 1 --branch "$project_branch" "https://gitlab-ci-token:${CI_JOB_TOKEN}@${CI_SERVER_HOST}/${project_path}.git" "$tmp_dir/$project_path"; then
    log_error "Failed to clone project $project_path"
    rm -f "$tmp_dir/pom.xml"
    return 1
  else 
    log_info " ... project \\e[32m${project_name}\\e[0m cloned successfully."
  fi

  # search openapi specs in the project
  resources_paths=$(find "$tmp_dir/$project_path" -name "resources" -type d | grep "src/main/resources")
  for resources_path in $resources_paths; do
    # search for the spec files in each resources directory
    log_info " ... searching for openapi spec file in \\e[33;1m$resources_path\\e[0m..."
    spec_files=$(grep -l -e 'openapi: "3' -e 'swagger: "2' "$resources_path"/*.yaml 2>/dev/null) # may have several spec files in src/main/resources (sample: user-role API)
    if [ -z "$spec_files" ]; then
      log_info " ... no openapi spec files found in \\e[33;1m$resources_path\\e[0m."
    else 
      toc_file="${tmp_dir}/toc/api-specifications"
      for spec_file in $spec_files; do
        # add spec file in docs/api-references/specifications
        log_info " ... found openapi spec file \\e[32m$spec_file\\e[0m in \\e[33;1m$resources_path\\e[0m."
        spec_file_name=$(basename "$spec_file") # Get the file name without the path
        spec_file_ext="${spec_file_name##*.}" # Get the file extension
        if [[ "$spec_file_ext" != "yaml" ]]; then
          log_error " ... \\e[32m$spec_file\\e[0m is not a yaml file (use .yaml file): skipping."
          continue
        fi
        cp "$spec_file" "$DOC_OUT/$SPECIFICATIONS_DIR"
        log_info " ... \\e[32m$spec_file\\e[0m copied to \\e[33;1m$DOC_OUT/$SPECIFICATIONS_DIR/$spec_file_name\\e[0m."
        # generate md file to refer spec file
        spec_file_md="$DOC_OUT/$SPECIFICATIONS_DIR/${spec_file_name%."${spec_file_ext}"}.md"
        spec_file_md_name=$(basename "$spec_file_md") # Get the file name without the path
        {
          echo "---" 
          echo "title: $spec_file_name" 
          echo "summary: API specification for $project_name"
          echo "---" 
          echo "" 
          echo "<swagger-ui src=\"./$spec_file_name\"/>"
         } > "$spec_file_md"
        log_info " ... \\e[32m$spec_file_md\\e[0m generated successfully."
        echo "- $project_name: $SPECIFICATIONS_DIR/$spec_file_md_name" >> "${toc_file}"
      done
    fi
  done

}

# Iterates over all projects from given group with given visibility and tries to process it as a product-inventory module project
# $1: GitLab group path
# $2: visibility to scan through
function process_modules_from_group() {
  group_path="$1"
  group_id=${group_path//\//%2f}
  visibility="$2"
  exlude_projects="$3"
  group_name=$(echo "$group_path" | sed -e 's/disco-product-inventory\///g')
  log_info "--- Looking for projects with visibility \\e[33;1m${visibility}\\e[0m from group \\e[32m${group_path}\\e[0m..."

  for project_b64 in $(curl -sSf -H "$AUTH_HEADER" "$API_URL/groups/$group_id/projects?archived=false&visibility=${visibility}&per_page=100" | jq -r '.[] | @base64')
  do
    project_json=$(echo "$project_b64" | base64 -d)
    project_path=$(echo "$project_json" | jq -r '.path')
    if echo "$exlude_projects" | grep -e "^${project_path}\$"
    then
      log_info "Project \\e[33;1m${project_path}\\e[0m matches excludes: skip"
    else
      process_module "$project_json" "$group_name"
    fi
  done
}

# Add ADR.md files from the default branch (should be main, as we don't release ADR files)
function build_architecture_decisions_toc() {
  log_info "--- Getting ADR markdown files from disco-project repo ..."
  adr_src_dir="$tmp_dir/architecture"
  git clone -q --depth 1 "https://gitlab-ci-token:${CI_JOB_TOKEN}@${CI_SERVER_HOST}/disco/disco-oda-components/disco-product-inventory/product-inventory-architecture.git" "$adr_src_dir"
  status="$?"
  if [ "$status" != 0 ]; then 
    error_message="Check the GitLab access token is enabled in the settings of https://${CI_SERVER_HOST}/disco/disco-oda-components/disco-product-inventory/product-inventory-architecture.git"
    log_error "$error_message"
    return
  fi
  
  # get adr markdown files
  adr_dst_dir=$DOC_OUT/architecture/decisions
  mkdir -p "$adr_dst_dir"
  cp -r "$adr_src_dir"/decisions/*.md "$adr_dst_dir"
  rm "$adr_dst_dir"/adr-template.md

  # because ADRs may refer other ADRs, xxxx-yyyy.md filenames must be changed as xxxx-yyyyyy
  log_info "In ADR files, replace referred ADR filenames by relative URLs (if not, links will not work)"
  for adr_file in "$adr_dst_dir"/*.md; do
    sed -i -e "s/([^0-9]*/(..\//g" -e "s/\.md)/)/g" "$adr_file"
  done

  # add entry to TOC
  toc_file="${tmp_dir}/toc/architecture"
  for adr_file in "$adr_dst_dir"/*.md
  do
    adr_label=$(basename "$adr_file" .md) # get filename without extension
    adr_label=${adr_label//-/ } # replace '-' by space
    adr_label="ADR-$adr_label"
    # sort by sub-folder
    adr_file_path=$(echo "$adr_file" | sed -e 's/.\/docs\/architecture\/decisions\///g' )
    toc_file="${tmp_dir}/toc/architecture"
    echo "- $adr_label: architecture/decisions/$adr_file_path" >> "$toc_file"
  done

  log_info "--- Generating architecture decisions TOC..."
  toc_file="$tmp_dir/architecture_decisions_toc.md"
  {
    MD_INDENT="    "
    if [[ -f "$tmp_dir/toc/architecture" ]]; then
      sort "$tmp_dir/toc/architecture" | sed "s/^ *-/${MD_INDENT}-/"
    fi
  } > "$toc_file"
}

function build_detailed_architecture_toc() {
  log_info "--- Generating detailed architecture TOC..."
  if [[ -f "$tmp_dir/toc/readmes" ]]; then
    toc_file="$tmp_dir/detailed_architecture_toc.md"
    echo "  - Detailed architecture:" > "$toc_file"
    MD_INDENT="    "
    {
      # readme files
      sort "$tmp_dir/toc/readmes" | sed "s/^ *-/${MD_INDENT}- API >/"
    } >> "$toc_file"
  fi
}

function build_api_user_guides_toc() {
  log_info "--- Generating API user guides TOC..."
  toc_file="$tmp_dir/api_user_guides_toc.md"
  touch "$toc_file"
  # sort by API project names
  MD_INDENT="    "
  if [[ -f "$tmp_dir/toc/user-guides" ]]; then
    sort "$tmp_dir/toc/user-guides" | sed "s/^-/${MD_INDENT}- API >/" > "$toc_file"
  fi
}

function build_api_compliance_reports_toc() {
  log_info "--- Generating API compliance reports TOC..."
  toc_file="$tmp_dir/api_compliance_reports_toc.md"
  touch "$toc_file"
  # sort by API project names
  MD_INDENT="    "
  if [[ -f "$tmp_dir/toc/compliance-reports" ]]; then
    sort "$tmp_dir/toc/compliance-reports" | sed "s/^-/${MD_INDENT}- API >/" > "$toc_file"
  fi
}

function build_api_conformance_reports_toc() {
  log_info "--- Generating API conformance report TOC..."
  toc_file="$tmp_dir/api_conformance_reports_toc.md"
  touch "$toc_file"
  # sort by API project names
  MD_INDENT="    "
  if [[ -f "$tmp_dir/toc/conformance-reports" ]]; then
    sort "$tmp_dir/toc/conformance-reports" | sed "s/^-/${MD_INDENT}- API >/" > "$toc_file"
  fi
}

function build_api_specifications_toc() {
  log_info "--- Generating API specifications TOC..."
  toc_file="$tmp_dir/api_specifications_toc.md"
  touch "$toc_file"
  # sort by API project names
  MD_INDENT="    "
  if [[ -f "$tmp_dir/toc/api-specifications" ]]; then
    sort "$tmp_dir/toc/api-specifications" | sed "s/^-/${MD_INDENT}- API >/" > "$toc_file"
  fi
}


### ---------------------------------------------------------------------------
###
### MAIN
### 
### ---------------------------------------------------------------------------


# Init 
source setenv.sh "$@"

# Will not fetch remote data on Git
if [ "$STATIC_ENABLED" == true ]; then
  log_info "Skipping \\e[33;1mprocess-doc.sh\\e[0m (keep MD files generated previously)"
  exit 0
fi

log_info "=========================================================================================="
log_info "\\e[33;1mprocess-doc.sh\\e[0m processes DISCOBOLE modules:"
log_info "- GitLab API url     (--api)                    : \\e[33;1m${API_URL}\\e[0m"
log_info "- Output docs dir    (--doc-out)                : \\e[33;1m${DOC_OUT}\\e[0m"
log_info "- Static enabled     (--static-enabled)         : \\e[33;1m${STATIC_ENABLED}\\e[0m"
log_info "- CI commit ref name (--branch)                 : \\e[33;1m${CI_COMMIT_REF_NAME}\\e[0m"
log_info "- CI commit tag      (--tag)                    : \\e[33;1m${CI_COMMIT_TAG}\\e[0m"
log_info "- CI project name    (--project)                : \\e[33;1m${CI_PROJECT_ROOT_NAMESPACE}\\e[0m"
log_info "- Groups to process  (--groups)                 : \\e[33;1m$(echo "${GITLAB_GROUPS}" | jq -c '.[] | .path')\\e[0m"

eval "$TRACE"

# create a temporary directory to download files
tmp_dir=$(mktemp -d)
mkdir -p "$tmp_dir/toc"

log_info " ... working in: \\e[33;1m${tmp_dir}\\e[0m"

## ----------------------------------------------------------------------------
## PART I - Architecture (README, ADRs) and Deployment (RUN start part)
## ----------------------------------------------------------------------------

# iterate on groups
extensions_array_json='[]'
for group_b64 in $(echo "$GITLAB_GROUPS" | jq -r '.[] | @base64')
do
  group_json=$(echo "$group_b64" | base64 -d)

  # process modules from group
  group_path=$(echo "$group_json" | jq -r .path)
  group_visibility=$(echo "$group_json" | jq -r .visibility)
  exclude=$(echo "$group_json" | jq -r ".exclude[]? // empty")
  process_modules_from_group "$group_path" "$group_visibility" "$exclude"

  # append extension if any
  group_extension=$(echo "$group_json" | jq .extension)
  if [[ "$group_extension" != "null" ]]
  then
    extensions_array_json=$(echo "$extensions_array_json" | jq ". += [$group_extension]")
  fi
done

# process ADR files to produce architecture decisions toc
build_architecture_decisions_toc

# process all product-inventory module toc
build_detailed_architecture_toc

# process all API user guides
build_api_user_guides_toc

# process all API compliance reports
build_api_compliance_reports_toc

# process all API conformance reports
build_api_conformance_reports_toc

# process all API specifications
build_api_specifications_toc

## ----------------------------------------------------------------------------
## PART II - Update navigation part
## ----------------------------------------------------------------------------

# update nav in mkdocs.yml
if [[ -f "mkdocs.yml" ]]
then
  log_info "Replacing architecture decisions toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/architecture_decisions_toc.md" sed 's/^/  /')
  awk -v toc="${toc//&/\\\\&}" '{sub(/.*INSERT_ARCHITECTURE_DECISIONS_TOC_HERE.*/,toc)}1' mkdocs.yml > "${tmp_dir}/mkdocs-with-toc-tmp1.yml"

  log_info "Replacing detailed architecture toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/detailed_architecture_toc.md" sed 's/^/  /' | awk '{gsub(/&/, "\\\\&"); printf "%s\\n", $0}')
  awk -v toc="${toc}" '/INSERT_DETAILED_ARCHITECTURE_TOC_HERE/ {print toc; next} 1' "${tmp_dir}/mkdocs-with-toc-tmp1.yml" > "${tmp_dir}/mkdocs-with-toc-tmp2.yml"

  log_info "Replacing API user guides toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/api_user_guides_toc.md" sed 's/^/  /')
  awk -v toc="${toc//&/\\\\&}" '{sub(/.*INSERT_API_USER_GUIDES_TOC_HERE.*/,toc)}1' "${tmp_dir}/mkdocs-with-toc-tmp2.yml" > "${tmp_dir}/mkdocs-with-toc-tmp3.yml"

  log_info "Replacing API compliance report toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/api_compliance_reports_toc.md" sed 's/^/  /')
  awk -v toc="${toc//&/\\\\&}" '{sub(/.*INSERT_API_COMPLIANCE_REPORTS_TOC_HERE.*/,toc)}1' "${tmp_dir}/mkdocs-with-toc-tmp3.yml" > "${tmp_dir}/mkdocs-with-toc-tmp4.yml"

  log_info "Replacing API conformance report toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/api_conformance_reports_toc.md" sed 's/^/  /')
  awk -v toc="${toc//&/\\\\&}" '{sub(/.*INSERT_API_CONFORMANCE_REPORT_TOC_HERE.*/,toc)}1' "${tmp_dir}/mkdocs-with-toc-tmp4.yml" > "${tmp_dir}/mkdocs-with-toc-tmp5.yml"

  log_info "Replacing API specifications toc in mkdocs.yml..."
  toc=$(< "$tmp_dir/api_specifications_toc.md" sed 's/^/  /')
  awk -v toc="${toc//&/\\\\&}" '{sub(/.*INSERT_API_SPECIFICATIONS_TOC_HERE.*/,toc)}1' "${tmp_dir}/mkdocs-with-toc-tmp5.yml" > mkdocs-with-toc.yml
fi

## ----------------------------------------------------------------------------
## PART III - Commit generated pages to skip the page building for OW2
## ----------------------------------------------------------------------------

log_info "Push eventual new commits to $CI_COMMIT_REF_NAME [ci skip]"

[[ -z "$CI_REPOSITORY_URL" ]] && log_warn "Can't commit any changes (CI_REPOSITORY_URL variable is missing)" && exit 0

# add all md and html files
# use magic signatures instead of wildcard (don't work in CI pipeline)
# (see https://css-tricks.com/git-pathspecs-and-how-to-use-them/#aa-glob)
git add ":(glob)**/*.md"
git add ":(glob)**/*.html"

git commit -m "chore(mkdocs): generate MD files and index.html"
# because HEAD is detached by the job, do not commit from
# local branch to remote branch but from HEAD to remote branch
git_base_url=$(echo "$CI_REPOSITORY_URL" | cut -d\@ -f2)
git_auth_url="https://token:${GITLAB_TOKEN}@${git_base_url}"
git push -o ci.skip "$git_auth_url" HEAD:"$CI_COMMIT_REF_NAME"

exit $?
