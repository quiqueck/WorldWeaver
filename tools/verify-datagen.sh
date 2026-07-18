#!/usr/bin/env bash
#
# Datagen golden-file regression guard.
#
# Re-runs the committed data generation and fails if the output differs from what is checked in.
# This locks the "resulting datagen stays unaltered" half of Concern 2 (traits): if a change to a
# trait, block property, loot table, model or tag silently alters generated output, this catches it.
#
# Usage:
#   tools/verify-datagen.sh                # regenerate testmod datagen for all modules and diff
#   tools/verify-datagen.sh --main         # also regenerate main (library) datagen
#
# CI runs this and treats a non-empty git diff of the generated trees as a failure.
set -euo pipefail

cd "$(dirname "$0")/.."

if [ -z "${JAVA_HOME:-}" ] && command -v /usr/libexec/java_home >/dev/null 2>&1; then
    JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
    export JAVA_HOME
fi

INCLUDE_MAIN=false
[ "${1:-}" = "--main" ] && INCLUDE_MAIN=true

capitalize() { printf '%s' "$(printf '%s' "$1" | cut -c1 | tr '[:lower:]' '[:upper:]')$(printf '%s' "$1" | cut -c2-)"; }

TASKS=()
GEN_DIRS=()
for module in wover-*-api; do
    [ -d "$module" ] || continue
    cap="$(capitalize "$module")"
    if [ -d "$module/src/testmodDatagen" ]; then
        TASKS+=(":$module:run$cap-testmodDatagen")
        GEN_DIRS+=("$module/src/testmod/generated")
    fi
    if [ "$INCLUDE_MAIN" = true ] && [ -d "$module/src/datagen" ]; then
        TASKS+=(":$module:run$cap-datagen")
        GEN_DIRS+=("$module/src/main/generated")
    fi
done

if [ "${#TASKS[@]}" -eq 0 ]; then
    echo "No datagen tasks found."
    exit 0
fi

echo "Regenerating datagen via ${#TASKS[@]} task(s)..."
./gradlew "${TASKS[@]}" --console=plain

echo "Checking for drift against committed golden files..."
# `git diff` covers modified/deleted TRACKED golden files; `git status --porcelain` additionally
# surfaces NEW untracked generated files (a rule/model/loot-table that regeneration would add but
# that was never committed). Both are drift. Ignore the datagen `.cache` bookkeeping directory.
MODIFIED="$(git diff --name-only -- "${GEN_DIRS[@]}" | grep -v '/\.cache/' || true)"
UNTRACKED="$(git ls-files --others --exclude-standard -- "${GEN_DIRS[@]}" | grep -v '/\.cache/' || true)"

if [ -z "$MODIFIED" ] && [ -z "$UNTRACKED" ]; then
    echo "OK: datagen output matches the committed golden files."
    exit 0
else
    echo "DATAGEN DRIFT DETECTED - generated output differs from what is committed:" >&2
    [ -n "$MODIFIED" ] && { echo "Changed/removed committed files:" >&2; echo "$MODIFIED" | sed 's/^/  /' >&2; }
    [ -n "$UNTRACKED" ] && { echo "New, uncommitted generated files:" >&2; echo "$UNTRACKED" | sed 's/^/  /' >&2; }
    echo >&2
    echo "If this change is intentional, re-run the datagen and commit the updated files." >&2
    exit 1
fi
