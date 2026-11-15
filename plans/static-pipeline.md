# Static pipeline analysis

Status: steps 1 to 5 are done; 2 of the 323 `HardwareValidationSuite` tests remain red. It builds on the cost model (`cost-model.md`, F3) and shares its pipeline model with F4 of `performance-profile.md`, the dynamic timing replay. `rewrite-equivalence.md` and `rewrite-search.md` use it as their cost function.

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

The redirect applies to taken back-edges only. `Architecture.branchRedirect` is 10 cycles on sm_89 and 22 on gfx1036, both probed. `Op.backEdge` marks the op that closes a CFG back-edge: the last op of a latch in `BlockAnalysis`, and the last row of a table loop. Only those ops pay the redirect, combined with their stall hold by max. Straight blocks and loops without a back-edge op pay nothing. The gfx1036 value is measured with the loop control ahead of the body, so the `s_add` and `s_cmp` holds are not charged twice; see the loop-control probe below.

The gfx1036 dependent `Fma` latency is 5 cycles, probed from dependent `v_fma_f32` chains of 16, 32 and 64 ops on one wave.

Other current values:

- Result latency is carried apart from the issue stall, and a variable-latency instruction is timed whenever a table latency exists.
- sm_89 FP64 is one pipe per multiprocessor, as architecture data, with a probed dependent DFMA latency of 48 cycles.
- A constant-bank DFMA operand is charged 19 cycles per warp-DFMA and a register operand 16, from the probed slopes. `NvidiaLatency.operandRate` selects the rate from the row's operand form, not from the kernel name.
- A register-operand DFMA loop pays a 21-cycle drain of the FP64 pipe at the back edge. The measured lone-warp intercept is 38 cycles per trip at N=8 and 16, 17 of which an FFMA stream also pays. `Architecture.pipeRestart` carries the value. It applies when one warp per partition is resident, as busy time on the pipe after the first result; the partitions that share the pipe split it, so the 2 and 4-warp spread placements pay 11 and 6. Several warps on one partition keep the pipe busy and pay nothing, which the 4-warp same-partition rows and the saturated N=8 rows measure. The constant-bank form does not pay the drain; its 19-cycle interval already carries the operand fetch.
- The first predicate result takes 13 cycles on sm_89. The control-only loop is 27.01 cycles per trip: 4 for IADD3, 13 for the predicate and 10 for the redirect. `NvidiaLatency` carries the value as a measured sm_89 latency for `ISETP`.
- The table path uses the `NvidiaLatency` measured and fixed tables for every producer before the encoded-stall fallback, so DFMA chains use the 48-cycle latency instead of their encoded issue stall.
- Loads, stores, and shared memory are simulator resources.
- `PipelineSimulator.Block.binding` is a resource only at 0.9 pressure or more, and the 0.9 threshold is unchanged. `HardwareValidationSuite` counts a measured resource as matched when it is the block's highest-pressure resource at any level, and a measured `Path` as matched when the block's own binding is `Path`. Partition-local pressure is measured on its own partition, so one saturated partition binds its pipe; shared pipes keep the whole-multiprocessor pressure.
- gfx1036 many-wave rates were measured again at 8, 16, and 32 waves. FP64 is flat at 8.0 lanes per cycle per WGP, which confirms the recorded rate. MUFU reaches 31.2 and is still rising at 32 waves, so the 32.0 estimate stays.
- Issue order is per target: sm_89 is round-robin and gfx1036 is oldest-first, from the 2026-11-13 same-SIMD placements (oldest wave keeps its lone MUFU, DFMA and FFMA rate, the youngest is 1.5 to 5.5 times slower, and delayed starts are not equalised). `Architecture.issueOrder` carries the choice with probed provenance and is inherited like the other fields. The simulator applies it when one partition is active; spread placements keep round-robin because their measured cycles match it.
- The 2026-11-13 probe also measured the sm_89 MUFU interval at 8.25 cycles per warp-op. That is 3 percent above the 16 per multiprocessor of the 2026-11-10 rate kernel, which the model keeps and `PipelineSimulatorSuite` pins. The two probes disagree, so one row stays red below.

The gfx1036 loop-control probe separates the back-edge redirect from the scalar loop control. Every kernel is one inline asm loop on one wave with the loop top on a 64-byte boundary, timed with `clock64` (`s_memtime`) around the loop, 1024 trips per launch, median of 201 launches after 5 warm-ups. Two full passes in opposite kernel order agree to 0.001 cycles per trip. Card: Ryzen 9 9950X3D integrated GPU, Linux 7.3.3 amdgpu, ROCm 7.2 hipcc (HIP 7.2.53211, clang 22), 2026-11-15, host load average 7.4 to 11.9 on 32 threads. The timed region of each kernel was disassembled and holds only the listed instructions. The rows are the `ctl_*` loops in `HardwareLoops`, with the cycles in `HardwareValidationSuite`. Cycles per trip:

- 16 independent `v_fma_f32` with `s_add`, `s_cmp` and the branch after the body (the compiled form): 52.00, equal to the compiler-built loop of the same body. With both control ops ahead of the body and the branch alone after it: 40.01. With a single `s_sub` ahead and a `scc0` branch after: 39.99. With the single `s_sub` after the body: 50.00.
- Extra independent scalar ops after the body each cost 1.00 cycle: 2 extra `s_add` 54.00, 4 gives 56.00, 8 gives 60.00; extra `s_cmp` give the same. Ahead of the body they cost nothing: two control ops and one cost the same 40.0.
- Control placed inside the body costs more: the two control ops spread through the body 51.01, and 4 extra independent `s_add` spread through it 70.00, 4.5 cycles each.
- The same loop unrolled twice and four times: 68.00 and 100.00, so 16.0 cycles per 16 FMAs and the same 36-cycle intercept per back-edge.
- The loop top at offsets 0 to 60 bytes in a 64-byte line moves the 16-FMA loop between 52.00 and 55.51.
- 4 `v_fma_f64`: 67.01 with tail control, 63.91 with the control ahead (the 64 cycles of FP64 pipe, so the loop overhead is hidden), 71.01 and 86.00 with 4 and 8 extra scalar ops, 138.00 unrolled twice. Loops of 1, 2, 3 and 6 DFMAs take 22.01, 37.01, 52.01 and 97.01.
- 8 `v_rcp_f32`: 50.00 with tail control, 41.00 with the control ahead, 92.99 unrolled twice. The 44-byte DFMA and MUFU loops take 67.0 and 50.0 when they sit inside one 64-byte line and 79.0 and 62.0 when they cross a 64-byte boundary, a 12-cycle step.

How the data splits the two costs:

- The scalar loop control costs one issue cycle per op, and nothing when it sits ahead of the body. It is not a separate 2-cycle term, so the earlier 21 (19 per trip beyond the dependent chain plus 2 assumed for control) is replaced by the measured 22: with the control ahead of the body the branch issues 18 cycles into the trip and the next trip starts at 40.
- A third term of 12 cycles appears whenever the control follows the body (52 against 40) and whenever a small loop crosses a 64-byte line (79 against 67). It does not depend on how many control ops there are, and a DFMA or MUFU loop that holds the pipe for 64 cycles hides most of it. It is the 13-cycle gap of the one-warp `stream_ffma` and `stream_ffma_8` rows. The model has no code addresses or fetch lines, so it cannot place the term. Adding it to the redirect (33 in total) clears the one-warp FFMA row and turns six other rows red: `stream_ffma_1`, `chain_ffma_1`, `stream_mufu_1`, `stream_dfma_1`, `stream_mufu` with four waves and `stream_dfma_rate`, which model 84 cycles against 63.99.
- A taken back-edge does not make a pipe-bound loop wait for the redirect: the DFMA loop with the control ahead takes exactly the pipe time. The simulator blocks a wave at issue until the pipe is free, so it charges the redirect after the last pipe op; a wave that issues ahead of the pipe would hide it. `ctl_mufu8_head` is red for this reason: 53 modelled against 41 measured.

Red cases in `core`: 2 failing tests of 323 in `HardwareValidationSuite`, and none elsewhere in the profile suites. Seven further rows were red on the 0.9 rule alone and pass once the suite matches the highest-pressure resource; the model's pressures for them are unchanged:

- sm_89 `stream_mufu_4` with one warp: MUFU is 0.889 busy. The 8.25-cycle interval of the 2026-11-13 probe would need a MUFU rate near 15.5; the committed 16.0 rate and the simulator unit tests that pin 8.0 cycles for one warp-MUFU stay.
- sm_89 `chain_ffma` with 16 warps: issue pressure is 0.880 over a 50-cycle window. The measurement implies 48.3 cycles, so the compiled path's holds on IADD3 and ISETP and the four warps' back-edge redirects overlap slightly more than the round-robin model gives.
- gfx1036 `stream_ffma_8` with 8 and 16 warps: issue pressures are 0.721 and 0.871. At the measured per-warp cycles of 34.4 and 65.8 they would be 0.64 and 0.67, so the rows expect Issue because they are at the smallest cycles the kernel reaches, not because the pressure reaches 0.9.
- gfx1036 `stream_mufu` with four waves, `stream_mufu_16_wide` with two waves per SIMD and `stream_dfma_rate` with one wave: pressures of 0.800, 0.889 and 0.889 against windows of 80, 72 and 72 cycles, where the measurement is 70.4, 142.7 per wave set and 63.99. The gap is the loop turn-around: the two-wave rows pay two back-edge redirects, and the FP64 rate row pays one after a 64-cycle pipe, where the measurement shows it fully hidden.

Remaining red:

- gfx1036 `stream_ffma_8` with one warp (compiled cycles): the model gives 16.0 against 22.0, and `stream_ffma` gives 20.0 against 26.0, which passes only by the 25 percent tolerance. Both windows are 12 cycles short of the measurement: the layout term above, which the model has no way to place.
- gfx1036 `ctl_mufu8_head` (compiled cycles): 53 modelled against 41 measured. The loop overhead hides under the MUFU pipe on the hardware, and the simulator charges the redirect after the last pipe op.

Unit suites:

- `ArchitectureSuite` asserts that the gfx1036 `pipeLatency` for `Fma` is 5, that its `branchRedirect` is 22, and that the sm_89 MUFU rate matches its probe.
- `BlockAnalysisSuite` expects 14 cycles for its synthetic loop, whose conditional back-edge pays the 10-cycle sm_89 redirect.
- `NvidiaLatencySuite` expects `ISETP` to name a producer now that the predicate latency is a measured value.

## Open decisions

- Whether the simulator also serves F4's dynamic replay, with the profile stream as input instead of a static block. Recommendation: yes, one model, so the two views cannot disagree.
- The tolerance at which a target's results count as validated.
- Whether the simulator should let a wave issue ahead of a busy pipe, so that a pipe-bound loop hides its back-edge. The gfx1036 DFMA and MUFU loops with the control ahead of the body say yes; the sm_89 rows have no such probe.
- Which sm_89 MUFU interval is right: 8.0 cycles per warp-op from the 2026-11-10 rate kernel, which the model and its unit tests use, or 8.25 from the 2026-11-13 stream fit, which would bind `stream_mufu_4`. A probe that times both forms in one session would decide it.
- The sm_89 redirect of 10 cycles is derived as 17.02 minus 7 loop-control holds in the probe. The split has not been checked independently.
