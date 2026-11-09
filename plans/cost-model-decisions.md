# Cost model: decisions for the user

Collected during the autonomous drive that started on 2026-11-03. Each item is a recommendation to approve or veto.

## Observed AMDGPU benchmark cost

`amdgpu-alu --observe` remains about 8 times slower than plain after the disassembler fixes, which gave about 1.1 times (operand-count table memoised per opcode, no cats `traverse` per operand, one schema computation). The benchmark compiles a fresh straight-line kernel and attaches a fresh recorder for every sample, so each sample pays the first sighting of every static instruction: interning its location and rendering its text. A real profile of one launch pays that once per static instruction.

Recommendation: accept. The alternative is a per-program text cache that outlives the kernel and recorder. It would make the benchmark measure something a real profile never does, and it would add cross-launch state to the VM.

Two further decoder attempts on 2026-11-06 gave no measurable gain and were discarded: array-indexed layout field lookups, and one `Field.get` per field shared between `fields` and `mods` in `decodeOperands`. JFR shows the cost spread across `EncodingField` map hashing, per-candidate `fields` maps and `BigInt` construction, `ClassTag`-driven copies, and the `encodeFields` re-encode filter in `InstructionCodec.decode`. A gain would need a different representation of encodings and fields in `decode`, which was not tried.

## `CodeObjectKernel` equality suppression

Removing the two unused `scalafix:CaseClassEquality` suppressions on `readOnly` and `globalMemory` exposed bonsai `unstable-equality` warnings, so the suppressions now name `bonsai:unstable-equality`. The existing comment still holds: kernels are never compared, and the arrays back VM memory without copying.

Recommendation: accept. Making the fields an immutable wrapper would copy the code object images on load.

## sm_89 findings report `documented`

Each finding now names the weakest source its cycles rest on. Sector and bank-conflict findings on `sm89` show `documented`, because the sector size, line size and bank layout come from the CUDA guide, even though the rates themselves were probed. Only idle-lane findings show `probed`.

Recommendation: accept. This is the rule the plan set out: a finding is only as trusted as its least trusted input.

## ptxas latency harvest stays out of the build

Step 2 decodes the scheduling fields from each SASS instruction, so planned issue cycles need no table. The harvested ptxas table (1008 rows, `sm_35` to `sm_121`, ptxas 11.8.89 and 13.3.73) has no consumer yet. Its generator reproduces it byte for byte from the pinned containers. Turning it into an `sbt codegen` output would make codegen fetch CUDA packages, which is a new build-system rule.

Recommendation: keep it out of the repository until a consumer exists, such as a PTX cost fallback or a PTX-to-SASS mapping. The generator and table are in `/tmp/gemstone-cost/harvest/sass-schedule/`; at the end of the drive they move into the Nextcloud probe kit.

## bonsai `NamespaceImports` no longer flags test imports

On 2026-11-06 `<module>/Test/scalafix --check` passes for `core`, `coreJS`, `coreNative`, `ptxVm`, `sassVm`, `explorer`, `explorerJS`, `testSupportJS`, and `spirvVm` on branch `f3-cost-model`, with no `NamespaceImports` finding on `import net.kurobako.gemstone.testing.Suite`. The 304-file finding recorded on 2026-11-03 does not reproduce with the bonsai build on this host, so no test files changed for it.

## ncu version in `ProfileGoldenSuite`

`ProfileGoldenSuite.scala:15` names ncu 2026.2.1. The step 2 source-counter probes ran under ncu 2026.1.0.0 on this host, so that header was not confirmed.

Recommendation: confirm the version that recorded the original golden rows, from the probe kit `NOTES.txt`, and correct the header if it differs.

## AMDGPU issue cycles: LLVM's throughput rule and LDS key

Approved 2026-11-06.

Issue cycles sum `ReleaseAtCycles` per resource, divide by `NumUnits` and take the maximum over resources, as LLVM's subtarget emitter merges a sched class's write resources. That matches llvm-mca on every probed row, including `v_swap_b32` on gfx906 (2), which a per-entry maximum gets wrong (1).

LDS-DMA loads share a mnemonic with the plain load. Timing keyed by mnemonic alone gave both forms the plain cost. Timing is now keyed by mnemonic and form. A record is in the LDS form when its assembly string has an `lds` token. The VM reads the form from the `lds` modifier on the decoded instruction.

A mnemonic with plain and LDS records is keyed in each table. An LDS-only mnemonic is keyed in the plain table under its own name, and `timing` falls back to that plain row on an LDS-table miss. `buffer_store_lds_dword` is LDS-only. LLVM has no GFX10 record for it, so gfx1030 has no timing, and the gfx906 row stands in for the check.

llvm-mca confirms 10 gfx1030 LDS rows (`global_load_*` and `scratch_load_*`) at 2 cycles, against 1 for the plain forms. llvm-mca hangs on the gfx1030 buffer-load LDS form, so those keys have no llvm-mca cross-check, though llvm-mc assembles the form. No LDS record in the LLVM 23.1.0 dump carries a GFX11 or GFX12 predicate, and llvm-mc rejects the gfx1100 buffer-load LDS spelling. gfx1100 has no LDS keys.

The change moves 22 `buffer_store_lds_dword` processor entries from the LDS table to the plain table. The 22 processors are GFX8 and GFX9 targets, not only the gfx906 family. The committed file holds 602 LDS processor entries from 224 LDS table entries in 19 of 28 tables. The regenerated file holds 580 from 216 in 19 of 28.

Open, not in this change: the gfx1030 `global_load_ubyte` saddr encoder rejection, and the LDS spelling of global loads, which is unresolved.

## AMDGPU scheduling generator without Python

`GenerateAMDGPUSched` no longer embeds a Python exporter. It runs `llvm-min-tblgen --print-records` into a temporary dump, which `AMDGPUSchedRecords` reads one line at a time, keeping only the scheduling classes and fields it uses. The dump is deleted afterwards.

The former `--dump-json` route peaked at 20.6 GiB resident (21635412 KiB, 29 s wall clock) over a 574 MB JSON file. `--print-records` peaks at 1.35 GiB (1415788 KiB, 10 s).

`GenerateAMDGPUISA` no longer passes `--python` to the scheduling generator. Codegen still runs `python3` for the ACO model, which is Python source. Identical reruns give identical bytes.

## Wave64 doubling on RDNA covers VALU only

RDNA issues a wave64 VALU instruction in two passes on a SIMD32. Scalar, memory, LDS, export and branch instructions issue once per wave. The first cut doubled everything.

Recommendation: accept. Revisit VMEM and LDS address throughput when step 5 probes give a measurement.

## Profile stream format 4

The NVIDIA and AMDGPU schedules both entered the stream in this uncommitted change, so the stream moves from 3 to 4 once, not twice. The report schema moves from 2 to 3 once.

The per-instruction compute pipe (`Pipe`, optional per instruction) joins format 4 for the same reason. Neither format has shipped, so the pipe adds no version bump.

## CI reds on `8e24c88a`

CI failed on the pushed commit: one lint hit, `BenchmarkProfileSuite` asking for fixtures CI cannot build, and two CUDA conformance regressions on every target. The working tree fixes all four. Evidence and notes are in `/tmp/gemstone-cost/ci/`.
- PTX `control/cuda/early-exit` hung. The reconvergence plan put a join in front of the bare `ret` that an early return branches to, so the returning lanes parked there while the rest waited for them at `bar.warp.sync`. A branch whose join block only returns, exits or traps now gets no join, as ptxas emits a plain `EXIT`.
- SASS `atomics/cuda/integer-operations` summed 1604 instead of 8256 in each block's add slot, while the grid-wide sums were correct. Cohorts interleaved through uniform-register code, so the following `VOTEU`, `UFLO` and `REDUX` ran on partial cohorts. `ExecutableProgram.runsAhead` (`e82f8b79`) prevents that interleaving: with it forced to `false`, each block's add slot reads 1604, and with it on, 8256. The `YIELD` drain was not the cause. The owed-lane workaround that first hid the sum is removed in `70a586bd`.
- Fixture-backed benchmarks now report skipped when `generateBenchmarkFixtures` has not run, as the codegen suites do for missing generated inputs.

Decision: the user approved on 2026-11-06 keeping the immediate `YIELD` drain, which matches hardware, and dropping the owed-lane design. The sm_89 probes (RTX 4060 Ti, CUDA 13.3 `nvcc` and `ptxas`) showed:
- A lane that executes `YIELD` leaves the pending convergence barrier at once, and `BSYNC` does not wait for it. The earlier premise that `BSYNC` waits for yielded lanes is false.
- `NANOSLEEP` does the same. A sleeping lane leaves the barrier, and `BSYNC` completes with one lane per mask.
- A compare-and-swap contended counter and a spin lock reach the hardware values with the immediate drain (496 and 2016 for the spin lock).
- A counted loop containing `YIELD` does not reconverge after the loop.

Applied in `20f20db8`: SASS `YIELD` lowers to `Yield`, which drains lanes from every convergence barrier of their warp at once. PTX already lowers `yield` to `Yield`, so both front ends share the rule. The approval of 2026-11-06 covers lowering `NANOSLEEP.WARP` to the immediate drain. No probe kernel produced `NANOSLEEP.WARP`, so that form's drain on hardware is unmeasured. Its lowering follows by analogy.

The user approved the same lowering for the unmodified `NANOSLEEP` form on 2026-11-06. It is what ptxas emits for `__nanosleep`, and the probes measured it. With one sleep before the join, the odd lanes leave the barrier and finish as `__activemask()` 0xaaaaaaaa, and the even lanes finish as 0x55555555. Without the sleep, all 32 lanes reconverge at 0xffffffff. The other sleep shapes (a store before the sleep, two sleeps, a store either side, a shorter sleep) gave the same split, three runs each. `Pause` and `pauseLanes` are removed, as nothing produces them after this change.

While testing the guarded and unmodified forms, the emulator merged yield-drained lanes back with lanes still in the barrier at the same PC, so they reconverged at `VOTE`. `VMInterpreter` now keeps drained lanes in their own partition for that step. The guarded-`YIELD` fixture (`guardedYieldSm89`) was written by hand from the sm_89 shape. It was not measured on hardware.

PTX `nanosleep.u32` lowers to `Yield` in ptxVm, as unmodified SASS `NANOSLEEP` does, under the 2026-11-06 approval. `compiler nanosleep PTX lanes that sleep before a join leave the barrier at once, as on sm_89` in `CompilerExecutionSuite` pins the probe masks: 0xaaaaaaaa for odd lanes and 0x55555555 for even lanes. Before the change, every lane reached 0xffffffff. The existing PTX suites pass unedited, and `Reconvergence.scala` needed no change.

The conformance cases were not rerun locally. The integers reproduction is the test `integer atomics and CAS loops count each lane once per block and per grid` in `ControlFlowSuite`, which loads the `integersSm86` fixture. The probe passed the same listing under the immediate drain.

## Architecture audits

The user approved every audit recommendation on 2026-11-07. Evidence is in `nvidia-report.md`, `nvidia-report-2.md`, `amd-report.md`, and `amd-report-2.md`, kept with the probe kit.

A value whose cited document nobody could read is an `Estimate` naming that document, even where the value is probably right. Downloading the document upgrades it.

`gfx1251.global` stays `Inherited` from the `gfx1250` `Estimate`, not `Estimate` as audit row W7 asked, because `derived` tags every derived chip `Inherited`.

`gfx906.fp64` is an `Estimate`. The second AMD audit read a Wikipedia row that agrees with it, but not the primary source.

## Divide pipe classification differs by ISA

Integer and float divide differ across ISAs in pipe classification. SPIR-V classifies integer divide (`SDiv`, `UDiv`, `SMod`, `UMod`, `SRem`) as `Alu`, and float divide (`FDiv`, `FRem`, `FMod`) as `Mufu`, or `Fp64` at 64 bits. NVIDIA gives neither a pipe, because its divide is a multi-instruction sequence, so its cost sits in the instructions it expands to.

## sm_89 rates

The `SHF` rate of 60.2 lanes conflicts with the 128 lanes that BPG 13 Table 5 gives for `add.s32`. The conflict stays unresolved because ptxas folds `add`.

The FMA and FP64 shortfalls on sm_89 are open. The loop-overhead and SM-pair hypotheses were refuted.

Warp loads on sm_89 run at 0.5 per SM clock for up to 8 bytes per lane, and at 0.25 per SM clock for 16 bytes per lane.

## Values kept as cited

Volta `l1` 28 cites a Turing table, and no source read confirms the Volta figure. Recommendation: tag it `Estimate` naming that table, under the rule in Architecture audits. Approve or veto.

sm_75 `dram` keeps the T4 value of 296. An unnamed Turing figure of 434, with caching prevented, disagrees with it. Recommendation: keep 296. Approve or veto.

`gfx1150` tensor values stay on `gfx1150`. Inheriting them from `gfx1100` would be circular.

## Limits the cost model accepts

Recommendation for each limit below: accept it and state it in the report. Veto any item to change the model.

SPIR-V charges an issue for structural instructions such as `OpLabel` and `OpFunctionParameter`. They produce no hardware instruction, yet they tie with real work at the top of `spirv-execution`. SPIR-V and PTX costs describe the VM's instruction stream, not compiled code.

AMDGPU `s_load` records `Space.Global`, so its path floor uses a vector or global tier, although the access goes through the scalar cache.

SPIR-V `Uniform`, `PushConstant`, and `UniformConstant` reads record `Space.Constant`, so they charge no load terms. Non-uniformly indexed uniform buffers go through the load path on hardware, so they are undercharged until the VM reports whether an index is uniform.

A parent's generic read of its own child-launch parameter buffer records `Constant`, and the VM cannot tell it from `ld.param` by address.

## amd.com documents to download

The sources report, kept with the probe kit, lists in section 4 the amd.com documents to download by hand. Downloading a document upgrades the values it backs, under the rule in Architecture audits.

## Generic aliases

`gfx9` and `gfx12` stay aliases of `gfx900` and `gfx1200`, with no code change. Recommendation: approve. Veto to add the generic names to `Target`.

LLVM 23.1.0 `gfx12-generic` differs from `gfx1200` only by `FeatureRequiresCOV6`, a code object requirement the catalogue does not model. Timing and decode do not need it, so the alias already behaves as the generic does. `gfx9-generic` omits `FeatureMadMixInsts`, so the `gfx9` alias decodes `mad_mix` forms that `gfx9-generic` lacks. Code built for `gfx9-generic` never contains them.

Code objects with a generic mach are a separate gap. `Target` rejects `gfx9-generic`, so an ELF with mach 0x51 fails with an unsupported processor target error.

## Output review notes

- `Kernel.assembly` falls back silently to the IR's `toString` when decoding fails, and the kernel then has no schedule. Surfacing decode failures is open.
- An unused `SRC2 = 255` (the literal marker) on VOP3 makes the decoder consume a literal dword. The re-encode is shorter, so the candidate is rejected. Canonical encodings are unaffected.
- `nvidia-control-flow --isa sass` builds its kernel for SM80 and shows IR text with no planned cycles, whatever `--architecture` says. `--architecture` selects the cost table, not the kernel. Real SASS kernels show SASS text, as `ProfileSuite` asserts.
- On GFX6 and GFX7, `llvm-mc` assembles `v_readlane_b32 s4, v2, 3` to a VOP2 encoding that `Binary` does not decode, so that form falls back. The Gemstone encoder emits VOP3, which decodes. The VOP2 semantics are unverified, and `v_writelane_b32` and the SGPR lane-select forms are unmeasured.
- The LLVM oracle corpus holds only encodings the Gemstone encoder also produces, so it cannot find a decode gap against `llvm-mc` output. Finding those needs compiled code objects.
