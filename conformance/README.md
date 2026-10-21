# GPU conformance

This independent CMake/CTest project builds relocatable test artifacts for `cuda`, `hip`, `hsa`, `opencl`, `sycl`,
`levelzero`, `vulkan`, and `openacc`, and runs them on host devices or through a launcher such as Gemstone.
Commands below run from this directory. Workloads select tests at run time; they never change what is compiled.

## Artifacts

`artifacts/` is an outer CMake project that expands a target set into container builds:

```console
cmake -S artifacts -B build/artifacts -G Ninja
cmake --build build/artifacts
cmake --build build/artifacts --target cuda-sm_89
```

Artifacts are written to `build/artifacts/output`:

```text
cuda/support-11.8/   cuda/support-13.3/   cuda/support-gcc/
cuda/sm_89/          cuda/clang/sm_89/    cuda/gcc/sm_80/
hip/support/         hip/gfx1030/
hsa/support/         hsa/support-gcc/     hsa/gfx1030/   hsa/gcc/gfx906/
opencl/  sycl/  levelzero/  vulkan/
```

Support directories hold the shared `lib/`, `data/`, and `licences/` payloads; compiler variants share their
backend's support unless their runtime closure differs, as GCC's offload plugins do. Architecture directories hold
`artifact.toml`, `conformance-device`, `test-programs.txt`, and the test executables.

| Target set | Matrix |
| --- | --- |
| `default` | CUDA `sm_89`, HIP/HSA `gfx1030`, OpenCL, SYCL, Level Zero, Vulkan |
| `complete` | CUDA `sm_35`-`sm_120` (11.8 up to `sm_70`, 13.3 after), the architecture-specific `sm_90a`, `sm_100a`, `sm_103a`, and `sm_120a`, the family-specific `sm_100f`, `sm_103f`, and `sm_120f`, and AMD `gfx803`-`gfx1201` |
| `legacy-gfx803` | HIP and HSA `gfx803`, without rocBLAS |

Select a set with `-DCONFORMANCE_TARGET_SET=<name>`, or override parts of it with `-DCONFORMANCE_BACKENDS`,
`-DCUDA_ARCHITECTURES`, `-DAMDGPU_ARCHITECTURES`, and `-DCONFORMANCE_COMPILERS` (comma-separated).
`cmake -DCONFORMANCE_TARGET_SET=complete -P artifacts/Targets.cmake` prints the execution routes.

| Compiler | Backends | Coverage |
| --- | --- | --- |
| `vendor` | All | Every suite |
| `clang` | CUDA | Clang 22 CUDA frontend; unified tests and CUDA miniapps |
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

CI builds the SYCL CTS as three shards on separate runners, each with `CONFORMANCE_SYCL_SHARD=<n>` writing
`artifacts/sycl-shard-<n>`; `cmake -DARTIFACTS=<artifact directory> -P artifacts/Merge.cmake` joins them into
`artifacts/sycl`. Shard membership is in `cts/sycl/CMakeLists.txt`; shard 0 also builds the unified suite and the
miniapps.

## Driver

```console
cmake -S . -B build/executable -G Ninja -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/executable --target conformance
build/executable/bin/conformance run --profiles profiles --artifacts build/artifacts/output \
  --machine soraws-uk --workload full --output build/reports/direct
```

`conformance run` resolves artifacts, probes devices, discovers cases, and runs them in parallel. Each case applies
profile skips and runs one test in its owning executable within the program's timeout; a timeout or interruption kills
the case's whole process group, and exit status 4 marks a skip. Cases on the same PCI/UUID device run one at a time
while different devices run concurrently; a device without an identity excludes every other device. Exclusion covers
one invocation only. Omit `--machine` to use the profile named after the local host. Linux is the only supported host.

Machine profiles select backends, architectures, devices, environment, and skips. `--direct --workloads workloads`
replaces profiles with explicit `backend[/architecture[/compiler]]` routes and uses no profile environment or skips.
`--isolated-launcher --launcher -- <command>` runs each case through a launcher that owns its device, deduplicating
identical routes. `CONFORMANCE_JOBS` sets case parallelism (default: the processor count). Each case writes `output.log`
to its report directory; failures print the exit status or timeout and the log path. Each run writes `junit.xml`, the
resolved workload as `<workload>.toml`, and every target's effective `environment.txt` to its report directory.

CUDA routes are `cuda-ptx` and `cuda-sass`; `cuda/sm_*` expands to both. They share one artifact; a route sets only
`CONFORMANCE_CUDA_CODE`, from which the runner derives NVIDIA's `CUDA_FORCE_PTX_JIT`/`CUDA_DISABLE_PTX_JIT` and the
`sbt` launcher derives `GEMSTONE_CUDA_CODE`. PTX mode rejects cubin-only images and SASS mode never falls back to PTX.
Explicit memory fixtures are assembled to cubins by the pinned `ptxas`. BLAS kernels absent from the pinned vendor
package are reported as unsupported. `-DCONFORMANCE_CUDA_CUBIN_ONLY=ON` builds fatbinaries without PTX in a separate
output directory. Large GEMM cases can exceed Gemstone's default instruction budget; raise
`GEMSTONE_EXECUTION_MAX_INSTRUCTIONS` for them.

## Workloads

| `--workload` | Selection |
| --- | --- |
| `compact` | Compact unified matrices, CTS samples, and all miniapps |
| `full` | Unified tests and miniapps plus the three bounded CTS selections |
| `cts-bounded` | The bounded OpenCL, SYCL, and Vulkan CTS selections |
| `opencl-cts` / `sycl-cts` | Every compiled case from that CTS |
| `vulkan-cts` | The pinned Vulkan mustpass manifest |
| `vulkan-cts-bounded` | Explicit API, compute, graphics, and shader cases |
| `vulkan-info` | Feature, property, limit, and driver-identity cases from the mustpass manifest |
| `opencl-cts-bounded` | OpenCL CTS without the exhaustive integer, half-store, image-size, and thread sweeps |
| `sycl-cts-bounded` | SYCL CTS without the full-type `range_api` launch |
| `miniapps` | Miniapps across the selected compiler routes |

The TOML files in [workloads](workloads) define them: `extends` inherits a workload, supplied fields replace inherited
ones, `include_programs` takes globs, exact `exclude_programs` win, `specs` filters Catch2 names, and
`levelzero_cases` filters Google Test names. Targets without matching tests are omitted; an empty selection fails.

| Environment variable | Effect |
| --- | --- |
| `CONFORMANCE_OPENCL_CTS_ELEMENTS` | OpenCL input count; empty keeps the upstream default |
| `CONFORMANCE_OPENCL_CTS_WIMPY` | Reduced OpenCL stress; `0` disables it |
| `CONFORMANCE_OPENCL_CTS_SMALL_IMAGES` | Small image sizes in the OpenCL image programs; `0` runs the full size sweep |
| `CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS` / `_THREADS` | OpenCL C11 atomic stress |
| `CONFORMANCE_OPENCL_CTS_TIMEOUT` | OpenCL per-case deadline in seconds (default 600) |
| `CONFORMANCE_VULKAN_CTS_TIMEOUT` | Vulkan per-case deadline in seconds (default 600; 7,200 in `lavapipe.linux`) |

Unless the profile or the invoking environment sets them, isolated launchers use 1,024 OpenCL elements and wimpy
mode; `sbt conformance/emulate` passes smaller defaults. `vulkan-cts` and `vulkan-info` expand glob selectors against
the pinned mustpass manifest.

`-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=ON` builds the full SYCL type and operation matrices; keep it in a separate
build directory.

## Suites

The unified suite registers `memory/<allocation>/<scenario>` for CUDA, HIP, HSA, Level Zero, OpenCL, and SYCL:
contiguous, strided, gather, and linked access, queue ordering, publication, fences, RMW, and compare-exchange.
Compact uses each backend's default allocation and 32-bit words; full adds further allocation kinds and 64-bit
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
