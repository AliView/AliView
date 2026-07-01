#!/bin/bash
# AliView Package Builder
#
# This only wraps the existing jpackage app-image (bundled JRE + launcher) into
# a self-extracting installer, so the BUILD OS does not affect compatibility:
# nothing native is compiled here. The glibc floor of the final bundle is
# inherited entirely from the JDK that produced the app-image (jlink copies the
# runtime .so files and jpackage copies a prebuilt launcher). For wide Linux
# compatibility, build the app-image with a low-glibc JDK such as Eclipse
# Temurin (x64 floor ~glibc 2.17), NOT a distro-packaged JDK. Verify the floor
# with:  objdump -T <bundle>/lib/runtime/lib/server/libjvm.so | grep GLIBC
#
# Requires: makeself (apt install makeself)

set -euo pipefail
trap 'echo "ERROR: Build failed at line $LINENO."; exit 1' ERR

# Resolve to this script's own directory so install.sh, the icon, and the
# output archive are found regardless of the caller's working directory.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# --- Configuration ---
# APP_IMAGE_DIR defaults to the jpackage output relative to the repo root, but
# can be overridden via the environment (e.g. in CI).
APP_IMAGE_DIR="${APP_IMAGE_DIR:-$SCRIPT_DIR/../target/jpackage-linux/AliView}"
PACKAGE_NAME="aliview.install.run"
ICON_SRC="splash_128x128.png"                       # <-- must be in current directory
INSTALL_SCRIPT="install.sh"                          # <-- must be in current directory

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "=== AliView Package Builder ==="
echo ""

# 1. Verify required tools
if ! command -v makeself &> /dev/null; then
    echo -e "${RED}Error:${NC} makeself not found. Install it with: sudo apt install makeself"
    exit 1
fi

# 2. Verify required source files and directories exist
if [ ! -d "$APP_IMAGE_DIR" ]; then
    echo -e "${RED}Error:${NC} jpackage output not found at: $APP_IMAGE_DIR"
    echo "Please update APP_IMAGE_DIR at the top of this script."
    exit 1
fi

if [ ! -f "$INSTALL_SCRIPT" ]; then
    echo -e "${RED}Error:${NC} $INSTALL_SCRIPT not found in current directory."
    exit 1
fi

if [ ! -f "$ICON_SRC" ]; then
    echo -e "${RED}Error:${NC} $ICON_SRC not found in current directory."
    exit 1
fi

# 3. Verify the jpackage binary — explicit name first, then scan
APP_BINARY="AliView"
if [ ! -f "$APP_IMAGE_DIR/bin/$APP_BINARY" ]; then
    echo -e "   ${YELLOW}Warning:${NC} Expected binary $APP_IMAGE_DIR/bin/$APP_BINARY not found, scanning..."
    FOUND=$(find "$APP_IMAGE_DIR/bin" -maxdepth 1 -type f -executable | head -n1 || true)
    if [ -z "$FOUND" ]; then
        echo -e "${RED}Error:${NC} No executable found in $APP_IMAGE_DIR/bin/. Check your jpackage output."
        exit 1
    fi
    APP_BINARY=$(basename "$FOUND")
    echo "   Found binary: $APP_BINARY"
fi

# 4. Report the glibc floor of the app-image being packaged.
#    The build OS is irrelevant (nothing native is compiled here); the floor is
#    inherited from the JDK that produced the app-image. This is informational —
#    the CI workflow enforces a hard threshold separately.
LIBJVM="$APP_IMAGE_DIR/lib/runtime/lib/server/libjvm.so"
if command -v objdump &> /dev/null && [ -f "$LIBJVM" ]; then
    GLIBC_FLOOR=$(objdump -T "$APP_IMAGE_DIR/bin/$APP_BINARY" "$LIBJVM" 2>/dev/null \
        | grep -oE 'GLIBC_[0-9.]+' | sort -uV | tail -1)
    echo -e "-> App-image glibc floor: ${GREEN}${GLIBC_FLOOR:-unknown}${NC}"
    echo "   (a low floor needs a low-glibc JDK such as Temurin, not a distro JDK)"
fi

# 5. Stage installer files into the app-image folder
echo "-> Staging installer files..."
cp "$INSTALL_SCRIPT" "$APP_IMAGE_DIR/"
cp "$ICON_SRC"       "$APP_IMAGE_DIR/"
chmod +x "$APP_IMAGE_DIR/$INSTALL_SCRIPT"

# 6. Create the self-extracting archive
echo "-> Building self-extracting archive: $PACKAGE_NAME"
makeself \
    --nox11 \
    --nowait \
    --sha256 \
    --xz \
    "$APP_IMAGE_DIR" \
    "$PACKAGE_NAME" \
    "AliView Desktop Installer" \
    ./$INSTALL_SCRIPT

# 7. Verify the output was created
if [ ! -f "$PACKAGE_NAME" ]; then
    echo -e "${RED}Error:${NC} Package file was not created."
    exit 1
fi

PACKAGE_SIZE=$(du -sh "$PACKAGE_NAME" | cut -f1)
PACKAGE_SHA=$(sha256sum "$PACKAGE_NAME" | cut -d' ' -f1)

echo ""
echo -e "${GREEN}=== Build Complete! ===${NC}"
echo "Package : $PACKAGE_NAME"
echo "Size    : $PACKAGE_SIZE"
echo "SHA256  : $PACKAGE_SHA"
echo ""
echo "To install on any Linux desktop:"
echo "  chmod +x $PACKAGE_NAME && sudo ./$PACKAGE_NAME"
