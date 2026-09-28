#!/bin/bash
# Download the latest CI-built installers (Linux .run, macOS DMGs, Windows MSI)
# into a single directory, ready for a release.
#
# By default it grabs the most recent SUCCESSFUL run of each workflow ON MAIN.
# Restricting to a branch matters: without it the "latest successful run" of a
# workflow can belong to a dependabot (or any other) branch, which would mix
# artifacts built from unmerged code into a release.
#
#     ./download_latest_artifacts.sh                 # latest successful on main
#     ./download_latest_artifacts.sh v1.33           # pin to a tag/branch
#     COMMIT=5665f47 ./download_latest_artifacts.sh  # pin to one exact commit
#
# COMMIT is the safest for a release: it guarantees every artifact comes from
# the same commit instead of from whatever each workflow happened to build last.
#
# Output dir defaults to ./dist (override with DEST=/some/dir).
# Requires: gh (GitHub CLI), authenticated (gh auth login).
set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

REF="${1:-main}"
COMMIT="${COMMIT:-}"
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

# Clear previous copies of only the artifact families THIS script downloads (all
# versions, so a version bump doesn't leave stale files behind). Anything else in
# the directory — e.g. a locally-built AliView-*-macOS-no-java.zip — is left
# untouched, since the script only owns what it fetches.
rm -f "$DEST"/AliView-*-linux-x86_64.run \
      "$DEST"/AliView-*-macOS-*.dmg \
      "$DEST"/AliView-*-Windows-x64.msi \
      "$DEST"/AliView-*-Windows-x64.exe

echo "=> Downloading artifacts into: $DEST"
if [ -n "$COMMIT" ]; then
    echo "   (pinned to commit: $COMMIT)"
else
    echo "   (ref: $REF)"
fi

for WF in "${WORKFLOWS[@]}"; do
    # Find the newest successful run for this workflow. When COMMIT is given we
    # look through more runs and take the one built from that exact commit,
    # regardless of which branch/tag triggered it; otherwise the newest on $REF.
    if [ -n "$COMMIT" ]; then
        RUN_ARGS=(--workflow="$WF" --status=success --limit 30 --json databaseId,headSha,displayTitle)
    else
        RUN_ARGS=(--workflow="$WF" --status=success --limit 1 --branch "$REF" --json databaseId,headSha,displayTitle)
    fi

    RUN_JSON="$(gh run list "${RUN_ARGS[@]}")"
    RUN_ID="$(echo "$RUN_JSON" | COMMIT="$COMMIT" python3 -c '
import sys, json, os
runs = json.load(sys.stdin)
want = os.environ.get("COMMIT", "")
if want:
    runs = [r for r in runs if r["headSha"].startswith(want)]
print(runs[0]["databaseId"] if runs else "")')"

    if [ -z "$RUN_ID" ]; then
        echo "   !! $WF: no successful run found for ${COMMIT:-$REF} — skipping"
        continue
    fi
    SHA="$(gh run view "$RUN_ID" --json headSha -q '.headSha[0:7]')"
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
