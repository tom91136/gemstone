# GPU conformance

This independent CMake/CTest project builds relocatable artifacts for `cuda`, `hip`, `hsa`, `opencl`, `sycl`,
and `openacc`. Commands in this file run from this directory.

Each backend has Catch2 test programs, a runtime closure, and a data bundle plus one small overlay per compiled
architecture. CUDA support bundles are keyed by SDK because overlays built by different CUDA generations cannot safely
share one runtime closure. Workloads select tests at runtime; they do not change what is compiled.

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
```

Support directories contain the shared `lib/`, `data/`, and `licenses/` payloads; the root driver is built once,
outside every backend artifact. Architecture directories are flat and
contain `artifact.toml`, `conformance-device`, `test-programs.txt`, and their Catch2 test executables. Architecture
metadata names the exact support bundle it needs. The driver asks each test executable for its Catch2 inventory,
filters it through the selected workload, and materializes one lean CTest entry per selected case.

Configure and build the default target set from this directory:

```console
cmake -S artifacts -B build/artifacts -G Ninja
cmake --build build/artifacts
```

Artifacts are written to `build/artifacts/output`.

`artifacts/targets/default.cmake` selects CUDA `sm_89`, HIP and HSA `gfx1030`, OpenCL, and SYCL. Extend or replace
the matrix at configure time:

```console
cmake -S artifacts -B build/artifacts -G Ninja \
  -DCONFORMANCE_BACKENDS=cuda,hip,hsa,opencl,sycl \
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

Machine profiles select backends, architectures, devices, environment, and explicit skips. Each invocation selects one
profile; omit `--machine` to use the profile matching the local short hostname.
`--isolated-launcher` skips duplicate resolved routes and disables physical-device locks because the launcher replaces the
profile's physical provider. Server ownership remains outside conformance: the sbt emulation task starts one Gemstone
server, while each launcher invocation registers an isolated client with it.

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
For `sbt conformance/emulate`, the default is at most eight concurrent tests, with the server worker pool sized from all
but two logical CPUs and its maximum heap scaled to the test count (8-32 GiB). `CONFORMANCE_JOBS`,
`GEMSTONE_EXECUTION_PARALLELISM`, and `GEMSTONE_CONFORMANCE_SERVER_HEAP` override these defaults.
`workloads/compact.toml` and `workloads/full.toml` define the available workload sizes.

## Unit tests

```console
cmake -S . -B build/unit -G Ninja \
  -DCONFORMANCE_BUILD_UNIFIED=OFF -DCONFORMANCE_BUILD_CTS=OFF \
  -DCONFORMANCE_BUILD_MINIAPPS=OFF
cmake --build build/unit --target conformance_unit_tests
ctest --test-dir build/unit --output-on-failure
```
