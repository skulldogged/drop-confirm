#!/usr/bin/env bash
set -euo pipefail

# Temporary 26.3 dependency build until YACL publishes the SDL input fixes.
# Upstream commit includes PR #358; the patch includes PR #339 plus dropdown fixes.
# Use Java 25; JAVA21_HOME can supply NeoForge's Java 21 mapping helper.
repo_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
upstream_commit=10d86ff956f41983c970d82bbd82e426d9cebaf6
archive_sha=130dc22e197a08668648dcffd3e075e331696eecc6b4583f4c16e3d8f4e7c0ae
patch_file="$repo_root/scripts/patches/yacl-26.3.patch"
build_root="$repo_root/build/yacl-26.3"
patch_sha=$(sha256sum "$patch_file" | cut -d ' ' -f 1)
source_dir="$build_root/$patch_sha"
archive="$build_root/upstream.tar.gz"

loader=${1:-fabric}
case "$loader" in
  fabric|neoforge) ;;
  *) printf 'Usage: %s [fabric|neoforge]\n' "$0" >&2; exit 2 ;;
esac
if (( $# > 0 )); then shift; fi
if [[ "$loader" == neoforge ]]; then
  "$repo_root/scripts/build-kff-26.3.sh"
fi

mkdir -p "$build_root"
if [[ ! -f "$archive" ]]; then
  curl --fail --location --retry 3 \
    "https://codeload.github.com/isXander/YetAnotherConfigLib/tar.gz/$upstream_commit" \
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
bash gradlew ":26.3-$loader:publishToMavenLocal" \
  --configure-on-demand --console=plain --no-daemon --max-workers=2 \
  -Dorg.gradle.jvmargs=-Xmx1G -x javadoc \
  -Porg.gradle.java.installations.fromEnv=JAVA_HOME,JAVA21_HOME "$@"
tar --exclude='./.gradle' --exclude='./.kotlin' --exclude='*/build' \
  --exclude='*/run' --exclude='./.patched' \
  -czf "$build_root/yacl-26.3-source.tar.gz" .
