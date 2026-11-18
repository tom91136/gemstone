#!/bin/sh
# Builds one Julia depot into the interpreter cache; packages and their artifacts
# are fetched here only, and the checked-in manifest is the lock file.
set -eu

cache=$1
environment=$2
key=$3
project=$4
julia_directory=$5
offline=$6
warmup=$7

julia="$cache/$julia_directory/bin/julia"
if [ ! -x "$julia" ]; then
  echo "missing pinned interpreter: $julia" >&2
  exit 1
fi
target="$cache/$environment-$key"
if [ -f "$target/.complete" ]; then
  exit 0
fi
rm -rf "$target"
mkdir -p "$target/environment"
export JULIA_DEPOT_PATH="$target"
export JULIA_PKG_OFFLINE=false
if [ "$offline" = "1" ]; then
  export JULIA_PKG_OFFLINE=true
fi
export JULIA_PKG_PRECOMPILE_AUTO=1

for name in babelstream tests; do
  mkdir -p "$target/environment/$name"
  cp "$project/$name/Project.toml" "$project/$name/Manifest.toml" "$target/environment/$name/"
  if [ -f "$project/LocalPreferences.toml" ]; then
    cp "$project/LocalPreferences.toml" "$target/environment/$name/"
  fi
  "$julia" --startup-file=no --project="$target/environment/$name" -e 'using Pkg; Pkg.instantiate(); Pkg.precompile()'
done
"$julia" --startup-file=no --project="$target/environment/babelstream" "$warmup"

printf '%s\n' "$key" >"$target/.key"
touch "$target/.complete"
du -sh "$target"
