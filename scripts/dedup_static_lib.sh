#!/bin/bash
#
# Removes duplicate .o members from a static library (.a file).
# Xcode/SPM builds can produce archives with identical duplicate .o entries,
# which cause "duplicate symbol" linker errors for consumers.
# Supports both thin (single-arch) and fat (universal) archives.
#
# Usage: dedup_static_lib.sh <path_to.a>

set -euo pipefail

LIB="$1"

if [ ! -f "$LIB" ]; then
    echo "Error: file not found: $LIB"
    exit 1
fi

# Dedup a single thin archive in-place
dedup_thin() {
    local lib="$1"
    local duplicates
    duplicates=$(ar t "$lib" | sort | uniq -d)

    if [ -z "$duplicates" ]; then
        echo "  No duplicate members in $(basename "$lib")"
        return
    fi

    echo "  Duplicate members found in $(basename "$lib"):"
    echo "$duplicates" | sed 's/^/    /'

    local tmpdir
    tmpdir=$(mktemp -d)

    # ar x overwrites files with the same name, keeping one copy of each identical duplicate
    (cd "$tmpdir" && ar x "$lib")

    rm "$lib"
    (cd "$tmpdir" && ar rcs "$lib" *.o)
    ranlib "$lib"

    rm -rf "$tmpdir"
    echo "  Dedup complete for $(basename "$lib")"
}

FILE_TYPE=$(file -b "$LIB")

if echo "$FILE_TYPE" | grep -q "universal"; then
    # Fat binary: split into thin archives, dedup each, recombine
    echo "Fat archive detected: $(basename "$LIB")"
    ARCHS=$(lipo -archs "$LIB")
    echo "Architectures: $ARCHS"

    FAT_TMPDIR=$(mktemp -d)
    trap 'rm -rf "$FAT_TMPDIR"' EXIT

    for arch in $ARCHS; do
        local_lib="$FAT_TMPDIR/libAdaptySwiftBridge-$arch.a"
        lipo -thin "$arch" "$LIB" -output "$local_lib"
        dedup_thin "$local_lib"
    done

    # Recombine thin archives into fat binary
    lipo -create "$FAT_TMPDIR"/libAdaptySwiftBridge-*.a -output "$LIB"
    echo "Recombined fat archive: $(basename "$LIB")"
else
    # Thin archive: dedup directly
    echo "Thin archive: $(basename "$LIB")"
    dedup_thin "$LIB"
fi
