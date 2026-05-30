#!/usr/bin/env bash

# SPDX-FileCopyrightText: 2025 - 2026 Orange SA
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

# autodetects any Maven settings file in and builds the Java CLI option accordingly
function eval_mvn_settings_opt() {
    if [[ -f "$MAVEN_SETTINGS_FILE" ]]
    then
        log_info "Maven settings file found: \\e[33;1m$MAVEN_SETTINGS_FILE\\e[0m"
        mvn_settings_opt="-s $MAVEN_SETTINGS_FILE"
    fi
}

# check number of arguments
if [[ "$#" -le 2 ]]; then
    log_error "Missing arguments"
    log_error "Usage: $0 <current version> <next version> <branch name>"
    exit 1
fi

nextVer=$1
relType=$2
branchName=$3
tempNextVer=$(echo $nextVer | awk -F. '{print $1 "." $2 + 1 ".0"}')
snapVer=$tempNextVer-SNAPSHOT

eval_mvn_settings_opt

log_info "Bump version from \\e[33;1m${nextVer}\\e[0m to \\e[33;1m${snapVer}\\e[0m (release type: $relType)..."

# because HEAD is detached, create temporary branch to retrieve
# last commits before switching to develop
# (see https://git-scm.com/docs/git-checkout/en#_detached_head)

log_info "Create release branch to retrieve changes done by bumpversion.sh"

branch_from_release=release/${nextVer}
git checkout -b $branch_from_release

#
# bump to release version and commit changes in release branch
#

# replace in README
if [ -f README.md ]
then
    log_info "Bump from \\e[33;1m${nextVer}\\e[0m to \\e[33;1m${snapVer}\\e[0m version in README.md"
    sed -i "s/${nextVer//./\\.}\+\(-SNAPSHOT\)\?/${snapVer}/g" README.md
    log_info "Bump from \\e[33;1mdocker-stable\\e[0m to \\e[33;1mdocker-unstable\\e[0m repo in README.md"
    sed -i "s/docker-stable/docker-unstable/g" README.md
fi
git add ":(glob)**/*.md" # at least, CHANGELOG.md to add

# replace in pom files
if [ -f pom.xml ]
then
    log_info "Bump from \\e[33;1m${nextVer}\\e[0m to \\e[33;1mHEAD\\e[0m scm tag in pom.xml"
    find . -type f -name pom.xml -exec sed -i "s/<tag>[0-9\.]\+\(-SNAPSHOT\)\?/<tag>HEAD/" {} \;
    log_info "Bump from \\e[33;1m${nextVer}\\e[0m to \\e[33;1m${snapVer}\\e[0m version in pom.xml file(s)"
    mvn versions:set $MAVEN_CLI_OPTS $mvn_settings_opt -DnewVersion=$snapVer -q
    git add ":(glob)**/pom.xml"
fi

# replace in Dockerfile
if [ $(find . -type f -name Dockerfile) ]
then
    log_info "Bump from \\e[33;1m${nextVer}\\e[0m to \\e[33;1m${snapVer}\\e[0m version in Dockerfile"
    find . -type f -name Dockerfile -exec sed -i "s/version=\"[0-9\.]\+\(-SNAPSHOT\)\?\"/version=\"${tempNextVer}\"/" {} \;
    git add ":(glob)**/Dockerfile"
fi

# replace in helm chart file
if [ $(find . -type f -name Chart.yaml) ]
then
    log_info "Bump from \\e[33;1m${nextVer}\\e[0m to \\e[33;1m${tempNextVer}\\e[0m version in helm chart"
    find . -type f -name Chart.yaml -exec sed -i "s/^\(version: \)[0-9\.]\+\(-SNAPSHOT\)\?/\1${tempNextVer}/" {} \;
    log_info "Keep \\e[33;1mappVersion=$DISCOBOLE_RELEASE_VERSION\\e[0m in helm chart because charts may have several versions for the same DISCOBOLE release"
    log_info "To bump \\e[33;1mappVersion\\e[0m to the next DISCOBOLE release, edit \\e[33;1mDISCOBOLE_RELEASE_VERSION\\e[0m in the CI settings at the beginning of the new PI and run \\e[33;1msemantic-release\e[0m job"
    git add ":(glob)**/Chart.yaml"
fi

# commit changes (see https://css-tricks.com/git-pathspecs-and-how-to-use-them/#aa-glob)
git commit -m "chore: prepare next development iteration ${snapVer}"

#
# merge changes from release to develop
#

log_info "Merge '${branch_from_release}' into develop"

# retrieve origin/develop branch
git fetch origin
git checkout develop
git merge --no-ff $branch_from_release -m "Merge '$branch_from_release' into develop [ci skip]"
result=$?

git_base_url=$(echo "$CI_REPOSITORY_URL" | cut -d\@ -f2)
git_auth_url="https://token:${GITLAB_TOKEN}@${git_base_url}"

if [ $result -eq 0 ]
then
    log_info "Push new commit to develop [ci skip]"
    git push $git_auth_url develop
    result=$?
else
    log_info "Merge FAILED, create merge request..."
    git push \
      -o merge_request.create \
      -o merge_request.target=develop \
      -o merge_request.remove_source_branch \
      -o merge_request.title="Version ${nextVer}" \
      -o merge_request.description="Apply changes done in ${branchName} for version ${nextVer}" \
      -o merge_request.assign="$GITLAB_USER_ID" \
      $git_auth_url $branch_from_release
    result=$?
fi

exit $?
