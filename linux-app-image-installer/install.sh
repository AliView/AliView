#!/bin/bash
# AliView Linux Installer
# Compatible with: Ubuntu 20.04+, Debian 11+, Fedora 38+, Arch Linux, openSUSE Leap/Tumbleweed
# Build requirement: jpackage output built on Ubuntu 20.04 (glibc 2.31) for maximum compatibility

set -euo pipefail

# --- Configuration ---
APP_NAME="aliview"
APP_BINARY="AliView"
INSTALL_DIR="/opt/$APP_NAME"
INSTALL_DIR_EXPECTED="/opt/aliview"   # sanity-check anchor — never changes
BIN_LINK="/usr/local/bin/$APP_NAME"
ICON_SRC="splash_128x128.png"
ICON_DEST_NAME="aliview"
ICON_DEST="/usr/share/icons/hicolor/128x128/apps/${ICON_DEST_NAME}.png"
DESKTOP_FILE="/usr/share/applications/aliview.desktop"
APP_VERSION="1.0"
INSTALL_MARKER="$INSTALL_DIR/.installing"
LOG_FILE="$INSTALL_DIR/install.log"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# --- Sanity check: guard against a misconfigured INSTALL_DIR variable
#     before we ever touch the filesystem with rm -rf ---
if [ "$INSTALL_DIR" != "$INSTALL_DIR_EXPECTED" ]; then
    echo -e "${RED}Error:${NC} INSTALL_DIR sanity check failed."
    echo "  Expected: $INSTALL_DIR_EXPECTED"
    echo "  Got:      $INSTALL_DIR"
    echo "Do not modify INSTALL_DIR without also updating INSTALL_DIR_EXPECTED."
    exit 1
fi

# --- Trap: only clean up if our in-progress marker exists,
#     AND the path still matches what we expect ---
cleanup_on_error() {
    local lineno="${1:-unknown}"
    echo ""
    echo -e "${RED}ERROR: Installation failed at line $lineno.${NC}"
    if [ -f "$INSTALL_MARKER" ] && [ "$INSTALL_DIR" = "$INSTALL_DIR_EXPECTED" ]; then
        echo "-> Cleaning up incomplete install at $INSTALL_DIR..."
        rm -rf "$INSTALL_DIR"
    else
        echo "-> Skipping cleanup (marker not found or path mismatch)."
    fi
    exit 1
}
trap 'cleanup_on_error $LINENO' ERR

echo -e "${BLUE}=== AliView Linux Installer ===${NC}"
echo ""

# 1. Root check
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}Error:${NC} Please run as root: sudo ./aliview.install.run"
    exit 1
fi

# 2. Detect binary in the makeself temp dir (this script runs from the extracted
#    package root, so ./bin/ refers to the bundled jpackage output, not /opt).
#    Try the expected name first; fall back to scanning ./bin/ for any executable.
RESOLVED_BINARY=""
if [ -f "./bin/$APP_BINARY" ] && [ -x "./bin/$APP_BINARY" ]; then
    RESOLVED_BINARY="./bin/$APP_BINARY"
else
    echo -e "   ${YELLOW}Warning:${NC} Expected binary ./bin/$APP_BINARY not found — scanning ./bin/..."
    RESOLVED_BINARY=$(find ./bin -maxdepth 1 -type f -executable | head -n1 || true)
    if [ -z "$RESOLVED_BINARY" ]; then
        echo -e "${RED}Error:${NC} No executable found in ./bin/. Package may be corrupted."
        exit 1
    fi
    APP_BINARY=$(basename "$RESOLVED_BINARY")
    echo "   Found binary: $APP_BINARY"
fi

# 3. Detect distro family
DISTRO="unknown"
if [ -f /etc/os-release ]; then
    # shellcheck source=/dev/null
    . /etc/os-release
    case "${ID_LIKE:-} ${ID:-}" in
        *fedora*|*rhel*|*centos*)  DISTRO="rpm-fedora" ;;
        *suse*)                    DISTRO="rpm-suse"   ;;
        *arch*)                    DISTRO="arch"       ;;
        *debian*|*ubuntu*)         DISTRO="debian"     ;;
    esac
fi
echo "-> Detected distro family: $DISTRO"

# 4. Detect SELinux state
SELINUX_ACTIVE=false
if command -v getenforce &> /dev/null; then
    SE_STATE=$(getenforce 2>/dev/null || echo "Disabled")
    if [ "$SE_STATE" = "Enforcing" ] || [ "$SE_STATE" = "Permissive" ]; then
        SELINUX_ACTIVE=true
        echo "-> SELinux detected: $SE_STATE"
    fi
fi

# 5. Cleanup old version
if [ -d "$INSTALL_DIR" ]; then
    echo "-> Removing existing version at $INSTALL_DIR..."
    rm -rf "$INSTALL_DIR"
fi

# 5b. Remove artifacts from the OLD (pre-/opt) install layout, if present.
#     The old installer used /usr/bin, /usr/share/aliview and a capitalized
#     AliView.desktop — none of which collide with the new paths, so they would
#     otherwise survive as a stale launcher and a duplicate menu entry.
echo "-> Checking for a previous (legacy) AliView installation..."

# Old launcher was a plain script in /usr/bin (new one is a symlink in /usr/local/bin)
if [ -f /usr/bin/aliview ] && [ ! -L /usr/bin/aliview ]; then
    echo "   Removing legacy launcher /usr/bin/aliview"
    rm -f /usr/bin/aliview
fi

# Old data dir
if [ -d /usr/share/aliview ]; then
    echo "   Removing legacy data dir /usr/share/aliview"
    rm -rf /usr/share/aliview
fi

# Old system desktop entry (capitalized -> shows as a duplicate menu item)
rm -f /usr/share/applications/AliView.desktop

# Old per-user desktop entry, in the invoking user's home (not root's)
if [ -n "${SUDO_USER:-}" ]; then
    USER_HOME=$(getent passwd "$SUDO_USER" | cut -d: -f6)
    if [ -n "$USER_HOME" ] && [ -f "$USER_HOME/.local/share/applications/AliView.desktop" ]; then
        echo "   Removing legacy menu entry for user $SUDO_USER"
        rm -f "$USER_HOME/.local/share/applications/AliView.desktop"
    fi
fi

# 6. Create install dir and drop in-progress marker so the trap
#    knows it is safe to clean up on failure from this point on
mkdir -p "$INSTALL_DIR"
touch "$INSTALL_MARKER"

# Start log now that the dir exists
echo "AliView install log — $(date)" > "$LOG_FILE"
echo "Distro: $DISTRO | SELinux: $SELINUX_ACTIVE" >> "$LOG_FILE"

# 7. Deploy files
#    Prefer rsync, fall back to tar pipe, fall back to find+cp.
#    All three exclude install.sh from the destination.
echo -e "-> Installing files to ${GREEN}$INSTALL_DIR${NC}..."
if command -v rsync &> /dev/null; then
    echo "   (using rsync)" | tee -a "$LOG_FILE"
    rsync -a --exclude="install.sh" ./ "$INSTALL_DIR/"
elif command -v tar &> /dev/null; then
    echo "   (using tar pipe)" | tee -a "$LOG_FILE"
    tar -c --exclude="./install.sh" . | tar -x -C "$INSTALL_DIR"
else
    echo "   (using find+cp fallback)" | tee -a "$LOG_FILE"
    find . -maxdepth 1 -mindepth 1 ! -name "install.sh" -exec cp -a {} "$INSTALL_DIR/" \;
fi

# Record installed version
echo "$APP_VERSION" > "$INSTALL_DIR/.version"

# 8. Fix ownership and permissions
#    rsync/tar preserve source ownership, which can be a normal user from the
#    build machine. Reset everything to root:root and make the tree world-readable
#    so normal users can actually launch the app.
echo "-> Setting ownership and permissions..."
chown -R root:root "$INSTALL_DIR"
find "$INSTALL_DIR" -type d -exec chmod 755 {} \;
find "$INSTALL_DIR" -type f -perm /111 -exec chmod 755 {} \;
find "$INSTALL_DIR" -type f ! -perm /111 -exec chmod 644 {} \;

# 9. SELinux context fix
if [ "$SELINUX_ACTIVE" = true ]; then
    if command -v restorecon &> /dev/null; then
        echo "-> Applying SELinux file contexts (restorecon)..." | tee -a "$LOG_FILE"
        restorecon -R "$INSTALL_DIR/" 2>/dev/null || \
            echo -e "   ${YELLOW}Warning:${NC} restorecon failed. If AliView won't launch, run: sudo restorecon -R $INSTALL_DIR"
    elif command -v chcon &> /dev/null; then
        echo "-> Applying SELinux file contexts (chcon fallback)..." | tee -a "$LOG_FILE"
        chcon -R -t bin_t "$INSTALL_DIR/bin/" 2>/dev/null || true
        chcon -R -t lib_t "$INSTALL_DIR/lib/" 2>/dev/null || true
    else
        echo -e "   ${YELLOW}Warning:${NC} SELinux active but no labelling tool found." | tee -a "$LOG_FILE"
        echo "   If AliView won't launch, try: sudo restorecon -R $INSTALL_DIR"
    fi
fi

# 10. Create launcher symlink
echo "-> Creating symbolic link at $BIN_LINK..."
mkdir -p "$(dirname "$BIN_LINK")"
ln -sf "$INSTALL_DIR/bin/$APP_BINARY" "$BIN_LINK"

# 11. Install icon via hicolor theme
#     Named icon (not hardcoded path) = correct display in all DEs and file managers
echo "-> Installing application icon..."
mkdir -p /usr/share/icons/hicolor/128x128/apps/
if [ -f "$INSTALL_DIR/$ICON_SRC" ]; then
    cp "$INSTALL_DIR/$ICON_SRC" "$ICON_DEST"
    chmod 644 "$ICON_DEST"
else
    echo -e "   ${YELLOW}Warning:${NC} Icon file not found, skipping icon installation."
fi

# 12. Desktop entry
#     MimeType= here is sufficient for "Open With" associations in all major DEs.
#     We deliberately do NOT install a MIME XML package — many distros already
#     define these biological sequence types, and adding duplicate definitions
#     can silently break other apps' file associations.
echo "-> Registering desktop menu entry..."
mkdir -p /usr/share/applications
cat <<EOF > "$DESKTOP_FILE"
[Desktop Entry]
Version=1.0
Type=Application
Name=AliView
GenericName=Alignment Viewer
Comment=Fast and lightweight alignment viewer and editor
Exec=$BIN_LINK %f
TryExec=$BIN_LINK
Icon=${ICON_DEST_NAME}
Terminal=false
Categories=Science;Biology;Education;
MimeType=application/x-fasta;text/x-fasta;application/x-clustal;application/x-phylip;application/x-nexus;
Keywords=alignment;biology;bioinformatics;fasta;phylip;clustal;nexus;
StartupWMClass=AliView
StartupNotify=true
EOF
chmod 644 "$DESKTOP_FILE"

# 13. Create uninstaller
echo "-> Generating uninstaller..."
cat <<'UNINSTALL_EOF' > "$INSTALL_DIR/uninstall.sh"
#!/bin/bash
set -euo pipefail

# Hardcoded paths — do not use variables for rm -rf targets without a sanity check
INSTALL_DIR="/opt/aliview"
INSTALL_DIR_EXPECTED="/opt/aliview"
BIN_LINK="/usr/local/bin/aliview"
DESKTOP_FILE="/usr/share/applications/aliview.desktop"
ICON_FILE="/usr/share/icons/hicolor/128x128/apps/aliview.png"

if [ "$EUID" -ne 0 ]; then
    echo "Please run with sudo: sudo /opt/aliview/uninstall.sh"
    exit 1
fi

# Sanity check before rm -rf
if [ "$INSTALL_DIR" != "$INSTALL_DIR_EXPECTED" ]; then
    echo "Error: INSTALL_DIR sanity check failed. Aborting to prevent data loss."
    echo "  Expected: $INSTALL_DIR_EXPECTED"
    echo "  Got:      $INSTALL_DIR"
    exit 1
fi

echo "Removing AliView..."
rm -f "$BIN_LINK"
rm -f "$DESKTOP_FILE"
rm -f "$ICON_FILE"

# Refresh databases BEFORE rm -rf INSTALL_DIR — do all meaningful work first.
# bash reads the whole script into memory so rm -rf won't cut execution short,
# but this ordering is still correct practice.
echo "-> Refreshing system databases..."
update-desktop-database /usr/share/applications > /dev/null 2>&1 || true

# Icon cache — GTK desktops (GNOME, Xfce, LXDE, Cinnamon)
if command -v gtk-update-icon-cache &> /dev/null; then
    gtk-update-icon-cache /usr/share/icons/hicolor/ -f -t > /dev/null 2>&1 || true
fi
# Icon cache — KDE Plasma 5
if command -v kbuildsycoca5 &> /dev/null; then
    kbuildsycoca5 --noincremental > /dev/null 2>&1 || true
fi
# Icon cache — KDE Plasma 6
if command -v kbuildsycoca6 &> /dev/null; then
    kbuildsycoca6 --noincremental > /dev/null 2>&1 || true
fi

# Final act — remove the install directory (also removes this running script)
rm -rf "$INSTALL_DIR"

echo "AliView has been successfully removed."
UNINSTALL_EOF
chmod 755 "$INSTALL_DIR/uninstall.sh"

# 14. Remove in-progress marker — installation succeeded
rm -f "$INSTALL_MARKER"

# 15. Refresh system databases
echo "-> Refreshing system databases..."
update-desktop-database /usr/share/applications > /dev/null 2>&1 || true

# Icon cache — GTK desktops (GNOME, Xfce, LXDE, Cinnamon)
if command -v gtk-update-icon-cache &> /dev/null; then
    gtk-update-icon-cache /usr/share/icons/hicolor/ -f -t > /dev/null 2>&1 || true
fi
# Icon cache — KDE Plasma 5
if command -v kbuildsycoca5 &> /dev/null; then
    kbuildsycoca5 --noincremental > /dev/null 2>&1 || true
fi
# Icon cache — KDE Plasma 6
if command -v kbuildsycoca6 &> /dev/null; then
    kbuildsycoca6 --noincremental > /dev/null 2>&1 || true
fi

echo ""
echo -e "${GREEN}=== Installation Complete! ===${NC}"
echo "Launch AliView from your Applications menu or by typing 'aliview' in a terminal."
echo "To uninstall, run: sudo $INSTALL_DIR/uninstall.sh"
echo "Install log:       $LOG_FILE"
