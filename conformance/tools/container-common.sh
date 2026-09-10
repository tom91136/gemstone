#!/usr/bin/env bash
# Shared by conformance builders. Caller sets backend before initialization.
# Cleanup owns only paths created here, never similarly named inherited variables.
artifact_staging=""
builder_identity_file=""
container_name=""

conformance_container_init() {
  local common_directory
  common_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
  case "$backend" in
    host|opencl) default_image="docker.io/library/ubuntu@sha256:1e0a86e57d247923571b75e0aaf48a1449cf8c543d51fb3e07a4a7d7bfa79316" ;;
    cuda) default_image="docker.io/nvidia/cuda@sha256:69e9e39eb8fe2cda271654a0f5eac2f1bb946b2fb9c460eb19c7c3c155f4e64e" ;;
    hip|hsa) default_image="docker.io/rocm/dev-ubuntu-24.04@sha256:92f309c51b52cef8762867848f1529dee821624f23cd5df38455e819538f762f" ;;
    sycl) default_image="docker.io/intel/oneapi-toolkit@sha256:e9db518398753434ee5aab9740a25f1d3134396a30be1569cfad8f8b0d90740c" ;;
    openacc) default_image="" ;;
    *) echo "unsupported backend: $backend" >&2; return 2 ;;
  esac
  engine="${GEMSTONE_CONTAINER_ENGINE:-podman}"
  command -v "$engine" >/dev/null || { echo "container engine not found: $engine" >&2; return 1; }
  local image_backend="$backend"
  [[ "$backend" == opencl ]] && image_backend=host
  [[ "$backend" == hsa ]] && image_backend=hip
  local image_variable="GEMSTONE_${image_backend^^}_IMAGE"
  image="${!image_variable:-$default_image}"
  [[ -n "$image" ]] || { echo "OpenACC requires GEMSTONE_OPENACC_IMAGE" >&2; return 2; }

  local default_build_jobs
  default_build_jobs="$(nproc)"
  ((default_build_jobs > 8)) && default_build_jobs=8
  build_jobs="${CMAKE_BUILD_PARALLEL_LEVEL:-$default_build_jobs}"
  [[ "$build_jobs" =~ ^[1-9][0-9]*$ ]] || {
    echo "CMAKE_BUILD_PARALLEL_LEVEL must be a positive integer: $build_jobs" >&2; return 2;
  }
  builder_uid="$(id -u)"
  builder_gid="$(id -g)"
  builder_image="localhost/gemstone-conformance-builder:${backend}"
  container_name="gemstone-fixture-${backend}-${builder_uid}-$$"
  builder_identity_file="$(mktemp)"
  trap conformance_container_cleanup EXIT
  container_network="${GEMSTONE_CONTAINER_NETWORK:-none}"
  container_userns_args=()
  [[ "$engine" == podman ]] && container_userns_args+=(--userns=keep-id)
  container_cgroup_args=()
  if [[ -n "${GEMSTONE_BUILD_SLICE:-}" ]]; then
    [[ "$engine" == podman ]] || { echo "resource-accounted builds require Podman systemd cgroups" >&2; return 2; }
    container_cgroup_args+=(--cgroup-parent "$GEMSTONE_BUILD_SLICE")
  fi
  container_device_args=()
  if [[ -n "${GEMSTONE_CONTAINER_DEVICES:-}" ]]; then
    local devices device
    IFS=',' read -r -a devices <<<"$GEMSTONE_CONTAINER_DEVICES"
    for device in "${devices[@]}"; do
      [[ -n "$device" ]] && container_device_args+=(--device "$device")
    done
  fi
  container_environment_args=(--env "CMAKE_BUILD_PARALLEL_LEVEL=$build_jobs")
  container_build_context="$common_directory/../containers"
}

conformance_build_image() {
  local build_pull_args=() install_opencl=0
  if [[ "$engine" == podman ]]; then
    "$engine" image exists "$image" >/dev/null 2>&1 || "$engine" pull "$image"
    build_pull_args+=(--pull=never)
  else
    "$engine" image inspect "$image" >/dev/null 2>&1 || "$engine" pull "$image"
    build_pull_args+=(--pull=false)
  fi
  [[ "$backend" == opencl ]] && install_opencl=1
  rm -f -- "$builder_identity_file"
  "$engine" build --iidfile "$builder_identity_file" --force-rm "${build_pull_args[@]}" "${container_cgroup_args[@]}" \
    --file "$container_build_context/Containerfile" \
    --build-arg "BASE_IMAGE=$image" \
    --build-arg "BUILDER_UID=$builder_uid" \
    --build-arg "BUILDER_GID=$builder_gid" \
    --build-arg "INSTALL_OPENCL=$install_opencl" \
    --tag "$builder_image" "$container_build_context"
  conformance_resolve_image
}

# POSIX locking matches the native build lock used by the sbt helper.
conformance_fixture_lock() {
  local root lock
  root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
  lock="$root/target/fixture-build.lock"
  if [[ "${GEMSTONE_FIXTURE_LOCK_HELD:-}" != "$lock" ]]; then
    exec python3 "$root/conformance/tools/fixture-run.py" "$lock" bash "$0" "$@"
  fi
}

conformance_container_cleanup() {
  # Only remove resources created by this invocation, including unpublished artifacts.
  if [[ -n "${container_name:-}" && -n "${engine:-}" ]]; then
    if [[ "$engine" == podman ]]; then
      "$engine" rm --force --ignore --time=0 "$container_name" >/dev/null 2>&1 || true
    else
      "$engine" rm --force "$container_name" >/dev/null 2>&1 || true
    fi
  fi
  [[ -z "${builder_identity_file:-}" ]] || rm -f -- "$builder_identity_file"
  [[ -z "${artifact_staging:-}" ]] || rm -rf -- "$artifact_staging"
}

conformance_resolve_image() {
  builder_image="$(cat -- "$builder_identity_file")"
  [[ "$builder_image" =~ ^sha256:[0-9a-f]{64}$ ]] || { echo "builder did not produce an image ID" >&2; return 1; }
}

# CTS artifacts are published only after both discovery and provenance finish.
conformance_artifact_begin() {
  local parent name
  parent="$(dirname -- "$1")"
  mkdir -p -- "$parent"
  parent="$(cd -- "$parent" && pwd -P)"
  name="$(basename -- "$1")"
  [[ "$name" != . && "$name" != .. && "$name" != / ]] || { echo "invalid artifact destination" >&2; return 1; }
  artifact_destination="$parent/$name"
  [[ ! -L "$artifact_destination" ]] || { echo "artifact destination is a symlink" >&2; return 1; }
  if [[ -f "$artifact_destination/cases.jsonl" && -f "$artifact_destination/manifest.json" ]]; then
    echo "reusing completed CTS artifact: $artifact_destination"
    exit 0
  fi
  if [[ -e "$artifact_destination" ]]; then
    [[ -d "$artifact_destination" ]] || { echo "artifact destination is not a directory" >&2; return 1; }
    local entry
    for entry in "$artifact_destination"/* "$artifact_destination"/.[!.]* "$artifact_destination"/..?*; do
      [[ -e "$entry" || -L "$entry" ]] || continue
      case "$(basename -- "$entry")" in
        bin|lib|licenses|provenance|share|cases.jsonl|manifest.json|upstream-list-*.xml) ;;
        *) echo "unrecognized contents in incomplete artifact: $entry" >&2; return 1 ;;
      esac
    done
  fi
  artifact_staging="$(mktemp -d "$parent/.$name.build.XXXXXX")"
  output_directory="$artifact_staging"
  trap conformance_container_cleanup EXIT
}

conformance_artifact_commit() {
  local common_directory
  common_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
  python3 "$common_directory/fixture-run.py" --publish "$artifact_staging" "$artifact_destination"
  artifact_staging=""
  output_directory="$artifact_destination"
}
