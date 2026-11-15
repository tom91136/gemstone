#!/bin/sh
# Builds one Python environment into the interpreter cache. Runs in the route's container image so the wheels match the
# artifact's libc; the interpreter itself is the pinned standalone build in the cache.
set -eu

cache=$1
environment=$2
key=$3
requirements=$4
python_directory=$5
offline=$6

interpreter="$cache/$python_directory/bin/python3"
if [ ! -x "$interpreter" ]; then
  echo "missing pinned interpreter: $interpreter" >&2
  exit 1
fi
target="$cache/$environment-$key"
wheels="$cache/wheels-$key"

if [ ! -f "$wheels/.complete" ]; then
  if [ "$offline" = "1" ]; then
    echo "offline environment build has no wheel cache: $wheels" >&2
    exit 1
  fi
  rm -rf "$wheels"
  mkdir -p "$wheels"
  PIP_DISABLE_PIP_VERSION_CHECK=1 "$interpreter" -m pip download --only-binary=:all: --require-hashes \
    --no-cache-dir --dest "$wheels" -r "$requirements"
  touch "$wheels/.complete"
fi

rm -rf "$target"
mkdir -p "$target/site-packages"
PIP_DISABLE_PIP_VERSION_CHECK=1 "$interpreter" -m pip install --no-index --find-links "$wheels" --no-deps \
  --no-compile --target "$target/site-packages" -r "$requirements"

ln -s "../$python_directory/bin/python3" "$target/python"
mkdir -p "$target/cuda/nvvm/lib64" "$target/cuda/nvvm/libdevice" "$target/cuda/lib64"
ln -s ../../../site-packages/nvidia/cu13/lib/libnvvm.so.4 "$target/cuda/nvvm/lib64/libnvvm.so.4"
ln -s libnvvm.so.4 "$target/cuda/nvvm/lib64/libnvvm.so"
ln -s ../../../site-packages/nvidia/cu13/nvvm/libdevice/libdevice.10.bc "$target/cuda/nvvm/libdevice/libdevice.10.bc"
ln -s ../../site-packages/nvidia/cu13/lib/libnvvm.so.4 "$target/cuda/lib64/libnvvm.so.4"

printf '%s\n' "$key" >"$target/.key"
touch "$target/.complete"
du -sh "$target"
