# Conformance

The conformance inventory consists of the unified C++ cases, the four-miniapp model matrix, and pinned upstream
OpenCL/SYCL CTS profiles. CTest sees one exact case execution per test. Oracle and Gemstone variants have separate
names, results, deadlines and logs. The retired vendor harnesses remain retired; CTS uses the same fixture-build,
CTest and reporting lifecycle as the other cases. Retained coverage limits are described in the
[unified guide](unified/README.md#coverage-limits).

```console
sbt --server --batch integration/testFull
GEMSTONE_TEST_PROFILE=default sbt --server --batch conformanceIntegration/testFull
GEMSTONE_CONFORMANCE_BACKENDS=cuda GEMSTONE_CONFORMANCE_FILTER='^unified/cuda/algorithms/copy/i32/n1$' sbt --server --batch conformanceIntegration/testFull
GEMSTONE_CONFORMANCE_BACKENDS= GEMSTONE_MINIAPP_APPS=babelstream sbt --server --batch conformanceIntegration/testFull
```

Integration defaults to a shared 2-billion-instruction execution budget; an explicit environment override wins.
For the aligned full qualification matrix:

```console
UNIFIED_SUITE_CTEST_PROFILE=full GEMSTONE_EXECUTION_MAX_INSTRUCTIONS=2000000000 \
  sbt --server --batch conformanceIntegration/testFull
```

Use `--server --batch` so the sbt JVM observes the selected environment. Root `test` remains MUnit unit tests.
`integration/testFull` builds the native providers, prepares fixtures, starts one shared server, invokes CTest, and closes
all sessions and the server. The Scala integration suite is a lifecycle bridge, not a second case scheduler.

## Host profiles

Profile selection follows Polyregion's polytest conventions: `GEMSTONE_TEST_PROFILE` with an OS suffix, the explicit
profile without a suffix, short hostname with an OS suffix, hostname, then `profiles/default.env`. OS suffixes are
`linux`, `windows` and `darwin`. Files accept comments, `export KEY=value`, and append assignments `KEY+=value`.

`GEMSTONE_TEST_ORACLES` contains colon- or semicolon-separated `provider@target!physical-resource` entries. For example,
`hip@gfx1036!amd-0` and `hsa@gfx1036!amd-0` share one GPU lock. OpenCL and SYCL OpenCL entries identify an ICD vendor;
SYCL OpenCL and Level Zero targeting the same Intel GPU share `intel-0`. The harness isolates the selected OpenCL ICD.
The initial profiles describe device zero for each listed vendor. Target architectures also supply fixture build defaults.
Explicit architecture settings can still override these defaults.

On `soraws-uk`, the physical Radeon is gfx1036, while the bundled ROCm BLAS library contains gfx1030 kernels.
The host profile uses a tested gfx1030 compatibility target and `HSA_OVERRIDE_GFX_VERSION=10.3.0` consistently for
HIP/HSA fixture compilation and execution. This is not an official vendor support claim. Regenerate ROCm artifacts
and case plans after changing that profile; do not reuse gfx1036-only runners with the override.

The same host profile enables native SYCL persistent compilation caching with `SYCL_CACHE_PERSISTENT=1`.
Independent oracle processes reuse compiled device binaries in the SDK's stable user-cache directory; the SDK
retains its normal cache-size and eviction limits. This profile setting is passed to oracle plans, not injected
into emulation plans. See [Intel's cache settings](https://intel.github.io/llvm/EnvironmentVariables.html).

The generic profile declares no GPU oracle. Such oracle tests exit with CTest skip code 77 before spawning a workload;
emulation still runs. `sora-framework16.env` describes Radeon gfx1150 and deliberately contains no NVIDIA target.
The profile format/precedence and hardware descriptions were adapted from `polyregion/native/polytest` and
`polyregion/native/test-profiles`; the harness has no build-time dependency on the Polyregion checkout, LLVM or Aspartame.

## Runtime routes and upstream CTS

On compatible Linux ROCm artifacts, unified HIP and HIP/Kokkos/StdPar miniapps run through both `hip` (Gemstone HIP)
and `hip-hsa` (the artifact’s vendor HIP over Gemstone HSA). The same binary and workload arguments share one native
HIP oracle. Reports retain actual library binding evidence for each emulated route. Windows HIP does not imply HSA;
`hip-hsa` is Linux-only. OpenMP target and direct HSA workloads remain HSA cases. See
[HIP runtime routes](../server/HIP_RUNTIME_ROUTES.md) for the API/platform distinction.

The initial upstream profiles are deliberately bounded: OpenCL builds five categories and schedules ten representative
cases by default; SYCL schedules eleven `item`/`range` cases through both Level Zero and OpenCL. This is partial upstream
coverage, not CTS certification. Upstream result files distinguish passes, failures and unsupported-case skips; device
cases still require authenticated emulation activity. Reviewed host/header/property-only OpenCL entries and two
host-only SYCL cases waive device activity but still require a validated upstream result.

`GEMSTONE_CTS_SUITES` selects `opencl,sycl` by default; an empty value disables CTS. An explicit
`GEMSTONE_CONFORMANCE_FILTER` can select any exported OpenCL case, including cases outside the default ten. Thus
`^cts/opencl/` requests every exported OpenCL case, rather than just the smoke profile. Pinned sources, profile scope
and artifact contracts are documented in [OpenCL CTS](cts/opencl/README.md) and [SYCL CTS](cts/sycl/README.md).

## Scheduling and ownership

Native runtimes built by sbt default to CMake `Release`, matching fixture optimisation. Standard `CMAKE_BUILD_TYPE`
or an explicit `nativeConfigureArguments` build-type argument can override this; native tests retain assertions.

Fixture projects build sequentially under the shared build lock. `CMAKE_BUILD_PARALLEL_LEVEL` controls compiler jobs
inside that one build. The container engine defaults to Podman; its build and run scopes join `GEMSTONE_BUILD_SLICE`
when set. This keeps fixture builds and test processes inside the same systemd resource envelope.

CTest schedules independent case processes concurrently. The CPU resource capacity is the configured
`GEMSTONE_EXECUTION_PARALLELISM`, or the available CPU count. CTest's process limit adds one slot per configured
physical GPU, allowing GPU oracles to start even when all CPU resources are occupied. Only oracle executions acquire physical GPU
`RESOURCE_LOCK`s. CUDA, HIP/HSA and Intel oracle work can overlap on different GPUs. CPU emulation has no oracle GPU lock.

The generated `resources.json` describes the shared CPU capacity. Emulation and host-reference cases request
CPU slots through CTest's standard `RESOURCE_GROUPS` property. Large emulated GEMMs reserve that whole capacity:
edge GEMMs from n512 and matrix shapes with at least 16 million multiply-accumulates. Ordinary CPU cases request
one slot. CloverLeaf OpenMP-target emulation also reserves the whole pool: measured isolated execution fits its
unchanged deadline, while competing CPU cases can push it over. GPU oracles use their physical GPU locks and do not consume emulation CPU slots, so a heavy kernel can
run alongside them. The shared worker pool and systemd envelope still bound actual CPU execution; resource
allocation adds no worker threads. Workload sizes and per-backend deadlines are unchanged.

All emulated cases connect to one server JVM. Each case receives a separate credential, with process state keyed by
PID and process incarnation inside that client scope. Multiple connections from one process share its API state;
other clients cannot access its allocations or satisfy its attestation. Closing a client cancels and releases its state.

Emulated cases require an authenticated request from their own client session. The reviewed entries in
`unified/execution-metadata.json` identify the narrow exceptions: canonical empty-input operations and four
host-only rocThrust tuple/serialization probes. An exception still runs its unchanged native case and must report
exactly one PASS; runtime probes, canary work, unknown operations, and nonzero device workloads keep mandatory
attestation. The driver verifies that the selected `--case` matches the inventory identity and records the applied
requirement/reason, counters, native exit status, and any validation error. Changes to the execution behavior of a
listed case require review of this metadata; a generic `/n0` suffix is not an exemption.


The server has one bounded CPU execution pool, shared across clients and ISAs. Jobs rotate after workgroups, allowing
small jobs to progress alongside large kernels. Necessarily sequential kernels use a single worker. Parsed immutable
programs may be reused through bounded caches; allocations, contexts, queues and completions remain private.

## Results and controls

The generated matrix and authoritative per-case JUnit report are in `target/conformance/ctest/` by default,
or `<GEMSTONE_CONFORMANCE_REPORT_DIR>/ctest/`. `cases/` retains stdout, stderr, timing and child-process peak RSS for
each execution. `profile.json` records the selected host profile and oracle resources.

For direct CTest iteration after generating a matrix, keep a server running with
`sbt --server --batch 'server/run serve --endpoint-file <matrix>/server.json'`, then use
ordinary `ctest --test-dir <matrix> --resource-spec-file <matrix>/resources.json -R <regex> -j35`
for 32 CPU slots and three physical GPUs. The matrix reads the refreshed endpoint file; no JVM is started per case.

Run `python3 conformance/tools/report-run.py <report-directory>` to summarize results. It distinguishes oracle skips
from native passes. Summed case durations overlap under parallel execution; the report also shows wall spans. Client
RSS excludes the shared server; the systemd scope provides the combined memory peak.

| Setting | Purpose |
| --- | --- |
| `GEMSTONE_TEST_PROFILE` | Select a host profile; `default` forces the generic no-GPU profile |
| `GEMSTONE_TEST_ORACLES` | Explicit override of the profile's oracle target list |
| `GEMSTONE_CONFORMANCE_BACKENDS` | Unified artifact backends; empty selects no unified artifacts |
| `GEMSTONE_CONFORMANCE_FILTER` | Regex over individual logical case names |
| `GEMSTONE_MINIAPP_APPS`, `GEMSTONE_MINIAPP_PORTS` | Miniapp/model selection |
| `CMAKE_BUILD_PARALLEL_LEVEL` | Compiler jobs inside the current fixture build |
| `GEMSTONE_EXECUTION_PARALLELISM` | Shared emulation workers and default CTest job count |
| `GEMSTONE_CONFORMANCE_REPORT_DIR` | Logs, profiles and measurements |
| `GEMSTONE_CONFORMANCE_ARTIFACTS` | Relocatable artifact cache root |
| `GEMSTONE_BUILD_SLICE`, `GEMSTONE_RESOURCE_CGROUP` | Shared container/systemd accounting |
| `GEMSTONE_<BACKEND>_IMAGE` | Optional pinned-image override |

Hash shards and the `GEMSTONE_NO_ORACLE` / `GEMSTONE_ORACLE_ONLY` modes are retired. Select the generic profile to skip
GPU oracles. Workload sizes and types remain ordinary runner arguments / `UNIFIED_SUITE_*` CMake settings.

## Debugging

Keep transport recording and execution limits available for diagnosis. AMDGPU instruction diagnostics use one
`GEMSTONE_AMDGPU_TRACE` comma-separated category selector, for example `dispatch,arguments,loads,stores` or `all`.
Former `GEMSTONE_AMDGPU_TRACE_*` switches map to lowercase hyphenated categories (`MERGE_STATE` becomes
`merge-state`). Categories are read once; `control` traces control flow for all kernels. Architecture-affecting
AMDGPU options remain separate from tracing and have not been changed by this cleanup.

Native facade diagnostics use the same category convention: `GEMSTONE_HSA_TRACE` accepts
`api,memory,packets,loaders`, `GEMSTONE_HIP_TRACE` accepts `modules,allocations`, and
`GEMSTONE_OPENCL_TRACE` accepts `allocations`. Each also accepts `all`.

Oracle and emulator executions are independent; paired comparisons run after both finish. A passing paired
acceptance result requires both successes and evidence that the emulator received execution requests. A generic-profile run validates emulation without claiming native GPU coverage. A configured oracle failure remains a failure.

## Measured full runs

`GEMSTONE_CONFORMANCE_ARTIFACTS` selects a common artifact root (`unified`, `miniapps` and `cts` subdirectories), allowing a
fresh fixture build without removing prior artifacts. `GEMSTONE_CONFORMANCE_REPORT_DIR` retains per-process logs,
case/fixture timing and cgroup peak measurements, plus integration JFR recordings. `GEMSTONE_BUILD_SLICE` places
Podman build/run containers in the same systemd slice as sbt; limits on the sbt service alone would not cover sibling
libpod scopes. `GEMSTONE_RESOURCE_CGROUP` identifies that slice's cgroup for peak-memory measurements.
HIP loader provenance is captured once for each runner/runtime-route combination and reused by later cases in the
same report. Full loader output is retained for that qualification or for a failure, rather than for every case.

Native builds and SDK fixture builds share a POSIX lock. Each build uses `CMAKE_BUILD_PARALLEL_LEVEL` workers;
fixture builds and test groups do not fan out across projects. Backend suites and per-model lazy miniapp fixtures
isolate build failures so unrelated jobs are still attempted. The configured interpreter worker count is honored
by every miniapp model; ISA execution code still decides when a dispatch must be sequential.

Virtual runtime targets use `GEMSTONE_CUDA_TARGET` and `GEMSTONE_AMDGPU_TARGET`. The CUDA runtime still accepts
`GEMSTONE_CUDA_TARGET_ARCH` as a compatibility fallback; a non-empty `GEMSTONE_CUDA_TARGET` takes precedence.
These execution settings are distinct from the fixture compilation settings `GEMSTONE_CUDA_ARCH` and
`GEMSTONE_AMDGPU_TARGETS`.

CTS builders publish completed artifacts from private staging directories. Failed stages are removed;
recognized legacy partial artifacts are preserved beside their replacement. Unrecognized destination contents
are rejected. Container execution uses the image ID produced by the build, so another checkout cannot retag
its selected builder. Measurement failures preserve an existing command failure and become infrastructure
errors when the command otherwise succeeds.
