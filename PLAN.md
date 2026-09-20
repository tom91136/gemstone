# Core ISA and Virtual Machine Architecture Plan

## Purpose

Refactor the shared execution model so PTX, AMDGPU, and SPIR-V implement a small, coherent ISA/VM model while the server retains ownership of API identities, transport, lifecycle, and asynchronous execution.

The desired conceptual path is:

```text
image -> Decoder -> immutable Program -> VirtualMachine -> KernelTask -> Result
                                              |
                                      Observer / Scheduler
```

The native API and RPC path is related but separate:

```text
native API -> RPC -> server session -> runtime adapter -> typed ISA/VM
```

This is a structural refactor. It must preserve current execution behaviour, native API behaviour, validation, diagnostics, concurrency guarantees, and performance characteristics unless a change is explicitly called out below. It also owns bringing the repository to a clean `scalafixAll --check` result under the new lint policy.

## Working rules

- Re-read all affected files immediately before editing them. In particular, the PTX package is under active unrelated refactoring; do not rely on paths, names, or structure recorded in this plan.
- Preserve user changes and avoid broad mechanical rewrites.
- Prefer small hand-edited patches. Use existing generators only for artifacts that are genuinely generated, after the hand-written schema/source change is settled, and review their diffs.
- New and materially changed handwritten sources must satisfy the active lint rules when introduced. Do not postpone new lint debt to the final cleanup phase.
- Do not make the lint check green through broad suppressions. A narrow suppression is acceptable only when it documents a real, reviewed exception such as a generator-proven unchecked refinement or measured hot path.
- Do not combine this architecture migration with parser, instruction-semantic, code-generation, or performance clean-ups.
- Keep the old and new paths side by side only while a backend is actively migrating. Every temporary bridge must be named in `WORK.md` with its deletion point, and must be removed as soon as its final consumer moves.
- Do not preserve obsolete entry points, aliases, wrappers, forwarding layers, fallback paths, or duplicate representations for hypothetical compatibility. The finished tree has one current path for each operation.
- Outside ISA decoding, validation, preparation, and execution, perform only checks required for transport safety, ownership, and live resource access. Do not duplicate ISA semantic checks in the server, RPC, or native facades.
- Compile and test JVM and Scala Native variants at each viable boundary rather than postponing cross-platform verification.

## Decisions already made

- The submitted unit of kernel work is `KernelTask`.
- `Program` is immutable decoded program content.
- Mutable per-load state is represented separately as a VM-owned loaded program.
- `VirtualMachine` owns the execution surface and retains its `Observer` and `Scheduler` dependencies.
- API/RPC identifiers and lifecycle are not VM concepts.
- `ProgramFormat` is removed as a closed enumeration. The transport uses an open program-encoding identifier.
- PTX, AMDGPU, and SPIR-V remain strongly typed inside their modules. Type erasure happens only in the server/runtime adapter.
- ISA tooling capabilities such as decoding, printing, validation, and execution remain small and composable; there is no monolithic interface that forces every ISA to support every capability.
- Automatic program-format sniffing is not introduced. Native API facades already know which encoding they send, and explicit selection gives better diagnostics.
- Server submission/completion remains asynchronous; backend VM execution remains synchronous. Use `execute` for the VM operation and `submit` for the server/runtime operation so the two scheduling layers are not confused.
- The current PTX organization is the reference shape for the other ISA modules: a cohesive named root per file, closely related vocabulary in its companion, and explicit components such as `Parser`, `Printer`, `Validator`, `Lowering`, `ISA`, and `VirtualMachine`. AMDGPU and SPIR-V should converge on the same architectural clarity without copying PTX-specific stages or terminology where their semantics differ.
- No new ABI version negotiation, compatibility mode, capability handshake, dual schema reader, or speculative verification layer is introduced. Update the single current implementation and all in-tree consumers together.
- Every abstraction must represent a real boundary or owned behaviour. Remove layers that merely rename, forward, or pre-emptively generalize another layer.

## Target dependency direction

```text
core utilities
    |
    +-- isa interfaces
    +-- vm interfaces and values
              |
              +-- PTX implementation
              +-- AMDGPU implementation
              +-- SPIR-V implementation
                         |
                  server runtime adapters
                         |
                   RPC protocol/codecs
                         |
                  native API facades
```

The following reverse dependencies must disappear:

- ISA and VM domain types deriving or importing RPC `Wire.Codec`.
- ISA implementations returning transport-oriented `ExecutionFailure` directly.
- VM tasks containing `ProgramId`, `AllocationId`, or `CompletionId`.
- Core ISA interfaces enumerating every supported program encoding.
- Memory interfaces reporting server completion data such as pointer relocations.

Keep this separation within the existing project structure unless implementation reveals a real build dependency that cannot otherwise be expressed. Do not add an SBT project, adapter layer, or permanent checker solely to restate the intended direction; make the source imports and ownership direct and obvious.

## Source layout and lint policy

The new lint rules are architectural constraints, not a formatting pass. The initial `sbt 'scalafixAll --check'` baseline is red. The first observed failures are:

- `GemstoneFilenameConventions` across grouped handwritten files in core, AMDGPU, SPIR-V, server, codegen, tests, and benchmarks;
- `GemstoneFilenameConventions` across generated API and ISA sources;
- `GemstoneForbiddenAPIs` for direct `java.lang.Runtime` use in the launcher.

The first diagnostic per file is not necessarily the only violation, and JVM/Native project matrices repeat some diagnostics. Re-run the complete check after every batch until no hidden follow-on violations remain. The final authority is the full repository command, not a manually maintained list of known errors.

The active policy also requires:

- no unsuppressed `var`, `return`, `null`, XML literals, or finalizers;
- explicit method-value eta expansion;
- portable APIs in place of banned JVM APIs;
- only the standard wildcard Iron imports;
- checked Iron refinement at input boundaries, with narrow visible suppressions for proven values or measured hot paths;
- Scala 3 creator syntax rather than explicit or anonymous `new` allocations;
- explicit concurrency primitives rather than intrinsic monitors;
- named top-level declarations to match their source filename.

### File cohesion convention

Follow the shape already established by the current PTX refactor:

- a file has one named top-level root matching the filename;
- a companion may contain the vocabulary that is structurally owned by that root;
- a genuinely independent concept gets its own matching file;
- generic catch-all files such as `Syntax.scala`, `ExecutionState.scala`, `Identifiers.scala`, or `Programs.scala` should disappear when their contents have distinct owners;
- do not create artificial nesting solely to silence the linter: use companions where there is real ownership and separate files where concepts stand independently.

Examples from the intended PTX shape are `Program.scala` with the PTX syntax vocabulary in `Program`, `Metadata.scala` with metadata vocabulary in `Metadata`, generated `Instruction.scala` with instruction definitions in `Instruction`, and `VirtualMachine.scala` with the executable model in `VirtualMachine`.

### Generated sources

Never hand-edit a generated source to satisfy lint. Update its generator or generated-source layout, regenerate, and review the result.

Generated API surfaces and generated AMDGPU/SPIR-V instruction models currently violate the filename rule. Bring their generators toward a named facade/companion or correctly partitioned file layout. PTX's generated `Instruction.scala` is the reference for attaching definitions and metadata to the generated instruction root. For large native API surfaces, choose a generator-owned layout that remains usable and does not create thousands of incidental hand-maintained files; update imports and consumers deliberately after the generated public shape is chosen.

Generated sources may retain narrow rule suppressions only where the generator itself proves the invariant, as PTX currently does for checked refinements. Do not disable filename/cohesion checks for the generated tree as a blanket workaround.

### Lint execution strategy

- Fix unrelated handwritten and generated baseline violations in a dedicated early workstream so they do not obscure architecture diagnostics.
- Fix AMDGPU and SPIR-V layout violations as part of their convergence toward the PTX reference shape, avoiding a temporary split followed by a second structural rewrite.
- Run project-scoped Scalafix checks after each migrated component and the full `sbt 'scalafixAll --check'` at integration points.
- Keep `codestyleTests/test` green whenever a lint rule or its fixture changes.
- End every migration phase with no new lint failures in its touched scope, even if a documented untouched baseline remains elsewhere.

### Terminology cleanup

Remove the word `contract` from all repository-authored identifiers, comments, diagnostics, test names, and documentation. Replace each occurrence with the specific idea intended, such as interface, invariant, behaviour, requirement, boundary, protocol, guarantee, layout, or semantics. This plan is the sole explanatory exception and names the prohibited word so the requirement is unambiguous.

The initial inventory includes authored occurrences in performance documentation, core ABI comments, PTX/AMDGPU/SPIR-V implementation comments, native CUDA/HSA/Level Zero code and tests, and conformance source comments/messages. Rename code identifiers and their call sites as well as prose. Do not alter unrelated technical names that merely contain the same character sequence as part of a different word, and do not rewrite third-party or legal licence text.

Add a repository lint check over authored Scala, C, C++, build, and Markdown sources so the removed term cannot be reintroduced. Its exclusions must be narrow and explicit: build outputs, VCS metadata, immutable third-party licence text, and externally fixed names whose spelling only happens to contain the same character sequence as part of a different word. The check must cover generated output too; if a generator emits the prohibited terminology, fix the generator.

## Target core model

The exact Scala encoding should be validated with small compile experiments, especially on Scala Native. The intended shape is:

```scala
package net.kurobako.gemstone.isa

trait Program

trait Decoder[-Input, +P <: Program] {
  type Error
  def decode(input: Input): Either[Error, P]
}

trait Printer[-A] {
  def render(value: A): String
}
```

`Decoder` has no format/encoding member. A decoder knows how to decode its input; server routing is not its responsibility.

The VM side should have this responsibility split:

```scala
package net.kurobako.gemstone.vm

trait VirtualMachine[P <: isa.Program] {
  type LoadedProgram
  type Event

  def observer: Observer[Event]
  def scheduler: Scheduler

  def load(program: P): Either[Fault, LoadedProgram]

  def execute(
      program: LoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[Fault, Result]

  def unload(program: LoadedProgram): Unit
}
```

Do not expose path-dependent types through the server. Each backend adapter closes over its typed `Program`, `VirtualMachine`, and `LoadedProgram` and exposes an erased runtime program.

### Program and loaded-program state

`Program` contains immutable decoded syntax or executable content. It may internally retain immutable lazy indexes and caches where safe.

The loaded program owns state associated with one logical API load:

- global bindings;
- writable global images;
- compiled/lowered kernel caches that depend on bindings;
- target/environment selection;
- any other state that must not be shared between two program loads or two server sessions.

Loading the same immutable program twice must create isolated loaded state.

### KernelTask

`KernelTask` replaces `KernelDispatch` in the VM domain and contains:

- kernel name;
- launch geometry;
- arguments;
- private and group segment sizes;
- input pointer relocations.

It does not contain `ProgramId`. The server resolves `ProgramId` to a runtime program before invoking the VM.

API allocation identities must likewise be translated before VM execution. Introduce a VM-domain `MemoryRegion` (or equivalent neutral reference), and make `KernelArgument` and relocation types refer to it. The server may initially preserve the same numeric representation internally, but the conversion must be explicit at the boundary.

### Memory

Rename and narrow `ExecutionMemory` to a VM-domain `Memory` interface:

- read and write bytes;
- optimized scalar read/write;
- atomic modification where supported;
- optional address resolution through a separate address-space capability rather than `allocationAddresses` snapshots on every memory implementation.

Remove `recordPointerRelocation` from memory. A relocation produced by execution is an execution event/result, not a storage operation. Dirty-range tracking remains in the server's memory adapter because it describes changes to API-owned mirrored allocations.

PTX's virtual address mapping, AMDGPU's code-object/global addresses, and SPIR-V's logical pointers must all be expressible through the common memory/address-space boundary without exposing API allocation IDs.

### Control, scheduling, and observation

Split the current `ExecutionControl` responsibilities:

- `TaskControl`: per-task instruction budget, cancellation, and usage accounting;
- `Scheduler`: bounded internal scheduling of workgroups/work-items;
- `Observer[Event]`: execution event delivery;
- `Result`: terminal VM result and execution statistics.

Rename the role currently represented by `ExecutionParallelism` to `Scheduler`. This scheduler is used inside a kernel execution. It is not the server queue that schedules whole kernel tasks.

The VM retains the observer and scheduler supplied at construction. The server owns the concrete implementations and their mutable resources. This is dependency injection: ownership of state remains with the server, while the VM owns the references and decides when to invoke them.

Define event-delivery failure semantics explicitly. Because print delivery is currently fallible and semantically observable, `Observer.observe` should return a typed error that the VM maps to `Fault`; it must not silently drop output. Common events should cover at least printing and output pointer relocations. Backend-specific event types, such as PTX instruction traces, remain backend-specific.

Tracing must retain its current deterministic semantics. PTX currently changes parallel scheduling when trace capture is enabled; replace the boolean with an explicit observation mode/capability rather than accidentally changing that behaviour.

### Errors

Use errors at their owning boundary:

- decoder-specific errors for malformed source/binary input;
- validation/preparation errors owned by the ISA backend;
- VM `Fault` for execution failure;
- `RuntimeError` for IDs, lifecycle, unsupported runtime capabilities, and session state;
- RPC failure DTOs only at the transport boundary.

Adapters perform explicit mappings. Do not make backend errors derive wire codecs merely to avoid the mapping.

### Program metadata and optional capabilities

Keep the format-neutral kernel/global metadata needed by native API facades, but remove wire derivation from the domain definitions.

Rename the current `ProgramCatalog` to `ProgramSymbols`. It is not a registry and owns no programs; it is the immutable exported symbol surface used by native facades to construct kernel and global handles:

```scala
final case class ProgramSymbols(
    kernels: Vec[KernelSymbol],
    globals: Vec[GlobalSymbol]
)
```

Keep detailed per-kernel metadata as a separate lookup because argument layout may be expensive or lazily decoded. `BackendCatalog` remains the server registry of available backends, while `ProgramStore` owns loaded runtime programs; neither should be confused with `ProgramSymbols`.

Avoid a base trait with many methods that default to `Unsupported`. The erased runtime program should expose required introspection plus explicit optional capability facets, for example:

```scala
trait RuntimeProgram {
  def symbols: Either[RuntimeError, ProgramSymbols]
  def kernel(name: KernelName): Either[RuntimeError, KernelMetadata]
  def globals: Option[RuntimeGlobals]
  def execute(task: KernelTask, memory: Memory, control: TaskControl): Either[RuntimeError, Result]
  def close(): Unit
}
```

If binding and writing are independently optional in practice, split `RuntimeGlobals` further rather than adding unsupported default methods.

## ISA tooling versus executable backends

Do not force one `ISA` aggregate to imply that a printable syntax program and a loadable executable container are the same thing.

Small capabilities can be composed where they naturally line up:

```scala
trait Language[P] {
  def decoder: Decoder[?, P]
  def printer: Printer[P]
}
```

Validation and compilation/preparation remain separate capabilities. A backend can expose language tooling, executable loading, or both.

### PTX

Re-read the current PTX package before every PTX step. The current working tree already has `Program`, `Parser`, `Printer`, `ISA`, `VirtualMachine`, and the `VM*` implementation split; preserve and build on that refactor.

Target responsibilities:

- `Parser`/`Decoder`: PTX text to immutable `PTX.Program`.
- `Printer`: canonical `PTX.Program` rendering.
- validation: PTX program plus explicit PTX target/version.
- VM load: collect entries/functions/globals and establish per-load binding/lowering state.
- VM execute: resolve the task's kernel, lower lazily using current bindings, prepare arguments/address space, and execute.
- observer: replace `captureTrace` and the private trace accumulation switch with explicit PTX observation.
- runtime adapter: translate PTX errors and metadata to the server boundary.

The current `ModuleDecoder` is the legacy composition point. Decompose it rather than duplicating its logic: parsing belongs to the decoder, per-load global/lowering state belongs to the loaded program, and server error/metadata translation belongs to the runtime adapter.

PTX global rebinding currently invalidates the lowered-kernel cache. Preserve that invariant explicitly in loaded-program state.

### SPIR-V

Target responsibilities:

- binary decoder: bytes to immutable SPIR-V `Module`/`Program`;
- VM load: validate for an explicitly supplied environment and construct/cache `PreparedProgram`;
- VM execute: wrap the existing interpreter;
- runtime adapter: expose kernel metadata and translate failures.

Converge its source shape toward PTX while retaining SPIR-V terminology:

- replace the broad `Syntax.scala` grouping with cohesive roots and companions;
- keep `Module` if that is the correct SPIR-V domain term, and make it satisfy the common program interface rather than renaming it merely for symmetry;
- make generated `Instruction.scala` own its instruction definitions, decoder function type, and instruction metadata through `Instruction` and its companion, following the PTX generated layout;
- keep raw binary records under a cohesive `Binary`/raw-module owner, separate from the strict typed module;
- expose a small `ISA` facade for version, instruction definitions, decode/encode, and validation;
- present execution through `VirtualMachine`, with the existing interpreter and operation files becoming implementation details behind it;
- give remaining independent implementation roots matching files rather than collecting them into another `Syntax` or `Execution` catch-all.

Do not infer the execution environment solely from the presence of entry points. The OpenCL and Level Zero adapters should select an appropriate environment/backend configuration explicitly so future SPIR-V environments do not silently inherit OpenCL rules.

SPIR-V currently lacks a canonical printer. Do not add an unsupported placeholder merely for symmetry. Add a real printer later as an independent capability if required.

### AMDGPU and HSA code objects

Preserve the distinction revealed by the current implementation:

- AMDGPU assembly has its own syntax `Program`, parser, printer, validator, encoder, and decoder.
- An HSA code object is an executable ELF container and is not the AMDGPU assembly syntax tree.

The HSA code-object backend should therefore provide a code-object decoder/loader and AMDGPU VM without pretending its loaded container is printable assembly.

Target responsibilities:

- immutable program: validated image and immutable/lazy ELF index;
- loaded program: selected kernel variants, mutable global images, global bindings, and prepared-kernel cache;
- VM execute: wrap `DispatchInterpreter.prepare/execute`;
- runtime adapter: expose code-object symbols/metadata and translate failures.

Converge its source shape toward PTX while retaining AMDGPU's family/target and binary-codec semantics:

- replace `Syntax.scala` with `Program.scala`, making `Program` and its companion own assembly statements, operands, modifiers, parse errors, and closely related refined names;
- provide top-level `Parser`, `Printer`, and `Validator` components rather than hiding the printer inside `ISA`;
- make generated `Instruction.scala` own instruction definitions, decoders, forms, and generated instruction metadata through `Instruction` and its companion, mirroring the successful PTX generator structure where practical;
- keep genuinely independent concepts such as `Target`, `Family`, or binary encoding in matching roots/files when nesting them in `Metadata` would reduce cohesion;
- expose a compact family-aware `ISA` facade over instructions, validation, encoding, and decoding;
- introduce the public `VirtualMachine` execution surface, with dispatch interpretation, wave state, register state, and executable instructions organized as VM-owned companion vocabulary or clearly named private implementation roots;
- retain `CodeObject` as a separate executable-container domain with its own decoder/loader and runtime adapter.

Audit the current shared mutable `globalMemory` arrays carefully. Two loads of the same image and two sessions must not share writable global state. Preserve deferred indexing and lazy kernel materialization so the refactor does not make program loading substantially more expensive.

## Server/runtime design

### Root ownership

```text
GemstoneServer
|-- BackendCatalog
|-- WorkerPool
`-- ExecutionSession (per RPC/client session)
    |-- AllocationStore        owns AllocationId
    |-- ProgramStore           owns ProgramId
    |-- CompletionStore        owns CompletionId
    |-- TaskQueue              schedules whole KernelTasks
    |-- CompletionObserver
    `-- BackendSessions
        |-- PTX VM
        |-- AMDGPU VM
        `-- SPIR-V VM
```

Backend sessions are created per execution session, preferably lazily. They receive the session's observer and bounded internal scheduler. No loaded program or VM-owned mutable state is shared across client sessions unless it is proven immutable and deliberately cached above the session boundary.

### Open backend registration

Replace `ProgramFormat` with a transport/runtime value such as:

```scala
opaque type ProgramEncoding = String
```

Use stable identifiers such as:

- `ptx-text`;
- `hsa-code-object`;
- `spirv-binary`.

These identify encodings, not ISAs. They should not contain version numbers already carried and validated by the image unless routing genuinely requires a distinct decoder.

The server composition root registers entries:

```scala
trait RuntimeBackend {
  def encoding: ProgramEncoding
  def open(services: MachineServices): BackendSession
}
```

`BackendCatalog` is a simple immutable mapping assembled by the server composition root. Adding a backend requires a new registration, not an edit to a core enum or an exhaustive protocol match. Do not add a plugin lifecycle, discovery protocol, duplicate-registration audit, or capability negotiation around this static in-tree composition.

### Load flow

```text
LoadProgram(encoding, image)
  -> select RuntimeBackend
  -> obtain/create BackendSession
  -> decode immutable Program
  -> VM.load Program into isolated LoadedProgram
  -> wrap typed values as RuntimeProgram
  -> assign ProgramId in ProgramStore
```

If any stage fails, close any partially created loaded state and do not publish the ID. Preserve the current rule that expensive decoding/loading does not occur while holding the session state lock.

### Execution flow

```text
RPC launch DTO
  -> validate ProgramId and AllocationIds
  -> resolve RuntimeProgram and MemoryRegions
  -> construct VM-domain KernelTask
  -> reserve CompletionId and TaskControl
  -> enqueue server job
  -> RuntimeProgram.execute
  -> observer/memory adapter update CompletionRecord
  -> publish terminal completion state
```

The runtime's launch DTO contains `ProgramId`; `KernelTask` does not. Keep these as distinct types rather than reusing one structure on both sides of the boundary.

The task queue owns asynchronous execution and completion transitions. The VM's `Scheduler` only decides internal workgroup/work-item parallelism during one execution.

### Observation and completion

Use an internal `TaskToken` to correlate VM events. It is not `CompletionId` and requires no wire codec.

The session's completion observer maps `TaskToken` to the relevant completion record and records:

- print events;
- output pointer relocations;
- optionally common start/finish events;
- backend-specific debug traces only when explicitly enabled.

The runtime memory adapter records dirty allocation ranges. Cancellation remains owned by the completion/task control, and the VM polls it through `TaskControl`.

### Global operations

`ProgramStore` resolves a `ProgramId` to `RuntimeProgram`. Describe, bind, and write operations use the runtime program's explicit capabilities. The session continues to enforce:

- no unload while a task is pending;
- no binding race with pending execution;
- allocation retention while bound or referenced by a completion;
- rollback if a bind/load operation fails or session close races it.

The backend owns the meaning and implementation of a global binding; the session owns API allocation lifetime and concurrency policy.

### Unload and session close

Unload removes the runtime program from visibility only after lifecycle checks and then invokes `RuntimeProgram.close`. Closing a successfully loaded runtime program must be total, idempotent, and non-throwing; lifecycle errors are detected before removal rather than delegated to the ISA backend. Session close cancels pending work, drains/marks completions consistently, closes every runtime program, and finally releases backend-session resources.

## RPC and native boundary

Move domain codecs out of ISA/VM definitions. Define wire DTOs and codecs under the RPC/protocol layer and map explicitly to domain values. The wire layer enforces only its existing structural and payload bounds; ISA meaning is checked by the selected decoder and VM.

Change `LoadProgram` from the closed `ProgramFormat` enum to `ProgramEncoding` as one coordinated in-tree update:

- update the hand-written RPC schema;
- regenerate derived Scala/C++ bindings with the existing generator;
- inspect generated diffs;
- update CUDA, HSA/HIP, OpenCL, and Level Zero callers to send the stable encoding identifier;
- regenerate the existing RPC fingerprint and journal/replay fixtures only where the current generator requires it;
- verify current-client round trips and the unknown-encoding error path.

Do not add an RPC or ABI version field, version negotiation, a legacy decoder, dual readers, compatibility shims, or fallback routing. The repository moves to the new schema atomically.

Do not expose backend-specific decoder or VM errors directly on the wire. Map them to the existing stable runtime failure categories while preserving detailed messages.

## Migration phases

### Phase 0: inventory and baseline

- Re-read the dirty working tree, especially PTX, immediately before implementation.
- Record current JVM and Native compile/test status without changing unrelated failures.
- Record the full `scalafixAll --check` output, collapse duplicate JVM/Native diagnostics into unique source issues, and track the baseline to zero.
- Fix unrelated lint failures that would otherwise obscure the architecture work, including launcher forbidden APIs and filename/cohesion failures in core utilities, server support, codegen, tests, and benchmarks.
- Identify every generated lint failure and its owning generator; never patch the generated output directly.
- Remove `contract` from repository-authored code, identifiers, comments, diagnostics, tests, and documentation; rename all references together and add the repository terminology check.
- Map all uses of `ProgramFormat`, `ProgramDecoder`, `ProgramInstance`, `ProgramExecutor`, `KernelDispatch`, `ExecutionMemory`, `ExecutionControl`, `ExecutionOutput`, and direct `Wire.Codec` derivation.
- Inventory existing deprecated aliases, compatibility helpers, fallback branches, pass-through wrappers, duplicate validation, and migration residue so their deletion is assigned to the phase that removes their final consumer.
- Identify generated files and their owning generators before editing protocol artifacts.
- Add no architecture code in this phase.

### Phase 1: introduce clean domain interfaces

- Add `isa.Program`, `Decoder`, and `Printer` interfaces.
- Add VM `KernelTask`, `Memory`, `TaskControl`, `Scheduler`, `Observer`, `Fault`, `Result`, and `VirtualMachine` interfaces.
- Move or mirror format-neutral metadata without wire derivation.
- Add focused behaviour tests, including task validation and scheduler/control semantics.
- Give every new top-level type a matching file and keep owned helper vocabulary in its companion.
- Keep legacy execution interfaces compiling for the moment.

Exit criterion: the new interfaces compile on JVM and Native, import no RPC package, and their project-scoped Scalafix checks pass.

### Phase 2: introduce server runtime adapters alongside legacy registry

- Add `RuntimeBackend`, `BackendSession`, `RuntimeProgram`, `BackendCatalog`, and capability facets.
- Add adapters between API allocation IDs and VM memory regions.
- Add the session observer, task token, and runtime-to-VM error mapping.
- Exercise the design with a small fake backend before adapting a real ISA, then delete that migration fixture once the real backend coverage replaces it.

Exit criterion: server lifecycle, load, execute, completion, cancellation, and unload can be tested through the new abstractions with a fake backend.

### Phase 3: migrate PTX as the first vertical slice

- Re-read and preserve the latest PTX refactor.
- Make the existing PTX `Program`, parser, and printer satisfy the new tooling interfaces with minimal reshaping.
- Move module preparation, global bindings, and lowering caches from `ModuleDecoder` into PTX VM loaded-program state.
- Lift VM execution from a pre-lowered kernel to a loaded PTX program plus `KernelTask`.
- Route trace and output through the new observer while retaining deterministic trace behaviour.
- Add the PTX runtime backend adapter and run existing PTX direct and server tests.
- Preserve the current PTX file/cohesion shape and keep PTX handwritten and generated sources lint-clean throughout the migration.
- Do not delete the legacy decoder until the server can load PTX through the new registry.

Exit criterion: PTX direct tooling, VM execution, global binding, tracing, CUDA server integration, and JVM/Native tests pass through the new path.

### Phase 4: migrate SPIR-V

- Replace `Syntax.scala` and other grouped roots with the cohesive module/instruction/binary/VM layout described above.
- Update the SPIR-V generator so generated instruction declarations live under the matching `Instruction` root and regenerate its sources.
- Adapt binary decoding to immutable SPIR-V program content.
- Move validation/preparation into VM load with explicit environment configuration.
- Wrap interpreter execution behind the VM interface.
- Route printf and relocation events through the observer.
- Add OpenCL and Level Zero runtime backend configuration and adapters.

Exit criterion: SPIR-V direct execution and OpenCL/Level Zero integration pass without the legacy `ProgramInstance` path, and SPIR-V JVM/Native Scalafix checks pass.

### Phase 5: migrate AMDGPU/HSA code objects

- Replace `Syntax.scala`, `ExecutionState.scala`, and generated catch-all declarations with the cohesive Program/Instruction/Metadata-or-independent-root/VM layout described above.
- Update AMDGPU generators before regenerating instruction metadata, architecture facades, and encoding constants; do not repair generated output by hand.
- Keep assembly language tooling separate from code-object loading.
- Separate immutable code-object data/indexes from per-load writable global state.
- Wrap dispatch preparation/execution behind the AMDGPU VM.
- Implement global capability facets and the HSA code-object runtime adapter.
- Verify HIP and HSA paths, target variant selection, deferred failure behaviour, atomics, address aliases, and multiple independent loads.

Exit criterion: AMDGPU direct tests plus HSA/HIP integration pass through the new path with no cross-load global-state sharing, and AMDGPU JVM/Native Scalafix checks pass.

### Phase 6: switch server ownership and remove legacy execution abstractions

- Make `ExecutionSession`/`ExecutionService` use `BackendCatalog`, `ProgramStore`, and `RuntimeProgram` exclusively.
- Rename the internal work-item scheduler and distinguish it from the whole-task queue.
- Remove `ProgramExecutor`, `ProgramInstance`, `ProgramDecoder`, and `KernelDispatch` after all call sites migrate.
- Remove unsupported default methods and obsolete adapters.
- Remove temporary backend bridges immediately after the server switches; do not retain legacy entry points for tests or hypothetical external consumers.
- Remove API IDs from VM-domain structures.

Exit criterion: no ISA module imports server or RPC packages, and no VM interface references API/runtime IDs.

### Phase 7: replace ProgramFormat in RPC/native code

- Introduce open `ProgramEncoding` in the runtime/protocol layer.
- Change the RPC schema and regenerate only the derived bindings.
- Update every native facade and protocol test.
- Delete `ProgramFormat` and exhaustive decoder-format maps.

Exit criterion: a new registered encoding can be loaded without modifying a closed core enumeration or the RPC operation schema.

### Phase 8: enforce, simplify, and remove residue

- Audit imports and remove every reverse dependency from `vm` and `isa` domain packages to RPC/server packages; keep the boundary evident in the source rather than adding a new enforcement subsystem.
- Consolidate documentation and remove every transitional path.
- Delete all legacy adapters, deprecated aliases, old/new conversion helpers, temporary type erasure bridges, dual registries, fallback selection branches, migration-only feature flags, obsolete test fixtures, and dead files introduced or exposed by the migration.
- Audit remaining adapter and wrapper types. Retain only conversions that cross a real transport, ownership, memory, or typed-backend boundary; inline or delete layers that only forward calls, rename values, or anticipate an unimplemented extension.
- Audit validation outside the ISA modules. Retain transport bounds, identifier/resource lookup, allocation bounds, and lifecycle/concurrency enforcement; remove duplicated program, kernel, instruction, target, metadata, and execution checks that belong to decoder/validator/VM code.
- Remove speculative ABI/version infrastructure, compatibility readers, negotiation, fallback routing, and pre-emptive capability checks. There is one supported in-tree protocol and one current implementation path.
- Search for all removed type names and migration markers and require zero live references before declaring cleanup complete.
- Resolve every remaining handwritten and generated lint baseline issue, then run `sbt 'scalafixAll --check'` until it completes cleanly across the full project matrix.
- Run `codestyleTests/test` and keep all rule fixtures current.
- Run the full JVM, Native, native-runtime, integration, and conformance-relevant test suites.
- Compare representative execution performance and allocation behaviour with the baseline.

Exit criterion: the repository contains only the final architecture, every remaining indirection corresponds to a named real boundary, and no migration scaffolding or speculative compatibility machinery remains.

## Verification matrix

### Codestyle

- `codestyleTests/test` passes for every custom rule;
- the repository terminology check passes across authored Scala, native, build, generated, and Markdown sources;
- touched projects pass their JVM and Native Scalafix checks at the end of each phase;
- generated sources are lint-clean when regenerated from a clean tree;
- no blanket suppression is introduced for filename conventions, generated trees, or an entire project;
- the final full `sbt 'scalafixAll --check'` succeeds;
- the standard aggregate lint command in `build.sbt` succeeds, including format, native, and conformance lint checks.

### Core/VM

- `KernelTask` validation, including geometry, segment sizes, arguments, and relocation bounds.
- Instruction budget accounting under sequential and parallel scheduling.
- Cancellation and observer failure propagation.
- Memory scalar/atomic operations and address resolution.
- Scheduler fail-fast behaviour.
- Domain packages compile without RPC imports.

### PTX

- parser/printer round trip;
- validation and lowering diagnostics;
- kernel metadata and exported program symbols;
- global binding invalidates dependent lowering caches;
- argument/address-space translation;
- deterministic trace mode and normal parallel mode;
- cancellation and instruction budget;
- CUDA integration.

### SPIR-V

- binary decode/encode behaviour;
- explicit environment validation;
- prepared-program isolation;
- kernel metadata;
- pointer relocation events;
- printf events;
- atomics and parallel execution;
- OpenCL and Level Zero integration.

### AMDGPU

- assembly parser/printer remains independent;
- ELF header validation and deferred full indexing;
- target variant and alias selection;
- kernel metadata/exported symbols;
- global bind/write behaviour and alias overlap;
- isolated mutable global images across two loads and sessions;
- prepared-kernel cache;
- atomics and address aliases;
- HSA and HIP integration.

### Server/runtime

- unknown encoding failure;
- load failure does not publish a `ProgramId`;
- concurrent load does not hold the session state lock during decode;
- task submission and completion transitions;
- cancellation and session loss;
- bind/unload/pending-task race rules;
- allocation retention and dirty ranges;
- observer event correlation by task token;
- cleanup after close and partial failure.

### RPC/native

- open encoding round trip and bounds;
- generated client/server agreement;
- current-schema RPC journal/replay behaviour;
- no ABI version negotiation, legacy reader, or fallback route;
- CUDA sends PTX encoding;
- HSA/HIP send HSA code-object encoding;
- OpenCL/Level Zero send SPIR-V encoding;
- native smoke and session-loss tests.

## Risks and mitigations

- **PTX refactor overlap:** re-read before every edit, make narrow patches, and never restore files from an earlier shape.
- **Scala path-dependent type complexity:** keep typed VM pairings inside backend adapters; expose closures through `RuntimeProgram`; prove JVM/Native compilation early.
- **AMDGPU shared global state:** add isolation tests before moving ownership, then move mutable images to loaded state.
- **SPIR-V environment ambiguity:** make environment an explicit backend-session configuration chosen by the API adapter.
- **Trace/scheduler semantic change:** characterize existing trace ordering and parallelism first; preserve it with explicit observation mode.
- **Protocol update drift:** change the one current schema, generated bindings, fixtures, and all in-tree callers atomically; do not mitigate drift with a second reader or compatibility layer.
- **Performance regression from eager preparation:** preserve PTX/AMDGPU/SPIR-V lazy caches and benchmark load plus first execution.
- **Over-generalized common API:** keep interfaces small and use capability facets; do not add no-op or unsupported methods merely to make backends look identical.
- **Confusing scheduling layers:** reserve `Scheduler` for intra-kernel VM work and `TaskQueue`/worker pool for server-level kernel tasks.
- **Filename-rule churn:** use the PTX ownership pattern to choose final homes once; do not mechanically split grouped files only to recombine them later.
- **Generated-source lint failures:** change generators and verify regeneration determinism; never hide repository-wide failures with a generated-tree exemption.
- **Large generated API surface reshaping:** choose the facade/nesting strategy explicitly, update imports in one coordinated change, and verify that Scala/JVM/Native symbol usability is preserved.

## Non-goals

- Rewriting PTX, AMDGPU, or SPIR-V parsers or instruction semantics.
- Making every program representation printable.
- Adding automatic program-format detection.
- Exposing backend debug traces over RPC by default.
- Changing native API-visible program, allocation, or completion semantics.
- Sharing loaded mutable program state across sessions.
- Replacing the current wire protocol beyond what is required for open program encoding and codec separation.
- Preserving removed internal interfaces, adapters, wire shapes, or migration paths for out-of-tree consumers.
- Introducing ABI versioning, capability negotiation, compatibility modes, or speculative checks for implementations that do not exist.

## Completion criteria

The migration is complete when:

- all three ISA backends implement the new typed VM boundary;
- `KernelTask` is the VM work description and contains no API program identity;
- server runtime objects exclusively own program/allocation/completion IDs and asynchronous lifecycle;
- `ProgramFormat`, `ProgramDecoder`, `ProgramInstance`, and `ProgramExecutor` are gone;
- all pre-existing and migration-created legacy adapters, aliases, fallback paths, duplicate representations, forwarding-only layers, and obsolete fixtures are gone;
- adding a new encoding requires registration only, not modification of a closed core enumeration;
- VM/ISA domain sources have no RPC/server dependency or wire derivation;
- observer, scheduler, memory, control, and errors have one clear owner each;
- non-ISA layers contain only transport-safety, ownership, allocation-bound, and lifecycle/concurrency checks; ISA semantics are checked once in the decoder, validator, loader, or VM that owns them;
- no additional ABI versioning, schema negotiation, compatibility reader, capability handshake, or speculative verification mechanism remains;
- multiple loads and sessions have isolated mutable program state;
- AMDGPU and SPIR-V have converged on the PTX-style cohesive component shape while retaining their own semantic stages and terminology;
- all handwritten and generated sources satisfy the active lint policy, `codestyleTests/test` passes, and the full `scalafixAll --check` command is green;
- repository-authored identifiers, comments, diagnostics, tests, and documentation outside this plan contain no occurrence of `contract`;
- JVM, Scala Native, native RPC/API integration, and relevant conformance tests pass;
- representative load and execution performance does not regress materially.
