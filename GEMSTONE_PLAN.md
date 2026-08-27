# GEMSTONE implementation plan

## 1. Objective

GEMSTONE is a functional, vendor-neutral GPU execution sandbox for GPUless CI and kernel debugging.

The project will execute code compiled through the normal CUDA, HIP, and SYCL/oneDPL toolchains without using a physical GPU. It will prioritize:

- Functional correctness over timing or cycle accuracy.
- Deterministic, reproducible execution.
- Checked memory, synchronization diagnostics, and detailed state tracing.
- Compatibility with real vendor host APIs and device-code containers.
- Successful execution of representative CCCL Thrust, rocThrust, and oneDPL algorithms.
- A rapid JVM development cycle while producing native vendor-compatible DSOs with Scala Native.

The name expands to **GPU Execution Model for State-Tracked, Offload-Neutral Emulation**. The working technical namespace and command name are `gemstone`.

## 2. Decisions

The initial architectural decisions are:

1. Use Scala 3 for the implementation.
2. Cross-build the execution model, parsers, runtime, scheduler, and debugger for JVM and Scala Native.
3. Implement vendor DSO exports in Scala Native using its C FFI and dynamic-library support.
4. Permit small embedded C sources where an ABI or operating-system facility cannot be expressed reliably in Scala Native. Such C code must remain generated or mechanical and contain no execution semantics.
5. Keep pointer-level FFI entry points Native-only, but put the logical API handlers behind shared Scala interfaces so they can be tested on the JVM.
6. Begin with interpretation and predecoded instruction tables. Do not require LLVM, native code generation, or JIT compilation.
7. Maintain separate PTX, AMDGPU, and SPIR-V execution engines over a shared runtime, memory, scheduling, and tracing substrate.
8. Keep the patched GPU Ocelot tree as a donor, compatibility oracle, and source of tests. Do not make its legacy architecture the new core.
9. Pin explicit compiler and library profiles instead of claiming support for every version of each evolving ISA and API.

## 3. Scope

### Initial targets

- CUDA runtime and driver APIs sufficient for current pinned CCCL Thrust tests.
- PTX emitted by a pinned CUDA toolkit and selected virtual compute architecture.
- HIP runtime APIs sufficient for pinned rocThrust/rocPRIM tests.
- AMDGPU code objects for one initial RDNA2 target, preferably `gfx10-3-generic`/`gfx1036`, followed by CDNA2 where useful.
- Level Zero APIs sufficient for a pinned DPC++/oneDPL toolchain.
- SPIR-V modules emitted for that Level Zero/oneDPL profile.

### Explicit non-goals for the first release

- Cycle-accurate simulation or performance prediction.
- Graphics APIs or graphics pipelines.
- CUDA SASS/cubin interpretation.
- Every CUDA, HIP, or Level Zero API.
- Dynamic parallelism, tensor instructions, and advanced graph APIs unless the selected library corpus requires them.
- Transparent access to every arbitrary system `malloc` allocation as HMM/USM.
- Native-code JIT compilation.
- Hardware-like performance.

## 4. Source and module layout

GEMSTONE should initially live beside Ocelot so that both can be built and tested independently:

```text
gemstone/
  build.sbt
  project/
  api/
  codegen/
  core/
  isa_ptx/
  isa_amdgpu/
  server/
  native/
    dispatch/
  conformance/
  corpus/
  docs/
```

The project boundaries follow actual compilation and deployment constraints:

```text
native -> isa_ptx -> core -> api
       -> isa_amdgpu -> core
server -> isa_ptx -> core -> api
       -> isa_amdgpu -> core
codegen -> api
```

Runtime state, checked memory, scheduling, tracing, vendor handlers, and the small CLI live together in `core`.
PTX parsing and execution live in `isa_ptx`; AMDGPU code-object parsing, generated ISA data, decoding, and execution
live in `isa_amdgpu`. Shared engine contracts remain in `core`, rather than creating an `isa_common` project. RPC and
ISA source generators live in the JVM-only `codegen` project.

Pinned external ISA inputs are acquired explicitly with `codegen / prepareISAReferences`; normal builds remain
offline. The task verifies hashes, caches archives outside the repository, and selectively prepares LLVM AMDGPU MC
tests, Mesa ACO sources, AMD's official machine-readable ISA XML, and NVIDIA's archived PTX documentation beneath the
codegen target directory. Generated decoders and tests will consume this prepared view rather than system toolchain
sources or floating upstream URLs.

Shared modules must not expose `scala.scalanative.unsafe.Ptr` or other Native-only types. Guest pointers are represented as unsigned 64-bit address values plus allocation metadata.

## 5. JVM and Scala Native split

### Shared JVM/Native code

The following should compile from the same Scala sources on both platforms:

- Canonical runtime object model.
- Device, context, stream, event, module, kernel, and launch state.
- PTX lexer, parser, validator, normalizer, and interpreter.
- AMDGPU decoded instruction model and wave interpreter.
- SPIR-V decoder and subgroup interpreter.
- Deterministic and parallel scheduler logic.
- Abstract checked-memory implementation.
- Barrier, collective, atomic, and memory-order models.
- Trace events, provenance, poison state, diagnostics, and replay.
- Logical CUDA, HIP, and Level Zero API handlers.
- Unit, property, scheduler, parser, and semantic tests.

### JVM implementation

The JVM backend exists for rapid development, testing, fuzzing, and ordinary debugger use. It will provide:

- Array- or `ByteBuffer`-backed guest memory.
- Pure-Scala fake host allocations and handles.
- Deterministic scheduler tests.
- Fast parser and instruction tests.
- Trace replay and analysis tools.
- Hardware-oracle result comparison.

It does not need to produce vendor-compatible DSOs or preserve real host pointer values.

### Scala Native implementation

The Native backend adds:

- Exported vendor ABI functions.
- Native structure layout and pointer conversion.
- ELF symbol visibility, aliases, versions, and library lifecycle.
- Real host-pointer and guest-address association.
- `mmap`, dual mappings, `memfd`, protection, and guard pages.
- `mprotect`/signal handling and optional `userfaultfd` support.
- Dynamic loading and fatbinary/code-object registration.
- Native conformance programs compiled with CUDA, HIP, and SYCL toolchains.

The same shared test suites must run on Scala Native in CI. JVM success alone is not sufficient because primitive layout, floating-point behavior, concurrency, and FFI boundaries can differ.

## 6. Vendor DSO strategy

The preferred experiment is one Scala Native-produced union DSO containing the complete set of CUDA, HIP, and Level Zero entry points. It can be installed under vendor-facing names using links or loader configuration so that only one Scala Native runtime and one GEMSTONE device state exist in a process.

This must be validated against:

- Applications loading both `libcudart` and `libcuda`.
- Required SONAMEs and ELF symbol-version nodes.
- `dlopen`, `dlsym`, constructors, destructors, and repeated initialization.
- Calls arriving concurrently from foreign application threads.
- Callbacks from GEMSTONE into host code.
- `fork` behavior where relevant.

If a union DSO cannot satisfy loader or SONAME behavior, use tiny vendor-specific C facade DSOs forwarding into one Scala Native `libgemstone_core.so`. This is the fallback, not the initial design. Facades must be generated from the API registry and contain no runtime policy.

Every exported function must:

1. Validate raw pointers and structure versions at the boundary.
2. Convert ABI values into canonical shared Scala values.
3. Catch every Scala exception before returning across the C ABI.
4. Store a structured diagnostic and return the correct vendor error code.
5. Avoid invoking managed Scala code from an asynchronous signal handler.

## 7. Canonical runtime

CUDA, HIP, and Level Zero frontends translate into one internal runtime model:

```text
Device
Context
Stream
Event
Allocation
Module
Kernel
Launch
Command
```

The frontends share implementation where the semantics match, but must preserve vendor-specific behavior for:

- Error values and thread-local last-error state.
- Structure layouts and versioning.
- Default-stream behavior.
- Fatbinary and code-object registration.
- Function and global-variable registration.
- Device attributes and capability queries.
- Stream/event ordering.
- Allocation kinds and pointer attributes.

API similarity must not be implemented as blind CUDA/HIP prefix replacement.

## 8. Memory and USM/UVM

The shared core uses an allocation table and checked guest addresses. Device instruction handlers must never dereference guest pointers directly.

The Native memory backend should use shared backing with distinct views where possible:

```text
application view: returned vendor pointer; protectable
engine view:      internal read/write alias of the same backing
guest address:    numeric application pointer value
```

Initial allocation behavior:

- Device memory: inaccessible application view plus checked engine backing.
- CUDA/HIP managed memory: host-readable shared backing, protectable while exclusive device work is active.
- Level Zero shared USM: shared host/engine backing.
- Level Zero device USM: inaccessible application view plus engine backing.
- Host/pinned memory: host allocation registered with exact bounds and lifetime.

`mprotect` and signal handling are enforcement and diagnostic mechanisms, not the primary storage model. Signal handlers may only perform async-safe classification and notification. Optional `userfaultfd` support can move fault processing into an ordinary monitor thread.

Prefetch and memory-advice APIs may initially validate, order, and record metadata without migrating bytes.

## 9. Execution engines

### PTX

The PTX engine uses per-lane registers, predicates, and program counters. Lanes at the same PC execute as a cohort. Divergence splits cohorts naturally; equal PCs permit merging.

Warp and block collectives use explicit rendezvous state keyed by instruction location, scope, phase, and participant mask. This supports diagnostics for missing lanes and mismatched masks.

### AMDGPU

The AMDGPU engine uses one PC per wave, SGPR/VGPR state, SCC/VCC, and the explicit `EXEC` mask. It shares memory and scheduling infrastructure with PTX but not PTX control-flow machinery.

### SPIR-V

The SPIR-V engine uses structured control flow, workgroups, subgroups, storage classes, and the selected memory model. Initial coverage is driven by SPIR-V emitted by the pinned DPC++/oneDPL compiler profile.

### Scheduling

Do not allocate an operating-system thread per GPU lane.

Provide two modes:

- Deterministic debug mode: stable workgroup, wave, and lane ordering with replayable decisions.
- Parallel mode: resumable workgroups executed on a bounded worker pool with fair instruction quanta.

Fair rescheduling is required to prevent CPU worker starvation when device algorithms use global polling or decoupled-lookback protocols.

## 10. Parsing and generated specifications

### Build-time JVM generator

A JVM-only Scala generator will consume external specifications and emit compact tables plus Scala identifiers.

For AMD XML it will generate:

- Encoding masks and values.
- Decode decision tables.
- Operand extraction tables.
- Instruction lengths and extension-word rules.
- Register/data-format metadata.
- Branch and termination metadata.
- Architecture and schema-version manifests.

For SPIR-V it will consume Khronos grammar JSON and generate opcode and operand tables.

For PTX it will consume a maintained GEMSTONE instruction-form schema. NVIDIA documentation may be scraped to report additions or changes, but scraped documentation is not an authoritative build input.

### PTX frontend

Use one shared handwritten lexer and structural parser followed by generated validation and normalization:

```text
PTX text -> raw module/instructions -> form validation -> normalized IR
```

Do not generate or maintain a separate parser for every opcode.

### Semantic overlay

Machine-readable specifications do not provide executable semantics. A reviewed semantic overlay maps decoded instructions to shared semantic families. Generation must emit a coverage report and fail when a required corpus instruction lacks a mapping.

## 11. Debugging model

Debugging is a primary product surface, not an optional callback layer.

Required capabilities include:

- Deterministic record and replay.
- Kernel, grid, block, warp/wave, and lane filtering.
- Source mapping from PTX `.file`/`.loc` and available code-object metadata.
- Per-instruction register, predicate, `EXEC`, and memory diffs.
- Allocation provenance and exact bounds checks.
- Poison/uninitialized register and memory tracking.
- Use-after-free quarantine.
- Barrier and collective deadlock reports.
- Configurable instruction/time budgets and no-progress detection.
- Optional race detection and schedule exploration.
- Output last-writer tracking and backward value provenance.
- Structured trace files plus human-readable rendering.
- Hardware-oracle comparison with exact or instruction-appropriate floating-point tolerances.

The default debug scheduler must remain deterministic. Parallel execution is an explicitly selected mode.

## 12. Testing strategy

### Shared tests on JVM and Native

- Lexer/parser positive and negative tests.
- Generated decoder-table tests.
- Individual instruction semantics.
- Arithmetic edge cases and floating-point modes.
- Divergence and reconvergence.
- Warp/wave collectives.
- Barriers, reductions, atomics, and fences.
- Scheduler fairness and deadlock reporting.
- Memory bounds, poison, races, and provenance.
- Trace serialization and deterministic replay.
- Canonical vendor API handler behavior.

### Differential oracles

- PTX parser acceptance against `ptxas` for the pinned toolchain.
- PTX execution against NVIDIA hardware.
- AMD decoding against AMD ISADecoder and LLVM disassembly.
- AMD execution against local RDNA2/CDNA hardware where available.
- SPIR-V validation against Khronos tools and execution against Intel hardware.
- Ocelot differential tests for its supported PTX subset.

### Native-only ABI tests

- Compile and link small C/C++ programs against each generated DSO.
- Inspect exported symbols, symbol versions, visibility, and SONAME behavior.
- Exercise registration callbacks emitted by the real compilers.
- Call APIs concurrently from foreign threads.
- Exercise initialization, shutdown, `dlopen`, and error paths.
- Run under a container with physical GPU devices hidden.
- Verify that invalid kernels cannot crash or hang the controlling test process.

### Library conformance matrix

Track algorithms rather than a single pass/fail label:

```text
Thrust:    transform, fill, copy, reduce, scan, sort, unique
rocThrust: transform, fill, copy, reduce, scan, sort, unique
oneDPL:    transform, reduce, scan, sort and representative USM policies
```

Test empty, one-element, small, partial-warp/wave, multi-workgroup, and moderate input sizes.

## 13. Implementation phases

### Phase 0: Scala Native feasibility spike

Before committing the complete implementation:

- Build a Scala Native dynamic library exporting a minimal CUDA-shaped API.
- Load it from an ordinary C++ executable.
- Test exact exported names, symbol visibility, and version-script support.
- Test concurrent entry from foreign threads and thread-local errors.
- Test exception containment at every export.
- Test a callback into host code.
- Create and protect a dual-mapped allocation.
- Confirm that a native fault can be diagnosed without entering Scala from the signal handler.
- Determine whether the union-DSO approach provides one runtime when loaded through multiple vendor names.
- Prove the same core operation with JVM and Native tests.

Exit criterion: the all-Scala DSO path is demonstrated, or the exact minimal C facade requirement is documented and accepted.

### Phase 1: Cross-platform core skeleton

- Create the sbt cross-project structure.
- Establish shared JVM/Native tests and CI.
- Define handles, errors, runtime objects, memory interfaces, scheduler interfaces, and trace events.
- Implement deterministic command and workgroup scheduling.
- Add versioned support-profile manifests.

Exit criterion: identical deterministic runtime tests pass on JVM and Native.

### Phase 2: CUDA/PTX vertical slice

- Implement CUDA fatbinary and function registration for a pinned compiler.
- Implement allocations, copies, streams, events, modules, launch, and synchronization.
- Implement the PTX lexer/parser and initial normalized IR.
- Execute vector addition through the replacement vendor DSO.
- Add checked memory and a useful instruction trace.

Exit criterion: an unmodified host binary compiled by the pinned CUDA toolchain executes vector addition without a GPU.

### Phase 3: CCCL Thrust baseline

- Add predicates, divergence, calls, address spaces, conversions, and required math.
- Implement warp collectives, block barriers, atomics, and fences.
- Add temporary allocation and relevant stream behavior.
- Implement fair resumable scheduling for polling algorithms.
- Progress through transform, reduce, scan, and sort.

Exit criterion: the pinned Thrust algorithm matrix passes representative small and moderate cases.

### Phase 4: Debugging hardening

- Add poison, last-writer, allocation provenance, snapshots, replay, deadlock reports, and watchdogs.
- Add optional worker-process isolation.
- Add hardware differential reports and floating-point mismatch classification.

Exit criterion: deliberately broken memory, synchronization, infinite-loop, race, and wrong-result kernels produce actionable deterministic reports.

### Phase 5: HIP/AMDGPU vertical slice

- Generate the RDNA2 decoder from AMD XML.
- Load HIP code objects and kernel metadata.
- Implement HIP registration and launch paths over the canonical runtime.
- Implement the initial wave32 instruction corpus.
- Progress through the rocThrust algorithm matrix.

Exit criterion: the pinned rocThrust algorithms pass without an AMD GPU.

### Phase 6: Level Zero/SPIR-V vertical slice

- Generate SPIR-V opcode tables from the Khronos grammar.
- Implement the Level Zero API subset required by the selected SYCL runtime.
- Implement host, device, and shared USM.
- Implement the SPIR-V workgroup/subgroup corpus emitted by the pinned compiler.
- Progress through the oneDPL algorithm matrix.

Exit criterion: pinned oneDPL algorithms using USM pass without an Intel GPU.

### Phase 7: Compatibility and performance

- Expand API and instruction coverage from observed corpora.
- Add more compiler/library support profiles.
- Optimize predecoded dispatch, cohort execution, and workgroup parallelism.
- Consider basic-block JIT only after profiling demonstrates a need.
- Stabilize trace format, CLI, packaging, and documentation.

## 14. Quality gates and invariants

The following are permanent project rules:

- Guest device pointers are never unchecked host dereferences.
- No Scala exception may cross a C ABI boundary.
- No managed Scala code runs inside an asynchronous signal handler.
- Every supported instruction has semantic tests.
- Every generated ISA table records the source specification version and hash.
- JVM and Native run the same semantic suites.
- Native vendor conformance runs with GPUs hidden.
- Deterministic mode produces byte-identical traces for identical inputs and support profiles.
- Unsupported behavior fails with a structured diagnostic rather than silently becoming a no-op, except for explicitly documented performance hints.
- Donor code and derived tests retain license and provenance records.

## 15. Principal risks

### Scala Native DSO fidelity

Exact ELF symbol versions, multiple vendor SONAMEs, foreign-thread entry, callbacks, and runtime initialization must be proven early. The mitigation is the Phase 0 spike and, only if necessary, generated C facades around one Scala Native core.

### JVM/Native drift

The mitigation is one shared source implementation, parity tests on every change, fixed-width representations, and native oracle runs.

### ISA semantic breadth

The mitigation is pinned profiles, generated corpus coverage, normalized semantic families, and incremental library algorithm milestones.

### Memory-model correctness

The mitigation is an explicit logical memory model, deterministic scheduling, scope/order tests, optional race detection, and hardware differential cases. A sequentially stronger implementation must not be mistaken for race-free correctness.

### Floating-point fidelity

The mitigation is per-instruction rounding/denormal metadata, bit-pattern tests, hardware oracles, and diagnostics that identify approximated semantics.

### Scheduler forward progress

The mitigation is resumable workgroups, bounded instruction quanta, fair scheduling, no-progress detection, and specific tests for decoupled-lookback and atomic polling.

## 16. First concrete deliverable

The first repository milestone should contain:

- An sbt Scala 3 JVM/Native cross-project.
- A shared canonical runtime operation tested on both platforms.
- A Scala Native vendor-shaped DSO loaded by C++.
- A generated export/version report.
- A dual-mapped allocation with a checked fault demonstration.
- A tiny handwritten PTX parser that accepts one generated kernel.
- A deterministic single-block interpreter executing integer vector addition.
- A structured trace showing every instruction and output write.

This slice deliberately crosses every high-risk boundary before broad API or ISA implementation begins.

## 17. Reference projects and specifications

- GPU Ocelot: PTX semantics, functional emulator behavior, tracing, race checking, and tests.
- ZLUDA: modern PTX parser corpus and CUDA compatibility experience.
- chipStar: HIP registration, offload-bundle handling, and backend organization.
- AMD machine-readable ISA XML and ISADecoder: AMDGPU decoding and oracle.
- LLVM AMDGPU backend and documentation: code-object and architecture reference.
- NVIDIA PTX ISA: PTX syntax and semantics.
- Khronos SPIR-V grammar and specification: SPIR-V decoding and semantics.
- Level Zero specification: Intel vendor API and USM behavior.

All reuse must be reviewed for license compatibility and recorded in a provenance file before code is copied or mechanically transformed.
