#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -z "${JAVA_HOME:-}" ]] && [[ "$(uname)" == Darwin ]]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
fi
./gradlew :app:packageRelease "$@"
printf 'APK: %s/app/build/outputs/apk/release/app-release.apk\n' "$PWD"
