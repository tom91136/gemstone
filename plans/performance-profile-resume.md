# Performance profile: resume brief

Companion to `performance-profile.md`. State of the working tree as left uncommitted on `main` (nothing is committed or pushed). Scratch, logs and agent reports live in `/tmp/gemstone-profile/` (gone after a reboot; everything needed to continue is restated here).

## Agreed scope

F0, then F1 and F2 with model tests. Hardware validation uses the local RTX 4060 Ti through ncu; no lab runs. F3 (cost tables from microbenchmarks) and F4 (timing replay) are out of scope until the user says otherwise.

## Done

### F0 event stream (core)

- `core/.../vm/LaunchObserver.scala`: new default hooks `access(lane, space, kind, address, bytes)`, `asynchronous(lane, kind, target, bytes)` with `AsyncKind` (Transfer, Commit, WaitGroups, Arrive, Wait), `launched(KernelResources)`, and `LaunchObserver.both(first, second)`. `AccessKind` derives `CanEqual`.
- `core/.../vm/ArchitecturalState.scala`: new abstract `subgroupSize`, `eachLane(each)` (active lanes, workgroup indices, ascending) and `text`.
- `core/.../vm/KernelResources.scala`: workgroup lanes, subgroup size, registers per lane (optional), shared bytes.
- `core/.../profile/ProfileRecorder.scala`: thread-buffered recorder of packed `Long` records; merges buffers by issue ordinal. Keeps each static instruction's text and first non-empty `RegisterUse`, and the launch's `KernelResources`.
- `core/.../profile/ProfileStream.scala`: replay (`foreach(visitor)`), versioned binary `write`/`read`.

### F0 per VM (done by three Sonnet agents; reports summarised)

- nvidia (`isa/nvidia`, new `VMObservation.scala`): all lane memory paths in `LaneLoop`/`LaneFaults` and the machine primitives, every async site except Commit/WaitGroups. A `lane` parameter was added to 11 lane-less primitives in `NvidiaMachine`, `NvidiaMachineRender`, `MachineSemantics`. Parameter-block reads report `Space.Constant`. `text` is the guard plus the VMIR instruction's `toString` (VMIR keeps no PTX/SASS text). Tests: `ptxVm` `ProfileSuite` (16), `sassVm` `ProfileSuite` (1).
- amdgpu (`isa/amdgpu-vm`): vector, DS, scalar, flat (resolved space), LDS DMA; `text` via new `Kernel.assembly`. Scalar accesses reported from the wave's first lane as Global. Only LDS DMA reports async (`Transfer`, target -1); `s_waitcnt` lowers to a payload-free `Instruction.Wait`. Tests: `ProfileSuite` (14).
- spirv (`isa/spirv-vm`): every access through `MemoryState` in `VMMemory.scala`, bound per op by `Observed.run`. Input/Output storage classes not reported. OpGroupAsyncCopy reports Transfer then Commit from invocation 0; OpGroupWaitEvents reports WaitGroups from every lane. Tests: `ProfileSuite` (8).

### F1 models (core)

- `profile/Architecture.scala`: sm_89, gfx1030, gfx1036 tables; every value `Sourced` with `Documented`, `Probed` or `Estimate`. Deviation from the plan: tables are typed Scala data, not `perf/*.tsv` (correct by construction, no resource loading on JS).
- `profile/MemoryModel.scala`: sectors, lines, ideal units, bank wavefronts.
- `profile/Profiler.scala`: groups lanes into subgroup issues (by issue; by per-lane occurrence when every issue has one lane, as SPIR-V), measures requests per (ordinal within issue, space, kind), lane efficiency, divergent issues (lanes continue at more than one instruction) and reconverging issues (lanes arrive from more than one subgroup issue).
- `profile/Profile.scala`, `profile/ProfileReport.scala`: counters, JSON lines (schema 1: one `launch` line, then `instruction` lines), text summary, findings ranked within kind (excess sectors, bank-conflict wavefronts, idle lanes).

### F2 (core)

- `profile/Occupancy.scala`: the `cuda_occupancy.h` algorithm (sub-partitioned registers in 256-register granules, warps, blocks, shared with 1 KB reserved and 128-byte granules), generalised to RDNA2 (4 SIMDs per WGP, 1024 VGPRs per SIMD per lane, 16-register granules in wave32 and 8 in wave64, LLVM `getVGPRAllocGranule`). Matches all 1800 configurations of `cudaOccMaxActiveBlocksPerMultiprocessor` from CUDA 13.3, limiters included; a 210-row subset is in `OccupancySuite`.
- Register pressure (`Profile.Pressure`): backward liveness over each subgroup's issue sequence using the recorded per-instruction `RegisterUse`; peak and mean live register names.

### Hardware data (sm_89, RTX 4060 Ti, driver 615.78.08, CUDA 13.3, ncu 2026.2.1)

Probe: `/home/tom/Nextcloud/Notes/gemstone_ref_gpus/probes/profile/profile_memory.cu` with `profile/run.sh`; results tarball `probes/profile-soraws-uk-sm_89.tar.gz` (PTX, SASS, cubins, ncu CSV). One warp per launch. The F1 model predicts every memory column exactly.

```
id kernel           ldReq ldSec stReq stSec shLdWf shStWf shLdCf shStCf thrInst inst brT brDiv brUni
0  ld_stride 1      1 4   1 4  0  0  0  0  320  10 0 0 0
1  ld_stride 2      1 8   1 4  0  0  0  0  320  10 0 0 0
2  ld_stride 4      1 16  1 4  0  0  0  0  320  10 0 0 0
3  ld_stride 8      1 32  1 4  0  0  0  0  320  10 0 0 0
4  ld_stride 32     1 32  1 4  0  0  0  0  320  10 0 0 0
5  ld_offset 1      1 5   1 4  0  0  0  0  320  10 0 0 0
6  ld_offset 8      1 4   1 4  0  0  0  0  320  10 0 0 0
7  ld_double 1      1 8   1 8  0  0  0  0  320  10 0 0 0
8  ld_double 2      1 16  1 8  0  0  0  0  320  10 0 0 0
9  ld_float4        1 16  1 16 0  0  0  0  288  9  0 0 0
10 shared_stride 1  0 0   1 4  1  1  0  0  384  12 0 0 0
11 shared_stride 2  0 0   1 4  2  2  1  1  384  12 0 0 0
12 shared_stride 4  0 0   1 4  4  4  3  3  384  12 0 0 0
13 shared_stride 8  0 0   1 4  8  8  7  7  384  12 0 0 0
14 shared_stride 16 0 0   1 4  16 16 15 15 384  12 0 0 0
15 shared_stride 32 0 0   1 4  32 32 31 31 384  12 0 0 0
16 shared_stride 33 0 0   1 4  1  1  0  0  384  12 0 0 0
17 shared_broadcast 1 1   1 4  1  1  0  0  420  18 1 1 0
18 shared_double 1  0 0   1 8  2  2  0  0  384  12 0 0 0
19 shared_double 2  0 0   1 8  4  4  2  2  384  12 0 0 0
20 divergent 1      2 5   1 4  0  0  0  0  1088 55 9 1 8
21 divergent 16     2 5   1 4  0  0  0  0  1088 55 9 1 8
```

Build note: nvcc 13.3 rejects the host GCC; pass `-allow-unsupported-compiler`. ncu needs root (`RmProfilingAdminOnly: 1`); `sudo -n` works on this host.

## Done since hand-off (2026-11-01)

- **Tree verified.** Every touched module compiles on the JVM, Scala.js and Scala Native; `scalafmtCheckAll` passes; scalafix is clean on every touched file (record tags and `recordLength` live in the package-private `ProfileRecords`); full `testOnly *` passes for core (375), nvidia (302), ptxVm (6610), sassVm (6624), amdgpuVm (2635), spirvVm (1863) and explorer (101). The Scala.js variants compile.
- **F0 overhead, unobserved launches.** 6 alternating rounds against `7edb17ad`, 19 cases (`amdgpu-program-lifecycle` needs `--image` and was skipped). Allocation per run is unchanged (new/base 0.95 to 1.05). Time ratios run 0.65 to 1.24 with overlapping per-round ranges on a host at load ~17, so there is no measurable time cost; no JFR CPU sample has an observation frame on top. Results: `/tmp/gemstone-profile/bench/summary.txt`. Benchmark cases with generated fixtures must run with the repository root as working directory; `run.sh` now does.
- **Hardware golden tests.** `ProfileGoldenSuite` in `sassVm` (nvdisasm listings in `fixtures/ProfileFixtures.scala`) and `ptxVm` (the probe module), each a text table of the counters above. SASS matches every column of all 22 rows, `inst` and `thrInst` included. PTX matches 20 rows; `divergent` stays red (see below).
- **Issues as hardware counts them.** `ArchitecturalState.begins` says whether an issue begins a source instruction. nvidia derives it from the source map (`ExecutableProgram.begins`): the first VMIR op a SASS or PTX instruction lowered to begins it, and ops without a source in a program that has sources (the entry prologue) begin none. The recorder keeps it in the issue header (stream version 2); the `Profiler` folds a non-beginning issue into the lane's current subgroup issue, and counts traffic but no issue for one with nothing to fold into. AMDGPU and SPIR-V always begin.

## Done: reconvergence (2026-11-02)

Mirrors ptxas, probed with CUDA 13.3 for sm_89: a divergent branch gets `BSSY` before it and `BSYNC` at its immediate post-dominator, hoisted to the loop preheader for loop exits; loops containing atomics, volatile or strong loads get a `YIELD`; loops of arithmetic or plain loads do not.

- `Instruction.Yield`: a yielding lane leaves every live convergence barrier of its warp. SASS `YIELD` lowers to it, so a ptxas intra-warp spin lock terminates instead of livelocking (`ControlFlowSuite`).
- `ptx/Reconvergence.scala` plans barriers and yields per routine (dominators, post-dominators, natural loops); `VMCompiler` emits them as unsourced instructions, so the profiler counts no issue for them. The `ptxVm` `ProfileGoldenSuite` now matches all 22 hardware rows.
- Warp and warpgroup collectives no longer wait for lanes parked at a convergence barrier, so a divergent `.aligned` collective still faults with "requires all 32 lanes" instead of deadlocking.
- VM fast paths for convergence (per-warp setup, unparked arrival when one cohort completes the barrier). Remaining cost on a busy host: `ptx-bulk-copy` 1.03-1.30x, `nvidia-control-flow` 1.06-1.23x, others within noise.
- A loop whose header is entered by fall-through from its own body (`bra TEST; BODY: ...; TEST:`) gets its setup in an out-of-line stub that outside branches are retargeted to.
- Barriers share token registers by interference (`Reconvergence.assign`, greedy in barrier order): two barriers conflict when their held blocks (reached from the setup without passing the join) overlap, one is set up or joins inside the other, or they share a join. `registerCount` grows by the colour count.

## Done: launch resources and occupancy (2026-11-02)

- Every VM calls `launched(KernelResources)` once per observed top-level launch, so `Profile.occupancy` is defined. SASS: cubin register count (`EIATTR_REGCOUNT`) and `.nv.shared.<kernel>` plus dynamic shared; PTX: no registers, static `.shared` plus dynamic; AMDGPU: VGPRs from `GRANULATED_WORKITEM_VGPR_COUNT` (encoding granule by ISA and wave size), wave size from the descriptor, group segment plus dynamic LDS; SPIR-V: no registers, `Workgroup` storage bytes. Device-side child launches are not reported.
- `OccupancySuite` holds 41 sm_89 launches measured with ncu (`profile/occupancy.cu` in the Nextcloud kit, results `probes/profile-soraws-uk-sm_89-occupancy.tar.gz`); the model matches every register, warp and block limit and the theoretical occupancy. The driver shrinks the shared carveout when shared memory does not bind, so the shared limit compares only on full-carveout rows.
- gfx1030 register granule corrected to 512 lane-registers per wave (LLVM `getVGPRAllocGranule`: 16 VGPRs in wave32, 8 in wave64 on GFX10.3).
- Approximations: PTX static shared counts module-level `.shared` the entry does not use; SPIR-V counts every `Workgroup` variable in the module.

## Done: gfx1036 and sm_89 rates (2026-11-02)

- rocprofv3 (container `localhost/gemstone-rocprof:7.1.1` from `probes/profile/rocprof.Containerfile`, ROCm 7.1.1 with rocprofiler-sdk, aqlprofile and libdw1; run with `--device /dev/kfd --device /dev/dri --group-add keep-groups --security-opt label=disable` and `HSA_OVERRIDE_GFX_VERSION=10.3.0`) exposes no counters for gfx1036, so the iGPU was measured by timing instead: `profile/profile_timing.hip` times dependent pointer chains with `s_memtime` on one wave32 (results `probes/profile-soraws-uk-gfx1036-timing.tar.gz`). LDS cost grows by one cycle per further conflicting lane up to 31 extra, and wraps at 33 lanes, so 32 banks; global cost grows with each further 128-byte line, so gfx1030 global sectors are 128 bytes. Both are `Probed` and asserted by hardware rows in `ProfilerSuite`.
- sm_89 per-SM rates from ncu `peak_sustained` (`probes/profile-soraws-uk-sm_89-peak.tar.gz`): one l1tex data-stage wavefront (128 bytes, 4 sectors) per cycle for global and shared, 4 warp instructions issued. `Architecture.sm89.throughput` cites them as `Probed`.
- `ProfileReport` findings rank by estimated cycles through `Architecture.Throughput`.

## Done: benchmark profiling (2026-11-02)

- Every VM benchmark case runs one launch with a `LaunchObserver` (cases with several launches profile the first). `BenchmarkRunner profile <case> [-- args] [--architecture sm_89|gfx1030|gfx1036] [--stream file] [--replay file] [--json file]` prints `ProfileReport.summary`; NVIDIA defaults to `sm_89`, AMDGPU to `gfx1036`, SPIR-V and OpenCL require `--architecture`. `run <case> --observe` attaches a fresh `ProfileRecorder` per launch and adds `"observed"` to the result JSON. `BenchmarkProfileSuite` checks record, write, read and replay give the identical `Profile` for every case. `ptx-cache`, `ptx-program-lifecycle --operation decode` and `amdgpu-dispatch-memory` issue no launch and are rejected.
- Benchmark `run` and tests use the repository root as working directory (`build.sbt`), and `generateBenchmarkFixtures` resolves its paths from the root.
- Observed cost, median ns/op observed/plain on a busy host (3 rounds, 30 samples, cores 8-11): PTX 1.3 to 5.9x, SPIR-V 1.3 to 6.9x, OpenCL 3.0x, AMDGPU 6 to 104x (`amdgpu-alu` 104x, `amdgpu-atomic` 88x, `hip-cloverleaf-flux` 18x). Not profiled; the likely cost is the per-issue work an observed AMDGPU wave does (a `Fault.Location`, an `ArchitecturalState` view, `RegisterUse` and the recorder's locked instruction lookup) against a fast unobserved path.

## Done: explorer heat map (2026-11-02)

- `ProgramRun.launch` records a `ProfileRecorder` next to the `Recording`; `Run.profile` measures it under the ISA's default architecture (`sm_89` for PTX, SASS and SPIR-V, `gfx1036` for AMDGPU) and `Run.profiled` under another one.
- `Heat.of` maps profiled instructions to listing lines through `Listing.find`, merging instructions that share a line. The listing shades lines by issues, idle lanes, excess sectors, bank conflicts or estimated cycles lost on one five-step ramp with an inset bar and a value column, so the shade does not rely on hue; tooltips carry every counter. A summary under the listing shows lane efficiency, divergence, occupancy and the top five findings. `HeatSuite` covers the mapping on every sample ISA and the scale as pure functions. Not yet looked at in a browser.

## Done: event stream gaps (2026-11-02)

- `AsyncKind.Expect` and `Complete` report standalone `mbarrier.expect_tx` and `complete_tx`. `cp.async` and bulk group commits and waits (PTX), and `LDGDEPBAR` and `DEPBAR.LE SB0, n` (SASS), report `Commit` and `WaitGroups`; `target` names the group class. AMDGPU `s_waitcnt` decodes its counts per generation and reports `WaitCounter` per constrained counter from the wave's first active lane.
- `access` docs say nvidia parameter-block reads report `Space.Constant`.
- `Profiler` finalises a subgroup's issues once no later record can fold into them, so memory stays bounded by open groups (`Profiler.measure` reports the peak).
- Findings rank in estimated cycles (see the rates above).
- nvidia issue text is the PTX or SASS source text. The runtimes may not depend on their asm packages (scalafix `PackageBoundary`), so `PTXBackend`, `CubinBackend` and both `LoadedProgram`s take the printer as a mandatory `text` parameter; the server, explorer, samples, benchmarks and tests pass the asm printer.
- Register pressure counts 32-bit units (`RegisterUse.units`), so a 64-bit register counts as two.

- AMDGPU split `s_waitcnt_*` forms and GFX12 `s_wait_*` counter waits report `WaitCounter` steps; `Instruction.WaitCounts` holds counter and count pairs, and the target numbering (0 vm/load, 1 exp, 2 lgkm/ds, 3 vs/store, 4 km, 5 sample, 6 bvh, 7 x, 8 async) lives in the `AsyncKind.WaitCounter` scaladoc.

## Done: gap follow-ups (2026-11-02)

- Observed AMDGPU cost: `View.text` disassembled the instruction from the code object on every call (84% of samples in an observed `amdgpu-alu` run). `Kernel.assembly` now caches the text per instruction. `ProfileRecorder` resolves an issue's instruction through a per-thread cache keyed by address for the kernel and function the thread last ran, so repeat issues skip hashing `Fault.Location` and the lock, and it remembers the last buffer instead of looking one up per call. Observed/plain on a busy host: `amdgpu-atomic` 88x to 2.3x, `hip-cloverleaf-flux` 17.5x to 3.1x, `spirv-execution` 6.9x to 4.5x, `amdgpu-alu` 104x to about 8 to 13x. `amdgpu-alu` compiles a fresh straight-line kernel per operation, so it disassembles every instruction once per operation; a real profile pays that once per static instruction.
- Split `s_waitcnt_*` forms: the RDNA3 ISA (16.2, SOPK) defines the wait as `counter <= S0.u[w-1:0] + S1.u[w-1:0]` compared in `w` bits without clamping, `w` = 3 for EXPCNT and 6 for the others; the reported count now wraps the same way. GFX1250 `s_wait_xcnt` and `s_wait_asynccnt` are 6 bits at shift 0, as upstream LLVM `AMDGPUBaseInfo.cpp` (`getXcntBitWidth`, `getAsynccntBitWidth`) states.
- Lint findings in `Dots.scala` (method value) and `SASSModel.scala` (a doc comment orphaned since it was written) fixed; scalafix is clean on every module touched.

## Known gaps

- One `spirvVm/testOnly *` run (2026-11-02) crashed the forked JVM: SIGSEGV reading offset `0xc` from null inside C2-compiled `spirv.VMInterpreter` lambda code under `IntegerOpSuite`, 3 s into the fork, right after deoptimisations on the same thread (OpenJDK 25.0.4.1 Fedora build, no newer build available). A null field load in compiled code should raise a `NullPointerException`, so the crash points at C2. Eight runs in total, one crash. Report at `/tmp/gemstone-profile/final/hs_err_pid1226663.log`, core in `coredumpctl` (pid 1226663).
