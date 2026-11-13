#!/usr/bin/env bash
# usage: native/test/almalinux8-smoke.sh <build-dir>
set -euo pipefail

image=quay.io/almalinuxorg/almalinux@sha256:7596d3c7d159ec8e1df2b7d2b961db939de291a3e00c5e4d8f6a9d610b3b6d35
container=${GEMSTONE_CONTAINER_EXE:-podman}
identity=(--user "$(id -u):$(id -g)")
if [[ ${container##*/} == podman ]]; then
  identity+=(--userns keep-id)
fi
self=$(realpath "${BASH_SOURCE[0]}")

if [[ ${1:-} == --container ]]; then
  work=$2
  export work
  mkdir -p "$work/logs"
  run_case() {
    local index=$1 name=$2 dir=$3 limit=$4 skip=$5
    shift 5
    local environment=()
    while [[ $1 != -- ]]; do
      if [[ $1 == @prepend:* ]]; then
        local assignment=${1#@prepend:} variable=${1#@prepend:}
        variable=${variable%%=*}
        local value=${assignment#*=} inherited=${!variable:-}
        environment+=("$variable=$value${inherited:+:$inherited}")
      else
        environment+=("$1")
      fi
      shift
    done
    shift
    local code=0
    (cd "$dir" && timeout "$limit" env "${environment[@]}" "$@" < /dev/null > "$work/logs/$index.log" 2>&1) || code=$?
    local status=fail
    if ((code == 0)); then status=pass; elif [[ -n $skip && $code == "$skip" ]]; then status=skip; fi
    printf '%s\t%s\t%s\t%s\n' "$status" "$code" "$index" "$name" >> "$work/results.tsv"
  }
  export -f run_case
  xargs -d '\n' -P 8 -I CMD bash -c 'CMD' < "$work/cases.sh"
  exit 0
fi

build=$(realpath "$1")
repo=$(dirname "$(dirname "$(dirname "$self")")")
work="$build/almalinux8-smoke"
rm -rf "$work"
mkdir -p "$work"
: > "$work/results.tsv"

if "$container" image inspect "$image" > /dev/null 2>&1; then
  pulled=0
else
  "$container" pull "$image"
  pulled=1
fi
trap 'if ((pulled)); then "$container" image rm "$image"; fi' EXIT

(cd "$build" && ctest --show-only=json-v1) | jq -r --arg build "$build" '
  def values($t; $k): [$t.properties[]? | select(.name == $k) | .value | if type == "array" then .[] else . end];
  .tests | to_entries[] | .key as $index | .value as $t
  | select(($t.command[0] | startswith($build + "/")) and (any(values($t; "LABELS")[]; . == "opencl-compiler") | not))
  | (values($t; "WORKING_DIRECTORY") | .[0] // $build) as $dir
  | (values($t; "TIMEOUT") | .[0] // 1500) as $limit
  | (values($t; "SKIP_RETURN_CODE") | .[0] // "") as $skip
  | (values($t; "ENVIRONMENT") + [values($t; "ENVIRONMENT_MODIFICATION")[] |
      if test("=set:") then sub("=set:"; "=")
      elif test("=path_list_prepend:") then "@prepend:" + sub("=path_list_prepend:"; "=")
      else error("unsupported \(.)") end]) as $environment
  | ["run_case", $index, $t.name, $dir, $limit, $skip] + $environment + ["--"] + $t.command
  | @sh' > "$work/cases.sh"

cases=$(wc -l < "$work/cases.sh")
echo "cases: $cases"

"$container" run --rm --security-opt label=disable "${identity[@]}" \
  --mount "type=bind,source=$build,target=$build" \
  --mount "type=bind,source=$repo,target=$repo,readonly" \
  --env HOME=/tmp --workdir "$build" \
  "$image" bash "$self" --container "$work"

results="$work/results.tsv"
if [[ $(wc -l < "$results") != "$cases" ]]; then
  echo "results: $(wc -l < "$results") of $cases cases reported" >&2
  exit 1
fi
cut -f1 "$results" | sort | uniq -c
awk -F'\t' '$1 == "fail" { print $4 }' "$results" | sort > "$work/failed.txt"
if [[ -s "$work/failed.txt" ]]; then
  cat "$work/failed.txt" >&2
  exit 1
fi
