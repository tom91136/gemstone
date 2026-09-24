# Unified memory implementation and regression coverage

Latest verification: see [the third-review TDD follow-up](#third-review-tdd-follow-up).
Earlier verification covers [the typed-pointer CTS regression](#typed-pointer-cts-regression). Capability status
is recorded in [the compare-exchange follow-up](#compare-exchange-and-two-location-publication).
Earlier sections record intermediate stages.

Implemented against `5e1e276`, 2026-09-23. The 17 original red cases now pass,
including the five Level Zero ordinary/interior-pointer cases. No expected-failure
markers or capability changes were used to turn them green.

## Focused native coverage

| API | Previously red, now passing | Passing controls | Original gap |
| --- | ---: | ---: | --- |
| CUDA | 3 | 4 | Driver stack/heap registration and runtime stack registration return unsupported |
| HIP | 3 | 4 | Device-pointer queries for registered/allocated host memory and registered-host flags are unsupported |
| HSA | 5 | 1 | Interior lock views use the wrong offset; repeated, nested and overlapping locks lose independent lifetime or fail; pool locking has the same interior defect |
| Level Zero | 5 | 0 | Ordinary interior pointers fail at queue execution: first use of small heap, large heap, stack and static storage, plus the existing base-then-interior regression |
| OpenCL | 1 | 0 | SVM allocator accepts invalid or unsupported flag combinations (six failing assertions) |
| **Total** | **17** | **9** | Includes the previously enabled Level Zero red case |

There are 25 new custom cases plus that existing regression. Passing controls cover
CUDA managed/pinned interior aliases, repeated CPU edits between launches, an indirect
managed pointer graph and device-written pointer identity; HIP managed/pinned/registered
interior writes and CPU uploads; and independent HSA/HIP registration release and reuse.

CUDA cases use real PTX execution through the Scala server. HIP, HSA, Level Zero and
OpenCL native cases use the existing RPC fixture: they test facade/RPC behavior, with
mocked execution where applicable. HIP round trips assert both uploaded CPU edits and
synchronized output. The CTS probes below use the actual interpreter.

Explicit CUDA/HIP/HSA range tests disable the optional system-memory fallback. L0
ordinary-pointer cases use the new exact-access RPC pump. The old CUDA/HSA process-map
scans and L0 allocator-size probing were removed. No allocation interposition,
userfaultfd or signal mechanism is involved.

## Memory model

- Common native half-open ranges and registration leases track ownership, flags,
  aliases and independent release. CUDA and HIP registration/query APIs use them;
  HSA overlapping locks share backing bytes and retain independent lifetimes.
- All adapters register native address identity with the execution service, including
  OpenCL SVM and Level Zero allocations and virtual reservations. Existing API handle
  metadata remains in each adapter. This is not cross-API or cross-process interop.
- HSA upload/writeback clips borrowed backing to live registration views, excluding
  released prefixes, suffixes and gaps. Async copy and fill also span backing fragments.
- Known allocations keep their direct remote backing. Accesses spanning adjacent
  registration fragments split at those boundaries and use the latest remote bytes.
- Unknown host addresses use a sparse, completion-managed mirror. The interpreter
  requests only the exact missing bytes of an actual access; it never infers malloc
  bounds or rounds a pointer out to a page. The client services requests while waiting
  for completion, and copies back only written bytes before reporting completion.
- Requests coalesce adjacent bytes, preserve holes and deduplicate aliases. PTX and
  AMDGPU prepare active-lane loads together; SPIR-V suspends missing invocations and
  batches their requests. Scalar/data-dependent loads remain incremental.
- Each transfer is bounded by 1 MiB and 1,024 ranges, with individual contiguous
  chunks capped at 64 KiB. Writeback drains before completion is reported. Delayed
  host replies cannot overwrite a newer device write.

The existing `GEMSTONE_CUDA_SYSTEM_PAGING` and `GEMSTONE_HSA_SYSTEM_PAGING` environment
switches now select this demand mechanism; their names are retained for compatibility.
Level Zero enables it for shared system memory. OpenCL/SYCL expose explicit SVM/USM.
The later concurrency follow-ups below add native atomic exchanges and expand
the advertised capabilities for tested allocation kinds.

## Access-pattern regressions

`HostMemoryExecutionSuite` uses the real PTX/SPIR-V interpreters with a host-byte oracle
that rejects any unrequested gap or speculative read:

| Pattern | Observed transfers |
| --- | --- |
| PTX and SPIR-V: 32 contiguous 32-bit reads | One batch, one 128-byte range |
| PTX and SPIR-V: stride-three reads | One batch, 32 exact ranges, 128 bytes |
| PTX gather through indices | Two batches, 128 index bytes and 28 target bytes |
| PTX linked traversal, 4,096 shuffled nodes | 8,192 dependent batches, 49,152 bytes |
| PTX 64-bit load across two registered fragments | No host reads; current device bytes |
| PTX 32 atomic increments on one ordinary host word | One initial 4-byte fetch; final value includes all lanes |

`CodeObjectExecutionSuite` verifies that an AMDGPU vector-copy instruction prepares all
64 external lane reads together (256 bytes). This is interpreter/address-space coverage;
it does not measure a HIP RPC round trip. Native facade tests and existing HSA/HIP
execution tests cover the adapter path. `HostMemoryMirrorSuite` covers alias reuse,
write-only storage, unsolicited replies, retirement of fulfilled requests, in-flight write
preservation and transfer limits.

Sources:

- [CUDA tests](native/cuda/test/cross-workgroup.cpp), registered by
  [CUDACrossWorkgroupSuite](server/src/test/scala/net/kurobako/gemstone/server/CUDACrossWorkgroupSuite.scala).
- [HIP tests](native/hip/test/hip_runtime_test.cpp).
- [HSA tests](native/hsa/test/hsa_runtime_test.cpp).
- [Level Zero tests](native/levelzero/test/execution_rpc_test.cpp).
- [OpenCL tests](native/opencl/test/opencl_smoke.cpp).

The HSA overlap tests follow the [ROCr lock API contract](https://raw.githubusercontent.com/ROCm/ROCR-Runtime/master/src/inc/hsa_ext_amd.h).
They check data offsets and surviving registrations, allowing returned agent addresses
to differ from host addresses. OpenCL flag expectations follow
[clSVMAlloc](https://registry.khronos.org/OpenCL/specs/unified/refpages/man/html/clSVMAlloc.html).

## CTS reuse

Added three L0 shared/system memory kernel cases at 4 KB to compact, and those three
plus their 6 KB variants to full: `svm_to_usm`, `usm_to_svm`, `svm_to_svm`, all
`non_atomic_regular`. All six actually executed and passed; observed per-case time was
approximately 0.36–0.97 seconds including launcher startup. The existing compact
`svm_to_usm_atomic_regular_128B` and `svm_to_svm_atomic_immediate_128B` cases were also
rerun with the actual interpreter and passed (0.76 and 0.31 seconds respectively).
The latter first reproduced the missing mirrored-device-atomic path before its fix.

Existing workload selections already include these exercised OpenCL cases:

- `svm_allocate_shared_buffer`, `svm_allocate_shared_buffer_negative`
- `svm_enqueue_api`, `svm_migrate`, `svm_pointer_passing`, `svm_shared_sub_buffers`
- `svm_cross_buffer_pointers_coarse_grain`, `svm_shared_address_space_fine_grain_buffers`
- `svm_set_kernel_exec_info_svm_ptrs`, `svm_byte_granularity`

All ten passed. These cover interior byte offsets, linked allocations, explicit indirect
allocation lists, map/unmap and migration. The upstream negative case checks oversized
buffer creation, hence the separate custom SVM allocation-flags test.

Seven already-selected SYCL cases were initially reported as passing. Those standalone
probes selected physical hardware because the launcher removed their inherited
selector. The allocation/ordering follow-up below corrects and reruns them on
Gemstone, including a pointer-kind defect exposed by that correction:

- `usm_get_pointer_queries`, `usm_malloc_shared`, `usm_malloc_host`, `usm_malloc_device`
- `usm_fill_memset_memcpy`
- `usm_api_memcpy_queue_multiple_events`, `usm_api_copy_queue_single_event`

Four additional selections returned success **without exercising the requested capability**:

- OpenCL `svm_fine_grain_memory_consistency`, `svm_fine_grain_sync_buffers`, and
  `svm_shared_address_space_fine_grain` reported unavailable SVM levels/extensions.
- The initial SYCL `Tests for usm atomics with core types` skip was on physical hardware,
  not Gemstone. The corrected Gemstone probe is covered below.

These are coverage gaps, not passing coherence/atomic tests. Existing workload coverage
also includes L0 virtual-memory and allocation-lifetime cases; no duplicate list was added.

## Reproduction and observed run

From the repository root, using the existing native build:

```sh
cmake --build target/native/build -j2
ctest --test-dir target/native/build --output-on-failure --timeout 30
sbt 'server/testOnly net.kurobako.gemstone.server.CUDACrossWorkgroupSuite'
```

Initial implementation verification (before the larger progress matrix below):

- 116/116 native CTest cases passed (1.62 seconds).
- 167 focused Scala tests passed: core 21, AMDGPU 12, PTX 69, SPIR-V 35,
  server/integration 30 (including nine CUDA integration cases).
- Eight L0 and ten OpenCL CTS probes passed. The seven initial SYCL results
  were invalid as Gemstone evidence; see the corrected reruns below.
- Generated RPC verification, affected Scala lint, changed native-source formatting,
  native codestyle and `git diff --check` passed.
- Repository-wide native formatting is blocked by existing violations in untouched
  `native/levelzero/test/levelzero_smoke.cpp` and `native/opencl/src/compiler.cpp`.

The former intermittent HIP paging-section failure was not reproduced after replacing
speculative memory scans with exact demand accesses. These results are focused validation,
not a full CTS run or a renewed estimate of the combined one-hour workload.

Additional focused commands:

```sh
sbt 'server/testOnly *HostMemory*Suite *AllocationAddressSuite *ExecutionServiceSuite'
sbt 'amdgpu/testOnly *CodeObjectExecutionSuite *AllocationAddressSuite'
sbt 'codegen/checkGeneratedExecutionRPC'
```

CTS selections use the existing [conformance runner](conformance/README.md) and
[compact](conformance/workloads/compact.toml)/[full](conformance/workloads/full.toml)
workloads. OpenCL probes used the existing wimpy mode with 32 elements. These measurements
are focused probes, not a timing claim for the whole workload.

## Large access-pattern matrix and RPC progress

[UnifiedMemoryProgressSuite](server/src/test/scala/net/kurobako/gemstone/server/UnifiedMemoryProgressSuite.scala)
now runs actual native API calls, TCP RPC and interpreters for the same four patterns
through every API in this unified-memory workstream. Unlike the earlier native facade
checks, these runs execute the kernel. The shared fixture and build instructions are in
[native memory regressions](native/rpc/test/MEMORY.md).

| API | ISA | API-owned storage | Ordinary host storage | Verified launches |
| --- | --- | --- | --- | ---: |
| CUDA | PTX | Managed | Yes | 16 |
| HIP | AMDGPU | Managed | Yes | 16 |
| HSA | AMDGPU | HSA region | Yes | 16 |
| Level Zero | SPIR-V | Shared | Yes | 16 |
| OpenCL | SPIR-V | Buffer SVM | Not advertised | 8 |
| SYCL | SPIR-V | Shared USM | Not advertised | 8 |
| **Total** | **Three ISAs** | | | **80** |

Every combination runs contiguous, stride-17, indexed-gather and linked-list patterns
with 4,096 items/nodes, interior pointers, guard bytes, checked results and a second
launch after CPU edits. The list follows actual pointers through a deterministic
permutation of 64-byte nodes. All ten client runs passed, including SYCL with its real
oneAPI runtime. SYCL explicitly skips when its compiler was unavailable at build time.
Vulkan is not covered: its buffer-device-address entry points are currently unsupported.

These tests exposed a SPIR-V pointer-load defect: converting a pending raw read into
an Option discarded the suspension fault and reported an invalid typed pointer slot.
The L0 ordinary-host linked case first failed, then passed after preserving that fault
through pointer resolution.

A separate temporary TCP observation proxy counted actual CUDA host-memory requests.
Its 4,096-node linked traversal used 8,192 supplied read ranges (49,152 bytes), with
8,193 polls. The initial run slowed as the graph grew: `HostMemoryMirror.supply` copied
the accumulated pending/issued sets on every reply and retained fulfilled requests.
Supply now checks membership directly and retires fulfilled requests. A unit regression
also verifies that an old reply cannot authorize later unsolicited data.

Observed timings on the noisy machine, in seconds (diagnostic observations, not a
controlled benchmark):

| Proxy probe | Before bookkeeping fix | After |
| --- | ---: | ---: |
| Linked, 1,024 nodes | 0.987 | 0.178 |
| Linked, 4,096 nodes | 11.386 | 0.505 |
| Contiguous, 4,096 elements | 0.143 | 0.060 |
| Strided, 4,096 elements | 0.117 | 0.051 |

With an artificial **2 ms delay per Poll/Supply RPC**, the same 4,096-node traversal
finished correctly in **34.982 seconds**. Progress remained steady at approximately
940 RPCs per two seconds; all 8,192 read faults were serviced. This confirms forward
progress under latency while demonstrating the remaining cost of sequential dependent
reads. It does not make linked traversal independent of network latency.

The larger tests have bounded process deadlines, without timing assertions that would
fail merely because the machine is noisy. Proxy logs for this run are in
`/tmp/gemstone-fault-progress/` (`before.json`, `after.json`, `delayed-4096.log`);
those temporary diagnostics are not required by the checked-in tests.

Follow-up verification after the progress fixes:

- 24 server tests passed: six mirror tests, eight interpreter demand-memory tests,
  and ten native API matrix runs (80 checked kernel launches; no skips here).
- 35 affected SPIR-V regression tests passed.
- 116 native CTest cases passed in 1.85 seconds.
- Changed Scala lint, native codestyle, new native fixture formatting, generator
  shell syntax and `git diff --check` passed.

```sh
sbt 'server/testOnly *HostMemory*Suite *UnifiedMemoryProgressSuite'
sbt 'spirv/testOnly *ExecutionLayoutSuite *ExecutionControlFlowSuite *ExecutionCollectiveSuite *ExecutionAtomicSuite *VirtualMachineSuite'
```

## Controlled concurrency follow-up

The shared native matrix now includes six additional capability/concurrency cases:
**16 processes and 104 actual kernel launches** in its default mode. Four APIs each
execute six launches: CUDA pinned host memory, HIP coherent mapped host memory, HSA
fine-grained region memory and OpenCL fine-grained buffer SVM. L0 and SYCL instead
verify their concurrency restrictions; their sequential kernel coverage remains.

A test-only backend wrapper pauses each launch after upload/before execution and
after execution/before completion. A CPU thread reads shared input and changes
disjoint input/output bytes at those checkpoints. Cases include adjacent words,
separate bytes within a word and a 64 KiB transfer boundary, repeated after CPU edits.
These tests use actual PTX/AMDGPU/SPIR-V execution and native TCP transport.

TDD exposed two writeback defects:

1. Registered allocation dirty tracking expanded writes to entire **64 KiB** chunks,
   overwriting untouched bytes that the CPU had legitimately changed. `WrittenRanges`
   now merges exact intervals and splits large transfers without filling gaps.
2. CUDA ignored completion dirty ranges and downloaded every mirrored allocation,
   including read-only inputs. It now uses the shared completion collector and exact
   ranges. The separate whole-allocation dirty flag/download path was removed.

The initial legal concurrency cases failed through CUDA, HIP, HSA and OpenCL. All
four now pass. An exploratory SYCL concurrent run was not retained as a conformance
case because its advertised aspects do not promise this concurrency; it is explicitly
a capability check in the final matrix.

Capability tests also exposed and corrected:

- CUDA `concurrentManagedAccess` and `pageableMemoryAccess` now return zero. Their
  previous values promised live system-scope atomic support absent from the mirror.
  Explicit opt-in demand access remains available for the existing experimental tests.
- Intel OpenCL USM `CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL` now returns zero,
  consistent with `clSetKernelArgMemPointerINTEL` requiring known SVM/USM allocations.
- OpenCL still advertises fine-grained buffer SVM and the implemented Intel USM
  concurrency capabilities, without concurrent atomics. L0/SYCL retain their existing
  restrictions. No broad claim of full HSA/HIP atomic coherence is made by these tests.

The CUDA distinction follows the [CUDA atomicity contract](https://docs.nvidia.com/cuda/cuda-programming-guide/05-appendices/cuda-cpp-memory-model.html#atomicity).
The L0/SYCL capability boundaries follow their
[access capabilities](https://oneapi-src.github.io/level-zero-spec/level-zero/latest/core/PROG.html#access-capabilities)
and [USM aspects](https://registry.khronos.org/SYCL/specs/sycl-2020/html/sycl-2020.html).
OpenCL permits the disjoint accesses tested here under its
[fine-grained SVM rules](https://registry.khronos.org/OpenCL/specs/unified/html/OpenCL_API.html#_memory_consistency_model).

### Follow-up: exact host access and live atomic coherence

The former scoped PTX red is green. The expanded probe also reproduced ignored
AMDGPU acquire/release semantics and a HIP managed-allocation registration gap;
both are now fixed. The matching 32/64-bit live tests execute through CUDA, HIP,
HSA, Level Zero, OpenCL and SYCL, using 32 bidirectional payload handshakes and a
shared counter with 4,096 CPU increments plus one GPU increment per round.

- Registered host-backed allocations now use the same sparse, exact-demand path
  as ordinary host access. Device-only allocations retain remote storage.
- CUDA bulk managed uploads were removed. HSA CPU allocations bypass uploads and
  the obsolete host snapshot was removed. HIP managed allocations now select the
  HSA CPU region. OpenCL/L0 shared allocations bypass uploads and pointer scans.
- Native CAS provides one atomic location shared with the CPU. Release publishes
  earlier writes, acquire invalidates clean cached reads, and reply tickets reject
  duplicate or unsolicited responses. Separate synchronization requests acknowledge
  write publication without fabricating a memory access.
- PTX scoped atomic instructions and 32/64-bit acquire/release loads/stores use that
  path. AMDGPU preserves coherent-load flags, flushes host writes at waits/end, and
  invalidates host reads at cache invalidation. SPIR-V atomics use the same service.
- Batches retain aligned store widths so native 32/64-bit atomic stores are not
  turned into byte copies. A byte-budget test checks that batching cannot split a
  word. The host-memory polling loop is iterative, avoiding stack growth on wakes.

The current default matrix is **28 process runs / 128 kernel launches**, including
all six disjoint-access executions and twelve live-atomic executions. The previous
L0/SYCL capability-only cases now also execute explicitly labelled implementation
probes. Capability assertions remain conservative; these tests do not establish
full conformance or enable the broader advertised capabilities.

RPC recording assertions reject speculative uploads to host-backed allocations and
ordinary reads of atomic control words. The registered-allocation unit oracle rejects
reads of unused allocation bytes. The 4,096-node linked tests remain in the default
matrix and continue to make progress. See
[native fixture coverage](native/rpc/test/MEMORY.md) for the allocation
kind and ISA used by each API.

```sh
# Full targeted matrix, including atomics:
sbt 'server/testOnly *UnifiedMemoryProgressSuite'
# Just the twelve independent 32/64-bit atomic cases:
GEMSTONE_TEST_HOST_ATOMICS=1 sbt 'server/testOnly *UnifiedMemoryProgressSuite'
```

No host fault handler, new page-protection mechanism or allocator interposition was
introduced. The observed matrix run took approximately 13 seconds on the current
machine; this is a progress measurement, not a stable performance threshold.

## Explicit fences and deterministic contention

The follow-up reproduced six missing-fence failures before fixing them: PTX
`membar.sys`, four PTX `fence` variants, and SPIR-V `OpMemoryBarrier`. Supported
thread fences now use a full publication/acknowledgement/invalidation boundary,
including read-only fences. The native pump executes `atomic_thread_fence(seq_cst)`
before acknowledging a fence. PTX's dynamic-allocation overlay forwards this boundary.
SPIR-V flushes its buffered external writes before the fence. AMDGPU uses the
compiler's waits and cache-invalidation sequence; the compiled fence cases exercise it.

The default matrix is now **52 processes / 152 kernel launches** across all six APIs:
80 baseline launches, 36 disjoint-access launches, 12 ordered-atomic handshakes,
12 relaxed-atomic plus explicit-fence handshakes, and 12 four-workgroup atomic cases.
Every atomic case runs independently at 32 and 64 bits. Two CPU threads force two
CAS failures per device RMW; both its retry count and final results are checked.
The four-workgroup cases also check every returned old value across 128 work-items.

The first multi-workgroup fixture expected one atomic instruction per work-item,
but ROCm legally combined wave increments. The fixture now disables only that
optimization, preserving an identical collision schedule across PTX, AMDGPU and
SPIR-V. This was a test scheduling assumption, not a lost-update implementation bug.
Compiler fences use LLVM's empty-string system scope, as documented in the
[AMDGPU memory model](https://llvm.org/docs/AMDGPUMemoryModel.html).

### Capability decision

Level Zero single-device shared allocations now advertise `CONCURRENT` and
`CONCURRENT_ATOMIC`, supported by the shared-allocation disjoint, handshake,
fence and multi-workgroup tests. Other L0 allocation kinds retain their existing
flags. These are separate promises under the
[Level Zero access-capability contract](https://oneapi-src.github.io/level-zero-spec/level-zero/latest/core/PROG.html#access-capabilities).

Other API promotions remain separate work:

- CUDA atomic probes use managed storage; extend them to pinned and registered
  allocations before promoting the host-native-atomic flag.
- HIP atomic probes use coherent host storage; managed allocations need the same
  live concurrency coverage before promoting concurrent-managed access.
- OpenCL/SYCL need consistent SVM/Intel-USM capability bits, allocation-flag
  acceptance and compiler feature exposure, followed by capability-gated CTS probes.
  The current custom SVM atomic probes deliberately exceed advertised capabilities.
- Queue/event dependency tests and the remaining allocation-kind combinations are
  still needed; this round does not claim all memory-model litmus tests or CTS pass.

Verification for this follow-up:

- 94 focused server tests passed, including 23 demand/fence interpreter tests and
  the complete 52-process matrix (about 19 seconds for the matrix on this machine).
- All nine Level Zero matrix cases passed again after the capability promotion.
- 17 focused PTX/SPIR-V regressions and 116 native CTest cases passed.
- Affected Scala lint/format checks, changed native-file formatting, native
  codestyle, generator shell syntax, generated RPC checks and `git diff --check` passed.

The native count includes the existing native unit/facade tests. It is separate
from the 152 real kernel launches in the shared-memory matrix. No full CTS rerun
or revised one-hour workload timing estimate was performed in this follow-up.

## Allocation and ordering follow-up

The default native matrix now runs **138 processes / 368 kernel launches** over
16 API/allocation combinations:

| API | Allocation kinds |
| --- | --- |
| CUDA | Managed, pinned, registered host |
| HIP | Managed, coherent mapped host, registered host |
| HSA | Fine-grained CPU region, fine-grained pool, locked host |
| Level Zero | Shared, host |
| OpenCL | Fine-grained SVM with atomics, Intel host/shared USM |
| SYCL | Host/shared USM |

The launch split is 80 baseline, 96 disjoint, 32 ordered-atomic, 32 explicit-fence,
32 multi-workgroup, and 96 queue/event dependency launches. Queue probes reuse
allocations across three producer/consumer pairs, including two cross-queue pairs,
CPU edits and interior guards. CUDA/HIP currently submit synchronously; their
queue tests establish dependency visibility rather than asynchronous overlap.

Capability assertions first failed in 13 of the 16 allocation combinations.
CUDA/HIP now advertise host-native atomics and concurrent managed access. HIP's
legacy and current device-property ABIs agree. L0 host allocations gain the same
concurrent/atomic flags as single-device shared allocations; other kinds keep
conservative flags. OpenCL now accepts atomic fine-grained SVM allocation flags,
exposes Intel host/shared USM atomics and the matching OpenCL C 3 atomic order/scope
features. SYCL sees the host/shared atomic aspects. System SVM remains unavailable.

The expanded tests revealed a lost wakeup: a poller could see an acknowledged
fence before its worker removed the exchange, then remain asleep while another
worker waited for host bytes. Removing an exchange now wakes pollers. A focused
512-iteration two-worker regression failed before the fix and passes afterward.

The corrected SYCL atomic CTS exposed synchronous idle-queue kernel submission:
the kernel waited for a host flag set after submission returned. All OpenCL kernel
submissions now use the existing deferred scheduler, including idle queues. The
CTS went from a 60-second timeout to six passing assertions in about eight seconds.

Intel USM allocation metadata now distinguishes host, device and shared storage.
Device USM uses remote device backing; host/shared USM use exact host transfers.
Pointer queries preserve interior identity, enforce the owning context, and reject
one-past and freed pointers as unknown. A native regression first failed the type
and context/bounds checks; the already-selected SYCL pointer-query CTS also caught
the type defect before the fix.

Verification:

- The 138-process matrix passed repeatedly: earlier runs took 32–35 seconds;
  the final run under heavier machine contention took 110 seconds, with no timeouts.
- 38 focused execution/mirror/range tests passed, including the lost-wakeup regression.
- 117 native CTest cases passed after adding the Intel USM query regression.
- OpenCL `svm_fine_grain_memory_consistency` and `svm_fine_grain_sync_buffers`
  actually execute with atomics enabled (32-element CTS probes, roughly 0.4–1.5 s).
  A recorded run captured 3,524 RPC frames across the two cases, with no capability skip.
- SYCL USM atomic core types passed six assertions in about eight seconds. The
  seven allocation/query/copy cases listed above were rerun with an explicit
  Gemstone OpenCL selector; all pass after the pointer-kind fix.
- Scala lint/format checks, changed native-file formatting, native codestyle and
  `git diff --check` pass.

The CTS cases were already selected by the existing workload inventory; no parallel
harness or duplicate workload entries were added. Standalone SYCL reproduction must
set the selector in the launched child, because `gemstone run` removes inherited
selectors: `gemstone run --apis opencl -- env ONEAPI_DEVICE_SELECTOR='opencl:*' ...`.
The custom matrix directly supplies its isolated ICD and selector and was unaffected
by the earlier standalone-probe mistake. No full CTS timing budget was re-estimated.

## Queued lifetime and asynchronous submission

The default matrix now runs **170 processes / 400 kernel launches**. The additional
32 cases cover the same 16 allocation kinds at both atomic widths. The submitting
thread launches a kernel, then supplies its 32 host/device handshakes. A rescue
thread releases a blocking implementation after two seconds and reports a failure,
so this regression does not require waiting for the process timeout. CUDA and HIP
both failed before the fix; all six APIs now pass.

CUDA and HIP use one shared native command-queue implementation with one worker per
adapter. Launch arguments are copied before return. Kernel completion, queued
copies, stream/event waits and callbacks observe FIFO order; queries report pending
work and asynchronous failures. Synchronous frees, host unregister, module unload
and context teardown wait for queued work before retiring resources. Explicit
async free uses the queue. CUDA's completion worker has a separate RPC connection,
so its host-memory polling does not monopolize the submitting connection.

This is conservative device-wide serialization. It enables host/device progress;
it does not establish overlapping kernels or independent progress between streams.
CUDA/HIP submission probes additionally check pending event/stream queries, event
re-recording, and asynchronous copy visibility. Matching native lifetime probes
hold a queued copy behind a callback and verify that synchronous free waits.

Intel `clMemBlockingFreeINTEL` now tracks per-allocation pending uses. Copies,
fills, map/unmap/migration/advice and queued kernel arguments retain their uses
until completion or cancellation. Kernel exec-info pointer lists and indirect
host/device/shared access declarations also count; unrelated allocations do not
wait. Ordinary `clMemFreeINTEL` retains its application-managed lifetime contract.
The transfer regression first failed six assertions; the direct/indirect kernel
regression first failed three. Both now pass, including failed dependency events.
This follows the [Intel USM blocking-free contract](https://registry.khronos.org/OpenCL/extensions/intel/cl_intel_unified_shared_memory.html).

Two server lifecycle tests close a session while its worker awaits either host
bytes or a fence acknowledgement, with a second execution queued. They verify
cancellation, resource disposal and rejection of late replies. These passed on
the existing common server cancellation path. Ordinary host storage still must
remain alive until its documented completion point; early application free is
not detected by this work.

Verification:

- All 170 matrix cases passed twice (about 36 and 40 seconds on this noisy machine).
- All 32 submission cases passed again with CUDA/HIP query/copy assertions.
- Targeted OpenCL fine-grained SVM consistency/synchronization and SYCL atomic,
  queue memcpy and queue copy CTS probes pass with an explicit Gemstone selector.
- 212 targeted server tests passed: the matrix plus 42 memory/range/lifecycle tests.
- Nine CUDA integration tests passed, including queued-free and callback completion.
- All 122 native CTest cases passed. Scala lint/format, native codestyle and
  changed native-file formatting checks passed; `git diff --check` is clean.

No full CTS profile or remote-latency benchmark was run in this follow-up.

## Independent queues and kernel publication

At this stage the matrix was **202 processes / 464 kernel launches**. Its 32 new cases
cover all six APIs, all 16 allocation kinds, and both 32/64-bit widths. A consumer
kernel first exchanges a payload with the host, then remains running while a
producer on a second queue publishes a different payload and a release flag.
The consumer must acquire the flag and see the new payload. A two-second rescue
thread bounds serialization failures; it never supplies the producer's update on
a passing run. The original server rendezvous test and CUDA/HIP probes failed
before the changes. HSA's single doorbell consumer was a further reproduced block.

- The execution service admits up to its configured worker count of dispatches.
  Program execution shares a read lock; lifecycle mutations retain exclusion.
  Pending work still retains programs and allocations through cancellation.
- Host read caches and unpublished writes belong to each completion. Aliases and
  lanes within one completion share its view; independent completions communicate
  through published native memory. A focused test gives two overlapping reads
  different replies and verifies that neither completion imports the other's cache.
- CUDA/HIP use two native workers and explicit stream/event dependency handles.
  Same-stream order, legacy-default barriers, nonblocking streams and recorded
  event dependencies are preserved. HIP dispatches use separate AQL queues.
- OpenCL's existing scheduler has two workers, with one active worker per queue.
  OpenCL and Level Zero serialize metadata preparation/copy-back while polling
  execution on separate connections. Relocation metadata updates are locked.
- HSA's monitor has two workers and one consumer per queue. Each worker has an
  independent RPC connection. Dirty-range draining and copy-back are serialized
  together so no queue signals completion before another worker finishes copying
  its output. The public dirty-range interface retains its previous behavior.

This establishes two-queue progress and release/acquire publication for the tested
paths. It does not promise unlimited resident kernels, fairness under oversubscription,
or a portable guarantee that arbitrary inter-kernel spin dependencies are safe on
physical GPUs. Server worker count must permit overlap; a one-worker server still
serializes dispatches. No new capability flags were promoted in this round.

Verification:

- 255 targeted Scala tests passed, including the 202-process matrix and the
  execution, cache-isolation, lifecycle and CUDA integration tests. The final
  matrix took about 28 seconds on this machine; earlier overlap runs took longer.
- All 123 native CTest cases passed, including explicit stream dependencies and
  default-stream ordering with an independently runnable nonblocking stream.
- The two OpenCL SVM atomic probes and the three SYCL atomic/copy probes passed
  again through Gemstone (about 0.3–9 seconds per selected probe).
- Scala lint/format, native codestyle, affected native formatting and
  `git diff --check` passed.

No full CTS profile or remote-latency benchmark was run in this follow-up.

## Compare-exchange and two-location publication

The current matrix is **234 processes / 496 kernel launches**. The 32 new cases
cover all six APIs, all 16 allocation kinds and both widths. Each runs 32 host/kernel
handshakes: release publication through one atomic location, failed acquire CAS
with an observed-value check, then release acknowledgement through a second atomic
location. Payloads and atomic tokens set the top bit, exercising unsigned 32-bit
and full 64-bit transport. The host checks that failed CAS leaves its target unchanged.
RPC recording still rejects ordinary reads of either atomic control location and
speculative uploads of host-backed allocations.

This exposed missing PTX `atom.cas.b32/b64` interpreter support. A CUDA integration
probe failed with API error 700. Twelve focused interpreter cases then failed
before implementation: successful and failed CAS through global, generic and shared
addresses, scoped global CAS, and a 32-lane race with exactly one winner, at both
widths. CAS now uses the existing atomic modification path, with comparisons masked
to the instruction width. Shared-memory effects retain their existing lane ordering.
No new RPC or memory bookkeeping system was introduced. AMDGPU and SPIR-V passed
the additional cases without runtime changes.

Verification for this follow-up:

- All 12 new PTX CAS tests reproduced the missing instruction before the fix.
- All 38 PTX interpreter tests and 23 host-memory execution tests passed afterward.
- All 234 native-client scenarios passed through real RPC, covering 496 kernel
  launches in about 46 seconds on the noisy machine (295 targeted Scala tests total).
- The focused 32-case compare-exchange matrix also passed separately, in about
  3.4 seconds. No capability flags or timeouts were increased to obtain these passes.
- PTX/server Scala lint and format checks, native codestyle, affected C++ formatting
  and `git diff --check` passed.

The relaxed polling operations currently have stronger ordering than their minimum
contract, so these cases establish the end-to-end publication outcome rather than
prove an isolated CAS acquire implementation. This round does not establish every
memory-model litmus outcome or all scope/order combinations. An actual remote-network
benchmark and a completed full-CTS budget measurement remain outstanding.

## Typed-pointer CTS regression

A fresh full-workload inventory contains **1,040 OpenCL, 1,172 SYCL and 550 Level
Zero CTS cases (2,762 total)**. A two-worker, stop-on-failure run found an actual
memory regression in `cts/opencl/api/clone_kernel_with_exec_info`, before reaching
the cloning step. This was a null-pointer failure, not a timeout.

SPIR-V typed external arguments containing pointers were given synthetic null
slots whenever allocation relocation metadata was absent. This bypassed the
registered host bytes. Binding now leaves those slots absent, allowing the existing
exact-access reader to fetch and resolve the stored pointer. Explicit relocations
retain their existing behavior.

Four regressions cover pointer-to-pointer and pointer-to-structure arguments, each
at allocation base and interior offset. All four failed before the fix and passed
afterward, checking the exact eight-byte read and indirect write. The complete
host-memory execution suite passed **27/27**, and SPIR-V layout tests passed
**10/10**. The same OpenCL CTS case then passed through real RPC in 1.31 seconds.
It is already selected in compact and full, so no duplicate CTS entry was added.
The six-API memory matrix was rerun after the fix: **234/234 processes passed**,
covering **496 launches**, including the existing concurrency and atomic scenarios.

The initial full-CTS attempt stopped after 17 cases (16 passed, one failed).
After the fix, the two-worker rerun reached **121 cases: 109 passed, 11 skipped,
one failed**, in 42.56 seconds of CTest wall time. The failure is Level Zero's
`zeEventSignalingTests.GivenEventsSignaledWhenResetOnImmediateCmdListAppendCmdListsExpThenQueryStatusReturnsNotReady`:
`zeCommandListImmediateAppendCommandListsExp` returns
`ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`. Its following buffer check also fails
because the commands were not appended. This is an API support gap, not a timeout
or evidence of a memory-coherence defect. The full manifest selects it through
`zeEventSignalingTests.*`; it remains red. Implementing that extension with focused
command-list/event tests is a separate next step.

Neither partial run establishes the combined one-hour runtime. Diagnostic SYCL
discovery used the OpenCL backend explicitly, with the inherited forced Level Zero
UR adapter cleared. The inventory and reports are in
`/tmp/gemstone-cts-budget-inventory3/` (`post-pointer-fix.xml` for the second run).
Final `scalafmtSbtCheck`, SPIR-V/server `scalafixAll --check` and `git diff --check`
passed.

## Two-review TDD follow-up

Two independent reviews found four gaps. Each now has a failing-before/fixed-after
regression:

| Finding | Regression and correction |
| --- | --- |
| HIP/HSA overlapping device writes overwritten by stale uploads | Both native clients failed the same disjoint-word, two-queue scenario. Removed whole-allocation dispatch uploads; explicit copy/fill APIs update device backing. A legacy HSA fixture now initializes its device pointer table with `hsa_memory_copy`. |
| Device-memory synchronization word failed to order host payload | Eight PTX cases (32/64 bits, release/acquire, load/store or RMW) failed before the fix. Both memory-service atomic entry points publish host writes before device atomics and invalidate host reads afterward. Pure device sessions retain their direct path. |
| SPIR-V retained or replayed stale pointer metadata after CPU replacement | Four cases cover reload/no-reload and initial relocation/no relocation, using an interior pointer field. Host-backed storage now reads authoritative bytes and emits no completion relocation replay; device-backed relocation behavior remains covered by layout tests. |
| Queued OpenCL buffers leaked after the last application reference was released | Fill and kernel commands, each with success and dependency failure, failed allocation reclamation checks. Command and retained-buffer leases now end before releasing the final memory-object reference. |

`UnifiedMemoryReviewSuite` adds the same three end-to-end scenarios through every
API: disjoint device words on overlapping queues, a device-memory atomic flag
publishing a shared payload, and a synchronized CPU replacement of a device-written
pointer. Actual kernels provide the handshakes; no timing comparison selects pass/fail.
Deadlines only bound failure. The pointer scenario also verifies that completion
leaves the CPU's replacement intact.

Verification after the fixes:

- **240 native processes / 532 launches**, all passing: the existing 234-case
  matrix plus six new API cases, six launches each.
- **39 host-memory execution tests**, **7 execution-service tests**, and
  **10 SPIR-V layout tests**, all passing.
- **124 native API/RPC tests**, all passing; the existing OpenCL
  `clone_kernel_with_exec_info` CTS case also passed after these fixes.
- Core, SPIR-V and server `scalafixAll --check` passed; changed Scala/C++ files formatted.
  The changed C++ files passed `clang-format --dry-run --Werror`. Repository-wide
  `native/lint` still reports pre-existing formatting in `native/opencl/src/compiler.cpp`
  and `native/levelzero/test/levelzero_smoke.cpp`; those unrelated lines were left intact.

The red/green logs are `/tmp/gemstone-opencl-leak-red.log`,
`/tmp/gemstone-pointer-order-red2.log`, `/tmp/gemstone-review-native-red.log` and
`/tmp/gemstone-review-full-green.log`. This resolves the four review findings;
it does not broaden the scope to automatic allocation lifetime discovery or
prove every memory-model combination. The separate unsupported Level Zero
command-list extension remains outside these fixes.

## Third-review TDD follow-up

The next self-review identified three remaining gaps. Each has a failing-before,
passing-after regression:

| Finding | Regression and correction |
| --- | --- |
| Device-backed SPIR-V pointer fields still used stale metadata after another kernel replaced the pointer | The same two-queue device-table replacement now runs through all six APIs. Level Zero, OpenCL and SYCL failed before the fix. Both the consumer's dereference and the final copied-back pointer must match the replacement. Memory now reports whether pointer bytes use canonical addresses; registered device and host storage read those bytes directly and emit no stale relocation replay. Legacy relocation-only storage retains its existing behavior. |
| By-value aggregates manufactured null pointers when relocation metadata was absent | Two generated SPIR-V tests pass a typed struct with a nonzero interior pointer, with and without a registered target. Both previously faulted on a null pointer. Missing metadata now leaves the slot absent, so the load decodes the actual argument bytes. |
| OpenCL backing leaked when the original kernel outlived its buffer, or a sub-buffer kept its parent alive | The queued-buffer regression now includes early/late kernel release and sub-buffer ownership, for successful commands and failed dependencies. Six new reclamation assertions failed before correction. Final root-buffer destruction releases its backing independently of incidental kernel/relocation references; queued uses and child buffers retain the root object. |

The first native red run also returned HIP launch error 719 before the new pointer
case. A traced rerun passed, but inspection found a concrete HSA dirty-range race:
a producer can append between another worker's count query and drain. A deterministic
AQL regression inserts a second completion exactly between those calls and failed
with the old drain. The drain now takes the requested prefix and preserves newly
appended ranges for the later completion. The mirror handoff remains serialized.
This fixes the demonstrated race; the original untraced 719 does not identify its
cause conclusively.

Final verification:

- **240 API processes / 544 kernel launches** passed across CUDA, HIP, HSA,
  Level Zero, OpenCL and SYCL: the 234-process progress matrix plus six review
  processes with eight launches each.
- **56 focused server memory tests** and **34 SPIR-V layout, atomic, collective
  and control-flow tests** passed (330 server/SPIR-V tests including the API matrices).
- **124 native tests** passed, including the deterministic HSA drain regression
  and all eight queued OpenCL cleanup combinations.
- Changed C++ files pass formatting checks; `git diff --check` and
  `native/nativeCodestyleCheck` pass. Scala formatting and core/SPIR-V/server
  `scalafixAll --check` pass.

Logs: `/tmp/gemstone-review3-byval-red.log`, `/tmp/gemstone-review3-leak-red.log`,
`/tmp/gemstone-review3-pointer-red.log`, `/tmp/gemstone-review3-drain-red.log`,
`/tmp/gemstone-review3-final.log`, `/tmp/gemstone-review3-native-green.log`,
`/tmp/gemstone-review3-lint.log`.
No performance threshold was used, and this round did not rerun the full CTS inventory.

## Remaining limits

This covers allocation ownership, explicit borrowed ranges, interior aliases,
overlap and lock lifetime, address reuse, pointer graphs/identity, exact irregular
access and synchronization between sequential CPU edits and kernel work.

The tests cover aligned 32/64-bit host/device atomic loads, stores and RMWs, with
bounded contention. They do not establish every ISA memory-ordering combination,
unbounded-contention progress, cross-process sharing or multiple remote-device
coherence. Unsynchronized conflicting CPU/device accesses and automatic allocation-
lifetime discovery remain outside this work. Applications must keep ordinary storage alive until completion. Invalid
host accesses are not made safe by this design. Deep pointer chains still require
sequential round trips; contiguous/strided waves and independent gathers batch, but
there is no speculative fetch of surrounding storage. These tests do not model WAN bandwidth, jitter or loss. Special load
instructions without a batching hook remain correct through the exact-access fallback.

## Unified conformance migration

Public access patterns, event dependencies and synchronized CPU pointer
replacement now live in `conformance/unified/src/memory`, with shared API adapters
used by the remaining native wrappers. The unified inventory adds **42 compact /
112 full named cases** for one target per API, covering **90 / 240 launches**.
Full exercises all 16 allocation kinds; linked traversal remains 4,096 nodes.
Level Zero now builds a project-owned unified artifact as well as upstream CTS.

The current implementation-only matrices are **224 processes / 400 launches**:
218 progress cases plus six review processes. They retain deterministic CAS
collisions, exact RPC assertions, ordinary-host extensions and Gemstone queue
progress checks. The earlier counts above describe the historical pre-migration
runs. See the conformance and native memory READMEs for the current split.

Migration verification:

- All **112 unified memory cases** passed through Gemstone with no skips: CUDA
  21, HIP 21, HSA 21, Level Zero 14, OpenCL 21, SYCL 14. Compact listings select
  seven per backend; full listings select the allocation matrix above.
- Both retained server suites passed: **224 tests**, no failures or skips.
- Level Zero's standard artifact assembly includes unified by default, passes
  relocation verification, and its installed executable passes all 14 cases.
- HIP's new conformance prerequisites exposed missing unified-addressing and
  mapped-host capability flags. Native property/attribute assertions failed
  before the reporting fix and passed afterward.
- HIP's compiled unified executable also exposed an exit-time use-after-destruction:
  compiler fatbinary cleanup drained an already-destroyed command queue. The
  standalone `hip/fatbinary-exit-lifetime` regression segfaulted before the fix.
  Constructing the queue before registering its dependent exit callbacks fixes
  the lifetime order. All **17 HIP native tests** and the full 21-case HIP memory
  selection pass, including normal process exit.

These runs verify correctness and progress. They do not establish a fresh runtime
budget for the complete combined CTS workload on the noisy machine.

## Conformance ownership and provider independence

The implementation clients live under each `native/<api>/test/` directory, with
SYCL-over-OpenCL coverage under `native/opencl/test/`. Their shared test harness
lives under `native/rpc/test/` and the facade CMake files register the clients. RPC protocol tests remain under
`native/rpc/test/`. Shared public scenarios live in
`conformance/unified/src/memory_cases.hpp`, with registrations in `src/cases/memory.inl`
and adapters and entry points in each API directory. CUDA owns the PTX fixtures;
`src/memory_kernel.cl` supplies the shared HIP, HSA, OpenCL and Level Zero kernels.
The independent native clients own exact Gemstone capability assertions, ordinary-host
extensions, pending-state checks and interpreter rendezvous.

Unified memory coverage is now **66 compact / 240 full named cases**, with
**114 / 368 kernel launches** for one target per API. The new cases cover
32/64-bit acquire/release publication, explicit fences, 128-work-item atomic
return values, and failed compare-exchange publication. The 64-bit variants and
additional allocation kinds use `[full-matrix]`. Separate SPIR-V fixtures and
SYCL kernel instantiations keep 32-bit cases usable without 64-bit atomics.

The remaining server suites run **198 implementation cases / 374 launches**:
192 progress cases and six review cases. Failed-CAS transport assertions retain
one representative per API; public results cover every allocation and width in
conformance. The forced-collision cases own their retry schedule and assertions about forced
CPU updates. Public conformance assertions no longer expose integration callbacks.

Provider knowledge is outside conformance. `build.sbt` generates a temporary
launcher to supply Gemstone environment, virtual architecture and device
selection. The runtime launcher and OpenCL compiler no longer interpret conformance metadata, and
the conformance runner no longer inherits Gemstone-specific environment or
selects a named emulator. Shared adapters accept explicit arguments. Toolchain
image names are provider-neutral. The SVM CTS source-size patch tied to the RPC
collection limit was removed; the build-owned reduced run requests 256 elements
through the normal CTS adapter option instead.

Verification of this split: all **240 public memory cases** passed without skips;
all **198 implementation cases** and **9 launcher tests** passed. The conformance
driver's selector-preservation regression failed against the old hard-coded
Level Zero selector, then passed with the fix (12 driver cases / 74 assertions).
Sbt-driven Level Zero and OpenCL CTS smokes also passed through the build-owned launcher, including packaged OpenCL compiler selection.
Both coarse-grained cross-buffer and fine-grained-buffer SVM linked-list CTS
cases passed with the upstream source and 256 elements.

The independence audit also restored checks previously weakened for emulation:
rocThrust complex inputs now cover negative quadrants, allocation-failure
recovery is executable instead of an unconditional skip, and SYCL segmented
exclusive-scan output starts with a non-result sentinel. The HIP checks currently
fail during rocThrust initialization with an unimplemented AMDGPU `s_trap`,
before reaching their allocation-failure or complex-value assertions. These
failures are retained as conformance results rather than provider-specific skips.

The strengthened SYCL i32 compact segmented scan passed all nine assertions with
the sentinel initialization. Static searches found no Gemstone/RPC references in
conformance and no conformance environment handling in the runtime. Formatting
checks passed for files edited in this cleanup; whole-tree lint still reports
other existing formatting violations (including the Level Zero native smoke
and conformance driver/adapter files).

### AMDGPU trap decoding

`s_trap` is now retained as an executable IR operation. Decoding a linked failure
handler does not itself fault; actually reaching the trap produces a backend
execution fault with the immediate and PC. Six red-to-green regressions cover
unreachable linked traps and executed traps on gfx900/gfx1030. All 17 cases in
`CodeObjectExecutionSuite` pass.

The two restored HIP checks progress beyond the `__cxa_pure_virtual` trap. They
remain blocked at linked-text byte 2556: `v_and_b32_e64 v0, s4, v0` in
`__ockl_hsa_signal_add` is misclassified as unknown VOPC opcode 275 for gfx900.
This is another compile/decode failure, not an executed trap or a result from
the allocation-recovery/complex-math assertions.

## Memory source layout alignment

Memory entry points, capability checks and adapters now live in each API directory.
Shared assertions remain in `memory_cases.hpp`, with per-allocation registrations
in `cases/memory.inl`. The central `src/memory/` directory has been removed.
Existing CMake targets and image-generation helpers consume the relocated sources.

Verification: all six unified targets and six native integration clients rebuilt;
all 240 public memory cases passed without skips. Enumerated names and tags
retain the 66 compact / 240 full split. Conformance contains no Gemstone or RPC
references. This layout change does not address the separate HIP linked-code
VOP3 decoder failure documented above.

## Native test migration completed

The `native/test/memory` subproject has been removed. Each facade registers its
own `test/memory.cpp` client through its existing CMake test block. The
SYCL-over-OpenCL client belongs to `native/opencl/test/memory_sycl.cpp`; shared
transport/checkpoint scenarios live in `native/rpc/test/memory_patterns.hpp`.
Native clients call their API directly and include no conformance headers.
The native compiler dependency graph confirms this boundary.

Only build descriptions reference the reusable conformance kernel fixtures and
pinned toolchains. The SYCL client now uses the pinned container compiler and
existing runtime bundler, without host compiler discovery or a compiler-absence
skip. Public adapters no longer expose native-only allocation/overlap helpers,
integration callbacks or a disk-image mode. Their public registrations remain
66 compact / 240 full cases.

All 198 implementation regressions passed without skips, including SYCL with
its bundled runtime. Fresh native client smoke runs also passed for all six
APIs after the image-loader cleanup. Clearing the cached Level Zero loader
reproduced a missing-target configure bug; moving registration after discovery
made the same configure check pass. The native build then reported all client targets up to date.

Final public verification: all six unified builds passed and all 240 memory
cases passed without skips. Executable enumeration verified the exact case names
and allocation/width tags, preserving 66 compact / 240 full. Scoped C++ and
Scala formatting and `git diff --check` passed.

## Cross-project build boundary

Native tests and benchmark fixtures now invoke the documented
`conformance/artifacts` build entry point. Fixture exports own the memory and
vector-copy sources; command jobs own pinned toolchain execution and optional
SYCL runtime packaging. Callers pass recipes and declared inputs/outputs, then
consume artifacts. No native or benchmark build includes conformance's internal
CMake modules or reads its suite source paths. The formatting symlink remains
an explicitly accepted exception.

All six native clients and all four benchmark fixtures built through these
interfaces. Repeated builds were incremental no-ops. A copy of conformance
outside the repository exported the four CUDA fixtures and configured its
normal suite build independently. All 198 implementation regressions passed
without skips using the newly exported kernels and bundled SYCL runtime.

The full native build also caught an earlier cleanup error: removing the
existing `native/test` include directory hid `gemstone/test_support.hpp` from
HSA tests. Restoring that legitimate shared include path fixed the reproduced
compile failures; the complete native build and server regression run passed.
