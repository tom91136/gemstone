# GPU conformance

This independent CMake/CTest project builds relocatable artifacts for `cuda`, `hip`, `hsa`, `opencl`, `sycl`,
`levelzero`, and `openacc`. Commands in this file run from this directory.

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

The outer CMake project expands a named target set into serial container builds. Architecture targets depend on their
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
```

Support directories contain the shared `lib/`, `data/`, and `licenses/` payloads; the root driver is built once,
outside every backend artifact. Architecture directories are flat and
contain `artifact.toml`, `conformance-device`, `test-programs.txt`, and their test executables. Architecture
metadata names the exact support bundle it needs. The driver asks each executable for its Catch2 or Google Test inventory,
filters it through the selected workload, and materializes one lean CTest entry per selected case.

Configure and build the default target set from this directory:

```console
cmake -S artifacts -B build/artifacts -G Ninja
cmake --build build/artifacts
```

Artifacts are written to `build/artifacts/output`.

`artifacts/targets/default.cmake` selects CUDA `sm_89`, HIP and HSA `gfx1030`, OpenCL, SYCL, and Level Zero. Extend or replace
the matrix at configure time:

```console
cmake -S artifacts -B build/artifacts -G Ninja \
  -DCONFORMANCE_BACKENDS=cuda,hip,hsa,opencl,sycl,levelzero \
  -DCUDA_ARCHITECTURES=sm_89,sm_90 -DAMDGPU_ARCHITECTURES=gfx1036,gfx1100
```

The outer matrix is always serial; each artifact build retains its internal parallelism. Ninja avoids unchanged entries,
and Podman caches only reusable toolchain images. Compilation runs in disposable containers, so intermediate build trees
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

## Driver

Build the dual-use driver without building suites:

```console
cmake -S . -B build/executable -G Ninja \
  -DBUILD_TESTING=OFF \
  -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/executable --target conformance
```

`conformance run` is the profile coordinator. It resolves artifacts, probes their physical devices, discovers Catch2
cases, writes a run-local CTest inventory, and starts CTest. Each CTest entry calls `conformance exec`, which applies
profile skips, takes the physical-device lock, and runs exactly one case in its owning Catch2 executable.

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
`workloads/compact.toml` and `workloads/full.toml` define the available workload sizes.
The Level Zero CTS uses Google Test. `levelzero_cases` in a workload selects full case names with `*` and `?` globs;
an empty list selects the full compiled inventory. The compact workload selects 182 short driver, device,
context, queue, event, fence, memory, SPIR-V kernel, specialization constant, and verified copy cases.
Its queue and event selection includes asynchronous submission, host-signaled dependencies, and event lifetime.
Memory cases include cross-context copies, indirect pointer copies, allocation-property queries, memory lifetime,
and shared-system prefetch and region copies. Shared/system kernel transfers include 4 KB cases in compact
and additional 6 KB cases in full.
The Level Zero selection also covers SPIR-V function pointers, linked modules, and virtual memory mappings.
Linking separate SPIR-V modules uses `spirv-link` from the host's `PATH` (included in the conformance container).
The full workload selects 550 Level Zero cases from the 20,362-case compiled inventory, including every compact case.
It broadens API, kernel, allocation, SVM, and copy coverage with small parameter samples. Compact also includes short
SYCL context, device, event, queue, kernel, and work-item cases plus OpenCL memory-host-flag cases. The large Level Zero
memory/copy sweeps and OpenCL CTS `thread_dimensions` program are outside the one-hour combined workload budget.

## Unit tests

```console
cmake -S . -B build/unit -G Ninja \
  -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/unit --target conformance_unit_tests
ctest --test-dir build/unit --output-on-failure
```

### Shared memory cases

The project-owned unified suite registers `memory/<allocation>/<scenario>` cases
for CUDA, HIP, HSA, Level Zero, OpenCL and SYCL. Every allocation runs the same
seven access and ordering scenarios: contiguous, stride-17, indexed gather, a dependent 4,096-node
linked traversal, same-queue events, cross-queue events, and synchronized CPU
pointer replacement. Access patterns run twice with CPU edits, interior pointers
and guards. Pointer replacement uses one kernel and a host worker, so it does not
require two kernels to make simultaneous progress. Four additional atomic scenarios
check acquire/release publication, explicit fences, returned RMW values across
128 work-items, and failed compare-exchange publication. Compact runs use 32-bit
words; full runs also cover 64-bit words, subject to device capabilities.

| Backend | Compact allocation | Additional full allocations | Compact cases | Full cases |
| --- | --- | --- | ---: | ---: |
| CUDA | managed | pinned, registered | 11 | 45 |
| HIP | managed | host, registered | 11 | 45 |
| HSA | region | pool, locked | 11 | 45 |
| Level Zero | shared | host | 11 | 30 |
| OpenCL | SVM | Intel host/shared USM | 11 | 45 |
| SYCL | shared | host | 11 | 30 |
| **One target per backend** | | | **66** | **240** |

These counts are named cases before capability skips, not upstream CTS counts.
The existing workload selectors include default allocations in both workloads
and tag additional allocations `[full-matrix]`. They add 114 compact or 368 full
kernel launches. Runtime prerequisites are checked per allocation/operation;
missing optional features produce explicit skips. The CUDA PTX fixture currently
requires sm_80 or newer; OpenCL requires SPIR-V input and fine-grained SVM or the
Intel USM extension. Host pointer graphs require identical host/device addresses.
Generated kernels remain in the build tree and are embedded into the artifacts.

Shared scenario templates live in `unified/src/memory_cases.hpp`, with the
registration list in `unified/src/cases/memory.inl`. Each API directory owns its
`memory.cpp` entry point, capability checks and `memory.hpp` adapter. CUDA PTX
fixtures live in `unified/src/cuda/`; `unified/src/memory_kernel.cl` is shared by
HIP, HSA, OpenCL and Level Zero.

The public scenarios and API adapters have one shared definition.
