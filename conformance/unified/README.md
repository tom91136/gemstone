# Unified GPU conformance suite

`unified_suite` is a header-only C++20 workload-conformance library. It runs the same deterministic algorithm and
BLAS cases through three normal vendor programming stacks:

| Executable | Algorithms | BLAS |
| --- | --- | --- |
| `unified_suite_cuda` | NVIDIA Thrust | cuBLAS |
| `unified_suite_hip` | rocThrust | rocBLAS |
| `unified_suite_sycl` | oneDPL device policies over SYCL USM | oneMKL |

The shared headers own case identities, problem generation, independent host references, validation, configuration,
reporting, filtering, and sharding. Vendor adapters contain only allocation, transfer, synchronization, and matching
library calls. The project does not vendor any vendor implementation or unit-test tree.

This is intentionally a GPU workload-conformance suite, not a claim that it replaces every compile-time, overload,
allocator, device-standard-library, or regression test in the upstream projects.

## Coverage

The algorithm catalog contains 74 parameterized semantic forms. It covers copying, generation, mutation, unary and
binary transformation, replacement/removal, reductions, inner product, ordinary and transformed scans, sorting,
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

The separate `edge-cases` catalog targets uncommon but bounded semantics: composed fancy iterators, indices above
32 bits, singleton/giant/workgroup-boundary segments, in-place scans with canaries, stable payload association,
multi-stream or multi-queue dependency diamonds, bit-preserving numeric round trips, CUB/rocPRIM/SYCL workgroup
scans, runtime-error recovery where the API exposes it, negative BLAS increments, padded leading dimensions, and
`beta=0` GEMM with uninitialized/NaN C storage.

The `stress` catalog is deliberately distinct. It repeats the dependency, primitive-scan, and numeric-roundtrip
workloads over larger sizes, but is bounded by explicit size and iteration parameters. Edge cases remain quick
semantic checks; stress cases are the place for longer-running kernel and resource-lifetime pressure.

All sizes, matrix shapes and scalar-type selections are runtime configurable. With default types and sizes, the
catalog contains 1,557 stable cases: 1,332 algorithm, 38 BLAS, 175 edge-case, and 12 stress cases.

This is broad runtime-semantic coverage, not a replacement for every upstream compile-time library test. Compile-time
overload/SFINAE coverage and vendor regression tests that do not express portable runtime behavior remain upstream.

## Build

The dependency-free host adapter validates the suite itself:

```console
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build --parallel
ctest --test-dir build --output-on-failure --parallel
```

Configure each vendor target with its vendor compiler/environment. They are separate build trees because CUDA, HIP,
and SYCL have different compiler drivers:

```console
cmake -S . -B build-cuda -DUNIFIED_SUITE_BUILD_HOST=OFF -DUNIFIED_SUITE_BUILD_CUDA=ON
cmake --build build-cuda --parallel

cmake -S . -B build-hip -DUNIFIED_SUITE_BUILD_HOST=OFF -DUNIFIED_SUITE_BUILD_HIP=ON \
  -DCMAKE_HIP_COMPILER=/opt/rocm/llvm/bin/clang++
cmake --build build-hip --parallel

cmake -S . -B build-sycl -DUNIFIED_SUITE_BUILD_HOST=OFF -DUNIFIED_SUITE_BUILD_SYCL=ON \
  -DCMAKE_CXX_COMPILER=icpx
cmake --build build-sycl --parallel
```

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

## CTest sharding

Each enabled integration executable is registered as stable hash shards. The default is eight shards and can be
changed at configure time:

```console
cmake -S . -B build -DUNIFIED_SUITE_CTEST_SHARDS=16
ctest --test-dir build --parallel 16 --output-on-failure
```

Each shard has a configurable timeout (`UNIFIED_SUITE_CTEST_TIMEOUT`, 1800 seconds by default) so slow device JITs
remain bounded without requiring a wrapper script.

Algorithm, BLAS, edge-case, and stress shards are separate CTest tests. Their matrices are also CMake cache settings, so CI can configure
the complete workload without wrapping the executables:

```console
cmake -S . -B build-cuda \
  -DUNIFIED_SUITE_CTEST_SHARDS=16 \
  -DUNIFIED_SUITE_ALGORITHM_SIZES=0,1,31,32,33,1021,1024,1025 \
  -DUNIFIED_SUITE_ALGORITHM_TYPES=i8,i32,i64,f32,f64 \
  -DUNIFIED_SUITE_BLAS_SIZES=1,64,1024 \
  -DUNIFIED_SUITE_MATRIX_SHAPES=2x2x3,31x17x65 \
  -DUNIFIED_SUITE_BLAS_TYPES=f16,f32,f64 \
  -DUNIFIED_SUITE_EDGE_CASE_SIZES=0,1,31,32,33,257 \
  -DUNIFIED_SUITE_EDGE_ALGORITHM_TYPES=i32,f32,f64 \
  -DUNIFIED_SUITE_EDGE_BLAS_TYPES=f32,f64 \
  -DUNIFIED_SUITE_STRESS_SIZES=4096,65536 \
  -DUNIFIED_SUITE_STRESS_TYPES=i32,f32 \
  -DUNIFIED_SUITE_STRESS_ITERATIONS=16
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
