#!/usr/bin/env bash
# Shared ELF closure policy. SDK runtimes are bundled; host ABI and kernel drivers are not.
is_host_abi_library() {
  case "$(basename -- "$1")" in
    libc.so*|ld-linux*.so*|libm.so*|libpthread.so*|libdl.so*|librt.so*|libresolv.so*|libutil.so*|libcrypt.so*|libnss_*.so*|libstdc++.so*|libgcc_s.so*|libcuda.so*|libnvidia-*.so*|libze_intel_gpu.so*) return 0 ;;
    libOpenCL.so*|libze_loader.so*) [[ "${bundle_system_loaders:-1}" == 0 ]] ;;
    *) return 1 ;;
  esac
}
is_bundle_library() {
  case "$1" in /opt/*|/usr/local/cuda/*|/usr/local/cuda-*/*|/usr/lib/*|/usr/lib64/*|/lib/*|/lib64/*) ;; *) return 1 ;; esac
  ! is_host_abi_library "$1"
}
bundle_begin() {
  bundle_output="$1"
  bundle_system_loaders="$2"
  mkdir -p "$bundle_output/lib"
  pending=()
  declare -gA seen=()
  local executable
  for executable in "$bundle_output"/bin/*; do
    [[ -x "$executable" && ! -d "$executable" ]] && pending+=("$executable")
  done
  return 0
}
bundle_library() {
  local dependency="$1" real real_name soname dependency_name
  is_host_abi_library "$dependency" && return 0
  real="$(readlink -f -- "$dependency")"
  [[ -f "$real" ]] || return 0
  real_name="$(basename -- "$real")"
  if [[ -z "${seen[$real]+present}" ]]; then
    seen["$real"]=1
    cp -a -- "$real" "$bundle_output/lib/$real_name"
    pending+=("$real")
  fi
  soname="$(readelf -d "$real" | sed -n 's/.*(SONAME).*\[\([^]]*\)\].*/\1/p' | head -1)"
  if [[ -n "$soname" && "$soname" != "$real_name" ]]; then
    ln -sfn -- "$real_name" "$bundle_output/lib/$soname"
  fi
  dependency_name="$(basename -- "$dependency")"
  if [[ "$dependency_name" != "$real_name" ]]; then
    ln -sfn -- "$real_name" "$bundle_output/lib/$dependency_name"
  fi
}
bundle_resolve() {
  local pass file dependency_output unresolved unresolved_name dependency
  local current=()
  for pass in $(seq 1 32); do
    current=("${pending[@]}")
    pending=()
    for file in "${current[@]}"; do
      [[ -f "$file" ]] || continue
      dependency_output="$(ldd "$file" 2>&1)"
      while IFS= read -r unresolved; do
        [[ -n "$unresolved" ]] || continue
        IFS=$' \t' read -r unresolved_name _ <<<"$unresolved"
        is_host_abi_library "$unresolved_name" && continue
        echo "unresolved runtime dependency in $file: $unresolved" >&2
        return 1
      done < <(grep 'not found' <<<"$dependency_output" || true)
      while IFS= read -r dependency; do
        [[ -n "$dependency" ]] || continue
        is_bundle_library "$dependency" || continue
        bundle_library "$dependency"
      done < <(sed -nE 's/.*=> (\/[^ ]+).*/\1/p' <<<"$dependency_output")
    done
    ((${#pending[@]} == 0)) && break
  done
  if ((${#pending[@]} != 0)); then
    echo "runtime dependency closure did not converge" >&2
    return 1
  fi
}
bundle_relocate() {
  local output="$1" file rpath
  for file in "$output"/lib/* "$output"/bin/*; do
    [[ -f "$file" ]] || continue
    file -b "$file" | grep -q '^ELF ' || continue
    rpath='$ORIGIN'
    [[ "$file" == "$output"/bin/* ]] && rpath='$ORIGIN/../lib'
    patchelf --set-rpath "$rpath" "$file"
    if readelf -d "$file" | grep -Eq '/(src|work|opt/rocm|opt/intel|usr/local/cuda[^/]*)/'; then
      echo "non-relocatable path embedded in $file" >&2
      return 1
    fi
  done
}
