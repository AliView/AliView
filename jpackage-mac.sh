#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "JAVA_HOME is not set. Point it to a JDK 21 install." >&2
  exit 1
fi

JDEPS="$JAVA_HOME/bin/jdeps"
JLINK="$JAVA_HOME/bin/jlink"
JPACKAGE="$JAVA_HOME/bin/jpackage"

if [[ ! -x "$JDEPS" || ! -x "$JLINK" || ! -x "$JPACKAGE" ]]; then
  echo "JDK tools not found under JAVA_HOME: $JAVA_HOME" >&2
  exit 1
fi

APP_NAME="AliView"
APP_VERSION="${APP_VERSION:-$(python3 - <<'PY'
import xml.etree.ElementTree as ET
tree = ET.parse("pom.xml")
root = tree.getroot()
ns = {"m": root.tag.split("}")[0].strip("{")}
print(root.find("m:version", ns).text)
PY
)}"

INPUT_JAR="$ROOT_DIR/target/aliview.jar"
BUILD_DIR="$ROOT_DIR/target/jpackage"
INPUT_DIR="$BUILD_DIR/input"
RUNTIME_DIR="$BUILD_DIR/runtime"
TYPES="${JPACKAGE_TYPES:-app-image,dmg}"

rm -rf "$BUILD_DIR"
mkdir -p "$INPUT_DIR"

echo "Building fat jar..."
mvn -DskipTests package

if [[ ! -f "$INPUT_JAR" ]]; then
  echo "Expected jar not found: $INPUT_JAR" >&2
  exit 1
fi

cp "$INPUT_JAR" "$INPUT_DIR/"

echo "Computing module list with jdeps..."
JDEPS_JAR="$BUILD_DIR/jdeps-aliview.jar"
cp "$INPUT_DIR/aliview.jar" "$JDEPS_JAR"
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
  --output "$RUNTIME_DIR"

for TYPE in ${TYPES//,/ }; do
  JPACKAGE_ARGS=(
    --type "$TYPE"
    --name "$APP_NAME"
    --app-version "$APP_VERSION"
    --input "$INPUT_DIR"
    --main-jar "aliview.jar"
    --main-class "aliview.AliView"
    --icon "$ROOT_DIR/src/main/resources/img/alignment_ico.icns"
    --runtime-image "$RUNTIME_DIR"
    --file-associations "$ROOT_DIR/jpackage/file-associations.properties"
    --dest "$BUILD_DIR"
    --java-options "-Xmx1024m"
    --java-options "-Xms128m"
  )

  if [[ -n "${MAC_BUNDLE_ID:-}" ]]; then
    if [[ "$(uname -s)" == "Darwin" ]]; then
      JPACKAGE_ARGS+=(--mac-package-identifier "$MAC_BUNDLE_ID")
    else
      echo "MAC_BUNDLE_ID set, but --mac-package-identifier is only valid on macOS. Ignoring." >&2
    fi
  fi

  echo "Packaging with jpackage: $TYPE"
  "$JPACKAGE" "${JPACKAGE_ARGS[@]}"
done

echo "Done: $BUILD_DIR"
