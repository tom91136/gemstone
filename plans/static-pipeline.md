# Static pipeline analysis

Status: steps 1 to 5 are done; all 351 `HardwareValidationSuite` tests pass. The gfx1036 rows carry real loop addresses and the simulator models a fetch-line redirect, a condition latency, a scalar delay behind `v_fma` and a one-op queue on the FP64 and MUFU pipes. It builds on the cost model (`cost-model.md`, F3) and shares its pipeline model with F4 of `performance-profile.md`, the dynamic timing replay. `rewrite-equivalence.md` and `rewrite-search.md` use it as their cost function.

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

The redirect applies to taken back-edges only. `Architecture.branchRedirect` is 10 cycles on sm_89 (probed) and 23 on gfx1036 (fitted, see the front-end rule below); a gfx1036 loop that sits within one 64-byte line pays `Architecture.lineRedirect`, 11. `Op.backEdge` marks the op that closes a CFG back-edge: the last op of a latch in `BlockAnalysis`, and the last row of a table loop. Only those ops pay the redirect, combined with their stall hold by max. Straight blocks and loops without a back-edge op pay nothing. The gfx1036 values are fitted over loops with the control ahead of, after and through the body, so the holds of the loop-control ops are modelled apart from the redirect; see the loop-control probe and the front-end rule below.

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
- A third term of 12 cycles appears whenever the control follows the body (52 against 40) and whenever a small loop crosses a 64-byte line (79 against 67). It does not depend on how many control ops there are, and a DFMA or MUFU loop that holds the pipe for 64 cycles hides most of it. It is the 13-cycle gap of the one-warp `stream_ffma` and `stream_ffma_8` rows. The front-end rule below places it: the crossing step is a line redirect and the tail step is a condition latency.
- A taken back-edge does not make a pipe-bound loop wait for the redirect: the DFMA loop with the control ahead takes exactly the pipe time. The pipe queue below holds one op behind the running one, so the wave issues ahead of the pipe and the redirect overlaps it; `ctl_mufu8_head` (41.00 measured) passes.

## Front-end rule and pipe queue (gfx1036)

Round 4 rebuilt the gfx1036 and sm_89 probe rows from the binaries that were timed. The kits are `/var/tmp/gs/laneA`, `laneA2`, `t15-backedge`, `t15-clock`, `t15-sm89loop` and `t15-round2`; the disassembly and extraction scripts are in `/var/tmp/gs/t15-round4`. Every `HardwareLoops` row of those kits now carries the real instruction addresses. Rows whose recorded instruction order or operands were wrong are rewritten from the binary:

- gfx1036: `stream_dfma_rate` (the compiler interleaves the loop control with the DFMAs), `stream_dfma_16_wide` and `stream_mufu_16_wide` (control placement and register assignment).
- sm_89: `chain_ffma_8` (operands) and `loop_control` (the back-edge `@!P0 BRA` was missing).
- The multi-warp `stream_ffma_8` rows of both targets and the multi-warp `stream_mufu_16` rows of sm_89 were timed on the placement kernels, whose layout differs from the sweep kernels, so they are now the `stream_ffma_8_wide` and `stream_mufu_16_wide` rows.
- No measured value changed and no previously green row went red.

Six loops per target have no addresses because the 2026-11-10 sweep kernels were not kept: `chain_ffma`, `stream_ffma`, `stream_mufu`, `stream_dfma`, `mixed_ldg` and `reduce_tree`. They stay as recorded; the suite gives them synthetic addresses, which `BlockAnalysis` reads as a placement, so their gfx1036 timings rest on that.

The rule, read from the instruction addresses (`Op.span`, from the CFG offsets in `BlockAnalysis`):

- A taken back-edge costs `lineRedirect` (11) when every op of the loop lies within one `fetchLine` (64 bytes) and `branchRedirect` (23) otherwise. The 12-cycle difference is the step the layout probe shows at the line boundary. An op without an address counts as crossing.
- The back-edge waits `conditionLatency` (11) cycles after the op that wrote the registers it reads (`scc`).
- An op without a pipe waits `scalarAfter` cycles behind the last vector op: 3 behind a `v_fma`, 0 behind FP64 and MUFU.
- `pipeQueue` is the number of ops a pipe keeps waiting behind the one it runs: 1 for FP64 and MUFU. A queued op starts when the pipe frees and its result counts from then. The wave's next pipe op waits the pipe's interval less one cycle (derived from the pipe rate, not a constant), so a wave runs ahead of the pipe by up to one op. A scalar op is not held.
- No prefetch limit is modelled. Restarts of the fit landed on prefetch distances of 2 and 3 lines and fetch latencies of 2, 9 and 16 cycles at the same error, and removing the limit changes no cell, even for the 64-FMA loops of eight lines: the fetch runs far enough ahead of a wave that issues one op per cycle.

Derivation. The fit is a coordinate descent with random restarts over the six constants and the data of 456 cells: the 384-kernel layout table (`layout.tsv`, `p1.tsv`, `p2.tsv`, mean of the two passes), the 45 loop-control kernels of `ctl_*` rows, the 22 back-edge kernels of the round-2 sweep and the five placement kernels. `/var/tmp/gs/t15-round4/model2.py` and `fit2b.py` hold the model and the fit; `fit3.log` the restarts. Four of six restarts reach the same optimum (11, 23, 11, 3 and a queue of 1); the others stop higher. A first model also had separate scalar and vector redirects, a prefetch window, a fetch latency, an SGPR dependency delay, an SALU-to-SALU delay and a queue lead time. Its fit set each of those to a value that costs nothing, or left the error unchanged when it varied, and the six-constant model reproduces its error, so the committed rule drops them.

Held-out check. The 96 kernels `s_f8_*` (8 FMAs, always crossing) and `s_d4v_*` (4 DFMAs on register operands) at every offset and control placement were kept out of the fit. On the 360 calibration cells the rule has a mean error of 1.69 cycles (3.6 percent) and a worst of 9.0 cycles; on the held-out 96 it has 1.85 cycles (3.8 percent) and a worst of 7.1. No cell of the 456 is outside 25 percent: the two worst are `s_m8_comp_a24` (49 against 40.0, 22.5 percent) and `s_m8_head_a24` (50 against 41.0, 21.9 percent), then `s_f8_comp_a60` (38 against 44.0, 13.6 percent). 434 cells are within 10 percent. The committed held-out rows are `layout_f8_head_a0`, `layout_f8_head_a48`, `layout_f8_comp_a20`, `layout_f8_tail_a40`, `layout_d4v_head_a24` and `layout_d4v_tail_a8`; all pass. The Python model and the simulator agree on the unit-test loops (29 and 41 cycles within and across a line).

Rows moved by each constant (a change of one cycle, or of the queue depth by one, moves the modelled value of this many of the 456 cells): `branchRedirect` 322, `conditionLatency` 283, `scalarAfter` for `Fma` 195, queue depth 186, `lineRedirect` 94. The committed rows each constant leans on are listed under the calibration rows below.

Calibration rows. A row is calibration when a model constant was derived from that same measurement, so its pass says the constant was read back correctly, not that the model predicts the card. Every other row is an independent check. The provenance of a constant names its probe and not always the suite row, so the rows below are matched by their numbers:

- sm_89 redirect of 10: derived from the 17.02-cycle intercept of the FFMA loop sweep, whose points are the one-warp rows `stream_ffma_1` (27.02), `stream_ffma_8` (27.02) and `stream_ffma_64` (81.05, which is 64 plus 17.05).
- sm_89 `ISETP` latency of 13: derived from the one-warp `loop_control` row (27.01), as 27.01 less 4 for IADD3 and 10 for the redirect. It also rests on the redirect, so that row calibrates two constants.
- sm_89 FP64 restart of 21: derived from the 38-cycle lone-warp intercept of the register-operand DFMA loops, which are the one-warp rows `stream_dfma_reg_8` (166.08) and `stream_dfma_reg_16` (294.08).
- sm_89 DFMA operand intervals of 19 (constant bank) and 16 (register): derived from the 8 and 16 DFMA loops, the one-warp rows `stream_dfma_8` (188.03), `stream_dfma_16` (340.03), `stream_dfma_reg_8` and `stream_dfma_reg_16`, and the saturated 2 and 4-warp rows of the same kernels.
- sm_89 shared FP64 pipe: derived from the one-warp `stream_dfma` row, where one warp reaches 1.5 lanes per cycle.
- gfx1036 front end (`branchRedirect` 23, `lineRedirect` 11, `conditionLatency` 11, `scalarAfter` for `Fma` 3, `pipeQueue` 1 for FP64 and MUFU): fitted jointly to the 360 calibration cells, which are the `ctl_*` rows, the `layout_*` rows other than the held-out `layout_f8_*` and `layout_d4v_*`, and the back-edge sweep and placement kernels of `/var/tmp/gs/t15-backedge` and `/var/tmp/gs/laneA2`. The held-out layout rows above are independent checks. The earlier redirect of 22, derived from `ctl_f16_head` (40.01) and `ctl_f16_head1` (39.99), is replaced.
- gfx1036 dependent `Fma` latency of 5: derived from dependent chains of 16, 32 and 64 ops. `chain_ffma_64` (169.50) is the 64-op point; the other two are not suite rows.
- gfx1036 oldest-first issue order: derived from the same-SIMD placements of `stream_dfma_16_wide`, `stream_mufu_16_wide` and `stream_ffma_8`. The per-wave times that fixed it are not committed, so the rows hold only their per-warp averages.

The sm_89 MUFU rate and the gfx1036 FP64 and MUFU rates name probes or estimates that are not suite rows, so no row calibrates them. The `ctl_*` rows other than `ctl_f16_head` and `ctl_f16_head1` are independent checks.

Red cases in `core`: none. Seven rows were red on the 0.9 rule alone and pass once the suite matches the highest-pressure resource; the model's pressures for them were derived before the front-end rule and have not been re-derived:

- sm_89 `stream_mufu_4` with one warp: MUFU is 0.889 busy. The 8.25-cycle interval of the 2026-11-13 probe would need a MUFU rate near 15.5; the committed 16.0 rate and the simulator unit tests that pin 8.0 cycles for one warp-MUFU stay.
- sm_89 `chain_ffma` with 16 warps: issue pressure is 0.880 over a 50-cycle window. The measurement implies 48.3 cycles, so the compiled path's holds on IADD3 and ISETP and the four warps' back-edge redirects overlap slightly more than the round-robin model gives.
- gfx1036 `stream_ffma_8` with 8 and 16 warps: issue pressures were 0.721 and 0.871 against measured per-warp cycles of 34.4 and 65.8, which would give 0.64 and 0.67, so the rows expect Issue because they are at the smallest cycles the kernel reaches.
- gfx1036 `stream_mufu` with four waves, `stream_mufu_16_wide` with two waves per SIMD and `stream_dfma_rate` with one wave: pressures of 0.800, 0.889 and 0.889 before the queue, where the measurement is 70.4, 142.7 per wave set and 63.99.

Unit suites:

- `ArchitectureSuite` asserts that the gfx1036 `pipeLatency` for `Fma` is 5, that its `branchRedirect` is 23 and that its fetch line, line redirect, condition latency, scalar delay and pipe queues are the fitted values with probed provenance, that sm_89 carries none of them, and that the sm_89 MUFU rate matches its probe.
- `PipelineSimulatorSuite` pins each rule of the front end on a small loop: 29 cycles within a line and 41 across, the scalar delay (issues at 0, 3, 4, 5), the condition latency (0 then 11), the queue (issues at 0 and 15, retire at 20 and 36) and a younger wave taking the queue while the oldest is held. The oldest-first priority test now uses an FMA body, because the queue lets a younger wave issue a MUFU while the oldest wave is held.
- `BlockAnalysisSuite` expects 14 cycles for its synthetic loop, whose conditional back-edge pays the 10-cycle sm_89 redirect.
- `NvidiaLatencySuite` expects `ISETP` to name a producer now that the predicate latency is a measured value.

## Open decisions

- Whether the simulator also serves F4's dynamic replay, with the profile stream as input instead of a static block. Recommendation: yes, one model, so the two views cannot disagree.
- The tolerance at which a target's results count as validated.
- Whether the sm_89 simulator needs the same front end. `fetchLine`, `lineRedirect`, `conditionLatency`, `scalarAfter` and `pipeQueue` are unset for sm_89 because no sm_89 layout probe exists; the 16-byte SASS encoding and a different fetch granule need their own sweep. A pipe queue is modelled for gfx1036 only.
- How a shared pipe queue behaves across waves. The fit is on one-wave loops; the multi-wave rows (`stream_mufu_16_wide`, `stream_dfma_16_wide`, `stream_ffma_8_wide`) pass, but the per-wave times that would show which wave takes the queue slot are not committed.
- The six 2026-11-10 sweep loops per target have no addresses. Re-timing them from a kept binary would remove the synthetic placement from their gfx1036 rows.
- Which sm_89 MUFU interval is right: 8.0 cycles per warp-op from the 2026-11-10 rate kernel, which the model and its unit tests use, or 8.25 from the 2026-11-13 stream fit, which would bind `stream_mufu_4`. A probe that times both forms in one session would decide it.
- The sm_89 redirect of 10 cycles is derived as 17.02 minus 7 loop-control holds in the probe. The split has not been checked independently.
