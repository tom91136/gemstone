# Concurrency checking

Status: proposed, not started. It comes before `ecosystem-conformance.md` and `workload-corpus.md`, which need it to tell a race in a program from a defect in the emulator.

## Goal

Run a launch under a chosen, reproducible order of waves and workgroups, and report data races and undefined behaviour with the two program locations involved. The same runs check the emulator: a program that is free of races must give the same result under every order, so a result that changes with the order and has no race behind it is an emulator defect in barrier, atomic, or fence semantics.

## Current state

- Workgroups of a non-cooperative launch run on `WorkerPool` threads, so their order follows host thread scheduling, not anything a run can name. `Workgroups.Sequential` runs them one at a time.
- `Pacing` keeps running workgroups within a small step bound of the slowest once a launch polls global memory, to match NVIDIA hardware, where blocks leave a grid barrier at near-equal rates (`conformance-coverage.md` item 15).
- Within a workgroup the order is fixed. AMDGPU runs a wave until it reaches a barrier, ends, or suspends, and a resident wave yields after 64 backward branches. NVIDIA runs cohorts from a ready queue in arrival order; a cohort in a warp partition runs apart from the others until a barrier spans them.
- `ReplayDevice` replays a launch to any instruction from a memory snapshot. Replay is exact only with sequential workgroups.
- Nothing detects races. A race shows only when the fixed order happens to expose it, as the cuSPARSE `find_colors_ker` race did under host load.

## Design

### Schedules

- One schedule decides which wave or cohort runs next inside a workgroup and which workgroup runs next in a launch. The default stays today's fixed order, so normal runs keep their speed.
- **Random:** a uniform choice at each scheduling point, from a seed.
- **PCT:** probabilistic concurrency testing (Burckhardt et al., 2010). Random priorities with `d` priority change points find any bug of depth `d` with a known lower bound on probability per run.
- Under a seeded schedule every workgroup of the launch runs on one host thread. The seed, the schedule kind, the program, and the inputs then fix the run, and `ReplayDevice` can replay it. Parallelism comes from running many cases or seeds at once.
- Scheduling points are barriers, yields, and ends today. Races need switches at memory operations too: shared, global, atomic, and fence instructions. ALU instructions are not scheduling points, which bounds the cost.
- NVIDIA sm_70 and newer may also reorder diverged cohorts of one warp, as independent thread scheduling allows. Older NVIDIA targets and AMDGPU keep a wave in lockstep.

### Race detection

- A race is two accesses to the same byte from different threads, at least one a write, at least one not atomic, with no happens-before order between them.
- **Shared memory:** barrier intervals. A shadow word per byte holds the last writer and a reader summary for the current interval, and a workgroup barrier starts a new interval. This is cheap and covers the common missing-`__syncthreads` bugs.
- **Global memory:** vector clocks per workgroup, in the FastTrack style, with release and acquire edges from atomics and fences. Scope matters: an atomic or fence at workgroup scope does not order accesses from another workgroup, so a scoped race is a race.
- **Warp level:** on sm_70 and newer, lanes that exchange data through shared memory without `__syncwarp` or a shuffle race with each other.
- Each ISA maps its own instructions to these events: SASS `MEMBAR` and `ATOM` scopes, PTX `.acquire`, `.release`, and scope qualifiers, AMDGPU `s_waitcnt` with cache controls and invalidates, and SPIR-V memory semantics and scope operands.
- A report names both accesses: kernel, instruction offset, source line when debug information has one, thread and workgroup, address space, allocation, and offset.

### Strict mode

- Reads of uninitialised shared memory, registers, or allocations.
- Barriers that only part of a workgroup reaches.
- Shuffles and votes that read inactive lanes, and `__syncwarp` masks that the arriving lanes do not match.
- Misaligned accesses that hardware tolerates but the ISA leaves undefined.

## Steps

### 1. Seeded order inside a workgroup

- A schedule interface in `core`, read by both interpreters at their existing scheduling points, with fixed and random kinds.
- An environment key for the schedule, generated through `GenerateEnvironment` like the other keys.
- Test first: a kernel whose result depends on wave order gives different results under two seeds, and the same result under one seed twice.

### 2. Seeded order across workgroups

- Run all workgroups of a launch on one thread under a seeded schedule, cooperative launches included. Decide how `Pacing` applies: a seeded run should still respect the hardware property `Pacing` models, or report where it relies on it.
- `ReplayDevice` replays seeded launches.

### 3. Memory scheduling points and PCT

- Switch points at memory, atomic, and fence instructions, and the PCT schedule.
- Measure the cost against the fixed order on the benchmark cases.

### 4. Shared-memory races

- Barrier-interval detection on every ISA.
- Test first with kernels that miss a barrier in a reduction, and with race-free kernels that must report nothing.

### 5. Global-memory races

- Vector clocks with atomics, fences, and scopes, per ISA.
- Tests: a last-block reduction without a fence, a flag published at workgroup scope and read from another workgroup, and correct versions of both.

### 6. Strict mode

- The checks above, each behind the same mode, each with a failing and a passing test.

### 7. Corpus runs

- Run the conformance workloads under a set of seeds. A case that changes result with the seed and reports no race points at the emulator. A case that reports a race points at the program or library.
- Add a bounded seeded workload to CI.

## Checks

- Test first, seen failing, for behaviour changes.
- The fixed order stays the default, and the full test and conformance runs stay as they are.
- The vendor conformance test suites are written to be free of races, so a race report or a seed-dependent result on them is investigated as an emulator defect first.
- Failures stay red.

## Risks to confirm

- Cost of shadow memory for global allocations of several GB. Shadowing only the allocations a launch touches may be enough.
- AMDGPU expresses ordering through cache controls and wait counts rather than scoped instructions, so mapping it to scopes needs care per generation.
- Spin loops under a random schedule can run for a long time before the waited-for wave runs. The yield on backward branches bounds this, but the bound may need to be part of the schedule.

## Non-goals

- Weak memory behaviour beyond sequential consistency, such as store buffering. Race detection already reports the missing synchronisation that such behaviour would expose.
- Host-side races between API calls and streams.
- Proving a program free of races. A clean run covers the schedules it ran.

## Open decisions

- Whether a race inside a vendor library fails a run. Recommendation: report it, and fail only for code the suite compiles, the same split the floor check uses.
- Schedule key format. Recommendation: one key with `fixed`, `random:<seed>`, and `pct:<seed>:<depth>`, and a separate key that enables race and strict checks, so a seeded run without checks stays cheap.
