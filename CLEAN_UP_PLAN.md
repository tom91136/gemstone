# ISA and Virtual Machine Cleanup Plan

## Status and scope

This is the frozen plan for the post-migration cleanup. `PLAN.md` remains frozen and `WORK.md` remains the record of the completed architecture migration. Implementation progress, verification, and incidental findings belong in `CLEAN_UP_WORK.md`; do not amend this plan unless the user explicitly reopens it.

The cleanup covers the core ISA/VM model, PTX, AMDGPU, SPIR-V, and their server runtime boundary. Native and conformance code are included only where they contain obsolete in-tree paths; vendor ABI aliases and test-framework integration remain when they represent real external requirements.

The goals are:

- give every important type one clear owner;
- make dependencies point from composition toward stable domain roots and from execution toward ISA-specific VM IR;
- remove parallel execution APIs, duplicate representations, forwarding layers, stale adapters, and migration residue;
- apply one nesting and naming strategy across all ISAs without pretending their semantics are identical;
- retain one execution path per ISA and keep the repository lint-green throughout.

## Decisions settled during review

- Each ISA uses the name `VMIR` for its private execution intermediate representation. The common name describes the same architectural role; the types and semantics remain unrelated across ISA packages.
- PTX's `ISA.lower` and `ISA.compile` methods are removed. Their only consumers are in-tree tests and a benchmark; those callers move to `VirtualMachine` or narrow package-private `VMCompiler` test support.
- AMDGPU's partial packed instruction representation is deleted. `VMIR.Instruction`, initially based on `DecodedInstruction`, becomes the sole runtime instruction representation.
- SPIR-V performance reporting moves to observation. Interpreter overloads do not accept a metrics accumulator.
- `VMCompiler` is the name for the program-to-VMIR stage in all three ISAs, including PTX; `Lowering` does not remain as a parallel architectural name.
- Every temporary rename, forwarding alias, bridge, compatibility path, duplicate representation, and migration-only test fixture is removed with its final consumer. The completed tree contains only the final architecture.

## Architectural vocabulary

Each ISA has a private execution intermediate representation named `VMIR`. It is ISA-specific and is not a new core abstraction.

`VMIR` is the semantic boundary between program decoding/loading and execution:

```text
source or binary
    -> Parser / Decoder
    -> immutable Program or Module
    -> VM lowering / compilation
    -> ISA-specific VMIR
    -> VMInterpreter
    -> VM state, memory, observer, scheduler
```

For AMDGPU code objects the container path is:

```text
ELF image
    -> CodeObject.Decoder
    -> CodeObject.Program
    -> VM loader and code-object index
    -> selected kernel bytes and metadata
    -> VM compiler
    -> AMDGPU VMIR
    -> VMInterpreter
```

`VMIR` should own the instruction form actually consumed by the interpreter, its operands, control-flow addresses, and compiled function/kernel structure. It must not become a common lowest-denominator IR shared by PTX, AMDGPU, and SPIR-V.

## Dependency rules

1. `Program`, `Module`, parsers, decoders, printers, and validators do not depend on VM execution state.
2. VM lowering or compilation may depend on the immutable program model and produce `VMIR`.
3. `VMIR` may depend on stable ISA value types and core binary utilities. It does not depend on the interpreter, mutable VM state, server, RPC, or native API.
4. `VMInterpreter` consumes `VMIR` and coordinates execution. It may depend on VM state, semantics, memory, `TaskControl`, `Scheduler`, and `Observer`.
5. Mutable VM state may refer to `VMIR` values, but `VMIR` never refers to mutable state.
6. A VM-owned loaded program owns per-load indexing, global bindings, and compiled-kernel caches. It does not expose a second execution API.
7. `VirtualMachine` is the sole public execution surface. Internal interpreters and compilers are package-private.
8. Server runtime implementations depend on concrete ISA `VirtualMachine` instances. ISA modules never depend on server or RPC types.
9. Tests use the public program and VM surfaces by default. Narrow package-private tests may exercise VM compilation or semantics where that is the unit genuinely under test.
10. Optimized storage is allowed only when it is the canonical representation or a measured, coherent cache with explicit ownership. A permanent decoded representation plus partial packed representation and fallback is not acceptable.

## Nesting and file strategy

Ownership decides placement; line count only decides whether owned vocabulary remains nested or is extracted.

- Keep public domain roots in matching top-level files: `Program`, `Module`, `Parser`, `Decoder`, `Printer`, `Validator`, `ISA`, and `VirtualMachine`.
- Nest small types that have no useful meaning outside their owner in that owner's companion.
- Extract a substantial owned component to a matching top-level `OwnerThing` file: `VMIR`, `VMInterpreter`, `VMLoadedProgram`, `VMMemory`, `VMLaneMask`, `VMState`, or `VMInvocation`.
- Keep independently reusable ISA concepts top-level: examples include AMDGPU `Target` and `Family`, and SPIR-V `Word` and `SPIRVVersion`.
- Do not create generic top-level implementation names such as `Interpreter`, `Memory`, `Operations`, or `State`.
- Do not use `ThingData` plus an opaque `Thing` when a direct class expresses the same invariant.
- Do not retain a one-field `PreparedThing` whose only purpose is hiding the meaningful loaded or compiled value. Name and own that value directly.

A type should normally be nested when it is small, only meaningful through the owner, and not used as an independent component. It should normally be extracted when it has substantial invariants or behaviour, needs focused tests, or would make the companion difficult to navigate.

## Core VM review

### Cohesive pieces

- `KernelTask` correctly owns arguments and pointer relocations.
- `TaskControl` correctly owns cancellation and instruction budgeting.
- `LaunchGeometry` correctly owns dimensions and coordinates.
- `Scheduler`, `Observer`, `Memory`, `AddressSpace`, `Fault`, and `Result` express independent VM services or values.
- The path-dependent `LoadedProgram` keeps each concrete VM's loaded state type-safe. Server adapters correctly close over it.

### Cleanup

- Remove the associated `VirtualMachine.Event` type. Make core `Event` an extensible observation root whose companion owns the common print and pointer-relocation events. ISA-specific diagnostic or performance events extend that root, so `VirtualMachine` can consistently own `Observer[Event]` without an associated type.
- Keep API identifiers, RPC values, session lifecycle, and asynchronous submission out of the VM model.

Target core dependency:

```text
Program
   ^
   |
VirtualMachine[P]
   +-- owns Observer[Event]
   +-- owns Scheduler
   +-- load(P) -> machine.LoadedProgram
   +-- execute(LoadedProgram, KernelTask, Memory, TaskControl)
```

## PTX coupling and cohesion

### Current map

```text
Parser ----------> Program <---------- Printer / Validator
                       |
                       v
                  ModuleDecoder
                    |   |  \
                    |   |   +--> mutable global bindings and lowering cache
                    |   +------> Lowering
                    +----------> VirtualMachine.execute (older path)

Lowering ---------> executable types nested in VirtualMachine
VMInterpreter ----> executable types + VMState + VMSemantics
VMSemantics ------> executable types + VMExecutionContext + VMStateEffects
VirtualMachine ---> ModuleDecoder.LoadedProgram
```

### Assessment

- `Program`, `Parser`, `Printer`, and `Validator` are cohesive and point in the right direction.
- `Lowering` is a real VM compiler, but its output is presented as miscellaneous `VirtualMachine` companion vocabulary. This obscures the IR boundary.
- `ModuleDecoder` is not a decoder. It owns program preparation, loaded mutable state, global binding, lowering caches, argument preparation, and execution wiring.
- `VirtualMachine` has both the desired instance API and an older static execution API with `ObservationMode`, private tracing, separate error/result types, and test callers.
- `VMInterpreter`, `VMExecutionContext`, `VMState`, `VMRegisterFile`, `VMPredicateFile`, `VMMemory`, `VMStateEffects`, `VMStateWrite`, and `VMSemantics` are mostly cohesive components with sensible dependency direction.
- `LaneMaskData` plus opaque `LaneMask` hides a class behind another name without gaining representation or ownership safety.
- PTX `ISA.lower` and `ISA.compile` expose the execution model through a general ISA facade. That makes the public tooling facade depend on VM internals unlike AMDGPU and SPIR-V.

### Target map

```text
Parser ----------> Program <---------- Printer / Validator
                       |
                       v
                  VMCompiler
                       |
                       v
                      VMIR
                       |
        +--------------+----------------+
        v              v                v
 VMInterpreter   VMSemantics      VMLoadedProgram
        |              |                |
        +-----> VM execution state <----+
                       ^
                       |
                VirtualMachine
```

Proposed ownership:

- `VMIR.scala`: `Instruction`, `Value`, operation families, `Predicate`, `Program`, `Function`, `Kernel`, parameter descriptions, and IR program counters or labels.
- `VMCompiler.scala`: conversion from PTX `Program` statements to `VMIR`.
- `VMLoadedProgram.scala`: prepared entries/functions/globals, per-load global addresses, and compiled-kernel cache.
- `VMLaneMask.scala`: a direct final class and companion; no storage alias layer.
- `VirtualMachine.scala`: the VM instance and only genuinely small VM-owned values.
- `VMInterpreter.scala`: orchestration only; tracing goes through the owned core observer.
- Existing register, predicate, state, memory, effects, and semantics components remain separate unless moving IR types reveals a smaller natural merge.

Remove the static execution entry point, `ObservationMode`, private trace model, duplicate PTX error/result types, and `ModuleDecoder` after their final callers move. Remove PTX `ISA.lower`/`compile`; migrate their test and benchmark consumers to the VM or narrow package-private `VMCompiler` support. If instruction, output-write, or scheduler-decision diagnostics remain useful, express them as PTX VM observation events rather than a returned trace.

## AMDGPU coupling and cohesion

### Current map

```text
CodeObject.Decoder -> CodeObject.Program -> HSACodeObjectDecoder
                                              |
                                              +--> CodeObject.Index / metadata
                                              +--> mutable globals and bindings
                                              +--> DispatchInterpreter.prepare/execute

Binary decoder -------> DispatchInterpreter.decodeProgram
                               |
                               +--> DecodedInstruction
                               +--> packed ExecutableInstruction
                               +--> PreparedKernel holding both
                               +--> WaveState / lane and memory components

VirtualMachine -------> HSACodeObjectDecoder.LoadedProgram
```

### Assessment

- `CodeObject`, its strict decoder, index, metadata, and target selection form a legitimate executable-container domain.
- `HSACodeObjectDecoder` is misnamed and over-coupled. The real initial decoder is `CodeObject.Decoder`; this object performs loading, lazy indexing, symbol selection, binding, caching, and execution.
- `DispatchInterpreter` combines instruction decoding, VM compilation, argument packing, dispatch orchestration, wave creation, instruction semantics, memory helpers, and tracing in one approximately 7,700-line object.
- `DecodedInstruction` is the actual semantic VM IR.
- The opaque `Long` used for packed instructions is a reasonable representation technique by itself. The problem is that it covers only a few operations, while `PreparedKernel` permanently stores both instruction arrays and uses a fallback between them.
- `WaveState`, `VectorRegisters`, `ScratchMemory`, and `LaneBits` are cohesive VM components but their generic top-level names hide ownership.
- `LaneCount`, `LaneIndex`, `WaveSize`, and `WaveBoundary` are small VM-only vocabulary currently spread across tiny files.

### Target map

```text
CodeObject.Decoder -> CodeObject.Program -> VMLoader -> VMLoadedProgram
                                           |               |
                                           v               |
                                      CodeObject.Index      |
                                           |               |
                                           v               |
                                      VMCompiler ----------+
                                           |
                                           v
                                          VMIR
                                           |
                                           v
                                      VMInterpreter
                                           |
             +-----------------------------+---------------------------+
             v                             v                           v
        VMWaveState                 VM memory helpers          core VM services
```

Proposed ownership:

- `VMIR.scala`: canonical `Instruction` (currently `DecodedInstruction`), sources, branch conditions, sub-dword selections, and compiled instruction layout/index information.
- `VMCompiler.scala`: decode selected AMDGPU machine code and produce `VMIR.Program` or `VMIR.Kernel`.
- `VMLoader.scala`: code-object indexing, alias resolution, kernel selection, and creation of `VMLoadedProgram`.
- `VMLoadedProgram.scala`: per-load global state, bindings, symbol information, and compiled-kernel cache.
- `VMInterpreter.scala`: task/argument preparation and execution orchestration.
- `VMWaveState`, `VMVectorRegisters`, `VMScratchMemory`, and `VMLaneBits`: substantial execution components.
- `LaneCount`, `LaneIndex`, and `WaveSize` nest in the `VirtualMachine` companion. `WaveBoundary` nests in `VMInterpreter`, which owns wave orchestration.

The initial cleanup should make the enum-like `VMIR.Instruction` the single canonical instruction representation and delete the partial packed array. A packed form should return only if profiling justifies it and it can replace the canonical runtime representation coherently rather than shadowing it with fallback.

The large interpreter should then be split by responsibility, not by arbitrary line count. Instruction-family semantics may remain grouped where they share state and helpers, but decoding, compilation, loaded-program lifecycle, and global binding must leave the interpreter.

## SPIR-V coupling and cohesion

### Current map

```text
Binary <-> RawModule -> ISA.decode -> Module -> Validation
                                      |
                                      v
                                  Interpreter
                                      |
           +--------------------------+--------------------------+
           |             |             |            |            |
      DispatchProgram RuntimeType  MemoryState  Invocation  Collectives
           |
      PreparedProgram one-field wrapper

VirtualMachine -----------------------> Interpreter.prepare/run
```

### Assessment

- Binary framing, raw instructions, generated typed instructions, `Module`, and validation are distinct coherent stages.
- Several very small binary-domain values are over-fragmented into top-level files. This is mostly navigation noise, not a dependency violation; they should be nested only where ownership is unambiguous.
- The approximately 10,000-line `Interpreter` over-nests almost the entire execution domain in one object.
- `DispatchProgram` is the compiled execution model and therefore the beginning of SPIR-V's VM IR, even though individual executable operations still use the generated `Instruction` enum.
- `PreparedProgram` is a one-field wrapper around `DispatchProgram` rather than the meaningful VM-owned value.
- Public `prepareUnchecked`, numerous `run` overloads, and `runUnchecked` create a second execution surface used heavily by tests.
- Top-level `Memory` and `Operations` are VM implementation components with overly generic names.
- `Performance` contains useful measurements, but it is wired through interpreter overloads rather than observation.

### Target map

```text
Binary <-> raw binary model -> ISA.decode -> Module -> Validation
                                               |
                                               v
                                          VMCompiler
                                               |
                                               v
                                              VMIR
                                               |
                                               v
                                         VMInterpreter
                                               |
                 +-----------------------------+-------------------+
                 v                             v                   v
              VMMemory                  VMInvocation       VMCollectives
                                               |
                                        core VM services
```

Proposed ownership:

- `VMIR.scala`: compiled program, runtime types, function/global information, resolved decorations and layouts, and the instruction stream used for execution. It may retain generated `Instruction` values when no semantic lowering is useful.
- `VMCompiler.scala`: validation-adjacent compilation from `Module` into `VMIR.Program`.
- `VMLoadedProgram.scala`: the immutable compiled `VMIR.Program` plus per-load layout caches. The caches stay out of `VMIR`, preserving the IR as immutable data and making the loaded value more than a hiding wrapper.
- `VMInterpreter.scala`: dispatch and instruction-loop orchestration.
- `VMMemory.scala`: regions, pointers, internal memory, buffered external writes, and relocation observation.
- `VMInvocation.scala`: invocation values, call frames, and per-invocation execution state.
- `VMCollectives.scala`: collective requests, payloads, and workgroup coordination.
- Small arithmetic operation enums should live with VM IR or semantics, not in generic `Operations.scala`.
- A small execution-local counter set gathers the existing SPIR-V measurements and emits one `VMObservation.Performance` event at task completion. A metrics observer may retain or aggregate snapshots outside the interpreter. The normal VM method signature remains unchanged.

Remove the one-field prepared wrapper, metrics overloads, and public run overload family, then collapse public execution onto `VirtualMachine`. Preserve unchecked compilation only as a narrow package-private test utility if malformed low-level fixtures genuinely require it.

## Server boundary cleanup

### Current map

```text
ProgramEncodings ----> every concrete backend encoding
BackendCatalog ------> RuntimeBackend -> BackendSession -> RuntimeProgram
                                                        -> three optional global facets
RuntimeProgram ------> concrete VM through backend-owned wrappers
```

### Cleanup

- Delete the central `ProgramEncodings` list. Each backend owns its stable open `ProgramEncoding`; the product composition root registers available backends without defining their identities.
- Keep `BackendCatalog` as the open lookup assembled by `GemstoneBackendCatalog`.
- Consolidate `RuntimeGlobals`, `RuntimeGlobalBinder`, and `RuntimeGlobalWriter` into one optional `RuntimeProgram.Globals` capability with `image`, `bind`, and `write` operations. SPIR-V has no globals capability; PTX reports unsupported writes; AMDGPU implements all three operations.
- Retain the runtime adapter because it is the real type-erasure and error-translation boundary between server lifecycle and a typed concrete VM.
- Do not move server lifecycle, `ProgramId`, completion tracking, or asynchronous submission into `VirtualMachine`.

## Non-architectural residue

- Remove obsolete CUDA environment-variable spellings once all in-tree callers use the current name.
- Preserve CUDA, HIP, HSA, OpenCL, and Level Zero aliases that are required by their external ABIs.
- Preserve AMDGPU code-object symbol aliases that model actual ROCm/ELF behaviour.
- Preserve conformance adapters that connect genuinely different external test frameworks.
- Remove debug branches and workload-name-specific tracing from VM semantics unless they remain an intentionally supported diagnostic facility with one clear owner.
- Delete empty source directories as files move.

## Implementation sequence

### Phase 1: establish VM IR ownership

- Introduce the private `VMIR` root in each ISA without changing execution semantics.
- Introduce `VMCompiler` as the sole name for each program-to-VMIR stage.
- Move direct compiler tests behind narrow package-private fixtures in preparation for removing public alternate execution paths.
- Make core `Event` extensible for ISA-specific observations and remove the unused VM-associated event type.

Exit criterion: the core observation shape and all three VM IR ownership boundaries compile on JVM and Native, with no alternate architectural names.

### Phase 2: server boundary simplification

- Remove `ProgramEncodings` and let each backend own its encoding.
- Consolidate runtime global capabilities.
- Compile and test core/server on JVM and Native.

### Phase 3: PTX reference cleanup

- Introduce PTX `VMIR` and move executable vocabulary to it by hand.
- Replace `ModuleDecoder` with VM-loaded-state and `VMCompiler` ownership.
- Replace the lane-mask alias layer with `VMLaneMask`.
- Remove `ISA.lower`/`compile` and the static execution/trace path; move tests and benchmarks to the VM or narrow package-private compiler/semantics units.
- Verify PTX JVM/Native tests and lint before using the result as the reference layout.

### Phase 4: AMDGPU decomposition

- Introduce AMDGPU `VMIR` using `DecodedInstruction` as the canonical starting point.
- Delete the partial packed/fallback representation.
- Separate VM compiler, loader, loaded state, interpreter, and wave components.
- Rename remaining VM-owned components and nest small wave vocabulary.
- Move tests away from direct `DispatchInterpreter` entry points.
- Verify AMDGPU JVM/Native tests, code-object suites, performance-sensitive benchmarks, and lint.

### Phase 5: SPIR-V decomposition

- Introduce SPIR-V `VMIR` around the compiled program and runtime type/function metadata.
- Split memory, invocation, collective, and interpreter responsibilities.
- Move performance snapshots to ISA-specific observations and remove metrics parameters and overloads.
- Remove the prepared wrapper and public run-overload family.
- Review binary-domain tiny files for genuine companion ownership without flattening distinct decode stages.
- Verify SPIR-V JVM/Native tests, precision/layout suites, and lint.

### Phase 6: residue and integration audit

- Search for removed names, duplicate execution entry points, fallback paths, wrapper-only types, stale adapters, generic implementation names, and workload-specific debug branches.
- Confirm dependency direction through imports and public signatures.
- Remove every migration-only alias, temporary bridge, forwarding wrapper, duplicate old/new conversion, compatibility path, and obsolete test fixture introduced or exposed by this work.
- Remove empty directories.
- Run the full Scala/JVM/Native test and lint matrix.
- Run compact emulation and hardware conformance only after the structural suite is green.

## Acceptance criteria

- Each ISA has one immutable decoded program model, one ISA-specific VM IR, one VM-owned loaded-program model, and one public execution surface.
- No server, RPC, or native API type leaks into an ISA or VM model.
- No VM IR depends on mutable VM execution state.
- ISA-specific diagnostics and performance data flow through observation without widening `VirtualMachine.execute`.
- No interpreter performs parsing, external container decoding, or server lifecycle work.
- No permanent decoded/packed dual instruction representation or fallback remains.
- No `ThingData`/opaque-class layering or wrapper-only prepared type remains.
- Top-level names reveal ownership; companions contain only genuinely owned small vocabulary.
- PTX, AMDGPU, and SPIR-V follow the same structural rules while retaining their different semantics.
- No migration marker, temporary alias, bridge, compatibility path, duplicate conversion, or forwarding-only wrapper remains.
- All touched generated code is changed through its generator.
- The full lint and JVM/Native test matrix is green, followed by compact emulation and hardware conformance.
