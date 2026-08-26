# ISA execution semantics

PTX and AMDGPU share an execution architecture, but not one executable instruction IR. Their parsed syntax trees are
validated and lowered into ISA-specific executable ADTs before execution:

```text
source / bytes -> syntax AST -> target validation -> executable instruction -> Step
```

Generated code owns facts available from the pinned specifications: instruction names, forms, operand roles and
widths, modifier groups, encodings, target predicates, and coverage manifests. Hand-written Scala owns normalization
and behaviour. Documentation prose is not treated as executable semantics: the PTX document has explicit semantics
for only a subset of instruction sections, and AMD's XML describes encodings rather than complete behaviour.

## Shared execution core

The shared core will provide:

- fixed-width bit values and checked conversions;
- typed global, shared/local, constant, and parameter memory spaces;
- explicit faults, waits, barriers, traps, exits, and trace events;
- execution budgets, cancellation, and bounded scheduler quanta;
- pure arithmetic, bitwise, comparison, and conversion helpers reusable by both ISAs.

An instruction executes as an extension method over its typed executable ADT:

```scala
extension (instruction: ExecutableInstruction)
  def execute(context: ExecutionContext): Step = instruction match {
    // hand-written semantic families
  }
```

`Step` is data, not implicit mutation of control flow. It includes advance, jump, divergent split, suspend, exit, and
trap outcomes. Programs remain immutable and indexed by program counter; execution never destructively pops a shared
instruction stream. The scheduler repeatedly executes runnable agents only up to a configured quantum and total
budget, so a malformed kernel cannot create an uninterruptible `while (true)` loop.

## PTX

PTX lowering produces a `PtxExecutable` with dense register slots and resolved labels. State is initially per lane,
then grouped into cohorts sharing a PC and active mask. Branches split cohorts and reconverge them explicitly. Calls,
predicates, barriers, warp operations, and memory scopes therefore remain visible to the scheduler rather than being
hidden inside instruction methods.

The current vector-add interpreter is the first narrow lowering slice. It runs independent lanes to completion and is
valid only for straight-line kernels; it must be replaced by cohort scheduling before barriers, warp operations, or
divergent control flow are marked supported.

## AMDGPU

AMDGPU lowering first decodes a target-specific instruction completely, then produces an executable instruction.
Wave state contains a wave PC, SGPRs, VGPRs, AGPRs, `EXEC`, `VCC`, `SCC`, and `M0`. Instruction execution stages all
reads before committing writes, which is required for correct aliasing and VOPD simultaneous-read behaviour.

Architectural families remain package-level catalogues, while an exact processor target carries feature predicates.
Validation must return `Unsupported` whenever only a broad RDNA/CDNA family fact is known; it must never infer exact
processor availability from the family alone.

## Implementation order

1. Introduce the shared `Step`, fault, budget, wait, and immutable-program substrate.
2. Replace the straight-line PTX loop with per-lane/cohort PCs and explicit scheduling.
3. Lower and implement PTX integer/control/memory families needed by the differential kernel corpus.
4. Add AMD wave state and SOPP/SOP1/SOP2 execution, followed by vector ALU and memory families.
5. Add barriers, divergence/reconvergence, calls, atomics, and memory-ordering rules.
6. Expand identical-kernel differential tests against NVIDIA and, where available, AMD hardware/tooling.

Initially the memory model is sequentially consistent and AMD memory completion is synchronous. `waitcnt` can validate
and trace as a no-op only inside that explicitly restricted, race-free model; asynchronous queues are introduced before
concurrent memory behaviour is claimed.
