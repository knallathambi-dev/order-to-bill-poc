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

# check number of arguments
if [[ "$#" -le 4 ]]; then
    log_error "Missing arguments"
    log_error "Usage: $0 <current version> <next version> <release type> <branch name> <release notes>"
    exit 1
fi

curVer=$1
nextVer=$2
relType=$3
branchName=$4
notes=$5

if [ -n "$curVer" ]; then
    log_info "Bump version from \\e[33;1m${curVer}\\e[0m to \\e[33;1m${nextVer}\\e[0m (release type: $relType)..."
else
    log_info "Bump version to \\e[33;1m${nextVer}\\e[0m (release type: $relType): this is the first release..."
fi

log_info "Switched to a new branch '${branchName}'"

#
# bump from snapshot version to release number and commit
#

# use magic signatures instead of wildcard (don't work in CI pipeline)
# (see https://css-tricks.com/git-pathspecs-and-how-to-use-them/#aa-glob)
git add ":(glob)**/CHANGELOG.md"

# replace in package.json (Node.js / React projects)
if [ -f package.json ]
then
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in package(-lock).json"
    npm version "$nextVer" --no-git-tag-version --allow-same-version
    git add ":(glob)**/package*.json"
fi

# replace in Dockerfile
if [ $(find . -type f -name Dockerfile) ]
then
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in Dockerfile"
    find . -name 'Dockerfile' -exec sed -i "s/version=\"[0-9\.]\+\"/version=\"$nextVer\"/" {} \;
    git add ":(glob)**/Dockerfile"
fi

# replace in helm chart file
if [ $(find . -type f -name Chart.yaml) ]
then
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in helm chart"
    find . -type f -name Chart.yaml -exec sed -i "s/version: [0-9\.]\+/version: $nextVer/"  {} \;
    find . -type f -name Chart.yaml -exec sed -i "s/appVersion: \"[0-9\.]\+\"/appVersion: \"${DISCOBOLE_RELEASE_VERSION}\"/"  {} \;
    git add ":(glob)**/Chart.yaml"
fi

#
# push changes in remote master branch and skip CI pipeline
#

log_info "Push new commit to $branchName [ci skip]"

git commit -m "chore(release): ${nextVer}" -m "${notes}"
# because HEAD is detached by semantic-release job, do not commit from
# local master to remote master but from HEAD to remote master branch
git_base_url=$(echo "$CI_REPOSITORY_URL" | cut -d\@ -f2)
git_auth_url="https://token:${GITLAB_TOKEN}@${git_base_url}"
git push -o ci.skip $git_auth_url HEAD:$branchName

exit $?