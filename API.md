# Library API

Gemstone should work as a library as well as a server. Users take one ISA and either compose a functionally correct
device from its public parts, or use its assembler, disassembler and instruction model as they would Capstone or
Keystone: decode, patch, re-encode, run, single-step, inspect architectural state and checkpoint.

This document records the current public surface and the agreed direction.

## Success criterion

The server uses only the public API of core and every ISA module, as any downstream user sees it. It works correctly,
with no performance penalty and no workarounds caused by visibility.

The `publicapi` compiler plugin (`codestyle/publicapi`) enforces this for the server's main sources: a reference that
reaches a private or qualified-private declaration only through package nesting fails to compile.

## Principles

- Keep each ISA's own model. Do not force AMDGPU, PTX, SASS and SPIR-V into a common shape that serves no purpose.
- Standardise the basics through traits, and marker traits where that tells users which component they hold: codec,
  container, backend, loaded program, architectural state.
- Separate at most into `<isa>-asm` (instruction model, codec, container reading and writing) and `<isa>-vm`
  (execution), for example `amdgpu-asm` and `amdgpu-vm`. No finer split.
- Relax `NoPackagePrivate` where an internal tier is genuinely needed, rather than leaving every cross-file
  internal public.

## Current surface

| | SPIR-V | PTX | SASS | AMDGPU |
| --- | --- | --- | --- | --- |
| Decode | `ISA.decode` to `Module`; `asm.Printer` | `Parser` to `Program`; `asm.Printer` | `asm.Disassembler(target)`; `InstructionDecoder` | `asm.Disassembler(target, wave)`; `Binary.decodeStatementAt` |
| Encode | `asm.Parser`, `ISA.encode` | text only; no `encode` | `asm.Assembler(target)` | `asm.Assembler.encode` |
| Round trip | byte-exact; ids numeric, names lost | canonical text, equal AST | byte-exact sections | byte-exact; 4154 LLVM MC cases |
| Rewriting | case-class `copy`; no id allocator, `LiteralString` private, no operand builders | immutable AST; only `Operand.mapChildren` for traversal | `DecodedInstruction.rewrite` drops machine fields and scheduling; Volta+ control bits untyped | private `Instruction` constructor; rebuild through `Instruction.decode` |
| Container | n/a | n/a | `Cubin.decode`; `patchKernel` and `replaceKernel` | `CodeObject.decode`, relocations pre-applied; `patchKernel` and `replaceKernel` |
| Execution | `SPIRVBackend.load`, `execute` | `PTXBackend.load`, `execute` | `CubinBackend.load`, `execute` | `AMDGPUBackend.load`, `execute` |
| Step, trace, state | `InvocationView` | `CohortView` | `CohortView` | `WaveView` |
| Checkpoint | `ReplayDevice` | `ReplayDevice` | `ReplayDevice` | `ReplayDevice` |

### Core

- `vm.Backend.load(bytes)` returns a `Backend.Program`; `execute(KernelTask, AddressSpace, TaskControl, handler)`
  runs a kernel in-process without the server. Optional program features return `Fault.Unsupported` at run time.
- `vm.Device` is the embedder entry point: a `BackendRegistry`, one `AddressSpace`, and synchronous launches that
  return the raised events under a fresh `TaskControl` per launch. Programs stay owned by the caller.
- Backends name their image format with the open `ProgramFormat`; the six Gemstone formats are constants on it, and a
  backend outside Gemstone mints its own. `BackendRegistry` holds one backend per format.
- `ExecutionSession` in `core/rpc` remains the receiver for the native facades. Its types follow the wire protocol
  (`ExecutionFailure`, `CompletionId`), and the wire's closed `rpc.ProgramEncoding` maps onto `ProgramFormat`, so only
  the six Gemstone formats are reachable over RPC.
- `AddressSpace` is an open trait: `read`, `write`, `address` and `locate` are required; atomics, deferred reads and
  host fences are optional. `vm.ArrayMemory` is the reference implementation, one byte array per allocation behind one
  lock.
- `TaskControl` provides cancellation and an instruction budget. Budget charging differs by backend: per block
  (NVIDIA VM), per batch (SPIR-V), per instruction (AMDGPU).
- `Event` has only `Print` and `PointerRelocation`; PTX, SASS and AMDGPU ignore the handler.
- `Fault` carries an optional `Fault.Location`: kernel, function, the ISA's own instruction address, workgroup, and
  the lane when one lane alone faulted. Equality and extraction ignore it; `description` renders it, and the RPC
  failure messages carry that text.
- A `LaunchObserver` on `TaskControl` sees every instruction before it executes, with an `ArchitecturalState` view
  in the ISA's terms: SPIR-V `InvocationView` (values by result id), NVIDIA `CohortView` (lowered register and
  predicate slots per lane), AMDGPU `WaveView` (EXEC, SGPRs, VGPRs). `Device` also reports device memory accesses
  to it. Blocking the callback under the sequential scheduler single-steps the launch; returning a fault stops it.
  Unobserved launches cost SPIR-V nothing and AMDGPU and NVIDIA one null check per instruction or cohort step.
- Refinement types (`Int > 0`, `NonEmpty`) and the `refine`/`unsafe` macros appear in nearly every signature. They
  suit Scala 3 users and exclude Java, Kotlin and C callers.

### ISA internals

- SPIR-V: `VMProgram`, `VMInterpreter`, `InvocationState`, `Op` and lowering are private.
- PTX and SASS: lowering to the shared NVIDIA `VMIR` is private, and `VMIR` and `VMInterpreter` are `private[isa]`.
  The NVIDIA scheduler (`Execution`) and lane state are not reachable from outside.
- AMDGPU: `Wave`, register files, `VMIR` and `VMCompiler` are private. Execution decodes with `InstructionDecoder`
  while the assembler uses `InstructionCodec`, so encodable and executable can differ silently.
- No ISA exposes serialisable execution state. NVIDIA state views use lowered slots rather than PTX or SASS register
  names.

### Packaging and tooling

- Module graph: each ISA has an `<isa>-asm` module (instruction model, codecs, assembly text, containers, validation)
  on core alone, and an `<isa>-vm` module (lowering, execution, backend) on top of it. `ptx-vm` and `sass-vm` also
  need the shared NVIDIA VM in `nvidia`. No asm module depends on a VM.
- Core, `nvidia` and the eight ISA modules publish with licence, SCM and developer metadata, sources and Scaladoc;
  their POMs list no test or unpublished dependencies. Nothing has been released: there is no `publishTo` or
  credentials, and the version is a snapshot. MiMa checks binary compatibility in `lint` once
  `Publishing.previousRelease` names a release; TASTy compatibility of inline APIs is unchecked.
- The assemble and disassemble facade, including ELF and cubin section extraction, lives in the unpublished,
  JVM-only `isaTools`.
- Scala Native exports no C ABI, and the native RPC client is static, hidden and not installed.
- Record and replay compares wire responses for regressions. It is not deterministic and is not a checkpoint.
- `samples`, in a package outside Gemstone's so the compiler admits only public API, assembles a kernel for each ISA,
  runs it on a `Device`, disassembles it, changes its constant and adds an instruction, through `Program.mapInstructions`
  and `mapBodies` (PTX), `Module.freshIds` (SPIR-V), `Assembler.relaid` (SASS) and `CodeObject.replaceKernel`
  (AMDGPU). SASS assembles nvdisasm's descriptor-free memory syntax, and AMDGPU packs listed arguments at their
  metadata offsets with the hidden arguments the dispatch implies.

## Direction

1. Relax `NoPackagePrivate` where an internal tier is useful. Keep codecs, containers, backends, programs and the
   execution facade public. `NoPackagePrivate` now admits `private[gemstone]` and `private[isa]`, the packages that
   span modules, and the shared NVIDIA VM uses them. Core helpers used only by ISA modules (`binary`, `text`,
   `vm.Dispatch`, `vm.KernelArguments`) remain public until the backend registry settles what a third-party backend
   needs.
2. Split each ISA into `<isa>-asm` and `<isa>-vm` where the separation is real; go no further. Done for all four
   ISAs; asm modules depend on core alone.
3. Add a reference host memory (`AddressSpace`), a small device facade for embedders in place of the RPC-shaped
   `ExecutionSession`, and an open backend registry instead of the closed `ProgramEncoding` enum. Done: `ArrayMemory`,
   `Device`, `ProgramFormat` and `BackendRegistry`.
4. Add a per-launch execution observer (instruction, memory access, barrier), a read-only architectural-state view
   per ISA in that ISA's own terms, single-stepping under the sequential scheduler, and structured fault locations.
   The fast path must not change when no observer is attached. Done except barrier events and source register names
   for the NVIDIA view.
5. Checkpoint and restore lane, wave and work-group state plus memory under the sequential scheduler; parallel
   work-groups make whole-dispatch snapshots non-deterministic. Done by deterministic replay: `ReplayDevice` loads
   each launch's program afresh from its image and snapshots `ArrayMemory` as the launch begins, so every observed
   instruction carries a free `Checkpoint`. `resume` restores the snapshot and replays unobserved up to the
   checkpoint. Restoring costs time proportional to the checkpoint's depth, and requires backends that run workgroups
   sequentially.
6. Write containers: same-size section patching for cubin and HSACO first, then full re-linking with relocations,
   symbols and `.nv.info`. Done: `elf.Image.patched` and `resized` rewrite sections, segments and tables;
   `Cubin.replaceKernel` moves later sections and updates the kernel symbol, `.debug_frame` range, text relocations
   and the exit and CTA-ID offsets in `.nv.info`, rejecting other offset attributes; `CodeObject.replaceKernel` grows
   a kernel in place or moves it to the end of `.text` with its symbols and descriptor entry offset, never moving
   other addresses.
7. Publish with metadata and binary-compatibility checks, and document the API. Done short of a release: the README
   documents the library and `samples` exercises it; releasing needs a repository, credentials and a first version.
