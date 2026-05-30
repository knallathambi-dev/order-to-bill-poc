#!/usr/bin/env bash

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

# autodetects any Maven settings file in and builds the Java CLI option accordingly
function eval_mvn_settings_opt() {
    if [[ -f "$MAVEN_SETTINGS_FILE" ]]
    then
        log_info "Maven settings file found: \\e[33;1m$MAVEN_SETTINGS_FILE\\e[0m"
        mvn_settings_opt="-s $MAVEN_SETTINGS_FILE"
    fi
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

eval_mvn_settings_opt

if [ "$curVer" ]; then
    log_info "Bump version from \\e[33;1m${curVer}\\e[0m to \\e[33;1m${nextVer}\\e[0m (release type: $relType)..."
    tempNextVer=$(echo $curVer | awk -F. '{print $1 "." $2 + 1 ".0"}')
    snapVer=$tempNextVer-SNAPSHOT
else
    # no tag found => first release
    # try to find snapVer from maven project
    if [ -f pom.xml ]
    then
        apt-get update -q 1>/dev/null
        apt-get install -qy maven 1>/dev/null
        snapVer=$(mvn help:evaluate $mvn_settings_opt -Dexpression=project.version -q -DforceStdout | sed "s/[^0-9]*\([0-9\.]\+\(-SNAPSHOT\)\?\).*/\1/")
    else
        # set to first version 0.0.1-SNAPSHOT
        snapVer=0.0.1-SNAPSHOT
    fi
    curVer=${snapVer/\-SNAPSHOT/}
    log_info "Bump version from \\e[33;1m${curVer}\\e[0m to \\e[33;1m${nextVer}\\e[0m (release type: $relType): this is the first release..."
fi

log_info "Switched to a new branch '${branchName}'"

#
# bump from snapshot version to release number and commit
#

# replace in README
if [ -f README.md ]
then
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in README.md"
    sed -i "s/${snapVer//./\\.}/${nextVer}/g" README.md
    sed -i "s/${curVer//./\\.}/${nextVer}/g" README.md
    log_info "Bump from \\e[33;1mdocker-unstable\\e[0m to \\e[33;1mdocker-stable\\e[0m repo in README.md"
    sed -i "s/docker-unstable/docker-stable/g" README.md
fi

# add all md files (including CHANGELOG.md)
# use magic signatures instead of wildcard (don't work in CI pipeline)
# (see https://css-tricks.com/git-pathspecs-and-how-to-use-them/#aa-glob)
git add ":(glob)**/*.md"

# replace in pom files
if [ -f pom.xml ]
then
    log_info "Bump from \\e[33;1mHEAD\\e[0m to \\e[33;1m${nextVer}\\e[0m scm tag in pom.xml"
    find . -name pom.xml -exec sed -i "s/<tag>[0-9\.]\+\(-SNAPSHOT\)\?/<tag>$nextVer/g" {} \;
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in pom.xml file(s)"
    apt-get update -q 1>/dev/null
    apt-get install -qy maven 1>/dev/null
    mvn versions:set $MAVEN_CLI_OPTS $mvn_settings_opt -DnewVersion=$nextVer -q
    git add ":(glob)**/pom.xml"
fi

# replace in Dockerfile
#if [ $(find . -type f -name Dockerfile) ]
#then
#    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in Dockerfile"
#    find . -name 'Dockerfile' -exec sed -i "s/version=\"[0-9\.]\+\(-SNAPSHOT\)\?\"/version=\"$nextVer\"/" {} \;
#    git add ":(glob)**/Dockerfile"
#fi

# replace in helm chart file
if [ $(find . -type f -name Chart.yaml) ]
then
    log_info "Bump to \\e[33;1m${nextVer}\\e[0m version in helm chart"
    find . -type f -name Chart.yaml -exec sed -i "s/version: [0-9\.]\+\(-SNAPSHOT\)\?/version: $nextVer/"  {} \;
    find . -type f -name Chart.yaml -exec sed -i "s/appVersion: \"[0-9\.]\+\"/appVersion: \"${DISCOBOLE_RELEASE_VERSION}\"/"  {} \;
    git add ":(glob)**/Chart.yaml"
#    log_info "Bump from \\e[33;1mdocker-unstable\\e[0m to \\e[33;1mdocker-stable\\e[0m repo in helm values"
#    find . -name values* -exec sed -i "s/docker-unstable.repos.tech/docker-stable.repos.tech/g" {} \;
#    log_info "Bump from \\e[33;1m${curVer}\\e[0m to \\e[33;1mmain\\e[0m tag in helm values"
#    find . -name values-staging.yaml -exec sed -i "s/tag: \"[0-9\.]\+\(-SNAPSHOT\)\?\"/tag: \"main\"/g" {} \;
#    find . -name values-prod.yaml -exec sed -i "s/tag: \"[0-9\.]\+\(-SNAPSHOT\)\?\"/tag: \"main\"/g" {} \;
#    find . -type f -name values-prod.yaml -exec sed -i "s/tag: "[0-9]\.[0-9]\.*"/tag: $nextVer/"  {} \;
#    git add ":(glob)**/values*"
fi

#
# push changes in remote master branch and skip CI pipeline
#

log_info "Push new commit to $branchName [ci skip]"

git commit -m "chore(release): ${nextVer}" -m "${notes}"
# because HEAD is detached by semantic-release job, do not commit from
# local master to remote master but from HEAD to remote master branch
git_base_url=$(echo "$CI_REPOSITORY_URL" | cut -d\@ -f2)
git_auth_url="https://token:${SEMANTIC_TOKEN}@${git_base_url}"
git push -o ci.skip $git_auth_url HEAD:$branchName

exit $?