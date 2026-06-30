#!/bin/bash
# AliView Package Builder
# Run this on Ubuntu 20.04 (or in a Ubuntu 20.04 Docker container) for maximum
# glibc compatibility across Ubuntu, Debian, Fedora, Arch, and openSUSE.
#
# Quick Docker build:
#   docker run --rm -v $(pwd):/build ubuntu:20.04 bash -c \
#     "apt-get update && apt-get install -y openjdk-17-jdk makeself && cd /build && ./create-package.sh"
#
# Requires: makeself (apt install makeself)

set -euo pipefail
trap 'echo "ERROR: Build failed at line $LINENO."; exit 1' ERR

# --- Configuration ---
APP_IMAGE_DIR="/home/anders/projekt/maven/AliView/target/jpackage-linux/AliView"   # <-- update this path
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

# 4. Warn if not building on Ubuntu 20.04
if [ -f /etc/os-release ]; then
    . /etc/os-release
    MAJOR_VERSION="${VERSION_ID%%.*}"
    if [ "${ID:-}" != "ubuntu" ] || [ "${MAJOR_VERSION:-99}" -gt 20 ]; then
        echo -e "${YELLOW}WARNING:${NC} For maximum Linux compatibility, build on Ubuntu 20.04 (glibc 2.31)."
        echo "         Current OS: ${PRETTY_NAME:-unknown}"
        echo "         Binaries built on newer systems will not run on older distros."
        echo "         Press Ctrl+C to cancel, or wait 5 seconds to continue anyway..."
        sleep 5
    fi
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
