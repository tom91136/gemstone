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
