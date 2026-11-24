# Cost model coverage

Status: steps 1, 3, 4 and 6 landed in `e8b1f15c`; step 2 now reaches the cost through regenerated control words (`c612f1cf`, `cc249a04`, `fe00ae06`) and probed producer latencies (`ada650da`, `2cf2a8d3`, `7b46fa49`, `ef6bb864`, `3f41d428`), with the ptxas harvest kept out of the build; step 5 has sm_89 and gfx1036 anchors, but no framework16 or 3900X probe is recorded in the repository. The same model backs rewrite search's recorded-launch costing and its noise margin (`cdf3a2d6`, `90a505f4`, `f56a5097`, `d837f3ad`). This is F3 of `performance-profile.md`. F4, the timing replay, stays deferred and will reuse these tables. The profiling work it builds on is in commit `8e24c88a`, and `performance-profile-resume.md` records its state.

## Goal

Every target the decoders know gets a cost for each instruction, and each value names where it comes from. Hardware we own calibrates one target per family. Values for the other targets come from compilers, documents and published measurements, and the report says so. No target silently borrows another target's numbers.

A cost has three parts:
- Issue cost: the cycles the compiler plans between an instruction and the next.
- Latency: how long a variable-latency result takes (global, L2, shared or LDS, MUFU or transcendental, texture, tensor).
- Throughput: what one multiprocessor serves per cycle on each pipe (FMA, ALU, MUFU or transcendental, FP64, tensor, load-store, shared).

For each instruction, the report takes the larger of the per-warp path cost (issue plus waited latency) and the multiprocessor bound (throughput), and says which of the two binds.

## Principles

- Values are `Sourced`. Two new kinds join `Documented`, `Probed` and `Estimate`:
  - `Published(citation)`: a third-party measurement, cited per value;
  - `Inherited(from, basis)`: a value measured on a sibling, with the reason it carries over, for example "same SM design per the Ada whitepaper".
- Each finding shows its weakest source, so a reader can tell a measured cost from an inherited one.
- Coverage is derived, not listed by hand. A test enumerates the targets the SASS and AMDGPU decoders support, and fails when any target lacks a fully sourced `Architecture`. Siblings are built with copy-with-overrides from their anchor, as `gfx1036 = gfx1030.copy(...)` is today.
- Compiler-derived tables are generated into resources by `sbt codegen`, like the ISA catalogues, and pinned to the toolchain version that produced them.
- Probe results become committed tests (inline vectors plus a compact golden corpus). Tarballs stay in the Nextcloud probe kit.

## Sources by tier

| Tier | NVIDIA | AMDGPU | Covers |
|---|---|---|---|
| Compiler | Stall count, yield and scoreboard fields that ptxas writes into each SASS instruction | LLVM scheduling models (`SISchedule.td` and the per-subtarget models), plus `s_delay_alu` and `s_nop` in compiled code | Issue cost; which operations have variable latency |
| Documented | CUDA C++ Programming Guide arithmetic throughput and resource tables (every compute capability), tuning guides | RDNA and CDNA ISA guides and whitepapers, CDNA MFMA pass counts | Throughput, limits, banks and sectors |
| Published | Microbenchmark dissections (for example Jia et al. for Volta and Turing, Luo et al. for Hopper) | Published RDNA and CDNA latency measurements | Memory latencies on targets we cannot run |
| Probed | RTX 4060 Ti locally; framework16 sm_120 | gfx1036 locally; framework16 gfx1150; 3900X box gfx803 and gfx906 | Calibration of one anchor per family |
| Inherited | Siblings of an anchor (sm_86 and sm_87 from sm_89; sm_121 from sm_120) | gfx1030 to gfx1035 from gfx1036, the RDNA3 parts from gfx1150, GCN and CDNA1 from gfx906 | Remaining family members |
| Estimate | Whatever is left, with its basis stated | Same | Nothing silent |

Targets with no anchor are Hopper and datacenter Blackwell (sm_90, sm_100, sm_101, sm_110), CDNA3 to CDNA5 and RDNA4. They rest on the compiler, documented and published tiers. Renting time on such hardware is a later option, not part of this plan.

## Steps

### 1. Provenance and coverage

Done in `e8b1f15c`: `Source.Published` and `Source.Inherited`, weakest-source reporting in the text and JSON lines, and a coverage test over every target in `Architecture.all` (`ArchitectureChecks.scala`). Portable profiles require `--architecture` (`8e24c88a`). No open item is recorded for this step.

- Add `Source.Published` and `Source.Inherited`.
- Each report finding names its weakest source, in the text summary and in the JSON lines (schema bump).
- Add the coverage test: one `Architecture` per decoder target, every field sourced. It starts red for the targets that have no table yet. Do not mark it ignored or expected to fail. Land it together with step 4, or have it check only the targets that step 4 has reached, using a rule derived from the target list, not a hand-kept allow-list.
- SPIR-V and OpenCL profiles take an explicit `--architecture` in the explorer too, replacing the implicit `sm89`.

### 2. NVIDIA issue cost from ptxas

Done: ptxas's planned stalls reach the cost as regenerated control words (`c612f1cf`, `cc249a04`, table moved out of generated in `fe00ae06`), and variable-latency producers carry probed rows in `NvidiaLatency` (`ada650da`, `2cf2a8d3`, `7b46fa49`, `ef6bb864`, `3f41d428`), so no harvested table is a build input. Open: the 1008-row harvest stays outside the repository until a consumer exists, and the ncu version named by `ProfileGoldenSuite` is unconfirmed (`cost-model-decisions.md`).

- Decode the scheduling fields in `isa/sass-asm`: `req_bit_set` (wait mask), `src_rel_sb` (read scoreboard), `dst_wr_sb` (write scoreboard), `usched_info` and `opex` (stall count and yield), `batch_t` and `pm_pred`. `NVDisasm.scala:102` treats these today as aliases that do not change the instruction. Keep that rule for instruction identity, but expose the values on the decoded instruction. Kepler keeps the fields in a control word for each bundle of 7 instructions, Maxwell and Pascal in a word for every 3, and Volta onwards inside each 128-bit instruction; `CodeWords` already separates these layouts.
- First, confirm how `usched_info` and `opex` encode the stall count on sm_89. Decode the eight nvdisasm fixtures in `isa/sass-vm/.../fixtures/ProfileFixtures.scala` and check the values against the ptxas output for the same kernels.
- Harvest ptxas's latency table. Generate dependent-chain kernels for each instruction class (integer and FP32 ALU, FP64, MUFU, conversions, shared and global loads, atomics, tensor operations where the target has them). Compile them with ptxas for every `sm_XX` the decoder supports, decode the producer and consumer pair, and record:
  - the stall count for fixed-latency operations;
  - for variable-latency operations, that they set a scoreboard.
- CUDA 13 no longer compiles for Maxwell to Volta, so those targets use a CUDA 12.x ptxas in a container. Check what the toolchain env registry provides, and pin the versions in the generated data.
- Per warp, issue cost is the sum of the planned stalls over the issues the profile recorded, so divergence and trip counts come from the stream.
- Validate on sm_89: ncu with `--set source` gives per-SASS-instruction executed counts and stall samples, which become a golden suite like `ProfileGoldenSuite`.

### 3. AMDGPU issue cost from LLVM

Done in `e8b1f15c`: scheduling data comes from `llvm-min-tblgen --print-records` rather than the Python exporter (`cost-model-decisions.md`), is cross-checked against llvm-mca, and stays `Estimate` where LLVM is the source; `s_delay_alu` and hazard `s_nop` regeneration landed in `663ad41d`, with wave64 doubling limited to VALU (`cost-model-decisions.md`). Open: the gfx1030 `global_load_ubyte` saddr rejection and the LDS spelling of global loads remain unresolved (`cost-model-decisions.md`).

- Generate scheduling data from TableGen (`llvm-tblgen --dump-json` over `AMDGPU.td`, using the tree at `~/llvm_workspace/llvm-project`, with the LLVM revision pinned). Keep each subtarget's SchedModel, its write resources and latencies, and the instruction-to-write mapping. Join that to our opcode catalogue by instruction name, and fail codegen on any unmatched opcode the catalogue holds.
- Cross-check a sample against `llvm-mca` for gfx906, gfx1030 and gfx1100.
- Read compiler hints from the code: `s_delay_alu` on GFX11 and later, and `s_nop` padding. Wave64 on RDNA issues in two passes.
- Values are `Estimate`, citing LLVM, until a probe confirms them.

### 4. Architecture tables for every family

Done in `e8b1f15c`: tables for sm_50 to sm_121 and GFX6 to GFX12, one rate per pipe, and `Published` memory latencies; the architecture audits were approved on 2026-11-07 (`cost-model-decisions.md`). Open: values cited to documents nobody could read and the amd.com downloads listed in `cost-model-decisions.md`.

- NVIDIA: sm_50 to sm_121, from the documented limits (CUDA guide tables and the `cuda_occupancy.h` constants per compute capability) and documented throughput. Banks (32 of 4 bytes) and sectors (32 bytes in 128-byte lines) have been stable since Maxwell, and their source says so.
- AMDGPU: GFX6 to GFX12, CDNA1 to CDNA5, RDNA1 to RDNA4, from LLVM `AMDGPUBaseInfo` (wave limits, VGPR granules, LDS) and the ISA guides. LDS banks and the cache line come from the guides, and the gfx1036 probe anchors RDNA2.
- Pipe throughputs extend `Architecture.Throughput` from the three rates today (sectors, wavefronts, issues) to one rate per pipe.
- Memory latencies come from the published tier where no anchor exists, cited per value.

### 5. Anchors on hardware we own

Done locally: sm_89 (RTX 4060 Ti) rows in `NvidiaLatency` and gfx1036 rows in `Architecture.scala` are `Source.Probed` (`3f24e5e6`, `99c250c2`, `2cf2a8d3`, `7b46fa49`, `ef6bb864`, `3f41d428`), and the card confirmed regenerated control words (`cc249a04`). Open: no framework16 (sm_120, gfx1150) or 3900X (gfx803, gfx906) probe is recorded in the repository; those targets rest on the other tiers.

Published values come first. Hardware runs need the user's agreement each time, and a family gets a probe only where the probe would change a finding.
- framework16: sm_120 and gfx1150. Run the existing `profile/` probes (`occupancy.cu`, `profile_memory.cu`, `profile_memory.hip`, `profile_timing.hip`, and `run.sh` with ncu metrics where ncu supports the card), plus latency chains for each pipe.
- 3900X box: gfx803 and gfx906, the same HIP probes. Check whether rocprofv3 exposes counters there; if not, use timing as on gfx1036.
- Local: add per-pipe latency chains and peak rates on sm_89 and gfx1036.
- Each run becomes committed golden rows. Results stay in the Nextcloud kit (`probes/profile-<host>-<target>-<what>.tar.gz`, with `NOTES.txt`).

### 6. Report

Done: `e8b1f15c` names the binding bound and the weakest source in each finding and ranks findings by estimated cycles; the explorer heat map carries a `cycles` metric and a source column (`Heat.scala`), and `BenchmarkRunner profile` reports the same costs (`8e24c88a`). Open: the limits the model accepts still await user approval (`cost-model-decisions.md`).

- The per-instruction cost is the larger of the per-warp path and the multiprocessor bound, naming the bound and the weakest source.
- Findings rank by estimated cycles across every cause, as today.
- `BenchmarkRunner profile` and the explorer heat map gain a "cycles" metric and a source column.

## Checks for every step

- Build and test as `performance-profile.md` describes: `sbt --client`, `testOnly *` per touched module, `Test/compile` on the JVM, Scala.js and Scala Native variants, `scalafmtCheckAll`, `scalafmtSbtCheck` (the pre-commit hook runs it), and `<module>/scalafix --check`.
- Generated tables are reproducible. Rerunning codegen with the pinned toolchains yields identical resources.
- Hardware-validated values carry a test that fails if the model moves away from the probe.

## Non-goals

- Cycle-accurate timing. That is F4's event-driven replay, and it stays deferred.
- Modelling caches as replacement policies.
- Buying or renting hardware for the targets without an anchor.
- SKU and device performance. The tables describe an architecture. Clock rates, board power, and memory configuration of a particular card are outside the model, and a named card appears only as the source of a value.

## Open items carried over

The user decided on 2026-11-03 to fold these into this plan and to run steps 1 to 4 and these items autonomously, with decisions collected at the end in `cost-model-decisions.md`.

- PTX spin set. Done 2026-11-03: ptxas (CUDA 11.8 and 13.3, sm_70 to sm_90) emits `YIELD` in a loop only for `atom.cas` and `atom.exch`, so `Reconvergence.spins` now matches exactly that (`8e24c88a`); `ld.sc` is not legal PTX. Evidence in `/tmp/gemstone-cost/spin/report.md`.
- Observed AMDGPU cost. Open. `amdgpu-alu` runs about 8 times slower than plain after the disassembler fixes (`e8b1f15c`, `cost-model-decisions.md`), because the benchmark compiles a fresh straight-line kernel every operation, and each one is disassembled for its text. Real profiles pay this once per static instruction. The disassembler path (`asm.Binary.decodeStatementAt`, `InstructionCodec.decode`) is slow in its own right; profile and speed it up.
- Two `spirvVm` scalafix findings predate this work: `ShaderReflection.scala:131` (fold identity) and `DeviceEnqueueOps.scala:9` (namespace import). Done 2026-11-06: both were already absent at base `10fb792d`, and `spirvVm/scalafix --check` and `spirvVm/Test/scalafix --check` pass.
- Possible cleanups that round 2 of the simplify pass deferred. Do each, unless there is a clear code quality reason not to; record any objection in `simplify-feedback.md` in this directory, so review guidance can adjust:
  - Done in `8e24c88a`: one nullable-observer pattern across the three VMs (`LaunchObserver | Null`, with the extension in `LaunchObserver.scala`).
  - Open: pass observation state through `execution` in the SPIR-V VM, instead of the side channel on `VMMemory` (`VMMemory.scala:180` still holds `var observer`).
  - Done in `e8b1f15c`: the `-1` async target is named `LaunchObserver.DefaultTarget`.
  - Resolved 2026-11-06: `Instruction.Wait` stays separate from `WaitCounts`, because `Wait` also covers trace, prefetch and cache-hint instructions; the reason is in `simplify-feedback.md`.
- Intermittent JVM crash. One `spirvVm/testOnly *` run crashed the forked JVM with a SIGSEGV in C2-compiled `spirv.VMInterpreter` code (OpenJDK 25.0.4.1, Fedora). Seven reruns passed; no recurrence is recorded in `git log`. The report and core dump are in `/tmp/gemstone-profile/final/` and `coredumpctl` (pid 1226663). Do nothing unless it recurs. If it does, debug it to the root cause, and leave a write-up and a reducer for the user to read. File nothing upstream.
