#!/bin/bash
# Download the latest CI-built installers (Linux .run, macOS DMGs, Windows MSI)
# into a single directory, ready for a release.
#
# By default it grabs the most recent SUCCESSFUL run of each workflow on the
# default branch. Pass a git ref (branch or tag) to pin to that ref instead:
#     ./download_latest_artifacts.sh              # latest successful on main
#     ./download_latest_artifacts.sh v1.31        # latest successful for tag
#
# Output dir defaults to ./dist (override with DEST=/some/dir).
# Requires: gh (GitHub CLI), authenticated (gh auth login).
set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

REF="${1:-}"
DEST="${DEST:-dist}"

# workflow file -> the artifact name(s) it produces
WORKFLOWS=(
    "build-linux-installer.yml"
    "build-macos-dmg.yml"
    "build-windows-msi.yml"
)

if ! command -v gh &> /dev/null; then
    echo "Error: GitHub CLI (gh) is not installed." >&2
    exit 1
fi
if ! gh auth status &> /dev/null; then
    echo "Error: not authenticated. Run: gh auth login" >&2
    exit 1
fi

mkdir -p "$DEST"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

# Clear any previously-downloaded installers so the result is a clean set of the
# current build (stale versions would otherwise pile up, since differing version
# numbers don't overwrite each other).
rm -f "$DEST"/AliView-*

echo "=> Downloading artifacts into: $DEST"
[ -n "$REF" ] && echo "   (pinned to ref: $REF)"

for WF in "${WORKFLOWS[@]}"; do
    # Find the latest successful run for this workflow (optionally on $REF).
    RUN_ARGS=(--workflow="$WF" --status=success --limit 1 --json databaseId,headSha,displayTitle)
    [ -n "$REF" ] && RUN_ARGS+=(--branch "$REF")

    RUN_JSON="$(gh run list "${RUN_ARGS[@]}")"
    RUN_ID="$(echo "$RUN_JSON" | python3 -c 'import sys,json; r=json.load(sys.stdin); print(r[0]["databaseId"] if r else "")')"

    if [ -z "$RUN_ID" ]; then
        echo "   !! $WF: no successful run found${REF:+ for ref $REF} — skipping"
        continue
    fi
    SHA="$(echo "$RUN_JSON" | python3 -c 'import sys,json; print(json.load(sys.stdin)[0]["headSha"][:7])')"
    echo "   $WF: run $RUN_ID (commit $SHA)"

    # Download every artifact of this run into a scratch dir, then flatten the
    # actual files (gh nests each artifact in its own subdirectory).
    RUN_TMP="$TMP/$RUN_ID"
    mkdir -p "$RUN_TMP"
    gh run download "$RUN_ID" -D "$RUN_TMP" &> /dev/null || {
        echo "   !! failed to download artifacts for run $RUN_ID"
        continue
    }
    find "$RUN_TMP" -type f -exec mv -f {} "$DEST/" \;
done

echo ""
echo "=> Collected installers:"
if ls -1 "$DEST"/AliView-* &> /dev/null; then
    ( cd "$DEST" && ls -1 AliView-* | sed 's/^/   /' )
else
    echo "   (none found)"
fi
