#!/usr/bin/env bash

# External environment variables
VERSION_CODE="$1"
export VERSION_NAME=${VERSION_CODE:0:1}.${VERSION_CODE:1:1}.${VERSION_CODE:2}

CHANGES=$(cat fastlane/metadata/android/en-US/changelogs/$VERSION_CODE.txt)

DATE=$(date '+%Y-%m-%d')
NEW="### Version ${VERSION_NAME} Tag: v${VERSION_NAME} (${DATE})\n${CHANGES}\n"
echo -e "${NEW}\n$(cat CHANGELOG.md)" > CHANGELOG.md