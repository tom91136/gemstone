# GPU conformance

This independent CMake/CTest project builds relocatable test artifacts for `cuda`, `hip`, `hsa`, `opencl`, `sycl`,
`levelzero`, `vulkan`, and `openacc`, and runs them on host devices or through a launcher such as Gemstone.
Commands below run from this directory. Workloads select tests at run time; they never change what is compiled.

## Artifacts

`artifacts/` is an outer CMake project that expands a build profile into container builds:

```console
cmake -S artifacts -B build/artifacts -G Ninja
cmake --build build/artifacts
cmake --build build/artifacts --target cuda-sm_89
```

Artifacts are written to `build/artifacts/output`:

```text
cuda/support-11.8/   cuda/support-13.3/   cuda/support-gcc/
cuda/support-offload/
cuda/sm_89/          cuda/clang/sm_89/    cuda/gcc/sm_80/
hip/support/         hip/gfx1030/
hsa/support/         hsa/support-gcc/     hsa/gfx1030/   hsa/gcc/gfx906/
opencl/  sycl/  levelzero/  vulkan/
```

Support directories hold the shared `lib/`, `data/`, and `licences/` payloads; compiler variants share their
backend's support unless their runtime closure differs, as GCC's offload plugins do. Clang routes also load
`support-offload`, which holds the OpenMP offload runtime and is named by `supplements` in `artifact.toml`.
Architecture directories hold `artifact.toml`, `conformance-device`, `test-programs.txt`, and the test executables.

| Build profile | Matrix |
| --- | --- |
| machine profile | The backends and architectures named by the targets of `profiles/<name>.toml`, for example `sora-framework16` builds CUDA `sm_120`, HIP/HSA `gfx1150`, OpenCL, and Vulkan |
| `complete` | CUDA `sm_35`-`sm_120` (11.8 up to `sm_70`, 13.3 after), the architecture-specific `sm_90a`, `sm_100a`, `sm_103a`, and `sm_120a`, the family-specific `sm_100f`, `sm_103f`, and `sm_120f`, AMD `gfx803`-`gfx1201`, OpenCL, SYCL, Level Zero, Vulkan, and OpenACC `sm_50`-`sm_120` (NVHPC 25.7 up to `sm_70`, 26.5 after, base targets only) |

Whatever the profile, every program is built for the selected architectures; the run tier alone decides what runs.
Select a profile with `-DCONFORMANCE_BUILD_PROFILE=<name>` (empty names the profile of the local short hostname), or
override parts of it with `-DCONFORMANCE_BACKENDS`, `-DCUDA_ARCHITECTURES`, `-DAMDGPU_ARCHITECTURES`, and
`-DCONFORMANCE_COMPILERS` (comma-separated). `cmake -DCONFORMANCE_BUILD_PROFILE=complete -P artifacts/Targets.cmake`
prints the execution routes. A rocBLAS gap on one architecture, such as `gfx803`, disables rocBLAS for that architecture's
route only.

| Compiler | Backends | Coverage |
| --- | --- | --- |
| `vendor` | All | Every suite |
| `clang` | CUDA | Clang 22 CUDA frontend and OpenMP offload; unified tests, CUDA miniapps, and OpenMP target miniapps |
| `gcc` | CUDA, HSA | GCC 14 OpenMP target miniapps; CUDA routes are PTX only |

CUDA architectures are `sm_<capability>` with an optional `a` or `f` suffix. An `a` route compiles
architecture-specific code (`compute_90a`, `.target sm_90a`), which runs only on that compute capability; an `f` route
compiles family-specific code, which also runs on later minor revisions of the same major. Both use CUDA 13.3, take the
numeric capability wherever one is compared, and pass the suffixed name to `ptxas -arch` for the explicit fixtures and
to the device probe. Gemstone runs them as the plain capability, so `GEMSTONE_TARGET_ARCH=sm_90a` reports compute
capability 9.0 and loads both the suffixed PTX and its cubin. Clang 22 accepts `a` targets but not `f` ones, so the
`clang` compiler omits the `f` routes.

Unsupported compiler and architecture combinations are omitted; a selection with none left fails. The GCC images
rebuild libgomp 14.2.0 with a default-device fix, and the runner sets `OMP_TARGET_OFFLOAD=MANDATORY` so CPU
fallback cannot pass.

Builds run in disposable containers against persistent trees under `CONFORMANCE_WORK_DIRECTORY`; a changed
configuration or dependency pin starts that tree afresh, and `-DCONFORMANCE_KEEP_WORK=OFF` deletes each tree once
its artifact is verified. Toolchain images are tagged by a hash of their definition, so identical checkouts share
them; `cmake --build build/artifacts --target prune-images` removes superseded tags. Dependencies are pinned in
`dependencies.cmake` and downloaded once into `CONFORMANCE_DEPENDENCY_DIRECTORY`.

Share one job budget across the outer build and every container with GNU Make 4.4+ and Ninja 1.13+:

```console
sh cmake/jobserver.sh -- cmake --build build/artifacts --target artifacts
```

The budget defaults to `CMAKE_BUILD_PARALLEL_LEVEL` or the core count; do not add inner `-j` flags.
`CONFORMANCE_ARTIFACT_PARALLELISM` caps concurrent containers (default: cores / 8, at most 4).

CI builds the SYCL CTS as shards on separate runners, each with `CONFORMANCE_SYCL_SHARD_INDEX=<n>` writing
`artifacts/sycl-shard-<n>`; `cmake -DARTIFACTS=<artifact directory> -P artifacts/Merge.cmake` joins them into
`artifacts/sycl`. Shard membership is in `cts/sycl/Shards.cmake`; shard 0 also builds the unified suite and the
miniapps.

`cmake -DCONFORMANCE_BUILD_PROFILE=<build profile> -DMODE=build|run -P artifacts/Shards.cmake` prints the CI build or
emulation shards as a GitHub Actions matrix.
Its policy groups routes by the first pattern they match, and a route that matches none goes to the first group's
shard 0 with a warning. Each group cuts its architectures, in route order, into equal consecutive slices. The
cuda build shards hold one architecture and amd ones at most two, so their count follows the routes. The run shards use
the same policy, so they cover exactly the routes the build shards built. A shard names its routes in
`CONFORMANCE_SHARD_ROUTE_LIST`, so its own first route of each support closure builds that closure; the closures are
identical, so the shards' artifacts unpack over each other. CI packs each closure once: the lowest build shard holding
it uploads it as `conformance-<shard>-support`, and SYCL shards past 0 leave out `lib`. CI joins every build artifact
into one full package, merges the SYCL shards, caches that package alone, and hands it to every run shard; the
`conformance` artifact of a run is that package. `artifacts/Package.cmake` packs both the shard archives and the full
package. A failed build shard leaves its backends out of the package, which is then not cached. A group with
`CASES <n>` runs each of its run shards as n shards; the driver keeps every n-th case of the routes through
`CONFORMANCE_CASE_SHARD=<index>/<count>`. OpenCL runs this way, as one route too long for a runner.

CI records the emulated server with JFR and uploads each conformance run's recording as the `jfr-<shard>` artifact,
beside the `report-<shard>` reports.

## Driver

```console
cmake -S . -B build/executable -G Ninja -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/executable --target conformance
build/executable/bin/conformance run --profiles profiles --artifacts build/artifacts/output \
  --machine soraws-uk --workload xlong --output build/reports/direct
```

`conformance run` resolves artifacts, probes devices, discovers cases, and runs them in parallel. Each case applies
profile skips and runs one test in its owning executable within the program's timeout; a timeout or interruption kills
the case's whole process group, and exit status 4 marks a skip. Cases on the same PCI/UUID device run one at a time
while different devices run concurrently; a device without an identity excludes every other device. Exclusion covers
one invocation only. Omit `--machine` to use the profile named after the local host. Linux is the only supported host.

Machine profiles select backends, architectures, devices, environment, and skips. `--direct --workloads workloads`
replaces profiles with explicit `backend[/architecture[/compiler]]` routes and uses no profile environment or skips.
`--isolated-launcher --launcher -- <command>` runs each case through a launcher that owns its device, deduplicating
identical routes. `CONFORMANCE_JOB_COUNT` sets case parallelism (default: the thread count). Each case writes `output.log`
to its report directory; failures print the exit status or timeout and the log path. Each run writes `junit.xml`, the
resolved workload as `<workload>.toml`, and every target's effective `environment.txt` to its report directory.

Timeouts suit a 32-thread host. Isolated launchers stretch them up to fourfold on hosts with fewer threads, and
`CONFORMANCE_TIMEOUT_SCALE` (default 1, up to three decimals such as `1.5`) multiplies every timeout.
`CONFORMANCE_THREAD_COUNT` overrides the thread count, which defaults to the CPUs available to the process.
`sbt conformance/emulate` sets both from `GEMSTONE_THREAD_COUNT` and `GEMSTONE_TIMEOUT_SCALE` unless they are already
set. Integer settings take positive whole numbers and `_ON` settings take `1 true on y yes` or `0 false off n no` in
any case; an empty value counts as unset, and any other value fails before the run starts.

CUDA routes are `cuda-ptx` and `cuda-sass`; `cuda/sm_*` expands to both. Each has its own artifact: `cuda-ptx/<arch>`
holds PTX only, so the driver JITs the program's own kernels while vendor libraries keep their SASS, and `cuda/<arch>`
is the fatbin with SASS and PTX for `cuda-sass`. Clang cannot emit a PTX-only image, so its PTX-only artifacts compile
each translation unit through `cmake/clang-cuda-ptx.sh`, which emits PTX, wraps it with `fatbinary`, and embeds the
fatbinary in the host object. The route sets only `CONFORMANCE_CUDA_CODE_MODE`, from which the
artifact directory and the expected artifact `code` follow, and the `sbt` launcher derives `GEMSTONE_CUDA_CODE_MODE`.
SASS mode sets `CUDA_DISABLE_PTX_JIT` and never falls back to PTX.
Clang PTX modules can take minutes each to JIT, so on a PTX-only artifact without an isolated launcher the driver first runs
one unskipped case of every program with `CUDA_MODULE_LOADING=EAGER`. That loads, and so caches, all of the program's modules,
and the cases then run with their normal timeouts. The warm-up has a two-hour bound (scaled by `CONFORMANCE_TIMEOUT_SCALE`),
prints its time per program, writes its log under `warmup/` in the target's report directory, and fails the program's cases
when it fails. The driver pins `CUDA_CACHE_PATH` to `jit-cache` in the output directory and `CUDA_CACHE_MAXSIZE` to the
driver maximum of 4 GiB, because the default 1 GiB cache in `~/.nv` evicts the modules; later runs reuse the cache.
Explicit memory fixtures are assembled to cubins by the pinned `ptxas`. rocBLAS kernels absent from the pinned
vendor package are reported as unsupported. Large GEMM cases can exceed Gemstone's default instruction budget; raise
`GEMSTONE_EXECUTION_MAX_INSTRUCTION_COUNT` for them.

## Workloads

| `--workload` | Selection |
| --- | --- |
| `short` | Compact unified matrices, CTS samples, and all miniapps, without vendor suites, rocThrust-semantic cases, or release-only programs; for fast local iteration |
| `long` | Local pre-push assurance: everything `xlong` runs except the vendor suites, the rocThrust-semantic cases, and the heavy Level Zero CTS families |
| `xlong` | What CI runs: `long` plus the vendor suites, the rocThrust-semantic cases, and the heavy Level Zero CTS families |
| `all` | Release run: every compiled case, including the exhaustive CTS sweeps, the full OpenCL CTS categories, and the slow vendor samples |
| `cts-bounded` | The bounded OpenCL, SYCL, and Vulkan CTS selections |
| `opencl-cts` / `sycl-cts` | Every compiled case from that CTS |
| `vulkan-cts` | The pinned Vulkan mustpass manifest |
| `vulkan-cts-bounded` | Explicit API, compute, graphics, and shader cases |
| `vulkan-info` | Feature, property, limit, and driver-identity cases from the mustpass manifest |
| `opencl-cts-bounded` | OpenCL CTS without the exhaustive integer, half-store, image-size, and thread sweeps |
| `sycl-cts-bounded` | SYCL CTS without the full-type `range_api` launch |
| `miniapps` | Miniapps across the selected compiler routes |

The tiers nest: `short` < `long` < `xlong` < `all`, and each extends the previous. The TOML files in
[workloads](workloads) define them: `extends` inherits a workload, supplied fields replace inherited ones, and an
`add_` prefix (`add_levelzero_cases`, `add_exclude_programs`, ...) appends to the inherited list instead.
`include_programs` and `exclude_programs` take program-name globs and exclusions win, `exclude_cases` takes case-identity
globs dropped after discovery, `specs` filters Catch2 names, and `levelzero_cases` filters Google Test names. Targets
without matching tests are omitted; an empty selection fails. `conformance run --dry-run` and
`sbt conformance/emulate --dry-run` print the selected case count per route and program without running anything.

Programs named `vendor_*` are the vendor suites (CUDA samples, libraries and CCCL; ROCm examples, rocPRIM, hipCUB, and
rocThrust tests); name new ones the same way and only `xlong` and `all` select them. Cases tagged `[release]` are
selected only by `all`.

| Environment variable | Effect |
| --- | --- |
| `CONFORMANCE_OPENCL_CTS_ELEMENT_COUNT` | OpenCL input count; empty keeps the upstream default |
| `CONFORMANCE_OPENCL_CTS_WIMPY_ON` | Reduced OpenCL stress; `0` disables it |
| `CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATION_COUNT` / `_THREAD_COUNT` | OpenCL C11 atomic stress |
| `CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS` | OpenCL per-case deadline in seconds (default 600) |
| `CONFORMANCE_VULKAN_CTS_TIMEOUT_SECONDS` | Vulkan per-case deadline in seconds (default 600; 7,200 in `lavapipe.linux`) |

Unless the profile or the invoking environment sets them, isolated launchers use 1,024 OpenCL elements and wimpy
mode; `sbt conformance/emulate` passes smaller defaults. `vulkan-cts` and `vulkan-info` expand glob selectors against
the pinned mustpass manifest.

Every program is always built, including the release-only ones: the OpenCL CTS allocation, bruteforce, conversion and
select categories and the slow CUDA samples, libraries and CCCL examples, all tagged `[release]`. Only the `all` workload
selects them. The SYCL CTS also builds each category with `SYCL_CTS_ENABLE_FULL_CONFORMANCE` as a separate program
named `sycl_cts_full_conformance_<category>`, because that mode changes test bodies at compile time and cannot be
selected at run time. These programs are excluded by every workload except `all`.

## Suites

The unified suite registers `memory/<allocation>/<scenario>` for CUDA, HIP, HSA, Level Zero, OpenCL, and SYCL:
contiguous, strided, gather, and linked access, queue ordering, publication, fences, RMW, and compare-exchange.
Short uses each backend's default allocation and 32-bit words; long and above add further allocation kinds and 64-bit
operations. Missing optional capabilities skip explicitly.

Vulkan builds VK-GL-CTS `vulkan-cts-1.4.6.2` headlessly. dEQP runs one case per process with shader caching
disabled; `Pass`, `QualityWarning`, and `CompatibilityWarning` pass, `NotSupported` skips, and anything else fails.
Waivers are not applied implicitly; QPA logs stay in the case report directories with the original status.
GGML checks llama.cpp's Vulkan backend against host references. Dear ImGui renders upstream demo scenes and compares
them with lavapipe references, allowing three units per channel on at most 0.2% of pixels.

## External fixtures

Other projects reuse the pinned toolchains through `CONFORMANCE_ARTIFACT_KIND`:

```console
cmake -S artifacts -B build/memory-hsa -G Ninja -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=memory \
  -DCONFORMANCE_BACKEND=hsa -DCONFORMANCE_ARCHITECTURE=gfx900 -DCONFORMANCE_ARTIFACT_DIRECTORY=/absolute/output
cmake --build build/memory-hsa --target artifacts
```

| Fixture | Backend | Output |
| --- | --- | --- |
| `memory` | `cuda` | `kernel.ptx`, `exchange.ptx`, `atomic.ptx`, `fence.ptx`, `contended.ptx` |
| `memory` | `hsa` | `kernel.hsaco` |
| `memory` | `opencl` | `kernel.spv` |
| `vector-copy` | `hsa` | `vector-copy.hsaco` |

Memory fixtures expose `probe(input, output, indices, mode)` with the unified suite's memory-case protocol;
`vector_copy(source, destination, unused)` copies one 32-bit element per work-item.

The `command` kind runs a caller-owned CMake script (`CONFORMANCE_COMMAND_SCRIPT`) in a pinned image with read-only
input at `/source` and output at `/output`. `CONFORMANCE_COMMAND_INPUTS` and `CONFORMANCE_COMMAND_OUTPUTS` list its
dependencies and results; `CONFORMANCE_RUNTIME_DIRECTORY` bundles a SYCL runtime beside the outputs.

## Unit tests

```console
cmake -S . -B build/unit -G Ninja -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/unit
ctest --test-dir build/unit --output-on-failure
```
