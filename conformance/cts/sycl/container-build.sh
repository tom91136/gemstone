#!/usr/bin/env bash
set -euo pipefail
script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
root="$(cd -- "$script_directory/../../.." && pwd -P)"
source "$root/conformance/tools/container-common.sh"
conformance_fixture_lock "$@"
[[ $# == 1 ]] || { echo 'usage: container-build.sh <output-dir>' >&2; exit 2; }
conformance_artifact_begin "$1"
revision="$(python3 -c 'import json,sys;print(json.load(open(sys.argv[1]))["revision"])' "$script_directory/pin.json")"
source_directory="${GEMSTONE_SYCL_CTS_SOURCE:-$root/target/cts-sources/sycl-$revision}"
if [[ ! -e "$source_directory/.git" ]]; then
  git init "$source_directory"
  git -C "$source_directory" remote add origin https://github.com/KhronosGroup/SYCL-CTS.git
fi
if ! git -C "$source_directory" rev-parse --verify HEAD >/dev/null 2>&1; then
  git -C "$source_directory" remote get-url origin >/dev/null 2>&1 || \
    git -C "$source_directory" remote add origin https://github.com/KhronosGroup/SYCL-CTS.git
  git -C "$source_directory" fetch --depth 1 origin "$revision"
  git -C "$source_directory" checkout --detach "$revision"
fi
source_directory="$(cd "$source_directory" && pwd -P)"
[[ -z "$(git -C "$source_directory" status --porcelain --untracked-files=all)" ]] || { echo "SYCL-CTS source tree is modified" >&2; exit 1; }
[[ "$(git -C "$source_directory" rev-parse HEAD)" == "$revision" ]] || { echo 'SYCL-CTS revision mismatch' >&2; exit 1; }
git -C "$source_directory" submodule update --init --recursive --depth 1
python3 - "$script_directory/pin.json" "$source_directory" <<'PY'
import json,subprocess,sys
from pathlib import Path
pin=json.load(open(sys.argv[1])); source=Path(sys.argv[2])
for path,expected in pin['submodulepins'].items():
 actual=subprocess.check_output(['git','-C',str(source/path),'rev-parse','HEAD'],text=True).strip()
 if actual != expected: raise SystemExit(f'{path}: expected {expected}, got {actual}')
PY
backend=sycl
conformance_container_init
conformance_build_image
"$engine" run --rm --name "$container_name" "${container_userns_args[@]}" "${container_cgroup_args[@]}" \
  --user "$builder_uid:$builder_gid" --security-opt label=disable --network "$container_network" \
  --cpus="$build_jobs" --memory="$((build_jobs * 2))g" --memory-swap="$((build_jobs * 2))g" "${container_environment_args[@]}" \
  --mount "type=bind,src=$source_directory,dst=/src,ro" \
  --mount "type=bind,src=$script_directory,dst=/recipe,ro" \
  --mount "type=bind,src=$root/conformance/tools,dst=/gemstone-tools,ro" \
  --mount "type=bind,src=$output_directory,dst=/out,rw" \
  "$builder_image" /bin/bash /recipe/build.sh /src /work/sycl-cts /out
python3 - "$script_directory/pin.json" "$output_directory/manifest.json" "$image" <<'PY'
import json,sys
manifest=json.load(open(sys.argv[1]));manifest['image']=sys.argv[3]
manifest['compiler']='pinned oneAPI icpx, spir64';manifest['opencl_interop']=False
open(sys.argv[2],'w').write(json.dumps(manifest,indent=2)+'\n')
PY

conformance_artifact_commit
