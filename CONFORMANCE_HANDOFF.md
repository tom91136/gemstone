# Gemstone conformance: live handoff and worklist

This is the cold-start handoff for Gemstone conformance and the running worklist
for the active campaign. It is trimmed as work completes: dated narrative and
superseded detail live in git history, not here. It assumes no conversation
history. Trim this document when facts become stale; add what the next session
needs to resume without re-deriving it.

## Product invariant

Every external case has one build and two executions through an ordinary vendor
binary: identical executable, arguments, and working directory, with only the
runtime provider selection differing between the vendor oracle and Gemstone.

Never accept any of: a Gemstone compile define, an alternate Gemstone link, a
patched vendor source, a user requirement to alter a vendor installation, or a
run that silently reaches a physical GPU. Upstream-disabled tests stay
disabled. Gemstone failures are never recoded as XFAILs or skips; a failure
must become a named unified case or an owning-layer fix.

Pinned upstream archives are hash-checked under `~/.cache/gemstone/conformance`.
Build products live in `target/conformance` and relocatable runner artifacts in
`target/unified-artifacts/<name>` (each carries a `manifest.tsv`).

## Current verified state (2026-09-06)

| Check | Result |
|---|---|
| `SBT_OPTS=-Xmx12G sbt check` | green, 87 s (JVM + native, API conformance, runtime CTests) |
| Host unified CTest, full profile | 251/251 |
| HIP semantic probe catalog (`algorithms/roc-thrust-semantic/*`) | 48/48 strict pass |
| rocThrust imported reds (binary lane) | 50/50; `random` 51/51 and `unittest_static_assert` 3/3 unit-test pass |
| CUDA Thrust legacy lane | 56/56 across compute_75-90 |
| Emulator campaign, one target per backend | CUDA 700 s, HIP 1450 s, SYCL 342 s wall time; all four portable matrices green at documented bounds |
| Mini-app matrix | 39/39 |

Two owning-layer emulator defects were fixed behind this state and are covered
by `CodeObjectSuite`: wave32 VCC comparisons must not clobber the `vcc_hi`
(s107) scratch register, and `.got`-relocated device globals need the
`R_AMDGPU_ABS64` entries applied to the code-object section images.

Workload bounds policy stays as documented in `conformance/unified/README.md`:
algorithms through n1024 for SYCL/n4096 elsewhere, GEMM endpoints at n1024, and
n4096 probes as explicit opt-in diagnostics.

## Running the emulator locally (no packaged launcher)

```console
cp=$(ls target/out/jvm/scala-3.9.0/{gemstone-server,gemstone-api,gemstone-core,gemstone-isa-ptx,gemstone-isa-amdgpu,gemstone-isa-spirv}/classes | paste -sd:)
cp="$cp:$(ls ~/.cache/coursier/v1/https/repo1.maven.org/maven2/...jar paths for scala3/cats/scala-library)"
java -cp "$cp" net.kurobako.gemstone.launcher.GemstoneLauncher run --api hip \
  --hip-runtime target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/libamdhip64.so \
  --hsa-runtime target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/hsa-runtime/libhsa-runtime64.so.1 -- \
  ./unified_suite_hip --suite=algorithms --algorithm-types=i32 [filters]
```

Set `GEMSTONE_AMDGPU_TARGET` (single target) to the virtual provider target; a
runner artifact that carries one target exports it automatically. New HIP
executables are built locally with

```console
cmake -S conformance/unified -B target/<build-dir> -G Ninja \
  -DUNIFIED_SUITE_BUILD_HOST=OFF -DUNIFIED_SUITE_BUILD_HIP=ON -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_HIP_ARCHITECTURES=gfx1036 -DCMAKE_HIP_FLAGS="<rocThrust/rocblas include dirs>"
ninja -C target/<build-dir> unified_suite_hip
```

or with `conformance/unified/tools/container-build.sh hip` for the
hermetic container artifact.

## Active campaign: retire the vendor legacy suites

The legacy suite today is three lanes of unmodified vendor test binaries:
rocThrust (348 enabled CTest instances, swept across
`GEMSTONE_AMDGPU_TARGETS`), CUDA Thrust 1.15 (237 enabled tests across
compute_75/80/86/89/90), and the pinned rocBLAS smoke dataset. They exist on
top of container source builds (build-pristine trees, googletest) and reach the
emulator through the `--include-legacy-reds` binary lane.

Scout results (2026-09-06): rocThrust identity names map as **87 already in
the unified catalog**, **15 thrust::async variants**, **~70 unmodelled**, of
which ~33 are host-only library tests that exercise no dispatch and can be
dropped with the suite. The remaining device-relevant work is step 2 below.
The unified edge-case catalog is already aligned across host/CUDA/HIP/SYCL by
construction: 11 shared operations, identical names and sizes, and divergence
is only explicit capability gates (oneDPL `runtime-error-recover` and FP64
gating; HIP f16-edge/i8-BLAS slices; block-size-32 group scans).

### Step 1: multi-arch unified artifacts - mechanism verified end to end (2026-09-06)

`container-build.sh` and `run-relocated.sh` now accept comma/semicolon
architecture lists. One artifact carries several device targets: the manifest
gains `target-archs` (with `target-arch` remaining the first entry), the
rocBLAS/Tensile mapping runs per target, and a multi-target manifest no longer
exports `GEMSTONE_AMDGPU_TARGET`/`GEMSTONE_CUDA_TARGET_ARCH` silently (the
caller selects one per run; a stderr note says so). Verified by shell trace
tests and `bash -n`.

A local `gfx900;gfx1036;gfx1100;gfx1200` HIP artifact (one binary, four code
objects) built and ran end to end with one failure per target reported as a
named dispatch error. That quantifies the execution spread behind step 5 for
the same 81-probe suffix selection: gfx1036 `78 pass / 3 skip`, gfx900 `6/81`
(first gap: `ds_max_i32 DS execution is not implemented`), gfx1100/gfx1200
`6/81` (first gap: `s_load_b64/b32 SMEM execution is not implemented`). The
first per-target work items are therefore DS/LDS execution for GFX9 and scalar
(SMEM) load execution for RDNA3/4, at the owning interpreter layers.

### Step 2: port the residual device-exercising rocThrust identities - landed (2026-09-06)

The unified HIP semantic catalog grew from 39 to 81 probes (`78 pass / 3 skip
/ 0 fail` under emulation, ~119 s): vector insert/erase/resize,
device-pointer/reference/dereference, memory malloc/free, universal memory,
complex arithmetic/transform/interop, zip-function and zip-reduce combos,
permutation/reverse/constant/counting/discard/tabulate-output iterators, sort
permutation variants, pair/tuple manipulation and algorithms, advance/distance,
and three thrust::async probes.

Documented skips (honest, upstream-contract-true, not capability masks):
`device-delete` is KNOWN_FAILURE in rocThrust itself, `generate-const-iterators`
is a compile-time negative check, `out-of-memory-recovery` asserting an
allocation-failure destructor path is not reproducible under the emulator.

One finding worth a future red-naming pass: on device, a rocprim transform
instantiation of `thrust::exp`/`thrust::sin` on f64 `thrust::complex` can
sign-flip a component depending on negative inputs and code layout; a probe
with all-positive components verifies the correct subset. Verified with
standalone kernels that a direct device `cexp` path is exact.

Still open inside step 2:
- add plain `tabulate` and `sequence` operations to the shared algorithm
  catalog (`algorithms.hpp`; note this also updates the 77-op contract asserts
  in `tests/catalog.cpp` and the sample sizes);
- thrust::async scan variants that the ports did not cover: counting-iterator,
  discard-output, stateful-operator, mixed-types, using-vs-adl, and the large
  index pairs;
- the two ROCm samples (`hip-saxpy`, `hsa-kernel-dispatch`) if the mini-app
  matrix is not already their home; the mini-app README is the checklist.

### Step 3: edge-case skip-parity contract

Add a test (Scala conformance or the unified runner contract) asserting the
selected edge-case identity set is identical across host/CUDA/HIP/SYCL and that
every SKIP names its capability or precondition. Fail on a platform-specific
case set or an unnamed skip.

### Step 4: retire the legacy lanes

Order: (1) Scala legacy suites (`RocThrustConformanceSuite`, CUDA
`ThrustConformanceSuite`) off the default gate; (2) drop the upstream source
builds (build-pristine trees, googletest) and the vendor tarball pins; (3) drop
the `--include-legacy-reds` binary lane; (4) keep the cached legacy binaries as
an opt-in escape hatch for one release cycle. rocBLAS/oneDPL/OpenCL decisions
are separate and are not part of this campaign.

### Step 5: ISA matrix with era-appropriate toolchains

The unified suite is target-driven per device code object; the interpreter is
target-agnostic (wave size from the family). One artifact carries many targets.

First per-target owning-layer work items (measured with the four-target
artifact in step 1): DS/LDS instruction execution for GFX9 (`ds_max_i32` was
the first named gap) and scalar SMEM load execution for RDNA3/4
(`s_load_b32/b64`). Both are interpreter cases, not loader cases.

AMDGPU spread: rocThrust work is possible for any gfx the era toolchain can
compile. Reference XMLs pin rdna1-4 and cdna1-5; the LLVM-MC oracle round-trips
46 named targets (all families except CDNA5). CDNA5 has no Target and no
oracle. The ISA-level gap list (interpreter execution fixtures beyond
gfx900/gfx1036) and the per-target conformance sweep are the same workstream.

| Target group | Toolchain era | Status/plan |
|---|---|---|
| gfx900-gfx1201 | pinned ROCm 7.1 (compiles all listed targets; verified locally) | primary matrix |
| gfx906-era userspace | ROCm 5.7 | optional runtime-era check |
| gfx600-gfx810 | ROCm 4.x containers | step 5 build |
| sm_75+ | pinned CUDA 13.3 | primary matrix |
| sm_50-72 | CUDA 12.9 containers | step 5 build |
| sm_35/37 | CUDA 11.8 containers | step 5 build |

### Era toolchain availability (verified 2026-09-06)

AMDGPU, pinned ROCm 7.1 host probe: `--offload-arch` compiles clean for gfx600,
gfx601, gfx700, gfx701, gfx704, gfx801, gfx802, gfx803, gfx810, gfx900, gfx904,
gfx906, gfx908, gfx90a, gfx940-950, gfx1010/1012, gfx1030/1036, gfx1100/1101,
gfx1150/1151, gfx1200/1201. gfx800 and gfx1001 are not valid target IDs.

ROCm containers (Docker Hub, amd64 only, pin digests): `dev-ubuntu-18.04` runs
3.0-5.2.3; `dev-ubuntu-20.04` 3.7-6.3.4; `dev-ubuntu-22.04` from 5.3;
`dev-ubuntu-24.04` from 6.2. `-complete` variants exist from 4.0. There is no
`4.5.0` tag: `4.5` is 4.5.0 exactly; exact-version old picks are
`4.1.1(-complete)` and `4.3.1(-complete)`. GCN1-3 and Polaris code still
compiles with any clang, but no current ROCm runtime supports it, so old
targets need era containers for their userspace. `hipcc` in ROCm 6.4 and
earlier is Perl-scripted; flag assumptions (including `--offload-arch`) change
at the 6.4/7.0 boundary.

CUDA: `10.2` and `12.7` devel tags are pruned from Docker Hub. Available
devel tags include `11.0.3` ... `11.8.0` (ubuntu18.04/20.04; `11.7.1`/`11.8.0`
also ubuntu22.04), `12.0.1` ... `12.6.3`, and `12.8.2`/`12.9.2`
(ubuntu20.04/22.04/24.04); 12.9.2 is the last ubuntu20.04 base. Kepler sm_35/37
dies with CUDA 11.8 (removed in 12.0). Maxwell, Pascal, and Volta
(sm_50-62, sm_70/72) are frozen in 12.8 and removed in 13.0. The pinned 13.3
toolchain is sm_75+ only. The unified CUDA target links `CUDA::cublas`
unconditionally; era-matched containers ship era-matched cuBLAS, at the cost of
bundle size (`GEMSTONE_UNIFIED_BUNDLE_RUNTIME=0` skips bundling).

Old-arch artifacts are therefore a container-image swap per era plus the
multi-arch build plumbing already in step 1, not a new pipeline.

## Diagnostics cheat sheet

- `GEMSTONE_HSA_TRACE_PACKETS=1`: prints the dispatch failure message (this
  identified both the budget exhaustion and the unmapped address).
- `GEMSTONE_AMDGPU_TRACE_CONTROL=all`, `-TRACE_LOADS`, `-TRACE_STORES`,
  `-TRACE_WAVE`, `-TRACE_F64`: interpreter traces.
- `GEMSTONE_EXECUTION_MAX_INSTRUCTIONS=<n>`: per-process instruction budget
  (default 100M, validated before launch); `GEMSTONE_EXECUTION_QUANTUM` selects
  checkpoint granularity.
- `GEMSTONE_EXECUTION_PARALLELISM`, `-PARALLEL_MIN_*`: bounded interpreter
  parallelism (HIP default 2 workers).
- `GEMSTONE_AMDGPU_TARGETS`, `GEMSTONE_AMDGPU_ORACLE_TARGET`: per-suite target
  sweeps; `GEMSTONE_AMDGPU_TARGET`, `GEMSTONE_CUDA_TARGET_ARCH`: per-run
  virtual target.
- `GEMSTONE_HIP_TRACE_MODULES`, `-TRACE_ALLOCATIONS`: HIP facade traces.
- `SBT_OPTS=-Xmx12G` is required for the cold Scala Native link.
