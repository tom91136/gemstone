# Performance Profile Model

Status: future work, not scheduled. Everything this work needs to know about the repository is in this file; "What already exists" summarises the machine semantics it builds on.

## Goal

Gemstone should explain why a kernel is slow, with a profile whose shape matches the hardware's. It is not cycle-accurate.

A profile here means:
- per-kernel and per-region statistics (instruction mix, memory transactions, divergence, stalls by reason, occupancy);
- a timeline whose phases and bottlenecks rank the same way the hardware's do.

"Profile-accurate" means that the top bottlenecks Gemstone reports for a kernel are the ones a hardware profiler (Nsight Compute, rocprof) reports, in the same order, and that their relative sizes are within a stated tolerance. Absolute cycle counts are not a goal.

Target questions:
- Is this TMA or bulk-copy pipeline actually overlapping loads with compute, or is it serialised?
- Which accesses are uncoalesced or misaligned, and what do they cost in sectors and transactions?
- Where are the shared-memory bank conflicts, and how many replays do they cause?
- How divergent is each region, and where do lanes reconverge?
- Is occupancy limited by registers, shared memory or block size? What is the register pressure over time?
- Which instructions dominate the cost: transcendentals, 64-bit integer, atomics, tensor ops?
- Where do warps or waves stall, and why: barrier, memory dependency, async wait, scoreboard?

## Non-goals

- Cycle-accurate timing, or modelling caches as exact replacement policies.
- Modelling the warp scheduler's exact policy. A simple, documented policy is enough if rankings hold.
- Graphics pipelines.
- Replacing hardware profilers. Gemstone is for kernels without hardware, and for explaining what a profiler shows.

## Principles

- **One source of effects.** Costs attach to the machine algebra's primitives (memory by space, barriers, async completion, control requests), not to hand-written VM paths. A profile is a third interpretation next to execute and render: an observer over primitive calls, or a cost instance.
- **No cost on unobserved runs.** As with the timeline and liveness work, profiling runs only when an observer is attached. The unobserved VM path does not change.
- **Separate the facts from the model.** The VM records facts: addresses, masks, events, register use. Per-architecture models turn facts into costs. Models are data (tables) plus small pure functions, versioned per architecture.
- **Validated against hardware.** Every model table comes from probes or vendor documentation, with provenance. Committed tests encode the probe results, as with `HardwareGoldenSuite`; probe tarballs are never committed.
- **Statistics are derived, never stored twice.** Counters are computed from the event stream, so new statistics need no VM change.

## What already exists

Machine semantics (finished):
- **Algebra:** `core/.../semantics/MachineAlgebra.scala` (memory reads and writes by space with per-lane addresses, barrier arrive and park, async completion, per-lane control requests), `UnitAlgebra` and `UnitView` (cross-lane operations within a warp, wave or subgroup), and the lane `Algebra[V]`. Each ISA has its machine trait: `NvidiaMachine` (PTX and SASS, through VMIR), `AmdGpuMachine` and `SPIRVMachine`.
- **Instances:** the algebra is parametric over the value type. The execute instances are the VMs (`isa/nvidia/.../VMMachineOps.scala`, `VMInterpreter.WaveRun` in amdgpu-vm, `isa/spirv-vm/.../InvocationMachine.scala`); the render instances print executable Scala (`*MachineRender`, `StepRenderer`). `Primitive(name, function)` (`core/.../semantics/Primitive.scala`) wraps large layout functions (TMA boxes, tcgen05 layouts, wmma fragments) that execute and render share. A cost model can be a third instance or an observer at the same sites.
- **Coverage:** `explorer/src/test/scalajvmnative/net/kurobako/gemstone/explorer/CoverageSuite.scala` classifies every instruction form of every target: Machine or lane semantics (executable), NoOp, NotModelled (no observable effect in the model), Mechanics (decoded but executed by hand-written VM code outside the algebra) or NotImplemented. Exceptions are listed with reasons in `explorer/src/test/resources/coverage/policy.tsv`. Mechanics is zero on every target; NotImplemented remains only on SASS (263 target and form pairs) and AMDGPU (1025, mostly gfx1250 and gfx1251, which need hardware evidence). A cost model therefore sees every executed instruction through the algebra.
- **Typed operand domains:** operand fields carry their real domain (register ranges, named values, immediates with width and range) for AMDGPU, SASS, PTX and SPIR-V. They give instruction classes and operand kinds for cost tables without new decoding.
- **Observation:** `core/.../vm/LaunchObserver.scala`, timeline units with issue ordinals (`MachineUnit`), block and resume events with reasons (`MachineEvent`), task budgets (`TaskControl`). Observed paths exist only when an observer is attached.
- **Liveness:** per-issue register reads and writes (`core/.../vm/RegisterUse.scala`, `isa/nvidia/.../VMRegisterUse.scala`), live spans and register timelines in the explorer (`explorer/.../SandboxRunner.scala` runs the real VM on a synthesised program; there is no second VM).
- **Divergence:** units split on branches and reconverge through the convergence operations.

Performance rules the VM follows, which this work must keep:
- The unobserved path allocates nothing new per issue; observed-only work stays behind the observer check.
- Every hot method stays under the JIT inlining size limit (`isa/nvidia/.../MethodSizeSuite.scala`, also in amdgpu-vm and spirv-vm).
- Benchmark protocol: `tools/benchmarks` holds 23 cases (`BenchmarkRunner list`; `BenchmarkRunner run <case> --warmup 30 --samples 40` prints one JSON line per sample and records JFR unless `--no-jfr`). Compare a base classpath with the new one in alternating rounds (base then new, then new then base), pinned to the same cores, and accept at a median ratio of 1.01 or better on a quiet host. On a busy host, compare the JFR recordings instead: total allocated bytes (`jdk.ThreadAllocationStatistics`) and CPU execution samples, with allocation sites (`jfr view allocation-by-site`) to attribute a change. The coordinator's local scripts for this (`.plan/bench2.sh`, `.plan/bench-jfr.sh`, `.plan/build-point.sh` for a HEAD baseline) are not committed.
- Known baseline cost: the nvidia `Cohort` carries a unit id for the timeline, which made each cohort copy a third larger (ptx-integer-alu allocates 16% more than before the id existed).

Hardware and probes:
- Local: an RTX 4060 Ti (sm_89) and an AMD gfx1036. Lab NVIDIA reruns are slow and need agreement first.
- The probe kit and its README live outside the repository (`/home/tom/Nextcloud/Notes/gemstone_ref_gpus/probes/`), with importers (`import_nvidia.py`, `import_amd.py`). Every probe result becomes a committed test (for example `isa/ptx-vm/.../HardwareGoldenSuite.scala`, `hardwareGoldenTables.scala`); probe tarballs are never committed.
- Workloads: the benchmark cases and the conformance suites under `conformance/` (OpenCL CTS, miniapps, workloads).

Missing:
- per-architecture cost tables;
- a memory transaction model (sectors, lines, banks);
- occupancy calculation;
- any notion of time: async work completes at issue, so overlap cannot be seen;
- probe points and a statistics API.

## Architecture

### 1. Event stream (facts)

Extend the observer so that, when attached, each machine primitive emits a typed event:
- `MemoryAccess(unit, pc, space, width, lane addresses, active mask, kind)`, where kind is load, store, atomic or bulk;
- `AsyncIssue(unit, pc, op, bytes, completion target)` and `AsyncComplete`, with completion carried as an event instead of happening at issue;
- `BarrierArrive`, `BarrierRelease`, `Park(reason)`, `Resume`;
- `ControlSplit(unit, arms, masks)` and `Reconverge`;
- `Issue(unit, pc, instruction class, active lanes)`.

These come from the execute instance's primitives when an observer is present, at the same sites the algebra already defines. Lane address arrays reuse the context scratch, so no per-issue allocation is added when observing.

### 2. Models (per architecture)

Small pure functions over events, with per-architecture tables:
- **Coalescing:** lane addresses to 32-byte sectors and 128-byte lines (NVIDIA), or 64-byte transactions (AMDGPU). Reports transactions per request and efficiency.
- **Banks:** shared and LDS addresses to bank conflicts and replays (32 banks of 4 bytes, plus the 64-bit modes; LDS with 32 or 64 banks).
- **Instruction cost:** instruction class to issue cost and latency class (ALU, FMA, SFU and transcendental, int64, conversion, tensor, memory, atomic).
- **Occupancy:** registers per thread, shared memory per block and block size against SM or CU limits, using cubin and code-object metadata, or SASS register counts.
- **Async:** bulk, TMA and `cp.async` transfer time from bytes and a bandwidth class, plus a fixed latency.

Tables are versioned data files per architecture, such as `perf/sm_89.tsv` and `perf/gfx1030.tsv`, each with provenance (probe, card, driver, toolkit).

### 3. Timing model (overlap)

An event-driven model on top of the stream, enough to see overlap:
- each unit has a ready time;
- an issue costs its class's issue slots on the unit's scheduler;
- results become available after the class latency, so dependent issues wait (a scoreboard from register use);
- async completion is a timed event, and waits block until it fires;
- the memory model gives transaction counts and a bandwidth-limited service time per space.

The scheduler policy is round robin over ready units per scheduler, documented as a simplification.

This runs as a replay over the recorded event stream, offline from VM execution, so VM semantics and speed are unaffected and models can be iterated without re-running kernels.

### 4. Probe points and statistics

- **Probe points:** user-named regions, set from the explorer, from source markers (PTX labels, SASS addresses, AMDGPU labels, SPIR-V labels or debug lines), or from loops the control-flow structure detects automatically.
- **Statistics:** per region and per kernel:
  - instruction mix;
  - transactions and efficiency per space;
  - bank-conflict replays;
  - divergence efficiency (active lanes per issue) and reconvergence depth;
  - stall time by reason;
  - occupancy and its limiter;
  - register pressure (peak and time-weighted);
  - async overlap ratio (time with async work in flight and compute issuing).
- **Output:** a stable machine-readable schema (JSON lines), an explorer view (timeline, heat map over the listing, top bottlenecks), and a CLI summary.

## Phases

### F0: event stream

- Add the event types and emit them from the machine primitives, only when an observer is attached.
- Check with the benchmark protocol above that unobserved ratios stay within 1.01 (or that allocation and CPU samples do not grow, on a busy host), that `MethodSizeSuite` passes, and that observed overhead is measured and reported.
- Record and replay streams for the benchmark kernels.

### F1: memory and divergence statistics (no timing)

- Coalescing and bank models for sm_89 and gfx1030.
- Divergence efficiency and reconvergence.
- Explorer heat map over the listing.
- Validate against Nsight Compute (sm_89, local) and rocprof (gfx1036, local) on a set of kernels:
  - transactions per request;
  - shared-memory bank conflicts;
  - branch efficiency.

### F2: occupancy and register pressure

- Occupancy from metadata, with the limiter named.
- Register pressure over time from liveness.
- Validate occupancy against the CUDA occupancy calculator and vendor-reported values.

### F3: instruction cost tables

- Per-class issue cost and latency tables for sm_89 and gfx1030, from microbenchmark probes run with the existing probe kit, encoded as committed tests.
- Instruction-mix cost statistics.

### F4: timing model and pipelining

- The event-driven replay: scoreboard, scheduler slots, async completion events, memory service time.
- Overlap analysis for `cp.async`, TMA and bulk pipelines, `mbarrier` waits and double buffering.
- Validate the ranking of stall reasons and the overlap ratio against Nsight Compute warp-state sampling on sm_89 for a set of pipelined kernels (CUTLASS-style GEMM, stencil, reduction).

### F5: more architectures and polish

- Tables for sm_90 and sm_100 from documented values and any lab data already collected. New lab runs only by agreement.
- Tables for other AMDGPU targets as probes allow.
- CLI and JSON schema stabilisation.
- Documentation of every simplification.

## Checks for every task

- Build with sbt 2 through the running server (`sbt --client`). The modules involved are `core`, `nvidia` (`isa/nvidia`, shared by PTX and SASS), `ptxVm`, `sassVm`, `amdgpuVm`, `spirvVm`, `explorer` and `benchmarks` (`tools/benchmarks`).
- Run tests with `testOnly *` (for example `sbt --client "nvidia/testOnly *"`). Plain `test` runs only tests whose inputs changed.
- `Test/compile` for the whole build, `scalafmtCheckAll`, and `<module>/scalafix --check` on the modules touched (only hand-written files must be clean).
- `MethodSizeSuite` in `isa/nvidia`, `isa/amdgpu-vm` and `isa/spirv-vm` tests keeps hot methods under the JIT inlining size limit.
- The benchmark protocol above for any change on an execute path, observed or not.
- Never change an existing test's expectation without showing the old one was wrong.

## Validation

- **Kernel set:** the benchmark cases, conformance kernels with known behaviour, and purpose-built pathological kernels (strided access, bank-conflict patterns, divergent branches, register-heavy loops, serialised versus pipelined TMA).
- **Metric:**
  - for each kernel, the top-3 bottleneck ranking matches the hardware profiler's;
  - relative magnitudes are within a stated tolerance (start at 25%, tighten per phase);
  - each pathological kernel's defect is detected and attributed to the right instruction.
- Results are encoded as committed tests, with provenance.

## Risks

- **Observer overhead** on large kernels: keep the event stream compact (per-warp records, address compression) and allow sampling.
- **Table provenance:** vendor numbers are sparse. Probe where hardware exists, and mark the rest as estimates.
- **Async semantics:** introducing completion as an event must not change functional results. The replay is separate from execution for that reason.
- **Model drift between architectures:** keep tables data-only with per-arch tests.
- **Scope creep toward cycle accuracy:** hold the line at ranking and relative magnitude.

## Open questions

- Should the cost model be an algebra instance (compile-time) or an observer over the event stream (run-time)? Recommendation: an observer plus offline replay. It is simpler, keeps VM code paths unchanged, and models can change without recompiling semantics.
- Event stream size limits and sampling policy for long kernels.
- Which hardware profiler metrics are the reference set for each validation metric.
- How probe points map across PTX and SASS for the same kernel (line tables, labels).

## Breaking the nvidia and spirv-vm dependency cycles

Two `baumkuchen:acyclic` findings remain in the semantics and renderer code: a 7-file cycle in `isa/nvidia` and a 20-file cycle in `isa/spirv-vm`. The plan removes them by moving declarations, not by suppressing. The design stays as it is: semantics are written once against the machine algebra, and the execute instance (VM) and render instance (explorer) both run them. Nothing here changes what executes or what renders.

Paths below are relative to `isa/nvidia/src/main/scala/net/kurobako/gemstone/isa/nvidia/` and `isa/spirv-vm/src/main/scala/net/kurobako/gemstone/isa/spirv/`.

### Why the cycles exist

Three patterns recur, and each is an edge that points the wrong way:
- **Helpers declared in a consumer.** A leaf helper lives in the file that also consumes the layer above it (`StepRenderer.MatrixTerms`, `ImageOps.ImageLevel`, `Op.Prefetch`).
- **A semantics object that also holds the render or execute entry point.** `XSemantics` has the generic `inline` definitions, and also `render(instruction, machine: SPIRVMachineRender)` or `execute(execution, ...)` that names a concrete instance. The concrete instance then calls back into `XSemantics` for the forms it expands (`SPIRVMachineRender.renderImage` calls `SamplingSemantics.render`; `ImageOps.execute` calls `SamplingSemantics.execute`).
- **Lowering and execution in one file.** `DeviceEnqueueOps` and `TensorAddressingOps` hold both the lowering (`lower`, an `Op` that calls the machine) and the hand-written execution that the machine calls (`execute`). `PipeLowering`/`PipeOps` and `ImageLowering`/`ImageOps` are already split this way.

A name-based reconstruction of the file graph (imports included, as the check counts them) reproduces both components: 7 and 20 files. Removing the edges listed per cycle below leaves no component, so the plan needs no other change to reach a clean check.

### nvidia (7 files)

Edges that close cycles:
- `NvidiaMachineRender:790,794` -> `TensorMemoryRender.element` and `.dispatch`.
- `TensorMemoryRender:24` extends `StepRenderer.MatrixTerms`; `:42,44,45` call `StepRenderer.elementType`.
- `StepRenderer:67` -> `MachineSemantics.render(..., NvidiaMachineRender(...))`.
- `MachineSemantics:85-312` -> `NvidiaMachineRender` (26 references): the `render*` family of non-inline wrappers that apply the generic definitions to the concrete renderer.
- `StepRenderer:23,34,73` -> `NvidiaStaged.staged`; `NvidiaStaged:17` -> `LaneLoop` (the parameter type of `execute`); `LaneLoop:829,845` -> `VMTensor` and `MachineSemantics`; `VMTensor:38,41` -> `MachineSemantics`. These only join the component because `MachineSemantics` reaches the renderer.

Two edges are enough to need attention. Removing `TensorMemoryRender` -> `StepRenderer` alone makes the graph acyclic. Removing `MachineSemantics` -> `NvidiaMachineRender` alone leaves a 3-file cycle (`NvidiaMachineRender`, `TensorMemoryRender`, `StepRenderer`). The plan does both, because the second also stops the VM-side file `MachineSemantics` depending on the renderer.

Target layering (a file depends only on files below it):
- Leaf: `MatrixTerms` (new), `Nvidia*Staged` families, `MachineSemantics` (generic definitions only), `VMTensor`.
- `TensorMemoryRender` -> `MatrixTerms`. `NvidiaMachineRender` -> `TensorMemoryRender`. `LaneLoop` -> `MachineSemantics`, `VMTensor`.
- `MachineRendering` (new, the `render*` family) -> `MachineSemantics`, `NvidiaMachineRender`.
- `StepRenderer` -> `MachineRendering`, `NvidiaStaged`, `MatrixTerms`.

Moves and steps; each compiles and passes on its own:
1. **Leaf matrix terms.** Move `class MatrixTerms` and `def elementType` out of `StepRenderer` (about 50 lines) into a new `MatrixTerms.scala` (`private[isa] class MatrixTerms` with a companion holding `elementType`). `WarpgroupTerms` and `TensorMemoryTerms` stay in `StepRenderer`, which imports the new file. `TensorMemoryRender` changes 4 references. Size: 3 files (1 new), about 70 lines, mostly moved verbatim. This step alone removes the finding.
2. **Render dispatch out of `MachineSemantics`.** Move `render` and its 25 private `render*` wrappers (`MachineSemantics:85-313`, about 230 lines) into a new `MachineRendering.scala` as `object MachineRendering`. They use only public members of `MachineSemantics`, so no visibility changes. `StepRenderer:67` calls `MachineRendering.render`. In `MethodSizeSuite`, drop `MachineSemantics` from the excluded names (it then holds only inline definitions and small helpers), exclude `MachineRendering` instead, and point the 16 KB render-method check at it. Size: 4 files (1 new), about 250 lines moved and 6 edited.
3. **Optional: shared dispatch below both instances.** `NvidiaStaged.execute` and `Unstaged` exist for the VM (`VMSemantics:13,14` is the only caller) and give `NvidiaStaged` its `LaneLoop` dependency. Move both into `VMSemantics` as a method of their own, so the expansion of `staged(Execute, ...)` keeps its separate method for the JIT. `NvidiaStaged` then depends on the `Nvidia*Staged` families only. This is not needed for the check. It makes the shared dispatcher independent of both instances. Size: 2 files, about 15 lines.

Risks:
- **Performance:** none on the VM path. Step 1 touches only render code. Step 2 moves non-inline render wrappers that run once per definition. Step 3 moves one method between classes with the same body.
- **Method size and inlining:** step 2 and step 3 change which class holds a method. `MethodSizeSuite` covers both. Run it, do not assume.
- **`@publicInBinary`:** `StepRenderer` carries it for members reached from inline expansions. If an inline site reaches a `MatrixTerms` member, the new class needs the same annotation. The compiler reports this at the use site.
- **Test churn:** `MethodSizeSuite` only. No test calls `MachineSemantics.render`; tests that use `MachineSemantics.*` call generic definitions, which stay.

### spirv-vm (20 files)

The component is `InvocationMachine` (execute instance), `SPIRVMachineRender` (render instance), `SPIRVMachine` (the trait both implement), `Op`, `VMInvocation`, and the files that join through them: `Indirect`, `Sampling`, `SamplingSemantics`, `ImageOps`, `ImageMachine`, `StagedOps`, `PipeOps`, `CooperativeVectorOps`, `CollectiveMachine`, `DeviceEnqueueOps`, `DeviceEnqueueSemantics`, `TensorAddressingOps`, `TensorAddressingSemantics`, `MemoryAccessSemantics`, `FragmentSemantics`.

Edges that close cycles (the others in the component follow from these):
- `Op:40` -> `InvocationMachine` and `MemoryAccessSemantics`: `Op.Prefetch` is declared in `Op`'s companion. Every file that does `import Op.*` for `scalar`, `Done` or `Flow` (`StagedOps` 61 uses, `PipeOps`, `CooperativeVectorOps`, `CollectiveMachine`, `ImageMachine`, `ImageOps`, the two lowering files) sits in the cycle through this edge, and `Indirect:6` reaches it as `SPIRVMachine:33` -> `Indirect` -> `Op`.
- `VMInvocation:293` -> `ImageOps.ImageLevel` (the type of `InvocationState.texels`). `ImageOps` imports `VMInvocation.*`, so this is a 2-cycle by itself, and puts every importer of `VMInvocation.*` in reach.
- `DeviceEnqueueOps:29` -> `InvocationMachine` (the `DeviceEnqueueOp` lowering) and `InvocationMachine:2696` -> `DeviceEnqueueOps.execute`. `TensorAddressingOps:29` and `InvocationMachine:2699` are the same pair.
- `ImageOps:28` -> `SamplingSemantics.execute`, which builds an `InvocationMachine` (`SamplingSemantics:1062,1065`), while `InvocationMachine:473,1675,2684` -> `ImageOps`. The machine's `image` escape hatch re-enters the algebra for sampling forms.
- `SPIRVMachineRender:1340` -> `SamplingSemantics.render`, and `SamplingSemantics:1068` -> `SPIRVMachineRender`. This is the render-side twin of the previous edge.
- `DeviceEnqueueSemantics:34`, `TensorAddressingSemantics:30` and `MemoryAccessSemantics:208,278` -> `SPIRVMachineRender` (their `render` entry points). These are fine once the machines no longer reach back; they sit in the component only through the edges above.

Target layering:
- Leaf: `Op` (the abstract class and its helpers), `VMInvocation`, `VMMemory` (gains `ImageLevel`), `Indirect`, `Sampling`, `ImageOps`, `ImageMachine`, `StagedOps`, `PipeOps`, `CooperativeVectorOps`, `CollectiveMachine`, `DeviceEnqueueOps` and `TensorAddressingOps` (execution only).
- `SPIRVMachine` -> `Indirect`. Generic semantics (`XSemantics` inline definitions) -> `SPIRVMachine`.
- `InvocationMachine` -> `SPIRVMachine` and the leaf execution files. `SPIRVMachineRender` -> `SPIRVMachine`, `FragmentSemantics`. Neither names any `*Semantics` entry point that also names the other instance.
- `XSemantics.execute`/`render` entry points and the `*Lowering` files -> `InvocationMachine` and `SPIRVMachineRender`. The roots are `VMLowering` (VM) and `MachineSemantics` (explorer).

Moves and steps; each compiles and passes on its own:
1. **`ImageLevel` to a leaf.** Move `case class ImageLevel` (`ImageOps:491`) into `VMMemory`, next to `Pointer`. Update `ImageOps` (3 references) and `VMInvocation:293`. If a test importing `VMMemory.*` clashes with the test fixture `ImageLevel` (`fixtures.scala:156`), rename the main one. Size: 3 files, about 15 lines.
2. **`Prefetch` out of `Op`.** Move `Op.prefetch` and `class Prefetch` (`Op:35-42`) into `MemoryOps`, which already depends on `InvocationMachine` and is the only caller (`MemoryOps:41`). `Op.scala` is left depending on `VMInvocation`, `VMMemory` and `VMValue` only. Size: 2 files, about 10 lines.
3. **Split lowering from execution for device enqueue and tensor addressing.** Move `lower` and the `Op` subclass (`DeviceEnqueueOps:23-31`, `TensorAddressingOps:23-31`) into new `DeviceEnqueueLowering.scala` and `TensorAddressingLowering.scala`, as `PipeLowering` does. `VMLowering:52,57` point at them. Size: 5 files (2 new), about 40 lines.
4. **Sampling execution dispatch above the machine.** Remove the `SamplingSemantics.execute(...).getOrElse` wrapper from `ImageOps.execute` (`ImageOps:28`). `ImageLowering.ImageOp.run` tries `SamplingSemantics.execute(execution, instruction)` first and falls back to `ImageSemantics.image(InvocationMachine(execution))(instruction)`, written as a match on the `Option` so no closure is allocated. The order of attempts and the algebra program run are unchanged. Size: 2 files, about 10 lines.
5. **Sampling render dispatch above the machine.** `ImageSemantics.render` tries `SamplingSemantics.render(instruction, machine)` first, then `Option.when(handles(instruction))(image(machine)(instruction))`. `SPIRVMachineRender.renderImage` (`:1337-1340`) drops its `SamplingSemantics.render(...).getOrElse` and matches the instruction directly. Size: 2 files, about 8 lines.

Steps 1 to 3 can land in any order, and 4 and 5 in either order. The check goes clean once all five are in. Every step keeps the same generic definitions and the same expansions: single-source semantics are preserved because `SamplingSemantics.sample` and `.query` still serve both instances, and only the file that chooses between "sampling form" and "plain image form" changes.

Risks:
- **Performance:** the VM path changes in step 4 only. `ImageOp.run` gains one `Option` match in place of the `getOrElse` closure that `ImageOps.execute` had, so it allocates the same or less. `SamplingSemantics.execute` allocates its `Option` per call as before. Resolving the form at lowering time would remove that allocation; it is out of scope. No new virtual call sites: `Op.run` was already the dispatch point, and `InvocationMachine` stays a value class constructed per call.
- **Binary size and method size:** expansions stay in the same classes (`SamplingSemantics.runSample`/`runQuery`, `*Ops.execute`). `Op` subclasses move between files, so JVM class names change (`DeviceEnqueueOps$DeviceEnqueueOp` becomes `DeviceEnqueueLowering$DeviceEnqueueOp`). `MethodSizeSuite` scans the whole package and covers them without edits.
- **Generated code:** none. The grammar tables (`Instruction`, `ExtendedInstruction`) are not touched.
- **Test churn:** none expected. Tests construct programs and run them; none names the moved declarations apart from the possible `ImageLevel` clash in step 1.

### Verification

Per step:
- The `baumkuchen:acyclic` warning for the module disappears at the step named above and no new finding appears (compile plugin warnings; `Test/compile` for the module).
- `nvidia/testOnly *`, `ptxVm/testOnly *` and `sassVm/testOnly *` for the nvidia plan; `spirvVm/testOnly *` for the spirv-vm plan; `explorer/testOnly *` for both, since it renders every instruction (`CoverageSuite` and the rendered-definition tests).
- The rendered output is unchanged: the nvidia `MachineDefinitions`/`NvidiaStagedSuite` goldens and the explorer's coverage resources pass untouched. No expectation is edited.
- `scalafmtCheckAll`, and `<module>/scalafix --check` on the touched modules.

At the end of each plan:
- Whole-build `baumkuchenAnalyse`: the 7-file and 20-file `acyclic` findings are gone and nothing is added to `.baumkuchen.conf` suppressions.
- The benchmark protocol under "What already exists", HEAD against the change, as JFR comparisons on a busy host (total allocated bytes and CPU execution samples, with `jfr view allocation-by-site` to attribute any difference):
  - nvidia: `ptx-tensor`, `ptx-integer-alu`, `nvidia-control-flow`, `ptx-bulk-copy`. Only step 3 can touch these; steps 1 and 2 are expected to be exactly neutral.
  - spirv-vm: `spirv-image-sampling` (the only case whose path changes), `spirv-execution`, `spirv-cooperative-matrix`.
  - amdgpu (`amdgpu-alu`, `amdgpu-vector-copy`): no source change in the module; run one as a control for host drift.
  - Accept no growth in allocation or samples beyond run-to-run spread; on a quiet host, a median ratio of 1.01 or better.

### Order and scope

The two plans are independent and can land in either order. Within nvidia, 1 then 2 (3 optional). Within spirv-vm, 1, 2, 3 in any order, then 4 and 5. Total: 8 steps (3 nvidia, of which 2 are required, and 5 spirv-vm). The nvidia plan touches 8 files and moves about 335 lines; the spirv-vm plan touches 13 files and moves or edits about 85 lines. Nearly all of it is verbatim. The other `acyclic` findings (`UnitAlgebra`/`UnitView`, `MachineCall`/`Term`, the codegen cycle) are out of scope.
