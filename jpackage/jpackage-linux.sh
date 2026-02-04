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
APP_VERSION="$(python3 - <<'PY'
import xml.etree.ElementTree as ET
tree = ET.parse("pom.xml")
root = tree.getroot()
ns = {"m": root.tag.split("}")[0].strip("{")}
print(root.find("m:version", ns).text)
PY
)"

patch_version=$(git rev-list --count HEAD)
patch_version=$((patch_version % 256))
APP_VERSION="${APP_VERSION}.${patch_version}"
echo "APP_VERSION=$APP_VERSION"
TYPES="${JPACKAGE_TYPES:-deb,rpm}"

rm -rf "target/jpackage-linux"
mkdir -p "target/jpackage-linux/input"


echo "Building fat jar..."
mvn -DskipTests package

if [[ ! -f "target/aliview.jar" ]]; then
  echo "Expected jar not found: target/aliview.jar" >&2
  exit 1
fi

cp "target/aliview.jar" "target/jpackage-linux/input/"

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

for TYPE in ${TYPES//,/ }; do
  JPACKAGE_ARGS=(
    --type "$TYPE"
    --name "$APP_NAME"
    --app-version "$APP_VERSION"
    --input "target/jpackage-linux/input"
    --main-jar "aliview.jar"
    --main-class "aliview.AliView"
    --icon "src/main/resources/img/alignment_ico_128x128.png"
    --runtime-image "target/jpackage-linux/runtime"
    --file-associations "jpackage/file-associations/nexus.properties"
    --file-associations "jpackage/file-associations/nex.properties"
    --file-associations "jpackage/file-associations/fasta.properties"
    --file-associations "jpackage/file-associations/fas.properties"
    --file-associations "jpackage/file-associations/fa.properties"
    --file-associations "jpackage/file-associations/afa.properties"
    --file-associations "jpackage/file-associations/phylip.properties"
    --file-associations "jpackage/file-associations/phy.properties"
    --file-associations "jpackage/file-associations/aln.properties"
    --file-associations "jpackage/file-associations/clustal.properties"
    --file-associations "jpackage/file-associations/clustalw.properties"
    --file-associations "jpackage/file-associations/clustalx.properties"
    --file-associations "jpackage/file-associations/msf.properties"
    --dest "target/jpackage-linux"
    --java-options "-Xmx1024m"
    --java-options "-Xms128m"
  )

  if [[ "$TYPE" != "app-image" ]]; then
    JPACKAGE_ARGS+=(--vendor "Systematic Biology, Uppsala University")
    JPACKAGE_ARGS+=(--about-url "https://www.ormbunkar.se")
  fi

  if [[ -n "${LINUX_PACKAGE_NAME:-}" ]]; then
    JPACKAGE_ARGS+=(--linux-package-name "$LINUX_PACKAGE_NAME")
  fi
  if [[ -n "${LINUX_APP_CATEGORY:-}" ]]; then
    JPACKAGE_ARGS+=(--linux-app-category "$LINUX_APP_CATEGORY")
  fi
  if [[ -n "${LINUX_MENU_GROUP:-}" ]]; then
    JPACKAGE_ARGS+=(--linux-menu-group "$LINUX_MENU_GROUP")
  fi

  echo "Packaging with jpackage: $TYPE"
  "$JPACKAGE" "${JPACKAGE_ARGS[@]}"
done

echo "Done: target/jpackage-linux"
