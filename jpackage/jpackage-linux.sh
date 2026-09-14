#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "JAVA_HOME is not set. Point it to a JDK install." >&2
  exit 1
fi
echo "JAVA_HOME=$JAVA_HOME"

JDEPS="$JAVA_HOME/bin/jdeps"
JLINK="$JAVA_HOME/bin/jlink"
JPACKAGE="$JAVA_HOME/bin/jpackage"

if [[ ! -x "$JDEPS" || ! -x "$JLINK" || ! -x "$JPACKAGE" ]]; then
  echo "JDK tools not found under JAVA_HOME: $JAVA_HOME" >&2
  exit 1
fi

APP_NAME="AliView"
POM_VERSION="$(python3 - <<'PY'
import xml.etree.ElementTree as ET
tree = ET.parse("pom.xml")
root = tree.getroot()
ns = {"m": root.tag.split("}")[0].strip("{")}
print(root.find("m:version", ns).text)
PY
)"
if [[ -z "${APP_VERSION:-}" ]]; then
  APP_VERSION="$POM_VERSION"
fi
echo "APP_VERSION=$APP_VERSION"

rm -rf "target/jpackage-linux"
mkdir -p "target/jpackage-linux/input"


echo "Building fat jar..."
mvn -DskipTests package

# Use the LINUX flavoured jar, not target/aliview.jar. The pom's
# cleanup-unneeded-files step strips muscle*linux* from target/aliview.jar
# (it is the mac jar) and strips the mac/windows binaries from this one, so
# only this jar carries the bundled Linux MUSCLE binaries that
# "Realign everything" extracts at runtime. The Windows script likewise uses
# its own target/windows-version-*/aliview.jar.
LINUX_JAR="target/linux-version-${POM_VERSION}/aliview/aliview.jar"
if [[ ! -f "$LINUX_JAR" ]]; then
  echo "Expected jar not found: $LINUX_JAR" >&2
  exit 1
fi

cp "$LINUX_JAR" "target/jpackage-linux/input/aliview.jar"
cp -f "src/main/resources/img/splash_128x128.png" "target/jpackage-linux/input/"


echo "Computing module list with jdeps..."
JDEPS_JAR="target/jpackage-linux/jdeps-aliview.jar"
cp "target/jpackage-linux/input/aliview.jar" "$JDEPS_JAR"
if command -v zip >/dev/null 2>&1; then
  zip -q -d "$JDEPS_JAR" "module-info.class" "META-INF/versions/*/module-info.class" || true
fi
if MODULES="$("$JDEPS" --multi-release 21 --ignore-missing-deps --class-path "$JDEPS_JAR" --print-module-deps "$JDEPS_JAR" 2>/dev/null)"; then
  if [[ -z "$MODULES" ]]; then
    MODULES="java.desktop,java.logging,java.prefs,java.xml,java.management"
  fi
else
  echo "jdeps failed; falling back to a conservative module list." >&2
  MODULES="java.desktop,java.logging,java.prefs,java.xml,java.management"
fi

echo "Creating runtime image with jlink..."
"$JLINK" \
  --add-modules "$MODULES" \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --output "target/jpackage-linux/runtime"

# We build the app-image only. Native packages (.deb/.rpm) are intentionally not
# produced: the self-extracting installer in linux-app-image-installer/ wraps
# this app-image and is distro-agnostic, and jpackage's auto-computed .deb
# dependencies break across distro releases (e.g. the Ubuntu 24.04 t64 renames).
echo "Packaging app-image with jpackage..."
"$JPACKAGE" \
  --type app-image \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --input "target/jpackage-linux/input" \
  --main-jar "aliview.jar" \
  --main-class "aliview.AliView" \
  --icon "src/main/resources/img/alignment_ico_128x128.png" \
  --runtime-image "target/jpackage-linux/runtime" \
  --dest "target/jpackage-linux" \
  --java-options "-Xmx1024m" \
  --java-options "-Xms128m" \
  --java-options "-splash:\$APPDIR/splash_128x128.png"

APP_IMAGE_DIR="target/jpackage-linux/${APP_NAME}"
APP_IMAGE_TGZ="target/jpackage-linux/${APP_NAME}-${APP_VERSION}-linux-app-image.tar.gz"
if [[ -d "$APP_IMAGE_DIR" ]]; then
  tar -C "target/jpackage-linux" -czf "$APP_IMAGE_TGZ" "$APP_NAME"
  echo "Created app-image archive: $APP_IMAGE_TGZ"
fi

echo "Done: target/jpackage-linux"
