#!/usr/bin/env bash
set -euo pipefail
script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
source "$script_directory/../../tools/container-common.sh"
conformance_fixture_lock "$@"
[[ $# == 1 ]] || { echo 'usage: container-build.sh <output-directory>' >&2; exit 2; }
repository_directory="$(cd -- "$script_directory/../../.." && pwd -P)"
conformance_artifact_begin "$1"
archive_directory="$repository_directory/target/cts/opencl/archives"
python3 "$script_directory/prepare.py" fetch "$archive_directory"
backend=opencl
conformance_container_init
conformance_build_image
# BuildKit FROM cannot reliably resolve a bare image ID (moby/buildkit#2204).
# A tag derived from that ID cannot collide with a different concurrent build.
base_builder="localhost/gemstone-cts-base:${builder_image#sha256:}"
"$engine" tag "$builder_image" "$base_builder"
builder_image="localhost/gemstone-opencl-cts-builder:representative"
pull_argument=--pull=false
[[ "$engine" == podman ]] && pull_argument=--pull=never
rm -f -- "$builder_identity_file"
"$engine" build --iidfile "$builder_identity_file" "$pull_argument" "${container_cgroup_args[@]}" \
  --file "$script_directory/Containerfile" --build-arg "BASE_IMAGE=$base_builder" \
  --build-arg "BUILDER_UID=$builder_uid" --build-arg "BUILDER_GID=$builder_gid" \
  --tag "$builder_image" "$script_directory"
conformance_resolve_image
builder_identity="$builder_image"
"$engine" run --rm -i --name "$container_name" \
  "${container_userns_args[@]}" "${container_cgroup_args[@]}" \
  --user "$builder_uid:$builder_gid" --security-opt label=disable \
  --network "$container_network" "${container_environment_args[@]}" \
  --mount "type=bind,src=$script_directory,dst=/recipe,ro" \
  --mount "type=bind,src=$archive_directory,dst=/archives,ro" \
  --mount "type=bind,src=$repository_directory/conformance/tools,dst=/gemstone-tools,ro" \
  --mount "type=bind,src=$output_directory,dst=/out,rw" \
  "$builder_image" /bin/bash -s -- "$image" "$builder_identity" <<'BUILD'
set -euo pipefail
python3 /recipe/prepare.py extract /archives /work/source
for patch in /recipe/patches/*.patch; do
  [[ -e "$patch" ]] || continue
  git -C /work/source/cts apply "$patch"
done
cmake -S /work/source/cts -B /work/build -G Ninja \
  -DCMAKE_BUILD_TYPE=Release -DCL_INCLUDE_DIR=/work/source/opencl-headers \
  -DSPIRV_INCLUDE_DIR=/work/source/spirv-headers -DCL_LIB_DIR=/usr/lib/x86_64-linux-gnu \
  -DOPENCL_LIBRARIES=OpenCL
mapfile -t categories < <(python3 -c 'import json; print("\n".join(json.load(open("/recipe/pins.json"))["categories"]))')
targets=()
for category in "${categories[@]}"; do targets+=("test_$category"); done
cmake --build /work/build --parallel "$CMAKE_BUILD_PARALLEL_LEVEL" --target "${targets[@]}"
mkdir -p /out/bin /out/licenses /out/provenance
for category in "${categories[@]}"; do
  cp "/work/build/test_conformance/$category/test_$category" /out/bin/
done
cp -a /work/build/test_conformance/compiler/includeTestDirectory /out/bin/
cp -a /work/build/test_conformance/compiler/secondIncludeTestDirectory /out/bin/
cp -a /work/build/test_conformance/compiler/spirv_bin /out/bin/
cp /work/source/cts/LICENSE.txt /out/licenses/OpenCL-CTS.txt
cp /work/source/opencl-headers/LICENSE /out/licenses/OpenCL-Headers.txt
cp /work/source/spirv-headers/LICENSE /out/licenses/SPIRV-Headers.txt
cp /usr/share/doc/ocl-icd-libopencl1/copyright /out/licenses/ocl-icd.txt
source /gemstone-tools/runtime-bundle.sh
bundle_begin /out 1
bundle_resolve
bundle_relocate /out
python3 /recipe/inventory.py /work/source/cts /out "$1" "$2"
BUILD

conformance_artifact_commit
