#!/bin/bash
# Build the Linux app-image and wrap it into the self-extracting .run installer,
# locally, in one step.
#
# FOR LOCAL TESTING ONLY: the glibc floor of the result equals your local JDK's
# floor (a distro-packaged JDK is typically high, e.g. 2.38). Distributable
# builds should come from the tagged CI workflow (.github/workflows/
# build-linux-installer.yml), which uses Eclipse Temurin for a low glibc floor.
#
# Requires: JAVA_HOME pointing at a JDK, and makeself on PATH.
set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

./jpackage/jpackage-linux.sh
./linux-app-image-installer/create-package.sh

echo
echo "Local installer: linux-app-image-installer/aliview.install.run"
echo "NOTE: this is a local test build. For distribution use the CI build,"
echo "      which uses Temurin so the installer runs on older Linux distros."
