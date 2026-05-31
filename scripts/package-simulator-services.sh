#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f scripts/use-java17.sh ]]; then
  # shellcheck source=/dev/null
  source scripts/use-java17.sh
fi

mvn -f custom-services/pom.xml clean package
