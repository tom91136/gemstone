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

`HardwareValidationSuite` compares the compiled loop path, and the table path where a loop has a variable-latency op, with `clock64` measurements from the RTX 4060 Ti (sm_89) and the gfx1036.

The simulator takes an explicit placement: the number of resident warps on each partition. `Placement.even` reproduces the former even spread, so existing results are unchanged; the validation suite passes the measured sm_89 slot map (slot mod 4), which lets the same-SMSP and spread rows differ. Cycles per iteration divide by the number of active partitions, which equals `min(partitions, subgroups)` for an even spread.

The redirect applies to taken back-edges only. `Architecture.branchRedirect` is 10 cycles on sm_89 and 21 on gfx1036, both probed. `Op.backEdge` marks the op that closes a CFG back-edge: the last op of a latch in `BlockAnalysis`, and the last row of a table loop. Only those ops pay the redirect, combined with their stall hold by max. Straight blocks and loops without a back-edge op pay nothing. The gfx1036 value includes 2 cycles of scalar loop control, which the `s_add` and `s_cmp` holds may charge again, so up to 2 cycles per trip may be double counted. This is not resolved.

The gfx1036 dependent `Fma` latency is 5 cycles, probed from dependent `v_fma_f32` chains of 16, 32 and 64 ops on one wave.

Other current values:

- Result latency is carried apart from the issue stall, and a variable-latency instruction is timed whenever a table latency exists.
- sm_89 FP64 is one pipe per multiprocessor, as architecture data, with a probed dependent DFMA latency of 48 cycles.
- A constant-bank DFMA operand is charged 19 cycles per warp-DFMA and a register operand 16, from the probed slopes. `NvidiaLatency.operandRate` selects the rate from the row's operand form, not from the kernel name.
- The table path uses the `NvidiaLatency` measured and fixed tables for every producer before the encoded-stall fallback, so DFMA chains use the 48-cycle latency instead of their encoded issue stall.
- Loads, stores, and shared memory are simulator resources.
- A resource binds only at 0.9 pressure or more. Partition-local pressure is measured on its own partition, so one saturated partition binds its pipe; shared pipes keep the whole-multiprocessor pressure. The 0.9 threshold is unchanged.
- gfx1036 many-wave rates were measured again at 8, 16, and 32 waves. FP64 is flat at 8.0 lanes per cycle per WGP, which confirms the recorded rate. MUFU reaches 31.2 and is still rising at 32 waves, so the 32.0 estimate stays.
- Issue order is per target: sm_89 is round-robin and gfx1036 is oldest-first, from the 2026-11-13 same-SIMD placements (oldest wave keeps its lone MUFU, DFMA and FFMA rate, the youngest is 1.5 to 5.5 times slower, and delayed starts are not equalised). `Architecture.issueOrder` carries the choice with probed provenance and is inherited like the other fields. The simulator applies it when one partition is active; spread placements keep round-robin because their measured cycles match it.

Red cases in `core` (23 failing tests of 1059).

- sm_89 `stream_dfma` with one warp (compiled and table binding), `stream_dfma_reg_1` with one and two warps (compiled and table cycles), `stream_dfma_reg_2` and `stream_dfma_reg_4` with one warp (cycles, and binding for 4), and `stream_dfma_reg_8` with one warp (compiled and table binding): the one-warp DFMA per-trip intercept is not modelled, by decision, because its mechanism is still unknown. The 2026-11-13 probe fixes the shape: the control-only loop is 27.01 cycles per trip, N=8 and N=16 fit 16.00 cycles per DFMA with intercept 38.00, and any second warp, on the same SMSP or not, removes the intercept. The model charges 16 per DFMA and no lone-warp bubble, so it gives 128 against 166.01 at N=8 and 64 against 113.01 at N=4. The `stream_dfma` constant-bank row still binds Fp64 at pressure 1.000 against Path expected.
- sm_89 `loop_control` with one warp (compiled cycles): the model gives 17 against 27.01 measured. The control loop is one IADD3, one ISETP and one back-edge, so the model's loop-control cost is about 10 cycles short.
- sm_89 `chain_ffma` with 16 warps (compiled binding): the model binds Path at issue pressure 0.880, below the 0.9 threshold, where Issue is expected. The cycles pass: the model period is 50 cycles against 48.3 implied by the measurement (3.02 * 16). The four warps' back-edge redirects fall in step, so their 10-cycle bubbles overlap instead of hiding one another. The window has 6 idle issue slots per period, against about 4.3 implied by hardware. Compiled-path stall holds on the loop-control ops (3 cycles each on IADD3 and ISETP) are also charged, which the table path does not do. Which of the two causes contributes more is not separated.
- sm_89 `stream_mufu_4` with one warp (compiled binding): the model binds Path at MUFU pressure 0.889, where Mufu is expected. The cycles match (36.00 against 36.01). One warp at 4 MUFU per trip is just under the 0.9 threshold and the measurement is within noise, so this row stays red rather than lowering the threshold.
- gfx1036 `stream_mufu` with four waves (compiled binding): the model binds Path at MUFU pressure 0.800. The cycles pass (10.0 against 8.8). This spread placement keeps round-robin, so the model does not reproduce the oldest-first spread the probe measured; the measured slowest wave stays below the 0.9 pressure.
- gfx1036 `stream_ffma_8` with one warp (compiled cycles), and with 8 and 16 warps (compiled binding): the model gives 15.5, 3.8125 and 3.15625 cycles against 22.0, 4.3 and 4.11 measured, and binds Path at issue pressure 0.721 and 0.871 where Issue is expected. The spread placements keep round-robin, so the issue pressure is unchanged from step 5.
- gfx1036 `stream_dfma_rate` with one wave (compiled binding): the model gives 4 DFMA in a window that carries loop overhead, so FP64 pressure is 0.800 and it binds Path where Fp64 is expected. The 16-op `stream_dfma_16_wide` rows bind Fp64.
- gfx1036 `stream_mufu_16_wide` with 8 warps as two per SIMD (compiled binding): the model binds Path at MUFU pressure 0.895 where Mufu is expected. The measured two waves per SIMD are at the pipe interval, but the model's window includes the loop overhead.

Unit suites:

- `ArchitectureSuite` asserts that the gfx1036 `pipeLatency` for `Fma` is 5.
- `BlockAnalysisSuite` expects 14 cycles for its synthetic loop, whose conditional back-edge pays the 10-cycle sm_89 redirect.

## Open decisions

- Whether the simulator also serves F4's dynamic replay, with the profile stream as input instead of a static block. Recommendation: yes, one model, so the two views cannot disagree.
- The tolerance at which a target's results count as validated.
- Whether the 21-cycle gfx1036 redirect applies to MUFU loops. Only a probe of a MUFU loop can decide it.
- The sm_89 redirect of 10 cycles is derived as 17.02 minus 7 loop-control holds in the probe. The split has not been checked independently.
