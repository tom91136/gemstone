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

- Windows: `Liveness` and `Windows` in `core/.../flow` give static live-in and live-out sets and the windows of up to eight instructions, and `AMDGPUWindows` keeps the instructions the equivalence checker admits by their own form (`AMDGPUForms`, see `rewrite-equivalence.md` step 3), carries through VCC among them. The live sets carry VCC, SCC, EXEC and M0 as well as registers; a window stops at an instruction that writes EXEC, and no write ends the live range of a vector register. AMDGPU is done on gfx906 fixtures and on a gfx1036 code object: admission asks the checked cases of the target's ISA (`AMDGPUGenerator.checked`), so gfx1036 takes gfx1030's. SASS has no static per-instruction register use yet, so its windows and its loop-kernel test wait for that source.
- SASS: `RegisterUses.of` in `isa/nvidia` gives the slots and condition flags a lowered VMIR instruction reads and writes, and `StaticRegisterUse` in `isa/sass-vm` names them `R`, `UR`, `P` and `UP` per decoded instruction through `VMAssembler.source`, with a guard as a read and a guarded write also a read so it never ends a live range; `SASSWindows` admits each instruction by its own form (`SASSForms`), constant-bank reads among them, tested on the sm_89 `divergentSm89` fixture: 24 windows of up to eight instructions, where admission by mnemonic gave 20.
- Moves: `AMDGPUMoves` proposes one-move candidates of a window (opcode replacement within an operand-signature class from the catalogue, source swap, delete, insert, immediate change) from a seeded random source; each candidate round-trips through the encoder and decoder, holds only instructions `AMDGPUForms` admits, reads only live-in or earlier-written registers, and writes no live-out register the window does not write. AMDGPU only, on any target whose ISA has checked cases.
- Stochastic search over windows of up to about eight instructions, in the style of STOKE: MCMC over moves, with a cost of correctness distance plus model cycles.
- Search: `RewriteSearch` in the semantics-check main sources walks one-move candidates of a window with a seeded Metropolis rule over a cost of changed live-out bits plus cycles per iteration; every distinct candidate that changes no live-out bit and costs fewer cycles goes to `Proof`, and only a proved one is reported with the weakest timing source behind its gain. A window that leaves none of its writes live out has nothing to preserve, so the search evaluates one candidate, the deletion of the whole window, and reports it with the reason `no write is live out`. The search samples the move set, so a window with no rewrite is coverage, not a proof.
- Cost: `AMDGPUBlockCost` has one path, the block analysis of the static pipeline view. For each candidate it writes the kernel's assembly with the window replaced, re-targets the branches that cross the window, assembles it and replaces the kernel in the code object, then reads the VM's own static view of that code object (control flow, register use with implicit state, pipe, planned cycles, offsets) and records it with `StaticProfile` as one wave issuing the window's loop or block once with every lane active. `BlockAnalysis.of` gives the cycles per iteration and their weakest source. `HardwareValidationSuite` records its rows through the same `StaticProfile`.
  - Addresses come from the new layout, so a rewrite that moves a loop into or out of one fetch line is charged the redirect that layout gives. Code after the window also moves; each report line says by how many bytes, and the cost does not include what that does to later loops.
  - Occupancy comes from the kernel descriptor's register count, changed by the granules the rewrite adds or frees over the whole kernel, and from the launch the caller states (workgroup lanes, shared bytes, placement).
  - Not costed, and reported with the reason instead of an estimate: a target with no probed model of its own (gfx906), a loop or block that holds a memory instruction (no recorded traffic), a loop with more than one path (no recorded issue counts), a size-changing rewrite of a kernel with a program-counter-relative instruction other than a branch, and a region the analysis withholds.
  - Not modelled: control words a rewrite would need (`s_nop` hazards on gfx1036), so a rewrite that creates a hazard is undercharged.
  - On the gfx1036 fixture `ctl_f4_tail`, the loop of that `HardwareValidationSuite` row, the unmodified block costs the 29.0 cycles the static view gives the row (29.01 measured). `ctl_f4_dead` adds a dead `v_mov_b32` to the loop; the default configuration proves its deletion at 29.00 from 30.00 cycles, weakest source `estimate` (the planned issue cycles are LLVM's). In the compiled `square_plus_one`, ten of the fifteen windows sit in a loop or block that reaches memory and are reported as not costed; the other five give no proved rewrite at seed 1, budget 3.
  - A recorded launch of the original kernel would supply memory traffic and issue counts for the regions refused above. The search does not take one yet.
  - SASS needs moves and a search driver, and for this cost path the VM's static pipe and `Schedule.Encoded` of a rewritten instruction. The stall and barrier are part of the SASS encoding, so a candidate cannot be costed before step 2 regenerates its control words. sm_89 has no fetch-line fields, so its cost has no layout term.
- Runner: `RewriteSearchRunner` in the same package takes `<code-object> <kernel> <target>` and optional `--seed`, `--budget`, `--temperature` and `--window` flags, with the launch as `--lanes` (default one wave), `--shared` and `--placement`, and prints one line per window and one per proved rewrite with its cycles before and after, the weakest source behind them and the bytes the later code moves.
- Run it on the benchmark cases and conformance kernels. Report each proven rewrite with its model gain and the weakest source behind that gain.

### 2. Control-word regeneration

- AMDGPU first, where LLVM's model gives the delays: recompute `s_delay_alu` and `s_nop` for a rewritten block, and test against LLVM's output on the same sequence.
- AMDGPU done for straight-line blocks: `ControlWords` regenerates `s_delay_alu` as `AMDGPUInsertDelayAlu` does, from delay types, wait classes and tied operands that codegen takes from LLVM's instruction records into `scheduling.json`, and `s_nop` for three `GCNHazardRecognizer` rules (`s_setreg`/`s_getreg` repeats, a VMEM read of a VALU-written SGPR, a VALU overwriting a wide store's data); `ControlWordsSuite` matches LLVM on all 55 compiled blocks (gfx906, gfx1100, gfx1150, gfx1200, VOPD off). Not covered: VOPD pairs, DPP and SDWA forms, and every other hazard rule (VALU trans use, `v_div_fmas`, readlane, LDS, MFMA).
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
