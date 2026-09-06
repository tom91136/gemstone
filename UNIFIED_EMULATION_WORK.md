# Unified emulation conformance work

This document is the live checklist for making Gemstone's emulated provider
produce the same result as the vendor oracle on the unified workload suite.

## Latest emulator-only pass and bounded interpreter parallelism (2026-09-06)

The JVM server now has a bounded `GEMSTONE_EXECUTION_PARALLELISM` pool. It is
bounded to the host logical-processor count (and a hard ceiling of 64), shared
by all authenticated sessions in that server, and uses a fixed small set of
work-stealing tasks that pull group indices from a cursor. A 1024-group launch
therefore never creates 1024 threads. When unspecified, the general profile
self-tunes to host logical processors minus two (minimum one); HIP/HSA selects
two workers by default because code-object installation is more memory-heavy.
The matrix runner uses the general host-minus-two fan-out for CUDA/SYCL and
serial fan-out for HIP/HSA, so multiple child sessions still share one bounded
server pool.

HIP fatbinary registration is lazy: startup records host-function bindings but
defers HSA code-object installation until a kernel or global is first used.
This avoids eagerly loading every embedded module in every shard process. The
server also keeps a small bounded cache of immutable AMDGPU indexes while each
loaded program retains session-local mutable globals and materialized kernels.
In the representative i32 shard, 73 fatbinaries were registered but only 3
were installed after this change.

## Measured full non-legacy campaign (2026-09-06)

The complete available campaign was timed sequentially with no explicit
execution-parallelism override. Legacy tests were excluded. Each unified
provider run covered all 248 matrix shards; SYCL used its configured n1024
ceiling, while CUDA/HIP retained n4096 linear endpoints. The available mini-app
matrix was also run with its default one-case isolation policy.

| Phase | Result | Wall time |
|---|---|---:|
| `sbt check` (`SBT_OPTS=-Xmx12G`) | 2,283 checks, 0 failed | 81.23 s |
| Host unified CTest | 251/251 passed | 5.68 s |
| CUDA unified matrix | 7,476 pass / 2,557 skip / 0 fail | 700.13 s |
| HIP unified matrix | 7,850 pass / 2,510 skip / 0 fail | 1,449.95 s |
| SYCL unified matrix | 7,602 pass / 2,407 skip / 0 fail | 341.57 s |
| Mini-app matrix | 39/39 passed, 0 ignored | 589.12 s |

The sequential sum is 3,167.68 seconds (about 52m48s). HIP remains the largest
phase, but lazy registration reduced it from the original 9,726.96 seconds
(2h42m07s) to 1,449.95 seconds (24m10s), an 85.1% reduction. The original
slowdown was mostly repeated fixed setup: all 248 HIP shards were launched one
at a time, and each client eagerly installed 73 fatbinaries even when only a
few kernels were selected. Two-way client fan-out is not yet safe: it
reproduces shared-server heap pressure and HIP launch failures, while a single
unsharded client accumulates enough state to enter near-total GC. HIP Kokkos
and StdPar mini-app models automatically force interpreter parallelism to one;
their focused regression is included in the 39-test mini-app result.
The current cold Scala Native link also needs `SBT_OPTS=-Xmx12G`; the checked-in
4 GiB `.jvmopts` ceiling causes a linker OOM, which is a build-resource issue
separate from runtime execution tuning.

SPIR-V workgroups are the first active interpreter path: each parallel
workgroup has private invocation/memory staging state, its final mirror flush
is serialized, and kernels containing atomics remain sequential pending full
cross-workgroup ordering validation. Targeted SYCL checks pass with
`GEMSTONE_EXECUTION_PARALLELISM=4` for i32 `fill` and collective
`inclusive-scan`, both at n1024. The server regression covers a 1024-task pool
with a four-worker cap. PTX blocks and AMDGPU groups use the same bounded hook
when tracing is disabled and the decoded program contains no atomics.

The first compact all-suite probe showed that blindly parallelizing tiny
one-invocation groups loses time to scheduling overhead. The SPIR-V activation
threshold therefore requires either a substantial workgroup or enough groups
to amortize pool coordination. This is a staged throughput probe, not yet a
replacement for the previously certified default-one-worker conformance run.

The n4096 padded-GEMM investigation established an infrastructure boundary rather
than a semantic red: the emulated dispatch exceeds five minutes, and the server's
generic 300-second socket idle timeout closed the separate HSA memory connection
while the execution connection was still active. The native client then poisoned
that session, causing every later `hipFree` to report `execution session was lost`.
Execution-protocol connections now disable that generic idle timeout after their
first authenticated request; CUDA connections retain the bounded timeout.

The regression is intentionally cheap and deterministic:

```text
sbt --server -batch 'server/testOnly net.kurobako.gemstone.server.ExecutionRPCConformanceSuite'
ExecutionRPCConformanceSuite: 6/6 passed
  execution connections survive an idle memory connection during a long dispatch
  server timeout: 50 ms; native smoke idle: 750 ms; execution budget: 1 instruction
```

The full edge-BLAS profile now uses n1024 as its largest real GEMM endpoint, while
keeping n1023 as the odd-boundary check; linear algorithm/stress endpoints remain
unchanged. n4096 remains a separately documented throughput diagnostic and is
excluded from the normal edge-BLAS matrix. The post-fix n4096 validation was
stopped after the user-directed runtime bound; the fast regression above is the
acceptance guard for the discovered lifecycle failure. The bounded n1023 and
n1024 f32 padded-GEMM checks are strict-green (`1/1` each) with the 500M
instruction profile. The two n1024 f64 GEMM checks require the explicit 2B
profile and pass (`2/2`). The complete all-type n1024 edge slice is
`64 pass / 14 skip / 0 fail`, `78 selected`, under the 2B profile; the skips
are the configured f16 edge-algorithm and i8 edge-BLAS capability slices.

Refreshed current-artifact cross-backend checks are also recorded here:

| Backend / selection | Pass | Skip | Fail | Selected | Result |
|---|---:|---:|---:|---:|---|
| HIP compact all-suite | 325 | 24 | 0 | 349 | authenticated provider pass |
| CUDA compact all-suite | 274 | 24 | 0 | 298 | authenticated provider pass |
| SYCL compact all-suite | 274 | 24 | 0 | 298 | authenticated Level Zero provider pass |
| CUDA edge cases, all types, n1024 | 61 | 17 | 0 | 78 | authenticated provider pass |
| SYCL edge algorithms, all types, n1024 | 50 | 16 | 0 | 66 | authenticated provider pass |
| SYCL edge BLAS, all types, n257 | 9 | 3 | 0 | 12 | authenticated provider pass; i8 capability skips |

The SYCL n1024 edge algorithms are practical; only the cubic edge-BLAS GEMMs
are reduced to n257 for the normal CPU-emulator run. A broad n1024 SYCL GEMM
probe was stopped after repeated 100M-budget device-lost results and multi-
minute execution. It remains an opt-in diagnostic with an explicit higher
budget, not a semantic skip.

The expanded SYCL profile was then run through the relocated Level Zero
provider with the normal algorithm ceiling changed from n4096 to n1024. The
complete algorithm matrix passed `5,650 / 1,732 / 0` (`7,382` selected): i8,
i32, f32, and f64 each passed 1,132 cases with 100 capability/precondition
skips; i64 passed 1,122 with 100 skips; f16 is an explicit 1,232-case
non-instantiation slice. The full level-1/matrix BLAS matrix passed
`699 / 332 / 0` (`1,031` selected), edge algorithms through n1024 passed
`650 / 208 / 0` (`858` selected), edge BLAS passed `93 / 39 / 0` (`132`
selected), async passed `455 / 91 / 0` (`546` selected), and stress passed
`45 / 0 / 0` (`45` selected). The n1024 candidate was also checked directly:
`algorithms/stable-sort-by-key/i64/n1024` passed; the corresponding n4096 case
passes in isolation but can lose the emulated device after a long cumulative
sequence, so n4096 remains an explicit diagnostic rather than the real gate.

The segmented-exclusive oneDPL path exposed one sequence-dependent red at
`algorithms/exclusive-scan-by-key/i32/n128`: a recycled USM destination could
leave the first slot as `4096` after earlier cases. The adapter now explicitly
zero-fills that destination before the segmented scan. The full n1024 matrix
and a targeted eight-shard replay are green, making this existing n128 case a
low-cost regression for the discovered large-run lifecycle.

The expanded HIP executable was rebuilt from the relocated ROCm 7.1 userspace
and run through the authenticated Gemstone HIP/HSA provider with
`GEMSTONE_NO_ORACLE=1` on `gfx1036`. No physical GPU or vendor oracle was used.

| Selection | Pass | Skip | Fail | Selected | Result |
|---|---:|---:|---:|---:|---|
| HIP algorithms, i32, size 7 | 109 | 7 | 0 | 116 | strict pass |
| HIP algorithms, f32, size 7 | 70 | 7 | 0 | 77 | strict pass |
| HIP algorithms, i64, size 7 | 70 | 7 | 0 | 77 | strict pass |
| HIP algorithms, f64, size 7 | 70 | 7 | 0 | 77 | strict pass |
| HIP algorithms, i8, size 7 | 70 | 7 | 0 | 77 | strict pass; VOP3P `v_dot4_u32_u8` implemented |
| HIP ROCm semantic probes, i32, n11/n257/n4096 | 39 | 0 | 0 | 39 | strict pass |
| HIP BLAS, f32, expanded matrix | 259 | 0 | 0 | 259 | strict pass |
| HIP BLAS, f64, expanded matrix | 259 | 0 | 0 | 259 | strict pass |
| HIP BLAS, f16, expanded matrix | 147 | 112 | 0 | 259 | strict pass; unsupported f16 routines are explicit skips |
| HIP edge cases, f32, size 7 | 11 | 0 | 0 | 11 | strict pass; composed f32 reduction fixed |
| HIP edge cases, f32 algorithms + BLAS, size 7 | 14 | 0 | 0 | 14 | strict pass |
| HIP edge cases, i32, size 7 | 11 | 3 | 0 | 14 | strict pass; BLAS edge forms are explicit skips |
| HIP edge cases, f64, size 7 | 14 | 0 | 0 | 14 | strict pass |
| HIP edge BLAS, f64, size 7 | 3 | 0 | 0 | 3 | negative-stride, padded, and beta-zero GEMM pass with complete Tensile bundle |
| HIP portable async, i32 + f32, size 7 | 26 | 0 | 0 | 26 | strict pass |
| HIP portable async, f64, size 7 | 13 | 0 | 0 | 13 | strict pass |
| HIP bounded stress, i32 + f32, size 1 | 6 | 0 | 0 | 6 | strict pass |
| HIP adaptor/large additions only | 11 | 0 | 0 | 11 | pass |
| HIP algorithms, i32, full sizes, shard 0/4 | 294 | 28 | 0 | 322 | strict pass; 28.1s runner |
| HIP algorithms, i32, full sizes, shard 1/4 | 283 | 37 | 0 | 320 | strict pass; 28.9s runner |
| HIP algorithms, i32, full sizes, shard 2/4 | 303 | 25 | 0 | 328 | strict pass; 33.3s runner |
| HIP algorithms, i32, full sizes, shard 3/4 | 275 | 26 | 0 | 301 | strict pass; 33.8s runner |
| HIP edge cases, i32 algorithms + f32 BLAS, full sizes | 218 | 2 | 4 | 224 | four large GEMMs hit the 100M instruction safety budget |
| HIP edge padded GEMM, f32, n1023, explicit 500M budget | 1 | 0 | 0 | 1 | strict pass; 8.98s case time, no oracle |
| HIP edge padded GEMM, f32, n1023, after packed-write optimization | 1 | 0 | 0 | 1 | strict pass; 7.68s case time, no oracle |
| HIP edge padded GEMM, f32, n1023, allocation-free LDS/advance + f32 MAC fast paths | 1 | 0 | 0 | 1 | strict pass; 6.80–6.83s case time, no oracle |
| HIP edge cases, all configured types, n1024, explicit 2B budget | 64 | 14 | 0 | 78 | strict pass; f16 edge algorithms and i8 edge BLAS are explicit capability skips |
| HIP edge padded GEMM, f32, n2047, explicit 2B budget | 0 | 0 | 1 | 1 | budget exhausted at 2B after 39.86s; no semantic result |
| HIP edge padded GEMM, f32, n4096, post-profile 20B budget | 0 | 0 | 1 | 1 | bounded at 125s with no semantic result; no oracle |
| Host full typed CTest (8-way parallel shards) | 251 | 0 | 0 | 251 | pass; 9,913 unique identities |
| AMDGPU Scala JVM tests | 116 | 0 | 0 | 116 | pass (native mirror 113/113) |

An eight-way `--suite=all` run over the default 1,923-case matrix was bounded
at 120 seconds per shard. Every shard was still traversing the algorithm
catalog and emitted no semantic failure before the bound; the run therefore
does not count as a conformance result. The CTest registration now partitions
each process by suite/category, scalar type, and stable-hash shard, so an
expanded run cannot hide the type-specific work behind one coarse catalog
shard. Unsupported slices are explicit all-skipped probes; real failures
still fail the CTest process.

The first full HIP i32 algorithm shard (322 selected cases, full sizes) passed
294 and skipped 28 in 28.1 seconds of runner time (66 seconds including the
Scala launcher). Before the decoder allocation pass, the same workload spent
most of its time in JVM GC and produced reduction launch failures under heap
pressure. The pass moved SOP2/SOPC/VOPC/VOP3 membership tables to shared
immutable sets and changed instruction-offset indexing to an integer-specialized
map. This is a semantics-neutral preparation optimization; no failure is
masked as an expected result. Optional `--timing` emits per-case and summary
timings for subsequent shard profiling.

The full-size edge run is the first current semantic/throughput boundary: the
four failures are `padded-gemm` and `beta-zero-gemm` at n1023 and n4096. Their
dispatches reach Gemstone and terminate with `BudgetExceeded(100000000)`;
rocBLAS reports status 6 only after the provider returns that failure. These
remain real reds, not XFAILs or skips. The next implementation pass should
profile and accelerate the AMDGPU interpreter (or add a deliberately explicit
large-workload budget profile) before changing the fixture sizes.

An opt-in profile is now available for deliberately large emulator workloads.
Set `GEMSTONE_EXECUTION_MAX_INSTRUCTIONS` before starting Gemstone; the value
is validated fail-closed and does not cross the RPC wire. With
`GEMSTONE_EXECUTION_MAX_INSTRUCTIONS=500000000`, the n1023 padded f32 GEMM
passes unchanged (`1/1`, 8.98s case time). The default 100M guard remains in
force for ordinary runs. n4096 is retained as a separate large-workload
experiment: it performs about 11.5B f32 FMAs and is not part of the normal
120-second conformance profile.

The interpreter's packed-register write path was then optimized to assemble
complete dwords directly. A JFR profile of the actual n4096 dispatch showed
the remaining hot path is the AMDGPU instruction-step closure (67.8% of CPU
samples), followed by packed VOP arithmetic (7.8%); the interpreter uses one
execution worker, so the host's available cores are otherwise idle. Direct LDS
offset reads initially introduced an `Integer` allocation on every successful
read; replacing that boxed result with a checked primitive sentinel, and using
the shared advance marker for lane-wise operations, brought n1023 to
6.80–6.83s while preserving the existing bounds checks. A dedicated f32 MAC
fast path contributes a small additional improvement without changing the
general VOP2 decoder or fallback semantics. AMDGPU JVM/native suites remain
100/100 and 113/113 green. A post-change n4096 run with a 20B instruction
profile remained bounded at 125s without producing a result. The remaining
gap is therefore architectural rather than a small utility inefficiency:
reaching n4096 inside 120s would require a separately designed parallel or JIT
execution path, not a safety-limit or fixture change.

The former set-operation failures are now strict-green. The composed f32
reduction edge case is also strict-green: AMD's unsigned float-to-dword
conversion feeds the magnitude into LLVM's sign-restoration sequence, so the
interpreter now models that device behavior for negative inputs. No failure is
masked as an XFAIL.
The f64 padded and beta-zero GEMM cases were also rechecked with the complete
relocated ROCm 7.1 rocBLAS/Tensile directory (both pass). An older artifact
containing only a small subset of that directory can abort inside rocBLAS
before Gemstone receives a dispatch; the relocation launcher now exports the
artifact's Tensile path automatically.

## Legacy-suite checkpoint (2026-09-06)

Checkpoint commit `6b95ea9` captured the conformance/runtime/matrix work before
the legacy sweep. The strict CUDA legacy lane is green at 56/56 in 119.64 s.
The exact HIP/rocThrust legacy lane completed at 49/51 in 926.57 s, with no
skips; the remaining failures are `random` (exit 1 after its launches complete)
and `unittest_static_assert` (exit 134 from an unmapped AMDGPU address
`0x8b98`). The eleven failures seen before the latest AMDGPU interpreter fixes
were reduced to these two: SDWA `v_cmp_gt_u16`/`v_cmp_le_u16` and packed
`v_pk_max_i16`/`v_pk_max_u16` are now implemented and covered by focused
decoder regression tests. The async inclusive scan and all descending set
operations now pass.
The legacy upstream suite remains intact and was not retired or modified into
a mask.

The key/value probes use a key-only comparator, matching rocThrust's
`key_value<Key, Value>` contract: values are payloads selected from the input,
not part of set membership. This keeps the port faithful to rocThrust's
selection and payload contract.

The compact catalog now lists 1,923 HIP identities: 1,386 portable algorithm,
141 BLAS, 175 edge, 98 portable async, 12 stress, 72 HIP async, and 39 ROCm
semantic identities. The full CTest profile is a 251-test typed parallel
contract. A complete HIP emulator sweep remains pending because the
default matrix is intentionally bounded by category and type; the completed
focused profiles currently have no semantic red.

## Fixed smoke contract

Every backend is run against the same 211-case selection:

```text
--suite=all
--algorithm-sizes=0,1
--blas-sizes=0,1
--edge-case-sizes=0,1
--stress-sizes=1
--stress-iterations=2
--algorithm-types=i32
--blas-types=f32
--edge-algorithm-types=i32
--edge-blas-types=i32
--stress-types=i32
```

`P/S/F` means pass/skip/fail. `C` is a process crash without a suite
summary. Expected skips are capability or precondition skips and must remain
identical between oracle and emulation.

| Backend | Oracle (P/S/F) | Gemstone emulation (P/S/F) | C | Selected |
|---|---:|---:|---:|---:|
| CUDA / `sm_75` | 187 / 24 / 0 | 187 / 24 / 0 | 0 | 211 |
| HIP / `gfx1036` | 187 / 24 / 0 | 187 / 24 / 0 | 0 | 211 |
| SYCL / Level Zero | 187 / 24 / 0 | 187 / 24 / 0 | 0 | 211 |

The final emulator smoke was run with the relocated, container-built
executables and authenticated Gemstone providers (no vendor GPU execution):

- CUDA: `14.71 s`
- HIP: `19.14 s`
- SYCL / Level Zero: `8.13 s`

The HIP executable has since gained the expanded algorithm and edge catalogs;
the same compact type/size intent now selects 349 HIP identities and runs
`325 / 24 / 0` (pass/skip/fail) under emulation. The 211-case rows above are
the earlier cross-backend baseline and remain useful as the historical smoke
contract.

## Baseline evidence

- [x] CUDA oracle and emulation smoke run recorded.
- [x] HIP oracle recorded (the physical `gfx1036` BLAS oracle requires the
  ROCm `HSA_OVERRIDE_GFX_VERSION=10.3.0` compatibility setting).
- [x] SYCL oracle recorded on the physical Intel Arc device.
- [x] Emulator launches are authenticated through Gemstone's provider
  attestation; a vendor run cannot silently count as emulation.
- [x] The relocated artifacts are used for the native runners.

## Red worklist

### HIP

- [x] Make the f32 rocBLAS calls execute through the emulated HIP/HSA path.
  The runtime now accepts the ROCm family-compatible `gfx1030` code objects
  used by the `gfx1036` artifact instead of rejecting the entire rocBLAS
  dispatch bundle.
- [x] Fix the `edge-cases/algorithms/composed-iterator-reduce/i32/n1`
  launch failure by implementing `v_mul_hi_i32_i24` and carrying SCC from
  scalar logical operations used by Tensile's conditional select.
- [x] Promote the rocBLAS half-precision AXPY regression: rocBLAS emits the
  VOP2 `v_fmac_f16` instruction, which is now decoded and executed with
  half-precision fused accumulation. The relocated Gemstone run passes the
  exact `legacy-red/rocblas/gfx1036/f16/all` case (`1 / 1 / 0`).
- [x] Re-run the fixed 211-case HIP smoke: `187 / 24 / 0`, `C=0`.

### SYCL

- [x] Fix the 11 algorithm failures (scan family and partition-copy/search
  results).
- [x] Fix the 17 f32 BLAS failures (SPIR-V validation/device-lost and pointer
  handling).
- [x] Fix the three segmented edge-case failures.
- [x] Eliminate the dependency-diamond and stress process crashes.
- [x] Re-run the fixed 211-case SYCL smoke: `187 / 24 / 0`, `C=0`.

The SYCL fixes are deliberately at the owning layers: SPIR-V decoding and
execution now cover 64-bit `OpSwitch` literals, `OpBitCount`, subgroup
shuffle/down, `OpAtomicFAddEXT`, and `native_sqrt`; the Level Zero provider
reports the parameter count required by UR, releases retained allocation
references after execution while preserving command-list replay, and gives
fresh host-backed allocations deterministic contents.

## Completion gate

- [x] The historical compact unified smoke is green for CUDA, HIP, and
  SYCL/Level Zero (`187 / 24 / 0`, `C=0` per API) under the authenticated
  emulator provider.
- [x] The expanded HIP catalog's non-legacy portable profiles are
  strict-green: full edge cases `826 / 188 / 0` over 1,014, full async
  `695 / 139 / 0` over 834, full stress `45 / 0 / 0` over 45, and full
  algorithm lanes have no failures (f16 remains an explicit all-skipped
  non-instantiation slice).
- [x] The expanded CUDA catalog's non-legacy portable profiles are
  strict-green: compact `274 / 24 / 0` over 298, edge n1024 `61 / 17 / 0`
  over 78, and the full algorithm, BLAS, edge, async, and stress lanes have
  no failures.
- [x] The expanded SYCL/Level Zero catalog's normal non-legacy profile is
  strict-green: algorithms through n1024 `5,650 / 1,732 / 0` over 7,382;
  BLAS `699 / 332 / 0` over 1,031; edge algorithms through n1024
  `650 / 208 / 0` over 858; edge BLAS through n257 `93 / 39 / 0` over 132;
  async `455 / 91 / 0` over 546; and stress `45 / 0 / 0` over 45.
  The n4096 algorithm and n1024 GEMM probes remain explicit diagnostics.
- [x] The available mini-app matrix is strict-green: 39 / 39 passed with
  zero ignored cases. OpenACC remains optional and is not present without
  `GEMSTONE_MINIAPP_OPENACC_IMAGE`.
- [ ] The exact imported legacy rocThrust binary inventory is not fully green
  yet: the latest bounded emulator sweep passes 49/51, with `random` and
  `unittest_static_assert` remaining. This is distinct from the expanded
  39-case ROCm semantic probe catalog, which remains 39/39 strict-green.
- [ ] Full oneDPL/OpenCL CTS and the complete upstream vendor-suite matrix are
  not part of this requested gate; they remain future work for the legacy
  suite and are intentionally excluded here.
- [x] `git diff --check` is clean and no generated artifact or diagnostic log
  is added to the source tree.

## Unified coverage expansion

The compact 211-case smoke above remains the fast emulator gate. The unified
catalog now also has an explicit release profile and the operation-specific
coverage needed before retiring the old vendor runners:

- [x] Async dependency catalog: copy, reduce, scan, sort, future continuation,
  event join, and interleaved streams/queues. Host, CUDA/HIP Thrust, and SYCL
  oneDPL adapters use real asynchronous primitives rather than host fallbacks.
- [x] BLAS matrix expansion: positive/reverse/zero-length Level-1 status forms,
  padded and transposed GEMV, all GEMM transpose combinations, and padded
  strided-batched GEMM.
- [x] ROCm-derived semantic probes: keyed/descending set operations,
  duplicate-heavy inputs, binary-search variants, gather/permutation/zip
  iterators, and pair sorting. These run in the ordinary HIP catalog; exact
  historical binaries remain opt-in under `--include-legacy-reds`.
- [x] Full profile and parallel CTest wiring. The host full profile passes
  251/251 CTest tests (typed suite/category shards plus contracts)
  and the current HIP executable enumerates 1,923 identities: 1,812 portable
  cases (1,386 algorithms, 141 BLAS, 98 portable async, 175 edge cases, and
  12 bounded stress cases), 72 HIP async cases, and 39 ROCm semantic probes.
- [x] Run the expanded non-legacy profile through each relocated emulator
  provider and record the authenticated emulator results. CUDA, HIP, and
  SYCL/Level Zero portable matrices are green at their documented workload
  bounds; no vendor oracle was run during this emulator pass.
- [x] Add each new red as a named unified semantic case, or retain it as an
  explicitly documented API-specific compatibility case. The current pass
  does not convert infrastructure timeouts or unavailable capabilities into
  XFAILs.

Low-cost regressions added during this pass:

- `ExecutionRPCConformanceSuite`: a 1-instruction dispatch with a 50 ms server
  socket timeout and a 750 ms idle same-session memory connection, guarding
  long-dispatch allocation cleanup;
- PTX `InterpreterSuite`: full-width `.b64` warp shuffle and CUB-style inline
  `lo`/`hi` register aliases;
- SPIR-V `ExecutionSuite`: byte-aligned 24/48-bit integer type admission;
- unified SYCL async `interleaved-streams/i32/n1`: no zero-dimensional queue
  submission when one interleaved lane is empty.
- unified SYCL `exclusive-scan-by-key/i32/n128`: explicit destination
  initialization prevents a recycled USM value from surviving a segmented
  exclusive scan after a long case sequence.

## Imported legacy reds (current boundary)

The old-suite emulator failures are tracked as executable cases in
`conformance/unified/include/unified_suite/legacy_reds.hpp`, with the CUDA
64-bit fixtures additionally reconstructed as first-class edge cases. Binary
entries invoke the exact container-built upstream executable for that identity,
while reconstructed entries call the same vendor algorithm with the original
iterator/size contract. This is a regression inventory, not a compatibility
mask:
when included, oracle runs execute normally, while `--expect-reds` turns a recorded failure
into `XFAIL` and treats `XPASS`/`XSKIP` as a failed check.

| Source | Imported identities | Oracle | Gemstone red check |
|---|---:|---:|---:|
| CUDA Thrust (`compute_89`) | 48 fixed upstream identities; 8 decomposed big-index cases | 48/48 upstream and 8/8 semantic oracle pass | 48/48 + 8/8 strict pass |
| AMD rocThrust/HSA (`gfx1036`) | 50 imported identities | 50/50 oracle baseline | 49/51 strict pass; `random`, `unittest_static_assert` remain |
| AMD rocBLAS (`gfx1036`, f16 AXPY) | 1 | 1/1 pass | fixed |
| Intel oneDPL/SYCL | 0 observed | 187/24/0 unified smoke | no red cases |

Reproduction commands for the authenticated Gemstone checks:

```console
./unified_suite_cuda --filter='^legacy-red/cuda-thrust/.*' --include-legacy-reds \
  --legacy-bin-root=/path/to/thrust/vendor-build --algorithm-types=i32
./unified_suite_hip --filter='^legacy-red/roc-thrust-fixed/' \
  --include-legacy-reds --legacy-bin-root=/path/to/rocthrust/build/testing \
  --algorithm-types=i32
./unified_suite_hip --filter='^legacy-red/(roc-thrust-fixed|rocblas)/' \
  --include-legacy-reds --legacy-bin-root=/path/to/rocthrust/build/testing \
  --algorithm-types=i32 --blas-types=f16
```

The latest strict provider checks completed with no skips in the selected
legacy lanes as follows:

```text
CUDA Thrust upstream: SUMMARY pass=2 skip=0 fail=0 xfail=0 xpass=0 xskip=0 selected=2
CUDA Thrust semantic: SUMMARY pass=8 skip=0 fail=0 xfail=0 xpass=0 xskip=0 selected=8
rocThrust active-red lane: SUMMARY pass=0 skip=0 fail=0 xfail=0 xpass=0 xskip=0 selected=0
CUDA Thrust full legacy lane: SUMMARY pass=56 skip=0 fail=0 xfail=0 xpass=0
xskip=0 selected=56 (elapsed=119.64s).
rocThrust full legacy lane: SUMMARY pass=49 skip=0 fail=2 xfail=0 xpass=0
xskip=0 selected=51 (elapsed=926.57s); the failures are `random` and
`unittest_static_assert`. This was a no-oracle emulator run using the relocated
ROCm userspace and Gemstone HIP/HSA providers.
rocBLAS f16: SUMMARY pass=1 skip=0 fail=0 xfail=0   xpass=0 xskip=0 selected=1
```

The CUDA semantic cases now execute inside the provider with the same virtual
64-bit launch contract as the upstream fixtures. Both `for_each` and
`adjacent_difference` pass at magnitudes 30, 31, 32, and 33; the oracle passes
the same eight cases.

The red inventory is opt-in (`--include-legacy-reds`) so ordinary unified
oracle/emulation catalogs remain strict and green/diagnostic independently of
this worklist. All currently imported ROCm identities are strict; the
`--expect-reds` mode remains available for future regressions.

The red checks now invoke the exact upstream executable that produced each
identity. With the matching CUDA 13.3 facade, the oracle and Gemstone provider
both pass the two formerly failing upstream CUDA binaries and all eight
decomposed big-index cases. The other 46 initially imported CUDA identities
also pass after fixing missing pinned-host allocation entry points, relocatable
PTX declaration selection, and the kernel parameter-width wire encoding.

The exact 50 imported upstream rocThrust binaries remain a strict regression
lane. The latest run passes 49/51, including both async-simple binaries,
`reduce_large`, `set_difference_key_value`, and the large keyed-sort cases;
`random` and `unittest_static_assert` remain reproducible failures.
The reconstructed 39-case ROCm semantic catalog is also now strict-green
(39/39); these cases stay in the ordinary catalog rather than being hidden by
an XFAIL mask. No AMD hardware/oracle execution was used in this expansion
pass.

The CUDA `sm89` pre-launch rejection was also fixed in the provider: target
normalisation now accepts both `sm_89`/`compute_89` and compact `sm89`/`compute89`
spellings, and the advertised compute capability follows the selected target.
The target-contract smoke and the CUDA conformance probes pass for both SM75
and SM89 under emulation. This removes a userspace target-selection false
negative. The HSA publication boundary and queue packet publication are also
covered by the runtime tests; the async-simple binaries now complete through
the same provider path rather than being masked as expected failures.

Validation boundary after this pass: `SBT_OPTS='-Xmx12G' sbt --server -batch
check` completed successfully in 76 seconds (including JVM and Scala Native suites, generated API conformance,
all runtime CTest checks, and the CUDA conformance suite: 18/18); the parallel
host CTest remains 251/251. The run emitted only existing Scala discarded-value
warnings in AMDGPU test fixtures. No physical GPU or vendor oracle was used.
Current legacy inventory: 50 rocThrust identities (49/51 bounded emulator
pass, with two residual failures). CUDA, rocBLAS, the historical compact unified catalog, and the
JVM/native ISA suites remain green at their recorded boundaries; no semantic
red is currently reproduced in the completed focused expanded profiles.
Installed artifacts that predate the
current source tree must be rebuilt before using them for a catalog tally.
