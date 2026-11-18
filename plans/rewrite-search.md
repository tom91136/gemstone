# Rewrite search

Status: proposed, not started. It depends on `static-pipeline.md` for its cost and on `rewrite-equivalence.md` for correctness.

## Goal

Find cheaper equivalent instruction sequences for short windows of compiled SASS or AMDGPU code on a chosen target. Search comes first. Reinforcement learning comes later, and only if search shows rewrites worth finding that search alone finds too slowly.

## Why search before RL

- A learner optimises its reward, and the reward is a model. The static pipeline model is a set of bounds and estimates, some of them `Estimate`. A learner finds where the model undercharges, such as an unmeasured pipe or an estimated latency, and reports gains the hardware does not have.
- Search with an exact correctness check and hardware validation shows how large real gains are, and where the model is wrong, before training on it.
- Search needs the same parts RL needs: an action space, a correctness check, a cost, and a way to emit code. Building them for search is not wasted.

## Parts

- **Windows:** straight-line sequences inside a basic block (`static-pipeline.md` step 1), with live-in and live-out registers from `RegisterUse` and liveness.
- **Moves:** replace an opcode, swap operands, insert or delete an instruction, or change an immediate. Each must be legal for the target, which the generated instruction catalogues decide, as the explorer already does.
- **Correctness:** the differential filter rejects most candidates cheaply. The SMT check proves the survivors.
- **Cost:** the static pipeline model for the block containing the window, with cycles per iteration as the objective, under the occupancy the kernel already has. A rewrite that changes register count also changes occupancy, which the cost includes.
- **Emission:** the SASS and AMDGPU encoders, which round-trip in tests. After a rewrite, the SASS control words (stall counts, barriers, yield) and the AMDGPU `s_delay_alu` and `s_nop` must be regenerated. That is a scheduling problem of its own and the largest risk in this plan.

## Steps

### 1. Superoptimiser

- Windows: `Liveness` and `Windows` in `core/.../flow` give static live-in and live-out sets and the windows of up to eight instructions, and `AMDGPUWindows` keeps the instructions the equivalence checker admits by their own form (`AMDGPUForms`, see `rewrite-equivalence.md` step 3), carries through VCC and loads and stores of global memory among them. A window that holds a store has memory live out whatever registers it leaves live, so it is never deleted as dead; `s_waitcnt` has no rendering and ends a window, so compiled code seldom has a load and the first use of its result in one window. The live sets carry VCC, SCC, EXEC and M0 as well as registers; a window stops at an instruction that writes EXEC, and no write ends the live range of a vector register. AMDGPU is done on gfx906 fixtures and on a gfx1036 code object: admission asks the checked cases of the target's ISA (`AMDGPUGenerator.checked`), so gfx1036 takes gfx1030's.
- SASS: `RegisterUses.of` in `isa/nvidia` gives the slots and condition flags a lowered VMIR instruction reads and writes, and `StaticRegisterUse` in `isa/sass-vm` names them `R`, `UR`, `P` and `UP` per decoded instruction through `VMAssembler.source`, with a guard as a read and a guarded write also a read so it never ends a live range; `SASSWindows` admits each instruction by its own form (`SASSForms`), constant-bank reads and `LDG` and `STG` among them, tested on the sm_89 `divergentSm89` fixture: 23 windows of up to eight instructions, three of them through a load or the store, where admission by mnemonic gave 20.
- Moves: `AMDGPUMoves` proposes one-move candidates of a window (opcode replacement within an operand-signature class from the catalogue, source swap, delete, insert, immediate change) from a seeded random source; each candidate round-trips through the encoder and decoder, holds only instructions `AMDGPUForms` admits, reads only live-in or earlier-written registers, and writes no live-out register the window does not write. AMDGPU only, on any target whose ISA has checked cases.
- Memory in moves: one rule in `Moves` (`ordered`) serves both proposers. A window's loads and stores are kept as they are and in their order: no move changes, deletes, reorders or creates one, and the other moves work around them. Whether two accesses may change places is for the proof, which tells apart addresses that may alias. On AMDGPU a load is itself by its text, and no candidate reads a register a load of the window wrote, since the `s_waitcnt` that makes it readable is outside the window. On SASS a reader waits on a load through its own control word, so a candidate may read what a load wrote, and control-word regeneration (step 2) owes it the barrier.
- SASS moves: `SASSMoves` proposes the same five kinds on sm_89 windows, sharing the kinds, seeded choice and register-effect filter through `Moves`. Each candidate round-trips through the sass-asm encoder and decoder, holds only instructions `SASSWindows.admitted` accepts, and obeys the same read and live-out rules. Kept instructions keep their decoded encoding word and scheduling in the model; changed and inserted ones carry neither, and the canonical encoder writes control word 0x7e0 for every instruction. Emitted bytes therefore do not preserve the original control words, so candidates serve for costing and proving, not emission, until control words are regenerated. A SASS load or store is itself only as decoded, with its encoding word: the canonical encoder writes URZ into the descriptor field of the `memdesc` forms of `LDG` and `STG`, and the rendered text the round trip compares does not show that field. The moves therefore keep each one as decoded and create none, and an emitter must write a kept load or store from its decoded word, changing only the control bits it regenerates, never from the canonical encoding of its text.
- Stochastic search over windows of up to about eight instructions, in the style of STOKE: MCMC over moves, with a cost of correctness distance plus model cycles.
- Search: `RewriteSearch` in the semantics-check main sources walks one-move candidates of a window with a seeded Metropolis rule over a cost of changed live-out bits plus cycles per iteration; every distinct candidate that changes no live-out bit and costs fewer cycles goes to `Proof`, and only a proved one is reported with the weakest timing source behind its gain. A window that leaves none of its writes live out has nothing to preserve, so the search evaluates one candidate, the deletion of the whole window, and reports it with the reason `no write is live out`. The search samples the move set, so a window with no rewrite is coverage, not a proof.
- Cost: `AMDGPUBlockCost` has one path, the block analysis of the static pipeline view. For each candidate it writes the kernel's assembly with the window replaced, re-targets the branches that cross the window, assembles it and replaces the kernel in the code object, then reads the VM's own static view of that code object (control flow, register use with implicit state, pipe, planned cycles, offsets) and records it with `StaticProfile` as one wave issuing the window's loop or block once with every lane active. `BlockAnalysis.of` gives the cycles per iteration and their weakest source. `HardwareValidationSuite` records its rows through the same `StaticProfile`.
  - Addresses come from the new layout, so a rewrite that moves a loop into or out of one fetch line is charged the redirect that layout gives. Code after the window also moves; each report line says by how many bytes, and the cost does not include what that does to later loops.
  - Occupancy comes from the kernel descriptor's register count, changed by the granules the rewrite adds or frees over the whole kernel, and from the launch the caller states (workgroup lanes, shared bytes, placement).
  - Not costed, and reported with the reason instead of an estimate: a target with no probed model of its own (gfx906), a loop or block that holds a memory instruction (no recorded traffic), a loop with more than one path (no recorded issue counts), a size-changing rewrite of a kernel with a program-counter-relative instruction other than a branch, and a region the analysis withholds.
  - Not modelled: control words a rewrite would need (`s_nop` hazards on gfx1036), so a rewrite that creates a hazard is undercharged.
  - On the gfx1036 fixture `ctl_f4_tail`, the loop of that `HardwareValidationSuite` row, the unmodified block costs the 29.0 cycles the static view gives the row (29.01 measured). `ctl_f4_dead` adds a dead `v_mov_b32` to the loop; the default configuration proves its deletion at 29.00 from 30.00 cycles, weakest source `estimate` (the planned issue cycles are LLVM's). In the compiled `square_plus_one`, ten of the fifteen windows sit in a loop or block that reaches memory and are reported as not costed; the other five give no proved rewrite at seed 1, budget 3.
  - A recorded launch of the original kernel would supply memory traffic and issue counts for the regions refused above. The search does not take one yet.
  - SASS needs a search driver, and for this cost path the VM's static pipe and `Schedule.Encoded` of a rewritten instruction. The stall and barrier are part of the SASS encoding, so a candidate cannot be costed before step 2 regenerates its control words. sm_89 has no fetch-line fields, so its cost has no layout term.
- Runner: `RewriteSearchRunner` in the same package takes `<code-object> <kernel> <target>` and optional `--seed`, `--budget`, `--temperature` and `--window` flags, with the launch as `--lanes` (default one wave), `--shared` and `--placement`, and prints one line per window and one per proved rewrite with its cycles before and after, the weakest source behind them and the bytes the later code moves.
- Run it on the benchmark cases and conformance kernels. Report each proven rewrite with its model gain and the weakest source behind that gain.

### 2. Control-word regeneration

- AMDGPU first, where LLVM's model gives the delays: recompute `s_delay_alu` and `s_nop` for a rewritten block, and test against LLVM's output on the same sequence.
- AMDGPU done for straight-line blocks: `ControlWords` regenerates `s_delay_alu` as `AMDGPUInsertDelayAlu` does, from delay types, wait classes and tied operands that codegen takes from LLVM's instruction records into `scheduling.json`, and `s_nop` for three `GCNHazardRecognizer` rules (`s_setreg`/`s_getreg` repeats, a VMEM read of a VALU-written SGPR, a VALU overwriting a wide store's data) on GFX9 and earlier only, since LLVM skips them on GFX10 and later. `ControlWordsSuite` matches LLVM on all 76 compiled blocks (gfx906, gfx1036, gfx1100, gfx1150, gfx1200). Not covered: VOPD pairs, DPP and SDWA forms, FPAtomicToDenormMode (no fixture reaches it), and every other hazard rule (VALU trans use, `v_div_fmas`, readlane, LDS, MFMA).
- gfx1036 (GFX10, no-data-dependency model): no hazard rule that applies has a reaching fixture, so none is ported for this target.
  - Applies, not ported: FPAtomicToDenormMode. `checkFPAtomicToDenormModeHazard` (`GCNHazardRecognizer.cpp:2544`) runs from `PreEmitNoopsCommon` at line 360, before the early return at 362-363. Feature `hasFPAtomicToDenormModeHazard` (`GCNSubtarget.h:597`) is true for GFX10. The window closes after three wait states, at any VALU, or at any waitcnt (`IsExpiredFn`, lines 2561-2565). The gfx1036 probes `denorm/fpC` and `denorm/fpm3` place an FP atomic before a mode change, but each has a VALU in the gap (fpm3 also has a waitcnt), so the rule emits nothing. `denorm/fpA`, `fpB` and `fpD` have no mode change. The explicit FP add probes reached nothing: the FP add forms do not assemble on gfx1036 (`probe/fpatomic/assemble.log`).
  - Applies to GFX9 and earlier only, gated off on gfx1036 by the early returns at 219 (`getHazardType`) and 362-363 (`PreEmitNoopsCommon`): byReg (`checkSetRegHazards`, line 926, `getSetRegWaitStates`, `GCNSubtarget.h:296`), byVmem (`checkVMEMHazards`, line 843, feature `hasVMEMReadSGPRVALUDefHazard`, `GCNSubtarget.h:289`), byStore (`createsVALUHazard`, line 938, used at 1001 and 1025), RFE (`checkRFEHazards`, line 1348, feature `hasRFEHazards`, `GCNSubtarget.h:293`, called at 396), and ReadM0 (`checkReadM0Hazards`, line 1363, called at 261 and 406).
  - Off by feature test on gfx1036: NSAtoVMEM (`hasNSAtoVMEMBug`, gfx101x set), VcmpxPermlane, VMEMtoScalarWrite, SMEMtoVectorWrite, VcmpxExecWAR and LdsBranchVmemWAR (gfx101x set), SMRD VALU def (`hasSMRDReadVALUDefHazard`, `GCNSubtarget.h:283`, Southern Islands only), VALU trans use (GFX11 sets), WMMA coexecution (GFX12.5 sets), the `v_div_fmas` forms (`checkDivFMasHazards`, line 898, needs the GFX940 forwarding features), soft clause (XNACK off on gfx1036), LdsDirect (GFX11 and later), and every post-RA fix in `fixHazards` (lines 1379-1404).
  - Not checked function by function for gfx1036 (unverified): `checkDPPHazards` (867), `checkGetRegHazards` (913), the uniform-window and SOFFSET-window helpers (993, 1009), `checkVALUHazardsHelper` (1046), `checkVALUHazards` (1136), `checkInlineAsmHazards` (1265), `checkRWLaneHazards` (1326), the LDS checks, and the MFMA family (`checkMAIHazards`, line 2572). gfx1036 has no MAI, and no compiled gfx1036 block has a DPP form.
  - SDWA: `conv2.gfx1036.o` line 16 has `v_cvt_f32_i32_sdwa`. Its bytes regenerate exactly, and no ported rule applies to it.
- SASS next. Derive stall counts and barriers from the latency table that `static-pipeline.md` step 3 builds, and check against ptxas on regenerated code. Until this passes for a target, rewrites on that target are reported, not emitted.

### 3. Hardware validation

- On the sm_89 and gfx1036 cards, run the original and the rewritten kernel and time both, with conformance cases as the correctness check in context.
- A rewrite counts as a gain only when the hardware confirms it. Disagreements between the model and the hardware go back to `static-pipeline.md` as calibration cases.

### 4. Learning, if step 3 justifies it

- Learn a proposal distribution for the search (which move, where), trained on the rewrites search proved and hardware confirmed, rather than a policy that emits code directly.
- The reward stays the proven-correct model gain. Hardware-confirmed results form the held-out set. A target whose model is mostly `Estimate` is excluded from training.

## Non-goals

- Rewriting PTX or SPIR-V. Gains there depend on a compiler the tool does not run.
- Register allocation and instruction scheduling across blocks.
- Shipping a compiler pass. The output is a report and rewritten binaries for study.

## Open decisions

- Window size and the move set per target.
- Whether to stop after step 3 if real gains on compiler output are rare. Recommendation: decide from the step 3 numbers.
