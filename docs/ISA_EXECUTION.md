# ISA execution

PTX, AMDGPU and SPIR-V share dispatch, checked memory, control and binary utilities in `core`.
Each ISA owns preparation, architectural state, instruction semantics and scheduling. There is
no common executable instruction IR. The server composes `ProgramLoader`, `LoadedProgram` and
`ProgramExecutor`; see [runtime boundaries](RUNTIME.md).

Generated sources own specification facts: names, forms, operand roles, encodings and target
predicates. Handwritten code owns normalization and execution. A generated declaration or
successful parse does not prove executable support. Unsupported behavior fails explicitly.

## Shared execution contract

Generic allocation, program and completion IDs are positive, process-session-owned identities;
zero is invalid. Submission and completion have distinct identities. Arguments carry copied
scalar bytes, an allocation ID plus byte offset, or explicitly sized local memory. Exact global
extents are distinct from padded group dimensions; private/group segment sizes remain explicit.

All guest access goes through checked memory and supported provenance/mapping rules. Native
addresses can be classification or alias metadata, never foreign pointers dereferenced by the
endpoint. Dirty writes must reach host-visible storage before a provider signals completion.
An established native client cannot reconnect silently with stale allocation/program IDs; rebuild
its session after loss.

CUDA preserves exact exported C names. Its generated schema checks name, derived ID and
fingerprint together. Kernel lookup supplies parameter widths so the facade copies values behind
`kernelParams` into owned bytes. Callback function pointers remain in the native application;
see [capture/replay](REPLAY.md) for recorded callback effects.

## Scheduling and memory

`ExecutionControl` belongs to one dispatch. Direct ISA use defaults to sequential execution;
the endpoint supplies one bounded pool across clients and ISAs. Every launch gets a fresh budget.
One PTX cohort, AMD wave and SPIR-V invocation represent different work, so instruction charging
must remain explicit in the owning interpreter. AMDGPU batches charges at sequential checkpoints
but charges the shared budget before parallel waves execute.

Atomic dependency analysis includes device callees. SPIR-V tracks reachable atomic/collective
functions; AMDGPU follows branches and conservatively handles indirect calls. Local LDS atomics
do not serialize unrelated AMDGPU groups. Device-wide atomic scheduling remains conservative.
Do not invent an implicit grid barrier or promise arbitrary vector atomicity.

`Effects` validates staged writes before committing them; fast paths need equivalent checks.
Scalar operations use the existing value boundary. Vector consumers retain owned immutable
snapshots rather than exposing backing arrays. Overlays must delegate complete coherent transfers
to their shared owner, not rely on per-view monitors. Preserve bounds, lifetime, cancellation and
mixed-range behavior when optimizing.

## ISA-specific ownership

| ISA | Preparation and execution |
| --- | --- |
| [PTX](../isa/ptx/README.md) | Parser/lowering, cohorts with independent PCs and active masks, branch splitting, barriers and device-call state; shared `StateEffects` for staged writes. |
| [AMDGPU](../isa/amdgpu/README.md) | ELF/descriptors and exact targets; packed wave execution, SGPR/VGPR masks, scratch and LDS. Global regions pass explicitly through workers. |
| [SPIR-V](../isa/spirv/README.md) | Binary/environment validation; type/layout metadata, invocation values, checked pointers, calls and suspended collectives. Preserve `ByVal` layout and decorated strides. |

Full-grid execution does not replace work with kernel-name shortcuts or boundary-only samples.
Do not unify operations merely because their names match: carry/borrow, shifts, signedness,
rounding, pointer provenance and visibility must agree. Changes to hot dispatch loops need
correctness regressions and profiling on both JVM and Native.

`GEMSTONE_PERF_TRACE` exposes provider transfer/scan counts and parallel dispatch observations.
SPIR-V also offers opt-in `Interpreter.PerformanceMetrics`. Profiling and supported execution
coverage remain separate from grammar/oracle inventory counts.

## Deferred paging constraints

Fault-driven paging is deferred in the [plan](../GEMSTONE_PLAN.md), not an accepted coherence model.
Preserve these constraints if that work resumes:

- Handle only owned live mappings; preserve unrelated faults, prior handlers and errno. Allocation,
  logging, managed code, symbol lookup and ordinary RPC do not belong in a fault handler. Parking
  and wakeups need an audited platform-specific path; the pager cannot depend on locks or managed
  pages held by the interrupted thread.
- Start from an agreed page-isolated allocation scope. Do not protect allocator headers or unrelated
  objects sharing a heap page. Query protection/transfer/allocation granularities. Fill privately
  before publishing access, revoke conflicting ownership before writes, and retire generations and
  in-flight users before reuse/free/disconnect.
- CPU protection does not intercept GPU faults. Kernel I/O needs a prefault/pin contract; an
  inaccessible syscall buffer is not equivalent to a recoverable user-mode load. Opaque hardware
  pointer graphs need valid address mappings or explicit metadata, never guessed pointer words.
  Concurrent CPU/GPU atomics and shared writers need their own vendor-compatible contract.
- RMA does not imply hardware RDMA. Transfers must preserve versions, ownership, registration and
  in-flight retirement; owner loss cannot publish stale data into a reused allocation.
- Windows guard pages are one-shot, not a persistent ownership barrier. Signals, userfaultfd,
  vectored exceptions and Mach exceptions have different safety, forwarding and debugger rules;
  no platform is qualified merely by using a similar API.

Primary references: [signal safety](https://man7.org/linux/man-pages/man7/signal-safety.7.html),
[userfaultfd](https://docs.kernel.org/admin-guide/mm/userfaultfd.html),
[Windows guard pages](https://learn.microsoft.com/en-us/windows/win32/memory/creating-guard-pages),
[Mach exceptions](https://developer.apple.com/documentation/kernel/1538049-task_set_exception_ports).
