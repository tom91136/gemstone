# Static pipeline analysis

Status: proposed, not started. It builds on the cost model (`cost-model.md`, F3) and shares its pipeline model with F4 of `performance-profile.md`, the dynamic timing replay. `rewrite-equivalence.md` and `rewrite-search.md` use it as their cost function.

## Goal

Given a basic block or loop body of compiled code, show where each instruction spends its cycles on one target, without running the kernel: a per-instruction timeline, resource pressure per pipe, and the steady-state throughput and its bottleneck. LLVM MCA gives this view for CPU code. This plan gives it for SASS and AMDGPU code on every target with an `Architecture` table.

PTX and SPIR-V are out of scope. They carry no schedule, and the instructions they lower to depend on a compiler the tool does not run.

## What already exists

- **Pipes:** each static instruction records its pipe (`isa/nvidia/.../Pipes.scala` and `isa/amdgpu-vm/.../Pipes.scala`).
- **Capacity:** `Architecture.Throughput` holds the per-target issue rate, lanes per cycle on each pipe, load-store sectors, shared wavefronts and warp loads. `Architecture.Limits.partitions` holds the scheduler count.
- **Latency:**
  - AMDGPU: the LLVM scheduling model, through `Scheduling.timing` in `isa/amdgpu-asm`. `VMIR.Kernel` in `isa/amdgpu-vm` already turns it, with `s_delay_alu` and `s_nop`, into planned cycles per instruction.
  - SASS: the stall count and write barrier of each control word (`Schedule.Encoded`, set in `isa/sass-vm/.../VMCompiler.scala`).
  - Memory: `Architecture.Latency` holds the shared, L1, L2 and DRAM tiers. L2 and DRAM describe one named card.
- **Dependencies:** `RegisterUse` (`core/.../vm/RegisterUse.scala`) names the registers each instruction reads and writes.
- **Occupancy:** `Occupancy` gives the resident subgroups per multiprocessor and what limits them.
- **Provenance:** every value is `Sourced`, so each result can name its weakest source.

## What is missing

- A block extractor: control flow over decoded SASS and AMDGPU code, giving basic blocks and natural loops. The decoders and `ControlFlow` know branch targets. Nothing builds a graph from them yet.
- A pipeline simulator for one scheduler partition. Each cycle it chooses a ready subgroup, issues within the per-partition share of `issues`, and occupies a pipe for `lanes / rate` cycles. It stalls on register dependencies, and it releases scoreboards after the latency. Steady state takes enough iterations to repeat its occupancy.
- An NVIDIA per-instruction latency table. Stall counts give the compiler's view of fixed-latency results. Variable-latency results (MUFU, FP64 on consumer parts, conversions, shared and global loads) need either the ptxas harvest from step 2 of `cost-model.md`, or a probe.
- Concurrency between subgroups: the simulator interleaves the resident subgroups that occupancy allows, so that latency hiding shows. With one subgroup it degenerates to a latency-bound path.

## Steps

### 1. Blocks and loops

- Build a control-flow graph for decoded SASS and AMDGPU kernels, with basic blocks, dominators and natural loops.
- Tests come from existing fixtures: the nvdisasm listings in `isa/sass-vm` and the LLVM-compiled code objects in `isa/amdgpu-vm`.
- The explorer lists a kernel's blocks and loops.

### 2. Pipeline simulator

- Implement it in `core`, with no ISA knowledge. Its input is a sequence of `(pipe, lanes, reads, writes, latency, issue cost)` and an `Architecture`.
- Report per instruction: the dispatch, issue, and retire cycles of each iteration, the stall reason (dependency, pipe busy, issue slot), and the resource pressure per pipe.
- Report per block: cycles per iteration in steady state, the binding resource, and the weakest source of the numbers it used.
- Tests:
  - hand-built sequences with known answers: a dependency chain, a pipe-bound stream, and an issue-bound stream;
  - cross-checks with `ProfileReport.costs`: on a block executed once, the throughput bound must agree.

### 3. NVIDIA instruction latency

- Harvest fixed latencies from the stall counts ptxas writes, as step 2 of `cost-model.md` plans.
- Probe variable latencies on the sm_89 card for the instruction classes the simulator needs. Probe results become committed tests, as for the F3 probes.
- Other NVIDIA targets inherit with a named basis, or stay `Estimate`.

### 4. View

- Add `BenchmarkRunner analyse <case> --architecture <target> [--block <label>]`, plus the same view in the explorer next to the heat map: a timeline, a resource pressure table, and a bottleneck line.
- JSON output for tools such as the rewrite search.

### 5. Validation on hardware we own

- Use the sm_89 and gfx1036 cards. Time loop kernels with `clock64` in the probe kits: dependency chains, pipe-bound streams, mixed FMA and load loops, and an unrolled reduction.
- The metric is cycles per iteration within a stated tolerance (start at 25 percent), plus the same binding resource as the hardware shows. Results become committed tests.

## Checks for every step

As in `cost-model.md`: test first, seen failing; `testOnly *` on the touched modules; `Test/compile` on the JVM, JS and Native rows; `scalafix --check`; `scalafmtCheckAll`. Failures stay red.

## Non-goals

- Cache behaviour and DRAM contention. The memory tiers stay floors, as in F3.
- Whole-kernel runtime. The view covers a block or loop body, as LLVM MCA does.
- Cross-multiprocessor effects, the L2 and fabric included.

## Findings from step 5

`HardwareValidationSuite` holds 83 cases from the RTX 4060 Ti (sm_89) and the gfx1036. 15 fail the 25 percent tolerance and stay red until the model is fixed. The first run had 53. Fixed since then:

- Result latency is carried apart from the issue stall, and a variable-latency instruction is timed whenever a table latency exists.
- SASS profiles record each instruction's own register use. AMDGPU profiles do not yet record any.
- sm_89 FP64 is one pipe per multiprocessor, as architecture data.
- Loads, stores, and shared memory are simulator resources.
- A resource binds only at 0.9 pressure or more, with partition-local pressure scaled by active partitions.
- gfx1036 vector FMA has a probed dependent latency of 8 cycles.

Still red:

- sm_89 `stream_dfma` (7 cases): no DFMA latency for sm_89, so the loops are withheld. A dependent-DFMA probe would supply it. The single-warp FP64 gap is also unmodelled.
- gfx1036 single-wave issue (4 cases): a lone wave issues independent `v_fma_f32` every 2.7 to 3.25 cycles, against an LLVM issue cost of 1.
- gfx1036 `stream_mufu` and `stream_dfma` (4 cases): the committed one- and two-wave cases contradict the earlier many-wave plateaus, so the rates stay until the many-wave cases are measured again and committed.

## Open decisions

- Whether the simulator also serves F4's dynamic replay, with the profile stream as input instead of a static block. Recommendation: yes, one model, so the two views cannot disagree.
- The tolerance at which a target's results count as validated.
