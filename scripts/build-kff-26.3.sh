#!/usr/bin/env bash
set -euo pipefail

# Build the 6.3.0 release sources with a 26.3-only Minecraft range.
# Java 21 is required by the upstream build; JAVA21_HOME takes precedence.
repo_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
upstream_commit=9cd48346a4a6e30c0c75df049d4f6fa1b66cb975
archive_sha=7069d7cd387127fec4d57971744520586b6b27a314cb40effd8302dd33bb3f46
build_root="$repo_root/build/kff-26.3"
patch_file="$repo_root/scripts/patches/kff-26.3.patch"
patch_sha=$(sha256sum "$patch_file" | cut -d ' ' -f 1)
source_dir="$build_root/$upstream_commit-$patch_sha"
archive="$build_root/upstream.tar.gz"

if [[ -n ${JAVA21_HOME:-} ]]; then
  export JAVA_HOME="$JAVA21_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
fi
mkdir -p "$build_root"
if [[ ! -f "$archive" ]]; then
  curl --fail --location --retry 3 \
    "https://codeload.github.com/thedarkcolour/KotlinForForge/tar.gz/$upstream_commit" \
    --output "$archive"
fi
printf '%s  %s\n' "$archive_sha" "$archive" | sha256sum --check --status
if [[ ! -f "$source_dir/.patched" ]]; then
  mkdir -p "$source_dir"
  tar -xzf "$archive" --strip-components=1 -C "$source_dir"
  patch --directory "$source_dir" -p1 < "$patch_file"
  touch "$source_dir/.patched"
fi

cd "$source_dir"
bash gradlew \
  publishKfflang_neoforgePublicationToMavenLocal \
  publishKfflib_neoforgePublicationToMavenLocal \
  publishKffmod_neoforgePublicationToMavenLocal \
  publishKotlinforforge_neoforgePublicationToMavenLocal \
  -Pkff_version=6.3.0+26.3.dropconfirm.1 \
  -Pmin_mc_version=26.3 -Punsupported_mc_version=26.4 \
  --no-daemon --max-workers=2 --console=plain "$@"
tar --exclude='./.gradle' --exclude='./.kotlin' --exclude='*/build' --exclude='./.patched' \
  -czf "$build_root/kff-26.3-source.tar.gz" .
