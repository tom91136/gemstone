# Unified GPU conformance suite

`unified_suite` is a header-only C++20 workload-conformance library. It runs the same deterministic algorithm and
BLAS cases through three normal vendor programming stacks:

| Executable | Algorithms | Async dependencies | BLAS |
| --- | --- | --- | --- |
| `unified_suite_cuda` | NVIDIA Thrust | CUDA streams/events | cuBLAS |
| `unified_suite_hip` | rocThrust | HIP streams/events | rocBLAS |
| `unified_suite_sycl` | oneDPL device policies over SYCL USM | SYCL queues/events | oneMKL |

The shared headers own case identities, problem generation, independent host references, validation, configuration,
reporting, filtering, and sharding. Vendor adapters contain only allocation, transfer, synchronization, and matching
library calls. The project does not vendor any vendor implementation or unit-test tree.

This is intentionally a GPU workload-conformance suite, not a claim that it replaces every compile-time, overload,
allocator, device-standard-library, or regression test in the upstream projects.

## Coverage

The algorithm catalog contains 77 parameterized semantic forms. It covers copying, uninitialized construction,
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

All sizes, matrix shapes and scalar-type selections are runtime configurable. With default types and sizes, the
portable catalog contains 1,812 stable cases: 1,386 algorithm, 141 BLAS, 98 portable async, 175 edge-case, and 12
stress cases. The HIP target adds 72 HIP-specific async cases and 39 ROCm-specific iterator/set-operation probes, for
1,923 listed HIP identities. These HIP-only forms are kept out of the CUDA, host, and SYCL counts because those
overloads are not portable.

This is broad runtime-semantic coverage, not a replacement for every upstream compile-time library test. Compile-time
overload/SFINAE coverage and vendor regression tests that do not express portable runtime behavior remain upstream.

### Legacy-suite retirement boundary

The old vendor runners still carry coverage that has no honest unified equivalent: CUDA/HIP-specific async and
allocator paths, device-pointer and iterator-type contracts, placeholder and functional-object compile-time checks,
sequence and vector container tests, random-engine/distribution contracts, and vendor-specific keyed set-operation
overloads. The imported `legacy-red/...` catalog below is a bounded regression inventory for the reds observed in the
old emulator runs; it is not a claim that all of that vendor-specific coverage has been ported. These cases must either
remain in an API-specific regression suite or be ported as explicit capability tests before the legacy runners are
deleted.

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
GEMSTONE_UNIFIED_HIP_IMAGE=e52c187e7f93 \
  conformance/unified/tools/container-build.sh hip target/unified-artifacts/hip
```

Run a relocated runner without the build container:

```console
conformance/unified/tools/run-relocated.sh target/unified-artifacts/hip hip --list
conformance/unified/tools/run-relocated.sh target/unified-artifacts/hip hip \
  --suite=algorithms --algorithm-sizes=0,1,31 --quiet-pass

# Emulator-only edge GEMMs use an explicit server-side budget profile. The
# release profile uses n1024; n4096 is retained as a separately documented
# throughput diagnostic and is not part of the normal matrix.
GEMSTONE_EXECUTION_MAX_INSTRUCTIONS=500000000 \
  gemstone run --api hip --hip-runtime "$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/libamdhip64.so.7" \
  --hsa-runtime "$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/hsa-runtime/libhsa-runtime64.so.1" -- \
  "$PWD/target/unified-hip-debug10/unified_suite_hip" \
  --case=edge-cases/blas/padded-gemm/f32/n1024 --edge-case-sizes=1024 --edge-blas-types=f32
```

Interpreter work-group parallelism is opt-in and bounded per Gemstone JVM
server. `GEMSTONE_EXECUTION_PARALLELISM=N` shares one ForkJoinPool across all
authenticated sessions, accepts values from `1` through the host's logical
processor bound, and never creates one thread per work-group. The default is
the host logical-processor count minus two (minimum one), leaving capacity for
RPC, launcher, and GC work. HIP/HSA uses a two-worker execution pool by default
because ROCm/HSA code-object installation is more memory-intensive; its matrix
fan-out is one. CUDA and SYCL use the host-minus-two pool and fan-out. Start with
`4` or `8` for a deliberately smaller run; the pool is also used for
dispatch-queue submission, so separate sessions cannot multiply the configured
worker count.
`GEMSTONE_EXECUTION_PARALLEL_MIN_GROUPS` and
`GEMSTONE_EXECUTION_PARALLEL_MIN_WORK_ITEMS` tune the profitability gate: a
kernel is submitted to the pool only when it has enough groups or enough local
work per group. Both default to conservative values derived from the worker
count (`4*N` groups and 64 local work-items).
The execution budget remains shared atomically across work-group workers, and
SPIR-V global-memory/atomic bookkeeping is serialized at its owning memory
state. This is a throughput feature, not a way to make one intrinsically
large n4096 dispatch cheap.

HIP fatbinary registration is lazy: the runtime records host-function bindings
without installing every embedded code object. The selected module is loaded
on first use, and the server reuses a bounded cache of immutable AMDGPU indexes
while keeping mutable globals session-local. This is important for the HIP
matrix because a typical shard registers dozens of fatbinaries but dispatches
only a few of them.

For the SYCL artifact, the wrapper also sets `UR_ADAPTERS_FORCE_LOAD` to the
bundled Level Zero adapter. This is required because the oneAPI UR loader does
not discover adapters from an arbitrary relocated directory; set that variable
explicitly when selecting the bundled OpenCL adapter instead.

Set `GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1` to run the CTest shards in the build container before installation. This
is appropriate for the dependency-free host target and for a vendor oracle when the container is started with the
required GPU devices. Pass those devices as a comma-separated
`GEMSTONE_UNIFIED_CONTAINER_DEVICES` value (for example,
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

The full profile expands the configured sizes, matrix shapes, scalar types, and
bounded stress inputs; it does not change the catalog or silently add host
fallbacks. Backend capability differences remain explicit `SKIP` results.

The vendor targets are intentionally separate build trees because CUDA, HIP, and SYCL have different compiler drivers;
the container wrapper selects the corresponding compiler and SDK automatically. CUDA builds target `sm_75` and HIP
builds target `gfx900` by default, rather than discovering an architecture from the host; set
`UNIFIED_SUITE_CUDA_ARCHITECTURES` or `UNIFIED_SUITE_HIP_ARCHITECTURES` for a different device matrix.

The SYCL target links the oneMKL BLAS component explicitly. It does not use the convenient `-qmkl=parallel` umbrella,
which would add dynamic dependencies on unrelated DFT, LAPACK, RNG, sparse, statistics, VM, and data-fitting libraries.
It also enables per-kernel SYCL device-code splitting. This is important for the large catalog: an exact case or CTest
shard must not make the driver JIT every unrelated algorithm kernel in the executable.

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

SYCL CTest shards set these variables by default. Configure with
`-DUNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION=OFF` to disable that behavior; direct executable invocations still use
the caller's environment.

The exact same executable can first run against a physical GPU and then through an emulator or interposed provider.
Only process launch and environment should differ.

For HIP artifacts, the relocation wrapper sets `ROCBLAS_TENSILE_LIBPATH` to the
bundled `lib/rocblas/library` directory. The directory is part of the runtime
closure (not an optional build-time cache): rocBLAS loads its Tensile kernels
through this path at runtime, and a partial copy can fail before Gemstone sees
the first dispatch.

## Imported legacy reds

The old CUDA Thrust and ROCm rocThrust runners produced a large number of
Gemstone-only failures. The unified catalog carries only identities reproduced
by the current container-built upstream binaries: all 48 imported CUDA Thrust
identities are now recorded as fixed, and all 50 AMD rocThrust/HSA cases are
recorded as strict regressions. The latest HIP emulation sweep passes 49/51;
`random` and `unittest_static_assert` remain failures pending investigation.
The two formerly failing CUDA identities are also decomposed into the
eight tested 64-bit big-index magnitudes as ordinary semantic unified cases. The complete 317-entry AMD historical inventory is
retained as `roc_historical`; 72 cases whose bounded probe timed out remain in
`legacy_reds::deferred`. The formerly red rocBLAS half-precision AXPY case is
carried as a direct unified BLAS call and now runs as a strict fixed regression.
No upstream implementation is vendored into the new
runner; the executable root is supplied explicitly for this regression probe.
The CUDA big-index entries are reconstructed in the Thrust edge-case adapter
so their iterator and launch contract is tested directly as well as through
the original executable.

The CUDA mini-app reduction probes are also available as opt-in legacy-red
regressions. They reproduce CloverLeaf's 256-thread shared-memory reduction,
including the case where divergent cohorts reach the same `bar.sync` slot from
different PTX program counters, and verify the result on the host. Both the
physical CUDA oracle and the current Gemstone emulation path pass them. Run the
focused probe with:

```console
./unified_suite_cuda --case=legacy-red/miniapp/cloverleaf/cuda-divergent-barrier-id \
  --include-legacy-reds
```

The original single-array reduction remains available as
`legacy-red/miniapp/cloverleaf/cuda-shared-reduction`; both are strict
diagnostics and report failures normally.

The HIP runner also includes 39 ordinary `algorithms/roc-thrust-semantic/*`
probes for keyed/descending/key-value set operations, duplicate handling,
binary-search and gather variants, permutation/zip/pair/tuple iterators,
async-adjacent reductions, and larger stable workloads. These are always
selected with the HIP `i32` algorithm family; the exact upstream executables
remain opt-in under `--include-legacy-reds`. The current emulator result is
39/39 strict pass. The cases remain ordinary tests rather than an XFAIL mask,
so future lookback/set regressions remain visible.
Key/value cases compare keys only, with values validated as selected payloads,
matching rocThrust's `key_value<Key, Value>` contract.

When included, oracle runs are strict and must pass these cases. The fixed CUDA
identities and reconstructed edge cases are ordinary strict tests. The
`--expect-reds` option remains available for future regressions:

```console
./unified_suite_cuda --suite=edge-cases --filter='^legacy-red/cuda-thrust/.*big-indexes/' \
  --include-legacy-reds
./unified_suite_hip --filter='^legacy-red/roc-thrust-fixed/' \
  --include-legacy-reds --legacy-bin-root=/path/to/rocthrust/build/testing \
  --algorithm-types=i32
./unified_suite_hip --filter='^legacy-red/(roc-thrust-fixed|rocblas)/' \
  --include-legacy-reds --legacy-bin-root=/path/to/rocthrust/build/testing \
  --algorithm-types=i32 --blas-types=f16
```

For a failing imported executable, preserve its upstream GoogleTest output
without flooding the unified runner log:

```console
./unified_suite_hip --filter='^legacy-red/roc-thrust-fixed/' \
  --include-legacy-reds --legacy-bin-root=/path/to/rocthrust/build/testing \
  --legacy-output-dir=target/legacy-reports
```

Each identity gets separate `.stdout` and `.stderr` files, making the
individual upstream subtest failures available for porting and review.

`XFAIL` means an imported case is still red as expected. `XPASS` (or `XSKIP`)
is a regression in a future red manifest: the case no longer exercises the
recorded failure and must be reviewed before removing or reclassifying it.
The formerly red CUDA entries are no longer in the active manifest and their
reconstructed cases run as ordinary strict tests.
Running without `--expect-reds` keeps the normal strict behavior and is what
the vendor oracle uses. A missing `--legacy-bin-root` is reported as an
explicit skip, never as a semantic failure. The remaining CUDA and ROCm timeout entries are listed in
`legacy_reds::deferred` and intentionally not run until their bounded behavior
is understood.

The captured Intel oneDPL/SYCL run had no emulator reds to import; its current
unified smoke baseline is recorded in `UNIFIED_EMULATION_WORK.md` rather than
being represented by fabricated expected-failure cases.

## CTest sharding

Each enabled integration executable is registered as stable hash shards. The default is eight shards and can be
changed through the container wrapper (all `UNIFIED_SUITE_*` cache variables are forwarded into its isolated CMake
configure):

```console
UNIFIED_SUITE_CTEST_SHARDS=16 GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1 \
  conformance/unified/tools/container-build.sh host target/unified-artifacts/host
```

Each shard has a configurable timeout (`UNIFIED_SUITE_CTEST_TIMEOUT`, 120 seconds by default) so slow device JITs
remain bounded in both the container CTest run and any low-level CMake build.

The full profile keeps n1024 in the shared edge-algorithm matrix and for CUDA/HIP edge BLAS. SYCL uses the separate
`UNIFIED_SUITE_SYCL_ALGORITHM_SIZES` default, which stops the linear algorithm matrix at n1024, and the separate
`UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES` default, which stops edge BLAS at n257 for the CPU emulator. Override either
explicitly when an Intel GPU or a deliberately long emulation diagnostic is available. These backend-specific bounds
are workload limits, not capability skips: n4096 SYCL algorithm and n1024 SYCL GEMM probes remain reproducible opt-in
diagnostics, while the smaller cases stay in the normal matrix.

Algorithm, BLAS, async, edge-case, and stress shards are separate CTest tests. Their matrices are CMake cache settings; the
container wrapper forwards the same variables while keeping the configure and compiler inside the image:

```console
UNIFIED_SUITE_CTEST_SHARDS=16 \
UNIFIED_SUITE_ALGORITHM_SIZES=0,1,31,32,33,1021,1024,1025 \
UNIFIED_SUITE_ALGORITHM_TYPES=i8,i32,i64,f32,f64 \
UNIFIED_SUITE_BLAS_SIZES=1,64,1024 \
UNIFIED_SUITE_MATRIX_SHAPES=2x2x3,31x17x65 \
UNIFIED_SUITE_BLAS_TYPES=f16,f32,f64 \
UNIFIED_SUITE_EDGE_CASE_SIZES=0,1,31,32,33,257 \
UNIFIED_SUITE_EDGE_ALGORITHM_TYPES=i32,f32,f64 \
UNIFIED_SUITE_EDGE_BLAS_TYPES=f32,f64 \
UNIFIED_SUITE_STRESS_SIZES=4096,65536 \
UNIFIED_SUITE_STRESS_TYPES=i32,f32 \
UNIFIED_SUITE_STRESS_ITERATIONS=16 \
  conformance/unified/tools/container-build.sh cuda target/unified-artifacts/cuda
```

A shard prints every skipped or failed case plus a summary. Any case can be reproduced independently with the exact
`--case` name shown in its output. Stable-name hashing means adding a case does not shift unrelated cases between
shards for a fixed shard count.

## Adapter contracts

Algorithm adapters are ordinary classes with templated operations over `std::vector` inputs. They return host-visible
values after synchronizing the vendor operation. BLAS adapters are class templates over `ScalarType` and provide a
move-only device buffer plus the BLAS operations. See the host adapters for minimal reference implementations
and the vendor adapters for real integrations. Edge-case adapters add vendor-specific composed iterators, dependency
graphs, group primitives, bitwise round trips, and recoverable runtime errors. The bounded stress suite deliberately
reuses those adapter operations so it pressures the exact same paths without creating a second semantic vocabulary.
