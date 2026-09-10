# Unified GPU conformance suite

`unified_suite` is a header-only C++20 workload-conformance library. It runs the same deterministic algorithm and
BLAS cases through three normal vendor programming stacks:

| Executable | Algorithms | Async dependencies | BLAS |
| --- | --- | --- | --- |
| `unified_suite_cuda` | NVIDIA Thrust | CUDA streams/events | cuBLAS |
| `unified_suite_hip*` | rocThrust | HIP streams/events | rocBLAS |
| `unified_suite_sycl` | oneDPL device policies over SYCL USM | SYCL queues/events | oneMKL |

The shared headers own case identities, problem generation, independent host references, validation, configuration,
reporting and exact case selection. Vendor adapters contain only allocation, transfer, synchronization, and matching
library calls. The project does not vendor any vendor implementation or unit-test tree.

HIP installs one runner per algorithm type and workload family. This keeps each process from mapping and registering
the complete rocThrust/rocBLAS code-object set; CTest still exposes the same case names and schedules them together.

This is intentionally a GPU workload-conformance suite, not a claim that it replaces every compile-time, overload,
allocator, device-standard-library, or regression test in the upstream projects.

## Coverage

The algorithm catalog covers copying, uninitialized construction,
generation, mutation, unary and binary transformation, replacement/removal, reductions, inner product, ordinary and
transformed scans, sorting,
keyed and stable sorting, uniqueness, counting/searching/logical queries, binary-search operations, merging,
partitioning, all four set operations, extrema, sortedness, rotation, gather/scatter, keyed or segmented reductions
and scans, keyed uniqueness/merge, partial sorting, nth-element, and in-place merge.

The catalog intentionally records native API asymmetry. Thrust reports `SKIP` for rotate, rotate-copy, includes,
partial-sort, nth-element, and in-place-merge. oneDPL reports `SKIP` for gather, scatter, unique-by-key, merge-by-key,
and partition-point; its segmented reduction and scan APIs implement the corresponding keyed semantic forms. A skip
is therefore a discoverable API-capability result, not a host fallback.

The BLAS catalog is derived from Gemstone's original cross-vendor harness. It covers strided AXPY, SCAL, COPY, DOT,
NRM2 and ASUM, ordinary and transposed column-major GEMV, all four GEMM transpose combinations, and strided-batched
GEMM. Adapters explicitly report unsupported type/operation pairs.

Default algorithm sizes deliberately include empty/singleton ranges and straddle warp/wave and common workgroup
boundaries:

```text
0,1,31,32,33,63,64,65,257
```

The separate `async` catalog targets observable dependency semantics: asynchronous copy, reduction, scan, and sort,
future-style continuations, event joins, and interleaved streams/queues. The same cases use host futures, CUDA/HIP
stream events, or SYCL queue events depending on the backend.

The separate `edge-cases` catalog targets uncommon but bounded semantics: composed fancy iterators, indices above
32 bits, singleton/giant/workgroup-boundary segments, in-place scans with canaries, stable payload association,
multi-stream or multi-queue dependency diamonds, bit-preserving numeric round trips, CUB/rocPRIM/SYCL workgroup
scans, runtime-error recovery where the API exposes it, negative BLAS increments, padded leading dimensions, and
`beta=0` GEMM with uninitialized/NaN C storage.

The `stress` catalog is deliberately distinct. It repeats the dependency, primitive-scan, and numeric-roundtrip
workloads over larger sizes, but is bounded by explicit size and iteration parameters. Edge cases remain quick
semantic checks; stress cases are the place for longer-running kernel and resource-lifetime pressure.

All sizes, matrix shapes and scalar-type selections are runtime configurable. The executable's `--list`
and exported `ctest-inventory.json` are authoritative. HIP-specific overloads/probes remain separate from
portable identities; a documentation case count is not the inventory contract.

This is broad runtime-semantic coverage, not a replacement for every upstream compile-time library test. Compile-time
overload/SFINAE coverage and vendor regression tests that do not express portable runtime behavior remain upstream.

This catalog complements the miniapp matrix and pinned representative CTS profiles. Retired upstream test
inventories are not implied coverage. The optional self-contained regression probes use `--include-regressions`.
See [the shared conformance guide](../README.md) for paired execution and configuration.

## Build and relocation

Run the wrapper from the repository root (the commands below use repository-relative paths).

The unified suite is built in a vendor container, never against the host's compiler, headers, or SDK. The supplied
container build installs a relocatable artifact into a host directory. It includes the runner, headers, CMake package,
and (for a vendor target) the vendor user-space libraries and dynamically loaded SYCL/UR backend plugins needed by the
runner. The source and build directories remain
inside the container and are not runtime dependencies. As with every native GPU program, the host still supplies the
ELF loader/libc and the kernel-facing vendor driver (or Gemstone's interposed provider); those are recorded explicitly
in `manifest.tsv` and are not confused with the build SDK:

```console
conformance/unified/tools/container-build.sh host
conformance/unified/tools/container-build.sh cuda
conformance/unified/tools/container-build.sh hip
conformance/unified/tools/container-build.sh sycl
```

Artifacts default to `target/unified-artifacts/<backend>`. The image names are pinned defaults and can be replaced with
an approved local digest (useful on an air-gapped or GPU host):

```console
GEMSTONE_HIP_IMAGE=e52c187e7f93 \
  conformance/unified/tools/container-build.sh hip target/unified-artifacts/hip
```

Run a relocated runner without the build container:

```console
conformance/unified/tools/run-relocated.sh target/unified-artifacts/hip hip --list
conformance/unified/tools/run-relocated.sh target/unified-artifacts/hip hip \
  --suite=algorithms --algorithm-sizes=0,1,31 --quiet-pass

```

CTest schedules individual workload cases concurrently. `GEMSTONE_EXECUTION_PARALLELISM` controls the shared
interpreter pool and the configured CTest job count. Without an override, CTest uses the available CPU count and
the interpreter leaves two logical CPUs for RPC and GC, with at least one worker and at most 64. Scheduling
thresholds are internal, derived from worker count. Oracle cases lock their physical GPU resource.
See [configuration](../README.md) for the common build and execution controls.

HIP fatbinary registration is lazy: the runtime records host-function bindings
without installing every embedded code object. The selected module is loaded
on first use, and the server reuses a bounded cache of immutable AMDGPU indexes
while keeping mutable globals session-local. This is important for the HIP
matrix because a typical invocation registers dozens of fatbinaries but dispatches
only a few of them.

For the SYCL artifact, the wrapper also sets `UR_ADAPTERS_FORCE_LOAD` to the
bundled Level Zero adapter. This is required because the oneAPI UR loader does
not discover adapters from an arbitrary relocated directory; set that variable
explicitly when selecting the bundled OpenCL adapter instead.

Set `GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1` to run the individual CTest cases in the build container before installation. This
is appropriate for the dependency-free host target and for a vendor oracle when the container is started with the
required GPU devices. Pass those devices as a comma-separated
`GEMSTONE_CONTAINER_DEVICES` value (for example,
`/dev/kfd,/dev/dri` for ROCm or `nvidia.com/gpu=all` through Podman CDI). Gemstone/emulator runs use the relocated
runner on the host, so the build container may be removed immediately after the artifact is produced.
`GEMSTONE_UNIFIED_BUNDLE_RUNTIME=0` is available for deliberately host-supplied vendor runtimes, but the default
bundles vendor user-space dependencies. Existing artifact directories are intentionally rejected; remove or rename one
explicitly before rebuilding so a stale binary can never be mistaken for a fresh container result.
The resulting bundle inherits the license terms of the selected vendor image and its redistributable runtime libraries;
it is intended as a local conformance artifact unless those terms permit redistribution.

The relocation step walks the complete ELF dependency closure (including CUDA libraries under `/usr/local/cuda`),
creates SONAME aliases for copied libraries, and fails on an unresolved non-driver dependency. `run-relocated.sh`
replaces rather than appends `LD_LIBRARY_PATH` and clears inherited SDK/toolchain roots, so a run cannot silently fall
back to a host ROCm, CUDA, or oneAPI installation; explicit provider selectors such as `OCL_ICD_VENDORS` remain under
caller control. The only external runtime boundary is the host libc/loader and
the kernel-facing GPU driver (plus its ordinary OS data such as the DRM ASIC table where the driver requires it).

The low-level CMake project remains available for consumers and for debugging a container image:

```console
cmake -S conformance/unified -B target/unified-debug -DCMAKE_BUILD_TYPE=Release
cmake --build target/unified-debug --parallel
ctest --test-dir target/unified-debug --output-on-failure --parallel
```

CTest uses the compact profile by default (`i32,f32` and boundary-sized
workloads) so an ordinary developer build stays quick. Release qualification
can select the complete scalar/size matrix reproducibly:

```console
cmake -S conformance/unified -B target/unified-full \
  -DUNIFIED_SUITE_CTEST_PROFILE=full
cmake --build target/unified-full --parallel
ctest --test-dir target/unified-full --output-on-failure --parallel
```

`Configuration` defines compact and full profile data once; direct runners accept `--profile=compact|full`, and
CTest passes the selected profile to the same runner. Explicit size/type/shape overrides take precedence regardless
of CLI order. CUDA, HIP and SYCL use identical portable matrices: full algorithms include both 1024 and 4096, and
full edge BLAS includes 1023 and 1024. Unsupported capabilities produce explicit `SKIP` results.

The `UNIFIED_SUITE_*` matrix cache settings are optional overrides, empty by default. Common overrides apply to every
backend. `UNIFIED_SUITE_EDGE_BLAS_SIZES` can override edge BLAS specifically; otherwise it follows the common edge-case
sizes/profile. The former `UNIFIED_SUITE_SYCL_ALGORITHM_SIZES`, `UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES`, and
`UNIFIED_SUITE_CTEST_SHARDS` settings are rejected. Remove those entries when reusing an old CMake cache.

The integration lifecycle checks portable case identities, parameters and deadlines across the available host,
CUDA, HIP and SYCL artifacts before executing workloads. Backend API extensions and `edge-cases/runtime/...` probes
are separate. Runtime probes have fixed geometry encoded in their names and carry CTest's `fixed-api-probe` label;
they intentionally ignore the portable size/type options. The direct HSA/OpenCL runners provide these API probes;
HIP and SYCL provide the corresponding portable algorithm/BLAS coverage.

Full emulation qualification also needs an explicit server execution envelope. Large edge GEMMs exceed the compact
100-million-instruction default; the aligned validation uses `GEMSTONE_EXECUTION_MAX_INSTRUCTIONS=2000000000` for
all backends. This changes the emulator guard, not the problem sizes, data, tolerances or oracle execution.

The vendor targets are intentionally separate build trees because CUDA, HIP, and SYCL have different compiler drivers;
the container wrapper selects the corresponding compiler and SDK automatically. CUDA builds target `sm_75` and HIP
builds target `gfx900` by default, rather than discovering an architecture from the host; set
`UNIFIED_SUITE_CUDA_ARCHITECTURES` or `UNIFIED_SUITE_HIP_ARCHITECTURES` for a different device matrix.

The SYCL target links the oneMKL BLAS component explicitly. It does not use the convenient `-qmkl=parallel` umbrella,
which would add dynamic dependencies on unrelated DFT, LAPACK, RNG, sparse, statistics, VM, and data-fitting libraries.
It also enables per-kernel SYCL device-code splitting. This is important for the large catalog: an exact case or CTest
case must not make the driver JIT every unrelated algorithm kernel in the executable.

The installed library remains header-only. The supplied integration executables are deliberately multi-translation-
unit programs: algorithms, BLAS, edge cases, and bounded stress have independent catalog builders, and the especially
template-heavy algorithm catalog is further split by scalar type. `cmake --build ... --parallel` can therefore compile
vendor templates concurrently, incremental changes rebuild only the affected family, and no single compiler process
must instantiate the entire catalog. This is only the build shape of the supplied runners; consumers do not acquire a
compiled-library dependency.

As a header-only dependency, consumers may use `add_subdirectory`, `FetchContent`, or the installed package target:

```cmake
find_package(unified_suite CONFIG REQUIRED)
target_link_libraries(my_conformance_program PRIVATE unified_suite::unified_suite)
```

## Case discovery and configuration

Every vendor executable has the same interface:

```console
./unified_suite_cuda --list
./unified_suite_cuda --case=algorithms/stable-sort-by-key/i32/n257
./unified_suite_cuda --filter='^(algorithms/(reduce|.*scan)|blas/gemm)/'
./unified_suite_cuda --algorithm-sizes=0,1,127,128,129,4093 \
  --algorithm-types=i8,i32,i64,f32,f64
./unified_suite_cuda --blas-sizes=1,64,1024 --matrix-shapes=2x2x3,31x17x65 \
  --blas-types=f16,f32,f64
./unified_suite_cuda --suite=edge-cases --edge-case-sizes=0,1,31,32,33,257 \
  --edge-algorithm-types=i32,f32,f64 --edge-blas-types=f32,f64
./unified_suite_cuda --suite=async --edge-case-sizes=0,1,31,32,33,257 \
  --edge-algorithm-types=i32,f32
./unified_suite_cuda --suite=stress --stress-sizes=4096,65536 --stress-iterations=16 \
  --stress-types=i32,f32
```

`--sizes` and `--types` set all suites when that is convenient. Unsupported combinations produce explicit `SKIP`
results rather than silently selecting another implementation.

Intel Arc FP64 emulation must be opted into when requesting `f64` on hardware without native FP64:

```console
OverrideDefaultFP64Settings=1 IGC_EnableDPEmulation=1 \
  ./unified_suite_sycl --suite=edge-cases --edge-case-types=f64
```

SYCL individual CTest cases set these variables by default. Configure with
`-DUNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION=OFF` to disable that behavior; direct executable invocations still use
the caller's environment.

The exact same executable can first run against a physical GPU and then through an emulator or interposed provider.
Only process launch and environment should differ.

For HIP artifacts, the relocation wrapper sets `ROCBLAS_TENSILE_LIBPATH` to the
bundled `lib/rocblas/library` directory. The directory is part of the runtime
closure (not an optional build-time cache): rocBLAS loads its Tensile kernels
through this path at runtime, and a partial copy can fail before Gemstone sees
the first dispatch.

## Migrated boundary cases

The normal CUDA and HIP runtime catalogs share direct and indirect device-function calls over 257 lanes, with four
per-lane indirect targets, signed inputs, and boundary canaries. SYCL adds buffer/accessor local-memory barriers and
device-USM event/offset checks on both providers. Direct OpenCL probes compile, dispatch and reload program binaries;
direct HSA probes load an embedded code object, submit an AQL packet and copy after completion. Native CUDA
symbol-surface and invalid-token checks live under `runtime/cuda/test`.

Build direct runners with `container-build.sh opencl` and `container-build.sh hsa`.
`UNIFIED_SUITE_HSA_ARCHITECTURE` selects the single embedded HSA target (default `gfx900`).
All focused cases use `edge-cases/runtime/...` and have a dedicated CTest entry. The container exports its complete
CTest inventory for sbt; see [shared conformance configuration](../README.md).

## Coverage limits

Retained runtime probes cover device functions, HSA dispatch/copy, OpenCL compilation/binaries and SYCL
USM/accessor dependencies. They do not replace upstream compile-only/SFINAE, allocator/container or
random-distribution suites. Exhaustive image/sampler/interop, SVM ordering and CUDA array/pitched-copy/
callback combinations are not asserted as equivalent coverage. Selected upstream CTS cases use the same
[conformance pipeline](../README.md); their presence is not full CTS certification.

Provider overrides must fail when their requested library is missing. Artifacts retain their runtime closure;
source/container paths are not execution dependencies. Host C++ ABI libraries remain host-provided where
required by the installed driver. Do not reinterpret skips or oracle-only/emulator-only runs as paired acceptance.

## Regression probes

`--include-regressions` adds self-contained CUDA big-index, CloverLeaf barrier/shared-reduction, and rocBLAS f16
probes. Their identities start with `regressions/`. These run inside the same vendor-built runner; there is no
external legacy-binary directory or imported test inventory. `--expect-reds` is a diagnostic mode and is never
used by the paired acceptance runner.

CTest discovers cases using `--list` and invokes each with `--case=<exact-name>`. A single unsupported case returns
77 (`SKIP_RETURN_CODE`). Hash partitions are no longer part of the runner or CTest inventory. Gemstone's integration
bridge consumes the exported inventory and creates independent oracle/Gemstone CTest variants with host-profile GPU locks.
