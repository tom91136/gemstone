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

- Stochastic search over windows of up to about eight instructions, in the style of STOKE: MCMC over moves, with a cost of correctness distance plus model cycles.
- Run it on the benchmark cases and conformance kernels. Report each proven rewrite with its model gain and the weakest source behind that gain.

### 2. Control-word regeneration

- AMDGPU first, where LLVM's model gives the delays: recompute `s_delay_alu` and `s_nop` for a rewritten block, and test against LLVM's output on the same sequence.
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
