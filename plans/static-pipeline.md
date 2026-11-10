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

`HardwareValidationSuite` holds 83 cases from the RTX 4060 Ti (sm_89) and the gfx1036. 53 fail the 25 percent tolerance, and they stay red until the model is fixed. The causes:

- `BlockAnalysis` withholds every loop with an instruction marked variable latency, even where a table latency exists.
- Recorded register use is unusable on AMDGPU: the VM never reports it, so dependency chains are invisible and every loop looks issue-bound. SASS now records each instruction's own reads and writes, merged over its issues. A SASS 64-bit operand is two 32-bit slots (for example R10 and R11), not one pair, until lowering changes.
- Latency comes from the compiler's issue stall or LLVM's issue cost, not result latency. Chains are under-predicted by about 30 percent on sm_89 and up to 84 percent on gfx1036.
- sm_89 FP64 is one pipe per SM, not per partition: one warp takes 166 cycles where the model predicts 512.
- Memory is not a simulator resource, so a load-bound loop cannot report loads as its limit.
- gfx1036 MUFU and FP64 rates disagree with the measured plateaus (5.06 against 8.0, and 15.8 against 32 cycles).

## Open decisions

- Whether the simulator also serves F4's dynamic replay, with the profile stream as input instead of a static block. Recommendation: yes, one model, so the two views cannot disagree.
- The tolerance at which a target's results count as validated.
