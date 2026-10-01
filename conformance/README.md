# GPU conformance

This independent CMake/CTest project builds relocatable artifacts for `cuda`, `hip`, `hsa`, `opencl`, `sycl`,
`levelzero`, `vulkan`, and `openacc`. Commands in this file run from this directory.

Each backend has test programs, a runtime closure, and a data bundle plus one small overlay per compiled
architecture. CUDA support bundles are keyed by SDK because overlays built by different CUDA generations cannot safely
share one runtime closure. Workloads select tests at runtime; they do not change what is compiled.

## External fixture and toolchain builds

Other projects use the artifact builder through CMake invocations. Its `cmake/`
helpers and suite source paths are internal; consumers do not include them.
The formatting symlink is independent of this build interface.

Export a memory fixture without building or fetching the CTS suites:

```console
cmake -S artifacts -B build/memory-hsa -G Ninja \
  -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=memory \
  -DCONFORMANCE_BACKEND=hsa -DCONFORMANCE_ARCHITECTURE=gfx900 \
  -DCONFORMANCE_ARTIFACT_DIRECTORY=/absolute/output
cmake --build build/memory-hsa --target artifacts
```

| Fixture | Backend | Architecture | Exported files |
| --- | --- | --- | --- |
| `memory` | `cuda` | None; PTX targets sm_80 | `kernel.ptx`, `atomic.ptx`, `fence.ptx`, `contended.ptx` |
| `memory` | `hsa` | Required, e.g. `gfx900` | `kernel.hsaco` |
| `memory` | `opencl` | None | `kernel.spv` (32/64-bit atomics) |
| `vector-copy` | `hsa` | Required | `vector-copy.hsaco` |

Memory fixtures expose `probe(input, output, indices, mode)`: three 64-bit
pointers and a 32-bit mode. The inputs use the existing memory-case protocol:
0–3 select contiguous, strided, gather and linked access; 4 selects sparse byte
writes; 5–14 select publication, fences, RMW and failed-CAS operations; 15–22
contain the device/shared publication operations (20 is reserved). Linked nodes
are 64 bytes: a pointer, a 32-bit value, and thirteen padding words. The separate
PTX atomic/fence/contention images use the same argument layout.
The vector-copy fixture exposes `vector_copy(source, destination, unused)`,
copying one 32-bit element per work-item.

The `command` kind runs a caller-owned CMake recipe in a pinned compiler image:

```console
cmake -S artifacts -B build/custom -G Ninja \
  -DCONFORMANCE_ARTIFACT_KIND=command -DCONFORMANCE_BACKEND=sycl \
  -DCONFORMANCE_COMMAND_SOURCE=/absolute/input \
  -DCONFORMANCE_COMMAND_SCRIPT=/absolute/compile.cmake \
  -DCONFORMANCE_COMMAND_INPUTS=src/example.cpp \
  -DCONFORMANCE_COMMAND_OUTPUTS=example \
  -DCONFORMANCE_ARTIFACT_DIRECTORY=/absolute/output \
  -DCONFORMANCE_RUNTIME_DIRECTORY=lib
cmake --build build/custom --target artifacts
```

The recipe sees read-only input at `/source`, writable output at `/output`, and
the compiler on `PATH`. `CONFORMANCE_COMMAND_INPUTS` is a semicolon-separated
list of source-relative dependencies; `CONFORMANCE_COMMAND_OUTPUTS` lists
artifact-relative outputs. `CONFORMANCE_COMMAND_ARGUMENTS` supplies optional
CMake arguments such as `-DMODE=example`. CUDA jobs require an architecture such
as `sm_86`; other command recipes select their compiler target themselves.
For SYCL executables, the optional `CONFORMANCE_RUNTIME_DIRECTORY` bundles their
runtime and installed adapters into that output subdirectory. Other jobs omit it.
`CONTAINER_EXECUTABLE` and `ROCM_VERSION` configure the existing pinned toolchains.

Consumers should invoke the nested build on each build request. The artifact
builder tracks its own internal sources, recipes and toolchains; unchanged
outputs remain up to date. Consumers need only declare the resulting artifacts.

## Artifacts

The outer CMake project expands a named target set into container builds. Architecture targets depend on their
backend's support target and export this layout:

```text
cuda/support-11.8/
cuda/support-13.3/
cuda/sm_89/
hip/support/
hip/gfx1030/
hsa/support/
hsa/gfx1030/
opencl/
sycl/
levelzero/
vulkan/
```

Support directories contain the shared `lib/`, `data/`, and `licenses/` payloads; the root driver is built once,
outside every backend artifact. Architecture directories are flat and
contain `artifact.toml`, `conformance-device`, `test-programs.txt`, and their test executables. Architecture
metadata names the exact support bundle it needs. The driver asks each executable for its Catch2, Google Test, or dEQP inventory,
filters it through the selected workload, and materializes one lean CTest entry per selected case.

Configure and build the default target set from this directory:

```console
cmake -S artifacts -B build/artifacts -G Ninja
cmake --build build/artifacts
```

Artifacts are written to `build/artifacts/output`.

`artifacts/targets/default.cmake` selects CUDA `sm_89`, HIP and HSA `gfx1030`, OpenCL, SYCL, Level Zero, and Vulkan. Extend or replace
the matrix at configure time:

```console
cmake -S artifacts -B build/artifacts -G Ninja \
  -DCONFORMANCE_BACKENDS=cuda,hip,hsa,opencl,sycl,levelzero,vulkan \
  -DCUDA_ARCHITECTURES=sm_89,sm_90 -DAMDGPU_ARCHITECTURES=gfx1036,gfx1100
```

Ninja avoids unchanged entries, and Podman caches reusable toolchain images. Compilation runs in disposable containers, so intermediate build trees
do not consume persistent container storage. Set `CONTAINER_EXECUTABLE=docker` to use Docker. Build one entry with
`cmake --build build/artifacts --target cuda-sm_89`.

The `complete` target set covers the maintained CUDA and ROCm 7 architecture generations:

```console
cmake -S artifacts -B build/complete -G Ninja \
  -DCONFORMANCE_TARGET_SET=complete
cmake --build build/complete
```

CUDA `sm_35`, `sm_50`, `sm_60`, and `sm_70` use CUDA 11.8; `sm_75` through `sm_120` use CUDA 13.3. The complete AMD
matrix spans `gfx803` through `gfx1201`. The `gfx803` HIP overlay remains intentionally limited: it keeps rocThrust and
viable miniapps, while rocBLAS cases are omitted because current ROCm releases no longer ship a matching kernel
library. Use the independent `legacy-gfx803` target set when that is the only artifact required.

```console
cmake -S artifacts -B build/gfx803 -G Ninja \
  -DCONFORMANCE_TARGET_SET=legacy-gfx803
cmake --build build/gfx803
```

### Build concurrency

Share a job budget across outer and container builds with GNU Make >=4.4, Ninja >=1.13, and `setsid`:

```console
sh artifacts/jobserver.sh --jobs 4 -- cmake --build build/artifacts --target artifacts
```

`CONFORMANCE_ARTIFACT_PARALLELISM` caps active artifact pipelines (default 1).
Do not add inner `--parallel`/`-j` flags: they bypass the shared budget. Cancellation stops the invocation's containers.
Without the wrapper, each container uses `CMAKE_BUILD_PARALLEL_LEVEL` or four jobs; budgets are not shared.
The container images provide Ninja 1.13.2. Compiler-internal threads and memory are not bounded by job tokens.

## Driver

Build the dual-use driver without building suites:

```console
cmake -S . -B build/executable -G Ninja \
  -DBUILD_TESTING=OFF \
  -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/executable --target conformance
```

`conformance run` is the profile coordinator. It resolves artifacts, probes their physical devices, discovers test
cases, writes a run-local CTest inventory, and starts CTest. Each CTest entry calls `conformance exec`, which applies
profile skips, takes the physical-device lock, and runs exactly one case in its owning executable.

Identified devices take a shared coordination lock and an exclusive PCI/UUID device lock, allowing different devices
to run concurrently. A runtime without a usable physical identity (for example, some CPU OpenCL runtimes) takes the
coordination lock exclusively. All participating runners must use the same temporary directory and locking scheme.

Machine profiles select backends, architectures, devices, environment, and explicit skips. Each invocation selects one
profile; omit `--machine` to use the profile matching the local short hostname.
`--isolated-launcher` skips duplicate resolved routes and disables physical-device locks because the launcher replaces the
profile's physical provider. The caller owns the launcher and any servers it requires.

Run directly or through any compiler-wrapper-style launcher:

```console
build/executable/bin/conformance run \
  --profiles profiles --artifacts build/artifacts/output \
  --machine soraws-uk --workload full --output build/reports/direct

build/executable/bin/conformance run \
  --profiles profiles --artifacts build/artifacts/output \
  --machine soraws-uk --workload full --output build/reports/emulated --isolated-launcher \
  --launcher -- /path/to/launcher
```

Pass backend or target names before `--launcher` to narrow a run. `CONFORMANCE_JOBS` controls CTest parallelism.
Each executed case saves both output streams in its report directory's `output.log`. Failed cases print a bounded excerpt and
the full log path, including for crashes and timeouts.
### Workloads

| `--workload` | Selection |
| --- | --- |
| `compact` | Broad iteration suite: compact unified matrices, CTS samples and small miniapps |
| `full` | Unified tests and miniapps plus all three bounded CTS selections |
| `cts-bounded` | The OpenCL, SYCL and Vulkan bounded CTS selections together |
| `opencl-cts` / `sycl-cts` | All compiled cases from that CTS |
| `vulkan-cts` | The pinned official Vulkan mustpass manifest |
| `vulkan-cts-bounded` | Explicit API, compute, graphics and shader regression cases |
| `opencl-cts-bounded` | OpenCL CTS excluding exhaustive integer, half-store, image-size and thread-dimension sweeps |
| `sycl-cts-bounded` | SYCL CTS excluding the full-type `range_api` launch of roughly 8.7 trillion work-items |

No backend suffix is required: targets with no matching tests are omitted. An entirely empty selection is an error.
Workloads filter compiled tests; they do not enable additional build categories or guarantee a runtime.
On the development host, the warm-run budgets are ten minutes for `compact` and one hour for `full`,
including discovery, excluding compilation. Actual times depend on load and concurrency.
Compact defers long copy/shuffle cases and fluid miniapps to full. Both use compact unified matrices;
exhaustive CTS and large matrices are separate stress workloads.

The TOML files in [workloads](workloads) record the exact selections:
`extends` loads a base workload; explicitly supplied fields replace inherited values (including empty lists).
`full` and standalone bounded CTS workloads share `cts-bounded` to keep their CTS selections aligned.
`include_programs` accepts program-name globs; exact `exclude_programs` entries take precedence.
`specs` filters Catch2 names; `levelzero_cases` filters Google Test names. Empty program or Level Zero filters select all.
Level Zero module-linking cases require `spirv-link` on `PATH` (provided by the container).

SYCL defaults to `SYCL_CTS_ENABLE_FULL_CONFORMANCE=OFF`. Configure the artifact builder with
`-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=ON` for the full type/operation matrix; existing CMake caches retain their setting.
Use separate build/output directories to retain both variants. Workload selection does not change this option.

OpenCL stress controls are independent of workload selection:

| Environment variable | Purpose |
| --- | --- |
| `CONFORMANCE_OPENCL_CTS_ELEMENTS` | Input count; empty retains the upstream default |
| `CONFORMANCE_OPENCL_CTS_WIMPY` | Reduced stress; `0` disables it |
| `CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS` / `CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS` | C11 atomic stress; empty retains upstream defaults |
| `CONFORMANCE_OPENCL_CTS_TIMEOUT` | Per-case deadline in seconds; default 600 |

Isolated launchers default to 1,024 elements and wimpy mode unless overridden. Emulator profiles reduce atomic
stress to 16 iterations and 128 threads. Effective settings are recorded in each run's `ctest/targets/*.json`.

## Vulkan

The `vulkan` artifact builds Khronos VK-GL-CTS `vulkan-cts-1.4.6.2` and miniapps in
`toolchain-vulkan`, using sources pinned in `cmake/Dependencies.cmake`. All run headlessly;
no display server or presentation surface is required.

Build just Vulkan from this directory:

```console
cmake -S artifacts -B build/vulkan -G Ninja -DCONFORMANCE_BACKENDS=vulkan -DROCM_VERSION=7.1.1
cmake --build build/vulkan --target vulkan
```

Workloads select cases from the full CTS executable; empty selections and unmatched selectors fail.
Version-specific workloads use the pinned upstream mustpass manifests.

dEQP runs one case per process on the selected device, with shader caching disabled.
`Pass`, `QualityWarning` and `CompatibilityWarning` pass; `NotSupported` skips. Other results,
incomplete results and nonzero exits fail. QPA logs remain alongside CTest reports.

GGML tests addition, F16-to-F32 conversion, F16/F32 matrix multiplication and softmax through
llama.cpp's Vulkan backend, checking against host references. No model download is needed.
Direct CMake builds can select `CONFORMANCE_MINIAPP_MODELS=ggml:vulkan`.

Dear ImGui uses upstream demo scenes and its Vulkan renderer: primitives/gradients in compact,
plus application layout and framebuffer resize in full. Each renders three frames. The build
compiles upstream GLSL and generates reference PPMs with the container's lavapipe, packaging them
beside the executable in `imgui/`; no binaries are checked in. Comparisons allow three units of
error per RGB channel, with at most 0.2% of pixels exceeding that tolerance. Failures save actual,
reference and amplified-difference images in the test report directory. Direct CMake builds can
select `CONFORMANCE_MINIAPP_MODELS=imgui:vulkan` and require `glslc` and lavapipe.

## Unit tests

```console
cmake -S . -B build/unit -G Ninja \
  -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/unit --target conformance_unit_tests
ctest --test-dir build/unit --output-on-failure
```

### Shared memory cases

The unified suite registers `memory/<allocation>/<scenario>` for CUDA, HIP, HSA, Level Zero, OpenCL and SYCL.
Shared cases cover contiguous, strided, gather and 4,096-node linked access; queue ordering; CPU pointer replacement;
and publication, fences, RMW and compare-exchange. Access checks include CPU edits, interior pointers and guards.
Compact uses default allocations and 32-bit words; full adds allocation types and 64-bit operations.

| Backend | Default allocation | Additional full allocations |
| --- | --- | --- |
| CUDA | managed | pinned, registered |
| HIP | managed | host, registered |
| HSA | region | pool, locked |
| Level Zero | shared | host |
| OpenCL | SVM | Intel host/shared USM |
| SYCL | shared | host |

Missing optional capabilities produce explicit skips. CUDA fixtures require sm_80 or newer; OpenCL requires SPIR-V
input and fine-grained SVM or Intel USM. Host pointer graphs require identical host/device addresses.
Generated kernels stay in the build tree and are embedded into artifacts.

Shared templates and registrations live in `unified/src/memory_cases.hpp` and `unified/src/cases/memory.inl`.
Each API directory owns its `memory.cpp` entry point and `memory.hpp` adapter. CUDA PTX lives in `unified/src/cuda/`;
`unified/src/memory_kernel.cl` is shared by HIP, HSA, OpenCL and Level Zero.

Vulkan CTS deadlines can be set with `CONFORMANCE_VULKAN_CTS_TIMEOUT` in the target
environment. Isolated runs default to 600 seconds; the full `vulkan10.linux` profile
uses 7,200 seconds for long randomized stress cases. QualityWarning and
CompatibilityWarning are accepted under the upstream Vulkan CTS criteria and remain
visible in QPA logs. Waivers are not applied implicitly.

### Direct artifact targets

Launchers can select artifact routes without a machine profile:

```console
conformance run --direct --workloads workloads --artifacts artifacts --output results \
  --workload compact vulkan cuda/sm_89 --isolated-launcher --launcher -- /path/to/launcher
```

`--direct` requires explicit `backend[/architecture]` routes and rejects `--profiles` and `--machine`.
It uses no profile environment, device selectors, skips or device locks. Every requested artifact must exist.
The caller owns the route inventory and launcher; the conformance runner has no emulator-specific defaults.
